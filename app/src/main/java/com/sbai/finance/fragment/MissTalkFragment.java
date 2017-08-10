package com.sbai.finance.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.MessagesActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.MyQuestionsActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.activity.train.TrainDetailActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.NewMessage;
import com.sbai.finance.model.missTalk.Miss;
import com.sbai.finance.model.missTalk.Prise;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.mediaPlayerUtil;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.MyListView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.R.id.missAvatar;
import static com.sbai.finance.R.id.recyclerView;

public class MissTalkFragment extends BaseFragment implements View.OnClickListener {

	private static final int SUBMIT_QUESTION = 1001;
	private static final int MY_QUESTION = 1002;
	private static final int MESSAGE = 1003;

	@BindView(R.id.more)
	ImageView mMore;
	@BindView(R.id.message)
	ImageView mMessage;
	@BindView(R.id.redPoint)
	ImageView mRedPoint;
	Unbinder unbinder;
	@BindView(R.id.hotListView)
	MyListView mHotListView;
	@BindView(R.id.LatestListView)
	MyListView mLatestListView;
	@BindView(recyclerView)
	EmptyRecyclerView mRecyclerView;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;
	@BindView(R.id.hotQuestion)
	TextView mHotQuestion;
	@BindView(R.id.empty)
	TextView mEmpty;
	@BindView(R.id.missEmpty)
	TextView mMissEmpty;
	@BindView(R.id.titleBar)
	RelativeLayout mTitleBar;

	private List<Miss> mMissList;
	private List<Question> mLatestQuestionList;
	private MissListAdapter mMissListAdapter;
	private HotQuestionListAdapter mHotQuestionListAdapter;
	private LatestQuestionListAdapter mLatestQuestionListAdapter;
	private Long mCreateTime;
	private int mPageSize = 20;
	private HashSet<Integer> mSet;
	private View mFootView;
	private PopupWindow mPopupWindow;

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
		initPopupWindow();
		mSet = new HashSet<>();

		initMissList();
		initHotQuestionList();
		initLatestQuestionList();

		requestMissList();
		requestLatestQuestionList();
		initSwipeRefreshLayout();
	}

	private void initPopupWindow() {
		View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.view_popup_window, null);
		mPopupWindow = new PopupWindow(contentView);
		mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
		mPopupWindow.setClippingEnabled(true);
		TextView tv1 = (TextView) contentView.findViewById(R.id.askHerQuestion);
		TextView tv2 = (TextView) contentView.findViewById(R.id.myQuestion);

		tv1.setOnClickListener(this);
		tv2.setOnClickListener(this);
	}

	private void initMissList() {
		mMissList = new ArrayList<>();
		mMissListAdapter = new MissListAdapter(getActivity(), mMissList);
		GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
		gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
		mRecyclerView.setLayoutManager(gridLayoutManager);
		mRecyclerView.setEmptyView(mMissEmpty);
		mRecyclerView.setAdapter(mMissListAdapter);
		mMissListAdapter.setOnItemClickListener(new MissListAdapter.OnItemClickListener() {
			@Override
			public void onItemClick(Miss item) {
				if (item != null) {
					Launcher.with(getActivity(), MissProfileActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
				}
			}
		});
	}

	private void initHotQuestionList() {
		mHotQuestionListAdapter = new HotQuestionListAdapter(getActivity(), TAG);
		mHotListView.setFocusable(false);
		mHotListView.setAdapter(mHotQuestionListAdapter);
		mHotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Question item = (Question) parent.getItemAtPosition(position);
				if (item != null) {
					Launcher.with(getActivity(), QuestionDetailActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
				}
			}
		});
	}

	private void initLatestQuestionList() {
		mLatestQuestionListAdapter = new LatestQuestionListAdapter(getActivity(), TAG);
		mLatestListView.setFocusable(false);
		mLatestListView.setEmptyView(mEmpty);
		mLatestListView.setAdapter(mLatestQuestionListAdapter);
		mLatestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Question item = (Question) parent.getItemAtPosition(position);
				if (item != null) {
					Launcher.with(getActivity(), QuestionDetailActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
				}
			}
		});
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mCreateTime = null;
				requestMissList();
				requestLatestQuestionList();
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if (LocalUser.getUser().isLogin()) {
			requestNewMessageCount();
			startScheduleJob(10 * 1000);
		} else {
			mRedPoint.setVisibility(View.GONE);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopScheduleJob();
		mediaPlayerUtil.release();
	}

	@Override
	public void onTimeUp(int count) {
		super.onTimeUp(count);
		if (LocalUser.getUser().isLogin()) {
			requestNewMessageCount();
		}
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (!isVisibleToUser) {
			mediaPlayerUtil.release();
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayerUtil.release();
	}

	private void requestNewMessageCount() {
		Client.getNewMessageCount().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<NewMessage>>, List<NewMessage>>(false) {

					@Override
					protected void onRespSuccessData(List<NewMessage> newMessagesList) {
						int count = 0;
						for (NewMessage newMessage : newMessagesList) {
							if (newMessage.getClassify() == 4) {
								count += newMessage.getCount();
							}
						}

						if (count > 0) {
							mRedPoint.setVisibility(View.VISIBLE);
						}
					}
				}).fire();

	}

	private void requestMissList() {
		Client.getMissList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
					@Override
					protected void onRespSuccessData(List<Miss> missList) {
						updateMissList(missList);
					}
				}).fire();
	}


	private void requestHotQuestionList() {
		Client.getHotQuestionList().setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						/*Collections.sort(questionList, new Comparator<Question>() {
							@Override
							public int compare(Question o2, Question o1) {
								return Long.valueOf(o1.getCreateTime() - o2.getCreateTime()).intValue();
							}
						});*/
						updateHotQuestionList(questionList);
					}

					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
					}
				}).fire();
	}

	private void requestLatestQuestionList() {
		Client.getLatestQuestionList(mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						mLatestQuestionList = questionList;
						updateLatestQuestionList(questionList);

						if (questionList.size() >= 20) {
							mHotQuestion.setVisibility(View.VISIBLE);
							requestHotQuestionList();
						}
					}

					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
					}
				}).fire();
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	private void updateMissList(List<Miss> missList) {
		mMissListAdapter.clear();
		mMissListAdapter.addAll(missList);
	}

	private void updateHotQuestionList(List<Question> questionList) {
		mHotQuestionListAdapter.clear();
		mHotQuestionListAdapter.addAll(questionList);
	}

	private void updateLatestQuestionList(List<Question> questionList) {

		if (questionList == null) {
			stopRefreshAnimation();
			return;
		}

		if (mFootView == null) {
			mFootView = View.inflate(getActivity(), R.layout.view_footer_load_more, null);
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mCreateTime = mLatestQuestionList.get(mLatestQuestionList.size() - 1).getCreateTime();
					requestLatestQuestionList();
				}
			});
			mLatestListView.addFooterView(mFootView, null, true);
		}

		if (questionList.size() < mPageSize) {
			mLatestListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mLatestQuestionListAdapter != null) {
				mLatestQuestionListAdapter.clear();
				mLatestQuestionListAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (Question question : questionList) {
			if (mSet.add(question.getId())) {
				mLatestQuestionListAdapter.add(question);
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
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

				Glide.with(context).load(item.getPortrait())
						.placeholder(R.drawable.ic_default_avatar_big)
						.transform(new GlideCircleTransform(context))
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

	static class HotQuestionListAdapter extends ArrayAdapter<Question> {

		private Context mContext;
		private String TAG;

		private HotQuestionListAdapter(@NonNull Context context, String TAG) {
			super(context, 0);
			this.mContext = context;
			this.TAG = TAG;
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
			viewHolder.bindingData(mContext, getItem(position), position, TAG);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;
			@BindView(R.id.askTime)
			TextView mAskTime;
			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.question)
			TextView mQuestion;
			@BindView(missAvatar)
			ImageView mMissAvatar;
			@BindView(R.id.voice)
			TextView mVoice;
			@BindView(R.id.listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.loveNumber)
			TextView mLoveNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;
			@BindView(R.id.split)
			View mSplit;
			@BindView(R.id.voiceLevel)
			View mVoiceLevel;
			@BindView(R.id.voiceArea)
			LinearLayout mVoiceArea;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Question item,
			                        final int position, final String TAG) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				Glide.with(context).load(item.getCustomPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mMissAvatar);

				mName.setText(item.getUserName());
				mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
				mQuestion.setText(item.getQuestionContext());
				mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
				mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
				mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
				mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

				if (MissVoiceRecorder.isHeard(item.getId())) {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
				} else {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
				}

				if (item.getIsPrise() == 0) {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
				} else {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
				}

				mMissAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(context, MissProfileActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, item.getAnswerCustomId())
								.execute();
					}
				});

				mLoveNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (LocalUser.getUser().isLogin()) {
							Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

								@Override
								protected void onRespSuccessData(Prise prise) {
									if (prise.getIsPrise() == 0) {
										mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
									} else {
										mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
									}
									mLoveNumber.setText(StrFormatter.getFormatCount(prise.getPriseCount()));
								}
							}).fire();
						} else {
							Launcher.with(context, LoginActivity.class).execute();
						}
					}
				});

				mVoiceArea.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						//播放动画
						mVoiceLevel.setBackgroundResource(R.drawable.bg_play_voice);
						AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
						animation.start();

						if (!MissVoiceRecorder.isHeard(item.getId())) {
							//没听过
							Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
								@Override
								protected void onRespSuccess(Resp<JsonPrimitive> resp) {
									if (resp.isSuccess()) {
										if (mediaPlayerUtil.isPlaying()) {
											mediaPlayerUtil.release();
											mVoiceLevel.clearAnimation();
											mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
										} else {
											mediaPlayerUtil.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
												@Override
												public void onCompletion(MediaPlayer mp) {
													mVoiceLevel.clearAnimation();
													mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
												}
											});

											MissVoiceRecorder.markHeard(item.getId());
											item.setListenCount(item.getListenCount() + 1);
											mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
											mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
										}
									}
								}
							}).fire();
						} else {
							//听过
							if (mediaPlayerUtil.isPlaying()) {
								mediaPlayerUtil.release();
								mVoiceLevel.clearAnimation();
								mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
							} else {
								mediaPlayerUtil.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										mVoiceLevel.clearAnimation();
										mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
									}
								});
							}
						}
					}
				});
			}
		}
	}

	static class LatestQuestionListAdapter extends ArrayAdapter<Question> {

		private Context mContext;
		private String TAG;

		private LatestQuestionListAdapter(@NonNull Context context, String TAG) {
			super(context, 0);
			this.mContext = context;
			this.TAG = TAG;
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
			viewHolder.bindingData(mContext, getItem(position), position, TAG);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.name)
			TextView mName;
			@BindView(R.id.askTime)
			TextView mAskTime;
			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.question)
			TextView mQuestion;
			@BindView(missAvatar)
			ImageView mMissAvatar;
			@BindView(R.id.voice)
			TextView mVoice;
			@BindView(R.id.listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.loveNumber)
			TextView mLoveNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;
			@BindView(R.id.split)
			View mSplit;
			@BindView(R.id.voiceLevel)
			View mVoiceLevel;
			@BindView(R.id.voiceArea)
			LinearLayout mVoiceArea;


			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Question item,
			                        int position, final String TAG) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				Glide.with(context).load(item.getCustomPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mMissAvatar);

				mName.setText(item.getUserName());
				mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
				mQuestion.setText(item.getQuestionContext());
				mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
				mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
				mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
				mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

				if (MissVoiceRecorder.isHeard(item.getId())) {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
				} else {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
				}

				if (item.getIsPrise() == 0) {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
				} else {
					mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
				}

				mMissAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(context, MissProfileActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, item.getAnswerCustomId())
								.execute();
					}
				});

				mLoveNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (LocalUser.getUser().isLogin()) {
							Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

								@Override
								protected void onRespSuccessData(Prise prise) {
									if (prise.getIsPrise() == 0) {
										mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
									} else {
										mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
									}
									mLoveNumber.setText(StrFormatter.getFormatCount(prise.getPriseCount()));
								}
							}).fire();
						} else {
							Launcher.with(context, LoginActivity.class).execute();
						}
					}
				});

				mVoiceArea.setOnClickListener(new View.OnClickListener() {

					@Override
					public void onClick(View v) {
						//加动画
						mVoiceLevel.setBackgroundResource(R.drawable.bg_play_voice);
						AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
						animation.start();

						if (!MissVoiceRecorder.isHeard(item.getId())) {
							//没听过
							Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
								@Override
								protected void onRespSuccess(Resp<JsonPrimitive> resp) {
									if (resp.isSuccess()) {
										if (mediaPlayerUtil.isPlaying()) {
											mediaPlayerUtil.release();
											mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
										} else {
											mediaPlayerUtil.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
												@Override
												public void onCompletion(MediaPlayer mp) {
													mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
												}
											});


											MissVoiceRecorder.markHeard(item.getId());
											item.setListenCount(item.getListenCount() + 1);
											mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
											mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
										}
									}
								}
							}).fire();
						} else {
							//听过
							if (mediaPlayerUtil.isPlaying()) {
								mediaPlayerUtil.release();
								mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
							} else {
								mediaPlayerUtil.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
									}
								});
							}
						}
					}
				});
			}
		}
	}


	@OnClick({R.id.more, R.id.message, R.id.titleBar})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.more:
				//showPopupWindow();
				//Launcher.with(getActivity(), TrainExperienceActivity.class).execute();
				Launcher.with(getActivity(), TrainDetailActivity.class).execute();
				break;
			case R.id.message:
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), MessagesActivity.class).execute();
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(intent, MESSAGE);
				}
				mRedPoint.setVisibility(View.INVISIBLE);
				break;
			case R.id.titleBar:
				mScrollView.smoothScrollTo(0, 0);
				break;
		}
	}

	private void showPopupWindow() {
		if (mPopupWindow != null) {
			if (!mPopupWindow.isShowing()) {
				mPopupWindow.showAsDropDown(mMore, -30, 50);
			} else {
				mPopupWindow.dismiss();
			}
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.askHerQuestion: {
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}

				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(intent, SUBMIT_QUESTION);
				}
			}
			break;
			case R.id.myQuestion: {
				if (mPopupWindow.isShowing()) {
					mPopupWindow.dismiss();
				}

				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), MyQuestionsActivity.class).execute();
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(intent, MY_QUESTION);
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

		if (requestCode == MY_QUESTION && resultCode == RESULT_OK) {
			Launcher.with(getActivity(), MyQuestionsActivity.class).execute();
		}

		if (requestCode == MESSAGE && resultCode == RESULT_OK) {
			Launcher.with(getActivity(), MessagesActivity.class).execute();
		}
	}
}
