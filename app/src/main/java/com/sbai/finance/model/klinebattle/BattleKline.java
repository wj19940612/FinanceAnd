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

    //N 无持仓 Y 有持仓
    public static final String POSITION_HAVE = "Y";
    public static final String POSITION_NO = "N";

    private String battleVarietyCode;
    private String battleVarietyName;
    private String battleStockEndTime;
    private String battleStockStartTime;
    private int line;
    private double rise;
    private long endTime;

    private BattleBean staInfo;
    private List<BattleBean> battleStaList;
    private List<BattleKlineData> userMarkList;

    public BattleBean getStaInfo() {
        return staInfo;
    }

    public void setStaInfo(BattleBean staInfo) {
        this.staInfo = staInfo;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getBattleVarietyCode() {
        return battleVarietyCode;
    }

    public void setBattleVarietyCode(String battleVarietyCode) {
        this.battleVarietyCode = battleVarietyCode;
    }

    public String getBattleVarietyName() {
        return battleVarietyName;
    }

    public void setBattleVarietyName(String battleVarietyName) {
        this.battleVarietyName = battleVarietyName;
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

    public List<BattleBean> getBattleStaList() {
        return battleStaList;
    }

    public void setBattleStaList(List<BattleBean> battleStaList) {
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

    public static class UserInfo {
        private int userId;
        private String userName;
        private String userPortrait;

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
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
    }

    public static class BattleBean implements Comparable<BattleBean>, Parcelable {
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
        private BattleKlineData next;
        private List<UserInfo> userMatchList;

        public BattleKlineData getNext() {
            return next;
        }

        public void setNext(BattleKlineData next) {
            this.next = next;
        }

        public String getBattleType() {
            return battleType;
        }

        public void setBattleType(String battleType) {
            this.battleType = battleType;
        }

        public List<UserInfo> getUserMatch() {
            return userMatchList;
        }

        public void setUserMatchList(List<UserInfo> userMatchList) {
            this.userMatchList = userMatchList;
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
        public int compareTo(@NonNull BattleBean battleBean) {
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
            dest.writeList(this.userMatchList);
            dest.writeString(this.battleType);
        }

        public BattleBean() {
        }

        protected BattleBean(Parcel in) {
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
            this.userMatchList = new ArrayList<UserInfo>();
            in.readList(this.userMatchList, BattleBean.class.getClassLoader());
        }

        public static final Parcelable.Creator<BattleBean> CREATOR = new Parcelable.Creator<BattleBean>() {
            @Override
            public BattleBean createFromParcel(Parcel source) {
                return new BattleBean(source);
            }

            @Override
            public BattleBean[] newArray(int size) {
                return new BattleBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.battleVarietyCode);
        dest.writeString(this.battleVarietyName);
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
        this.battleVarietyCode = in.readString();
        this.battleVarietyName = in.readString();
        this.battleStockEndTime = in.readString();
        this.battleStockStartTime = in.readString();
        this.line = in.readInt();
        this.rise = in.readDouble();
        this.endTime = in.readLong();
        this.battleStaList = in.createTypedArrayList(BattleBean.CREATOR);
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
