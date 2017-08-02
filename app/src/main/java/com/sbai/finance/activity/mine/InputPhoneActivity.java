package com.sbai.finance.activity.mine;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 注册 & 找回密码的输入手机号页面
 */
public class InputPhoneActivity extends BaseActivity {

    public static final String PAGE_TYPE = "page_type";
    public static final int PAGE_TYPE_REGISTER = 801;

    @BindView(R.id.rootView)
    RelativeLayout mRootView;

    @BindView(R.id.phoneNumber)
    EditText mPhoneNumber;
    @BindView(R.id.phoneNumberClear)
    ImageView mPhoneNumberClear;

    @BindView(R.id.next)
    TextView mNext;

    private int mPageType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_phone);
        ButterKnife.bind(this);

        initData(getIntent());

        if (mPageType == PAGE_TYPE_REGISTER) {

        }

        mPhoneNumber.addTextChangedListener(mPhoneValidationWatcher);

        mPhoneNumber.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!TextUtils.isEmpty(mPhoneNumber.getText().toString()) && hasFocus) {
                    mPhoneNumberClear.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void initData(Intent intent) {
        mPageType = intent.getIntExtra(PAGE_TYPE, 0);
    }

    private ValidationWatcher mPhoneValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {

            formatPhoneNumber();

            mPhoneNumberClear.setVisibility(checkClearBtnVisible() ? View.VISIBLE : View.INVISIBLE);
        }
    };

    private boolean checkClearBtnVisible() {
        String phone = mPhoneNumber.getText().toString();
        if (!TextUtils.isEmpty(phone)) {
            return true;
        }
        return false;
    }

    private void formatPhoneNumber() {
        String oldPhone = mPhoneNumber.getText().toString();
        String phoneNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatPhoneNumber(phoneNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mPhoneNumber.setText(newPhone);
            mPhoneNumber.setSelection(newPhone.length());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPhoneNumber.removeTextChangedListener(mPhoneValidationWatcher);
    }

    @OnClick({R.id.next, R.id.rootView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.next:

                break;
            case R.id.rootView:
                KeyBoardUtils.closeKeyboard(this, mRootView);
                break;
        }
    }
}
