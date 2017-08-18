package com.sbai.finance.activity.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.fragment.training.ExplanationFragment;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.view.training.DragImageView;
import com.sbai.finance.view.training.TrainHeaderView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class NounExplanationActivity extends AppCompatActivity {

	@BindView(R.id.trainHeaderView)
	TrainHeaderView mTrainHeaderView;
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

	private TrainingQuestion mTrainingQuestion;
	private long mCountDown;
	private List<TrainingQuestion.ContentBean> mNounExplanationList;
	private List<Fragment> mFragments;
	private ExplanationFragmentAdapter mExplanationFragmentAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_noun_explanation);
		ButterKnife.bind(this);
		initData(getIntent());
		mFragments = getFragments();
		initViewPager();
	}

	private void initData(Intent intent) {
		mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.TRAIN_QUESTIONS);
		mCountDown = intent.getIntExtra(ExtraKeys.TRAIN_TARGET_TIME, 0);
		mNounExplanationList = mTrainingQuestion.getContent();
	}

	private List<Fragment> getFragments() {
		List<Fragment> mFragments = new ArrayList<>();
		for (int i = 0; i < mNounExplanationList.size(); i++) {
			mFragments.add(ExplanationFragment.newInstance(mNounExplanationList.get(i).getContent()));
		}
		return mFragments;
	}

	private void initViewPager() {
		mViewPager.setOffscreenPageLimit(mFragments.size() * 2);
		mExplanationFragmentAdapter = new ExplanationFragmentAdapter(getSupportFragmentManager(), mFragments);
		mViewPager.setAdapter(mExplanationFragmentAdapter);
	}

	@OnClick({R.id.previous, R.id.next})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.previous:
				break;
			case R.id.next:
				break;
		}
	}

	public class ExplanationFragmentAdapter extends FragmentPagerAdapter{
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
		public int getCount() {
			return mFragments != null ? mFragments.size() : 0;
		}
	}
}
