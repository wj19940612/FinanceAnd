package com.sbai.finance.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

/**
 * Modified by john on 03/11/2017
 * <p>
 * Description: 对战点赞控件
 * <p>
 * APIs:
 */
public class PraiseView extends View {

    private Paint mPaint;
    private Bitmap mBitmap;

    private int mWidth;
    private int mHeight;
    private int mRadius;
    private int mDrawablePadding;
    private LinearGradient mGradient;
    private float mBorderWidth;
    private String mText;
    private float mOffset4CenterText;
    private RectF mRectF;

    public PraiseView(Context context) {
        super(context);
        init();
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        setWillNotDraw(false);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_praise);

        mWidth = (int) Display.dp2Px(55f, getResources());
        mHeight = (int) Display.dp2Px(22f, getResources());
        mRadius = (int) Display.dp2Px(10f, getResources());
        mDrawablePadding = (int) Display.dp2Px(3f, getResources());
        mGradient = new LinearGradient(0, 0, mWidth, 0,
                Color.parseColor("#F8DEA3"), Color.parseColor("#EF6D6A"),
                Shader.TileMode.CLAMP);
        mPaint.setShader(mGradient);
        mPaint.setColor(Color.parseColor("#ffffff"));
        float textSize = Display.sp2Px(10, getResources());
        mPaint.setTextSize(textSize);
        mBorderWidth = Display.dp2Px(1, getResources());
        mPaint.setStrokeWidth(mBorderWidth);
        mText = "0";

        mOffset4CenterText = calOffsetY4TextCenter();
        mRectF = new RectF();
    }

    protected float calOffsetY4TextCenter() {
        Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top;
        return fontHeight / 2 - fontMetrics.bottom;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setShader(mGradient);
        mPaint.setStrokeWidth(mBorderWidth);
        mPaint.setStyle(Paint.Style.STROKE);
        mRectF.set(0 + mBorderWidth, 0 + mBorderWidth,
                mWidth - mBorderWidth, mHeight - mBorderWidth);
        canvas.drawRoundRect(mRectF, mRadius, mRadius, mPaint);
        int centerX = mWidth / 2;
        int centerY = mHeight / 2;
        int bw = mBitmap.getWidth();
        int bh = mBitmap.getHeight();
        float textW = mPaint.measureText(mText);
        float contentW = bw + mDrawablePadding + textW;
        float contentL = centerX - contentW / 2;

        canvas.drawBitmap(mBitmap, contentL, centerY - bh / 2, mPaint);
        mPaint.setShader(null);
        mPaint.setStrokeWidth(1);
        mPaint.setStyle(Paint.Style.FILL);
        float textL = contentL + bw + mDrawablePadding;
        canvas.drawText(mText, textL, centerY + mOffset4CenterText, mPaint);
    }

    public void setPraise(int praise) {
        mText = praise > 999 ? "999+" : String.valueOf(praise);
        invalidate();
    }
}
