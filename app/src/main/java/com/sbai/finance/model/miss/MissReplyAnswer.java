package com.sbai.finance.model.miss;

import com.sbai.finance.net.Client;
import com.sbai.finance.utils.audio.MissAudioManager;

import java.util.List;

/**
 * Created by ${wangJie} on 2017/12/1.
 * {@link Client#/explain/questionApp/replyDetail.do}
 */

public class MissReplyAnswer implements MissAudioManager.IAudio {

    public static final int QUESTION_SOLVE_STATUS_ALREADY_SOLVE = 1;

    /**
     * askSavant : 0
     * createTime : 1507717141000
     * hidden : 0
     * id : 564
     * questionContext : 一人
     * questionUserId : 1087
     * replyVO : [{"customContext":"https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171012/等我回家 (Live).mp3","customId":22,"customName":"阿拉蕾","customPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171129/726i1511948758699.png","id":564,"questionId":564,"replyTime":1507775416000,"soundTime":294,"type":0}]
     * show : 0
     * solve : 1
     * userName : PANDA盼
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1505284907490.png
     */

    private int askSavant;
    private long createTime;
    private int hidden;
    private int id;
    private String questionContext;
    private int questionUserId;   //提问人的id
    private int show;
    private int solve;            // 为题解决状态 1 解决 0为解决
    private String userName;
    private String userPortrait;  //提问人头像
    private List<ReplyVOBean> replyVO;
    private int appointCustomId;
    private int userType;

    //自己加的
    private String audioPath;
    private int audioId;

    public int getUserType() {
        return userType;
    }

    public void setUserType(int userType) {
        this.userType = userType;
    }

    public void setAudioId(int audioId) {
        this.audioId = audioId;
    }

    public String getAudioPath() {
        return audioPath;
    }

    public void setAudioPath(String audioPath) {
        this.audioPath = audioPath;
    }

    public void setMissPortrait(String missPortrait) {
        this.audioPath = missPortrait;
    }

    public int getAskSavant() {
        return askSavant;
    }

    public void setAskSavant(int askSavant) {
        this.askSavant = askSavant;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getHidden() {
        return hidden;
    }

    public void setHidden(int hidden) {
        this.hidden = hidden;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getQuestionContext() {
        return questionContext;
    }

    public void setQuestionContext(String questionContext) {
        this.questionContext = questionContext;
    }

    public int getQuestionUserId() {
        return questionUserId;
    }

    public void setQuestionUserId(int questionUserId) {
        this.questionUserId = questionUserId;
    }

    public int getShow() {
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }

    public int getSolve() {
        return solve;
    }

    public void setSolve(int solve) {
        this.solve = solve;
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

    public List<ReplyVOBean> getReplyVO() {
        return replyVO;
    }

    public void setReplyVO(List<ReplyVOBean> replyVO) {
        this.replyVO = replyVO;
    }

    public int getAppointCustomId() {
        return appointCustomId;
    }

    public void setAppointCustomId(int appointCustomId) {
        this.appointCustomId = appointCustomId;
    }

    @Override
    public int getAudioId() {
        return audioId;
    }

    @Override
    public String getAudioUrl() {
        return audioPath;
    }

    @Override
    public String getAvatar() {
        return null;
    }

    public static class ReplyVOBean {
        /**
         * customContext : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171012/等我回家 (Live).mp3
         * customId : 22
         * customName : 阿拉蕾
         * customPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171129/726i1511948758699.png
         * id : 564
         * questionId : 564
         * replyTime : 1507775416000
         * soundTime : 294
         * type : 0
         */

        private String customContext;
        private int customId;
        private String customName;
        private String customPortrait;
        private int id;
        private int questionId;
        private long replyTime;
        private int soundTime;
        private int type;

        public String getCustomContext() {
            return customContext;
        }

        public void setCustomContext(String customContext) {
            this.customContext = customContext;
        }

        public int getCustomId() {
            return customId;
        }

        public void setCustomId(int customId) {
            this.customId = customId;
        }

        public String getCustomName() {
            return customName;
        }

        public void setCustomName(String customName) {
            this.customName = customName;
        }

        public String getCustomPortrait() {
            return customPortrait;
        }

        public void setCustomPortrait(String customPortrait) {
            this.customPortrait = customPortrait;
        }

        public int getId() {
            return id;
        }

        public void setId(int id) {
            this.id = id;
        }

        public int getQuestionId() {
            return questionId;
        }

        public void setQuestionId(int questionId) {
            this.questionId = questionId;
        }

        public long getReplyTime() {
            return replyTime;
        }

        public void setReplyTime(long replyTime) {
            this.replyTime = replyTime;
        }

        public int getSoundTime() {
            return soundTime;
        }

        public void setSoundTime(int soundTime) {
            this.soundTime = soundTime;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        @Override
        public String toString() {
            return "ReplyVOBean{" +
                    "customContext='" + customContext + '\'' +
                    ", customId=" + customId +
                    ", customName='" + customName + '\'' +
                    ", customPortrait='" + customPortrait + '\'' +
                    ", id=" + id +
                    ", questionId=" + questionId +
                    ", replyTime=" + replyTime +
                    ", soundTime=" + soundTime +
                    ", type=" + type +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MissReplyAnswer{" +
                "askSavant=" + askSavant +
                ", createTime=" + createTime +
                ", hidden=" + hidden +
                ", id=" + id +
                ", questionContext='" + questionContext + '\'' +
                ", questionUserId=" + questionUserId +
                ", show=" + show +
                ", solve=" + solve +
                ", userName='" + userName + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                ", replyVO=" + replyVO +
                ", appointCustomId=" + appointCustomId +
                '}';
    }
}
