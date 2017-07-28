package com.sbai.finance.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.Preference;

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
        Preference.get().setAnswerIds(ids);
    }

    private static void readFromPreference() {
        if (sIntegerQueue == null) {
            String ids = Preference.get().getAnswerIds();
            if (!TextUtils.isEmpty(ids)) {
                Type type = new TypeToken<LinkedList<Integer>>() {}.getType();
                sIntegerQueue = sGson.fromJson(ids, type);
            } else {
                sIntegerQueue = new LinkedList<>();
            }
        }
    }

    public static boolean isHeard(int id) {
        readFromPreference();

        return sIntegerQueue.contains(id);
    }
}
