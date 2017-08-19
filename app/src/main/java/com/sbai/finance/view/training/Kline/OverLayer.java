package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.SparseArray;
import android.util.TypedValue;
import android.view.View;

import java.lang.ref.WeakReference;

public class OverLayer extends View {

    private static final int LINE = 7;
    private static final int DENSITY = 1000;
    private static final int MARGIN_BOTTOM_DP = 30; // because kline has a 30dp height timeline area
    private static final int BUTTONS_AREA_WIDTH = 80; //dp
    private SparseArray<Kline.IntersectionPoint> mIntersectionPointArray;

    private static Paint sPaint;
    private static RectF sRectF;

    private float mBaseLineWidth;
    private RedrawHandler mHandler;
    private float mButtonsAreaWidth;
    private float mMarginBottom;
    private boolean mStarted;
    private int mNextStopIndex;
    private OnFocusIntersectionPointListener mOnFocusIntersectionPointListener;

    public interface OnFocusIntersectionPointListener {
        void onFocus(Kline.IntersectionPoint point);
    }

    public OverLayer(Context context) {
        super(context);
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sRectF = new RectF();
        mBaseLineWidth = dp2Px(0.5f);
        mButtonsAreaWidth = dp2Px(BUTTONS_AREA_WIDTH);
        mMarginBottom = dp2Px(MARGIN_BOTTOM_DP);

        mHandler = new RedrawHandler(this);
        mNextStopIndex = 0;
    }

    private static class RedrawHandler extends Handler {
        private WeakReference<View> mRefs;
        private int mCounter;

        public RedrawHandler(View view) {
            mRefs = new WeakReference<>(view);
            mCounter = 0;
        }

        @Override
        public void handleMessage(Message msg) {
            View view = mRefs.get();
            if (view != null) {
                mCounter++;
                view.postInvalidateDelayed(30);
            }
        }

        public int getCounter() {
            return mCounter;
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        float startX = mHandler.getCounter() * 1.0f / DENSITY * getWidth();
        drawBackground(startX, canvas);
        drawBaseLines(startX, canvas);

        if (mNextStopIndex < mIntersectionPointArray.size()) {
            Kline.IntersectionPoint point = mIntersectionPointArray.valueAt(mNextStopIndex);
            if (startX > point.getPoint().x) {
                mNextStopIndex++;
                onFocusIntersectionPoint(point);

            }
        }

        if (mHandler.getCounter() < DENSITY && mStarted) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private void drawBackground(float startX, Canvas canvas) {
        sPaint.setColor(Color.parseColor("#222222"));
        sPaint.setStyle(Paint.Style.FILL);
        sRectF.set(startX, 0, getWidth() - mButtonsAreaWidth, getHeight() - mMarginBottom);

        canvas.drawRect(sRectF, sPaint);
    }

    private void drawBaseLines(float startX, Canvas canvas) {
        float verticalInterval = (getHeight() - mMarginBottom) / (LINE - 1);
        float topY = 0;
        float stopX = getWidth() - mButtonsAreaWidth;
        for (int i = 0; i < LINE; i++) {
            sPaint.setColor(Color.parseColor("#2a2a2a"));
            sPaint.setStyle(Paint.Style.STROKE);
            sPaint.setStrokeWidth(mBaseLineWidth);

            canvas.drawLine(startX, topY, stopX, topY, sPaint);

            topY += verticalInterval;
        }
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }

    public void start() {
        mStarted = true;
        mHandler.sendEmptyMessage(0);
    }

    public void stop() {
        mStarted = false;
        mHandler.removeCallbacksAndMessages(null);
    }

    private void onFocusIntersectionPoint(Kline.IntersectionPoint point) {
        stop();

        if (mOnFocusIntersectionPointListener != null) {
            mOnFocusIntersectionPointListener.onFocus(point);
        }
    }

    public void setIntersectionPointArray(SparseArray<Kline.IntersectionPoint> intersectionPointArray) {
        mIntersectionPointArray = intersectionPointArray;
    }

    public void setOnFocusIntersectionPointListener(OnFocusIntersectionPointListener onFocusIntersectionPointListener) {
        mOnFocusIntersectionPointListener = onFocusIntersectionPointListener;
    }
}
