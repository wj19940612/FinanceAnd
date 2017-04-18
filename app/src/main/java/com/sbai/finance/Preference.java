package com.sbai.finance;

import android.content.Context;
import android.content.SharedPreferences;

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

}
