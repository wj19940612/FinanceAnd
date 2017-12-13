package com.sbai.finance.view.training.guesskline;

import android.content.Context;
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

public class CountDownView extends LinearLayout {
    @BindView(R.id.minuteHigh)
    TextView mMinuteHigh;
    @BindView(R.id.minuteLow)
    TextView mMinuteLow;
    @BindView(R.id.secondHigh)
    TextView mSecondHigh;
    @BindView(R.id.secondLow)
    TextView mSecondLow;
    private Context mContext;
    private int mTotalTime;

    public interface OnCountDownListener {
        void finish();

        void count(int costTime);
    }

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_count_down, this, true);
        ButterKnife.bind(this);
        mContext = context;
    }

}
