package com.sbai.finance.view.training;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.os.CountDownTimer;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

/**
 * Created by ${wangJie} on 2017/8/14.
 */

public class TrainProgressBar extends ProgressBar {


    private static final String TAG = "TrainProgressBar";

    private int mBoundaryColor;
    private int mBoundaryHeight;
    private int mHintSplitLineColor;
    private float mHintSplitLineWidth;
    private Paint mBoundaryPaint;
    private Paint mHintSplitLinePaint;
    private float mHintSplitScale;
    private boolean mHasSplitLine;


    //因为目前的样式差不多，所以设置一个通用的，可以关闭
    private boolean mUseDefaultProgressDrawable;

    private long mProgressTotalTime;
    private CountDownTimer mCountDownTimer;
    private OnTimeUpListener mOnTimeUpListener;
    private static final int MAX_PROGRESS = 10000;


    public interface OnTimeUpListener {
        void onTick(long millisUntilUp);

        void onFinish();
    }

    public void setOnTimeUpListener(OnTimeUpListener onTimeUpListener) {
        this.mOnTimeUpListener = onTimeUpListener;
    }


    public TrainProgressBar(Context context) {
        this(context, null);
    }

    public TrainProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, android.R.attr.progressBarStyleHorizontal);
    }

    public TrainProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TrainProgressBar);
        mBoundaryColor = typedArray.getColor(R.styleable.TrainProgressBar_boundaryColor, Color.WHITE);
        mBoundaryHeight = typedArray.getDimensionPixelSize(R.styleable.TrainProgressBar_boundaryHeight, 1);
        mHintSplitLineColor = typedArray.getColor(R.styleable.TrainProgressBar_hintSplitLineColor, Color.WHITE);
        mHintSplitLineWidth = typedArray.getDimension(R.styleable.TrainProgressBar_hintSplitLineWidth, 4);
        mHintSplitScale = typedArray.getFloat(R.styleable.TrainProgressBar_hintSplitScale, 0.8f);
        mHasSplitLine = typedArray.getBoolean(R.styleable.TrainProgressBar_hasSplitLine, true);
        mUseDefaultProgressDrawable = typedArray.getBoolean(R.styleable.TrainProgressBar_isUseDefaultProgressDrawable, true);
        typedArray.recycle();
    }

    private void init() {
        setMax(MAX_PROGRESS);
        if (mUseDefaultProgressDrawable) {
            setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.bg_train_progress));
        }
        mBoundaryPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBoundaryPaint.setAntiAlias(true);
        mBoundaryPaint.setColor(mBoundaryColor);
        mBoundaryPaint.setStyle(Paint.Style.FILL);
        mBoundaryPaint.setStrokeWidth(Display.dp2Px(mBoundaryHeight, getResources()));

        mHintSplitLinePaint = new Paint();
        mHintSplitLinePaint.setAntiAlias(true);
        mHintSplitLinePaint.setColor(mHintSplitLineColor);
        mHintSplitLinePaint.setStyle(Paint.Style.FILL);

    }

    public void setViewProgress(int progress) {
        this.setViewProgress(progress, false);
    }


    public void setTotalMinuteTime(long minuteTime) {
        setTotalSecondTime(minuteTime * 60);
    }

    public void setTotalMillisecondTime(long time) {
        setTotalMillisecondTime(time, 1);
    }

    public void setTotalMillisecondTime(long time, long countDownInterval) {
        mProgressTotalTime = time;
        startTimeUp(time, countDownInterval);
    }

    private void startTimeUp(final long time, final long countDownInterval) {
        mCountDownTimer = new CountDownTimer(time, countDownInterval) {

            @Override
            public void onTick(long millisUntilFinished) {
                long upTime = mProgressTotalTime - millisUntilFinished;
                TrainProgressBar.this.setTrainChangeTime(upTime);
                if (mOnTimeUpListener != null) {
                    mOnTimeUpListener.onTick(upTime);
                }
            }

            @Override
            public void onFinish() {
                mCountDownTimer.cancel();
                if (mOnTimeUpListener != null) {
                    mOnTimeUpListener.onFinish();
                }
            }
        }.start();
    }

    public void cancelCountDownTimer() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
        }
    }

    /**
     * 将进行多少秒
     *
     * @param secondTime
     */
    public void setTotalSecondTime(long secondTime) {
        setTotalMillisecondTime(secondTime * 1000);
    }

    public void setTrainChangeTime(long changeTime) {
        if (mProgressTotalTime > 0) {
            double v1 = changeTime * 1.0 / mProgressTotalTime;
            int progress = (int) (v1 * MAX_PROGRESS);
            this.setViewProgress(progress, false);
        }
    }

    public void setViewProgress(int progress, boolean animate) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            this.setProgress(progress, animate);
        } else {
            this.setProgress(progress);
        }

    }

    @Override
    protected synchronized void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawLine(0, 0, getWidth(), 0, mBoundaryPaint);
        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), mBoundaryPaint);
        if (mHasSplitLine) {
            float rectLeft = (getWidth() * mHintSplitScale);
            float rectRight = rectLeft + mHintSplitLineWidth;
            canvas.drawRect(rectLeft, 0, rectRight, getHeight(), mHintSplitLinePaint);
        }
    }

}
