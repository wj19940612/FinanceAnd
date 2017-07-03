package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
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
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.websocket.PushCode;
import com.sbai.finance.websocket.WSClient;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.CancelBattle;
import com.sbai.finance.websocket.cmd.CurrentBattle;
import com.sbai.finance.websocket.cmd.QuickMatchLauncher;
import com.sbai.finance.websocket.cmd.SubscribeBattle;
import com.sbai.finance.websocket.cmd.UnSubscribeBattle;
import com.sbai.finance.websocket.cmd.UserPraise;
import com.sbai.httplib.BuildConfig;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_DRAW;
import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_LOSE;
import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_WIN;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_END;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_OBESERVE;
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

    private BattleFragment mBattleFragment;
    private FutureBattleDetailFragment mFutureBattleDetailFragment;

    private StartMatchDialogFragment mStartMatchDialogFragment;
    private StartGameDialogFragment mStartGameDialogFragment;

    private ShareDialogFragment mShareDialogFragment;

    private SmartDialog mCancelMatchDialog;
    private SmartDialog mOvertimeMatchDialog;
    private SmartDialog mCancelBattleDialog;

    private Battle mBattle;
    private BattleInfo mBattleInfo;
    private BattleRoom mBattleRoom;
    private int mPageType;

    @Override
    protected void onBattlePushReceived(WSPush<Battle> battleWSPush) {
        super.onBattlePushReceived(battleWSPush);

        if (BuildConfig.DEBUG) ToastUtil.show(battleWSPush.getContent().getType());

        switch (battleWSPush.getContent().getType()) {
            case PushCode.BATTLE_JOINED:
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
                mBattleFragment.refreshTradeView();
                break;
            case PushCode.ORDER_CLOSE:
                requestBattleInfo();
                mBattleFragment.refreshTradeView();
                break;
            case PushCode.QUICK_MATCH_SUCCESS:
                //和对战有人加入逻辑一样 多了个匹配头像
                break;
            case PushCode.QUICK_MATCH_TIMEOUT:
                //匹配超时逻辑 只有在快速匹配的情况下才会匹配超时
                mStartMatchDialogFragment.dismiss();
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
        mBattleRoom = BattleRoom.getInstance(mBattle, LocalUser.getUser().getUserInfo().getId());
    }

    private void initViews() {
        if (mPageType == PAGE_TYPE_RECORD) {
            initBattleRecordPage();
        } else {
            initBattlePage();
        }
    }

    public void initBattlePage() {
        if (mBattleFragment == null) {
            mBattleFragment = BattleFragment.newInstance(mBattle);
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
                    .setOnAvatarClickListener(new BattleFloatView.onAvatarClickListener() {
                        @Override
                        public void onCreateAvatarClick() {
                            umengEventCount(UmengCountEventIdUtils.BATTLE_USER_AVATAR);
                            if (LocalUser.getUser().isLogin()) {
                                Launcher.with(BattleActivity.this, UserDataActivity.class)
                                        .putExtra(Launcher.USER_ID, mBattle.getLaunchUser())
                                        .execute();
                            } else {
                                Launcher.with(getActivity(), LoginActivity.class).execute();
                            }
                        }

                        @Override
                        public void onAgainstAvatarClick() {
                            umengEventCount(UmengCountEventIdUtils.BATTLE_USER_AVATAR);
                            if (LocalUser.getUser().isLogin()) {
                                Launcher.with(BattleActivity.this, UserDataActivity.class)
                                        .putExtra(Launcher.USER_ID, mBattle.getAgainstUser())
                                        .execute();
                            } else {
                                Launcher.with(getActivity(), LoginActivity.class).execute();
                            }
                        }
                    })
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

        requestSubscribeBattle();
        requestBattleInfo();
    }

    private void dismissAllDialog() {
        if (mStartMatchDialogFragment != null) {
            mStartMatchDialogFragment.dismiss();
        }
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
                .setWinResult(mBattle.getWinResult())
                .setOnAvatarClickListener(new BattleFloatView.onAvatarClickListener() {
                    @Override
                    public void onCreateAvatarClick() {
                        if (LocalUser.getUser().isLogin()) {
                            Launcher.with(BattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, mBattle.getLaunchUser())
                                    .execute();
                        } else {
                            Launcher.with(getActivity(), LoginActivity.class).execute();
                        }
                    }

                    @Override
                    public void onAgainstAvatarClick() {
                        if (LocalUser.getUser().isLogin()) {
                            Launcher.with(BattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, mBattle.getAgainstUser())
                                    .execute();
                        } else {
                            Launcher.with(getActivity(), LoginActivity.class).execute();
                        }
                    }
                });
    }

    private void requestSubscribeBattle() {
        WSClient.get().send(new SubscribeBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {

            }

            @Override
            public void onError(int code) {
            }

        });
    }

    private void requestUnSubscribeBattle() {
        if (mBattle.getGameStatus() == GAME_STATUS_OBESERVE) {
            WSClient.get().send(new UnSubscribeBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
                @Override
                public void onResponse(WSMessage<Resp> respWSMessage) {
                }

                @Override
                public void onError(int code) {
                }

            });
        }
    }

    private void requestBattleInfo() {
        WSClient.get().send(new CurrentBattle(mBattle.getId(), mBattle.getBatchCode()),
                new WSCallback<WSMessage<Resp<BattleInfo>>>() {
                    @Override
                    public void onResponse(WSMessage<Resp<BattleInfo>> respWSMessage) {
                        if (respWSMessage.getContent().isSuccess()) {
                            mBattleInfo = respWSMessage.getContent().getData();
                            //更新左右点赞数
                            updatePraiseView(mBattleInfo.getLaunchPraise(), mBattleInfo.getLaunchUser());
                            updatePraiseView(mBattleInfo.getAgainstPraise(), mBattleInfo.getAgainstUser());
                            //游戏结束后
                            if (mBattleInfo.getGameStatus() == GAME_STATUS_END) {
                                mBattleRoom.setRoomState(ROOM_STATE_END);
                            }
                            if (mBattleRoom.getRoomState() == ROOM_STATE_END) {
                                showGameOverDialog();
                            }
                        }
                    }
                });

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

    private void requestAddBattlePraise(final int userId) {
        umengEventCount(UmengCountEventIdUtils.WITNESS_BATTLE_PRAISE);
        WSClient.get().send(new UserPraise(mBattle.getId(), userId), new WSCallback<WSMessage<Resp<Integer>>>() {
            @Override
            public void onResponse(WSMessage<Resp<Integer>> respWSMessage) {
                setPraiseLight(userId);
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

    private void setPraiseLight(int userId) {
        boolean isLeft = userId == mBattle.getLaunchUser();
        mBattleView.setPraiseLight(isLeft);
    }


    @Override
    public void onInviteButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_INVITE_FRIENDS);
        showInviteDialog();
    }

    private void showInviteDialog() {
        if (mShareDialogFragment == null) {
            String shareTitle = getString(R.string.invite_you_join_future_battle
                    , LocalUser.getUser().getUserInfo().getUserName());
            String shareDescribe = getString(R.string.future_battle_desc);
            String url = "";
            mShareDialogFragment = ShareDialogFragment
                    .newInstance()
                    .setShareMode(true)
                    .setShareContent(BattleActivity.this, shareTitle, shareDescribe, url);
        }
        mShareDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onMatchButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_FAST_MATCH);
        requestQuickSearchForLaunch(TYPE_QUICK_MATCH);
    }

    //初始化开始游戏弹窗
    private void showStartGameDialog() {
        if (mStartGameDialogFragment == null) {
            mStartGameDialogFragment = StartGameDialogFragment
                    .newInstance(mBattle.getAgainstUserPortrait());
        }
        mStartGameDialogFragment.show(getSupportFragmentManager());
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
        WSClient.get().send(new QuickMatchLauncher(type, mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
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
                        Launcher.with(BattleActivity.this, CreateFightActivity.class).execute();
                        finish();
                    }
                })
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
        BattleResultDialogFragment fragment = null;

        if (mBattleInfo.getWinResult() == 0) {
            //平局
            fragment = BattleResultDialogFragment
                    .newInstance(GAME_RESULT_DRAW, getString(R.string.return_reward));
        } else {
            boolean win = getWinResult();

            String coinType = getCoinType(mBattleInfo);

            if (win) {
                fragment = BattleResultDialogFragment
                        .newInstance(GAME_RESULT_WIN, "+" + mBattleInfo.getReward() + coinType);
            } else {
                fragment = BattleResultDialogFragment
                        .newInstance(GAME_RESULT_LOSE, "-" + mBattleInfo.getReward() + coinType);
            }
        }

        final BattleResultDialogFragment finalFragment = fragment;
        fragment.setOnCloseListener(new BattleResultDialogFragment.OnCloseListener() {
            @Override
            public void onClose() {
                finalFragment.dismissAllowingStateLoss();
                finish();
            }
        });

        // handle  java.lang.IllegalStateException: Can not perform this action after onSaveInstanceState
//        if (fragment.isResumed()) {
//            fragment.show(getSupportFragmentManager());
//        }
        fragment.show(getSupportFragmentManager());

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
        WSClient.get().send(new CancelBattle(mBattle.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {
                Intent intent = new Intent();
                intent.putExtra(Launcher.EX_PAYLOAD, Battle.GAME_STATUS_CANCELED);
                intent.putExtra(Launcher.EX_PAYLOAD_1, mBattle.getId());
                setResult(RESULT_OK, intent);
                finish();
            }

            @Override
            public void onError(int code) {
                ToastUtil.curt(getString(R.string.cancel_failed_game_start));
            }

        });
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
        mBattleView.setDeadline(mBattle.getGameStatus(), diff);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        //判断游戏是否结束
        if (mBattleRoom.getRoomState() != ROOM_STATE_END) {
            requestBattleInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScheduleJob();
        requestUnSubscribeBattle();
    }
}
