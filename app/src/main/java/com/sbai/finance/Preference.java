package com.sbai.finance;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.model.fund.UsablePlatform;
import com.sbai.finance.model.system.ServiceConnectWay;
import com.sbai.finance.model.training.TrainingSubmit;

import java.util.ArrayList;
import java.util.List;


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
        String AUTHORIZATION_LOGIN_TIME = "authorization_login_time";
        String RECHARGE_WAY = "recharge_way_string";
        String IS_FIRST_WITH_DRAW = "is_first_with_draw";
        String USER_HAS_SafePass = "user_has_safe_pass";
        String MISS_TALK_ANSWERS = "miss_talk_answers";
        String USER_LOOK_DETAIL = "user_look_detail";
        String IS_FIRST_TRAIN = "IS_FIRST_TRAIN";
        String IS_FIRST_OPEN_APP = "IS_FIRST_OPEN_APP";
        String IS_GUIDE_UPDATE = "IS_GUIDE_UPDATE";
        String STUDY_OPTION = "study_option";
        String MY_STUDY = "my_study";
        String TRAINING_SUBMITS = "training_submits";
        String SERVICE_CONNECT_WAY = "service_connect_way";
        String ACCOUNT_CREDIT = "account_credit";
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

    private void apply(String key, float value) {
        getEditor().putFloat(key, value).apply();
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

    public void setRechargeWay(String key, String way) {
        apply(key + Key.RECHARGE_WAY, way);
    }

    public String getRechargeWay(String key) {
        return mPrefs.getString(key + Key.RECHARGE_WAY, UsablePlatform.PLATFORM_AIL_PAY);
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

    public void setAnswerIds(String answerIds) {
        apply(Key.MISS_TALK_ANSWERS, answerIds);
    }

    public String getAnswerIds() {
        return mPrefs.getString(Key.MISS_TALK_ANSWERS, null);
    }

    public void setUserHasLookTrainDetail(String key, boolean hasLookTrainDetail) {
        apply(key + Key.USER_LOOK_DETAIL, hasLookTrainDetail);
    }

    public boolean isUserLookTrainDetail(String key) {
        return mPrefs.getBoolean(key + Key.USER_LOOK_DETAIL, false);
    }

    public String getStudyOptionData(int id) {
        return mPrefs.getString(id + Key.STUDY_OPTION, null);
    }

    public void setStudyOptionData(int id, String data) {
        apply(id + Key.STUDY_OPTION, data);
    }

    public String getMyStudyData(int id) {
        return mPrefs.getString(id + Key.MY_STUDY, null);
    }

    public void setMyStudyData(int id, String data) {
        apply(id + Key.MY_STUDY, data);
    }

    public boolean isFirstTrain(int trainId) {
        return mPrefs.getBoolean(Key.IS_FIRST_TRAIN + trainId, true);
    }

    public void setIsFirstTrainFalse(int trainId, boolean isFirst) {
        apply(Key.IS_FIRST_TRAIN + trainId, isFirst);
    }

    public boolean isFirstOpenApp() {
        return mPrefs.getBoolean(Key.IS_FIRST_OPEN_APP, true);
    }

    public boolean isGuideUpdate(String versionName) {
        return mPrefs.getBoolean(Key.IS_GUIDE_UPDATE + versionName, false);
    }

    public void setIsFirstOpenAppFalse(String versionName) {
        apply(Key.IS_FIRST_OPEN_APP, false);
        apply(Key.IS_GUIDE_UPDATE + versionName, false);
    }

    public void setTrainingSubmits(String phone, List<TrainingSubmit> submits) {
        apply(phone + Key.TRAINING_SUBMITS, new Gson().toJson(submits));
    }

    public List<TrainingSubmit> getTrainingSubmits(String phone) {
        String json = mPrefs.getString(phone + Key.TRAINING_SUBMITS, null);
        if (TextUtils.isEmpty(json)) {
            return new ArrayList<>();
        } else {
            return getTrainingSubmitsFromJson(json);
        }
    }

    @Nullable
    private List<TrainingSubmit> getTrainingSubmitsFromJson(String json) {
        List<TrainingSubmit> submits = null;
        try {
            submits = new Gson().fromJson(json,
                    new TypeToken<List<TrainingSubmit>>() {
                    }.getType());
        } catch (Exception e) {
            e.printStackTrace();
            submits = new ArrayList<>();
        } finally {
            return submits;
        }
    }

    public void setServiceConnectWay(ServiceConnectWay serviceConnectWay) {
        apply(Key.SERVICE_CONNECT_WAY, new Gson().toJson(serviceConnectWay));
    }

    public ServiceConnectWay getServiceConnectWay() {
        String string = mPrefs.getString(Key.SERVICE_CONNECT_WAY, "");
        return !TextUtils.isEmpty(string) ? new Gson().fromJson(string, ServiceConnectWay.class) : null;
    }
}
