package com.sbai.finance.model.fund;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lixiaokuan0819 on 2017/5/16.
 */

public class PaymentPath implements Parcelable{

    /**
     * codeUrl : http://123.56.119.177:8081/pay/jsp/pay/payinterface/wei_xin_scan_test.jsp?payordno=qjmQbM72Q3rKU1B
     * thridOrderId : 1001418
     */


    private String codeUrl;
    private String thridOrderId;
    private String platform;
    private long time;
    /**
     * money : 1
     * merchantOrderId : qt13020170619105657
     */

    /**
     * {"money":1,"merchantOrderId":"qt13020170619105657","time":1497841020392,"platform":"qtbankcardpay"}
     */

    private int money;
    //银行卡充值第三方id
    private String merchantOrderId;

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

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public String getMerchantOrderId() {
        return merchantOrderId;
    }

    public void setMerchantOrderId(String merchantOrderId) {
        this.merchantOrderId = merchantOrderId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.codeUrl);
        dest.writeString(this.thridOrderId);
        dest.writeString(this.platform);
        dest.writeLong(this.time);
        dest.writeInt(this.money);
        dest.writeString(this.merchantOrderId);
    }

    public PaymentPath() {
    }

    protected PaymentPath(Parcel in) {
        this.codeUrl = in.readString();
        this.thridOrderId = in.readString();
        this.platform = in.readString();
        this.time = in.readLong();
        this.money = in.readInt();
        this.merchantOrderId = in.readString();
    }

    public static final Creator<PaymentPath> CREATOR = new Creator<PaymentPath>() {
        @Override
        public PaymentPath createFromParcel(Parcel source) {
            return new PaymentPath(source);
        }

        @Override
        public PaymentPath[] newArray(int size) {
            return new PaymentPath[size];
        }
    };

    @Override
    public String toString() {
        return "PaymentPath{" +
                "codeUrl='" + codeUrl + '\'' +
                ", thridOrderId='" + thridOrderId + '\'' +
                ", platform='" + platform + '\'' +
                ", time=" + time +
                ", money=" + money +
                ", merchantOrderId='" + merchantOrderId + '\'' +
                '}';
    }
}
