package com.sbai.finance.activity.stock;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
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
import com.sbai.finance.activity.home.OptionalActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.trade.PublishOpinionActivity;
import com.sbai.finance.fragment.dialog.PredictionDialogFragment;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
import com.sbai.finance.fragment.trade.ViewpointFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Prediction;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
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
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.finance.view.stock.StockTrendView;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.view.TradeFloatButtons.HAS_ADD_OPITION;
import static com.umeng.socialize.utils.ContextUtil.getContext;

public abstract class StockTradeActivity extends BaseActivity {

    protected abstract ViewPager.OnPageChangeListener createPageChangeListener();

    protected abstract PagerAdapter createSubPageAdapter();

    protected abstract ViewpointFragment getViewpointFragment();

    private static final int REQ_CODE_PUBLISH_VIEWPOINT = 172;

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

    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.stockTrendView)
    protected StockTrendView mStockTrendView;
    @BindView(R.id.stockKlineView)
    protected KlineView mStockKlineView;

    @BindView(R.id.tradeFloatButtons)
    TradeFloatButtons mTradeFloatButtons;

    @BindView(R.id.slidingTab)
    SlidingTabLayout mSlidingTab;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    @BindView(R.id.chartArea)
    LinearLayout mChartArea;

    private StockRTData mStockRTData;
    private Prediction mPrediction;

    private PredictionDialogFragment mPredictionFragment;
    protected Variety mVariety;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_trade);
        ButterKnife.bind(this);

        initData();

        initTabLayout();
        initChartViews();
        initSlidingTab();
        initTitleBar();
        initTradeFloatButton();

        requestStockRTData();
        requestOptionalStatus();
    }

    private void initTradeFloatButton() {
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
    }

    private void requestOptionalStatus() {
        if (LocalUser.getUser().isLogin()) {
            Client.checkOptional(mVariety.getVarietyId())
                    .setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback<Resp<Integer>>() {
                        @Override
                        protected void onRespSuccess(Resp<Integer> resp) {
                            Integer result = resp.getData();
                            if (result != null) {
                                boolean hasAddInOption = (result == HAS_ADD_OPITION);
                                mTradeFloatButtons.setHasAddInOption(hasAddInOption);
                            }
                        }
                    }).fireFree();
        }
    }

    private void checkOptionalStatus() {
        if (mTradeFloatButtons.isHasAddInOptional()) {
            showDeleteOptionalDialog();
        } else {
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
                        requestDeleteOptional();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.primaryText))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.no)
                .show();
    }

    private void requestDeleteOptional() {
        Client.delOptional(mVariety.getVarietyId())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mTradeFloatButtons.setHasAddInOption(false);
                            CustomToast.getInstance().showText(getActivity(), R.string.delete_option_succeed);
                            sendAddOptionalBroadCast(mVariety, false);
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void requestAddOptional() {
        Client.addOption(mVariety.getVarietyId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mTradeFloatButtons.setHasAddInOption(true);
                            CustomToast.getInstance().showText(StockTradeActivity.this, R.string.add_option_succeed);
                            sendAddOptionalBroadCast(null, true);
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }

                    @Override
                    protected void onReceiveResponse(Resp<JsonObject> resp) {
                        super.onReceiveResponse(resp);
                        // 701 代表已经添加过
                        if (resp.getCode() == Resp.CODE_REPEAT_ADD) {
                            mTradeFloatButtons.setHasAddInOption(true);
                        }
                    }
                }).fire();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestStockTrendDataAndSet();
        requestExchangeStatus();

        startScheduleJob(1 * 1000);
    }

    private void requestExchangeStatus() {
        Client.getExchangeStatus(mVariety.getExchangeId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Integer>, Integer>() {
                    @Override
                    protected void onRespSuccessData(Integer data) {
                        int exchangeStatus = (data != null ? data.intValue() : mVariety.getExchangeStatus());
                        mVariety.setExchangeStatus(exchangeStatus);
                        updateExchangeStatusView();
                    }
                }).fireFree();
    }

    private void updateExchangeStatusView() {
        int exchangeStatus = mVariety.getExchangeStatus();
        View view = mTitleBar.getCustomView();
        TextView exchangeStatusView = (TextView) view.findViewById(R.id.exchangeStatus);
        if (exchangeStatus == Variety.EXCHANGE_STATUS_CLOSE) {
            exchangeStatusView.setText(R.string.market_close);
        } else {
            exchangeStatusView.setText(R.string.market_trading);
        }
    }

    private void sendAddOptionalBroadCast(Variety variety, Boolean isAddOptional) {
        Intent intent = new Intent();
        intent.setAction(OptionalActivity.OPTIONAL_CHANGE_ACTION);
        intent.putExtra(Launcher.EX_PAYLOAD, variety);
        intent.putExtra(Launcher.EX_PAYLOAD_1, isAddOptional);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(intent);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_PUBLISH_VIEWPOINT:
                case ViewpointFragment.REQ_CODE_USERDATA:
                case ViewpointFragment.REQ_CODE_ATTENTION:
                    updateViewPointPage(data);
                    break;
            }
        }
    }

    private void updateViewPointPage(Intent data) {
        OpinionDetails details = (OpinionDetails) data.getSerializableExtra(Launcher.EX_PAYLOAD);

        WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
                (WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);

        AttentionAndFansNumberModel attentionAndFansNumberModel =
                (AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);
        ViewpointFragment viewpointFragment = getViewpointFragment();
        if (viewpointFragment != null) {
            if (details != null) {
                viewpointFragment.updateItemById(details.getId(), details.getReplyCount(), details.getPraiseCount());
            } else if (whetherAttentionShieldOrNot != null && attentionAndFansNumberModel != null) {
                viewpointFragment.shieldUserByUserId(attentionAndFansNumberModel.getUserId(), whetherAttentionShieldOrNot.isShield());
                viewpointFragment.updateItemByUserId(attentionAndFansNumberModel.getUserId(), whetherAttentionShieldOrNot.isFollow());
            } else {
                viewpointFragment.refreshPointList();
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
                .setCallback(new Callback2D<Resp<StockRTData>, StockRTData>(false) {
                    @Override
                    protected void onRespSuccessData(StockRTData result) {
                        mStockRTData = result;
                        updateStockTrendView();
                        updateMarketDataView();
                        requestExchangeStatus();
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
                mPriceChange.setText(risePrice + "     " + risePercent);
            }
            mTodayOpen.setText(mStockRTData.getOpenPrice());
            mHighest.setText(mStockRTData.getHighestPrice());
            mLowest.setText(mStockRTData.getLowestPrice());
            mPreClose.setText(mStockRTData.getPreSetPrice());

            mStockTrendView.setStockRTData(mStockRTData);
        }
        mLastPrice.setTextColor(color);
        mPriceChange.setTextColor(color);
    }

    private void initTitleBar() {
        View view = mTitleBar.getCustomView();
        TextView productName = (TextView) view.findViewById(R.id.productName);
        TextView exchangeStatus = (TextView) view.findViewById(R.id.exchangeStatus);
        productName.setText(mVariety.getVarietyName() + " (" + mVariety.getVarietyType() + ")");
        if (mVariety.getExchangeStatus() == Variety.EXCHANGE_STATUS_OPEN) {
            exchangeStatus.setText(R.string.market_trading);
        } else {
            exchangeStatus.setText(R.string.market_close);
        }

        setUpTitleBar(mTitleBar);
    }

    public void setUpTitleBar(TitleBar titleBar) {
        final String shareUrl = String.format(Client.SHARE_URL_STOCK,
                mVariety.getVarietyType(), mVariety.getVarietyId());
        final String shareTitle = getString(R.string.wonderful_viewpoint, mVariety.getVarietyName());
        final String shareDescribe = getString(R.string.share_desc);
        titleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialogFragment
                        .newInstance()
                        .setShareContent(shareTitle, shareDescribe, shareUrl)
                        .show(getSupportFragmentManager());
            }
        });
    }

    private void initData() {
        mVariety = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
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
        settings.setDisplayMarketTimes(mVariety.getDisplayMarketTimes());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        mStockTrendView.setSettings(settings);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(5);
        settings2.setNumberScale(mVariety.getPriceScale());
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        settings2.setIndexesEnable(true);
        settings2.setIndexesBaseLines(2);
        mStockKlineView.setDayLine(true);
        mStockKlineView.setSettings(settings2);
        mStockKlineView.setOnReachBorderListener(null);
    }

    private void requestStockTrendDataAndSet() {
        Client.getStockTrendData(mVariety.getVarietyType()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockTrendData>>, List<StockTrendData>>(false) {
                    @Override
                    protected void onRespSuccessData(List<StockTrendData> result) {
                        if (!result.isEmpty()) {
                            mStockTrendView.setDataList(result);
                        }
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
            mPredictionFragment = PredictionDialogFragment.newInstance()
                    .setOnPredictButtonListener(new PredictionDialogFragment.OnPredictButtonListener() {
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
                .executeForResult(REQ_CODE_PUBLISH_VIEWPOINT);

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
