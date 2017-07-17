package com.sbai.finance.model.payment;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by lixiaokuan0819 on 2017/5/16.
 */

public class UsablePlatform implements Parcelable {

    public static final int TYPE_AIL_PAY = 1;
    public static final int TYPE_WECHAT_PAY = 2;
    public static final int TYPE_BANK_PAY = 3;

    public static final String PLATFORM_AIL_PAY = "alipay";

    /**
     * createTime : 1494402852000
     * id : 24
     * name : 钱通支付宝
     * payment : 1
     * platform : qtalipay
     * status : 1
     * transfer : 0
     * updateTime : 1494564420000
     */

//	data=[UsablePlatform{createTime=0, id=0, name='支付宝支付', payment=0, platform='qtalipay', status=1, transfer=0, updateTime=0, type=1},
// UsablePlatform{createTime=0, id=0, name='微信支付', payment=0, platform='qtwxscan', status=1, transfer=0, updateTime=0, type=2},
// UsablePlatform{createTime=0, id=0, name='银行卡支付', payment=0, platform='qtbankcardpay', status=1, transfer=0, updateTime=0, type=3}]}


    private long createTime;
    private int id;
    private String name;
    private int payment;
    private String platform;
    private int status;
    private int transfer;
    private long updateTime;
    //1 支付宝
    private int type;
    private boolean isSelectPayWay;

    public boolean isSelectPayWay() {
        return isSelectPayWay;
    }

    public void setSelectPayWay(boolean selectPayWay) {
        isSelectPayWay = selectPayWay;
    }

    public boolean isWeChatPay() {
        return getType() == TYPE_WECHAT_PAY;
    }

    public boolean isAliPay() {
        return getType() == TYPE_AIL_PAY;
    }

    public boolean isBankPay() {
        return getType() == TYPE_BANK_PAY;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getPayment() {
        return payment;
    }

    public void setPayment(int payment) {
        this.payment = payment;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getTransfer() {
        return transfer;
    }

    public void setTransfer(int transfer) {
        this.transfer = transfer;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    @Override
    public String toString() {
        return "UsablePlatform{" +
                "createTime=" + createTime +
                ", id=" + id +
                ", name='" + name + '\'' +
                ", payment=" + payment +
                ", platform='" + platform + '\'' +
                ", status=" + status +
                ", transfer=" + transfer +
                ", updateTime=" + updateTime +
                ", type=" + type +
                ", isSelectPayWay=" + isSelectPayWay +
                '}';
    }

    public UsablePlatform() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.createTime);
        dest.writeInt(this.id);
        dest.writeString(this.name);
        dest.writeInt(this.payment);
        dest.writeString(this.platform);
        dest.writeInt(this.status);
        dest.writeInt(this.transfer);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.type);
        dest.writeByte(this.isSelectPayWay ? (byte) 1 : (byte) 0);
    }

    protected UsablePlatform(Parcel in) {
        this.createTime = in.readLong();
        this.id = in.readInt();
        this.name = in.readString();
        this.payment = in.readInt();
        this.platform = in.readString();
        this.status = in.readInt();
        this.transfer = in.readInt();
        this.updateTime = in.readLong();
        this.type = in.readInt();
        this.isSelectPayWay = in.readByte() != 0;
    }

    public static final Creator<UsablePlatform> CREATOR = new Creator<UsablePlatform>() {
        @Override
        public UsablePlatform createFromParcel(Parcel source) {
            return new UsablePlatform(source);
        }

        @Override
        public UsablePlatform[] newArray(int size) {
            return new UsablePlatform[size];
        }
    };
}
