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
    public static final int ACTION_TYPE_APY = 30;


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
    //详细信息
    private String msg;
    //	0. 未读 1.已读
    private int status;
    //	1 关注 2.点赞帖子 3.点赞评论 4. 评论 10.想帮你的人 11拒绝你的人12.接受你帮
    private int type;
    //消息关联的id 根据type判断
    private int dataId;

    private UserInfo mUserInfo;

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

    public int getDataId() {
        return dataId;
    }

    public void setDataId(int dataId) {
        this.dataId = dataId;
    }

    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
    }

    @Override
    public String toString() {
        return "HistoryNewsModel{" +
                "id=" + id +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                ", type=" + type +
                ", dataId=" + dataId +
                ", mUserInfo=" + mUserInfo +
                '}';
    }
}
