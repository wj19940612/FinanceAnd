package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

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
import com.sbai.finance.game.GameCode;
import com.sbai.finance.game.PushCode;
import com.sbai.finance.game.WSMessage;
import com.sbai.finance.game.WSPush;
import com.sbai.finance.game.WsClient;
import com.sbai.finance.game.callback.WSCallback;
import com.sbai.finance.game.cmd.QuickMatchLauncher;
import com.sbai.finance.game.cmd.SubscribeBattle;
import com.sbai.finance.game.cmd.UnSubscribeBattle;
import com.sbai.finance.game.cmd.UserPraise;
import com.sbai.finance.market.DataReceiveListener;
import com.sbai.finance.market.MarketSubscribe;
import com.sbai.finance.market.MarketSubscriber;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.Praise;
import com.sbai.finance.model.battle.TradeOrder;
import com.sbai.finance.model.battle.TradeRecord;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.local.BattleStatus;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.BattleOperateView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.BaseDialog;
import com.sbai.finance.view.dialog.BattleResultDialog;
import com.sbai.finance.view.dialog.StartBattleDialog;
import com.sbai.finance.view.dialog.StartMatchDialog;
import com.sbai.finance.view.praise.FlyPraiseLayout;
import com.sbai.finance.view.slidingTab.HackTabLayout;
import com.sbai.glide.GlideApp;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static com.sbai.finance.model.battle.TradeRecord.OPT_STATUS_CLOSE_POSITION_LONG;
import static com.sbai.finance.model.battle.TradeRecord.OPT_STATUS_CLOSE_POSITION_SHORT;
import static com.sbai.finance.model.battle.TradeRecord.OPT_STATUS_OPEN_POSITION_LONG;
import static com.sbai.finance.model.battle.TradeRecord.OPT_STATUS_OPEN_POSITION_SHORT;
import static com.sbai.finance.model.local.BattleStatus.CANCELED;
import static com.sbai.finance.model.local.BattleStatus.CREATED_OWNER;
import static com.sbai.finance.model.local.BattleStatus.OVER_CHALLENGER;
import static com.sbai.finance.model.local.BattleStatus.OVER_OBSERVER;
import static com.sbai.finance.model.local.BattleStatus.OVER_OWNER;
import static com.sbai.finance.model.local.BattleStatus.STARTED_CHALLENGER;
import static com.sbai.finance.model.local.BattleStatus.STARTED_OBSERVER;
import static com.sbai.finance.model.local.BattleStatus.STARTED_OWNER;

/**
 * As requirement the battle room has 7 status with STATUS_ROLE format:
 * <p>
 * 1. CREATED_OWNER
 * 2. STARTED_OWNER
 * 3. STARTED_CHALLENGER
 * 4. STARTED_OBSERVER
 * 5. OVER_OWNER
 * 6. OVER_CHALLENGER
 * 7. OVER_OBSERVER
 * 8. CANCELED
 */
public class BattleActivity extends BaseActivity {
    private static final int UPDATE_BATTLE_INIT = 0;
    private static final int UPDATE_BATTLE_OVER = 1;
    private static final int UPDATE_BATTLE = 3;

    // 自定义结果码，再战一局 & 去普通练习场
    public static final int RESULT_CODE_FIGHT_AGAIN = 84;
    public static final int RESULT_CODE_GO_2_NORMAL_BATTLE = 844;

    @BindView(R.id.rootView)
    LinearLayout mRootView;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.lastPrice)
    TextView mLastPrice;
    @BindView(R.id.priceChange)
    TextView mPriceChange;
    @BindView(R.id.battleRemainingTime)
    TextView mBattleRemainingTime;

    @BindView(R.id.tabLayout)
    HackTabLayout mTabLayout;
    @BindView(R.id.trendView)
    TrendView mTrendView;
    @BindView(R.id.klineView)
    KlineView mKlineView;

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.battleOperateView)
    BattleOperateView mBattleOperateView;
    @BindView(R.id.praiseView)
    FlyPraiseLayout mPraiseView;

    private Battle mBattle;

    private Variety mVariety;

    private int mBattleStatus;

    private NetworkReceiver mNetworkReceiver;
    private OrderRecordListAdapter mOrderRecordListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        ButterKnife.bind(this);

        MissAudioManager.get().stop();

        initData(getIntent());
        initBattleOperateView();
        initListView();

        mNetworkReceiver = new NetworkReceiver();

        updateBattle(UPDATE_BATTLE_INIT);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        subscribeFutureData();
        subscribeBattle();

        if (StartMatchDialog.getCurrentDialog() == BaseDialog.DIALOG_START_MATCH) {
            requestFastMatchResult();
        }

        Network.registerNetworkChangeReceiver(this, mNetworkReceiver);

        startScheduleJob(1000); // 房间 和 对战 倒计时
    }

    @Override
    protected void onPause() {
        super.onPause();
        unSubscribeFutureData();
        unSubscribeBattle();
        Network.unregisterNetworkChangeReceiver(this, mNetworkReceiver);

        stopScheduleJob();
    }

    private void initListView() {
        mOrderRecordListAdapter = new OrderRecordListAdapter(getActivity());
        mListView.setAdapter(mOrderRecordListAdapter);
    }

    private void initData(Intent intent) {
        mBattle = intent.getParcelableExtra(ExtraKeys.BATTLE);
    }

    private void initBattleOperateView() {
        mBattleOperateView.setOnViewClickListener(new BattleOperateView.OnViewClickListener() {
            @Override
            public void onQuickMatchClick() {
                umengEventCount(UmengCountEventId.FUTURE_PK_FAST_MATCH);

                showQuickMatchDialog();
            }

            @Override
            public void onCancelBattleClick() {
                umengEventCount(UmengCountEventId.FUTURE_PK_CANCEL_MATCH);

                showCancelBattleDialog();
            }

            @Override
            public void onBuyLongClick() {
                if (mBattle != null) {
                    if (mBattle.getGameType() == Battle.GAME_TYPE_ARENA) {
                        umengEventCount(UmengCountEventId.MRPK_BUY);
                    } else {
                        umengEventCount(UmengCountEventId.FUTURE_PK_BUY);
                    }
                }

                createOrder(TradeOrder.DIRECTION_LONG);
            }

            @Override
            public void onSellShortClick() {
                if (mBattle != null) {
                    if (mBattle.getGameType() == Battle.GAME_TYPE_ARENA) {
                        umengEventCount(UmengCountEventId.MRPK_SELL);
                    } else {
                        umengEventCount(UmengCountEventId.FUTURE_PK_SELL);
                    }
                }

                createOrder(TradeOrder.DIRECTION_SHORT);
            }

            @Override
            public void onClosePositionClick() {
                if (mBattle != null) {
                    if (mBattle.getGameType() == Battle.GAME_TYPE_ARENA) {
                        umengEventCount(UmengCountEventId.MRPK_CLEAR);
                    } else {
                        umengEventCount(UmengCountEventId.FUTURE_PK_CLEAR);
                    }
                }

                TradeOrder ownerOrder = mBattleOperateView.getHoldingOrder();
                if (ownerOrder != null) {
                    closePosition(ownerOrder.getId());
                }
            }

            @Override
            public void onPraiseClick(boolean isOwner) {
                if (mBattle != null) {
                    if (mBattle.getGameType() == Battle.GAME_TYPE_ARENA) {
                        umengEventCount(UmengCountEventId.MRPK_PRAISE);
                    } else {
                        umengEventCount(UmengCountEventId.FUTURE_PK_PRAISE);
                    }
                }
                if (isOwner) {
                    requestAddBattlePraise(mBattle.getLaunchUser());
                } else {
                    requestAddBattlePraise(mBattle.getAgainstUser());
                }
            }

            @Override
            public void onAvatarClick() {
                if (mBattle != null) {
                    if (mBattle.getGameType() == Battle.GAME_TYPE_ARENA) {
                        umengEventCount(UmengCountEventId.MRPK_USER_AVATAR);
                    } else {
                        umengEventCount(UmengCountEventId.FUTURE_PK_USER_AVATAR);
                    }
                }
            }
        });
        mBattleOperateView.setPraiseView(mPraiseView);
    }

    private void updateBattle(final int updateBattle) {
        Client.getBattleInfo(mBattle.getId(), mBattle.getBatchCode()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle data) {
                        mBattle = data;
                        updateBattleStatus();
                        postUpdateBattle(updateBattle);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (updateBattle == UPDATE_BATTLE_OVER) {
                            stopScheduleJob();
                            mBattleOperateView.hideSettlingView();
                        }
                    }
                }).fire();
    }

    private void postUpdateBattle(int updateBattle) {
        switch (updateBattle) {
            case UPDATE_BATTLE_INIT:
                mOrderRecordListAdapter.setOwnerId(mBattle.getLaunchUser());
                initBasedOnBattleStatus();
                break;
            case UPDATE_BATTLE:
                mBattleOperateView.setBattle(mBattle);
                mBattleOperateView.updateView(mBattleStatus);
                break;
            case UPDATE_BATTLE_OVER:
                mBattleOperateView.setBattle(mBattle);
                mBattleOperateView.updateView(mBattleStatus);
                showGameOverDialog();
                break;
        }

    }

    private void initBasedOnBattleStatus() {
        if (mBattleStatus < OVER_OWNER && !LocalUser.getUser().isLogin()) { // 从 push 进，未登录情况
            Launcher.with(getActivity(), MainActivity.class).execute();
            finish();
            return;
        }

        if (mBattleStatus >= CANCELED) {
            showRoomOvertimeDialog();

        } else if (mBattleStatus >= OVER_OWNER) {
            mRootView.removeAllViews();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.rootView, BattleRecordsFragment.newInstance(mBattle))
                    .commitAllowingStateLoss();

        } else if (mBattleStatus >= STARTED_OWNER) {
            initTabLayout();
            mBattleOperateView.setBattle(mBattle);
            mBattleOperateView.updateView(mBattleStatus);

            requestOrderOperationHistory();
            requestHoldingOrders();
            requestVariety();

        } else if (mBattleStatus == CREATED_OWNER) {
            initTabLayout();
            mBattleOperateView.setBattle(mBattle);
            mBattleOperateView.updateView(mBattleStatus);

            requestVariety();
        }
    }

    private void updateBattleStatus() {
        if (mBattle.isBattleCreated()) {
            mBattleStatus = CREATED_OWNER;
            return;
        }

        if (mBattle.isBattleStarted()) {
            mBattleStatus = STARTED_OBSERVER;
            if (LocalUser.getUser().isLogin()) {
                int userId = LocalUser.getUser().getUserInfo().getId();
                if (mBattle.getLaunchUser() == userId) {
                    mBattleStatus = STARTED_OWNER;
                } else if (mBattle.getAgainstUser() == userId) {
                    mBattleStatus = STARTED_CHALLENGER;
                }
            }
            return;
        }

        if (mBattle.isBattleOver()) {
            mBattleStatus = OVER_OBSERVER;
            if (LocalUser.getUser().isLogin()) {
                int userId = LocalUser.getUser().getUserInfo().getId();
                if (mBattle.getLaunchUser() == userId) {
                    mBattleStatus = OVER_OWNER;
                } else if (mBattle.getAgainstUser() == userId) {
                    mBattleStatus = OVER_CHALLENGER;
                }
            }
            return;
        }

        if (mBattle.isBattleCanceled()) {
            mBattleStatus = CANCELED;
        }
    }

    @Override
    public void onTimeUp(int count) {
        if (count % TimerHandler.TREND_REFRESH_TIME == 0) {
            updateVarietyData();
        }

        if (mBattleStatus >= STARTED_OWNER && mBattleStatus <= STARTED_OBSERVER) {
            updateBattleRemainingTime();
            return;
        }

        if (mBattleStatus == BattleStatus.CREATED_OWNER) {
            mBattleOperateView.updateBattleWaitingCountDown();
            return;
        }
    }

    private void updateBattleRemainingTime() {
        long curTime = SysTime.getSysTime().getSystemTimestamp();
        long startTime = mBattle.getStartTime();
        long pastTime = curTime - startTime;
        long remainingTime = mBattle.getEndline() * 1000 - pastTime;
        remainingTime = Math.max(0, remainingTime);
        mBattleRemainingTime.setText(getString(R.string.remaining_time_x,
                DateUtil.format(remainingTime, "mm:ss")));

        if (remainingTime == 0) { // 对战结束后，一直没有收到结束推送，5秒后 自动刷新结果显示弹窗
            mBattleRemainingTime.setText(R.string.end);
            mBattleOperateView.showSettlingView();
            Object count = mBattleRemainingTime.getTag();
            if (count == null) {
                count = new Integer(0);
            }
            if (count instanceof Integer) {
                count = ((Integer) count).intValue() + 1;
                if (((Integer) count).intValue() == 5) {
                    updateBattle(UPDATE_BATTLE_OVER);
                }
            }
            mBattleRemainingTime.setTag(count);
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
        mTrendView.setVisibility(VISIBLE);
        mKlineView.setVisibility(GONE);
    }

    private void showKlineView() {
        mTrendView.setVisibility(GONE);
        mKlineView.setVisibility(VISIBLE);
    }

    private void requestAddBattlePraise(int launchUser) {

        WsClient.get().send(new UserPraise(mBattle.getId(), launchUser)); // praise push received
    }

    private void requestOrderOperationHistory() {
        Client.getTradeOperationRecords(mBattle.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeRecord>>, List<TradeRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeRecord> data) {
                        mOrderRecordListAdapter.setRecordList(data);
                        mListView.setSelection(mOrderRecordListAdapter.getCount() - 1);
                    }
                }).fire();
    }

    private void showGameOverDialog() {
        if (mBattleStatus != OVER_OBSERVER) {
            int winLoss;
            String content;
            if (mBattle.getWinResult() == Battle.WIN_RESULT_TIE) {
                winLoss = BattleResultDialog.GAME_RESULT_TIE;
                content = getString(R.string.return_reward);
            } else {
                String coinType = getCoinType();
                boolean win = (mBattleStatus == OVER_OWNER && mBattle.getWinResult() == Battle.WIN_RESULT_OWNER_WIN)
                        || (mBattleStatus == OVER_CHALLENGER && mBattle.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN);
                if (win) {
                    winLoss = BattleResultDialog.GAME_RESULT_WIN;
                    content = "+" + (mBattle.getReward() - (int) mBattle.getCommission()) + coinType;
                } else {
                    winLoss = BattleResultDialog.GAME_RESULT_LOSE;
                    content = "-" + mBattle.getReward() + coinType;
                }
            }
            BattleResultDialog.get(this, new BattleResultDialog.OnCallback() {
                @Override
                public void onClose() {
                    finish();
                }

                @Override
                public void onFightAgain() {
                    setResult(RESULT_CODE_FIGHT_AGAIN);
                    finish();
                }

                @Override
                public void onGo2NormalBattle() {
                    setResult(RESULT_CODE_GO_2_NORMAL_BATTLE);
                    finish();
                }
            }, winLoss, content, mBattle.getGameType());
        }
    }

    private String getCoinType() {
        if (mBattle.getCoinType() == 2) {
            return getString(R.string.ingot);
        }
        return getString(R.string.integral);
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
                        Launcher.with(getActivity(), ChooseFuturesActivity.class).execute();
                        finish();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .show();
    }

    private void dismissAllDialog() {
        SmartDialog.dismiss(this);
        BaseDialog.dismiss(this);
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

        WSPush.PushData pushData = push.getContent();
        Battle battle = (Battle) pushData.getData();

        if (battle.getId() != mBattle.getId()) { // 观战，查看对战记录
            if (pushData.getType() == PushCode.BATTLE_JOINED) {
                if (mBattleStatus >= STARTED_OBSERVER && mBattleStatus <= OVER_OBSERVER) {
                    showQuickJoinBattleDialog(battle);
                }
            }
        } else { // 有人加入，对战结束，订单操作，匹配玩家
            int pushType = pushData.getType();
            if (pushType == PushCode.BATTLE_JOINED && mBattleStatus == CREATED_OWNER) {
                mBattle = battle;
                updateBattleStatus();
                dismissAllDialog();
                startBattle();
                return;
            }
            if (pushType == PushCode.BATTLE_OVER) {
                if (mBattleStatus >= STARTED_OWNER && mBattleStatus <= STARTED_OBSERVER) {
                    mBattle = battle;
                    updateBattleStatus();
                    mBattleOperateView.setBattle(mBattle);
                    mBattleOperateView.updateView(mBattleStatus);

                    mBattleRemainingTime.setText(R.string.end);
                    showGameOverDialog();
                } else if (mBattleStatus >= OVER_OWNER && mBattleStatus <= OVER_OBSERVER) {
                    // 最后对战结束平仓推送会提前把对战改为结束状态，此时再收到结束对战通知，显示弹框
                    mBattleRemainingTime.setText(R.string.end);
                    showGameOverDialog();
                }
                return;
            }

            if (pushType == PushCode.QUICK_MATCH_TIMEOUT && mBattleStatus == CREATED_OWNER) {
                dismissAllDialog();
                showOvertimeMatchDialog();
                return;
            }

            if (pushType == PushCode.ROOM_CREATE_TIMEOUT && mBattleStatus == CREATED_OWNER) {
                if (battle.getId() == mBattle.getId()) {
                    showRoomOvertimeDialog();
                }
            }
        }
    }

    @Override
    protected void onBattlePraiseReceived(WSPush<Praise> praiseWSPush) {
        super.onBattlePraiseReceived(praiseWSPush);
        Praise praise = (Praise) praiseWSPush.getContent().getData();
        if (praise.getBattleId() != mBattle.getId()) return;

        if (mBattleStatus >= STARTED_OWNER && mBattleStatus <= STARTED_OBSERVER) {
            if (praise.getPraiseUserId() == mBattle.getLaunchUser()) {
                mBattle.setLaunchPraise(praise.getCurrentPraise());
            } else {
                mBattle.setAgainstPraise(praise.getCurrentPraise());
            }
            mBattleOperateView.setPraise(mBattle.getLaunchPraise(), mBattle.getAgainstPraise());
        }
    }

    @Override
    protected void onBattleOrdersReceived(WSPush<TradeOrder> tradeOrderWSPush) {
        super.onBattleOrdersReceived(tradeOrderWSPush);
        TradeOrder tradeOrder = (TradeOrder) tradeOrderWSPush.getContent().getData();
        if (tradeOrder.getBattleId() != mBattle.getId()) return;

        if (mBattleStatus >= STARTED_OWNER && mBattleStatus <= STARTED_OBSERVER) {
            TradeRecord record = TradeRecord.getRecord(tradeOrder);
            updateBattleRecordListView(record, tradeOrder.getOptLogCount());

            updateHoldingOrders(tradeOrder, tradeOrder.getUnwindType() > 0);

            if (tradeOrder.getUnwindType() > 0) {
                updateBattle(UPDATE_BATTLE);
            }
        }
    }

    private void updateHoldingOrders(TradeOrder order, boolean closePosition) {
        if (order.getUserId() == mBattle.getLaunchUser()) {
            mBattleOperateView.setOwnerOrder(closePosition ? null : order);
        }
        if (order.getUserId() == mBattle.getAgainstUser()) {
            mBattleOperateView.setChallengerOrder(closePosition ? null : order);
        }
    }

    private void updateBattleRecordListView(TradeRecord record, int optCount) {
        if (optCount == mOrderRecordListAdapter.getCount() + 1) {
            mOrderRecordListAdapter.addRecord(record);
            mListView.setSelection(mOrderRecordListAdapter.getCount() - 1);
        } else {
            requestOrderOperationHistory();
        }
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

    private void startBattle() {
        StartBattleDialog.get(getActivity(),
                mBattle.getLaunchUserPortrait(), mBattle.getLaunchUserName(),
                mBattle.getAgainstUserPortrait(), mBattle.getAgainstUserName(),
                new StartBattleDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mBattleOperateView.setBattle(mBattle);
                        mBattleOperateView.updateView(mBattleStatus);
                    }
                });
    }

    private void requestFastMatchResult() {
        Client.getQuickMatchResult(Battle.CREATE_FAST_MATCH, mBattle.getId()).setTag(TAG)
                .setCallback(new Callback<Resp<Battle>>() {
                    @Override
                    public void onSuccess(Resp<Battle> battleResp) {
                        if (battleResp.getCode() == Battle.CODE_CREATE_FAST_MATCH_TIMEOUT) {
                            StartMatchDialog.dismiss(BattleActivity.this);
                        }
                    }

                    @Override
                    protected void onRespSuccess(Resp<Battle> resp) {
                    }
                }).fireFree();
    }

    private void unSubscribeFutureData() {
        if (mVariety != null) {
            MarketSubscriber.get().unSubscribe(mVariety.getContractsCode());
            MarketSubscriber.get().removeDataReceiveListener(mDataReceiveListener);
        }
    }

    private void subscribeBattle() {
        if (mBattle != null && !mBattle.isBattleOver()) {
            WsClient.get().send(new SubscribeBattle(mBattle.getId()));
        }
    }

    private void unSubscribeBattle() {
        if (mBattle != null && !mBattle.isBattleOver()) {
            WsClient.get().send(new UnSubscribeBattle(mBattle.getId()));
        }
    }

    private void requestVariety() {
        Client.requestVariety(mBattle.getVarietyId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<Variety>, Variety>() {
                    @Override
                    protected void onRespSuccessData(Variety variety) {
                        mVariety = variety;
                        mBattleOperateView.setVariety(mVariety);
                        initChartViews();
                        mTabLayout.getTabAt(1).select();
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
                if (mVariety != null) {
                    updateChartView(futureData);
                    updateMarketDataView(futureData);
                    if (mVariety.getExchangeStatus() == Variety.EXCHANGE_STATUS_CLOSE) {
                        requestExchangeStatus();
                    }
                    mBattleOperateView.updatePlayersProfit(futureData);
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

    private void requestHoldingOrders() {
        Client.requestCurrentOrders(mBattle.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TradeOrder>>, List<TradeOrder>>() {
                    @Override
                    protected void onRespSuccessData(List<TradeOrder> data) {
                        if (!data.isEmpty()) {
                            for (TradeOrder order : data) {
                                updateHoldingOrders(order, false);
                            }
                        }
                    }
                }).fire();
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
        WsClient.get().send(new QuickMatchLauncher(machType, mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
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

    private void showCancelBattleDialog() {
        SmartDialog.single(getActivity(), getString(R.string.cancel_battle_tip))
                .setTitle(getString(R.string.cancel_battle))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        cancelBattle();
                    }
                }).setNegative(R.string.continue_to_battle, new SmartDialog.OnClickListener() {
            @Override
            public void onClick(Dialog dialog) {
                dialog.dismiss();
            }
        }).show();

    }

    private void cancelBattle() {
        Client.cancelBattle(mBattle.getId()).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        Intent intent = new Intent();
                        intent.putExtra(Launcher.EX_PAYLOAD, Battle.GAME_STATUS_CANCELED);
                        intent.putExtra(Launcher.EX_PAYLOAD_1, mBattle.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                }).fire();
    }

    private void createOrder(int orderDirection) {
        Client.createOrder(mBattle.getId(), orderDirection).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<TradeOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrder> resp) {
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        ToastUtil.show(failedResp.getMsg());
                        if (failedResp.getCode() == GameCode.ORDER_EXISTED) {
                            requestOrderOperationHistory();
                            requestHoldingOrders();
                        }
                    }
                }).fire();
    }

    private void closePosition(int oderId) {
        Client.closePosition(mBattle.getId(), oderId).setTag(TAG)
                .setCallback(new Callback<Resp<TradeOrder>>() {
                    @Override
                    protected void onRespSuccess(Resp<TradeOrder> resp) {
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        ToastUtil.show(failedResp.getMsg());
                        if (failedResp.getCode() == GameCode.ORDER_CLOSED) {
                            requestOrderOperationHistory();
                            requestHoldingOrders();
                        }
                    }
                }).fire();
    }

    private class NetworkReceiver extends Network.NetworkChangeReceiver {

        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                subscribeFutureData();
                subscribeBattle();

                if (StartMatchDialog.getCurrentDialog() == BaseDialog.DIALOG_START_MATCH) {
                    requestFastMatchResult();
                }
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BaseDialog.dismiss(this);
        mTabLayout.removeOnTabSelectedListener(mOnTabSelectedListener);

        GlideApp.with(App.getAppContext()).pauseRequestsRecursive();
    }

    public static class OrderRecordListAdapter extends BaseAdapter {

        private int mOwnerId;
        private Context mContext;
        private List<TradeRecord> mRecordList;

        public OrderRecordListAdapter(Context context) {
            mContext = context;
            mRecordList = new ArrayList<>();
        }

        public void setOwnerId(int ownerId) {
            mOwnerId = ownerId;
        }

        public void setRecordList(List<TradeRecord> list) {
            mRecordList = list;
            notifyDataSetChanged();
        }

        public void addRecord(TradeRecord record) {
            mRecordList.add(record);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mRecordList.size();
        }

        @Override
        public Object getItem(int position) {
            return mRecordList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_battle_record, null, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            TradeRecord record = mRecordList.get(position);
            viewHolder.bindingData(record, record.getUserId() == mOwnerId, mContext);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.myInfo)
            TextView mMyInfo;
            @BindView(R.id.myInfoTime)
            TextView mMyInfoTime;
            @BindView(R.id.userInfo)
            TextView mUserInfo;
            @BindView(R.id.userInfoTime)
            TextView mUserInfoTime;
            @BindView(R.id.point)
            ImageView mPoint;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(TradeRecord item, boolean isOwner, Context context) {
                StringBuilder info = new StringBuilder();
                info.append(FinanceUtil.formatWithScale(item.getOptPrice(), item.getMarketPoint()));
                String time = DateUtil.getBattleFormatTime(item.getOptTime());

                switch (item.getOptStatus()) {
                    case OPT_STATUS_OPEN_POSITION_LONG:
                        info.append(context.getString(R.string.buy_long_open_position));
                        break;
                    case OPT_STATUS_OPEN_POSITION_SHORT:
                        info.append(context.getString(R.string.sell_short_open_position));
                        break;
                    case OPT_STATUS_CLOSE_POSITION_LONG:
                        info.append(context.getString(R.string.buy_long_close_position));
                        break;
                    case OPT_STATUS_CLOSE_POSITION_SHORT:
                        info.append(context.getString(R.string.sell_short_close_position));
                        break;
                }
                if (isOwner) {
                    mMyInfo.setVisibility(VISIBLE);
                    mMyInfoTime.setVisibility(VISIBLE);
                    mUserInfo.setVisibility(GONE);
                    mUserInfoTime.setVisibility(GONE);

                    mMyInfo.setText(info.toString());
                    mMyInfoTime.setText(time);
                    mPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.point_purple));
                } else {
                    mMyInfo.setVisibility(GONE);
                    mMyInfoTime.setVisibility(GONE);
                    mUserInfo.setVisibility(VISIBLE);
                    mUserInfoTime.setVisibility(VISIBLE);

                    mUserInfo.setText(info.toString());
                    mUserInfoTime.setText(time);
                    mPoint.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.point_purple));
                }
            }
        }
    }

}
