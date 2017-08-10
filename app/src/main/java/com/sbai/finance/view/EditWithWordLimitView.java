package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableString;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ValidationWatcher;

public class EditWithWordLimitView extends LinearLayout {
    private CharSequence mShowHint;
    private int mHintColor;
    private int mTextColor;
    private int mWordWarnColor;
    private int mWordTextColor;
    private int mTextSize;
    private int mWordTextSize;
    private int mWordLimit;
    private boolean mFocusable;

    private EditText mEditText;
    private TextView mWordLimitView;
    private OnTextChangeCallback mOnTextChangeCallback;

    public EditWithWordLimitView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    public interface OnTextChangeCallback {
        void afterText(String text);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (mOnTextChangeCallback != null) {
                mOnTextChangeCallback.afterText(s.toString());
            }
            setHintText(s.toString().length());
        }
    };

    public void setOnTextChangeCallback(OnTextChangeCallback onTextChangeCallback) {
        mOnTextChangeCallback = onTextChangeCallback;
        mEditText.addTextChangedListener(mValidationWatcher);
    }

    public void removeTextChangeCallback() {
        mOnTextChangeCallback = null;
        mEditText.removeTextChangedListener(mValidationWatcher);
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.EditWithWordLimitView);

        int defaultTextSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 15,
                getResources().getDisplayMetrics());
        int defaultWordSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10,
                getResources().getDisplayMetrics());

        mShowHint = typedArray.getText(R.styleable.EditWithWordLimitView_showHint);
        mWordLimit = typedArray.getInt(R.styleable.EditWithWordLimitView_wordLimit, 0);
        mHintColor = typedArray.getColor(R.styleable.EditWithWordLimitView_hintColor, Color.GRAY);
        mTextColor = typedArray.getColor(R.styleable.EditWithWordLimitView_InputTextColor, Color.BLACK);
        mWordWarnColor = typedArray.getColor(R.styleable.EditWithWordLimitView_wordWarnColor, Color.RED);
        mWordTextColor = typedArray.getColor(R.styleable.EditWithWordLimitView_wordTextColor, Color.GRAY);
        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.EditWithWordLimitView_InputTextSize, defaultTextSize);
        mWordTextSize = typedArray.getDimensionPixelOffset(R.styleable.EditWithWordLimitView_wordTextSize, defaultWordSize);
        mFocusable = typedArray.getBoolean(R.styleable.EditWithWordLimitView_focusable, true);
        typedArray.recycle();

    }

    private void init() {
        setOrientation(VERTICAL);
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, 0, 1);
        if (mEditText == null) {
            mEditText = new EditText(getContext());
        }
        mEditText.setGravity(Gravity.TOP);
        mEditText.setHintTextColor(mHintColor);
        mEditText.setHint(mShowHint);
        mEditText.setTextColor(mTextColor);
        mEditText.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mEditText.setBackgroundColor(Color.TRANSPARENT);
        addView(mEditText, params);
        params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        if (mWordLimitView == null) {
            mWordLimitView = new TextView(getContext());
        }
        mWordLimitView.setGravity(Gravity.RIGHT);
        mWordLimitView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mWordTextSize);
        mWordLimitView.setTextColor(mWordTextColor);
        mWordLimitView.setPadding(0, (int) Display.dp2Px(1, getResources()), 0, 0);
        mWordLimitView.setBackgroundColor(Color.TRANSPARENT);
        mWordLimitView.setLayoutParams(params);
        addView(mWordLimitView);
        setHintText(0);
    }

    private void setHintText(int count) {
        if (count <= mWordLimit) {
            mWordLimitView.setText(StrUtil.mergeTextWithColor(String.valueOf(count), mWordTextColor, "/" + mWordLimit));
        } else {
            mWordLimitView.setText(StrUtil.mergeTextWithColor(String.valueOf(count), mWordWarnColor, "/" + mWordLimit));
        }
    }

    public int getWordLimitCount() {
        return mWordLimit;
    }

    public String getInputComment() {
        return mEditText.getText().toString();
    }

    public void clearFocus() {
        mEditText.clearFocus();
    }
}
