package com.sbai.finance.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

public class TradeFloatButtons extends LinearLayout {

    public static final int HAS_ADD_OPITION =  1;//添加自选 1 已添加  0 未添加

    private LinearLayout mPublishPoint;
    private LinearLayout mAddOptional;
    private LinearLayout mTrade;

    public interface OnViewClickListener {
        void onPublishPointButtonClick();

        void onAddOptionalButtonClick();

        void onTradeButtonClick();
    }

    private boolean mHasAddInOption;
    private boolean mHasTradeButton;
    private OnViewClickListener mOnViewClickListener;

    public TradeFloatButtons(Context context) {
        super(context);
        init();
    }

    public TradeFloatButtons(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        processAttrs(attrs);

        init();
    }

    private void init() {
        setOrientation(HORIZONTAL);
        setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));

        mTrade = createChildView(false, R.string.trade);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        addView(mTrade, params);

        View splitLine = new View(getContext());
        splitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.normalDivider));
        int splitLineWidth = (int) dp2Px(0.5f);
        params = new LayoutParams(splitLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(splitLine, params);

        mAddOptional = createChildView(false, R.string.add_optional);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        addView(mAddOptional, params);

        splitLine = new View(getContext());
        splitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.normalDivider));
        splitLineWidth = (int) dp2Px(0.5f);
        params = new LayoutParams(splitLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(splitLine, params);

        mPublishPoint = createChildView(true, R.string.publish_point);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        addView(mPublishPoint, params);

        setHasTradeButton(mHasTradeButton);

        mPublishPoint.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onPublishPointButtonClick();
                }
            }
        });
        mAddOptional.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onAddOptionalButtonClick();
                }
            }
        });
        mTrade.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnViewClickListener != null) {
                    mOnViewClickListener.onTradeButtonClick();
                }
            }
        });
    }

    public void setOnViewClickListener(OnViewClickListener onViewClickListener) {
        mOnViewClickListener = onViewClickListener;
    }

    public boolean isHasAddInOptional() {
        return mHasAddInOption;
    }

    public void setHasAddInOption(boolean hasAddInOption) {
        mHasAddInOption = hasAddInOption;
        updateOptionalStatus();
    }

    private void updateOptionalStatus() {
        if (mHasAddInOption) {
            ((TextView) mAddOptional.getChildAt(0)).setText(R.string.delete_optional);
        } else {
            ((TextView) mAddOptional.getChildAt(0)).setText(R.string.add_optional);
        }
    }

    public void setHasTradeButton(boolean hasTradeButton) {
        mHasTradeButton = hasTradeButton;

        View splitLine = getChildAt(getChildCount() - 2);
        if (mHasTradeButton) {
            mTrade.setVisibility(VISIBLE);
            splitLine.setVisibility(VISIBLE);
        } else {
            mTrade.setVisibility(GONE);
            splitLine.setVisibility(GONE);
        }
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TradeFloatButtons);

        mHasTradeButton = typedArray.getBoolean(R.styleable.TradeFloatButtons_hasTradeButton, true);

        typedArray.recycle();
    }

    private LinearLayout createChildView(boolean isPublish, int textRes) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setGravity(Gravity.CENTER);
        int padding = (int) dp2Px(16);
        layout.setPadding(0, padding, 0, padding);
        TextView textView = new TextView(getContext());
        textView.setText(textRes);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        if (isPublish) {
            layout.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
            textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        } else {
            layout.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        }
        layout.addView(textView);
        return layout;
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
