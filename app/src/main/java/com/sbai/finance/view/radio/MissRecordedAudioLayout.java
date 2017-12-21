package com.sbai.finance.view.radio;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.provider.Settings;
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
import com.sbai.finance.utils.FileUtils;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.audio.MediaRecorderManager;
import com.sbai.finance.view.SmartDialog;


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
    public static final int RECORD_AUDIO_STATUS_END = 2; //录制结束

    private OnRecordAudioListener mOnRecordAudioListener;
    private TextView mRecordStatusTextView;
    private MediaRecorderManager mMediaRecorderManager;
    private int mAudioLengthTextColor;

    private int mRecordBtnX;
    private int mRecordBtnY;
    private boolean mInLegalRange = true;

    private String audioPath;

    private boolean hasRecordPermission = true;
    private Rect mRect;

    public interface OnRecordAudioListener {
        void onRecordAudioFinish(String audioPath, int audioLength);
    }

    public void setOnRecordAudioListener(OnRecordAudioListener onRecordAudioListener) {
        mOnRecordAudioListener = onRecordAudioListener;
    }

    private TextView mAudioLengthTextView;
    private AppCompatButton mRecordAudioBtn;

    private int mRecordAudioBtnWidth;
    private int mRecordAudioBtnHeight;

    private volatile boolean isStartRecord;  //是否开始长按录制
    private TimerHandler mTimerHandler;
    private int mAudioLength;

    private boolean isLegalAreaUp;

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
        setRecordStatus(RECORD_AUDIO_STATUS_INIT);
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

        mRecordAudioBtn.post(new Runnable() {
            @Override
            public void run() {
                int[] point = new int[2];
                mRecordAudioBtn.getLocationOnScreen(point);
                mRecordBtnX = point[0];
                mRecordBtnY = point[1];
                mRecordAudioBtnWidth = mRecordAudioBtn.getWidth();
                mRecordAudioBtnHeight = mRecordAudioBtn.getHeight();

                int offsetWidth = mRecordAudioBtnWidth / 2;
                int offsetHeight = mRecordAudioBtnHeight / 2;

                Log.d(TAG, "run: " + mRecordBtnX + "  " + mRecordBtnY + " " + mRecordAudioBtnWidth + " " + mRecordAudioBtnHeight);

                Log.d(TAG, "run: " + mRecordAudioBtn.getLeft() + "  " + mRecordAudioBtn.getTop() + "  " + mRecordAudioBtn.getRight() + "  " + mRecordAudioBtn.getBottom() + " " + offsetWidth + " " + offsetHeight);

                mRect = new Rect(mRecordBtnX - mRecordAudioBtnWidth, mRecordBtnY - mRecordAudioBtnHeight, mRecordBtnX + mRecordAudioBtnWidth, mRecordBtnY + mRecordAudioBtnHeight);
            }
        });

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
                mRecordStatusTextView.setText(R.string.press_record);
                mAudioLengthTextView.setVisibility(VISIBLE);
                hasRecordPermission = true;
                break;
            case MotionEvent.ACTION_UP:
                if (isStartRecord) {
                    if (mAudioLength < UNLAWFUL_AUDIO_TIME) {
                        ToastUtil.show(R.string.record_audio_time_is_short);
                        FileUtils.deleteFile();
                    } else {
                        if (mOnRecordAudioListener != null && mInLegalRange) {
                            mOnRecordAudioListener.onRecordAudioFinish(mMediaRecorderManager.getRecordAudioPath(), mAudioLength);
                        }
                        reset();
                        setRecordStatus(RECORD_AUDIO_STATUS_END);
                    }
                }
                reset();
                break;
            case MotionEvent.ACTION_MOVE:
                float x = motionEvent.getRawX();
                float y = motionEvent.getRawY();
                if (isStartRecord) {
                    mInLegalRange = pointISInsideRecordBtn(x, y);
                }
                break;
        }
        return false;
    }

    private boolean pointISInsideRecordBtn(float x, float y) {
        boolean x1 = x > (mRecordBtnX - 30);
        boolean x2 = (mRecordBtnX + mRecordAudioBtnWidth) > x;
        boolean y1 = y > (mRecordBtnY - 30);
        boolean y2 = (mRecordBtnY + mRecordAudioBtnHeight + 50) > y;
        Log.d(TAG, "pointISInsideRecordBtn: " + x1 + " " + x2 + "  " + y1 + "  " + y2 + " " + y + " x " + x);
        return x1 && x2 && y1 && y2;
//        return mRect.contains((int) x, (int) y);
    }

    private void reset() {
        mTimerHandler.resetCount();
        mTimerHandler.removeCallbacksAndMessages(null);
        isStartRecord = false;
        mMediaRecorderManager.onRecordStop();
        setRecordStatus(RECORD_AUDIO_STATUS_INIT);
        SpannableString spannableString = StrUtil.mergeTextWithColor(getContext().getString(R.string.recording), "  " +
                        getContext().getString(R.string.voice_time, 0),
                mAudioLengthTextColor);
        mAudioLengthTextView.setText(spannableString);
    }


    @Override
    public void onMediaMediaRecorderPrepared(String path) {
        audioPath = path;
        isStartRecord = true;
        mAudioLength = 0;
        mTimerHandler.sendEmptyMessageDelayed(UPDATE_AUDIO_LENGTH, 0);
    }

    @Override
    public void onError(int what, Exception e) {
        Log.d(TAG, "onError: " + e.toString());
        switch (what) {
            case MediaRecorderManager.RECORD_MEDIA_ERROR_SYSTEM_CODE:
//                break;
            case MediaRecorderManager.RECORD_MEDIA_ERROR_CODE_PERMISSION:
                error();
                break;
        }
    }


    private void error() {
        mTimerHandler.removeCallbacksAndMessages(null);
        isStartRecord = false;
        mAudioLength = 0;
        showPermissionDialog();
    }

    private void stopRecord() {
        mTimerHandler.removeCallbacksAndMessages(null);
        setRecordStatus(RECORD_AUDIO_STATUS_INIT);
        isStartRecord = false;
        mAudioLength = 0;
    }

    private void showPermissionDialog() {
        SmartDialog.single((Activity) getContext(), getContext().getString(R.string.please_open_the_recording_permission))
                .setPositive(R.string.go_to_set, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Uri packageURI = Uri.parse("package:" + getContext().getPackageName());
//                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS, packageURI);
                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_SETTINGS);
                        getContext().startActivity(intent);
                    }
                })
                .show();
    }

    @Override
    public void onTimeUp(int count) {
        if (hasRecordPermission && count == 2) {
            int realVolume = mMediaRecorderManager.getRealVolume();
            if (realVolume <= 0) {
                error();
                hasRecordPermission = false;
                return;
            }
        }

        mAudioLength = count;
        if (mInLegalRange) {
            SpannableString spannableString = StrUtil.mergeTextWithColor(getContext().getString(R.string.recording), "  " +
                            getContext().getString(R.string.voice_time, count),
                    mAudioLengthTextColor);
            mAudioLengthTextView.setText(spannableString);
        } else {
            mAudioLengthTextView.setText("超出范围取消");
        }
    }

    public void setRecordStatus(int status) {
        switch (status) {
            case RECORD_AUDIO_STATUS_INIT:
                mAudioLengthTextView.setVisibility(GONE);
                break;
            case RECORD_AUDIO_STATUS_RECORDING:
                mRecordStatusTextView.setText(R.string.press_record);
                mAudioLengthTextView.setVisibility(VISIBLE);
                break;
            case RECORD_AUDIO_STATUS_END:
                mRecordStatusTextView.setText(R.string.restart_recording);
                break;

        }
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        mRecordAudioBtn.setEnabled(enabled);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mTimerHandler.removeCallbacksAndMessages(null);
        mTimerHandler = null;
        mMediaRecorderManager.onDestroy();
        mMediaRecorderManager = null;
    }


    private int dp2px(int px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

}
