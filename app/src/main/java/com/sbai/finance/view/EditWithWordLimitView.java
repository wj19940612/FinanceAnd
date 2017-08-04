package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

/**
 * Created by houcc on 2017-08-03.
 */

public class EditWithWordLimitView extends LinearLayout {
    private CharSequence mShowHint;
    private CharSequence mWordLimit;
    private ColorStateList mHintColor;
    private ColorStateList mTextColor;
    private ColorStateList mWordWarnColor;
    private ColorStateList mWordTextColor;
    private int mTextSize;
    private int mWordTextSize;

    private EditText mEditText;
    private TextView mWordLimitView;

    public EditWithWordLimitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EditWithWordLimitView);

        int defaultTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15,
                getResources().getDisplayMetrics());
        int defaultWordSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                getResources().getDisplayMetrics());

        mShowHint = typedArray.getText(R.styleable.EditWithWordLimitView_showHint);
        mWordLimit = typedArray.getText(R.styleable.EditWithWordLimitView_wordLimit);
        mHintColor = typedArray.getColorStateList(R.styleable.EditWithWordLimitView_hintColor);
        mTextColor = typedArray.getColorStateList(R.styleable.EditWithWordLimitView_textColor);
        mWordWarnColor = typedArray.getColorStateList(R.styleable.EditWithWordLimitView_wordWarnColor);
        mWordTextColor = typedArray.getColorStateList(R.styleable.EditWithWordLimitView_wordTextColor);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.EditWithWordLimitView_textSize, defaultTextSize);
        mWordTextSize = typedArray.getDimensionPixelOffset(R.styleable.EditWithWordLimitView_wordTextSize, defaultWordSize);
        typedArray.recycle();

    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        if (mEditText == null) {
            mEditText = new EditText(getContext());
            mEditText.setGravity(Gravity.TOP);
            mEditText.setHintTextColor(mHintColor);
            mEditText.setHint(mShowHint);
            mEditText.setTextColor(mTextColor);
            mEditText.setTextSize(mTextSize);
            mEditText.setBackground(getBackground());
        }
        addView(mEditText);
        if (mWordLimitView == null) {
            mWordLimitView = new TextView(getContext());

        }

    }
}
