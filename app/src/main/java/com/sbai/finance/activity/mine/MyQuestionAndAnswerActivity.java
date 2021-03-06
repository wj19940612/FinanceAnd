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
import com.sbai.finance.fragment.mine.QuestionOrCommentFragment;

import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于解说  我的问题 和评论
 */
public class MyQuestionAndAnswerActivity extends BaseActivity {


    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private MyQuestionAndAnswerFragmentAdapter mMyQuestionAndAnswerFragmentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_my_question_and_answer);
        ButterKnife.bind(this);

        mMyQuestionAndAnswerFragmentAdapter = new MyQuestionAndAnswerFragmentAdapter(getSupportFragmentManager(), this);
        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setSelectedIndicatorPadding(Display.dp2Px(60, getResources()));
        mSlidingTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mViewPager.setAdapter(mMyQuestionAndAnswerFragmentAdapter);
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    umengEventCount(UmengCountEventId.ME_MY_QUESTION_ASK);
                } else {
                    umengEventCount(UmengCountEventId.ME_MY_QUESTION_COMMENT);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        mSlidingTabLayout.setViewPager(mViewPager);
    }


    static class MyQuestionAndAnswerFragmentAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private Context mContext;

        public MyQuestionAndAnswerFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return QuestionOrCommentFragment.newInstance(QuestionOrCommentFragment.TYPE_QUESTION);
                case 1:
                    return QuestionOrCommentFragment.newInstance(QuestionOrCommentFragment.TYPE_COMMENT);
            }
            return null;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.question);
                case 1:
                    return mContext.getString(R.string.comment);
            }
            return super.getPageTitle(position);
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
