package com.sbai.finance.activity.mine.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
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
        UserBankCardInfoModel userBankCardInfoModel = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        String money = getIntent().getStringExtra(Launcher.EX_PAY_END);
        double mPredict_poundage = getIntent().getDoubleExtra(Launcher.EX_PAYLOAD_1, 0);

        String withDrawBankAndNumber = "      " + userBankCardInfoModel.getIssuingBankName() + "  (" + userBankCardInfoModel.getCardNumber().substring(userBankCardInfoModel.getCardNumber().length() - 4) + ")";
        mBankCardAndName.setText(withDrawBankAndNumber);
        mWithDrawMoney.setText(getString(R.string.yuan, money));
        mPoundage.setText(getString(R.string.yuan, FinanceUtil.formatWithScale(mPredict_poundage)));
    }

    @OnClick(R.id.withdraw)
    public void onViewClicked() {
        finish();
    }
}
