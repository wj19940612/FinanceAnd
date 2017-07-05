package com.sbai.finance.websocket;

public interface AbsWsClient {

    void connect();

    void close();

    void send(WSMessage wsMessage);

    void onOpen();

    void onClose();

    void onError(Exception e);

    void onMessage(String message);

    boolean isConnecting();

    boolean isConnected();

    boolean isClosing();

    boolean isClosed();

}
