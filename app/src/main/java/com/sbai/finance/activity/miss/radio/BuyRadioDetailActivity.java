package com.sbai.finance.activity.miss.radio;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.CheckBox;
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
        super.onTimeUp(count);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initData() {
        money = 3;
    }

    private void initView() {
        mCheckboxClick.setChecked(true);
//        BuyRadioResultDialog.get(this, new BuyRadioResultDialog.OnCallback() {
//            @Override
//            public void onDialogClick() {
//
//            }
//        }, 200);
    }

    @OnClick({R.id.aliRecharge, R.id.miliRecharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.aliRecharge:
                if (!mCheckboxClick.isChecked() && !mMiliCheckboxClick.isChecked()) {
                    //两个按钮都没点
                    mCheckboxClick.setChecked(true);
                } else if (mCheckboxClick.isChecked() && !mMiliCheckboxClick.isChecked()) {

                } else if (!mCheckboxClick.isChecked() && mMiliCheckboxClick.isChecked()) {
                    //点了米粒按钮
                    if (mMili / 100 < money) {
                        mCheckboxClick.setChecked(true);
                    }
                }else{
                    //两个按钮都点了
                }
                break;
            case R.id.miliRecharge:
                if (!mCheckboxClick.isChecked() && !mMiliCheckboxClick.isChecked()) {

                } else if (mCheckboxClick.isChecked() && !mMiliCheckboxClick.isChecked()) {
                    //支付宝按钮有，没点米粒的按钮
                    dealMiliTip();
                    calcPayNum();
                    mMiliCheckboxClick.setChecked(true);
                } else if (!mCheckboxClick.isChecked() && mMiliCheckboxClick.isChecked()) {
                    //点了米粒按钮
                    if (mMili / 100 < money) {
                        mCheckboxClick.setChecked(true);
                    }
                }else{
                    //两个按钮都点了
                }

                break;
        }
    }

    private void dealMiliTip() {
        if (mMili == 0) {
            mMiliCheckboxClick.setVisibility(View.GONE);
            mNoMiliTip.setVisibility(View.VISIBLE);
            mDeduction.setVisibility(View.GONE);
        } else {
            mMiliCheckboxClick.setVisibility(View.VISIBLE);
            mNoMiliTip.setVisibility(View.GONE);
            mDeduction.setVisibility(View.VISIBLE);
        }
    }

    private void calcPayNum(){
        if (!mMiliCheckboxClick.isChecked()) {
            float deductionNum;
            if (mMili / 100 >= money) {
                mCheckboxClick.setChecked(false);
                deductionNum = money;
            } else {
                deductionNum = mMili / 100;
            }
            mDeduction.setVisibility(View.VISIBLE);
            mDeduction.setText(String.format(getString(R.string.have_deduction_num), deductionNum));
        } else {
            mDeduction.setVisibility(View.GONE);
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
                    }
                })
                .fireFree();
    }
}
