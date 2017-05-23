package com.sbai.finance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
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
import com.sbai.finance.activity.economiccircle.BorrowMoneyDetailsActivity;
import com.sbai.finance.activity.economiccircle.ContentImgActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.EconomicCircle;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.activity.economiccircle.OpinionDetailsActivity.REFRESH_ATTENTION;

public class EconomicCircleFragment extends BaseFragment implements AbsListView.OnScrollListener {

	private static final int TYPE_BORROW_MONEY = 1;
	private static final int TYPE_OPINION = 0;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.swipeRefreshLayout)
	SwipeRefreshLayout mSwipeRefreshLayout;
	Unbinder unbinder;

	private List<EconomicCircle> mEconomicCircleList;
	private EconomicCircleAdapter mEconomicCircleAdapter;
	private TextView mFootView;
	private RefreshAttentionReceiver mReceiver;

	private int mPage = 0;
	private int mPageSize = 15;
	private HashSet<String> mSet;

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
		addTopPaddingWithStatusBar(mTitleBar);
		mEconomicCircleList = new ArrayList<>();
		mSet = new HashSet<>();
		mEconomicCircleAdapter = new EconomicCircleAdapter(getContext(), mEconomicCircleList);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mEconomicCircleAdapter);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				EconomicCircle economicCircle = (EconomicCircle) parent.getItemAtPosition(position);
				if (economicCircle.getType() == 2) {
					Launcher.with(getContext(), OpinionDetailsActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, economicCircle.getDataId())
							.execute();
				} else {
					Launcher.with(getContext(), BorrowMoneyDetailsActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, economicCircle.getDataId())
							.execute();
				}
			}
		});

		mEconomicCircleAdapter.setCallback(new EconomicCircleAdapter.Callback() {
			@Override
			public void onAvatarOpinionClick(EconomicCircle economicCircle) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getContext(), UserDataActivity.class)
							.putExtra(Launcher.USER_ID, economicCircle.getUserId())
							.execute();
				} else {
					Launcher.with(getContext(), LoginActivity.class).execute();
				}
			}

			@Override
			public void onAvatarBorrowMoneyClick(EconomicCircle economicCircle) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getContext(), UserDataActivity.class)
							.putExtra(Launcher.USER_ID, economicCircle.getUserId())
							.execute();
				} else {
					Launcher.with(getContext(), LoginActivity.class).execute();
				}
			}
		});

		requestEconomicCircleList();
		initSwipeRefreshLayout();

		registerRefreshReceiver();
	}

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		if (isVisibleToUser && isVisible()) {
			mSwipeRefreshLayout.setRefreshing(true);
			mSet.clear();
			mPage = 0;
			requestEconomicCircleList();
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

	private void initSwipeRefreshLayout() {
		mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
			@Override
			public void onRefresh() {
				mSet.clear();
				mPage = 0;
				requestEconomicCircleList();
			}
		});
	}

	private void requestEconomicCircleList() {
		Client.getEconomicCircleList(mPage, mPageSize).setTag(TAG)
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
			mFootView = new TextView(getActivity());
			int padding = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 16, getResources().getDisplayMetrics());
			mFootView.setPadding(padding, padding, padding, padding);
			mFootView.setText(getText(R.string.load_more));
			mFootView.setGravity(Gravity.CENTER);
			mFootView.setTextColor(ContextCompat.getColor(getContext(), R.color.greyAssist));
			mFootView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.greyLightAssist));
			mFootView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					if (mSwipeRefreshLayout.isRefreshing()) return;
					mPage++;
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
					borrowMoneyViewHolder.bindingData(mContext, (EconomicCircle) getItem(position), mCallback);
					break;
			}

			return convertView;
		}

		static class OpinionViewHolder {
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
				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));

				if (item.getDirection() == 1) {
					if (item.getGuessPass() == 1) {
						mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_up_succeed));
					} else if (item.getGuessPass() == 2) {
						mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_up_failed));
					} else {
						mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_up));
					}

				} else {
					if (item.getGuessPass() == 1) {
						mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_down_succeed));
					} else if (item.getGuessPass() == 2) {
						mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_down_failed));
					} else {
						mOpinionContent.setText(StrUtil.mergeTextWithImage(context, item.getContent(), R.drawable.ic_opinion_down));
					}
				}

				mBigVarietyName.setText(item.getBigVarietyTypeName());
				mVarietyName.setText(item.getVarietyName());

			/*	if (TextUtils.isEmpty(item.getLastPrice())) {
					mLastPrice.setText("--");
					mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
				} else {
					if (item.getRisePrice().startsWith("-")) {
						mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
					} else {
						mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
					}
					mLastPrice.setText(item.getLastPrice());
				}

				if (TextUtils.isEmpty(item.getRisePrice())) {
					mUpDownPrice.setText("");
				} else {
					if (item.getRisePrice().startsWith("-")) {
						mUpDownPrice.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
					} else {
						mUpDownPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
					}
					mUpDownPrice.setText(item.getRisePrice());
				}

				if (TextUtils.isEmpty(item.getRisePre())) {
					mUpDownPercent.setText("");
				} else {
					if (item.getRisePre().startsWith("-")) {
						mUpDownPercent.setTextColor(ContextCompat.getColor(context, R.color.greenPrimary));
					} else {
						mUpDownPercent.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
					}
					mUpDownPercent.setText(item.getRisePre());
				}*/


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
			@BindView(R.id.borrowTime)
			TextView mBorrowTime;
			@BindView(R.id.borrowInterest)
			TextView mBorrowInterest;
			@BindView(R.id.isAttention)
			TextView mIsAttention;
			@BindView(R.id.contentImg)
			LinearLayout mContentImg;
			@BindView(R.id.image1)
			ImageView mImage1;
			@BindView(R.id.image2)
			ImageView mImage2;
			@BindView(R.id.image3)
			ImageView mImage3;
			@BindView(R.id.image4)
			ImageView mImage4;


			BorrowMoneyViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(Context context, final EconomicCircle item, final Callback callback) {
				if (item == null) return;
				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mUserName.setText(item.getUserName());
				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}

				if (TextUtils.isEmpty(item.getLand())) {
					mLocation.setText(R.string.no_location_information);
				} else {
					mLocation.setText(item.getLand());
				}

				mNeedAmount.setText(context.getString(R.string.RMB, String.valueOf(FinanceUtil.formatWithScaleNoZero(item.getMoney()))));
				mBorrowTime.setText(context.getString(R.string.day, String.valueOf(FinanceUtil.formatWithScaleNoZero(item.getDays()))));
				mBorrowInterest.setText(context.getString(R.string.RMB, String.valueOf(FinanceUtil.formatWithScaleNoZero(item.getInterest()))));
				mBorrowMoneyContent.setText(item.getContent());


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
					switch (images.length) {
						case 1:
							mContentImg.setVisibility(View.VISIBLE);
							mImage1.setVisibility(View.VISIBLE);
							loadImage(context, images[0], mImage1);
							mImage2.setVisibility(View.INVISIBLE);
							mImage3.setVisibility(View.INVISIBLE);
							mImage4.setVisibility(View.INVISIBLE);
							imageClick(context, images, mImage1, 0);
							break;
						case 2:
							mContentImg.setVisibility(View.VISIBLE);
							mImage1.setVisibility(View.VISIBLE);
							loadImage(context, images[0], mImage1);
							mImage2.setVisibility(View.VISIBLE);
							loadImage(context, images[1], mImage2);
							mImage3.setVisibility(View.INVISIBLE);
							mImage4.setVisibility(View.INVISIBLE);
							imageClick(context, images, mImage1, 0);
							imageClick(context, images, mImage2, 1);
							break;
						case 3:
							mContentImg.setVisibility(View.VISIBLE);
							mImage1.setVisibility(View.VISIBLE);
							loadImage(context, images[0], mImage1);
							mImage2.setVisibility(View.VISIBLE);
							loadImage(context, images[1], mImage2);
							mImage3.setVisibility(View.VISIBLE);
							loadImage(context, images[2], mImage3);
							mImage4.setVisibility(View.INVISIBLE);
							imageClick(context, images, mImage1, 0);
							imageClick(context, images, mImage2, 1);
							imageClick(context, images, mImage3, 2);
							break;
						case 4:

							mImage1.setVisibility(View.VISIBLE);
							loadImage(context, images[0], mImage1);
							mImage2.setVisibility(View.VISIBLE);
							loadImage(context, images[1], mImage2);
							mImage3.setVisibility(View.VISIBLE);
							loadImage(context, images[2], mImage3);
							mImage4.setVisibility(View.VISIBLE);
							loadImage(context, images[3], mImage4);
							imageClick(context, images, mImage1, 0);
							imageClick(context, images, mImage2, 1);
							imageClick(context, images, mImage3, 2);
							imageClick(context, images, mImage4, 3);
							break;
						default:
							break;
					}
				} else {
					mContentImg.setVisibility(View.GONE);
				}
			}

			private void loadImage(Context context, String src, ImageView image) {
				Glide.with(context)
						.load(src)
						.placeholder(R.drawable.img_loading)
						.error(R.drawable.logo_login)
						.into(image);
			}

			private void imageClick(final Context context, final String[] images,
			                        ImageView imageView, final int i) {
				imageView.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Intent intent = new Intent(context, ContentImgActivity.class);
						intent.putExtra(Launcher.EX_PAYLOAD, images);
						intent.putExtra(Launcher.EX_PAYLOAD_1, i);
						context.startActivity(intent);
					}
				});
			}
		}
	}

	private void registerRefreshReceiver() {
		mReceiver = new RefreshAttentionReceiver();
		IntentFilter filter = new IntentFilter(REFRESH_ATTENTION);
		LocalBroadcastManager.getInstance(getContext()).registerReceiver(mReceiver, filter);
	}

	private class RefreshAttentionReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
					(WhetherAttentionShieldOrNot) intent.getSerializableExtra(Launcher.EX_PAYLOAD_1);

			AttentionAndFansNumberModel attentionAndFansNumberModel =
					(AttentionAndFansNumberModel) intent.getSerializableExtra(Launcher.EX_PAYLOAD_2);

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
