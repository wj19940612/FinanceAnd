package com.sbai.finance.activity.mine.wallet;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.TheDetailActivity;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WalletActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.balance)
    AppCompatTextView mBalance;
    @BindView(R.id.recharge)
    IconTextRow mRecharge;
    @BindView(R.id.withdraw)
    IconTextRow mWithdraw;
    @BindView(R.id.market_detail)
    IconTextRow mMarketDetail;
    @BindView(R.id.bankCard)
    IconTextRow mBankCard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
        SpannableString spannableString = StrUtil.mergeTextWithRatio(getString(R.string.balance), "\n" + FinanceUtil.formatWithScale(1000000000), 2.6f);
        mBalance.setText(spannableString);
    }

    @OnClick({R.id.balance, R.id.recharge, R.id.withdraw, R.id.market_detail, R.id.bankCard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.balance:
                break;
            case R.id.recharge:
                Launcher.with(getActivity(), RechargeActivity.class).execute();
                break;
            case R.id.withdraw:
                // TODO: 2017/6/13 第一次点击提现  跳转至添加安全密码
//                Client.getUserHasPassWord()
//                        .setTag(TAG)
//                        .setIndeterminate(this)
//                        .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
//                            @Override
//                            protected void onRespSuccessData(Boolean data) {
//                                Launcher.with(getActivity(), ModifySafetyPassActivity.class).putExtra(Launcher.EX_PAYLOAD, data.booleanValue()).execute();
//                            }
//                        })
//                        .fire();

                break;
            case R.id.market_detail:
                Launcher.with(getActivity(), TheDetailActivity.class).execute();
                break;
            case R.id.bankCard:
                break;
        }
    }
}
