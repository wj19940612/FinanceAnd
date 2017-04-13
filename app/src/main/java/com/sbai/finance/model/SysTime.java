package com.sbai.finance.model;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.Preference;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.TimeRecorder;

import java.util.Date;

public class SysTime {

    private static final String RECORD_KEY = "SysTime";

    private static SysTime sSysTime;

    public static SysTime getSysTime() {
        if (sSysTime == null) {
            sSysTime = new SysTime();
        }
        return sSysTime;
    }

    private long mSystemTime;

    public void sync() {
        if (Math.abs(TimeRecorder.getElapsedTimeInMinute(RECORD_KEY)) < 10) return;

        Client.getSystemTime()
                .setRetryPolicy(new DefaultRetryPolicy(5 * 1000, 3, 1))
                .setCallback(new Callback2D<Resp<Long>, Long>(false) {
                    @Override
                    protected void onRespSuccessData(Long data) {
                        mSystemTime = data.longValue();
                        Preference.get().setServerTime(mSystemTime);
                        TimeRecorder.record(RECORD_KEY);
                    }
                }).fire();
    }

    public long getSystemTimestamp() {
        if (mSystemTime == 0) {
            mSystemTime = Preference.get().getServerTime();
            if (mSystemTime == 0) {
                return new Date().getTime();
            }
        }
        return mSystemTime + TimeRecorder.getElapsedTimeInMillis(RECORD_KEY);
    }
}
