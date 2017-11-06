package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Shader;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

import com.sbai.finance.R;

/**
 * Created by ${wangJie} on 2017/11/5.
 */

public class BgShadowTextView extends AppCompatTextView {

    private int mDefaultColor;
    private int mStartColor;
    private int mEndColorColor;
    private Paint mRankPaint;
    private LinearGradient mLinearGradient;
    private Paint mVirgulePaint;

    public BgShadowTextView(Context context) {
        this(context, null);
    }

    public BgShadowTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BgShadowTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mDefaultColor = Color.parseColor("#4C4165");
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BgShadowTextView);
        mStartColor = typedArray.getColor(R.styleable.BgShadowTextView_bgStartColor, mDefaultColor);
        mEndColorColor = typedArray.getColor(R.styleable.BgShadowTextView_bgEndColor, mDefaultColor);
        typedArray.recycle();

        init();
    }

    private void init() {
        mRankPaint = new Paint();
        mRankPaint.setStyle(Paint.Style.STROKE);
        mRankPaint.setStrokeWidth(5);


        mVirgulePaint = new Paint();
        mVirgulePaint.setStyle(Paint.Style.STROKE);
        mVirgulePaint.setStrokeWidth(5);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isSelected()) {
            mLinearGradient = new LinearGradient(0, 0, getWidth(), 0, mStartColor, mEndColorColor, Shader.TileMode.CLAMP);
            mRankPaint.setShader(mLinearGradient);

            mVirgulePaint.setColor(mStartColor);
            canvas.drawLine(0, 0, 0, getHeight(), mVirgulePaint);
            mVirgulePaint.setColor(mEndColorColor);
            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), mVirgulePaint);

        } else {
            mRankPaint.setColor(mDefaultColor);
            mRankPaint.setShader(null);
            mVirgulePaint.setColor(mDefaultColor);

            canvas.drawLine(0, 0, 0, getHeight(), mVirgulePaint);
            canvas.drawLine(getWidth(), 0, getWidth(), getHeight(), mVirgulePaint);

        }

        canvas.drawLine(0, getHeight(), getWidth(), getHeight(), mRankPaint);
        canvas.drawLine(0, 0, getWidth(), 0, mRankPaint);
    }

}
