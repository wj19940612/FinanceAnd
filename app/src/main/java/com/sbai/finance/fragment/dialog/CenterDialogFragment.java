package com.sbai.finance.fragment.dialog;

import android.view.Gravity;

import com.sbai.finance.R;

/**
 * 中间弹窗基础类
 */
public class CenterDialogFragment extends BottomDialogFragment {

    protected float getWidthRatio() {
        return 0.75f;
    }

    protected int getWindowGravity() {
        return Gravity.CENTER;
    }

    protected int getDialogTheme() {
        return R.style.BaseDialogFragment;
    }
}
