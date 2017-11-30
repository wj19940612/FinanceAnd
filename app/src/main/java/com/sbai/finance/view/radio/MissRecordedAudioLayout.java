package com.sbai.finance.view.radio;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.audio.MediaRecorderManager;


/**
 * Created by ${wangJie} on 2017/11/29.
 * 音频录制view
 */

public class MissRecordedAudioLayout extends LinearLayout implements View.OnTouchListener, MediaRecorderManager.MediaMediaRecorderPrepareListener, TimerHandler.TimerCallback {

    private static final String TAG = "MediaRecorderManager";

    private static final int UNLAWFUL_AUDIO_TIME = 5; //5秒钟

    private static final int UPDATE_AUDIO_LENGTH = 1000;

    public static final int RECORD_AUDIO_STATUS_INIT = 0; //初始
    public static final int RECORD_AUDIO_STATUS_RECORDING = 1; //录制中
    public static final int RECORD_AUDIO_STATUS_RESTART = 2; //重新录制

    private OnRecordAudioListener mOnRecordAudioListener;
    private TextView mRecordStatusTextView;
    private MediaRecorderManager mMediaRecorderManager;
    private int mAudioLengthTextColor;


    public interface OnRecordAudioListener {
        void onRecordAudioFinish(String audioPath, long audioLength);
    }

    public void setOnRecordAudioListener(OnRecordAudioListener onRecordAudioListener) {
        mOnRecordAudioListener = onRecordAudioListener;
    }

    private TextView mAudioLengthTextView;
    private AppCompatButton mRecordAudioBtn;

    private boolean isStartRecord;  //是否开始长按录制
    private TimerHandler mTimerHandler;
    private int mAudioLength;

    public MissRecordedAudioLayout(Context context) {
        this(context, null);
    }

    public MissRecordedAudioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissRecordedAudioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();

        mMediaRecorderManager = new MediaRecorderManager();
        mMediaRecorderManager.setMediaMediaRecorderPrepareListener(this);
        mTimerHandler = new TimerHandler(this);
    }

    private void init() {
        setOrientation(VERTICAL);
        setGravity(Gravity.CENTER_VERTICAL);

        int defaultPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics());


        mAudioLengthTextColor = ContextCompat.getColor(getContext(), R.color.colorPrimary);
        mAudioLengthTextView = new TextView(getContext());
        mAudioLengthTextView.setGravity(Gravity.CENTER);
        mAudioLengthTextView.setPadding(dp2px(14), dp2px(8), dp2px(8), dp2px(14));
        mAudioLengthTextView.setBackgroundResource(R.drawable.bg_splite_rounded);
        mAudioLengthTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        mAudioLengthTextView.setText(R.string.recording);
        mAudioLengthTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        mAudioLengthTextView.setVisibility(GONE);
        addView(mAudioLengthTextView);

        mRecordAudioBtn = new AppCompatButton(getContext());
        mRecordAudioBtn.setBackgroundResource(R.drawable.bg_miss_record_audio);
        mRecordAudioBtn.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                isStartRecord = true;
                mMediaRecorderManager.onRecordStart();
                return false;
            }
        });

        mRecordAudioBtn.setOnTouchListener(this);

        LayoutParams layoutParams = new LayoutParams(dp2px(80), dp2px(80));
        layoutParams.gravity = Gravity.CENTER;
        layoutParams.setMargins(0, defaultPadding, 0, defaultPadding * 2);
        addView(mRecordAudioBtn, layoutParams);


        mRecordStatusTextView = new TextView(getContext());
        mRecordStatusTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12);
        mRecordStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        mRecordStatusTextView.setText(R.string.press_record);
        mRecordStatusTextView.setGravity(Gravity.CENTER);
        addView(mRecordStatusTextView);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {

        switch (motionEvent.getAction()) {
            case MotionEvent.ACTION_DOWN:
//                setRecordStatus(RECORD_AUDIO_STATUS_RECORDING);
                mAudioLengthTextView.setVisibility(VISIBLE);
                break;
            case MotionEvent.ACTION_UP:
                if (isStartRecord && mAudioLength < UNLAWFUL_AUDIO_TIME) {
                    ToastUtil.show(R.string.record_audio_time_is_short);
                    reset();
                } else {
                    reset();
                }

                break;
            case MotionEvent.ACTION_MOVE:

                break;
        }
        return false;
    }

    private void reset() {
        mTimerHandler.removeCallbacksAndMessages(null);
        isStartRecord = false;
    }

    @Override
    public void onMediaMediaRecorderPrepared() {
        isStartRecord = true;
        mAudioLength = 0;
        mTimerHandler.sendEmptyMessageDelayed(UPDATE_AUDIO_LENGTH, 0);
    }

    @Override
    public void onTimeUp(int count) {
        mAudioLength = count;
        SpannableString spannableString = StrUtil.mergeTextWithColor(getContext().getString(R.string.recording), "  " +
                        getContext().getString(R.string.voice_time, count),
                mAudioLengthTextColor);
        Log.d(TAG, "onTimeUp: " + count + " " + spannableString.toString());
        mAudioLengthTextView.setText(spannableString);
    }

    public void setRecordStatus(int status) {
        switch (status) {
            case RECORD_AUDIO_STATUS_INIT:
            case RECORD_AUDIO_STATUS_RECORDING:
                mRecordStatusTextView.setText(R.string.press_record);
                break;

        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimerHandler.removeCallbacksAndMessages(null);
        mTimerHandler = null;
        mMediaRecorderManager.onRecordStop();
        mMediaRecorderManager = null;
    }

    private int dp2px(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }
}
