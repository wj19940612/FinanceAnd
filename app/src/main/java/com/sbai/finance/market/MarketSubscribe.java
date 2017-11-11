package com.sbai.finance.market;

public interface MarketSubscribe {

    int REQ_QUOTA = 100;
    int REQ_HEART = 201;
    int REQ_HEART_UP = 202;

    int REQ_SUB = 101;
    int REQ_UNSUB = 102;

    int REQ_SUB_ALL = 103;
    int REQ_UNSUB_ALL = 104;

    void subscribe(String varietyType);

    void subscribeAll();

    void unSubscribeAll();

    void unSubscribe(String varietyType);

    void onMessage(String message);
}
