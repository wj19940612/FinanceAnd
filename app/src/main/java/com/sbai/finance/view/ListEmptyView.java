package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

/**
 * Created by ${wangJie} on 2017/9/25.
 * 我的提问 消息等页面的空白viewl
 */

public class ListEmptyView extends LinearLayout {

    private Drawable mTitleImage;

    private String mContentText;
    private int mContentSize;
    private int mContentColor;

    private String mHintText;
    private int mHintTextColor;
    private float mHintTextSize;
    private String mGoingText;
    private int mGoingTextColor;
    private float mGoingTextSize;
    private Drawable mGoingBg;
    private TextView mContentTextView;
    private TextView mHintTextView;
    private TextView mGoingTextView;

    private OnGoingViewClickListener mOnGoingViewClickListener;

    public interface OnGoingViewClickListener {
        void onGoingViewClick();
    }

    public ListEmptyView(Context context) {
        this(context, null);
    }

    public ListEmptyView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ListEmptyView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        processAttributeSet(attrs);
        init();
    }

    private void processAttributeSet(@Nullable AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ListEmptyView);
        mTitleImage = typedArray.getDrawable(R.styleable.ListEmptyView_titleImage);
        mContentText = typedArray.getString(R.styleable.ListEmptyView_contentMessage);
        mContentSize = typedArray.getDimensionPixelSize(R.styleable.ListEmptyView_contentMessageSize, 16);
        mContentColor = typedArray.getColor(R.styleable.ListEmptyView_contentMessageColor, ContextCompat.getColor(getContext(), R.color.blackPrimary));
        mHintText = typedArray.getString(R.styleable.ListEmptyView_contentHint);
        mHintTextColor = typedArray.getColor(R.styleable.ListEmptyView_contentHintColor, ContextCompat.getColor(getContext(), R.color.unluckyText));
        mHintTextSize = typedArray.getDimension(R.styleable.ListEmptyView_contentHintSize, 12);
        mGoingText = typedArray.getString(R.styleable.ListEmptyView_goingText);
        mGoingTextColor = typedArray.getColor(R.styleable.ListEmptyView_goingTextColor, Color.WHITE);
        mGoingTextSize = typedArray.getDimension(R.styleable.ListEmptyView_goingTextSize, 15);
        mGoingBg = typedArray.getDrawable(R.styleable.ListEmptyView_goingBg);
        typedArray.recycle();
    }

    private void init() {
        setGravity(Gravity.CENTER);
        setOrientation(VERTICAL);
        int defaultMargin = (int) Display.dp2Px(14, getResources());
        LayoutParams layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        createContentTextView();
        addView(mContentTextView, layoutParams);

        createHintTextView();

        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(defaultMargin, (int) Display.dp2Px(4, getResources()), defaultMargin, defaultMargin);
        addView(mHintTextView, layoutParams);
        createGoingTextView();
        layoutParams = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        addView(mGoingTextView, layoutParams);
    }

    private void createGoingTextView() {
        mGoingTextView = new TextView(getContext());
        mGoingTextView.setGravity(Gravity.CENTER);
        mGoingTextView.setText(mGoingText);
        mGoingTextView.setTextSize(mGoingTextSize);
        mGoingTextView.setTextColor(mGoingTextColor);
        mGoingTextView.setMinWidth((int) Display.dp2Px(130, getResources()));
        mGoingTextView.setMinHeight((int) Display.dp2Px(36, getResources()));
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mGoingTextView.setBackground(mGoingBg);
        } else {
            mGoingTextView.setBackgroundDrawable(mGoingBg);
        }
        mGoingTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnGoingViewClickListener != null) {
                    mOnGoingViewClickListener.onGoingViewClick();
                }
            }
        });
    }

    private void createHintTextView() {
        mHintTextView = new TextView(getContext());
        mHintTextView.setText(mHintText);
        mHintTextView.setTextSize(mHintTextSize);
        mHintTextView.setGravity(Gravity.CENTER);
        mHintTextView.setTextColor(mHintTextColor);
    }

    private void createContentTextView() {
        mContentTextView = new TextView(getContext());
        mContentTextView.setCompoundDrawablesWithIntrinsicBounds(null, mTitleImage, null, null);
        mContentTextView.setText(mContentText);
        mContentTextView.setGravity(Gravity.CENTER);
        mContentTextView.setTextColor(mContentColor);
        mContentTextView.setTextSize(mContentSize);
    }

    public void setContentText(int resId) {
        mContentTextView.setText(resId);
    }

    public void setContentText(String contentText) {
        mContentTextView.setText(contentText);
    }

    public void setHintText(int resId) {
        mHintTextView.setText(resId);
    }

    public void setHintText(String hintText) {
        mHintTextView.setText(hintText);
    }

    public void setGoingText(int resId) {
        mGoingTextView.setText(resId);
    }

    public void setGoingText(String goingText) {
        mGoingTextView.setText(goingText);
    }

    public void setGoingTextViewVisable(int viewVisable) {
        mGoingTextView.setVisibility(viewVisable);
    }

    public void setOnGoingViewClickListener(OnGoingViewClickListener onGoingViewClickListener) {
        this.mOnGoingViewClickListener = onGoingViewClickListener;
    }

    public void setTitleImage(int resId) {
        mContentTextView.setCompoundDrawablesWithIntrinsicBounds(0, resId, 0, 0);
    }

    public void setTitleImage(Drawable drawable) {
        mContentTextView.setCompoundDrawablesWithIntrinsicBounds(null, drawable, null, null);
    }

    public void setGoingBg(int resId) {
        mGoingTextView.setBackgroundResource(resId);
    }

    public void setGoingBtnGone(){
        mGoingTextView.setVisibility(View.GONE);
    }
}
