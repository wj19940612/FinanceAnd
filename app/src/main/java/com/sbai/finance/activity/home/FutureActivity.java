package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.home.ChinaFutureFragment;
import com.sbai.finance.fragment.home.ForeignFutureFragment;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-17.
 */

public class FutureActivity extends BaseActivity {
	@BindView(R.id.viewPager)
	ViewPager mViewPager;
	@BindView(R.id.tabLayout)
	SlidingTabLayout mTabLayout;
	private FuturePagesAdapter mFuturePagesAdapter;
	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_future);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		mViewPager.setOffscreenPageLimit(2);
		mViewPager.setCurrentItem(0, false);
		mFuturePagesAdapter = new FuturePagesAdapter(getSupportFragmentManager(), FutureActivity.this);
		mViewPager.setAdapter(mFuturePagesAdapter);

		mTabLayout.setDistributeEvenly(true);
		mTabLayout.setDividerColors(ContextCompat.getColor(FutureActivity.this, android.R.color.transparent));
		mTabLayout.setSelectedIndicatorPadding(80);
		mTabLayout.setPadding(13);
		mTabLayout.setSelectedIndicatorColors( ContextCompat.getColor(this,R.color.blueAssist));
		mTabLayout.setViewPager(mViewPager);
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
	static class FuturePagesAdapter extends FragmentPagerAdapter {

		private Context mContext;
		FragmentManager mFragmentManager;

		public FuturePagesAdapter(FragmentManager fm, Context context) {
			super(fm);
			mContext = context;
			mFragmentManager = fm;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			switch (position) {
				case 0:
					return mContext.getString(R.string.future_foreign);
				case 1:
					return mContext.getString(R.string.future_china);
			}
			return super.getPageTitle(position);
		}

		@Override
		public Fragment getItem(int position) {
			switch (position) {
				case 0:
					return new ForeignFutureFragment();
				case 1:
					return new ChinaFutureFragment();
			}
			return null;
		}

		@Override
		public int getCount() {
			return 2;
		}

		public Fragment getFragment(int position) {
			return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
		}
	}
}
