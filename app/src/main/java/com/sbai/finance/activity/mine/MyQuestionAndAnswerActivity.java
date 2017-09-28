package com.sbai.finance.activity.mine;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.fragment.mine.QuestionOrCommentFragment;
import com.sbai.finance.model.miss.Prise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 关于解说  我的问题 和评论
 */
public class MyQuestionAndAnswerActivity extends BaseActivity implements QuestionOrCommentFragment.OnQuestionClickListener {

    private static final int REQ_CODE_QUESTION_DETAIL = 444;

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private MyQuestionAndAnswerFragmentAdapter mMyQuestionAndAnswerFragmentAdapter;

    private int mClickPosition;
    private Question mClickQuestion;

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
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    @Override
    public void onQuestionClick(Question question, int clickPosition) {
        mClickPosition = clickPosition;
        mClickQuestion = question;
        Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
        intent.putExtra(Launcher.EX_PAYLOAD, question.getDataId());
        startActivityForResult(intent, REQ_CODE_QUESTION_DETAIL);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_QUESTION_DETAIL:
                Prise prise = data.getParcelableExtra(Launcher.EX_PAYLOAD);
                int replyCount = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
                int rewardCount = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
                if (prise != null) {
                    mClickQuestion.setPriseCount(prise.getPriseCount());
                }
                mClickQuestion.setReplyCount(replyCount);
                mClickQuestion.setAwardCount(rewardCount);

                QuestionOrCommentFragment questionFragment = (QuestionOrCommentFragment) mMyQuestionAndAnswerFragmentAdapter.getFragment(0);
                QuestionOrCommentFragment commentFragment = (QuestionOrCommentFragment) mMyQuestionAndAnswerFragmentAdapter.getFragment(1);
                if (questionFragment != null) {
                    questionFragment.updateClickItem(mClickPosition, mClickQuestion);
                }
                if (commentFragment != null) {
                    commentFragment.updateClickItem(mClickPosition, mClickQuestion);
                }
                break;
        }
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
