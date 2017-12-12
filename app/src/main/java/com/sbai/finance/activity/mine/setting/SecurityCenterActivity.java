package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.AppCompatImageView;
import android.view.View;
import android.widget.LinearLayout;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class SecurityCenterActivity extends BaseActivity {

    private static final int REQ_CODE_UPDATE_SECURITY_PSD = 247;
    private static final int REQ_CODE_UPDATE_LOGIN_PSD = 246;

    public static final int REQ_CODE_ALLOW_SMALL_NO_SECRET_PAYMENT = 3886;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.setLoginPassword)
    IconTextRow mSetLoginPassword;
    @BindView(R.id.modifyLoginPassword)
    IconTextRow mModifyLoginPassword;

    @BindView(R.id.setSecurityPassword)
    IconTextRow mSetSecurityPassword;

    @BindView(R.id.modifySecurityPasswordArea)
    LinearLayout mModifySecurityPasswordArea;
    @BindView(R.id.modifySecurityPassword)
    IconTextRow mModifySecurityPassword;
    @BindView(R.id.forgetSecurityPassword)
    IconTextRow mForgetSecurityPassword;
    @BindView(R.id.allowSmallNoSecretPayment)
    AppCompatImageView mAllowSmallNoSecretPayment;

    private boolean mHasSecurityPassword;
    private boolean mHasLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_center);
        ButterKnife.bind(this);

        initData(getIntent());
        initViews();
        if (mHasSecurityPassword) {
            requestUserSmallNoSecretPayment();
        }
    }

    private void initViews() {
        if (mHasLoginPassword) {
            mSetLoginPassword.setVisibility(View.GONE);
            mModifyLoginPassword.setVisibility(View.VISIBLE);
        } else {
            mSetLoginPassword.setVisibility(View.VISIBLE);
            mModifyLoginPassword.setVisibility(View.GONE);
        }

        if (mHasSecurityPassword) {
            mModifySecurityPasswordArea.setVisibility(View.VISIBLE);
            mSetSecurityPassword.setVisibility(View.GONE);
        } else {
            mModifySecurityPasswordArea.setVisibility(View.GONE);
            mSetSecurityPassword.setVisibility(View.VISIBLE);
        }
    }

    private void initData(Intent intent) {
        mHasSecurityPassword = intent.getBooleanExtra(ExtraKeys.HAS_SECURITY_PSD, false);
        mHasLoginPassword = LocalUser.getUser().getUserInfo().isSetPass();
    }

    @OnClick({R.id.modifySecurityPassword, R.id.forgetSecurityPassword, R.id.setSecurityPassword,
            R.id.setLoginPassword, R.id.modifyLoginPassword, R.id.allowSmallNoSecretPayment})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.forgetSecurityPassword:
                umengEventCount(UmengCountEventId.ME_FORGIVE_SAFETY_PASSWORD);
                Launcher.with(getActivity(), ForgetSecurityPassActivity.class)
                        .executeForResult(REQ_CODE_UPDATE_SECURITY_PSD);
                break;

            case R.id.modifySecurityPassword:
                umengEventCount(UmengCountEventId.ME_MODIFY_SAFETY_PASSWORD);
                Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                        .putExtra(ExtraKeys.HAS_SECURITY_PSD, mHasSecurityPassword)
                        .executeForResult(REQ_CODE_UPDATE_SECURITY_PSD);
                break;
            case R.id.setSecurityPassword:
                umengEventCount(UmengCountEventId.ME_SET_SAFETY_PASSWORD);
                Launcher.with(getActivity(), UpdateSecurityPassActivity.class)
                        .putExtra(ExtraKeys.HAS_SECURITY_PSD, mHasSecurityPassword)
                        .executeForResult(REQ_CODE_UPDATE_SECURITY_PSD);
                break;

            case R.id.setLoginPassword:
                umengEventCount(UmengCountEventId.ME_SET_LOGIN_PASSWORD);
                Launcher.with(getActivity(), UpdatePasswordActivity.class)
                        .putExtra(ExtraKeys.HAS_LOGIN_PSD, mHasLoginPassword)
                        .executeForResult(REQ_CODE_UPDATE_LOGIN_PSD);
                break;
            case R.id.modifyLoginPassword:
                umengEventCount(UmengCountEventId.ME_MODIFY_LOGIN_PASSWORD);
                Launcher.with(getActivity(), UpdatePasswordActivity.class)
                        .putExtra(ExtraKeys.HAS_LOGIN_PSD, mHasLoginPassword)
                        .executeForResult(REQ_CODE_UPDATE_LOGIN_PSD);
                break;
            case R.id.allowSmallNoSecretPayment:
                setNonSecretPayment();
                break;
        }
    }

    private void requestUserSmallNoSecretPayment() {
        //想增加个缓存
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

    private void setNonSecretPayment() {
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_UPDATE_SECURITY_PSD:
                    if (mHasSecurityPassword) {
                        mModifySecurityPasswordArea.setVisibility(View.VISIBLE);
                        mSetSecurityPassword.setVisibility(View.GONE);
                    } else { // 第一次添加设置安全密码后关闭安全中心，回到设置
                        finish();
                    }
                    break;
                case REQ_CODE_UPDATE_LOGIN_PSD:
                    mHasLoginPassword = LocalUser.getUser().getUserInfo().isSetPass();
                    mSetLoginPassword.setVisibility(View.GONE);
                    mModifyLoginPassword.setVisibility(View.VISIBLE);
                    break;
            }
        }

    }
}
