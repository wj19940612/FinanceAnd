package com.sbai.finance.activity.stock;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.stock.FiveMarketFragment;
import com.sbai.finance.fragment.trade.IntroduceFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockTradeActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.todayOpen)
    TextView mTodayOpen;
    @BindView(R.id.preClose)
    TextView mPreClose;
    @BindView(R.id.highest)
    TextView mHighest;
    @BindView(R.id.lowest)
    TextView mLowest;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.trendView)
    TrendView mTrendView;
    @BindView(R.id.klineView)
    KlineView mKlineView;

    @BindView(R.id.tradeFloatButtons)
    TradeFloatButtons mTradeFloatButtons;

    @BindView(R.id.slidingTab)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.chartArea)
    LinearLayout mChartArea;
    @BindView(R.id.subPageArea)
    LinearLayout mSubPageArea;
    @BindView(R.id.detailView)
    FrameLayout mDetailView;
    @BindView(R.id.fiveHq)
    TextView mFiveHq;
    @BindView(R.id.splitHq)
    TextView mSplitHq;
    @BindView(R.id.detailMarket)
    LinearLayout mDetailMarket;
    private SubPageAdapter mSubPageAdapter;
    private Variety mVariety;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trade);
        ButterKnife.bind(this);
        translucentStatusBar();
        if (Build.VERSION.SDK_INT >= 19) {
            addStatusBarHeightTopPadding(mTitleBar);
        }

        mVariety = new Gson().fromJson("{\"displayMarketTimes\": \"06:00;12:00;18:00;00:00;05:00\",\"decimalScale\": 0.2,\"sign\": \"$\",\"varietyType\": \"CL\",\"baseline\": 9,\"isDomestic\": 0,\"tags\": 0,\"exchangeId\": 9,\"openMarketTime\": \"06:00;05:00\",\"flashChartPriceInterval\": 0.2,\"varietyId\": 10,\"exchangeStatus\": 1,\"contractsCode\": \"CL1706\",\"advertisement\": \" \",\"currency\": \"USD\",\"marketPoint\": 2,\"varietyName\": \"美原油\",\"eachPointMoney\": 1000,\"currencyUnit\": \"美元\",\"ratio\": 7.3}", Variety.class);
        initView();
        initTabLayout();
        initChartViews();
        initSlidingTab();
    }

    private void initView() {
        mFiveHq.setSelected(true);
        getSupportFragmentManager().beginTransaction().add(R.id.detailView,new FiveMarketFragment()).commit();
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.five_day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.week_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.month_k_line));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void initChartViews() {
        TrendView.Settings settings = new TrendView.Settings();
        settings.setBaseLines(mVariety.getBaseline());
        settings.setNumberScale(mVariety.getPriceScale());
        settings.setOpenMarketTimes(mVariety.getOpenMarketTime());
        settings.setDisplayMarketTimes(mVariety.getDisplayMarketTimes());
        settings.setLimitUpPercent((float) mVariety.getLimitUpPercent());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        mTrendView.setSettings(settings);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(mVariety.getBaseline());
        settings2.setNumberScale(mVariety.getPriceScale());
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        mKlineView.setSettings(settings2);
        mKlineView.setOnAchieveTheLastListener(null);
    }

    private void initSlidingTab() {
        mViewPager.setOffscreenPageLimit(2);
        mSubPageAdapter = new SubPageAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mSubPageAdapter);

        mSlidingTab.setDistributeEvenly(true);
        mSlidingTab.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTab.setSelectedIndicatorPadding((int) Display.dp2Px(70, getResources()));
        mSlidingTab.setPadding(Display.dp2Px(12, getResources()));
        mSlidingTab.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.blueAssist));
        mSlidingTab.setViewPager(mViewPager);
    }

    private static class SubPageAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;
        Context mContext;

        public SubPageAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.point);
                case 1:
                    return mContext.getString(R.string.stock_news);
                case 2:
                    return mContext.getString(R.string.stock_finance);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new IntroduceFragment();
                case 1:
                    return new IntroduceFragment();
                case 2:
                    return new IntroduceFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String tabText = tab.getText().toString();
            if (tabText.equals(getString(R.string.day_k_line))) {
                requestKlineDataAndSet(null);
                showKlineView();
            } else if (tabText.equals(getString(R.string.five_day_k_line))) {

            } else if (tabText.equals(getString(R.string.week_k_line))) {

            } else if (tabText.equals(getString(R.string.month_k_line))) {

            } else {
                showTrendView();
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    private void showTrendView() {
        mTrendView.setVisibility(View.VISIBLE);
        mKlineView.setVisibility(View.GONE);
        mDetailMarket.setVisibility(View.VISIBLE);
    }

    private void showKlineView() {
        mTrendView.setVisibility(View.GONE);
        mKlineView.setVisibility(View.VISIBLE);
        mDetailMarket.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void requestKlineDataAndSet(final String type) {
        Client.getKlineData(mVariety.getContractsCode(), type, null).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<KlineViewData>>, List<KlineViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<KlineViewData> data) {
                        if (TextUtils.isEmpty(type)) { // dayK
                            mKlineView.setDayLine(true);
                        } else {
                            mKlineView.setDayLine(false);
                        }
                        Collections.reverse(data);
                        mKlineView.setDataList(data);
                    }
                }).fire();
    }
}
