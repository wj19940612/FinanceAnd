package com.sbai.finance.view.training;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import com.sbai.finance.R;

import java.util.ArrayList;
import java.util.List;

/**
 * 菱形
 */

public class DiamondView extends View {
    public static final String TAG = "DiamondView";
    public static final int TYPE_WHITE = 0;
    public static final int TYPE_DARK = 1;
    private Paint mPaint;
    private Paint mEdgePaint;
    private int mWidth;
    private int mHeight;
    private int mColor;
    private boolean mSelected;
    private List<Point> mPoints;

    private int mCurrentIndex;
    private Point mCurrentPoint;
    private FinishDrawListener mFinishDrawListener;

    public interface FinishDrawListener {
        void finish();
    }

    public void setFinishDrawListener(FinishDrawListener finishDrawListener) {
        mFinishDrawListener = finishDrawListener;
    }

    public DiamondView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        initPaint();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DiamondView);
        mColor = typedArray.getColor(R.styleable.DiamondView_backgroundColor, Color.WHITE);
        typedArray.recycle();
    }

    private void initPaint() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);

        mEdgePaint = new Paint();
//        mEdgePaint.setColor(Color.parseColor("#6F56CA"));
        mEdgePaint.setColor(Color.WHITE);
        mEdgePaint.setStyle(Paint.Style.STROKE);
        mEdgePaint.setDither(true);
        mEdgePaint.setAntiAlias(true);
        mEdgePaint.setStrokeWidth(8f);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        initPoints();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);
        if (mSelected) {
            drawView(canvas, mHeight - 2, mWidth - 2);
            if (mCurrentPoint == null && mCurrentIndex < mPoints.size() - 1) {
                startAnimation(mPoints.get(mCurrentIndex), mPoints.get(mCurrentIndex + 1));
            }
            if (mCurrentPoint != null && mCurrentIndex < mPoints.size()) {
                Point startPoint = mPoints.get(mCurrentIndex);
                switch (mCurrentIndex) {
                    case 0:
                        drawLine(canvas, startPoint, mCurrentPoint);
                        break;
                    case 1:
                        drawLine(canvas, mPoints.get(0), mPoints.get(1));
                        drawLine(canvas, startPoint, mCurrentPoint);
                        break;
                    case 2:
                        drawLine(canvas, mPoints.get(0), mPoints.get(1));
                        drawLine(canvas, mPoints.get(1), mPoints.get(2));
                        drawLine(canvas, startPoint, mCurrentPoint);
                        break;
                    case 3:
                        drawLine(canvas, mPoints.get(0), mPoints.get(1));
                        drawLine(canvas, mPoints.get(1), mPoints.get(2));
                        drawLine(canvas, mPoints.get(2), mPoints.get(3));
                        drawLine(canvas, startPoint, mCurrentPoint);
                        break;
                    case 4:
                        drawLine(canvas, mPoints.get(0), mPoints.get(1));
                        drawLine(canvas, mPoints.get(1), mPoints.get(2));
                        drawLine(canvas, mPoints.get(2), mPoints.get(3));
                        drawLine(canvas, mPoints.get(3), mPoints.get(4));
                        drawLine(canvas, startPoint, mCurrentPoint);
                        break;
                    case 5:
                        drawLine(canvas, mPoints.get(0), mPoints.get(1));
                        drawLine(canvas, mPoints.get(1), mPoints.get(2));
                        drawLine(canvas, mPoints.get(2), mPoints.get(3));
                        drawLine(canvas, mPoints.get(3), mPoints.get(4));
                        drawLine(canvas, mPoints.get(4), mPoints.get(5));
                        drawLine(canvas, startPoint, mCurrentPoint);
                        break;
                }
            }
        } else {
            mCurrentIndex = 0;
            mCurrentPoint = null;
            drawView(canvas, mHeight, mWidth);
        }
    }

    private void drawLine(Canvas canvas, Point startPoint, Point endPoint) {
        canvas.drawLine(startPoint.getX(), startPoint.getY(), endPoint.getX(), endPoint.getY(), mEdgePaint);
    }

    private void drawView(Canvas canvas, int height, int width) {
        float h = (float) ((height - width / Math.sqrt(3)) / 2);
        Path path = new Path();
        path.moveTo(0, -height / 2);
        path.lineTo(width / 2, -h);
        path.lineTo(width / 2, h);
        path.lineTo(0, height / 2);
        path.lineTo(-width / 2, h);
        path.lineTo(-width / 2, -h);
        path.close();
        canvas.drawPath(path, mPaint);
    }

    private void initPoints() {
        float h = (float) ((mHeight - mWidth / Math.sqrt(3)) / 2);
        if (mPoints == null) {
            mPoints = new ArrayList<>();
        }
        mPoints.clear();
        mPoints.add(new Point(0, -mHeight / 2 + 2));
        mPoints.add(new Point(-mWidth / 2 + 2, -h));
        mPoints.add(new Point(-mWidth / 2 + 2, h));
        mPoints.add(new Point(0, mHeight / 2 - 1));
        mPoints.add(new Point(mWidth / 2 - 2, h));
        mPoints.add(new Point(mWidth / 2 - 2, -h));
        mPoints.add(new Point(0, -mHeight / 2 + 2));
    }

    private void startAnimation(Point startPoint, Point endPoint) {
        ValueAnimator anim = ValueAnimator.ofObject(new PointEvaluator(), startPoint, endPoint);
        anim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (mCurrentIndex == mPoints.size() - 2) {
                    if (mFinishDrawListener != null) {
                        mFinishDrawListener.finish();
                    }
                    return;
                }
                mCurrentIndex++;
                mCurrentPoint = null;
                if (mCurrentIndex < mPoints.size() - 1) {
                    invalidate();
                }
            }
        });
        anim.setInterpolator(new LinearInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mCurrentPoint = (Point) animation.getAnimatedValue();
                if (mCurrentPoint != null && (mCurrentIndex < mPoints.size() - 1)) {
                    invalidate();
                }
            }
        });
        anim.setDuration(20);
        anim.start();
    }

    public void setBackgroundType(int type) {
        if (TYPE_DARK == type) {
            mPaint.setColor(Color.parseColor("#372F54"));
            mEdgePaint.setColor(Color.WHITE);
        } else {
            mPaint.setColor(Color.WHITE);
            mEdgePaint.setColor(Color.parseColor("#372F54"));
        }
        mSelected = false;
        invalidate();
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    public boolean getSelected() {
        return mSelected;
    }

    public class PointEvaluator implements TypeEvaluator<Point> {

        @Override
        public Point evaluate(float fraction, Point startValue, Point endValue) {
            float x = startValue.getX() + fraction * (endValue.getX() - startValue.getX());
            float y = startValue.getY() + fraction * (endValue.getY() - startValue.getY());
            Point point = new Point(x, y);
            return point;
        }

    }

    public static class Point {

        private float x;

        private float y;

        public Point(float x, float y) {
            this.x = x;
            this.y = y;
        }

        public float getX() {
            return x;
        }

        public float getY() {
            return y;
        }

    }
}
