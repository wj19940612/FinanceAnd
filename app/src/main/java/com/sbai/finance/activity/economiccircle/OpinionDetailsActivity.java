package com.sbai.finance.activity.economiccircle;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.PublishActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.economiccircle.OpinionDetails;
import com.sbai.finance.model.economiccircle.OpinionReply;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
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
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.activity.trade.PublishOpinionActivity.REFRESH_POINT;


public class OpinionDetailsActivity extends BaseActivity {

	public static final String REFRESH_ATTENTION = "refresh_point";

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
	@BindView(R.id.variety)
	LinearLayout mVariety;
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

	private OpinionReplyAdapter mOpinionReplyAdapter;
	private OpinionDetails mOpinionDetails;
	private List<OpinionReply> mOpinionReplyList;
	private TextView mFootView;
	private RefreshAttentionReceiver mReceiver;

	private Long mCreateTime;
	private int mPageSize = 15;
	private HashSet<Integer> mSet;
	private int mDataId;
	private int mReplyId = -1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_opinion_details);
		ButterKnife.bind(this);

		initData(getIntent());

		mSet = new HashSet<>();
		mOpinionReplyList = new ArrayList<>();
		mOpinionReplyAdapter = new OpinionReplyAdapter(this, mOpinionReplyList);
		mMyListView.setEmptyView(mEmpty);
		mMyListView.setAdapter(mOpinionReplyAdapter);

		requestOpinionDetails(false);
		initSwipeRefreshLayout();

		registerRefreshReceiver();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
		mReplyId = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
	}

	private void requestOpinionDetails(final boolean isSendBroadcast) {
		Client.getOpinionDetails(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<OpinionDetails>, OpinionDetails>() {
					@Override
					protected void onRespSuccessData(OpinionDetails opinionDetails) {
						mOpinionDetails = opinionDetails;
						updateOpinionDetails();
						requestOpinionReplyList();

						if (isSendBroadcast) {
							Intent intent = new Intent(REFRESH_POINT);
							intent.putExtra(Launcher.EX_PAYLOAD, mOpinionDetails);
							LocalBroadcastManager.getInstance(OpinionDetailsActivity.this)
									.sendBroadcast(intent);
//                            setResult(RESULT_OK, intent);
						}
					}
				}).fire();
	}


	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mCreateTime = null;
				if (mReplyId != -1) {
					mReplyId = -1;
				}
				requestOpinionDetails(false);
			}
		});
	}

	private void requestOpinionReplyList() {
		if (mOpinionDetails != null) {
			Client.getOpinionReplyList(mCreateTime, mPageSize, mOpinionDetails.getId(),
					mReplyId != -1 ? mReplyId : null).setTag(TAG)
					.setCallback(new Callback2D<Resp<List<OpinionReply>>, List<OpinionReply>>() {
						@Override
						protected void onRespSuccessData(List<OpinionReply> opinionReplyList) {
							mOpinionReplyList = opinionReplyList;
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
			mFootView.setTextColor(ContextCompat.getColor(this, R.color.greyAssist));
			mFootView.setBackgroundColor(ContextCompat.getColor(this, R.color.greyLightAssist));
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mCreateTime = mOpinionReplyList.get(mOpinionReplyList.size() - 1).getCreateTime();
					;
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

		for (OpinionReply opinionReply : opinionReplyList) {
			if (mSet.add(opinionReply.getId())) {
				mOpinionReplyAdapter.add(opinionReply);
			}
		}
	}


	private void updateOpinionDetails() {
		if (mOpinionDetails != null) {

			mUserName.setText(mOpinionDetails.getUserName());

			Glide.with(this).load(mOpinionDetails.getUserPortrait())
					.placeholder(R.drawable.ic_default_avatar)
					.bitmapTransform(new GlideCircleTransform(this))
					.into(mAvatar);

			if (mOpinionDetails.getIsAttention() == 2) {
				mIsAttention.setText(R.string.is_attention);
			} else {
				mIsAttention.setText("");
			}

			mPublishTime.setText(DateUtil.getFormatTime(mOpinionDetails.getCreateTime()));

			if (mOpinionDetails.getDirection() == 1) {
				if (mOpinionDetails.getGuessPass() == 1) {
					mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_up_succeed));
				} else if (mOpinionDetails.getGuessPass() == 2) {
					mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_up_failed));
				} else {
					mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_up));
				}
			} else {
				if (mOpinionDetails.getGuessPass() == 1) {
					mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_down_succeed));
				} else if (mOpinionDetails.getGuessPass() == 2) {
					mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_down_failed));
				} else {
					mOpinionContent.setText(StrUtil.mergeTextWithImage(this, mOpinionDetails.getContent(), R.drawable.ic_opinion_down));
				}
			}

			mBigVarietyName.setText(mOpinionDetails.getBigVarietyTypeName());
			mVarietyName.setText(mOpinionDetails.getVarietyName());

			/*if (TextUtils.isEmpty(mOpinionDetails.getLastPrice())) {
			    mLastPrice.setText("--");
				mLastPrice.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
			} else {
				if (mOpinionDetails.getRisePrice().startsWith("-")) {
					mLastPrice.setTextColor(ContextCompat.getColor(this, R.color.greenPrimary));
				} else {
					mLastPrice.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
				}
				mLastPrice.setText(mOpinionDetails.getLastPrice());
			}

			if (TextUtils.isEmpty(mOpinionDetails.getRisePrice())) {
				mUpDownPrice.setText("--");
				mUpDownPrice.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
			} else {
				if (mOpinionDetails.getRisePrice().startsWith("-")) {
					mUpDownPrice.setTextColor(ContextCompat.getColor(this, R.color.greenPrimary));
				} else {
					mUpDownPrice.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
				}
				mUpDownPrice.setText(mOpinionDetails.getRisePrice());
			}

			if (TextUtils.isEmpty(mOpinionDetails.getRisePre())) {
				mUpDownPercent.setText("--");
				mUpDownPercent.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
			} else {
				if (mOpinionDetails.getRisePre().startsWith("-")) {
					mUpDownPercent.setTextColor(ContextCompat.getColor(this, R.color.greenPrimary));
				} else {
					mUpDownPercent.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
				}
				mUpDownPercent.setText(mOpinionDetails.getRisePre());
			}*/


			if (mOpinionDetails.getIsPraise() == 1) {
				mLoveNum.setSelected(true);
			} else {
				mLoveNum.setSelected(false);
			}
			if (mOpinionDetails.getPraiseCount() > 999) {
				mLoveNum.setText("999+");
			} else {
				mLoveNum.setText(String.valueOf(mOpinionDetails.getPraiseCount()));
			}
			if (mOpinionDetails.getReplyCount() > 999) {
				mCommentNum.setText("(999+)");
			} else {
				mCommentNum.setText(getString(R.string.comment_number, String.valueOf(mOpinionDetails.getReplyCount())));
			}
			mScrollView.smoothScrollTo(0, 0);
		}
	}

	@OnClick(R.id.variety)
	public void onViewClicked() {
		if (getCallingActivity() != null) {
			String className = getCallingActivity().getClassName();
			if (!TextUtils.isEmpty(className)) {
				if (className.equalsIgnoreCase(FutureTradeActivity.class.getName())
						|| className.equalsIgnoreCase(StockDetailActivity.class.getName())) {
					return;
				}
			}
		}

		if (mOpinionDetails != null) {
			Client.getVarietyDetails(mOpinionDetails.getVarietyId()).setTag(TAG)
					.setCallback(new Callback2D<Resp<Variety>, Variety>() {
						@Override
						protected void onRespSuccessData(Variety variety) {
							Log.d(TAG, "onRespSuccessData: " + variety.toString());
							if (Variety.VAR_FUTURE.equals(mOpinionDetails.getBigVarietyTypeCode())) {
								Launcher.with(OpinionDetailsActivity.this, FutureTradeActivity.class)
										.putExtra(Launcher.EX_PAYLOAD, variety)
										.execute();
							} else {
								Launcher.with(OpinionDetailsActivity.this, StockDetailActivity.class)
										.putExtra(Launcher.EX_PAYLOAD, variety)
										.execute();
							}
						}
					}).fire();
		}
	}

	static class OpinionReplyAdapter extends BaseAdapter {

		private Context mContext;
		private List<OpinionReply> mOpinionReplyList;

		private OpinionReplyAdapter(Context context, List<OpinionReply> opinionReplyList) {
			this.mContext = context;
			this.mOpinionReplyList = opinionReplyList;
		}

		public void clear() {
			mOpinionReplyList.clear();
			notifyDataSetChanged();
		}

		public void add(OpinionReply opinionReply) {
			mOpinionReplyList.add(opinionReply);
			notifyDataSetChanged();
		}

		public void addAll(List<OpinionReply> opinionReplyList) {
			mOpinionReplyList.clear();
			mOpinionReplyList.addAll(opinionReplyList);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mOpinionReplyList.size();
		}

		@Override
		public Object getItem(int position) {
			return mOpinionReplyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
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
			viewHolder.bindingData(mContext, (OpinionReply) getItem(position));
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

			private void bindingData(final Context context, final OpinionReply item) {
				mUserName.setText(item.getUserName());

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (LocalUser.getUser().isLogin()) {
							Launcher.with(context, UserDataActivity.class)
									.putExtra(Launcher.USER_ID, item.getUserId())
									.execute();
						} else {
							Launcher.with(context, LoginActivity.class).execute();
						}
					}
				});

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}

				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				mOpinionContent.setText(item.getContent());

				if (item.getIsPraise() == 1) {
					mLoveNum.setSelected(true);
				} else {
					mLoveNum.setSelected(false);
				}
				if (item.getPraiseCount() > 999) {
					mLoveNum.setText("999+");
				} else {
					mLoveNum.setText(String.valueOf(item.getPraiseCount()));
				}
				mLoveNum.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (LocalUser.getUser().isLogin()) {
							Client.opinionReplyPraise(item.getId())
									.setCallback(new Callback<Resp<JsonPrimitive>>() {
										@Override
										protected void onRespSuccess(Resp<JsonPrimitive> resp) {
											if (resp.isSuccess()) {
												if (mLoveNum.isSelected()) {
													mLoveNum.setSelected(false);
													int praiseCount = item.getPraiseCount() - 1;
													item.setPraiseCount(praiseCount);
													if (praiseCount < 1000) {
														mLoveNum.setText(String.valueOf(praiseCount));
													} else if (item.getPraiseCount() > 999) {
														mLoveNum.setText(R.string.number999);
													}
												} else {
													mLoveNum.setSelected(true);
													int praiseCount = item.getPraiseCount() + 1;
													item.setPraiseCount(praiseCount);
													if (praiseCount < 1000) {
														mLoveNum.setText(String.valueOf(praiseCount));
													} else if (item.getPraiseCount() > 999) {
														mLoveNum.setText(R.string.number999);
													}
												}
											}
										}
									}).fire();
						} else {
							Launcher.with(context, LoginActivity.class).execute();
						}
					}
				});
			}
		}
	}

	@OnClick({R.id.loveNum, R.id.commentContent, R.id.reply, R.id.avatar})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.loveNum:
				if (LocalUser.getUser().isLogin()) {
					Client.opinionPraise(mOpinionDetails.getId()).setTag(TAG)
							.setCallback(new Callback<Resp<JsonPrimitive>>() {
								@Override
								protected void onRespSuccess(Resp<JsonPrimitive> resp) {
									if (resp.isSuccess()) {
										if (mLoveNum.isSelected()) {
											mLoveNum.setSelected(false);
											int praiseCount = mOpinionDetails.getPraiseCount() - 1;
											mOpinionDetails.setPraiseCount(praiseCount);
											if (praiseCount < 1000) {
												mLoveNum.setText(String.valueOf(praiseCount));
											} else if (mOpinionDetails.getPraiseCount() > 999) {
												mLoveNum.setText(R.string.number999);
											}
										} else {
											mLoveNum.setSelected(true);
											int praiseCount = mOpinionDetails.getPraiseCount() + 1;
											mOpinionDetails.setPraiseCount(praiseCount);
											if (praiseCount < 1000) {
												mLoveNum.setText(String.valueOf(praiseCount));
											} else if (mOpinionDetails.getPraiseCount() > 999) {
												mLoveNum.setText(R.string.number999);
											}
										}
										Intent intent = new Intent(REFRESH_POINT);
										intent.putExtra(Launcher.EX_PAYLOAD, mOpinionDetails);
										LocalBroadcastManager.getInstance(OpinionDetailsActivity.this)
												.sendBroadcast(intent);

									}
								}
							}).fire();
				} else {
					Launcher.with(this, LoginActivity.class).execute();
				}
				break;

			case R.id.reply:
				if (LocalUser.getUser().isLogin()) {
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
										mCreateTime = null;
										mSwipeRefreshLayout.setRefreshing(true);
										requestOpinionReplyList();
										requestOpinionDetails(true);
										mCommentContent.setText("");
										mScrollView.smoothScrollTo(0, 0);
									}
								}
							}).fire();
				} else {
					Launcher.with(this, LoginActivity.class).execute();
				}
				break;

			case R.id.avatar:
				if (LocalUser.getUser().isLogin()) {
					ComponentName callingActivity = getCallingActivity();
					if (callingActivity != null && callingActivity.getClassName().equalsIgnoreCase(PublishActivity.class.getName())) {
						return;
					}
					Launcher.with(this, UserDataActivity.class)
							.putExtra(Launcher.USER_ID, mOpinionDetails.getUserId())
							.execute();
				} else {
					Launcher.with(this, LoginActivity.class).execute();
				}
				break;
		}
	}

	private void registerRefreshReceiver() {
		mReceiver = new RefreshAttentionReceiver();
		IntentFilter filter = new IntentFilter(REFRESH_ATTENTION);
		LocalBroadcastManager.getInstance(this).registerReceiver(mReceiver, filter);
	}

	private class RefreshAttentionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
					(WhetherAttentionShieldOrNot) intent.getSerializableExtra(Launcher.EX_PAYLOAD_1);

			AttentionAndFansNumberModel attentionAndFansNumberModel =
					(AttentionAndFansNumberModel) intent.getSerializableExtra(Launcher.EX_PAYLOAD_2);


			if (whetherAttentionShieldOrNot != null) {
				if (whetherAttentionShieldOrNot.isFollow()) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}
			}

			if (attentionAndFansNumberModel != null && whetherAttentionShieldOrNot != null) {
				for (OpinionReply opinionReply : mOpinionReplyList) {
					if (opinionReply.getUserId() == attentionAndFansNumberModel.getUserId()) {
						if (whetherAttentionShieldOrNot.isFollow()) {
							opinionReply.setIsAttention(2);
							mOpinionReplyAdapter.notifyDataSetChanged();
						} else {
							opinionReply.setIsAttention(1);
							mOpinionReplyAdapter.notifyDataSetChanged();
						}
						break;
					}
				}

				if (whetherAttentionShieldOrNot.isShield()) {
					for (Iterator it = mOpinionReplyList.iterator(); it.hasNext(); ) {
						OpinionReply opinionReply = (OpinionReply) it.next();
						if (opinionReply.getUserId() == attentionAndFansNumberModel.getUserId()) {
							it.remove();
						}
					}

					mOpinionReplyAdapter.addAll(mOpinionReplyList);
					mOpinionReplyAdapter.notifyDataSetChanged();
					mCommentNum.setText(getString(R.string.comment_number, String.valueOf(mOpinionReplyList.size())));
				}
			}
		}
	}
}
