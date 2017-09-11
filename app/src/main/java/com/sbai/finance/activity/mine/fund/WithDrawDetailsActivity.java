package com.sbai.finance.activity.mine.fund;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.fund.UserBankCardInfo;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WithDrawDetailsActivity extends AppCompatActivity {

    @BindView(R.id.bankCardAndName)
    TextView mBankCardAndName;
    @BindView(R.id.with_draw_money)
    TextView mWithDrawMoney;
    @BindView(R.id.withdraw)
    AppCompatButton mWithdraw;
    @BindView(R.id.poundage)
    TextView mPoundage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_with_draw_details);
        ButterKnife.bind(this);
        UserBankCardInfo userBankCardInfo = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        String money = getIntent().getStringExtra(Launcher.EX_PAY_END);
        double mPredict_poundage = getIntent().getDoubleExtra(Launcher.EX_PAYLOAD_1, 0);

        if (userBankCardInfo != null && !TextUtils.isEmpty(userBankCardInfo.getCardNumber())) {
            String withDrawBankAndNumber = "      " + userBankCardInfo.getIssuingBankName() + "  (" + userBankCardInfo.getCardNumber().substring(userBankCardInfo.getCardNumber().length() - 4) + ")";
            mBankCardAndName.setText(withDrawBankAndNumber);
        }
        mWithDrawMoney.setText(getString(R.string.yuan_symbol, FinanceUtil.formatWithScale(money)));
        mPoundage.setText(getString(R.string.yuan_symbol, FinanceUtil.formatWithScale(mPredict_poundage)));
    }

    @OnClick(R.id.withdraw)
    public void onViewClicked() {
        finish();
    }
}
