package com.sbai.finance.activity.mine.wallet;

import android.content.Intent;
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
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;


public class RechargeActivity extends BaseActivity {

    private static final int REQ_CODE_BIND_CARD = 417;

    @BindView(R.id.rechargeWay)
    AppCompatTextView mRechargeWay;
    @BindView(R.id.rechargeCount)
    AppCompatEditText mRechargeCount;
    @BindView(R.id.recharge)
    AppCompatButton mRecharge;
    @BindView(R.id.connect_service)
    AppCompatTextView mConnectService;

    private String[] mPayData;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recharge);
        ButterKnife.bind(this);
        mPayData = new String[]{getString(R.string.bank_card), getString(R.string.ali), getString(R.string.wei_xin),};
        mRechargeCount.addTextChangedListener(mValidationWatcher);
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
        return !TextUtils.isEmpty(count) && Integer.parseInt(count) > 1;
    }

    @OnClick({R.id.rechargeWay, R.id.rechargeCount, R.id.recharge, R.id.connect_service})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rechargeWay:
                showRechargePicker();
                break;
            case R.id.rechargeCount:
                break;
            case R.id.recharge:
                break;
            case R.id.connect_service:
                // TODO: 2017/6/14 先写绑定银行卡
                Launcher.with(getActivity(), BindBankCardActivity.class).executeForResult(REQ_CODE_BIND_CARD);
                break;
        }
    }

    private void showRechargePicker() {
        OptionPicker picker = new OptionPicker(this, mPayData);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(1);
        picker.setSelectedItem(Preference.get().getRechargeWay());
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
        WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setLineConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    Preference.get().setRechargeWay(item);
                    if (index == 0) {
                        // TODO: 2017/6/13 写死数据
                        SpannableString 招商银行 = StrUtil.mergeTextWithRatioColor(getString(R.string.bank_card_pay, "招商银行", 0122),
                                "\n" + getString(R.string.bank_card_recharge_limit, 444), 0.98f,
                                ContextCompat.getColor(RechargeActivity.this, R.color.unluckyText));
                        mRechargeWay.setText(招商银行);
                    } else {
                        mRechargeWay.setText(item);
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
                case REQ_CODE_BIND_CARD:
                    break;
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
