package com.sbai.finance.activity.training;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Dialog;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.CardView;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
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
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.training.DragImageView;
import com.sbai.finance.view.training.NoScrollViewPager;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NounExplanationActivity extends BaseActivity implements View.OnTouchListener, ViewPager.OnPageChangeListener {

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

	private TrainingDetail mTrainingDetail;
	private Question mQuestion;
	private List<RemoveData> mNounExplanationList;
	private List<RemoveData> mNewNounExplanationList;
	private List<Fragment> mFragments;
	private ExplanationFragmentAdapter mExplanationFragmentAdapter;
	private float mDownX;
	private float mDownY;
	private float mDx;
	private float mDy;
	private Rect mCardRect;
	private Rect mStarImageRect;
	private List<Integer> mStarColor;
	private int mCompleteCount = 0;
	//游戏进行的时间
	private long mTrainingCountTime;
	private int mTrainTargetTime;
	private boolean mIsSuccess;
	private int mQuestionCount;

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
		mProgressBar.setTotalSecondTime(mTrainingDetail.getTrain().getTime());
		mTrainTargetTime = mTrainingDetail.getTrain().getTime() * 1000;
		mProgressBar.setOnTimeUpListener(new TrainProgressBar.OnTimeUpListener() {
			@Override
			public void onTick(long millisUntilUp) {
				mTrainingCountTime = millisUntilUp;
				mTitleBar.setTitle(DateUtil.format(mTrainingCountTime, "mm:ss.SS"));
			}

			@Override
			public void onFinish() {
				mTitleBar.setTitle(DateUtil.format(mTrainTargetTime, "mm:ss.SS"));
				mIsSuccess = false;
				LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(HowPlayActivity.TYPE_FINISH));
				requestEndTrain();
			}
		});
		mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.with(getActivity(), HowPlayActivity.class)
						.putExtra(ExtraKeys.TRAINING, mTrainingDetail.getTrain())
						.execute();
			}
		});
	}

	private void requestEndTrain() {
		TrainingSubmit trainingSubmit = new TrainingSubmit(mTrainingDetail.getTrain().getId());
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
		mNewNounExplanationList = new ArrayList<>();
		mStarColor = new ArrayList<>();
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
		for (int i = 0; i < mNewNounExplanationList.size(); i++) {
			mStarColor.add(i);
			mFragments.add(ExplanationFragment.newInstance(mNewNounExplanationList.get(i).getKey().getContent(),
					mNewNounExplanationList.get(i).getKey().getSeq()));
		}
		return mFragments;
	}

	private void initViewPager() {
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
				mCardView.setVisibility(View.VISIBLE);
				mStarImage.setVisibility(View.INVISIBLE);
				mExplanation.setText(mNewNounExplanationList.get(mViewPager.getCurrentItem()).getKey().getContent());
				TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 2000);
				translateAnimation.setDuration(500);
				translateAnimation.setFillAfter(true);
				mCardView.startAnimation(translateAnimation);


				if (mViewPager.getCurrentItem() > 0) {
					int position = mViewPager.getCurrentItem() - 1;
					mViewPager.setCurrentItem(position, true);
				} else {
					int position = mFragments.size() - 1;
					mViewPager.setCurrentItem(position, true);
				}
				break;

			case R.id.next:
				mCardView.setVisibility(View.VISIBLE);
				mStarImage.setVisibility(View.INVISIBLE);
				mExplanation.setText(mNewNounExplanationList.get(mViewPager.getCurrentItem()).getKey().getContent());
				TranslateAnimation translateAnimation2 = new TranslateAnimation(0, 0, 0, 1000);
				translateAnimation2.setDuration(500);
				translateAnimation2.setFillAfter(true);
				mCardView.startAnimation(translateAnimation2);

				if (mViewPager.getCurrentItem() < mFragments.size() - 1) {
					int position = mViewPager.getCurrentItem() + 1;
					mViewPager.setCurrentItem(position, true);
				} else {
					int position = 0;
					mViewPager.setCurrentItem(position, true);
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
				mStarColor.remove(mViewPager.getCurrentItem());
				mCompleteCount++;
				mNumber.setText(getString(R.string.explanation_number, mCompleteCount, mQuestionCount));

			} else {
				//只剩一套题目了
				initCardStar(mStarColor.get(mViewPager.getCurrentItem()));
				view.setVisibility(View.INVISIBLE);
				mViewPager.setVisibility(View.INVISIBLE);
				mCardView.setVisibility(View.VISIBLE);
				mStarImage.setVisibility(View.VISIBLE);
				TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, 0);
				translateAnimation.setDuration(1000);
				translateAnimation.setFillAfter(false);
				mCardView.startAnimation(translateAnimation);

				mCompleteCount++;
				mNumber.setText(getString(R.string.explanation_number, mCompleteCount, mQuestionCount));
				mIsSuccess = true;
				requestEndTrain();
			}
		} else {
			startBackAnimation(view);
		}
	}

	private void startCardFlyAnimation(View view) {
		initCardStar(mStarColor.get(mViewPager.getCurrentItem()));
		view.setVisibility(View.INVISIBLE);
		mCardView.setVisibility(View.VISIBLE);
		mStarImage.setVisibility(View.VISIBLE);
		TranslateAnimation translateAnimation = new TranslateAnimation(0, -1000, 0, -1000);
		translateAnimation.setDuration(500);
		translateAnimation.setFillAfter(true);
		mCardView.startAnimation(translateAnimation);
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
		animatorSet.setDuration(500).setInterpolator(new OvershootInterpolator());
		animatorSet.start();
	}

	@Override
	public void onBackPressed() {
		showCloseDialog();
	}

	private void showCloseDialog() {
		SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
				.setTitle(getString(R.string.is_sure_exit_train))
				.setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						finish();
					}
				})
				.setPositive(R.string.continue_train, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				}).show();
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
