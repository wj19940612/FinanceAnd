package com.sbai.finance.kgame;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.App;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.Preference;
import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.httplib.CookieManger;
import com.sbai.socket.SimpleConnector;
import com.sbai.socket.WsRequest;
import com.sbai.socket.WsRespCode;
import com.sbai.socket.WsResponse;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

/**
 * Modified by john on 15/12/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class GamePusher extends SimpleConnector {

    private static final String HEAD_PROTOCOL = BuildConfig.FLAVOR.equalsIgnoreCase("dev") ? "ws://" : "ws://";
    private static final String URI = HEAD_PROTOCOL + API.getDomain() + "/game/ws.do";

    private static GamePusher sGamePusher;
    private OnPushReceiveListener mOnPushReceiveListener;
    private Handler mHandler;
    private MessageIdQueue mMessageIdQueue;
    private boolean mAddressRequesting;

    public void setOnPushReceiveListener(OnPushReceiveListener onPushReceiveListener) {
        mOnPushReceiveListener = onPushReceiveListener;
    }

    public void removeOnPushReceiveListener() {
        mOnPushReceiveListener = null;
    }

    public GamePusher() {
        mHandler = new Handler(Looper.getMainLooper());
        mMessageIdQueue = new MessageIdQueue(100);

        setOnConnectedListener(new OnConnectedListener() {
            @Override
            public void onConnected() {
                register();
            }
        });
        setOnMessageListener(new OnMessageListener() {
            @Override
            public void onMessage(String msg) {
                handleMessage(msg);
            }
        });
    }

    public void open() {
        if (isConnecting() || isConnected()) return;

        Client.getGameSocketAddress()
                .setCallback(new Callback<Resp<List<SocketAddress>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<SocketAddress>> resp) {
                        if (resp.hasData()) {
                            connect(resp.getData().get(0));
                        }
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mAddressRequesting = false;
                    }

                    @Override
                    public void onStart() {
                        super.onStart();
                        mAddressRequesting = true;
                    }
                }).fireFree();
    }

    private void connect(SocketAddress address) {
        setUri(address.toUri());
        connect();
    }

    @Override
    public boolean isConnecting() {
        return super.isConnecting() || mAddressRequesting;
    }

    private void handleMessage(final String msg) {
        WsResponse<JsonObject> resp = null;
        try {
            Type type = new TypeToken<WsResponse<JsonObject>>() {
            }.getType();
            resp = new Gson().fromJson(msg, type);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (resp == null) return;

        if (resp.getCode() == WsResponse.REGISTER_SUCCESS) {
            Log.d(TAG, "Register success");
            return;
        }

        if (resp.getCode() == WsResponse.REGISTER_FAILURE) {
            Log.d(TAG, "Register failure");
            // TODO: 26/06/2017 是否需要重新注册
            return;
        }

        if (resp.getCode() == WsResponse.PUSH) {
            Log.d(TAG, "onPush: " + resp.getContent());
            ackPushMessage(resp);
            onPushReceived(msg, resp);
            return;
        }

        if (resp.getCode() == WsRespCode.MSG_ACK_SUCCESS) {
            Log.d(TAG, "response of ack success: " + resp.getMsgId());
            mMessageIdQueue.remove(resp.getMsgId());
            return;
        }

        if (resp.getCode() == WsResponse.HEART) {
            Log.d(TAG, "SOCKET HEART: " + resp);
            sendHeart();
            return;
        }
    }

    private void onPushReceived(final String msg, WsResponse<JsonObject> resp) {
        if (mMessageIdQueue.contain(resp.getMsgId())) return;

        if (mOnPushReceiveListener != null) {
            final JsonObject jsonObject = resp.getContent();
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    Type type = mOnPushReceiveListener.getGenericType();
                    if (type != null) {
                        Object o = new Gson().fromJson(jsonObject, type);
                        mOnPushReceiveListener.onPushReceive(o, msg);
                    }
                }
            });
            mMessageIdQueue.add(resp.getMsgId());
        }
    }

    private void ackPushMessage(WsResponse resp) {
        AckPush ackPush = new AckPush(resp.getMsgId());
        WsRequest request = WsUtils.getRequest(WsRequest.MSG_ACK, ackPush);
        send(request);
    }

    private void sendHeart() {
        WsRequest request = new WsRequest(WsRequest.HEART, null);
        send(request);
    }

    public static GamePusher get() {
        if (sGamePusher == null) {
            sGamePusher = new GamePusher();
        }
        return sGamePusher;
    }

    private void register() {
        String tokens = CookieManger.getInstance().getCookies();
        RegisterInfo registerInfo = new RegisterInfo(tokens);
        registerInfo.setDevice(Preference.get().getPushClientId());
        registerInfo.setChannel(AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"));
        WsRequest<RegisterInfo> request = new WsRequest<>(WsRequest.REGISTER, registerInfo);

        Log.d(TAG, "register: " + registerInfo);

        send(request);
    }

    static class MessageIdQueue {

        private int mMaxCapacity;

        private LinkedList<Long> mQueue;

        public MessageIdQueue(int maxCapacity) {
            this.mMaxCapacity = maxCapacity;
            this.mQueue = new LinkedList<>();
        }

        public void add(long msgId) {
            if (mQueue.size() >= mMaxCapacity) {
                mQueue.removeFirst();
            }
            mQueue.addLast(msgId);
        }

        public void remove(long msgId) {
            int indexOf = mQueue.indexOf(msgId);
            if (indexOf >= 0) {
                mQueue.remove(indexOf);
            }
        }

        public boolean contain(long msgId) {
            return mQueue.contains(msgId);
        }
    }
}
