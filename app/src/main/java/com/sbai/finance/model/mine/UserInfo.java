package com.sbai.finance.model.mine;

import android.os.Parcel;
import android.os.Parcelable;

import com.sbai.finance.model.LocalUser;

/**
 * 登录后的用户信息数据
 */
public class UserInfo implements Parcelable {
    //	待审核0、审核通过1、审核未通过 2
    public static final int CREDIT_IS_APPROVE_ING = 0;
    public static final int CREDIT_IS_ALREADY_APPROVE = 1;
    public static final int CREDIT_IS_NOT_APPROVE = 2;

    public static final int SEX_BOY = 2;
    public static final int SEX_GIRL = 1;

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

    private int id;
    private String userName;
    private Integer age;
    private int userSex;     //1 女 2男

    private String land;
    private String userPhone;
    private boolean bIsSetNickName;
    private String userPortrait;     //用户头像网址
    private int agencyId;
    private Integer certificationStatus;     //认证状态
    private long createTime;
    private long lastLoginTime;
    private int loginErrorNum;
    private String loginIp;
    private int loginNum;
    private int modifyNickNameTimes;
    private String registrationIp;
    private int status;     //	待审核0、审核通过1、审核未通过 2

    private double longitude;     //经度
    private double latitude;       //纬度

    private boolean isSetPass;      // 是否设置登录密码
    private boolean isNewUser;      // 是否是新注册的用户
    private int registerRewardIngot; // 新注册的用户奖励的元宝数量

    private int evaluate; //是否进行过测试
    private int maxLevel; //最高测评结果
    private String wxOpenId;//微信openid
    private String wxName;//微信昵称

    public String getWxName() {
        return wxName;
    }

    public void setWxName(String wxName) {
        this.wxName = wxName;
    }

    public String getWxOpenId() {
        return wxOpenId;
    }

    public void setWxOpenId(String wxOpenId) {
        this.wxOpenId = wxOpenId;
    }

    public boolean isNewUser() {
        return isNewUser;
    }

    public void setNewUser(boolean isNewUser) {
        this.isNewUser = isNewUser;
    }

    public int getRegisterRewardIngot() {
        return registerRewardIngot;
    }

    public boolean hasEvaluated() {
        return evaluate == 1;
    }

    public void setEvaluate(int evaluate) {
        this.evaluate = evaluate;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public static boolean isGril(int userSex) {
        return userSex == 1;
    }

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

    public boolean isSetPass() {
        return isSetPass;
    }

    public void setSetPass(boolean setPass) {
        isSetPass = setPass;
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
        setUserSex(userDetailInfo.getUserSex());
        setStatus(userDetailInfo.getStatus());
        setWxOpenId(userDetailInfo.getWxOpenId());
        setWxName(userDetailInfo.getWxName());
        LocalUser.getUser().setUserInfo(this);
    }

    public int getAgencyId() {
        return agencyId;
    }

    public void setAgencyId(int agencyId) {
        this.agencyId = agencyId;
    }

    public Integer getCertificationStatus() {
        return certificationStatus;
    }

    public void setCertificationStatus(Integer certificationStatus) {
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

    public void setRegisterRewardIngot(int registerRewardIngot) {
        this.registerRewardIngot = registerRewardIngot;
    }

    public int getEvaluate() {
        return evaluate;
    }

    public int getMaxLevel() {
        return maxLevel;
    }

    public void setMaxLevel(int maxLevel) {
        this.maxLevel = maxLevel;
    }


    @Override
    public String toString() {
        return "UserInfo{" +
                "id=" + id +
                ", userName='" + userName + '\'' +
                ", age=" + age +
                ", userSex=" + userSex +
                ", land='" + land + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", bIsSetNickName=" + bIsSetNickName +
                ", userPortrait='" + userPortrait + '\'' +
                ", agencyId=" + agencyId +
                ", certificationStatus=" + certificationStatus +
                ", createTime=" + createTime +
                ", lastLoginTime=" + lastLoginTime +
                ", loginErrorNum=" + loginErrorNum +
                ", loginIp='" + loginIp + '\'' +
                ", loginNum=" + loginNum +
                ", modifyNickNameTimes=" + modifyNickNameTimes +
                ", registrationIp='" + registrationIp + '\'' +
                ", status=" + status +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", isSetPass=" + isSetPass +
                ", isNewUser=" + isNewUser +
                ", registerRewardIngot=" + registerRewardIngot +
                ", evaluate=" + evaluate +
                ", maxLevel=" + maxLevel +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.id);
        dest.writeString(this.userName);
        dest.writeValue(this.age);
        dest.writeInt(this.userSex);
        dest.writeString(this.land);
        dest.writeString(this.userPhone);
        dest.writeByte(this.bIsSetNickName ? (byte) 1 : (byte) 0);
        dest.writeString(this.userPortrait);
        dest.writeInt(this.agencyId);
        dest.writeValue(this.certificationStatus);
        dest.writeLong(this.createTime);
        dest.writeLong(this.lastLoginTime);
        dest.writeInt(this.loginErrorNum);
        dest.writeString(this.loginIp);
        dest.writeInt(this.loginNum);
        dest.writeInt(this.modifyNickNameTimes);
        dest.writeString(this.registrationIp);
        dest.writeInt(this.status);
        dest.writeDouble(this.longitude);
        dest.writeDouble(this.latitude);
        dest.writeByte(this.isSetPass ? (byte) 1 : (byte) 0);
        dest.writeByte(this.isNewUser ? (byte) 1 : (byte) 0);
        dest.writeInt(this.registerRewardIngot);
        dest.writeInt(this.evaluate);
        dest.writeInt(this.maxLevel);
    }

    public UserInfo() {
    }

    protected UserInfo(Parcel in) {
        this.id = in.readInt();
        this.userName = in.readString();
        this.age = (Integer) in.readValue(Integer.class.getClassLoader());
        this.userSex = in.readInt();
        this.land = in.readString();
        this.userPhone = in.readString();
        this.bIsSetNickName = in.readByte() != 0;
        this.userPortrait = in.readString();
        this.agencyId = in.readInt();
        this.certificationStatus = (Integer) in.readValue(Integer.class.getClassLoader());
        this.createTime = in.readLong();
        this.lastLoginTime = in.readLong();
        this.loginErrorNum = in.readInt();
        this.loginIp = in.readString();
        this.loginNum = in.readInt();
        this.modifyNickNameTimes = in.readInt();
        this.registrationIp = in.readString();
        this.status = in.readInt();
        this.longitude = in.readDouble();
        this.latitude = in.readDouble();
        this.isSetPass = in.readByte() != 0;
        this.isNewUser = in.readByte() != 0;
        this.registerRewardIngot = in.readInt();
        this.evaluate = in.readInt();
        this.maxLevel = in.readInt();
    }

    public static final Creator<UserInfo> CREATOR = new Creator<UserInfo>() {
        @Override
        public UserInfo createFromParcel(Parcel source) {
            return new UserInfo(source);
        }

        @Override
        public UserInfo[] newArray(int size) {
            return new UserInfo[size];
        }
    };
}


