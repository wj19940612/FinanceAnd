package com.sbai.finance.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.Preference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONTokener;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.Queue;

public class MissVoiceRecorder {

    private static final int TOTAL_IDS = 1000;

    private static Queue<Integer> sIntegerQueue;
    private static Gson sGson = new Gson();

    public static void markHeard(int id) {
        readFromPreference();

        if (sIntegerQueue.size() >= TOTAL_IDS) {
            sIntegerQueue.poll();
        }
        sIntegerQueue.add(id);
        String ids = sGson.toJson(sIntegerQueue);
        // TODO: 2017/11/22 需要去掉个人闲置
        Preference.get().setAnswerIds(ids);
    }

    private static void readFromPreference() {
        if (sIntegerQueue == null) {
            String ids = Preference.get().getAnswerIds();
            if (!TextUtils.isEmpty(ids) && isJsonArray(ids)) {
                Type type = new TypeToken<LinkedList<Integer>>() {}.getType();
                sIntegerQueue = sGson.fromJson(ids, type);
            } else {
                sIntegerQueue = new LinkedList<>();
            }
        }
    }

    private static boolean isJsonArray(String ids) {
        boolean result = false;
        try {
            Object json = new JSONTokener(ids).nextValue();
            if (json instanceof JSONArray) {
                result = true;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } finally {
            return result;
        }
    }

    public static boolean isHeard(int id) {
        readFromPreference();

        return sIntegerQueue.contains(id);
    }
}
