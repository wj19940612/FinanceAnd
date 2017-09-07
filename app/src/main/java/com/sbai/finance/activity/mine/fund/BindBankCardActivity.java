package com.sbai.finance.activity.mine.fund;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.model.fund.CanUseBankList;
import com.sbai.finance.model.fund.UserBankCardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SmartDialog;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

public class BindBankCardActivity extends BaseActivity {

    public static final int REQ_CODE_BIND_CARD = 417;

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


    @BindView(R.id.addBackLayout)
    LinearLayout mAddBackLayout;
    @BindView(R.id.bindBankName)
    TextView mBindBankName;
    @BindView(R.id.unBind)
    TextView mUnBind;
    @BindView(R.id.bindBankCard)
    TextView mBindBankCard;
    @BindView(R.id.bankCardLayout)
    RelativeLayout mBankCardLayout;

    private List<CanUseBankList> mCanUseBankLists;
    //可用银行卡列表的银行卡名
    private String[] mBankNameList;
    private UserBankCardInfo mUserBankCardInfo;
    private CanUseBankList mCanUseBankList;

    private UserBankCardInfo mUserBank;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bind_bank_card);
        ButterKnife.bind(this);
        mUserBank = new UserBankCardInfo();
        mUserBankCardInfo = getIntent().getParcelableExtra(Launcher.EX_PAY_END);
        mBankCardNumber.addTextChangedListener(mBankCardValidationWatcher);
        mName.addTextChangedListener(mValidationWatcher);
        mIdentityCard.addTextChangedListener(mValidationWatcher);
        mPhoneNumber.addTextChangedListener(mValidationWatcher);
        mBank.addTextChangedListener(mValidationWatcher);
        updateBankCardInfo();
    }

    private void updateBankCardInfo() {
        if (mUserBankCardInfo == null) return;
        if (mUserBankCardInfo.isBindBank()) {
            mAddBackLayout.setVisibility(View.GONE);
            mBankCardLayout.setVisibility(View.VISIBLE);
            SpannableString spannableString = StrUtil.mergeTextWithRatio(getString(R.string.bank_card_name, mUserBankCardInfo.getIssuingBankName()), getString(R.string.deposit_card), 0.8f);
            mBindBankName.setText(spannableString);
            mBindBankCard.setText(mUserBankCardInfo.getCardNumber().substring(mUserBankCardInfo.getCardNumber().length() - 4));

        } else {
            mAddBackLayout.setVisibility(View.VISIBLE);
            mBankCardLayout.setVisibility(View.GONE);
            mName.setText(mUserBankCardInfo.getRealName());
            mIdentityCard.setText(mUserBankCardInfo.getIdCard());
            mBankCardNumber.setText(mUserBankCardInfo.getCardNumber());
            mBank.setText(mUserBankCardInfo.getIssuingBankName());
            mPhoneNumber.setText(mUserBankCardInfo.getCardPhone());
            mCanUseBankList = new CanUseBankList();
            mCanUseBankList.setName(mUserBankCardInfo.getIssuingBankName());
            mName.postDelayed(new Runnable() {
                @Override
                public void run() {
                    KeyBoardUtils.openKeyBoard(mName);
                }
            }, 200);
        }
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

    @OnClick({R.id.bindBankCardHint, R.id.bank, R.id.bankChoose, R.id.submitBankCardInfo, R.id.unBind})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.bindBankCardHint:
                new BindBankHintDialogFragment().show(getSupportFragmentManager());
                break;
            case R.id.bank:
                showCanUserBankList();
                break;
            case R.id.bankChoose:
                showCanUserBankList();
                break;
            case R.id.submitBankCardInfo:
                umengEventCount(UmengCountEventIdUtils.BANK_CARD_FILL_NEXT_STEP);
                submitBankCardInfo();
                break;
            case R.id.unBind:
                umengEventCount(UmengCountEventIdUtils.BANK_CARD_LIST_UNBUNDLED);
                unbindBankCard();
                break;
        }
    }

    @Override
    public void onBackPressed() {
        if (mUserBankCardInfo != null && mUserBankCardInfo.isNotConfirmBankInfo()) {
            showGiveUpBindBankDialog();
        } else {
            try {
                // IllegalStateException: Can not perform this action after onSaveInstanceState
                // onBackPressed 出现抛出这个异常 。。。 禅道6733
                super.onBackPressed();
            } catch (RuntimeException e) {
                Log.e(TAG, "onBackPressed: " + e.toString());
                finish();
            }
        }

    }

    private void showGiveUpBindBankDialog() {
        if (isFinishing()) return;
        SmartDialog.with(this, R.string.is_give_up_bind_bank_card)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    private void unbindBankCard() {
        SmartDialog.with(this, R.string.un_bind_bank_card_hint, R.string.un_bind_bank_card_title)
                .setPositive(R.string.un_bind_bank_card_dialog_confirm, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        Launcher.with(getActivity(), FeedbackActivity.class).execute();
                    }
                })
                .show();
    }

    private void showBankCardPicker() {
        OptionPicker picker = new OptionPicker(this, mBankNameList);
        picker.setCancelTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setSubmitTextColor(ContextCompat.getColor(getActivity(), R.color.warningText));
        picker.setTopBackgroundColor(ContextCompat.getColor(getActivity(), R.color.background));
        picker.setPressedTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
        picker.setTopHeight(50);
        picker.setAnimationStyle(R.style.BottomDialogAnimation);
        picker.setOffset(2);
        if (mCanUseBankList != null && mCanUseBankList.getName() != null) {
            picker.setSelectedItem(mCanUseBankList.getName());
        }
        picker.setTextColor(ContextCompat.getColor(getActivity(), R.color.primaryText));
        WheelView.LineConfig lineConfig = new WheelView.LineConfig(0);//使用最长的分割线
        lineConfig.setColor(ContextCompat.getColor(getActivity(), R.color.split));
        picker.setLineConfig(lineConfig);
        picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
            @Override
            public void onOptionPicked(int index, String item) {
                if (!TextUtils.isEmpty(item)) {
                    mBank.setText(item);
                    for (CanUseBankList data : mCanUseBankLists) {
                        if (data.getName().equalsIgnoreCase(item)) {
                            mCanUseBankList = data;
                            mUserBankCardInfo.setBankId(mCanUseBankList.getId());
                            break;
                        }
                    }
                }
            }
        });
        picker.show();
    }

    private void submitBankCardInfo() {
        String name = getName();
        String bankCardNumber = getBankCardNumber();
        String bank = getBank();
        String identityCard = getIdentityCard();
        String phoneNumber = getPhoneNumber();
        if (mUserBankCardInfo != null) {
            if (mUserBankCardInfo.isNotConfirmBankInfo()) {
                Client.bindBankCard(name, identityCard, bankCardNumber, phoneNumber, bank, mCanUseBankList.getId())
                        .setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<Integer>>() {
                            @Override
                            protected void onRespSuccess(Resp<Integer> resp) {
                                if (resp.isSuccess()) {
                                    mUserBankCardInfo.setBindStatus(1);
                                    Intent intent = new Intent();
                                    intent.putExtra(Launcher.EX_PAY_END, true);
                                    intent.putExtra(Launcher.EX_PAYLOAD, mUserBank);
                                    setResult(RESULT_OK, intent);
                                    finish();

                                } else {
                                    ToastUtil.show(resp.getMsg());
                                }
                            }
                        })
                        .fire();
            } else {
                Client.updateBankCard(name, identityCard, bankCardNumber, phoneNumber, bank, mUserBankCardInfo.getBankId(), mUserBankCardInfo.getId())
                        .setTag(TAG)
                        .setIndeterminate(this)
                        .setCallback(new Callback<Resp<Integer>>() {
                            @Override
                            protected void onRespSuccess(Resp<Integer> resp) {
                                ToastUtil.show(resp.getMsg());
                                if (resp.isSuccess()) {
                                    mUserBankCardInfo.setBindStatus(1);
                                    if (resp.hasData()) {
                                        mUserBankCardInfo.setId(resp.getData());
                                    }
                                    Intent intent = new Intent();
                                    intent.putExtra(Launcher.EX_PAYLOAD, mUserBankCardInfo);
                                    setResult(RESULT_OK, intent);
                                    ToastUtil.show(resp.getMsg());
                                    finish();
                                }
                            }
                        })
                        .fire();
            }
        }

    }

    private void showCanUserBankList() {
        if (mCanUseBankLists != null && !mCanUseBankLists.isEmpty() && mBankNameList != null) {
            showBankCardPicker();
        } else {
            requestCanUserBankList();
        }

    }

    private void requestCanUserBankList() {
        Client.requestCanUseBankList()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<CanUseBankList>>, List<CanUseBankList>>() {
                    @Override
                    protected void onRespSuccessData(List<CanUseBankList> canUseBankListModelList) {
                        mCanUseBankLists = canUseBankListModelList;
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
