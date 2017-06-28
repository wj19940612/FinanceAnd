package com.sbai.finance.fragment.future;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.chart.domain.TrendViewData;
import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureBattleActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.versus.TradeOrder;
import com.sbai.finance.model.versus.TradeOrderClosePosition;
import com.sbai.finance.model.versus.TradeRecord;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.HackTabLayout;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_CREATED;
import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_STARTED;
import static com.sbai.finance.view.BattleTradeView.STATE_CLOSE_POSITION;
import static com.sbai.finance.view.BattleTradeView.STATE_TRADE;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class FutureBattleFragment extends BaseFragment {

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
    HackTabLayout mTabLayout;
    @BindView(R.id.trendView)
    TrendView mTrendView;
    @BindView(R.id.klineView)
    KlineView mKlineView;

    @BindView(R.id.battleButtons)
    BattleButtons mBattleButtons;
    @BindView(R.id.battleTradeView)
    BattleTradeView mBattleTradeView;

    Unbinder unbinder;

    private VersusGaming mVersusGaming;
    private Variety mVariety;
    private FutureData mFutureData;
    private TradeOrder mCurrentOrder;
    private TradeOrder mCreatorOrder;
    private TradeOrder mAgainstOrder;

    private int mCount = 600;
    private int mGameStatus;


    public static FutureBattleFragment newInstance(VersusGaming versusGaming) {
        FutureBattleFragment futureBattleFragment = new FutureBattleFragment();
        Bundle bundle = new Bundle();
        bundle.putParcelable("versusGaming", versusGaming);
        futureBattleFragment.setArguments(bundle);
        return futureBattleFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mVersusGaming = (VersusGaming) getArguments().get("versusGaming");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future_battle, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initBattleArea();

        initTabLayout();

        initBattleViews();

        requestVarietyData();
    }

    private void initBattleArea() {
        //判断是否是观战 观战条件 房主id!=本地id && 对抗者id!=本地id
        //观战情况下 对战区域只显示对战数据
        int userId = LocalUser.getUser().getUserInfo().getId();
        if (mVersusGaming.getAgainstUser() != userId && mVersusGaming.getLaunchUser() != userId) {
            showBattleTradeView();
            setVisitorMode();
            requestOrderHistory();
        } else {
            //判断状态是否在对抗中
            //未开始显示邀请 匹配  取消  视图
            if (mVersusGaming.getGameStatus() == GAME_STATUS_CREATED) {
                mGameStatus = GAME_STATUS_CREATED;
                showBattleButtons();
                updateRoomExistsTime();
            } else if (mVersusGaming.getGameStatus() == GAME_STATUS_STARTED) {
                showBattleTradeView();
                requestOrderHistory();
                requestCurrentOrder();
            }
        }
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
        settings.setGameMode(true);
        mTrendView.setSettings(settings);

        KlineChart.Settings settings2 = new KlineChart.Settings();
        settings2.setBaseLines(5);
        settings2.setNumberScale(mVariety.getPriceScale());
        settings2.setXAxis(40);
        settings2.setIndexesType(KlineChart.Settings.INDEXES_VOL);
        settings2.setGameMode(true);
        mKlineView.setSettings(settings2);
        mKlineView.setOnAchieveTheLastListener(null);
    }

    private void initBattleViews() {
        mBattleButtons.setOnViewClickListener(new BattleButtons.OnViewClickListener() {
            @Override
            public void onInviteButtonClick() {
                ((FutureBattleActivity)getActivity()).onInviteButtonClick();
            }

            @Override
            public void onMatchButtonClick() {
                ((FutureBattleActivity)getActivity()).onMatchButtonClick();
            }

            @Override
            public void onCancelButtonClick() {
                ((FutureBattleActivity)getActivity()).onCancelButtonClick();
            }
        });
        mBattleTradeView.setOnViewClickListener(new BattleTradeView.OnViewClickListener() {
            @Override
            public void onLongPurchaseButtonClick() {
                requestCreateOrder(1);
            }

            @Override
            public void onShortPurchaseButtonClick() {
                requestCreateOrder(0);
            }

            @Override
            public void onClosePositionButtonClick() {
                requestClosePosition(mCurrentOrder.getId());
            }
        });
    }

    private void requestCreateOrder(int direction) {
        Client.createOrder(mVersusGaming.getId(), direction)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<TradeOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrder> resp) {
                        refreshTradeView();
                    }
                })
                .fire();
    }

    private void requestClosePosition(int orderId) {
        Client.closePosition(mVersusGaming.getId(), orderId)
                .setTag(TAG)
                .setCallback(new Callback<Resp<TradeOrderClosePosition>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrderClosePosition> resp) {
                        refreshTradeView();
                    }
                })
                .fire();
    }

    private void requestVarietyData() {
        Client.getVarietyDetails(mVersusGaming.getVarietyId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Variety>, Variety>() {
                    @Override
                    protected void onRespSuccessData(Variety variety) {
                        mVariety = variety;
                        initChartViews();
                        showTrendView();
                        startRefresh();
                    }
                }).fire();
    }

    public void requestOrderHistory(){
        Client.getOrderHistory(mVersusGaming.getId())
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<TradeRecord>>,List<TradeRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeRecord> data) {
                        updateTradeHistory(data);
                    }
                })
                .fire();
    }

    private void requestCurrentOrder() {
        Client.requestCurrentOrder(mVersusGaming.getId())
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeOrder>>,List<TradeOrder>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeOrder> data) {
                        updateCurrentOrder(data);
                    }
                })
                .fire();
    }

    private void updateTradeHistory(List<TradeRecord> resp) {
        mBattleTradeView.addTradeData(resp, mVersusGaming.getLaunchUser(), mVersusGaming.getAgainstUser());
    }

    private void updateCurrentOrder(List<TradeOrder> data) {
        for (TradeOrder tradeOrder : data) {
            if (tradeOrder.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                mCurrentOrder = tradeOrder;
            }
            if (tradeOrder.getId() == mVersusGaming.getLaunchUser()) {
                mCreatorOrder = tradeOrder;
            }
            if (tradeOrder.getId() == mVersusGaming.getAgainstUser()) {
                mAgainstOrder = tradeOrder;
            }
        }
        if (mCurrentOrder != null) {
            setBattleTradeState(STATE_CLOSE_POSITION);
            mBattleTradeView.setTradeData(mCurrentOrder.getDirection(), mCurrentOrder.getOrderPrice(), 0);
        } else {
            setBattleTradeState(STATE_TRADE);
        }
        mGameStatus = GAME_STATUS_STARTED;
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

    private void requestTrendDataAndSet() {
        Client.getTrendData(mVariety.getContractsCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TrendViewData>>, List<TrendViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<TrendViewData> data) {
                        mTrendView.setDataList(data);
                    }
                }).fireFree();
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
                updateTradeProfit(mFutureData);

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

    private void updateTradeProfit(FutureData futureData) {
        double myProfit = 0;
        double creatorProfit = 0;
        double againstProfit = 0;
        //这里只更新对战者的持仓信息
        if (mCurrentOrder != null && mBattleTradeView.isShown() && mBattleTradeView.getTradeState() == STATE_CLOSE_POSITION) {

            if (mCurrentOrder.getDirection() == 1) {
                myProfit = futureData.getLastPrice() - mCurrentOrder.getOrderPrice();
            } else {
                myProfit = mCurrentOrder.getOrderPrice() - futureData.getLastPrice();
            }
            mBattleTradeView.setTradeData(mCurrentOrder.getDirection(), mCurrentOrder.getOrderPrice(), myProfit);
        }
        //房主的累计收益
        if (mCreatorOrder != null) {
            if (mCreatorOrder.getDirection() == 1) {
                creatorProfit = futureData.getLastPrice() - mCreatorOrder.getOrderPrice();
            } else {
                creatorProfit = mCreatorOrder.getOrderPrice() - futureData.getLastPrice();
            }
        }
        //对抗者的累计收益
        if (mAgainstOrder != null) {
            if (mAgainstOrder.getDirection() == 1) {
                againstProfit = futureData.getLastPrice() - mAgainstOrder.getOrderPrice();
            } else {
                againstProfit = mAgainstOrder.getOrderPrice() - futureData.getLastPrice();
            }
        }
        ((FutureBattleActivity)getActivity()).updateBattleInfo(creatorProfit,againstProfit);

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

    private void updateExchangeStatusView() {
        int exchangeStatus = mVariety.getExchangeStatus();
        if (exchangeStatus == Variety.EXCHANGE_STATUS_CLOSE) {
            updateTitleBar(getString(R.string.market_close));
        } else {
            updateTitleBar(getString(R.string.market_trading));
        }
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

    private void showTrendView() {
        mTrendView.setVisibility(View.VISIBLE);
        mKlineView.setVisibility(View.GONE);
    }

    private void showKlineView() {
        mTrendView.setVisibility(View.GONE);
        mKlineView.setVisibility(View.VISIBLE);
    }

    private void setVisitorMode() {
        mBattleTradeView.setVisitor(true);
    }

    public void updateRoomExistsTime(){
        //更新房间存在倒计时
        long currentTime = System.currentTimeMillis();
        long createTime = mVersusGaming.getCreateTime();
        int diff = DateUtil.getDiffSeconds(currentTime, createTime);
        if (mBattleButtons.getVisibility() == View.VISIBLE) {
            mBattleButtons.updateCountDownTime(DateUtil.getCountdownTime(mCount, diff));
        }
    }

    public void showBattleButtons() {
        //显示邀请等三个按钮 只针对对战者
        mBattleButtons.setVisibility(View.VISIBLE);
        mBattleTradeView.setVisibility(View.GONE);
    }

    public void showBattleTradeView() {
        //显示交易视图 区分游客和对战者 默认对战者
        mBattleButtons.setVisibility(View.GONE);
        mBattleTradeView.setVisibility(View.VISIBLE);
    }

    public void setBattleTradeState(int state) {
        //切换交易视图显示模式
        mBattleTradeView.changeTradeState(state);
    }

    public void refreshTradeView(){
        //每次点击交易或平仓后 刷新数据
        requestOrderHistory();
        requestCurrentOrder();
    }

    @Override
    public void onPause() {
        super.onPause();
        stopRefresh();
    }

    private void stopRefresh() {
        if (mVariety != null) {
            stopScheduleJob();
            Netty.get().subscribe(Netty.REQ_UNSUB, mVariety.getContractsCode());
            Netty.get().removeHandler(mNettyHandler);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        startRefresh();
    }

    private void startRefresh() {
        if (mVariety != null) {
            startScheduleJob(1000);
            Netty.get().subscribe(Netty.REQ_SUB, mVariety.getContractsCode());
            Netty.get().addHandler(mNettyHandler);

            requestExchangeStatus();
            requestTrendDataAndSet();
        }
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TimerHandler.TREND_REFRESH_TIME == 0) {
            requestTrendDataAndSet();
        }
        if (mGameStatus == GAME_STATUS_CREATED) {
            updateRoomExistsTime();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
        unbinder.unbind();
    }

}
