package com.sbai.finance.activity.arena.klinebattle;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.arena.KLineResultActivity;
import com.sbai.finance.activity.arena.KlineRankListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.activity.web.LocalImageHtmlActivity;
import com.sbai.finance.fragment.dialog.KlineBattleRecordFragment;
import com.sbai.finance.game.callback.OnPushReceiveListener;
import com.sbai.finance.kgame.GamePusher;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.klinebattle.BattleKline;
import com.sbai.finance.model.klinebattle.BattleKlineConf;
import com.sbai.finance.model.klinebattle.BattleKlineInfo;
import com.sbai.finance.model.klinebattle.BattleKlineMatch;
import com.sbai.finance.model.klinebattle.BattleKlineMyRecord;
import com.sbai.finance.model.mutual.ArticleProtocol;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.BaseDialog;
import com.sbai.finance.view.dialog.BattleKlineMatchSuccessDialog;
import com.sbai.finance.view.dialog.StartMatchDialog;
import com.sbai.glide.GlideApp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * K线对决选择页面
 */

public class BattleKlineActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.rank)
    TextView mRank;
    @BindView(R.id.fourPkIngot)
    TextView mFourPkIngot;
    @BindView(R.id.onePkIngot)
    TextView mOnePkIngot;

    private ImageView mAvatar;
    private TextView mIngot;
    private TextView mRecharge;
    private BaseDialog mStartMatchDialog;

    private BroadcastReceiver mLoginBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LoginActivity.ACTION_LOGIN_SUCCESS)) {
                GamePusher.get().open();
                updateAvatar();
                requestUserFindInfo();
            }
        }
    };
    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                GamePusher.get().open();
                requestUserFindInfo();
                requestBattleConf();
            }
        }
    };

    private OnPushReceiveListener<BattleKlineMatch> mKlineBattlePushReceiverListener = new OnPushReceiveListener<BattleKlineMatch>() {
        @Override
        public void onPushReceive(BattleKlineMatch battleKlineMatch, String originalData) {
            if (battleKlineMatch != null) {
                onBattleKlinePushReceived(battleKlineMatch);
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_kline);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData(getIntent());
        initTitleView();
        initBroadcastReceiver();
        requestBattleConf();
        GamePusher.get().open();
    }

    private void initData(Intent intent) {
        String type = intent.getStringExtra(ExtraKeys.GUESS_TYPE);
        if (!TextUtils.isEmpty(type)) {
            judgeCurrentBattle(type);
        }
    }

    private void requestBattleConf() {
        Client.requestKlineBattleConf().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<BattleKlineConf>>, List<BattleKlineConf>>() {
                    @Override
                    protected void onRespSuccessData(List<BattleKlineConf> data) {
                        updateBattleConf(data);
                    }
                }).fireFree();
    }

    private void updateBattleConf(List<BattleKlineConf> data) {
        if (data == null) return;
        for (BattleKlineConf battleKlineConf : data) {
            if (battleKlineConf == null) return;
            if (battleKlineConf.getBattleType().equalsIgnoreCase(BattleKline.TYPE_4V4)) {
                mFourPkIngot.setText(getString(R.string.number_ingot, String.valueOf(battleKlineConf.getBounty())));
            }
            if (battleKlineConf.getBattleType().equalsIgnoreCase(BattleKline.TYPE_1V1)) {
                mOnePkIngot.setText(getString(R.string.number_ingot, String.valueOf(battleKlineConf.getBounty())));
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLoginBroadcastReceiver);
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
        GamePusher.get().removeOnPushReceiveListener();
        GamePusher.get().close();
    }

    private void initTitleView() {
        View view = mTitleBar.getCustomView();
        mAvatar = view.findViewById(R.id.avatar);
        mIngot = view.findViewById(R.id.ingot);
        mRecharge = view.findViewById(R.id.recharge);
        TextView myBattleResult = view.findViewById(R.id.myBattleResult);
        mRecharge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventId.FUTURE_PK_RECHARGE);
                openWalletPage();
            }
        });
        mIngot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWalletPage();
            }
        });
        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                lookBattleResult();
            }
        });
        myBattleResult.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                umengEventCount(UmengCountEventId.FUTURE_PK_RULE);
                lookBattleResult();
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lookBattleRule();
            }
        });
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginBroadcastReceiver, intentFilter);

        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    private void openWalletPage() {
        if (LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), WalletActivity.class).execute();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void lookBattleRule() {
        Client.getArticleProtocol(ArticleProtocol.PROTOCOL_BATTLE_KLINE).setTag(TAG)
                .setCallback(new Callback2D<Resp<ArticleProtocol>, ArticleProtocol>() {
                    @Override
                    protected void onRespSuccessData(ArticleProtocol data) {
                        Launcher.with(getActivity(), LocalImageHtmlActivity.class)
                                .putExtra(WebActivity.EX_TITLE, getString(R.string.game_help))
                                .putExtra(WebActivity.EX_HTML, data.getContent())
                                .execute();
                    }

                }).fire();
    }

    private void lookBattleResult() {
        if (LocalUser.getUser().isLogin()) {
            showMyRecordDialog();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void showMyRecordDialog() {
        Client.requestKlineBattleMyRecord()
                .setCallback(new Callback2D<Resp<BattleKlineMyRecord>, BattleKlineMyRecord>() {
                    @Override
                    protected void onRespSuccessData(BattleKlineMyRecord data) {
                        KlineBattleRecordFragment.newInstance(data).show(getSupportFragmentManager());
                    }
                }).fireFree();
    }

    private void requestUserFindInfo() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestUserFundInfo()
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                        @Override
                        protected void onRespSuccessData(UserFundInfo data) {
                            updateUserFund(data);
                        }
                    })
                    .fireFree();
        }
    }

    private void updateUserFund(UserFundInfo data) {
        if (data == null) return;
        mIngot.setText(getString(R.string.battle_list_ingot_number, StrFormatter.formIngotNumber(data.getYuanbao())));
    }

    private void updateAvatar() {
        if (LocalUser.getUser().isLogin()) {
            GlideApp.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .circleCrop()
                    .into(mAvatar);
        } else {
            mAvatar.setImageResource(R.drawable.ic_default_avatar);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (LocalUser.getUser().isLogin()) {
            requestUserFindInfo();
        } else {
            mIngot.setText(R.string.not_login);
        }
        updateAvatar();
        GamePusher.get().setOnPushReceiveListener(mKlineBattlePushReceiverListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @OnClick({R.id.fourPk, R.id.onePk, R.id.exercise, R.id.rank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.fourPk:
                judgeCurrentBattle(BattleKline.TYPE_4V4);
                break;
            case R.id.onePk:
                judgeCurrentBattle(BattleKline.TYPE_1V1);
                break;
            case R.id.exercise:
                judgeCurrentBattle(BattleKline.TYPE_EXERCISE);
                break;
            case R.id.rank:
                Launcher.with(getActivity(), KlineRankListActivity.class).execute();
                break;
        }
    }

    protected void onBattleKlinePushReceived(final BattleKlineMatch battleKlineMatch) {
        if (battleKlineMatch.getCode() == BattleKline.PUSH_CODE_MATCH_FAILED) {
            showMatchTimeoutDialog(battleKlineMatch.getBattleType());
        } else if (battleKlineMatch.getCode() == BattleKline.PUSH_CODE_MATCH_SUCCESS) {
            if (mStartMatchDialog != null) {
                if ((battleKlineMatch.getBattleType().equalsIgnoreCase(BattleKline.TYPE_1V1) && battleKlineMatch.getUserMatchList().size() == 2)
                        || battleKlineMatch.getBattleType().equalsIgnoreCase(BattleKline.TYPE_4V4) && battleKlineMatch.getUserMatchList().size() == 4) {
                    SmartDialog.dismiss(this);
                    BattleKlineMatchSuccessDialog.get(getActivity(), battleKlineMatch.getUserMatchList(), new BattleKlineMatchSuccessDialog.OnDismissListener() {
                        @Override
                        public void onDismiss() {
                            Launcher.with(getActivity(), BattleKlineDetailActivity.class)
                                    .putExtra(ExtraKeys.GUESS_TYPE, battleKlineMatch.getBattleType())
                                    .execute();
                        }
                    });
                    return;
                }
                setMatchedPeople(battleKlineMatch.getUserMatchList().size());
            }
        }
    }

    private void judgeCurrentBattle(final String type) {
        if (LocalUser.getUser().isLogin()) {
            Client.getCurrentKlineBattle().setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<BattleKlineInfo>>() {
                        @Override
                        protected void onRespSuccess(final Resp<BattleKlineInfo> resp) {
                            if (resp.getData() == null) {
                                if (type.equalsIgnoreCase(BattleKline.TYPE_EXERCISE)) {
                                    Launcher.with(getActivity(), SingleKlineExerciseActivity.class).execute();
                                } else {
                                    requestMatch(type);
                                }
                            } else {
                                if (resp.getData().getStatus() == BattleKline.STATUS_BATTLING) {
                                    if (type.equalsIgnoreCase(resp.getData().getBattleType())) {
                                        goBattlingActivity(resp.getData().getBattleType());
                                    } else {
                                        goBattlingActivityWithDialog(resp.getData().getBattleType());
                                    }
                                } else if (resp.getData().getStatus() == BattleKline.STATUS_END) {
                                    if (type.equalsIgnoreCase(resp.getData().getBattleType())) {
                                        goBattleResultActivity(resp.getData().getBattleType());
                                    } else {
                                        goBattleResultActivityWithDialog(resp.getData().getBattleType());
                                    }
                                }
                            }
                        }
                    }).fireFree();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void goBattlingActivityWithDialog(final String type) {
        SmartDialog.single(getActivity(), getString(R.string.you_have_batting_please_go_to_see))
                .setPositive(R.string.go_to_see, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        goBattlingActivity(type);
                    }
                })
                .setCancelableOnTouchOutside(false)
                .setNegativeVisible(View.GONE)
                .show();
    }

    private void goBattlingActivity(final String type) {
        Launcher.with(getActivity(), BattleKlineDetailActivity.class)
                .putExtra(ExtraKeys.GUESS_TYPE, type)
                .execute();
    }

    private void goBattleResultActivity(final String type) {
        Launcher.with(getActivity(), KLineResultActivity.class)
                .putExtra(ExtraKeys.GUESS_TYPE, type)
                .execute();

    }

    private void goBattleResultActivityWithDialog(final String type) {
        SmartDialog.single(getActivity(), getString(R.string.you_have_batting_please_go_to_see))
                .setPositive(R.string.go_to_see, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        goBattleResultActivity(type);
                    }
                })
                .setCancelableOnTouchOutside(false)
                .setNegativeVisible(View.GONE)
                .show();
    }

    private void requestMatch(final String type) {
        Client.requestKlineBattleMatch(type).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        showStartMatchDialog(type);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Battle.CODE_NO_ENOUGH_MONEY) {
                            showRechargeDialog(failedResp.getMsg());
                        } else if (failedResp.getCode() == Resp.CODE_BATTLE_NOW_MATCH_1V1) {
                            showStartMatchDialog(BattleKline.TYPE_1V1);
                        } else if (failedResp.getCode() == Resp.CODE_BATTLE_NOW_MATCH_4V4) {
                            showStartMatchDialog(BattleKline.TYPE_4V4);
                        }
                    }
                }).fireFree();
    }

    private void showStartMatchDialog(final String type) {
        final boolean showMatchedAmount = type.equalsIgnoreCase(BattleKline.TYPE_4V4);
        SmartDialog.dismiss(this);
        mStartMatchDialog = StartMatchDialog.get(getActivity(), new StartMatchDialog.OnCancelListener() {
            @Override
            public void onCancel() {
                StartMatchDialog.dismiss(getActivity());
                showCancelMatchDialog(type);
            }
        }, showMatchedAmount);
    }

    private void showRechargeDialog(String msg) {
        SmartDialog.single(getActivity(), msg)
                .setTitle(getString(R.string.match_failed))
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        openWalletPage();
                    }
                })
                .setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void showCancelMatchDialog(final String type) {
        final boolean showMatchedAmount = type.equalsIgnoreCase(BattleKline.TYPE_4V4);
        SmartDialog.single(getActivity(), getString(R.string.cancel_tip))
                .setTitle(getString(R.string.cancel_matching))
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestCancelMatch(type);
                    }
                })
                .setNegative(R.string.continue_match, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        mStartMatchDialog = StartMatchDialog.get(getActivity(), new StartMatchDialog.OnCancelListener() {
                            @Override
                            public void onCancel() {
                                StartMatchDialog.dismiss(getActivity());
                                showCancelMatchDialog(type);
                            }
                        }, showMatchedAmount);
                    }
                })
                .show();
    }

    private void showMatchTimeoutDialog(final String type) {
        StartMatchDialog.dismiss(getActivity());
        String msg;
        if (type.equalsIgnoreCase(BattleKline.TYPE_1V1)) {
            msg = getString(R.string.no_people_match_please_try_later);
        } else {
            msg = getString(R.string.no_match_people_please_try_1v1);
        }
        SmartDialog.single(getActivity(), msg)
                .setTitle(getString(R.string.match_failed))
                .setCancelableOnTouchOutside(false)
                .setPositive(R.string.rematch, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestMatch(type);
                    }
                })
                .setNegative(R.string.later_try_again, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    private void setMatchedPeople(int count) {
        if (mStartMatchDialog != null && mStartMatchDialog.getCustomView() != null) {
            TextView roomPeople = mStartMatchDialog.getCustomView().findViewById(R.id.roomPeople);
            if (roomPeople != null) {
                roomPeople.setText(String.valueOf(count));
            }
        }
    }

    private void requestCancelMatch(String type) {
        Client.requestKlineBattleCancleMatch(type).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }
                }).fireFree();

    }
}