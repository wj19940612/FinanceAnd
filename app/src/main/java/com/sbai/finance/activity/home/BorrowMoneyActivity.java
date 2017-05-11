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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoney;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BorrowMoneyActivity extends BaseActivity implements AbsListView.OnScrollListener {

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
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				BorrowMoney borrowMoney = (BorrowMoney) parent.getItemAtPosition(position);
				Launcher.with(BorrowMoneyActivity.this, BorrowMoneyDetailsActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, borrowMoney.getDataId())
						.execute();
			}
		});

		mBorrowMoneyAdapter.setCallback(new BorrowMoneyAdapter.Callback() {
			@Override
			public void onAvatarBorrowMoneyClick(BorrowMoney borrowMoney) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(BorrowMoneyActivity.this, UserDataActivity.class).execute();
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
			mFootView.setBackgroundColor(ContextCompat.getColor(this, R.color.greyLightAssist));
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mPage++;
					requestBorrowMoneyList();
				}
			});
			mListView.addFooterView(mFootView);
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
			viewHolder.bindingData(mContext, (BorrowMoney) getItem(position), mCallback);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.location)
			TextView mLocation;
			@BindView(R.id.needAmount)
			TextView mNeedAmount;
			@BindView(R.id.borrowTime)
			TextView mBorrowTime;
			@BindView(R.id.borrowInterest)
			TextView mBorrowInterest;
			@BindView(R.id.borrowMoneyContent)
			TextView mBorrowMoneyContent;
			@BindView(R.id.contentImg)
			LinearLayout mContentImg;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(final Context context, final BorrowMoney item, final Callback callback) {
				if (item == null) return;
				mUserName.setText(item.getUserName());
				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				if (TextUtils.isEmpty(item.getLand())) {
					mLocation.setText(R.string.no_location_information);
				} else {
					mLocation.setText(item.getLand());
				}

				mNeedAmount.setText(context.getString(R.string.RMB, String.valueOf(item.getMoney())));
				mBorrowTime.setText(context.getString(R.string.day, String.valueOf(item.getDays())));
				mBorrowInterest.setText(context.getString(R.string.RMB, String.valueOf(item.getInterest())));
				mBorrowMoneyContent.setText(item.getContent());
				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.onAvatarBorrowMoneyClick(item);
						}
					}
				});

				int screenWidth = context.getResources().getDisplayMetrics().widthPixels;

				int width = (screenWidth) / 4;
				int margin = context.getResources().getDimensionPixelSize(R.dimen.common_split);
				 final String[] contentImgArray = item.getContentImg().split(",");
				for (int i = 0; i < contentImgArray.length; i++) {
					ImageView imageView = new ImageView(context);
					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
					//params.leftMargin = (i == 0 ? 0 : margin);

					Glide.with(context).load(contentImgArray[i])
							.placeholder(R.drawable.help)
							.into(imageView);
					imageView.setLayoutParams(params);
					mContentImg.addView(imageView);

					final int temp = i;
					imageView.setOnClickListener(new View.OnClickListener() {
						@Override
						public void onClick(View v) {
							Intent intent = new Intent(context,ContentImgActivity.class);
							intent.putExtra("urlList", contentImgArray);
							intent.putExtra("currentItem",temp);
							context.startActivity(intent);
						}
					});
				}
			}
		}
	}
}
