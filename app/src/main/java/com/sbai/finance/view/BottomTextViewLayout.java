package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.LinearLayout;

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
    private float mHeadLineMinWidth;
    private int mViewGravity;
    private float mHeadLinePaddingRight;
    private float mInfoTextLineSpacingExtra;
    private int padding = 16;


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
        setGravity(mViewGravity);
        mHeadLineTextView = new AppCompatTextView(getContext());
        mHeadLineTextView.setMinWidth((int) Display.dp2Px(mHeadLineMinWidth, getResources()));
        mHeadLineTextView.setMinHeight(50);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, 0, 0, (int) Display.dp2Px(25f, getResources()));
        mHeadLineTextView.setPadding(pxTodp(padding), 0, pxTodp(mHeadLinePaddingRight), 0);
        mHeadLineTextView.setText(mHeadLineText);
        mHeadLineTextView.setTextSize(mHeadLineTextSize);
        mHeadLineTextView.setTextColor(mHeadLineTextColor);
        mHeadLineTextView.setLayoutParams(layoutParams);
        addView(mHeadLineTextView, 0);
        mInfoTextView = new AppCompatTextView(getContext());
        mInfoTextView.setText(mInfoText);
        mInfoTextView.setMinHeight(50);
        mInfoTextView.setPadding(pxTodp(padding), 0, pxTodp(padding), (int) Display.dp2Px(25f, getResources()));
        LinearLayout.LayoutParams infoLayouts = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        infoLayouts.setMargins(0, 0, 0, (int) Display.dp2Px(25f, getResources()));
        layoutParams.setMargins(0, 0, 0, 0);
        mInfoTextView.setLayoutParams(infoLayouts);
        mInfoTextView.setTextColor(mInfoTextColor);
        mInfoTextView.setTextSize(mInfoTextSize);
        mInfoTextView.setLineSpacing(mInfoTextLineSpacingExtra, 1);
        addView(mInfoTextView, 1);

    }

    private int pxTodp(float padding) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, padding, getResources().getDisplayMetrics());
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BottomTextViewLayout);
        mHeadLinePaddingRight = typedArray.getDimension(R.styleable.BottomTextViewLayout_headline_text_padding_right, 16);
        mHeadLineText = typedArray.getString(R.styleable.BottomTextViewLayout_headline_text);
        mHeadLineTextColor = typedArray.getColor(R.styleable.BottomTextViewLayout_headline_text_color, ContextCompat.getColor(getContext(), R.color.secondaryText));
        mHeadLineTextSize = typedArray.getDimension(R.styleable.BottomTextViewLayout_headline_text_size, 14);
        mHeadLineMinWidth = typedArray.getDimension(R.styleable.BottomTextViewLayout_headline_text_minWidth, 150f);
        mInfoText = typedArray.getString(R.styleable.BottomTextViewLayout_info_text);
        mInfoTextColor = typedArray.getColor(R.styleable.BottomTextViewLayout_info_text_color, ContextCompat.getColor(getContext(), R.color.primaryText));
        mInfoTextSize = typedArray.getDimension(R.styleable.BottomTextViewLayout_info_text_size, 14);
        mDefault = typedArray.getString(R.styleable.BottomTextViewLayout_default_text);
        mInfoTextLineSpacingExtra = typedArray.getDimension(R.styleable.BottomTextViewLayout_info_text_lineSpacingExtra, 0);
        typedArray.recycle();
    }

    public void setInfoText(CharSequence infoText) {
        mInfoTextView.setText(infoText);
    }

    public void setInfoText(int infoTextResId) {
        mInfoTextView.setText(infoTextResId);
    }
}
