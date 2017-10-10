package com.sbai.finance.fragment;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sbai.finance.utils.MediaPlayerManager;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.AUDIO_SERVICE;
import static com.sbai.finance.R.id.listenerNumber;
import static com.sbai.finance.R.id.playImage;
import static com.sbai.finance.R.id.progressBar;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGOUT_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_PLAY_FINISH;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

public class MissTalkFragment extends BaseFragment {

	private static final int SUBMIT_QUESTION = 1001;

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

	private List<Question> mLatestQuestionList;
	private MissListAdapter mMissListAdapter;
	private QuestionListAdapter mQuestionListAdapter;
	private Long mCreateTime;
	private int mPageSize = 20;
	private HashSet<Integer> mSet;
	private RefreshReceiver mRefreshReceiver;
	private AudioManager mAudioManager;
	private View mFootView;
	private int mCurrentPosition;

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

		mSet = new HashSet<>();
		mAudioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
		mListView.setEmptyView(mEmpty);
	}

	private void initTitleBar() {
		mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(intent, SUBMIT_QUESTION);
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
					Launcher.with(getActivity(), MissProfileActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
				}
			}
		});
		mListView.addHeaderView(header);
	}

	private void initListView() {
		mQuestionListAdapter = new QuestionListAdapter(getActivity());
		mListView.setAdapter(mQuestionListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Question item = (Question) parent.getItemAtPosition(position);
				if (item != null) {
					Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
					intent.putExtra(ExtraKeys.QUESTION, item);
					startActivityForResult(intent, REQ_QUESTION_DETAIL);
					umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
				}
			}
		});

		mSwipeRefreshLayout.setOnScrollListener(new CustomSwipeRefreshLayout.OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					if (mCurrentPosition < firstVisibleItem || mCurrentPosition > mListView.getLastVisiblePosition()) {
                        mMissFloatWindow.setVisibility(View.VISIBLE);
					} else {
                        mMissFloatWindow.setVisibility(View.GONE);
					}
				} else {
                    mMissFloatWindow.setVisibility(View.GONE);
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
						startActivity(intent);
					} else {
						Launcher.with(getActivity(), LoginActivity.class).execute();
					}
				} else {
					Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
					intent.putExtra(ExtraKeys.QUESTION, item);
					startActivityForResult(intent, REQ_QUESTION_DETAIL);
				}
			}

			@Override
			public void onRewardClick(Question item) {
				if (LocalUser.getUser().isLogin()) {
					RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void onPlayClick(final Question item, int position) {
				umengEventCount(UmengCountEventId.MISS_TALK_VOICE);

                mMissFloatWindow.setMissAvatar(item.getCustomPortrait());
                mMissFloatWindow.startAnim();

				mCurrentPosition = position + 1;//拿到当前播放的位置

                mMissFloatWindow.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
						intent.putExtra(ExtraKeys.QUESTION, item);
						startActivityForResult(intent, REQ_QUESTION_DETAIL);
						umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
					}
				});

                toggleQuestionVoice(item);

//				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_STOP) {
//					//结束状态直接开始播放
//					playVoice(item);
//				} else {
//					if (MediaPlayerManager.playingId == item.getId()) {
//						if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PAUSE) {
//							MediaPlayerManager.resume();
//							mQuestionListAdapter.notifyDataSetChanged();
//						} else {
//							MediaPlayerManager.pause();
//							mQuestionListAdapter.notifyDataSetChanged();
//						}
//					} else {
//						stopPreviousVoice();
//						//关闭上一个语音,开始这个
//						playVoice(item);
//					}
//				}

			}
		});
	}

    private void toggleQuestionVoice(Question item) {
        if (MissAudioManager.get().isPlaying(item.getAnswerContext())) {
            MissAudioManager.get().pause();
            mQuestionListAdapter.notifyDataSetChanged();
        } else if (MissAudioManager.get().isPaused(item.getAnswerContext())) {
            MissAudioManager.get().resume();
            mQuestionListAdapter.notifyDataSetChanged();
        } else {
            updateQuestionListenCount(item);
            MissAudioManager.get().play(item.getAnswerContext());
            MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
                @Override
                public void onCompleted(String url) {
                    mMissFloatWindow.setVisibility(View.GONE);
                    sendPlayFinishBroadcast();
                }
            });
            mQuestionListAdapter.notifyDataSetChanged();
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
                    }
                }
            }).fire();
            mQuestionListAdapter.notifyDataSetChanged();
        }
    }

//    private void playVoice(final Question item) {
//		MediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnPreparedListener() {
//			@Override
//			public void onPrepared(MediaPlayer mp) {
//				int result = mAudioManager.requestAudioFocus(afChangeListener,
//						AudioManager.STREAM_MUSIC,
//						AudioManager.AUDIOFOCUS_GAIN);
//				if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
//					//获取焦点之后开始播放,避免音轨并发
//					MediaPlayerManager.start();
//					MediaPlayerManager.setPlayingId(item.getId());
//					MediaPlayerManager.setPortrait(item.getCustomPortrait());
//					mQuestionListAdapter.notifyDataSetChanged();
//				}
//			}
//		}, new MediaPlayer.OnCompletionListener() {
//			@Override
//			public void onCompletion(MediaPlayer mp) {
//				MediaPlayerManager.release();
//				mQuestionListAdapter.notifyDataSetChanged();
//				mAudioManager.abandonAudioFocus(afChangeListener);
//				//播放结束之后发个广播给问题详情页刷新界面
//			}
//		});
//	}

	private void sendPlayFinishBroadcast() {
		LocalBroadcastManager.getInstance(getActivity())
				.sendBroadcast(new Intent(ACTION_PLAY_FINISH));
	}

	public AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.pause();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (MediaPlayerManager.STATUS != MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.start();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.release();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.release();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.release();
				}
			}
		}
	};

	public void stopPreviousVoice() {
		MediaPlayerManager.release();

		if (mMissFloatWindow!= null) {
            mMissFloatWindow.setVisibility(View.GONE);
		}

		for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
			Question question = mQuestionListAdapter.getItem(i);
			if (question != null) {
				if (question.getId() == MediaPlayerManager.playingId) {
					mQuestionListAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				//下拉刷新时关闭语音播放
				MediaPlayerManager.release();
                mMissFloatWindow.setVisibility(View.GONE);

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
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (!isVisibleToUser) {
			//不可见时停止播放和动画
			//stopPreviousAnimation();
			MediaPlayerManager.release();
			if (mMissFloatWindow != null) {
                mMissFloatWindow.setVisibility(View.GONE);
			}
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
						updateHotQuestionList(questionList);
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
						updateLatestQuestionList(questionList);
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

	private void updateLatestQuestionList(List<Question> questionList) {
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
		mQuestionListAdapter.addAll(questionList);
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRefreshReceiver);
	}

	@OnClick(R.id.missFloatWindow)
	public void onViewClicked() {
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
			@BindView(R.id.hotQuestion)
			TextView mHotQuestion;

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
			@BindView(R.id.soundTime)
			TextView mSoundTime;
			@BindView(listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.praiseNumber)
			TextView mPraiseNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;

            @BindView(playImage)
			ImageView mPlayImage;
			@BindView(progressBar)
			ProgressBar mProgressBar;
			@BindView(R.id.split)
			View mSplit;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private Timer mTimer = new Timer();
			private TimerTask mTimerTask;

			public void bindingData(final Context context, final Question item, final Callback callback, final int position) {
				if (item.getType() == Question.TYPE_HOT) {
					mSplit.setVisibility(View.GONE);
					mHotQuestion.setVisibility(View.VISIBLE);
					mHotQuestion.setText(R.string.hot_question);
					mHotQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_hot, 0, 0, 0);
				} else if (item.getType() == Question.TYPE_LATEST) {
					mSplit.setVisibility(View.GONE);
					mHotQuestion.setVisibility(View.VISIBLE);
					mHotQuestion.setText(R.string.latest_question);
					mHotQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_latest, 0, 0, 0);
				} else {
					mHotQuestion.setVisibility(View.GONE);
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
				mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
				mQuestion.setText(item.getQuestionContext());
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
				mPraiseNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
				mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

				if (item.getReplyCount() == 0) {
					mCommentNumber.setText(context.getString(R.string.comment));
				} else {
					mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
				}

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

				mPraiseNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onPraiseClick(item);
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

                if (MissAudioManager.get().isPlaying(item.getAnswerContext())) {
                    mPlayImage.setImageResource(R.drawable.ic_pause);
                } else if (MissAudioManager.get().isPaused(item.getAnswerContext())) {
                    mPlayImage.setImageResource(R.drawable.ic_play);
//                    mProgressBar.setMax(item.getSoundTime() * 1000);
//                    mProgressBar.setProgress(MediaPlayerManager.getCurrentPosition());
//                    mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - MediaPlayerManager.getCurrentPosition()) / 1000));
                } else {
                    mPlayImage.setImageResource(R.drawable.ic_play);
                    mProgressBar.setMax(0);
                    mProgressBar.setProgress(0);
                    mSoundTime.setText(context.getString(R.string._seconds, item.getSoundTime()));
                }

//                if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING
//                        && MediaPlayerManager.playingId == item.getId()) {
//                    mPlayImage.setImageResource(R.drawable.ic_pause);
//                    startCountDownTime(mSoundTime, item.getSoundTime(), mProgressBar, activity);
//                } else if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PAUSE
//                        && MediaPlayerManager.playingId == item.getId()) {
//                    mPlayImage.setImageResource(R.drawable.ic_play);
//                    mProgressBar.setMax(item.getSoundTime() * 1000);
//                    mProgressBar.setProgress(MediaPlayerManager.getCurrentPosition());
//                    mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - MediaPlayerManager.getCurrentPosition()) / 1000));
//                    stopCountDownTime();
//                } else {
//                    mPlayImage.setImageResource(R.drawable.ic_play);
//                    mProgressBar.setMax(0);
//                    mProgressBar.setProgress(0);
//                    mSoundTime.setText(context.getString(R.string._seconds, item.getSoundTime()));
//                    stopCountDownTime();
//                }

            }

			private void startCountDownTime(final TextView sound, final int soundTime,
			                                final ProgressBar progressBar, final Activity activity) {
				progressBar.setMax(soundTime * 1000);
				mTimerTask = new TimerTask() {
					@Override
					public void run() {
						activity.runOnUiThread(new Runnable() {
							@Override
							public void run() {
								int position = MediaPlayerManager.getCurrentPosition();
								sound.setText(activity.getString(R.string._seconds, (soundTime * 1000 - position) / 1000));
								progressBar.setProgress(position);

							}
						});

					}
				};
				mTimer.schedule(mTimerTask, 0, 100);
			}

			private void stopCountDownTime() {
				if (mTimerTask != null) {
					mTimerTask.cancel();
					mTimerTask = null;
				}
			}
		}
	}


	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == SUBMIT_QUESTION && resultCode == RESULT_OK) {
			Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
		}

		if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
			if (data != null) {
				Praise prise = data.getParcelableExtra(Launcher.EX_PAYLOAD);
				int replyCount = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
				int rewardCount = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
				int listenCount = data.getIntExtra(Launcher.EX_PAYLOAD_3, -1);
				for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
					Question question = mQuestionListAdapter.getItem(i);
					if (question != null) {
						if (question.getId() == data.getIntExtra(ExtraKeys.QUESTION_ID, -1)) {
							if (prise != null) {
								question.setIsPrise(prise.getIsPrise());
								question.setPriseCount(prise.getPriseCount());
							}
							if (replyCount != -1) {
								question.setReplyCount(replyCount);
							}
							if (rewardCount != -1) {
								question.setAwardCount(rewardCount);
							}
							if (listenCount != -1) {
								question.setListenCount(listenCount);
							}
							mQuestionListAdapter.notifyDataSetChanged();
						}
					}
				}

				if (replyCount != -1) {

					for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
						Question question = mQuestionListAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == data.getIntExtra(ExtraKeys.QUESTION_ID, -1)) {

								mQuestionListAdapter.notifyDataSetChanged();
							}
						}
					}
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
