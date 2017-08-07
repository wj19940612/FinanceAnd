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
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.PasswordInputFilter;

import java.lang.reflect.Field;


public class PasswordEditText extends LinearLayout {

    private EditText mPassword;
    private ImageView mShowPassword;

    private CharSequence mTextHint;
    private boolean mHasBottomLine;
    private ColorStateList mBottomLineColor;

    private Paint mPaint;
    private float mBottomLineHeight;

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
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconTextRow);

        mTextHint = typedArray.getText(R.styleable.PasswordEditText_textHint);
        mHasBottomLine = typedArray.getBoolean(R.styleable.PasswordEditText_hasBottomLine, false);
        mBottomLineColor = typedArray.getColorStateList(R.styleable.PasswordEditText_bottomLineColor);
        if (mBottomLineColor == null) {
            mBottomLineColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.black));
        }

        typedArray.recycle();
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBottomLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 0.5f,
                getResources().getDisplayMetrics());

        setOrientation(HORIZONTAL);
        mPassword = initPasswordEditText();
        mPassword.setHint(mTextHint);
        mPassword.setText("SSS");

        Log.d("TAG", "init: " + mTextHint);
        
        mShowPassword = new ImageView(getContext());
        mShowPassword.setImageResource(R.drawable.btn_show_password);

        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.MATCH_PARENT);
        params.weight = 1;
        addView(mPassword, params);
        addView(mShowPassword);
    }

    private EditText initPasswordEditText() {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 12,
                getResources().getDisplayMetrics());
        EditText editText = new EditText(getContext());
        editText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        editText.setFilters(new InputFilter[]{new PasswordInputFilter()});
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
        if (mHasBottomLine) {
            mPaint.setColor(mBottomLineColor.getDefaultColor());
            mPaint.setStrokeWidth(mBottomLineHeight);
            mPaint.setStyle(Paint.Style.STROKE);
            float lineY = getHeight() - mBottomLineHeight;
            canvas.drawLine(getPaddingLeft(), lineY, getWidth() - getPaddingRight(), lineY, mPaint);
            Log.d("TAG", "onDraw: ");
        }
    }
}
