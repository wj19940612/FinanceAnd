package com.sbai.finance.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.anchor.CommentActivity;
import com.sbai.finance.activity.anchor.SubmitQuestionActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.anchor.QuestionAndAnswerFragment;
import com.sbai.finance.fragment.anchor.RecommendFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnPageSelectedListener;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.activity.BaseActivity.REQ_CODE_LOGIN;

/**
 * 米圈页面
 */
public class AnchorCircleFragment extends BaseFragment {

    private static final int PAGE_POSITION_RECOMMEND = 0;
    private static final int PAGE_POSITION_QUESTION_AND_ANSWER = 1;

    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mTabLayout;
    @BindView(R.id.askAQuestion)
    TextView mAskAQuestion;
    private Unbinder mBind;
    private AnchorCircleFragmentAdapter mAnchorCircleFragmentAdapter;

    private MediaPlayService mMediaPlayService;

    public AnchorCircleFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_anchor_circle, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mAnchorCircleFragmentAdapter = new AnchorCircleFragmentAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setCurrentItem(0, false);
        mViewPager.setAdapter(mAnchorCircleFragmentAdapter);

        mTabLayout.setDistributeEvenly(true);
        mTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mTabLayout.setPadding(Display.dp2Px(15, getResources()));
        mTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(35, getResources()));
        mTabLayout.setHorizontalMargin((int) Display.dp2Px(80,getResources()));
        mTabLayout.setViewPager(mViewPager);
        mTabLayout.setTabViewTextSize(16);
        mTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
        mViewPager.addOnPageChangeListener(mOnPageSelectedListener);

        mTabLayout.highlightItem(0);

        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                QuestionAndAnswerFragment questionAndAnswerFragment = (QuestionAndAnswerFragment) mAnchorCircleFragmentAdapter.getFragment(PAGE_POSITION_QUESTION_AND_ANSWER);
                if (questionAndAnswerFragment != null && mMediaPlayService != null) {
                    questionAndAnswerFragment.setService(mMediaPlayService);
                }
            }
        });
    }

    private OnPageSelectedListener mOnPageSelectedListener = new OnPageSelectedListener() {

        @Override
        public void onPageSelected(int position) {
            if (position == PAGE_POSITION_QUESTION_AND_ANSWER) {
                mAskAQuestion.setVisibility(View.VISIBLE);
            } else {
                mAskAQuestion.setVisibility(View.GONE);
            }
        }
    };

    public void setService(MediaPlayService mediaPlayService) {
        mMediaPlayService = mediaPlayService;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewPager.removeOnPageChangeListener(mOnPageSelectedListener);
        mOnPageSelectedListener = null;
        mBind.unbind();
    }

    @OnClick(R.id.askAQuestion)
    public void onViewClicked() {
        if (LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, REQ_CODE_LOGIN);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int iad = data.getIntExtra(ExtraKeys.QUESTION_ID, -1);

        Log.d(TAG, "onActivityResult: "+iad);
        if (resultCode == BaseActivity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_LOGIN:
                    Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
                    break;
                case CommentActivity.REQ_CODE_COMMENT:
                    if (data != null) {
                        int id = data.getIntExtra(ExtraKeys.QUESTION_ID, -1);
                        if (id != -1) {
                            RecommendFragment fragment = (RecommendFragment) mAnchorCircleFragmentAdapter.getFragment(PAGE_POSITION_RECOMMEND);
                            if(fragment!=null){
                                fragment.updatePointComment(id);
                            }
                        }
                    }
                    break;
            }
        }
    }

    private static class AnchorCircleFragmentAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private Context mContext;

        public AnchorCircleFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new RecommendFragment();
                case 1:
                    return new QuestionAndAnswerFragment();
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.recommend);
                case 1:
                    return mContext.getString(R.string.question_and_answer);
            }
            return super.getPageTitle(position);
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
