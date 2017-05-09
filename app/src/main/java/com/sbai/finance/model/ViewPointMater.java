package com.sbai.finance.model;

/**
 * Created by Administrator on 2017-05-09.
 */

public class ViewPointMater {
    public static final String TYPE_FUTURE="future";
    public static final String TYPE_FOREX="forex";
    public static final String TYPE_STOCK="stock";

    /**
     * adeptType : 测试内容84x8
     * introduction : 测试内容ksws
     * land : 测试内容wukg
     * userEmail : 测试内容u8p3
     * userName : 测试内容k6qp
     * userPortrait : 测试内容7s9u
     * userSex : 26641
     * userSign : 测试内容i64p
     * passCount : 2
     * passRat : 0.6667
     * sumCount : 3
     * userId : 93
     */

    private String adeptType;
    private String introduction;
    private String land;
    private String userEmail;
    private String userName;
    private String userPortrait;
    private int userSex;
    private String userSign;
    private int passCount;
    private double passRat;
    private int sumCount;
    private int userId;

    public String getAdeptType() {
        return adeptType;
    }

    public void setAdeptType(String adeptType) {
        this.adeptType = adeptType;
    }

    public String getIntroduction() {
        return introduction;
    }

    public void setIntroduction(String introduction) {
        this.introduction = introduction;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getUserSign() {
        return userSign;
    }

    public void setUserSign(String userSign) {
        this.userSign = userSign;
    }

    public int getPassCount() {
        return passCount;
    }

    public void setPassCount(int passCount) {
        this.passCount = passCount;
    }

    public double getPassRat() {
        return passRat;
    }

    public void setPassRat(double passRat) {
        this.passRat = passRat;
    }

    public int getSumCount() {
        return sumCount;
    }

    public void setSumCount(int sumCount) {
        this.sumCount = sumCount;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }
}
