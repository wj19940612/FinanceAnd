package com.sbai.finance.view;

import android.app.Activity;
import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;

public class TitleBar extends RelativeLayout {

    private static final float HEIGHT_SPLIT_LINE_DP = 0.5f;

    private CharSequence mTitle;
    private float mTitleSize;
    private ColorStateList mTitleColor;
    private CharSequence mRightText;
    private float mRightTextSize;
    private ColorStateList mRightTextColor;
    private Drawable mRightImage;
    private Drawable mRightBackground;
    private boolean mRightVisible;
    private boolean mBackFeature;
    private Drawable mBackIcon;

    private TextView mTitleView;
    private TextView mLeftView;
    private LinearLayout mRightViewParent;
    private TextView mRightView;
    private View mCustomView;

    private boolean mHasBottomSplitLine;
    private ColorStateList mSplitLineColor;
    private Paint mPaint;
    private float mSplitLineHeight;

    public TitleBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);
        init();
    }

    private OnBackClickListener mBackClickListener;

    public interface OnBackClickListener {
        void onClick();
    }

    public void setBackClickListener(OnBackClickListener onBackClickListener) {
        mBackClickListener = onBackClickListener;
    }

    public void setOnTitleBarClickListener(OnClickListener listener) {
        setOnClickListener(listener);
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TitleBar);

        int defaultTitleSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 18,
                getResources().getDisplayMetrics());
        int defaultFontSize = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12,
                getResources().getDisplayMetrics());

        mTitle = typedArray.getText(R.styleable.TitleBar_titleText);
        mTitleSize = typedArray.getDimension(R.styleable.TitleBar_titleTextSize, defaultTitleSize);
        mTitleColor = typedArray.getColorStateList(R.styleable.TitleBar_titleTextColor);
        mRightText = typedArray.getText(R.styleable.TitleBar_rightText);
        mRightTextSize = typedArray.getDimension(R.styleable.TitleBar_rightTextSize, defaultFontSize);
        mRightTextColor = typedArray.getColorStateList(R.styleable.TitleBar_rightTextColor);
        mRightImage = typedArray.getDrawable(R.styleable.TitleBar_rightImage);
        mRightBackground = typedArray.getDrawable(R.styleable.TitleBar_rightBackground);
        mRightVisible = typedArray.getBoolean(R.styleable.TitleBar_rightVisible, false);
        mBackFeature = typedArray.getBoolean(R.styleable.TitleBar_backFeature, false);
        mBackIcon = typedArray.getDrawable(R.styleable.TitleBar_backIcon);
        int customViewResId = typedArray.getResourceId(R.styleable.TitleBar_customView, -1);
        if (customViewResId != -1) {
            mCustomView = LayoutInflater.from(getContext()).inflate(customViewResId, null);
        }
        mHasBottomSplitLine = typedArray.getBoolean(R.styleable.TitleBar_hasBottomSplitLine, false);
        mSplitLineColor = typedArray.getColorStateList(R.styleable.TitleBar_splitLineColor);
        if (mSplitLineColor == null) {
            mSplitLineColor = ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.split));
        }
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
            canvas.drawLine(0, getHeight() - mSplitLineHeight, getWidth(), getHeight() - mSplitLineHeight, mPaint);
        }
    }

    private void init() {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setBackgroundResource(android.R.color.white);

        int fixedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                getResources().getDisplayMetrics());
        int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14,
                getResources().getDisplayMetrics());

        // center view
        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, fixedHeight);
        if (mCustomView != null) {
            addView(mCustomView, params);
        } else {
            mTitleView = new TextView(getContext());
            mTitleView.setGravity(Gravity.CENTER);
            addView(mTitleView, params);
        }

        // left view
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, fixedHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        mLeftView = new TextView(getContext());
        mLeftView.setGravity(Gravity.CENTER);
        mLeftView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        addView(mLeftView, params);
        if (mBackFeature) {
            setBackButtonIcon(mBackIcon);
            mLeftView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackClick(view);
                    if (mBackClickListener != null) {
                        mBackClickListener.onClick();
                    }
                }
            });
        }

        // right view
        mRightViewParent = new LinearLayout(getContext());
        mRightViewParent.setGravity(Gravity.CENTER);
        mRightViewParent.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        mRightView = new TextView(getContext());
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mRightViewParent.addView(mRightView, params);
        params = new LayoutParams(LayoutParams.WRAP_CONTENT, fixedHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
        addView(mRightViewParent, params);

        setTitle(mTitle);
        setTitleSize(TypedValue.COMPLEX_UNIT_PX, mTitleSize);
        setTitleColor(mTitleColor);
        setRightText(mRightText);
        setRightTextSize(TypedValue.COMPLEX_UNIT_PX, mRightTextSize);
        setRightTextColor(mRightTextColor);
        setRightImage(mRightImage);
        setRightBackground(mRightBackground);
        setRightVisible(mRightVisible);
    }

    private void onBackClick(View view) {
        if (getContext() instanceof Activity) {
            Activity activity = (Activity) getContext();
            activity.onBackPressed();
        }
    }

    public void setBackButtonIcon(Drawable backIcon) {
        if (backIcon != null) {
            mLeftView.setCompoundDrawablesWithIntrinsicBounds(backIcon, null, null, null);
        } else { // default icon
            mLeftView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tb_back_black, 0, 0, 0);
        }
    }

    public void setTitle(int resid) {
        CharSequence title = getContext().getText(resid);
        setTitle(title);
    }

    public void setTitle(CharSequence title) {
        mTitle = title;
        mTitleView.setText(mTitle);
    }

    public void setTitleSize(int unit, float titleSize) {
        mTitleView.setTextSize(unit, titleSize);
        mTitleSize = mTitleView.getTextSize();
    }

    public void setTitleSize(int titleSize) {
        mTitleView.setTextSize(titleSize);
        mTitleSize = mTitleView.getTextSize();
    }

    public void setRightText(int resid) {
        mRightText = getContext().getText(resid);
        setRightText(mRightText);
    }

    public void setRightText(CharSequence rightText) {
        mRightText = rightText;
        mRightView.setText(rightText);
    }

    public void setLeftText(String text) {
        mLeftView.setText(text);
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void setLeftText(int textResId) {
        mLeftView.setText(textResId);
        mLeftView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
    }

    public void setLeftTextColor(int color) {
        mLeftView.setTextColor(color);
    }

    public void setRightTextSize(int unit, float rightTextSize) {
        mRightView.setTextSize(unit, rightTextSize);
        mRightTextSize = mRightView.getTextSize();
    }

    public void setRightTextSize(float rightTextSize) {
        mRightView.setTextSize(rightTextSize);
        mRightTextSize = mRightView.getTextSize();
    }

    private void setRightBackground(Drawable background) {
        mRightBackground = background;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mRightView.setBackground(mRightBackground);
        } else {
            mRightView.setBackgroundDrawable(mRightBackground);
        }
    }

    public void setRightImage(Drawable rightImage) {
        mRightImage = rightImage;
        mRightView.setCompoundDrawablesWithIntrinsicBounds(mRightImage, null, null, null);
    }

    public void setRightVisible(boolean rightVisible) {
        mRightVisible = rightVisible;
        mRightView.setVisibility(mRightVisible ? VISIBLE : INVISIBLE);
    }

    public void setOnRightViewClickListener(OnClickListener listener) {
        mRightViewParent.setOnClickListener(listener);
    }

    public void setTitleColor(ColorStateList titleColor) {
        mTitleColor = titleColor;
        if (mTitleColor != null) {
            mTitleView.setTextColor(mTitleColor);
        } else {
            mTitleView.setTextColor(ColorStateList.valueOf(Color.parseColor("#222222")));
        }
    }

    public void setRightTextColor(ColorStateList rightTextColor) {
        mRightTextColor = rightTextColor;
        if (mRightTextColor != null) {
            mRightView.setTextColor(mRightTextColor);
        } else {
            mRightView.setTextColor(ColorStateList.valueOf(Color.parseColor("#222222")));
        }
    }

    public void setRightViewEnable(boolean enable) {
        mRightViewParent.setEnabled(enable);
    }

    public CharSequence getTitle() {
        return mTitle;
    }

    public View getCustomView() {
        return mCustomView;
    }
}
