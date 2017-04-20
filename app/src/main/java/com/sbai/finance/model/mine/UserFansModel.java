package com.sbai.finance.model.mine;

/**
 * Created by ${wangJie} on 2017/4/19.
 */

public class UserFansModel {

    private String userName;
    private String userHeadImage;

    public UserFansModel(String userName, String userHeadImage) {
        this.userName = userName;
        this.userHeadImage = userHeadImage;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserHeadImage() {
        return userHeadImage;
    }

    public void setUserHeadImage(String userHeadImage) {
        this.userHeadImage = userHeadImage;
    }
}
