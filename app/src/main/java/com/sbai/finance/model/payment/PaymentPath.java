package com.sbai.finance.model.payment;

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

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

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
                ", time=" + time +
                '}';
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
    }

    public PaymentPath() {
    }

    protected PaymentPath(Parcel in) {
        this.codeUrl = in.readString();
        this.thridOrderId = in.readString();
        this.platform = in.readString();
        this.time = in.readLong();
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
}
