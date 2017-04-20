package com.sbai.finance.activity.future;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.trade.IntroduceFragment;
import com.sbai.finance.fragment.trade.PointFragment;
import com.sbai.finance.model.Product;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FutureTradeActivity extends BaseActivity {

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
    FrameLayout mChartArea;
    @BindView(R.id.subPageArea)
    LinearLayout mSubPageArea;

    private SubPageAdapter mSubPageAdapter;
    private Product mProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_trade);
        ButterKnife.bind(this);
        translucentStatusBar();
        if (Build.VERSION.SDK_INT >= 19) {
            addTopPaddingWithStatusBar(mTitleBar);
        }

        mProduct = new Gson().fromJson("{\"displayMarketTimes\": \"06:00;12:00;18:00;00:00;05:00\",\"decimalScale\": 0.2,\"sign\": \"$\",\"varietyType\": \"CL\",\"baseline\": 9,\"isDomestic\": 0,\"tags\": 0,\"exchangeId\": 9,\"openMarketTime\": \"06:00;05:00\",\"flashChartPriceInterval\": 0.2,\"varietyId\": 10,\"exchangeStatus\": 1,\"contractsCode\": \"CL1706\",\"advertisement\": \" \",\"currency\": \"USD\",\"marketPoint\": 2,\"varietyName\": \"美原油\",\"eachPointMoney\": 1000,\"currencyUnit\": \"美元\",\"ratio\": 7.3}", Product.class);

        initTabLayout();
        initChartViews();
        initSlidingTab();

        mSubPageArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.sixty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.thirty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fifteen_min_k));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void initChartViews() {
        mTrendView.clearData();
        TrendView.Settings settings = new TrendView.Settings();
        settings.setBaseLines(mProduct.getBaseline());
        settings.setNumberScale(mProduct.getPriceDecimalScale());
        settings.setOpenMarketTimes(mProduct.getOpenMarketTime());
        settings.setDisplayMarketTimes(mProduct.getDisplayMarketTimes());
        settings.setLimitUpPercent((float) mProduct.getLimitUpPercent());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        mTrendView.setSettings(settings);

        Client.getTrendData(mProduct.getVarietyType()).setTag(TAG)
                .setCallback(new Callback<String>() {
                    @Override
                    protected void onRespSuccess(String resp) {
                        TrendView.Settings settings = mTrendView.getSettings();
                        mTrendView.setDataList(TrendView.Util.createDataList(resp, settings.getOpenMarketTimes()));
                    }
                }).fireSync();
    }

    private void initSlidingTab() {
        mViewPager.setOffscreenPageLimit(1);
        mSubPageAdapter = new SubPageAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mSubPageAdapter);

        mSlidingTab.setDistributeEvenly(true);
        mSlidingTab.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTab.setSelectedIndicatorPadding((int) Display.dp2Px(80, getResources()));
        mSlidingTab.setPadding(Display.dp2Px(12, getResources()));
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
                    return mContext.getString(R.string.introduce);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PointFragment();
                case 1:
                    return new IntroduceFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }
}
