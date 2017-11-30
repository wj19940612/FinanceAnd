package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.Display;

public class IconTextRow extends LinearLayout {

    private static final float HEIGHT_SPLIT_LINE_DP = 0.5f;

    private Drawable mLeftIcon;
    private Drawable mRightIcon;
    private Drawable mSubTextViewBg;
    private int mRightIconVisibility;

    private CharSequence mText;
    private float mTextSize;
    private ColorStateList mTextColor;

    private CharSequence mRowSubText;
    private float mRowSubTextSize;
    private ColorStateList mRowSubTextColor;

    private CharSequence mSubText;
    private float mSubTextSize;
    private ColorStateList mSubTextColor;
    private Drawable mSubTextLeftDrawable;
    private Drawable mSubTextRightDrawable;
    private int mSubTextRightMargin;
    private int mSubTextVisible;
    private boolean mSubTextSingleLine;
    private int mSubTextLeftMargin;

    private int mVerticalPadding;
    private int mHorizontalPadding;
    private int mRowTextSpaceExtra;
    private boolean mHasBottomSplitLine;
    private ColorStateList mSplitLineColor;
    private int mSplitLineLeftPadding;

    private TextView mTextView;
    private TextView mRowSubTextView;
    private TextView mSubTextView;
    private ImageView mRightImage;

    private Paint mPaint;
    private float mSplitLineHeight;

    public IconTextRow(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.IconTextRow);

        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 14,
                getResources().getDisplayMetrics());
        int defaultPadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14,
                getResources().getDisplayMetrics());

        mLeftIcon = typedArray.getDrawable(R.styleable.IconTextRow_leftIcon);
        mRightIcon = typedArray.getDrawable(R.styleable.IconTextRow_rightIcon);
        mRightIconVisibility = typedArray.getInt(R.styleable.IconTextRow_rightIconVisibility, 0);
        mText = typedArray.getText(R.styleable.IconTextRow_rowText);
        mTextSize = typedArray.getDimension(R.styleable.IconTextRow_rowTextSize, defaultFontSize);
        mTextColor = typedArray.getColorStateList(R.styleable.IconTextRow_rowTextColor);
        mRowSubText = typedArray.getText(R.styleable.IconTextRow_rowSubText);
        mRowSubTextSize = typedArray.getDimension(R.styleable.IconTextRow_rowSubTextSize, defaultFontSize);
        mRowSubTextColor = typedArray.getColorStateList(R.styleable.IconTextRow_rowSubTextColor);
        mRowTextSpaceExtra = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_rowTextSpaceExtra, 0);
        mSubText = typedArray.getText(R.styleable.IconTextRow_subText);
        mSubTextSize = typedArray.getDimension(R.styleable.IconTextRow_subTextSize, defaultFontSize);
        mSubTextColor = typedArray.getColorStateList(R.styleable.IconTextRow_subTextColor);
        mSubTextViewBg = typedArray.getDrawable(R.styleable.IconTextRow_subTextBackground);
        mSubTextLeftDrawable = typedArray.getDrawable(R.styleable.IconTextRow_subTextLeftDrawable);
        mSubTextRightDrawable = typedArray.getDrawable(R.styleable.IconTextRow_subTextRightDrawable);
        mSubTextRightMargin = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_subTextRightMargin, defaultPadding);
        mSubTextVisible = typedArray.getInt(R.styleable.IconTextRow_subTextVisible, 0);
        mSubTextSingleLine = typedArray.getBoolean(R.styleable.IconTextRow_subTextSingleLine, false);
        mSubTextLeftMargin = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_subTextLeftMargin, defaultPadding);
        mVerticalPadding = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_rowVerticalPadding, defaultPadding);
        mHorizontalPadding = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_rowHorizontalPadding, defaultPadding);
        mHasBottomSplitLine = typedArray.getBoolean(R.styleable.IconTextRow_hasBottomSplitLine, false);
        mSplitLineColor = typedArray.getColorStateList(R.styleable.IconTextRow_splitLineColor);
        if (mSplitLineColor == null) {
            mSplitLineColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(), android.R.color.black));
        }
        mSplitLineLeftPadding = typedArray.getDimensionPixelOffset(R.styleable.IconTextRow_splitLineLeftPadding, defaultPadding);
        mSplitLineHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, HEIGHT_SPLIT_LINE_DP,
                getResources().getDisplayMetrics());

        typedArray.recycle();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (mHasBottomSplitLine) {
            mPaint.setColor(mSplitLineColor.getDefaultColor());
            mPaint.setStrokeWidth(mSplitLineHeight);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawLine(mSplitLineLeftPadding, getHeight() - mSplitLineHeight, getWidth(), getHeight() - mSplitLineHeight, mPaint);
        }
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        if (mHasBottomSplitLine) {
            setWillNotDraw(false);
        }

        setOrientation(HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14, getResources().getDisplayMetrics());
        setPadding(mHorizontalPadding, mVerticalPadding, mHorizontalPadding, mVerticalPadding);

        LayoutParams params;
        if (mLeftIcon != null) {
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, padding, 0);
            ImageView leftIcon = new ImageView(getContext());
            leftIcon.setImageDrawable(mLeftIcon);
            addView(leftIcon, params);
        }

        if (mText == null) mText = "";
        if(mSubTextSingleLine){
            params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
        }else{
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        }
        params.weight = 1;
        params.setMargins(0, 0, padding, 0);
        mTextView = new TextView(getContext());
        mTextView.setText(mText);
        mTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mTextView.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(Color.BLACK));
        mRowSubTextView = new TextView(getContext());
        mRowSubTextView.setText(mRowSubText);
        mRowSubTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mRowSubTextSize);
        mRowSubTextView.setTextColor(mRowSubTextColor != null ? mRowSubTextColor : ColorStateList.valueOf(Color.GRAY));
        LinearLayout rowTextParent = new LinearLayout(getContext());
        rowTextParent.setOrientation(VERTICAL);
        rowTextParent.addView(mTextView);
        addView(rowTextParent, params);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        params.setMargins(0, mRowTextSpaceExtra, 0, 0);
        rowTextParent.addView(mRowSubTextView, params);

        if (mRowSubText == null) mRowSubText = "";
        if (TextUtils.isEmpty(mRowSubText)) {
            mRowSubTextView.setVisibility(GONE);
        }



        mSubTextView = new TextView(getContext());
        mSubTextView.setText(mSubText);
        mSubTextView.setGravity(Gravity.CENTER);
        mSubTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mSubTextSize);
        mSubTextView.setTextColor(mSubTextColor != null ? mSubTextColor : ColorStateList.valueOf(Color.GRAY));
        mSubTextView.setVisibility(mSubTextVisible);
        if (mSubTextSingleLine) {
            params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            params.weight = 2f;
            mSubTextView.setGravity(Gravity.CENTER_VERTICAL|Gravity.RIGHT);
            mSubTextView.setSingleLine(mSubTextSingleLine);
            mSubTextView.setEllipsize(TextUtils.TruncateAt.END);
        }else{
            params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.setMargins(mSubTextLeftMargin, 0, mSubTextRightMargin, 0);
        }
        if (mSubTextViewBg != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mSubTextView.setBackground(mSubTextViewBg);
            } else {
                mSubTextView.setBackgroundDrawable(mSubTextViewBg);
            }
        }
        mSubTextView.setCompoundDrawablesWithIntrinsicBounds(mSubTextLeftDrawable, null, mSubTextRightDrawable, null);
        addView(mSubTextView, params);

        if (mRightIcon != null) {
            mRightImage = new ImageView(getContext());
            mRightImage.setImageDrawable(mRightIcon);
            mRightImage.setVisibility(mRightIconVisibility);
            addView(mRightImage);
        }
    }

    public void setRightIconVisible(boolean visible) {
        if (mRightImage != null) {
            mRightImage.setVisibility(visible ? VISIBLE : GONE);
        }
    }

    public void setSubTextViewBg(int resId) {
        mSubTextView.setBackgroundResource(resId);
    }

    public void setText(String text) {
        mTextView.setText(text);
    }

    public void setSubText(CharSequence subText) {
        mSubTextView.setText(subText);
    }

    public void setText(int resid) {
        mTextView.setText(resid);
    }

    public void setSubText(int resid) {
        mSubTextView.setText(resid);
    }

    public void setSubTextColor(int color) {
        mSubTextView.setTextColor(color);
    }

    public void setSubTextVisible(int visible) {
        mSubTextView.setVisibility(visible);
    }

    public String getSubText() {
        return mSubTextView.getText().toString();
    }

    public TextView getSubTextView() {
        return mSubTextView;
    }

    public void setRowSubText(CharSequence rowSubText) {
        mRowSubTextView.setText(rowSubText);
    }

    public void setRowSubText(int resid) {
        mRowSubTextView.setText(resid);
    }

    public void setSubTextSize(float size) {
        mSubTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, Display.sp2Px(size, getResources()));
    }
}
