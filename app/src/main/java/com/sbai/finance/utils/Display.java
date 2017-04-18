package com.sbai.finance.utils;

import android.content.res.Resources;
import android.util.TypedValue;

public class Display {

    public static float dp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, res.getDisplayMetrics());
    }

    public static float sp2Px(float value, Resources res) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, value, res.getDisplayMetrics());
    }

}
