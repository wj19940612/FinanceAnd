package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;


public class BattleProgress extends LinearLayout {

    private TextView mRightValue;
    private TextView mLeftValue;
    private ProgressBar mLeftProgressBar;
    private ProgressBar mRightProgressBar;

    private int mTextSize;

    public BattleProgress(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BattleProgress);

        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.BattleProgress_android_textSize, 0);

        typedArray.recycle();
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

    }

    private float dp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    private void init() {
        setOrientation(HORIZONTAL);
        mLeftValue = new TextView(getContext());
        mLeftValue.setText(FinanceUtil.formatWithScale(0));
        mLeftValue.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        mRightValue = new TextView(getContext());
        mRightValue.setText(FinanceUtil.formatWithScale(0));
        mRightValue.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        if (mTextSize == 0) {
            mLeftValue.setTextSize(14);
            mRightValue.setTextSize(14);
        } else {
            mLeftValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
            mRightValue.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        }

        int height = (int) dp2Px(8, getResources());
        int width = (int) dp2Px(80, getResources());
        mLeftProgressBar = createProgressBar();
        mRightProgressBar = createProgressBar();
        mRightProgressBar.setRotation(180);

        LinearLayout leftPart = new LinearLayout(getContext());
        leftPart.setOrientation(LinearLayout.VERTICAL);
        leftPart.addView(mLeftValue, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        int marginTop = (int) dp2Px(4, getResources());
        LayoutParams params = new LayoutParams(width, height);
        params.setMargins(0, marginTop, 0, 0);
        leftPart.addView(mLeftProgressBar, params);

        addView(leftPart);

        LinearLayout rightPart = new LinearLayout(getContext());
        rightPart.setOrientation(VERTICAL);
        mRightValue.setGravity(Gravity.RIGHT);
        rightPart.addView(mRightValue, LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);

        params = new LayoutParams(width, height);
        params.setMargins(0, marginTop, 0, 0);
        rightPart.addView(mRightProgressBar, params);

        int marginLeft = (int) dp2Px(20, getResources());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(marginLeft, 0, 0, 0);
        addView(rightPart, params);
    }

    private ProgressBar createProgressBar() {
        ProgressBar progressBar = new ProgressBar(getContext(), null, android.R.attr.progressBarStyleHorizontal);
        progressBar.setMax(100);
        progressBar.setProgress(0);
        progressBar.setProgressDrawable(ContextCompat.getDrawable(getContext(), R.drawable.progress_battle));
        return progressBar;
    }

    public void setBattleProfit(double ownerProfit, double challengerProfit) {
        setProgressValue(ownerProfit, challengerProfit);

        if (ownerProfit == 0 && challengerProfit == 0) {
            // 00
            setProgressBars(0, 0);
            return;
        }

        if (ownerProfit >= 0 && challengerProfit >= 0) {
            // ++
            int progress = (int) (ownerProfit * 100 / (ownerProfit + challengerProfit));
            setProgressBars(progress, 100 - progress);
            return;
        }

        if (ownerProfit < 0 && challengerProfit < 0) {
            // --
            ownerProfit = Math.abs(ownerProfit);
            challengerProfit = Math.abs(challengerProfit);
            int progress = (int) (ownerProfit * 100 / (ownerProfit + challengerProfit));
            setProgressBars(100 - progress, progress);
            return;
        }

        if (ownerProfit >= 0 && challengerProfit < 0) {
            // +-
            setProgressBars(100, 0);
            return;
        }

        if (ownerProfit < 0 && challengerProfit >= 0) {
            // -+
            setProgressBars(0, 100);
            return;
        }
    }

    private void setProgressValue(double ownerProfit, double challengerProfit) {
        String ownerProfitValue = FinanceUtil.formatWithScale(ownerProfit);
        String challengerProfitValue = FinanceUtil.formatWithScale(challengerProfit);
        if (ownerProfit > 0) {
            ownerProfitValue = "+" + ownerProfitValue;
        }
        if (challengerProfit > 0) {
            challengerProfitValue = "+" + challengerProfitValue;
        }
        mLeftValue.setText(ownerProfitValue);
        mRightValue.setText(challengerProfitValue);
    }

    private void setProgressBars(int left, int right) {
        mLeftProgressBar.setProgress(left);
        mRightProgressBar.setProgress(right);
    }

    public void setRightText(CharSequence rightText) {
    }

    public void setRightText(int resId) {
    }

    public void setLeftText(CharSequence leftText) {
    }

    public void setLeftText(int resId) {
    }

    public void setRightTextColor(ColorStateList rightTextColor) {
    }

    public void setLeftTextColor(ColorStateList leftTextColor) {
    }

    public void setMax(int max) {
    }

    public void setProgress(int progress) {
    }

    public void setSecondaryProgress(int secondaryProgress) {
    }
}
