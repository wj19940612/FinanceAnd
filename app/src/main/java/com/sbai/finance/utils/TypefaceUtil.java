package com.sbai.finance.utils;

import android.graphics.Typeface;
import android.widget.TextView;

import com.sbai.finance.App;

/**
 * Created by ${wangJie} on 2017/8/18.
 */

public class TypefaceUtil {

    public static Typeface getHelveticaTypeface() {
        return Typeface.createFromAsset(App.getAppContext().getAssets(),
                "fonts/Helvetica LT Compressed.ttf");
    }

    public static void setHelveticaLTCompressedFont(TextView textView) {
        textView.setTypeface(getHelveticaTypeface());
    }

}
