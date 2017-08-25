package com.sbai.finance.activity.training;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Experience;
import com.sbai.finance.model.training.IsTrained;
import com.sbai.finance.model.training.TrainingExperiencePraise;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingExperienceActivity extends BaseActivity {

	private static final int REQ_WRITE_EXPERIENCE = 1001;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.hotListView)
	MyListView mHotListView;
	@BindView(R.id.latestListView)
	MyListView mLatestListView;
	@BindView(R.id.empty)
	TextView mEmpty;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.spit)
	View mSpit;
	@BindView(R.id.hotExperience)
	TextView mHotExperience;

	private int mPageSize = 20;
	private int mPage = 0;
	private HashSet<String> mSet;
	private View mFootView;
	private Training mTraining;
	private HotExperienceListAdapter mHotExperienceListAdapter;
	private LatestExperienceListAdapter mLatestExperienceListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training_experience);
		ButterKnife.bind(this);
		initData(getIntent());
		mSet = new HashSet<>();

		initTitleBar();
		initHotExperienceList();
		initLatestExperienceList();
		requestHotExperienceList();
		requestLatestExperienceList();
		initSwipeRefreshLayout();
		scrollToTop(mTitleBar, mScrollView);
	}

	private void initData(Intent intent) {
		mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
	}

	private void initTitleBar() {
		if (LocalUser.getUser().isLogin()) {
			requestIsTrained();
		} else {
			mTitleBar.setRightVisible(false);
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				requestHotExperienceList();
				requestLatestExperienceList();
			}
		});
	}

	private void initHotExperienceList() {
		mHotExperienceListAdapter = new HotExperienceListAdapter(this);
		mHotListView.setFocusable(false);
		mHotListView.setAdapter(mHotExperienceListAdapter);
	}

	private void initLatestExperienceList() {
		mLatestExperienceListAdapter = new LatestExperienceListAdapter(this);
		mLatestListView.setEmptyView(mEmpty);
		mLatestListView.setFocusable(false);
		mLatestListView.setAdapter(mLatestExperienceListAdapter);
	}

	private void requestIsTrained() {
		if (mTraining != null) {
			Client.isTrained(mTraining.getId()).setTag(TAG).setIndeterminate(this)
					.setCallback(new Callback2D<Resp<IsTrained>, IsTrained>() {
						@Override
						protected void onRespSuccessData(IsTrained data) {
							if (data.getIsPerception() == 1) {
								//训练过出现写心得按钮
								mTitleBar.setRightVisible(true);
								mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										Launcher.with(getActivity(), WriteExperienceActivity.class)
												.putExtra(ExtraKeys.TRAINING, mTraining)
												.executeForResult(REQ_WRITE_EXPERIENCE);
									}
								});
							} else {
								//没训练过隐藏写心得按钮
								mTitleBar.setRightVisible(false);
							}
						}
					}).fire();
		}
	}

	private void requestHotExperienceList() {
		if (mTraining != null) {
			Client.getHotExperienceList(mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
						@Override
						protected void onRespSuccessData(List<Experience> experienceList) {
							if (experienceList.size() == 0) {
								mSpit.setVisibility(View.GONE);
								mHotExperience.setVisibility(View.GONE);
							} else {
								mSpit.setVisibility(View.VISIBLE);
								mHotExperience.setVisibility(View.VISIBLE);
							}
							updateHotExperienceList(experienceList);
						}

						@Override
						public void onFailure(VolleyError volleyError) {
							super.onFailure(volleyError);
							mSpit.setVisibility(View.GONE);
							mHotExperience.setVisibility(View.GONE);
							stopRefreshAnimation();
						}
					}).fire();
		}
	}

	private void updateHotExperienceList(List<Experience> experienceList) {
		mHotExperienceListAdapter.clear();
		mHotExperienceListAdapter.addAll(experienceList);
	}

	private void requestLatestExperienceList() {
		if (mTraining != null) {
			Client.getLatestExperienceList(mPage, mPageSize, mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
						@Override
						protected void onRespSuccessData(List<Experience> experienceList) {
							updateLatestExperienceList(experienceList);
						}

						@Override
						public void onFailure(VolleyError volleyError) {
							super.onFailure(volleyError);
							stopRefreshAnimation();
						}
					}).fire();
		}
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	private void updateLatestExperienceList(List<Experience> experienceList) {
		if (experienceList == null) {
			stopRefreshAnimation();
			return;
		}

		if (mFootView == null) {
			mFootView = View.inflate(getActivity(), R.layout.view_footer_load_more, null);
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mPage++;
					requestLatestExperienceList();
				}
			});
			mLatestListView.addFooterView(mFootView, null, true);
		}

		if (experienceList.size() < mPageSize) {
			mLatestListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mLatestExperienceListAdapter != null) {
				mLatestExperienceListAdapter.clear();
				mLatestExperienceListAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (Experience experience : experienceList) {
			if (mSet.add(experience.getId())) {
				mLatestExperienceListAdapter.add(experience);
			}
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
									.setCallback(new Callback2D<Resp<TrainingExperiencePraise>, TrainingExperiencePraise>() {
										@Override
										protected void onRespSuccessData(TrainingExperiencePraise data) {
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
							Launcher.with(context, LoginActivity.class).executeForResult(REQ_LOGIN);
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

	static class LatestExperienceListAdapter extends ArrayAdapter<Experience> {

		private Context mContext;

		public LatestExperienceListAdapter(@NonNull Context context) {
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
									.setCallback(new Callback2D<Resp<TrainingExperiencePraise>, TrainingExperiencePraise>() {
										@Override
										protected void onRespSuccessData(TrainingExperiencePraise data) {
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
							Launcher.with(context, LoginActivity.class).executeForResult(REQ_LOGIN);
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
					case 0:
						mStar1.setVisibility(View.GONE);
						mStar2.setVisibility(View.GONE);
						mStar3.setVisibility(View.GONE);
						break;
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_WRITE_EXPERIENCE && resultCode == RESULT_OK) {
			mSet.clear();
			mPage = 0;
			mSwipeRefreshLayout.setRefreshing(true);
			requestLatestExperienceList();
			mScrollView.smoothScrollTo(0, 0);
		}

		if (requestCode == REQ_LOGIN && resultCode == RESULT_OK) {
			requestIsTrained();
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
}