package com.sbai.finance.view.slidingTab;

import android.app.Activity;
import android.content.Context;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;

/**
 * 带有返回键的tablayout
 */

public class SlidingTabTitle extends SlidingTabLayout {

    public SlidingTabTitle(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlidingTabTitle(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initView(context);
    }

    private void initView(Context context) {
        int fixedHeight = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 48,
                getResources().getDisplayMetrics());
        int paddingHorizontal = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 14,
                getResources().getDisplayMetrics());
        removeAllViews();
        RelativeLayout viewGroup = new RelativeLayout(context);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, fixedHeight);
        viewGroup.setLayoutParams(params);

        // left back view
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, fixedHeight);
        params.addRule(RelativeLayout.ALIGN_PARENT_LEFT, RelativeLayout.TRUE);
        TextView leftView = new TextView(getContext());
        leftView.setGravity(Gravity.CENTER);
        leftView.setPadding(paddingHorizontal, 0, paddingHorizontal, 0);
        leftView.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_tb_back_black, 0, 0, 0);
        leftView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() instanceof Activity) {
                    Activity activity = (Activity) getContext();
                    activity.onBackPressed();
                }
            }
        });
        viewGroup.addView(leftView, params);
        // center tab view
        params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(RelativeLayout.CENTER_HORIZONTAL, RelativeLayout.TRUE);
        params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM, RelativeLayout.TRUE);
        params.setMargins(0, 0, 0, 0);
        viewGroup.addView(getTabStrip(), params);
        addView(viewGroup, LayoutParams.MATCH_PARENT, fixedHeight);
    }

    public void setTabLeftAndRightMargin(int margin) {
        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) getTabStrip().getLayoutParams();
        if (params != null) {
            params.setMargins(margin, 0, margin, 0);
        }
    }

    public void setTabIndex(final int index) {
        if (index < getTabStrip().getChildCount()) {
            getTabStrip().getChildAt(index).performClick();
        }
    }

    @Override
    protected TextView createDefaultTabView(Context context) {
        TextView textView = super.createDefaultTabView(context);
        textView.setPadding(0,0,0,textView.getPaddingBottom());
        return textView;
    }
}
