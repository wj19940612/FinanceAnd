package com.sbai.finance.view;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

public class BottomTabs extends LinearLayout {

    private static final int KEY_POSITION = -2;

    private int mLength;
    private int[] mIcons;
    private int[] mTexts;

    private int mTextSize;
    private ColorStateList mTextColor;
    private int mInterval;

    private OnTabClickListener mOnTabClickListener;

    public static final int TAB_INDEX_LIVE = 1;

    public interface OnTabClickListener {
        void onTabClick(int position);
    }

    public BottomTabs(Context context, AttributeSet attrs) {
        super(context, attrs);

        processAttrs(attrs);

        init();
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.BottomTabs);

        mTextSize = typedArray.getDimensionPixelOffset(R.styleable.BottomTabs_textSize, 14);
        mTextColor = typedArray.getColorStateList(R.styleable.BottomTabs_textColor);
        mInterval = typedArray.getDimensionPixelOffset(R.styleable.BottomTabs_interval, 0);

        typedArray.recycle();
    }

    private void init() {
        mIcons = new int[]{R.drawable.tab_diamond, R.drawable.tab_economic_circle, R.drawable.tab_mine};
        mTexts = new int[]{R.string.app_name, R.string.economic_circle, R.string.mine};

        setOrientation(HORIZONTAL);
        if (mIcons != null && mTexts != null) {
            mLength = Math.min(mIcons.length, mTexts.length);
        }
        for (int i = 0; i < mLength; i++) {
            LayoutParams params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            addView(createTab(i), params);
        }
        selectTab(0);
    }

    private View createTab(int i) {
        TextView text = new TextView(getContext());
        text.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        text.setTextColor(mTextColor != null ? mTextColor : ColorStateList.valueOf(0));
        text.setText(mTexts[i]);
        text.setCompoundDrawablePadding(mInterval);
        text.setCompoundDrawablesWithIntrinsicBounds(0, mIcons[i], 0, 0);
        text.setGravity(Gravity.CENTER);
        text.setTag(KEY_POSITION, i);
        text.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                int pos = (int) view.getTag(KEY_POSITION);
                if (mOnTabClickListener != null) {
                    mOnTabClickListener.onTabClick(pos);
                }
            }
        });
        return text;
    }

    public void selectTab(int index) {
        if (index < 0 || index >= mLength) return;
        unSelectAll();
        getChildAt(index).setSelected(true);
    }

    public void setOnTabClickListener(OnTabClickListener listener) {
        mOnTabClickListener = listener;
    }

    private void unSelectAll() {
        for (int i = 0; i < getChildCount(); i++) {
            getChildAt(i).setSelected(false);
        }
    }
}
