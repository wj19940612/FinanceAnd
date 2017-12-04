package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.ValidityCheckUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Modified by john on 22/11/2017
 * <p>
 * Description:
 * <p>
 * APIs:
 */
public class PlusMinusEditText extends FrameLayout {

    @BindView(R.id.minus)
    TextView mMinus;
    @BindView(R.id.editText)
    EditText mEditText;
    @BindView(R.id.plus)
    TextView mPlus;

    private float mChangeInterval;
    private String mHint;
    private int mInputType;
    private int mImeOptions;
    private int mMaxLength;

    public PlusMinusEditText(Context context) {
        super(context);
        init();
    }

    public PlusMinusEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PlusMinusEditText);

        mChangeInterval = typedArray.getFloat(R.styleable.PlusMinusEditText_changeInterval, 0);
        mHint = typedArray.getString(R.styleable.PlusMinusEditText_android_hint);
        mInputType = typedArray.getInt(R.styleable.PlusMinusEditText_inputType, 0);
        mImeOptions = typedArray.getInt(R.styleable.PlusMinusEditText_android_imeOptions, 0);
        mMaxLength = typedArray.getInt(R.styleable.PlusMinusEditText_android_maxLength, 15);

        typedArray.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_plus_minus_edit, this, true);
        ButterKnife.bind(this);

        setHint(mHint);
        setInputType(mInputType);
        setImeOptions(mImeOptions);
        setMaxLength(mMaxLength);
    }

    private void setMaxLength(int maxLength) {
        mEditText.setFilters(new InputFilter[] {new InputFilter.LengthFilter(maxLength)});
    }

    public void setImeOptions(int imeOptions) {
        mImeOptions = imeOptions;
        mEditText.setImeOptions(mImeOptions);
    }

    private void setInputType(int inputType) {
        if (inputType == 1) {
            mEditText.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }

    public void addTextChangedListener(TextWatcher watcher) {
        mEditText.addTextChangedListener(watcher);
    }

    public void removeTextChangedListener(TextWatcher watcher) {
        mEditText.removeTextChangedListener(watcher);
    }

    public void setText(CharSequence text) {
        if (text == null || ValidityCheckUtil.isNumber(text)) {
            mEditText.setText(text);
        }
    }

    public String getText() {
        return mEditText.getText().toString();
    }

    public void setHint(String hint) {
        mEditText.setHint(hint);
    }

    public void setHint(CharSequence hint) {
        mEditText.setHint(hint);
    }

    public void setHint(int hintRes) {
        CharSequence hint = getContext().getText(hintRes);
        setHint(hint);
    }

    @OnClick({R.id.minus, R.id.plus})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.minus:
                minus();
                break;
            case R.id.plus:
                plus();
                break;
        }
    }

    private void plus() {
        String text = mEditText.getText().toString();
        double value = 0;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        value += mChangeInterval;
        mEditText.setText(FinanceUtil.formatWithScale(value, mInputType == 0 ? 2 : 0));
        mEditText.setSelection(mEditText.getText().toString().length());
    }

    private void minus() {
        String text = mEditText.getText().toString();
        double value = 0;
        try {
            value = Double.parseDouble(text);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        value -= mChangeInterval;
        value = Math.max(0, value);
        mEditText.setText(FinanceUtil.formatWithScale(value, mInputType == 0 ? 2 : 0));
        mEditText.setSelection(mEditText.getText().toString().length());
    }
}
