package com.sbai.finance.activity.home;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
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
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoney;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BorrowMoneyActivity extends BaseActivity implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;

	private List<BorrowMoney> mBorrowMoneyList;
	private BorrowMoneyAdapter mBorrowMoneyAdapter;

	private TextView mFootView;
	private int mPage = 0;
	private int mPageSize = 15;
	private HashSet<String> mSet;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_money);
		ButterKnife.bind(this);
		mBorrowMoneyList = new ArrayList<>();
		mSet = new HashSet<>();
		mBorrowMoneyAdapter = new BorrowMoneyAdapter(this, mBorrowMoneyList);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mBorrowMoneyAdapter);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mBorrowMoneyAdapter.setCallback(new BorrowMoneyAdapter.Callback() {
			@Override
			public void onAvatarBorrowMoneyClick(BorrowMoney borrowMoney) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(BorrowMoneyActivity.this, UserDataActivity.class)
							.putExtra(Launcher.USER_ID, borrowMoney.getUserId())
							.executeForResult(REQ_CODE_USERDATA);
				} else {
					Launcher.with(BorrowMoneyActivity.this, LoginActivity.class).execute();
				}
			}
		});

		requestBorrowMoneyList();
		initSwipeRefreshLayout();
	}

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				requestBorrowMoneyList();
			}
		});
	}

	private void requestBorrowMoneyList() {
		Client.getBorrowMoneyList(mPage, mPageSize).setTag(TAG)
				.setCallback(new Callback2D<Resp<List<BorrowMoney>>, List<BorrowMoney>>() {
					@Override
					protected void onRespSuccessData(List<BorrowMoney> borrowMoneyList) {
						mBorrowMoneyList = borrowMoneyList;
						updateBorrowMoneyList(mBorrowMoneyList);
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

	private void updateBorrowMoneyList(List<BorrowMoney> borrowMoneyList) {
		if (borrowMoneyList == null) {
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
			mFootView.setBackgroundColor(ContextCompat.getColor(this, R.color.split));
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mPage++;
					requestBorrowMoneyList();
				}
			});
			mListView.addFooterView(mFootView, null, true);
		}

		if (borrowMoneyList.size() < mPageSize) {
			mListView.removeFooterView(mFootView);
			mFootView = null;
		}

		if (mSwipeRefreshLayout.isRefreshing()) {
			if (mBorrowMoneyAdapter != null) {
				mBorrowMoneyAdapter.clear();
				mBorrowMoneyAdapter.notifyDataSetChanged();
			}
			stopRefreshAnimation();
		}

		for (BorrowMoney borrowMoney : borrowMoneyList) {
			if (mSet.add(borrowMoney.getId())) {
				mBorrowMoneyAdapter.add(borrowMoney);
			}
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
		BorrowMoney item = (BorrowMoney) parent.getItemAtPosition(position);
		if (item != null) {
			Launcher.with(BorrowMoneyActivity.this, BorrowMoneyDetailsActivity.class)
					.putExtra(Launcher.EX_PAYLOAD, item.getDataId())
					.executeForResult(REQ_CODE_USERDATA);
		}
	}

	static class BorrowMoneyAdapter extends BaseAdapter {

		interface Callback {
			void onAvatarBorrowMoneyClick(BorrowMoney economicCircle);
		}

		private Callback mCallback;
		private Context mContext;
		private List<BorrowMoney> mBorrowMoneyList;

		private BorrowMoneyAdapter(Context context, List<BorrowMoney> borrowMoneyList) {
			this.mContext = context;
			this.mBorrowMoneyList = borrowMoneyList;
		}

		public void setCallback(Callback callback) {
			mCallback = callback;
		}

		public void clear() {
			mBorrowMoneyList.clear();
			notifyDataSetChanged();
		}

		public void add(BorrowMoney borrowMoney) {
			mBorrowMoneyList.add(borrowMoney);
			notifyDataSetChanged();
		}

		public void addAll(List<BorrowMoney> economicCircleList) {
			mBorrowMoneyList.clear();
			mBorrowMoneyList.addAll(economicCircleList);
			notifyDataSetChanged();
		}

		@Override
		public int getCount() {
			return mBorrowMoneyList.size();
		}

		@Override
		public Object getItem(int position) {
			return mBorrowMoneyList.get(position);
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.row_borrow_money, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, (BorrowMoney) getItem(position), mCallback, position);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.divider)
			View mDivider;
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

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(final Context context, final BorrowMoney item, final Callback callback, int position) {
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
					Glide.with(context).load(images[0])
							.placeholder(R.drawable.ic_loading_pic)
							.into(mBorrowingImg);
					if (images.length >= 2) {
						mCircleMoreIcon.setVisibility(View.VISIBLE);
					} else {
						mCircleMoreIcon.setVisibility(View.GONE);
					}
				} else {
					mBorrowingImg.setVisibility(View.GONE);
					mCircleMoreIcon.setVisibility(View.GONE);
				}
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_USERDATA && resultCode == RESULT_OK) {
			if (data != null) {
				WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
						(WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);

				AttentionAndFansNumberModel attentionAndFansNumberModel =
						(AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);

				if (attentionAndFansNumberModel != null && whetherAttentionShieldOrNot != null) {
					for (BorrowMoney borrowMoney : mBorrowMoneyList) {
						if (borrowMoney.getUserId() == attentionAndFansNumberModel.getUserId()) {
							if (whetherAttentionShieldOrNot.isFollow()) {
								borrowMoney.setIsAttention(2);
								mBorrowMoneyAdapter.notifyDataSetChanged();
							} else {
								borrowMoney.setIsAttention(1);
								mBorrowMoneyAdapter.notifyDataSetChanged();
							}
						}
					}

					if (whetherAttentionShieldOrNot.isShield()) {
						for (Iterator it = mBorrowMoneyList.iterator(); it.hasNext(); ) {
							BorrowMoney borrowMoney = (BorrowMoney) it.next();
							if (borrowMoney.getUserId() == attentionAndFansNumberModel.getUserId()) {
								it.remove();
							}
						}
						mBorrowMoneyAdapter.addAll(mBorrowMoneyList);
					}
				}
			}
		}
	}
}
