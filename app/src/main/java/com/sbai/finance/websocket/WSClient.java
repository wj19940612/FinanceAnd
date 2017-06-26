package com.sbai.finance.websocket;

import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.httplib.CookieManger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class WSClient implements WSAbsClient {

    private static final String TAG = "WebSocket";

    private static WSClient sInstance;

    private InnerWSClient mWSClient;
    private Queue<WSMessage> mPendingList;
    private Queue<WSMessage> mExecutedList;
    private boolean isRegistered;
    private OnPushReceiveListener mOnPushReceiveListener;

    public static WSClient get() {
        if (sInstance == null) {
            sInstance = new WSClient();
        }
        return sInstance;
    }

    private WSClient() {
        mWSClient = new InnerWSClient(createURI());
        isRegistered = false;
        mWSClient.setClient(this);

        mPendingList = new LinkedList<>();
        mExecutedList = new LinkedList<>();
    }

    private void register() {
        Log.d(TAG, "register: ");
        String tokens = CookieManger.getInstance().getCookies();
        WSMessage message = new WSMessage(SocketCode.CODE_REGISTER, new WSRegisterInfo(tokens));
        mWSClient.send(message.toJson()); // do not add executed list
    }

    public void send(WSCmd WSCmd, WSCallback callback) {
        send(new WSMessage(SocketCode.CODE_CMD, WSCmd, callback));
    }

    public void setOnPushReceiveListener(OnPushReceiveListener onPushReceiveListener) {
        mOnPushReceiveListener = onPushReceiveListener;
    }

    @Override
    public void send(WSMessage message) {
        if (mWSClient.isOpen() && isRegistered) {
            Log.d(TAG, "execute: " + message.toJson());
            mWSClient.send(message.toJson());
            mExecutedList.offer(message);
        } else {
            mPendingList.offer(message);
            mWSClient.connect();
        }
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
        register();
    }

    @Override
    public void onClose() {
        mWSClient = new InnerWSClient(createURI());
        mWSClient.setClient(this);
        isRegistered = false;
    }

    @Override
    public void onError() {
        mWSClient = new InnerWSClient(createURI());
        mWSClient.setClient(this);
        isRegistered = false;
    }

    @Override
    public void close() {
        mWSClient.close();
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
            Log.d(TAG, "onMessage: register success");
            isRegistered = true;
            executePendingList();
            return;
        }

        if (resp.getCode() == SocketCode.CODE_RESP_REGISTER_FAILURE) {
            // TODO: 26/06/2017 是否需要重新注册
            return;
        }

        if (resp.getCode() == SocketCode.CODE_RESP_PUSH) {
            if (mOnPushReceiveListener != null) {
                Object o = new Gson().fromJson(message, mOnPushReceiveListener.getGenericType());
                mOnPushReceiveListener.onPushReceive(o);
            }
            return;
        }

        WSMessage request = getRequest(resp);
        if (request == null) return;

        if (resp.getCode() == SocketCode.CODE_RESP_HEART) {
            Log.d(TAG, "SOCKET HEART: " + resp);
            return;
        }

        if (resp.getCode() == SocketCode.CODE_RESP_CMD_SUCCESS) {
            WSCallback callback = request.getCallback();
            if (callback != null) {
                callback.onResponse(new Gson().fromJson(message, callback.getGenericType()));
            }
        } else {
            WSCallback callback = request.getCallback();
            if (callback != null) {
                callback.onError(resp.getCode());
            }
            if (request.isRetry()) {
                mPendingList.offer(request);
                executePendingList();
            }
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
            return new URI("ws://192.168.1.7/game/ws.do");
//            return new URI("ws://" + API.getDomain() + "/game/ws.do");
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
            Log.d(TAG, "onOpen: " + handshakedata);
            if (client != null) {
                client.onOpen();
            }
        }

        @Override
        public void onMessage(String message) {
            Log.d(TAG, "onMessage: " + message);
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
