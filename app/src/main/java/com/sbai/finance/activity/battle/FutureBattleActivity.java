package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.fragment.battle.BattleResultDialogFragment;
import com.sbai.finance.fragment.battle.FutureBattleDetailFragment;
import com.sbai.finance.fragment.battle.FutureBattleFragment;
import com.sbai.finance.fragment.battle.StartGameDialogFragment;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.BattleInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.websocket.WSClient;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.QuickMatchLauncher;
import com.sbai.finance.websocket.cmd.SubscribeBattle;
import com.sbai.finance.websocket.cmd.UnSubscribeBattle;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_DRAW;
import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_LOSE;
import static com.sbai.finance.fragment.battle.BattleResultDialogFragment.GAME_RESULT_WIN;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_CANCELING;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_CREATED;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_END;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_OBESERVE;
import static com.sbai.finance.model.battle.Battle.GAME_STATUS_STARTED;
import static com.sbai.finance.model.battle.Battle.PAGE_RECORD;
import static com.sbai.finance.websocket.PushCode.BATTLE_JOINED;
import static com.sbai.finance.websocket.PushCode.BATTLE_OVER;
import static com.sbai.finance.websocket.PushCode.ORDER_CLOSE;
import static com.sbai.finance.websocket.PushCode.ORDER_CREATED;
import static com.sbai.finance.websocket.PushCode.QUICK_MATCH_SUCCESS;
import static com.sbai.finance.websocket.PushCode.QUICK_MATCH_TIMEOUT;
import static com.sbai.finance.websocket.PushCode.ROOM_CREATE_TIMEOUT;
import static com.sbai.finance.websocket.PushCode.USER_PRAISE;
import static com.sbai.finance.websocket.cmd.QuickMatchLauncher.TYPE_CANCEL;
import static com.sbai.finance.websocket.cmd.QuickMatchLauncher.TYPE_QUICK_MATCH;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class FutureBattleActivity extends BaseActivity implements BattleButtons.OnViewClickListener {

    @BindView(R.id.futureArea)
    LinearLayout mFutureArea;
    @BindView(R.id.battleView)
    BattleFloatView mBattleView;

    private FutureBattleFragment mFutureBattleFragment;
    private FutureBattleDetailFragment mFutureBattleDetailFragment;

    private StartMatchDialogFragment mStartMatchDialogFragment;
    private StartGameDialogFragment mStartGameDialogFragment;
    private ShareDialogFragment mShareDialogFragment;

    private SmartDialog mCancelMatchDialog;
    private SmartDialog mOvertimeMatchDialog;
    private SmartDialog mCancelBattleDialog;

    private Battle mBattle;
    private BattleInfo mBattleInfo;
    private int mGameStatus;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_battle);
        ButterKnife.bind(this);

        initData();

        initViews();
    }

    private void initData() {
        mBattle = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
    }


    private void initViews() {
        if (mBattle.getPageType() == PAGE_RECORD) {
            showFutureBattleDetail();
        } else {
            showFutureBattle();
        }
    }

    public void showFutureBattle() {
        if (mFutureBattleFragment == null) {
            mFutureBattleFragment = FutureBattleFragment.newInstance(mBattle);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleFragment)
                .commitAllowingStateLoss();

        //观战模式  刷新底部框 可以点赞
        int userId = LocalUser.getUser().getUserInfo().getId();
        if (mBattle.getAgainstUser() != userId && mBattle.getLaunchUser() != userId) {
            mGameStatus = GAME_STATUS_OBESERVE;
            mBattleView.setMode(BattleFloatView.Mode.VISITOR)
                    .initWithModel(mBattle)
                    .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                    .setOnAvatarClickListener(new BattleFloatView.onAvatarClickListener() {
                        @Override
                        public void onCreateAvatarClick() {
                            umengEventCount(UmengCountEventIdUtils.BATTLE_USER_AVATAR);
                            Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, mBattle.getLaunchUser())
                                    .execute();
                        }

                        @Override
                        public void onAgainstAvatarClick() {
                            umengEventCount(UmengCountEventIdUtils.BATTLE_USER_AVATAR);
                            Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, mBattle.getAgainstUser())
                                    .execute();
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
            int gameStatus = mBattle.getGameStatus();
            if (gameStatus == GAME_STATUS_CREATED) {
                mGameStatus = GAME_STATUS_CREATED;
                mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), true);
            } else if (gameStatus == GAME_STATUS_STARTED) {
                mGameStatus = GAME_STATUS_STARTED;
                mBattleView.setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false);
                startScheduleJob(1000);
            }
        }

        initPushReceiveListener();
        requestSubscribeBattle();
        requestBattleInfo();
    }


    private OnPushReceiveListener<WSPush<Battle>> mPushReceiveListener = new OnPushReceiveListener<WSPush<Battle>>() {
        @Override
        public void onPushReceive(WSPush<Battle> versusGamingWSPush) {
            switch (versusGamingWSPush.getContent().getType()) {
                case BATTLE_JOINED:
                    //初始化底部栏  取消一切弹窗 显示交易视图 开始计时
                    if (mGameStatus == GAME_STATUS_CANCELING) {
                        dismissAllDialog();
                        ToastUtil.curt(getString(R.string.cancel_failed_game_start));
                        startGame(versusGamingWSPush);
                    } else {
                        dismissAllDialog();
                        startGame(versusGamingWSPush);
                    }
                    break;
                case BATTLE_OVER:
                    //对战结束 一个弹窗
                    if (mGameStatus != GAME_STATUS_OBESERVE) {
                        mGameStatus = GAME_STATUS_END;
                        requestBattleInfo();
                    }
                    break;
                case ORDER_CREATED:
                    requestBattleInfo();
                    mFutureBattleFragment.refreshTradeView();
                    break;
                case ORDER_CLOSE:
                    requestBattleInfo();
                    mFutureBattleFragment.refreshTradeView();
                    break;
                case QUICK_MATCH_SUCCESS:
                    //和对战有人加入逻辑一样 多了个匹配头像
                    break;
                case QUICK_MATCH_TIMEOUT:
                    //匹配超时逻辑 只有在快速匹配的情况下才会匹配超时
                    mStartMatchDialogFragment.dismiss();
                    showOvertimeMatchDialog();
                    break;
                case ROOM_CREATE_TIMEOUT:
                    showRoomOvertimeDialog();
                    break;
                case USER_PRAISE:
                    Battle temp = (Battle) versusGamingWSPush.getContent().getData();
                    updatePraiseView(temp.getCurrentPraise(), temp.getPraiseUserId(), false);
                    break;

            }
        }
    };

    private void initPushReceiveListener() {
        WSClient.get().setOnPushReceiveListener(mPushReceiveListener);
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
        mFutureBattleFragment.showBattleTradeView();
        mFutureBattleFragment.updateGameInfo(mBattle);
        mGameStatus = GAME_STATUS_STARTED;
        showStartGameDialog();
        startScheduleJob(1000);
    }

    public void showFutureBattleDetail() {
        if (mFutureBattleDetailFragment == null) {
            mFutureBattleDetailFragment = FutureBattleDetailFragment.newInstance(mBattle);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleDetailFragment)
                .commitAllowingStateLoss();

        mBattleView.setMode(BattleFloatView.Mode.MINE)
                .initWithModel(mBattle)
                .setDeadline(mBattle.getGameStatus(), 0)
                .setProgress(mBattle.getLaunchScore(), mBattle.getAgainstScore(), false)
                .setWinResult(mBattle.getWinResult())
                .setOnAvatarClickListener(new BattleFloatView.onAvatarClickListener() {
                    @Override
                    public void onCreateAvatarClick() {
                        Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                .putExtra(Launcher.USER_ID, mBattle.getLaunchUser())
                                .execute();
                    }

                    @Override
                    public void onAgainstAvatarClick() {
                        Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                .putExtra(Launcher.USER_ID, mBattle.getAgainstUser())
                                .execute();
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
        Client.getBattleInfo(mBattle.getId(), mBattle.getBatchCode())
                .setTag(TAG)
                .setCallback(new Callback<Resp<BattleInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<BattleInfo> resp) {
                        if (resp.isSuccess()) {
                            mBattleInfo = resp.getData();
                            //游戏结束后
                            if (mGameStatus == GAME_STATUS_END) {
                                showGameOverDialog();
                            }
                        }
                    }
                })
                .fire();
    }

    public void updateBattleInfo(double createProfit, double againstProfit) {
        double leftProfit = createProfit;
        double rightProfit = againstProfit;
        boolean isInviting = mGameStatus == GAME_STATUS_CREATED;
        if (mBattleInfo != null) {
            leftProfit += mBattleInfo.getLaunchUnwindScore();
            rightProfit += mBattleInfo.getAgainstUnwindScore();
        }
        if (mGameStatus != GAME_STATUS_END) {
            mBattleView.setProgress(leftProfit, rightProfit, isInviting);
        }
    }

    private void requestAddBattlePraise(final int userId) {
        umengEventCount(UmengCountEventIdUtils.WITNESS_BATTLE_PRAISE);
        Client.addBattlePraise(mBattle.getId(), userId)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Integer>>() {
                    @Override
                    protected void onRespSuccess(Resp<Integer> resp) {
                        if (resp.isSuccess()) {
                            int data = resp.getData();
                            updatePraiseView(data, userId, true);
                        } else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void updatePraiseView(int count, int userId, boolean needLight) {
        boolean isLeft = userId == mBattle.getLaunchUser();
        if (isLeft) {
            mBattle.setLaunchPraise(count);
        } else {
            mBattle.setAgainstPraise(count);
        }
        if (needLight) {
            mBattleView.setPraiseLight(isLeft);
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
            String shareTitle = getString(R.string.invite_you_join_future_battle
                    , LocalUser.getUser().getUserInfo().getUserName());
            String shareDescribe = getString(R.string.future_battle_desc);
            String url = "";
            mShareDialogFragment = ShareDialogFragment
                    .newInstance()
                    .setShareMode(true)
                    .setShareContent(FutureBattleActivity.this, shareTitle, shareDescribe, url);
        }
        mShareDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onMatchButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_FAST_MATCH);
        requestQuickSearchForLaunch(TYPE_QUICK_MATCH);
    }

    //初始化开始游戏弹窗
    private void showStartGameDialog(){
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
                    .setNegative(R.string.continue_versus, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            showMatchDialog();
                        }
                    })
                    .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                    .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText));
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
                    .setMessageTextSize(15)
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
                    })
                    .setTitleMaxLines(1)
                    .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                    .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText));
        }
        mOvertimeMatchDialog.show();

    }

    //房间超时弹窗
    private void showRoomOvertimeDialog() {
        SmartDialog.with(getActivity(), getString(R.string.wait_to_overtime), getString(R.string.wait_overtime))
                .setMessageTextSize(15)
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
                        Launcher.with(FutureBattleActivity.this, CreateFightActivity.class).execute();
                        finish();
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    @Override
    public void onCancelButtonClick() {
        umengEventCount(UmengCountEventIdUtils.WAITING_ROOM_CANCEL_BATTLE);
        mGameStatus = GAME_STATUS_CANCELING;
        showCancelBattleDialog();
    }

    //取消对战弹窗
    private void showCancelBattleDialog() {
        if (mCancelBattleDialog == null) {
            mCancelBattleDialog = SmartDialog.with(getActivity(), getString(R.string.cancel_battle_tip), getString(R.string.cancel_battle))
                    .setMessageTextSize(15)
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
                    })
                    .setTitleMaxLines(1)
                    .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                    .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText));
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

        fragment.setOnCloseListener(new BattleResultDialogFragment.OnCloseListener() {
            @Override
            public void onClose() {
                finish();
            }
        });

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
        Client.cancelBattle(mBattle.getId())
                .setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        Intent intent = new Intent();
                        intent.putExtra(Launcher.EX_PAYLOAD, Battle.GAME_STATUS_CANCELED);
                        intent.putExtra(Launcher.EX_PAYLOAD_1, mBattle.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .fire();
    }


    @Override
    public void onTimeUp(int count) {
        if (mGameStatus == GAME_STATUS_OBESERVE) {
            showDeadlineTime();
        }
        if (mGameStatus == GAME_STATUS_STARTED) {
            showDeadlineTime();
        }
    }

    private void showDeadlineTime() {
        long currentTime = System.currentTimeMillis();
        long startTime = mBattle.getStartTime();
        int diff = mBattle.getEndline() - DateUtil.getDiffSeconds(currentTime, startTime);
        mBattleView.setDeadline(mBattle.getGameStatus(), diff);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
//        requestSubscribeBattle();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScheduleJob();
        requestUnSubscribeBattle();
        WSClient.get().removePushReceiveListener(mPushReceiveListener);
    }
}
