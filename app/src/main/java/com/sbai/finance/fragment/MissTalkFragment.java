package com.sbai.finance.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.CommentActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.RewardMissActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.R.id.listenerNumber;
import static com.sbai.finance.R.id.soundTime;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGOUT_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_LOGIN;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

public class MissTalkFragment extends BaseFragment implements MissAudioManager.IAudioDisplay {

	private static final int REQ_COMMENT = 1001;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;

	@BindView(R.id.swipeRefreshLayout)
	VerticalSwipeRefreshLayout mSwipeRefreshLayout;

	@BindView(R.id.missFloatWindow)
	MissFloatWindow mMissFloatWindow;

	private List<Question> mHotQuestionList;
	private List<Question> mLatestQuestionList;
	private MissListAdapter mMissListAdapter;
	private QuestionListAdapter mQuestionListAdapter;
	private Long mCreateTime;
	private int mPageSize = 20;
	private HashSet<Integer> mSet;
	private RefreshReceiver mRefreshReceiver;
	private View mFootView;
	private Question mPlayIngItem;

	Unbinder unbinder;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_miss_talk, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		initTitleBar();
		initListView();
		initMissHeaderView();

		requestMissList();
		requestHotQuestionList(true);

		initSwipeRefreshLayout();

		registerRefreshReceiver();

        MissAudioManager.get().addAudioView(this);
	}

	private void initTitleBar() {
		mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
				} else {
					stopQuestionVoice();
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(intent, REQ_LOGIN);
				}
			}
		});
	}

	private void initMissHeaderView() {
		LinearLayout header = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_header_miss_talk_1, null);
		EmptyRecyclerView recyclerView = (EmptyRecyclerView) header.findViewById(R.id.recyclerView);
		TextView emptyView = (TextView) header.findViewById(R.id.missEmpty);
		mMissListAdapter = new MissListAdapter(getActivity(), new ArrayList<Miss>());
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
		gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		recyclerView.setLayoutManager(gridLayoutManager);
		recyclerView.setEmptyView(emptyView);
		recyclerView.setAdapter(mMissListAdapter);
		mMissListAdapter.setOnItemClickListener(new MissListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Miss item) {
				if (item != null) {
					stopQuestionVoice();
					Launcher.with(getActivity(), MissProfileActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
				}
			}
		});
		mListView.addHeaderView(header);
	}

	private void initListView() {
		mSet = new HashSet<>();
		mListView.setEmptyView(mEmpty);
		mQuestionListAdapter = new QuestionListAdapter(getActivity());
		mListView.setAdapter(mQuestionListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Question item = (Question) parent.getItemAtPosition(position);
				if (item != null) {
					Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
					intent.putExtra(Launcher.EX_PAYLOAD, item.getId());
					if (mPlayIngItem != null) {
						intent.putExtra(ExtraKeys.PLAYING_ID, mPlayIngItem.getId());
						intent.putExtra(ExtraKeys.PLAYING_URL, mPlayIngItem.getAnswerContext());
						intent.putExtra(ExtraKeys.PLAYING_AVATAR, mPlayIngItem.getCustomPortrait());
					}
					intent.putExtra(ExtraKeys.IS_FROM_MISS_TALK, true);
					startActivityForResult(intent, REQ_QUESTION_DETAIL);
					umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
				}
			}
		});

		mQuestionListAdapter.setCallback(new QuestionListAdapter.Callback() {
			@Override
			public void onPraiseClick(final Question item) {
				if (LocalUser.getUser().isLogin()) {
					umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);

					Client.praise(item.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {
						@Override
						protected void onRespSuccessData(Praise praise) {
							item.setIsPrise(praise.getIsPrise());
							item.setPriseCount(praise.getPriseCount());
							mQuestionListAdapter.notifyDataSetChanged();
						}
					}).fire();
				} else {
					stopQuestionVoice();
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void onCommentClick(Question item) {
				if (item.getReplyCount() == 0) {
					if (LocalUser.getUser().isLogin()) {
						Intent intent = new Intent(getActivity(), CommentActivity.class);
						intent.putExtra(Launcher.EX_PAYLOAD, item.getQuestionUserId());
						intent.putExtra(Launcher.EX_PAYLOAD_1, item.getId());
						startActivityForResult(intent, REQ_COMMENT);
					} else {
						stopQuestionVoice();
						Launcher.with(getActivity(), LoginActivity.class).execute();
					}
				} else {
					Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
					intent.putExtra(Launcher.EX_PAYLOAD, item.getId());
					startActivityForResult(intent, REQ_QUESTION_DETAIL);
				}
			}

			@Override
			public void onRewardClick(Question item) {
				if (LocalUser.getUser().isLogin()) {
					RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
				} else {
					stopQuestionVoice();
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void onPlayClick(final Question item, int position) {
				umengEventCount(UmengCountEventId.MISS_TALK_VOICE);
				mPlayIngItem = item;//记录播放的item
				toggleQuestionVoice(item);
			}
		});
	}

	private void toggleQuestionVoice(Question item) {
		if (MissAudioManager.get().isPlaying(item.getAnswerContext(), item.getId())) {
			MissAudioManager.get().pause();
			mQuestionListAdapter.notifyDataSetChanged();
			stopScheduleJob();
		} else if (MissAudioManager.get().isPaused(item.getAnswerContext(), item.getId())) {
			MissAudioManager.get().resume();
			mQuestionListAdapter.notifyDataSetChanged();
			startScheduleJob(100);
		} else {
			updateQuestionListenCount(item);
			MissAudioManager.get().play(item.getAnswerContext(), item.getId());
			mQuestionListAdapter.notifyDataSetChanged();
			MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
				@Override
				public void onCompleted(String url) {
					mMissFloatWindow.setVisibility(View.GONE);
					mMissFloatWindow.stopAnim();
					mQuestionListAdapter.notifyDataSetChanged();
					stopScheduleJob();
				}
			});
			startScheduleJob(100);
		}
	}

	@Override
	public void onTimeUp(int count) {
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int lastVisiblePosition = mListView.getLastVisiblePosition();
		for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
			// Skip header && footer
			if (i == 0 || i - 1 >= mQuestionListAdapter.getCount()) continue;
			Question question = mQuestionListAdapter.getItem(i - 1);
			if (question != null && MissAudioManager.get().isPlaying(question.getAnswerContext(), question.getId())) {
				View view = mListView.getChildAt(i - firstVisiblePosition);
				ImageView playImage = (ImageView) view.findViewById(R.id.playImage);
				TextView soundTime = (TextView) view.findViewById(R.id.soundTime);
				ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
				playImage.setImageResource(R.drawable.ic_pause);
				progressBar.setMax(question.getSoundTime() * 1000);
				int pastTime = MissAudioManager.get().getCurrentPosition();
				soundTime.setText(getString(R.string._seconds, (question.getSoundTime() * 1000 - pastTime) / 1000));
				progressBar.setProgress(pastTime);
			}
		}
	}

	private void updateQuestionListenCount(final Question item) {
		if (!MissVoiceRecorder.isHeard(item.getId())) {
			Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
				@Override
				protected void onRespSuccess(Resp<JsonPrimitive> resp) {
					if (resp.isSuccess()) {
						MissVoiceRecorder.markHeard(item.getId());
						item.setListenCount(item.getListenCount() + 1);
						mQuestionListAdapter.notifyDataSetChanged();
					}
				}
			}).fire();
		}
	}

    @Override
	public void onPause() {
		super.onPause();
		stopScheduleJob();
	}

	public void stopQuestionVoice() {
		MissAudioManager.get().stop();

		if (mMissFloatWindow != null) {
			mMissFloatWindow.setVisibility(View.GONE);
			mMissFloatWindow.stopAnim();
		}

		if (mQuestionListAdapter != null) {
			mQuestionListAdapter.notifyDataSetChanged();
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mCreateTime = null;
				mSwipeRefreshLayout.setLoadMoreEnable(true);
				mListView.removeFooterView(mFootView);
				mFootView = null;
				requestMissList();
				requestHotQuestionList(true);
			}
		});

		mSwipeRefreshLayout.setOnLoadMoreListener(new VerticalSwipeRefreshLayout.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				mListView.postDelayed(new Runnable() {
					@Override
					public void run() {
						requestLatestQuestionList(false);
					}
				}, 1000);
			}
		});

		mSwipeRefreshLayout.setOnScrollListener(new CustomSwipeRefreshLayout.OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				int firstVisiblePosition = mListView.getFirstVisiblePosition();
				int lastVisiblePosition = mListView.getLastVisiblePosition();

				for (int i = 0; i <= mQuestionListAdapter.getCount(); i++) {
					if (i == 0 || i - 1 >= mQuestionListAdapter.getCount()) continue;
					Question question = mQuestionListAdapter.getItem(i - 1);
					if (question != null && MissAudioManager.get().isPlaying(question.getAnswerContext(), question.getId())) {
						if (i < firstVisiblePosition || i > lastVisiblePosition) {
							mMissFloatWindow.setVisibility(View.VISIBLE);
							mMissFloatWindow.setMissAvatar(question.getCustomPortrait());
							mMissFloatWindow.startAnim();
							break;
						} else {
							mMissFloatWindow.setVisibility(View.GONE);
							mMissFloatWindow.stopAnim();
						}
					} else {
						mMissFloatWindow.setVisibility(View.GONE);
						mMissFloatWindow.stopAnim();
					}
				}

				mMissFloatWindow.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
						intent.putExtra(Launcher.EX_PAYLOAD, mPlayIngItem.getId());
						intent.putExtra(ExtraKeys.IS_FROM_MISS_TALK, true);
						startActivityForResult(intent, REQ_QUESTION_DETAIL);
						umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
					}
				});
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int lastVisiblePosition = mListView.getLastVisiblePosition();
		for (int i = 0; i <= mQuestionListAdapter.getCount(); i++) {
			if (i == 0 || i - 1 >= mQuestionListAdapter.getCount()) continue;
			Question question = mQuestionListAdapter.getItem(i - 1);
			if (question != null && MissAudioManager.get().isPlaying(question.getAnswerContext(), question.getId())) {
				startScheduleJob(100);
				MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
					@Override
					public void onCompleted(String url) {
						mMissFloatWindow.setVisibility(View.GONE);
						mMissFloatWindow.stopAnim();
						mQuestionListAdapter.notifyDataSetChanged();
						stopScheduleJob();
					}
				});

				if (i < firstVisiblePosition || i > lastVisiblePosition) {
					mMissFloatWindow.setVisibility(View.VISIBLE);
					mMissFloatWindow.setMissAvatar(question.getCustomPortrait());
					mMissFloatWindow.startAnim();
					break;
				} else {
					mMissFloatWindow.setVisibility(View.GONE);
					mMissFloatWindow.stopAnim();
				}
			} else {
				mMissFloatWindow.setVisibility(View.GONE);
				mMissFloatWindow.stopAnim();
			}
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (!isVisibleToUser) {
			//四个item左右切换时,停止语音
			stopQuestionVoice();
			Preference.get().setMissTalkIsVisible(false);
			Log.d("xxx", Preference.get().missTalkIsVisible() + "");
		} else {
			Preference.get().setMissTalkIsVisible(true);
			Log.d("xxx", Preference.get().missTalkIsVisible() + "");
		}
	}

	private void requestMissList() {
		Client.getMissList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
					@Override
					protected void onRespSuccessData(List<Miss> missList) {
						updateMissList(missList);
					}
				}).fireFree();
	}

	private void requestHotQuestionList(final boolean isRefreshing) {
		Client.getHotQuestionList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						if (!questionList.isEmpty()) {
							Question question = questionList.get(0);
							question.setType(Question.TYPE_HOT);
						}
						mHotQuestionList = questionList;
					}

					@Override
					public void onFinish() {
						super.onFinish();
						requestLatestQuestionList(isRefreshing);
					}
				}).fireFree();
	}

	private void requestLatestQuestionList(final boolean isRefreshing) {
		Client.getLatestQuestionList(mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						if (!questionList.isEmpty() && isRefreshing) {
							Question question = questionList.get(0);
							question.setType(Question.TYPE_LATEST);
						}
						mLatestQuestionList = questionList;
						updateLatestQuestionList(questionList, isRefreshing);
					}

					@Override
					public void onFinish() {
						super.onFinish();
						stopRefreshAnimation();
					}
				}).fire();
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}

		if (mSwipeRefreshLayout.isLoading()) {
			mSwipeRefreshLayout.setLoading(false);
		}
	}

	private void updateMissList(List<Miss> missList) {
		mMissListAdapter.clear();
		mMissListAdapter.addAll(missList);
	}

	private void updateHotQuestionList(List<Question> questionList) {
		mQuestionListAdapter.clear();
		mQuestionListAdapter.addAll(questionList);
	}

	private void updateLatestQuestionList(List<Question> questionList, boolean isRefreshing) {
		if (isRefreshing) {
			updateHotQuestionList(mHotQuestionList);
		}

		if (questionList.size() < mPageSize) {
			mSwipeRefreshLayout.setLoadMoreEnable(false);
		} else {
			mCreateTime = mLatestQuestionList.get(mLatestQuestionList.size() - 1).getCreateTime();
		}

		if (questionList.size() < mPageSize && mCreateTime != null) {
			if (mFootView == null) {
				mFootView = View.inflate(getActivity(), R.layout.view_footer_load_complete, null);
				mListView.addFooterView(mFootView, null, true);
			}
		}

		for (Question question : questionList) {
			if (mSet.add(question.getId())) {
				mQuestionListAdapter.add(question);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRefreshReceiver);

        MissAudioManager.get().removeAudioView(this);
	}

    @Override
    public void onAudioStart() {
        Log.d(TAG, "onAudioStart: ");
    }

    @Override
    public void onAudioPlay() {
        Log.d(TAG, "onAudioPlay: ");
    }

    @Override
    public void onAudioPause() {
        Log.d(TAG, "onAudioPause: ");
    }

    @Override
    public void onAudioResume() {
        Log.d(TAG, "onAudioResume: ");
    }

    @Override
    public void onAudioStop() {
        Log.d(TAG, "onAudioStop: ");
    }

    public static class MissListAdapter extends RecyclerView.Adapter<MissListAdapter.ViewHolder> {

		private OnItemClickListener mOnItemClickListener;

		public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
			this.mOnItemClickListener = onItemClickListener;
		}

		public interface OnItemClickListener {
			void onItemClick(Miss miss);
		}

		private List<Miss> mMissList;
		private Context mContext;
		private LayoutInflater mLayoutInflater;

		public MissListAdapter(Context context, List<Miss> missList) {
			this.mMissList = missList;
			this.mContext = context;
			mLayoutInflater = LayoutInflater.from(context);
		}

		public void clear() {
			mMissList.clear();
			notifyItemRangeRemoved(0, mMissList.size());
		}

		public void addAll(List<Miss> missList) {
			mMissList.addAll(missList);
			notifyDataSetChanged();
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = mLayoutInflater.inflate(R.layout.row_misstalk_list, parent, false);
			return new ViewHolder(view);
		}

		@Override
		public void onBindViewHolder(ViewHolder holder, int position) {
			holder.bindDataWithView(mContext, mMissList.get(position), mOnItemClickListener);
		}

		@Override
		public int getItemCount() {
			return mMissList != null ? mMissList.size() : 0;
		}

		public class ViewHolder extends RecyclerView.ViewHolder {

			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;

			public ViewHolder(View itemView) {
				super(itemView);
				ButterKnife.bind(this, itemView);
			}

			public void bindDataWithView(Context context, final Miss item, final OnItemClickListener onItemClickListener) {
				if (item == null) return;
				GlideApp.with(context).load(item.getPortrait())
						.placeholder(R.drawable.ic_default_avatar_big)
						.circleCrop()
						.into(mAvatar);
				mName.setText(item.getName());

				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (onItemClickListener != null) {
							onItemClickListener.onItemClick(item);
						}
					}
				});
			}
		}
	}

	static class QuestionListAdapter extends ArrayAdapter<Question> {

		private QuestionListAdapter(@NonNull Context context) {
			super(context, 0);
		}

		private Callback mCallback;

		public interface Callback {
			void onPraiseClick(Question item);

			void onCommentClick(Question item);

			void onRewardClick(Question item);

			void onPlayClick(Question item, int position);
		}

		private void setCallback(Callback callback) {
			mCallback = callback;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_misstalk_answer, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(getContext(), getItem(position), mCallback, position);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.label)
			TextView mLabel;
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;
			@BindView(R.id.askTime)
			TextView mAskTime;
			@BindView(R.id.question)
			TextView mQuestion;
			@BindView(R.id.missAvatar)
			ImageView mMissAvatar;
			@BindView(soundTime)
			TextView mSoundTime;
			@BindView(listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.praiseNumber)
			TextView mPraiseNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;
			@BindView(R.id.playImage)
			ImageView mPlayImage;
			@BindView(R.id.progressBar)
			ProgressBar mProgressBar;
			@BindView(R.id.split)
			View mSplit;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Question item, final Callback callback, final int position) {
				if (item.getType() == Question.TYPE_HOT) {
					mSplit.setVisibility(View.GONE);
					mLabel.setVisibility(View.VISIBLE);
					mLabel.setText(R.string.hot_question);
					mLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_hot, 0, 0, 0);
				} else if (item.getType() == Question.TYPE_LATEST) {
					mSplit.setVisibility(View.GONE);
					mLabel.setVisibility(View.VISIBLE);
					mLabel.setText(R.string.latest_question);
					mLabel.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_latest, 0, 0, 0);
				} else {
					mLabel.setVisibility(View.GONE);
					mSplit.setVisibility(View.VISIBLE);
				}

				GlideApp.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.circleCrop()
						.into(mAvatar);

				GlideApp.with(context).load(item.getCustomPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.circleCrop()
						.into(mMissAvatar);

				mName.setText(item.getUserName());
				mAskTime.setText(DateUtil.getMissFormatTime(item.getCreateTime()));
				mQuestion.setText(item.getQuestionContext());
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));

				if (MissVoiceRecorder.isHeard(item.getId())) {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
				} else {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
				}

				if (item.getIsPrise() == 0) {
					mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
				} else {
					mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
				}

				if (item.getPriseCount() == 0) {
					mPraiseNumber.setText(context.getString(R.string.praise));
				} else {
					mPraiseNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
				}

				if (item.getReplyCount() == 0) {
					mCommentNumber.setText(context.getString(R.string.comment));
				} else {
					mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
				}

				if (item.getAwardCount() == 0) {
					mIngotNumber.setText(context.getString(R.string.reward));
				} else {
					mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));
				}

				mPraiseNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onPraiseClick(item);
						}
					}
				});

				mCommentNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onCommentClick(item);
						}
					}
				});

				mIngotNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onRewardClick(item);
						}
					}
				});

				mPlayImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onPlayClick(item, position);
						}
					}
				});

				mProgressBar.setMax(item.getSoundTime() * 1000);
				if (MissAudioManager.get().isPlaying(item.getAnswerContext(), item.getId())) {
					mPlayImage.setImageResource(R.drawable.ic_pause);
					int pastTime = MissAudioManager.get().getCurrentPosition();
					mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - pastTime) / 1000));
					mProgressBar.setProgress(pastTime);
				} else if (MissAudioManager.get().isPaused(item.getAnswerContext(), item.getId())) {
					mPlayImage.setImageResource(R.drawable.ic_play);
					int pastTime = MissAudioManager.get().getCurrentPosition();
					mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - pastTime) / 1000));
					mProgressBar.setProgress(pastTime);
				} else {
					mPlayImage.setImageResource(R.drawable.ic_play);
					mProgressBar.setProgress(0);
					mSoundTime.setText(context.getString(R.string._seconds, item.getSoundTime()));
				}
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		if (requestCode == REQ_LOGIN && resultCode == RESULT_OK) {
			Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
		}

		if (requestCode == REQ_COMMENT && resultCode == RESULT_OK) {
			if (data != null) {
				int id = data.getIntExtra(ExtraKeys.QUESTION_ID, -1);
				for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
					Question item = mQuestionListAdapter.getItem(i);
					if (item != null && id == item.getId()) {
						item.setReplyCount(1);
						mQuestionListAdapter.notifyDataSetChanged();
					}
				}
			}
		}

		if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
			if (data != null) {
				Question question = data.getParcelableExtra(ExtraKeys.QUESTION);
				if (question != null) {
					if (MissAudioManager.get().isPlaying(question.getAnswerContext(), question.getId())) {
						mPlayIngItem = question;
					}

					for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
						Question item = mQuestionListAdapter.getItem(i);
						if (item != null && question.getId() == item.getId()) {
							item.setIsPrise(question.getIsPrise());
							item.setPriseCount(question.getPriseCount());
							item.setReplyCount(question.getReplyCount());
							item.setAwardCount(question.getAwardCount());
							item.setListenCount(question.getListenCount());
						}

						if (item != null) {
							if (MissAudioManager.get().isPlaying(item.getAnswerContext(), item.getId())) {
								startScheduleJob(100);
								MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
									@Override
									public void onCompleted(String url) {
										mMissFloatWindow.setVisibility(View.GONE);
										mMissFloatWindow.stopAnim();
										mQuestionListAdapter.notifyDataSetChanged();
										stopScheduleJob();
									}
								});
							}
						}
					}

					mQuestionListAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void registerRefreshReceiver() {
		mRefreshReceiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REWARD_SUCCESS);
		filter.addAction(ACTION_LOGIN_SUCCESS);
		filter.addAction(ACTION_LOGOUT_SUCCESS);
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRefreshReceiver, filter);
	}

	private class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {

					for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
						Question question = mQuestionListAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
								int questionRewardCount = question.getAwardCount() + 1;
								question.setAwardCount(questionRewardCount);
								mQuestionListAdapter.notifyDataSetChanged();
							}
						}
					}
				}
			}

			if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())
					|| ACTION_LOGOUT_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				mSet.clear();
				mCreateTime = null;
				mListView.removeFooterView(mFootView);
				mFootView = null;
				requestMissList();
				requestHotQuestionList(true);
			}
		}
	}
}
