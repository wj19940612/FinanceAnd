package com.sbai.finance.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mine.EconomicCircleNewMessageActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.EconomicCircle;
import com.sbai.finance.model.economiccircle.NewMessage;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

public class EconomicCircleFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

	private static final int TYPE_BORROW_MONEY = 1;
	private static final int TYPE_OPINION = 0;

	private List<EconomicCircle> mEconomicCircleList;
	private EconomicCircleAdapter mEconomicCircleAdapter;
	private View mFootView;
	private View mNewMessageHeaderView;
	private Long mCreateTime;
	private int mPageSize = 15;
	private HashSet<String> mSet;
	private List<NewMessage> mNewMessageList;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	Unbinder unbinder;

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_economic_circle, container, false);
		unbinder = ButterKnife.bind(this, view);
		return view;
	}

	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
//		addTopPaddingWithStatusBar(mTitleBar);
		mEconomicCircleList = new ArrayList<>();
		mSet = new HashSet<>();
		mEconomicCircleAdapter = new EconomicCircleAdapter(getContext(), mEconomicCircleList);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mEconomicCircleAdapter);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mEconomicCircleAdapter.setCallback(new EconomicCircleAdapter.Callback() {
			@Override
			public void onAvatarOpinionClick(EconomicCircle economicCircle) {
				if (LocalUser.getUser().isLogin()) {
					Intent intent = new Intent(getContext(), UserDataActivity.class);
					intent.putExtra(Launcher.USER_ID, economicCircle.getUserId());
					startActivityForResult(intent, REQ_CODE_USERDATA);
				} else {
					Launcher.with(getContext(), LoginActivity.class).execute();
				}
			}

			@Override
			public void onAvatarBorrowMoneyClick(EconomicCircle economicCircle) {
				if (LocalUser.getUser().isLogin()) {
					Intent intent = new Intent(getContext(), UserDataActivity.class);
					intent.putExtra(Launcher.USER_ID, economicCircle.getUserId());
					startActivityForResult(intent, REQ_CODE_USERDATA);
				} else {
					Launcher.with(getContext(), LoginActivity.class).execute();
				}
			}
		});

		requestEconomicCircleList();
		initSwipeRefreshLayout();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser && isVisible()) {
			mSwipeRefreshLayout.setRefreshing(true);
			mSet.clear();
			mCreateTime = null;
			requestEconomicCircleList();
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (LocalUser.getUser().isLogin()) {
			requestNewMessageCount();
			startScheduleJob(10000);
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopScheduleJob();
	}

	@Override
	public void onTimeUp(int count) {
		super.onTimeUp(count);
		if (LocalUser.getUser().isLogin()) {
			requestNewMessageCount();
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

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EconomicCircle item = (EconomicCircle) parent.getItemAtPosition(position);
		if (item != null) {
			if (item.getType() == 2) {
				Intent intent = new Intent(getContext(), OpinionDetailsActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, item.getDataId());
				startActivityForResult(intent, REQ_CODE_USERDATA);

			} else {
				Intent intent = new Intent(getContext(), BorrowMoneyDetailsActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, item.getDataId());
				startActivityForResult(intent, REQ_CODE_USERDATA);
			}
		}
	}

	private void requestNewMessageCount() {
		Client.getNewMessageCount().setTag(TAG).setCallback(new Callback2D<Resp<List<NewMessage>>, List<NewMessage>>() {
			@Override
			protected void onRespSuccessData(List<NewMessage> newMessageList) {
				mNewMessageList = newMessageList;

				int count = 0;
				for (NewMessage newMessage : mNewMessageList) {
					if (newMessage.getClassify() == 2 || newMessage.getClassify() == 3) {
						count += newMessage.getCount();
					}
				}

				if (mNewMessageHeaderView == null && count > 0) {
					mNewMessageHeaderView = View.inflate(getActivity(), R.layout.view_header_new_message, null);
					mNewMessageHeaderView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Launcher.with(getActivity(), EconomicCircleNewMessageActivity.class).execute();
							mListView.removeHeaderView(mNewMessageHeaderView);
							mNewMessageHeaderView = null;
						}
					});

					mListView.addHeaderView(mNewMessageHeaderView);
					TextView textView = (TextView) mNewMessageHeaderView.findViewById(R.id.newMessageCount);
					textView.setText(getString(R.string.new_message_count, count));
				}

				if (mNewMessageHeaderView != null) {
					TextView textView = (TextView) mNewMessageHeaderView.findViewById(R.id.newMessageCount);
					textView.setText(getString(R.string.new_message_count, count));
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
				requestEconomicCircleList();
			}
		});
	}

	private void requestEconomicCircleList() {
		Client.getEconomicCircleList(mCreateTime, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<EconomicCircle>>, List<EconomicCircle>>() {
					@Override
					protected void onRespSuccessData(List<EconomicCircle> economicCircleList) {
						mEconomicCircleList = economicCircleList;
						updateEconomicCircleList(mEconomicCircleList);
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

	private void updateEconomicCircleList(List<EconomicCircle> economicCircleList) {
		if (economicCircleList == null) {
			stopRefreshAnimation();
			return;
		}

		if (mFootView == null) {
			mFootView = View.inflate(getActivity(), R.layout.view_footer_load_more, null);
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mCreateTime = mEconomicCircleList.get(mEconomicCircleList.size() - 1).getCreateTime();
					requestEconomicCircleList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (economicCircleList.size() < mPageSize) {
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mEconomicCircleAdapter != null) {
				mEconomicCircleAdapter.clear();
				mEconomicCircleAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (EconomicCircle economicCircle : economicCircleList) {
			if (mSet.add(economicCircle.getId())) {
				mEconomicCircleAdapter.add(economicCircle);
			}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_USERDATA && resultCode == RESULT_OK) {
			if (data != null) {
				WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
						(WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);

				AttentionAndFansNumberModel attentionAndFansNumberModel =
						(AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);

				if (attentionAndFansNumberModel != null && whetherAttentionShieldOrNot != null) {
					for (EconomicCircle economicCircle : mEconomicCircleList) {
						if (economicCircle.getUserId() == attentionAndFansNumberModel.getUserId()) {
							if (whetherAttentionShieldOrNot.isFollow()) {
								economicCircle.setIsAttention(2);
								mEconomicCircleAdapter.notifyDataSetChanged();
							} else {
								economicCircle.setIsAttention(1);
								mEconomicCircleAdapter.notifyDataSetChanged();
							}
						}
					}

					if (whetherAttentionShieldOrNot.isShield()) {
						for (Iterator it = mEconomicCircleList.iterator(); it.hasNext(); ) {
							EconomicCircle economicCircle = (EconomicCircle) it.next();
							if (economicCircle.getUserId() == attentionAndFansNumberModel.getUserId()) {
								it.remove();
							}
						}
						mEconomicCircleAdapter.addAll(mEconomicCircleList);
					}
				}
			}
		}
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		unbinder.unbind();
	}

	static class EconomicCircleAdapter extends BaseAdapter {

		interface Callback {
			void onAvatarOpinionClick(EconomicCircle economicCircle);

			void onAvatarBorrowMoneyClick(EconomicCircle economicCircle);
		}

		private Context mContext;
		private Callback mCallback;
		private List<EconomicCircle> mEconomicCircleList;

		private EconomicCircleAdapter(Context context, List<EconomicCircle> economicCircleList) {
			this.mContext = context;
			this.mEconomicCircleList = economicCircleList;
		}

		public void setCallback(Callback callback) {
			mCallback = callback;
		}

		public void clear() {
			mEconomicCircleList.clear();
			notifyDataSetChanged();
		}

		public void add(EconomicCircle economicCircle) {
			mEconomicCircleList.add(economicCircle);
			notifyDataSetChanged();
		}

		public void addAll(List<EconomicCircle> economicCircleList) {
			mEconomicCircleList.clear();
			mEconomicCircleList.addAll(economicCircleList);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mEconomicCircleList.size();
		}

		@Override
		public Object getItem(int position) {
			return mEconomicCircleList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public int getViewTypeCount() {
			return 2;
		}

		@Override
		public int getItemViewType(int position) {
			if (mEconomicCircleList.get(position).getType() == 2) {
				return TYPE_OPINION;
			}
			return TYPE_BORROW_MONEY;
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OpinionViewHolder opinionViewHolder = null;
			BorrowMoneyViewHolder borrowMoneyViewHolder = null;
			int type = getItemViewType(position);
			if (convertView == null) {
				switch (type) {
					case TYPE_OPINION:
						convertView = LayoutInflater.from(mContext).inflate(R.layout.row_economic_circle_opinion, null);
						opinionViewHolder = new OpinionViewHolder(convertView);
						convertView.setTag(R.id.tag_opinion, opinionViewHolder);
						break;

					case TYPE_BORROW_MONEY:
						convertView = LayoutInflater.from(mContext).inflate(R.layout.row_borrow_money, null);
						borrowMoneyViewHolder = new BorrowMoneyViewHolder(convertView);
						convertView.setTag(R.id.tag_borrow_money, borrowMoneyViewHolder);
						break;
				}
			} else {
				switch (type) {
					case TYPE_OPINION:
						opinionViewHolder = (OpinionViewHolder) convertView.getTag(R.id.tag_opinion);
						break;
					case TYPE_BORROW_MONEY:
						borrowMoneyViewHolder = (BorrowMoneyViewHolder) convertView.getTag(R.id.tag_borrow_money);
						break;
				}
			}

			switch (type) {
				case TYPE_OPINION:
					opinionViewHolder.bindingData(mContext, (EconomicCircle) getItem(position), mCallback);
					break;
				case TYPE_BORROW_MONEY:
					borrowMoneyViewHolder.bindingData(mContext, (EconomicCircle) getItem(position), mCallback, position);
					break;
			}

			return convertView;
		}

		static class OpinionViewHolder {
			@BindView(R.id.divider)
			View mDivider;
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
			@BindView(R.id.label)
			ImageView mLabel;
			@BindView(R.id.bigVarietyName)
			TextView mBigVarietyName;
			@BindView(R.id.varietyName)
			TextView mVarietyName;

			OpinionViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(Context context, final EconomicCircle item, final Callback callback) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mUserName.setText(item.getUserName());

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}

				mOpinionContent.setText(item.getContent());

				if (item.getDirection() == 1) {
					if (item.getGuessPass() == 1) {
						mLabel.setBackgroundResource(R.drawable.ic_opinion_up_succeed);
					} else if (item.getGuessPass() == 2) {
						mLabel.setBackgroundResource(R.drawable.ic_opinion_up_failed);
					} else {
						mLabel.setBackgroundResource(R.drawable.ic_opinion_up);
					}
				} else {
					if (item.getGuessPass() == 1) {
						mLabel.setBackgroundResource(R.drawable.ic_opinion_down_succeed);
					} else if (item.getGuessPass() == 2) {
						mLabel.setBackgroundResource(R.drawable.ic_opinion_down_failed);
					} else {
						mLabel.setBackgroundResource(R.drawable.ic_opinion_down);
					}
				}

				mBigVarietyName.setText(context.getString(R.string.big_variety_name, item.getBigVarietyTypeName()));
				mVarietyName.setText(item.getVarietyName());
				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onAvatarOpinionClick(item);
						}
					}
				});
			}
		}

		static class BorrowMoneyViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.location)
			TextView mLocation;
			@BindView(R.id.borrowMoneyContent)
			TextView mBorrowMoneyContent;
			@BindView(R.id.needAmount)
			TextView mNeedAmount;
			@BindView(R.id.borrowDeadline)
			TextView mBorrowDeadline;
			@BindView(R.id.borrowInterest)
			TextView mBorrowInterest;
			@BindView(R.id.isAttention)
			TextView mIsAttention;
			@BindView(R.id.borrowingImg)
			ImageView mBorrowingImg;
			@BindView(R.id.circleMoreIcon)
			ImageView mCircleMoreIcon;


			BorrowMoneyViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(Context context, final EconomicCircle item, final Callback callback, int position) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mUserName.setText(item.getUserName());

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}

				mBorrowMoneyContent.setText(item.getContent());
				mNeedAmount.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getMoney())));
				mBorrowDeadline.setText(context.getString(R.string.day, FinanceUtil.formatWithScaleNoZero(item.getDays())));
				mBorrowInterest.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getInterest())));

				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				if (TextUtils.isEmpty(item.getLand())) {
					mLocation.setText(R.string.no_location_information);
				} else {
					mLocation.setText(item.getLand());
				}

				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onAvatarBorrowMoneyClick(item);
						}
					}
				});

				if (!TextUtils.isEmpty(item.getContentImg())) {
					String[] images = item.getContentImg().split(",");
					if (images.length >= 2) {
						mCircleMoreIcon.setVisibility(View.VISIBLE);
					} else {
						mCircleMoreIcon.setVisibility(View.GONE);
					}
					Glide.with(context).load(images[0])
							.placeholder(R.drawable.ic_loading_pic)
							.into(mBorrowingImg);
				} else {
					mBorrowingImg.setVisibility(View.GONE);
					mCircleMoreIcon.setVisibility(View.GONE);
				}
			}
		}
	}
}
