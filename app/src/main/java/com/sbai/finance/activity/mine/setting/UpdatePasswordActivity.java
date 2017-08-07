package com.sbai.finance.activity.mine.setting;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UpdatePasswordActivity extends AppCompatActivity {


    @BindView(R.id.confirm)
    TextView mConfirm;

    @BindView(R.id.rootView)
    LinearLayout mRootView;

    private boolean mHasLoginPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_password);
        ButterKnife.bind(this);

        initData(getIntent());

//        if (mHasLoginPassword) { // modify password view
//            mPasswordArea.setVisibility(View.VISIBLE);
//            mOldPasswordArea.setVisibility(View.VISIBLE);
//            mPassword.setFilters(new InputFilter[]{new PasswordInputFilter()});
//            mOldPassword.setFilters(new InputFilter[]{new PasswordInputFilter()});
//        } else {
//            mPasswordArea.setVisibility(View.VISIBLE);
//            mOldPasswordArea.setVisibility(View.GONE);
//            mPassword.setFilters(new InputFilter[]{new PasswordInputFilter()});
//        }
    }

    private void initData(Intent intent) {
        mHasLoginPassword = intent.getBooleanExtra(ExtraKeys.HAS_LOGIN_PSD, false);
    }

    @OnClick({R.id.confirm, R.id.rootView, R.id.showOldPassword})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.confirm:
                updateLoginPassword();
                break;
//            case R.id.rootView:
//                KeyBoardUtils.closeKeyboard(mPassword);
//                break;
//            case R.id.showOldPassword:
//                break;
//            case R.id.showPassword:
//                break;
        }
    }

    private void updateLoginPassword() {
        // TODO: 04/08/2017 update login password
    }
}
