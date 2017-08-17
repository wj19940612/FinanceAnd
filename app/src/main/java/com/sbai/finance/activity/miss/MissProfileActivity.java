package com.sbai.finance.activity.miss;

import android.animation.ArgbEvaluator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.model.miss.Attention;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Prise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MediaPlayerManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.ObservableScrollView;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 小姐姐详细资料页面
 */
public class MissProfileActivity extends BaseActivity implements
		AdapterView.OnItemClickListener, ObservableScrollView.ScrollViewListener {

	private static final int REQ_SUBMIT_QUESTION_LOGIN = 1001;
	private static final int REQ_MISS_REWARD_LOGIN = 1002;

	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	TextView mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.askHerQuestion)
	LinearLayout mAskHerQuestion;
	@BindView(R.id.back)
	ImageView mBack;
	@BindView(R.id.titleName)
	TextView mTitleName;
	@BindView(R.id.titleBar)
	RelativeLayout mTitleBar;
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.name)
	TextView mName;
	@BindView(R.id.voice)
	TextView mVoice;
	@BindView(R.id.lovePeopleNumber)
	TextView mLovePeopleNumber;
	@BindView(R.id.introduce)
	TextView mIntroduce;
	@BindView(R.id.attentionImage)
	ImageView mAttentionImage;
	@BindView(R.id.attentionText)
	TextView mAttentionText;
	@BindView(R.id.attentionNumber)
	TextView mAttentionNumber;
	@BindView(R.id.attention)
	LinearLayout mAttention;
	@BindView(R.id.rewardNumber)
	TextView mRewardNumber;
	@BindView(R.id.reward)
	LinearLayout mReward;
	@BindView(R.id.scrollView)
	ObservableScrollView mScrollView;

	private HerAnswerAdapter mHerAnswerAdapter;
	private float duration = 300.0f;
	private ArgbEvaluator evaluator = new ArgbEvaluator();
	private Long mCreateTime;
	private int mPageSize = 20;
	private HashSet<Integer> mSet;
	private int mCustomId;
	private View mFootView;
	private List<Question> mHerAnswerList;
	private Miss mMiss;
	private RefreshReceiver mRefreshReceiver;
	private int mPlayingID = -1;
	private MediaPlayerManager mMediaPlayerManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miss_profile);
		ButterKnife.bind(this);
		initData(getIntent());
		initFooterView();
		mMediaPlayerManager = new MediaPlayerManager(this);
		mSet = new HashSet<>();
		mHerAnswerList = new ArrayList<>();
		mHerAnswerAdapter = new HerAnswerAdapter(this);
		mListView.setFocusable(false);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mHerAnswerAdapter);
		mListView.setOnItemClickListener(this);
		mScrollView.setScrollViewListener(this);

		requestMissDetail();
		requestHerAnswerList();
		initSwipeRefreshLayout();
		registerRefreshReceiver();

		mHerAnswerAdapter.setCallback(new HerAnswerAdapter.ItemCallback() {
			@Override
			public void loveOnClick(final Question item) {
				if (LocalUser.getUser().isLogin()) {
					Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

						@Override
						protected void onRespSuccessData(Prise prise) {
							item.setIsPrise(prise.getIsPrise());
							item.setPriseCount(prise.getPriseCount());
							mHerAnswerAdapter.notifyDataSetChanged();
							int praiseCount;
							if (prise.getIsPrise() == 0) {
								praiseCount = mMiss.getTotalPrise() - 1;
								mMiss.setTotalPrise(praiseCount);
							} else {
								praiseCount = mMiss.getTotalPrise() + 1;
								mMiss.setTotalPrise(praiseCount);
							}
							mLovePeopleNumber.setText(getString(R.string.love_people_number, StrFormatter.getFormatCount(praiseCount)));
						}
					}).fire();
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void rewardOnClick(Question item) {
				if (LocalUser.getUser().isLogin()) {
					RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}

			@Override
			public void voiceOnClick(final Question item, final int position) {
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

				if (!MissVoiceRecorder.isHeard(item.getId())) {
					//没听过的
					Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
						@Override
						protected void onRespSuccess(Resp<JsonPrimitive> resp) {
							if (resp.isSuccess()) {
								mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
									@Override
									public void onCompletion(MediaPlayer mp) {
										item.setPlaying(false);
										mHerAnswerAdapter.notifyDataSetChanged();
									}
								});

								MissVoiceRecorder.markHeard(item.getId());
								item.setPlaying(true);
								item.setListenCount(item.getListenCount() + 1);
								mHerAnswerAdapter.notifyDataSetChanged();
								mPlayingID = item.getId();
							}
						}
					}).fire();
				} else {
					//听过的
					if (mPlayingID == item.getId()) {
						mMediaPlayerManager.release();
						item.setPlaying(false);
						mHerAnswerAdapter.notifyDataSetChanged();
						mPlayingID = -1;
					} else {
						mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
							@Override
							public void onCompletion(MediaPlayer mp) {
								item.setPlaying(false);
								mHerAnswerAdapter.notifyDataSetChanged();
							}
						});
						item.setPlaying(true);
						mHerAnswerAdapter.notifyDataSetChanged();
						mPlayingID = item.getId();
					}
				}
			}
		});
	}

	private void initData(Intent intent) {
		mCustomId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void initFooterView() {
		View view = new View(getActivity());
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(60, getResources()));
		view.setLayoutParams(params);
		mListView.addFooterView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
		//锁屏或者在后台运行或者跳转页面时停止播放和动画
		mMediaPlayerManager.release();
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
		mPlayingID = -1;
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
		Glide.with(this).load(miss.getPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(this))
				.into(mAvatar);

		mName.setText(miss.getName());
		mTitleName.setText(miss.getName());
		mTitleName.setAlpha(0);
		if (miss.getSoundTime() == 0) {
			mVoice.setVisibility(View.GONE);
		} else {
			mVoice.setText(getString(R.string.voice_time, miss.getSoundTime()));
		}

		mLovePeopleNumber.setText(getString(R.string.love_people_number, StrFormatter.getFormatCount(miss.getTotalPrise())));
		if (!TextUtils.isEmpty(miss.getBrifeingText())) {
			mIntroduce.setText(miss.getBrifeingText());
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
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Question item = (Question) parent.getItemAtPosition(position);
		if (item != null) {
			Launcher.with(this, QuestionDetailActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
		}
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mCreateTime = null;
				requestMissDetail();
				requestHerAnswerList();

				//下拉刷新时关闭语音播放
				mMediaPlayerManager.release();
				mPlayingID = -1;
			}
		});
	}

	private void requestHerAnswerList() {
		Client.getHerAnswerList(mCustomId, mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						mHerAnswerList = questionList;
						updateHerAnswerList(questionList);
					}

					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
						mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(0)));
						mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(0)));
						mIntroduce.setText(R.string.no_miss_introduce);
					}
				}).fire();
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	private void updateHerAnswerList(final List<Question> questionList) {
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
					mCreateTime = mHerAnswerList.get(mHerAnswerList.size() - 1).getCreateTime();
					requestHerAnswerList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (questionList.size() < mPageSize) {
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mHerAnswerAdapter != null) {
				mHerAnswerAdapter.clear();
				mHerAnswerAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (Question question : questionList) {
			if (mSet.add(question.getId())) {
				mHerAnswerAdapter.add(question);
			}
		}
	}

	@OnClick({R.id.avatar, R.id.voice, R.id.attention, R.id.reward, R.id.askHerQuestion, R.id.back})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.avatar:
				if (mMiss != null) {
					Launcher.with(this, MissAvatarActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mMiss.getPortrait())
							.execute();
				}
				break;
			case R.id.voice:
				break;
			case R.id.attention:
				if (mMiss != null) {
					if (LocalUser.getUser().isLogin()) {
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
						Launcher.with(getActivity(), LoginActivity.class).execute();
					}
				}
				break;
			case R.id.reward:
				if (mMiss != null) {
					if (LocalUser.getUser().isLogin()) {
						RewardMissActivity.show(getActivity(), mCustomId, RewardInfo.TYPE_MISS);
					} else {
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivityForResult(intent, REQ_MISS_REWARD_LOGIN);
					}
				}
				break;
			case R.id.askHerQuestion:
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), SubmitQuestionActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mCustomId)
							.execute();
				} else {
					Intent intent = new Intent(getActivity(), LoginActivity.class);
					startActivityForResult(intent, REQ_SUBMIT_QUESTION_LOGIN);
				}
				break;
			case R.id.back:
				finish();
				break;
		}
	}

	@Override
	public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {

		int bgColor = 0X0affffff;
		float alpha = 0;
		if (y < 0) {
			bgColor = 0X0aFFFFFF;
			alpha = 0;
		} else if (y > 300) {
			bgColor = 0XFF55ADFF;
			alpha = 1;
		} else {
			bgColor = (int) evaluator.evaluate(y / duration, 0X03aFFFFF, 0XFF55ADFF);
			alpha = y / duration;
		}
		mTitleBar.setBackgroundColor(bgColor);
		mTitleName.setAlpha(alpha);
	}

	static class HerAnswerAdapter extends ArrayAdapter<Question> {

		public interface ItemCallback {
			void loveOnClick(Question item);

			void rewardOnClick(Question item);

			void voiceOnClick(Question item, int position);
		}

		private Context mContext;
		private ItemCallback mCallback;

		private HerAnswerAdapter(@NonNull Context context) {
			super(context, 0);
			this.mContext = context;
		}

		public void setCallback(ItemCallback callback) {
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

			viewHolder.bindingData(mContext, getItem(position), position, mCallback);
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
			@BindView(R.id.missAvatar)
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
			                        final int position, final ItemCallback callback) {
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

				mLoveNumber.setOnClickListener(new View.OnClickListener() {
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

				if (!item.isPlaying()) {
					mVoiceLevel.clearAnimation();
					mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
				} else {
					mVoiceLevel.setBackgroundResource(R.drawable.bg_play_voice);
					AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
					if (animation != null) {
						animation.start();
					}
				}

				mVoiceArea.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.voiceOnClick(item, position);
						}
					}
				});
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
	}

	private void registerRefreshReceiver() {
		mRefreshReceiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REWARD_SUCCESS);
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
					int rewardCount = mMiss.getTotalAward() + 1;
					if (mMiss != null) {
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
		}
	}
}
