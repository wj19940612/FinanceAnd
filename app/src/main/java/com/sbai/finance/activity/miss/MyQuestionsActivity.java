package com.sbai.finance.activity.miss;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
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
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.model.miss.RewardMoney;
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
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.voiceArea;

/**
 * 我的提问页面
 */
public class MyQuestionsActivity extends BaseActivity implements AdapterView.OnItemClickListener, AbsListView.OnScrollListener {

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.empty)
	RelativeLayout mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	@BindView(R.id.askQuestion)
	TextView mAskQuestion;

	private MyQuestionAdapter mMyQuestionAdapter;
	private Long mCreateTime;
	private int mPageSize = 20;
	private HashSet<Integer> mSet;
	private View mFootView;
	private RewardInfo mRewardInfo;
	private List<Question> mMyQuestionList;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_questions);
		ButterKnife.bind(this);
		initRewardInfo();
		mSet = new HashSet<>();
		mMyQuestionList = new ArrayList<>();
		mMyQuestionAdapter = new MyQuestionAdapter(this, mMyQuestionList, TAG);
		mMyQuestionAdapter.setOnClickCallback(new MyQuestionAdapter.OnClickCallback() {
			@Override
			public void onRewardClick(Question item) {
			}
		});
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mMyQuestionAdapter);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		initSwipeRefreshLayout();
	}

	@Override
	protected void onResume() {
		super.onResume();
		requestMyQuestionList();
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
	}

	private void initRewardInfo() {
		mRewardInfo = new RewardInfo();
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
				mCreateTime = null;
				requestMyQuestionList();
			}
		});
	}

	public RewardInfo getRewardInfo() {
		return mRewardInfo;
	}


	private void requestMyQuestionList() {
		Client.getMyQuestionList(mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
					@Override
					protected void onRespSuccessData(List<Question> questionList) {
						updateMyQuestionList(questionList);
					}

					@Override
					public void onFailure(VolleyError volleyError) {
						super.onFailure(volleyError);
						stopRefreshAnimation();
					}
				}).fire();
	}

	private void updateMyQuestionList(final List<Question> questionList) {
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
					requestMyQuestionList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (questionList.size() < mPageSize) {
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mMyQuestionAdapter != null) {
				mMyQuestionAdapter.clear();
				mMyQuestionAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (Question question : questionList) {
			if (mSet.add(question.getId())) {
				mMyQuestionAdapter.add(question);
			}
		}
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Question item = (Question) parent.getItemAtPosition(position);
		if (item != null && item.getSolve() == 1) {
			Launcher.with(getActivity(), QuestionDetailActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		int topRowVerticalPosition =
				(mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
		mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
	}

	@OnClick(R.id.askQuestion)
	public void onViewClicked() {
		Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
	}

	static class MyQuestionAdapter extends ArrayAdapter<Question> {

		private Context mContext;
		private OnClickCallback mOnClickCallback;
		private List<Question> mMyQuestionList;
		private String TAG;

		public void setOnClickCallback(OnClickCallback onClickCallback) {
			mOnClickCallback = onClickCallback;
		}

		interface OnClickCallback {
			void onRewardClick(Question item);
		}

		private MyQuestionAdapter(@NonNull Context context, List<Question> myQuestionList, String TAG) {
			super(context, 0);
			this.mContext = context;
			this.mMyQuestionList = myQuestionList;
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

			viewHolder.bindingData(mContext, getItem(position), mOnClickCallback, position, mMyQuestionList, TAG);
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
			@BindView(R.id.label)
			LinearLayout mLabel;
			@BindView(R.id.noMissReply)
			TextView mNoMissReply;
			@BindView(R.id.voiceLevel)
			View mVoiceLevel;
			@BindView(voiceArea)
			LinearLayout mVoiceArea;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(final Context context, final Question item,
			                        final OnClickCallback onClickCallback, int position,
			                        List<Question> myQuestionList, final String TAG) {
				if (item == null) return;

				if (position == myQuestionList.size() - 1) {
					mSplit.setVisibility(View.GONE);
				}

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				Glide.with(context).load(item.getCustomPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mMissAvatar);

				if (item.getSolve() == 0) {
					mLabel.setVisibility(View.GONE);
					mVoiceArea.setVisibility(View.GONE);
					mMissAvatar.setVisibility(View.GONE);
					mListenerNumber.setVisibility(View.GONE);
					mNoMissReply.setVisibility(View.VISIBLE);
				} else {
					mLabel.setVisibility(View.VISIBLE);
					mLabel.setVisibility(View.VISIBLE);
					mVoiceArea.setVisibility(View.VISIBLE);
					mMissAvatar.setVisibility(View.VISIBLE);
					mListenerNumber.setVisibility(View.VISIBLE);
					mNoMissReply.setVisibility(View.GONE);
				}

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
