package com.sbai.finance.model.arena;

/**
 * Created by ${wangJie} on 2017/10/25.
 * 竞技场的信息 标题 时间 可获得奖品等信息
 * <p>
 * Client from /activity/activity/findActivityDetail.do
 */

public class ArenaInfo {


    public static final String DEFAULT_ACTIVITY_CODE = "stocktrade01";

    private static final int ACTIVITY_OVER = 1;

    /**
     * activityCode : future_game
     * activityDesc : 期货对战游戏（测试专用）
     * activityName : lalala
     * activityStatus : 1
     * createTime : 1508996966000
     * dayLimit : 1
     * deleted : 0
     * endDate : 1509428988000
     * id : 17
     * limit : 10
     * linkType : 1
     * linkUrl : http://devlm.esongbai.abc/
     * modifyTime : 1509073966000
     * pushContext : 期货对战
     * startDate : 1508996984000
     */

    private String activityCode;  //活动code
    private String activityDesc;  //活动描述
    private String activityName;  //活动名称
    private int activityStatus;   //活动状态   '当前活动状态是否可用(1：待开始，2：活动中，3 活动结束）'
    private long createTime;      //创建时间
    private int dayLimit;         //该活动完成任务的单日限制'
    private int deleted;          //是否删除    0 没有删除  1 删除
    private long endDate;         //活动结束时间
    private int id;
    private int limit;            //该活动的任务总限制
    private String linkType;      //链接类型(链接类型(banner-banner，模板-module，H5-H5)
    private String linkUrl;       //链接地址
    private long modifyTime;      //修改地址
    private String pushContext;   //推送的文案
    private long startDate;       // 活动开始时间


    public boolean isArenaActivityOver() {
        return getDeleted() == ACTIVITY_OVER;
    }

    public String getActivityCode() {
        return activityCode;
    }

    public void setActivityCode(String activityCode) {
        this.activityCode = activityCode;
    }

    public String getActivityDesc() {
        return activityDesc;
    }

    public void setActivityDesc(String activityDesc) {
        this.activityDesc = activityDesc;
    }

    public String getActivityName() {
        return activityName;
    }

    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }

    public int getActivityStatus() {
        return activityStatus;
    }

    public void setActivityStatus(int activityStatus) {
        this.activityStatus = activityStatus;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getDayLimit() {
        return dayLimit;
    }

    public void setDayLimit(int dayLimit) {
        this.dayLimit = dayLimit;
    }

    public int getDeleted() {
        return deleted;
    }

    public void setDeleted(int deleted) {
        this.deleted = deleted;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public String getLinkType() {
        return linkType;
    }

    public void setLinkType(String linkType) {
        this.linkType = linkType;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public long getModifyTime() {
        return modifyTime;
    }

    public void setModifyTime(long modifyTime) {
        this.modifyTime = modifyTime;
    }

    public String getPushContext() {
        return pushContext;
    }

    public void setPushContext(String pushContext) {
        this.pushContext = pushContext;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    @Override
    public String toString() {
        return "ArenaInfo{" +
                "activityCode='" + activityCode + '\'' +
                ", activityDesc='" + activityDesc + '\'' +
                ", activityName='" + activityName + '\'' +
                ", activityStatus=" + activityStatus +
                ", createTime=" + createTime +
                ", dayLimit=" + dayLimit +
                ", deleted=" + deleted +
                ", endDate=" + endDate +
                ", id=" + id +
                ", limit=" + limit +
                ", linkType='" + linkType + '\'' +
                ", linkUrl='" + linkUrl + '\'' +
                ", modifyTime=" + modifyTime +
                ", pushContext='" + pushContext + '\'' +
                ", startDate=" + startDate +
                '}';
    }
}
