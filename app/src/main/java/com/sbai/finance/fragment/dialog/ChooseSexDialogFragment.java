package com.sbai.finance.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/7.
 */

public class ChooseSexDialogFragment extends BaseDialogFragment {

    @BindView(R.id.boy)
    AppCompatTextView mBoy;
    @BindView(R.id.girl)
    AppCompatTextView mGirl;
    @BindView(R.id.takePhoneCancel)
    AppCompatTextView mTakePhoneCancel;
    private Unbinder mBind;

    private OnUserSexListener mOnUserSexListener;

    public interface OnUserSexListener {
        void onUserSex(int userSex);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_choose_sex_, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnUserSexListener) {
            mOnUserSexListener = (OnUserSexListener) context;
        } else {
            throw new IllegalStateException(context.toString() + " " + " must implements OnUserSexListener");
        }

    }

    @OnClick({R.id.boy, R.id.girl, R.id.takePhoneCancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.boy:
                if (mOnUserSexListener != null) {
                    mOnUserSexListener.onUserSex(UserInfo.SEX_BOY);
                    LocalUser.getUser().getUserInfo().setUserSex(UserInfo.SEX_BOY);
                }
                dismissAllowingStateLoss();
                break;
            case R.id.girl:
                if (mOnUserSexListener != null) {
                    mOnUserSexListener.onUserSex(UserInfo.SEX_GIRL);
                    LocalUser.getUser().getUserInfo().setUserSex(UserInfo.SEX_GIRL);
                }
                dismissAllowingStateLoss();
                break;
            case R.id.takePhoneCancel:
                dismissAllowingStateLoss();
                break;
        }
    }
}
