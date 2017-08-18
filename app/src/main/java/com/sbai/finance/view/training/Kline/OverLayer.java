package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.View;

import java.lang.ref.WeakReference;

public class OverLayer extends View {

    private static final int LINE = 7;
    private static final int DENSITY = 1000;

    private static Paint sPaint;
    private static RectF sRectF;

    private float mBaseLineWidth;
    private RedrawHandler mHandler;

    public OverLayer(Context context) {
        super(context);
        sPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        sRectF = new RectF();
        mBaseLineWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f,
                getResources().getDisplayMetrics());

        mHandler = new RedrawHandler(this);
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

        if (mHandler.getCounter() < DENSITY) {
            mHandler.sendEmptyMessage(0);
        }
    }

    private void drawBackground(float startX, Canvas canvas) {
        sPaint.setColor(Color.parseColor("#222222"));
        sPaint.setStyle(Paint.Style.FILL);
        sRectF.set(startX, 0, getWidth(), getHeight());
        canvas.drawRect(sRectF, sPaint);
    }

    private void drawBaseLines(float startX, Canvas canvas) {
        float verticalInterval = getHeight() * 1.0f / (LINE - 1);
        float topY = 0;
        float stopX = getWidth();
        for (int i = 0; i < LINE; i++) {
            sPaint.setColor(Color.parseColor("#2a2a2a"));
            sPaint.setStyle(Paint.Style.STROKE);
            sPaint.setStrokeWidth(mBaseLineWidth);
            canvas.drawLine(startX, topY, stopX, topY, sPaint);
            topY += verticalInterval;
        }
    }
}
