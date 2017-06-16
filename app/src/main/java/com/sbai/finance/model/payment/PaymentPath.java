package com.sbai.finance.model.payment;

/**
 * Created by lixiaokuan0819 on 2017/5/16.
 */

public class PaymentPath {

    /**
     * codeUrl : http://123.56.119.177:8081/pay/jsp/pay/payinterface/wei_xin_scan_test.jsp?payordno=qjmQbM72Q3rKU1B
     * thridOrderId : 1001418
     */

    private String codeUrl;
    private String thridOrderId;
    private String platform;

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getCodeUrl() {
        return codeUrl;
    }

    public void setCodeUrl(String codeUrl) {
        this.codeUrl = codeUrl;
    }

    public String getThridOrderId() {
        return thridOrderId;
    }

    public void setThridOrderId(String thridOrderId) {
        this.thridOrderId = thridOrderId;
    }

    @Override
    public String toString() {
        return "PaymentPath{" +
                "codeUrl='" + codeUrl + '\'' +
                ", thridOrderId='" + thridOrderId + '\'' +
                ", platform='" + platform + '\'' +
                '}';
    }
}
