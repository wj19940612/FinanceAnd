package com.sbai.finance.websocket;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.finance.App;
import com.sbai.finance.net.API;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.httplib.CookieManger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class WSClient implements WSAbsClient {

    private static final String TAG = "WebSocket";

    private static WSClient sInstance;

    private InnerWSClient mWSClient;
    private Queue<WSMessage> mPendingList;
    private Queue<WSMessage> mExecutedList;
    private Status mStatus;
    private List<OnPushReceiveListener> mOnPushReceiveListeners;
    private Handler mHandler;

    private boolean mCloseAuto; // websocket will be closed automatically

    enum Status {
        UNREGISTERED,
        REGISTERING,
        REGISTERED
    }

    public static WSClient get() {
        if (sInstance == null) {
            sInstance = new WSClient();
        }
        return sInstance;
    }

    private WSClient() {
        mStatus = Status.UNREGISTERED;
        mHandler = new Handler(App.getAppContext().getMainLooper());

        mPendingList = new LinkedList<>();
        mExecutedList = new LinkedList<>();
        mCloseAuto = false;
    }

    private void register() {
        Log.d(TAG, "register: ");
        String tokens = CookieManger.getInstance().getCookies();
        WSMessage message = new WSMessage(SocketCode.CODE_REGISTER, new WSRegisterInfo(tokens));
        mWSClient.send(message.toJson()); // do not add executed list
        mStatus = Status.REGISTERING;
    }

    public void send(WSCmd WSCmd, WSCallback callback) {
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
            mWSClient.send(message.toJson());
            mExecutedList.offer(message);
        } else {
            mPendingList.offer(message);

            if (isConnecting()) {
                return;
            }

            if (isClosing()) {
                return;
            }

            if (isConnected() && mStatus == Status.REGISTERING) {
                return;
            }

            connect();
        }
    }

    private void connect() {
        mWSClient = new InnerWSClient(createURI());
        mWSClient.setClient(this);
        mWSClient.connect();
    }

    @Override
    public boolean isConnecting() {
        return mWSClient != null && mWSClient.isConnecting();
    }

    @Override
    public boolean isConnected() {
        return mWSClient != null && mWSClient.isOpen() && !mWSClient.isClosing();
    }

    @Override
    public boolean isClosing() {
        return mWSClient != null && mWSClient.isClosing();
    }

    private void executePendingList() {
        if (mWSClient.isOpen()) {
            while (!mPendingList.isEmpty()) {
                WSMessage wsMessage = mPendingList.poll();
                send(wsMessage);
            }
        }
    }

    @Override
    public void onOpen() {
        mCloseAuto = true;
        register();
    }

    @Override
    public void onClose() {
        mWSClient = null;
        mStatus = Status.UNREGISTERED;
        if (mCloseAuto) { // if close automatically, reconnect
            connect();
        }
    }

    @Override
    public void onError() {
        mWSClient = null;
        mStatus = Status.UNREGISTERED;
    }

    @Override
    public void close() {
        if (isConnected()) {
            mWSClient.close();
            mCloseAuto = false;
        }
    }


    @Override
    public void onMessage(String message) {
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
        if (request == null) return;

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
        if (mWSClient.isOpen()) {
            WSMessage heartMsg = new WSMessage(SocketCode.CODE_HEART, null);
            mWSClient.send(heartMsg.toJson());
            Log.d(TAG, "sendHeart: " + heartMsg);
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

    public static URI createURI() {
        try {
            return new URI("ws://" + API.getDomain() + "/game/ws.do");
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static class InnerWSClient extends WebSocketClient {

        private WSAbsClient client;

        public void setClient(WSAbsClient client) {
            this.client = client;
        }

        public InnerWSClient(URI serverUri) {
            super(serverUri);
        }

        @Override
        public void onOpen(ServerHandshake handshakedata) {
            Log.d(TAG, "onOpen: ");
            if (client != null) {
                client.onOpen();
            }
        }

        @Override
        public void onMessage(String message) {
            Log.d(TAG, "onMessage: unprocessed message: " + message);
            if (client != null) {
                client.onMessage(message);
            }
        }

        @Override
        public void onClose(int code, String reason, boolean remote) {
            Log.d(TAG, "onClose: code: " + code + ", reason: " + reason + ", remote: " + remote);
            if (client != null) {
                client.onClose();
            }
        }

        @Override
        public void onError(Exception ex) {
            Log.d(TAG, "onError: " + ex.getMessage());
            if (client != null) {
                client.onError();
            }
        }
    }
}
