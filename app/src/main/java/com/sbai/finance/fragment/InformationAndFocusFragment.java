package com.sbai.finance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sbai.finance.R;
import com.sbai.finance.fragment.focusnews.FocusNewsFragment;
import com.sbai.finance.fragment.focusnews.InformationFragment;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.slidingTab.SlidingTabTitle;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\11\15 0015.
 */

public class InformationAndFocusFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.tabLayout)
    SlidingTabTitle mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private PagerAdapter mPagerAdapter;
    private int mPage;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.activity_information_and_focus_news, container, false);
        unbinder = ButterKnife.bind(this, view);
        initViewPager();
        initTabView();
        return view;
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mPagerAdapter = new PagerAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mPagerAdapter);
    }

    private void initTabView() {
        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setTabLeftAndRightMargin((int) Display.dp2Px(80, getResources()));
        mTabLayout.setPadding(Display.dp2Px(15, getResources()));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(35, getResources()));
        mTabLayout.setSelectedIndicatorHeight(3);
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setTabViewTextSize(16);
        mTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
        if (mPage > 0) {
            mTabLayout.setTabIndex(mPage);
        }
    }

    public void setPage(int page) {
        mPage = page;
        mTabLayout.setTabIndex(mPage);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            InformationFragment fragment = (InformationFragment) mPagerAdapter.getFragment(0);
            if (fragment != null) {
                fragment.refreshData();
            }

            FocusNewsFragment focusNewsFragment = (FocusNewsFragment) mPagerAdapter.getFragment(1);
            if (focusNewsFragment != null) {

            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public static class PagerAdapter extends FragmentPagerAdapter {

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
                    return mContext.getString(R.string.all_day_news);
                case 1:
                    return mContext.getString(R.string.focus_news);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new InformationFragment();
                case 1:
                    return new FocusNewsFragment();

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
