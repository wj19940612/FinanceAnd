package com.sbai.finance.view.radio;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;


/**
 * Created by ${wangJie} on 2017/11/29.
 * 音频录制view
 */

public class MissRecordedAudioLayout extends LinearLayout {

    private static final int UNLAWFUL_AUDIO_TIME = 1000;

    public static final int RECORD_AUDIO_STATUS_INIT = 0; //初始
    public static final int RECORD_AUDIO_STATUS_RECORDING = 1; //录制中
    public static final int RECORD_AUDIO_STATUS_RESTART = 2; //重新录制

    private OnRecordAudioListener mOnRecordAudioListener;
    private TextView mRecordStatusTextView;

    public interface OnRecordAudioListener {
        void onRecordAudioFinish(String audioPath, long audioLength);
    }

    public void setOnRecordAudioListener(OnRecordAudioListener onRecordAudioListener) {
        mOnRecordAudioListener = onRecordAudioListener;
    }

    private LinearLayout mRecordTimeLinearLayout;
    private TextView mAudioLength;
    private AppCompatButton mRecordAudioBtn;

    public MissRecordedAudioLayout(Context context) {
        this(context, null);
    }

    public MissRecordedAudioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissRecordedAudioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int defaultPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());

        mRecordTimeLinearLayout = new LinearLayout(getContext());
        mRecordTimeLinearLayout.setPadding(dp2px(14), dp2px(8), dp2px(8), dp2px(14));
        mRecordTimeLinearLayout.setBackgroundResource(R.drawable.bg_splite_rounded);

        TextView textView = new TextView(getContext());
        textView.setText(R.string.recording);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mRecordTimeLinearLayout.addView(textView);

        mAudioLength = new TextView(getContext());
        mAudioLength.setGravity(Gravity.CENTER);
        mAudioLength.setTextColor(Color.parseColor("#FF250000"));
        mAudioLength.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mAudioLength.setPadding(dp2px(4), 0, 0, 0);
        mRecordTimeLinearLayout.addView(mAudioLength);

        addView(mRecordTimeLinearLayout);
        mRecordTimeLinearLayout.setVisibility(GONE);

        mRecordAudioBtn = new AppCompatButton(getContext());
        mRecordAudioBtn.setBackgroundResource(R.drawable.bg_miss_record_audio);
        mRecordAudioBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, defaultPadding, 0, defaultPadding * 2);
        addView(mRecordAudioBtn, layoutParams);


        mRecordStatusTextView = new TextView(getContext());
        mRecordStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mRecordStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));

    }

    private int dp2px(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }
}
