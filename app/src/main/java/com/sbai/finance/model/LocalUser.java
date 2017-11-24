package com.sbai.finance.model;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.sbai.finance.Preference;
import com.sbai.finance.model.mine.UserInfo;

public class LocalUser {
    private static LocalUser sLocalUser;

    private UserInfo mUserInfo;
    private String mPhone;

    public static LocalUser getUser() {
        if (sLocalUser == null) {
            sLocalUser = loadFromPreference();
        }
        return sLocalUser;
    }

    private static LocalUser loadFromPreference() {
        String userJson = Preference.get().getUserJson();
        if (!TextUtils.isEmpty(userJson)) {
            Gson gson = new Gson();
            return gson.fromJson(userJson, LocalUser.class);
        }

        return new LocalUser();
    }

    private void saveToPreference() {
        String userJson = new Gson().toJson(this);
        Preference.get().setUserJson(userJson);
    }

    public void setUserInfo(UserInfo userInfo, String phone) {
        mUserInfo = userInfo;
        mPhone = phone;
        saveToPreference();
    }

    public void setUserInfo(UserInfo userInfo) {
        mUserInfo = userInfo;
        saveToPreference();
    }

    public void setNewUser(boolean isNewUser) {
        mUserInfo.setNewUser(isNewUser);
        saveToPreference();
    }


    public UserInfo getUserInfo() {
        return mUserInfo;
    }

    public String getPhone() {
        return mPhone;
    }

    public boolean isLogin() {
        return mUserInfo != null;
    }

    public void logout() {
        mUserInfo = null;
        saveToPreference();
    }

    /**
     * 是否为小姐姐的账号
     */
    public boolean isMiss() {
        if (mUserInfo == null) return false;
        if (mUserInfo.getCustomId() == 0) return false;
        return true;
    }

    @Override
    public String toString() {
        return "User{" +
                "mUserInfo=" + mUserInfo +
                '}';
    }

}
