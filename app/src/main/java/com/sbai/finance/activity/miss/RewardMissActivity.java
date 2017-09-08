package com.sbai.finance.activity.miss;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.activity.mine.setting.UpdateSecurityPassActivity;
import com.sbai.finance.fragment.dialog.RewardInputSafetyPassDialogFragment;
import com.sbai.finance.fragment.dialog.RewardOtherMoneyDialogFragment;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.RewardSelectMoneyView;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 姐说打赏页面
 */

public class RewardMissActivity extends BaseActivity {
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
    private int mId;
    private int mType;
    private int mSelectedIndex = -1;

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
    }

    private void initView() {
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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
                                if (mRewardMoneyContent.getOtherMoney() == 0) {
                                    mRewardMoneyContent.clearSelected();
                                }
                                if (mSelectedIndex != -1) {
                                    mRewardMoneyContent.setSelectedIndex(mSelectedIndex);
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

    @OnClick({R.id.cancelArea, R.id.confirmReward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.cancelArea:
                finish();
                break;
            case R.id.confirmReward:
                requestUserFindInfo();
                break;
        }
    }

    private void requestUserFindInfo() {
        umengEventCount(UmengCountEventIdUtils.MISS_TALK_REWARD);
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        if (data.getYuanbao() < Long.valueOf(mRewardMoney.getText().toString())) {
                            showRechargeDialog(getActivity());
                        } else {
                            requestUserHasSafetyPass();
                        }
                    }
                })
                .fireFree();
    }

    private void requestUserHasSafetyPass() {
        Client.getUserHasPassWord()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        if (!data) {
                            showAddSafetyPassDialog();
                        } else {
                            showInputSafetyPassDialog();
                        }
                    }
                })
                .fire();
    }

    private void showAddSafetyPassDialog() {
        mRewardArea.setVisibility(View.GONE);
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
                        Launcher.with(getActivity(), UpdateSecurityPassActivity.class).putExtra(Launcher.EX_PAYLOAD, false).execute();
                    }
                }).show();
    }

    private void showRechargeDialog(final FragmentActivity activity) {
        mRewardArea.setVisibility(View.GONE);
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
                        Launcher.with(activity, CornucopiaActivity.class).execute();
                    }
                })
                .setNegative(R.string.cancel)
                .show();
    }

    private void showInputSafetyPassDialog() {
        mRewardArea.setVisibility(View.GONE);
        long rewardMoney = mRewardMoneyContent.getSelectedMoney();
        String content = getString(R.string.ingot_number, StrFormatter.getFormIngot(rewardMoney));
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
}
