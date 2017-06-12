package com.sbai.finance.fragment.dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.trade.TradeWebActivity;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.utils.Launcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by linrongfang on 2017/5/10.
 */

public class TradeOptionDialogFragment extends DialogFragment {

    private Unbinder mBind;

    public TradeOptionDialogFragment() {

    }

    public static TradeOptionDialogFragment newInstance() {
        Bundle args = new Bundle();
        TradeOptionDialogFragment fragment = new TradeOptionDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.UpLoadHeadImageDialog);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_trade_option, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.quickTrade, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.quickTrade:
                checkAuthorizationTimeOverdue();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    private void checkAuthorizationTimeOverdue() {
        long lastTime = Preference.get().getAuthorizationTime();
        long diffTime = SysTime.getSysTime().getSystemTimestamp() - lastTime;
        boolean overdue = diffTime / (1000 * 60 * 60 * 24) >= 7;
        if (lastTime == 0 || overdue) {
            AuthorizationLoginDialogFragment.newInstance().show(getActivity().getSupportFragmentManager());
        } else {
            Launcher.with(getContext(), TradeWebActivity.class).execute();
        }
        dismiss();
    }

    public void show(FragmentManager manager) {
        this.show(manager, TradeOptionDialogFragment.class.getSimpleName());
    }

}