package com.sbai.finance.activity.stock;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

import com.sbai.finance.R;
import com.sbai.finance.fragment.stock.PriceLimitRankingFragment;
import com.sbai.finance.fragment.trade.ViewpointFragment;

public class StockIndexActivity extends StockTradeActivity {

    private SubPageAdapter mSubPageAdapter;

    @Override
    protected void initChartViews() {
        super.initChartViews();
        mStockTrendView.setHasFiveMarketView(false);
    }

    @Override
    protected ViewPager.OnPageChangeListener createPageChangeListener() {
        return null;
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
