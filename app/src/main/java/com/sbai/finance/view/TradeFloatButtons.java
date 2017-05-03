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

    private LinearLayout mPublishPoint;
    private LinearLayout mAddOptional;
    private LinearLayout mTrade;

    public interface OnViewClickListener {
        void onPublishPointButtonClick();

        void onAddOptionalButtonClick();

        void onTradeButtonClick();
    }

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
        int padding = (int) dp2Px(4);
        setPadding(0, padding, 0, padding);
        setBackgroundColor(ContextCompat.getColor(getContext(), R.color.redPrimary));

        mPublishPoint = createChildView(R.drawable.ic_futures_btn_opinion, R.string.publish_point);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        int margin = (int) dp2Px(12);
        params.setMargins(0, margin, 0, margin);
        addView(mPublishPoint, params);

        View splitLine = new View(getContext());
        splitLine.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
        int splitLineWidth = (int) dp2Px(0.5f);
        params = new LayoutParams(splitLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(splitLine, params);

        int buttonWidth = (int) dp2Px(90);
        mAddOptional = createChildView(0, R.string.add_optional);
        params = new LayoutParams(buttonWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mAddOptional, params);

        splitLine = new View(getContext());
        splitLine.setBackgroundColor(ContextCompat.getColor(getContext(), android.R.color.white));
        splitLineWidth = (int) dp2Px(0.5f);
        params = new LayoutParams(splitLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(splitLine, params);

        mTrade = createChildView(R.drawable.ic_futures_btn_trade, R.string.trade);
        params = new LayoutParams(buttonWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(mTrade, params);

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

    private LinearLayout createChildView(int leftRes, int textRes) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setGravity(Gravity.CENTER);
        TextView textView = new TextView(getContext());
        textView.setText(textRes);
        if (leftRes != 0) {
            textView.setCompoundDrawablesWithIntrinsicBounds(leftRes, 0, 0, 0);
        }
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        layout.addView(textView);
        return layout;
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
