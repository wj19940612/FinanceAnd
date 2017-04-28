package com.sbai.finance.activity.future;

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
import com.sbai.finance.fragment.future.FutureFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onPostResume() {
        super.onPostResume();
        Netty.get().subscribe(Netty.REQ_SUB_ALL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Netty.get().subscribe(Netty.REQ_UNSUB_ALL);
    }

    private void initView() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mFuturePagesAdapter = new FuturePagesAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mFuturePagesAdapter);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(FutureActivity.this, android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding(Display.dp2Px(70, getResources()));
        mTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.blueAssist));
        mTabLayout.setViewPager(mViewPager);
    }

    static class FuturePagesAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

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
                    return  FutureFragment.newInstance(Variety.FUTURE_FOREIGN);
                case 1:
                    return  FutureFragment.newInstance(Variety.FUTURE_CHINA);
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
