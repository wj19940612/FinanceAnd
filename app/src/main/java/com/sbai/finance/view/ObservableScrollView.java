package com.sbai.finance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * 有滑动监听的ScrollView
 */

public class ObservableScrollView extends ScrollView {

	public interface ScrollViewListener {
		void onScrollChanged(ObservableScrollView scrollView, int x, int y,
		                     int oldX, int oldY);
	}

	private ScrollViewListener scrollViewListener;

	public void setScrollViewListener(ScrollViewListener scrollViewListener) {
		this.scrollViewListener = scrollViewListener;
	}

	public ObservableScrollView(Context context) {
		super(context);
	}

	public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public ObservableScrollView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onScrollChanged(int x, int y, int oldX, int oldY) {
		super.onScrollChanged(x, y, oldX, oldY);
		if (scrollViewListener != null) {
			scrollViewListener.onScrollChanged(this, x, y, oldX, oldY);
		}
	}
}
