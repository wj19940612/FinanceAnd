package com.sbai.finance.activity.mine;

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
import com.sbai.finance.fragment.mine.QuestionOrCommentFragment;
import com.sbai.finance.fragment.mine.WaitAnswerFragment;
import com.sbai.finance.fragment.mine.WaitRaceAnswerFragment;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\11\21 0021.
 */

public class WaitForMeAnswerActivity extends BaseActivity {

    public static final int WAIT_ME_ANSWER = 1;
    public static final int WAIT_RACE_ANSWER = 2;
    public static final int HAVE_ANSWER = 3;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private WaitForMeAnswerFragmentAdapter mWaitForMeAnswerFragmentAdapter;
    private int mWaitAnswerCount;
    private int mRaceAnswerCount;
    private int mHaveAnswerCount;
    private int pagePosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wait_for_me_answer);
        ButterKnife.bind(this);

        mWaitForMeAnswerFragmentAdapter = new WaitForMeAnswerFragmentAdapter(getSupportFragmentManager(), this);
        mViewPager.setOffscreenPageLimit(2);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setSelectedIndicatorPadding(Display.dp2Px(45, getResources()));
        mSlidingTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mViewPager.setAdapter(mWaitForMeAnswerFragmentAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                pagePosition = position;
                updateTitleBar();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void updateTitleBar() {
        if (mWaitAnswerCount == 0) {
            mSlidingTabLayout.getTabItems()[0].setText(getString(R.string.wait_answer));
        } else {
            mSlidingTabLayout.getTabItems()[0].setText(getString(R.string.wait_answer_, mWaitAnswerCount));
        }

        if (mRaceAnswerCount == 0) {
            mSlidingTabLayout.getTabItems()[1].setText(getString(R.string.wait_race_answer));
        } else {
            mSlidingTabLayout.getTabItems()[1].setText(getString(R.string.wait_race_answer_, mRaceAnswerCount));
        }

        if (mHaveAnswerCount == 0) {
            mSlidingTabLayout.getTabItems()[2].setText(getString(R.string.have_answered));
        } else {
            mSlidingTabLayout.getTabItems()[2].setText(getString(R.string.have_answered_, mHaveAnswerCount));
        }
    }

    static class WaitForMeAnswerFragmentAdapter extends FragmentPagerAdapter {
        private FragmentManager mFragmentManager;
        private Context mContext;

        public WaitForMeAnswerFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    WaitAnswerFragment waitAnswerFragment = WaitAnswerFragment.newInstance(WAIT_ME_ANSWER);
                    return waitAnswerFragment;
                case 1:
                    WaitRaceAnswerFragment waitRaceAnswerFragment = WaitRaceAnswerFragment.newInstance();
                    return waitRaceAnswerFragment;
                case 2:
                    WaitAnswerFragment haveAnswerFragment = WaitAnswerFragment.newInstance(HAVE_ANSWER);
                    return haveAnswerFragment;
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.wait_answer);
                case 1:
                    return mContext.getString(R.string.wait_race_answer);
                case 2:
                    return mContext.getString(R.string.have_answered);
            }
            return super.getPageTitle(position);
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
