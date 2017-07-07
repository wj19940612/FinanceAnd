package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.R;

/**
 * Created by ${wangJie} on 2017/6/7.
 */

public class BaseDialogFragment extends DialogFragment {

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

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.UpLoadHeadImageDialog);
    }

    public void show(FragmentManager manager) {
        this.show(manager, this.getClass().getSimpleName());
    }

    public void showAsync(FragmentManager manager) {
        if (!isAdded()) {
            FragmentTransaction ft = manager.beginTransaction();
            ft.add(this, this.getClass().getSimpleName());
            ft.commitAllowingStateLoss();
        }
    }
}
