package com.sbai.finance.utils;

import android.content.Context;
import android.content.res.Resources;
import android.os.Build;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.WindowManager;

import com.sbai.finance.App;

public class Display {

    public static float dp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    public static float sp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, res.getDisplayMetrics());
    }

    public static DisplayMetrics getDisplayMetrics() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) App.getAppContext().getSystemService(Context.WINDOW_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            windowManager.getDefaultDisplay().getRealMetrics(displayMetrics);
        } else {
            windowManager.getDefaultDisplay().getMetrics(displayMetrics);
        }
        return displayMetrics;
    }

    public static float getScreenWidth() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        if (displayMetrics != null && displayMetrics.widthPixels != 0) {
            return displayMetrics.widthPixels;
        }
        return 1080f;
    }

    public static float getScreenHeight() {
        DisplayMetrics displayMetrics = getDisplayMetrics();
        if (displayMetrics != null && displayMetrics.heightPixels != 0) {
            return displayMetrics.heightPixels;
        }
        return 1920f;
    }

}
