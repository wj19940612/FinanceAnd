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

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.fragment.dialog.InputSafetyPassDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.model.payment.WithPoundageModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;

import java.util.List;

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

    private double mMoney;

    private String mPassWord;
    //手续费
    private double mPoundage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw);
        ButterKnife.bind(this);

        mUserBankCardInfoModel = getIntent().getParcelableExtra(Launcher.EX_PAY_END);
        updateBankInfo(mUserBankCardInfoModel);
        mMoney = getIntent().getDoubleExtra(Launcher.EX_PAYLOAD, 0);
        //从消息界面进入
        requestUserBankInfo();
        requestFundInfo();

        mWithdrawMoney.addTextChangedListener(mValidationWatcher);
        mCanWithDrawMoney.setText(getString(R.string.can_with_draw_money, FinanceUtil.formatWithScale(mMoney)));

        requestWithDrawPoundage();
    }

    private void requestFundInfo() {
        Client.requestUserFundInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        mMoney = data.getMoney();
                        mCanWithDrawMoney.setText(getString(R.string.can_with_draw_money, FinanceUtil.formatWithScale(mMoney)));
                    }
                })
                .fireSync();
    }

    private void requestUserBankInfo() {
        Client.requestUserBankCardInfo()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<UserBankCardInfoModel>>, List<UserBankCardInfoModel>>() {
                    @Override
                    protected void onRespSuccessData(List<UserBankCardInfoModel> data) {
                        if (data != null && !data.isEmpty()) {
                            mUserBankCardInfoModel = data.get(0);
                            if (mUserBankCardInfoModel != null) {
                                updateBankInfo(mUserBankCardInfoModel);
                            }
                        }
                    }

                })
                .fire();
    }


    private void updateBankInfo(UserBankCardInfoModel userBankCardInfoModel) {
        if (userBankCardInfoModel != null && !TextUtils.isEmpty(userBankCardInfoModel.getCardNumber())) {
            String withDrawBankAndNumber = "      " + userBankCardInfoModel.getIssuingBankName() + "  (" + userBankCardInfoModel.getCardNumber().substring(userBankCardInfoModel.getCardNumber().length() - 4) + ")";
            mWithdrawBank.setText(getString(R.string.with_draw_bank, withDrawBankAndNumber));
        }
    }

    private void requestWithDrawPoundage() {
        Client.requestWithDrawPoundage()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<WithPoundageModel>, WithPoundageModel>() {
                    @Override
                    protected void onRespSuccessData(WithPoundageModel data) {
                        if (data != null) {
                            mPoundage = data.getFee();
                        }
                        mCanWithPoundage.setText(getString(R.string.can_with_poundage, FinanceUtil.formatWithScale(mPoundage)));
                        if (Preference.get().isFirstWithDraw(LocalUser.getUser().getPhone())) {
                            showWithDrawRuleDialog();
                            Preference.get().setIsFirstWithDraw(LocalUser.getUser().getPhone(), false);
                        }
                    }
                })
                .fire();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            formatWithDrawMoney();
            boolean confirmEnable = checkConfirmEnable();
            if (confirmEnable != mWithdraw.isEnabled()) {
                mWithdraw.setEnabled(confirmEnable);
            }
        }
    };

    private void formatWithDrawMoney() {
        String withDrawMoney = mWithdrawMoney.getText().toString().trim();
        String formatMoney = StrFormatter.getFormatMoney(withDrawMoney);
        if (!formatMoney.equalsIgnoreCase(withDrawMoney)) {
            mWithdrawMoney.setText(formatMoney);
            mWithdrawMoney.setSelection(formatMoney.length());
        } else {
            mWithdrawMoney.setSelection(withDrawMoney.length());
        }
    }

    private boolean checkConfirmEnable() {
        String withDrawMoney = mWithdrawMoney.getText().toString();
        if (withDrawMoney.startsWith(".")) {
            return false;
        }
        return !TextUtils.isEmpty(withDrawMoney)
                && Double.parseDouble(withDrawMoney) >= 5
                && Double.parseDouble(withDrawMoney) <= mMoney;
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
                InputSafetyPassDialogFragment.newInstance(getString(R.string.yuan, mWithdrawMoney.getText().toString())).show(getSupportFragmentManager());
                break;
            case R.id.connect_service:
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
                break;
        }
    }

    private void showWithDrawRuleDialog() {
        BindBankHintDialogFragment.newInstance(R.string.can_with_rule, getString(R.string.can_with_rule_content, FinanceUtil.formatWithScale(mPoundage))).show(getSupportFragmentManager());
    }

    @Override
    public void onPassWord(String passWord) {
        mPassWord = passWord;
        if (!TextUtils.isEmpty(passWord) && mUserBankCardInfoModel != null) {
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
                                        .putExtra(Launcher.EX_PAYLOAD_1, mPoundage)
                                        .execute();
                                finish();
                            }
                        }
                    })
                    .fire();
        }
    }
}
