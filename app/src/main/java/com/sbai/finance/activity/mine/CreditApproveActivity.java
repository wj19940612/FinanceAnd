package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatImageView;
import android.support.v7.widget.AppCompatTextView;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideRoundTransform;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.utils.ValidationWatcher;

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


    private ArrayList<String> mImagePath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_credit_approve);
        ButterKnife.bind(this);
        mImagePath = new ArrayList<>();
        mRealNameInput.addTextChangedListener(mValidationWatcher);
        mIdentityCardNumber.addTextChangedListener(mIdentityCardApproveWatcher);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        mRealNameInput.removeTextChangedListener(mValidationWatcher);
        mIdentityCardNumber.removeTextChangedListener(mIdentityCardApproveWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
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
        return mImagePath.size() == 2 && !TextUtils.isEmpty(getRealName()) && getIdentityCard().length() > 14;
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
                submitUserCreditApprove();
                break;
        }
    }

    private void submitUserCreditApprove() {
        String realName = getRealName();
        String identityCard = getIdentityCard();
        if (!mImagePath.isEmpty() && mImagePath.size() > 1) {
//            String imageFront = ImageUtils.bitmapToBase64(BitmapFactory.decodeFile(mImagePath.get(0)));
//            String imageReserve = ImageUtils.bitmapToBase64(BitmapFactory.decodeFile(mImagePath.get(1)));

            String imageFront = ImageUtils.compressImageToBase64(mImagePath.get(0));
            String imageReserve = ImageUtils.compressImageToBase64(mImagePath.get(1));
//            Log.d(TAG, "submitUserCreditApprove: " + imageFront.length() + "  压缩后的 " + imageToBase64.length() +
//                    " \n 反面 " + imageReserve.length() + " 压缩后的  " + base64.length());
            Client.submitUserCreditApproveInfo(imageFront, imageReserve, identityCard, realName)
                    .setIndeterminate(this)
                    .setCallback(new Callback<Resp<JsonObject>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonObject> resp) {
                            Log.d(TAG, "onRespSuccess: " + resp.toString());
                        }
                    })
                    .fire();

        }
    }

    @Override
    public void onImagePath(int index, String imagePath) {
        Log.d(TAG, "onImagePath: " + index + "  地址 " + imagePath);
        mImagePath.add(index, imagePath);
        if (index == IDENTITY_CARD_FONT) {
            Glide.with(this).load(imagePath).fitCenter()
                    .bitmapTransform(new GlideRoundTransform(this))
                    .placeholder(R.drawable.bg_add_identity_card_font)
                    .into(mIdentityCardFrontImage);
        } else if (index == IDENTITY_CARD_REVERSE) {
            Glide.with(this).load(imagePath).fitCenter()
                    .bitmapTransform(new GlideRoundTransform(this))
                    .placeholder(R.drawable.bg_add_identity_card_reserve)
                    .into(mIdentityCardReverseImage);
        }
        changeSubmitEnable();
    }

    private void changeSubmitEnable() {
        boolean submitEnable = checkSubmitEnable();
        if (submitEnable != mSubmit.isEnabled()) {
            mSubmit.setEnabled(submitEnable);
        }
    }
}
