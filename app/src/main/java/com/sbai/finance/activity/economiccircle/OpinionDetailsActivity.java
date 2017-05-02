package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.model.economiccircle.OpinionReply;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.MyListView;

import org.w3c.dom.Comment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class OpinionDetailsActivity extends BaseActivity {

	@BindView(R.id.scrollView)
	ScrollView mScrollView;
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.userName)
	TextView mUserName;
	@BindView(R.id.isAttention)
	TextView mIsAttention;
	@BindView(R.id.publishTime)
	TextView mPublishTime;
	@BindView(R.id.opinionContent)
	TextView mOpinionContent;
	@BindView(R.id.bigVarietyName)
	TextView mBigVarietyName;
	@BindView(R.id.varietyName)
	TextView mVarietyName;
	@BindView(R.id.lastPrice)
	TextView mLastPrice;
	@BindView(R.id.upDownPrice)
	TextView mUpDownPrice;
	@BindView(R.id.upDownPercent)
	TextView mUpDownPercent;
	@BindView(R.id.upDownArea)
	LinearLayout mUpDownArea;
	@BindView(R.id.loveNum)
	TextView mLoveNum;
	@BindView(R.id.commentNum)
	TextView mCommentNum;
	@BindView(android.R.id.list)
	MyListView mMyListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.comment)
	EditText mComment;
	@BindView(R.id.reply)
	TextView mReply;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	private List<OpinionReply> mOpinionReplyList;
	private OpinionReplyAdapter mOpinionReplyAdapter;
	private OpinionDetails mOpinionDetails;
	private TextView mFootView;

	private int mPage = 0;
	private int mPageSize = 15;
	private HashSet<Integer> mSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_details);
		ButterKnife.bind(this);

		initData(getIntent());

		initView();
	}

	private void initData(Intent intent) {
		mOpinionDetails = (OpinionDetails) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
	}

	@Override
	protected void onResume() {
		super.onResume();
		mOpinionReplyList = new ArrayList<>();

		mOpinionReplyAdapter = new OpinionReplyAdapter(this, mOpinionReplyList);
		mMyListView.setEmptyView(mEmpty);
		mMyListView.setAdapter(mOpinionReplyAdapter);

		requestOpinionReplyList();

	}

	private void requestOpinionReplyList() {
		if (mOpinionDetails != null) {
			Client.getOpinionReply(mPage, mPageSize, mOpinionDetails.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<List<OpinionReply>>, List<OpinionReply>>() {
						@Override
						protected void onRespSuccessData(List<OpinionReply> opinionReplyList) {
							mOpinionReplyList.clear();
							mOpinionReplyList.addAll(opinionReplyList);
							sortCommentList(mOpinionReplyList);
							updateEconomicCircleList(mOpinionReplyList);

						}

						@Override
						public void onFailure(VolleyError volleyError) {
							super.onFailure(volleyError);
							stopRefreshAnimation();
						}
					}).fire();
		}
	}

	private void stopRefreshAnimation() {
		if (mSwipeRefreshLayout.isRefreshing()) {
			mSwipeRefreshLayout.setRefreshing(false);
		}
	}

	private void sortCommentList(List<OpinionReply> opinionReplyList) {
		Collections.sort(opinionReplyList, new Comparator<OpinionReply>() {
			@Override
			public int compare(OpinionReply o1, OpinionReply o2) {
				return Long.valueOf(o2.getCreateTime() - o1.getCreateTime()).intValue();
			}
		});
	}

	private void updateEconomicCircleList(List<OpinionReply> opinionReplyList) {
		if (opinionReplyList == null) {
			stopRefreshAnimation();
			return;
		}

		if (mFootView == null) {
			mFootView = new TextView(getActivity());
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
			mFootView.setPadding(padding, padding, padding, padding);
			mFootView.setText(getText(R.string.load_more));
			mFootView.setGravity(Gravity.CENTER);
			mFootView.setTextColor(Color.WHITE);
			mFootView.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimaryDark));
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mPage++;
					requestOpinionReplyList();
				}
			});
			mMyListView.addFooterView(mFootView);
		}

		if (opinionReplyList.size() < mPageSize) {
			mMyListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mOpinionReplyAdapter != null) {
				mOpinionReplyAdapter.clear();
				mOpinionReplyAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (OpinionReply opinionReply : opinionReplyList) {
			if (mSet.add(opinionReply.getId())) {
				mOpinionReplyAdapter.add(opinionReply);
			}
		}
	}


	private void initView() {
		if (mOpinionDetails != null ) {
			mScrollView.smoothScrollTo(0, 0);
			mUserName.setText(mOpinionDetails.getUserName());
			if (mOpinionDetails.getIsAttention() == 1) {
				mIsAttention.setText(R.string.is_attention);
			}

			mPublishTime.setText(DateUtil.getFormatTime(mOpinionDetails.getCreateTime()));

			if (mOpinionDetails.getDirection() == 1) {
				mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_up));
			} else {
				mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_down));
			}

			mBigVarietyName.setText(mOpinionDetails.getBigVarietyTypeName());
			mVarietyName.setText(mOpinionDetails.getVarietyName());

			mLastPrice.setText("88.88");
			mUpDownPrice.setText("+8.8");
			mUpDownPercent.setText("+10%");

			mLoveNum.setText(String.valueOf(mOpinionDetails.getPraiseCount()));
			mCommentNum.setText(getString(R.string.comment_number, String.valueOf(mOpinionDetails.getReplyCount())));
		}
	}

	static class OpinionReplyAdapter extends ArrayAdapter<OpinionReply> {
		private Context mContext;
		private List<OpinionReply> mOpinionReplyList;

		private OpinionReplyAdapter(Context context, List<OpinionReply> opinionReplyList) {
			super(context, 0);
			this.mContext = context;
			this.mOpinionReplyList = opinionReplyList;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.row_opinion_details, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, (Comment) getItem(position));
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.isAttention)
			TextView mIsAttention;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.opinionContent)
			TextView mOpinionContent;
			@BindView(R.id.loveNum)
			TextView mLoveNum;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(Context context, Comment item) {
				mUserName.setText("刘亦菲");
				mIsAttention.setText("已关注");
				mPublishTime.setText("战国");
				mOpinionContent.setText("话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。话说天下大势，分久必合，合久必分。");
				mLoveNum.setText("8888");
				mLoveNum.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (mLoveNum.isSelected()) {
							mLoveNum.setSelected(false);
						} else {
							mLoveNum.setSelected(true);
						}
					}
				});
			}
		}
	}

	@OnClick({R.id.loveNum, R.id.reply})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.loveNum:
				if (mLoveNum.isSelected()) {
					mLoveNum.setSelected(false);
				} else {
					mLoveNum.setSelected(true);
				}
				break;
			case R.id.reply:
				break;
		}
	}
}
