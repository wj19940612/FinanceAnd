package com.sbai.finance.model.anchor;

/**
 * Created by ${wangJie} on 2018/1/2.
 * 主播观点model
 */

public class AnchorPoint {

    private long time;
    private String anchorName;
    private String pointTitle;
    private String pointContent;
    private int prise;
    private int review;
    private String anchorPortrait;
    private String imageContent;
    private int id;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getAnchorName() {
        return anchorName;
    }

    public void setAnchorName(String anchorName) {
        this.anchorName = anchorName;
    }

    public String getPointTitle() {
        return pointTitle;
    }

    public void setPointTitle(String pointTitle) {
        this.pointTitle = pointTitle;
    }

    public String getPointContent() {
        return pointContent;
    }

    public void setPointContent(String pointContent) {
        this.pointContent = pointContent;
    }

    public int getPrise() {
        return prise;
    }

    public void setPrise(int prise) {
        this.prise = prise;
    }

    public int getReview() {
        return review;
    }

    public void setReview(int review) {
        this.review = review;
    }

    public String getAnchorPortrait() {
        return anchorPortrait;
    }

    public void setAnchorPortrait(String anchorPortrait) {
        this.anchorPortrait = anchorPortrait;
    }

    public String getImageContent() {
        return imageContent;
    }

    public void setImageContent(String imageContent) {
        this.imageContent = imageContent;
    }
}
