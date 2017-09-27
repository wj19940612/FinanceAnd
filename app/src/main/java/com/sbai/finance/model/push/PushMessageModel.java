package com.sbai.finance.model.push;

import com.google.gson.annotations.SerializedName;

/**
 * Created by ${wangJie} on 2017/5/4.
 */

public class PushMessageModel {

    private static final int CLASSIFY_SYS = 0;
    private static final int CLASSIFY_USER = 1;

    // 关注的小姐姐回复问题了
    public static final int PUSH_TYPE_ATTENTION_MISS_ANSWERED = 10;
    //日报
    public static final int PUSH_TYPE_DAILY_REPORT = 11;
    //小姐姐回复你的问题了
    public static final int PUSH_TYPE_MISS_ANSWER_YOUR_QUESTION = 12;
    //训练
    public static final int PUSH_TYPE_TRAINING = 13;
    //自习室
    public static final int PUSH_TYPE_SELF_STUDY_ROOM= 14;
    //活动
    public static final int PUSH_TYPE_ACTIVITY= 15;
    //意见反馈
    public static final int PUSH_TYPE_FEED_BVACK_REPLY= 16;


    //日报
    private static final int TYPE_REPORT = 0;

//    "msg":"对战匹配成功,赶紧加入吧！","classify":2,"data":"hall","createTime":1499052945482,"title":"对战匹配成功","type":1

    /**
     * classify : 14761
     * createTime : 54111
     * data : {}
     * dataId : 10734
     * iconUrl : 测试内容q9s8
     * msg : 测试内容ln2q
     * title : 测试内容84hj
     * type : 64687
     * url : 测试内容cn15
     */

    private int classify;
    private long createTime;
    private String dataId;
    private String iconUrl;
    private String msg;
    private String title;
    private int type;
    private String url;
    /**
     * data : {"coinType":2,"reward":200,"againstPraise":0,"gameStatus":2,"againstFrom":"hall","batchCode":"HV0SIOYf","launchScore":0,"againstScore":0,"launchPraise":0,"varietyType":"CL","againstUserName":"溺水的鱼","launchUser":154,"launchUserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1498556534650.png","againstUserPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1498716924314.png","bigVarietyType":"future","endline":60,"modifyTime":1499068428000,"createTime":1499068428000,"varietyId":1,"startTime":1499068438215,"id":538,"varietyName":"美原油","launchUserName":"用户2559","againstUser":130}
     */

    private DataBean data;


    public boolean isDailyReportDetail() {
        return getClassify() == PushMessageModel.CLASSIFY_SYS && getType() == PushMessageModel.TYPE_REPORT;
    }


    public boolean isBattleMatchSuccess() {
        return getClassify() == 2 && getType() == 1;
    }

    //小姐姐回复你的问题
    public boolean isMissAnswer() {
        return getClassify() == 4 && getType() == 1;
    }

    public boolean isStudy() {
        return getClassify() == 0 && getType() == 2;
    }

    public boolean isFeedBackInfo() {
        return getClassify() == 2 && getType() == 2;
    }

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }


    public String getDataId() {
        return dataId;
    }

    public void setDataId(String dataId) {
        this.dataId = dataId;
    }

    public String getIconUrl() {
        return iconUrl;
    }

    public void setIconUrl(String iconUrl) {
        this.iconUrl = iconUrl;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "PushMessageModel{" +
                "classify=" + classify +
                ", createTime=" + createTime +
                ", dataId=" + dataId +
                ", iconUrl='" + iconUrl + '\'' +
                ", msg='" + msg + '\'' +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", url='" + url + '\'' +
                '}';
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
    }

    public static class DataBean {
        /**
         * coinType : 2
         * reward : 200
         * againstPraise : 0
         * gameStatus : 2
         * againstFrom : hall
         * batchCode : HV0SIOYf
         * launchScore : 0
         * againstScore : 0
         * launchPraise : 0
         * varietyType : CL
         * againstUserName : 溺水的鱼
         * launchUser : 154
         * launchUserPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1498556534650.png
         * againstUserPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1498716924314.png
         * bigVarietyType : future
         * endline : 60
         * modifyTime : 1499068428000
         * createTime : 1499068428000
         * varietyId : 1
         * startTime : 1499068438215
         * id : 538
         * varietyName : 美原油
         * launchUserName : 用户2559
         * againstUser : 130
         */

        private int coinType;
        private int reward;
        private int againstPraise;
        private int gameStatus;
        private String againstFrom;
        private String batchCode;
        private int launchScore;
        private int againstScore;
        private int launchPraise;
        private String varietyType;
        private String againstUserName;
        private int launchUser;
        private String launchUserPortrait;
        private String againstUserPortrait;
        private String bigVarietyType;
        private int endline;
        private long modifyTime;
        @SerializedName("createTime")
        private long createTimeX;
        private int varietyId;
        private long startTime;
        private int id;
        private String varietyName;
        private String launchUserName;
        private int againstUser;

        public int getCoinType() {
            return coinType;
        }

        public void setCoinType(int coinType) {
            this.coinType = coinType;
        }

        public int getReward() {
            return reward;
        }

        public void setReward(int reward) {
            this.reward = reward;
        }

        public int getAgainstPraise() {
            return againstPraise;
        }

        public void setAgainstPraise(int againstPraise) {
            this.againstPraise = againstPraise;
        }

        public int getGameStatus() {
            return gameStatus;
        }

        public void setGameStatus(int gameStatus) {
            this.gameStatus = gameStatus;
        }

        public String getAgainstFrom() {
            return againstFrom;
        }

        public void setAgainstFrom(String againstFrom) {
            this.againstFrom = againstFrom;
        }

        public String getBatchCode() {
            return batchCode;
        }

        public void setBatchCode(String batchCode) {
            this.batchCode = batchCode;
        }

        public int getLaunchScore() {
            return launchScore;
        }

        public void setLaunchScore(int launchScore) {
            this.launchScore = launchScore;
        }

        public int getAgainstScore() {
            return againstScore;
        }

        public void setAgainstScore(int againstScore) {
            this.againstScore = againstScore;
        }

        public int getLaunchPraise() {
            return launchPraise;
        }

        public void setLaunchPraise(int launchPraise) {
            this.launchPraise = launchPraise;
        }

        public String getVarietyType() {
            return varietyType;
        }

        public void setVarietyType(String varietyType) {
            this.varietyType = varietyType;
        }

        public String getAgainstUserName() {
            return againstUserName;
        }

        public void setAgainstUserName(String againstUserName) {
            this.againstUserName = againstUserName;
        }

        public int getLaunchUser() {
            return launchUser;
        }

        public void setLaunchUser(int launchUser) {
            this.launchUser = launchUser;
        }

        public String getLaunchUserPortrait() {
            return launchUserPortrait;
        }

        public void setLaunchUserPortrait(String launchUserPortrait) {
            this.launchUserPortrait = launchUserPortrait;
        }

        public String getAgainstUserPortrait() {
            return againstUserPortrait;
        }

        public void setAgainstUserPortrait(String againstUserPortrait) {
            this.againstUserPortrait = againstUserPortrait;
        }

        public String getBigVarietyType() {
            return bigVarietyType;
        }

        public void setBigVarietyType(String bigVarietyType) {
            this.bigVarietyType = bigVarietyType;
        }

        public int getEndline() {
            return endline;
        }

        public void setEndline(int endline) {
            this.endline = endline;
        }

        public long getModifyTime() {
            return modifyTime;
        }

        public void setModifyTime(long modifyTime) {
            this.modifyTime = modifyTime;
        }

        public long getCreateTimeX() {
            return createTimeX;
        }

        public void setCreateTimeX(long createTimeX) {
            this.createTimeX = createTimeX;
        }

        public int getVarietyId() {
            return varietyId;
        }

        public void setVarietyId(int varietyId) {
            this.varietyId = varietyId;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public String getVarietyName() {
            return varietyName;
        }

        public void setVarietyName(String varietyName) {
            this.varietyName = varietyName;
        }

        public String getLaunchUserName() {
            return launchUserName;
        }

        public void setLaunchUserName(String launchUserName) {
            this.launchUserName = launchUserName;
        }

        public int getAgainstUser() {
            return againstUser;
        }

        public void setAgainstUser(int againstUser) {
            this.againstUser = againstUser;
        }
    }
}
