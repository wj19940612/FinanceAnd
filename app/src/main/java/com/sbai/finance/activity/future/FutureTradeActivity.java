package com.sbai.finance.activity.future;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;
import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.chart.domain.TrendViewData;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.home.OptionalActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.dialog.TradeOptionDialogFragment;
import com.sbai.finance.model.FutureIntroduce;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Prediction;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.future.FutureTradeStatus;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.TradeFloatButtons;
import com.sbai.finance.market.DataReceiveListener;
import com.sbai.finance.market.MarketSubscriber;
import com.umeng.socialize.UMShareAPI;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.R.id.klineView;
import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;
import static com.sbai.finance.view.TradeFloatButtons.HAS_ADD_OPITION;
import static com.sbai.finance.market.MarketSubscribe.REQ_QUOTA;
import static com.umeng.socialize.utils.ContextUtil.getContext;

public class FutureTradeActivity extends BaseActivity {

    //打开观点详情页
    public static final int REQ_CODE_PUBLISH = 5439;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

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
    @BindView(R.id.trendView)
    TrendView mTrendView;
    @BindView(klineView)
    KlineView mKlineView;

    @BindView(R.id.tradeFloatButtons)
    TradeFloatButtons mTradeFloatButtons;

    @BindView(R.id.chartArea)
    LinearLayout mChartArea;
    @BindView(R.id.lastPrice)
    TextView mLastPrice;

    @BindView(R.id.tradeCategory)
    TextView mTradeCategory;
    @BindView(R.id.tradeCode)
    TextView mTradeCode;
    @BindView(R.id.tradeTimeSummerWinter)
    TextView mTradeTimeSummerWinter;
    @BindView(R.id.holdingTime)
    TextView mHoldingTime;
    @BindView(R.id.tradeUnit)
    TextView mTradeUnit;
    @BindView(R.id.quoteUnit)
    TextView mQuoteUnit;
    @BindView(R.id.lowestMargin)
    TextView mLowestMargin;
    @BindView(R.id.tradeType)
    TextView mTradeType;
    @BindView(R.id.tradeSystem)
    TextView mTradeSystem;
    @BindView(R.id.deliveryTime)
    TextView mDeliveryTime;
    @BindView(R.id.dailyPriceMaximumVolatilityLimit)
    TextView mDailyPriceMaximumVolatilityLimit;

    private Variety mVariety;
    private Prediction mPrediction;
    private FutureData mFutureData;

    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                requestExchangeStatus();
                requestOptionalStatus();
                requestTrendDataAndSet();
                requestVarietyTradeIntroduce();
                requestTradeButtonVisible();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_trade);
        ButterKnife.bind(this);

        initData();

        initTabLayout();
        initChartViews();
        initFloatBar();
        initTitleBar();

        updateExchangeStatusView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MarketSubscriber.get().subscribe(mVariety.getContractsCode());
        MarketSubscriber.get().addDataReceiveListener(mDataReceiveListener);

        requestExchangeStatus();
        requestOptionalStatus();

        requestTrendDataAndSet();

        requestVarietyTradeIntroduce();

        requestTradeButtonVisible();

        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    private void requestTradeButtonVisible() {
        Client.getFutureTradeStatus().setTag(TAG)
                .setCallback(new Callback2D<Resp<FutureTradeStatus>, FutureTradeStatus>() {
                    @Override
                    protected void onRespSuccessData(FutureTradeStatus data) {
                        updateTradeStatus(data);
                    }
                }).fireFree();
    }

    private void requestVarietyTradeIntroduce() {
        Client.getVarietyTradeIntroduce(mVariety.getVarietyId())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<FutureIntroduce>, FutureIntroduce>() {
                    @Override
                    protected void onRespSuccessData(FutureIntroduce data) {
                        updateFutureIntroduce(data);
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fireFree();
    }

    private void updateFutureIntroduce(FutureIntroduce data) {
        mTradeCategory.setText(StrFormatter.getFormatText(data.getVarietyName()));
        mTradeCode.setText(StrFormatter.getFormatText(String.valueOf(data.getVarietyType())));
        mTradeTimeSummerWinter.setText(StrFormatter.getFormatText(data.getTradeTime()));
        mHoldingTime.setText(StrFormatter.getFormatText(data.getOpsitionTime()));
        mTradeUnit.setText(StrFormatter.getFormatText(data.getTradeUnit()));
        mQuoteUnit.setText(StrFormatter.getFormatText(data.getReportPriceUnit()));
        mLowestMargin.setText(StrFormatter.getFormatText(data.getLowestMargin()));
        mTradeType.setText(StrFormatter.getFormatText(data.getTradeType()));
        mTradeSystem.setText(StrFormatter.getFormatText(data.getTradeRegime()));
        mDeliveryTime.setText(StrFormatter.getFormatText(data.getDeliveryTime()));
        mDailyPriceMaximumVolatilityLimit.setText(StrFormatter.getFormatText(data.getEverydayPriceMaxFluctuateLimit()));
    }

    private void updateTradeStatus(FutureTradeStatus data) {
        if (data.getFurtureDealStatus() == FutureTradeStatus.ALLOW_TRADE) {
            mTradeFloatButtons.setHasTradeButton(true);
        } else {
            mTradeFloatButtons.setHasTradeButton(false);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        MarketSubscriber.get().unSubscribe(mVariety.getContractsCode());
        MarketSubscriber.get().removeDataReceiveListener(mDataReceiveListener);
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TimerHandler.TREND_REFRESH_TIME == 0) {
            requestTrendDataAndSet();
        }
    }

    private void requestExchangeStatus() {
        Client.getExchangeStatus(mVariety.getExchangeId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Integer>, Integer>() {
                    @Override
                    protected void onRespSuccessData(Integer data) {
                        int exchangeStatus = (data != null ?
                                data.intValue() : mVariety.getExchangeStatus());
                        mVariety.setExchangeStatus(exchangeStatus);
                        updateExchangeStatusView();
                    }
                }).fireFree();
    }

    private void initData() {
        mVariety = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
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
        TrendView.Settings settings = new TrendView.Settings();
        settings.setBaseLines(5);
        settings.setNumberScale(mVariety.getPriceScale());
        settings.setOpenMarketTimes(mVariety.getOpenMarketTime());
        settings.setDisplayMarketTimes(mVariety.getDisplayMarketTimes());
        settings.setLimitUpPercent((float) mVariety.getLimitUpPercent());
        settings.setCalculateXAxisFromOpenMarketTime(true);
        mTrendView.setSettings(settings);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(5);
        settings2.setNumberScale(mVariety.getPriceScale());
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        mKlineView.setSettings(settings2);
        mKlineView.setOnReachBorderListener(new KlineView.OnReachBorderListener() {
            @Override
            public void onReachLeftBorder(KlineViewData theLeft, List<KlineViewData> dataList) {
                requestKlineDataAndAdd(theLeft);
            }

            @Override
            public void onReachRightBorder(KlineViewData theRight, List<KlineViewData> dataList) {

            }
        });
    }

    private void requestKlineDataAndAdd(KlineViewData data) {
        String endTime = Uri.encode(data.getTime());
        String type = (String) mKlineView.getTag();
        Client.getKlineData(mVariety.getContractsCode(), type, endTime)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<KlineViewData>>, List<KlineViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<KlineViewData> data) {
                        if (data != null && !data.isEmpty()) {
                            Collections.reverse(data);
                            mKlineView.addHistoryData(data);
                        } else {
                            ToastUtil.show(R.string.there_is_no_more_data);
                        }
                    }
                }).fireFree();
    }

    private void requestTrendDataAndSet() {
        Client.getTrendData(mVariety.getContractsCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TrendViewData>>, List<TrendViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<TrendViewData> data) {
                        mTrendView.setDataList(data);
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return true;
                    }
                }).fireFree();
    }

    private void initFloatBar() {
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
                umengEventCount(UmengCountEventId.FIND_FUTURE_TRADE);
                TradeOptionDialogFragment.newInstance().show(getSupportFragmentManager());
            }
        });
    }


    private void checkOptionalStatus() {
        if (mTradeFloatButtons.isHasAddInOptional()) {
            requestDeleteOptional();
        } else {
            requestAddOptional();
        }
    }

    private void requestPrediction() {
        Client.getPrediction(mVariety.getBigVarietyTypeCode(), mVariety.getVarietyId())
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Prediction>, Prediction>() {
                    @Override
                    protected void onRespSuccessData(Prediction data) {
                        mPrediction = data;
                        if (mPrediction.isCalculate()) {
                        }
                    }
                }).fire();
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
                    }).fire();
        }
    }

    private void requestAddOptional() {
        umengEventCount(UmengCountEventId.DISCOVERY_ADD_SELF_OPTIONAL);
        Client.addOption(mVariety.getVarietyId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mTradeFloatButtons.setHasAddInOption(true);
                            CustomToast.getInstance().showText(FutureTradeActivity.this, R.string.add_option_succeed);
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
                            mTradeFloatButtons.setHasAddInOption(true);
                        }
                    }
                })
                .fire();
    }

    private void requestDeleteOptional() {
        SmartDialog.with(getActivity(), getString(R.string.whether_to_cancel_optional), getString(R.string.hint))
                .setMessageTextSize(15)
                .setPositive(R.string.yes, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        Client.delOptional(mVariety.getVarietyId())
                                .setTag(TAG)
                                .setIndeterminate(FutureTradeActivity.this)
                                .setCallback(new Callback<Resp<JsonObject>>() {
                                    @Override
                                    protected void onRespSuccess(Resp<JsonObject> resp) {
                                        if (resp.isSuccess()) {
                                            mTradeFloatButtons.setHasAddInOption(false);
                                            CustomToast.getInstance().showText(FutureTradeActivity.this, R.string.delete_option_succeed);
                                            sendAddOptionalBroadCast(mVariety, false);
                                        } else {
                                            ToastUtil.show(resp.getMsg());
                                        }
                                    }
                                })
                                .fire();
                        dialog.dismiss();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.primaryText))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .setNegative(R.string.no)
                .show();
    }

    private void sendAddOptionalBroadCast(Variety variety, Boolean isAddOptional) {
        Intent intent = new Intent();
        intent.setAction(OptionalActivity.OPTIONAL_CHANGE_ACTION);
        intent.putExtra(Launcher.EX_PAYLOAD, variety);
        intent.putExtra(Launcher.EX_PAYLOAD_1, isAddOptional);
        LocalBroadcastManager.getInstance(getContext()).sendBroadcastSync(intent);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String tabText = tab.getText().toString();
            if (tabText.equals(getString(R.string.day_k_line))) {
                requestKlineDataAndSet(null);
                showKlineView();
            } else if (tabText.equals(getString(R.string.sixty_min_k))) {
                requestKlineDataAndSet("60");
                showKlineView();
            } else if (tabText.equals(getString(R.string.thirty_min_k))) {
                requestKlineDataAndSet("30");
                showKlineView();
            } else if (tabText.equals(getString(R.string.fifteen_min_k))) {
                requestKlineDataAndSet("15");
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
        mTrendView.setVisibility(View.VISIBLE);
        mKlineView.setVisibility(View.GONE);
    }

    private void showKlineView() {
        mTrendView.setVisibility(View.GONE);
        mKlineView.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UMShareAPI.get(this).release();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }

    private void requestKlineDataAndSet(final String type) {
        mKlineView.setTag(type);
        mKlineView.clearData();
        Client.getKlineData(mVariety.getContractsCode(), type, null)
                .setTag(TAG).setIndeterminate(this)
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

    private DataReceiveListener mDataReceiveListener = new DataReceiveListener<Resp<FutureData>>() {
        @Override
        public void onDataReceive(Resp<FutureData> data) {
            if (data.getCode() == REQ_QUOTA && data.hasData()) {
                mFutureData = data.getData();
                updateMarketDataView(mFutureData);
                updateChartView(mFutureData);

                int exchangeStatus = mVariety.getExchangeStatus();
                if (exchangeStatus == Variety.EXCHANGE_STATUS_CLOSE) {
                    requestExchangeStatus();
                }
            }
        }
    };

    private void updateChartView(FutureData futureData) {
        List<TrendViewData> dataList = mTrendView.getDataList();
        if (dataList != null && dataList.size() > 0) {
            TrendViewData lastData = dataList.get(dataList.size() - 1);
            String date = DateUtil.addOneMinute(lastData.getTime(), TrendViewData.DATE_FORMAT);
            String hhmm = DateUtil.format(date, TrendViewData.DATE_FORMAT, "HH:mm");
            TrendView.Settings settings = mTrendView.getSettings();
            if (TrendView.Util.isValidDate(hhmm, settings.getOpenMarketTimes())) {
                float lastPrice = (float) futureData.getLastPrice();
                TrendViewData unstableData = new TrendViewData(lastPrice, date);
                mTrendView.setUnstableData(unstableData);
            }
        }
    }

    private void updateMarketDataView(FutureData data) {
        mLastPrice.setText(FinanceUtil.formatWithScale(data.getLastPrice(), mVariety.getPriceScale()));
        double priceChange = FinanceUtil.subtraction(data.getLastPrice(), data.getPreSetPrice()).doubleValue();
        double priceChangePercent = FinanceUtil.divide(priceChange, data.getPreSetPrice(), 4)
                .multiply(new BigDecimal(100)).doubleValue();

        mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
        mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
        String priceChangeStr = FinanceUtil.formatWithScale(priceChange, mVariety.getPriceScale());
        String priceChangePercentStr = FinanceUtil.formatWithScale(priceChangePercent) + "%";
        if (priceChange >= 0) {
            priceChangeStr = "+" + priceChangeStr;
            priceChangePercentStr = "+" + priceChangePercentStr;
            mLastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
            mPriceChange.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
        }
        mPriceChange.setText(priceChangeStr + "     " + priceChangePercentStr);

        mTodayOpen.setText(FinanceUtil.formatWithScale(data.getOpenPrice(), mVariety.getPriceScale()));
        mHighest.setText(FinanceUtil.formatWithScale(data.getHighestPrice(), mVariety.getPriceScale()));
        mLowest.setText(FinanceUtil.formatWithScale(data.getLowestPrice(), mVariety.getPriceScale()));
        mPreClose.setText(FinanceUtil.formatWithScale(data.getPreClsPrice(), mVariety.getPriceScale()));
    }

    private void updateExchangeStatusView() {
        int exchangeStatus = mVariety.getExchangeStatus();
        if (exchangeStatus == Variety.EXCHANGE_STATUS_CLOSE) {
            updateTitleBar(getString(R.string.market_close));
        } else {
            updateTitleBar(getString(R.string.market_trading));
        }
    }

    private void initTitleBar() {
        final String shareUrl = String.format(Client.SHARE_URL_FUTURE, mVariety.getVarietyId());
        final String shareTitle = getString(R.string.wonderful_viewpoint, mVariety.getVarietyName());
        final String shareDescribe = getString(R.string.share_desc);
        // 先隐藏分享按钮点击事件
//        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                umengEventCount(UmengCountEventId.FIND_FUTURE_SHARE);
//                ShareDialogFragment
//                        .newInstance()
//                        .setShareContent(shareTitle, shareDescribe, shareUrl)
//                        .setListener(new ShareDialogFragment.OnShareDialogCallback() {
//                            @Override
//                            public void onSharePlatformClick(ShareDialogFragment.SHARE_PLATFORM platform) {
//                                switch (platform) {
//                                    case SINA_WEIBO:
//                                        umengEventCount(UmengCountEventId.FIND_FUTURE_SHARE_SINA);
//                                        break;
//                                    case WECHAT_FRIEND:
//                                        umengEventCount(UmengCountEventId.FIND_FUTURE_SHARE_FRIEND);
//                                        break;
//                                    case WECHAT_CIRCLE:
//                                        umengEventCount(UmengCountEventId.FIND_FUTURE_SHARE_CIRCLE);
//                                        break;
//                                }
//                            }
//                        })
//                        .show(getSupportFragmentManager());
//            }
//        });

        updateTitleBar(null);
    }

    private void updateTitleBar(String exchangeStatus) {
        View customView = mTitleBar.getCustomView();
        TextView productName = (TextView) customView.findViewById(R.id.productName);
        TextView productType = (TextView) customView.findViewById(R.id.productType);
        productName.setText(mVariety.getVarietyName() + " (" + mVariety.getContractsCode() + ")");
        String productTypeStr = getString(R.string.future_china);
        if (mVariety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.FUTURE_FOREIGN)) {
            productTypeStr = getString(R.string.future_foreign);
        }
        if (!TextUtils.isEmpty(exchangeStatus)) {
            productTypeStr += "-" + exchangeStatus;
        }
        productType.setText(productTypeStr);
    }

}
