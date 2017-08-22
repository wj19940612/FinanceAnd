package com.sbai.finance.view.leveltest;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.graphics.Shader;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.model.levelevaluation.EvaluationResult;


/**
 * Created by ${wangJie} on 2017/8/2.
 */

public class ScoreView extends View {
    private static final String TAG = "ScoreView";
    //数据个数
    private int DataCount;
    //每个角的弧度
    private float mRadian;
    //雷达图半径
    private float mRadius;
    //中心X坐标
    private int mCenterX;
    //中心Y坐标
    private int mCenterY;
    //各维度标题
    private String[] mTitles = {"盈利能力", "指标分析", "基本面分析", "风险控制", "理论掌握"};
    //各维度分值
    private double[] mData;
    //数据最大值
    private float mMaxValue;
    //雷达图与标题的间距
    private int mRadarMargin;
    //雷达区画笔
    private Paint mMainPaint;
    //数据区画笔
    private Paint mValuePaint;
    //分数画笔
    private Paint mScorePaint;
    //标题画笔
    private Paint mTitlePaint;
    //五个点画笔
    private Paint mPointPaint;

    //外围的圆环
    private Paint mOutCirclePaint;
    //外围环的颜色
    private int mOutCircleColor;

    //内部圆环
    private Paint mMiddleLoopPaint;
    //内部圆圈
    private Paint mMiddleCirclePaint;

    //分数大小
    private float mScoreTextSize;
    //标题文字大小
    private int mTitleSize;
    //内圆环直径
    private int mMiddleLoopRadius;
    //是否显示分数
    private boolean mShowScore;


    //是否显示外围5个点
    private boolean mShowFivePoint;
    private EvaluationResult mEvaluationResult;
    private int mScoreTextColor;
    private int mTitleTextColor;
    private boolean mHasInsideCircle;
    //内部圆半径
    private int mInsideCircleRadius;
    private Paint mInsidePaint;
    private int mInsideStrokeColor;
    private int mMiddleColor;
    private int mMiddleLoopCircleColor;
    private int mValueColor;
    private int mRadarColor;


    private int mStartX;
    private int mStartY;
    private int mEndX;
    private int mEndY;

    public ScoreView(Context context) {
        this(context, null);
    }

    public ScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ScoreView);
        DataCount = typedArray.getInt(R.styleable.ScoreView_areaCount, 5);
        mShowScore = typedArray.getBoolean(R.styleable.ScoreView_showScore, true);
        mScoreTextSize = typedArray.getDimension(R.styleable.ScoreView_scoreTextSizePx, 72);
        mScoreTextColor = typedArray.getColor(R.styleable.ScoreView_scoreViewTitleTextColor, ContextCompat.getColor(getContext(), R.color.blackPrimary));
        mShowFivePoint = typedArray.getBoolean(R.styleable.ScoreView_showAreaPoint, false);
        mOutCircleColor = typedArray.getColor(R.styleable.ScoreView_outSideCircleColor, ContextCompat.getColor(getContext(), R.color.yellowAssist));
        mTitleTextColor = typedArray.getColor(R.styleable.ScoreView_scoreViewTitleTextColor, ContextCompat.getColor(getContext(), R.color.luckyText));
        mTitleSize = typedArray.getDimensionPixelSize(R.styleable.ScoreView_scoreTextSizePx, 12);
        mHasInsideCircle = typedArray.getBoolean(R.styleable.ScoreView_showInsideCircle, false);
        mInsideStrokeColor = typedArray.getColor(R.styleable.ScoreView_insideCircleColor, ContextCompat.getColor(getContext(), R.color.background));
        mMiddleColor = typedArray.getColor(R.styleable.ScoreView_middleCircleColor, Color.parseColor("#FFFDF0"));
        mMiddleLoopCircleColor = typedArray.getColor(R.styleable.ScoreView_middleLoopCircleColor, Color.parseColor("#FFDF93"));
        mValueColor = typedArray.getColor(R.styleable.ScoreView_valueColor, Color.parseColor("#ffc336"));
        mRadarColor = typedArray.getColor(R.styleable.ScoreView_radarLineColor, Color.parseColor("#FFDF93"));
        typedArray.recycle();
    }


    private void init() {
        mRadarMargin = px2dp(15);
        mTitleSize = px2dp(mTitleSize);
        mRadian = (float) (Math.PI * 2 / DataCount);

        mMainPaint = new Paint();
        mMainPaint.setAntiAlias(true);
        mMainPaint.setStrokeWidth(px2dp(1));
        mMainPaint.setColor(mRadarColor);
        mMainPaint.setStyle(Paint.Style.STROKE);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setAntiAlias(true);
        mOutCirclePaint.setStrokeWidth(px2dp(1));
        mOutCirclePaint.setColor(mOutCircleColor);
        mOutCirclePaint.setStyle(Paint.Style.STROKE);


        mMiddleCirclePaint = new Paint();
        mMiddleCirclePaint.setAntiAlias(true);
        mMiddleCirclePaint.setColor(mMiddleColor);
        mMiddleCirclePaint.setAlpha(120);
        mMiddleCirclePaint.setStyle(Paint.Style.FILL);


        mMiddleLoopPaint = new Paint();
        mMiddleLoopPaint.setAntiAlias(true);
        mMiddleLoopPaint.setColor(mMiddleLoopCircleColor);
        mMiddleLoopPaint.setStyle(Paint.Style.STROKE);
        mMiddleLoopPaint.setStrokeWidth(px2dp(1));
        if (mShowFivePoint) {
            PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
            mMiddleLoopPaint.setPathEffect(effects);
        }

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(Color.parseColor("#80ffc200"));
        mPointPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setStrokeWidth(px2dp(2));
        mValuePaint.setColor(mValueColor);
        mValuePaint.setAlpha(120);
        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mScorePaint = new Paint();
        mScorePaint.setAntiAlias(true);
        mScorePaint.setTextSize(mScoreTextSize);
        mScorePaint.setColor(mScoreTextColor);
        mScorePaint.setTextAlign(Paint.Align.CENTER);
        mScorePaint.setStyle(Paint.Style.FILL);

        mTitlePaint = new Paint();
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setColor(mTitleTextColor);
        mTitlePaint.setStyle(Paint.Style.FILL);

        if (mHasInsideCircle) {
            mInsidePaint = new Paint();
            mInsidePaint.setAntiAlias(true);
            mInsidePaint.setStrokeWidth(px2dp(1));
            mInsidePaint.setColor(mInsideStrokeColor);
            mInsidePaint.setStyle(Paint.Style.STROKE);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //雷达图半径
        mRadius = Math.min(h, w) / 2 * 0.5f;
        mMaxValue = mRadius;

        if (mEvaluationResult != null) {
            double profitAccuracy = mEvaluationResult.getProfitAccuracy() > 1 ? 1 : mEvaluationResult.getProfitAccuracy();
            double skillAccuracy = mEvaluationResult.getSkillAccuracy() > 1 ? 1 : mEvaluationResult.getSkillAccuracy();
            double baseAccuracy = mEvaluationResult.getBaseAccuracy() > 1 ? 1 : mEvaluationResult.getBaseAccuracy();
            double riskAccuracy = mEvaluationResult.getRiskAccuracy() > 1 ? 1 : mEvaluationResult.getRiskAccuracy();
            double theoryAccuracy = mEvaluationResult.getTheoryAccuracy() > 1 ? 1 : mEvaluationResult.getTheoryAccuracy();
            double[] dataScore = {
                    profitAccuracy * mMaxValue, skillAccuracy * mMaxValue, baseAccuracy * mMaxValue,
                    riskAccuracy * mMaxValue, theoryAccuracy * mMaxValue};

            mData = dataScore;
        }

        if (mHasInsideCircle) {
            mInsideCircleRadius = (int) (mRadius / 3);
            mMiddleLoopRadius = mInsideCircleRadius * 2;
        } else {
            mMiddleLoopRadius = (int) (mRadius * 0.8);
        }
        //中心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawLines(canvas);
        drawCircle(canvas);
        drawRegion(canvas);
        if (mShowScore) {
            drawScore(canvas);
        }
        drawTitle(canvas);
    }


    private void drawCircle(Canvas canvas) {
        canvas.drawCircle(mCenterX, mCenterY, mMiddleLoopRadius, mMiddleLoopPaint);
        if (mHasInsideCircle) {
            canvas.drawCircle(mCenterX, mCenterY, mInsideCircleRadius, mInsidePaint);
        } else {
            canvas.drawCircle(mCenterX, mCenterY, mMiddleLoopRadius - 2, mMiddleCirclePaint);
        }
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mOutCirclePaint);
    }


    /**
     * 绘制连接线
     *
     * @param canvas 画布
     */
    private void drawLines(Canvas canvas) {
        Path path = new Path();
        for (int i = 0; i < DataCount; i++) {
            int x = getPoint(i).x;
            int y = getPoint(i).y;

            if (i == 1) {
                mEndX = x;

                Log.d(TAG, "show: " + x + " " + y);
            } else if (i == 3) {
                mStartX = x;
                mStartY = y;
                Log.d(TAG, "end: " + x + " " + y);
            }
            path.reset();
            path.moveTo(mCenterX, mCenterY);
            path.lineTo(x, y);
            canvas.drawPath(path, mMainPaint);
            if (mShowFivePoint) {
                canvas.drawCircle(x, y, px2dp(3), mPointPaint);
            }
        }
    }

    /**
     * 绘制覆盖区域
     *
     * @param canvas 画布
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();
        if (mData == null) return;
        for (int i = 0; i < DataCount; i++) {
            //计算百分比
            float percent = (float) (mData[i] / mMaxValue);
            int x = getPoint(i, 0, percent).x;
            int y = getPoint(i, 0, percent).y;
            if (i == 0) {
                path.moveTo(x, y);
            } else {
                path.lineTo(x, y);
            }
        }

        //绘制填充区域的边界
        path.close();
        mValuePaint.setStyle(Paint.Style.STROKE);
        canvas.drawPath(path, mValuePaint);

        //绘制填充区域
        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);
        canvas.drawPath(path, mValuePaint);
    }

    /**
     * 绘制分数
     *
     * @param canvas 画布
     */
    private void drawScore(Canvas canvas) {
        int score = 0;
        //计算总分
        if (mEvaluationResult != null) {
            score = mEvaluationResult.getTotalScore();
        }
        canvas.drawText(String.valueOf(score), mCenterX, mCenterY + mScoreTextSize / 3, mScorePaint);
    }

    /**
     * 绘制标题
     *
     * @param canvas 画布
     */
    private void drawTitle(Canvas canvas) {
        for (int i = 0; i < DataCount; i++) {
            int x = getPoint(i, mRadarMargin, 1).x;
            int y = getPoint(i, mRadarMargin, 1).y;
            int iconHeight = 0;
            float titleWidth = mTitlePaint.measureText(mTitles[i]);

            if (i == 1) {
                y += (iconHeight / 2);
            } else if (i == 2) {
                x -= titleWidth;
                y += (iconHeight / 2);
            } else if (i == 3) {
                x -= titleWidth;
            } else if (i == 4) {
                x -= titleWidth / 2;
            }
            canvas.drawText(mTitles[i], x, y, mTitlePaint);
        }
    }


    /**
     * 获取雷达图上各个点的坐标
     *
     * @param position 坐标位置（右上角为0，顺时针递增）
     * @return 坐标
     */
    private Point getPoint(int position) {
        return getPoint(position, 0, 1);
    }

    /**
     * 获取雷达图上各个点的坐标（包括维度标题坐标）
     *
     * @param position    坐标位置
     * @param radarMargin 雷达图与维度标题的间距
     * @param percent     覆盖区的的百分比
     * @return 坐标
     */
    private Point getPoint(int position, int radarMargin, float percent) {
        int x = 0;
        int y = 0;

        if (position == 0) {
            x = (int) (mCenterX + (mRadius + radarMargin) * Math.sin(mRadian) * percent);
            y = (int) (mCenterY - (mRadius + radarMargin) * Math.cos(mRadian) * percent);

        } else if (position == 1) {
            x = (int) (mCenterX + (mRadius + radarMargin) * Math.sin(mRadian / 2) * percent);
            y = (int) (mCenterY + (mRadius + radarMargin) * Math.cos(mRadian / 2) * percent);

        } else if (position == 2) {
            x = (int) (mCenterX - (mRadius + radarMargin) * Math.sin(mRadian / 2) * percent);
            y = (int) (mCenterY + (mRadius + radarMargin) * Math.cos(mRadian / 2) * percent);

        } else if (position == 3) {
            x = (int) (mCenterX - (mRadius + radarMargin) * Math.sin(mRadian) * percent);
            y = (int) (mCenterY - (mRadius + radarMargin) * Math.cos(mRadian) * percent);

        } else if (position == 4) {
            x = mCenterX;
            y = (int) (mCenterY - (mRadius + radarMargin) * percent);
        }

        return new Point(x, y);
    }

    /**
     * 获取文本的高度
     *
     * @param paint 文本绘制的画笔
     * @return 文本高度
     */
    private int getTextHeight(Paint paint) {
        Paint.FontMetrics fontMetrics = paint.getFontMetrics();
        return (int) (fontMetrics.descent - fontMetrics.ascent);
    }

    private int px2dp(float px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, px, getResources().getDisplayMetrics());
    }

    private int px2sp(float px) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, px, getResources().getDisplayMetrics());
    }

    private int sp2px(float sp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, sp, getResources().getDisplayMetrics());
    }

    public void setData(EvaluationResult data) {
        mEvaluationResult = data;
        postInvalidate();
    }

    //了解分页数据
    public void setUserTrainScoreData(EvaluationResult evaluationResult) {
//        TestResultModel testResultModel = new TestResultModel();
//        testResultModel.setAllAccuracy(0.50);
//        testResultModel.setPassPercent(0.20);
//        testResultModel.setLevel(4);
//
//        testResultModel.setBaseAccuracy(0.7);
//        testResultModel.setProfitAccuracy(0.7);
//        testResultModel.setRiskAccuracy(0.5);
//        testResultModel.setSkillAccuracy(0.9);
//        testResultModel.setTheoryAccuracy(0.3);
//        mTestResultModel = testResultModel;

        mEvaluationResult = evaluationResult;
        mValuePaint.reset();
        mValuePaint.setAntiAlias(true);
        int startColor = Color.parseColor("#64A0FE");
        int endColor = Color.parseColor("#995BF4");
        LinearGradient gradient = new LinearGradient(0, 0, 100, 100, startColor, endColor, Shader.TileMode.MIRROR);
        mValuePaint.setShader(gradient);
        mValuePaint.setStyle(Paint.Style.FILL);
        invalidate();
    }
}
