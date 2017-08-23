package com.sbai.finance.view.training;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * 菱形图片
 */

public class DiamondImageView extends ImageView {
    private Paint mPaint;
    private Xfermode mXfermode;
    private Bitmap mRectMask;

    public DiamondImageView(Context context) {
        this(context, null);
    }

    public DiamondImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DiamondImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setColor(Color.RED);
        // 关键方法
        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_IN);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        createMask();
    }

    private void createMask() {
        if (mRectMask == null) {
            int maskWidth = getMeasuredWidth();
            int maskHeight = getMeasuredHeight();
            if (maskWidth > 0 && maskHeight > 0) {
                mRectMask = Bitmap.createBitmap(maskWidth, maskHeight, Bitmap.Config.ARGB_8888);
                Canvas canvas = new Canvas(mRectMask);
                canvas.translate(maskWidth / 2, maskHeight / 2);
                drawDiamond(canvas, maskHeight, maskWidth - 2);
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        int id = canvas.saveLayer(0, 0, canvas.getWidth(), canvas.getHeight(), null, Canvas.ALL_SAVE_FLAG);
        super.onDraw(canvas);
        // 关键方法
        if (getMeasuredHeight() > 0 && getMeasuredWidth() > 0) {
            mPaint.setXfermode(mXfermode);
            canvas.drawBitmap(mRectMask, 0, 0, mPaint);
            mPaint.setXfermode(null);
            canvas.restoreToCount(id);
        }
    }

    private void drawDiamond(Canvas canvas, int height, int width) {
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
}
