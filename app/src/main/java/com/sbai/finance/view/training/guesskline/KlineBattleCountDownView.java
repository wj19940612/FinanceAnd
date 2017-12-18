package com.sbai.finance.view.training.guesskline;

import android.content.Context;
import android.os.CountDownTimer;
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
    private CountDownTimer mCountDownTimer;

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

    public void setTotalTime(long totalTime, final OnCountDownListener onCountDownListener) {
        setRemainTime(totalTime);
        mCountDownTimer = new CountDownTimer(totalTime, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                setRemainTime(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                if (onCountDownListener != null) {
                    onCountDownListener.finish();
                }
            }
        };
        mCountDownTimer.start();
    }

    public void cancelCount() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    private void setRemainTime(long time) {
        if (time < 0) time = 0;
        int mh, ml, sh, sl;
        int minutes = (int) (time / 60000);
        int seconds = (int) (time / 1000 - minutes * 60);
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
