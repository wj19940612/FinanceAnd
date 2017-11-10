package com.sbai.finance.model.leaderboard;

import java.io.Serializable;
import java.util.List;

/**
 * 排行榜数据
 */

public class LeaderBoardRank implements Serializable {
    public static final String INGOT = "gold";
    public static final String PROFIT = "profit";
    public static final String SAVANT = "appraise";
    public static final String TODAY = "day";
    public static final String WEEK = "week";
    private String type;
    private CurrBean curr;
    private List<DataBean> data;

    public CurrBean getCurr() {
        return curr;
    }

    public void setCurr(CurrBean curr) {
        this.curr = curr;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public static class DataBean implements Serializable {
        /**
         * score : 9999994.2
         * user : {"id":171,"userName":"用户2464","userPhone":"17853127907"}
         */

        private double score;
        private int worshipCount;
        private boolean isWorship;
        private UserBean user;

        public int getWorshipCount() {
            return worshipCount;
        }

        public void setWorshipCount(int worshipCount) {
            this.worshipCount = worshipCount;
        }

        public boolean isWorship() {
            return isWorship;
        }

        public void setWorship(boolean worship) {
            isWorship = worship;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }

        public UserBean getUser() {
            return user;
        }

        public void setUser(UserBean user) {
            this.user = user;
        }

        public static class UserBean implements Serializable {
            /**
             * id : 171
             * userName : 用户2464
             * userPhone : 17853127907
             */

            private int id;
            private String userName;
            private String userPhone;
            private String userPortrait;

            public String getUserPortrait() {
                return userPortrait;
            }

            public void setUserPortrait(String userPortrait) {
                this.userPortrait = userPortrait;
            }

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

            public String getUserPhone() {
                return userPhone;
            }

            public void setUserPhone(String userPhone) {
                this.userPhone = userPhone;
            }
        }
    }

    public static class CurrBean implements Serializable {
        private int no;
        private double score;

        public int getNo() {
            return no;
        }

        public void setNo(int no) {
            this.no = no;
        }

        public double getScore() {
            return score;
        }

        public void setScore(double score) {
            this.score = score;
        }
    }
}
