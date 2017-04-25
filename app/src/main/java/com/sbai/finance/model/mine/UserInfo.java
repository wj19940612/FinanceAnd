package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/17.
 */

public class UserInfo {

    private String userName;
    private int age;
    private String land;
    private String userPhone;
    private boolean bIsSetNickName;


    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
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

    @Override
    public String toString() {
        return "UserInfo{" +
                "userName='" + userName + '\'' +
                ", age=" + age +
                ", land='" + land + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", bIsSetNickName=" + bIsSetNickName +
                '}';
    }
}
