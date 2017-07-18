package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.chart.KlineChart;
import com.sbai.chart.KlineView;
import com.sbai.chart.TrendView;
import com.sbai.chart.domain.KlineViewData;
import com.sbai.chart.domain.TrendViewData;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.fragment.battle.BattleRecordsFragment;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
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
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.BaseDialog;
import com.sbai.finance.view.dialog.BattleResultDialog;
import com.sbai.finance.view.dialog.StartGameDialog;
import com.sbai.finance.view.dialog.StartMatchDialog;
import com.sbai.finance.view.slidingTab.HackTabLayout;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.WsClient;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.CancelBattle;
import com.sbai.finance.websocket.cmd.CurrentBattle;
import com.sbai.finance.websocket.cmd.QuickMatchLauncher;
import com.sbai.finance.websocket.cmd.SubscribeBattle;
import com.sbai.finance.websocket.cmd.UnSubscribeBattle;
import com.sbai.finance.websocket.cmd.UserPraise;
import com.sbai.httplib.ApiCallback;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.model.battle.Battle.GAME_STATUS_CANCELED;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_CREATED;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_END;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_STARTED;
import static com.sbai.finance.model.battle.BattleRoom.ROOM_STATE_CREATE;
import static com.sbai.finance.model.battle.BattleRoom.ROOM_STATE_START;
import static com.sbai.finance.model.battle.TradeOrder.DIRECTION_LONG_PURCHASE;
import static com.sbai.finance.model.battle.TradeOrder.DIRECTION_SHORT_PURCHASE;
import static com.sbai.finance.view.BattleTradeView.STATE_CLOSE_POSITION;
import static com.sbai.finance.view.BattleTradeView.STATE_TRADE;
import static com.sbai.finance.view.dialog.BaseDialog.DIALOG_START_MATCH;
import static com.sbai.finance.view.dialog.BattleResultDialog.GAME_RESULT_DRAW;
import static com.sbai.finance.view.dialog.BattleResultDialog.GAME_RESULT_LOSE;
import static com.sbai.finance.view.dialog.BattleResultDialog.GAME_RESULT_WIN;
import static com.sbai.finance.websocket.PushCode.ORDER_CLOSE;
import static com.sbai.finance.websocket.PushCode.ORDER_CREATED;
import static com.sbai.finance.websocket.cmd.QuickMatchLauncher.TYPE_CANCEL;
import static com.sbai.finance.websocket.cmd.QuickMatchLauncher.TYPE_QUICK_MATCH;


/**
 * As requirement the battle room has 7 status with STATUS_ROLE format:
 * <p>
 * 0. CREATED_OWNER
 * 1. STARTED_OWNER
 * 2. STARTED_CHALLENGER
 * 3. STARTED_OBSERVER
 * 4. OVER_OWNER
 * 5. OVER_CHALLENGER
 * 6. OVER_OBSERVER
 * <p>
 * For now, they can be merged to:
 * 0. CREATED_OWNER
 * 1. STARTED_PLAYERS (1 & 2)
 * 2. STARTED_OBSERVER
 * 3. OVER_PLAYERS (4 & 5)
 * 4. OVER_OBSERVER
 * <p>
 * In code:
 * 0 -> GAME_STATUS_CREATED
 * 1 -> GAME_STATUS_STARTED
 * 2 -> GAME_STATUS_STARTED && mIsObserver
 * 3 -> GAME_STATUS_END
 * 4 -> GAME_STATUS_END && mIsObserver
 */
public class FutureBattleActivity extends BaseActivity implements BattleButtons.OnViewClickListener, BattleTradeView.OnViewClickListener {

    @BindView(R.id.content)
    LinearLayout mContent;

    @BindView(R.id.battleContent)
    LinearLayout mBattleContent;

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

    private ShareDialogFragment mShareDialogFragment;

    private int mBattleId;
    private String mBatchCode;

    private Battle mBattle;
    private Variety mVariety;
    private FutureData mFutureData;
    private boolean mIsObserver;
    private int mGameStatus;

    private TradeOrder mCurrentOrder;
    private TradeOrder mCreatorOrder;
    private TradeOrder mAgainstOrder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);

        initData();

        requestLastBattleInfo(mBattleId, mBatchCode);
    }

    private void initData() {
        mBattleId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mBatchCode = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);
    }

    private void requestLastBattleInfo(int battleId, String batchCode) {
        Client.getBattleInfo(battleId, batchCode).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        mBattle = data;
                        mGameStatus = mBattle.getGameStatus();
                        mIsObserver = checkUserIsObserver();

                        if (mBattle.isBattleOver()) {
                            initBattleRecordPage();
                        } else {
                            initBattlePage();
                        }
                    }
                }).fire();
    }

    /**
     * 只加载一个对战记录的 Fragment
     */
    private void initBattleRecordPage() {
        mContent.removeAllViews();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, BattleRecordsFragment.newInstance(mBattle))
                .commitAllowingStateLoss();
    }

    private void initBattlePage() {
        //处理从通知栏点进来的
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), MainActivity.class).execute();
            finish();
        }

        mBattleContent.setVisibility(View.VISIBLE);

        initTabLayout();
        initBattleViews();
        initBottomFloatView();

        if (mIsObserver) {
            showObserverView();
            requestOrderHistory();
            requestCurrentOrder();
        } else if (mGameStatus == GAME_STATUS_CREATED) {
            showBattleButtons();
            updateRoomExistsTime();
        } else if (mGameStatus == GAME_STATUS_STARTED) {
            showBattleTradeView();
            requestOrderHistory();
            requestCurrentOrder();
        }

        requestVarietyData();

        subscribeBattle();
    }

    private void initTabLayout() {
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.trend_chart));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.one_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.three_min_k));
        mTabLayout.addTab(mTabLayout.newTab().setText(R.string.five_min_k));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    private TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            String tabText = tab.getText().toString();
            if (tabText.equals(getString(R.string.one_min_k))) {
                requestKlineDataAndSet("1");
                showKlineView();
            } else if (tabText.equals(getString(R.string.three_min_k))) {
                requestKlineDataAndSet("3");
                showKlineView();
            } else if (tabText.equals(getString(R.string.five_min_k))) {
                requestKlineDataAndSet("5");
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

    private void initBottomFloatView() {
        if (mIsObserver) {
            mBattleView.setMode(BattleFloatView.Mode.VISITOR)
                    .initWithModel(mBattle)
                    .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                    .setOnPraiseListener(new BattleFloatView.OnPraiseListener() {
                        @Override
                        public void addCreatePraiseCount() {
                            requestAddBattlePraise(mBattle.getLaunchUser());
                        }

                        @Override
                        public void addAgainstPraiseCount() {
                            requestAddBattlePraise(mBattle.getAgainstUser());
                        }
                    });

            startScheduleJob(1000);
        } else {
            //初始化
            mBattleView.setMode(BattleFloatView.Mode.MINE)
                    .initWithModel(mBattle);

            //分两种状态  1.发起匹配  2.对战中
            if (mBattle.getGameStatus() == GAME_STATUS_CREATED) {
                mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), true);
            } else if (mBattle.getGameStatus() == GAME_STATUS_STARTED) {
                mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false);
                startScheduleJob(1000);
            }
        }
    }

    private void requestAddBattlePraise(final int userId) {
        umengEventCount(UmengCountEventIdUtils.WITNESS_BATTLE_PRAISE);
        WsClient.get().send(new UserPraise(mBattle.getId(), userId), new WSCallback<WSMessage<Resp<Integer>>>() {
            @Override
            public void onResponse(WSMessage<Resp<Integer>> respWSMessage) {

            }
        });
    }

    private void updatePraiseView(int count, int userId) {
        boolean isLeft = userId == mBattle.getLaunchUser();
        if (isLeft) {
            mBattle.setLaunchPraise(count);
        } else {
            mBattle.setAgainstPraise(count);
        }
        mBattleView.setPraise(mBattle.getLaunchPraise(), mBattle.getAgainstPraise());
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

    private void updateTradeHistory(TradeRecord record) {
        mBattleTradeView.addTradeData(record, mBattle.getLaunchUser(), mBattle.getAgainstUser());
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

    //根据推送的数据更新订单
    private void updateCurrentOrder(TradeOrder order, int type) {
        if (order.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
            mCurrentOrder = type == ORDER_CREATED ? order : null;
        }
        if (order.getUserId() == mBattle.getLaunchUser()) {
            mCreatorOrder = type == ORDER_CREATED ? order : null;
        }
        if (order.getUserId() == mBattle.getAgainstUser()) {
            mAgainstOrder = type == ORDER_CREATED ? order : null;
        }

        if (mCurrentOrder != null) {
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
    }

    //根据请求的数据更新订单
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
                        subscribeFutureData();
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
                }).fireFree();
    }

    private void requestKlineDataAndSet(final String type) {
        mKlineView.clearData();
        mKlineView.setTag(type);
        Client.getKlineData(mVariety.getContractsCode(), type, null).setTag(TAG)
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
        if (LocalUser.getUser().isLogin()) {
            int userId = LocalUser.getUser().getUserInfo().getId();
            if (mBattle.getLaunchUser() != userId
                    && mBattle.getAgainstUser() != userId) {
                return true;
            }
            return false;
        }
        return true;
    }


    @Override
    protected void onBattlePushReceived(WSPush<Battle> push) {
        super.onBattlePushReceived(push);
        // 对战详情只能收到有人加入推送
        if (mBattle.isBattleOver()) { // OVER_PLAYERS + OVER_OBSERVER
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
                //观战中 可以弹出有人加入
                if (mIsObserver) {
                    if (push.getContent() != null) {
                        Battle data = (Battle) push.getContent().getData();
                        showQuickJoinBattleDialog(data);
                    }
                }
                //初始化底部栏  取消一切弹窗 显示交易视图 开始计时
                if (mBattle.getGameStatus() != GAME_STATUS_STARTED) {
                    dismissAllDialog();
                    startGame(push);
                }
                break;
            case PushCode.BATTLE_OVER:
                //对战结束 一个弹窗
                if (!mBattle.isBattleOver()) {
                    if (push.getContent() != null) {
                        Battle battle = (Battle) push.getContent().getData();
                        if (battle.getId() == mBattle.getId()) {
                            mBattle = (Battle) push.getContent().getData();
                            if (!mIsObserver) {
                                updateBattleInfo();
                                showGameOverDialog();
                                refreshTradeView();
                            } else {
                                mBattleView.setWinResult(mBattle.getWinResult());
                                mBattleView.setPraiseEnable(false);
                            }
                        }
                    }
                }
                break;

            case PushCode.ORDER_CREATED:
                //对比订单房间操作次数 对不上就刷新
                updateOrderStatus(push, ORDER_CREATED);
                break;
            case PushCode.ORDER_CLOSE:
                updateOrderStatus(push, ORDER_CLOSE);
                break;

            case PushCode.QUICK_MATCH_TIMEOUT:
                //匹配超时逻辑 只有在快速匹配的情况下才会匹配超时
                dismissAllDialog();
                showOvertimeMatchDialog();
                break;

            case PushCode.ROOM_CREATE_TIMEOUT:
                //房间创建超时
                showRoomOvertimeDialog();
                break;

            case PushCode.USER_PRAISE:
                Battle temp = (Battle) push.getContent().getData();
                updatePraiseView(temp.getCurrentPraise(), temp.getPraiseUserId());
                break;

        }

    }

    //根据推送更新订单状态
    private void updateOrderStatus(WSPush<Battle> push, int type) {
        requestBattleScore();
        if (push.getContent() != null) {
            Battle battle = (Battle) push.getContent().getData();
            int optCount = mBattleTradeView.getListView().getAdapter().getCount();
            if (optCount == battle.getOptLogCount() - 1) {
                //更新本次数据
                TradeRecord record = TradeRecord.getRecord(battle, mVariety, type);
                TradeOrder order = TradeOrder.getTradeOrder(battle);
                updateCurrentOrder(order, type);
                updateTradeHistory(record);
            } else {
                requestBattleInfo();
            }
        }
    }

    private void requestBattleScore() {
        WsClient.get().send(new CurrentBattle(mBattle.getId(), mBattle.getBatchCode()),
                new WSCallback<WSMessage<Resp<Battle>>>() {
                    @Override
                    public void onResponse(WSMessage<Resp<Battle>> resp) {
                        if (resp.getContent().isSuccess()) {
                            mBattle = resp.getContent().getData();
                        }
                    }
                });
    }

    private void dismissAllDialog() {
        SmartDialog.dismiss(this);
        BaseDialog.dismiss(this);

        if (mShareDialogFragment != null
                && mShareDialogFragment.getDialog() != null
                && mShareDialogFragment.getDialog().isShowing()) {
            mShareDialogFragment.dismissAllowingStateLoss();
        }
    }

    private void startGame(WSPush<Battle> push) {
        mBattle = (Battle) push.getContent().getData();
        mBattleView.initWithModel(mBattle);
        mBattleView.setProgress(0, 0, false);
        showBattleTradeView();
        showStartGameDialog();
        startScheduleJob(1000);
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
                                Launcher.with(getActivity(), FutureBattleActivity.class)
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
                myProfit = FinanceUtil.subtraction(futureData.getLastPrice()
                        , mCurrentOrder.getOrderPrice()).doubleValue();
            } else {
                myProfit = FinanceUtil.subtraction(mCurrentOrder.getOrderPrice()
                        , futureData.getLastPrice()).doubleValue();
            }
            myProfit = myProfit * mVariety.getEachPointMoney();
            mBattleTradeView.setTradeData(mCurrentOrder.getDirection()
                    , FinanceUtil.formatWithScale(mCurrentOrder.getOrderPrice(), mVariety.getPriceScale()), myProfit);
        }
        //房主的累计收益
        if (mCreatorOrder != null) {
            if (mCreatorOrder.getDirection() == 1) {
                creatorProfit = FinanceUtil.subtraction(futureData.getLastPrice()
                        , mCreatorOrder.getOrderPrice()).doubleValue();
            } else {
                creatorProfit = FinanceUtil.subtraction(mCreatorOrder.getOrderPrice()
                        , futureData.getLastPrice()).doubleValue();
            }
            creatorProfit = creatorProfit * mVariety.getEachPointMoney();
        }
        //对抗者的累计收益
        if (mAgainstOrder != null) {
            if (mAgainstOrder.getDirection() == 1) {
                againstProfit = FinanceUtil.subtraction(futureData.getLastPrice()
                        , mAgainstOrder.getOrderPrice()).doubleValue();
            } else {
                againstProfit = FinanceUtil.subtraction(mAgainstOrder.getOrderPrice()
                        , futureData.getLastPrice()).doubleValue();
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

    //结束比赛后调用
    private void updateBattleInfo() {
        boolean isInviting = mBattle.getGameStatus() == GAME_STATUS_CREATED;
        if (mBattle != null) {
            mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), isInviting);
        }
        mBattleView.setDeadline(mBattle.getGameStatus(), -1);
    }

    //初始化开始游戏弹窗
    private void showStartGameDialog() {
        StartGameDialog.get(this, mBattle.getAgainstUserPortrait());
    }

    //匹配超时弹窗
    private void showOvertimeMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.match_overtime))
                .setTitle(getString(R.string.match_failed))
                .setPositive(R.string.later_try_again, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.rematch, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestQuickSearchForLaunch(TYPE_QUICK_MATCH);
                    }
                }).show();
    }

    //房间超时弹窗
    private void showRoomOvertimeDialog() {
        SmartDialog.single(getActivity(), getString(R.string.wait_to_overtime))
                .setTitle(getString(R.string.wait_overtime))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dismissAllDialog();
                        finish();
                    }
                })
                .setNegative(R.string.recreate_room, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dismissAllDialog();
                        Launcher.with(FutureBattleActivity.this, CreateBattleActivity.class).execute();
                        finish();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }


    @Override
    public void onInviteButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_INVITE_FRIENDS);
        showInviteDialog();
    }

    private void showInviteDialog() {
        if (mShareDialogFragment == null) {
            String shareTitle = getString(R.string.invite_you_join_future_battle,
                    LocalUser.getUser().getUserInfo().getUserName());
            String shareDescribe = getString(R.string.future_battle_desc);
            mShareDialogFragment = ShareDialogFragment
                    .newInstance()
                    .setShareMode(true)
                    .setShareContent(FutureBattleActivity.this, shareTitle, shareDescribe, mBattle.getBatchCode());
        }
        mShareDialogFragment.showAsync(getSupportFragmentManager());
    }

    @Override
    public void onMatchButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_FAST_MATCH);
        showMatchConfirmDialog();
    }

    private void showMatchConfirmDialog() {
        SmartDialog.single(getActivity(), getString(R.string.start_match_and_battle))
                .setTitle(getString(R.string.match_battle))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestQuickSearchForLaunch(TYPE_QUICK_MATCH);
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void requestQuickSearchForLaunch(final int type) {
        WsClient.get().send(new QuickMatchLauncher(type, mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                if (type == TYPE_QUICK_MATCH) {
                    showMatchDialog();
                }

                if (type == TYPE_CANCEL) {
                    SmartDialog.dismiss(FutureBattleActivity.this);
                }
            }

            @Override
            public void onError(int code) {
            }

        });
    }

    //开始匹配弹窗
    private void showMatchDialog() {
        StartMatchDialog.get(this, new StartMatchDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                StartMatchDialog.dismiss(FutureBattleActivity.this);
                showCancelMatchDialog();
            }
        });
    }

    //取消匹配弹窗
    private void showCancelMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_tip))
                .setTitle(getString(R.string.cancel_matching))
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        requestQuickSearchForLaunch(TYPE_CANCEL);
                    }
                })
                .setNegative(R.string.continue_match, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showMatchDialog();
                    }
                }).show();
    }

    @Override
    public void onCancelButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_CANCEL_BATTLE);
        showCancelBattleDialog();
    }

    //取消对战弹窗
    private void showCancelBattleDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_battle_tip))
                .setTitle(getString(R.string.cancel_battle))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestCancelBattle();
                    }
                })
                .setNegative(R.string.continue_to_battle, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();

    }

    private void requestCancelBattle() {
        WsClient.get().send(new CancelBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                Intent intent = new Intent();
                intent.putExtra(Launcher.EX_PAYLOAD, GAME_STATUS_CANCELED);
                intent.putExtra(Launcher.EX_PAYLOAD_1, mBattle.getId());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(int code) {
                ToastUtil.show(getString(R.string.cancel_failed_game_start));
            }
        });
    }

    @Override
    public void onLongPurchaseButtonClick() {
        umengEventCount(UmengCountEventIdUtils.BATTLE_BULLISH);
        requestCreateOrder(DIRECTION_LONG_PURCHASE);
    }

    @Override
    public void onShortPurchaseButtonClick() {
        umengEventCount(UmengCountEventIdUtils.BATTLE_BEARISH);
        requestCreateOrder(DIRECTION_SHORT_PURCHASE);
    }

    @Override
    public void onClosePositionButtonClick() {
        umengEventCount(UmengCountEventIdUtils.BATTLE_CLOSE_POSITION);
        if (mCurrentOrder != null) {
            requestClosePosition(mCurrentOrder.getId());
        }
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

    private void subscribeFutureData() {
        if (mVariety != null) {
            startScheduleJob(1000);
            Netty.get().subscribe(Netty.REQ_SUB, mVariety.getContractsCode());
            Netty.get().addHandler(mNettyHandler);

            requestExchangeStatus();
            requestTrendDataAndSet();
        }
    }

    private void UnSubscribeFutureData() {
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

            if (mKlineView.isLastDataVisible()) {
                if (mTabLayout.getSelectedTabPosition() == 1) {
                    requestKlineDataAndSet("1");
                } else if (mTabLayout.getSelectedTabPosition() == 2) {
                    requestKlineDataAndSet("3");
                } else if (mTabLayout.getSelectedTabPosition() == 3) {
                    requestKlineDataAndSet("5");
                }
            }
        }
        if (mGameStatus == GAME_STATUS_CREATED) {
            updateRoomExistsTime();
        }
        if (mIsObserver || mBattle.getGameStatus() == GAME_STATUS_STARTED) {
            showDeadlineTime();
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

    private void showDeadlineTime() {
        long currentTime = SysTime.getSysTime().getSystemTimestamp();
        long startTime = mBattle.getStartTime();
        int diff = mBattle.getEndline() - DateUtil.getDiffSeconds(currentTime, startTime);
        if (diff == 0 && !mIsObserver) {
            showCalculatingView();
        }
        if (diff == 0 && mIsObserver) {
            refreshTradeView();
        }
        //5秒没收到结果自动结算
        if (diff == -5 && !mIsObserver
                && mBattle.getGameStatus() != GAME_STATUS_END) {
            requestBattleInfo();
        }
        mBattleView.setDeadline(mBattle.getGameStatus(), diff);
    }

    public void refreshTradeView() {
        //刷新交易数据
        requestOrderHistory();
        requestCurrentOrder();
    }

    private void subscribeBattle() {
        if (mBattle != null && !mBattle.isBattleOver()) {
            WsClient.get().send(new SubscribeBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
                @Override
                public void onResponse(WSMessage<Resp> respWSMessage) {
                }
            });
        }
    }

    private void unSubscribeBattle() {
        if (mBattle != null && !mBattle.isBattleOver()) {
            WsClient.get().send(new UnSubscribeBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
                @Override
                public void onResponse(WSMessage<Resp> respWSMessage) {
                }
            });
        }
    }

    //快速匹配结果查询
    private void requestFastMatchResult() {
        Client.getQuickMatchResult(Battle.CREATE_FAST_MATCH, mBattle.getId()).setTag(TAG)
                .setCallback(new ApiCallback<Resp<Battle>>() {
                    @Override
                    public void onSuccess(Resp<Battle> battleResp) {
                        if (battleResp.getCode() == Battle.CODE_CREATE_FAST_MATCH_TIMEOUT) {
                            StartMatchDialog.dismiss(FutureBattleActivity.this);
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {

                    }
                }).fireFree();
    }

    private void requestBattleInfo() {
        WsClient.get().send(new CurrentBattle(mBattle.getId(), mBattle.getBatchCode()),
                new WSCallback<WSMessage<Resp<Battle>>>() {
                    @Override
                    public void onResponse(WSMessage<Resp<Battle>> resp) {
                        if (resp.getContent().isSuccess()) {
                            int gameStatus = mBattle.getGameStatus();
                            mBattle = resp.getContent().getData();
                            //更新左右点赞数
                            updatePraiseView(mBattle.getLaunchPraise(), mBattle.getLaunchUser());
                            updatePraiseView(mBattle.getAgainstPraise(), mBattle.getAgainstUser());

                            //2017/7/4 从匹配或者创建房间过渡到游戏开始 如果直接过渡到结束 另外处理 最后一种过渡到房间取消
                            if (gameStatus == ROOM_STATE_CREATE) {
                                if (mBattle.getGameStatus() == GAME_STATUS_STARTED) {
                                    //切换状态
                                    dismissAllDialog();
                                    updateRoomState();
                                }
                                if (mBattle.getGameStatus() == GAME_STATUS_END) {
                                    //切换状态
                                    updateRoomState();
                                }
                                if (mBattle.getGameStatus() == GAME_STATUS_CANCELED) {
                                    dismissAllDialog();
                                    showRoomOvertimeDialog();
                                }

                            }
                            //正在对战中
                            if (gameStatus == ROOM_STATE_START) {
                                refreshTradeView();
                            }
                            //游戏结束后
                            if (mBattle.getGameStatus() == GAME_STATUS_END
                                    && !mIsObserver) {
                                dismissCalculatingView();
                                updateBattleInfo();
                                showGameOverDialog();
                                refreshTradeView();
                            }

                            if (mBattle.getGameStatus() == GAME_STATUS_END
                                    && mIsObserver) {
                                mBattleView.setWinResult(mBattle.getWinResult());
                                mBattleView.setPraiseEnable(false);
                            }
                        }
                    }
                });
    }

    private void updateRoomState() {
        mBattleView.initWithModel(mBattle);
        mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false);
        showBattleTradeView();
        refreshTradeView();
    }

    private void showGameOverDialog() {
        int result;
        String content;
        if (mBattle.getWinResult() == 0) {
            //平局
            result = GAME_RESULT_DRAW;
            content = getString(R.string.return_reward);
        } else {
            boolean win = getWinResult();

            String coinType = getCoinType();

            if (win) {
                result = GAME_RESULT_WIN;
                content = "+" + (mBattle.getReward() - (int) mBattle.getCommission()) + coinType;
            } else {
                result = GAME_RESULT_LOSE;
                content = "-" + mBattle.getReward() + coinType;
            }
        }
        BattleResultDialog.get(this, new BattleResultDialog.OnCloseListener() {
            @Override
            public void onClose() {
                finish();
            }
        }, result, content);

        dismissCalculatingView();

        mBattleView.setWinResult(mBattle.getWinResult());
    }

    private boolean getWinResult() {
        boolean win = false;
        //我是房主
        if (mBattle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId()) {
            //result ==1为房主赢
            if (mBattle.getWinResult() == 1) {
                //我赢了
                win = true;
            } else {
                //我输了
                win = false;
            }
        } else {
            //我不是房主
            if (mBattle.getWinResult() == 2) {
                //我赢了
                win = true;
            } else {
                //我输了
                win = false;
            }
        }
        return win;
    }

    private String getCoinType() {
        if (mBattle.getCoinType() == 2) {
            return getString(R.string.ingot);
        }
        return getString(R.string.integral);
    }

    private void showCalculatingView() {
        mLoadingContent.setVisibility(View.VISIBLE);
        mLoading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));
    }

    private void dismissCalculatingView() {
        mLoading.clearAnimation();
        mLoadingContent.setVisibility(View.GONE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        UnSubscribeFutureData();
        unSubscribeBattle();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        subscribeFutureData();
        subscribeBattle();

        if (mBattle != null) {
            if (!mBattle.isBattleOver()) {
                requestBattleInfo();
            }
            //正在快速匹配的要检测快速匹配结果
            if (StartMatchDialog.getCurrentDialog() == DIALOG_START_MATCH) {
                requestFastMatchResult();
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseDialog.dismiss(this);
        if (mTabLayout != null) {
            mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
        }
    }
}
