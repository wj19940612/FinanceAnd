package com.sbai.finance.activity.miss;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.dialog.ReplyDialogFragment;
import com.sbai.finance.fragment.dialog.RewardMissDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.model.miss.RewardMoney;
import com.sbai.finance.model.missTalk.Prise;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.model.missTalk.QuestionReply;
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
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.question;
import static com.sbai.finance.activity.miss.ReplyActivity.ACTION_REPLY_SUCCESS;
import static com.sbai.finance.net.Client.SHARE_URL_QUESTION;


public class QuestionDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {

	private static final int REQ_COMMENT = 1001;
	private static final int REQ_COMMENT_LOGIN = 1002;
	private static final int REQ_REWARD_LOGIN = 1003;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.name)
	TextView mName;
	@BindView(R.id.askTime)
	TextView mAskTime;
	@BindView(R.id.hotArea)
	RelativeLayout mHotArea;
	@BindView(question)
	TextView mQuestion;
	@BindView(R.id.missAvatar)
	ImageView mMissAvatar;
	@BindView(R.id.voice)
	TextView mVoice;
	@BindView(R.id.listenerNumber)
	TextView mListenerNumber;
	@BindView(R.id.loveNumber)
	TextView mLoveNumber;
	@BindView(R.id.rewardNumber)
	TextView mRewardNumber;
	@BindView(R.id.split)
	View mSplit;
	@BindView(R.id.listView)
	MyListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.commentArea)
	LinearLayout mCommentArea;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.loveImage)
	ImageView mLoveImage;
	@BindView(R.id.love)
	LinearLayout mLove;
	@BindView(R.id.comment)
	LinearLayout mComment;
	@BindView(R.id.reward)
	LinearLayout mReward;
	@BindView(R.id.commentNumber)
	TextView mCommentNumber;
	@BindView(R.id.voiceLevel)
	View mVoiceLevel;
	@BindView(R.id.voiceArea)
	LinearLayout mVoiceArea;
	@BindView(R.id.noComment)
	TextView mNoComment;

	private int mQuestionId;
	private int mType = 1;
	private int mPageSize = 20;
	private int mPage = 0;
	private HashSet<String> mSet;
	private View mFootView;
	private QuestionReplyListAdapter mQuestionReplyListAdapter;
	private RewardInfo mRewardInfo;
	private Question mQuestionDetail;
	private RefreshReceiver mRefreshReceiver;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);
		ButterKnife.bind(this);

		initData(getIntent());
		initRewardInfo();
		mSet = new HashSet<>();
		mQuestionReplyListAdapter = new QuestionReplyListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mQuestionReplyListAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setFocusable(false);

		requestQuestionDetail();
		requestQuestionReplyList();
		initSwipeRefreshLayout();
		registerRefreshReceiver();

		mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				share();
			}
		});
	}

	private void share() {
		ShareDialog.with(getActivity())
				.setTitle(getString(R.string.share_title))
				.setShareTitle(getString(R.string.question_share_share_title))
				.setShareDescription(getString(R.string.question_share_description))
				.setShareUrl(SHARE_URL_QUESTION)
				.hasFeedback(false)
				.setListener(new ShareDialog.OnShareDialogCallback() {
					@Override
					public void onShareSuccess(ShareDialog.SHARE_PLATFORM platform) {
						Client.share().setTag(TAG).fire();
					}

					@Override
					public void onFeedbackClick(View view) {
					}
				}).show();
	}

	private void requestQuestionDetail() {
		Client.getQuestionDetails(mQuestionId).setTag(TAG)
				.setCallback(new Callback2D<Resp<Question>, Question>() {
					@Override
					protected void onRespSuccessData(Question question) {
						updateQuestionDetail(question);
						mQuestionDetail = question;
					}
				}).fire();
	}

	private void initRewardInfo() {
		mRewardInfo = new RewardInfo();
		mRewardInfo.setId(mQuestionId);
		mRewardInfo.setType(RewardInfo.TYPE_QUESTION);
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

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				requestQuestionDetail();
				requestQuestionReplyList();
				mScrollView.smoothScrollTo(0, 0);
			}
		});
	}

	@Override
	protected void onPause() {
		super.onPause();
		mediaPlayerUtil.release();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		mediaPlayerUtil.release();
		LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
	}

	public RewardInfo getRewardInfo() {
		return mRewardInfo;
	}

	private void initData(Intent intent) {
		mQuestionId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void updateQuestionDetail(final Question question) {
		Glide.with(this).load(question.getUserPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(this))
				.into(mAvatar);

		Glide.with(this).load(question.getCustomPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(this))
				.into(mMissAvatar);

		if (question.getIsPrise() == 0) {
			mLoveImage.setImageResource(R.drawable.ic_miss_love);
		} else {
			mLoveImage.setImageResource(R.drawable.ic_miss_love_yellow);
		}

		mName.setText(question.getUserName());
		mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(question.getCreateTime()));
		mQuestion.setText(question.getQuestionContext());
		mListenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(question.getListenCount())));
		mLoveNumber.setText(getString(R.string.love_miss, StrFormatter.getFormatCount(question.getPriseCount())));
		mRewardNumber.setText(getString(R.string.reward_miss, StrFormatter.getFormatCount(question.getAwardCount())));
		mCommentNumber.setText(getString(R.string.comment_number_string, StrFormatter.getFormatCount(question.getReplyCount())));
		mVoice.setText(getString(R.string.voice_time, question.getSoundTime()));

		if (question.getReplyCount() > 0) {
			mNoComment.setVisibility(View.GONE);
			mCommentArea.setBackgroundColor(ContextCompat.getColor(this, R.color.background));
		}

		if (MissVoiceRecorder.isHeard(question.getId())) {
			mListenerNumber.setTextColor(ContextCompat.getColor(this, R.color.unluckyText));
		} else {
			mListenerNumber.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
		}

		mMissAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.with(QuestionDetailActivity.this, MissProfileActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, question.getAnswerCustomId())
						.execute();
			}
		});

		mVoiceArea.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				//加动画
				mVoiceLevel.setBackgroundResource(R.drawable.bg_play_voice);
				AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
				animation.start();

				if (!MissVoiceRecorder.isHeard(question.getId())) {
					//没听过
					Client.listen(question.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
						@Override
						protected void onRespSuccess(Resp<JsonPrimitive> resp) {
							if (resp.isSuccess()) {
								if (mediaPlayerUtil.isPlaying()) {
									mediaPlayerUtil.release();
									mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
								} else {
									mediaPlayerUtil.play(question.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
										@Override
										public void onCompletion(MediaPlayer mp) {
											mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
										}
									});

									MissVoiceRecorder.markHeard(question.getId());
									question.setListenCount(question.getListenCount() + 1);
									mListenerNumber.setTextColor(ContextCompat.getColor(QuestionDetailActivity.this, R.color.unluckyText));
									mListenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(question.getListenCount())));
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
						mediaPlayerUtil.play(question.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
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

	private void requestQuestionReplyList() {
		Client.getQuestionReplyList(mType, mQuestionId, mPage, mPageSize)
				.setTag(TAG)
				.setCallback(new Callback2D<Resp<QuestionReply>, QuestionReply>() {
					@Override
					protected void onRespSuccessData(QuestionReply questionReply) {
						updateQuestionReplyList(questionReply.getData());
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

	private void updateQuestionReplyList(List<QuestionReply.DataBean> questionReplyList) {
		if (questionReplyList == null) {
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
					requestQuestionReplyList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (questionReplyList.size() < mPageSize) {
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mQuestionReplyListAdapter != null) {
				mQuestionReplyListAdapter.clear();
				mQuestionReplyListAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (QuestionReply.DataBean questionReply : questionReplyList) {
			if (mSet.add(questionReply.getId())) {
				mQuestionReplyListAdapter.add(questionReply);
			}
		}
	}

	@OnClick({R.id.comment, R.id.reward, R.id.love})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.comment:
				if (mQuestionDetail != null) {
					if (LocalUser.getUser().isLogin()) {
						Launcher.with(getActivity(), CommentActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mQuestionDetail.getQuestionUserId())
								.putExtra(Launcher.EX_PAYLOAD_1, mQuestionDetail.getId())
								.executeForResult(REQ_COMMENT);

					} else {
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivityForResult(intent, REQ_COMMENT_LOGIN);
					}
				}
				break;
			case R.id.reward:
				if (mQuestionDetail != null) {
					if (LocalUser.getUser().isLogin()) {
						mRewardInfo.setMoney(0);
						mRewardInfo.setIndex(-1);
						RewardMissDialogFragment.newInstance()
								.show(getSupportFragmentManager());
					} else {
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivityForResult(intent, REQ_REWARD_LOGIN);
					}
				}
				break;
			case R.id.love:
				if (mQuestionDetail != null) {
					if (LocalUser.getUser().isLogin()) {
						Client.prise(mQuestionDetail.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

							@Override
							protected void onRespSuccessData(Prise prise) {
								int praiseCount;
								if (prise.getIsPrise() == 0) {
									mLoveImage.setImageResource(R.drawable.ic_miss_love);
									praiseCount = mQuestionDetail.getPriseCount() - 1;
									mQuestionDetail.setPriseCount(praiseCount);
								} else {
									mLoveImage.setImageResource(R.drawable.ic_miss_love_yellow);
									praiseCount = mQuestionDetail.getPriseCount() + 1;
									mQuestionDetail.setPriseCount(praiseCount);
								}
								mLoveNumber.setText(getString(R.string.love_miss, StrFormatter.getFormatCount(praiseCount)));
							}
						}).fire();
					} else {
						Launcher.with(getActivity(), LoginActivity.class).execute();
					}
				}
				break;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		QuestionReply.DataBean item = (QuestionReply.DataBean) parent.getItemAtPosition(position);
		ReplyDialogFragment.newInstance(item).show(getSupportFragmentManager());
	}

	static class QuestionReplyListAdapter extends ArrayAdapter<QuestionReply.DataBean> {
		private Context mContext;

		private QuestionReplyListAdapter(@NonNull Context context) {
			super(context, 0);
			this.mContext = context;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_reply, null);
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
			RelativeLayout mHotArea;
			@BindView(R.id.opinionContent)
			TextView mOpinionContent;
			@BindView(R.id.replyName)
			TextView mReplyName;
			@BindView(R.id.replyContent)
			TextView mReplyContent;
			@BindView(R.id.replyArea)
			LinearLayout mReplyArea;
			@BindView(R.id.publishTime)
			TextView mPublishTime;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(Context context, QuestionReply.DataBean item) {
				if (item == null) return;

				if (item.getUserModel() != null) {
					Glide.with(context).load(item.getUserModel().getUserPortrait())
							.placeholder(R.drawable.ic_default_avatar)
							.transform(new GlideCircleTransform(context))
							.into(mAvatar);
					mUserName.setText(item.getUserModel().getUserName());
				} else {
					Glide.with(context).load(R.drawable.ic_default_avatar)
							.transform(new GlideCircleTransform(context))
							.into(mAvatar);
					mUserName.setText("");
				}

				mPublishTime.setText(DateUtil.getMissFormatTime(item.getCreateDate()));
				mOpinionContent.setText(item.getContent());

				if (item.getReplys() != null) {
					if (item.getReplys().size() == 0) {
						mReplyArea.setVisibility(View.GONE);
					} else {
						mReplyArea.setVisibility(View.VISIBLE);
						if (item.getReplys().get(0) != null) {
							if (item.getReplys().get(0).getUserModel() != null) {
								mReplyName.setText(context.getString(R.string.reply_name, item.getReplys().get(0).getUserModel().getUserName()));
							} else {
								mReplyName.setText("");
							}
							mReplyContent.setText(item.getReplys().get(0).getContent());
						} else {
							mReplyName.setText("");
							mReplyContent.setText("");
						}
					}
				} else {
					mReplyArea.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_COMMENT && resultCode == RESULT_OK) {
			mSet.clear();
			mPage = 0;
			mSwipeRefreshLayout.setRefreshing(true);
			requestQuestionDetail();
			requestQuestionReplyList();
			mScrollView.smoothScrollTo(0, 0);
		}

		if (requestCode == REQ_COMMENT_LOGIN && resultCode == RESULT_OK) {
			if (mQuestionDetail != null) {
				Launcher.with(getActivity(), CommentActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mQuestionDetail.getQuestionUserId())
						.putExtra(Launcher.EX_PAYLOAD_1, mQuestionDetail.getId())
						.executeForResult(REQ_COMMENT);
			}
		}

		if (requestCode == REQ_REWARD_LOGIN && resultCode == RESULT_OK) {
			mRewardInfo.setMoney(0);
			mRewardInfo.setIndex(-1);
			RewardMissDialogFragment.newInstance()
					.show(getSupportFragmentManager());
		}
	}

	private void registerRefreshReceiver() {
		mRefreshReceiver = new RefreshReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(ACTION_REPLY_SUCCESS);
		filter.addAction(ACTION_REWARD_SUCCESS);
		LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
	}


	private class RefreshReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (ACTION_REPLY_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				mSet.clear();
				mPage = 0;
				mSwipeRefreshLayout.setRefreshing(true);
				requestQuestionDetail();
				requestQuestionReplyList();
				mScrollView.smoothScrollTo(0, 0);
			}

			if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
				int rewardCount = mQuestionDetail.getAwardCount() + 1;
				mQuestionDetail.setAwardCount(rewardCount);
				mRewardNumber.setText(getString(R.string.reward_miss, StrFormatter.getFormatCount(rewardCount)));
			}
		}
	}
}
