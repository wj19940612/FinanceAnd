package com.sbai.finance.activity.mine.wallet;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.fragment.dialog.InputSafetyPassDialogFragment;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WithDrawActivity extends BaseActivity implements InputSafetyPassDialogFragment.OnPasswordListener {


    @BindView(R.id.withDrawRule)
    AppCompatTextView mWithDrawRule;
    @BindView(R.id.withdrawBank)
    AppCompatTextView mWithdrawBank;
    @BindView(R.id.withdrawMoneyHelp)
    TextView mWithdrawMoneyHelp;
    @BindView(R.id.withdrawMoney)
    AppCompatEditText mWithdrawMoney;
    @BindView(R.id.withdrawMoneyLl)
    LinearLayout mWithdrawMoneyLl;
    @BindView(R.id.canWithDrawMoney)
    TextView mCanWithDrawMoney;
    @BindView(R.id.allWithDraw)
    TextView mAllWithDraw;
    @BindView(R.id.can_with_poundage)
    TextView mCanWithPoundage;
    @BindView(R.id.withdraw)
    AppCompatButton mWithdraw;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;
    private UserBankCardInfoModel mUserBankCardInfoModel;
    private int mMoney;

    private String mPassWord;
    private int mWithDrawRuleRes [];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        ButterKnife.bind(this);
        mWithDrawRuleRes = new int[]{R.string.can_with_rule_content_1, R.string.can_with_rule_content_2, R.string.can_with_rule_content_3};

        mUserBankCardInfoModel = getIntent().getParcelableExtra(Launcher.EX_PAY_END);
        mMoney = getIntent().getIntExtra(Launcher.EX_PAYLOAD, 0);
        String withDrawBankAndNumber = "      " + mUserBankCardInfoModel.getIssuingBankName() + "  (" + mUserBankCardInfoModel.getCardNumber().substring(mUserBankCardInfoModel.getCardNumber().length() - 4) + ")";
        mWithdrawBank.setText(getString(R.string.with_draw_bank, withDrawBankAndNumber));
        mWithdrawMoney.addTextChangedListener(mValidationWatcher);
        mCanWithDrawMoney.setText(getString(R.string.can_with_draw_money, FinanceUtil.formatWithScale(mMoney)));

        requestWithDrawPoundage();
    }

    private void requestWithDrawPoundage() {
        Client.requestWithDrawPoundage()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Double>, Double>() {
                    @Override
                    protected void onRespSuccessData(Double data) {
                        mCanWithPoundage.setText(getString(R.string.can_with_poundage, FinanceUtil.formatWithScale(data)));
                    }
                })
                .fire();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean confirmEnable = checkConfirmEnable();
            if (confirmEnable != mWithdraw.isEnabled()) {
                mWithdraw.setEnabled(confirmEnable);
            }
            if (!TextUtils.isEmpty(s.toString())) {
                mWithdrawMoney.setSelection(s.toString().length());
            }
        }
    };

    private boolean checkConfirmEnable() {
        return !TextUtils.isEmpty(mWithdrawMoney.getText().toString());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWithdrawMoney.removeTextChangedListener(mValidationWatcher);
    }

    @OnClick({R.id.withDrawRule, R.id.allWithDraw, R.id.withdraw, R.id.connect_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.withDrawRule:
                showWithDrawRuleDialog();
                break;
            case R.id.allWithDraw:
                mWithdrawMoney.setText(FinanceUtil.formatWithScale(mMoney));
                break;
            case R.id.withdraw:
                InputSafetyPassDialogFragment.newInstance(mWithdrawMoney.getText().toString()).show(getSupportFragmentManager());
                break;
            case R.id.connect_service:
                break;
        }
    }

    private void showWithDrawRuleDialog() {
        BindBankHintDialogFragment.newInstance(R.string.can_with_rule, mWithDrawRuleRes).show(getSupportFragmentManager());
    }

    @Override
    public void onPassWord(String passWord) {
        mPassWord = passWord;
        if (!TextUtils.isEmpty(passWord)) {
            Client.withDraw(mWithdrawMoney.getText().toString(), mUserBankCardInfoModel.getId(), passWord)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<Object>>() {
                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            Log.d(TAG, "onRespSuccess: " + resp.toString());
                            if (resp.isSuccess()) {
                                Launcher.with(getActivity(), WithDrawDetailsActivity.class)
                                        .putExtra(Launcher.EX_PAYLOAD, mUserBankCardInfoModel)
                                        .putExtra(Launcher.EX_PAY_END, mWithdrawMoney.getText().toString())
                                        .execute();
                                finish();
                            }
                        }
                    })
                    .fire();
        }
    }
}
