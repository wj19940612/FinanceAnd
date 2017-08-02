package com.sbai.finance.view.leveltest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.Point;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import static android.content.ContentValues.TAG;

/**
 * Created by ${wangJie} on 2017/8/2.
 */

public class CreditScoreView extends View {

    //数据个数
    private int DataCount = 5;
    //每个角的弧度
    private float mRadian = (float) (Math.PI * 2 / DataCount);
    //雷达图半径
    private float mRadius;
    //中心X坐标
    private int mCenterX;
    //中心Y坐标
    private int mCenterY;
    //各维度标题
    private String[] mTitles = {"盈利能力", "信用历史", "基本面分析", "指标分析", "理论掌握"};
    //各维度图标
//    private int[] icons = {R.mipmap.ic_performance, R.mipmap.ic_history, R.mipmap.ic_contacts,
//            R.mipmap.ic_predilection, R.mipmap.ic_identity};
    //各维度分值
    private float[] data = {170, 180, 160, 170, 180};
    //数据最大值
    private float mMaxValue = 280;
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
    //图标画笔
    private Paint mIconPaint;
    //五个点画笔
    private Paint mPointPaint;

    //外围的圆环
    private Paint mOutCirclePaint;

    //内部圆环
    private Paint mInsideLoopPaint;
    //内部圆圈
    private Paint mInsideCirclePaint;

    //分数大小
    private int mScoreSize;
    //标题文字大小
    private int mTitleSize;
    //内圆环直径
    private int mInsideLoopRadius;

    public CreditScoreView(Context context) {
        this(context, null);
    }

    public CreditScoreView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CreditScoreView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private void init() {

        mScoreSize = px2sp(28);
        mRadarMargin = px2dp(15);
        mTitleSize = px2sp(12);

        mMainPaint = new Paint();
        mMainPaint.setAntiAlias(true);
        mMainPaint.setStrokeWidth(px2dp(1));
        mMainPaint.setColor(Color.parseColor("#FFDF93"));
        mMainPaint.setStyle(Paint.Style.STROKE);

        mOutCirclePaint = new Paint();
        mOutCirclePaint.setAntiAlias(true);
        mOutCirclePaint.setStrokeWidth(px2dp(1));
        mOutCirclePaint.setColor(Color.parseColor("#ffc336"));
        mOutCirclePaint.setStyle(Paint.Style.STROKE);


        mInsideCirclePaint = new Paint();
        mInsideCirclePaint.setAntiAlias(true);
        mInsideCirclePaint.setColor(Color.parseColor("#FFFDF0"));
        mInsideCirclePaint.setAlpha(120);
        mInsideCirclePaint.setStyle(Paint.Style.FILL);


        mInsideLoopPaint = new Paint();
        mInsideLoopPaint.setAntiAlias(true);
        mInsideLoopPaint.setColor(Color.parseColor("#FFDF93"));
        mInsideLoopPaint.setStyle(Paint.Style.STROKE);
        mInsideLoopPaint.setStrokeWidth(px2dp(1));
        PathEffect effects = new DashPathEffect(new float[]{5, 5, 5, 5}, 1);
        mInsideLoopPaint.setPathEffect(effects);

        mPointPaint = new Paint();
        mPointPaint.setAntiAlias(true);
        mPointPaint.setColor(Color.parseColor("#80ffc200"));
        mPointPaint.setStyle(Paint.Style.FILL);

        mValuePaint = new Paint();
        mValuePaint.setAntiAlias(true);
        mValuePaint.setStrokeWidth(px2dp(2));
        mValuePaint.setColor(Color.parseColor("#ffc336"));
        mValuePaint.setAlpha(120);
        mValuePaint.setStyle(Paint.Style.FILL_AND_STROKE);

        mScorePaint = new Paint();
        mScorePaint.setAntiAlias(true);
        mScorePaint.setTextSize(mScoreSize);
        mScorePaint.setColor(Color.parseColor("#ffc336"));
        mScorePaint.setTextAlign(Paint.Align.CENTER);
        mScorePaint.setStyle(Paint.Style.FILL);

        mTitlePaint = new Paint();
        mTitlePaint.setAntiAlias(true);
        mTitlePaint.setTextSize(mTitleSize);
        mTitlePaint.setColor(Color.parseColor("#666666"));
        mTitlePaint.setStyle(Paint.Style.FILL);

        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //雷达图半径
        mRadius = Math.min(h, w) / 2 * 0.5f;
        mInsideLoopRadius = (int) (mRadius * 0.8);
        //中心坐标
        mCenterX = w / 2;
        mCenterY = h / 2;
        postInvalidate();
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        drawCircle(canvas);
        drawPolygon(canvas);
        drawLines(canvas);
        drawRegion(canvas);
        drawScore(canvas);
        drawTitle(canvas);
        drawIcon(canvas);
    }

    private void drawCircle(Canvas canvas) {
        Log.d(TAG, "drawCircle: " + mInsideLoopRadius + " " + mRadius);
        canvas.drawCircle(mCenterX, mCenterY, mInsideLoopRadius, mInsideLoopPaint);
        canvas.drawCircle(mCenterX, mCenterY, mInsideLoopRadius - 2, mInsideCirclePaint);
        canvas.drawCircle(mCenterX, mCenterY, mRadius, mOutCirclePaint);
    }

    /**
     * 绘制多边形
     *
     * @param canvas 画布
     */
    private void drawPolygon(Canvas canvas) {
//        Path path = new Path();
//        for (int i = 0; i < DataCount; i++) {
//            if (i == 0) {
//                path.moveTo(getPoint(i).x, getPoint(i).y);
//            } else {
//                path.lineTo(getPoint(i).x, getPoint(i).y);
//            }
//        }
//
//        //闭合路径
//        path.close();
//        canvas.drawPath(path, mMainPaint);
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
            path.reset();
            path.moveTo(mCenterX, mCenterY);
            path.lineTo(x, y);
            canvas.drawPath(path, mMainPaint);
            canvas.drawCircle(x, y, px2dp(3), mPointPaint);
        }
    }

    /**
     * 绘制覆盖区域
     *
     * @param canvas 画布
     */
    private void drawRegion(Canvas canvas) {
        Path path = new Path();

        for (int i = 0; i < DataCount; i++) {
            //计算百分比
            float percent = data[i] / mMaxValue;
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
        for (int i = 0; i < DataCount; i++) {
            score += data[i];
        }
//        canvas.drawText(score + "", mCenterX, mCenterY + mScoreSize / 2, mScorePaint);
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

//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icons[i]);
//            int iconHeight = bitmap.getHeight();
            int iconHeight = 0;
            float titleWidth = mTitlePaint.measureText(mTitles[i]);

            //底下两个角的坐标需要向下移动半个图片的位置（1、2）
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
     * 绘制图标
     *
     * @param canvas 画布
     */
    private void drawIcon(Canvas canvas) {
//        for (int i = 0; i < DataCount; i++) {
//            int x = getPoint(i, mRadarMargin, 1).x;
//            int y = getPoint(i, mRadarMargin, 1).y;
//
//            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), icons[i]);
//            int iconWidth = bitmap.getWidth();
//            int iconHeight = bitmap.getHeight();
//            float titleWidth = mTitlePaint.measureText(mTitles[i]);
//
//            //上面获取到的x、y坐标是标题左下角的坐标
//            //需要将图标移动到标题上方居中位置
//            if (i == 0) {
//                x += (titleWidth - iconWidth) / 2;
//                y -= (iconHeight + getTextHeight(mTitlePaint));
//            } else if (i == 1) {
//                x += (titleWidth - iconWidth) / 2;
//                y -= (iconHeight / 2 + getTextHeight(mTitlePaint));
//            } else if (i == 2) {
//                x -= (iconWidth + (titleWidth - iconWidth) / 2);
//                y -= (iconHeight / 2 + getTextHeight(mTitlePaint));
//            } else if (i == 3) {
//                x -= (iconWidth + (titleWidth - iconWidth) / 2);
//                y -= (iconHeight + getTextHeight(mTitlePaint));
//            } else if (i == 4) {
//                x -= iconWidth / 2;
//                y -= (iconHeight + getTextHeight(mTitlePaint));
//            }
//
//            canvas.drawBitmap(bitmap, x, y, mTitlePaint);
//        }
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
     * 获取雷达图上各个点的坐标（包括维度标题与图标的坐标）
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
}
