package com.sbai.finance.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by ${wangJie} on 2017/4/24.
 * 消息界面 的经济圈 互助 系统消息的model
 */

public class HistoryNewsModel implements Parcelable {
    //	消息类型{1.系统消息 2.互助消息 3.经济圈消息}
    public static final int NEW_TYPE_SYSTEM_NEWS = 1;
    public static final int NEW_TYPE_MUTUAL_HELP = 2;
    public static final int NEW_TYPE_ECONOMIC_CIRCLE = 3;


    //1 关注 2.点赞帖子 3.点赞评论 4. 评论 *
    public static final int ACTION_TYPE_ATTENTION = 1;
    public static final int ACTION_TYPE_LIKE_POST = 2;
    public static final int ACTION_TYPE_LIKE_COMMENT = 3;
    public static final int ACTION_TYPE_COMMENT = 4;

    // 10.想帮你的人 11拒绝你的人12.接受你帮助的人    // 13.借款单审核未通过 14.借款发布成功 *
    public static final int ACTION_TYPE_WANT_TO_HELP_FOR_YOU = 10;
    public static final int ACTION_TYPE_REFUSE_YOU_PEOPLE = 11;
    public static final int ACTION_TYPE_ACCEPT_YOUR_HELP_PEOPLE = 12;
    public static final int BORROW_MONEY_AUDIT_IS_NOT_PASS = 13;
    public static final int BORROW_MONEY_PUBLISH_SUCCESS = 14;

    // 20.成为观点大神 21.实名认证已通过 22.实名认证未通过 * 25.涨跌预测成功 26.涨跌预测失败
    public static final int BECOME_VIEWPOINT_MANITO = 20;
    public static final int REAL_NAME_APPROVE_PASSED = 21;
    public static final int REAL_NAME_APPROVE_FAILED = 22;
    public static final int FORCAEST_highs_and_lows_fail = 25;
    public static final int FORCAEST_highs_and_lows_success = 26;

    //   30.意向金支付成功
    public static final int THE_EARNEST_MONEY_APY_SUCCESS = 30;
    /**
     * msg : 我
     * classify : 1
     * viewpointId : 31
     * dataId : 11
     * data : {}
     * sourceUser : {"id":78,"userName":"用户235","userPhone":"17317322083","userPortrait":"https://esongtest.oss-cn-shanghai.aliyuncs.com/ueditor/1493705928691.png"}
     * id : 7
     * sourceUserId : 78
     * type : 26
     * userId : 86
     * status : 0
     * createDate : 1493693125000
     */

    private String msg;
    private int classify;
    private int viewpointId;
    private int dataId;
    private DataBean data;
    private UserInfo sourceUser;
    private int id;
    private int sourceUserId;
    private int type;
    private int userId;
    private int status;
    private long createDate;

    public boolean isAlreadyRead() {
        return getStatus() == 1;
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

    public int getViewpointId() {
        return viewpointId;
    }

    public void setViewpointId(int viewpointId) {
        this.viewpointId = viewpointId;
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

    public UserInfo getUserInfo() {
        return sourceUser;
    }

    public void setSourceUser(UserInfo sourceUser) {
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

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
        }

        public DataBean() {
        }

        protected DataBean(Parcel in) {
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

    /**
     * create_date : 35203
     * dataId : 41874
     * id : 1
     * msg : 测试内容8oyc
     * source_user : {"age":1,"id":1,"land":1,"userName":1,"userPhone":1,"userPortrait":1,"userSex":1}
     * status : 30556
     * type : 52888
     */

    //{"msg":"评论......吵吵吵","classify":1,"viewpointId":31,"dataId":13,"source_user":{"id":78,"userPhone":"17317322083"},"id":9,"sourceUserId":78,"type":30,"userId":86,"status":0}
    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.msg);
        dest.writeInt(this.classify);
        dest.writeInt(this.viewpointId);
        dest.writeInt(this.dataId);
        dest.writeParcelable(this.data, flags);
        dest.writeParcelable(this.sourceUser, flags);
        dest.writeInt(this.id);
        dest.writeInt(this.sourceUserId);
        dest.writeInt(this.type);
        dest.writeInt(this.userId);
        dest.writeInt(this.status);
        dest.writeLong(this.createDate);
    }

    public HistoryNewsModel() {
    }

    protected HistoryNewsModel(Parcel in) {
        this.msg = in.readString();
        this.classify = in.readInt();
        this.viewpointId = in.readInt();
        this.dataId = in.readInt();
        this.data = in.readParcelable(DataBean.class.getClassLoader());
        this.sourceUser = in.readParcelable(UserInfo.class.getClassLoader());
        this.id = in.readInt();
        this.sourceUserId = in.readInt();
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

    @Override
    public String toString() {
        return "HistoryNewsModel{" +
                "msg='" + msg + '\'' +
                ", classify=" + classify +
                ", viewpointId=" + viewpointId +
                ", dataId=" + dataId +
                ", data=" + data +
                ", sourceUser=" + sourceUser +
                ", id=" + id +
                ", sourceUserId=" + sourceUserId +
                ", type=" + type +
                ", userId=" + userId +
                ", status=" + status +
                ", createDate=" + createDate +
                '}';
    }
}
