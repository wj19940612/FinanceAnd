package com.sbai.finance.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Modified by john on 27/11/2017
 *
 * 关于 Textview 的工具: 包括全局设置字体大小，颜色
 */
public class TextViewUtils {

    /**
     * 设置 tv 的总体大小，单位 sp
     *
     * @param view 可以是 textview 也可以是 textview 的外层布局
     * @param spSize
     */
    public static void setTextViewSize(View view, int spSize) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setTextViewSize(((ViewGroup) view).getChildAt(i), spSize);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextSize(spSize);
        }
    }

    /**
     * 设置 tv 的颜色
     *
     * @param view
     * @param color
     */
    public static void setTextViewColor(View view, int color) {
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                setTextViewColor(((ViewGroup) view).getChildAt(i), color);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(color);
        }
    }
}
