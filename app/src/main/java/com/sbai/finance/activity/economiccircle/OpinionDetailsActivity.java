package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
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
import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.model.economiccircle.OpinionReply;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.MyListView;

import java.util.ArrayList;
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
	@BindView(R.id.commentContent)
	EditText mCommentContent;
	@BindView(R.id.reply)
	TextView mReply;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	private List<OpinionReply.DataBean> mOpinionReplyList;
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

		mOpinionReplyList = new ArrayList<>();
		mSet = new HashSet<>();
		mOpinionReplyAdapter = new OpinionReplyAdapter(this, mOpinionReplyList);
		mMyListView.setEmptyView(mEmpty);
		mMyListView.setAdapter(mOpinionReplyAdapter);

		requestOpinionReplyList();
		initSwipeRefreshLayout();
	}

	private void initData(Intent intent) {
		mOpinionDetails = (OpinionDetails) intent.getSerializableExtra(Launcher.EX_PAYLOAD);
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				requestOpinionReplyList();
			}
		});
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.i(TAG, "onDestroy: xxx");
	}

	private void requestOpinionReplyList() {
		if (mOpinionDetails != null) {
			Client.getOpinionReplyList(mPage, mPageSize, mOpinionDetails.getId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<OpinionReply>, OpinionReply>() {
						@Override
						protected void onRespSuccessData(OpinionReply opinionReply) {
							updateEconomicCircleList(opinionReply.getData());
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

	private void updateEconomicCircleList(List<OpinionReply.DataBean> opinionReplyList) {
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
			mFootView.setTextColor(ContextCompat.getColor(this, R.color.greyAssist));
			mFootView.setBackgroundColor(ContextCompat.getColor(this, R.color.greyLightAssist));
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
			}
			stopRefreshAnimation();
		}

		for (OpinionReply.DataBean opinionReply : opinionReplyList) {
			if (mSet.add(opinionReply.getId())) {
				mOpinionReplyAdapter.add(opinionReply);
			}
		}
	}


	private void initView() {
		if (mOpinionDetails != null) {

			mUserName.setText(mOpinionDetails.getUserName());

			Glide.with(this).load(mOpinionDetails.getUserPortrait())
					.placeholder(R.drawable.ic_default_avatar)
					.bitmapTransform(new GlideCircleTransform(this))
					.into(mAvatar);

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

			mScrollView.smoothScrollTo(0, 0);
		}
	}

	static class OpinionReplyAdapter extends ArrayAdapter<OpinionReply.DataBean> {
		private Context mContext;
		private List<OpinionReply.DataBean> mOpinionReplyList;

		private OpinionReplyAdapter(Context context, List<OpinionReply.DataBean> opinionReplyList) {
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
			viewHolder.bindingData(mContext, getItem(position));
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

			private void bindingData(Context context, OpinionReply.DataBean item) {
				mUserName.setText(item.getUserName());

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				if (item.getIsAttention() == 1) {
					mIsAttention.setText(R.string.is_attention);
				}

				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				mOpinionContent.setText(item.getContent());
				mLoveNum.setText(String.valueOf(item.getPraiseCount()));
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

				Client.opinionPraise(mOpinionDetails.getId()).setTag(TAG)
						.setCallback(new Callback<Resp<JsonPrimitive>>() {
							@Override
							protected void onRespSuccess(Resp<JsonPrimitive> resp) {
								int praiseCount = mOpinionDetails.getPraiseCount();
								if (resp.isSuccess()) {
									if (mLoveNum.isSelected()) {
										mLoveNum.setSelected(false);
										mLoveNum.setText(String.valueOf(Integer.parseInt(mLoveNum.getText().toString()) - 1));
									} else {
										mLoveNum.setSelected(true);
										mLoveNum.setText(String.valueOf(Integer.parseInt(mLoveNum.getText().toString()) + 1));
									}
								}
							}
						}).fire();

				break;
			case R.id.reply:
				String commentContent = mCommentContent.getText().toString().trim();
				if (TextUtils.isEmpty(commentContent)) {
					ToastUtil.curt("评论内容不能为空");
					return;
				}

				Client.opinionReply(commentContent, mOpinionDetails.getId())
						.setTag(TAG)
						.setIndeterminate(this)
						.setCallback(new Callback<Resp<JsonObject>>() {
							@Override
							protected void onRespSuccess(Resp<JsonObject> resp) {
								if (resp.isSuccess()) {
									mSet.clear();
									mPage = 0;
									mSwipeRefreshLayout.setRefreshing(true);
									requestOpinionReplyList();
									mCommentContent.setText("");
									mScrollView.smoothScrollTo(0, 0);
								}
							}
						}).fire();
				break;
		}
	}
}
