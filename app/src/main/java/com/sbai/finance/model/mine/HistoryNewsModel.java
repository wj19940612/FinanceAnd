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


    //	1 关注 2.点赞帖子 3.点赞评论 4. 评论 10.想帮你的人 11拒绝你的人12.接受你帮
    public static final int ACTION_TYPE_ATTENTION = 1;
    public static final int ACTION_TYPE_LIKE_POST = 2;
    public static final int ACTION_TYPE_LIKE_COMMENT = 3;
    public static final int ACTION_TYPE_COMMENT = 4;
    public static final int ACTION_TYPE_WANT_TO_HELP_FOR_YOU = 10;
    public static final int ACTION_TYPE_REFUSE_YOU_PEOPLE = 11;
    public static final int ACTION_TYPE_ACCEPT_YOUR_HELP_PEOPLE = 12;


    /**
     * id : 57702
     * loan_id : 48775
     * msg : 测试内容53g3
     * pay_id : 60135
     * reply_id : 70682
     * source_user_id : 测试内容1623
     * status : 10775
     * type : 14331
     * viewpoint_id : 77214
     */

    //	消息id
    private int id;
    //借单id
    private int loan_id;
    //详细信息
    private String msg;
    //支付id
    private int pay_id;
    //评论id
    private int reply_id;
    //	触发这个消息的用户(非当前用户)
    private String source_user_id;
    //	0. 未读 1.已读
    private int status;
    //	1 关注 2.点赞帖子 3.点赞评论 4. 评论 10.想帮你的人 11拒绝你的人12.接受你帮
    private int type;
    //观点id
    private int viewpoint_id;

    // TODO: 2017/4/24 测试用


    public HistoryNewsModel(String userImage) {
        this.userImage = userImage;
    }

    private String userImage;

    public String getUserImage() {
        return userImage;
    }

    public void setUserImage(String userImage) {
        this.userImage = userImage;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLoan_id() {
        return loan_id;
    }

    public void setLoan_id(int loan_id) {
        this.loan_id = loan_id;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public int getPay_id() {
        return pay_id;
    }

    public void setPay_id(int pay_id) {
        this.pay_id = pay_id;
    }

    public int getReply_id() {
        return reply_id;
    }

    public void setReply_id(int reply_id) {
        this.reply_id = reply_id;
    }

    public String getSource_user_id() {
        return source_user_id;
    }

    public void setSource_user_id(String source_user_id) {
        this.source_user_id = source_user_id;
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

    public int getViewpoint_id() {
        return viewpoint_id;
    }

    public void setViewpoint_id(int viewpoint_id) {
        this.viewpoint_id = viewpoint_id;
    }

    public boolean isAlreadlyRead() {
        return getStatus() == 1;
    }

    @Override
    public String toString() {
        return "HistoryNewsModel{" +
                "id=" + id +
                ", loan_id=" + loan_id +
                ", msg='" + msg + '\'' +
                ", pay_id=" + pay_id +
                ", reply_id=" + reply_id +
                ", source_user_id='" + source_user_id + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", viewpoint_id=" + viewpoint_id +
                '}';
    }
}
