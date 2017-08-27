package com.sbai.finance.model.system;

/**
 * Created by ${wangJie} on 2017/8/25.
 */

public class ServiceConnectWay {

    private String qq;
    private String weixin;

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getWeixin() {
        return weixin;
    }

    public void setWeixin(String weixin) {
        this.weixin = weixin;
    }

    @Override
    public String toString() {
        return "ServiceConnectWay{" +
                "qq='" + qq + '\'' +
                ", weixin='" + weixin + '\'' +
                '}';
    }
}
