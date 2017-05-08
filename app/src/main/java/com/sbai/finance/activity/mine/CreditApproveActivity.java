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
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadUserImageDialogFragment;
import com.sbai.finance.utils.GlideRoundTransform;
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
    @BindView(R.id.addIdentityCardFront)
    AppCompatTextView mAddIdentityCardFront;
    @BindView(R.id.addIdentityCardReverse)
    AppCompatTextView mAddIdentityCardReverse;
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

    @OnClick({R.id.nameClear, R.id.identityCardNumberClear, R.id.addIdentityCardFront, R.id.addIdentityCardReverse, R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.nameClear:
                mRealNameInput.setText("");
                break;
            case R.id.identityCardNumberClear:
                mIdentityCardNumber.setText("");
                break;
            case R.id.addIdentityCardFront:
                UploadUserImageDialogFragment.newInstance(IDENTITY_CARD_FONT, false).show(getSupportFragmentManager());
                break;
            case R.id.addIdentityCardReverse:
                UploadUserImageDialogFragment.newInstance(IDENTITY_CARD_REVERSE, false).show(getSupportFragmentManager());
                break;
            case R.id.submit:
                submitUserCreditApprove();
                break;
        }
    }

    private void submitUserCreditApprove() {

    }

    @Override
    public void onImagePath(int index, String imagePath) {
        Log.d(TAG, "onImagePath: " + index + "  地址 " + imagePath);
        mImagePath.add(index, imagePath);
        if (index == IDENTITY_CARD_FONT) {
            mAddIdentityCardFront.setVisibility(View.GONE);
            mIdentityCardFrontImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imagePath).fitCenter().bitmapTransform(new GlideRoundTransform(this)).into(mIdentityCardFrontImage);
        } else if (index == IDENTITY_CARD_REVERSE) {
            mAddIdentityCardReverse.setVisibility(View.GONE);
            mIdentityCardReverseImage.setVisibility(View.VISIBLE);
            Glide.with(this).load(imagePath).fitCenter().bitmapTransform(new GlideRoundTransform(this)).into(mIdentityCardReverseImage);

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
