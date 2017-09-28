package com.sbai.finance.fragment.dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.trade.TradeWebActivity;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.utils.Launcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 *快速交易
 */

public class TradeOptionDialogFragment extends BottomDialogFragment {

    private Unbinder mBind;

    public static TradeOptionDialogFragment newInstance() {
        Bundle args = new Bundle();
        TradeOptionDialogFragment fragment = new TradeOptionDialogFragment();
        fragment.setArguments(args);
        return fragment;
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
            Preference.get().setAuthorizationTime(SysTime.getSysTime().getSystemTimestamp());
            Launcher.with(getContext(), TradeWebActivity.class)
                    .putExtra(WebActivity.EX_TITLE, getString(R.string.quick_trade))
                    .execute();
        }
        dismiss();
    }
}
