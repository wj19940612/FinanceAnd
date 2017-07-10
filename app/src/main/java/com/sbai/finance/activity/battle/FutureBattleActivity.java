package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.chart.domain.TrendViewData;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.fragment.battle.FutureBattleDetailFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.TradeOrder;
import com.sbai.finance.model.battle.TradeOrderClosePosition;
import com.sbai.finance.model.battle.TradeRecord;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.HackTabLayout;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSPush;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.model.battle.Battle.GAME_STATUS_CREATED;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_END;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_STARTED;
import static com.sbai.finance.model.battle.TradeOrder.DIRECTION_LONG_PURCHASE;
import static com.sbai.finance.model.battle.TradeOrder.DIRECTION_SHORT_PURCHASE;
import static com.sbai.finance.view.BattleFloatView.Mode.MINE;
import static com.sbai.finance.view.BattleTradeView.STATE_CLOSE_POSITION;
import static com.sbai.finance.view.BattleTradeView.STATE_TRADE;


/**
 * Created by linrongfang on 2017/7/7.
 */

public class FutureBattleActivity extends BaseActivity implements BattleButtons.OnViewClickListener,BattleTradeView.OnViewClickListener{

    public static final String PAGE_TYPE = "page_type";
    //0 对战记录 1 对战中
    public static final int PAGE_TYPE_RECORD = 0;
    public static final int PAGE_TYPE_BATTLE = 1;

    @BindView(R.id.content)
    LinearLayout mContent;

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

    @BindView(R.id.loading)
    ImageView mLoading;
    @BindView(R.id.loadingContent)
    LinearLayout mLoadingContent;

    @BindView(R.id.battleView)
    BattleFloatView mBattleView;


    private int mBattleId;
    private String mBatchCode;

    private Battle mBattle;
    private Variety mVariety;
    private FutureData mFutureData;
    private int mPageType = -1;
    private boolean mIsObserver;
    private int mGameStatus;

    private TradeOrder mCurrentOrder;
    private TradeOrder mCreatorOrder;
    private TradeOrder mAgainstOrder;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);
        initData();

    }

    private void initData() {
        mBattleId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mBatchCode = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);

        requestLastBattleInfo(mBattleId, mBatchCode);
    }

    private void requestLastBattleInfo(int battleId, String batchCode) {
        Client.getBattleInfo(battleId, batchCode).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        if (data != null) {
                            mBattle = data;
                            if (data.isBattleStop()) {
                                mPageType = PAGE_TYPE_RECORD;
                                initBattleRecordPage();
                            } else {
                                mPageType = PAGE_TYPE_BATTLE;
                                initBattlePage();
                            }

                        }
                    }
                }).fire();
    }

    private void initBattleRecordPage() {
        //只加载一个详情的Fragment
        mContent.removeAllViews();
        FutureBattleDetailFragment fragment = FutureBattleDetailFragment.newInstance(mBattle);

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, fragment)
                .commitAllowingStateLoss();

        mBattleView.setMode(MINE)
                .initWithModel(mBattle)
                .setDeadline(mBattle.getGameStatus(), 0)
                .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                .setWinResult(mBattle.getWinResult());
    }

    private void initBattlePage() {
        //处理从通知栏点进来的
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(FutureBattleActivity.this, MainActivity.class).execute();
            finish();
        }

        initTabLayout();

        //判断是否是观战
        mIsObserver = checkUserIsObserver();
        //获取游戏状态
        mGameStatus = mBattle.getGameStatus();

        if (mIsObserver) {
            showObserverView();
            requestOrderHistory();
            requestCurrentOrder();
        } else {
            if (mGameStatus == GAME_STATUS_CREATED) {
                showBattleButtons();
                updateRoomExistsTime();
            } else if (mGameStatus == GAME_STATUS_STARTED) {
                showBattleTradeView();
                requestOrderHistory();
                requestCurrentOrder();
            }
        }

        initBattleViews();

        requestVarietyData();

    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.day_k_line));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.sixty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.thirty_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.fifteen_min_k));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
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

    private void showObserverView() {
        showBattleTradeView();
        mBattleTradeView.setObserver(true);
    }

    private void showBattleTradeView() {
        //显示交易视图 区分游客和对战者 默认对战者
        mBattleButtons.setVisibility(View.GONE);
        mBattleTradeView.setVisibility(View.VISIBLE);
    }

    private void showBattleButtons() {
        //显示邀请等三个按钮 只针对游戏创建者
        mBattleButtons.setVisibility(View.VISIBLE);
        mBattleTradeView.setVisibility(View.GONE);
    }

    private void requestOrderHistory() {
        Client.getOrderHistory(mBattle.getId())
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeRecord>>, List<TradeRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeRecord> data) {
                        updateTradeHistory(data);
                    }
                }).fire();
    }

    private void updateTradeHistory(List<TradeRecord> resp) {
        mBattleTradeView.addTradeData(resp, mBattle.getLaunchUser(), mBattle.getAgainstUser());
    }

    private void requestCurrentOrder() {
        Client.requestCurrentOrder(mBattle.getId())
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeOrder>>, List<TradeOrder>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeOrder> data) {
                        updateCurrentOrder(data);
                    }
                }).fire();
    }

    private void updateCurrentOrder(List<TradeOrder> data) {
        TradeOrder currentOrder = null;
        TradeOrder creatorOrder = null;
        TradeOrder againstOrder = null;
        for (TradeOrder tradeOrder : data) {
            if (tradeOrder.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                currentOrder = tradeOrder;
            }
            if (tradeOrder.getUserId() == mBattle.getLaunchUser()) {
                creatorOrder = tradeOrder;
            }
            if (tradeOrder.getUserId() == mBattle.getAgainstUser()) {
                againstOrder = tradeOrder;
            }
        }
        if (currentOrder != null) {
            mCurrentOrder = currentOrder;
            setBattleTradeState(STATE_CLOSE_POSITION);
            if (mVariety != null) {
                mBattleTradeView.setTradeData(mCurrentOrder.getDirection(),
                        FinanceUtil.formatWithScale(mCurrentOrder.getOrderPrice(), mVariety.getPriceScale()), 0);
            } else {
                mBattleTradeView.setTradeData(mCurrentOrder.getDirection(),
                        String.valueOf(mCurrentOrder.getOrderPrice()), 0);
            }
        } else {
            setBattleTradeState(STATE_TRADE);
        }

        if (creatorOrder != null) {
            mCreatorOrder = creatorOrder;
        } else {
            mCreatorOrder = null;
        }

        if (againstOrder != null) {
            mAgainstOrder = againstOrder;
        } else {
            mAgainstOrder = null;
        }
    }

    private void setBattleTradeState(int state) {
        //切换交易视图显示模式
        mBattleTradeView.changeTradeState(state);
    }

    private void initBattleViews() {
        mBattleButtons.setOnViewClickListener(this);
        mBattleTradeView.setOnViewClickListener(this);
        scrollToTop(mTitleBar, mBattleTradeView.getListView());
    }

    private void requestVarietyData() {
        Client.requsetVarietyPrice(mBattle.getVarietyId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Variety>, Variety>() {
                    @Override
                    protected void onRespSuccessData(Variety variety) {
                        mVariety = variety;
                        initChartViews();
                        showTrendView();
                        startSubscribeFutureData();
                    }
                }).fire();
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

    private boolean checkUserIsObserver() {
        int userId = LocalUser.getUser().getUserInfo().getId();
        if (mBattle.getLaunchUser() != userId
                && mBattle.getAgainstUser() != userId) {
            return true;
        }
        return false;
    }


    @Override
    protected void onBattlePushReceived(WSPush<Battle> push) {
        super.onBattlePushReceived(push);
        //对战详情只能收到有人加入推送
        if (mPageType == PAGE_TYPE_RECORD) {
            if (push.getContent().getType() == PushCode.BATTLE_JOINED) {
                if (push.getContent() != null) {
                    Battle battle = (Battle) push.getContent().getData();
                    showQuickJoinBattleDialog(battle);
                }
            }
            return;
        }

        switch (push.getContent().getType()) {
            case PushCode.BATTLE_JOINED:
                //观战中 不可以弹出有人加入
                //初始化底部栏  取消一切弹窗 显示交易视图 开始计时
                break;
            case PushCode.BATTLE_OVER:
                //对战结束 一个弹窗
                break;
            case PushCode.ORDER_CREATED:
                break;
            case PushCode.ORDER_CLOSE:
                break;
            case PushCode.QUICK_MATCH_TIMEOUT:
                //匹配超时逻辑 只有在快速匹配的情况下才会匹配超时
                break;
            case PushCode.ROOM_CREATE_TIMEOUT:
                //房间创建超时
                break;
            case PushCode.USER_PRAISE:
                break;

        }

    }

    @Override
    protected void showQuickJoinBattleDialog(final Battle battle) {
        //只有在自己是房主的情况下才显示
        if (LocalUser.getUser().isLogin()) {
            boolean isRoomCreator = battle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId();
            if (isRoomCreator) {
                SmartDialog.single(getActivity(), getString(R.string.quick_join_battle))
                        .setTitle(getString(R.string.join_battle))
                        .setPositive(R.string.quick_battle, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                Launcher.with(getActivity(), BattleActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD_1, battle.getId())
                                        .putExtra(Launcher.EX_PAYLOAD_2, battle.getBatchCode())
                                        .execute();
                                finish();
                            }
                        }).setNegative(R.string.cancel)
                        .show();
            }
        }
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
            myProfit = myProfit * mVariety.getEachPointMoney();
            mBattleTradeView.setTradeData(mCurrentOrder.getDirection()
                    , FinanceUtil.formatWithScale(mCurrentOrder.getOrderPrice(), mVariety.getPriceScale()), myProfit);
        }
        //房主的累计收益
        if (mCreatorOrder != null) {
            if (mCreatorOrder.getDirection() == 1) {
                creatorProfit = futureData.getLastPrice() - mCreatorOrder.getOrderPrice();
            } else {
                creatorProfit = mCreatorOrder.getOrderPrice() - futureData.getLastPrice();
            }
            creatorProfit = creatorProfit * mVariety.getEachPointMoney();
        }
        //对抗者的累计收益
        if (mAgainstOrder != null) {
            if (mAgainstOrder.getDirection() == 1) {
                againstProfit = futureData.getLastPrice() - mAgainstOrder.getOrderPrice();
            } else {
                againstProfit = mAgainstOrder.getOrderPrice() - futureData.getLastPrice();
            }
            againstProfit = againstProfit * mVariety.getEachPointMoney();
        }
        updateBattleInfo(creatorProfit, againstProfit);

    }

    public void updateBattleInfo(double createProfit, double againstProfit) {
        double leftProfit = createProfit;
        double rightProfit = againstProfit;
        boolean isInviting = mBattle.getGameStatus() == GAME_STATUS_CREATED;
        if (mBattle != null) {
            leftProfit += mBattle.getLaunchUnwindScore();
            rightProfit += mBattle.getAgainstUnwindScore();
        }
        if (mBattle.getGameStatus() != GAME_STATUS_END) {
            mBattleView.setProgress(leftProfit, rightProfit, isInviting);
        }
    }


    @Override
    public void onInviteButtonClick() {

    }

    @Override
    public void onMatchButtonClick() {

    }

    @Override
    public void onCancelButtonClick() {

    }

    @Override
    public void onLongPurchaseButtonClick() {
        requestCreateOrder(DIRECTION_LONG_PURCHASE);
    }

    @Override
    public void onShortPurchaseButtonClick() {
        requestCreateOrder(DIRECTION_SHORT_PURCHASE);
    }

    @Override
    public void onClosePositionButtonClick() {
        requestClosePosition(mCurrentOrder.getId());
    }

    private void requestCreateOrder(int direction) {
        Client.createOrder(mBattle.getId(), direction)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<TradeOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrder> resp) {
                    }

                    @Override
                    protected void onToastErrorMessage(String msg) {
                    }
                })
                .fire();
    }

    private void requestClosePosition(int orderId) {
        Client.closePosition(mBattle.getId(), orderId)
                .setTag(TAG)
                .setCallback(new Callback<Resp<TradeOrderClosePosition>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrderClosePosition> resp) {
                    }

                    @Override
                    protected void onToastErrorMessage(String msg) {
                    }
                })
                .fire();
    }

    private void startSubscribeFutureData() {
        if (mVariety != null) {
            startScheduleJob(1000);
            Netty.get().subscribe(Netty.REQ_SUB, mVariety.getContractsCode());
            Netty.get().addHandler(mNettyHandler);

            requestExchangeStatus();
            requestTrendDataAndSet();
        }
    }

    private void stopSubscribeFutureData() {
        if (mVariety != null) {
            stopScheduleJob();
            Netty.get().subscribe(Netty.REQ_UNSUB, mVariety.getContractsCode());
            Netty.get().removeHandler(mNettyHandler);
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

    public void updateRoomExistsTime() {
        //更新房间存在倒计时
        long currentTime = SysTime.getSysTime().getSystemTimestamp();
        long createTime = mBattle.getCreateTime();
        int diff = DateUtil.getDiffSeconds(currentTime, createTime);
        if (mBattleButtons.getVisibility() == View.VISIBLE) {
            mBattleButtons.updateCountDownTime(DateUtil.getCountdownTime(600, diff));
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopSubscribeFutureData();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        startSubscribeFutureData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
    }
}
