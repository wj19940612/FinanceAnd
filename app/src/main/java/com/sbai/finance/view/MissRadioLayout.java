package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sbai.finance.utils.Display;

/**
 * Created by ${wangJie} on 2017/11/20.
 * 姐说主页电台
 */

public class MissRadioLayout extends LinearLayout {


    public MissRadioLayout(Context context) {
        this(context, null);
    }

    public MissRadioLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MissRadioLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        init();

    }

    private void init() {
        setOrientation(VERTICAL);
        int defaultPadding = (int) Display.dp2Px(14, getResources());
        setPadding(defaultPadding, defaultPadding, defaultPadding, defaultPadding);

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(com.sbai.chart.R.mipmap.ic_launcher);

    }


}
