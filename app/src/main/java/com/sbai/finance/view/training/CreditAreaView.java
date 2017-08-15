package com.sbai.finance.view.training;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;


/**
 * Created by ${wangJie} on 2017/8/11.
 * 分数区间view 0-200 -400
 */

public class CreditAreaView extends View {
    private static final String TAG = "CreditAreaView";


    //未达到部分区域画笔
    private Paint mNotReachedCreditPaint;
    //未达到分数区域宽度
    private float mNotReachedCreditAreaWidth;
    //未达到分数区域颜色
    private int mNotReachedCreditViewColor;
    //未达到分数区域矩形
    private RectF mNotReachedCreditAreaRectF;

    Paint mSplitLinePaint;

    private int mMeasuredWidth;
    private int mMeasuredHeight;


    // 分数所占区域画笔
    Paint mCreditAreaPaint;
    //分数  主体
    private RectF mCreditAreaRectF;
    //分数所占区域宽度
    private float mCreditAreaWidth;

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
        mCredPercent = typedArray.getFloat(R.styleable.CreditAreaView_credPercent, 1);
        mStartColor = typedArray.getColor(R.styleable.CreditAreaView_startColor, ContextCompat.getColor(getContext(), R.color.backgroundGradientStart));
        mEndColor = typedArray.getColor(R.styleable.CreditAreaView_endColor, ContextCompat.getColor(getContext(), R.color.creditEndColor));
        mSplitColor = typedArray.getColor(R.styleable.CreditAreaView_splitColor, Color.WHITE);
        mSplitWidth = typedArray.getDimensionPixelSize(R.styleable.CreditAreaView_splitWidth, dp2px(1));
        mSplitHeight = typedArray.getDimensionPixelSize(R.styleable.CreditAreaView_splitHeight, 0);
        mRadius = typedArray.getDimensionPixelSize(R.styleable.CreditAreaView_viewRadius, dp2px(8));
        mHasSplit = typedArray.getBoolean(R.styleable.CreditAreaView_hasSplit, true);
        mNotReachedCreditViewColor = typedArray.getColor(R.styleable.CreditAreaView_notReachedCreditViewColor, Color.RED);
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
        if (mCredPercent < 0 | mCredPercent > 1) {
            throw new IllegalStateException(" please input legal data   ");
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        mMeasuredWidth = getWidth();
        mMeasuredHeight = getHeight();
        if (mSplitHeight == 0) {
            mSplitHeight = mMeasuredHeight;
        }
        parameterIsNotLegal();

        //如果有分数在合理区间内
        getCreditAreaPercent();

        if (mCreditAreaWidth != 0) {
            mCreditAreaRectF = new RectF(0, 0, mCreditAreaWidth, mMeasuredHeight);
            LinearGradient linearGradient = new LinearGradient(0, 0,
                    mCreditAreaWidth, mMeasuredHeight, mStartColor, mEndColor, Shader.TileMode.MIRROR);
            mCreditAreaPaint.setShader(linearGradient);
        }
        if (0 < mCredPercent) {
            mNotReachedCreditAreaRectF = new RectF(mCreditAreaWidth, 0, mMeasuredWidth, mMeasuredHeight);
        }

        //画分数区域
        if (mCreditAreaRectF != null) {
            canvas.drawRoundRect(mCreditAreaRectF, mRadius, mRadius, mCreditAreaPaint);
        }
        //画未达到分数区域
        if (mNotReachedCreditAreaRectF != null) {
            canvas.drawRoundRect(mNotReachedCreditAreaRectF, mRadius, mRadius, mNotReachedCreditPaint);
        }
        drawSplit(canvas);

    }

    private void getCreditAreaPercent() {
        if (0 < mCredPercent && mCredPercent < 1) {
            mEndColor = getColor(mCredPercent);
            mCreditAreaWidth = FinanceUtil.multiply(mMeasuredWidth, mCredPercent).floatValue();
            //如果达到最高分
        } else if (mCredPercent == 1) {
            mCreditAreaWidth = mMeasuredWidth;
            //没有分数
        } else if (mCredPercent == 0) {
            mCreditAreaWidth = 0;
        }
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
        mCreditAreaPaint.setColor(Color.RED);
        mCreditAreaPaint.setShadowLayer(8, 0, 0, mStartColor);


        mNotReachedCreditPaint = new Paint();
        mNotReachedCreditPaint.setAntiAlias(true);
        mNotReachedCreditPaint.setColor(mNotReachedCreditViewColor);
        mNotReachedCreditPaint.setStyle(Paint.Style.FILL);

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
    public void setPercent(float percent) {
        setPercentAndGradeCount(percent, mGradeCount);
    }


    /**
     * @param percent    设置得分区域占总区域百分比
     * @param gradeCount 设置如果有分割线 ，将view分为几段
     */
    public void setPercentAndGradeCount(float percent, int gradeCount) {
        parameterIsNotLegal();
        this.mCredPercent = percent;
        this.mGradeCount = gradeCount;
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
