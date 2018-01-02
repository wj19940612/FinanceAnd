package com.sbai.finance.activity.anchor;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.activity.mine.setting.SecurityCenterActivity;
import com.sbai.finance.activity.mine.setting.UpdateSecurityPassActivity;
import com.sbai.finance.fragment.dialog.RewardInputSafetyPassDialogFragment;
import com.sbai.finance.fragment.dialog.RewardOtherMoneyDialogFragment;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.model.anchor.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.audio.OnPlayRadioManager;
import com.sbai.finance.view.RewardSelectMoneyView;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 姐说打赏页面
 */

public class RewardMissActivity extends BaseActivity implements OnPlayRadioManager {


    private static final int SMALL_NO_SECRET_PAYMENT = 100;

    @BindView(R.id.cancelArea)
    View mCancelArea;
    @BindView(R.id.rewardMoneyContent)
    RewardSelectMoneyView mRewardMoneyContent;
    @BindView(R.id.confirmReward)
    TextView mConfirmReward;
    @BindView(R.id.rewardMoney)
    TextView mRewardMoney;
    @BindView(R.id.rewardArea)
    FrameLayout mRewardArea;
    @BindView(R.id.openAllowAvoidClosePay)
    TextView mOpenAllowAvoidClosePay;
    private int mId;
    private int mType;
    private int mSelectedIndex = -1;

    private boolean mIsAllowAvoidClosePay;
    private boolean mHasSafetyPass;

    public static void show(Activity activity, int id, int type) {
        Launcher.with(activity, RewardMissActivity.class)
                .putExtra(Launcher.EX_PAYLOAD, id)
                .putExtra(Launcher.EX_PAYLOAD_1, type)
                .execute();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reward_miss);
        ButterKnife.bind(this);

        initData(getIntent());
        initView();
        requestUserHasSafetyPass();
    }


    private void requestUserHasSafetyPass() {
        Client.getUserHasPassWord()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        mHasSafetyPass = data;
                        if (data) {
                            requestUserSmallNoSecretPaymentStatus();
                        }
                    }
                })
                .fire();
    }

    private void requestUserSmallNoSecretPaymentStatus() {
        //想增加个缓存
        Client.requestUserSmallNoSecretPaymentStatus()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        mIsAllowAvoidClosePay = data;
                        changeSmallFreePayHintViewStatus(data);
                    }
                })
                .fireFree();
    }

    private void changeSmallFreePayHintViewStatus(Boolean data) {
        if (data) {
            mOpenAllowAvoidClosePay.setVisibility(View.INVISIBLE);
        } else {
            mOpenAllowAvoidClosePay.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        mRewardMoneyContent.setOnSelectedCallback(new RewardSelectMoneyView.OnSelectedCallback() {
            @Override
            public void selected(long money) {
                mSelectedIndex = mRewardMoneyContent.getSelectedIndex();
                mRewardMoney.setText((String.valueOf(money)));
                mConfirmReward.setEnabled(true);
            }

            @Override
            public void selectedOther() {
                mRewardArea.setVisibility(View.GONE);
                RewardOtherMoneyDialogFragment.newInstance(mRewardMoneyContent.getOtherMoney())
                        .setOnDismissListener(new RewardOtherMoneyDialogFragment.OnDismissListener() {
                            @Override
                            public void onDismiss() {
                                mRewardArea.setVisibility(View.VISIBLE);
                                if (mSelectedIndex != -1) {
                                    mRewardMoney.setText(String.valueOf(mRewardMoneyContent.getSelectedMoney()));
                                    mConfirmReward.setEnabled(true);
                                }

                            }
                        })
                        .setOnSelectMoneyCallback(new RewardOtherMoneyDialogFragment.OnSelectMoneyCallback() {
                            @Override
                            public void selectedMoney(long money) {
                                mRewardArea.setVisibility(View.VISIBLE);
                                mSelectedIndex = mRewardMoneyContent.getSelectedIndex();
                                mRewardMoneyContent.setOtherMoney(money);
                                mRewardMoney.setText((String.valueOf(money)));
                                mConfirmReward.setEnabled(true);
                            }
                        })
                        .show(getSupportFragmentManager());
            }
        });
    }

    private void initData(Intent intent) {
        mId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        mType = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        if (mRewardArea.getVisibility() == View.GONE) {
            mRewardArea.setVisibility(View.VISIBLE);
        }
    }

    @OnClick({R.id.cancelArea, R.id.confirmReward, R.id.openAllowAvoidClosePay})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancelArea:
                finish();
                break;
            case R.id.confirmReward:
                requestUserFindInfo();
                break;
            case R.id.openAllowAvoidClosePay:
                Launcher.with(getActivity(), SecurityCenterActivity.class)
                        .putExtra(ExtraKeys.HAS_SECURITY_PSD, mHasSafetyPass)
                        .executeForResult(SecurityCenterActivity.REQ_CODE_ALLOW_SMALL_NO_SECRET_PAYMENT);
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, R.anim.slide_out_to_bottom);
    }

    private void requestUserFindInfo() {
        umengEventCount(UmengCountEventId.MISS_TALK_REWARD);
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfo data) {
                        if (data.getYuanbao() < Long.valueOf(mRewardMoney.getText().toString())) {
                            showRechargeDialog(getActivity(), data);
                        } else {
                            if (mIsAllowAvoidClosePay && SMALL_NO_SECRET_PAYMENT >= Long.valueOf(mRewardMoney.getText().toString())) {
                                smallNoSecretReward();
                            } else {
                                if (!mHasSafetyPass) {
                                    showAddSafetyPassDialog();
                                } else {
                                    showInputSafetyPassDialog();
                                }
                            }
                        }
                    }
                })
                .fireFree();
    }

    private void smallNoSecretReward() {
        switch (mType) {
            case RewardInfo.TYPE_MISS:
                rewardToMiss();
                break;
            case RewardInfo.TYPE_QUESTION:
                rewardQuestion();
                break;
        }
    }

    private void rewardQuestion() {
        long rewardMoney = mRewardMoneyContent.getSelectedMoney();
        Client.rewardQuestion(mId, rewardMoney, AccountFundDetail.TYPE_INGOT, null)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(getString(R.string.success_reward));
                        sendRewardSuccessBroadcast(getActivity());
                        finish();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                            finish();
                        } else {
                            if (failedResp.getCode() == Resp.CODE_SAFETY_INPUT_ERROR) {
                            }
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }
                }).fire();
    }

    private void rewardToMiss() {
        long rewardMoney = mRewardMoneyContent.getSelectedMoney();
        Client.rewardMiss(mId, rewardMoney, AccountFundDetail.TYPE_INGOT, null)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        ToastUtil.show(getString(R.string.success_reward));
                        sendRewardSuccessBroadcast(getActivity());
                        finish();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.getCode() == Resp.CODE_EXCHANGE_FUND_IS_NOT_ENOUGH) {
                            finish();
                        } else {
                            if (failedResp.getCode() == Resp.CODE_SAFETY_INPUT_ERROR) {

                            }
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }
                }).fire();
    }

    private void sendRewardSuccessBroadcast(FragmentActivity activity) {
        Log.e("zzz","onOtherRewardBor");
        Intent intent = new Intent();
        intent.setAction(BaseActivity.ACTION_REWARD_SUCCESS);
        intent.putExtra(Launcher.EX_PAYLOAD, mType);
        intent.putExtra(Launcher.EX_PAYLOAD_1, mId);
        LocalBroadcastManager.getInstance(activity).sendBroadcast(intent);
    }

    private void showAddSafetyPassDialog() {
        mRewardArea.setVisibility(View.INVISIBLE);
        SmartDialog.with(getActivity(), getString(R.string.is_not_set_safety_pass))
                .setOnDismissListener(new SmartDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Dialog dialog) {
                        mRewardArea.setVisibility(View.VISIBLE);
                    }
                })
                .setPositive(R.string.go_to_set, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), UpdateSecurityPassActivity.class).putExtra(Launcher.EX_PAYLOAD, false).executeForResult(UpdateSecurityPassActivity.REQ_CODE_SET_SECURITY_PASS);
                    }
                }).show();
    }

    private void showRechargeDialog(final FragmentActivity activity, final UserFundInfo data) {
        mRewardArea.setVisibility(View.INVISIBLE);
        SmartDialog.single(getActivity(), getString(R.string.ignot_not_enough))
                .setOnDismissListener(new SmartDialog.OnDismissListener() {

                    @Override
                    public void onDismiss(Dialog dialog) {
                        mRewardArea.setVisibility(View.VISIBLE);
                    }
                })
                .setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();

                        Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                                .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                                .putExtra(ExtraKeys.USER_FUND, data != null ? data.getMoney() : 0)
                                .execute();
                    }
                })
                .setNegative(R.string.cancel)
                .show();
    }

    private void showInputSafetyPassDialog() {
        mRewardArea.setVisibility(View.INVISIBLE);
        long rewardMoney = mRewardMoneyContent.getSelectedMoney();
        String content = getString(R.string.ingot_number, StrFormatter.formIngotNumber(rewardMoney));
        RewardInputSafetyPassDialogFragment.newInstance(mId,
                content, getString(R.string.reward), rewardMoney, mType)
                .setOnSelectMoneyCallback(new RewardInputSafetyPassDialogFragment.RewardResultCallback() {
                    @Override
                    public void success() {
                        finish();
                    }

                    @Override
                    public void failure() {
                        mRewardArea.setVisibility(View.VISIBLE);
                    }
                })
                .show(getActivity().getSupportFragmentManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case SecurityCenterActivity.REQ_CODE_ALLOW_SMALL_NO_SECRET_PAYMENT:
                    boolean booleanExtra = data.getBooleanExtra(Launcher.EX_PAYLOAD, false);
                    mIsAllowAvoidClosePay = booleanExtra;
                    changeSmallFreePayHintViewStatus(mIsAllowAvoidClosePay);
                    break;
                case UpdateSecurityPassActivity.REQ_CODE_SET_SECURITY_PASS:
                    requestUserHasSafetyPass();
                    break;
            }

        }
    }
}
