package com.sbai.socket;

public interface WsConnector {

    void connect();

    void close();

    void send(WsRequest request);

    void onOpen();

    void onClose();

    void onError(Exception e);

    void onMessage(String message);

    boolean isConnecting();

    boolean isConnected();

    boolean isClosing();

    boolean isClosed();

}
