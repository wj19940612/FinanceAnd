package com.sbai.finance.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class TimerHandler extends Handler {

    public final static int TREND_REFRESH_TIME = 60;
    public final static int STOCK_RT_PULL_TIME = 6;

    private static final int MAX = Integer.MAX_VALUE;

    public interface TimerCallback {
        /**
         * count: 1 ~ Integer.MAX_VALUE
         * @param count from 1 to Integer.MAX_VALUE
         */
        void onTimeUp(int count);
    }

    WeakReference<TimerCallback> mWeakReference;
    private int mCount;

    public TimerHandler(TimerCallback callback) {
        mWeakReference = new WeakReference<>(callback);
        mCount = 0;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        TimerCallback callback = mWeakReference.get();
        if (callback != null) {
            if (mCount == MAX) {
                mCount = 0;
            }
            int delayMillis = msg.what;
            this.sendEmptyMessageDelayed(delayMillis, delayMillis);
            callback.onTimeUp(++mCount);
        }
    }
}
