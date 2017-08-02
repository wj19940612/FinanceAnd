package com.sbai.finance.fragment.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.setting.ModifySafetyPassActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.model.mine.cornucopia.ExchangeDetailModel;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.RewardSelectMoneyView;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 打赏页面
 */

public class RewardMissDialogFragment extends BaseDialogFragment {
    public static final String TAG = "RewardMissDialogFragment";
    @BindView(R.id.rewardMoneyContent)
    RewardSelectMoneyView mRewardMoneyContent;
    @BindView(R.id.confirmReward)
    TextView mConfirmReward;
    @BindView(R.id.rewardMoney)
    TextView mRewardMoney;
    Unbinder unbinder;
    private RewardInfo mRewardInfo;

    public static RewardMissDialogFragment newInstance() {
        RewardMissDialogFragment fragment = new RewardMissDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof SubmitQuestionActivity) {
            SubmitQuestionActivity activity = (SubmitQuestionActivity) getActivity();
            mRewardInfo = activity.getRewardInfo();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_reward_miss, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (mRewardInfo != null && mRewardInfo.getMoney() > 0) {
            mRewardMoneyContent.setOtherMoney(mRewardInfo.getMoney());
            mRewardMoneyContent.setSelectedIndex(3);
            mRewardMoney.setText(String.valueOf(mRewardInfo.getMoney()));
        }
        if (mRewardInfo != null && mRewardInfo.getMoneyList() != null) {
            mRewardMoneyContent.setMoneyList(mRewardInfo.getMoneyList())
                    .setOnMoneySelectListener(new RewardSelectMoneyView.OnMoneySelectListener() {
                        @Override
                        public void onSelected(int index) {
                            updateMoneyView(index);
                        }
                    });
            if (mRewardInfo.getIndex() >= 0 && mRewardInfo.getIndex() < 3) {
                mRewardMoneyContent.setSelectedIndex(mRewardInfo.getIndex());
                mRewardMoney.setText(String.valueOf(mRewardInfo.getMoneyList().get(mRewardInfo.getIndex()).getMoney()));
            }
        }

    }

    private void updateMoneyView(int index) {
        if (mRewardInfo != null) {
            mRewardInfo.setIndex(index);
        }
        switch (index) {
            case 0:
            case 1:
            case 2:
                if (mRewardInfo != null && mRewardInfo.getMoneyList().size() > 2) {
                    mRewardMoney.setText(String.valueOf(mRewardInfo.getMoneyList().get(index).getMoney()));
                }
                break;
            case 3:
                RewardOtherMoneyDialogFragment.newInstance()
                        .show(getActivity().getSupportFragmentManager());
                dismiss();

        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.confirmReward)
    public void onViewClicked() {
        requestUserHasSafetyPass();
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
                        Launcher.with(getActivity(), ModifySafetyPassActivity.class).putExtra(Launcher.EX_PAYLOAD, false).execute();
                    }
                }).show();
    }

    private void showInputSafetyPassDialog() {
        long rewardMoney = Long.valueOf(mRewardMoney.getText().toString());
        String content = getString(R.string.ingot_number, StrFormatter.getFormIngot(rewardMoney));
        if (mRewardInfo != null) {
            RewardInputSafetyPassDialogFragment.newInstance(mRewardInfo.getId(), content, getString(R.string.deduct), rewardMoney)
                    .show(getActivity().getSupportFragmentManager());
            dismiss();
        }
    }
}
