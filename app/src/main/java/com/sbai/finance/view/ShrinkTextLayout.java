package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

/**
 * Created by ${wangJie} on 2017/11/22.
 * 可收缩显示全部的text
 */

public class ShrinkTextLayout extends LinearLayout {
    private static final String TAG = "ShrinkTextLayout";
    private int mTextSize;
    private int mTextColor;
    private int mTextMaxLines;
    private int mShrinkBtnColor;
    private int mShrinkBtnTextSize;
    private String mShrinkBtnText;
    private String mSpreadTText;
    private boolean isSpreadEd; //是否展开

    private TextView mContentText;
    private TextView mShrinkBtnTextView;

    private CharSequence mContent;

    public ShrinkTextLayout(Context context) {
        this(context, null);
    }

    public ShrinkTextLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ShrinkTextLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        int defaultTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics());
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.ShrinkTextLayout);
        mTextSize = typedArray.getDimensionPixelSize(R.styleable.ShrinkTextLayout_contentTextSize, defaultTextSize);
        mTextColor = typedArray.getColor(R.styleable.ShrinkTextLayout_contentTextColor, ContextCompat.getColor(getContext(), R.color.unluckyText));
        mTextMaxLines = typedArray.getInt(R.styleable.ShrinkTextLayout_textMaxLines, 3);
        mShrinkBtnColor = typedArray.getColor(R.styleable.ShrinkTextLayout_shrinkBtnTextColor, ContextCompat.getColor(getContext(), R.color.radioText));
        mShrinkBtnTextSize = typedArray.getDimensionPixelSize(R.styleable.ShrinkTextLayout_shrinkBtnTextSize, defaultTextSize);
        mShrinkBtnText = typedArray.getString(R.styleable.ShrinkTextLayout_shrinkBtnText);
        mSpreadTText = typedArray.getString(R.styleable.ShrinkTextLayout_spreadBtnText);
        typedArray.recycle();


        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        mContentText = new TextView(getContext());
//        mContentText.setMaxLines(mTextMaxLines);
        mContentText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mContentText.setTextColor(mTextColor);
        mContentText.setEllipsize(TextUtils.TruncateAt.END);
        mContentText.setLineSpacing(3, 1);
        addView(mContentText);

        mShrinkBtnTextView = new TextView(getContext());
        mShrinkBtnTextView.setText(mSpreadTText);
        mShrinkBtnTextView.setTextColor(mShrinkBtnColor);
        mShrinkBtnTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mShrinkBtnTextSize);

        LayoutParams layoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(0, (int) Display.dp2Px(8, getResources()), 0, 0);
        addView(mShrinkBtnTextView, layoutParams);
        mShrinkBtnTextView.setVisibility(GONE);

        mShrinkBtnTextView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isSpreadEd) {
                    mShrinkBtnTextView.setText(mSpreadTText);
                    mContentText.setMaxLines(mTextMaxLines);
                    isSpreadEd = false;
                } else {
                    mContentText.setMaxLines(Integer.MAX_VALUE);
                    mShrinkBtnTextView.setText(mShrinkBtnText);
                    isSpreadEd = true;
                }
            }
        });
    }

    public void setContentText(CharSequence contentText) {
        if (TextUtils.isEmpty(mContent) || !TextUtils.equals(contentText, mContent)) {
            mContent = contentText;
            updateContentText(contentText);
        }
    }

    private void updateContentText(CharSequence contentText) {
        mContentText.setText(contentText);
        mContentText.setMaxLines(Integer.MAX_VALUE);
        if (!isSpreadEd) {
            mContentText.post(new Runnable() {
                @Override
                public void run() {
                    boolean needShow = mContentText.getLineCount() > mTextMaxLines;
                    mShrinkBtnTextView.setVisibility(needShow ? VISIBLE : GONE);
                    //必须在获取行数后才可进行设置最多行数，不然获取不到正确的最大行数
                    mContentText.setMaxLines(mTextMaxLines);
                }
            });
        }
    }
}
