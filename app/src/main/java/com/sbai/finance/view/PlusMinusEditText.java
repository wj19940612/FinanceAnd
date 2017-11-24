package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;

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

        typedArray.recycle();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_plus_minus_edit, this, true);
        ButterKnife.bind(this);

        setHint(mHint);
    }

    public void setText(CharSequence text) {
        mEditText.setText(text);
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
                break;
            case R.id.plus:
                break;
        }
    }
}
