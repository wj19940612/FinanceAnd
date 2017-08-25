package com.sbai.finance.activity.stock;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.fragment.stock.FinanceFragment;
import com.sbai.finance.fragment.stock.StockNewsFragment;
import com.sbai.finance.view.TitleBar;

public class StockDetailActivity extends StockTradeActivity {

    private SubPageAdapter mSubPageAdapter;
    private int pagePosition;

    @Override
    protected ViewPager.OnPageChangeListener createPageChangeListener() {

        return new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                pagePosition = position;
                if (position == 1) {
                    StockNewsFragment stockNewsFragment = getStockNewsFragment();
                    if (stockNewsFragment != null) {
                        stockNewsFragment.requestStockNewsList(0);
                    }
                }

                if (position == 2) {
                    FinanceFragment financeFragment = getFinanceFragment();
                    if (financeFragment != null) {
                        financeFragment.requestCompanyAnnualReport(0);
                    }
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        };

    }

    @Override
    public void setUpTitleBar(TitleBar titleBar) {
        titleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mSubPageAdapter.getFragment(pagePosition);
                if (fragment instanceof StockNewsFragment) {
                    ((StockNewsFragment) fragment).scrollToTop();
                } else if (fragment instanceof FinanceFragment) {
                    ((FinanceFragment) fragment).scrollToTop();
                }
            }
        });
        super.setUpTitleBar(titleBar);
    }



    private StockNewsFragment getStockNewsFragment() {
        Fragment fragment = mSubPageAdapter.getFragment(1);
        if (fragment != null && fragment instanceof StockNewsFragment) {
            return (StockNewsFragment) fragment;
        }
        return null;
    }

    private FinanceFragment getFinanceFragment() {
        Fragment fragment = mSubPageAdapter.getFragment(2);
        if (fragment != null && fragment instanceof FinanceFragment) {
            return (FinanceFragment) fragment;
        }
        return null;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected PagerAdapter createSubPageAdapter() {
        mSubPageAdapter = new SubPageAdapter(getSupportFragmentManager(), getActivity());
        return mSubPageAdapter;
    }

    private class SubPageAdapter extends FragmentPagerAdapter {

        FragmentManager mFragmentManager;
        Context mContext;

        public SubPageAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
//                case 0:
//                    return mContext.getString(R.string.point);
                case 0:
                    return mContext.getString(R.string.stock_news);
                case 1:
                    return mContext.getString(R.string.stock_finance);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
//                case 0:
//                    return ViewpointFragment.newInstance(mVariety.getVarietyId());
                case 0:
                    return StockNewsFragment.newInstance(mVariety.getVarietyType());
                case 1:
                    return FinanceFragment.newInstance(mVariety.getVarietyType());
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
