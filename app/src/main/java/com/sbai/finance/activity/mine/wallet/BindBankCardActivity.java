package com.sbai.finance.activity.mine.wallet;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.model.payment.CanUseBankListModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ValidationWatcher;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class BindBankCardActivity extends BaseActivity {

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

    private List<CanUseBankListModel> mCanUseBankListModels;
    //可用银行卡列表的银行卡名
    private String[] mBankNameList;


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
                showBankCardPicker();
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
        requestCanUseBankList();
    }

    private void submitBankCardInfo() {
        String name = getName();
        String bankCardNumber = getBankCardNumber();
        String bank = getBank();
        String identityCard = getIdentityCard();
        String bankId = "";
        String phoneNumber = getPhoneNumber();
//        Client.bindBankCard()
//                .setTag(this)
//                .setIndeterminate(this)
//                .setCallback(new Callback<Resp<Object>>() {
//                    @Override
//                    protected void onRespSuccess(Resp<Object> resp) {
//                        Log.d(TAG, "onRespSuccess: " + resp.toString());
//                    }
//                })
//                .fire();
    }

    private void requestCanUseBankList() {
        if (mCanUseBankListModels != null && !mCanUseBankListModels.isEmpty() && mBankNameList != null) {
            OptionPicker picker = new OptionPicker(this, mBankNameList);
            picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
            picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
            picker.setTopHeight(50);
            picker.setAnimationStyle(R.style.BottomDialogAnimation);
            picker.setOffset(2);
//            picker.setSelectedItem(mAgeList[mSelectAgeListIndex]);
            picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
            WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
            lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
            picker.setLineConfig(lineConfig);
            picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                @Override
                public void onOptionPicked(int index, String item) {
                    if (!TextUtils.isEmpty(item)) {
                        for (CanUseBankListModel data : mCanUseBankListModels) {
                            if (data.getName().equalsIgnoreCase(item)) {
                                break;
                            }
                        }
                    }
                }
            });
            picker.show();
        } else {
            Client.requestCanUseBankList()
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<CanUseBankListModel>, List<CanUseBankListModel>>() {
                        @Override
                        protected void onRespSuccessData(List<CanUseBankListModel> canUseBankListModelList) {
                            mCanUseBankListModels = canUseBankListModelList;
                            mBankNameList = new String[canUseBankListModelList.size()];
                            for (int i = 0; i < canUseBankListModelList.size(); i++) {
                                mBankNameList[i] = canUseBankListModelList.get(i).getName();
                            }
                            showBankCardPicker();
                        }
                    })
                    .fire();
        }

    }
}
