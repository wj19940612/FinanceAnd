package com.sbai.finance.websocket;

import android.util.Log;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.framing.CloseFrame;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class WSocketClient extends WebSocketClient {

    private static final String TAG = "WebSocket";

    public WSocketClient() {
        super(createURI());
    }

    public void send(WMessage message) {
        send(message.toJsonString());
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        Log.d(TAG, "onOpen: " + handshakedata);
    }

    @Override
    public void onMessage(String message) {
        Log.d(TAG, "onMessage: " + message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        Log.d(TAG, "onClose: code: " + code + ", reason: " + reason + ", remote: " + remote);
        if (code != CloseFrame.NORMAL) { // callback from close()
            connect();
        }
    }

    @Override
    public void onError(Exception ex) {
        Log.d(TAG, "onError: " + ex.getMessage());
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
}
