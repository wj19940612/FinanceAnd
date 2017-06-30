package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
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
import com.sbai.finance.utils.FinanceUtil;


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
        mMax = typedArray.getInt(R.styleable.BattleProgress_max,100);
        mProgress = typedArray.getInt(R.styleable.BattleProgress_progress,50);
        mSecondaryProgress = typedArray.getInt(R.styleable.BattleProgress_secondaryProgress,100);

        mRightSize = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_rightLabelSize,defaultFontSize);
        mRightText = typedArray.getText(R.styleable.BattleProgress_rightLabel);
        mRightTextColor = typedArray.getColorStateList(R.styleable.BattleProgress_rightLabelColor);

        mLeftSize = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_leftLabelSize,defaultFontSize);
        mLeftText = typedArray.getText(R.styleable.BattleProgress_leftLabel);
        mLeftTextColor = typedArray.getColorStateList(R.styleable.BattleProgress_leftLabelColor);

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
        mProgressBar = new ProgressBar(getContext(),null,android.R.attr.progressBarStyleHorizontal);
        mProgressBar.setProgressDrawable(mProgressDrawable);

        mProgressBar.setMax(mMax);
        mProgressBar.setProgress(mProgress);
        mProgressBar.setSecondaryProgress(mSecondaryProgress);
        addView(mProgressBar,params);
        //rightView
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,RelativeLayout.TRUE);
        mRightView = new TextView(getContext());
        mRightView.setGravity(Gravity.RIGHT);
        mRightView.setText(mRightText);
        mRightView.setTextColor(mRightTextColor);
        mRightView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mRightSize);
        mRightView.setPadding(0,0,mRightPadding,0);
        addView(mRightView,params);
        //leftView
        params = new LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.MATCH_PARENT);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT,RelativeLayout.TRUE);
        mLeftView = new TextView(getContext());
        mLeftView.setGravity(Gravity.LEFT);
        mLeftView.setText(mLeftText);
        mLeftView.setTextColor(mLeftTextColor);
        mLeftView.setTextSize(TypedValue.COMPLEX_UNIT_PX,mLeftSize);
        mLeftView.setPadding(mLeftPadding,0,0,0);
        addView(mLeftView,params);
    }
    public void showScoreProgress(double createProfit, double fighterProfit, boolean isInviting) {
        String myFlag = "";
        String fighterFlag = "";
        if (isInviting) {
            setProgress(0);
            setSecondaryProgress(0);
            setLeftText(null);
            setRightText(null);
        } else {
            //正正
            if ((createProfit > 0 && fighterProfit >= 0) || (createProfit >= 0 && fighterProfit > 0)) {
                int progress = (int) (createProfit * 100 / (createProfit + fighterProfit));
                setProgress(progress);
            }
            //正负
            if (createProfit >= 0 && fighterProfit < 0) {
                setProgress(100);
            }
            //负正
            if (createProfit < 0 && fighterProfit >= 0) {
                setProgress(0);
            }
            //负负
            if (createProfit < 0 && fighterProfit < 0) {
                int progress = (int) (Math.abs(createProfit) * 100 / (Math.abs(createProfit) + Math.abs(fighterProfit)));
                setProgress(100 - progress);
            }
            //都为0
            if (createProfit == 0 && fighterProfit == 0) {
                setProgress(50);
            }
            setSecondaryProgress(100);
            if (createProfit > 0) {
                myFlag = "+";
            }

            if (fighterProfit > 0) {
                fighterFlag = "+";
            }
            setLeftText(myFlag + FinanceUtil.formatWithScale(createProfit));
            setRightText(fighterFlag + FinanceUtil.formatWithScale(fighterProfit));
        }
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
        mLeftView.setText(leftText);
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
