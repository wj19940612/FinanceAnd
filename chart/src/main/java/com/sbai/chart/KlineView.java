package com.sbai.chart;

import android.content.Context;
import android.content.res.Resources;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.chart.domain.KlineViewData;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class KlineView extends RelativeLayout implements KlineChart.OnTouchLinesAppearListener {

    public interface OnReachBorderListener {

        void onReachLeftBorder(KlineViewData theLeft, List<KlineViewData> dataList);

        void onReachRightBorder(KlineViewData theRight, List<KlineViewData> dataList);
    }

    private KlineChart mKlineChart;
    private View mLeftSideBar;
    private View mRightSideBar;
    private boolean mIsDayLine;

    private SimpleDateFormat mDateFormat;

    private OnReachBorderListener mOnReachBorderListener;

    public KlineView(Context context) {
        super(context);
        init();
    }

    public KlineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mKlineChart = new KlineChart(getContext());
        mKlineChart.setOnTouchLinesAppearListener(this);
        int padding = (int) mKlineChart.dp2Px(14);
        RelativeLayout.LayoutParams params = new LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mKlineChart.setPadding(padding, 0, padding, 0);
        addView(mKlineChart, params);

        mLeftSideBar = LayoutInflater.from(getContext()).inflate(R.layout.kline_side_bar, null);
        mRightSideBar = LayoutInflater.from(getContext()).inflate(R.layout.kline_side_bar, null);

        int marginTop = (int) dp2Px(12f, getResources());
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_LEFT);
        params.setMargins(0, marginTop, 0, 0);
        mLeftSideBar.setVisibility(GONE);
        addView(mLeftSideBar, params);

        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.setMargins(0, marginTop, 0, 0);
        mRightSideBar.setVisibility(GONE);
        addView(mRightSideBar, params);

        mDateFormat = new SimpleDateFormat("yyyy/MM/dd\nHH:mm");
    }

    private float dp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    public void setOnReachBorderListener(OnReachBorderListener onReachBorderListener) {
        mOnReachBorderListener = onReachBorderListener;
        if (mKlineChart != null) {
            mKlineChart.setOnReachBorderListener(mOnReachBorderListener);
        }
    }

    private void setSideBar(View sideBar, KlineViewData data, KlineViewData previousData) {
        TextView time = (TextView) sideBar.findViewById(R.id.time);
        TextView openPrice = (TextView) sideBar.findViewById(R.id.openPrice);
        TextView highestPrice = (TextView) sideBar.findViewById(R.id.highestPrice);
        TextView lowestPrice = (TextView) sideBar.findViewById(R.id.lowestPrice);
        TextView closePrice = (TextView) sideBar.findViewById(R.id.closePrice);
        if (mIsDayLine) {
            time.setText(data.getDay().replaceAll("-", "/"));
        } else {
            time.setText(mDateFormat.format(new Date(data.getTimeStamp())));
        }
        openPrice.setText(mKlineChart.formatNumber(data.getOpenPrice()));
        highestPrice.setText(mKlineChart.formatNumber(data.getMaxPrice()));
        lowestPrice.setText(mKlineChart.formatNumber(data.getMinPrice()));
        closePrice.setText(mKlineChart.formatNumber(data.getClosePrice()));

        int redColor = ContextCompat.getColor(getContext(), R.color.colorRed);
        int greenColor = ContextCompat.getColor(getContext(), R.color.colorGreen);

        openPrice.setTextColor(redColor);
        if (previousData != null && data.getOpenPrice() < previousData.getClosePrice()) {
            openPrice.setTextColor(greenColor);
        }

        highestPrice.setTextColor(redColor);
        if (data.getMaxPrice() < data.getOpenPrice()) {
            highestPrice.setTextColor(greenColor);
        }

        lowestPrice.setTextColor(redColor);
        if (data.getMinPrice() < data.getOpenPrice()) {
            lowestPrice.setTextColor(greenColor);
        }

        closePrice.setTextColor(redColor);
        if (data.getClosePrice() < data.getOpenPrice()) {
            closePrice.setTextColor(greenColor);
        }
    }

    public void setDataList(List<KlineViewData> dataList) {
        mKlineChart.setDataList(dataList);
    }

    public void addHistoryData(List<KlineViewData> dataList) {
        mKlineChart.addHistoryData(dataList);
    }

    public void setDayLine(boolean dayLine) {
        mIsDayLine = dayLine;
        mKlineChart.setDayLine(dayLine);
    }

    public void clearData() {
        mKlineChart.clearData();
    }

    public boolean isLastDataVisible() {
        return mKlineChart.isLastDataVisible();
    }

    public void setSettings(ChartSettings settings) {
        mKlineChart.setSettings(settings);
    }

    public void scaleSideBarText(float scale) {
        scaleSideBarText(mLeftSideBar, scale);
        scaleSideBarText(mRightSideBar, scale);
    }

    private void scaleSideBarText(View sideBar, float v) {
        if (sideBar != null && sideBar instanceof ViewGroup) {
            ViewGroup group = (ViewGroup) sideBar;
            for (int i = 0; i < group.getChildCount(); i++) {
                View child = group.getChildAt(i);
                if (child instanceof TextView) {
                    float textSize = ((TextView) child).getTextSize();
                    ((TextView) child).setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize * v);
                }
            }
        }
    }

    @Override
    public void onAppear(KlineViewData data, KlineViewData previousData, boolean isLeftArea) {
        setSideBar(mLeftSideBar, data, previousData);
        setSideBar(mRightSideBar, data, previousData);

        if (isLeftArea) {
            showRightSideBar();
            hideLeftSideBar();
        } else {
            showLeftSideBar();
            hideRightSideBar();
        }
    }

    private void showRightSideBar() {
        if (mRightSideBar.getVisibility() == GONE) {
            mRightSideBar.setVisibility(VISIBLE);
            mRightSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_right));
        }
    }

    private void hideLeftSideBar() {
        if (mLeftSideBar.getVisibility() == VISIBLE) {
            mLeftSideBar.setVisibility(GONE);
            mLeftSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_left));
        }
    }

    private void showLeftSideBar() {
        if (mLeftSideBar.getVisibility() == GONE) {
            mLeftSideBar.setVisibility(VISIBLE);
            mLeftSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_in_from_left));
        }
    }

    private void hideRightSideBar() {
        if (mRightSideBar.getVisibility() == VISIBLE) {
            mRightSideBar.setVisibility(GONE);
            mRightSideBar.startAnimation(AnimationUtils.loadAnimation(getContext(), R.anim.slide_out_to_right));
        }
    }

    @Override
    public void onDisappear() {
        hideLeftSideBar();
        hideRightSideBar();
    }
}
