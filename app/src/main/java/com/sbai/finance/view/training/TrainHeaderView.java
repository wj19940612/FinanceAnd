package com.sbai.finance.view.training;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by houcc on 2017-08-16.
 * 训练页面头部
 */

public class TrainHeaderView extends LinearLayout {
    @BindView(R.id.back)
    TextView mBack;
    @BindView(R.id.countdown)
    CountDownView mCountdown;
    @BindView(R.id.howPlay)
    TextView mHowPlay;
    @BindView(R.id.progress)
    TrainProgressBar mProgress;
    @BindView(R.id.mainArea)
    LinearLayout mMainArea;

    private Callback mCallback;

    public interface Callback {
        void onBackClick();

        void onHowPlayClick();

        void onEndOfTimer();
    }

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public TrainHeaderView(Context context) {
        this(context, null);
    }

    public TrainHeaderView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(getContext()).inflate(R.layout.view_train_header, null, false);
        addView(view);
        ButterKnife.bind(this);
        mCountdown.setOnTimeStopChangeListener(new CountDownView.OnTimeStopChangeListener() {
            @Override
            public void StopChange() {
                if (mCallback != null) {
                    mCallback.onEndOfTimer();
                }
            }
        });
    }

    @OnClick({R.id.back, R.id.howPlay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                if (mCallback != null) {
                    mCallback.onBackClick();
                }
                break;
            case R.id.howPlay:
                if (mCallback != null) {
                    mCallback.onHowPlayClick();
                }
                break;
        }
    }
}
