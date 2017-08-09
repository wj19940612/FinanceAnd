package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import java.util.ArrayList;
import java.util.List;

public class FeaturesNavigation extends LinearLayout {

    private static class NavItem {
        public int iconRes;
        public int textRes;
        public Runnable task;

        public NavItem(int iconRes, int textRes, Runnable task) {
            this.iconRes = iconRes;
            this.textRes = textRes;
            this.task = task;
        }
    }

    private OnNavItemClickListener mListener;
    private List<NavItem> mNavItemList;

    public void setOnNavItemClickListener(OnNavItemClickListener onNavItemClickListener) {
        mListener = onNavItemClickListener;
    }

    public interface OnNavItemClickListener {

        void onOptionalClick();

        void onFuturesClick();

        void onStockClick();

        void onLeaderboardClick();
    }

    public FeaturesNavigation(@NonNull Context context) {
        super(context);
        init();
    }

    public FeaturesNavigation(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mNavItemList = new ArrayList<>();
        mNavItemList.add(new NavItem(R.drawable.ic_nav_optional, R.string.optional, new Runnable() {
            @Override
            public void run() {
                if (mListener != null) mListener.onOptionalClick();
            }
        }));
        mNavItemList.add(new NavItem(R.drawable.ic_nav_stock, R.string.stock, new Runnable() {
            @Override
            public void run() {
                if (mListener != null) mListener.onStockClick();
            }
        }));
        mNavItemList.add(new NavItem(R.drawable.ic_nav_futures, R.string.futures, new Runnable() {
            @Override
            public void run() {
                if (mListener != null) mListener.onFuturesClick();
            }
        }));
        mNavItemList.add(new NavItem(R.drawable.ic_nav_leaderboard, R.string.ranking, new Runnable() {
            @Override
            public void run() {
                if (mListener != null) mListener.onLeaderboardClick();
            }
        }));

        setupView();
    }

    private void setupView() {
        setOrientation(HORIZONTAL);

        for (int i = 0; i < mNavItemList.size(); i++) {
            View itemView = createItemView(mNavItemList.get(i));
            LinearLayout.LayoutParams params = new LayoutParams(0, LayoutParams.WRAP_CONTENT);
            params.weight = 1;
            addView(itemView, params);
        }
    }

    private View createItemView(final NavItem navItem) {
        int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20,
                getResources().getDisplayMetrics());
        int drawablePadding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8,
                getResources().getDisplayMetrics());
        TextView textView = new TextView(getContext());
        textView.setText(navItem.textRes);
        textView.setGravity(Gravity.CENTER);
        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
        textView.setBackgroundResource(R.drawable.bg_white);
        textView.setCompoundDrawablesWithIntrinsicBounds(0, navItem.iconRes, 0, 0);
        textView.setCompoundDrawablePadding(drawablePadding);
        textView.setPadding(0, padding, 0, padding);
        textView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Runnable task = navItem.task;
                if (task != null) task.run();
            }
        });
        return textView;
    }
}
