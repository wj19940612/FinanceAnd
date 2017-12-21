package com.sbai.finance.model.klinebattle;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

/**
 * 对战信息
 */

public class BattleKlineInfo implements Comparable<BattleKlineInfo>, Parcelable {
    /**
     * battleId : 19
     * battleStatus : 1
     * code : win
     * operate : false
     * positions : 0.0
     * profit : 0.0
     * sort : 0
     * status : 1
     * userId : 1070
     */

    private int battleId;
    private int battleStatus;
    private String battleType;
    private int code;
    private boolean operate;
    private double positions;
    private double profit;
    private int sort;
    private int status;
    private int userId;
    private String userName;
    private String userPortrait;


    public String getBattleType() {
        return battleType;
    }

    public void setBattleType(String battleType) {
        this.battleType = battleType;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public int getBattleId() {
        return battleId;
    }

    public void setBattleId(int battleId) {
        this.battleId = battleId;
    }

    public int getBattleStatus() {
        return battleStatus;
    }

    public void setBattleStatus(int battleStatus) {
        this.battleStatus = battleStatus;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public boolean isOperate() {
        return operate;
    }

    public void setOperate(boolean operate) {
        this.operate = operate;
    }

    public double getPositions() {
        return positions;
    }

    public void setPositions(double positions) {
        this.positions = positions;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int compareTo(@NonNull BattleKlineInfo battleBean) {
        return Double.compare(this.profit, battleBean.getProfit());
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.battleId);
        dest.writeInt(this.battleStatus);
        dest.writeInt(this.code);
        dest.writeByte(this.operate ? (byte) 1 : (byte) 0);
        dest.writeDouble(this.positions);
        dest.writeDouble(this.profit);
        dest.writeInt(this.sort);
        dest.writeInt(this.status);
        dest.writeInt(this.userId);
        dest.writeString(this.userName);
        dest.writeString(this.userPortrait);
        dest.writeString(this.battleType);
    }

    public BattleKlineInfo() {
    }

    protected BattleKlineInfo(Parcel in) {
        this.battleId = in.readInt();
        this.battleStatus = in.readInt();
        this.code = in.readInt();
        this.operate = in.readByte() != 0;
        this.positions = in.readDouble();
        this.profit = in.readDouble();
        this.sort = in.readInt();
        this.status = in.readInt();
        this.userId = in.readInt();
        this.userName = in.readString();
        this.userPortrait = in.readString();
        this.battleType = in.readString();
    }

    public static final Parcelable.Creator<BattleKlineInfo> CREATOR = new Parcelable.Creator<BattleKlineInfo>() {
        @Override
        public BattleKlineInfo createFromParcel(Parcel source) {
            return new BattleKlineInfo(source);
        }

        @Override
        public BattleKlineInfo[] newArray(int size) {
            return new BattleKlineInfo[size];
        }
    };
}
