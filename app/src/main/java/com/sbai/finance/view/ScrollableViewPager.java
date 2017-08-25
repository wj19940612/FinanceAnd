package com.sbai.finance.view;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

public class ScrollableViewPager extends ViewPager {

    private boolean mScrollable;

    public ScrollableViewPager(Context context) {
        super(context);
    }

    public ScrollableViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setScrollable(boolean scrollable) {
        mScrollable = scrollable;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        return mScrollable && super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return mScrollable && super.onInterceptTouchEvent(ev);
    }

    public boolean isScrollable() {
        return mScrollable;
    }
}
