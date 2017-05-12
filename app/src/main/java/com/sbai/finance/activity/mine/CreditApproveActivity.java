package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserIdentityCardInfo;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreditApproveActivity extends BaseActivity implements UploadUserImageDialogFragment.OnImagePathListener {


    private static final int IDENTITY_CARD_FONT = 0;
    private static final int IDENTITY_CARD_REVERSE = 1;
    @BindView(R.id.realNameInput)
    AppCompatEditText mRealNameInput;
    @BindView(R.id.nameClear)
    AppCompatImageView mNameClear;
    @BindView(R.id.identityCardNumber)
    AppCompatEditText mIdentityCardNumber;
    @BindView(R.id.identityCardNumberClear)
    AppCompatImageView mIdentityCardNumberClear;
    @BindView(R.id.identityCardFrontImage)
    AppCompatImageView mIdentityCardFrontImage;
    @BindView(R.id.identityCardReverseImage)
    AppCompatImageView mIdentityCardReverseImage;
    @BindView(R.id.errorHint)
    AppCompatTextView mErrorHint;
    @BindView(R.id.submit)
    AppCompatTextView mSubmit;

    private boolean mEnable;
    private ArrayList<String> mImagePath;
    private UserIdentityCardInfo mUserIdentityCardInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_approve);
        ButterKnife.bind(this);
        mImagePath = new ArrayList<>();
        requestUserCreditApproveStatus();
        mRealNameInput.addTextChangedListener(mValidationWatcher);
        mIdentityCardNumber.addTextChangedListener(mIdentityCardApproveWatcher);
//        loadIdentityCardReserveImage("");
//        loadIdentityCardFontImage("");
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealNameInput.removeTextChangedListener(mValidationWatcher);
        mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
    }

    private void requestUserCreditApproveStatus() {
        Client.getUserCreditApproveStatus()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<UserIdentityCardInfo>, UserIdentityCardInfo>(false) {
                    @Override
                    protected void onRespSuccessData(UserIdentityCardInfo data) {
                        UserInfo userInfo = LocalUser.getUser().getUserInfo();
                        userInfo.setStatus(data.getStatus());
                        LocalUser.getUser().setUserInfo(userInfo);
                        updateUserCreditStatus(data);
                        mUserIdentityCardInfo = data;
                    }
                })
                .fireSync();
    }

    private void updateUserCreditStatus(UserIdentityCardInfo data) {
        if (data.getStatus() != null) {
            switch (data.getStatus()) {
                case UserInfo.CREDIT_IS_NOT_APPROVE:
                    setViewEnable(true);
                    mEnable = true;
                    mSubmit.setText(R.string.submit_has_empty);
                    break;
                case UserInfo.CREDIT_IS_APPROVE_ING:
                    mRealNameInput.removeTextChangedListener(mValidationWatcher);
                    mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
                    mSubmit.setText(R.string.is_auditing);
                    mEnable = false;
                    setViewEnable(false);
                    break;
                case UserInfo.CREDIT_IS_ALREADY_APPROVE:
                    mRealNameInput.removeTextChangedListener(mValidationWatcher);
                    mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
                    setViewEnable(false);
                    mEnable = false;
                    mSubmit.setVisibility(View.GONE);
                    break;
            }
        }
        mRealNameInput.setText(data.getRealName());
        mIdentityCardNumber.setText(data.getCertCode());
        loadIdentityCardFontImage(data.getCertPositive());
        loadIdentityCardReserveImage(data.getCertBack());
    }

    private void setViewEnable(boolean enable) {
        mRealNameInput.setEnabled(enable);
        mIdentityCardNumber.setEnabled(enable);
        mIdentityCardFrontImage.setEnabled(enable);
        mIdentityCardReverseImage.setEnabled(enable);
        if (!enable) {
            mNameClear.setVisibility(View.INVISIBLE);
            mIdentityCardNumberClear.setVisibility(View.INVISIBLE);
        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (mUserIdentityCardInfo != null && mUserIdentityCardInfo.getStatus() != UserInfo.CREDIT_IS_NOT_APPROVE)
                return;
            changeSubmitEnable();
            if (!TextUtils.isEmpty(s.toString())) {
                mNameClear.setVisibility(View.VISIBLE);
            } else {
                mNameClear.setVisibility(View.INVISIBLE);
            }
        }
    };

    private ValidationWatcher mIdentityCardApproveWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            changeSubmitEnable();
            if (!TextUtils.isEmpty(s.toString())) {
                mIdentityCardNumberClear.setVisibility(View.VISIBLE);
            } else {
                mIdentityCardNumberClear.setVisibility(View.INVISIBLE);
            }
        }
    };

    private boolean checkSubmitEnable() {
        return mImagePath.size() > 1 && !TextUtils.isEmpty(getRealName()) && getIdentityCard().length() > 14;
    }

    private String getRealName() {
        return mRealNameInput.getText().toString().trim();
    }

    private String getIdentityCard() {
        return mIdentityCardNumber.getText().toString().trim();
    }

    @OnClick({R.id.nameClear, R.id.identityCardNumberClear, R.id.identityCardFrontImage, R.id.identityCardReverseImage, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nameClear:
                mRealNameInput.setText("");
                break;
            case R.id.identityCardNumberClear:
                mIdentityCardNumber.setText("");
                break;
            case R.id.identityCardFrontImage:
                UploadUserImageDialogFragment.newInstance(IDENTITY_CARD_FONT, false).show(getSupportFragmentManager());
                break;
            case R.id.identityCardReverseImage:
                UploadUserImageDialogFragment.newInstance(IDENTITY_CARD_REVERSE, false).show(getSupportFragmentManager());
                break;
            case R.id.submit:
                SmartDialog.with(this, R.string.if_submit_credit_approve)
                        .setNegative(R.string.cancel)
                        .setMessageTextSize(16)
                        .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                            @Override
                            public void onClick(Dialog dialog) {
                                dialog.dismiss();
                                submitUserCreditApprove();
                            }
                        }).show();

                break;
        }
    }

    private void submitUserCreditApprove() {
        final String realName = getRealName();
        String identityCard = getIdentityCard();
        if (!mImagePath.isEmpty() && mImagePath.size() > 1) {
            String imageFront = ImageUtils.compressImageToBase64(mImagePath.get(0));
            String imageReserve = ImageUtils.compressImageToBase64(mImagePath.get(1));
            Client.submitUserCreditApproveInfo(imageFront, imageReserve, identityCard, realName)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            setResult(RESULT_OK);
                            CustomToast.getInstance().showText(CreditApproveActivity.this, R.string.submit_success);
                            UserInfo userInfo = LocalUser.getUser().getUserInfo();
                            userInfo.setStatus(UserInfo.CREDIT_IS_APPROVE_ING);
                            LocalUser.getUser().setUserInfo(userInfo);
                            mSubmit.setText(R.string.is_auditing);
                            mSubmit.setEnabled(false);
                            setViewEnable(false);
                        }
                    })
                    .fire();
        }
    }

    @Override
    public void onImagePath(int index, String imagePath) {
        mImagePath.add(index, imagePath);
        if (index == IDENTITY_CARD_FONT) {
            loadIdentityCardFontImage(imagePath);
        } else if (index == IDENTITY_CARD_REVERSE) {
            loadIdentityCardReserveImage(imagePath);
        }
        changeSubmitEnable();
    }

    private void loadIdentityCardReserveImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mIdentityCardReverseImage.setBackground(null);
            }
            Glide.with(this).load(imagePath)
                    .centerCrop()
                    .error(R.drawable.bg_add_identity_card_reserve)
                    .placeholder(R.drawable.bg_add_identity_card_reserve)
                    .into(mIdentityCardReverseImage);
        }
    }

    private void loadIdentityCardFontImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mIdentityCardFrontImage.setBackground(null);
            }
            Glide.with(this).load(imagePath)
                    .error(R.drawable.bg_add_identity_card_font)
                    .placeholder(R.drawable.bg_add_identity_card_font)
                    .into(mIdentityCardFrontImage);
        }
    }

    private void changeSubmitEnable() {
        boolean submitEnable = checkSubmitEnable();
        if (submitEnable != mSubmit.isEnabled()) {
            mSubmit.setEnabled(submitEnable);
        }
    }
}
