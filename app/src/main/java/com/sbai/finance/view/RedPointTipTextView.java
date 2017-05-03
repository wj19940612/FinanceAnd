package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.sbai.finance.R;

/**
 * Created by ${wangJie} on 2017/4/27.
 */

public class RedPointTipTextView extends AppCompatTextView {

    //顶部的小红点显示
    private int mPointVisibility;
    private Paint mPointPaint;

    public RedPointTipTextView(Context context) {
        this(context, null);
    }

    public RedPointTipTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 16842884);
    }

    public RedPointTipTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.RedPointTipTextView);
        mPointVisibility = typedArray.getInt(R.styleable.RedPointTipTextView_redPointTipVisibility, 8);
        mPointPaint = new Paint();
        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mPointVisibility == VISIBLE) {
            int width = getWidth();
            int paddingRight = getPaddingRight();
            mPointPaint.setColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            mPointPaint.setAntiAlias(true);
            mPointPaint.setDither(true);
            mPointPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            canvas.drawCircle(width - getPaddingRight() / 2, paddingRight, TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 6, getResources().getDisplayMetrics()), mPointPaint);
        } else {
            canvas.drawCircle(0, 0, 0, mPointPaint);
        }
    }

    public void setRedPointVisibility(int visibility) {
        mPointVisibility = visibility;
        invalidate();
    }
}
