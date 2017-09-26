package com.sbai.finance.activity.mine;

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
import com.sbai.finance.fragment.mine.MissNewsFragment;
import com.sbai.finance.fragment.mine.SysNewsFragment;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的-消息页面
 */

public class NewsActivity extends BaseActivity {
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.tabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private NewsPagesAdapter mNewsPagesAdapter;
    private int pagePosition;

    public interface NoReadNewsCallback {
        void noReadNews(int count);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_list);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mNewsPagesAdapter = new NewsPagesAdapter(getSupportFragmentManager(), getActivity(),
                new NoReadNewsCallback() {
                    @Override
                    public void noReadNews(int count) {
                        if (mTabLayout.getTabItems().length < 1) return;
                        if (count == 0) {
                            mTabLayout.getTabItems()[0].setText(getString(R.string.reply));
                            mTitleBar.setRightViewEnable(false);
                            mTitleBar.setRightVisible(false);
                        } else {
                            mTabLayout.getTabItems()[0].setText(getString(R.string.reply_, count));
                            mTitleBar.setRightViewEnable(true);
                            mTitleBar.setRightVisible(true);
                        }
                    }
                },
                new NoReadNewsCallback() {
                    @Override
                    public void noReadNews(int count) {
                        if (mTabLayout.getTabItems().length < 2) return;
                        if (count == 0) {
                            mTabLayout.getTabItems()[1].setText(getString(R.string.system));
                            mTitleBar.setRightViewEnable(false);
                            mTitleBar.setRightVisible(false);
                        } else {
                            mTabLayout.getTabItems()[1].setText(getString(R.string.system_, count));
                            mTitleBar.setRightViewEnable(true);
                            mTitleBar.setRightVisible(true);
                        }
                    }
                });
        mViewPager.setAdapter(mNewsPagesAdapter);
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
                pagePosition = position;
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mTitleBar.setOnTitleBarClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mNewsPagesAdapter.getFragment(pagePosition);
                if (fragment instanceof MissNewsFragment) {
                    ((MissNewsFragment) fragment).scrollToTop();
                }
                if (fragment instanceof SysNewsFragment) {
                    ((SysNewsFragment) fragment).scrollToTop();
                }
            }
        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragment = mNewsPagesAdapter.getFragment(pagePosition);
                if (fragment instanceof MissNewsFragment) {
                    ((MissNewsFragment) fragment).requestBatchReadMessage();
                }
                if (fragment instanceof SysNewsFragment) {
                    ((SysNewsFragment) fragment).requestBatchReadMessage();
                }
            }
        });
    }

    static class NewsPagesAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;
        private NoReadNewsCallback mMissNewsCallback;
        private NoReadNewsCallback mSysNewsCallback;

        public NewsPagesAdapter(FragmentManager fm, Context context,
                                NoReadNewsCallback missNewsCallback,
                                NoReadNewsCallback sysNewsCallback) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
            mMissNewsCallback = missNewsCallback;
            mSysNewsCallback = sysNewsCallback;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.reply);
                case 1:
                    return mContext.getString(R.string.system);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    MissNewsFragment missNewsFragment = new MissNewsFragment();
                    missNewsFragment.setNoReadNewsCallback(mMissNewsCallback);
                    return missNewsFragment;
                case 1:
                    SysNewsFragment sysNewsFragment = new SysNewsFragment();
                    sysNewsFragment.setNoReadNewsCallback(mMissNewsCallback);
                    return sysNewsFragment;
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
