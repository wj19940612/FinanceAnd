package com.sbai.finance.view;

import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.GridView;

import com.sbai.finance.utils.Display;

/**
 * Created by houcc on 2017-07-27.
 */

public class HorizontalGridView extends GridView {
    public HorizontalGridView(Context context) {
        super(context);
    }

    public HorizontalGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HorizontalGridView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int rightPadding = (int) Display.dp2Px(20, getResources());
        int childCount = getChildCount();
        int childHeight = MeasureSpec.getSize(heightMeasureSpec);
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2, MeasureSpec.AT_MOST);
        super.onMeasure(expandSpec, heightMeasureSpec);
        //设置GridView的宽度
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            setMeasuredDimension(childCount * (getColumnWidth() + getHorizontalSpacing()) + rightPadding, childHeight);
        }
    }
}
