package com.sbai.finance.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.PluralsRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.future.FutureListsFragment;
import com.sbai.finance.fragment.optional.OptionalListFragment;
import com.sbai.finance.fragment.stock.StockListFragment;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.slidingTab.SlidingTabTitle;
import com.sbai.finance.websocket.market.MarketSubscriber;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 股票 期货 自选
 */

public class StockFutureActivity extends BaseActivity {
    @BindView(R.id.tabLayout)
    SlidingTabTitle mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private int mPage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_future);
        ButterKnife.bind(this);
        initData(getIntent());
        initViewPager();
        initTabView();
    }

    private void initData(Intent intent) {
        mPage = intent.getIntExtra(ExtraKeys.PAGE_INDEX, -1);
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mPagerAdapter = new PagerAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(mPagerAdapter);
    }


    private void initTabView() {
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setTabLeftAndRightMargin((int) Display.dp2Px(80, getResources()));
        mTabLayout.setPadding(Display.dp2Px(15, getResources()));
        mTabLayout.setSelectedIndicatorHeight(3);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setTabViewTextSize(16);
        mTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
        if (mPage > 0) {
            mTabLayout.setTabIndex(mPage);
        }
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
    static class PagerAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public PagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.ShangShen);
                case 1:
                    return mContext.getString(R.string.futures);
                case 2:
                    return mContext.getString(R.string.optional);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new StockListFragment();
                case 1:
                    return new FutureListsFragment();
                case 2:
                    return new OptionalListFragment();

            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
