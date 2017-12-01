package com.sbai.finance.model.miss;

/**
 * 姐说的消息
 */
public class MissMessage {
    public static final int TYPE_COMMENT = 50;
    public static final int TYPE_REPLY = 51;
    public static final int TYPE_MISS_ANSWER = 52;
    public static final int TYPE_MISS_TOPIC = 56;
    public static final int TYPE_MISS_TOPIC_COMMENT = 57;
    public static final int READ = 1;

    /**
     * msg : 忘了爱
     * classify : 4
     * createTime : 1501665633000
     * sourceUser : {"id":800331,"userName":"用户1188","userPhone":"15158487306"}
     * id : 4530
     * sourceUserId : 800331
     * title : 回复
     * type : 50
     * userId : 800329
     * status : 1
     */

    private String msg;
    private int classify;
    private long createTime;
    private SourceUserBean sourceUser;
    private int id;
    private int sourceUserId;
    private String title;
    private int type;
    private int userId;
    private int status;
    private Data data;
    private int dataId;
    private String mongoId;

    public String getMongoId() {
        return mongoId;
    }

    public void setMongoId(String mongoId) {
        this.mongoId = mongoId;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public boolean isNoRead() {
        return status != READ;
    }

    public Data getData() {
        return data;
    }

    public void setData(Data data) {
        this.data = data;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public SourceUserBean getSourceUser() {
        return sourceUser;
    }

    public void setSourceUser(SourceUserBean sourceUser) {
        this.sourceUser = sourceUser;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(int sourceUserId) {
        this.sourceUserId = sourceUserId;
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

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public static class SourceUserBean {
        /**
         * id : 800331
         * userName : 用户1188
         * userPhone : 15158487306
         */

        private int id;
        private String userName;
        private String userPhone;
        private String name;

        public String getUserPortrait() {
            return userPortrait;
        }

        public void setUserPortrait(String userPortrait) {
            this.userPortrait = userPortrait;
        }

        private String userPortrait;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getPortrait() {
            return portrait;
        }

        public void setPortrait(String portrait) {
            this.portrait = portrait;
        }

        private String portrait;

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

        @Override
        public String toString() {
            return "SourceUserBean{" +
                    "id=" + id +
                    ", userName='" + userName + '\'' +
                    ", userPhone='" + userPhone + '\'' +
                    ", name='" + name + '\'' +
                    ", userPortrait='" + userPortrait + '\'' +
                    ", portrait='" + portrait + '\'' +
                    '}';
        }
    }

    public static class Data{
        private String content;

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        @Override
        public String toString() {
            return "Data{" +
                    "content='" + content + '\'' +
                    '}';
        }
    }

    @Override
    public String toString() {
        return "MissMessage{" +
                "msg='" + msg + '\'' +
                ", classify=" + classify +
                ", createTime=" + createTime +
                ", sourceUser=" + sourceUser +
                ", id=" + id +
                ", sourceUserId=" + sourceUserId +
                ", title='" + title + '\'' +
                ", type=" + type +
                ", userId=" + userId +
                ", status=" + status +
                ", data=" + data +
                ", dataId=" + dataId +
                ", mongoId='" + mongoId + '\'' +
                '}';
    }
}
