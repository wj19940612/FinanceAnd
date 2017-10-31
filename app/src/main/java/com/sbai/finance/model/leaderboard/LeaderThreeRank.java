package com.sbai.finance.model.leaderboard;

/**
 * Created by Administrator on 2017\10\27 0027.
 */

/**
 * 排行榜数据,新接口，只返回三个榜首
 */
public class LeaderThreeRank {
    public static final String INGOT = "gold";
    public static final String PROFIT = "profit";
    public static final String SAVANT = "appraise";


    /**
     * score : 1012
     * type : appraise
     * userId : 846
     * user : {"id":846,"userName":"黄三","userPortrait":"http://wx.qlogo.cn/mmopen/6Fz3aS6k35aYiade1A7JjAu7msUojMwIhLojfsJAnzwYjKxm2f3thWXic6KCOia667vEx5eKXcVW4rmg3qMzo7cLg/0"}
     */

    private int score;
    private String type;
    private int userId;
    private UserBean user;
    private boolean isWorship;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public UserBean getUser() {
        return user;
    }

    public void setUser(UserBean user) {
        this.user = user;
    }

    public boolean isWorship() {
        return isWorship;
    }

    public void setWorship(boolean worship) {
        isWorship = worship;
    }

    public static class UserBean {
        /**
         * id : 846
         * userName : 黄三
         * userPortrait : http://wx.qlogo.cn/mmopen/6Fz3aS6k35aYiade1A7JjAu7msUojMwIhLojfsJAnzwYjKxm2f3thWXic6KCOia667vEx5eKXcVW4rmg3qMzo7cLg/0
         */

        private int id;
        private String userName;
        private String userPortrait;

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
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
}
