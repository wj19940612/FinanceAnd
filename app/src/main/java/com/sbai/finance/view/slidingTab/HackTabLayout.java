package com.sbai.finance.view.slidingTab;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import java.lang.reflect.Field;

/**
 * Modified by john on 26/10/2017
 *
 * 修改 TabLayout 添加顶部 tab 之间的 margin
 *
 */
public class HackTabLayout extends TabLayout {

    private static final float MARGIN_DP = 10;

    public HackTabLayout(Context context) {
        super(context);
    }

    public HackTabLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HackTabLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        Class<?> tabLayoutClass = getClass().getSuperclass();
        Field field = null;
        try {
            field = tabLayoutClass.getDeclaredField("mTabStrip");
            field.setAccessible(true);
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }

        LinearLayout tabStrip = null;
        try {
            if (field != null) {
                tabStrip = (LinearLayout) field.get(this);
            }

            for (int i = 0; i < tabStrip.getChildCount(); i++) {
                View child = tabStrip.getChildAt(i);
                LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) child.getLayoutParams();
                //int margin = (int) Display.dp2Px(MARGIN_DP, getResources());
                params.setMargins(0, 0, 0, 0);
                child.setPadding(0, 0, 0, 0);
                child.invalidate();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
