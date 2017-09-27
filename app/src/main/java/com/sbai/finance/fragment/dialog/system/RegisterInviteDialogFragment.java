package com.sbai.finance.fragment.dialog.system;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.dialog.CenterDialogFragment;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/9/12.
 * 用户注册即送300元宝
 */

public class RegisterInviteDialogFragment extends CenterDialogFragment {

    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    @BindView(R.id.immediatelyRegister)
    TextView mImmediatelyRegister;
    private Unbinder mBind;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_register_invite, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected float getWidthRatio() {
        return 0.8f;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.dialogDelete, R.id.immediatelyRegister})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.dialogDelete:
                Preference.get().setShowRegisterInviteDialog();
                dismissAllowingStateLoss();
                break;
            case R.id.immediatelyRegister:
                Preference.get().setShowRegisterInviteDialog();
                dismissAllowingStateLoss();
                Launcher.with(getActivity(), LoginActivity.class).execute();
                break;
        }
    }
}
