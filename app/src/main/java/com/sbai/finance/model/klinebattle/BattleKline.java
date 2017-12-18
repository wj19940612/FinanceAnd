package com.sbai.finance.model.klinebattle;

import android.support.annotation.NonNull;
import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 猜K 对战信息
 */

public class BattleKline {

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


    private List<BattleBean> battleStaList;
    private List<BattleKlineData> userMarkList;

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

    public static class BattleBean implements Comparable<BattleBean> {
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
        private int code;
        private boolean operate;
        private double positions;
        private double profit;
        private int sort;
        private int status;
        private int userId;
        private String userName;
        private String userPortrait;
        private List<BattleBean> otherUsers;

        public List<BattleBean> getOtherUsers() {
            return otherUsers;
        }

        public void setOtherUsers(List<BattleBean> otherUsers) {
            this.otherUsers = otherUsers;
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
    }

}
