package com.sbai.finance.activity.mine.userinfo;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.DefaultRetryPolicy;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
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
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.CustomToast;
import com.sbai.finance.view.SmartDialog;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class CreditApproveActivity extends BaseActivity implements UploadUserImageDialogFragment.OnImagePathListener {


    private static final int IDENTITY_CARD_FONT = 0;
    private static final int IDENTITY_CARD_REVERSE = 1;
    @BindView(R.id.approveHint)
    AppCompatTextView mApproveHint;
    @BindView(R.id.realNameInput)
    AppCompatEditText mRealNameInput;
    @BindView(R.id.nameClear)
    AppCompatImageView mNameClear;
    @BindView(R.id.identityCardNumber)
    AppCompatEditText mIdentityCardNumber;
    @BindView(R.id.identityCardNumberClear)
    AppCompatImageView mIdentityCardNumberClear;
    @BindView(R.id.identityCardPhone)
    AppCompatTextView mIdentityCardPhone;
    @BindView(R.id.identityCardFrontImage)
    AppCompatImageView mIdentityCardFrontImage;
    @BindView(R.id.identityCardReverseImage)
    AppCompatImageView mIdentityCardReverseImage;
    @BindView(R.id.phoneLl)
    LinearLayout mPhoneLl;
    @BindView(R.id.submit)
    AppCompatTextView mSubmit;

    private boolean mEnable;
    private SparseArray<String> mImagePath;
    private UserIdentityCardInfo mUserIdentityCardInfo;
    private int mDataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_approve);
        ButterKnife.bind(this);
        mDataId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        mImagePath = new SparseArray<>();
        requestUserCreditApproveStatus();
        mRealNameInput.addTextChangedListener(mValidationWatcher);
        mIdentityCardNumber.addTextChangedListener(mIdentityCardApproveWatcher);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealNameInput.removeTextChangedListener(mValidationWatcher);
        mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
    }

    private void requestUserCreditApproveStatus() {
        if (mDataId != -1) {
            Client.queryCertification(mDataId)
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<UserIdentityCardInfo>, UserIdentityCardInfo>() {
                        @Override
                        protected void onRespSuccessData(UserIdentityCardInfo data) {
                            Log.d(TAG, "onRespSuccessData: " + data.toString());
                            mImagePath.append(0, data.getCertPositive());
                            mImagePath.append(1, data.getCertBack());
                            updateUserCreditStatus(data);
                            mUserIdentityCardInfo = data;
                        }

                    })
                    .fireFree();
        } else {
            Client.getUserCreditApproveStatus()
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<UserIdentityCardInfo>>() {
                        @Override
                        protected void onRespSuccess(Resp<UserIdentityCardInfo> resp) {
                            if (resp.isSuccess()) {
                                if (resp.hasData()) {
                                    UserIdentityCardInfo data = resp.getData();
                                    UserInfo userInfo = LocalUser.getUser().getUserInfo();
                                    userInfo.setStatus(data.getStatus());
                                    LocalUser.getUser().setUserInfo(userInfo);
                                    mImagePath.append(0, data.getCertPositive());
                                    mImagePath.append(1, data.getCertBack());
                                    updateUserCreditStatus(data);
                                    mUserIdentityCardInfo = data;
                                } else {
                                    openKeyBoard();
                                }
                            }
                        }
                    })
                    .fireFree();
        }
    }

    private void updateUserCreditStatus(UserIdentityCardInfo data) {
        if (data.getStatus() != null) {
            switch (data.getStatus()) {
                case UserInfo.CREDIT_IS_NOT_APPROVE:
                    setViewEnable(true);
                    mEnable = true;
                    mSubmit.setText(R.string.submit_has_empty);
                    updateUserInfo(data);
                    openKeyBoard();
                    break;
                case UserInfo.CREDIT_IS_APPROVE_ING:
                    mRealNameInput.removeTextChangedListener(mValidationWatcher);
                    mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
                    mSubmit.setText(R.string.is_auditing);
                    mEnable = false;
                    setViewEnable(false);
                    updateUserInfo(data);
                    break;
                case UserInfo.CREDIT_IS_ALREADY_APPROVE:
                    mRealNameInput.removeTextChangedListener(mValidationWatcher);
                    mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
                    setViewEnable(false);
                    mEnable = false;
                    mApproveHint.setText(R.string.authenticated_info);
                    mSubmit.setVisibility(View.GONE);
                    mPhoneLl.setVisibility(View.GONE);
                    mIdentityCardPhone.setVisibility(View.GONE);
                    mRealNameInput.setText(formatUserName(data.getRealName()));
                    mIdentityCardNumber.setText(formatIdentityCard(data.getCertCode()));
                    break;
            }
        }
    }

    private void openKeyBoard() {
        mRealNameInput.postDelayed(new Runnable() {
            @Override
            public void run() {
                KeyBoardUtils.openKeyBoard(mRealNameInput);
            }
        }, 300);
    }


    private String formatUserName(String userName) {
        int length = userName.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length - 1; i++) {
            stringBuilder.append("*");
        }
        return userName.substring(0, 1) + stringBuilder.toString();
    }

    private String formatIdentityCard(String certCode) {
        int length = certCode.length();
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length - 6; i++) {
            stringBuilder.append("*");
        }
        return certCode.substring(0, 4) + stringBuilder.toString() + certCode.substring(length - 2);
    }

    private void updateUserInfo(UserIdentityCardInfo data) {
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
                UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMMOBILIZATION_AREA, IDENTITY_CARD_FONT).show(getSupportFragmentManager());
                break;
            case R.id.identityCardReverseImage:
                UploadUserImageDialogFragment.newInstance(UploadUserImageDialogFragment.IMAGE_TYPE_CLIPPING_IMMOBILIZATION_AREA, IDENTITY_CARD_REVERSE).show(getSupportFragmentManager());
                break;
            case R.id.submit:
                submit();
                break;
        }
    }

    private void submit() {
        final String realName = getRealName();
        final String identityCard = getIdentityCard();

//        if (!ValidityDecideUtil.isOnlyAChineseName(realName)) {
//            ToastUtil.curt(R.string.real_name_error);
//            return;
//        }
//
//        if (!ValidityDecideUtil.isIdentityCard(identityCard)) {
//            ToastUtil.curt(R.string.identity_card_error);
//            return;
//        }

        SmartDialog.with(this, R.string.if_submit_credit_approve, R.string.hint)
                .setNegative(R.string.cancel)
                .setMessageTextSize(16)
                .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        if (mUserIdentityCardInfo != null && mUserIdentityCardInfo.isCheckNotPass()) {
                            updateUserCreditApprove(realName, identityCard);
                        } else {
                            submitUserCreditApprove(realName, identityCard);
                        }

                    }
                }).show();
    }

    private void updateUserCreditApprove(final String realName, final String identityCard) {
        if (mImagePath.size() > 1) {
            final String imageFront = mImagePath.get(0);
            final String imageReserve = mImagePath.get(1);

            if (isSameIdentityCardFrontPath() && isSameIdentityCardReservePath()) {
                updateUserCreditApprove(realName, identityCard, imageFront, imageReserve);
            } else {
                if (!isSameIdentityCardFrontPath()) {
                    submitUserIdentityCardFrontImage(realName, identityCard, imageFront, imageReserve);
                } else if (!isSameIdentityCardReservePath()) {
                    submitUserIdentityCardReserveImage(realName, identityCard, imageFront, imageReserve);
                }
            }
        }
    }

    private void submitUserIdentityCardFrontImage(final String realName, final String identityCard, String imageFront, final String imageReserve) {
        Client.uploadImage(ImageUtils.compressImageToBase64(imageFront))
                .setTag(TAG)
                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setCallback(new Callback2D<Resp<String>, String>() {
                    @Override
                    protected void onRespSuccessData(final String frontPath) {
                        mImagePath.put(0, frontPath);
                        mUserIdentityCardInfo.setCertPositive(frontPath);
                        if (isSameIdentityCardReservePath()) {
                            updateUserCreditApprove(realName, identityCard, frontPath, imageReserve);
                        } else {
                            submitUserIdentityCardReserveImage(realName, identityCard, frontPath, imageReserve);
                        }
                    }
                })
                .fire();
    }

    private void submitUserIdentityCardReserveImage(final String realName, final String identityCard, final String frontPath, final String imageReserve) {
        Client.uploadImage(ImageUtils.compressImageToBase64(imageReserve))
                .setTag(TAG)
                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setCallback(new Callback2D<Resp<String>, String>() {
                    @Override
                    protected void onRespSuccessData(String data) {
                        mImagePath.put(1, data);
                        mUserIdentityCardInfo.setCertBack(data);
                        if (isSameIdentityCardFrontPath()) {
                            updateUserCreditApprove(realName, identityCard, frontPath, data);
                        } else {
                            submitUserIdentityCardFrontImage(realName, identityCard, frontPath, imageReserve);
                        }
                    }
                })
                .fire();
    }

    private boolean isSameIdentityCardFrontPath() {
        return mUserIdentityCardInfo != null
                && mUserIdentityCardInfo.getCertPositive().equalsIgnoreCase(mImagePath.get(0));
    }

    private boolean isSameIdentityCardReservePath() {
        return mUserIdentityCardInfo != null
                && mUserIdentityCardInfo.getCertBack().equalsIgnoreCase(mImagePath.get(1));
    }

    private void updateUserCreditApprove(String realName, String identityCard, String imageFront, String imageReserve) {
        Client.updateUserCreditApproveInfo(imageReserve, imageFront, identityCard, realName)
                .setIndeterminate(this)
                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setCallback(new Callback<Resp<JsonObject>>() {
                    @Override
                    protected void onRespSuccess(Resp<JsonObject> resp) {
                        if (resp.isSuccess()) {
                            setResult(RESULT_OK);
                            CustomToast.getInstance().showText(CreditApproveActivity.this, R.string.submit_success);
                            UserInfo userInfo = LocalUser.getUser().getUserInfo();
                            userInfo.setStatus(UserInfo.CREDIT_IS_APPROVE_ING);
                            LocalUser.getUser().setUserInfo(userInfo);
                            mSubmit.setText(R.string.is_auditing);
                            mSubmit.setEnabled(false);
                            setViewEnable(false);
                        } else {
                            mSubmit.setText(R.string.again_submit);
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                })
                .fire();
    }

    private void submitUserCreditApprove(final String realName, String identityCard) {
        if (mImagePath.size() > 1) {
            String imageFront = ImageUtils.compressImageToBase64(mImagePath.get(0));
            String imageReserve = ImageUtils.compressImageToBase64(mImagePath.get(1));
            if (TextUtils.isEmpty(imageFront)) {
                imageFront = mImagePath.get(0);
            }
            if (TextUtils.isEmpty(imageReserve)) {
                imageReserve = mImagePath.get(1);
            }
            Client.submitUserCreditApproveInfo(imageReserve, imageFront, identityCard, realName)
                    .setIndeterminate(this)
                    .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            if (resp.isSuccess()) {
                                setResult(RESULT_OK);
                                CustomToast.getInstance().showText(CreditApproveActivity.this, R.string.submit_success);
                                UserInfo userInfo = LocalUser.getUser().getUserInfo();
                                userInfo.setStatus(UserInfo.CREDIT_IS_APPROVE_ING);
                                LocalUser.getUser().setUserInfo(userInfo);
                                mSubmit.setText(R.string.is_auditing);
                                mSubmit.setEnabled(false);
                                setViewEnable(false);
                            } else {
                                mSubmit.setText(R.string.again_submit);
                                ToastUtil.show(resp.getMsg());
                            }
                        }
                    })
                    .fire();
        }
    }


    @Override
    public void onImagePath(int index, String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            mImagePath.append(index, imagePath);
            if (index == IDENTITY_CARD_FONT) {
                loadIdentityCardFontImage(imagePath);
            } else if (index == IDENTITY_CARD_REVERSE) {
                loadIdentityCardReserveImage(imagePath);
            }
        }
        changeSubmitEnable();
    }

    private void loadIdentityCardReserveImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mIdentityCardReverseImage.setBackground(null);
            }
            GlideApp.with(this).load(imagePath)
                    .centerCrop()
                    .placeholder(R.drawable.bg_add_identity_card_reserve)
                    .error(R.drawable.bg_add_identity_card_reserve)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .into(mIdentityCardReverseImage);
        }
    }

    private void loadIdentityCardFontImage(String imagePath) {
        if (!TextUtils.isEmpty(imagePath)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mIdentityCardFrontImage.setBackground(null);
            }
            GlideApp.with(this).load(imagePath)
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
                    .placeholder(R.drawable.bg_add_identity_card_font)
                    .error(R.drawable.bg_add_identity_card_font)
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
