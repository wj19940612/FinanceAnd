package com.sbai.finance.view.training;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
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
    private int mProgress;
    private float mHintSplitScale;
    private boolean mHasSplitLine;

    private OnProgressCompleteListener mOnProgressCompleteListener;
    private OnProgressFinishListener mOnProgressFinishListener;

    //因为目前的样式差不多，所以设置一个通用的，可以关闭
    private boolean mUseDefaultProgressDrawable;
    //进度条每隔多久变化一次
    private int mProgressChangeTime;

    private long mProgressTotalTime;

    public interface OnProgressCompleteListener {
        void onProgressComplete(int progress);
    }

    public interface OnProgressFinishListener {
        void onProgressFinish();
    }


    public void setOnProgressCompleteListener(OnProgressCompleteListener onProgressCompleteListener) {
        this.mOnProgressCompleteListener = onProgressCompleteListener;
    }

    public void setOnProgressFinishListener(OnProgressFinishListener onProgressFinishListener) {
        this.mOnProgressFinishListener = onProgressFinishListener;
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

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTimeHandler != null) {
            mTimeHandler.removeCallbacksAndMessages(null);
            mTimeHandler = null;
        }
    }


    /**
     * @param time 毫秒级的时间 倒计时将进行time时常
     */
    public void setMillisecondTime(long time) {
        mProgressChangeTime = (int) (time / 100);
        mTimeHandler.sendEmptyMessageDelayed(0, 0);
    }

    /**
     * 将进行多少秒
     *
     * @param secondTime
     */
    public void setSecondTime(long secondTime) {
        setMillisecondTime(secondTime * 1000);
    }

    /**
     * @param minuteTime 多少分钟
     */
    public void setMinuteTime(long minuteTime) {
        setSecondTime(minuteTime * 60);
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

    private Handler mTimeHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (mProgress < 100) {
                mProgress++;
                setProgress(mProgress);

                if (mOnProgressCompleteListener != null) {
                    mOnProgressCompleteListener.onProgressComplete(mProgress);
                }
                mTimeHandler.sendEmptyMessageDelayed(0, mProgressChangeTime);
            } else {
                mTimeHandler.removeCallbacksAndMessages(null);
                if (mOnProgressFinishListener != null) {
                    mOnProgressFinishListener.onProgressFinish();
                }
            }
        }
    };

}
