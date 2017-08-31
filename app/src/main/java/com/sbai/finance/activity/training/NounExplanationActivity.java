package com.sbai.finance.activity.training;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.training.ExplanationFragment;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.question.RemoveData;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.RenderScriptGaussianBlur;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.TrainingRuleDialog;
import com.sbai.finance.view.training.NoScrollViewPager;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NounExplanationActivity extends BaseActivity implements View.OnTouchListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.star1)
    TextView mStar1;
    @BindView(R.id.star2)
    TextView mStar2;
    @BindView(R.id.star3)
    TextView mStar3;
    @BindView(R.id.star4)
    TextView mStar4;
    @BindView(R.id.star5)
    TextView mStar5;
    @BindView(R.id.viewPager)
    NoScrollViewPager mViewPager;
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
    @BindView(R.id.explanation)
    TextView mExplanation;
    @BindView(R.id.bgImg)
    ImageView mBgImg;
    @BindView(R.id.content)
    RelativeLayout mContent;

    private TrainingDetail mTrainingDetail;
    private Question mQuestion;
    private List<RemoveData> mNounExplanationList;
    private List<RemoveData> mNewNounExplanationList;
    private List<Fragment> mFragments;
    private ExplanationFragmentAdapter mExplanationFragmentAdapter;
    private RenderScriptGaussianBlur mRenderScriptGaussianBlur;
    private float mDownX;
    private float mDownY;
    private float mDx;
    private float mDy;
    private Rect mCardRect;
    private Rect mStarImageRect;
    private int mCompleteCount = 0;
    //游戏进行的时间
    private long mTrainingCountTime;
    private int mTrainTargetTime;
    private boolean mIsSuccess;
    private int mQuestionCount;
    private boolean mIsLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_noun_explanation);
        ButterKnife.bind(this);
        initData(getIntent());
        translucentStatusBar();
        mStarImageRect = new Rect();
        mCardRect = new Rect();
        initView();
        initStar();
        mFragments = getFragments();
        initViewPager();
    }

    private void initView() {
        if (mTrainingDetail == null) return;
        mRenderScriptGaussianBlur = new RenderScriptGaussianBlur(this);
        mProgressBar.setTotalSecondTime(mTrainingDetail.getTrain().getTime());
        mTrainTargetTime = mTrainingDetail.getTrain().getTime() * 1000;
        mProgressBar.setOnTimeUpListener(new TrainProgressBar.OnTimeUpListener() {
            @Override
            public void onTick(long millisUntilUp) {
                mTrainingCountTime = millisUntilUp;
                formatTime(mTrainingCountTime);
            }

            @Override
            public void onFinish() {
                formatTime(mTrainTargetTime);
                mIsSuccess = false;
                if (!mIsLauncher) {
                    requestEndTrain();
                }
            }


            private void formatTime(long time) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    String format = DateUtil.format(time, "mm:ss.SS");
                    mTitleBar.setTitle(format);
                } else {
                    String format = DateUtil.format(time, "mm:ss.SSS");
                    String substring = format.substring(0, format.length() - 1);
                    mTitleBar.setTitle(substring);
                }
            }

        });
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHowPlayDialog();
            }
        });
    }

    private void requestEndTrain() {
        mIsLauncher = true;
        TrainingSubmit trainingSubmit = new TrainingSubmit(mTrainingDetail.getTrain().getId());
        trainingSubmit.addQuestionId(mQuestion.getId());
        trainingSubmit.setTime((int) (mTrainingCountTime / 1000));
        trainingSubmit.setFinish(mIsSuccess);
        Launcher.with(getActivity(), TrainingResultActivity.class)
                .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                .putExtra(ExtraKeys.TRAINING_SUBMIT, trainingSubmit)
                .execute();
        finish();
    }

    private void initData(Intent intent) {
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
        mNounExplanationList = mQuestion.getContent();
        Collections.shuffle(mNounExplanationList);
        mNewNounExplanationList = new ArrayList<>();
        if (mNounExplanationList.size() > 5) {
            for (int i = 0; i < 5; i++) {
                mNewNounExplanationList.add(mNounExplanationList.get(i));
            }
        } else {
            mNewNounExplanationList = mNounExplanationList;
        }
        mQuestionCount = mNewNounExplanationList.size();
    }

    private List<Fragment> getFragments() {
        List<Fragment> mFragments = new ArrayList<>();
        Collections.shuffle(mNewNounExplanationList);
        for (int i = 0; i < mNewNounExplanationList.size(); i++) {
            mFragments.add(ExplanationFragment.newInstance(mNewNounExplanationList.get(i).getKey().getContent(),
                    mNewNounExplanationList.get(i).getKey().getSeq()));
        }
        return mFragments;
    }

    private void initViewPager() {
        mPrevious.setVisibility(View.INVISIBLE);
        mViewPager.setOffscreenPageLimit(2);
        mViewPager.setPageTransformer(true, new CardPageTransformer((int) Display.dp2Px(10, getResources())));
        mExplanationFragmentAdapter = new ExplanationFragmentAdapter(getSupportFragmentManager(), mFragments);
        mViewPager.setAdapter(mExplanationFragmentAdapter);
        mNumber.setText(getString(R.string.explanation_number, 0, mQuestionCount));
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                mViewPager.getHitRect(mCardRect);
            }
        });
        mViewPager.addOnPageChangeListener(this);
    }

    private void initStar() {
        Collections.shuffle(mNewNounExplanationList);
        switch (mNewNounExplanationList.size()) {
            case 1:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.GONE);
                mStar3.setVisibility(View.GONE);
                mStar4.setVisibility(View.GONE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq(), 0, 0);
                break;
            case 2:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.GONE);
                mStar4.setVisibility(View.GONE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq(), 0, 0);
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq(), 300, 1);
                break;
            case 3:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                mStar4.setVisibility(View.GONE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq(), 0, 0);
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq(), 300, 1);
                setStarTextAndEvent(mStar3, mNewNounExplanationList.get(2).getValue().getContent(),
                        mNewNounExplanationList.get(2).getValue().getSeq(), 600, 2);
                break;
            case 4:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                mStar4.setVisibility(View.VISIBLE);
                mStar5.setVisibility(View.GONE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq(), 0, 0);
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq(), 300, 1);
                setStarTextAndEvent(mStar3, mNewNounExplanationList.get(2).getValue().getContent(),
                        mNewNounExplanationList.get(2).getValue().getSeq(), 600, 2);
                setStarTextAndEvent(mStar4, mNewNounExplanationList.get(3).getValue().getContent(),
                        mNewNounExplanationList.get(3).getValue().getSeq(), 900, 3);
                break;
            case 5:
                mStar1.setVisibility(View.VISIBLE);
                mStar2.setVisibility(View.VISIBLE);
                mStar3.setVisibility(View.VISIBLE);
                mStar4.setVisibility(View.VISIBLE);
                mStar5.setVisibility(View.VISIBLE);
                setStarTextAndEvent(mStar1, mNewNounExplanationList.get(0).getValue().getContent(),
                        mNewNounExplanationList.get(0).getValue().getSeq(), 0, 0);
                setStarTextAndEvent(mStar2, mNewNounExplanationList.get(1).getValue().getContent(),
                        mNewNounExplanationList.get(1).getValue().getSeq(), 300, 1);
                setStarTextAndEvent(mStar3, mNewNounExplanationList.get(2).getValue().getContent(),
                        mNewNounExplanationList.get(2).getValue().getSeq(), 600, 2);
                setStarTextAndEvent(mStar4, mNewNounExplanationList.get(3).getValue().getContent(),
                        mNewNounExplanationList.get(3).getValue().getSeq(), 900, 3);
                setStarTextAndEvent(mStar5, mNewNounExplanationList.get(4).getValue().getContent(),
                        mNewNounExplanationList.get(4).getValue().getSeq(), 1200, 4);
                break;
        }
    }

    public void setStarTextAndEvent(TextView view, String text, int tag, long delayTime, int number) {
        if (text.length() > 5) {
            text = text.substring(0, 5) + "\n" + text.substring(5, text.length());
        }
        view.setText(text);
        view.setOnTouchListener(this);
        view.setTag(tag);
        startAnimation(view, delayTime);
        view.setTag(R.id.tag_number, number);
    }

    private void startAnimation(View view, long delayTime) {
        view.setScaleX(0);
        view.setScaleY(0);
        AnimatorSet animatorSet = new AnimatorSet();
        animatorSet.playTogether(
                ObjectAnimator.ofFloat(view, "scaleX", 0f, 1.2f, 1f, 1.2f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 0f, 1.2f, 1f, 1.2f, 1f)
        );
        animatorSet.setDuration(400).setInterpolator(new AccelerateDecelerateInterpolator());
        animatorSet.setStartDelay(delayTime);
        animatorSet.start();
    }

    @OnClick({R.id.previous, R.id.next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.previous:
                mCardView.setVisibility(View.VISIBLE);
                mStarImage.setVisibility(View.INVISIBLE);
                mExplanation.setText(mNewNounExplanationList.get(mViewPager.getCurrentItem()).getKey().getContent());
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 2000);
                translateAnimation.setDuration(500);
                translateAnimation.setFillAfter(true);
                mCardView.startAnimation(translateAnimation);

                if (mViewPager.getCurrentItem() > 0) {
                    int position = mViewPager.getCurrentItem() - 1;
                    mViewPager.setCurrentItem(position);
                } else {
                    int position = mFragments.size() - 1;
                    mViewPager.setCurrentItem(position);
                }
                break;

            case R.id.next:
                mCardView.setVisibility(View.VISIBLE);
                mStarImage.setVisibility(View.INVISIBLE);
                mExplanation.setText(mNewNounExplanationList.get(mViewPager.getCurrentItem()).getKey().getContent());
                TranslateAnimation translateAnimation2 = new TranslateAnimation(0, 0, 0, 2000);
                translateAnimation2.setDuration(500);
                translateAnimation2.setFillAfter(true);
                mCardView.startAnimation(translateAnimation2);

                if (mViewPager.getCurrentItem() < mFragments.size() - 1) {
                    int position = mViewPager.getCurrentItem() + 1;
                    mViewPager.setCurrentItem(position);
                } else {
                    int position = 0;
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
        view.getHitRect(mStarImageRect);
        return mCardRect.contains(mStarImageRect);
    }

    private void matchAnswer(View view) {
        ExplanationFragment fragment = (ExplanationFragment) mExplanationFragmentAdapter.getItem(mViewPager.getCurrentItem());
        if ((int) view.getTag() == fragment.getStarTag()) {
            if (mFragments.size() > 1) {
                startCardFlyAnimation(view);
                mNewNounExplanationList.remove(mViewPager.getCurrentItem());
                mExplanationFragmentAdapter.destroyItem(mViewPager, mViewPager.getCurrentItem()
                        , mFragments.get(mViewPager.getCurrentItem()));
                mFragments.remove(mViewPager.getCurrentItem());
                initViewPager();
                mCompleteCount++;
                mNumber.setText(getString(R.string.explanation_number, mCompleteCount, mQuestionCount));
            } else {
                //只剩一套题目了
                initCardStar((int) view.getTag(R.id.tag_number));
                view.setVisibility(View.INVISIBLE);
                mViewPager.setVisibility(View.INVISIBLE);
                mCardView.setVisibility(View.VISIBLE);
                mStarImage.setVisibility(View.VISIBLE);
                mExplanation.setText("");
                TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0);
                translateAnimation.setDuration(1000);
                translateAnimation.setFillAfter(false);
                mCardView.startAnimation(translateAnimation);

                mCompleteCount++;
                mNumber.setText(getString(R.string.explanation_number, mCompleteCount, mQuestionCount));
                mIsSuccess = true;
                if (!mIsLauncher) {
                    requestEndTrain();
                }
            }
        } else {
            startBackAnimation(view);
        }
    }

    private void startCardFlyAnimation(View view) {
        initCardStar((int) view.getTag(R.id.tag_number));
        view.setVisibility(View.INVISIBLE);
        mCardView.setVisibility(View.VISIBLE);
        mStarImage.setVisibility(View.VISIBLE);
        mExplanation.setText("");
        TranslateAnimation translateAnimation = new TranslateAnimation(0, -2000, 0, -2000);
        translateAnimation.setDuration(800);
        translateAnimation.setFillAfter(true);
        mCardView.startAnimation(translateAnimation);
    }

    private void initCardStar(int number) {
        switch (number) {
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
        animatorSet.setDuration(500).setInterpolator(new OvershootInterpolator());
        animatorSet.start();
    }

    @Override
    public void onBackPressed() {
        showCloseDialog();
    }

    private void showCloseDialog() {
        mBgImg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBgImg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);

        SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
                .setTitle(getString(R.string.is_sure_exit_train))
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setOnDismissListener(new SmartDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Dialog dialog) {
                        mBgImg.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                        dialog.dismiss();
                    }
                })
                .setPositive(R.string.continue_train).show();
    }

    private void showHowPlayDialog() {
        mBgImg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBgImg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);
        TrainingRuleDialog.with(getActivity(), mTrainingDetail.getTrain())
                .setOnDismissListener(new TrainingRuleDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mBgImg.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                    }
                })
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressBar.cancelCountDownTimer();
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (position == 0) {
            mPrevious.setVisibility(View.INVISIBLE);
        } else {
            mPrevious.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    public class ExplanationFragmentAdapter extends FragmentStatePagerAdapter {
        private List<Fragment> mFragments;

        public ExplanationFragmentAdapter(FragmentManager fm, List<Fragment> fragments) {
            super(fm);
            this.mFragments = fragments;
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getItemPosition(Object object) {
            return PagerAdapter.POSITION_NONE;
        }

        @Override
        public int getCount() {
            return mFragments != null ? mFragments.size() : 0;
        }
    }

}
