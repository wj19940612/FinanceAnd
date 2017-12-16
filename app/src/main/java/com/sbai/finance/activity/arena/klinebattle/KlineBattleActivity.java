package com.sbai.finance.activity.arena.klinebattle;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.arena.KLineResultActivity;
import com.sbai.finance.activity.battle.BattleRuleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.WalletActivity;
import com.sbai.finance.fragment.dialog.KlineBattleRecordFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.klinebattle.BattleKlineConf;
import com.sbai.finance.model.klinebattle.BattleKline;
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

public class KlineBattleActivity extends BaseActivity {
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

    private UserFundInfo mUserFundInfo;
    private BroadcastReceiver mLoginBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(LoginActivity.ACTION_LOGIN_SUCCESS)) {
                updateAvatar();
                requestUserFindInfo();
            }
        }
    };
    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                requestUserFindInfo();
                requestBattleConf();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_battle);
        ButterKnife.bind(this);
        initTitleView();
        initBroadcastReceiver();
        requestBattleConf();
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
        for (BattleKlineConf battleKlineConf : data) {
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
        Launcher.with(getActivity(), BattleRuleActivity.class).execute();
    }

    private void lookBattleResult() {
        if (LocalUser.getUser().isLogin()) {
            new KlineBattleRecordFragment().show(getSupportFragmentManager());
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void requestUserFindInfo() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestUserFundInfo()
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                        @Override
                        protected void onRespSuccessData(UserFundInfo data) {
                            updateUserFund(data);
                            mUserFundInfo = data;
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
                Launcher.with(getActivity(), SingleKlineExerciseActivity.class)
                        .putExtra(ExtraKeys.GUESS_TYPE, BattleKline.TYPE_EXERCISE)
                        .execute();
                break;
            case R.id.rank:
                // TODO: 2017-12-13  
                break;
        }
    }

    private void judgeCurrentBattle(final String type) {
        if (LocalUser.getUser().isLogin()) {
            Client.getCurrentKlineBattle().setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<BattleKline.BattleBean>>() {
                        @Override
                        protected void onRespSuccess(Resp<BattleKline.BattleBean> resp) {
                            if (resp.getData() == null) {
                                showStartMatchDialog(type);
                            } else {
                                if (resp.getData().getStatus() == BattleKline.STATUS_BATTLEING) {
                                    Launcher.with(getActivity(), KlineBattlePkActivity.class)
                                            .putExtra(ExtraKeys.GUESS_TYPE, type)
                                            .execute();
                                } else if (resp.getData().getStatus() == BattleKline.STATUS_END) {
                                    showStartMatchDialog(type);
//                                    Launcher.with(getActivity(), KLineResultActivity.class).execute();
                                }
                            }
                        }

                        @Override
                        protected void onRespFailure(Resp failedResp) {
                            super.onRespFailure(failedResp);
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }).fireFree();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    private void showStartMatchDialog(final String type) {
        final boolean showMatchedAmount = type.equalsIgnoreCase(BattleKline.TYPE_4V4);
        Client.requestKlineBattleMatch(type).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        StartMatchDialog.get(getActivity(), new StartMatchDialog.OnCancelListener() {
                            @Override
                            public void onCancel() {
                                StartMatchDialog.dismiss(getActivity());
                                showCancelMatchDialog(type);
                            }
                        }, showMatchedAmount);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void showCancelMatchDialog(final String type) {
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
                        showStartMatchDialog(type);
                    }
                })
                .show();
    }

    private void requestCancelMatch(String type) {
        Client.requestKlineBattleCancleMatch(type).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {

                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();

    }
}
