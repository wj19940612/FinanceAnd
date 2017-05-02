package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/19.
 * 被屏蔽的用户model
 */

public class ShieldedUserModel {

    /**
     * createTime : 74861
     * id : 88525
     * shielduserId : 66584
     * shielduserName : 测试内容4749
     * shielduserPortrait : 测试内容a320
     */

    private int createTime;
    private int id;
    //	屏蔽用户id
    private int shielduserId;
    //屏蔽用户名
    private String shielduserName;
    private String shielduserPortrait;

    public int getCreateTime() {
        return createTime;
    }

    public void setCreateTime(int createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getShielduserId() {
        return shielduserId;
    }

    public void setShielduserId(int shielduserId) {
        this.shielduserId = shielduserId;
    }

    public String getShielduserName() {
        return shielduserName;
    }

    public void setShielduserName(String shielduserName) {
        this.shielduserName = shielduserName;
    }

    public String getShielduserPortrait() {
        return shielduserPortrait;
    }

    public void setShielduserPortrait(String shielduserPortrait) {
        this.shielduserPortrait = shielduserPortrait;
    }

    @Override
    public String toString() {
        return "ShieldedUserModel{" +
                "createTime=" + createTime +
                ", id=" + id +
                ", shielduserId=" + shielduserId +
                ", shielduserName='" + shielduserName + '\'' +
                ", shielduserPortrait='" + shielduserPortrait + '\'' +
                '}';
    }
}
