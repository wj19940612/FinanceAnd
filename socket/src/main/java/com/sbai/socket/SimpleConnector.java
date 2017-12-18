package com.sbai.socket;

import android.util.Log;

import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.http.AsyncHttpClient;
import com.koushikdutta.async.http.WebSocket;

/**
 * Modified by john on 15/12/2017
 * <p>
 * 先实现 push 功能
 */
public class SimpleConnector implements WsConnector {

    protected static String TAG;

    private boolean mConnecting;
    private String mUri;
    private WebSocket mWebSocket;
    private OnConnectedListener mOnConnectedListener;
    private OnMessageListener mOnMessageListener;

    private volatile boolean mNormalClosed;

    public interface OnConnectedListener {
        void onConnected();
    }

    public interface OnMessageListener {
        void onMessage(String msg);
    }

    public SimpleConnector(String uri) {
        mUri = uri;
        TAG = getClass().getSimpleName();
    }

    public SimpleConnector() {
        TAG = getClass().getSimpleName();
    }

    public void setUri(String uri) {
        mUri = uri;
    }

    public void setOnConnectedListener(OnConnectedListener onConnectedListener) {
        mOnConnectedListener = onConnectedListener;
    }

    public void setOnMessageListener(OnMessageListener onMessageListener) {
        mOnMessageListener = onMessageListener;
    }

    @Override
    public void connect() {
        mConnecting = true;
        AsyncHttpClient.getDefaultInstance().websocket(mUri, null, new AsyncHttpClient.WebSocketConnectCallback() {
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
    public void close() {
        if (isConnected()) {
            mNormalClosed = true;
            mWebSocket.close();
        }
    }

    @Override
    public void send(WsRequest request) {
        if (isConnected()) {
            mWebSocket.send(request.toJson());
        }
    }

    @Override
    public void onOpen() {
        Log.d(TAG, "onOpen: ");
        if (mOnConnectedListener != null) {
            mOnConnectedListener.onConnected();
        }
    }

    @Override
    public void onClose() {
        Log.d(TAG, "onClose: ");
        if (!mNormalClosed) { // if close automatically or by accident, reconnect
            Log.d(TAG, "onClose not right: reconnect");
            connect();
        }
    }

    @Override
    public void onError(Exception e) {
        Log.d(TAG, "onError: " + e.getMessage());

    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: " + message);
        if (mOnMessageListener != null) {
            mOnMessageListener.onMessage(message);
        }
    }

    @Override
    public boolean isConnecting() {
        return mConnecting;
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
}
