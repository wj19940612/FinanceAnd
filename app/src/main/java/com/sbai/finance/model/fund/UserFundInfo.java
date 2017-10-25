package com.sbai.finance.model.fund;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.net.Client;

/**
 * Modified by john on 25/10/2017
 *
 * {@link Client#requestUserFundInfo()} 用户资金信息
 *
 */
public class UserFundInfo implements Parcelable {


    /**
     * money : 0
     * yuanbao : 0
     * credit : 0
     * userId : 130
     */
    private double money; // 现金
    private double yuanbao; // 元宝
    private double credit; // 积分
    private int userId;

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public long getYuanbao() {
        return (long) yuanbao;
    }

    public void setYuanbao(double yuanbao) {
        this.yuanbao = yuanbao;
    }

    public double getCredit() {
        return credit;
    }

    public void setCredit(double credit) {
        this.credit = credit;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "UserFundInfoModel{" +
                "money=" + money +
                ", yuanbao=" + yuanbao +
                ", credit=" + credit +
                ", userId=" + userId +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(this.money);
        dest.writeDouble(this.yuanbao);
        dest.writeDouble(this.credit);
        dest.writeInt(this.userId);
    }

    public UserFundInfo() {
    }

    protected UserFundInfo(Parcel in) {
        this.money = in.readDouble();
        this.yuanbao = in.readDouble();
        this.credit = in.readDouble();
        this.userId = in.readInt();
    }

    public static final Parcelable.Creator<UserFundInfo> CREATOR = new Parcelable.Creator<UserFundInfo>() {
        @Override
        public UserFundInfo createFromParcel(Parcel source) {
            return new UserFundInfo(source);
        }

        @Override
        public UserFundInfo[] newArray(int size) {
            return new UserFundInfo[size];
        }
    };
}
