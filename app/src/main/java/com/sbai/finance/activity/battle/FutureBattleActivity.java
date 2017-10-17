package com.sbai.finance.activity.battle;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
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
import com.sbai.finance.App;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.fragment.battle.BattleRecordsFragment;
import com.sbai.finance.fragment.dialog.BattleShareDialog;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.TradeOrder;
import com.sbai.finance.model.battle.TradeOrderClosePosition;
import com.sbai.finance.model.battle.TradeRecord;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.model.system.Share;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.BattleBottomBothInfoView;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.BattleWaitAgainstLayout;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.BaseDialog;
import com.sbai.finance.view.dialog.BattleResultDialog;
import com.sbai.finance.view.dialog.StartGameDialog;
import com.sbai.finance.view.dialog.StartMatchDialog;
import com.sbai.finance.view.slidingTab.HackTabLayout;
import com.sbai.finance.websocket.GameCode;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.WsClient;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.CurrentBattle;
import com.sbai.finance.websocket.cmd.QuickMatchLauncher;
import com.sbai.finance.websocket.cmd.SubscribeBattle;
import com.sbai.finance.websocket.cmd.UnSubscribeBattle;
import com.sbai.finance.websocket.cmd.UserPraise;
import com.sbai.finance.websocket.market.DataReceiveListener;
import com.sbai.finance.websocket.market.MarketSubscribe;
import com.sbai.finance.websocket.market.MarketSubscriber;
import com.sbai.glide.GlideApp;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class FutureBattleActivity extends BaseActivity implements
        BattleWaitAgainstLayout.OnViewClickListener,
        BattleTradeView.OnViewClickListener,
        BattleBottomBothInfoView.OnUserPraiseListener {

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
    BattleWaitAgainstLayout mBattleWaitAgainstLayout;
    @BindView(R.id.battleTradeView)
    BattleTradeView mBattleTradeView;
    @BindView(R.id.battleView)
    BattleBottomBothInfoView mBattleView;
    @BindView(R.id.battleContent)
    LinearLayout mBattleContent;
    @BindView(R.id.rootView)
    LinearLayout mRootView;
    @BindView(R.id.loading)
    ImageView mLoading;
    @BindView(R.id.loadingContent)
    LinearLayout mLoadingContent;

    private BattleShareDialog mBattleShareDialog;
    private Battle mCurrentBattle;
    //
    private int mHistoryBattleId;
    private boolean mUserIsObserver;
    //登录的账号是不是对战的发起者

    //登陆者自己的订单
    private TradeOrder mCurrentOrder;
    //对战发起者订单
    private TradeOrder mInitiatorOrder;
    //应战者订单
    private TradeOrder mAgainstOrder;

    private Variety mVariety;

    //用来更新K线数据
    private static final int HANDLER_WHAT_VARIETY = 100;
    //等待有人加入对战的时候 房间多少秒后自动关闭
    private static final int HANDLER_WHAT_EXIT_ROOM = 200;
    //正在对战的时候  对战多久后关闭
    private static final int HANDLER_WHAT_BATTLE_COUNTDOWN = 300;
    private UserFundInfo mUserFundInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);
        mCurrentBattle = getIntent().getParcelableExtra(ExtraKeys.BATTLE);
        mHistoryBattleId = mCurrentBattle.getId();
        mUserFundInfo = getIntent().getParcelableExtra(ExtraKeys.USER_FUND);
        requestLatestBattleInfo();
        MissAudioManager.get().stop();
    }

    private void requestLatestBattleInfo() {
        if (mCurrentBattle == null) return;
        Client.getBattleInfo(mCurrentBattle.getId(), mCurrentBattle.getBatchCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {

                    @Override
                    protected void onRespSuccessData(Battle data) {
                        mCurrentBattle = data;
                        mUserIsObserver = checkUserIsObserver();

                        if (mCurrentBattle.isBattleOver()) {
                            openBattleRecordPage();
                        } else {
                            initBattlePage();
                        }
                    }
                }).fire();
    }

    //显示这句的历史对战记录页面
    private void openBattleRecordPage() {
        mRootView.removeAllViews();
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.rootView, BattleRecordsFragment.newInstance(mCurrentBattle))
                .commitAllowingStateLoss();
    }


    private void initBattlePage() {
        //未登录从通知栏点进来
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), MainActivity.class).execute();
            finish();
        }

        initView();

        if (mUserIsObserver || mCurrentBattle.isBattleStarted()) {
            showBattleTradeView();
            requestOrderHistory();
            requestCurrentOrder();
        } else if (mCurrentBattle.isBattleInitiating()) {
            showBattlePrepareButtons();
            updateRoomExistsTime();
        }
        requestVarietyPrice();
    }


    private void initView() {
        mBattleContent.setVisibility(View.VISIBLE);
        initTabLayout();
        mBattleWaitAgainstLayout.setOnViewClickListener(this);
        mBattleTradeView.setOnViewClickListener(this);
        initBattleFloatView();
        if (mUserIsObserver) {
            mBattleTradeView.setObserver(true);
        }
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
            switch (tab.getPosition()) {
                case 0:
                    showTrendView();
                    break;
                case 1:
                    requestKlineDataAndSet("1");
                    showKlineView();
                    break;
                case 2:
                    requestKlineDataAndSet("3");
                    showKlineView();
                    break;
                case 3:
                    requestKlineDataAndSet("5");
                    showKlineView();
                    break;
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

    private void initBattleFloatView() {
        if (mUserIsObserver) {
            mBattleView.setMode(BattleBottomBothInfoView.Mode.VISITOR)
                    .initWithModel(mCurrentBattle)
                    .setProgress(mCurrentBattle.getLaunchScore(), mCurrentBattle.getAgainstScore(), false);
            mBattleView.setOnUserPraiseListener(this);
            mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_BATTLE_COUNTDOWN, 0);

        } else {
            //初始化
            mBattleView.setMode(BattleBottomBothInfoView.Mode.MINE)
                    .initWithModel(mCurrentBattle);
            if (mCurrentBattle.isBattleInitiating()) {
                mBattleView.setProgress(mCurrentBattle.getLaunchScore(), mCurrentBattle.getAgainstScore(), true);
                mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_EXIT_ROOM, 0);
            } else if (mCurrentBattle.isBattleStarted()) {
                mBattleView.setProgress(mCurrentBattle.getLaunchScore(), mCurrentBattle.getAgainstScore(), false);
                mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_BATTLE_COUNTDOWN, 0);
            }
        }
    }

    private void requestAddBattlePraise(int launchUser) {
        umengEventCount(UmengCountEventId.WITNESS_BATTLE_PRAISE);

        WsClient.get().send(new UserPraise(mCurrentBattle.getId(), launchUser)); // praise push received
    }


    /**
     * @param praiseCount   被赞数量
     * @param praisedUserId 被赞用户id
     */
    private void updatePraiseCount(int praiseCount, int praisedUserId) {
        if (praisedUserId == mCurrentBattle.getLaunchUser()) {
            mCurrentBattle.setLaunchPraise(praiseCount);
        } else {
            mCurrentBattle.setAgainstPraise(praiseCount);
        }
        mBattleView.setPraise(mCurrentBattle.getLaunchPraise(), mCurrentBattle.getAgainstPraise());
    }


    private void showBattlePrepareButtons() {
        //显示邀请等三个按钮 只针对游戏创建者
        mBattleWaitAgainstLayout.setVisibility(View.VISIBLE);
        mBattleTradeView.setVisibility(View.GONE);
    }

    private void showBattleTradeView() {
        //显示交易视图 区分游客和对战者 默认对战者
        mBattleWaitAgainstLayout.setVisibility(View.GONE);
        mBattleTradeView.setVisibility(View.VISIBLE);
    }

    private void requestOrderHistory() {
        Client.getOrderHistory(mCurrentBattle.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeRecord>>, List<TradeRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeRecord> data) {
                        updateTradeHistory(data);
                    }
                }).fire();
    }

    private void updateTradeHistory(List<TradeRecord> data) {
        mBattleTradeView.updateUserHistoryOrderData(data, mCurrentBattle.getLaunchUser(), mCurrentBattle.getAgainstUser());
    }

    private void updateTradeHistory(TradeRecord record) {
        mBattleTradeView.updateUserHistoryOrderData(record, mCurrentBattle.getLaunchUser(), mCurrentBattle.getAgainstUser());
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case HANDLER_WHAT_VARIETY:
                    //60秒刷新一次
                    updateVarietyData();
                    this.sendEmptyMessageDelayed(HANDLER_WHAT_VARIETY, TimerHandler.DEFAULT_INTERVAL_TIME * TimerHandler.TREND_REFRESH_TIME);
                    break;
                case HANDLER_WHAT_BATTLE_COUNTDOWN:
                    this.sendEmptyMessageDelayed(HANDLER_WHAT_BATTLE_COUNTDOWN, TimerHandler.DEFAULT_INTERVAL_TIME);
                    showBattleOverCountdownTime();
                    break;
                case HANDLER_WHAT_EXIT_ROOM:
                    updateRoomExistsTime();
                    this.sendEmptyMessageDelayed(HANDLER_WHAT_EXIT_ROOM, TimerHandler.DEFAULT_INTERVAL_TIME);
                    break;
            }
        }
    };


    private void showBattleOverCountdownTime() {
        long currentTime = SysTime.getSysTime().getSystemTimestamp();
        long startTime = mCurrentBattle.getStartTime();

        int diff = mCurrentBattle.getEndline() - DateUtil.getDiffSeconds(currentTime, startTime);
        if (diff == 0) {
            if (mUserIsObserver) {
                refreshTradeView();
            } else {
                showCalculatingView();
            }
        }

        //5秒没收到结果自动结算
        if (diff <= -5
                && !mUserIsObserver
                && !mCurrentBattle.isBattleOver()) {
            requestCurrentBattleInfo();
        }
        mBattleView.setDeadline(mCurrentBattle.getGameStatus(), diff);
    }

    private void requestCurrentBattleInfo() {
        Client.getBattleInfo(mCurrentBattle.getId(), mCurrentBattle.getBatchCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {

                    @Override
                    protected void onRespSuccessData(Battle data) {
                        updateBattleStatusAndInfo(data);
                    }

                }).fire();
    }

    private void updateBattleStatusAndInfo(Battle data) {
        mCurrentBattle = data;
        updatePraiseCount(data.getLaunchPraise(), data.getLaunchUser());
        updatePraiseCount(data.getAgainstPraise(), data.getAgainstUser());

        if (data.isBattleStarted()) {
            dismissAllDialog();
            updateRoomState();
            refreshTradeView();
            mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_BATTLE_COUNTDOWN, 0);
        } else if (data.isBattleOver()) {
            updateRoomState();
            if (mUserIsObserver) {
                mBattleView.setWinResult(data.getWinResult());
                mBattleView.setPraiseEnable(false);
                //需要判断结果是否是这个对战的
            } else if (mHistoryBattleId == data.getId()) {
                dismissCalculatingView();
                updateBattleOverInfo();
                showGameOverDialog();
                refreshTradeView();
            }

        } else if (data.isBattleCancel()) {
            dismissAllDialog();
            showRoomOvertimeDialog();
        }
    }

    private void showGameOverDialog() {
        setBattleTradeState(BattleTradeView.STATE_TRADE_END);
        dismissCalculatingView();
        int result;
        String content;
        if (mCurrentBattle.getWinResult() == 0) {
            //平局
            result = BattleResultDialog.GAME_RESULT_DRAW;
            content = getString(R.string.return_reward);
        } else {
            String coinType = getCoinType();

            if (mCurrentBattle.getBattleResult()) {
                result = BattleResultDialog.GAME_RESULT_WIN;
                content = "+" + (mCurrentBattle.getReward() - (int) mCurrentBattle.getCommission()) + coinType;
            } else {
                result = BattleResultDialog.GAME_RESULT_LOSE;
                content = "-" + mCurrentBattle.getReward() + coinType;
            }
        }
        BattleResultDialog.get(this, new BattleResultDialog.OnCloseListener() {
            @Override
            public void onClose() {
                finish();
            }
        }, result, content);

        mBattleView.setWinResult(mCurrentBattle.getWinResult());
    }

    private String getCoinType() {
        if (mCurrentBattle.getCoinType() == 2) {
            return getString(R.string.ingot);
        }
        return getString(R.string.integral);
    }

    //比赛结束
    private void updateBattleOverInfo() {
        if (mCurrentBattle == null) return;
        mBattleView.setProgress(mCurrentBattle.getLaunchScore(), mCurrentBattle.getAgainstScore(), mCurrentBattle.isBattleInitiating());
        mBattleView.setDeadline(mCurrentBattle.getGameStatus(), -1);
    }

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
                        Launcher.with(getActivity(), ChooseFuturesActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, "")
                                .putExtra(ExtraKeys.USER_FUND, mUserFundInfo)
                                .execute();
                        finish();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }

    private void updateRoomState() {
        mBattleView.initWithModel(mCurrentBattle);
        mBattleView.setProgress(mCurrentBattle.getLaunchScore(), mCurrentBattle.getAgainstScore(), false);
        showBattleTradeView();
        refreshTradeView();
    }

    private void dismissAllDialog() {
        SmartDialog.dismiss(this);
        BaseDialog.dismiss(this);
        if (mBattleShareDialog != null) {
            mBattleShareDialog.dismiss();
        }
    }


    private void refreshTradeView() {
        //刷新交易数据
        requestOrderHistory();
        requestCurrentOrder();
    }

    private void showCalculatingView() {
        if (!mCurrentBattle.isBattleOver()) {
            mLoadingContent.setVisibility(View.VISIBLE);
            mLoading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));
        }
    }

    private void dismissCalculatingView() {
        mLoading.clearAnimation();
        mLoadingContent.setVisibility(View.GONE);
    }

    private void updateVarietyData() {
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

    private void requestTrendDataAndSet() {
        if (mVariety == null) return;
        Client.getTrendData(mVariety.getContractsCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TrendViewData>>, List<TrendViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<TrendViewData> data) {
                        mTrendView.setDataList(data);
                    }
                }).fireFree();
    }

    @Override
    protected void onBattlePushReceived(WSPush<Battle> push) {
        super.onBattlePushReceived(push);
        if (push == null) return;
        WSPush.PushData content = push.getContent();

        if (mCurrentBattle != null && mCurrentBattle.isBattleOver()) { // OVER_PLAYERS + OVER_OBSERVER
            if (content != null
                    && content.getType() == PushCode.BATTLE_JOINED) {
                Battle battle = (Battle) content.getData();
                showQuickJoinBattleDialog(battle);
            }
            return;
        }
        if (content == null) return;
        Battle battle = (Battle) content.getData();
        switch (content.getType()) {
            case PushCode.BATTLE_JOINED:
                //观战中 可以弹出有人加入
                if (mUserIsObserver) {
                    if (battle != null) {
                        showQuickJoinBattleDialog(battle);
                    }
                }
                if (!mCurrentBattle.isBattleStarted()) {
                    dismissAllDialog();
                    startGame(battle);
                }
                break;
            case PushCode.BATTLE_OVER:
                //对战结束 一个弹窗
                Log.d("WebSocket", "原来的: " + mCurrentBattle.toString());
                if (!mCurrentBattle.isBattleOver()) {
                    Log.d("WebSocket", "最新的信息: " + battle.toString());
                    Log.d("WebSocket", "刚开始的id : " + mHistoryBattleId);
                    Log.d("WebSocket", "是否观察者 : " + mUserIsObserver);
                    if (battle != null
                            && battle.getId() == mHistoryBattleId) {
                        mCurrentBattle = battle;
                        if (!mUserIsObserver) {
                            updateBattleOverInfo();
                            showGameOverDialog();
                            refreshTradeView();
                        } else {
                            mBattleView.setWinResult(mCurrentBattle.getWinResult());
                            mBattleView.setPraiseEnable(false);
                        }
                    }
                }
                break;

            case PushCode.ORDER_CREATED:
                //对比订单房间操作次数 对不上就刷新
                if (battle != null) {
                    updateOrderStatus(battle, PushCode.ORDER_CREATED);
                }
                break;
            case PushCode.ORDER_CLOSE:
                if (battle != null) {
                    updateOrderStatus(battle, PushCode.ORDER_CLOSE);
                }
                break;
            case PushCode.QUICK_MATCH_TIMEOUT:
                //匹配超时逻辑 只有在快速匹配的情况下才会匹配超时
                dismissAllDialog();
                showOvertimeMatchDialog();
                break;
            case PushCode.ROOM_CREATE_TIMEOUT:
                //房间创建超时
                if (battle != null
                        && battle.getId() == mHistoryBattleId) {
                    showRoomOvertimeDialog();
                }
                break;
            case PushCode.USER_PRAISE:
                if (battle != null) {
                    updatePraiseCount(battle.getCurrentPraise(), battle.getPraiseUserId());
                }
                break;
        }
    }

    private void updateOrderStatus(Battle battle, int orderStatus) {
        requestChangedBattleInfo();
        int optCount = mBattleTradeView.getListView().getAdapter().getCount();
        if (optCount == battle.getOptLogCount() - 1) {
            //更新本次数据
            if (mVariety != null) {
                TradeRecord record = TradeRecord.getRecord(battle, mVariety, orderStatus);
                updateTradeHistory(record);
            }
            TradeOrder order = TradeOrder.getTradeOrder(battle);
            updateCurrentOrder(order, orderStatus);
        } else {
            webSocketRequestBattleInfo();
        }
    }

    private void updateCurrentOrder(TradeOrder order, int orderStatus) {

        if (order.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
            mCurrentOrder = orderStatus == PushCode.ORDER_CREATED ? order : null;
        }

        if (order.getUserId() == mCurrentBattle.getLaunchUser()) {
            mInitiatorOrder = orderStatus == PushCode.ORDER_CREATED ? order : null;
        }
        if (order.getUserId() == mCurrentBattle.getAgainstUser()) {
            mAgainstOrder = orderStatus == PushCode.ORDER_CREATED ? order : null;
        }

        if (mCurrentOrder != null) {
            setBattleTradeState(BattleTradeView.STATE_CLOSE_POSITION);
            if (mVariety != null) {
                mBattleTradeView.setTradeData(mCurrentOrder.getDirection(),
                        FinanceUtil.formatWithScale(mCurrentOrder.getOrderPrice(), mVariety.getPriceScale()), 0);
            } else {
                mBattleTradeView.setTradeData(mCurrentOrder.getDirection(),
                        String.valueOf(mCurrentOrder.getOrderPrice()), 0);
            }
        } else {
            setBattleTradeState(BattleTradeView.STATE_TRADE);
        }
    }

    //当发生交易后，需要手动请求对战信息,服务端推送回来的数据没有当前对战信息
    private void requestChangedBattleInfo() {
        WsClient.get().send(new CurrentBattle(mCurrentBattle.getId(), mCurrentBattle.getBatchCode()),
                new WSCallback<WSMessage<Resp<Battle>>>() {
                    @Override
                    public void onResponse(WSMessage<Resp<Battle>> resp) {
                        if (resp.getContent().isSuccess()) {
                            Battle battle = (Battle) resp.getContent().getData();
                            if (battle != null) {
                                mCurrentBattle = battle;
                            }
                        }
                    }
                }, TAG);
    }

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
                        requestQuickSearchForLaunch(QuickMatchLauncher.TYPE_QUICK_MATCH);
                    }
                }).show();
    }

    private void startGame(Battle battle) {
        if (battle != null) {
            mCurrentBattle = battle;
            mBattleView.initWithModel(mCurrentBattle);
            mBattleView.setProgress(0, 0, false);
            StartGameDialog.get(this, mCurrentBattle.getAgainstUserPortrait());
        }
        showBattleTradeView();
        mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_BATTLE_COUNTDOWN, 0);
    }


    private void requestFastMatchResult() {
        Client.getQuickMatchResult(Battle.CREATE_FAST_MATCH, mCurrentBattle.getId()).setTag(TAG)
                .setCallback(new Callback<Resp<Battle>>() {
                    @Override
                    public void onSuccess(Resp<Battle> battleResp) {
                        if (battleResp.getCode() == Battle.CODE_CREATE_FAST_MATCH_TIMEOUT) {
                            StartMatchDialog.dismiss(FutureBattleActivity.this);
                        }
                    }

                    @Override
                    protected void onRespSuccess(Resp<Battle> resp) {
                    }
                }).fireFree();
    }

    private void webSocketRequestBattleInfo() {
        WsClient.get().send(new CurrentBattle(mCurrentBattle.getId(), mCurrentBattle.getBatchCode()),
                new WSCallback<WSMessage<Resp<Battle>>>() {
                    @Override
                    public void onResponse(WSMessage<Resp<Battle>> resp) {
                        if (resp.getContent().isSuccess()) {
                            Resp<Battle> content = resp.getContent();
                            if (content != null && content.getData() != null) {
                                updateBattleStatusAndInfo(resp.getContent().getData());
                            }
                        }
                    }
                }, TAG);
    }


    private void unSubscribeFutureData() {
        if (mVariety != null) {
            MarketSubscriber.get().unSubscribe(mVariety.getContractsCode());
            MarketSubscriber.get().removeDataReceiveListener(mDataReceiveListener);
        }
    }


    private void subscribeBattle() {
        if (mCurrentBattle != null && !mCurrentBattle.isBattleOver()) {
            WsClient.get().send(new SubscribeBattle(mCurrentBattle.getId()));
        }
    }

    @Override
    protected void showQuickJoinBattleDialog(Battle battle) {
        super.showQuickJoinBattleDialog(battle);
    }

    private void unSubscribeBattle() {
        if (mCurrentBattle != null && !mCurrentBattle.isBattleOver()) {
            WsClient.get().send(new UnSubscribeBattle(mCurrentBattle.getId()));
        }
    }


    private boolean checkUserIsObserver() {
        if (LocalUser.getUser().isLogin()) {
            int userId = LocalUser.getUser().getUserInfo().getId();
            return mCurrentBattle.getLaunchUser() != userId
                    && mCurrentBattle.getAgainstUser() != userId;
        }
        return true;
    }

    private void requestVarietyPrice() {
        Client.requestVarietyPrice(mCurrentBattle.getVarietyId()).setTag(TAG)
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

    private void requestKlineDataAndAdd(KlineViewData theLeft) {
        String endTime = Uri.encode(theLeft.getTime());
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

    private void subscribeFutureData() {
        if (mVariety != null) {
            MarketSubscriber.get().subscribe(mVariety.getContractsCode());
            MarketSubscriber.get().addDataReceiveListener(mDataReceiveListener);

            requestExchangeStatus();
            requestTrendDataAndSet();
            mHandler.sendEmptyMessageDelayed(HANDLER_WHAT_VARIETY, 0);
        }
    }

    private void requestExchangeStatus() {
        Client.getExchangeStatus(mVariety.getExchangeId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Integer>, Integer>() {
                    @Override
                    protected void onRespSuccessData(Integer data) {
                        int exchangeStatus = (data != null ?
                                data : mVariety.getExchangeStatus());
                        mVariety.setExchangeStatus(exchangeStatus);
                        updateExchangeStatusView();
                    }
                }).fireFree();
    }

    private void updateExchangeStatusView() {
        if (mVariety.getExchangeStatus() == Variety.EXCHANGE_STATUS_CLOSE) {
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

    private DataReceiveListener mDataReceiveListener = new DataReceiveListener<Resp<FutureData>>() {
        @Override
        public void onDataReceive(Resp<FutureData> data) {
            if (data.hasData() && data.getCode() == MarketSubscribe.REQ_QUOTA) {
                FutureData futureData = data.getData();
                updateChartView(futureData);
                if (mVariety != null) {
                    updateMarketDataView(futureData);
                    updateTradeProfit(futureData);
                    if (mVariety.getExchangeStatus() == Variety.EXCHANGE_STATUS_CLOSE) {
                        requestExchangeStatus();
                    }
                }
            }
        }
    };

    private void updateTradeProfit(FutureData futureData) {
        double myProfit = 0;
        double initiatorProfit = 0;
        double againstProfit = 0;
        //这里只更新对战者的持仓信息
        if (mCurrentOrder != null && mBattleTradeView.isShown() && mBattleTradeView.getTradeState() == BattleTradeView.STATE_CLOSE_POSITION) {
            if (mCurrentOrder.getDirection() == 1) {
                myProfit = FinanceUtil.subtraction(futureData.getLastPrice(),
                        mCurrentOrder.getOrderPrice()).doubleValue();
            } else {
                myProfit = FinanceUtil.subtraction(mCurrentOrder.getOrderPrice(),
                        futureData.getLastPrice()).doubleValue();
            }
            myProfit = myProfit * mVariety.getEachPointMoney();
            mBattleTradeView.setTradeData(mCurrentOrder.getDirection(),
                    FinanceUtil.formatWithScale(mCurrentOrder.getOrderPrice(), mVariety.getPriceScale()), myProfit);
        }
        //房主的累计收益
        if (mInitiatorOrder != null) {
            if (mInitiatorOrder.getDirection() == 1) {
                initiatorProfit = FinanceUtil.subtraction(futureData.getLastPrice(),
                        mInitiatorOrder.getOrderPrice()).doubleValue();
            } else {
                initiatorProfit = FinanceUtil.subtraction(mInitiatorOrder.getOrderPrice(),
                        futureData.getLastPrice()).doubleValue();
            }
            initiatorProfit = initiatorProfit * mVariety.getEachPointMoney();
        }
        //对抗者的累计收益
        if (mAgainstOrder != null) {
            if (mAgainstOrder.getDirection() == 1) {
                againstProfit = FinanceUtil.subtraction(futureData.getLastPrice(),
                        mAgainstOrder.getOrderPrice()).doubleValue();
            } else {
                againstProfit = FinanceUtil.subtraction(mAgainstOrder.getOrderPrice(),
                        futureData.getLastPrice()).doubleValue();
            }
            againstProfit = againstProfit * mVariety.getEachPointMoney();
        }
        updateBattleProfit(initiatorProfit, againstProfit);
    }

    private void updateBattleProfit(double initiatorProfit, double againstProfit) {
        double leftProfit = initiatorProfit;
        double rightProfit = againstProfit;
        if (mCurrentBattle != null) {
            leftProfit += mCurrentBattle.getLaunchUnwindScore();
            rightProfit += mCurrentBattle.getAgainstUnwindScore();
            if (!mCurrentBattle.isBattleOver()) {
                mBattleView.setProgress(leftProfit, rightProfit, mCurrentBattle.isBattleInitiating());
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


    private void updateRoomExistsTime() {
        //更新房间存在倒计时
        long currentTime = SysTime.getSysTime().getSystemTimestamp();
        long createTime = mCurrentBattle.getCreateTime();
        int diff = DateUtil.getDiffSeconds(currentTime, createTime);
        if (mBattleWaitAgainstLayout.getVisibility() == View.VISIBLE) {
            mBattleWaitAgainstLayout.updateCountDownTime(DateUtil.getCountdownTime(600, diff));
        }
    }

    private void requestCurrentOrder() {
        Client.requestCurrentOrder(mCurrentBattle.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeOrder>>, List<TradeOrder>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeOrder> data) {
                        if (data.isEmpty()) {
                            if (mCurrentBattle.isBattleStarted() && !mUserIsObserver) {
                                setBattleTradeState(BattleTradeView.STATE_TRADE);
                            }
                            return;
                        }
                        updateCurrentOrder(data);
                    }
                }).fire();
    }

    private void updateCurrentOrder(List<TradeOrder> data) {
        TradeOrder mineOrder = null;
        for (TradeOrder resultData : data) {
            if (resultData.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
                mineOrder = resultData;
            }
            if (resultData.getUserId() == mCurrentBattle.getLaunchUser()) {
                mInitiatorOrder = resultData;
            } else if (resultData.getUserId() == mCurrentBattle.getAgainstUser()) {
                mAgainstOrder = resultData;
            }
        }


        if (mineOrder != null) {
            mCurrentOrder = mineOrder;
            setBattleTradeState(BattleTradeView.STATE_CLOSE_POSITION);
            if (mVariety != null) {
                mBattleTradeView.setTradeData(mineOrder.getDirection(),
                        FinanceUtil.formatWithScale(mineOrder.getOrderPrice(), mVariety.getPriceScale()), 0);
            } else {
                mBattleTradeView.setTradeData(mineOrder.getDirection(),
                        String.valueOf(mineOrder.getOrderPrice()), 0);
            }
        } else {
            setBattleTradeState(BattleTradeView.STATE_TRADE);
        }

    }

    private void setBattleTradeState(int tradeState) {
        //切换交易视图显示模式
        mBattleTradeView.changeTradeState(tradeState);
    }

    private void requestKlineDataAndSet(final String klineType) {
        if (mVariety == null) return;
        mKlineView.clearData();
        mKlineView.setTag(klineType);
        Client.getKlineData(mVariety.getContractsCode(), klineType, null).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<KlineViewData>>, List<KlineViewData>>() {
                    @Override
                    protected void onRespSuccessData(List<KlineViewData> data) {
                        if (TextUtils.isEmpty(klineType)) { // dayK
                            mKlineView.setDayLine(true);
                        } else {
                            mKlineView.setDayLine(false);
                        }
                        Collections.reverse(data);
                        mKlineView.setDataList(data);
                    }
                }).fire();
    }

    private void showInviteDialog() {
        Client.requestShareData(Share.SHARE_CODE_FUTURE_BATTLE)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Share>, Share>() {

                    @Override
                    protected void onRespSuccessData(Share data) {
                        mBattleShareDialog = BattleShareDialog.with(getActivity())
                                .setShareThumbUrl(data.getShareLeUrl())
                                .setShareDescription(data.getContent())
                                .setShareTitle(data.getTitle())
                                .setShareUrl(data.getShareLink());
                        mBattleShareDialog.show();
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        mBattleShareDialog = BattleShareDialog.with(getActivity())
                                .setShareDescription(getString(R.string.future_battle_desc))
                                .setShareTitle(getString(R.string.invite_you_join_future_battle,
                                        LocalUser.getUser().getUserInfo().getUserName()))
                                .setShareUrl(Client.SHARE_URL_FUTURE_BATTLE);
                        mBattleShareDialog.show();
                    }
                })
                .fireFree();
    }


    @Override
    public void onInviteFriendClick() {
        umengEventCount(UmengCountEventId.WAITING_ROOM_INVITE_FRIENDS);

        showInviteDialog();
    }

    @Override
    public void onQuickMatchClick() {
        umengEventCount(UmengCountEventId.WAITING_ROOM_FAST_MATCH);

        showQuickMatchDialog();
    }

    private void showQuickMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.start_match_and_battle))
                .setTitle(getString(R.string.match_battle))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestQuickSearchForLaunch(QuickMatchLauncher.TYPE_QUICK_MATCH);
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setGravity(Gravity.CENTER)
                .show();
    }

    private void requestQuickSearchForLaunch(final int machType) {
        WsClient.get().send(new QuickMatchLauncher(machType, mCurrentBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                if (machType == QuickMatchLauncher.TYPE_QUICK_MATCH) {
                    showMatchDialog();
                }

                if (machType == QuickMatchLauncher.TYPE_CANCEL) {
                    SmartDialog.dismiss(getActivity());
                }
            }

            @Override
            public void onError(int code) {
                Log.d(TAG, "onError: " + code);
            }
        }, TAG);
    }

    private void showMatchDialog() {
        StartMatchDialog.get(this, new StartMatchDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                StartMatchDialog.dismiss(getActivity());
                showCancelMatchDialog();
            }
        });
    }

    private void showCancelMatchDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_tip))
                .setTitle(getString(R.string.cancel_matching))
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        requestQuickSearchForLaunch(QuickMatchLauncher.TYPE_CANCEL);
                    }
                })
                .setNegative(R.string.continue_match, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        showMatchDialog();
                    }
                })
                .setGravity(Gravity.CENTER)
                .show();
    }

    @Override
    public void onCancelBattleClick() {
        umengEventCount(UmengCountEventId.WAITING_ROOM_CANCEL_BATTLE);

        showCancelBattleDialog();
    }

    private void showCancelBattleDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_battle_tip))
                .setTitle(getString(R.string.cancel_battle))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        confirmCancelBattle();
                    }
                }).setNegative(R.string.continue_to_battle, new SmartDialog.OnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();

    }

    private void confirmCancelBattle() {
        Client.cancelBattle(mCurrentBattle.getId()).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        Intent intent = new Intent();
                        intent.putExtra(Launcher.EX_PAYLOAD, Battle.GAME_STATUS_CANCELED);
                        intent.putExtra(Launcher.EX_PAYLOAD_1, mCurrentBattle.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).fire();
    }

    @Override
    public void onLongPurchaseButtonClick() {
        umengEventCount(UmengCountEventId.BATTLE_BULLISH);

        confirmOrder(TradeOrder.DIRECTION_LONG_PURCHASE);
    }

    private void confirmOrder(int orderDirection) {
        Client.createOrder(mCurrentBattle.getId(), orderDirection).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<TradeOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrder> resp) {

                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == GameCode.ORDER_EXISIT) {
                            refreshTradeView();
                            ToastUtil.show(failedResp.getMsg());
                        } else if (failedResp.getCode() == GameCode.GAME_OVER) {
                            requestCurrentBattleInfo();
                        }
                    }
                }).fire();
    }

    @Override
    public void onShortPurchaseButtonClick() {
        umengEventCount(UmengCountEventId.BATTLE_BEARISH);

        confirmOrder(TradeOrder.DIRECTION_SHORT_PURCHASE);
    }

    @Override
    public void onClosePositionButtonClick() {
        umengEventCount(UmengCountEventId.BATTLE_CLOSE_POSITION);
        if (mCurrentOrder != null) {
            closePosition(mCurrentOrder.getId());
        }
    }

    private void closePosition(int oderId) {
        Client.closePosition(mCurrentBattle.getId(), oderId).setTag(TAG)
                .setCallback(new Callback<Resp<TradeOrderClosePosition>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrderClosePosition> resp) {
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == GameCode.ORDER_CLOSE) {
                            refreshTradeView();
                        }else if (failedResp.getCode() == GameCode.GAME_OVER) {
                            requestCurrentBattleInfo();
                        }
                    }
                }).fire();
    }

    @Override
    public void onPraiseBattleInitiatorClick() {
        if (mCurrentBattle == null) return;
        requestAddBattlePraise(mCurrentBattle.getLaunchUser());
    }

    @Override
    public void onPraiseBattleAgainstClick() {
        if (mCurrentBattle == null) return;
        requestAddBattlePraise(mCurrentBattle.getAgainstUser());
    }

    @Override
    protected void onNetworkAvailable(boolean available) {
        super.onNetworkAvailable(available);
        if (available) {
            subscribeFutureData();
            subscribeBattle();

            if (mCurrentBattle != null) {
                if (!mCurrentBattle.isBattleOver()) {
                    webSocketRequestBattleInfo();
                }

                //正在快速匹配的要检测快速匹配结果
                if (StartMatchDialog.getCurrentDialog() == BaseDialog.DIALOG_START_MATCH) {
                    requestFastMatchResult();
                }
            }
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        subscribeFutureData();
        subscribeBattle();

        if (mCurrentBattle != null) {
            if (!mCurrentBattle.isBattleOver()) {
                webSocketRequestBattleInfo();
            }

            //正在快速匹配的要检测快速匹配结果
            if (StartMatchDialog.getCurrentDialog() == BaseDialog.DIALOG_START_MATCH) {
                requestFastMatchResult();
            }
        }

        registerNetworkStatus();
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribeFutureData();
        unSubscribeBattle();

        unregisterNetworkStatus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacksAndMessages(null);
        BaseDialog.dismiss(this);
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);
        GlideApp.with(App.getAppContext()).pauseRequestsRecursive();
    }
}
