package com.sbai.finance.game;

public class WSPush<T> {
    /**
     * code : 3000
     * uuid : xxxx
     * content : {"type":11,"data":{"k1":"v1"}}
     */

    private int code;
    private String uuid;
    private PushData<T> content;

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

    public PushData getContent() {
        return content;
    }

    public void setContent(PushData content) {
        this.content = content;
    }

    public static class PushData<T> {
        /**
         * type : 11
         * data : {"k1":"v1"}
         */

        private int type;
        private T data;

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }
    }

    @Override
    public String toString() {
        return "WSPush{" +
                "code=" + code +
                ", uuid='" + uuid + '\'' +
                ", content=" + content +
                '}';
    }
}
