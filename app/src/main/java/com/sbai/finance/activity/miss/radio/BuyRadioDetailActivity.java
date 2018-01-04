package com.sbai.finance.activity.miss.radio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.BuyRadioResultDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by Administrator on 2018\1\3 0003.
 */

public class BuyRadioDetailActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.contentLayout)
    LinearLayout mContentLayout;
    @BindView(R.id.payName)
    TextView mPayName;
    @BindView(R.id.checkboxClick)
    CheckBox mCheckboxClick;
    @BindView(R.id.balanceNotEnough)
    TextView mBalanceNotEnough;
    @BindView(R.id.aliRecharge)
    RelativeLayout mAliRecharge;
    @BindView(R.id.miliPayName)
    TextView mMiliPayName;
    @BindView(R.id.miliCheckboxClick)
    CheckBox mMiliCheckboxClick;
    @BindView(R.id.noMiliTip)
    TextView mNoMiliTip;
    @BindView(R.id.miliRecharge)
    RelativeLayout mMiliRecharge;
    @BindView(R.id.paySelectLayout)
    LinearLayout mPaySelectLayout;
    @BindView(R.id.payBtn)
    TextView mPayBtn;
    @BindView(R.id.payTip)
    TextView mPayTip;
    @BindView(R.id.deduction)
    TextView mDeduction;

    private float mMili;
    private float money;

    private boolean mEntered;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_radio_detail);
        ButterKnife.bind(this);
        initData();
        initView();
        requestUserFund();
    }

    @Override
    public void onTimeUp(int count) {
        SmartDialog.dismiss(this);
        stopScheduleJob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        money = 200;
    }

    private void initView() {
        mMiliRecharge.setEnabled(false);
        mPayBtn.setEnabled(false);
        CompoundButton.OnCheckedChangeListener onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                float needPay = calcPayNum();
                mPayBtn.setText(String.format(getString(R.string.sure_to_pay_num), needPay));
            }
        };
        mCheckboxClick.setOnCheckedChangeListener(onCheckedChangeListener);
        mMiliCheckboxClick.setOnCheckedChangeListener(onCheckedChangeListener);
        mCheckboxClick.setChecked(true);
    }

    @OnClick({R.id.aliRecharge, R.id.miliRecharge})
    public void onViewClicked(View view) {
        //米粒足够的时候，只能选支付宝或者米粒支付，不考虑其他
        //米粒不够的时候，只能支付宝或者两个一起支付，不考虑其他
        switch (view.getId()) {
            case R.id.aliRecharge:
                if (!mCheckboxClick.isChecked() && mMiliCheckboxClick.isChecked()) {
                    //此时只点了米粒按钮
                    //单纯米粒不足以支付，两个按钮可以共存
                    if (mMili / 100 < money) {
                        mCheckboxClick.setChecked(true);
                    } else {
                        //当米粒足够的时候，只能出现单选
                        mCheckboxClick.setChecked(true);
                        mDeduction.setVisibility(View.GONE);
                        mMiliCheckboxClick.setChecked(false);
                    }
                }
                break;
            case R.id.miliRecharge:
                if (mCheckboxClick.isChecked() && !mMiliCheckboxClick.isChecked()) {
                    //计算抵扣的数额
                    float deduction = calcDeductionNum();
                    mDeduction.setText(String.format(getString(R.string.have_deduction_num), deduction));
                    mDeduction.setVisibility(View.VISIBLE);
                    //支付宝按钮有，没点米粒的按钮,如果米粒足够支付，支付宝的要去掉
                    if (deduction >= money && mMili != 0) {
                        mCheckboxClick.setChecked(false);
                    }
                    mMiliCheckboxClick.setChecked(true);
                } else if (mCheckboxClick.isChecked() && mMiliCheckboxClick.isChecked()) {
                    //两个按钮都点了
                    mDeduction.setVisibility(View.GONE);
                    mMiliCheckboxClick.setChecked(false);
                }

                break;
        }
    }

    private float calcPayNum() {
        float needPayNum = 0f;
        if (mCheckboxClick.isChecked() && !mMiliCheckboxClick.isChecked()) {
            needPayNum = money;
        } else if (!mCheckboxClick.isChecked() && mMiliCheckboxClick.isChecked()) {
            needPayNum = money;
        } else if (mCheckboxClick.isChecked() && mMiliCheckboxClick.isChecked()) {
            needPayNum = money - mMili / 100;
        }
        return needPayNum;
    }

    private float calcDeductionNum() {
        float deductionNum;
        if (mMili / 100 >= money) {
            deductionNum = money;
        } else {
            deductionNum = mMili / 100;
        }
        return deductionNum;
    }

    private void dealMiliTip() {
        if (mMili == 0) {
            mMiliRecharge.setEnabled(false);
            mMiliCheckboxClick.setVisibility(View.GONE);
            mNoMiliTip.setVisibility(View.VISIBLE);
        } else {
            mMiliRecharge.setEnabled(true);
            mMiliCheckboxClick.setVisibility(View.VISIBLE);
            mNoMiliTip.setVisibility(View.GONE);
        }
    }

    private void requestUserFund() {
        Client.requestUserFundInfo()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserFundInfo>, UserFundInfo>() {
                    @Override
                    protected void onRespSuccessData(UserFundInfo data) {
                        mMili = data.getYuanbao();
                        mMiliPayName.setText(String.format(getString(R.string.mili_pay), data.getYuanbao()));
                        dealMiliTip();
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mPayBtn.setEnabled(true);
                    }
                })
                .fireFree();
    }

    public void startDialog(Resp<Object> resp) {
        startScheduleJob(2000);
        BuyRadioResultDialog.get(this, new BuyRadioResultDialog.OnCallback() {
            @Override
            public void onDialogClick() {

            }
        }, 200);
    }
}
