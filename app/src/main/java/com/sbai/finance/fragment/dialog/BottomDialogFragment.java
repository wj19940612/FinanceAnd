package com.sbai.finance.fragment.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.R;

/**
 * 底部弹窗基础类
 */
public class BottomDialogFragment extends DialogFragment {

    protected String TAG;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = getClass().getSimpleName();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, getDialogTheme());
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(getWindowGravity());
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * getWidthRatio()), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public void show(FragmentManager manager) {
        try {
            this.show(manager, this.getClass().getSimpleName());
        } catch (IllegalStateException e) {
            Log.d(TAG, "show: " + e.toString());
        }
    }

    public void showAllowingStateLoss(FragmentManager manager) {
        FragmentTransaction ft = manager.beginTransaction();
        ft.add(this, this.getClass().getSimpleName());
        ft.commitAllowingStateLoss();
    }

    protected float getWidthRatio() {
        return 1f;
    }

    protected int getWindowGravity() {
        return Gravity.BOTTOM;
    }

    protected int getDialogTheme() {
        return R.style.BaseDialog_Bottom;
    }
}
