package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.MyQuestionsActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 打赏选择其他金额的页面
 */

public class RewardOtherMoneyDialogFragment extends DialogFragment {
    @BindView(R.id.dialogClose)
    ImageView mDialogClose;
    @BindView(R.id.otherMoneyContent)
    EditText mOtherMoneyContent;
    @BindView(R.id.confirm)
    TextView mConfirm;
    Unbinder unbinder;
    @BindView(R.id.warnTip)
    TextView mWarnTip;
    private String mContent;
    private RewardInfo mRewardInfo;

    public static RewardOtherMoneyDialogFragment newInstance() {
        RewardOtherMoneyDialogFragment fragment = new RewardOtherMoneyDialogFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BindBankHintDialog);
        if (getActivity() instanceof QuestionDetailActivity) {
            QuestionDetailActivity activity = (QuestionDetailActivity) getActivity();
            mRewardInfo = activity.getRewardInfo();
        }
        if (getActivity() instanceof MyQuestionsActivity) {
            MyQuestionsActivity activity = (MyQuestionsActivity) getActivity();
            mRewardInfo = activity.getRewardInfo();
        }
        if (getActivity() instanceof MissProfileActivity) {
            MissProfileActivity activity = (MissProfileActivity) getActivity();
            mRewardInfo = activity.getRewardInfo();
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_reward_other_money, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.75), WindowManager.LayoutParams.WRAP_CONTENT);
        }
        mOtherMoneyContent.addTextChangedListener(mValidationWatcher);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOtherMoneyContent.removeTextChangedListener(mValidationWatcher);
        if (mOtherMoneyContent.getText().toString().isEmpty()) {
            RewardMissDialogFragment.newInstance()
                    .show(getActivity().getSupportFragmentManager());
        } else {
            if (mRewardInfo != null) {
                mRewardInfo.setMoney(Long.valueOf(mOtherMoneyContent.getText().toString().replace(",", "")));
            }
            RewardMissDialogFragment.newInstance()
                    .show(getActivity().getSupportFragmentManager());
        }
        unbinder.unbind();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            String content = mOtherMoneyContent.getText().toString();
            if (content.isEmpty()) return;
            if (!content.substring(content.length() - 1).matches("[0-9]")) {
                mOtherMoneyContent.setText(content.subSequence(0, content.length() - 1));
                return;
            }
            if (mContent.equalsIgnoreCase(content)) return;
            if (content.length() > 15) {
                rewardOverMost();
                return;
            }
            long money = Long.valueOf(content.replace(",", ""));
            if (money < 10) {
                rewardLowLeast();
            } else if (money > 10000000) {
                rewardOverMost();
            } else {
                mOtherMoneyContent.setTextColor(ContextCompat.getColor(getContext(), R.color.primaryText));
                mWarnTip.setVisibility(View.INVISIBLE);
                mConfirm.setEnabled(true);
            }
            mOtherMoneyContent.setText(FinanceUtil.formatWithThousandsSeparatorAndScale(money, 0));
            mOtherMoneyContent.setSelection(mOtherMoneyContent.getText().length());
        }

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            super.beforeTextChanged(charSequence, i, i1, i2);
            mContent = charSequence.toString();
        }
    };

    private void rewardLowLeast() {
        mOtherMoneyContent.setTextColor(ContextCompat.getColor(getContext(), R.color.redAssist));
        mWarnTip.setText(getString(R.string.at_least_reward_ten_ingot));
        mWarnTip.setVisibility(View.VISIBLE);
        mConfirm.setEnabled(false);
    }

    private void rewardOverMost() {
        mOtherMoneyContent.setTextColor(ContextCompat.getColor(getContext(), R.color.redAssist));
        mWarnTip.setText(getString(R.string.most_reward_ten_million_ingot));
        mWarnTip.setVisibility(View.VISIBLE);
        mConfirm.setEnabled(false);
    }

    public void show(FragmentManager manager) {
        super.show(manager, getClass().getSimpleName());
    }

    @OnClick({R.id.dialogClose, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogClose:
                dismiss();
                break;
            case R.id.confirm:
                dismiss();
                break;
        }
    }
}
