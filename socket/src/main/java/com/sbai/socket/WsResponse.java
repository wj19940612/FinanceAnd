package com.sbai.socket;

/**
 * Modified by john on 15/12/2017
 *
 * socket: http response and push response
 */
public class WsResponse<T> implements WsRespCode {

    private int code;
    private String uuid;
    private long timestamp;
    private T content;
    private String contentType;
    private long msgId;
    private String message;

    public long getMsgId() {
        return msgId;
    }

    public int getCode() {
        return code;
    }

    public T getContent() {
        return content;
    }

    @Override
    public String toString() {
        return "WsResponse{" +
                "code=" + code +
                ", uuid='" + uuid + '\'' +
                ", timestamp=" + timestamp +
                ", content=" + content +
                ", contentType='" + contentType + '\'' +
                ", msgId=" + msgId +
                ", message='" + message + '\'' +
                '}';
    }
}
