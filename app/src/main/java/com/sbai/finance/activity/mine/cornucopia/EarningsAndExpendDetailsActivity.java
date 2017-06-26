package com.sbai.finance.activity.mine.cornucopia;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.mine.ExchangeDetailFragment;
import com.sbai.finance.model.mine.cornucopia.ExchangeDetailModel;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EarningsAndExpendDetailsActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private int mType;
    private int mSelectPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_earnings_and_expend_details);
        ButterKnife.bind(this);

        mType = getIntent().getIntExtra(Launcher.EX_PAY_END, 0);
        initView();
    }

    private void initView() {
        if (mType == ExchangeDetailModel.TYPE_COIN) {
            mTitleBar.setTitle(R.string.coin_detail);
        } else {
            mTitleBar.setTitle(R.string.integrate_detail);
        }

        final ExchangeDetailAdapter exchangeDetailAdapter = new ExchangeDetailAdapter(getSupportFragmentManager(), getActivity());
        mViewPager.setAdapter(exchangeDetailAdapter);
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
                mSelectPosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = exchangeDetailAdapter.getFragment(mSelectPosition);
                if (fragment != null) {
                    ((ExchangeDetailFragment) (fragment)).scrollToTop();
                }
            }
        });
    }

    class ExchangeDetailAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public ExchangeDetailAdapter(FragmentManager fm, Context context) {
            super(fm);
            this.mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ExchangeDetailFragment.newInstance(mType, ExchangeDetailModel.DIRECTION_EARNINGS);
                case 1:
                    return ExchangeDetailFragment.newInstance(mType, ExchangeDetailModel.DIRECTION_EXTEND);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.earnings);
                case 1:
                    return mContext.getString(R.string.extend);
            }
            return super.getPageTitle(position);
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
