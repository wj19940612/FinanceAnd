package com.sbai.finance.activity.mine.wallet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.View;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.recharge.AliPayActivity;
import com.sbai.finance.activity.recharge.BankCardPayActivity;
import com.sbai.finance.activity.recharge.WeChatPayActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UsablePlatform;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ValidationWatcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;


public class RechargeActivity extends BaseActivity {

    @BindView(R.id.rechargeWay)
    AppCompatTextView mRechargeWay;
    @BindView(R.id.rechargeCount)
    AppCompatEditText mRechargeCount;
    @BindView(R.id.recharge)
    AppCompatButton mRecharge;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;

    private String[] mPayData;
    private List<UsablePlatform> mUsablePlatformList;
    private UsablePlatform mUsablePlatform;
    private String bankCardId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        mRechargeCount.addTextChangedListener(mValidationWatcher);
        requestUsablePlatformList();
    }


    private void requestUsablePlatformList() {
        Client.getUsablePlatform().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<UsablePlatform>>, List<UsablePlatform>>() {
                    @Override
                    protected void onRespSuccessData(List<UsablePlatform> usablePlatformList) {
                        mPayData = new String[usablePlatformList.size()];
                        mUsablePlatformList = usablePlatformList;
                        for (int i = 0; i < usablePlatformList.size(); i++) {
                            UsablePlatform usablePlatform = usablePlatformList.get(i);
                            mPayData[i] = usablePlatform.getName();
                            if (usablePlatform.getType() == Preference.get().getRechargeWay()) {
                                mUsablePlatform = usablePlatform;
                            }
                        }
                    }
                }).fire();
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean rechargeBtnEnable = checkRechargeBtnEnable();
            if (mRecharge.isEnabled() != rechargeBtnEnable) {
                mRecharge.setEnabled(rechargeBtnEnable);
            }
        }
    };

    private boolean checkRechargeBtnEnable() {
        String count = mRechargeCount.getText().toString();
        return !TextUtils.isEmpty(count) && Double.parseDouble(count) > 1;
    }

    @OnClick({R.id.rechargeWay, R.id.rechargeCount, R.id.recharge, R.id.connect_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rechargeWay:
                if (mPayData != null && mPayData.length > 0) {
                    showRechargePicker();
                } else {
                    requestUsablePlatformList();
                }
                break;
            case R.id.rechargeCount:
                break;
            case R.id.recharge:
                submitRechargeData();
                break;
            case R.id.connect_service:
                Launcher.with(getActivity(), FeedbackActivity.class).execute();
                break;
        }
    }

    private void submitRechargeData() {
        if (mUsablePlatform == null) return;
        String money = mRechargeCount.getText().toString();
        Client.submitRechargeData(mUsablePlatform.getPlatform(), money, bankCardId)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
                    @Override
                    protected void onRespSuccessData(PaymentPath data) {
                        if (mUsablePlatform.getType() == UsablePlatform.TYPE_AIL_PAY) {
                            Launcher.with(getActivity(), AliPayActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, data.getPlatform())
                                    .putExtra(Launcher.EX_PAYLOAD_1, data.getThridOrderId())
                                    .execute();

                            Intent intent = new Intent();
//                            intent.setAction("android.intent.action.VIEW");
                            intent.setAction(Intent.ACTION_VIEW);
                            Uri content_url = Uri.parse(data.getCodeUrl());
                            intent.setData(content_url);
                            startActivity(intent);
                        } else if (mUsablePlatform.getType() == UsablePlatform.TYPE_WECHAT_PAY) {
                            Launcher.with(getActivity(), WeChatPayActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, data.getCodeUrl())
                                    .putExtra(Launcher.EX_PAYLOAD_1, data.getThridOrderId())
                                    .putExtra(Launcher.EX_PAYLOAD_2, data.getPlatform())
                                    .execute();
                        } else if (mUsablePlatform.getType() == UsablePlatform.TYPE_BANK_PAY) {
                            Launcher.with(getActivity(), BankCardPayActivity.class).putExtra(Launcher.EX_PAYLOAD,data.getThridOrderId()).execute();
                        }
                    }
                })
                .fire();


    }

    private void showRechargePicker() {
        OptionPicker picker = new OptionPicker(this, mPayData);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(1);
        picker.setSelectedItem(mUsablePlatform.getName());
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
        WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setLineConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mRechargeWay.setText(item);
                    for (UsablePlatform data : mUsablePlatformList) {
                        if (data.getName().equalsIgnoreCase(item)) {
                            Preference.get().setRechargeWay(data.getType());
                            if (item.contains("银行卡")) {
                                SpannableString 招商银行 = StrUtil.mergeTextWithRatioColor(getString(R.string.bank_card_pay, "招商银行", 0122),
                                        "\n" + getString(R.string.bank_card_recharge_limit, 444), 0.98f,
                                        ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                                mRechargeWay.setText(招商银行);
                            }
                            mUsablePlatform = data;
                            break;
                        }
                    }

                }
            }
        });
        picker.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
//                case REQ_CODE_BIND_CARD:
//                    break;
                default:
                    break;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRechargeCount.removeTextChangedListener(mValidationWatcher);
    }
}
