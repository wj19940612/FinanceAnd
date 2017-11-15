package com.sbai.finance.view.training;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.FloatRange;
import android.support.annotation.IntRange;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sbai.finance.R;


/**
 * Created by ${wangJie} on 2017/8/11.
 * 分数区间view 0-200 -400
 */

public class CreditAreaView extends View {
    private static final String TAG = "CreditAreaView";


    //未达到分数区域颜色
    private int mNotReachedCreditViewColor;

    Paint mSplitLinePaint;
    private int mMeasuredWidth;


    // 分数所占区域画笔
    Paint mCreditAreaPaint;

    private int mStartColor;
    private int mEndColor;

    private int mSplitWidth;
    private int mSplitColor;
    private int mSplitHeight;
    //view 的圆角
    private int mRadius;

    //分为几段
    private int mGradeCount;
    private boolean mHasSplit;
    //所得分数占总分的比例  0-1;
    private float mCredPercent;


    public CreditAreaView(Context context) {
        this(context, null);
    }

    public CreditAreaView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditAreaView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        progressAttributeSet(attrs);
        init();
    }

    private void progressAttributeSet(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.CreditAreaView);
        mCredPercent = typedArray.getFloat(R.styleable.CreditAreaView_credPercent, 0.0f);
        mStartColor = typedArray.getColor(R.styleable.CreditAreaView_startColor, ContextCompat.getColor(getContext(), R.color.backgroundGradientStart));
        mEndColor = typedArray.getColor(R.styleable.CreditAreaView_endColor, ContextCompat.getColor(getContext(), R.color.creditEndColor));
        mSplitColor = typedArray.getColor(R.styleable.CreditAreaView_splitColor, Color.WHITE);
        mSplitWidth = typedArray.getDimensionPixelSize(R.styleable.CreditAreaView_splitWidth, dp2px(1));
        mSplitHeight = typedArray.getDimensionPixelSize(R.styleable.CreditAreaView_splitHeight, 0);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CreditAreaView_viewRadius, dp2px(8));
        mHasSplit = typedArray.getBoolean(R.styleable.CreditAreaView_hasSplit, true);
        mNotReachedCreditViewColor = typedArray.getColor(R.styleable.CreditAreaView_notReachedCreditViewColor,
                ContextCompat.getColor(getContext(), R.color.split));
        if (mSplitWidth < 2) {
            mSplitWidth = 2;
        }
        mGradeCount = typedArray.getInt(R.styleable.CreditAreaView_gradeCount, 4);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    private void parameterIsNotLegal() {
//        if (mCredPercent < 0 | mCredPercent > 1) {
//            throw new IllegalStateException(" please input legal data   ");
//        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mMeasuredWidth = getWidth();
        if (mSplitHeight == 0) {
            mSplitHeight = getHeight();
        }
        parameterIsNotLegal();

        RectF creditAreaRectF = new RectF(0, 0, mMeasuredWidth, getHeight());

        if (mCredPercent != 0) {
            LinearGradient linearGradient = new LinearGradient(0, 0,
                    mMeasuredWidth, 0, new int[]{mStartColor, mEndColor, mNotReachedCreditViewColor}, new float[]{0f, mCredPercent, 0f},
                    Shader.TileMode.CLAMP);
            mCreditAreaPaint.setShader(linearGradient);
        }

        canvas.drawRoundRect(creditAreaRectF, mRadius, mRadius, mCreditAreaPaint);
        drawSplit(canvas);

    }


    private void drawSplit(Canvas canvas) {
        if (mHasSplit) {
            for (int i = 1; i < mGradeCount + 1; i++) {
                int lineX = mMeasuredWidth / (mGradeCount + 1) * i;
                canvas.drawLine(lineX, 0, lineX, mSplitHeight, mSplitLinePaint);
            }
        }
    }

    private void init() {
        mCreditAreaPaint = new Paint();
        mCreditAreaPaint.setAntiAlias(true);
        mCreditAreaPaint.setColor(mNotReachedCreditViewColor);


        mSplitLinePaint = new Paint();
        mSplitLinePaint.setAntiAlias(true);
        mSplitLinePaint.setStrokeWidth(mSplitWidth);
        mSplitLinePaint.setColor(mSplitColor);
        mSplitLinePaint.setStyle(Paint.Style.STROKE);
    }

    /**
     * 自己所得分数占总分的比例
     *
     * @param percent
     */
    public void setPercent(@FloatRange(from = 0, to = 1) float percent) {
        if (percent > 1)
            percent = 1;
        setPercentAndGradeCount(percent, mGradeCount);
    }


    /**
     * @param percent    设置得分区域占总区域百分比
     * @param gradeCount 设置如果有分割线 ，将view分为几段
     */
    public void setPercentAndGradeCount(float percent, @IntRange(from = 1, to = Integer.MAX_VALUE) int gradeCount) {
        parameterIsNotLegal();
        this.mCredPercent = percent;
        this.mGradeCount = gradeCount;
        mEndColor = getColor(mCredPercent);
        postInvalidate();
    }

    //获取某一个百分比间的颜色,radio取值[0,1]
    private int getColor(float radio) {
        int redStart = Color.red(mStartColor);
        int blueStart = Color.blue(mStartColor);
        int greenStart = Color.green(mStartColor);
        int redEnd = Color.red(mEndColor);
        int blueEnd = Color.blue(mEndColor);
        int greenEnd = Color.green(mEndColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }


    private int dp2px(float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    private float sp2px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, getResources().getDisplayMetrics());
    }

}
