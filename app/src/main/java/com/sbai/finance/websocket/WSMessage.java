package com.sbai.finance.websocket;

import com.google.gson.Gson;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.websocket.callback.WSCallback;

import java.util.UUID;

public class WSMessage<T> extends WSRequest {

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

    public WSMessage(int code, T content, WSCallback wsCallback) {
        super(wsCallback, false);
        this.code = code;
        this.timestamp = SysTime.getSysTime().getSystemTimestamp();
        this.uuid = createUUID();
        this.content = content;
    }

    public WSMessage(int code, T content) {
        super(false);
        this.code = code;
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

    public String toJson() {
        return new Gson().toJson(this);
    }

    @Override
    public String toString() {
        return "WSMessage{" +
                "code=" + code +
                ", uuid='" + uuid + '\'' +
                ", timestamp=" + timestamp +
                ", content=" + content +
                '}';
    }
}
