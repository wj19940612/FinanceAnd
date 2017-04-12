package com.sbai.finance.utils;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public class TimerHandler extends Handler {

    private static final int MAX = Integer.MAX_VALUE;

    public interface TimerCallback {
        /**
         * count: 1 ~ Integer.MAX_VALUE
         * @param count
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
