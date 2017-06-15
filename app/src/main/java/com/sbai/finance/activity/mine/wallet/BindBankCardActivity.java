package com.sbai.finance.activity.mine.wallet;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BindBankCardActivity extends AppCompatActivity {

    @BindView(R.id.name)
    AppCompatEditText mName;
    @BindView(R.id.bindBankCardHint)
    AppCompatImageView mBindBankCardHint;
    @BindView(R.id.identityCard)
    AppCompatEditText mIdentityCard;
    @BindView(R.id.bankCardNumber)
    AppCompatEditText mBankCardNumber;
    @BindView(R.id.bank)
    AppCompatTextView mBank;
    @BindView(R.id.bankChoose)
    AppCompatImageView mBankChoose;
    @BindView(R.id.phoneNumber)
    AppCompatEditText mPhoneNumber;
    @BindView(R.id.submitBankCardInfo)
    AppCompatButton mSubmitBankCardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_card);
        ButterKnife.bind(this);
        mBankCardNumber.addTextChangedListener(mBankCardValidationWatcher);
        mName.addTextChangedListener(mValidationWatcher);
        mIdentityCard.addTextChangedListener(mValidationWatcher);
        mPhoneNumber.addTextChangedListener(mValidationWatcher);
        mBank.addTextChangedListener(mValidationWatcher);
    }

    ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            boolean submitBtnEnable = checkSubmitBtnEnable();
            if (mSubmitBankCardInfo.isEnabled() != submitBtnEnable) {
                mSubmitBankCardInfo.setEnabled(submitBtnEnable);
            }
        }
    };

    ValidationWatcher mBankCardValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            mValidationWatcher.afterTextChanged(s);
            formatBankCardNumber();
        }
    };

    private void formatBankCardNumber() {
        String oldPhone = mBankCardNumber.getText().toString();
        String backNoSpace = oldPhone.replaceAll(" ", "");
        String newPhone = StrFormatter.getFormatBankCardNumber(backNoSpace);
        if (!newPhone.equalsIgnoreCase(oldPhone)) {
            mBankCardNumber.setText(newPhone);
            mBankCardNumber.setSelection(newPhone.length());
        }
    }

    private boolean checkSubmitBtnEnable() {
        return !TextUtils.isEmpty(getName())
                && !TextUtils.isEmpty(getIdentityCard())
                && !TextUtils.isEmpty(getBankCardNumber())
                && !TextUtils.isEmpty(getPhoneNumber())
                && !TextUtils.isEmpty(getBank());
    }

    private String getName() {
        return mName.getText().toString();
    }

    private String getIdentityCard() {
        return mIdentityCard.getText().toString().trim();
    }

    private String getBankCardNumber() {
        return mBankCardNumber.getText().toString().trim().replace(" ", "");
    }

    private String getPhoneNumber() {
        return mPhoneNumber.getText().toString().trim();
    }

    private String getBank() {
        return mBank.getText().toString();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mBankCardNumber.removeTextChangedListener(mBankCardValidationWatcher);
        mName.removeTextChangedListener(mValidationWatcher);
        mIdentityCard.removeTextChangedListener(mValidationWatcher);
        mPhoneNumber.removeTextChangedListener(mValidationWatcher);
        mBank.removeTextChangedListener(mValidationWatcher);
    }

    @OnClick({R.id.bindBankCardHint, R.id.bank, R.id.bankChoose, R.id.submitBankCardInfo})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bindBankCardHint:
                new BindBankHintDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.bank:
                break;
            case R.id.bankChoose:
                showBankCardPicker();
                break;
            case R.id.submitBankCardInfo:
                submitBankCardInfo();
                break;
        }
    }

    private void showBankCardPicker() {

    }

    private void submitBankCardInfo() {

    }
}
