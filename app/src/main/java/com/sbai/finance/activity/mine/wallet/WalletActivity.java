package com.sbai.finance.activity.mine.wallet;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.TheDetailActivity;
import com.sbai.finance.activity.mine.setting.ModifySafetyPassActivity;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WalletActivity extends BaseActivity {

    private static final int REQ_CODE_BIND_CARD = 417;
    private static final int REQ_CODE_ADD_SAFETY_PASS = 5120;

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
        SpannableString spannableString = StrUtil.mergeTextWithRatio(getString(R.string.balance), "\n" + FinanceUtil.formatWithScale(100000), 2.6f);
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
                Client.getUserHasPassWord()
                        .setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                            @Override
                            protected void onRespSuccessData(Boolean data) {
                                if (!data) {
                                    Launcher.with(getActivity(), ModifySafetyPassActivity.class).putExtra(Launcher.EX_PAYLOAD, data.booleanValue()).executeForResult(REQ_CODE_ADD_SAFETY_PASS);
                                }
                            }
                        })
                        .fire();

                break;
            case R.id.market_detail:
                Launcher.with(getActivity(), TheDetailActivity.class).execute();
                break;
            case R.id.bankCard:
                Launcher.with(getActivity(), BindBankCardActivity.class).executeForResult(REQ_CODE_BIND_CARD);
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQ_CODE_ADD_SAFETY_PASS&&resultCode==RESULT_OK){
            // TODO: 2017/6/15 打开体现界面
        }
    }
}
