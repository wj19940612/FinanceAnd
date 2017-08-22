package com.sbai.finance.activity.training;

import android.content.Context;
import android.content.Intent;
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
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Experience;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.TrainPraise;
import com.sbai.finance.model.training.TrainedUserRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingRecord;
import com.sbai.finance.model.training.TrainingTarget;
import com.sbai.finance.model.training.question.KData;
import com.sbai.finance.model.training.question.RemoveData;
import com.sbai.finance.model.training.question.SortData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.ImageListView;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.ObservableScrollView;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.finance.view.training.TrainingAchievementView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TrainingDetailActivity extends BaseActivity {

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.extraBackground)
	LinearLayout mExtraBackground;

	@BindView(R.id.title)
	TextView mTitle;
	@BindView(R.id.introduce)
	TextView mIntroduce;
	@BindView(R.id.scrollView)
	ObservableScrollView mObservableScrollView;

	@BindView(R.id.duration)
	TextView mDuration;
	@BindView(R.id.difficulty)
	TextView mDifficulty;
	@BindView(R.id.imageListView)
	ImageListView mImageListView;
	@BindView(R.id.completeNumber)
	TextView mCompleteNumber;

	@BindView(R.id.relatedKnowledge)
	TextView mRelatedKnowledge;

	@BindView(R.id.writeExperience)
	TextView mWriteExperience;

	@BindView(R.id.empty)
	LinearLayout mEmpty;

	@BindView(R.id.startTraining)
	TextView mStartTraining;
	@BindView(R.id.HotListView)
	MyListView mHotListView;

	private TrainingDetail mTrainingDetail;
	private Training mTraining;
	private TrainingRecord mMyTrainingRecord;

	private TrainingAchievementView2[] mAchievementView2s;

	private List<Experience> mHotExperienceList;
	private HotExperienceListAdapter mHotExperienceListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training_detail);
		ButterKnife.bind(this);

		initData(getIntent());
		initBackground();

		mHotExperienceList = new ArrayList<>();
		mHotExperienceListAdapter = new HotExperienceListAdapter(this);
		mHotListView.setEmptyView(mEmpty);
		mHotListView.setFocusable(false);
		mHotListView.setAdapter(mHotExperienceListAdapter);

		mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share();
			}
		});
		mObservableScrollView.setScrollChangedListener(new ObservableScrollView.OnScrollChangedListener() {
			@Override
			public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {

			}
		});

		initAchievementViews();

		requestTrainDetail();
		requestFinishPeopleList();
		requestHotExperienceList();
	}

	private void initAchievementViews() {
		mAchievementView2s = new TrainingAchievementView2[3];
		mAchievementView2s[0] = (TrainingAchievementView2) findViewById(R.id.achievement0);
		mAchievementView2s[1] = (TrainingAchievementView2) findViewById(R.id.achievement1);
		mAchievementView2s[2] = (TrainingAchievementView2) findViewById(R.id.achievement2);
	}

	private void updateAchievementViews() {
		if (mTrainingDetail != null) {
			List<TrainingTarget> targets = mTrainingDetail.getTargets();
			if (targets != null && !targets.isEmpty()) {
				for (int i = 0; i < targets.size() && i < mAchievementView2s.length; i++) {
					TrainingTarget target = targets.get(i);
					mAchievementView2s[i].setVisibility(View.VISIBLE);

					updateAchievementView(mAchievementView2s[i], target);

					if (mMyTrainingRecord != null && mMyTrainingRecord.getMaxLevel() >= target.getLevel()) {
						mAchievementView2s[i].setAchieved(true);
					}
				}
			}
		}
	}

	private void updateAchievementView(TrainingAchievementView2 achievementView, TrainingTarget target) {
		switch (target.getType()) {
			case TrainingTarget.TYPE_FINISH:
				achievementView.setContent(R.string.mission_complete);
				break;
			case TrainingTarget.TYPE_RATE:
				achievementView.setContent(getString(R.string.accuracy_to_,
						FinanceUtil.formatToPercentage(target.getRate(), 0)));
				break;
			case TrainingTarget.TYPE_TIME:
				achievementView.setContent(formatTime(target.getTime(),
						R.string._seconds_complete,
						R.string._minutes_complete,
						R.string._minutes_x_seconds_complete));
				break;
		}
	}

	private String formatTime(int seconds, int secondRes, int minRes, int minSecondRes) {
		if (seconds / 60 == 0) {
			return getString(secondRes, seconds);
		} else if (seconds % 60 == 0) {
			return getString(minRes, seconds / 60);
		} else {
			return getString(minSecondRes, seconds / 60, seconds % 60);
		}
	}

	@Override
	protected void onPostResume() {
		super.onPostResume();
		requestMyTrainingRecord();
	}

	private void requestMyTrainingRecord() {
		if (LocalUser.getUser().isLogin()) {
			Client.getMyTrainingRecord(mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<TrainingRecord>, TrainingRecord>() {
						@Override
						protected void onRespSuccessData(TrainingRecord data) {
							mMyTrainingRecord = data;
							updateAchievementViews();

							if (mMyTrainingRecord.getFinish() > 0) {
								mWriteExperience.setVisibility(View.VISIBLE);
							}
						}
					}).fireFree();
		}
	}

	private void initData(Intent intent) {
		mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
	}

	private void initBackground() {
		switch (mTraining.getType()) {
			case Training.TYPE_THEORY:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
				mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
				mStartTraining.setBackgroundResource(R.drawable.bg_train_theory);
				break;
			case Training.TYPE_TECHNOLOGY:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
				mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
				mStartTraining.setBackgroundResource(R.drawable.bg_train_technology);
				break;
			case Training.TYPE_FUNDAMENTAL:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
				mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
				mStartTraining.setBackgroundResource(R.drawable.bg_train_fundamentals);
				break;
			case Training.TYPE_COMPREHENSIVE:
				mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
				mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
				mStartTraining.setBackgroundResource(R.drawable.bg_train_comprehensive);
				break;
		}
	}

	private void requestTrainDetail() {
		Client.getTrainingDetail(mTraining.getId()).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<TrainingDetail>, TrainingDetail>() {
					@Override
					protected void onRespSuccessData(TrainingDetail data) {
						mTrainingDetail = data;
						updateTrainDetail();
						updateAchievementViews();
					}
				}).fire();
	}


	private void requestFinishPeopleList() {
		Client.getTrainedUserRecords(0, 3, mTraining.getId()).setTag(TAG)
				.setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<TrainedUserRecord>>, List<TrainedUserRecord>>() {
					@Override
					protected void onRespSuccessData(List<TrainedUserRecord> data) {
						List<String> userPortraitList = new ArrayList<String>();
						for (TrainedUserRecord userRecord : data) {
							userPortraitList.add(userRecord.getUser().getUserPortrait());
						}
						mImageListView.setImages(userPortraitList, R.drawable.ic_board_head_more_grey);
					}
				}).fire();
	}

	private void requestHotExperienceList() {
		Client.getHotExperienceList(mTraining.getId()).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
					@Override
					protected void onRespSuccessData(List<Experience> experienceList) {
						List<Experience> newExperienceList = new ArrayList<Experience>();
						// TODO: 2017/8/22 会出现问题 先注释掉
//						for (int i = 0; i < 2; i++) {
//							newExperienceList.add(experienceList.get(i));
//						}
						updateHotExperienceList(newExperienceList);
					}
				}).fire();
	}

	private void updateHotExperienceList(List<Experience> experienceList) {
		// TODO: 21/08/2017 更新两个 row
		mHotExperienceListAdapter.clear();
		mHotExperienceListAdapter.addAll(experienceList);
	}

	private void updateTrainDetail() {
		Training training = mTrainingDetail.getTrain();
		if (training != null) {
			mTitle.setText(training.getTitle());
			mIntroduce.setText(training.getRemark());
			mDuration.setText(getString(R.string.train_duration, training.getTime() / 60));
			mDifficulty.setText(getString(R.string.train_level, training.getLevel()));
			mCompleteNumber.setText(getString(R.string.complete_number, training.getFinishCount()));
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
					mPublishTime.setText(DateUtil.getMissFormatTime(item.getCreateDate()));
				} else {
					Glide.with(context).load(R.drawable.ic_default_avatar)
							.transform(new GlideCircleTransform(context))
							.into(mAvatar);
					mUserName.setText("");
					mPublishTime.setText("");
				}

				mExperience.setText(item.getContent());
				mLoveNumber.setText(StrFormatter.getFormatCount(item.getPraise()));

				if (item.getIsPraise() == 1) {
					mLoveNumber.setSelected(true);
				} else {
					mLoveNumber.setSelected(false);
				}

				mLoveNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (LocalUser.getUser().isLogin()) {
							Client.trainExperiencePraise(item.getId(), item.getIsPraise() == 0 ? 1 : 0)
									.setCallback(new Callback2D<Resp<TrainPraise>, TrainPraise>() {
										@Override
										protected void onRespSuccessData(TrainPraise data) {
											if (data.getIsPraise() == 1) {
												mLoveNumber.setSelected(true);
											} else {
												mLoveNumber.setSelected(false);
											}
											item.setIsPraise(data.getIsPraise());
											mLoveNumber.setText(StrFormatter.getFormatCount(data.getPraise()));
										}
									}).fire();

						} else {
							Launcher.with(context, LoginActivity.class).execute();
						}
					}
				});

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

	@OnClick({R.id.relatedKnowledge,
			R.id.trainingExperience, R.id.writeExperience,
			R.id.startTraining})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.relatedKnowledge:
				if (mTrainingDetail != null && mTrainingDetail.getTrain() != null) {
					Launcher.with(getActivity(), WebActivity.class)
							.putExtra(WebActivity.EX_TITLE, "相关知识点")
							.putExtra(WebActivity.EX_URL, mTrainingDetail.getTrain().getKnowledgeUrl())
							.execute();
				}
				break;
			case R.id.trainingExperience:
				Launcher.with(getActivity(), TrainExperienceActivity.class)
						.putExtra(ExtraKeys.TRAINING, mTraining)
						.execute();
				break;
			case R.id.writeExperience:
				Launcher.with(getActivity(), WriteExperienceActivity.class)
						.putExtra(ExtraKeys.TRAINING, mTraining)
						.execute();
				break;
			case R.id.startTraining:
				if (LocalUser.getUser().isLogin()) {
					requestTrainingContent();
				} else {
					// TODO: 17/08/2017 登录后要做页面更新
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
				break;
		}
	}

	private void requestTrainingContent() {
		if (mTraining.getPlayType() == Training.PLAY_TYPE_REMOVE
				|| mTraining.getPlayType() == Training.PLAY_TYPE_MATCH_STAR) {
			Client.getTrainingContent(mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<String>, List<Question<RemoveData>>>() {

						@Override
						protected String onInterceptData(String data) {
							return SecurityUtil.AESDecrypt(data);
						}

						@Override
						protected void onRespSuccessData(List<Question<RemoveData>> data) {
							if (!data.isEmpty()) {
								startTraining(data.get(0));
							}
						}
					}).fireFree();
		} else if (mTraining.getPlayType() == Training.PLAY_TYPE_SORT) {
			Client.getTrainingContent(mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<String>, List<Question<SortData>>>() {
						@Override
						protected String onInterceptData(String data) {
							return SecurityUtil.AESDecrypt(data);
						}

						@Override
						protected void onRespSuccessData(List<Question<SortData>> data) {
							if (!data.isEmpty()) {
								startTraining(data.get(0));
							}
						}
					}).fireFree();
		} else if (mTraining.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
			Client.getTrainingContent(mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<String>, List<Question<KData>>>() {
						@Override
						protected void onRespSuccessData(List<Question<KData>> data) {
							if (!data.isEmpty()) {
								startTraining(data.get(0));
							}
						}

						@Override
						protected String onInterceptData(String data) {
							return SecurityUtil.AESDecrypt(data);
						}
					}).fireFree();
		}
	}

	private void startTraining(Question question) {
		Launcher.with(getActivity(), TrainingCountDownActivity.class)
				.putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
				.putExtra(ExtraKeys.QUESTION, question)
				.execute();
	}

	private void share() {
		ShareDialog.with(getActivity())
				.setTitle(getString(R.string.share_title))
				.setShareTitle(getString(R.string.train_share_share_title, mTrainingDetail.getTrain().getTitle()))
				.setShareDescription(getString(R.string.train_share_description))
				.setShareUrl(String.format(Client.SHARE_URL_TRAIN_EXPERIENCE, mTraining.getId()))
				.hasFeedback(true)
				.setListener(new ShareDialog.OnShareDialogCallback() {
					@Override
					public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
						Client.share().setTag(TAG).fire();
					}

					@Override
					public void onFeedbackClick(View view) {
						Launcher.with(getActivity(), FeedbackActivity.class).execute();
					}
				}).show();
	}
}
