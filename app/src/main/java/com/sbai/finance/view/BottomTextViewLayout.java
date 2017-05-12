package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

/**
 * Created by ${wangJie} on 2017/5/12.
 * 有两个TextView的LinearLayout
 */

public class BottomTextViewLayout extends LinearLayoutCompat {
    //左边的描述文字
    private CharSequence mHeadLineText;
    //左边的描述文字颜色
    private int mHeadLineTextColor;
    private float mHeadLineTextSize;

    //右边文字 具体属性
    private CharSequence mInfoText;
    private int mInfoTextColor;
    private float mInfoTextSize;
    private String mDefault;
    private AppCompatTextView mHeadLineTextView;
    private AppCompatTextView mInfoTextView;


    public BottomTextViewLayout(Context context) {
        super(context);
    }

    public BottomTextViewLayout(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    public BottomTextViewLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttrs(attrs);
        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
        mHeadLineTextView = new AppCompatTextView(getContext());
        mHeadLineTextView.setMinWidth((int) Display.dp2Px(50f, getResources()));
        mHeadLineTextView.setPadding(padding, (int) Display.dp2Px(25f, getResources()), padding, 0);
        mHeadLineTextView.setText(mHeadLineText);
        mHeadLineTextView.setTextSize(mHeadLineTextSize);
        mHeadLineTextView.setTextColor(mHeadLineTextColor);
        addView(mHeadLineTextView, 0);
        mInfoTextView = new AppCompatTextView(getContext());
        mInfoTextView.setText(mInfoText);
        mInfoTextView.setPadding(padding, (int) Display.dp2Px(25f, getResources()), 0, 0);
        mInfoTextView.setTextColor(mInfoTextColor);
        mInfoTextView.setTextSize(mInfoTextSize);
        addView(mInfoTextView, 1);

    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BottomTextViewLayout);
        mHeadLineText = typedArray.getString(R.styleable.BottomTextViewLayout_headline_text);
        mHeadLineTextColor = typedArray.getColor(R.styleable.BottomTextViewLayout_headline_text_color, ContextCompat.getColor(getContext(), R.color.secondaryText));
        mHeadLineTextSize = typedArray.getDimension(R.styleable.BottomTextViewLayout_headline_text_size, 14);
        mInfoText = typedArray.getString(R.styleable.BottomTextViewLayout_info_text);
        mInfoTextColor = typedArray.getColor(R.styleable.BottomTextViewLayout_info_text_color, ContextCompat.getColor(getContext(), R.color.primaryText));
        mInfoTextSize = typedArray.getDimension(R.styleable.BottomTextViewLayout_info_text_size, 14);
        mDefault = typedArray.getString(R.styleable.BottomTextViewLayout_default_text);
        typedArray.recycle();
    }

    public void setInfoText(CharSequence infoText) {
        mInfoTextView.setText(infoText);
    }

    public void setInfoText(int infoTextResId) {
        mInfoTextView.setText(infoTextResId);
    }
}
