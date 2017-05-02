package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/24.
 * 消息界面 的经济圈 互助 系统消息的model
 */

public class HistoryNewsModel {
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
     * create_date : 35203
     * dataId : 41874
     * id : 1
     * msg : 测试内容8oyc
     * source_user : {"age":1,"id":1,"land":1,"userName":1,"userPhone":1,"userPortrait":1,"userSex":1}
     * status : 30556
     * type : 52888
     */

    //{"msg":"评论......吵吵吵","classify":1,"viewpointId":31,"dataId":13,"source_user":{"id":78,"userPhone":"17317322083"},"id":9,"sourceUserId":78,"type":30,"userId":86,"status":0}

    private int create_date;
    private int dataId;
    private int id;
    private String msg;
    //	0. 未读 1.已读
    private int status;
    //1 关注 2.点赞帖子 3.点赞评论 4. 评论 *
    // 10.想帮你的人 11拒绝你的人12.接受你帮助的人
    // 13.借款单审核未通过 14.借款发布成功 *
    // 20.成为观点大神 21.实名认证已通过 22.实名认证未通过 * 25.涨跌预测成功 26.涨跌预测失败 * 30.意向金支付成功
    private int type;
    private int classify;
    private int viewpointId;
    private UserInfo source_user;
    private int sourceUserId;
    private int userId;

    /**
     * classify : 1
     * viewpointId : 31
     * source_user : {"id":78,"userPhone":"17317322083"}
     * sourceUserId : 78
     * userId : 86
     */


    public HistoryNewsModel() {
    }

    public HistoryNewsModel(int status, int type) {
        this.status = status;
        this.type = type;
    }

    public HistoryNewsModel(int status, int type, UserInfo userInfo) {
        this.status = status;
        this.type = type;
        this.source_user = userInfo;
    }

    public HistoryNewsModel(int id, int status, int type, UserInfo userInfo) {
        this.status = status;
        this.type = type;
        source_user = userInfo;
        this.id = id;
    }


    public boolean isAlreadyRead() {
        return getStatus() == 1;
    }

    public int getCreate_date() {
        return create_date;
    }

    public void setCreate_date(int create_date) {
        this.create_date = create_date;
    }

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public UserInfo getUserInfo() {
        return source_user;
    }

    public void setSource_user(UserInfo source_user) {
        this.source_user = source_user;
    }

    public int getSourceUserId() {
        return sourceUserId;
    }

    public void setSourceUserId(int sourceUserId) {
        this.sourceUserId = sourceUserId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "HistoryNewsModel{" +
                "create_date=" + create_date +
                ", dataId=" + dataId +
                ", id=" + id +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", classify=" + classify +
                ", viewpointId=" + viewpointId +
                ", source_user=" + source_user +
                ", sourceUserId=" + sourceUserId +
                ", userId=" + userId +
                '}';
    }
}
