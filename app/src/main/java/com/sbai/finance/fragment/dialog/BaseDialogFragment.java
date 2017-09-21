package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.FloatRange;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
import android.support.annotation.RestrictTo;
import android.support.annotation.StyleRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.sbai.finance.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import static android.support.annotation.RestrictTo.Scope.LIBRARY_GROUP;

/**
 * Created by ${wangJie} on 2017/6/7.
 */

public class BaseDialogFragment extends DialogFragment {


    @RestrictTo(LIBRARY_GROUP)
    @IntDef({STYLE_NORMAL, STYLE_NO_TITLE, STYLE_NO_FRAME, STYLE_NO_INPUT})
    @Retention(RetentionPolicy.SOURCE)
    private @interface DialogStyle {
    }

    protected int mWindowGravity = Gravity.BOTTOM;
    protected float mWidthRatio = 1;
    protected int mDialogStyle = STYLE_NO_TITLE;
    protected int mDialogTheme = R.style.UpLoadHeadImageDialog;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setGravity(mWindowGravity);
            DisplayMetrics dm = new DisplayMetrics();
            getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
            window.setLayout((int) (dm.widthPixels * mWidthRatio), WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    public BaseDialogFragment setGravity(int gravity) {
        mWindowGravity = gravity;
        return this;
    }

    //dialog 和window宽度的比例
    public BaseDialogFragment setWindowWidthRatio(@FloatRange(from = 0, to = 1.0f) float widthRatio) {
        mWidthRatio = widthRatio;
        return this;
    }

    public BaseDialogFragment setDialogStyle(@DialogStyle int dialogStyle) {
        mDialogStyle = dialogStyle;
        return this;
    }

    public BaseDialogFragment setDialogTheme(@StyleRes int dialogTheme) {
        mDialogTheme = dialogTheme;
        return this;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(mDialogStyle, mDialogTheme);
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
