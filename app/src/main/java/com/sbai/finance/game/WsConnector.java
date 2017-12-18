package com.sbai.finance.game;

public interface WsConnector {

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
