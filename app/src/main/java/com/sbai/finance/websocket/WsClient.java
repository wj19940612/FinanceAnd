package com.sbai.finance.websocket;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.net.API;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.httplib.CookieManger;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class WsClient implements AbsWsClient {

    private static final String TAG = "WebSocket";
    private static final String HEAD_PROTOCOL = BuildConfig.FLAVOR.equalsIgnoreCase("dev") ? "ws://" : "wss://";
    private static final String URI = HEAD_PROTOCOL + API.getDomain() + "/game/ws.do";

    private static final int TIMEOUT_REQ = 3000;

    private static WsClient sInstance;

    private WebSocket mWebSocket;

    private Queue<WSMessage> mPendingList;
    private Queue<WSMessage> mExecutedList;

    private Status mStatus;
    private boolean mConnecting;

    private List<OnPushReceiveListener> mOnPushReceiveListeners;
    private Handler mHandler;
    private volatile boolean mNormalClosed;

    enum Status {
        UNREGISTERED,
        REGISTERING,
        REGISTERED
    }

    public static WsClient get() {
        if (sInstance == null) {
            sInstance = new WsClient();
        }
        return sInstance;
    }

    private WsClient() {
        mStatus = Status.UNREGISTERED;
        mHandler = new Handler(Looper.getMainLooper());

        mPendingList = new LinkedList<>();
        mExecutedList = new ConcurrentLinkedQueue<>();
    }

    private void register() {
        Log.d(TAG, "register: ");
        String tokens = CookieManger.getInstance().getCookies();
        WSMessage message = new WSMessage(SocketCode.CODE_REGISTER, new WSRegisterInfo(tokens));
        mWebSocket.send(message.toJson()); // do not add executed list
        mStatus = Status.REGISTERING;
    }

    public void send(WSCmd WSCmd) {
        send(new WSMessage(SocketCode.CODE_CMD, WSCmd, null));
    }

    public void send(WSCmd WSCmd, WSCallback callback, String tag) {
        callback.setTag(tag);
        send(new WSMessage(SocketCode.CODE_CMD, WSCmd, callback));
    }

    public void setOnPushReceiveListener(OnPushReceiveListener onPushReceiveListener) {
        if (mOnPushReceiveListeners == null) {
            mOnPushReceiveListeners = new ArrayList<>();
        }
        mOnPushReceiveListeners.add(onPushReceiveListener);
    }

    public void removePushReceiveListener(OnPushReceiveListener onPushReceiveListener) {
        if (mOnPushReceiveListeners != null) {
            if (mOnPushReceiveListeners.contains(onPushReceiveListener)) {
                mOnPushReceiveListeners.remove(onPushReceiveListener);
            }
        }
    }

    @Override
    public void send(WSMessage message) {
        if (isConnected() && mStatus == Status.REGISTERED) {
            Log.d(TAG, "execute: " + message.toJson());
            mWebSocket.send(message.toJson());
            mExecutedList.offer(message);
            mHandler.postDelayed(new RemoveTimeoutReqTask(message.getUuid()), TIMEOUT_REQ);
        } else {
            mPendingList.offer(message);

            if (mConnecting) {
                return;
            }

            if (isConnected() && mStatus == Status.REGISTERING) {
                return;
            }

            connect();
        }
    }

    private class RemoveTimeoutReqTask implements Runnable {

        private String uuid;

        public RemoveTimeoutReqTask(String uuid) {
            this.uuid = uuid;
        }

        @Override
        public void run() {
            Iterator iterator = mExecutedList.iterator();
            while (iterator.hasNext()) {
                WSMessage msg = (WSMessage) iterator.next();
                if (msg.getUuid().equals(uuid)) {
                    WSCallback callback = msg.getCallback();
                    if (callback != null) {
                        callback.onTimeout();
                    }
                    iterator.remove();
                    break;
                }
            }
        }
    }

    @Override
    public void connect() {
        mConnecting = true;
        AsyncHttpClient.getDefaultInstance().websocket(URI, null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                mConnecting = false;

                mWebSocket = webSocket;

                if (isConnected()) {
                    mNormalClosed = false;

                    initWebSocket();

                    onOpen();
                }
            }
        });
    }

    private void initWebSocket() {
        mWebSocket.setStringCallback(new WebSocket.StringCallback() {
            @Override
            public void onStringAvailable(String s) {
                onMessage(s);
            }
        });
        mWebSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                onClose();
            }
        });
        mWebSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                onError(ex);
            }
        });
        mWebSocket.setPingCallback(new WebSocket.PingCallback() {
            @Override
            public void onPingReceived(String s) {
                Log.d(TAG, "onPingReceived: " + s);
                mWebSocket.pong(s);
            }
        });
    }

    @Override
    public boolean isConnecting() {
        return false;
    }

    @Override
    public boolean isConnected() {
        return mWebSocket != null && mWebSocket.isOpen();
    }

    @Override
    public boolean isClosing() {
        return false;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    private void executePendingList() {
        if (mWebSocket.isOpen()) {
            while (!mPendingList.isEmpty()) {
                WSMessage wsMessage = mPendingList.poll();
                send(wsMessage);
            }
        }
    }

    @Override
    public void onOpen() {
        if (mWebSocket != null) {
            Log.d(TAG, "onOpen: " + mWebSocket.toString());
        }
        register();
    }

    @Override
    public void onClose() {
        if (mWebSocket != null) {
            Log.d(TAG, "onClose: " + mWebSocket.toString());
        }
        mStatus = Status.UNREGISTERED;
        if (!mNormalClosed) { // if close automatically or by accident, reconnect
            Log.d(TAG, "onClose: reconnect");
            connect();
        }
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError: " + e.getMessage());
        mStatus = Status.UNREGISTERED;
    }

    @Override
    public void close() {
        if (isConnected()) {
            mNormalClosed = true;
            mWebSocket.close();
        }
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: " + message);
        WSMessage resp = null;
        try {
            resp = new Gson().fromJson(message, WSMessage.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (resp == null) return;

        if (resp.getCode() == SocketCode.CODE_RESP_REGISTER_SUCCESS) {
            Log.d(TAG, "Register success");
            mStatus = Status.REGISTERED;
            executePendingList();
            return;
        }

        if (resp.getCode() == SocketCode.CODE_RESP_REGISTER_FAILURE) {
            mStatus = Status.UNREGISTERED;
            // TODO: 26/06/2017 是否需要重新注册
            return;
        }

        if (resp.getCode() == SocketCode.CODE_RESP_PUSH) {
            Log.d(TAG, "onPush: " + resp.getContent());
            if (mOnPushReceiveListeners != null) {
                for (int i = 0; i < mOnPushReceiveListeners.size(); i++) {
                    final OnPushReceiveListener listener = mOnPushReceiveListeners.get(i);
                    if (listener != null) {
                        final Object o = new Gson().fromJson(message, listener.getGenericType());
                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                listener.onPushReceive(o);
                            }
                        });
                    }
                }
            }
            return;
        }

        if (resp.getCode() == SocketCode.CODE_RESP_HEART) {
            Log.d(TAG, "SOCKET HEART: " + resp);
            sendHeart();
            return;
        }

        WSMessage request = getRequest(resp);
        if (request == null) return; // Maybe timeout

        if (resp.getCode() == SocketCode.CODE_RESP_CMD_SUCCESS) {
            final WSCallback callback = request.getCallback();
            if (callback != null) {
                final Object o = new Gson().fromJson(message, callback.getGenericType());
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onResponse(o);
                    }
                });
            }
        } else {
            final int errorCode = resp.getCode();
            if (errorCode == SocketCode.CODE_RESP_UNLOGIN) {
                // when logout token will change, need register again
                mPendingList.offer(request);
                register();
                return;
            }
            if (errorCode == SocketCode.CODE_RESP_UNREGISTER) {
                mPendingList.offer(request);
                register();
                return;
            }

            final WSCallback callback = request.getCallback();
            if (callback != null) {
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onError(errorCode);
                    }
                });
            }
            if (request.isRetry()) {
                mPendingList.offer(request);
                executePendingList();
            }
        }
    }

    private void sendHeart() {
        if (isConnected()) {
            WSMessage heartMsg = new WSMessage(SocketCode.CODE_HEART, null);
            mWebSocket.send(heartMsg.toJson());
        }
    }

    private WSMessage getRequest(WSMessage resp) {
        Iterator iterator = mExecutedList.iterator();
        WSMessage msg = null;
        while (iterator.hasNext()) {
            msg = (WSMessage) iterator.next();
            if (msg.getUuid().equals(resp.getUuid())) {
                iterator.remove();
                break;
            }
        }
        return msg;
    }

    public void cancel(String tag) {
        Iterator iterator = mExecutedList.iterator();
        while (iterator.hasNext()) {
            WSMessage msg = (WSMessage) iterator.next();
            WSCallback callback = msg.getCallback();
            if (callback != null && callback.getTag().equals(tag)) {
                iterator.remove();
            }
        }
    }
}
