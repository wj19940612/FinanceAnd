package com.sbai.finance.model.klinebattle;

/**
 * 我的记录
 */

public class BattleKlineMyRecord {

    /**
     * userRank1v1 : {"battle1v1Count":1,"rankingRate":14,"winIngots1v1":-100}
     * userRank4v4 : {"battle4v4Count":200,"one":1,"rankingRate":0,"three":3,"two":2,"winIngots4v4":200}
     */

    private UserRank1v1Bean userRank1v1;
    private UserRank4v4Bean userRank4v4;

    public UserRank1v1Bean getUserRank1v1() {
        return userRank1v1;
    }

    public void setUserRank1v1(UserRank1v1Bean userRank1v1) {
        this.userRank1v1 = userRank1v1;
    }

    public UserRank4v4Bean getUserRank4v4() {
        return userRank4v4;
    }

    public void setUserRank4v4(UserRank4v4Bean userRank4v4) {
        this.userRank4v4 = userRank4v4;
    }

    public static class UserRank1v1Bean {
        /**
         * battle1v1Count : 1
         * rankingRate : 14
         * winIngots1v1 : -100
         */

        private int battle1v1Count;
        private int rankingRate;
        private int winIngots1v1;

        public int getBattle1v1Count() {
            return battle1v1Count;
        }

        public void setBattle1v1Count(int battle1v1Count) {
            this.battle1v1Count = battle1v1Count;
        }

        public int getRankingRate() {
            return rankingRate;
        }

        public void setRankingRate(int rankingRate) {
            this.rankingRate = rankingRate;
        }

        public int getWinIngots1v1() {
            return winIngots1v1;
        }

        public void setWinIngots1v1(int winIngots1v1) {
            this.winIngots1v1 = winIngots1v1;
        }
    }

    public static class UserRank4v4Bean {
        /**
         * battle4v4Count : 200
         * one : 1
         * rankingRate : 0
         * three : 3
         * two : 2
         * winIngots4v4 : 200
         */

        private int battle4v4Count;
        private int one;
        private int rankingRate;
        private int three;
        private int two;
        private int winIngots4v4;

        public int getBattle4v4Count() {
            return battle4v4Count;
        }

        public void setBattle4v4Count(int battle4v4Count) {
            this.battle4v4Count = battle4v4Count;
        }

        public int getOne() {
            return one;
        }

        public void setOne(int one) {
            this.one = one;
        }

        public int getRankingRate() {
            return rankingRate;
        }

        public void setRankingRate(int rankingRate) {
            this.rankingRate = rankingRate;
        }

        public int getThree() {
            return three;
        }

        public void setThree(int three) {
            this.three = three;
        }

        public int getTwo() {
            return two;
        }

        public void setTwo(int two) {
            this.two = two;
        }

        public int getWinIngots4v4() {
            return winIngots4v4;
        }

        public void setWinIngots4v4(int winIngots4v4) {
            this.winIngots4v4 = winIngots4v4;
        }
    }
}
