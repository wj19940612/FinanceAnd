package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小额免密支付界面
 */
public class SmallAndFreePaymentPassSetActivity extends BaseActivity {

    public static final int REQ_CODE_ALLOW_SMALL_NO_SECRET_PAYMENT = 3886;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.allowSmallNoSecretPayment)
    AppCompatImageView mAllowSmallNoSecretPayment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_small_and_free_payment_pass_set);
        ButterKnife.bind(this);
        requestUserAllowAvoidClosePayStatus();
    }

    private void requestUserAllowAvoidClosePayStatus() {
        Client.requestUserSmallNoSecretPaymentStatus()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        mAllowSmallNoSecretPayment.setSelected(data);
                    }
                })
                .fireFree();
    }


    @OnClick(R.id.allowSmallNoSecretPayment)
    public void onViewClicked() {
        Client.setNonSecretPayment()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Boolean>, Boolean>() {
                    @Override
                    protected void onRespSuccessData(Boolean data) {
                        mAllowSmallNoSecretPayment.setSelected(data);
                        Intent intent = new Intent();
                        intent.putExtra(Launcher.EX_PAYLOAD, data.booleanValue());
                        setResult(RESULT_OK, intent);
                    }
                })
                .fireFree();
    }
}
