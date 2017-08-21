package com.sbai.finance.activity.training;

import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * Created by lixiaokuan0819 on 2017/8/18.
 */

public class CardPageTransformer implements ViewPager.PageTransformer{
	private int mOffset;

	public CardPageTransformer(int offset) {
		this.mOffset = offset;
	}

	@Override
	public void transformPage(View page, float position) {
		if (position <= 0.0f) {//被滑动的那页
			page.setTranslationX(0f);
			page.setAlpha(1f);
		} else {//未被滑动的页
			page.setTranslationX((-page.getWidth() * position) + (mOffset * position));
			//缩放比例
			float scale = (page.getWidth() - mOffset * position) / (float) (page.getWidth());
			page.setScaleX(scale);
			page.setScaleY(scale);
			page.setAlpha(0.5f);
			page.setTranslationY(mOffset * position);
		}
	}
}
