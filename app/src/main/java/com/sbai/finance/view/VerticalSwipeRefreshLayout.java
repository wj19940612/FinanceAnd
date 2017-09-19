package com.sbai.finance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * 解决横向滑动引起的SwipeRefreshLayout下拉事件冲突
 */

public class VerticalSwipeRefreshLayout extends CustomSwipeRefreshLayout {
	private float mDownX;
	private float mMoveX;

	public VerticalSwipeRefreshLayout(Context context) {
		this(context, null);
	}

	public VerticalSwipeRefreshLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent event) {

		switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				mDownX = event.getX();
				break;

			case MotionEvent.ACTION_MOVE:
				mMoveX = event.getX();
				float xDiff = Math.abs(mMoveX - mDownX);
				 //Log.d("refresh" ,"move----" + mMoveX + "   " + mDownX + "   " + mTouchSlop);
				// 增加60的容差，让下拉刷新在竖直滑动时就可以触发
				if (xDiff > mTouchSlop + 60) {
					return false;
				}
		}

		return super.onInterceptTouchEvent(event);
	}
}
