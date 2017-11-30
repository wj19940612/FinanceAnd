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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

public class TradeFloatButtons extends LinearLayout {

    public static final int HAS_ADD_OPITION = 1;//添加自选 1 已添加  0 未添加

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

        mTrade = createChildView(false, R.string.trade, false);
        LayoutParams params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        addView(mTrade, params);

        View splitLine = new View(getContext());
        splitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.split));
        int splitLineWidth = (int) dp2Px(0.5f);
        params = new LayoutParams(splitLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(splitLine, params);

        mAddOptional = createChildView(false, R.string.add_optional, true);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.weight = 1f;
        addView(mAddOptional, params);
        mAddOptional.setVisibility(GONE);

        splitLine = new View(getContext());
        splitLine.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.split));
        splitLineWidth = (int) dp2Px(0.5f);
        params = new LayoutParams(splitLineWidth, ViewGroup.LayoutParams.MATCH_PARENT);
        addView(splitLine, params);

        mPublishPoint = createChildView(true, R.string.publish_point, false);
        params = new LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT);
        //发表观点不能直接去掉
        mPublishPoint.setVisibility(GONE);
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
            mAddOptional.getChildAt(0).setVisibility(GONE);
            ((TextView) mAddOptional.getChildAt(1)).setText(R.string.cancel_optional);
        } else {
            mAddOptional.getChildAt(0).setVisibility(GONE);
            ((TextView) mAddOptional.getChildAt(1)).setText(R.string.add_optional);
        }
    }

    public void setHasTradeButton(boolean hasTradeButton) {
        mHasTradeButton = hasTradeButton;

        View splitLine = getChildAt(getChildCount() - 2);
        if (mHasTradeButton) {
            setVisibility(VISIBLE);
            mTrade.setVisibility(VISIBLE);
            splitLine.setVisibility(VISIBLE);
        } else {
            setVisibility(GONE);
            mTrade.setVisibility(GONE);
            splitLine.setVisibility(GONE);
        }
    }

    private void processAttrs(AttributeSet attrs) {
        TypedArray typedArray = getContext().obtainStyledAttributes(attrs, R.styleable.TradeFloatButtons);

        mHasTradeButton = typedArray.getBoolean(R.styleable.TradeFloatButtons_hasTradeButton, true);

        typedArray.recycle();
    }

    private LinearLayout createChildView(boolean isPublish, int textRes, boolean hasLeftIcon) {
        LinearLayout layout = new LinearLayout(getContext());
        layout.setGravity(Gravity.CENTER);
        int padding = (int) dp2Px(16);
        layout.setPadding(0, padding, 0, padding);
        if (hasLeftIcon) {
            ImageView imageView = new ImageView(getContext());
            imageView.setImageResource(R.drawable.ic_add_optional);
            layout.addView(imageView);
        }
        TextView textView = new TextView(getContext());
        textView.setText(textRes);
        textView.setGravity(Gravity.CENTER);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15);
        if (isPublish) {
            layout.setBackgroundResource(R.drawable.btn_trade_red_primary);
            textView.setTextColor(ContextCompat.getColor(getContext(), android.R.color.white));
        } else {
            layout.setBackgroundResource(R.drawable.btn_trade_white_primary);
            textView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorPrimary));
        }
        layout.addView(textView);
        return layout;
    }

    public float dp2Px(float value) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, getResources().getDisplayMetrics());
    }
}
