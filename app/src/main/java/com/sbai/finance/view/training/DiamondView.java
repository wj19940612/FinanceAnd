package com.sbai.finance.view.training;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.view.animation.BounceInterpolator;

import com.sbai.finance.R;

/**
 * 菱形
 */

public class DiamondView extends View {
    private Paint mPaint;
    private int mWidth;
    private int mHeight;
    private int mColor;
    private float mDashWidth;
    private float mLineHeight;
    private float mLineWidth;
    private int mLineColor;
    private boolean mSelected;

    private void initPaint() {
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        mPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        mPaint.setDither(true);
        mPaint.setAntiAlias(true);
        mPaint.setStrokeWidth(2f);
    }

    public DiamondView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        initPaint();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.DiamondView);
        int defaultDashWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 3,
                getResources().getDisplayMetrics());
        int defaultLineHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 1,
                getResources().getDisplayMetrics());

        mColor = typedArray.getColor(R.styleable.DiamondView_backgroundColor, Color.WHITE);
        mDashWidth = typedArray.getDimensionPixelOffset(R.styleable.DiamondView_dashWidth, defaultDashWidth);
        mLineHeight = typedArray.getDimensionPixelOffset(R.styleable.DiamondView_lineHeight, defaultLineHeight);
        mLineWidth = typedArray.getDimensionPixelOffset(R.styleable.DiamondView_lineWidth, defaultLineHeight);
        mLineColor = typedArray.getColor(R.styleable.DiamondView_lineColor, Color.WHITE);
        typedArray.recycle();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mWidth / 2, mHeight / 2);
        if (mSelected) {
            mPaint.setColor(Color.WHITE);
            drawView(canvas, mHeight, mWidth);
            mPaint.setColor(mColor);
            drawView(canvas, mHeight - 4, mWidth - 4);
        } else {
            mPaint.setColor(mColor);
            drawView(canvas, mHeight, mWidth);
        }
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


    public void setBackgroundColor(int color) {
        mColor = color;
        invalidate();
    }

    public void setSelected(boolean selected) {
        mSelected = selected;
        invalidate();
    }

    public boolean getSelected() {
        return mSelected;
    }
}
