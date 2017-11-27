package com.sbai.finance.model.stock;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Modified by john on 24/11/2017
 * <p>
 * Description: 股票账户数据结构
 * <p>
 * APIs:
 */
public class StockUser implements Parcelable {

    public static final int ACCOUNT_TYPE_REAL = 1; // 实盘
    public static final int ACCOUNT_TYPE_MOCK = 2; // 模拟
    public static final int ACCOUNT_TYPE_ACTI = 3; // 活动

    public static final int VIRTUAL_TYPE_SCORE = 1; // 积分

    public static final int ACCOUNT_ACTIVE = 1;//激活的账户

    /**
     * account : MA104214
     * accountName : 米宝63535
     * active : 1
     * activityCode : test
     * expiryDate : 2017-11-30 10:35:52
     * fund : 1000
     * id : 219
     * status : 1
     * type : 3
     * usableMoney : 1000
     * userId : 1123
     * virtualType : 1
     */


    private String account;
    private String accountName;
    private int active;
    private String activityCode;
    private String expiryDate; // 该账户过期时间
    private double fund; // 原始资金
    private int id;
    private int status;
    private int type;
    private double usableMoney; // 可用资金
    private int userId;
    private int virtualType; // 虚拟账户类型

    public String getAccount() {
        return account;
    }

    public String getAccountName() {
        return accountName;
    }

    public int getActive() {
        return active;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public double getFund() {
        return fund;
    }

    public int getId() {
        return id;
    }

    public int getStatus() {
        return status;
    }

    public int getType() {
        return type;
    }

    public double getUsableMoney() {
        return usableMoney;
    }

    public int getUserId() {
        return userId;
    }

    public int getVirtualType() {
        return virtualType;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.account);
        dest.writeString(this.accountName);
        dest.writeInt(this.active);
        dest.writeString(this.activityCode);
        dest.writeString(this.expiryDate);
        dest.writeDouble(this.fund);
        dest.writeInt(this.id);
        dest.writeInt(this.status);
        dest.writeInt(this.type);
        dest.writeDouble(this.usableMoney);
        dest.writeInt(this.userId);
        dest.writeInt(this.virtualType);
    }

    public StockUser() {
    }

    protected StockUser(Parcel in) {
        this.account = in.readString();
        this.accountName = in.readString();
        this.active = in.readInt();
        this.activityCode = in.readString();
        this.expiryDate = in.readString();
        this.fund = in.readDouble();
        this.id = in.readInt();
        this.status = in.readInt();
        this.type = in.readInt();
        this.usableMoney = in.readDouble();
        this.userId = in.readInt();
        this.virtualType = in.readInt();
    }

    public static final Parcelable.Creator<StockUser> CREATOR = new Parcelable.Creator<StockUser>() {
        @Override
        public StockUser createFromParcel(Parcel source) {
            return new StockUser(source);
        }

        @Override
        public StockUser[] newArray(int size) {
            return new StockUser[size];
        }
    };
}
