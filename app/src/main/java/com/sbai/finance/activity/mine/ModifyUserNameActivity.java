package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.utils.ValidityDecideUtil;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyUserNameActivity extends BaseActivity {

    @BindView(R.id.userNameInput)
    AppCompatEditText mUserName;
    @BindView(R.id.submitUserName)
    AppCompatButton mSubmitUserName;
    @BindView(R.id.clear)
    AppCompatImageView mClear;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_name);
        ButterKnife.bind(this);
        mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
        mUserName.addTextChangedListener(mValidationWatcher);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mUserName.removeTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean buttonEnable = checkConfirmButtonEnable();
            if (buttonEnable) {
                mClear.setVisibility(View.VISIBLE);
            } else {
                mClear.setVisibility(View.INVISIBLE);
            }
            if (mSubmitUserName.isEnabled() != buttonEnable) {
                mSubmitUserName.setEnabled(buttonEnable);
            }
            String string = s.toString();
            if (string.contains(" ")) {
                String newData = string.replaceAll(" ", "");
                mUserName.setText(newData);
                mUserName.setSelection(mUserName.getText().toString().length());
            }
        }
    };

    private boolean checkConfirmButtonEnable() {
        String userName = mUserName.getText().toString().trim().replaceAll(" ", "");
        return !TextUtils.isEmpty(userName);
    }

    private void submitNickName() {
        final String userName = mUserName.getText().toString().trim();
        if (!ValidityDecideUtil.isLegalNickName(userName)) {
            ToastUtil.curt(R.string.is_only_a_chinese_name);
            return;
        }

        Client.updateUserNickNmae(userName)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
//                            UserInfo userInfo = LocalUser.getUser().getUserInfo();
//                            userInfo.setUserName(userName);
//                            LocalUser.getUser().setUserInfo(userInfo);
                            LocalUser.getUser().getUserInfo().setUserName(userName);
                            Log.d(TAG, "onRespSuccess: " + LocalUser.getUser().getUserInfo());
                            setResult(RESULT_OK);
                            finish();
                        }
                    }
                })
                .fire();
    }


    @OnClick({R.id.submitUserName, R.id.clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submitUserName:
                submitNickName();
                break;
            case R.id.clear:
                mUserName.setText("");
                break;
        }
    }

}
