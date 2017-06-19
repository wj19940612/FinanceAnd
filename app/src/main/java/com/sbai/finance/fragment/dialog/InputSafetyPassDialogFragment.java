package com.sbai.finance.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SafetyPasswordEditText;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/17.
 */

public class InputSafetyPassDialogFragment extends DialogFragment {

    private static final String KEY_MONEY = "MONEY";

    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.money)
    TextView mMoney;
    @BindView(R.id.safety_password_number)
    SafetyPasswordEditText mSafetyPasswordNumber;
    private Unbinder mBind;

    OnPasswordListener mOnPasswordListener;
    private String mRechargeMoney;

    public interface OnPasswordListener {
        void onPassWord(String passWord);
    }


    public static InputSafetyPassDialogFragment newInstance(String money) {

        Bundle args = new Bundle();
        InputSafetyPassDialogFragment fragment = new InputSafetyPassDialogFragment();
        args.putString(KEY_MONEY, money);
        fragment.setArguments(args);
        return fragment;
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (s.toString().length() == 6) {
                mOnPasswordListener.onPassWord(s.toString());
                dismissAllowingStateLoss();
            }
        }
    };

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnPasswordListener) {
            mOnPasswordListener = (OnPasswordListener) context;
        } else {
            throw new IllegalStateException(context.toString() + " must  implements InputSafetyPassDialogFragment.OnPasswordListener");
        }
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.8), WindowManager.LayoutParams.WRAP_CONTENT);
        }

        mSafetyPasswordNumber.addTextChangedListener(mValidationWatcher);
        mMoney.setText(getString(R.string.yuan, mRechargeMoney));

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.input_safety_pass_dialog_fragment, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BindBankHintDialog);
        if (getArguments() != null) {
            mRechargeMoney = getArguments().getString(KEY_MONEY);
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, this.getClass().getSimpleName());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mBind != null) {
            mBind.unbind();
        }
//        mSafetyPasswordNumber.removeTextChangedListener(mValidationWatcher);
    }

    @OnClick(R.id.title)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }
}
