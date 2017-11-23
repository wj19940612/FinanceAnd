package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.JsonObject;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.DialogBaseActivity;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageAuthCodeActivity extends DialogBaseActivity {

    @BindView(R.id.authCodeInput)
    EditText mAuthCodeInput;
    @BindView(R.id.imageAuthCode)
    ImageView mImageAuthCode;
    @BindView(R.id.warnTip)
    TextView mWarnTip;
    @BindView(R.id.confirm)
    TextView mConfirm;

    private String mPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_auth_code);
        ButterKnife.bind(this);

        initData(getIntent());

        requestImageAuthCode();

        mAuthCodeInput.addTextChangedListener(mValidationWatcher);
        mAuthCodeInput.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    mWarnTip.setText("");
                }
            }
        });
    }

    private void requestImageAuthCode() {
        GlideApp.with(getActivity()).load(Client.getImageAuthCode(mPhone))
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(mImageAuthCode);
    }

    private void initData(Intent intent) {
        mPhone = intent.getStringExtra(ExtraKeys.PHONE);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {

        @Override
        public void afterTextChanged(Editable s) {
            boolean enable = checkConfirmButtonEnable();
            if (enable != mConfirm.isEnabled()) {
                mConfirm.setEnabled(enable);
            }
            String tip = mWarnTip.getText().toString();
            if (!TextUtils.isEmpty(tip)) {
                mWarnTip.setText("");
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        String authCode = mAuthCodeInput.getText().toString().trim();
        if (!TextUtils.isEmpty(authCode) && authCode.length() >= 4 && authCode.length() <= 6) {
            return true;
        }
        return false;
    }

    @OnClick({R.id.dialogClose, R.id.imageAuthCode, R.id.changeImageAuthCode, R.id.confirm})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogClose:
                finish();
                break;
            case R.id.imageAuthCode:
            case R.id.changeImageAuthCode:
                requestImageAuthCode();
                break;
            case R.id.confirm:
                String authCode = mAuthCodeInput.getText().toString().trim();
                Client.getAuthCode(mPhone, authCode).setTag(TAG)
                        .setCallback(new Callback<Resp<JsonObject>>() {
                            @Override
                            protected void onRespSuccess(Resp<JsonObject> resp) {
                                setResult(RESULT_OK);
                                finish();
                            }

                            @Override
                            protected void onRespFailure(Resp failedResp) {
                                mWarnTip.setText(failedResp.getMsg());
                            }
                        }).fire();
                break;
        }
    }
}
