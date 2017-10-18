package com.sbai.finance.activity.training;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Experience;
import com.sbai.finance.model.training.IsTrained;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingExperiencePraise;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class TrainingExperienceActivity extends BaseActivity {

	private static final int REQ_WRITE_EXPERIENCE = 1001;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.latestListView)
	ListView mLatestListView;
	@BindView(R.id.empty)
	LinearLayout mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	CustomSwipeRefreshLayout mSwipeRefreshLayout;

	private int mPageSize = 20;
	private int mPage = 0;
	private HashSet<String> mSet;
	private Training mTraining;
	private ExperienceListAdapter mHotExperienceListAdapter;
	private ExperienceListAdapter mLatestExperienceListAdapter;
	private TextView mHotExperience;
	private View mSpit;
	private View mWhiteSpit;
	private View mFootView;
	private MyListView mHotListView;
	private RefreshReceiver mRefreshReceiver;
	private int mIsFromTrainingResult;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_training_experience);
		ButterKnife.bind(this);
		initData(getIntent());
		mSet = new HashSet<>();

		initTitleBar();
		initHeadView1();
		initHeadView2();
		initLatestExperienceList();
		requestHotExperienceList();
		requestLatestExperienceList(true);
		initSwipeRefreshLayout();
		registerRefreshReceiver();

	}

	private void initData(Intent intent) {
		mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
		mIsFromTrainingResult = intent.getIntExtra(ExtraKeys.TRAIN_RESULT, -1);
	}

	private void initTitleBar() {
		if (LocalUser.getUser().isLogin()) {
			requestIsTrained();
		} else {
			mTitleBar.setRightVisible(false);
		}
	}

	private void initHeadView1() {
		LinearLayout header = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_header_training_experience, null);
		mHotExperience = (TextView) header.findViewById(R.id.hotExperience);
		mSpit = header.findViewById(R.id.spit);
		mWhiteSpit = header.findViewById(R.id.spitWhite);
		mHotListView = (MyListView) header.findViewById(R.id.hotListView);
		mHotExperienceListAdapter = new ExperienceListAdapter(this);
		mHotListView.setAdapter(mHotExperienceListAdapter);
		mLatestListView.addHeaderView(header);
		mHotExperienceListAdapter.setCallback(new ExperienceListAdapter.Callback() {
			@Override
			public void praiseOnClick(final Experience item) {
				if (LocalUser.getUser().isLogin()) {
					Client.trainExperiencePraise(item.getId(), item.getIsPraise() == 0 ? 1 : 0)
							.setCallback(new Callback2D<Resp<TrainingExperiencePraise>, TrainingExperiencePraise>() {
								@Override
								protected void onRespSuccessData(TrainingExperiencePraise data) {
									item.setIsPraise(data.getIsPraise());
									item.setPraise(data.getPraise());
									mHotExperienceListAdapter.notifyDataSetChanged();
								}
							}).fire();
				} else {
					Launcher.with(getActivity(), LoginActivity.class).executeForResult(REQ_CODE_LOGIN);
				}
			}
		});
	}

	private void initHeadView2() {
		LinearLayout header = (LinearLayout) LayoutInflater.from(this).inflate(R.layout.view_header_trainning_experience2, null);
		mLatestListView.addHeaderView(header);
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				mIsFromTrainingResult = -1;
				mSwipeRefreshLayout.setLoadMoreEnable(true);
				mLatestListView.removeFooterView(mFootView);
				mFootView = null;
				requestHotExperienceList();
				requestLatestExperienceList(true);
			}
		});

		mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				mLatestListView.postDelayed(new Runnable() {
					@Override
					public void run() {
						mIsFromTrainingResult = -1;
						requestLatestExperienceList(false);
					}
				}, 1000);
			}
		});
	}

	private void initLatestExperienceList() {
		mLatestExperienceListAdapter = new ExperienceListAdapter(this);
		mLatestListView.setAdapter(mLatestExperienceListAdapter);
		mLatestExperienceListAdapter.setCallback(new ExperienceListAdapter.Callback() {
			@Override
			public void praiseOnClick(final Experience item) {
				if (LocalUser.getUser().isLogin()) {
					Client.trainExperiencePraise(item.getId(), item.getIsPraise() == 0 ? 1 : 0)
							.setCallback(new Callback2D<Resp<TrainingExperiencePraise>, TrainingExperiencePraise>() {
								@Override
								protected void onRespSuccessData(TrainingExperiencePraise data) {
									item.setIsPraise(data.getIsPraise());
									item.setPraise(data.getPraise());
									mLatestExperienceListAdapter.notifyDataSetChanged();
								}
							}).fire();
				} else {
					Launcher.with(getActivity(), LoginActivity.class).executeForResult(REQ_CODE_LOGIN);
				}
			}
		});
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
								mWhiteSpit.setVisibility(View.GONE);
								mHotExperience.setVisibility(View.GONE);
							} else {
								mSpit.setVisibility(View.VISIBLE);
								mWhiteSpit.setVisibility(View.VISIBLE);
								mHotExperience.setVisibility(View.VISIBLE);
							}
							updateHotExperienceList(experienceList);
						}

						@Override
						public void onFailure(VolleyError volleyError) {
							super.onFailure(volleyError);
							stopRefreshAnimation();
							mSpit.setVisibility(View.GONE);
							mWhiteSpit.setVisibility(View.GONE);
							mHotExperience.setVisibility(View.GONE);
						}
					}).fire();
		}
	}

	private void updateHotExperienceList(List<Experience> experienceList) {
		mHotExperienceListAdapter.clear();
		mHotExperienceListAdapter.addAll(experienceList);
		if (mIsFromTrainingResult == 0) {
			mLatestListView.setSelection(1);
		}
	}

	private void requestLatestExperienceList(final boolean isRefresh) {
		if (mTraining != null) {
			Client.getLatestExperienceList(mPage, mPageSize, mTraining.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
						@Override
						protected void onRespSuccessData(List<Experience> experienceList) {
							if (experienceList.size() == 0 && mPage == 0) {
								mEmpty.setVisibility(View.VISIBLE);
								stopRefreshAnimation();
							} else {
								mEmpty.setVisibility(View.GONE);
								updateLatestExperienceList(experienceList, isRefresh);
							}
						}

						@Override
						public void onFailure(VolleyError volleyError) {
							super.onFailure(volleyError);
							stopRefreshAnimation();
							if (mPage == 0) {
								mEmpty.setVisibility(View.VISIBLE);
							}
						}
					}).fire();
		}
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}

		if (mSwipeRefreshLayout.isLoading()) {
			mSwipeRefreshLayout.setLoading(false);
		}
	}

	private void updateLatestExperienceList(List<Experience> experienceList, boolean isRefresh) {
		if (experienceList.size() < mPageSize) {
			mSwipeRefreshLayout.setLoadMoreEnable(false);
		} else {
			mPage++;
		}

		if (experienceList.size() < mPageSize && mPage > 0) {
			if (mFootView == null) {
				mFootView = View.inflate(getActivity(), R.layout.view_footer_load_complete, null);
				mLatestListView.addFooterView(mFootView, null, true);
			}
		}

		if (isRefresh) {
			if (mLatestExperienceListAdapter != null) {
				mLatestExperienceListAdapter.clear();
			}
		}
		stopRefreshAnimation();

		for (Experience experience : experienceList) {
			if (mSet.add(experience.getId())) {
				mLatestExperienceListAdapter.add(experience);
			}
		}

		if (mIsFromTrainingResult == 0) {
			mLatestListView.setSelection(1);
		}
	}


	@Override
	public void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
	}

	static class ExperienceListAdapter extends ArrayAdapter<Experience> {

		private Context mContext;
		private Callback mCallback;

		public interface Callback {
			void praiseOnClick(Experience item);
		}

		private void setCallback(Callback callback) {
			mCallback = callback;
		}

		public ExperienceListAdapter(@NonNull Context context) {
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
			viewHolder.bindingData(mContext, getItem(position), mCallback);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.experience)
			TextView mExperience;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.praiseNumber)
			TextView mPraiseNumber;
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

			public void bindingData(final Context context, final Experience item, final Callback callback) {
				if (item == null) return;

				if (item.getUserModel() != null) {
					GlideApp.with(context).load(item.getUserModel().getUserPortrait())
							.placeholder(R.drawable.ic_default_avatar)
							.circleCrop()
							.into(mAvatar);

					mUserName.setText(item.getUserModel().getUserName());
					mPublishTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateDate()));

					mAvatar.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Launcher.with(context, LookBigPictureActivity.class)
									.putExtra(Launcher.EX_PAYLOAD, item.getUserModel().getUserPortrait())
									.putExtra(Launcher.EX_PAYLOAD_2, 0)
									.execute();
						}
					});
				} else {
					GlideApp.with(context).load(R.drawable.ic_default_avatar)
							.circleCrop()
							.into(mAvatar);

					mUserName.setText("");
					mPublishTime.setText("");

					mAvatar.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Launcher.with(context, LookBigPictureActivity.class)
									.putExtra(Launcher.EX_PAYLOAD, "")
									.putExtra(Launcher.EX_PAYLOAD_2, 0)
									.execute();
						}
					});
				}


				mExperience.setText(item.getContent());

				if (item.getPraise() == 0) {
					mPraiseNumber.setText(R.string.praise);
				} else {
					mPraiseNumber.setText(StrFormatter.getFormatCount(item.getPraise()));
				}

				if (item.getIsPraise() == 1) {
					mPraiseNumber.setSelected(true);
				} else {
					mPraiseNumber.setSelected(false);
				}

				mPraiseNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.praiseOnClick(item);
						}
					}
				});

				if (TextUtils.isEmpty(item.getPicture())) {
					mImageView.setVisibility(View.GONE);
				} else {
					mImageView.setVisibility(View.VISIBLE);
					GlideApp.with(context).load(item.getPicture())
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_WRITE_EXPERIENCE && resultCode == RESULT_OK) {
			mSet.clear();
			mPage = 0;
			requestLatestExperienceList(true);
			mSwipeRefreshLayout.setLoadMoreEnable(true);
			mLatestListView.removeFooterView(mFootView);
			mFootView = null;
			mLatestListView.setSelection(1);
		}

		if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
			requestIsTrained();
		}
	}

	private void registerRefreshReceiver() {
		mRefreshReceiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_LOGIN_SUCCESS);
		LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
	}

	private class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {

			if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				mSet.clear();
				mPage = 0;
				mLatestListView.removeFooterView(mFootView);
				mFootView = null;
				requestHotExperienceList();
				requestLatestExperienceList(true);
			}
		}
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_OK);
		super.onBackPressed();
	}
}
