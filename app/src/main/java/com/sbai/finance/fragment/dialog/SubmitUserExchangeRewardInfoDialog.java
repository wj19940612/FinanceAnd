package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.arena.UserGameInfo;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/11/1.
 * 竞技场用户兑换奖品 提交个人用户信息
 */

public class SubmitUserExchangeRewardInfoDialog extends DialogFragment {

    @BindView(R.id.dialogDelete)
    ImageView mDialogDelete;
    @BindView(R.id.userName)
    AppCompatEditText mUserName;
    @BindView(R.id.userAddress)
    AppCompatEditText mUserAddress;
    @BindView(R.id.phone)
    AppCompatEditText mPhone;
    @BindView(R.id.commit)
    TextView mCommit;
    private Unbinder mBind;

    OnUserInfoInputListener mOnUserInfoInputListener;


    public interface OnUserInfoInputListener {
        void onUserInput(UserGameInfo userGameInfo);
    }

    public SubmitUserExchangeRewardInfoDialog setOnUserInfoInputListener(OnUserInfoInputListener onUserInfoInputListener) {
        mOnUserInfoInputListener = onUserInfoInputListener;
        return this;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_user_exchange_reward_info, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }
    public void show(FragmentManager manager) {
        this.show(manager, this.getClass().getSimpleName());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
//        mUserName.postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                KeyBoardUtils.openKeyBoard(mUserName);
//            }
//        }, 220);
        mUserName.addTextChangedListener(mValidationWatcher);
        mUserAddress.addTextChangedListener(mValidationWatcher);
        mPhone.addTextChangedListener(mValidationWatcher);
    }

//    @Override
//    protected float getWidthRatio() {
//        return SmartDialog.DEFAULT_SCALE;
//    }
//
//    @Override
//    protected int getWindowGravity() {
//        return Gravity.CENTER;
//    }

//    @Override
//    protected int getDialogTheme() {
//        return R.style.BaseDialog;
//    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
//            boolean commitBtnEnable = checkoutCommitBtnEnable();
//            if (mCommit.isEnabled() != commitBtnEnable) {
//                mCommit.setEnabled(commitBtnEnable);
//            }
        }
    };

    private boolean checkoutCommitBtnEnable() {
        String userAddress = mUserAddress.getText().toString();
        String userName = mUserName.getText().toString();
        String phone = mPhone.getText().toString();
        return !TextUtils.isEmpty(userAddress) &&
                !TextUtils.isEmpty(userName) &&
                !TextUtils.isEmpty(phone);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.dialogDelete, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                dismiss();
                break;
            case R.id.commit:
                String userAddress = mUserAddress.getText().toString();
                String userName = mUserName.getText().toString();
                String phone = mPhone.getText().toString();
                UserGameInfo userGameInfo = new UserGameInfo();
                userGameInfo.setGameNickName(userName);
                userGameInfo.setAddress(userAddress);
                userGameInfo.setPhone(phone);
                if (mOnUserInfoInputListener != null) {
                    mOnUserInfoInputListener.onUserInput(userGameInfo);
                }
                dismiss();
                break;
        }
    }


    @OnClick(R.id.commit)
    public void onViewClicked() {

    }
}
