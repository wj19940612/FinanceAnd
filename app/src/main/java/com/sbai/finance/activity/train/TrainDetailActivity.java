package com.sbai.finance.activity.train;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.train.CompletePeople;
import com.sbai.finance.model.train.Experience;
import com.sbai.finance.model.train.TrainDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.ImageListView;
import com.sbai.finance.view.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TrainDetailActivity extends BaseActivity {

	private static final int TYPE_THEORY = 1;
	private static final int TYPE_TECHNOLOGY = 2;
	private static final int TYPE_FUNDAMENTALS = 3;
	private static final int TYPE_COMPREHENSIVE = 4;

	@BindView(R.id.back)
	ImageView mBack;
	@BindView(R.id.titleName)
	TextView mTitleName;
	@BindView(R.id.share)
	ImageView mShare;
	@BindView(R.id.titleBar)
	RelativeLayout mTitleBar;
	@BindView(R.id.duration)
	TextView mDuration;
	@BindView(R.id.difficulty)
	TextView mDifficulty;
	@BindView(R.id.imageListView)
	ImageListView mImageListView;
	@BindView(R.id.completeNumber)
	TextView mCompleteNumber;
	@BindView(R.id.relevantKnowledge)
	RelativeLayout mRelevantKnowledge;
	@BindView(R.id.hotExperience)
	LinearLayout mHotExperience;
	@BindView(R.id.HotListView)
	MyListView mHotListView;
	@BindView(R.id.writeExperience)
	TextView mWriteExperience;
	@BindView(R.id.empty)
	LinearLayout mEmpty;
	@BindView(R.id.startTrain)
	TextView mStartTrain;
	@BindView(R.id.title)
	TextView mTitle;
	@BindView(R.id.introduce)
	TextView mIntroduce;
	@BindView(R.id.background)
	RelativeLayout mBackground;

	private int mTrainId = 5;
	private int mType = 3;
	private int mPage = 0;
	private int mPageSize = 3;
	private List<String> mCompletePeopleList;
	private HotExperienceListAdapter mHotExperienceListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_train_detail);
		ButterKnife.bind(this);
		initBackGround(mType);

		mCompletePeopleList = new ArrayList<>();
		mHotExperienceListAdapter = new HotExperienceListAdapter(this);
		mHotListView.setEmptyView(mEmpty);
		mHotListView.setFocusable(false);
		mHotListView.setAdapter(mHotExperienceListAdapter);

		requestTrainDetail();
		requestFinishPeopleList();
		requestHotExperienceList();
	}

	private void initBackGround(int type) {
		switch (type) {
			case TYPE_THEORY:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.theory_red));
				mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.theory_red));
				mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_theory));
				break;
			case TYPE_TECHNOLOGY:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.technology_violet));
				mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.technology_violet));
				mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_technology));
				break;
			case TYPE_FUNDAMENTALS:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.fundamentals_yellow));
				mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.fundamentals_yellow));
				mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_fundamentals));
				break;
			case TYPE_COMPREHENSIVE:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.comprehensive_blue));
				mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.comprehensive_blue));
				mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_comprehensive));
				break;
		}
	}

	private void requestTrainDetail() {
		Client.getTrainDetail(mTrainId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<TrainDetail>, TrainDetail>() {
					@Override
					protected void onRespSuccessData(TrainDetail trainDetail) {
						updateTrainDetail(trainDetail);
					}
				}).fire();
	}

	private void requestFinishPeopleList() {
		Client.getFinishPeopleList(mPage, mPageSize, mTrainId).setTag(TAG)
				.setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<CompletePeople>>, List<CompletePeople>>() {
					@Override
					protected void onRespSuccessData(List<CompletePeople> completePeopleList) {
						for (CompletePeople completePeople : completePeopleList) {
							mCompletePeopleList.add(completePeople.getUser().getUserPortrait());
						}
						mImageListView.setImages(mCompletePeopleList);
					}
				}).fire();
	}

	private void requestHotExperienceList() {
		Client.getHotExperienceList(mTrainId).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
					@Override
					protected void onRespSuccessData(List<Experience> experienceList) {
						updateHotExperienceList(experienceList);
					}
				}).fire();
	}

	private void updateHotExperienceList(List<Experience> experienceList) {
		mHotExperienceListAdapter.clear();
		mHotExperienceListAdapter.addAll(experienceList);
	}

	private void updateTrainDetail(TrainDetail trainDetail) {
		if (trainDetail.getTrain() != null) {
			mTitle.setText(trainDetail.getTrain().getTitle());
			mIntroduce.setText(trainDetail.getTrain().getRemark());
			mDuration.setText(getString(R.string.train_duration, trainDetail.getTrain().getTime() / 60));
			mDifficulty.setText(getString(R.string.train_level, trainDetail.getTrain().getLevel()));
			mCompleteNumber.setText(getString(R.string.complete_number, trainDetail.getTrain().getFinishCount()));
		}
	}

	static class HotExperienceListAdapter extends ArrayAdapter<Experience> {

		private Context mContext;

		public HotExperienceListAdapter(@NonNull Context context) {
			super(context, 0);
			this.mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_train_experience, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, getItem(position));
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.hotArea)
			LinearLayout mHotArea;
			@BindView(R.id.experience)
			TextView mExperience;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.loveNumber)
			TextView mLoveNumber;
			@BindView(R.id.imageView)
			ImageView mImageView;
			@BindView(R.id.star1)
			ImageView mStar1;
			@BindView(R.id.star2)
			ImageView mStar2;
			@BindView(R.id.star3)
			ImageView mStar3;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Experience item) {
				if (item == null) return;

				if (item.getUserModel() != null) {
					Glide.with(context).load(item.getUserModel().getUserPortrait())
							.placeholder(R.drawable.ic_default_avatar)
							.transform(new GlideCircleTransform(context))
							.into(mAvatar);

					mUserName.setText(item.getUserModel().getUserName());
					mPublishTime.setText(item.getUserModel().getCreateTime());
				} else {
					Glide.with(context).load(R.drawable.ic_default_avatar)
							.transform(new GlideCircleTransform(context))
							.into(mAvatar);
					mUserName.setText("");
					mPublishTime.setText("");
				}

				mExperience.setText(item.getContent());
				mLoveNumber.setText(StrFormatter.getFormatCount(item.getPraise()));

				if (item.getPicture() == null || "".equalsIgnoreCase(item.getPicture())) {
					mImageView.setVisibility(View.GONE);
				} else {
					mImageView.setVisibility(View.VISIBLE);
					Glide.with(context).load(item.getPicture())
							.placeholder(R.drawable.ic_default_image)
							.into(mImageView);

					mImageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Launcher.with(context, LookBigPictureActivity.class)
									.putExtra(Launcher.EX_PAYLOAD, item.getPicture())
									.execute();
						}
					});
				}

				switch (item.getStar()) {
					case 1:
						mStar1.setVisibility(View.VISIBLE);
						mStar2.setVisibility(View.GONE);
						mStar3.setVisibility(View.GONE);
						break;
					case 2:
						mStar1.setVisibility(View.VISIBLE);
						mStar2.setVisibility(View.VISIBLE);
						mStar3.setVisibility(View.GONE);
						break;
					case 3:
						mStar1.setVisibility(View.VISIBLE);
						mStar2.setVisibility(View.VISIBLE);
						mStar3.setVisibility(View.VISIBLE);
						break;
				}
			}
		}
	}

	@OnClick({R.id.back, R.id.share, R.id.relevantKnowledge, R.id.hotExperience, R.id.writeExperience, R.id.startTrain})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.back:
				finish();
				break;
			case R.id.share:
				break;
			case R.id.relevantKnowledge:
				break;
			case R.id.hotExperience:
				break;
			case R.id.writeExperience:
				break;
			case R.id.startTrain:
				break;
		}
	}
}
