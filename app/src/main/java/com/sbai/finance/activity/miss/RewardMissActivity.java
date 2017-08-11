package com.sbai.finance.activity.miss;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.activity.mine.setting.UpdateSecurityPassActivity;
import com.sbai.finance.fragment.dialog.RewardInputSafetyPassDialogFragment;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
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
        SmartDialog.with(getActivity(), getString(R.string.is_not_set_safety_pass))
                .setPositive(R.string.go_to_set, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), UpdateSecurityPassActivity.class).putExtra(Launcher.EX_PAYLOAD, false).execute();
                    }
                }).show();
    }

    private void showRechargeDialog(final FragmentActivity activity) {
        SmartDialog.single(getActivity(), getString(R.string.ignot_not_enough))
                .setPositive(R.string.go_exchange, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(activity, CornucopiaActivity.class).execute();
                    }
                }).setNegative(R.string.cancel)
                .show();
    }

    private void showInputSafetyPassDialog() {
        long rewardMoney = mRewardMoneyContent.getSelectedMoney();
        String content = getString(R.string.ingot_number, StrFormatter.getFormIngot(rewardMoney));
        RewardInputSafetyPassDialogFragment.newInstance(mId,
                content, getString(R.string.reward), rewardMoney, mType)
                .show(getActivity().getSupportFragmentManager());
    }
}
