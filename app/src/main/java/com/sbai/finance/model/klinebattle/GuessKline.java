package com.sbai.finance.model.klinebattle;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;

/**
 * 猜K 对战信息
 */

public class GuessKline {

    @StringDef({TYPE_1V1, TYPE_4V4, TYPE_EXERCISE})
    @Retention(RetentionPolicy.SOURCE)
    public @interface GuessType {
    }

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

    private List<BattleBean> battleStaList;
    private List<UserMarkBean> userMarkList;

    public List<BattleBean> getBattleStaList() {
        return battleStaList;
    }

    public void setBattleStaList(List<BattleBean> battleStaList) {
        this.battleStaList = battleStaList;
    }

    public List<UserMarkBean> getUserMarkList() {
        return userMarkList;
    }

    public void setUserMarkList(List<UserMarkBean> userMarkList) {
        this.userMarkList = userMarkList;
    }

    public static class BattleBean {
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
        private String code;
        private boolean operate;
        private double positions;
        private double profit;
        private int sort;
        private int status;
        private int userId;
        private String userName;
        private String userPortrait;

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

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
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
    }

    public static class UserMarkBean {
        /**
         * bettleId : 19
         * closePrice : 3373.95
         * day : 2015-01-07
         * id : 1
         * mark : N
         * maxPrice : 3374.9
         * minPrice : 3312.21
         * nowVolume : 391918880
         * openPrice : 3326.65
         * positions : 0.0
         * profit : 0.0
         * userId : 1070
         */

        private int battleId;
        private double closePrice;
        private String day;
        private int id;
        private String mark;
        private double maxPrice;
        private double minPrice;
        private int nowVolume;
        private double openPrice;
        private double positions;
        private double profit;
        private int userId;

        public int getBattleId() {
            return battleId;
        }

        public void setBattleId(int battleId) {
            this.battleId = battleId;
        }

        public double getClosePrice() {
            return closePrice;
        }

        public void setClosePrice(double closePrice) {
            this.closePrice = closePrice;
        }

        public String getDay() {
            return day;
        }

        public void setDay(String day) {
            this.day = day;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getMark() {
            return mark;
        }

        public void setMark(String mark) {
            this.mark = mark;
        }

        public double getMaxPrice() {
            return maxPrice;
        }

        public void setMaxPrice(double maxPrice) {
            this.maxPrice = maxPrice;
        }

        public double getMinPrice() {
            return minPrice;
        }

        public void setMinPrice(double minPrice) {
            this.minPrice = minPrice;
        }

        public int getNowVolume() {
            return nowVolume;
        }

        public void setNowVolume(int nowVolume) {
            this.nowVolume = nowVolume;
        }

        public double getOpenPrice() {
            return openPrice;
        }

        public void setOpenPrice(double openPrice) {
            this.openPrice = openPrice;
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

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }
    }
}
