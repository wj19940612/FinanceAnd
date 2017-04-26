package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/17.
 */

public class UserInfo {

    public static final int CREDIT_IS_ALREADY_APPROVE = 1;
    public static final int CREDIT_IS_APPROVEING = 2;

    /**
     * agencyId : 0
     * certificationStatus : 0
     * createTime : 1493090329000
     * id : 45
     * lastLoginTime : 1493103457000
     * loginErrorNum : 0
     * loginIp : 115.194.106.176
     * loginNum : 7
     * modifyNickNameTimes : 0
     * registrationIp : 115.194.106.176
     * status : 0
     */

    private String userName;
    private Integer age;
    private String land;
    private String userPhone;
    private boolean bIsSetNickName;
    //用户头像网址
    private String userPortrait;
    private int agencyId;
    private int certificationStatus;
    private long createTime;
    private int id;
    private long lastLoginTime;
    private int loginErrorNum;
    private String loginIp;
    private int loginNum;
    private int modifyNickNameTimes;
    private String registrationIp;
    //	0 未认证 1以认证 2待审核
    private int status;
    //1 女 2男
    private int userSex;
    private String chinaSex;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public boolean isbIsSetNickName() {
        return bIsSetNickName;
    }

    public void setbIsSetNickName(boolean bIsSetNickName) {
        this.bIsSetNickName = bIsSetNickName;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    public void updateLocalUserInfo(UserDetailInfo userDetailInfo) {
        setUserName(userDetailInfo.getUserName());
        setAge(userDetailInfo.getAge());
        setbIsSetNickName(userDetailInfo.isBIsSetNickName());
        setLand(userDetailInfo.getLand());
        setUserPortrait(userDetailInfo.getUserPortrait());
        setChinaSex(userDetailInfo.getChinaSex());
        setUserSex(userDetailInfo.getUserSex());
        setStatus(userDetailInfo.getStatus());
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public int getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(int certificationStatus) {
        this.certificationStatus = certificationStatus;
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

    public long getLastLoginTime() {
        return lastLoginTime;
    }

    public void setLastLoginTime(long lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
    }

    public int getLoginErrorNum() {
        return loginErrorNum;
    }

    public void setLoginErrorNum(int loginErrorNum) {
        this.loginErrorNum = loginErrorNum;
    }

    public String getLoginIp() {
        return loginIp;
    }

    public void setLoginIp(String loginIp) {
        this.loginIp = loginIp;
    }

    public int getLoginNum() {
        return loginNum;
    }

    public void setLoginNum(int loginNum) {
        this.loginNum = loginNum;
    }

    public int getModifyNickNameTimes() {
        return modifyNickNameTimes;
    }

    public void setModifyNickNameTimes(int modifyNickNameTimes) {
        this.modifyNickNameTimes = modifyNickNameTimes;
    }

    public String getRegistrationIp() {
        return registrationIp;
    }

    public void setRegistrationIp(String registrationIp) {
        this.registrationIp = registrationIp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getUserSex() {
        return userSex;
    }

    public void setUserSex(int userSex) {
        this.userSex = userSex;
    }

    public String getChinaSex() {
        return chinaSex;
    }

    public void setChinaSex(String chinaSex) {
        this.chinaSex = chinaSex;
    }

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", land='" + land + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", bIsSetNickName=" + bIsSetNickName +
                ", userPortrait='" + userPortrait + '\'' +
                ", agencyId=" + agencyId +
                ", certificationStatus=" + certificationStatus +
                ", createTime=" + createTime +
                ", id=" + id +
                ", lastLoginTime=" + lastLoginTime +
                ", loginErrorNum=" + loginErrorNum +
                ", loginIp='" + loginIp + '\'' +
                ", loginNum=" + loginNum +
                ", modifyNickNameTimes=" + modifyNickNameTimes +
                ", registrationIp='" + registrationIp + '\'' +
                ", status=" + status +
                ", userSex=" + userSex +
                ", chinaSex='" + chinaSex + '\'' +
                '}';
    }
}
