package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.fragment.battle.BattleFragment;
import com.sbai.finance.fragment.battle.BattleResultDialogFragment;
import com.sbai.finance.fragment.battle.FutureBattleDetailFragment;
import com.sbai.finance.fragment.battle.StartGameDialogFragment;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.BattleInfo;
import com.sbai.finance.model.battle.BattleRoom;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.SmartDialog;
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
import com.sbai.httplib.BuildConfig;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_DRAW;
import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_LOSE;
import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_WIN;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_CANCELED;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_END;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_STARTED;
import static com.sbai.finance.model.battle.BattleRoom.ROOM_STATE_CREATE;
import static com.sbai.finance.model.battle.BattleRoom.ROOM_STATE_END;
import static com.sbai.finance.model.battle.BattleRoom.ROOM_STATE_START;
import static com.sbai.finance.model.battle.BattleRoom.USER_STATE_OBSERVER;
import static com.sbai.finance.websocket.cmd.QuickMatchLauncher.TYPE_CANCEL;
import static com.sbai.finance.websocket.cmd.QuickMatchLauncher.TYPE_QUICK_MATCH;

public class BattleActivity extends BaseActivity implements BattleButtons.OnViewClickListener {

    public static final String PAGE_TYPE = "page_type";
    //0 对战记录 1 对战中
    public static final int PAGE_TYPE_RECORD = 0;
    public static final int PAGE_TYPE_VERSUS = 1;

    @BindView(R.id.content)
    LinearLayout mFutureArea;
    @BindView(R.id.battleView)
    BattleFloatView mBattleView;

    @BindView(R.id.loading)
    ImageView mLoading;
    @BindView(R.id.loadingContent)
    LinearLayout mLoadingContent;

    private BattleFragment mBattleFragment;
    private FutureBattleDetailFragment mFutureBattleDetailFragment;

    private StartMatchDialogFragment mStartMatchDialogFragment;
    private StartGameDialogFragment mStartGameDialogFragment;

    private ShareDialogFragment mShareDialogFragment;

    private SmartDialog mCancelMatchDialog;
    private SmartDialog mOvertimeMatchDialog;
    private SmartDialog mCancelBattleDialog;
    private SmartDialog mMatchConfirmDialog;

    private Battle mBattle;
    private BattleInfo mBattleInfo;
    private BattleRoom mBattleRoom;
    private int mPageType;
    private HashSet<Long> mSet;

    private BattleResultDialogFragment mBattleResultDialogFragment = null;

    @Override
    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
        super.onBattlePushReceived(battleWSPush);

        if (BuildConfig.DEBUG) ToastUtil.show(battleWSPush.getContent().getType());

        //对战详情只能收到有人加入推送
        if (mBattleRoom == null) {
            if (battleWSPush.getContent().getType() == PushCode.BATTLE_JOINED) {
                if (battleWSPush.getContent() != null) {
                    Battle data = (Battle) battleWSPush.getContent().getData();
                    showQuickJoinBattleDialog(data);
                }
            }
            return;
        }

        switch (battleWSPush.getContent().getType()) {
            case PushCode.BATTLE_JOINED:
                //观战中 也可以弹出有人加入
                if (mBattleRoom.getUserState() == USER_STATE_OBSERVER) {
                    if (battleWSPush.getContent() != null) {
                        Battle data = (Battle) battleWSPush.getContent().getData();
                        showQuickJoinBattleDialog(data);
                    }
                }
                //初始化底部栏  取消一切弹窗 显示交易视图 开始计时
                if (mBattleRoom.getRoomState() != ROOM_STATE_START) {
                    dismissAllDialog();
                    startGame(battleWSPush);
                }
                break;
            case PushCode.BATTLE_OVER:
                //对战结束 一个弹窗
                if (mBattleRoom.getUserState() != USER_STATE_OBSERVER
                        && mBattleRoom.getRoomState() != ROOM_STATE_END) {
                    mBattleRoom.setRoomState(ROOM_STATE_END);
                    requestBattleInfo();
                }
                break;
            case PushCode.ORDER_CREATED:
                requestBattleInfo();
                if (mBattleFragment.isVisible()) {
                    mBattleFragment.refreshTradeView();
                }
                break;
            case PushCode.ORDER_CLOSE:
                requestBattleInfo();
                if (mBattleFragment.isVisible()) {
                    mBattleFragment.refreshTradeView();
                }
                break;
            case PushCode.QUICK_MATCH_SUCCESS:
                //和对战有人加入逻辑一样 多了个匹配头像
                break;
            case PushCode.QUICK_MATCH_TIMEOUT:
                //匹配超时逻辑 只有在快速匹配的情况下才会匹配超时
                dismissQuickMatchDialog();
                showOvertimeMatchDialog();
                break;
            case PushCode.ROOM_CREATE_TIMEOUT:
                showRoomOvertimeDialog();
                break;
            case PushCode.USER_PRAISE:
                Battle temp = (Battle) battleWSPush.getContent().getData();
                updatePraiseView(temp.getCurrentPraise(), temp.getPraiseUserId());
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
                                        .putExtra(Launcher.EX_PAYLOAD, battle)
                                        .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
                                        .execute();
                                finish();
                            }
                        }).setNegative(R.string.cancel)
                        .show();
            }
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle);
        ButterKnife.bind(this);

        initData();

        initViews();
    }

    private void initData() {
        mBattle = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        mPageType = getIntent().getIntExtra(PAGE_TYPE, PAGE_TYPE_RECORD);
        //push handle
        int battleId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        String batchCode = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);
        if (mBattle == null && !TextUtils.isEmpty(batchCode)) {
            requestLastBattleInfo(battleId, batchCode);
        }
        mSet = new HashSet<>();
    }

    private void initViews() {
        if (mBattle == null) return;
        if (mPageType == PAGE_TYPE_RECORD) {
            initBattleRecordPage();
        } else {
            if (!LocalUser.getUser().isLogin()) {
                Launcher.with(BattleActivity.this, MainActivity.class).execute();
                finish();
                return;
            }
            mBattleRoom = BattleRoom.getInstance(mBattle, LocalUser.getUser().getUserInfo().getId());
            initBattlePage();
        }
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
                            } else {
                                mPageType = data.getGameStatus();
                            }
                            initViews();
                            if (mBattleRoom != null && mBattleRoom.getRoomState() != ROOM_STATE_END) {
                                requestBattleInfo();
                            }
                        }
                    }
                }).fire();
    }

    public void initBattlePage() {
        if (mBattleFragment == null) {
            mBattleFragment = BattleFragment.newInstance(mBattle);
        }
        if (mBattleResultDialogFragment == null) {
            mBattleResultDialogFragment = new BattleResultDialogFragment();
            mBattleResultDialogFragment.setOnCloseListener(new BattleResultDialogFragment.OnCloseListener() {
                @Override
                public void onClose() {
                    finish();
                }
            });
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, mBattleFragment)
                .commitAllowingStateLoss();

        //观战模式  刷新底部框 可以点赞
        if (mBattleRoom.getUserState() == USER_STATE_OBSERVER) {
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
            if (mBattleRoom.getRoomState() == ROOM_STATE_CREATE) {
                mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), true);
            } else if (mBattleRoom.getRoomState() == ROOM_STATE_START) {
                mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false);
                startScheduleJob(1000);
            }
        }

        requestBattleInfo();
    }

    private void dismissAllDialog() {
        dismissQuickMatchDialog();
        if (mShareDialogFragment != null) {
            mShareDialogFragment.dismiss();
        }
        if (mCancelMatchDialog != null) {
            mCancelMatchDialog.dismiss();
        }
        if (mOvertimeMatchDialog != null) {
            mOvertimeMatchDialog.dismiss();
        }
        if (mCancelBattleDialog != null) {
            mCancelBattleDialog.dismiss();
        }
        if (mMatchConfirmDialog != null) {
            mMatchConfirmDialog.dismiss();
        }
    }

    private void dismissQuickMatchDialog() {
        if (mStartMatchDialogFragment != null
                && mStartMatchDialogFragment.getDialog() != null
                && mStartMatchDialogFragment.getDialog().isShowing()) {
            mStartMatchDialogFragment.dismiss();
        }
    }

    private void startGame(WSPush<Battle> objectWSPush) {
        mBattle = (Battle) objectWSPush.getContent().getData();
        mBattleView.initWithModel(mBattle);
        mBattleView.setProgress(0, 0, false);
        mBattleFragment.showBattleTradeView();
        mBattleFragment.updateGameInfo(mBattle);
        mBattleRoom.setRoomState(ROOM_STATE_START);
        showStartGameDialog();
        startScheduleJob(1000);
    }

    public void initBattleRecordPage() {
        if (mFutureBattleDetailFragment == null) {
            mFutureBattleDetailFragment = FutureBattleDetailFragment.newInstance(mBattle);
        }

        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.content, mFutureBattleDetailFragment)
                .commitAllowingStateLoss();

        mBattleView.setMode(BattleFloatView.Mode.MINE)
                .initWithModel(mBattle)
                .setDeadline(mBattle.getGameStatus(), 0)
                .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                .setWinResult(mBattle.getWinResult());
    }

    private void requestSubscribeBattle() {
        WsClient.get().send(new SubscribeBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {

            }

            @Override
            public void onError(int code) {
            }

        });
    }

    private void requestUnSubscribeBattle() {
        WsClient.get().send(new UnSubscribeBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
            }

            @Override
            public void onError(int code) {
            }

        });
    }

    private void requestBattleInfo() {
        WsClient.get().send(new CurrentBattle(mBattle.getId(), mBattle.getBatchCode()),
                new WSCallback<WSMessage<Resp<BattleInfo>>>() {
                    @Override
                    public void onResponse(WSMessage<Resp<BattleInfo>> respWSMessage) {
                        if (respWSMessage.getContent().isSuccess()) {
                            mBattleInfo = respWSMessage.getContent().getData();
                            //更新左右点赞数
                            updatePraiseView(mBattleInfo.getLaunchPraise(), mBattleInfo.getLaunchUser());
                            updatePraiseView(mBattleInfo.getAgainstPraise(), mBattleInfo.getAgainstUser());
                            //2017/7/4 从匹配或者创建房间过渡到游戏开始 如果直接过渡到结束 另外处理 最后一种过渡到房间取消
                            if (mBattleRoom.getRoomState() == ROOM_STATE_CREATE) {
                                if (mBattleInfo.getGameStatus() == GAME_STATUS_STARTED) {
                                    mBattleRoom.setRoomState(ROOM_STATE_START);
                                    //切换状态
                                    dismissAllDialog();
                                    updateRoomState(ROOM_STATE_START, mBattleInfo);
                                }
                                if (mBattleInfo.getGameStatus() == GAME_STATUS_END) {
                                    mBattleRoom.setRoomState(ROOM_STATE_END);
                                    //切换状态
                                    updateRoomState(ROOM_STATE_END, mBattleInfo);

                                }
                                if (mBattleInfo.getGameStatus() == GAME_STATUS_CANCELED) {
                                    dismissAllDialog();
                                    showRoomOvertimeDialog();
                                }

                            }
                            //正在对战中
                            if (mBattleRoom.getRoomState() == ROOM_STATE_START) {
                                if (mBattleFragment.isVisible()) {
                                    mBattleFragment.refreshTradeView();
                                }
                            }
                            //游戏结束后
                            if (mBattleInfo.getGameStatus() == GAME_STATUS_END) {
                                mBattleRoom.setRoomState(ROOM_STATE_END);
                            }
                            if (mBattleRoom.getRoomState() == ROOM_STATE_END
                                    && mBattleRoom.getUserState() != USER_STATE_OBSERVER) {
                                dismissCalculatingView();
                                updateBattleInfo();
                                if (mSet.add(mBattleInfo.getEndTime())) {
                                    showGameOverDialog();
                                }
                            }
                        }
                    }
                });

    }

    private void updateRoomState(int state, BattleInfo info) {
        mBattle = BattleInfo.getBattle(info);
        mBattleView.initWithModel(mBattle);
        mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false);
        mBattleFragment.showBattleTradeView();
        mBattleFragment.updateGameInfo(mBattle);
        mBattleFragment.refreshTradeView();
        if (state == ROOM_STATE_START) {
            startScheduleJob(1000);
        }
    }

    public void updateBattleInfo(double createProfit, double againstProfit) {
        double leftProfit = createProfit;
        double rightProfit = againstProfit;
        boolean isInviting = mBattleRoom.getRoomState() == ROOM_STATE_CREATE;
        if (mBattleInfo != null) {
            leftProfit += mBattleInfo.getLaunchUnwindScore();
            rightProfit += mBattleInfo.getAgainstUnwindScore();
        }
        if (mBattleRoom.getRoomState() != ROOM_STATE_END) {
            mBattleView.setProgress(leftProfit, rightProfit, isInviting);
        }
    }

    //结束比赛后调用
    private void updateBattleInfo() {
        boolean isInviting = mBattleRoom.getRoomState() == ROOM_STATE_CREATE;
        if (mBattleInfo != null) {
            mBattleView.setProgress(mBattleInfo.getLaunchScore(), mBattleInfo.getAgainstScore(), isInviting);
        }
        mBattleView.setDeadline(mBattleInfo.getGameStatus(), -1);
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
            String url = "";
            mShareDialogFragment = ShareDialogFragment
                    .newInstance()
                    .setShareMode(true)
                    .setShareContent(BattleActivity.this, shareTitle, shareDescribe, mBattle.getBatchCode());
        }
        mShareDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onMatchButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_FAST_MATCH);
        showMatchConfirmDialog();
    }

    //初始化开始游戏弹窗
    private void showStartGameDialog() {
        if (mStartGameDialogFragment == null) {
            mStartGameDialogFragment = StartGameDialogFragment
                    .newInstance(mBattle.getAgainstUserPortrait());
        }
        mStartGameDialogFragment.showAsync(getSupportFragmentManager());
    }

    //开始匹配弹窗
    private void showMatchDialog() {
        if (mStartMatchDialogFragment == null) {
            mStartMatchDialogFragment = StartMatchDialogFragment
                    .newInstance()
                    .setOnCancelListener(new StartMatchDialogFragment.OnCancelListener() {
                        @Override
                        public void onCancel() {
                            mStartMatchDialogFragment.dismiss();
                            showCancelMatchDialog();
                        }
                    });
        }
        mStartMatchDialogFragment.show(getSupportFragmentManager());
    }

    //取消匹配弹窗
    private void showCancelMatchDialog() {
        if (mCancelMatchDialog == null) {
            mCancelMatchDialog = SmartDialog.with(getActivity(), getString(R.string.cancel_tip), getString(R.string.cancel_matching))
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
                    });
        }
        mCancelMatchDialog.show();

    }

    private void requestQuickSearchForLaunch(final int type) {
        WsClient.get().send(new QuickMatchLauncher(type, mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                if (type == TYPE_QUICK_MATCH) {
                    showMatchDialog();
                }

                if (type == TYPE_CANCEL) {
                    mCancelMatchDialog.dismiss();
                }
            }

            @Override
            public void onError(int code) {
            }

        });
    }

    private void showMatchConfirmDialog() {
        if (mMatchConfirmDialog == null) {
            mMatchConfirmDialog = SmartDialog.with(getActivity(), getString(R.string.start_match_and_battle), getString(R.string.match_battle))
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
                    });
        }

        mMatchConfirmDialog.show();
    }

    //匹配超时弹窗
    private void showOvertimeMatchDialog() {
        if (mOvertimeMatchDialog == null) {
            mOvertimeMatchDialog = SmartDialog.with(getActivity(), getString(R.string.match_overtime), getString(R.string.match_failed))
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
                    });
        }
        mOvertimeMatchDialog.show();

    }

    //房间超时弹窗
    private void showRoomOvertimeDialog() {
        SmartDialog.with(getActivity(), getString(R.string.wait_to_overtime), getString(R.string.wait_overtime))
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setNegative(R.string.recreate_room, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(BattleActivity.this, CreateBattleActivity.class).execute();
                        finish();
                    }
                })
                .setCancelableOnTouchOutside(false)
                .setCancelableOnTouchOutside(false)
                .show();
    }

    @Override
    public void onCancelButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_CANCEL_BATTLE);
        showCancelBattleDialog();
    }

    //取消对战弹窗
    private void showCancelBattleDialog() {
        if (mCancelBattleDialog == null) {
            mCancelBattleDialog = SmartDialog.with(getActivity(), getString(R.string.cancel_battle_tip), getString(R.string.cancel_battle))
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
                    });
        }
        mCancelBattleDialog.show();

    }

    private void showGameOverDialog() {

        if (mBattleInfo.getWinResult() == 0) {
            //平局
            mBattleResultDialogFragment.setResult(GAME_RESULT_DRAW);
            mBattleResultDialogFragment.setContent(getString(R.string.return_reward));
        } else {
            boolean win = getWinResult();

            String coinType = getCoinType(mBattleInfo);

            if (win) {
                mBattleResultDialogFragment.setResult(GAME_RESULT_WIN);
                mBattleResultDialogFragment.setContent("+" + (mBattleInfo.getReward() - mBattleInfo.getCommission()) + coinType);
            } else {
                mBattleResultDialogFragment.setResult(GAME_RESULT_LOSE);
                mBattleResultDialogFragment.setContent("-" + mBattleInfo.getReward() + coinType);
            }
        }

        mBattleResultDialogFragment.show(getSupportFragmentManager());

    }

    private boolean getWinResult() {
        boolean win = false;
        //我是房主
        if (mBattle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId()) {
            //result ==1为房主赢
            if (mBattleInfo.getWinResult() == 1) {
                //我赢了
                win = true;
            } else {
                //我输了
                win = false;
            }
        } else {
            //我不是房主
            if (mBattleInfo.getWinResult() == 2) {
                //我赢了
                win = true;
            } else {
                //我输了
                win = false;
            }
        }
        return win;
    }

    private String getCoinType(BattleInfo battleInfo) {
        if (battleInfo.getCoinType() == 2) {
            return getString(R.string.ingot);
        }
        return getString(R.string.integral);
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

    //快速匹配结果查询
    private void requestFastMatchResult() {
        Client.getQuickMatchResult(Battle.CREATE_FAST_MATCH, mBattle.getId()).setTag(TAG)
                .setCallback(new ApiCallback<Resp<Battle>>() {
                    @Override
                    public void onSuccess(Resp<Battle> battleResp) {
                        if (battleResp.getCode() == Battle.CODE_CREATE_FAST_MATCH_TIMEOUT) {
                            dismissQuickMatchDialog();
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {

                    }
                }).fireFree();
    }

    @Override
    public void onTimeUp(int count) {
        if (mBattleRoom.getUserState() == USER_STATE_OBSERVER
                || mBattleRoom.getRoomState() == ROOM_STATE_START) {
            showDeadlineTime();
        }
    }

    private void showDeadlineTime() {
        long currentTime = SysTime.getSysTime().getSystemTimestamp();
        long startTime = mBattle.getStartTime();
        int diff = mBattle.getEndline() - DateUtil.getDiffSeconds(currentTime, startTime);
        if (diff == 0 && mBattleRoom.getUserState() != USER_STATE_OBSERVER) {
            showCalculatingView();
        }
        if (diff == 0 && mBattleRoom.getUserState() == USER_STATE_OBSERVER) {
            if (mBattleFragment.isVisible()) {
                mBattleFragment.refreshTradeView();
            }
        }
        //5秒没收到结果自动结算
        if (diff == -5 && mBattleRoom.getUserState() != USER_STATE_OBSERVER
                && mBattleRoom.getRoomState() != ROOM_STATE_END) {
            requestBattleInfo();
        }
        mBattleView.setDeadline(mBattle.getGameStatus(), diff);
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
    protected void onPostResume() {
        super.onPostResume();
        //判断游戏是否结束
        if (mBattleRoom != null && mBattleRoom.getRoomState() != ROOM_STATE_END) {
            requestBattleInfo();
        }
        //正在快速匹配的要检测快速匹配结果
        if (mStartMatchDialogFragment != null
                && mStartMatchDialogFragment.getDialog() != null
                && mStartMatchDialogFragment.getDialog().isShowing()) {
            requestFastMatchResult();
        }
        if (mPageType == PAGE_TYPE_VERSUS) {
            requestSubscribeBattle();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mPageType == PAGE_TYPE_VERSUS) {
            requestUnSubscribeBattle();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScheduleJob();
        mSet.clear();
    }
}
