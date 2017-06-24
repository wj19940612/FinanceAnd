package com.sbai.finance;

import android.content.Context;
import android.content.SharedPreferences;

import com.sbai.finance.model.payment.UsablePlatform;

public class Preference {
    private static final String SHARED_PREFERENCES_NAME = BuildConfig.FLAVOR + "_prefs";

    interface Key {
        String FOREGROUND = "foreground";
        String SERVICE_PHONE = "servicePhone";
        String SERVICE_QQ = "serviceQQ";
        String USER_JSON = "userJson";
        String PUSH_CLIENT_ID = "PUSH_CLIENT_ID";
        String SERVER_IP_PORT = "server_ip_port";
        String SERVER_TIME = "server_time";
        String AVAILABLE_MARGIN = "available_margin";
        String USER_HAS_HOLDING_ORDER = "has_order";
        String DATA_SOURCE = "data_source";
        String AUTHORIZATION_LOGIN_TIME = "authorization_login_time";
        String RECHARGE_WAY = "recharge_way";
        String IS_FIRST_WITH_DRAW = "is_first_with_draw";
        String USER_HAS_SafePass = "user_has_safe_pass";
    }

    private static Preference sInstance;

    private SharedPreferences mPrefs;

    public static Preference get() {
        if (sInstance == null) {
            sInstance = new Preference();
        }
        return sInstance;
    }

    private Preference() {
        mPrefs = App.getAppContext().getSharedPreferences(SHARED_PREFERENCES_NAME, Context.MODE_PRIVATE);
    }

    private SharedPreferences.Editor getEditor() {
        return mPrefs.edit();
    }

    private void apply(String key, boolean value) {
        getEditor().putBoolean(key, value).apply();
    }

    private void apply(String key, String value) {
        getEditor().putString(key, value).apply();
    }

    private void apply(String key, long value) {
        getEditor().putLong(key, value).apply();
    }

    private void apply(String key, int value) {
        getEditor().putInt(key, value).apply();
    }

    public void setForeground(boolean foreground) {
        apply(Key.FOREGROUND, foreground);
    }

    public boolean isForeground() {
        return mPrefs.getBoolean(Key.FOREGROUND, false);
    }

    public void setMarketServerIpPort(String ipPortStr) {
        apply(Key.SERVER_IP_PORT, ipPortStr);
    }

    public String getMarketServerIpPort() {
        return mPrefs.getString(Key.SERVER_IP_PORT, null);
    }

    public void setTimestamp(String key, long timestamp) {
        apply(key, timestamp);
    }

    public void setUserJson(String userJson) {
        apply(Key.USER_JSON, userJson);
    }

    public String getUserJson() {
        return mPrefs.getString(Key.USER_JSON, null);
    }

    public void setPushClientId(String clientId) {
        apply(Key.PUSH_CLIENT_ID, clientId);
    }

    public String getPushClientId() {
        return mPrefs.getString(Key.PUSH_CLIENT_ID, "");
    }

    public String getServicePhone() {
        return mPrefs.getString(Key.SERVICE_PHONE, null);
    }

    public void setServiceQQ(String serviceQQ) {
        apply(Key.SERVICE_QQ, serviceQQ);
    }

    public String getServiceQQ() {
        return mPrefs.getString(Key.SERVICE_QQ, null);
    }

    public long getTimestamp(String key) {
        return mPrefs.getLong(key, 0);
    }

    public void setServerTime(long serverTime) {
        apply(Key.SERVER_TIME, serverTime);
    }

    public long getServerTime() {
        return mPrefs.getLong(Key.SERVER_TIME, 0);
    }

    public void setAuthorizationTime(long serverTime) {
        apply(Key.AUTHORIZATION_LOGIN_TIME, serverTime);
    }

    public long getAuthorizationTime() {
        return mPrefs.getLong(Key.AUTHORIZATION_LOGIN_TIME, 0);
    }

    public void setRechargeWay(String key, int way) {
        apply(key + Key.RECHARGE_WAY, way);
    }

    public int getRechargeWay(String key) {
        return mPrefs.getInt(key + Key.RECHARGE_WAY, UsablePlatform.TYPE_AIL_PAY);
    }

    public boolean isFirstWithDraw(String key) {
        return mPrefs.getBoolean(key + Key.IS_FIRST_WITH_DRAW, true);
    }

    public void setIsFirstWithDraw(String key, boolean isFirstWithDraw) {
        apply(key + Key.IS_FIRST_WITH_DRAW, isFirstWithDraw);
    }

    public boolean hasUserSetSafePass(String key) {
        return mPrefs.getBoolean(key + Key.USER_HAS_SafePass, false);
    }

    public void setUserSetSafePass(String key, boolean userHasSetPass) {
        apply(key + Key.USER_HAS_SafePass, userHasSetPass);
    }
}
