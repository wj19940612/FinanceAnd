package com.sbai.finance.model.klinebattle;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.List;

/**
 * 猜K 对战信息
 */

public class BattleKline implements Parcelable {

    @StringDef({TYPE_1V1, TYPE_4V4, TYPE_EXERCISE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface BattleType {
    }

    //推送code
    public static final int PUSH_CODE_MATCH_FAILED = 8101;//匹配失败
    public static final int PUSH_CODE_MATCH_SUCCESS = 8102;//匹配成功
    public static final int PUSH_CODE_BATTLE_FINISH = 8103;//游戏对战结束
    public static final int PUSH_CODE_AGAINST_PROFIT = 8104;//其他用户盈利情况

    public static final String TYPE_EXERCISE = "exercise";//本地自己训练的游戏类型
    public static final String TYPE_1V1 = "1v1";
    public static final String TYPE_4V4 = "4v4";
    // 0 已结束 1对战中
    public static final int STATUS_END = 0;
    public static final int STATUS_BATTLEING = 1;

    //B 买 S卖 P观望
    public static final String BUY = "B";
    public static final String SELL = "S";
    public static final String PASS = "P";

    private String battleStockCode;
    private String battleStockName;
    private String battleStockEndTime;
    private String battleStockStartTime;
    private int line;
    private double rise;
    private long endTime;

    private List<BattleKlineInfo> battleStaList;
    private List<BattleKlineData> userMarkList;
    private BattleKlineInfo staInfo;

    public BattleKlineInfo getStaInfo() {
        return staInfo;
    }

    public void setStaInfo(BattleKlineInfo staInfo) {
        this.staInfo = staInfo;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getBattleStockCode() {
        return battleStockCode;
    }

    public void setBattleStockCode(String battleStockCode) {
        this.battleStockCode = battleStockCode;
    }

    public String getBattleStockName() {
        return battleStockName;
    }

    public void setBattleStockName(String battleStockName) {
        this.battleStockName = battleStockName;
    }

    public String getBattleStockEndTime() {
        return battleStockEndTime;
    }

    public void setBattleStockEndTime(String battleStockEndTime) {
        this.battleStockEndTime = battleStockEndTime;
    }

    public String getBattleStockStartTime() {
        return battleStockStartTime;
    }

    public void setBattleStockStartTime(String battleStockStartTime) {
        this.battleStockStartTime = battleStockStartTime;
    }

    public int getLine() {
        return line;
    }

    public void setLine(int line) {
        this.line = line;
    }

    public List<BattleKlineInfo> getBattleStaList() {
        return battleStaList;
    }

    public void setBattleStaList(List<BattleKlineInfo> battleStaList) {
        this.battleStaList = battleStaList;
    }

    public List<BattleKlineData> getUserMarkList() {
        return userMarkList;
    }

    public void setUserMarkList(List<BattleKlineData> userMarkList) {
        this.userMarkList = userMarkList;
    }

    public double getRise() {
        return rise;
    }

    public void setRise(double rise) {
        this.rise = rise;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.battleStockCode);
        dest.writeString(this.battleStockName);
        dest.writeString(this.battleStockEndTime);
        dest.writeString(this.battleStockStartTime);
        dest.writeInt(this.line);
        dest.writeDouble(this.rise);
        dest.writeLong(this.endTime);
        dest.writeTypedList(this.battleStaList);
        dest.writeTypedList(this.userMarkList);
    }

    public BattleKline() {
    }

    protected BattleKline(Parcel in) {
        this.battleStockCode = in.readString();
        this.battleStockName = in.readString();
        this.battleStockEndTime = in.readString();
        this.battleStockStartTime = in.readString();
        this.line = in.readInt();
        this.rise = in.readDouble();
        this.endTime = in.readLong();
        this.battleStaList = in.createTypedArrayList(BattleKlineInfo.CREATOR);
        this.userMarkList = in.createTypedArrayList(BattleKlineData.CREATOR);
    }

    public static final Parcelable.Creator<BattleKline> CREATOR = new Parcelable.Creator<BattleKline>() {
        @Override
        public BattleKline createFromParcel(Parcel source) {
            return new BattleKline(source);
        }

        @Override
        public BattleKline[] newArray(int size) {
            return new BattleKline[size];
        }
    };
}
