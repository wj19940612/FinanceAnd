package com.sbai.finance.kgame;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.finance.App;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.httplib.CookieManger;
import com.sbai.socket.SimpleConnector;
import com.sbai.socket.WsRequest;
import com.sbai.socket.WsResponse;

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

    public void setOnPushReceiveListener(OnPushReceiveListener onPushReceiveListener) {
        mOnPushReceiveListener = onPushReceiveListener;
    }

    public GamePusher() {
        mHandler = new Handler(Looper.getMainLooper());

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

    @Override
    public void connect() {
        Client.getGameSocketAddress()
                .setCallback(new Callback<Resp<List<SocketAddress>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<SocketAddress>> resp) {
                        if (resp.hasData()) {
                            connect(resp.getData().get(0));
                        }
                    }
                }).fireFree();
    }

    private void connect(SocketAddress address) {
        setUri(address.toUri());
        super.connect();
    }

    private void handleMessage(final String msg) {
        WsResponse resp = null;
        try {
            resp = new Gson().fromJson(msg, WsResponse.class);
        } catch (JsonSyntaxException e) {
            e.printStackTrace();
        }

        if (resp == null) return;

        if (resp.getCode() == WsResponse.REGISTER_SUCCESS) {
            Log.d(TAG, "Register success");
            return;
        }

        if (resp.getCode() == WsResponse.REGISTER_FAILURE) {
            // TODO: 26/06/2017 是否需要重新注册
            return;
        }

        if (resp.getCode() == WsResponse.PUSH) {
            Log.d(TAG, "onPush: " + resp.getContent());
            ackPushMessage(resp);
            if (mOnPushReceiveListener != null) {
                final Object o = resp.getContent();
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mOnPushReceiveListener.onPushReceive(o, msg);
                    }
                });
            }
            return;
        }

        if (resp.getCode() == WsResponse.HEART) {
            Log.d(TAG, "SOCKET HEART: " + resp);
            sendHeart();
            return;
        }
    }

    private void ackPushMessage(WsResponse resp) {
        AckPush ackPush = new AckPush(resp.getMsgId());
        WsRequest request = WsUtils.getRequest(WsRequest.MSG_CONFIRM, ackPush);
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
        Log.d(TAG, "register: ");

        String tokens = CookieManger.getInstance().getCookies();
        RegisterInfo registerInfo = new RegisterInfo(tokens);
        registerInfo.setDevice(AppInfo.getDeviceHardwareId(App.getAppContext()));
        registerInfo.setChannel(AppInfo.getMetaData(App.getAppContext(), "UMENG_CHANNEL"));
        WsRequest<RegisterInfo> request = WsUtils.getRequest(WsRequest.REGISTER, registerInfo);

        send(request);

    }
}
