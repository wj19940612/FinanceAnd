package com.sbai.finance.activity.future;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.future.FutureListFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.finance.websocket.market.MarketSubscriber;

import butterknife.BindView;
import butterknife.ButterKnife;

public class FutureListActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    private FuturePagesAdapter mFuturePagesAdapter;
    private int pagePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_list);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        MarketSubscriber.get().subscribeAll();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MarketSubscriber.get().unSubscribeAll();
    }

    private void initView() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mFuturePagesAdapter = new FuturePagesAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mFuturePagesAdapter);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mTabLayout.setViewPager(mViewPager);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagePosition=position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mFuturePagesAdapter.getFragment(pagePosition);
                if (fragment instanceof FutureListFragment) {
                    ((FutureListFragment) fragment).scrollToTop();
                }
            }
        });
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
                    return FutureListFragment.newInstance(Variety.FUTURE_FOREIGN);
                case 1:
                    return FutureListFragment.newInstance(Variety.FUTURE_CHINA);
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
