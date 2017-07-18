package com.sbai.finance.websocket.market;

import android.util.Log;

import com.google.gson.Gson;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;
import com.sbai.finance.net.API;
import com.sbai.finance.websocket.Connector;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class MarketSubscriber implements MarketSubscribe, Connector {

    private static final String TAG = "MarketSubscriber";
    private static final String WS_URI = "ws://" + API.getDomain() + "/ws.do";

    private static MarketSubscriber sSubscriber;

    public static MarketSubscriber get() {
        if (sSubscriber == null) {
            sSubscriber = new MarketSubscriber();
        }
        return sSubscriber;
    }

    private static class Command {

        private int code;
        private Object data;

        public Command(int code) {
            this.code = code;
        }

        public Command(int code, String data) {
            this.code = code;
            this.data = data;
        }

        public String toJson() {
            return new Gson().toJson(this);
        }

        public int getCode() {
            return code;
        }
    }

    enum ConnectStatus {
        DISCONNECT,
        CONNECTED,
        CONNECTING,
        DISCONNECTING
    }

    private Queue<Command> mPendingList;
    private WebSocket mWebSocket;
    private ConnectStatus mStatus;
    private List<DataReceiveListener> mDataReceiveListeners;
    private Gson mGson;

    public MarketSubscriber() {
        mPendingList = new LinkedList<>();
        mStatus = ConnectStatus.DISCONNECT;
        mDataReceiveListeners = new ArrayList<>();
        mGson = new Gson();
    }

    private void subscribe(Command command) {
        if (isConnected()) {
            mWebSocket.send(command.toJson());
            Log.d(TAG, "subscribe: " + command.toJson());
        } else {
            mPendingList.offer(command);

            if (isConnecting() || isDisconnecting()) return;

            connect();
        }
    }

    public void addDataReceiveListener(DataReceiveListener listener) {
        mDataReceiveListeners.add(listener);
    }

    public void removeDataReceiveListener(DataReceiveListener listener) {
        mDataReceiveListeners.remove(listener);
    }

    @Override
    public void subscribe(String varietyType) {
        subscribe(new Command(REQ_SUB, varietyType));
    }

    @Override
    public void subscribeAll() {
        subscribe(new Command(REQ_SUB_ALL));
    }

    @Override
    public void unSubscribeAll() {
        subscribe(new Command(REQ_UNSUB_ALL));
    }

    @Override
    public void unSubscribe(String varietyType) {
        subscribe(new Command(REQ_UNSUB, varietyType));
    }

    @Override
    public void onMessage(String message) {
        Command command = mGson.fromJson(message, Command.class);
        if (command != null && command.getCode() == REQ_HEART) {
            mWebSocket.send(new Command(REQ_HEART_UP).toJson());
            return;
        }

        onDataReceive(message);
    }

    private void onDataReceive(String message) {
        Log.d(TAG, "onDataReceive: " + message);
        for (DataReceiveListener listener : mDataReceiveListeners) {
            Object o = mGson.fromJson(message, listener.getGenericType());
            listener.onDataReceive(o);
        }
    }

    @Override
    public void connect() {
        mStatus = ConnectStatus.CONNECTING;
        AsyncHttpClient.getDefaultInstance().websocket(WS_URI, null, new AsyncHttpClient.WebSocketConnectCallback() {
            @Override
            public void onCompleted(Exception ex, WebSocket webSocket) {
                mStatus = ConnectStatus.CONNECTED;
                mWebSocket = webSocket;

                if (isConnected()) {

                    onConnected();

                    initWebSocket();

                    executePendingList();

                    Log.d(TAG, "onCompleted: connected!");
                }
            }
        });
    }

    private void executePendingList() {
        while (!mPendingList.isEmpty()) {
            Command command = mPendingList.poll();
            subscribe(command);
        }
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
                mStatus = ConnectStatus.DISCONNECT;

                if (ex != null) {
                    onError(ex.getMessage());
                }

                Log.d(TAG, "onCompleted: setClosedCallback");
                onDisconnected();
            }
        });
        mWebSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {
                mStatus = ConnectStatus.DISCONNECT;

                if (ex != null) {
                    onError(ex.getMessage());
                }

                Log.d(TAG, "onCompleted: setEndCallback");
                onDisconnected();
            }
        });
        mWebSocket.setPingCallback(new WebSocket.PingCallback() {
            @Override
            public void onPingReceived(String s) {
                mWebSocket.pong(s);
            }
        });
    }

    @Override
    public boolean isConnecting() {
        return mStatus == ConnectStatus.CONNECTING;
    }

    @Override
    public boolean isConnected() {
        return mStatus == ConnectStatus.CONNECTED && mWebSocket != null && mWebSocket.isOpen();
    }

    @Override
    public void disconnect() {
        if (isConnected()) {
            mWebSocket.close();
        }
    }

    @Override
    public boolean isDisconnecting() {
        return mStatus == ConnectStatus.DISCONNECTING;
    }

    @Override
    public void onConnected() {

    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onDisconnected() {

    }
}
