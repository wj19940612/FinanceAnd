package com.sbai.finance.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${wangJie} on 2017/4/24.
 * 消息界面 的经济圈 互助 系统消息的model
 */

public class HistoryNewsModel implements Parcelable {
    //	消息类型 1系统 2观点 3借款
    public static final String NEW_TYPE_SYSTEM_NEWS = "1";
    public static final String NEW_TYPE_ECONOMIC_CIRCLE_NEWS = "2,3";


    //1 关注 2.点赞帖子 3.点赞评论 4. 评论 *
    public static final int ACTION_TYPE_ATTENTION = 1;
    public static final int ACTION_TYPE_LIKE_POST = 2;
    public static final int ACTION_TYPE_LIKE_COMMENT = 3;
    public static final int ACTION_TYPE_COMMENT = 4;

    // 10.想帮你的人 11拒绝你的人12.接受你帮助的人
    public static final int ACTION_TYPE_WANT_TO_HELP_FOR_YOU = 10;
    public static final int ACTION_TYPE_REFUSE_YOU_PEOPLE = 11;
    public static final int ACTION_TYPE_ACCEPT_YOUR_HELP_PEOPLE = 12;

    // 13.借款单审核未通过 14.借款发布成功 * 15.借款超时 16.借款取消 *
    public static final int BORROW_MONEY_AUDIT_IS_NOT_PASS = 13;
    public static final int BORROW_MONEY_PUBLISH_SUCCESS = 14;
    public static final int BORROW_MONEY_TIME_OUT = 15;
    public static final int BORROW_MONEY_CANCEL = 16;

    // 20.成为观点大神 21.实名认证已通过 22.实名认证未通过 * 25.涨跌预测成功 26.涨跌预测失败
    public static final int BECOME_VIEWPOINT_MANITO = 20;
    public static final int REAL_NAME_APPROVE_PASSED = 21;
    public static final int REAL_NAME_APPROVE_FAILED = 22;
    public static final int FORCAEST_highs_and_lows_fail = 25;
    public static final int FORCAEST_highs_and_lows_success = 26;

    //   30.意向金支付成功
    public static final int THE_EARNEST_MONEY_APY_SUCCESS = 30;
    /**
     * classify : 2
     * dataId : 252
     * data : {"contentImg":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1495431415800.png"}
     * sourceUser : {"id":254,"userName":"DEER","userPhone":"18888888881","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1495431080405.png"}
     * id : 2298
     * sourceUserId : 254
     * title : 感谢并婉拒了你的帮助
     * type : 11
     * userId : 185
     * status : 1
     * createDate : 1495453263000
     */

    private int classify;
    private int dataId;
    private DataBean data;
    private SourceUserBean sourceUser;
    private int id;
    private int sourceUserId;
    private String title;
    private int type;
    private int userId;
    private int status;
    private long createDate;


    public boolean isTheEarnestMoneyPaySuccess() {
        return getType() == THE_EARNEST_MONEY_APY_SUCCESS;
    }

    public boolean isNotRead() {
        return getStatus() == 0;
    }

    //如果失效或者借款取消，超时 不进行跳转
    public boolean isLossEfficacy() {
        return getStatus() == 3 ||
                getType() == BORROW_MONEY_TIME_OUT
                || getType() == BORROW_MONEY_CANCEL;
    }

    public int getClassify() {
        return classify;
    }

    public void setClassify(int classify) {
        this.classify = classify;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public DataBean getData() {
        return data;
    }

    public void setData(DataBean data) {
        this.data = data;
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

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }


    public static class DataBean implements Parcelable {
        /**
         * contentImg : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1495431415800.png
         */

        private String contentImg;


        public String getContentImg() {
            return contentImg;
        }

        public void setContentImg(String contentImg) {
            this.contentImg = contentImg;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.contentImg);
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
            this.contentImg = in.readString();
        }

        public static final Creator<DataBean> CREATOR = new Creator<DataBean>() {
            @Override
            public DataBean createFromParcel(Parcel source) {
                return new DataBean(source);
            }

            @Override
            public DataBean[] newArray(int size) {
                return new DataBean[size];
            }
        };
    }

    public static class SourceUserBean implements Parcelable {
        /**
         * id : 254
         * userName : DEER
         * userPhone : 18888888881
         * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1495431080405.png
         */

        private int id;
        private String userName;
        private String userPhone;
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

        public String getUserPhone() {
            return userPhone;
        }

        public void setUserPhone(String userPhone) {
            this.userPhone = userPhone;
        }

        public String getUserPortrait() {
            return userPortrait;
        }

        public void setUserPortrait(String userPortrait) {
            this.userPortrait = userPortrait;
        }

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeInt(this.id);
            dest.writeString(this.userName);
            dest.writeString(this.userPhone);
            dest.writeString(this.userPortrait);
        }

        public SourceUserBean() {
        }

        protected SourceUserBean(Parcel in) {
            this.id = in.readInt();
            this.userName = in.readString();
            this.userPhone = in.readString();
            this.userPortrait = in.readString();
        }

        public static final Creator<SourceUserBean> CREATOR = new Creator<SourceUserBean>() {
            @Override
            public SourceUserBean createFromParcel(Parcel source) {
                return new SourceUserBean(source);
            }

            @Override
            public SourceUserBean[] newArray(int size) {
                return new SourceUserBean[size];
            }
        };
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.classify);
        dest.writeInt(this.dataId);
        dest.writeParcelable(this.data, flags);
        dest.writeParcelable(this.sourceUser, flags);
        dest.writeInt(this.id);
        dest.writeInt(this.sourceUserId);
        dest.writeString(this.title);
        dest.writeInt(this.type);
        dest.writeInt(this.userId);
        dest.writeInt(this.status);
        dest.writeLong(this.createDate);
    }

    public HistoryNewsModel() {
    }

    protected HistoryNewsModel(Parcel in) {
        this.classify = in.readInt();
        this.dataId = in.readInt();
        this.data = in.readParcelable(DataBean.class.getClassLoader());
        this.sourceUser = in.readParcelable(SourceUserBean.class.getClassLoader());
        this.id = in.readInt();
        this.sourceUserId = in.readInt();
        this.title = in.readString();
        this.type = in.readInt();
        this.userId = in.readInt();
        this.status = in.readInt();
        this.createDate = in.readLong();
    }

    public static final Creator<HistoryNewsModel> CREATOR = new Creator<HistoryNewsModel>() {
        @Override
        public HistoryNewsModel createFromParcel(Parcel source) {
            return new HistoryNewsModel(source);
        }

        @Override
        public HistoryNewsModel[] newArray(int size) {
            return new HistoryNewsModel[size];
        }
    };
}
