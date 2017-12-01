package com.sbai.finance.activity.stock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.chart.ChartSettings;
import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.optional.OptionalListFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.model.stock.StockKlineData;
import com.sbai.finance.model.stock.StockRTData;
import com.sbai.finance.model.stock.StockTrendData;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;
import com.sbai.finance.utils.AnimUtils;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.StockUtil;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.finance.view.stock.StockTrendView;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;
import static com.umeng.socialize.utils.ContextUtil.getContext;

public abstract class StockTradeActivity extends BaseActivity {
    @BindView(R.id.mockTrade)
    protected Button mMockTrading;

    protected abstract ViewPager.OnPageChangeListener createPageChangeListener();

    protected abstract PagerAdapter createSubPageAdapter();

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;

    @BindView(R.id.todayOpen)
    TextView mTodayOpen;
    @BindView(R.id.preClose)
    TextView mPreClose;
    @BindView(R.id.highest)
    TextView mHighest;
    @BindView(R.id.lowest)
    TextView mLowest;

    @BindView(R.id.volume)
    TextView mVolume;
    @BindView(R.id.amount)
    TextView mAmount;
    @BindView(R.id.turnoverRate)
    TextView mTurnoverRate;
    @BindView(R.id.priceEarningRate)
    TextView mPriceEarningRate;
    @BindView(R.id.volumeRate)
    TextView mVolumeRate;
    @BindView(R.id.amplitude)
    TextView mAmplitude;
    @BindView(R.id.equity)
    TextView mEquity;
    @BindView(R.id.marketValue)
    TextView mMarketValue;
    @BindView(R.id.moreMarketDataArea)
    LinearLayout mMoreMarketDataArea;
    @BindView(R.id.moreDataDownArrow)
    ImageView mMoreDataDownArrow;

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.stockTrendView)
    protected StockTrendView mStockTrendView;
    @BindView(R.id.stockKlineView)
    protected KlineView mStockKlineView;

    @BindView(R.id.slidingTab)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.chartArea)
    LinearLayout mChartArea;

    private StockRTData mStockRTData;

    protected Stock mStock;
    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                requestStockTrendDataAndSet();
                requestStockRTData();
                requestExchangeStatus();
                requestOptionalStatus();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trade);
        ButterKnife.bind(this);

        initData(getIntent());

        initTabLayout();
        initChartViews();
        initSlidingTab();
        initTitleBar();

        requestStockRTData();
        requestOptionalStatus();
    }

    private void requestOptionalStatus() {
        if (LocalUser.getUser().isLogin()) {
            Client.getStockInfo(mStock.getVarietyCode()).setTag(TAG)
                    .setCallback(new Callback<Resp<Stock>>() {
                        @Override
                        protected void onRespSuccess(Resp<Stock> resp) {
                            Stock result = resp.getData();
                            if (result != null && result.isOptional()) {
                                showCancelOptionalOfTitleBar();
                            }
                        }
                    }).fireFree();
        }
    }

    private void showCancelOptionalOfTitleBar() {
        mTitleBar.setRightText(R.string.cancel_optional);
        mTitleBar.setRightTextLeftImage(null);
    }

    private void showAddOptionalOfTitleBar() {
        mTitleBar.setRightText(R.string.optional);
        mTitleBar.setRightTextLeftImage(R.drawable.ic_add_optional_of_title);
    }

    private void checkOptionalStatus() {
        if (mTitleBar.getRightText().toString().equals(getString(R.string.cancel_optional))) {
            showDeleteOptionalDialog();
        } else {
            umengEventCount(UmengCountEventId.DISCOVERY_ADD_SELF_OPTIONAL);
            requestAddOptional();
        }
    }

    private void showDeleteOptionalDialog() {
        SmartDialog.with(getActivity(), getString(R.string.whether_to_cancel_optional), getString(R.string.hint))
                .setMessageTextSize(15)
                .setPositive(R.string.yes, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestCancelOptional();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.primaryText))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.no)
                .show();
    }

    private void requestCancelOptional() {
        Client.delOptional(mStock.getVarietyCode(), mStock.getType())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            showAddOptionalOfTitleBar();
                            CustomToast.getInstance().showText(getActivity(), R.string.delete_option_succeed);
                            sendAddOptionalBroadCast(mStock, false);
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void requestAddOptional() {
        Client.addOption(mStock.getId(), Stock.OPTIONAL_TYPE_STOCK)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            showCancelOptionalOfTitleBar();
                            CustomToast.getInstance().showText(StockTradeActivity.this, R.string.add_option_succeed);
                            sendAddOptionalBroadCast(null, true);
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        // 701 代表已经添加过
                        if (failedResp.getCode() == Resp.CODE_REPEAT_ADD) {
                            showCancelOptionalOfTitleBar();
                        }
                    }
                }).fire();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestStockTrendDataAndSet();
        requestExchangeStatus();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
        startScheduleJob(1 * 1000);
    }

    private void requestExchangeStatus() {
        Client.getExchangeStatus(mStock.getExchangeId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Integer>, Integer>() {
                    @Override
                    protected void onRespSuccessData(Integer data) {
                        int exchangeStatus = (data != null ? data.intValue() : mStock.getExchangeOpened());
                        mStock.setExchangeOpened(exchangeStatus);
                        updateExchangeStatusView();
                    }
                }).fireFree();
    }

    private void updateExchangeStatusView() {
        int exchangeStatus = mStock.getExchangeOpened();
        View view = mTitleBar.getCustomView();
        TextView exchangeStatusView = (TextView) view.findViewById(R.id.exchangeStatus);
        if (exchangeStatus == Stock.EXCHANGE_STATUS_CLOSE) {
            exchangeStatusView.setText(R.string.market_close);
        } else {
            exchangeStatusView.setText(R.string.market_trading);
        }
    }

    private void sendAddOptionalBroadCast(Stock stock, Boolean isAddOptional) {
        Intent intent = new Intent();
        intent.setAction(OptionalListFragment.OPTIONAL_CHANGE_ACTION);
        intent.putExtra(Launcher.EX_PAYLOAD, stock);
        intent.putExtra(Launcher.EX_PAYLOAD_1, isAddOptional);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        stopScheduleJob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
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
        Client.getStockRealtimeData(mStock.getVarietyCode())
                .setCallback(new Callback2D<Resp<StockRTData>, StockRTData>() {
                    @Override
                    protected void onRespSuccessData(StockRTData result) {
                        mStockRTData = result;
                        updateStockTrendView();
                        updateMarketDataView();
                        requestExchangeStatus();
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fireFree();
    }

    private void updateStockTrendView() {
        ChartSettings settings = mStockTrendView.getSettings();
        if (settings != null && settings.getPreClosePrice() == 0) {
            if (!TextUtils.isEmpty(mStockRTData.getPreSetPrice())) {
                settings.setPreClosePrice(Float.valueOf(mStockRTData.getPreSetPrice()).floatValue());
                mStockTrendView.setSettings(settings);
            }
        }
    }

    private void updateMarketDataView() {
        int color = ContextCompat.getColor(getActivity(), R.color.redPrimary);
        if (mStockRTData != null) {
            String risePrice = mStockRTData.getUpDropPrice();
            String risePercent = FinanceUtil.formatToPercentage(mStockRTData.getUpDropSpeed());
            String lastPrice = mStockRTData.getLastPrice();
            if (!TextUtils.isEmpty(risePrice)) {
                if (risePrice.startsWith("-")) {
                    color = ContextCompat.getColor(getActivity(), R.color.greenAssist);
                } else {
                    risePrice = "+" + risePrice;
                    risePercent = "+" + risePercent;
                }
            }
            mLastPrice.setText(lastPrice);
            if (mStockRTData.getStatus().equals(StockRTData.STATUS_DELIST)) {
                color = ContextCompat.getColor(getActivity(), R.color.unluckyText);
                mPriceChange.setText(R.string.delist);
            } else {
                mPriceChange.setText(risePrice + "\n" + risePercent);
            }

            mTodayOpen.setText(StockUtil.getStockDecimal(mStockRTData.getOpenPrice()));
            mHighest.setText(StockUtil.getStockDecimal(mStockRTData.getHighestPrice()));
            mLowest.setText(StockUtil.getStockDecimal(mStockRTData.getLowestPrice()));
            mPreClose.setText(StockUtil.getStockDecimal(mStockRTData.getPreClsPrice()));

            mVolume.setText(StockUtil.getStockVolume(mStockRTData.getVolume()));
            mAmount.setText(StockUtil.getStockAmount(mStockRTData.getTurnover()));
            mTurnoverRate.setText(StockUtil.getStockPercentage(mStockRTData.getTurnoverRate()));
            mPriceEarningRate.setText(StockUtil.getStockDecimal(mStockRTData.getPe()));

            mVolumeRate.setText(StockUtil.getStockDecimal(mStockRTData.getVolRate()));
            mAmplitude.setText(mStockRTData.getAmplitude());
            mEquity.setText(StockUtil.getStockAmount(mStockRTData.getTotalShares()));
            mMarketValue.setText(StockUtil.getStockAmount(mStockRTData.getMarketValue()));

            mStockTrendView.setStockRTData(mStockRTData);
        }
        mLastPrice.setTextColor(color);
        mPriceChange.setTextColor(color);
    }

    private void initTitleBar() {
        View view = mTitleBar.getCustomView();
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView exchangeStatus = (TextView) view.findViewById(R.id.exchangeStatus);
        productName.setText(mStock.getVarietyName() + " (" + mStock.getVarietyCode() + ")");
        if (mStock.getExchangeOpened() == Stock.EXCHANGE_STATUS_OPEN) {
            exchangeStatus.setText(R.string.market_trading);
        } else {
            exchangeStatus.setText(R.string.market_close);
        }
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    checkOptionalStatus();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });

        setUpTitleBar(mTitleBar);
    }

    public void setUpTitleBar(TitleBar titleBar) {
    }

    private void initData(Intent intent) {
        mStock = intent.getParcelableExtra(Launcher.EX_PAYLOAD);
    }


    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.week_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.month_k_line));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    protected void initChartViews() {
        StockTrendView.Settings settings = new StockTrendView.Settings();
        settings.setBaseLines(5);
        settings.setNumberScale(2);
        settings.setIndexesEnable(true);
        settings.setIndexesBaseLines(2);
        settings.setOpenMarketTimes("09:30;11:30;13:00;15:00");
        settings.setDisplayMarketTimes("09:30;13:00;15:00");
        settings.setCalculateXAxisFromOpenMarketTime(true);
        mStockTrendView.setSettings(settings);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(5);
        settings2.setNumberScale(Stock.PRICE_SCALE);
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        settings2.setIndexesEnable(true);
        settings2.setIndexesBaseLines(2);
        mStockKlineView.setDayLine(true);
        mStockKlineView.setSettings(settings2);
        mStockKlineView.setOnReachBorderListener(null);
    }

    private void requestStockTrendDataAndSet() {
        Client.getStockTrendData(mStock.getVarietyCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockTrendData>>, List<StockTrendData>>() {
                    @Override
                    protected void onRespSuccessData(List<StockTrendData> result) {
                        if (!result.isEmpty()) {
                            mStockTrendView.setDataList(result);
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fireFree();
    }

    private void initSlidingTab() {
        mViewPager.setAdapter(createSubPageAdapter());

        mSlidingTab.setDistributeEvenly(true);
        mSlidingTab.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTab.setSelectedIndicatorPadding((int) Display.dp2Px(30, getResources()));
        mSlidingTab.setPadding(Display.dp2Px(12, getResources()));
        mSlidingTab.setViewPager(mViewPager);
        mSlidingTab.setOnPageChangeListener(createPageChangeListener());
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
        mStockKlineView.clearData();
        Client.getStockKlineData(mStock.getVarietyCode(), type)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new StockCallback<StockResp, List<StockKlineData>>() {
                    @Override
                    public void onDataMsg(List<StockKlineData> result, StockResp.Msg msg) {
                        List<KlineViewData> dataList = new ArrayList<KlineViewData>(result);
                        mStockKlineView.setDataList(dataList);
                    }
                }).fire();
    }

    @OnClick({R.id.moreDataDownArrow, R.id.mockTrade})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.moreDataDownArrow:
                if (mMoreMarketDataArea.getVisibility() == View.VISIBLE) {
                    mMoreDataDownArrow.setRotation(mMoreDataDownArrow.getRotation() + 180);
                    mMoreMarketDataArea.startAnimation(AnimUtils.createCollapseY(mMoreMarketDataArea, 400));
                } else {
                    mMoreDataDownArrow.setRotation(mMoreDataDownArrow.getRotation() - 180);
                    mMoreMarketDataArea.startAnimation(AnimUtils.createExpendY(mMoreMarketDataArea, 400));
                }
                break;
            case R.id.mockTrade:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), StockTradeOperateActivity.class)
                            .putExtra(ExtraKeys.VARIETY, mStock)
                            .execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }

    }
}
