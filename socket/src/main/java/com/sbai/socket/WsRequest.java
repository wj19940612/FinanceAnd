package com.sbai.socket;

import com.google.gson.Gson;

/**
 * Modified by john on 15/12/2017
 * <p>
 * todo unfinished
 */
public class WsRequest<T> implements WsReqCode {

    private int code;
    private String uuid;
    private long timestamp;
    private String url;
    private String method;
    private T parameter;

    public WsRequest(int code, T parameter) {
        this.code = code;
        this.parameter = parameter;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String toJson() {
        return new Gson().toJson(this);
    }
}
