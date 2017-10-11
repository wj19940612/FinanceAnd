package com.sbai.finance.activity.miss;

import android.animation.ArgbEvaluator;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
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

import com.android.volley.VolleyError;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Attention;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MediaPlayerManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.R.id.playImage;

/**
 * 小姐姐详细资料页面
 */
public class MissProfileActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, View.OnClickListener {

	private static final int REQ_SUBMIT_QUESTION_LOGIN = 1001;
	private static final int REQ_MISS_REWARD_LOGIN = 1002;

	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.swipeRefreshLayout)
	CustomSwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.titleBar)
	TitleBar mTitleBar;

	private HerAnswerAdapter mHerAnswerAdapter;
	private Long mCreateTime;
	private int mPageSize = 20;
	private HashSet<Integer> mSet;
	private int mCustomId;
	private List<Question> mHerAnswerList;
	private Miss mMiss;
	private RefreshReceiver mRefreshReceiver;
	private ImageView mAvatar;
	private TextView mName;
	private TextView mVoice;
	private TextView mLovePeopleNumber;
	private TextView mIntroduce;
	private LinearLayout mAttention;
	private ImageView mAttentionImage;
	private TextView mAttentionNumber;
	private TextView mRewardNumber;
	private LinearLayout mVoiceIntroduce;
	private View mVoiceLevel;
	private TextView mAttentionText;
	private LinearLayout mReward;
	private LinearLayout mEmpty;
	private ArgbEvaluator mEvaluator;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miss_profile);
		ButterKnife.bind(this);

		translucentStatusBar();
		initData(getIntent());
		initHeaderView();

		mSet = new HashSet<>();
		mEvaluator = new ArgbEvaluator();
		mHerAnswerList = new ArrayList<>();
		mHerAnswerAdapter = new HerAnswerAdapter(this);
		mListView.setAdapter(mHerAnswerAdapter);
		mListView.setOnItemClickListener(this);
		mSwipeRefreshLayout.setProgressViewEndTarget(false, (int) Display.dp2Px(100, getResources()));

		requestMissDetail();
		requestHerAnswerList(true);
		initSwipeRefreshLayout();
		registerRefreshReceiver();

		mSwipeRefreshLayout.setOnScrollListener(new CustomSwipeRefreshLayout.OnScrollListener() {
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
				if (mTitleBar != null) {
					int bgColor = 0X0affffff;
					float alpha = 0;
					if (getListScrollY() < 0) {
						bgColor = 0X0aFFFFFF;
						alpha = 0;
					} else if (getListScrollY() > 300) {
						bgColor = 0XFF55ADFF;
						alpha = 1;
					} else {
						bgColor = (int) mEvaluator.evaluate(getListScrollY() / 300.f, 0X03aFFFFF, 0XFF55ADFF);
						alpha = getListScrollY() / 300.f;
					}
					mTitleBar.setBackgroundColor(bgColor);
					mTitleBar.setTitleAlpha(alpha);
				}
			}
		});

		mHerAnswerAdapter.setCallback(new HerAnswerAdapter.ItemCallback() {
			@Override
			public void loveOnClick(final Question item) {
				if (LocalUser.getUser().isLogin()) {
					umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
					Client.praise(item.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {

						@Override
						protected void onRespSuccessData(Praise praise) {
							item.setIsPrise(praise.getIsPrise());
							item.setPriseCount(praise.getPriseCount());
							mHerAnswerAdapter.notifyDataSetChanged();
							int praiseCount;
							if (mMiss != null) {
								if (praise.getIsPrise() == 0) {
									praiseCount = mMiss.getTotalPrise() - 1;
									mMiss.setTotalPrise(praiseCount);
								} else {
									praiseCount = mMiss.getTotalPrise() + 1;
									mMiss.setTotalPrise(praiseCount);
								}
								mLovePeopleNumber.setText(getString(R.string.love_people_number, StrFormatter.getFormatCount(praiseCount)));
							}
						}
					}).fire();
				} else {
					stopPreviousVoice();
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void rewardOnClick(Question item) {
				if (LocalUser.getUser().isLogin()) {
					RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
				} else {
					stopPreviousVoice();
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void voiceOnClick(final Question item) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_STOP) {
					//结束状态直接开始播放
					playVoice(item);
				} else {
					if (MediaPlayerManager.playingId == item.getId()) {
						if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PAUSE) {
							MediaPlayerManager.resume();
							item.setPlaying(true);
							mHerAnswerAdapter.notifyDataSetChanged();
						} else {
							MediaPlayerManager.pause();
							item.setPlaying(false);
							mHerAnswerAdapter.notifyDataSetChanged();
						}
					} else {
						stopPreviousVoice();
						//关闭上一个语音,开始这个
						playVoice(item);
					}
				}
			}
		});
	}

	private void playVoice(final Question item) {
		if (!MissVoiceRecorder.isHeard(item.getId())) {
			//没听过的
			Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
				@Override
				protected void onRespSuccess(Resp<JsonPrimitive> resp) {
					if (resp.isSuccess()) {
						MissVoiceRecorder.markHeard(item.getId());
						item.setListenCount(item.getListenCount() + 1);
						mHerAnswerAdapter.notifyDataSetChanged();
					}
				}
			}).fire();
		}

		item.setPlaying(true);
		mHerAnswerAdapter.notifyDataSetChanged();

		MediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
					//获取焦点之后开始播放,避免音轨并发
					MediaPlayerManager.start();
					MediaPlayerManager.setPlayingId(item.getId());
					MediaPlayerManager.setPortrait(item.getCustomPortrait());
					mHerAnswerAdapter.notifyDataSetChanged();
			}
		}, new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				MediaPlayerManager.release();
				mHerAnswerAdapter.notifyDataSetChanged();
				//播放结束之后发个广播给问题详情页刷新界面
				//sendPlayFinishBroadcast();
			}
		});
	}

	public void stopPreviousVoice() {
		MediaPlayerManager.release();
		for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
			Question question = mHerAnswerAdapter.getItem(i);
			if (question != null) {
				if (question.getId() == MediaPlayerManager.playingId) {
					mHerAnswerAdapter.notifyDataSetChanged();
				}
			}
		}
	}

	private void initData(Intent intent) {
		mCustomId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void initHeaderView() {
		LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_miss_profile, null);
		mAvatar = (ImageView) header.findViewById(R.id.avatar);
		mName = (TextView) header.findViewById(R.id.name);
		mVoiceIntroduce = (LinearLayout) header.findViewById(R.id.voiceIntroduce);
		mVoice = (TextView) header.findViewById(R.id.voice);
		mVoiceLevel = header.findViewById(R.id.voiceLevel);
		mLovePeopleNumber = (TextView) header.findViewById(R.id.lovePeopleNumber);
		mIntroduce = (TextView) header.findViewById(R.id.introduce);
		mAttention = (LinearLayout) header.findViewById(R.id.attention);
		mAttentionImage = (ImageView) header.findViewById(R.id.attentionImage);
		mAttentionText = (TextView) header.findViewById(R.id.attentionText);
		mAttentionNumber = (TextView) header.findViewById(R.id.attentionNumber);
		mReward = (LinearLayout) header.findViewById(R.id.reward);
		mRewardNumber = (TextView) header.findViewById(R.id.rewardNumber);
		mEmpty = (LinearLayout) header.findViewById(R.id.empty);

		mAvatar.setOnClickListener(this);
		mAttention.setOnClickListener(this);
		mReward.setOnClickListener(this);
		mListView.addHeaderView(header);
	}

	public int getListScrollY() {
		View view = mListView.getChildAt(0);

		if (view == null) {
			return 0;
		}

		int firstVisiblePosition = mListView.getFirstVisiblePosition();
		int top = view.getTop();
		return -top + firstVisiblePosition * view.getHeight();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.avatar:
				if (mMiss != null) {
					umengEventCount(UmengCountEventId.MISS_TALK_AVATAR);
					Launcher.with(this, LookBigPictureActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mMiss.getPortrait())
							.putExtra(Launcher.EX_PAYLOAD_2, 0)
							.execute();
				} else {
					Launcher.with(this, LookBigPictureActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, "")
							.putExtra(Launcher.EX_PAYLOAD_2, 0)
							.execute();
				}
				break;
			case R.id.attention:
				if (mMiss != null) {
					if (LocalUser.getUser().isLogin()) {
						umengEventCount(UmengCountEventId.MISS_TALK_ATTENTION);

						Client.attention(mMiss.getId()).setCallback(new Callback2D<Resp<Attention>, Attention>() {

							@Override
							protected void onRespSuccessData(Attention attention) {
								if (attention.getIsAttention() == 0) {
									mAttentionImage.setImageResource(R.drawable.ic_not_attention);
									mAttentionText.setText(R.string.attention);
								} else {
									mAttentionImage.setImageResource(R.drawable.ic_attention);
									mAttentionText.setText(R.string.is_attention);
								}
								mAttentionNumber.setText(getString(R.string.count,
										StrFormatter.getFormatCount(attention.getAttentionCount())));
							}
						}).fire();
					} else {
						stopPreviousVoice();
						Launcher.with(getActivity(), LoginActivity.class).execute();
					}
				} else {
					ToastUtil.show(getString(R.string.no_miss));
				}
				break;
			case R.id.reward:
				if (mMiss != null) {
					if (LocalUser.getUser().isLogin()) {
						RewardMissActivity.show(getActivity(), mCustomId, RewardInfo.TYPE_MISS);
					} else {
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						stopPreviousVoice();
						startActivityForResult(intent, REQ_MISS_REWARD_LOGIN);
					}
				} else {
					ToastUtil.show(getString(R.string.no_miss));
				}
				break;
		}
	}

	@Override
	protected void onPause() {
		super.onPause();

	}

	private void requestMissDetail() {
		Client.getMissDetail(mCustomId).setTag(TAG)
				.setCallback(new Callback2D<Resp<Miss>, Miss>() {
					@Override
					protected void onRespSuccessData(Miss miss) {
						updateMissDetail(miss);
						mMiss = miss;
					}

					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						mVoice.setVisibility(View.GONE);
					}
				}).fire();
	}

	private void updateMissDetail(final Miss miss) {
		GlideApp.with(this).load(miss.getPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.circleCrop()
				.into(mAvatar);

		mName.setText(miss.getName());
		mTitleBar.setTitle(miss.getName());
		mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), SubmitQuestionActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mCustomId)
							.execute();
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					stopPreviousVoice();
					startActivityForResult(intent, REQ_SUBMIT_QUESTION_LOGIN);
				}
			}
		});

		if (miss.getSoundTime() == 0 || TextUtils.isEmpty(miss.getBriefingSound())) {
			mVoiceIntroduce.setVisibility(View.GONE);
		} else {
			mVoiceIntroduce.setVisibility(View.VISIBLE);
			mVoice.setText(getString(R.string.voice_time, miss.getSoundTime()));
		}

		mLovePeopleNumber.setText(getString(R.string.love_people_number, StrFormatter.getFormatCount(miss.getTotalPrise())));
		if (!TextUtils.isEmpty(miss.getBriefingText())) {
			mIntroduce.setText(miss.getBriefingText());
		} else {
			mIntroduce.setText(R.string.no_miss_introduce);
		}

		if (miss.isAttention() == 0) {
			mAttentionImage.setImageResource(R.drawable.ic_not_attention);
			mAttentionText.setText(R.string.attention);
		} else {
			mAttentionImage.setImageResource(R.drawable.ic_attention);
			mAttentionText.setText(R.string.is_attention);
		}
		mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(miss.getTotalAttention())));
		mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(miss.getTotalAward())));

		/*mVoiceIntroduce.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				umengEventCount(UmengCountEventId.MISS_TALK_VOICE);
				//播放下一个之前把上一个播放位置的动画停了
				if (mPlayingID != -1) {
					for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
						Question question = mHerAnswerAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == mPlayingID) {
								question.setPlaying(false);
								mHerAnswerAdapter.notifyDataSetChanged();
							}
						}
					}
				}

				if (mMissIntroducePlayingID == miss.getId()) {
					mMediaPlayerManager.release();
					mVoiceLevel.clearAnimation();
					mVoiceLevel.setBackgroundResource(R.drawable.ic_miss_voice_4);
					mMissIntroducePlayingID = -1;
				} else {
					mMediaPlayerManager.play(miss.getBriefingSound(), new MediaPlayer.OnCompletionListener() {
						@Override
						public void onCompletion(MediaPlayer mp) {
							mVoiceLevel.clearAnimation();
							mVoiceLevel.setBackgroundResource(R.drawable.ic_miss_voice_4);
							mMissIntroducePlayingID = -1;
						}
					});

					mVoiceLevel.setBackgroundResource(R.drawable.bg_miss_introduce_voice);
					AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
					animation.start();
					mMissIntroducePlayingID = miss.getId();
				}
			}
		});*/
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Question item = (Question) parent.getItemAtPosition(position);
		if (item != null) {
			Launcher.with(this, QuestionDetailActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, item.getId()).executeForResult(REQ_QUESTION_DETAIL);
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mCreateTime = null;
				mSwipeRefreshLayout.setLoadMoreEnable(true);
				requestMissDetail();
				requestHerAnswerList(true);

				mVoiceLevel.clearAnimation();
				mVoiceLevel.setBackgroundResource(R.drawable.ic_miss_voice_4);

			}
		});

		mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
			@Override
			public void onLoadMore() {
				mListView.postDelayed(new Runnable() {
					@Override
					public void run() {
						requestHerAnswerList(false);
					}
				}, 1000);
			}
		});
	}

	private void requestHerAnswerList(final boolean isRefresh) {
		Client.getHerAnswerList(mCustomId, mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						if (questionList.size() == 0 && mCreateTime == null) {
							mEmpty.setVisibility(View.VISIBLE);
							stopRefreshAnimation();
						} else {
							mEmpty.setVisibility(View.GONE);
							mHerAnswerList = questionList;
							updateHerAnswerList(questionList, isRefresh);
						}
					}

					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
						if (mCreateTime == null) {
							mEmpty.setVisibility(View.VISIBLE);
							mVoiceIntroduce.setVisibility(View.GONE);
							mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(0)));
							mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(0)));
							mIntroduce.setText(R.string.no_miss_introduce);
							mHerAnswerAdapter.clear();
							mHerAnswerAdapter.notifyDataSetChanged();
						}
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

	private void updateHerAnswerList(final List<Question> questionList, boolean isRefresh) {
		if (questionList == null) {
			stopRefreshAnimation();
			return;
		}

		if (questionList.size() < mPageSize) {
			mSwipeRefreshLayout.setLoadMoreEnable(false);
		} else {
			mCreateTime = mHerAnswerList.get(mHerAnswerList.size() - 1).getCreateTime();
		}

		if (isRefresh) {
			if (mHerAnswerAdapter != null) {
				mHerAnswerAdapter.clear();
			}
		}
		stopRefreshAnimation();

		for (Question question : questionList) {
			if (mSet.add(question.getId())) {
				mHerAnswerAdapter.add(question);
			}
		}
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
	}

	static class HerAnswerAdapter extends ArrayAdapter<Question> {

		public interface ItemCallback {
			void loveOnClick(Question item);

			void rewardOnClick(Question item);

			void voiceOnClick(Question item);
		}

		private Context mContext;
		private ItemCallback mCallback;
		private Activity mActivity;

		private HerAnswerAdapter(@NonNull Context context) {
			super(context, 0);
			this.mContext = context;
			this.mActivity = (Activity) context;
		}

		public void setCallback(ItemCallback callback) {
			mCallback = callback;
		}

		private boolean isTheDifferentMonth(int position) {
			if (position == 0) {
				return true;
			}
			Question pre = getItem(position - 1);
			Question next =getItem(position);
			//判断两个时间在不在一个月内  不是就要显示标题
			if (pre == null || next == null) return true;
			long preTime = pre.getCreateTime();
			long nextTime = next.getCreateTime();
			return !DateUtil.isInThisMonth(nextTime, preTime);
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_miss_answer, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.bindingData(mContext, getItem(position), position, mCallback, isTheDifferentMonth(position), mActivity);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.date)
			TextView mDate;
			@BindView(R.id.day)
			TextView mDay;
			@BindView(R.id.question)
			TextView mQuestion;
			@BindView(R.id.soundTime)
			TextView mSoundTime;
			@BindView(R.id.listenerNumber)
			TextView mListenerNumber;
			@BindView(R.id.praiseNumber)
			TextView mPraiseNumber;
			@BindView(R.id.commentNumber)
			TextView mCommentNumber;
			@BindView(R.id.ingotNumber)
			TextView mIngotNumber;
			@BindView(playImage)
			ImageView mPlayImage;
			@BindView(R.id.progressBar)
			ProgressBar mProgressBar;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private Timer mTimer = new Timer();
			private TimerTask mTimerTask;

			static class HeaderViewHolder {
				@BindView(R.id.adsorb_text)
				TextView mAdsorbText;

				HeaderViewHolder(View view) {
					ButterKnife.bind(this, view);
				}
			}

			public void bindingData(final Context context, final Question item,
			                        final int position, final ItemCallback callback,
			                        boolean theDifferentMonth, Activity activity) {
				if (item == null) return;

				if (theDifferentMonth) {
					mDate.setVisibility(View.VISIBLE);
					mDate.setText(DateUtil.getFormatYearMonth(item.getCreateTime()));
				} else {
					mDate.setVisibility(View.GONE);
				}

				mDay.setText(DateUtil.getFormatDay(item.getCreateTime()).substring(0, 2));
				mQuestion.setText(item.getQuestionContext());
				mSoundTime.setText(context.getString(R.string.voice_time, item.getSoundTime()));
				mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
				mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

				if (MissVoiceRecorder.isHeard(item.getId())) {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
				} else {
					mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
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

				if (item.getIsPrise() == 0) {
					mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
				} else {
					mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
				}

				if (!item.isPlaying()) {
					mPlayImage.setImageResource(R.drawable.ic_play);
				} else {
					mPlayImage.setImageResource(R.drawable.ic_pause);
				}

				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING
						&& MediaPlayerManager.playingId == item.getId()) {
					startCountDownTime(mSoundTime, item.getSoundTime(), mProgressBar, activity);
				} else if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PAUSE
						&& MediaPlayerManager.playingId == item.getId()) {
					mProgressBar.setMax(item.getSoundTime() * 1000);
					mProgressBar.setProgress(MediaPlayerManager.getCurrentPosition());
					mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - MediaPlayerManager.getCurrentPosition()) / 1000));
					stopCountDownTime();
				} else {
					mProgressBar.setMax(0);
					mProgressBar.setProgress(0);
					mSoundTime.setText(context.getString(R.string._seconds, item.getSoundTime()));
					stopCountDownTime();
				}

				mPraiseNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.loveOnClick(item);
						}
					}
				});

				mIngotNumber.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.rewardOnClick(item);
						}
					}
				});

				mPlayImage.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.voiceOnClick(item);
						}
					}
				});
			}

			private void startCountDownTime(final TextView sound, final int soundTime,
			                                final ProgressBar progressBar, final Activity activity) {
				progressBar.setMax(soundTime * 1000);
				if (mTimerTask == null) {
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
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_MISS_REWARD_LOGIN && resultCode == RESULT_OK) {
			RewardMissActivity.show(getActivity(), mCustomId, RewardInfo.TYPE_MISS);
		}

		if (requestCode == REQ_SUBMIT_QUESTION_LOGIN && resultCode == RESULT_OK) {
			Launcher.with(getActivity(), SubmitQuestionActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, mCustomId)
					.execute();
		}

		if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
			if (data != null) {
				Praise praise = data.getParcelableExtra(Launcher.EX_PAYLOAD);
				int replyCount = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
				int rewardCount = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
				int listenCount = data.getIntExtra(Launcher.EX_PAYLOAD_3, -1);
				if (praise != null) {
					for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
						Question question = mHerAnswerAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == data.getIntExtra(ExtraKeys.QUESTION_ID, -1)) {
								question.setIsPrise(praise.getIsPrise());
								question.setPriseCount(praise.getPriseCount());
								mHerAnswerAdapter.notifyDataSetChanged();
								int praiseCount;
								if (mMiss != null) {
									if (praise.getIsPrise() == 0) {
										praiseCount = mMiss.getTotalPrise() - 1;
										mMiss.setTotalPrise(praiseCount);
									} else {
										praiseCount = mMiss.getTotalPrise() + 1;
										mMiss.setTotalPrise(praiseCount);
									}
									mLovePeopleNumber.setText(getString(R.string.love_people_number, StrFormatter.getFormatCount(praiseCount)));
								}
							}
						}
					}
				}

				if (replyCount != -1) {
					for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
						Question question = mHerAnswerAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == data.getIntExtra(ExtraKeys.QUESTION_ID, -1)) {
								question.setReplyCount(replyCount);
								mHerAnswerAdapter.notifyDataSetChanged();
							}
						}
					}
				}

				if (rewardCount != -1) {
					for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
						Question question = mHerAnswerAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == data.getIntExtra(ExtraKeys.QUESTION_ID, -1)) {
								question.setAwardCount(rewardCount);
								mHerAnswerAdapter.notifyDataSetChanged();
							}
						}
					}
				}

				if (listenCount != -1) {
					for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
						Question question = mHerAnswerAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == data.getIntExtra(ExtraKeys.QUESTION_ID, -1)) {
								question.setListenCount(listenCount);
								mHerAnswerAdapter.notifyDataSetChanged();
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
		LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
	}

	private class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_MISS) {
					if (mMiss != null) {
						int rewardCount = mMiss.getTotalAward() + 1;
						mMiss.setTotalAward(rewardCount);
						mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(rewardCount)));
					}
				}
			}

			if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {
					if (mMiss != null) {
						int rewardCount = mMiss.getTotalAward() + 1;
						mMiss.setTotalAward(rewardCount);
						mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(rewardCount)));
					}

					for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
						Question question = mHerAnswerAdapter.getItem(i);
						if (question != null) {
							if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
								int questionRewardCount = question.getAwardCount() + 1;
								question.setAwardCount(questionRewardCount);
								mHerAnswerAdapter.notifyDataSetChanged();
							}
						}
					}
				}
			}

			if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				mSet.clear();
				mCreateTime = null;
				requestMissDetail();
				requestHerAnswerList(true);
			}
		}
	}
}
