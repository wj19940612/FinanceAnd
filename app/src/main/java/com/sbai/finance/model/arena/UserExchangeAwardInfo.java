package com.sbai.finance.model.arena;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.sbai.finance.net.Client;

/**
 * Created by ${wangJie} on 2017/11/2.
 * {@link Client# /activity/prize/exchangedPrizeList.do}
 * 用户兑换奖品信息
 */

public class UserExchangeAwardInfo {

    public static final int AWARD_TYPE_PLATFORM_CURRENCY = 1;
    public static final int AWARD_TYPE_ENTITYY = 2;
    public static final int AWARD_TYPE_VIRTUAL_PRODUCT = 3;

    public static final int EXCHANGE_STATUS_APPLY_FOR = 1;
    public static final int EXCHANGE_STATUS_SUCCESS = 2;
    public static final int EXCHANGE_STATUS_FAIL = 3;
    public static final int EXCHANGE_STATUS_ALREADY_GRANT = 4;

    /**
     * activityCode : future_game
     * createTime : 1509370605000
     * id : 3
     * modifyTime : 1509413181000
     * prizeId : 11
     * prizeType : 2
     * status : 3
     * userId : 691
     */

    private String activityCode;
    private long createTime;
    private int id;
    private long modifyTime;
    private int prizeId;
    private int prizeType;        //奖品类型 1平台货币2、实物3、虚拟奖品
    private int status;           //1、申请2、审批通过3、审批未通过4、已发奖
    private int userId;
    private String prizeParam;    // 奖品状态  用户提交的个人信   { "exchangephone":110,"wxqq":154514,"skin":"shin","gamename":"昵称","gamezone":"圣诞节客户端","useraddress":"浙江温州"} ",
    private String prize_content; //兑奖奖品内容


    public UserGameInfo getUserGameInfo() {
        if (!TextUtils.isEmpty(getPrizeParam())) {
            try {
                return new Gson().fromJson(getPrizeParam(), UserGameInfo.class);
            } catch (JsonSyntaxException e) {
                return null;
            }
        }
        return null;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public int getPrizeId() {
        return prizeId;
    }

    public void setPrizeId(int prizeId) {
        this.prizeId = prizeId;
    }

    public int getPrizeType() {
        return prizeType;
    }

    public void setPrizeType(int prizeType) {
        this.prizeType = prizeType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getPrizeParam() {
        return prizeParam;
    }

    public void setPrizeParam(String prizeParam) {
        this.prizeParam = prizeParam;
    }

    public String getPrize_content() {
        return prize_content;
    }

    public void setPrize_content(String prize_content) {
        this.prize_content = prize_content;
    }

    @Override
    public String toString() {
        return "UserExchangeAwardInfo{" +
                "activityCode='" + activityCode + '\'' +
                ", createTime=" + createTime +
                ", id=" + id +
                ", modifyTime=" + modifyTime +
                ", prizeId=" + prizeId +
                ", prizeType=" + prizeType +
                ", status=" + status +
                ", userId=" + userId +
                ", prizeParam='" + prizeParam + '\'' +
                ", prize_content='" + prize_content + '\'' +
                '}';
    }
}
