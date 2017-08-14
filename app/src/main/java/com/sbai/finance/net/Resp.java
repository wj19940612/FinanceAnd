package com.sbai.finance.net;


import java.util.List;

public class Resp<T> {

    // 注册、登入、找回密码请求验证码次数超过限制
    public static final int CODE_REQUEST_AUTH_CODE_OVER_LIMIT = 601;
    public static final int CODE_GET_PROMOTE_CODE_FAILED = 600;

    // 资金不足
    public static final int CODE_FUND_NOT_ENOUGH = 704;
    // 自选重复添加
    public static final int CODE_REPEAT_ADD = 701;
    // 聚宝盆兑换资金不足
    public static final int CODE_EXCHANGE_FUND_IS_NOT_ENOUGH = 2201;
    // 兑换项目不存在
    public static final int CODE_EXCHANGE_ITEM_IS_GONE = 2204;
    // 兑换项目已修改
    public static final int CODE_EXCHANGE_ITEM_IS_MODIFIED = 2205;
    // 安全密码错误
    public static final int CODE_SAFETY_INPUT_ERROR = 2203;
    public static final int CODE_LIGHTNING_ORDER_INVALID = 703;

    private int code;
    private String msg;
    private int page;
    private int pageSize;
    private int resultCount;
    private int total;

    private T data;

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg() {
        msg = "";
    }

    public T getData() {
        return data;
    }

    public boolean isSuccess() {
        return code == 200;
    }

    public boolean isTokenExpired() {
        return code == 503;
    }

    /**
     * Check if data is null (null or empty if data is a list)
     *
     * @return true if has data
     */
    public boolean hasData() {
        if (data != null && data instanceof List) {
            return ((List) data).size() > 0;
        }
        return data != null;
    }

    @Override
    public String toString() {
        return "Resp{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
