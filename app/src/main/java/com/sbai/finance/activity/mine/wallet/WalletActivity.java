package com.sbai.finance.activity.mine.wallet;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.SpannableString;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.TheDetailActivity;
import com.sbai.finance.activity.mine.setting.ModifySafetyPassActivity;
import com.sbai.finance.model.payment.UserBankCardInfoModel;
import com.sbai.finance.model.payment.UserFundInfoModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WalletActivity extends BaseActivity {

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

    private UserFundInfoModel mUserFundInfoModel;

    private double money;

    private UserBankCardInfoModel mUserBankCardInfoModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wallet);
        ButterKnife.bind(this);
        updateUserFund(0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestUserFindInfo();
    }


    private void updateUserFund(double fund) {
        SpannableString spannableString = StrUtil.mergeTextWithRatio(getString(R.string.balance), "\n" + FinanceUtil.formatWithScale(fund), 2.6f);
        mBalance.setText(spannableString);
    }

    private void requestUserFindInfo() {
        Client.requestUserFundInfo()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfoModel>, UserFundInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfoModel data) {
                        mUserFundInfoModel = data;
                        money = data.getMoney();
                        updateUserFund(data.getMoney());
                    }
                })
                .fireSync();
    }

    @OnClick({R.id.balance, R.id.recharge, R.id.withdraw, R.id.market_detail, R.id.bankCard})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.balance:
                break;
            case R.id.recharge:
                Client.requestUserBankCardInfo()
                        .setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<List<UserBankCardInfoModel>>>() {
                            @Override
                            protected void onRespSuccess(Resp<List<UserBankCardInfoModel>> resp) {
                                if (resp.isSuccess()) {
                                    if (resp.hasData()) {
                                        mUserBankCardInfoModel = resp.getData().get(0);
                                    }
                                    Launcher.with(getActivity(), RechargeActivity.class)
                                            .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                            .putExtra(Launcher.EX_PAYLOAD, money)
                                            .execute();
                                }
                            }
                        })
                        .fire();
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
                                } else {
                                    openWithDrawPage();
                                }
                            }
                        })
                        .fire();

                break;
            case R.id.market_detail:
                Launcher.with(getActivity(), TheDetailActivity.class).execute();
                break;
            case R.id.bankCard:
                Client.requestUserBankCardInfo()
                        .setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<List<UserBankCardInfoModel>>>() {
                            @Override
                            protected void onRespSuccess(Resp<List<UserBankCardInfoModel>> resp) {
                                if (resp.isSuccess()) {
                                    if (resp.hasData()) {
                                        mUserBankCardInfoModel = resp.getData().get(0);
                                    }
                                    Launcher.with(getActivity(), BindBankCardActivity.class)
                                            .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                            .execute();
                                }
                            }
                        })
                        .fire();
                break;
        }
    }

    private void showOpenBindCardDialog() {
        SmartDialog.with(getActivity(), R.string.you_not_bind_bank_card)
                .setPositive(R.string.go_to_bind_card, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), BindBankCardActivity.class)
                                .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                .executeForResult(BindBankCardActivity.REQ_CODE_BIND_CARD);
                    }
                })
                .show();
    }

    private void openWithDrawPage() {
        Client.requestUserBankCardInfo()
                .setTag(TAG)
                .setIndeterminate(WalletActivity.this)
//                .setCallback(new Callback<Resp<UserBankCardInfoModel>>() {
//                    @Override
//                    protected void onRespSuccess(Resp<List<UserBankCardInfoModel>> resp) {
//                        if (resp.isSuccess()) {
//                            if (resp.hasData()) {
//                                mUserBankCardInfoModel = resp.getData().get(0);
//                                Launcher.with(getActivity(), WithDrawActivity.class)
//                                        .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
//                                        .putExtra(Launcher.EX_PAYLOAD, mUserFundInfoModel.getMoney())
//                                        .execute();
//                            } else {
//                                showOpenBindCardDialog();
//                            }
//                        }
//                    }
//                })
                .setCallback(new Callback2D<Resp<UserBankCardInfoModel>, UserBankCardInfoModel>() {
                    @Override
                    protected void onRespSuccessData(UserBankCardInfoModel data) {
                        mUserBankCardInfoModel = data;
                        if (data.isNotConfirmBankInfo()) {
                            showOpenBindCardDialog();
                        } else {
                            Launcher.with(getActivity(), WithDrawActivity.class)
                                    .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                    .putExtra(Launcher.EX_PAYLOAD, money)
                                    .execute();
                        }
                    }
                })
                .fire();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //添加安全密码后回调
                case REQ_CODE_ADD_SAFETY_PASS:
                    openWithDrawPage();
                    break;
                case BindBankCardActivity.REQ_CODE_BIND_CARD:
                    mUserBankCardInfoModel = data.getParcelableExtra(Launcher.EX_PAYLOAD);
                    if (mUserBankCardInfoModel != null && !mUserBankCardInfoModel.isNotConfirmBankInfo()) {
                        Launcher.with(getActivity(), WithDrawActivity.class)
                                .putExtra(Launcher.EX_PAY_END, mUserBankCardInfoModel)
                                .putExtra(Launcher.EX_PAYLOAD, money)
                                .execute();
                    }
                    break;
            }
        }
    }
}
