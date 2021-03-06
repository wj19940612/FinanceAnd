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

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.trade.TradeWebActivity;
import com.sbai.finance.model.local.SysTime;
import com.sbai.finance.net.Client;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class AuthorizationLoginDialogFragment extends DialogFragment {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    private Unbinder mBind;

    public AuthorizationLoginDialogFragment() {

    }

    public static AuthorizationLoginDialogFragment newInstance() {
        Bundle args = new Bundle();
        AuthorizationLoginDialogFragment fragment = new AuthorizationLoginDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.BaseDialog_Bottom);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(Gravity.BOTTOM);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((dm.widthPixels), dm.heightPixels);
        }
        initTitleBar();
    }

    private void initTitleBar() {
        mTitleBar.setBackClickListener(new TitleBar.OnBackClickListener() {
            @Override
            public void onClick() {
                dismiss();
            }
        });
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_authorization_login, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.login, R.id.thirdPartyProtocol})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.login:
                Preference.get().setAuthorizationTime(SysTime.getSysTime().getSystemTimestamp());
                Launcher.with(getContext(), TradeWebActivity.class)
                        .putExtra(WebActivity.EX_TITLE, getString(R.string.quick_trade))
                        .execute();
                dismiss();
                break;
            case R.id.thirdPartyProtocol:
                openUserProtocolPage();
                break;
        }
    }

    private void openUserProtocolPage() {
        Launcher.with(getActivity(), WebActivity.class)
                .putExtra(WebActivity.EX_TITLE, getString(R.string.user_protocol))
                .putExtra(WebActivity.EX_URL, Client.WEB_USER_PROTOCOL_PAGE_URL)
                .execute();
    }


    public void show(FragmentManager manager) {
        this.show(manager, AuthorizationLoginDialogFragment.class.getSimpleName());
    }

}
