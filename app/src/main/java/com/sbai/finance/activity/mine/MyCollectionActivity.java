package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.mine.ArticleCollectionFragment;
import com.sbai.finance.fragment.mine.MyCollectQuestionFragment;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MyCollectionActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private MyCollectionFragmentPagerAdapter mMyCollectionFragmentPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_titlebar_sliding_tablayout_viewpager);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mTitleBar.setTitle(R.string.my_collection);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mSlidingTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mMyCollectionFragmentPagerAdapter = new MyCollectionFragmentPagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mMyCollectionFragmentPagerAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    static class MyCollectionFragmentPagerAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private Context mContext;

        public MyCollectionFragmentPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new MyCollectQuestionFragment();
                case 1:
                    return new ArticleCollectionFragment();
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.question);
                case 1:
                    return mContext.getString(R.string.article);
            }
            return super.getPageTitle(position);
        }

        @Override
        public int getCount() {
            return 2;
        }
    }

}
