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

import com.sbai.finance.R;
import com.sbai.finance.activity.home.SearchOptionalActivity;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.Launcher;

import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class AddOptionalDialogFragment extends DialogFragment {

    private Unbinder mBind;

    public AddOptionalDialogFragment() {

    }

    public static AddOptionalDialogFragment newInstance() {
        Bundle args = new Bundle();
        AddOptionalDialogFragment fragment = new AddOptionalDialogFragment();
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
            window.setLayout(dm.widthPixels, WindowManager.LayoutParams.WRAP_CONTENT);
        }
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
                Launcher.with(getContext(),SearchOptionalActivity.class).putExtra("type", Variety.VAR_STOCK).execute();
                dismiss();
                break;
            case R.id.future:
                Launcher.with(getContext(),SearchOptionalActivity.class).putExtra("type", Variety.VAR_FUTURE).execute();
                dismiss();
                break;
            case R.id.cancel:
                this.dismiss();
                break;
        }
    }

    public void show(FragmentManager manager) {
        this.show(manager, AddOptionalDialogFragment.class.getSimpleName());
    }

}
