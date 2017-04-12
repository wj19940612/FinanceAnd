package com.sbai.finance.net;

import com.sbai.httplib.ApiParams;

import static com.android.volley.Request.Method.POST;

public class Client {

    public static API getHoldingOrderList() {
        return new API("/order/forexOrder/getVarietyPositionOrders.do");
    }

    public static API sendMessage() {
        return new API(POST, "baidu", new ApiParams());
    }

}
