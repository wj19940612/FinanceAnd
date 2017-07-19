package com.sbai.finance.websocket;

public interface Connector {

    void connect();

    boolean isConnecting();

    boolean isConnected();

    void disconnect();

    boolean isDisconnecting();

    void onConnected();

    void onError(String error);

    void onDisconnected();
}
