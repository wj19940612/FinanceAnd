package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.PasswordInputFilter;

import java.lang.reflect.Field;


public class PasswordEditText extends LinearLayout {

    private static final float DEFAULT_LINE_HEIGHT = 0.5f;

    private EditText mPassword;
    private ImageView mShowPassword;

    private CharSequence mTextHint;
    private boolean mHasBottomSplitLine;
    private ColorStateList mSplitLineColor;

    private Paint mPaint;
    private float mBottomLineHeight;
    private int mMaxCharNum;

    public PasswordEditText(Context context) {
        super(context);
        init();
    }

    public PasswordEditText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.PasswordEditText);

        mTextHint = typedArray.getText(R.styleable.PasswordEditText_textHint);
        mHasBottomSplitLine = typedArray.getBoolean(R.styleable.PasswordEditText_hasBottomSplitLine, false);
        mSplitLineColor = typedArray.getColorStateList(R.styleable.PasswordEditText_splitLineColor);
        mMaxCharNum = typedArray.getInt(R.styleable.PasswordEditText_maxCharNum, Integer.MAX_VALUE);
        if (mSplitLineColor == null) {
            mSplitLineColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        typedArray.recycle();
    }

    private void init() {
        setWillNotDraw(false);
        setGravity(Gravity.CENTER_VERTICAL);

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mBottomLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                DEFAULT_LINE_HEIGHT,
                getResources().getDisplayMetrics());

        setOrientation(HORIZONTAL);
        mPassword = initPasswordEditText();
        setHint(mTextHint);

        mShowPassword = new ImageView(getContext());
        mShowPassword.setImageResource(R.drawable.btn_show_password);
        mShowPassword.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                togglePasswordVisible();
            }
        });

        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        addView(mPassword, params);
        addView(mShowPassword);
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        mPassword.addTextChangedListener(textWatcher);
    }

    public void removeTextChangedListener(TextWatcher textWatcher) {
        mPassword.removeTextChangedListener(textWatcher);
    }

    public EditText getEditText() {
        return mPassword;
    }

    public String getPassword() {
        return mPassword.getText().toString();
    }

    public void setPassword(String password) {
        mPassword.setText(password);
    }

    public void setHint(CharSequence hint) {
        mPassword.setHint(hint);
    }

    public void setHint(int hintRes) {
        mPassword.setHint(hintRes);
    }

    private void togglePasswordVisible() {
        if (mShowPassword.isSelected()) {
            mShowPassword.setSelected(false);
            mPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());
        } else {
            mShowPassword.setSelected(true);
            mPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        }
        mPassword.postInvalidate();
        CharSequence text = mPassword.getText();
        if (text != null) {
            Spannable spanText = (Spannable) text;
            Selection.setSelection(spanText, text.length());
        }
    }

    private EditText initPasswordEditText() {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setFilters(new InputFilter[]{new PasswordInputFilter(), new InputFilter.LengthFilter(mMaxCharNum)});
        editText.setBackground(null);
        editText.setPadding(0, padding, 0, padding);
        editText.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
        editText.setTextSize(15);
        editText.setMaxLines(1);
        editText.setHintTextColor(ContextCompat.getColor(getContext(), R.color.unluckyText));
        Field f;
        try {
            f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(editText, R.drawable.color_cursor);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return editText;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHasBottomSplitLine) {
            mPaint.setColor(mSplitLineColor.getDefaultColor());
            mPaint.setStrokeWidth(mBottomLineHeight);
            mPaint.setStyle(Paint.Style.STROKE);
            float lineY = getHeight() - mBottomLineHeight;
            canvas.drawLine(0, lineY, getWidth(), lineY, mPaint);
        }
    }
}
