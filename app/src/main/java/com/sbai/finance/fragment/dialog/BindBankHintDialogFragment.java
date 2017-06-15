package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.AppCompatImageView;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/6/15.
 */

public class BindBankHintDialogFragment extends DialogFragment {



    @BindView(R.id.dialogDelete)
    AppCompatImageView mDialogDelete;
    private Unbinder mBind;

    public BindBankHintDialogFragment() {
    }

    public static BindBankHintDialogFragment newInstance(int title, int[] content) {
        Bundle args = new Bundle();
        BindBankHintDialogFragment fragment = new BindBankHintDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BindBankHintDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_bind_bank_hint, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.CENTER);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * 0.65), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }


    @OnClick(R.id.dialogDelete)
    public void onViewClicked() {
        dismissAllowingStateLoss();
    }

    public void show(FragmentManager manager) {
        this.show(manager, this.getClass().getSimpleName());
    }
}
