package com.sbai.finance.fragment.dialog;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.activity.home.SearchOptionalActivity;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.Launcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AddOptionalDialogFragment extends BottomDialogFragment {

    private Unbinder mBind;

    public static AddOptionalDialogFragment newInstance() {
        Bundle args = new Bundle();
        AddOptionalDialogFragment fragment = new AddOptionalDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_add_optional, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    @OnClick({R.id.stock, R.id.future, R.id.cancel})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stock:
                Launcher.with(getContext(), SearchOptionalActivity.class).putExtra("type", Variety.VAR_STOCK).execute();
                dismiss();
                break;
            case R.id.future:
                Launcher.with(getContext(), SearchOptionalActivity.class).putExtra("type", Variety.VAR_FUTURE).execute();
                dismiss();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }
}
