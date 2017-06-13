package com.sbai.finance.activity.stock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.fragment.stock.PriceLimitRankingFragment;
import com.sbai.finance.fragment.trade.ViewpointFragment;
import com.sbai.finance.view.TitleBar;

public class StockIndexActivity extends StockTradeActivity {

    private SubPageAdapter mSubPageAdapter;
    private int pagePosition;

    @Override
    protected void initChartViews() {
        super.initChartViews();
        mStockTrendView.setHasFiveMarketView(false);
    }

    @Override
    protected ViewPager.OnPageChangeListener createPageChangeListener() {
        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        };
    }

    @Override
    protected PagerAdapter createSubPageAdapter() {
        mSubPageAdapter = new SubPageAdapter(getSupportFragmentManager(), getActivity());
        return mSubPageAdapter;
    }

    @Override
    protected ViewpointFragment getViewpointFragment() {
        Fragment fragment = mSubPageAdapter.getFragment(0);
        if (fragment != null && fragment instanceof ViewpointFragment) {
            return (ViewpointFragment) fragment;
        }
        return null;
    }

    @Override
    public void setUpTitleBar(TitleBar titleBar) {
        titleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mSubPageAdapter.getFragment(pagePosition);
                if(fragment!=null){
                    if(fragment instanceof ViewpointFragment){
                        ((ViewpointFragment)fragment).scrollToTop();
                    }else if(fragment instanceof PriceLimitRankingFragment){
                        ((PriceLimitRankingFragment)fragment).scrollToTop();
                    }
                }
            }
        });
        super.setUpTitleBar(titleBar);
    }

    private PriceLimitRankingFragment getPriceLimitRankingFragment() {
        Fragment fragment = mSubPageAdapter.getFragment(1);
        if (fragment != null && fragment instanceof PriceLimitRankingFragment) {
            return (PriceLimitRankingFragment) fragment;
        }
        return null;
    }

    private class SubPageAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;
        Context mContext;
        int mExchangeCode;

        public SubPageAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
            try {
                mExchangeCode = Integer.valueOf(mVariety.getExchangeCode());
            } catch (NumberFormatException e) {

            }
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.point);
                case 1:
                    return mContext.getString(R.string.rise_list);
                case 2:
                    return mContext.getString(R.string.fail_list);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ViewpointFragment.newInstance(mVariety.getVarietyId());
                case 1:
                    return PriceLimitRankingFragment.newInstance(1, mExchangeCode);
                case 2:
                    return PriceLimitRankingFragment.newInstance(2, mExchangeCode);
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
