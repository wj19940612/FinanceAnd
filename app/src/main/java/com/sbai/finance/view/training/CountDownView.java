package com.sbai.finance.view.training;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

/**
 * Created by ${wangJie} on 2017/8/15.
 * 倒计时view
 */

public class CountDownView extends LinearLayout {
    private static final String TAG = "CountDownView";

    private int mMinuteStartTimeTextSize;
    private int mMinuteStartTimeTextColor;

    private int mSecondStartTextColor;
    private int mSecondStartTextSize;

    private float mMillisecondStartTimeTextSize;
    private int mMillisecondStartTimeTextColor;

    private String mMinuteSecondSplitString;
    private String mSecondMillisecondSplitString;

    //是否显示毫秒倒计时
    private boolean mShowMillisecond;

    private TextView mMinuteTimeTextView;
    private TextView mSecondTimeTextView;
    private TextView mMinuteSecondTextView;

    private static final int MESSAGE_WHAT_MINUTE = 774;
    private static final int MESSAGE_WHAT_SECOND = 555;
    private static final int MESSAGE_WHAT_MILLISECOND = 6666;

    //用来记录总时常 以秒为单位
    private long mTotalTime;
    //用来记录变化的总时常 以秒为单位
    private long mTotalChangeTime;


    private int mMinuteTime;
    private int mSecondTime;
    private int mMillisecondTime;
    //用来控制显示时间的TextView的右边距
    private int mTimeViewMarginRight;
    private OnTimeStopChangeListener mOnTimeStopChangeListener;

    public interface OnTimeStopChangeListener {
        void StopChange();
    }

    public void setOnTimeStopChangeListener(OnTimeStopChangeListener onTimeStopChangeListener) {
        this.mOnTimeStopChangeListener = onTimeStopChangeListener;
    }

    public CountDownView(Context context) {
        this(context, null);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CountDownView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttr(attrs);
        init();
    }

    private void processAttr(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CountDownView);

        mMinuteStartTimeTextSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_minuteStartTimeTextSize, 20);
        mMinuteStartTimeTextColor = typedArray.getColor(R.styleable.CountDownView_minuteStartTimeTextColor, Color.WHITE);
        mMinuteSecondSplitString = typedArray.getString(R.styleable.CountDownView_minuteSecondSplitString);

        mSecondStartTextColor = typedArray.getColor(R.styleable.CountDownView_secondStartTextColor, Color.WHITE);
        mSecondStartTextSize = typedArray.getDimensionPixelSize(R.styleable.CountDownView_secondStartTextSize, 20);
        mSecondMillisecondSplitString = typedArray.getString(R.styleable.CountDownView_secondMillisecondSplitString);

        mMillisecondStartTimeTextSize = typedArray.getDimension(R.styleable.CountDownView_millisecondStartTimeTextSize, 20);
        mMillisecondStartTimeTextColor = typedArray.getColor(R.styleable.CountDownView_millisecondStartTimeTextColor, Color.WHITE);
        mTimeViewMarginRight = typedArray.getDimensionPixelSize(R.styleable.CountDownView_timeViewMarginRight, 4);

        mShowMillisecond = typedArray.getBoolean(R.styleable.CountDownView_showMillisecond, true);

        typedArray.recycle();
    }

    private void init() {
        if (TextUtils.isEmpty(mMinuteSecondSplitString)) {
            mMinuteSecondSplitString = ":";
        }

        if (TextUtils.isEmpty(mSecondMillisecondSplitString)) {
            mSecondMillisecondSplitString = ".";
        }

        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.weight = 1;
        layoutParams.setMargins(0, 0, (int) Display.dp2Px(mTimeViewMarginRight, getResources()), 0);
        createMinuteTimeTextView();
        addView(mMinuteTimeTextView, layoutParams);
        createSecondTimeTextView();
        addView(mSecondTimeTextView, layoutParams);
        if (mShowMillisecond) {
            createMinuteSecondTextView();
            addView(mMinuteSecondTextView);
        }
    }


    public long getTotalChangeTime() {
        return mTotalChangeTime;
    }

    /**
     * @param time 毫秒级的时间 倒计时将进行time时常
     */
    public void setMillisecondTime(long time) {
        setSecondTime(time / 1000);
    }

    /**
     * 将进行多少秒
     *
     * @param secondTime
     */
    public void setSecondTime(long secondTime) {
        mTotalTime = secondTime;
        mTimeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_MINUTE, 60_000);
        mTimeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_SECOND, 0);
        if (mShowMillisecond) {
            mTimeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_MILLISECOND, 0);
        }
    }

    /**
     * @param minuteTime 多少分钟
     */
    public void setMinuteTime(long minuteTime) {
        setSecondTime(minuteTime * 60);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacksAndMessages(null);
            mTimeHandler = null;
        }
    }

    private Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_WHAT_MINUTE:
                    mMinuteTime++;
                    String data = "";
                    if (mMinuteTime < 10) {
                        data = "0" + mMinuteTime;
                    } else {
                        data = String.valueOf(mMinuteTime);
                    }
                    String string = getContext().getString(R.string.data, data, mMinuteSecondSplitString);
                    mMinuteTimeTextView.setText(string);
                    if (mTotalTime > mTotalChangeTime) {
                        mTimeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_MINUTE, 60_000);
                    }
                    break;
                case MESSAGE_WHAT_SECOND:
                    mTotalChangeTime++;
                    if (mSecondTime < 59) {
                        mSecondTime++;
                    } else {
                        mSecondTime = 0;
                    }
                    String secondData = "";
                    if (mSecondTime < 10) {
                        secondData = "0" + mSecondTime;
                    } else {
                        secondData = String.valueOf(mSecondTime);
                    }
                    String result = getContext().getString(R.string.data, secondData, mSecondMillisecondSplitString);
                    mSecondTimeTextView.setText(result);
                    if (mTotalTime > mTotalChangeTime) {
                        mTimeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_SECOND, 1000);
                    }

                    if (mTotalTime <= mTotalChangeTime) {
                        mMinuteSecondTextView.setText("00");
                        mTimeHandler.removeCallbacksAndMessages(null);
                        if (mOnTimeStopChangeListener != null) {
                            mOnTimeStopChangeListener.StopChange();
                        }
                    }

                    break;
                case MESSAGE_WHAT_MILLISECOND:
                    if (mMillisecondTime < 990) {
                        mMillisecondTime += 10;
                    } else {
                        mMillisecondTime = 0;
                    }

                    String millisecondTimeData = "00";

                    if (mMillisecondTime != 0) {
                        millisecondTimeData = String.valueOf(mMillisecondTime).substring(0, 2);
                    }

                    mMinuteSecondTextView.setText(millisecondTimeData);
                    if (mTotalTime > mTotalChangeTime) {
                        mTimeHandler.sendEmptyMessageDelayed(MESSAGE_WHAT_MILLISECOND, 10);
                    } else {
                        mMinuteSecondTextView.setText("00");
                    }

                    break;
            }
        }
    };

    private void createMinuteSecondTextView() {
        String defaultText = "00";
        mMinuteSecondTextView = new TextView(getContext());
        mMinuteSecondTextView.setText(defaultText);
        mMinuteSecondTextView.setTextSize(mMillisecondStartTimeTextSize);
        mMinuteSecondTextView.setTextColor(mMillisecondStartTimeTextColor);
        mMinuteSecondTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
    }

    private void createSecondTimeTextView() {
        String defaultText = "00.";
        mSecondTimeTextView = new TextView(getContext());
        mSecondTimeTextView.setText(defaultText);
        mSecondTimeTextView.setTextColor(mSecondStartTextColor);
        mSecondTimeTextView.setTextSize(mSecondStartTextSize);
        mSecondTimeTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);

    }

    private void createMinuteTimeTextView() {
        String defaultText = "00:";
        mMinuteTimeTextView = new TextView(getContext());
        mMinuteTimeTextView.setText(defaultText);
        mMinuteTimeTextView.setTextSize(mMinuteStartTimeTextSize);
        mMinuteTimeTextView.setTextColor(mMinuteStartTimeTextColor);
        mMinuteTimeTextView.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
    }
}
