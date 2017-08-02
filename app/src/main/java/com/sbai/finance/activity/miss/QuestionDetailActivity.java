package com.sbai.finance.activity.miss;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.missTalk.Question;
import com.sbai.finance.model.missTalk.QuestionReply;
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

import static com.sbai.finance.R.id.commentNumber;
import static com.sbai.finance.R.id.question;


public class QuestionDetailActivity extends BaseActivity implements AbsListView.OnScrollListener {

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
	@BindView(commentNumber)
	TextView mCommentNumber;
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

	private Question mQuestionDetail;
	//private QuestionReplyListAdapter mQuestionReplyListAdapter;
	private int mType = 1;
	private int mPageSize = 20;
	private int mPage = 0;
	private HashSet<String> mSet;
	private View mFootView;
	private QuestionReplyListAdapter mQuestionReplyListAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_detail);
		ButterKnife.bind(this);

		initData(getIntent());
		updateQuestionDetails(this);
		mSet = new HashSet<>();
		mQuestionReplyListAdapter = new QuestionReplyListAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mQuestionReplyListAdapter);
		mListView.setOnScrollListener(this);

		requestQuestionReplyList();
		initSwipeRefreshLayout();
		mScrollView.smoothScrollTo(0, 0);
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				requestQuestionReplyList();
			}
		});
	}

	private void initData(Intent intent) {
		mQuestionDetail = (Question) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
	}

	private void updateQuestionDetails(Context context) {
		Glide.with(context).load(mQuestionDetail.getUserPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(context))
				.into(mAvatar);

		Glide.with(context).load(mQuestionDetail.getCustomPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(context))
				.into(mMissAvatar);

		mName.setText(mQuestionDetail.getUserName());
		mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(mQuestionDetail.getCreateTime()));
		mQuestion.setText(mQuestionDetail.getQuestionContext());
		mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(mQuestionDetail.getListenCount())));
		mLoveNumber.setText(context.getString(R.string.love_miss, StrFormatter.getFormatCount(mQuestionDetail.getPriseCount())));
		mRewardNumber.setText(context.getString(R.string.reward_miss, StrFormatter.getFormatCount(mQuestionDetail.getAwardCount())));
		mCommentNumber.setText(context.getString(R.string.comment_number_string, StrFormatter.getFormatCount(mQuestionDetail.getReplyCount())));
	}

	private void requestQuestionReplyList() {
		Client.getQuestionReplyList(mType, mQuestionDetail.getId(), mPage, mPageSize)
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

		mScrollView.smoothScrollTo(0, 0);
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

				Glide.with(context).load(item.getUserModel().getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mUserName.setText(item.getUserModel().getUserName());
				mOpinionContent.setText(item.getContent());
				mPublishTime.setText(DateUtil.getFormatTime(item.getUserModel().getCreateTime()));
				if (item.getReplys().size() == 0) {
					mReplyArea.setVisibility(View.GONE);
				} else {
					mReplyArea.setVisibility(View.VISIBLE);
					mReplyName.setText(item.getReplys().get(0).getUserModel().getUserName());
					mReplyContent.setText(item.getReplys().get(0).getContent());
				}
			}
		}
	}
}
