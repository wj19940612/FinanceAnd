package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 打赏选择其他金额的页面
 */

public class RewardOtherMoneyDialogFragment extends CenterDialogFragment {

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
    private OnSelectMoneyCallback mOnSelectMoneyCallback;
    private OnDismissListener mDismissListener;
    private boolean mSuccess;

    public interface OnDismissListener {
        void onDismiss();
    }

    public RewardOtherMoneyDialogFragment setOnDismissListener(OnDismissListener onDismissListener) {
        mDismissListener = onDismissListener;
        return this;
    }

    public RewardOtherMoneyDialogFragment setOnSelectMoneyCallback(OnSelectMoneyCallback onSelectMoneyCallback) {
        mOnSelectMoneyCallback = onSelectMoneyCallback;
        return this;
    }

    public interface OnSelectMoneyCallback {
        void selectedMoney(long money);
    }


    public static RewardOtherMoneyDialogFragment newInstance() {
        RewardOtherMoneyDialogFragment fragment = new RewardOtherMoneyDialogFragment();
        return fragment;
    }

    public static RewardOtherMoneyDialogFragment newInstance(long money) {
        RewardOtherMoneyDialogFragment fragment = new RewardOtherMoneyDialogFragment();
        Bundle bundle = new Bundle();
        bundle.putLong("money", money);
        fragment.setArguments(bundle);
        return fragment;
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
        mOtherMoneyContent.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mOtherMoneyContent.addTextChangedListener(mValidationWatcher);
        if (getArguments() != null) {
            long money = getArguments().getLong("money", 0);
            if (money > 0) {
                mOtherMoneyContent.setText(String.valueOf(money));
                mConfirm.setEnabled(true);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOtherMoneyContent.removeTextChangedListener(mValidationWatcher);
        if (mDismissListener != null && !mSuccess) {
            mDismissListener.onDismiss();
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
        mConfirm.setEnabled(false);
    }

    private void rewardOverMost() {
        mOtherMoneyContent.setTextColor(ContextCompat.getColor(getContext(), R.color.redAssist));
        mWarnTip.setText(getString(R.string.most_reward_ten_million_ingot));
        mWarnTip.setVisibility(View.VISIBLE);
        mConfirm.setEnabled(false);
    }

    @OnClick({R.id.dialogClose, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogClose:
                dismiss();
                break;
            case R.id.confirm:
                mSuccess = true;
                if (mOnSelectMoneyCallback != null) {
                    mOnSelectMoneyCallback.selectedMoney(Long.valueOf(mOtherMoneyContent.getText().toString().replace(",", "")));
                }
                dismiss();
                break;
        }
    }
}
