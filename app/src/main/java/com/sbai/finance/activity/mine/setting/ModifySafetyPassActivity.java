package com.sbai.finance.activity.mine.setting;

import android.os.Bundle;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ModifySafetyPassActivity extends BaseActivity {

    @BindView(R.id.safety_password_hint)
    AppCompatTextView mSafetyPasswordHint;
    @BindView(R.id.safety_password_number)
    SafetyPasswordEditText mSafetyPasswordNumber;
    @BindView(R.id.password_hint)
    TextView mPasswordHint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_safety_pass);
        ButterKnife.bind(this);
        mSafetyPasswordNumber.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mPasswordHint.setText(s.toString());

            if (s.toString().length() == 6) {
                ToastUtil.curt("完美");
                mSafetyPasswordNumber.clearSafetyNumber();
            }
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSafetyPasswordNumber.removeTextChangedListener(mValidationWatcher);
    }
}
