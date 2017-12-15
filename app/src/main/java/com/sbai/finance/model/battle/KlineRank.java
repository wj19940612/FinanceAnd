package com.sbai.finance.model.battle;

import java.util.List;

/**
 * Created by Administrator on 2017\12\13 0013.
 */

public class KlineRank {

    private List<Rank4v4> rank4v4;
    private List<Rank1V1> rank1v1;
    private UserRank1v1 userRank1v1;
    private UserRank4v4 userRank4v4;

    public List<Rank4v4> getRank4v4() {
        return rank4v4;
    }

    public void setRank4v4(List<Rank4v4> rank4v4) {
        this.rank4v4 = rank4v4;
    }

    public List<Rank1V1> getRank1v1() {
        return rank1v1;
    }

    public void setRank1v1(List<Rank1V1> rank1v1) {
        this.rank1v1 = rank1v1;
    }

    public UserRank1v1 getUserRank1v1() {
        return userRank1v1;
    }

    public void setUserRank1v1(UserRank1v1 userRank1v1) {
        this.userRank1v1 = userRank1v1;
    }

    public UserRank4v4 getUserRank4v4() {
        return userRank4v4;
    }

    public void setUserRank4v4(UserRank4v4 userRank4v4) {
        this.userRank4v4 = userRank4v4;
    }

    public class Rank4v4 {  //4v4总排行
        private int one;  //第一奖牌数
        private int two;    //第二奖牌数
        private int three;  //第三奖牌数
        private int userId; //用户id
        private String userName;  //用户昵称
        private String userPortrait;   //头像

        public int getOne() {
            return one;
        }

        public void setOne(int one) {
            this.one = one;
        }

        public int getTwo() {
            return two;
        }

        public void setTwo(int two) {
            this.two = two;
        }

        public int getThree() {
            return three;
        }

        public void setThree(int three) {
            this.three = three;
        }

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

    public class Rank1V1 {
        private float rankingRate;  //1v1胜率
        private int userId; //用户id
        private String userName;  //用户昵称
        private String userPortrait;   //头像

        public float getRankingRate() {
            return rankingRate;
        }

        public void setRankingRate(float rankingRate) {
            this.rankingRate = rankingRate;
        }

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

    public class UserRank1v1 {
        private int battle1v1Count;
        private String userId;
        private String userName;
        private String userPortrait;
        private int sort;
        private float rankingRate;  //当前用户1v1 胜率

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
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

        public float getRankingRate() {
            return rankingRate;
        }

        public void setRankingRate(float rankingRate) {
            this.rankingRate = rankingRate;
        }

        public int getBattle1v1Count() {
            return battle1v1Count;
        }

        public void setBattle1v1Count(int battle1v1Count) {
            this.battle1v1Count = battle1v1Count;
        }

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }

    public class UserRank4v4 {
        private int battle4v4Count;
        private String userId;
        private String userName;
        private String userPortrait;
        private int sort;
        private int one; //当前用户4v4 第一奖牌数
        private int two; //当前用户4v4 第二奖牌数
        private int three; //当前用户4v4 第三奖牌数

        public int getOne() {
            return one;
        }

        public void setOne(int one) {
            this.one = one;
        }

        public int getTwo() {
            return two;
        }

        public void setTwo(int two) {
            this.two = two;
        }

        public int getThree() {
            return three;
        }

        public void setThree(int three) {
            this.three = three;
        }

        public int getBattle4v4Count() {
            return battle4v4Count;
        }

        public void setBattle4v4Count(int battle4v4Count) {
            this.battle4v4Count = battle4v4Count;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
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

        public int getSort() {
            return sort;
        }

        public void setSort(int sort) {
            this.sort = sort;
        }
    }
}
