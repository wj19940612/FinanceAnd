package com.sbai.finance.socket;

import android.os.Handler;

import com.google.gson.Gson;
import com.koushikdutta.async.AsyncServer;
import com.koushikdutta.async.AsyncSocket;
import com.koushikdutta.async.ByteBufferList;
import com.koushikdutta.async.DataEmitter;
import com.koushikdutta.async.callback.CompletedCallback;
import com.koushikdutta.async.callback.ConnectCallback;
import com.koushikdutta.async.callback.DataCallback;
import com.sbai.finance.App;
import com.sbai.finance.model.SocketAddress;

import java.net.InetSocketAddress;
import java.util.LinkedList;
import java.util.Queue;

public class MarketSubscriber implements Subscriber {

    enum Status {
        DISCONNECTING,
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    private static class Command {

        private int code;
        private String data;

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
    }

    private Queue<Command> mPendingList;
    private Queue<Command> mExecutedList;
    private InetSocketAddress mInetSocketAddress;

    private Handler mHandler;
    private Status mStatus;
    private boolean mCloseAuto;
    private AsyncSocket mSocket;

    private static MarketSubscriber sSubscriber;

    public MarketSubscriber get() {
        if (sSubscriber == null) {
            sSubscriber = new MarketSubscriber();
        }
        return sSubscriber;
    }

    public MarketSubscriber() {
        mHandler = new Handler(App.getAppContext().getMainLooper());

        mPendingList = new LinkedList<>();
        mExecutedList = new LinkedList<>();
        mCloseAuto = false;
        mStatus = Status.DISCONNECTED;
    }

    private void subscribe(Command command) {
        if (isConnected()) {

        } else {
            mPendingList.offer(command);

            if (isConnecting()) return;

            connect();
        }
    }

    @Override
    public void subscribe(String varietyType) {

    }

    @Override
    public void subscribeAll() {

    }

    @Override
    public void unSubscribeAll() {

    }

    @Override
    public void unSubscribe(String varietyType) {

    }

    @Override
    public void shutdown() {

    }

    @Override
    public boolean isConnected() {
        return mStatus == Status.CONNECTED;
    }

    @Override
    public void connect() {
        mStatus = Status.CONNECTING;
        SocketAddress.requestMarketServerIpAndPort(new SocketAddress.Callback() {
            @Override
            public void onSuccess(SocketAddress ipPort) {
                String host = ipPort.getIp();
                int port = Integer.valueOf(ipPort.getPort()).intValue();
                mInetSocketAddress = InetSocketAddress.createUnresolved(host, port);
                doConnect();
            }

            @Override
            public void onFailure() {
                mStatus = Status.DISCONNECTED;
            }
        });
    }

    private void doConnect() {
        AsyncServer.getDefault().connectSocket(mInetSocketAddress, new ConnectCallback() {
            @Override
            public void onConnectCompleted(Exception ex, AsyncSocket socket) {
                mStatus = Status.CONNECTED;
                handleConnectCompleted(ex, socket);
            }
        });
    }

    private void handleConnectCompleted(Exception ex, AsyncSocket socket) {
        if (ex != null) {
            ex.printStackTrace();
        }
        mSocket = socket;
        setupSocket();
    }

    private void setupSocket() {
        mSocket.setClosedCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {

            }
        });
        mSocket.setEndCallback(new CompletedCallback() {
            @Override
            public void onCompleted(Exception ex) {

            }
        });
        mSocket.setDataCallback(new DataCallback() {
            @Override
            public void onDataAvailable(DataEmitter emitter, ByteBufferList bb) {

            }
        });
    }

    @Override
    public boolean isConnecting() {
        return mStatus == Status.CONNECTING;
    }
}
