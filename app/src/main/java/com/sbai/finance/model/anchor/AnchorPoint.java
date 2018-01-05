package com.sbai.finance.model.anchor;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.model.product.PayProductInfo;

/**
 * Created by ${wangJie} on 2018/1/2.
 * 主播观点model
 */

public class AnchorPoint implements PayProductInfo, Parcelable {

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
    private int praise;  // 0 没点赞  1 已点赞

    public int getPraise() {
        return praise;
    }

    public void setPraise(int praise) {
        this.praise = praise;
    }

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

    @Override
    public int getProductType() {
        return PRODUCT_TYPE_POINT;
    }

    @Override
    public double getPrice() {
        return getMoney();
    }

    @Override
    public int getProductId() {
        return getId();
    }

    @Override
    public String getProductName() {
        return getName();
    }

    public AnchorPoint() {
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.commentCount);
        dest.writeInt(this.customId);
        dest.writeInt(this.free);
        dest.writeInt(this.id);
        dest.writeString(this.imgUrls);
        dest.writeInt(this.money);
        dest.writeString(this.name);
        dest.writeString(this.portrait);
        dest.writeInt(this.praiseCount);
        dest.writeLong(this.updateTime);
        dest.writeInt(this.userUse);
        dest.writeString(this.viewDesc);
        dest.writeString(this.viewTitle);
        dest.writeInt(this.praise);
    }

    protected AnchorPoint(Parcel in) {
        this.commentCount = in.readInt();
        this.customId = in.readInt();
        this.free = in.readInt();
        this.id = in.readInt();
        this.imgUrls = in.readString();
        this.money = in.readInt();
        this.name = in.readString();
        this.portrait = in.readString();
        this.praiseCount = in.readInt();
        this.updateTime = in.readLong();
        this.userUse = in.readInt();
        this.viewDesc = in.readString();
        this.viewTitle = in.readString();
        this.praise = in.readInt();
    }

    public static final Creator<AnchorPoint> CREATOR = new Creator<AnchorPoint>() {
        @Override
        public AnchorPoint createFromParcel(Parcel source) {
            return new AnchorPoint(source);
        }

        @Override
        public AnchorPoint[] newArray(int size) {
            return new AnchorPoint[size];
        }
    };
}
