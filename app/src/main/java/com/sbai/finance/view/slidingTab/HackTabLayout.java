package com.sbai.finance.view.slidingTab;

import android.content.Context;
import android.support.design.widget.TabLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.sbai.finance.utils.Display;

import java.lang.reflect.Field;

public class HackTabLayout extends TabLayout {

    private static final float PADDING_DP = 10;

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
                int padding = (int) Display.dp2Px(PADDING_DP, getResources());
                params.setMargins(padding, 0, padding, 0);
                child.invalidate();
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
