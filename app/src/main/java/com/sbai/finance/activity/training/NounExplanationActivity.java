package com.sbai.finance.activity.training;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.training.ExplanationFragment;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.utils.Display;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.training.DragImageView;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NounExplanationActivity extends BaseActivity implements View.OnTouchListener {

    @BindView(R.id.star1)
    DragImageView mStar1;
    @BindView(R.id.star2)
    DragImageView mStar2;
    @BindView(R.id.star3)
    DragImageView mStar3;
    @BindView(R.id.star4)
    DragImageView mStar4;
    @BindView(R.id.star5)
    DragImageView mStar5;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.previous)
    ImageView mPrevious;
    @BindView(R.id.next)
    ImageView mNext;
    @BindView(R.id.number)
    TextView mNumber;
    @BindView(R.id.starImage)
    ImageView mStarImage;
    @BindView(R.id.cardView)
    CardView mCardView;


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progressBar)
    TrainProgressBar mProgressBar;

    private TrainingQuestion mTrainingQuestion;
    private long mCountDownTime;
    private Training mTraining;
    private List<TrainingQuestion.ContentBean> mNounExplanationList;
    private List<TrainingQuestion.ContentBean> mNewNounExplanationList;
    private List<Fragment> mFragments;
    private ExplanationFragmentAdapter mExplanationFragmentAdapter;
    private float mDownX;
    private float mDownY;
    private float mDx;
    private float mDy;
    private Rect mRect;
    private Rect mImageRect;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noun_explanation);
        ButterKnife.bind(this);
        translucentStatusBar();
        mImageRect = new Rect();
        mRect = new Rect();
        initData(getIntent());
        mFragments = getFragments();
        initViewPager();
        initStar();
    }

    private void initData(Intent intent) {
        mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.TRAIN_QUESTIONS);
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
        mCountDownTime = mTraining.getTime();
        mNounExplanationList = mTrainingQuestion.getContent();
        mNewNounExplanationList = new ArrayList<>();
        if (mNounExplanationList.size() > 5) {
            for (int i = 0; i < 5; i++) {
                mNewNounExplanationList.add(mNounExplanationList.get(i));
            }
        } else {
            mNewNounExplanationList = mNounExplanationList;
        }
    }

    private List<Fragment> getFragments() {
        List<Fragment> mFragments = new ArrayList<>();
        for (int i = 0; i < mNewNounExplanationList.size(); i++) {
            mFragments.add(ExplanationFragment.newInstance(mNewNounExplanationList.get(i).getKey().getContent(),
                    mNewNounExplanationList.get(i).getKey().getSeq(), i));
        }
        return mFragments;
    }

    private void initViewPager() {
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageTransformer(true, new CardPageTransformer((int) Display.dp2Px(10, getResources())));
        mExplanationFragmentAdapter = new ExplanationFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mExplanationFragmentAdapter);
        mNumber.setText(getString(R.string.explanation_number, (0), mFragments.size()));
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.getHitRect(mRect);
            }
        });
    }

    private void initStar() {
        switch (mNewNounExplanationList.size()) {
            case 1:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.GONE);
                mStar3.setVisibility(View.GONE);
                mStar4.setVisibility(View.GONE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq());
                break;
            case 2:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.GONE);
                mStar4.setVisibility(View.GONE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq());
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq());
                break;
            case 3:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                mStar4.setVisibility(View.GONE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq());
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq());
                setStarTextAndEvent(mStar3, mNewNounExplanationList.get(2).getValue().getContent(),
                        mNewNounExplanationList.get(2).getValue().getSeq());
                break;
            case 4:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                mStar4.setVisibility(View.VISIBLE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq());
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq());
                setStarTextAndEvent(mStar3, mNewNounExplanationList.get(2).getValue().getContent(),
                        mNewNounExplanationList.get(2).getValue().getSeq());
                setStarTextAndEvent(mStar4, mNewNounExplanationList.get(3).getValue().getContent(),
                        mNewNounExplanationList.get(3).getValue().getSeq());
                break;
            case 5:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                mStar4.setVisibility(View.VISIBLE);
                mStar5.setVisibility(View.VISIBLE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq());
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq());
                setStarTextAndEvent(mStar3, mNewNounExplanationList.get(2).getValue().getContent(),
                        mNewNounExplanationList.get(2).getValue().getSeq());
                setStarTextAndEvent(mStar4, mNewNounExplanationList.get(3).getValue().getContent(),
                        mNewNounExplanationList.get(3).getValue().getSeq());
                setStarTextAndEvent(mStar5, mNewNounExplanationList.get(4).getValue().getContent(),
                        mNewNounExplanationList.get(4).getValue().getSeq());
                break;
        }
    }

    public void setStarTextAndEvent(DragImageView view, String text, int tag) {
        view.setText(text);
        view.setOnTouchListener(this);
        view.setTag(tag);
    }

    @OnClick({R.id.previous, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.previous:
                if (mViewPager.getCurrentItem() > 0) {
                    int position = mViewPager.getCurrentItem() - 1;
                    mViewPager.setCurrentItem(position);
                }
                break;
            case R.id.next:
                if (mViewPager.getCurrentItem() < mFragments.size() - 1) {
                    int position = mViewPager.getCurrentItem() + 1;
                    mViewPager.setCurrentItem(position);
                }
                break;
        }
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mDownX = event.getRawX();
                mDownY = event.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                mDx = event.getRawX() - mDownX;
                mDy = event.getRawY() - mDownY;
                view.setTranslationX(mDx);
                view.setTranslationY(mDy);
                break;
            case MotionEvent.ACTION_UP:
                if (starIsInside(view)) {
                    matchAnswer(view);
                } else {
                    startBackAnimation(view);
                }
                break;
        }
        return true;
    }

    private boolean starIsInside(View view) {
        view.getHitRect(mImageRect);
        return mRect.contains(mImageRect);
    }

    private void matchAnswer(View view) {
        ExplanationFragment fragment = (ExplanationFragment) mExplanationFragmentAdapter.getFragment(mViewPager.getCurrentItem());
        if ((int) view.getTag() == fragment.getStarTag()) {
            if (mNewNounExplanationList.size() > 1) {
                startCardFlyAnimation(view);
                //4、移除数据notify
                mNewNounExplanationList.remove(mViewPager.getCurrentItem());
                mFragments = getFragments();
                initViewPager();
            } else {
                //只剩一套题目了
                mCardView.setVisibility(View.VISIBLE);
                view.setVisibility(View.INVISIBLE);
                mViewPager.setVisibility(View.INVISIBLE);
                initCardStar(mViewPager.getCurrentItem());

                // TODO: 2017/8/19 提交训练
            }
        } else {
            startBackAnimation(view);
        }
    }

    private void startCardFlyAnimation(View view) {
        mCardView.setVisibility(View.VISIBLE);
        view.setVisibility(View.INVISIBLE);
        initCardStar(mViewPager.getCurrentItem());
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playSequentially();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(mCardView, "translationX", 0, -1000),
                ObjectAnimator.ofFloat(mCardView, "translationY", 0, -1000));
        animatorSet.setDuration(2000).setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    private void initCardStar(int currentItem) {
        switch (currentItem) {
            case 0:
                mStarImage.setImageResource(R.drawable.ic_star_1s);
                break;
            case 1:
                mStarImage.setImageResource(R.drawable.ic_star_2s);
                break;
            case 2:
                mStarImage.setImageResource(R.drawable.ic_star_3s);
                break;
            case 3:
                mStarImage.setImageResource(R.drawable.ic_star_4s);
                break;
            case 4:
                mStarImage.setImageResource(R.drawable.ic_star_5s);
                break;
        }
    }

    private void startBackAnimation(View view) {
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "translationX", 0f),
                ObjectAnimator.ofFloat(view, "translationY", 0f));
        animatorSet.setDuration(400).setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    public class ExplanationFragmentAdapter extends FragmentPagerAdapter {
        private List<Fragment> mFragments;
        private FragmentManager mFragmentManager;

        public ExplanationFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
            this.mFragmentManager = fm;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments != null ? mFragments.size() : 0;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
