package com.sbai.finance.activity.future;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.LinearLayout;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.fragment.dialog.StartMatchDialogFragment;
import com.sbai.finance.fragment.future.FutureBattleDetailFragment;
import com.sbai.finance.fragment.future.FutureBattleFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.BattleButtons;
import com.sbai.finance.view.BattleFloatView;
import com.sbai.finance.view.BattleTradeView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.websocket.WSClient;
import com.sbai.finance.websocket.WSMessage;
import com.sbai.finance.websocket.WSPush;
import com.sbai.finance.websocket.callback.OnPushReceiveListener;
import com.sbai.finance.websocket.callback.WSCallback;
import com.sbai.finance.websocket.cmd.QuickMatchLauncher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_CREATED;
import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_OBESERVE;
import static com.sbai.finance.model.versus.VersusGaming.GAME_STATUS_STARTED;
import static com.sbai.finance.model.versus.VersusGaming.PAGE_RECORD;
import static com.sbai.finance.utils.TimerHandler.WATCH_REFRESH_TIME;

/**
 * Created by linrongfang on 2017/6/19.
 */

public class FutureBattleActivity extends BaseActivity implements
        BattleButtons.OnViewClickListener, BattleTradeView.OnViewClickListener {

    @BindView(R.id.futureArea)
    LinearLayout mFutureArea;
    @BindView(R.id.battleView)
    BattleFloatView mBattleView;

    private FutureBattleFragment mFutureBattleFragment;
    private FutureBattleDetailFragment mFutureBattleDetailFragment;
    private StartMatchDialogFragment mStartMatchDialogFragment;

    private VersusGaming mVersusGaming;
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
                                    .putExtra(Launcher.USER_ID,mVersusGaming.getLaunchUser())
                                    .execute();
                        }

                        @Override
                        public void onAgainstAvatarClick() {
                            Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                    .putExtra(Launcher.USER_ID,mVersusGaming.getAgainstUser())
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
            requestSubscribeBattle();

        } else {
            //初始化
            mBattleView.setMode(BattleFloatView.Mode.MINE)
                    .initWithModel(mVersusGaming);

            //分两种状态  1.发起匹配  2.对战中
            int gameStatus = mVersusGaming.getGameStatus();
            if (gameStatus == GAME_STATUS_CREATED) {
                mBattleView.setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), true);
            } else if (gameStatus == GAME_STATUS_STARTED) {
                mBattleView.setProgress(mVersusGaming.getLaunchScore(), mVersusGaming.getAgainstScore(), false);
                startScheduleJob(1000);
            }
        }

        WSClient.get().setOnPushReceiveListener(new OnPushReceiveListener<WSPush<VersusGaming>>() {
            @Override
            public void onPushReceive(WSPush<VersusGaming> versusGamingWSPush) {
                Log.d(TAG, "onPushReceive: " + versusGamingWSPush);
                // TODO: 26/06/2017 sample
            }
        });
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
                                .putExtra(Launcher.USER_ID,mVersusGaming.getLaunchUser())
                                .execute();
                    }

                    @Override
                    public void onAgainstAvatarClick() {
                        Launcher.with(FutureBattleActivity.this, UserDataActivity.class)
                                .putExtra(Launcher.USER_ID,mVersusGaming.getAgainstUser())
                                .execute();
                    }
                });
    }

    private void requestSubscribeBattle(){
        Client.requestSubscribeBattle(mVersusGaming.getId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {

                    }
                })
                .fire();
    }

    private void requestUnSubscribeBattle() {
        if (mVersusGaming.getGameStatus() == GAME_STATUS_OBESERVE) {
            Client.requestUnsubscribeBattle(mVersusGaming.getId())
                    .setTag(TAG)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {

                        }
                    })
                    .fire();
        }
    }

    private void requestAddBattlePraise(final int userId) {
        Client.addBattlePraise(mVersusGaming.getId(), userId)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Integer>>() {
                    @Override
                    protected void onRespSuccess(Resp<Integer> resp) {
                        if (resp.isSuccess()){
                            int data = resp.getData();
                            updatePraiseView(data,userId);
                        }else {
                            ToastUtil.curt(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void updatePraiseView(int count, int userId) {
        boolean isLeft = userId == mVersusGaming.getLaunchUser();
        if (isLeft){
            mVersusGaming.setLaunchPraise(count);
        }else {
            mVersusGaming.setAgainstPraise(count);
        }
        mBattleView.setPraiseLight(isLeft);
        mBattleView.setPraise(mVersusGaming.getLaunchPraise(), mVersusGaming.getAgainstPraise());
    }

    @Override
    public void onInviteButtonClick() {
         showInviteDialog();
    }

    private void showInviteDialog() {
        // TODO: 2017/6/22 分享
    }

    @Override
    public void onMatchButtonClick() {
        showMatchDialog();
        requestQuickSearchForLaunch(QuickMatchLauncher.TYPE_QUICK_MATCH);
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
        SmartDialog.with(getActivity(), getString(R.string.cancel_tip), getString(R.string.cancel_matching))
                .setMessageTextSize(15)
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestQuickSearchForLaunch(QuickMatchLauncher.TYPE_CANCEL);
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
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    private void requestQuickSearchForLaunch(int type) {
        WSClient.get().send(new QuickMatchLauncher(type, mVersusGaming.getId()), new WSCallback<WSMessage<Resp>>() {
            @Override
            public void onResponse(WSMessage<Resp> respWSMessage) {

            }

            @Override
            public void onError(int code) {

            }

        });
    }

    private void requestCancelBattle(){
        Client.cancelBattle(mVersusGaming.getId())
                .setTag(TAG)
                .setCallback(new Callback<VersusGaming>() {
                    @Override
                    protected void onRespSuccess(VersusGaming resp) {

                    }
                })
                .fire();
    }

    //超时弹窗
    private void showOvertimeMatchDialog() {
        SmartDialog.with(getActivity(), getString(R.string.match_overtime), getString(R.string.match_failed))
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
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    @Override
    public void onCancelButtonClick() {
        showCancelBattleDialog();
    }

    private void showCancelBattleDialog() {
        SmartDialog.with(getActivity(), getString(R.string.cancel_battle_tip), getString(R.string.cancel_battle))
                .setMessageTextSize(15)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setNegative(R.string.continue_to_battle, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        // TODO: 2017/6/22 退出房间 退出失败弹提示
                    }
                })
                .setTitleMaxLines(1)
                .setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
                .setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
                .show();

    }

    @Override
    public void onLongPurchaseButtonClick() {
        requestCreateOrder(1);
    }

    @Override
    public void onShortPurchaseButtonClick() {
        requestCreateOrder(0);
    }

    private void requestCreateOrder(int direction) {
        Client.createOrder(mVersusGaming.getId(), direction)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(null)
                .fire();
    }

    @Override
    public void onClosePositionButtonClick() {
          requestClosePosition(0);
    }

    private void requestClosePosition(int orderId){
       Client.closePosition(mVersusGaming.getId(),orderId)
               .setTag(TAG)
               .setCallback(null)
               .fire();
    }

    @Override
    public void onTimeUp(int count) {
        //观战 底部栏每秒刷 交易内容与点赞20s刷一次
        if (mGameStatus == GAME_STATUS_OBESERVE) {
            if (count % WATCH_REFRESH_TIME == 0) {
                requestBattleData();
                mFutureBattleFragment.requestOrderHistory();
            }
            int temp = (int) (mVersusGaming.getEndTime() - SysTime.getSysTime().getSystemTimestamp())/1000;
            int endTime = temp - count;
            mBattleView.setDeadline(mVersusGaming.getGameStatus(), endTime);
        }
    }

    private void requestBattleData() {
        Client.getBattleGamingData(String.valueOf(mVersusGaming.getId())).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<VersusGaming>>, List<VersusGaming>>() {
                    @Override
                    protected void onRespSuccessData(List<VersusGaming> data) {
                        updateBattleData(data);
                    }
                }).fire();
    }

    private void updateBattleData(List<VersusGaming> data) {
        if (data.isEmpty()) return;

        VersusGaming item = data.get(0);
        mVersusGaming.setLaunchScore(item.getLaunchScore());
        mVersusGaming.setAgainstScore(item.getAgainstScore());
        mVersusGaming.setLaunchPraise(item.getLaunchPraise());
        mVersusGaming.setAgainstPraise(item.getAgainstPraise());
        mVersusGaming.setGameStatus(item.getGameStatus());

        mBattleView.setPraise(mVersusGaming.getLaunchPraise(),mVersusGaming.getAgainstPraise())
                .setProgress(mVersusGaming.getLaunchScore(),mVersusGaming.getAgainstScore(),false);

    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        requestUnSubscribeBattle();
    }
}
