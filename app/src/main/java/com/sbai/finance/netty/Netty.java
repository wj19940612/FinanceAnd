package com.sbai.finance.netty;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.sbai.finance.model.SocketAddress;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;

public class Netty {

    private static final String TAG = "Netty";

    public static final int REQ_QUOTA = 100;
    public static final int REQ_HEART = 200;

    public static final int REQ_SUB = 101;
    public static final int REQ_UNSUB = 102;

    public static final int REQ_SUB_ALL = 103;
    public static final int REQ_UNSUB_ALL = 104;

    enum ConnectStatus {
        DISCONNECTING,
        DISCONNECTED,
        CONNECTING,
        CONNECTED
    }

    private ConnectStatus mConnectStatus;
    private NettyClientHandler.Callback mCallback;

    private EventLoopGroup mWorkerGroup;
    private Bootstrap mBootstrap;
    private Channel mChannel;
    private boolean mKeepAlive;

    private InetSocketAddress mInetSocketAddress;
    private List<NettyHandler> mHandlerList;
    private Queue<Command> mCommandList;

    private static Netty sInstance;

    public static Netty get() {
        if (sInstance == null) {
            sInstance = new Netty();
        }
        return sInstance;
    }

    private Netty() {
        mConnectStatus = ConnectStatus.DISCONNECTED;
        mCallback = new NettyClientHandler.Callback() {
            @Override
            public void onChannelActive(ChannelHandlerContext ctx) {
                Log.d(TAG, "onChannelActive: ");
                mConnectStatus = ConnectStatus.CONNECTED;
                executeCommand();
            }

            @Override
            public void onChannelInActive(ChannelHandlerContext ctx) {
                Log.d(TAG, "onChannelInActive: ");
                mConnectStatus = ConnectStatus.DISCONNECTED;
                onDisconnect();
            }

            @Override
            public void onReceiveData(String data) {
                Log.d(TAG, "onReceiveData: " + data);
                processData(data);
            }

            @Override
            public void onError(ChannelHandlerContext ctx, Throwable cause) {
                Log.d(TAG, "onError: ");
            }
        };
        mHandlerList = new ArrayList<>();
        mCommandList = new LinkedList<>();

        mWorkerGroup = new NioEventLoopGroup();
        mBootstrap = new Bootstrap()
                .group(mWorkerGroup)
                .channel(NioSocketChannel.class)
                .handler(new NettyInitializer(mCallback));
    }

    private void onDisconnect() {
        for (Handler handler : mHandlerList) {
            handler.obtainMessage(NettyHandler.WHAT_DISCONNECT).sendToTarget();
        }
    }

    private void processData(String data) {
        for (Handler handler : mHandlerList) {
            handler.obtainMessage(NettyHandler.WHAT_DATA, data).sendToTarget();
        }
    }

    private void onError(String msg) {
        for (Handler handler : mHandlerList) {
            handler.obtainMessage(NettyHandler.WHAT_ERROR, msg).sendToTarget();
        }
    }

    public void addHandler(NettyHandler handler) {
        mHandlerList.add(handler);
    }

    public void removeHandler(NettyHandler handler) {
        mHandlerList.remove(handler);
    }

    private void connect() {
        mConnectStatus = ConnectStatus.CONNECTING;
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
                mConnectStatus = ConnectStatus.DISCONNECTED;
            }
        });
    }

    private void doConnect() {
        ChannelFuture channelFuture = mBootstrap.connect(mInetSocketAddress);
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                if (channelFuture.isSuccess()) {
                    mChannel = channelFuture.sync().channel();
                    Log.d(TAG, "operationComplete: channel id: " + mChannel.id().asShortText());
                } else {
                    Throwable throwable = channelFuture.cause();
                    Log.d(TAG, "operationComplete error: " + throwable.getMessage());
                    onError(throwable.getMessage());
                    channelFuture.channel().eventLoop().schedule(new Runnable() {
                        @Override
                        public void run() {
                            doConnect();
                        }
                    }, 1, TimeUnit.MILLISECONDS);
                }
            }
        });
    }

    public void subscribe(int reqCode) {
        subscribe(reqCode, null);
    }

    private void executeCommand() {
        if (mConnectStatus == ConnectStatus.CONNECTED && mChannel != null) {
            while (!mCommandList.isEmpty()) {
                Command command = mCommandList.poll();
                Log.d(TAG, "executeCommand: " + command.toJson());
                mChannel.writeAndFlush(command.toJson());
            }
        }
    }

    public void subscribe(int reqCode, String varietyType) {
        mCommandList.add(new Command(reqCode, varietyType));

        if (mConnectStatus == ConnectStatus.CONNECTED) {
            executeCommand();
        } else if (mConnectStatus == ConnectStatus.DISCONNECTED || mConnectStatus == ConnectStatus.DISCONNECTING) {
            connect();
        } else {

        }
    }

    public void disconnect() {
        if (mChannel != null) {
            try {
                mChannel.close().sync();
                mConnectStatus = ConnectStatus.DISCONNECTING;
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void shutdown() {
        if (mKeepAlive) {
            mKeepAlive = false;
            return;
        }

        disconnect();
        if (mWorkerGroup != null) {
            mWorkerGroup.shutdownGracefully();
            sInstance = null;
        }
    }

    public void keepALive() {
        mKeepAlive = true;
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
}
