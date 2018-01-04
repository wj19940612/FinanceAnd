package com.sbai.finance.model.anchor;

import com.sbai.finance.utils.audio.ProductFreeStatusCode;

/**
 * Created by ${wangJie} on 2018/1/2.
 * 主播观点model
 */

public class AnchorPoint implements ProductFreeStatusCode {


    /**
     * commentCount : 0
     * customId : 51
     * free : 0
     * id : 22
     * imgUrls : http://img3.fengniao.com/album/upload/2/235/46848/9369559.jpg,http://img3.fengniao.com/album/upload/2/235/46848/9369559.jpg
     * money : 3
     * name : T姐
     * portrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20171228/1070i1514453434902.png
     * praiseCount : 0
     * updateTime : 1514963394000
     * userUse : 0
     * viewDesc : 观点副标题
     * viewTitle : 观点标题
     */

    private int commentCount; //评论数
    private int customId;     //主播id
    private int free;         //  0 免费 1 付费
    private int id;
    private String imgUrls;
    private int money;        //收费金额
    private String name;      //主播昵称
    private String portrait;  //主播昵称
    private int praiseCount;  // 点赞
    private long updateTime;
    private int userUse;    //用户是否已购买  0 没有购买  1 购买
    private String viewDesc; //内容
    private String viewTitle;


    public int getCommentCount() {
        return commentCount;
    }

    public void setCommentCount(int commentCount) {
        this.commentCount = commentCount;
    }

    public int getCustomId() {
        return customId;
    }

    public void setCustomId(int customId) {
        this.customId = customId;
    }

    public int getFree() {
        return free;
    }

    public void setFree(int free) {
        this.free = free;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getImgUrls() {
        return imgUrls;
    }

    public void setImgUrls(String imgUrls) {
        this.imgUrls = imgUrls;
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

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

    public int getPraiseCount() {
        return praiseCount;
    }

    public void setPraiseCount(int praiseCount) {
        this.praiseCount = praiseCount;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public int getUserUse() {
        return userUse;
    }

    public void setUserUse(int userUse) {
        this.userUse = userUse;
    }

    public String getViewDesc() {
        return viewDesc;
    }

    public void setViewDesc(String viewDesc) {
        this.viewDesc = viewDesc;
    }

    public String getViewTitle() {
        return viewTitle;
    }

    public void setViewTitle(String viewTitle) {
        this.viewTitle = viewTitle;
    }
}
