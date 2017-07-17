package com.sbai.finance.socket;

public interface Subscriber {

    int REQ_QUOTA = 100;
    int REQ_HEART = 200;

    int REQ_SUB = 101;
    int REQ_UNSUB = 102;

    int REQ_SUB_ALL = 103;
    int REQ_UNSUB_ALL = 104;

    void subscribe(String varietyType);

    void subscribeAll();

    void unSubscribeAll();

    void unSubscribe(String varietyType);

    void shutdown();

    boolean isConnected();

    void connect();

    boolean isConnecting();
}