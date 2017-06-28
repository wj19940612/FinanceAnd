package com.sbai.finance.activity.future;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.fragment.dialog.ShareDialogFragment;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.fragment.future.FutureBattleDetailFragment;
import com.sbai.finance.fragment.future.FutureBattleFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.versus.BattleInfo;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
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

import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_CANCELING;
import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_CREATED;
import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_OBESERVE;
import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_STARTED;
import static com.sbai.finance.model.versus.VersusGaming.PAGE_RECORD;
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
    private ShareDialogFragment mShareDialogFragment;

    private SmartDialog mCancelMatchDialog;
    private SmartDialog mOvertimeMatchDialog;
    private SmartDialog mCancelBattleDialog;

    private VersusGaming mVersusGaming;
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
        mVersusGaming = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
    }


    private void initViews() {
        if (mVersusGaming.getPageType() == PAGE_RECORD) {
            showFutureBattleDetail();
        } else {
            showFutureBattle();
        }
    }

    public void showFutureBattle() {
        if (mFutureBattleFragment == null) {
            mFutureBattleFragment = FutureBattleFragment.newInstance(mVersusGaming);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleFragment)
                .commit();

        //观战模式  刷新底部框 可以点赞
        int userId = LocalUser.getUser().getUserInfo().getId();
        if (mVersusGaming.getAgainstUser() != userId && mVersusGaming.getLaunchUser() != userId) {
            mGameStatus = GAME_STATUS_OBESERVE;
            mBattleView.setMode(BattleFloatView.Mode.VISITOR)
                    .initWithModel(mVersusGaming)
                    .setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), false)
                    .setOnAvatarClickListener(new BattleFloatView.onAvatarClickListener() {
                        @Override
                        public void onCreateAvatarClick() {
                            Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, mVersusGaming.getLaunchUser())
                                    .execute();
                        }

                        @Override
                        public void onAgainstAvatarClick() {
                            Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID, mVersusGaming.getAgainstUser())
                                    .execute();
                        }
                    })
                    .setOnPraiseListener(new BattleFloatView.OnPraiseListener() {
                        @Override
                        public void addCreatePraiseCount() {
                            requestAddBattlePraise(mVersusGaming.getLaunchUser());
                        }

                        @Override
                        public void addAgainstPraiseCount() {
                            requestAddBattlePraise(mVersusGaming.getAgainstUser());
                        }
                    });

            startScheduleJob(1000);

        } else {
            //初始化
            mBattleView.setMode(BattleFloatView.Mode.MINE)
                    .initWithModel(mVersusGaming);

            //分两种状态  1.发起匹配  2.对战中
            int gameStatus = mVersusGaming.getGameStatus();
            if (gameStatus == GAME_STATUS_CREATED) {
                mGameStatus = GAME_STATUS_CREATED;
                mBattleView.setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), true);
            } else if (gameStatus == GAME_STATUS_STARTED) {
                mGameStatus = GAME_STATUS_STARTED;
                mBattleView.setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), false);
                startScheduleJob(1000);
            }
        }

        initPushReceiveListener();
        requestSubscribeBattle();
        requestBattleInfo();
    }


    private OnPushReceiveListener<WSPush<VersusGaming>> mPushReceiveListener = new OnPushReceiveListener<WSPush<VersusGaming>>() {
        @Override
        public void onPushReceive(WSPush<VersusGaming> versusGamingWSPush) {
            switch (versusGamingWSPush.getContent().getType()) {
                case BATTLE_JOINED:
                    //初始化底部栏  取消一切弹窗 显示交易视图 开始计时
                    if (mGameStatus == GAME_STATUS_CANCELING){
                         ToastUtil.curt(getString(R.string.cancel_failed_game_start));
                         startGame(versusGamingWSPush);
                    }else {
                        dismissAllDialog();
                        startGame(versusGamingWSPush);
                    }
                    break;
                case BATTLE_OVER:
                    //对战结束 一个弹窗
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
                    VersusGaming temp = (VersusGaming) versusGamingWSPush.getContent().getData();
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

    private void startGame(WSPush<VersusGaming> objectWSPush) {
        mVersusGaming = (VersusGaming) objectWSPush.getContent().getData();
        mBattleView.initWithModel(mVersusGaming);
        mFutureBattleFragment.showBattleTradeView();
        mGameStatus = GAME_STATUS_STARTED;
        startScheduleJob(1000);
    }

    public void showFutureBattleDetail() {
        if (mFutureBattleDetailFragment == null) {
            mFutureBattleDetailFragment = FutureBattleDetailFragment.newInstance(mVersusGaming);
        }
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.futureArea, mFutureBattleDetailFragment)
                .commit();

        mBattleView.setMode(BattleFloatView.Mode.MINE)
                .initWithModel(mVersusGaming)
                .setDeadline(mVersusGaming.getGameStatus(), 0)
                .setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), false)
                .setWinResult(mVersusGaming.getWinResult())
                .setOnAvatarClickListener(new BattleFloatView.onAvatarClickListener() {
                    @Override
                    public void onCreateAvatarClick() {
                        Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                .putExtra(Launcher.USER_ID, mVersusGaming.getLaunchUser())
                                .execute();
                    }

                    @Override
                    public void onAgainstAvatarClick() {
                        Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                .putExtra(Launcher.USER_ID, mVersusGaming.getAgainstUser())
                                .execute();
                    }
                });
    }

    private void requestSubscribeBattle() {
        WSClient.get().send(new SubscribeBattle(mVersusGaming.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {

            }

            @Override
            public void onError(int code) {
            }

        });
    }

    private void requestUnSubscribeBattle() {
        if (mVersusGaming.getGameStatus() == GAME_STATUS_OBESERVE) {
            WSClient.get().send(new UnSubscribeBattle(mVersusGaming.getId()), new WSCallback<WSMessage<Resp>>() {
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
        Client.getBattleInfo(mVersusGaming.getId(),mVersusGaming.getBatchCode())
                .setTag(TAG)
                .setCallback(new Callback<Resp<BattleInfo>>() {
                    @Override
                    protected void onRespSuccess(Resp<BattleInfo> resp) {
                        if (resp.isSuccess()){
                            mBattleInfo = resp.getData();
                        }
                    }
                })
                .fire();
    }

    public void updateBattleInfo(double createProfit, double againstProfit) {
        double leftProfit = mBattleInfo.getLaunchUnwindScore() + createProfit;
        double rightProfit = mBattleInfo.getAgainstUnwindScore() + againstProfit;
        boolean isInviting = mGameStatus == GAME_STATUS_CREATED;
        mBattleView.setProgress(leftProfit, rightProfit, isInviting);
    }

    private void requestAddBattlePraise(final int userId) {
        Client.addBattlePraise(mVersusGaming.getId(), userId)
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
        boolean isLeft = userId == mVersusGaming.getLaunchUser();
        if (isLeft) {
            mVersusGaming.setLaunchPraise(count);
        } else {
            mVersusGaming.setAgainstPraise(count);
        }
        if (needLight) {
            mBattleView.setPraiseLight(isLeft);
        }
        mBattleView.setPraise(mVersusGaming.getLaunchPraise(), mVersusGaming.getAgainstPraise());
    }


    @Override
    public void onInviteButtonClick() {
        showInviteDialog();
    }

    private void showInviteDialog() {
        if (mShareDialogFragment == null) {
            mShareDialogFragment = ShareDialogFragment.newInstance();
        }
        mShareDialogFragment.show(getSupportFragmentManager());
    }

    @Override
    public void onMatchButtonClick() {
        requestQuickSearchForLaunch(TYPE_QUICK_MATCH);
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
                    .setMessageTextSize(15)
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
                    .setTitleMaxLines(1)
                    .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                    .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText));
        }

        mCancelMatchDialog.show();

    }

    private void requestQuickSearchForLaunch(final int type) {
        WSClient.get().send(new QuickMatchLauncher(type, mVersusGaming.getId()), new WSCallback<WSMessage<Resp>>() {
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
                    }
                })
                .setNegative(R.string.recreate_room, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(FutureBattleActivity.this,CreateFightActivity.class).execute();
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

    private void requestCancelBattle() {
        Client.cancelBattle(mVersusGaming.getId())
                .setTag(TAG)
                .setCallback(new Callback<Resp>() {
                    @Override
                    protected void onRespSuccess(Resp resp) {
                        Intent intent = new Intent();
                        intent.putExtra(Launcher.EX_PAYLOAD, VersusGaming.GAME_STATUS_CANCELED);
                        intent.putExtra(Launcher.EX_PAYLOAD_1, mVersusGaming.getId());
                        setResult(RESULT_OK, intent);
                        finish();
                    }
                })
                .fire();
    }


    @Override
    public void onTimeUp(int count) {
        //观战 底部栏每秒刷 交易内容与点赞20s刷一次
        if (mGameStatus == GAME_STATUS_OBESERVE) {
            showDeadlineTime();
        }
        if (mGameStatus == GAME_STATUS_STARTED) {
            showDeadlineTime();
        }
    }

    private void showDeadlineTime() {
        long currentTime = System.currentTimeMillis();
        long startTime = mVersusGaming.getStartTime();
        int diff = mVersusGaming.getEndline() - DateUtil.getDiffSeconds(currentTime, startTime);
        mBattleView.setDeadline(mVersusGaming.getGameStatus(), diff);
    }


    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopScheduleJob();
        requestUnSubscribeBattle();
        WSClient.get().removePushReceiveListener(mPushReceiveListener);
    }
}
