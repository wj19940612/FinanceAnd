package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;


public class BattleProgress extends RelativeLayout {
    public static final int ORIENTATION_HORIZONTAL=0;

    private Drawable mProgressDrawable;
    private CharSequence mRightText;
    private CharSequence mLeftText;
    private int mRightSize;
    private int mLeftSize;
    private ColorStateList mRightTextColor;
    private ColorStateList mLeftTextColor;
    private int mRightPadding;
    private int mLeftPadding;
    private int mOrientation;

    private int mMax;
    private int mProgress;
    private int mSecondaryProgress;

    private TextView mRightView;
    private TextView mLeftView;
    private ProgressBar mProgressBar;
    public BattleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BattleProgress);
        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                getResources().getDisplayMetrics());
        int defaultTextPadding =  (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                getResources().getDisplayMetrics());
        mProgressDrawable = typedArray.getDrawable(R.styleable.BattleProgress_progressDrawable);
        mOrientation= typedArray.getInt(R.styleable.BattleProgress_orientation,ORIENTATION_HORIZONTAL);
        mRightPadding = typedArray.getLayoutDimension(R.styleable.BattleProgress_rightPadding,defaultTextPadding);
        mLeftPadding = typedArray.getLayoutDimension(R.styleable.BattleProgress_leftPadding,defaultTextPadding);
        mMax = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_max,100);
        mProgress = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_progress,50);
        mSecondaryProgress = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_secondaryProgress,100);

        mRightSize = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_rightTextSize,defaultFontSize);
        mRightText = typedArray.getText(R.styleable.BattleProgress_rightText);
        mRightTextColor = typedArray.getColorStateList(R.styleable.BattleProgress_rightTextColor);

        mLeftSize = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_leftTextSize,defaultFontSize);
        mLeftText = typedArray.getText(R.styleable.BattleProgress_leftText);
        mLeftTextColor = typedArray.getColorStateList(R.styleable.BattleProgress_leftTextColor);

        if (mRightTextColor==null){
            mRightTextColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.white));
        }
        if (mLeftTextColor == null){
            mLeftTextColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(),R.color.white));
        }

        typedArray.recycle();
    }

    private void init() {
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.CENTER_IN_PARENT,RelativeLayout.TRUE);
        //progressBar
        mProgressBar = new ProgressBar(getContext());
        mProgressBar.setBackground(mProgressDrawable);
        mProgressBar.setMax(mMax);
        mProgressBar.setSecondaryProgress(mSecondaryProgress);
        mProgressBar.setPadding(mLeftPadding,0,mRightPadding,0);
        mProgressBar.setLayoutParams(params);
        addView(mProgressBar,params);
        //rightView
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        mRightView = new TextView(getContext());
        mRightView.setGravity(Gravity.RIGHT);
        mRightView.setText(mRightText);
        mRightView.setTextColor(mRightTextColor);
        mRightView.setTextSize(mRightSize);
        addView(mRightView,params);
        //leftView
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
        mLeftView = new TextView(getContext());
        mLeftView.setGravity(Gravity.LEFT);
        mLeftView.setText(mRightText);
        mLeftView.setTextColor(mRightTextColor);
        mLeftView.setTextSize(mRightSize);
        addView(mLeftView,params);
    }

    public void setRightText(CharSequence rightText) {
        mRightText = rightText;
        mRightView.setText(rightText);
    }
    public void setRightText(int resId) {
        mRightText = getContext().getText(resId);
        setRightText(mRightText);
    }

    public void setLeftText(CharSequence leftText) {
        mLeftText = leftText;
    }
    public void setLeftText(int resId){
        mLeftText = getContext().getText(resId);
        setLeftText(mLeftText);
    }

    public void setRightTextColor(ColorStateList rightTextColor) {
        mRightTextColor = rightTextColor;
        mRightView.setTextColor(rightTextColor);
    }

    public void setLeftTextColor(ColorStateList leftTextColor) {
        mLeftTextColor = leftTextColor;
        mLeftView.setTextColor(leftTextColor);
    }

    public void setMax(int max) {
        mMax = max;
        mProgressBar.setMax(max);
    }

    public void setProgress(int progress) {
        mProgress = progress;
        mProgressBar.setProgress(progress);
    }

    public void setSecondaryProgress(int secondaryProgress) {
        mSecondaryProgress = secondaryProgress;
        mProgressBar.setSecondaryProgress(secondaryProgress);
    }
}
