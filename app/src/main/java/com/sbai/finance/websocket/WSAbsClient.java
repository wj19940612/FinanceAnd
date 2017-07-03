package com.sbai.finance.websocket;

public interface WSAbsClient {

    void onOpen();

    void onMessage(String message);

    void onClose();

    void onError();

    void close();

    void send(WSMessage wsMessage);

    boolean isConnecting();

    boolean isConnected();
}
