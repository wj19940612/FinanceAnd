package com.sbai.finance.websocket;

import com.google.gson.Gson;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.utils.DateUtil;

import java.util.UUID;

public class WMessage<T> {

    /**
     * code : 1000
     * uuid : xxkjdfasfa-fds-fds-fds
     * timestamp : 1882821872121
     * content : {"cmd":"/battle/selectBattleGaming.do","parameters":{"k1":"v1"}}
     */

    private int code;
    private String uuid;
    private long timestamp;
    private T content;

    public WMessage(int code, T content) {
        this.code = code;
        this.timestamp = SysTime.getSysTime().getSystemTimestamp();
        this.uuid = createUUID();
        this.content = content;
    }

    private String createUUID() {
        String uuid = UUID.randomUUID().toString();
        String time = DateUtil.format(timestamp, "yyyyMMdd_HHmmss");
        return uuid + "-" + time;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public T getContent() {
        return content;
    }

    public void setContent(T content) {
        this.content = content;
    }

    public String toJsonString() {
        return new Gson().toJson(this);
    }
}
