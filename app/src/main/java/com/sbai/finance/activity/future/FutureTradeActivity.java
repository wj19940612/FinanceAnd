package com.sbai.finance.activity.future;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
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
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.trade.PublishOpinionActivity;
import com.sbai.finance.fragment.dialog.PredictionDialogFragment;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
import com.sbai.finance.fragment.dialog.TradeOptionDialogFragment;
import com.sbai.finance.fragment.trade.IntroduceFragment;
import com.sbai.finance.fragment.trade.ViewpointFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Prediction;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.DateUtil;
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
import com.umeng.socialize.UMShareAPI;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.fragment.trade.ViewpointFragment.REQ_CODE_ATTENTION;
import static com.sbai.finance.view.TradeFloatButtons.HAS_ADD_OPITION;

public class FutureTradeActivity extends BaseActivity implements PredictionDialogFragment.OnPredictButtonListener {
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
    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.exchangeCloseView)
    TextView mExchangeCloseView;
    @BindView(R.id.priceDataArea)
    LinearLayout mPriceDataArea;

    private PredictionDialogFragment mPredictionFragment;
    private SubPageAdapter mSubPageAdapter;
    private Variety mVariety;
    private Prediction mPrediction;
    private FutureData mFutureData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_trade);
        ButterKnife.bind(this);

        initData();

        initTabLayout();
        initChartViews();
        initSlidingTab();
        initFloatBar();

        updateTitleBar();
        updateExchangeStatusView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Netty.get().subscribe(Netty.REQ_SUB, mVariety.getContractsCode());
        Netty.get().addHandler(mNettyHandler);

        requestExchangeStatus();
        requestOptionalStatus();

        requestTrendDataAndSet();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Netty.get().subscribe(Netty.REQ_UNSUB, mVariety.getContractsCode());
        Netty.get().removeHandler(mNettyHandler);
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
                }).fireSync();
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
        mKlineView.setOnAchieveTheLastListener(null);
    }

    private void requestTrendDataAndSet() {
        Client.getTrendData(mVariety.getContractsCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TrendViewData>>, List<TrendViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<TrendViewData> data) {
                        mTrendView.setDataList(data);
                    }
                }).fireSync();
    }

    private void initSlidingTab() {
        mViewPager.setOffscreenPageLimit(1);
        mSubPageAdapter = new SubPageAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mSubPageAdapter);
        mViewPager.addOnPageChangeListener(mSubPageChangeListener);

        mSlidingTab.setDistributeEvenly(true);
        mSlidingTab.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTab.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mSlidingTab.setPadding(Display.dp2Px(12, getResources()));
        mSlidingTab.setViewPager(mViewPager);
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
                if (LocalUser.getUser().isLogin()) {
                    TradeOptionDialogFragment.newInstance().show(getSupportFragmentManager());
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
        });
    }

    @Override
    public void onBullishButtonClick(int directionLong) {
        mPrediction.setDirection(directionLong);
        startPublishPointPage();
    }

    @Override
    public void onBearishButtonClick(int directionShort) {
        mPrediction.setDirection(directionShort);
        startPublishPointPage();
    }

    private void startPublishPointPage() {
        Launcher.with(getActivity(), PublishOpinionActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, mVariety)
                .putExtra(Launcher.EX_PAYLOAD_1, mPrediction)
                .putExtra(Launcher.EX_PAYLOAD_2, mFutureData)
                .executeForResult(REQ_CODE_PUBLISH);
    }

    private void showPredictDialog() {
        if (mPredictionFragment == null) {
            mPredictionFragment = PredictionDialogFragment.newInstance().setOnPredictButtonListener(this);
        }
        mPredictionFragment.show(getSupportFragmentManager());
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
                            startPublishPointPage();
                        } else {
                            showPredictDialog();
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
        Client.addOption(mVariety.getVarietyId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            mTradeFloatButtons.setHasAddInOption(true);
                            CustomToast.getInstance().showText(FutureTradeActivity.this, R.string.add_option_succeed);
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }

                    @Override
                    protected void onReceive(Resp<JsonObject> resp) {
                        super.onReceive(resp);
                        // 701 代表已经添加过
                        if (resp.getCode() == Resp.CODE_REPEAT_ADD) {
                            mTradeFloatButtons.setHasAddInOption(true);
                        }
                    }
                })
                .fire();
    }

    private void requestDeleteOptional() {
        SmartDialog.with(getActivity(), getString(R.string.whether_to_cancel_optional))
                .setMessageTextSize(15)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
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


    private ViewpointFragment getViewpointFragment() {
        Fragment fragment = mSubPageAdapter.getFragment(0);
        if (fragment instanceof ViewpointFragment) {
            return (ViewpointFragment) (fragment);
        }
        return null;
    }

    private IntroduceFragment getIntroduceFragment() {
        Fragment fragment = mSubPageAdapter.getFragment(1);
        if (fragment instanceof IntroduceFragment) {
            return (IntroduceFragment) (fragment);
        }
        return null;
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
                    return mContext.getString(R.string.introduce);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ViewpointFragment.newInstance(mVariety.getVarietyId());
                case 1:
                    return IntroduceFragment.newInstance(mVariety);
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

    private ViewPager.OnPageChangeListener mSubPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            if (position == 0) {
                if (getViewpointFragment() != null) {
                    getViewpointFragment().refreshPointList();
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

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

    private NettyHandler mNettyHandler = new NettyHandler<Resp<FutureData>>() {
        @Override
        public void onReceiveData(Resp<FutureData> data) {
            if (data.getCode() == Netty.REQ_QUOTA && data.hasData()) {
                mFutureData = data.getData();
                updateMarketDataView(mFutureData);
                updateChartView(mFutureData);
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
            mExchangeCloseView.setVisibility(View.VISIBLE);
            mPriceDataArea.setVisibility(View.GONE);
            mTodayOpen.setText("--");
        } else {
            mExchangeCloseView.setVisibility(View.GONE);
            mPriceDataArea.setVisibility(View.VISIBLE);
        }
    }

    private void updateTitleBar() {
        View customView = mTitleBar.getCustomView();
        TextView productName = (TextView) customView.findViewById(R.id.productName);
        TextView productType = (TextView) customView.findViewById(R.id.productType);
        productName.setText(mVariety.getVarietyName() + " (" + mVariety.getContractsCode() + ")");
        String productTypeStr = getString(R.string.future_china);
        if (mVariety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.FUTURE_FOREIGN)) {
            productTypeStr = getString(R.string.future_foreign);
        }
        productType.setText(productTypeStr);

        final String shareUrl = String.format(Client.FUTURE_SHARE_URL, mVariety.getVarietyId());
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ShareDialogFragment
                        .newInstance()
                        .setShareContent(FutureTradeActivity.this, mVariety.getVarietyName(), shareUrl)
                        .show(getSupportFragmentManager());
            }
        });
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ViewpointFragment viewpointFragment = getViewpointFragment();
                if (viewpointFragment != null) {
                    viewpointFragment.scrollToTop();
                }
                IntroduceFragment introduceFragment = getIntroduceFragment();
                if (introduceFragment != null) {
                    introduceFragment.scrollToTop();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);
        if (resultCode == FragmentActivity.RESULT_OK) {
            if (requestCode == REQ_CODE_USERDATA || requestCode == REQ_CODE_ATTENTION || requestCode == REQ_CODE_PUBLISH) {
                updateViewPoint(data);
            }
        }
    }

    private void updateViewPoint(Intent intent) {
        OpinionDetails details = (OpinionDetails) intent.getSerializableExtra(Launcher.EX_PAYLOAD);

        WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
                (WhetherAttentionShieldOrNot) intent.getSerializableExtra(Launcher.EX_PAYLOAD_1);

        AttentionAndFansNumberModel attentionAndFansNumberModel =
                (AttentionAndFansNumberModel) intent.getSerializableExtra(Launcher.EX_PAYLOAD_2);
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

}
