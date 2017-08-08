package com.sbai.finance.activity.miss;

import android.animation.ArgbEvaluator;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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
import com.sbai.finance.fragment.dialog.RewardMissDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.model.miss.RewardMoney;
import com.sbai.finance.model.missTalk.Attention;
import com.sbai.finance.model.missTalk.Miss;
import com.sbai.finance.model.missTalk.Prise;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.mediaPlayerUtil;
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
public class MissProfileActivity extends BaseActivity implements AdapterView.OnItemClickListener, ObservableScrollView.ScrollViewListener {

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
	private RewardInfo mRewardInfo;
	private List<Question> mHerAnswerList;
	private Miss mMiss;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miss_profile);
		ButterKnife.bind(this);
		initData(getIntent());
		initRewardInfo();
		initFooterView();
		mSet = new HashSet<>();
		mHerAnswerList = new ArrayList<>();
		mHerAnswerAdapter = new HerAnswerAdapter(this, mHerAnswerList, TAG);
		mListView.setFocusable(false);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mHerAnswerAdapter);
		mListView.setOnItemClickListener(this);
		mScrollView.setScrollViewListener(this);

		requestMissDetail();
		requestHerAnswerList();
		initSwipeRefreshLayout();
	}

	private void initData(Intent intent) {
		mCustomId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void initRewardInfo() {
		mRewardInfo = new RewardInfo();
		mRewardInfo.setId(mCustomId);
		mRewardInfo.setType(RewardInfo.TYPE_MISS);
		List<RewardMoney> list = new ArrayList<>();
		RewardMoney rewardMoney = new RewardMoney();
		rewardMoney.setMoney(10);
		list.add(rewardMoney);
		rewardMoney = new RewardMoney();
		rewardMoney.setMoney(100);
		list.add(rewardMoney);
		rewardMoney = new RewardMoney();
		rewardMoney.setMoney(1000);
		list.add(rewardMoney);
		mRewardInfo.setMoneyList(list);
	}

	private void initFooterView() {
		View view = new View(getActivity());
		AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(60, getResources()));
		view.setLayoutParams(params);
		mListView.addFooterView(view);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayerUtil.release();
	}

	private void requestMissDetail() {
		Client.getMissDetail(mCustomId).setTag(TAG)
				.setCallback(new Callback2D<Resp<Miss>, Miss>() {
					@Override
					protected void onRespSuccessData(Miss miss) {
						updateMissDetail(miss);
						mMiss = miss;
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
		mVoice.setText(getString(R.string.voice_time, miss.getSoundTime()));
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
					.putExtra(Launcher.EX_PAYLOAD, item).execute();
		}
	}

	public RewardInfo getRewardInfo() {
		return mRewardInfo;
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mCreateTime = null;
				requestMissDetail();
				requestHerAnswerList();
			}
		});
	}

	private void requestHerAnswerList() {
		Client.getHerAnswerList(mCustomId, mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						updateHerAnswerList(questionList);
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
					mCreateTime = questionList.get(questionList.size() - 1).getCreateTime();
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
				Launcher.with(this, MissAvatarActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mMiss.getPortrait())
						.execute();
				break;
			case R.id.voice:
				break;
			case R.id.attention:
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
				break;
			case R.id.reward:
				if (LocalUser.getUser().isLogin()) {
					if (mRewardInfo != null) {
						mRewardInfo.setMoney(0);
						mRewardInfo.setIndex(-1);
					}
					RewardMissDialogFragment.newInstance()
							.show(getSupportFragmentManager());
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
				break;
			case R.id.askHerQuestion:
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), SubmitQuestionActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mCustomId)
							.execute();
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
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

		private Context mContext;
		private List<Question> mHerAnswerList;
		private String TAG;

		private HerAnswerAdapter(@NonNull Context context, List<Question> herAnswerList, String TAG) {
			super(context, 0);
			this.mContext = context;
			this.mHerAnswerList = herAnswerList;
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


			viewHolder.bindingData(mContext, getItem(position), position, mHerAnswerList, TAG);
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
			                        int position, List<Question> herAnswerList, final String TAG) {
				if (item == null) return;
				if (position == herAnswerList.size() - 1) {

				}

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
}
