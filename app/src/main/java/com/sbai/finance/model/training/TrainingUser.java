package com.sbai.finance.model.training;

/**
 * 参与过训练的用户的数据结构
 */
public class TrainingUser {
    /**
     * age : 1
     * id : 130
     * land : 浙江省-杭州市-临安市
     * userName : 溺水的鱼
     * userPhone : 15868423484
     * userPortrait : https://esongtest.oss-cn-shanghai.aliyuncs.com/upload/20170706/130i1499304941437.png
     */

    private int age;
    private int id;
    private String land;
    private String userName;
    private String userPhone;
    private String userPortrait;

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
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

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
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
        return "TrainingUser{" +
                "age=" + age +
                ", id=" + id +
                ", land='" + land + '\'' +
                ", userName='" + userName + '\'' +
                ", userPhone='" + userPhone + '\'' +
                ", userPortrait='" + userPortrait + '\'' +
                '}';
    }
}
