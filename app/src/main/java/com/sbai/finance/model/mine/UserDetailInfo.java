package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/24.
 * 修改个人界面的用户信息
 */

public class UserDetailInfo {

    /**
     * age : 16348
     * agencyId : 0
     * bIsSetNickName : false
     * certificationStatus : 0
     * createTime : 1492409365000
     * id : 19
     * land : 测试内容bw2l
     * lastLoginTime : 1492413305000
     * loginErrorNum : 0
     * loginIp : 192.168.1.108
     * loginNum : 2
     * modifyNickNameTimes : 0
     * passEncryptTimes : 1
     * registrationIp : 192.168.1.108
     * status : 0
     * userName : 用户59
     * userPass : 38f9666a3a4e9498e3f6be3400f86948
     * userPhone : 150*******62
     */

    private int age;
    private int agencyId;
    private boolean bIsSetNickName;
    private int certificationStatus;
    private long createTime;
    private int id;
    private String land;
    private long lastLoginTime;
    private int loginErrorNum;
    private String loginIp;
    private int loginNum;
    private int modifyNickNameTimes;
    private int passEncryptTimes;
    private String registrationIp;
    private int status;
    private String userName;
    private String userPass;
    private String userPhone;
    //用户头像
    private String userPortrait;


    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public boolean isBIsSetNickName() {
        return bIsSetNickName;
    }

    public void setBIsSetNickName(boolean bIsSetNickName) {
        this.bIsSetNickName = bIsSetNickName;
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

    public String getLand() {
        return land;
    }

    public void setLand(String land) {
        this.land = land;
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

    public int getPassEncryptTimes() {
        return passEncryptTimes;
    }

    public void setPassEncryptTimes(int passEncryptTimes) {
        this.passEncryptTimes = passEncryptTimes;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserPass() {
        return userPass;
    }

    public void setUserPass(String userPass) {
        this.userPass = userPass;
    }

    public String getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(String userPhone) {
        this.userPhone = userPhone;
    }

    public String getUserPortrait() {
        return userPortrait;
    }

    public void setUserPortrait(String userPortrait) {
        this.userPortrait = userPortrait;
    }

    @Override
    public String toString() {
        return "UserDetailInfo{" +
                "age=" + age +
                ", agencyId=" + agencyId +
                ", bIsSetNickName=" + bIsSetNickName +
                ", certificationStatus=" + certificationStatus +
                ", createTime=" + createTime +
                ", id=" + id +
                ", land='" + land + '\'' +
                ", lastLoginTime=" + lastLoginTime +
                ", loginErrorNum=" + loginErrorNum +
                ", loginIp='" + loginIp + '\'' +
                ", loginNum=" + loginNum +
                ", modifyNickNameTimes=" + modifyNickNameTimes +
                ", passEncryptTimes=" + passEncryptTimes +
                ", registrationIp='" + registrationIp + '\'' +
                ", status=" + status +
                ", userName='" + userName + '\'' +
                ", userPass='" + userPass + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                '}';
    }
}
