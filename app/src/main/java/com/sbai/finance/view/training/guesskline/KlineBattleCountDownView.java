package com.sbai.finance.view.training.guesskline;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * k线训练倒计时
 */

public class KlineBattleCountDownView extends LinearLayout {
    @BindView(R.id.minuteHigh)
    TextView mMinuteHigh;
    @BindView(R.id.minuteLow)
    TextView mMinuteLow;
    @BindView(R.id.secondHigh)
    TextView mSecondHigh;
    @BindView(R.id.secondLow)
    TextView mSecondLow;
    private Context mContext;
    //秒
    private int mTotalTime;
    private OnCountDownListener mOnCountDownListener;

    public interface OnCountDownListener {
        void finish();
    }

    public KlineBattleCountDownView(Context context) {
        this(context, null);
    }

    public KlineBattleCountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KlineBattleCountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_kline_battle_count_down, this, true);
        ButterKnife.bind(this);
        mContext = context;
    }

    public void setTotalTime(int totalTime, OnCountDownListener onCountDownListener) {
        mOnCountDownListener = onCountDownListener;
        if (totalTime >= 100 * 60) {
            totalTime = 99 * 60 + 59;
        }
        mTotalTime = totalTime;
        mMinuteHigh.postDelayed(new Runnable() {
            @Override
            public void run() {
                setRemainTime(mTotalTime);
                if (mTotalTime <= 0) {
                    if (mOnCountDownListener != null) {
                        mOnCountDownListener.finish();
                    }
                    return;
                }
                mTotalTime = mTotalTime - 1;
                if (mTotalTime < 0) {
                    mTotalTime = 0;
                }
                mMinuteHigh.postDelayed(this, 1000);
            }
        }, 0);
    }

    public void removeListener() {
        if (mOnCountDownListener != null) {
            mOnCountDownListener = null;
        }
    }

    private void setRemainTime(int time) {
        int mh, ml, sh, sl;
        int minutes = time / 60;
        int seconds = time - minutes * 60;
        mh = minutes / 10;
        ml = minutes % 10;
        sh = seconds / 10;
        sl = seconds % 10;
        mMinuteHigh.setText(String.valueOf(mh));
        mMinuteLow.setText(String.valueOf(ml));
        mSecondHigh.setText(String.valueOf(sh));
        mSecondLow.setText(String.valueOf(sl));
    }
}
