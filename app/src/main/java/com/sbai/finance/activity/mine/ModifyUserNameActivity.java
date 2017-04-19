package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.utils.ValidityDecideUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ModifyUserNameActivity extends BaseActivity {

    @BindView(R.id.userName)
    AppCompatEditText mUserName;
    @BindView(R.id.submitUserName)
    AppCompatButton mSubmitUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_user_name);
        ButterKnife.bind(this);
        mUserName.setText("hahah");
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

    @OnClick(R.id.submitUserName)
    public void onViewClicked() {
        String userName = mUserName.getText().toString().trim();
        if (!ValidityDecideUtil.isOnlyAChineseName(userName)) {
//            mErrorBar.show(R.string.is_only_a_chinese_name);
            ToastUtil.curt(R.string.is_only_a_chinese_name);
            return;
        }
        setResult(RESULT_OK);
        finish();
    }
}
