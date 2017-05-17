package com.sbai.finance.activity.stock;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.chart.ChartSettings;
import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.trade.PublishOpinionActivity;
import com.sbai.finance.fragment.dialog.PredictionDialogFragment;
import com.sbai.finance.fragment.stock.PriceLimitRankingFragment;
import com.sbai.finance.fragment.trade.ViewpointFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Prediction;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.stock.StockKlineData;
import com.sbai.finance.model.stock.StockRTData;
import com.sbai.finance.model.stock.StockTrendData;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.finance.view.stock.StockTrendView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class StockIndexTradeActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.marketArea)
    LinearLayout mMarketArea;

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
    @BindView(R.id.stockTrendView)
    StockTrendView mStockTrendView;
    @BindView(R.id.stockKlineView)
    KlineView mStockKlineView;

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

    private SubPageAdapter mSubPageAdapter;
    private Variety mVariety;
    private StockRTData mStockRTData;
    private Prediction mPrediction;

    PredictionDialogFragment mPredictionFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_index_trade);
        ButterKnife.bind(this);
        translucentStatusBar();
        if (Build.VERSION.SDK_INT >= 19) {
            addStatusBarHeightTopPadding(mTitleBar);
        }

        initData();

        initTabLayout();
        initChartViews();
        initSlidingTab();

        updateTitleBar();

        mTradeFloatButtons.setOnViewClickListener(new TradeFloatButtons.OnViewClickListener() {
            @Override
            public void onPublishPointButtonClick() {
                if (LocalUser.getUser().isLogin()) {
                    requestPrediction();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onAddOptionalButtonClick() {
                if (LocalUser.getUser().isLogin()) {
                    checkOptionalStatus();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onTradeButtonClick() {

            }
        });


        requestStockRTData();
    }

    private void checkOptionalStatus() {
        if (mTradeFloatButtons.isHasAddInOptional()) {
            requestDeleteOptional();
        } else {
            requestAddOptional();
        }
    }

    private void requestAddOptional() {
        Client.addOption(mVariety.getVarietyId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mTradeFloatButtons.setHasAddInOpition(true);
                            CustomToast.getInstance().showText(StockIndexTradeActivity.this, R.string.add_option_succeed);
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }

                    @Override
                    protected void onReceive(Resp<JsonObject> resp) {
                        super.onReceive(resp);
                        // 701 代表已经添加过
                        if (resp.getCode() == Resp.CODE_REPEAT_ADD) {
                            mTradeFloatButtons.setHasAddInOpition(true);
                        }
                    }
                }).fire();
    }

    private void requestDeleteOptional() {
        SmartDialog.with(getActivity(), getString(R.string.whether_to_cancel_optional))
                .setMessageTextSize(15)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Client.delOptional(mVariety.getVarietyId())
                                .setTag(TAG)
                                .setIndeterminate(StockIndexTradeActivity.this)
                                .setCallback(new Callback<Resp<JsonObject>>() {
                                    @Override
                                    protected void onRespSuccess(Resp<JsonObject> resp) {
                                        if (resp.isSuccess()) {
                                            mTradeFloatButtons.setHasAddInOpition(false);
                                            CustomToast.getInstance().showText(StockIndexTradeActivity.this, R.string.delete_option_succeed);
                                        } else {
                                            ToastUtil.curt(resp.getMsg());
                                        }
                                    }
                                })
                                .fire();
                        dialog.dismiss();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.cancel)
                .show();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startScheduleJob(1 * 1000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == StockTradeActivity.REQ_CODE_PUBLISH_VIEWPOINT && resultCode == RESULT_OK) {
            Fragment fragment = mSubPageAdapter.getFragment(0);
            if (fragment != null && fragment instanceof ViewpointFragment) {
                ((ViewpointFragment) fragment).refreshPointList();
            }
        }
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TimerHandler.TREND_REFRESH_TIME == 0) {
            requestStockTrendDataAndSet();
        }
        if (count % TimerHandler.STOCK_RT_PULL_TIME == 0) {
            requestStockRTData();
        }
    }

    private void requestStockRTData() {
        Client.getStockRealtimeData(mVariety.getVarietyType())
                .setCallback(new StockCallback<StockResp, List<StockRTData>>(false) {
                    @Override
                    public void onDataMsg(List<StockRTData> result, StockResp.Msg msg) {
                        if (!result.isEmpty()) {
                            mStockRTData = result.get(0);
                        }
                        updateMarketDataView();
                    }
                }).fireSync();
    }

    private void updateMarketDataView() {
        int color = ContextCompat.getColor(getActivity(), R.color.redPrimary);
        if (mStockRTData != null) {
            String risePrice = mStockRTData.getRise_price();
            String risePercent = mStockRTData.getRise_pre();
            String lastPrice = mStockRTData.getLast_price();
            if (risePrice.startsWith("-")) {
                color = ContextCompat.getColor(getActivity(), R.color.greenPrimary);
            } else {
                risePrice = "+" + risePrice;
                risePercent = "+" + risePercent;
            }
            mLastPrice.setText(lastPrice);
            mPriceChange.setText(risePrice + "     " + risePercent + "%");
            mTodayOpen.setText(mStockRTData.getOpen_price());
            mHighest.setText(mStockRTData.getHigh_price());
            mLowest.setText(mStockRTData.getLow_price());
            mPreClose.setText(mStockRTData.getPrev_price());

            mStockTrendView.setStockRTData(mStockRTData);
        }
        mMarketArea.setBackgroundColor(color);
        mTitleBar.setBackgroundColor(color);
    }

    private void updateTitleBar() {
        mTitleBar.setTitle(mVariety.getVarietyName() + " (" + mVariety.getVarietyType() + ")");
    }

    private void initData() {
        mVariety = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }


    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.week_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.month_k_line));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void initChartViews() {
        ChartSettings settings = new ChartSettings();
        settings.setBaseLines(mVariety.getBaseline());
        settings.setNumberScale(mVariety.getPriceScale());
        settings.setIndexesEnable(true);
        settings.setIndexesBaseLines(2);
        settings.setXAxis(241);
        mStockTrendView.setSettings(settings);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(mVariety.getBaseline());
        settings2.setNumberScale(mVariety.getPriceScale());
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        mStockKlineView.setDayLine(true);
        mStockKlineView.setSettings(settings2);
        mStockKlineView.setOnAchieveTheLastListener(null);

        requestStockTrendDataAndSet();
    }

    private void requestStockTrendDataAndSet() {
        Client.getStockTrendData(mVariety.getVarietyType()).setTag(TAG)
                .setCallback(new StockCallback<StockResp, List<StockTrendData>>() {
                    @Override
                    public void onDataMsg(List<StockTrendData> result, StockResp.Msg msg) {
                        mStockTrendView.setDataList(result);
                    }
                }).fireSync();
    }

    private void initSlidingTab() {
        mSubPageAdapter = new SubPageAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mSubPageAdapter);
        mSlidingTab.setDistributeEvenly(true);
        mSlidingTab.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTab.setSelectedIndicatorPadding((int) Display.dp2Px(70, getResources()));
        mSlidingTab.setPadding(Display.dp2Px(12, getResources()));
        mSlidingTab.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.blueAssist));
        mSlidingTab.setViewPager(mViewPager);
        mSlidingTab.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                if (position == 1) {
                    PriceLimitRankingFragment priceLimitRankingFragment = getPriceLimitRankingFragment(1);
                    if (priceLimitRankingFragment != null) {
                        priceLimitRankingFragment.requestStockSortList();
                    }
                } else if (position == 2) {
                    PriceLimitRankingFragment priceLimitRankingFragment = getPriceLimitRankingFragment(2);
                    if (priceLimitRankingFragment != null) {
                        priceLimitRankingFragment.requestStockSortList();
                    }
                }

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

    }

    private PriceLimitRankingFragment getPriceLimitRankingFragment(int position) {
        Fragment fragment = mSubPageAdapter.getFragment(position);
        if (fragment instanceof PriceLimitRankingFragment) {
            return (PriceLimitRankingFragment) fragment;
        }
        return null;
    }

    private void requestPrediction() {
        Client.getPrediction(mVariety.getBigVarietyTypeCode(), mVariety.getVarietyId())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Prediction>, Prediction>() {
                    @Override
                    protected void onRespSuccessData(Prediction data) {
                        mPrediction = data;
                        if (mPrediction.isCalculate()) {
                            openPublishPointPage();
                        } else {
                            showPredictDialog();
                        }
                    }
                }).fire();
    }

    private void showPredictDialog() {
        if (mPredictionFragment == null) {
            mPredictionFragment = PredictionDialogFragment.newInstance().setOnPredictButtonListener(new PredictionDialogFragment.OnPredictButtonListener() {
                @Override
                public void onBullishButtonClick(int directionLong) {
                    mPrediction.setDirection(directionLong);
                    openPublishPointPage();
                }

                @Override
                public void onBearishButtonClick(int directionShort) {
                    mPrediction.setDirection(directionShort);
                    openPublishPointPage();
                }
            });
        }
        mPredictionFragment.show(getSupportFragmentManager());
    }

    private void openPublishPointPage() {
        Launcher.with(getActivity(), PublishOpinionActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, mVariety)
                .putExtra(Launcher.EX_PAYLOAD_1, mPrediction)
//                .putExtra(Launcher.EX_PAYLOAD_2, mStockRTData)
                .executeForResult(StockTradeActivity.REQ_CODE_PUBLISH_VIEWPOINT);
    }

    private class SubPageAdapter extends FragmentPagerAdapter {

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
                    return mContext.getString(R.string.rise);
                case 2:
                    return mContext.getString(R.string.down);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ViewpointFragment.newInstance(mVariety.getVarietyId());
                case 1:
                    return PriceLimitRankingFragment.newInstance(1, mVariety.getExchangeCode());
                case 2:
                    return PriceLimitRankingFragment.newInstance(2, mVariety.getExchangeCode());
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
                requestKlineDataAndSet(StockKlineData.PERIOD_DAY);
                showKlineView();
            } else if (tabText.equals(getString(R.string.week_k_line))) {
                requestKlineDataAndSet(StockKlineData.PERIOD_WEEK);
                showKlineView();
            } else if (tabText.equals(getString(R.string.month_k_line))) {
                requestKlineDataAndSet(StockKlineData.PERIOD_MONTH);
                showKlineView();
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
        mStockTrendView.setVisibility(View.VISIBLE);
        mStockKlineView.setVisibility(View.GONE);
    }

    private void showKlineView() {
        mStockTrendView.setVisibility(View.GONE);
        mStockKlineView.setVisibility(View.VISIBLE);
    }

    private void requestKlineDataAndSet(int type) {
        Client.getStockKlineData(mVariety.getVarietyType(), type)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new StockCallback<StockResp, List<StockKlineData>>() {
                    @Override
                    public void onDataMsg(List<StockKlineData> result, StockResp.Msg msg) {
                        List<KlineViewData> dataList = new ArrayList<KlineViewData>(result);
                        mStockKlineView.setDataList(dataList);
                    }
                }).fire();
    }
}
