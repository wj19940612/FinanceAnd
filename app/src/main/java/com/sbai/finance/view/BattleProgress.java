package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;


public class BattleProgress extends RelativeLayout {
    private Drawable mProgressDrawable;
    private CharSequence mRightText;
    private CharSequence mLeftText;
    private int mRightSize;
    private int mLeftSize;
    private ColorStateList mRightTextColor;
    private ColorStateList mLeftTextColor;
    private float mRightPaddding;
    private float mLeftPadding;
    private int mOrientation;

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
        mProgressDrawable = typedArray.getDrawable(R.styleable.BattleProgress_progressDrawable);
//        mOrientation= typedArray.get
    }

    private void init() {
    }
}
