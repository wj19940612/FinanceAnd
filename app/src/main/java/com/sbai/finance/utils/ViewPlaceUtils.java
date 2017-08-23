package com.sbai.finance.utils;

import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by ${wangJie} on 2017/8/21.
 * view的位置 和改变view的位置
 */

public class ViewPlaceUtils {
    public static void setLayout(View view, int x, int y) {
        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

    public static void setLayout(View view, View old) {

        int left = old.getLeft();
        int right = old.getRight();
        int top = old.getTop();
        int bottom = old.getBottom();

        ViewGroup.MarginLayoutParams margin = new ViewGroup.MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(left, top, right, bottom);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }

}
