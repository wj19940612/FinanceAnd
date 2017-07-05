package com.sbai.finance.fragment;

import android.app.Dialog;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.battle.BattleActivity;
import com.sbai.finance.activity.economiccircle.OpinionDetailsActivity;
import com.sbai.finance.activity.mine.EconomicCircleNewMessageActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.mine.cornucopia.CornucopiaActivity;
import com.sbai.finance.activity.mutual.BorrowDetailsActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.economiccircle.EconomicCircle;
import com.sbai.finance.model.economiccircle.NewMessage;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnNoReadNewsListener;
import com.sbai.finance.view.BattleProgress;
import com.sbai.finance.view.CollapsedTextLayout;
import com.sbai.finance.view.CollapsedTextView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.httplib.ApiCallback;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.activity.battle.BattleListActivity.CANCEL_BATTLE;

public class EconomicCircleFragment extends BaseFragment implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener, View.OnClickListener {

	private static final int TYPE_OPINION = 0;
	private static final int TYPE_BORROW_MONEY = 1;
	private static final int TYPE_FUTURES_BATTLE = 2;

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

	private OnNoReadNewsListener mOnNoReadNewsListener;
	private Battle mBattle;

	@Override
	public void onAttach(Context context) {
		super.onAttach(context);
		if (context instanceof OnNoReadNewsListener) {
			mOnNoReadNewsListener = (OnNoReadNewsListener) context;
		}
	}

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
		mBattle = new Battle();
		mEconomicCircleAdapter = new EconomicCircleAdapter(getContext(), mEconomicCircleList);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mEconomicCircleAdapter);
		mListView.setOnScrollListener(this);
		mListView.setOnItemClickListener(this);
		mTitleBar.setOnTitleBarClickListener(this);
		mEconomicCircleAdapter.setCallback(new EconomicCircleAdapter.Callback() {
			@Override
			public void HotAreaClick(EconomicCircle economicCircle) {
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
		} else {
			//退出登陆时
			if (mNewMessageHeaderView != null) {
				mListView.removeHeaderView(mNewMessageHeaderView);
				mNewMessageHeaderView = null;
				if (mOnNoReadNewsListener != null) {
					mOnNoReadNewsListener.onNoReadNewsNumber(1, 0);
				}
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		stopScheduleJob();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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
	public void onClick(View v) {
		mListView.smoothScrollToPosition(0);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		EconomicCircle item = (EconomicCircle) parent.getItemAtPosition(position);
		setBattleData(item);
		if (item != null) {
			if (item.getType() == 1) {
				//借钱
				Intent intent = new Intent(getContext(), BorrowDetailsActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, item.getDataId());
				startActivityForResult(intent, REQ_CODE_USERDATA);
			} else if (item.getType() == 2) {
				//观点
				Intent intent = new Intent(getContext(), OpinionDetailsActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, item.getDataId());
				startActivityForResult(intent, REQ_CODE_USERDATA);
			} else if (item.getType() == 3) {
				//游戏
				if (item.getGameStatus() == EconomicCircle.GAME_STATUS_CANCELED) {
					SmartDialog.with(getActivity()).setMessage(getString(R.string.invite_invalid))
							.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
								@Override
								public void onClick(Dialog dialog) {
									dialog.dismiss();
								}
							})
							.setTitle(getString(R.string.join_versus_failure))
							.setNegativeVisible(View.GONE)
							.show();
				} else if (item.getGameStatus() == EconomicCircle.GAME_STATUS_END) {
					Launcher.with(getActivity(), BattleActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mBattle)
							.putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_RECORD)
							.execute();
				} else if (LocalUser.getUser().isLogin()) {
					if (item.getGameStatus() == EconomicCircle.GAME_STATUS_CREATED
							&& LocalUser.getUser().getUserInfo().getId() != item.getLaunchUser()) {
						showJoinBattleDialog(mBattle);
					} else {
						requestLastBattleInfo(mBattle);
					}
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}
		}
	}

	private void requestLastBattleInfo(final Battle item) {
		Client.getBattleInfo(item.getId(), item.getBatchCode()).setTag(TAG)
				.setCallback(new Callback2D<Resp<Battle>, Battle>() {
					@Override
					protected void onRespSuccessData(Battle data) {

						int pageType;
						if (data.getGameStatus() == Battle.GAME_STATUS_END) {
							pageType = BattleActivity.PAGE_TYPE_RECORD;
						} else {
							pageType = BattleActivity.PAGE_TYPE_VERSUS;
						}

						Intent intent = new Intent(getContext(), BattleActivity.class);
						intent.putExtra(Launcher.EX_PAYLOAD, data);
						intent.putExtra(BattleActivity.PAGE_TYPE, pageType);
						startActivityForResult(intent, REQ_CODE_USERDATA);

						Launcher.with(getActivity(), BattleActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, data)
								.putExtra(BattleActivity.PAGE_TYPE, pageType)
								.executeForResult(CANCEL_BATTLE);

					}
				}).fire();
	}

	private void setBattleData(EconomicCircle item) {
		mBattle.setAgainstFrom(item.getAgainstFrom());
		mBattle.setAgainstPraise(item.getAgainstPraise());
		mBattle.setAgainstScore(item.getAgainstScore());
		mBattle.setAgainstUser(item.getAgainstUser());
		mBattle.setAgainstUserName(item.getAgainstUserName());
		mBattle.setAgainstUserPortrait(item.getAgainstUserPortrait());
		mBattle.setBatchCode(item.getBatchCode());
		mBattle.setBattleId(item.getDataId());
		mBattle.setCoinType(item.getCoinType());
		mBattle.setCreateTime(item.getCreateTime());
		mBattle.setEndline(item.getEndline());
		mBattle.setEndTime(item.getEndTime());
		mBattle.setGameStatus(item.getGameStatus());
		mBattle.setLaunchUser(item.getLaunchUser());
		mBattle.setLaunchUserName(item.getUserName());
		mBattle.setLaunchPraise(item.getLaunchPraise());
		mBattle.setLaunchUserPortrait(item.getUserPortrait());
		mBattle.setLaunchScore(item.getLaunchScore());
		mBattle.setReward(item.getReward());
		mBattle.setStartTime(item.getStartTime());
		mBattle.setVarietyId(item.getVarietyId());
		mBattle.setVarietyName(item.getVarietyName());
		mBattle.setVarietyType(item.getVarietyType());
		mBattle.setWinResult(item.getWinResult());
		mBattle.setId(item.getDataId());
	}

	private void showJoinBattleDialog(final Battle item) {
		String reward = "";
		switch (item.getCoinType()) {
			case EconomicCircle.COIN_TYPE_INGOT:
				reward = item.getReward() + getActivity().getString(R.string.ingot);
				break;
			case EconomicCircle.COIN_TYPE_CASH:
				reward = item.getReward() + getActivity().getString(R.string.cash);
				break;
			case EconomicCircle.COIN_TYPE_INTEGRAL:
				reward = item.getReward() + getActivity().getString(R.string.integral);
				break;
		}

		SmartDialog.single(getActivity(), getString(R.string.join_versus_tip, reward))
				.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						requestJoinBattle(item);
					}
				})
				.setTitle(getString(R.string.join_versus_title))
				.setNegative(R.string.cancel)
				.show();

	}

	private void requestJoinBattle(final Battle data) {
		Client.joinBattle(data.getId(), Battle.SOURCE_COTERIE).setTag(TAG)
				.setCallback(new ApiCallback<Resp<Battle>>() {
					@Override
					public void onSuccess(Resp<Battle> resp) {
						if (resp.isSuccess()) {
							Battle battle = resp.getData();
							if (battle != null) {
								Launcher.with(getActivity(), BattleActivity.class)
										.putExtra(Launcher.EX_PAYLOAD, battle)
										.putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
										.execute();
							}
						} else {
							showJoinVersusFailureDialog(resp);
						}
					}

					@Override
					public void onFailure(VolleyError volleyError) {

					}
				}).fireFree();
	}

	private void showJoinVersusFailureDialog(final Resp<Battle> resp) {
		final int code = resp.getCode();
		int positiveMsg;
		String msg = null;
		SmartDialog smartDialog = SmartDialog.single(getActivity(), msg);
		if (code == Battle.CODE_BATTLE_JOINED_OR_CREATED) {
			msg = getString(R.string.battle_joined_or_created);
			positiveMsg = R.string.go_battle;
		} else if (code == Battle.CODE_NO_ENOUGH_MONEY) {
			msg = getString(R.string.join_battle_balance_not_enough);
			positiveMsg = R.string.go_recharge;
		} else {
			msg = getString(R.string.invite_invalid);
			positiveMsg = R.string.ok;
			smartDialog.setNegativeVisible(View.GONE);
		}
		smartDialog.setMessage(msg)
				.setPositive(positiveMsg, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						if (code == Battle.CODE_BATTLE_JOINED_OR_CREATED) {
							requestCurrentBattle();
						} else if (code == Battle.CODE_NO_ENOUGH_MONEY) {
							Launcher.with(getActivity(), CornucopiaActivity.class).execute();
						}
					}
				})
				.setTitle(getString(R.string.join_versus_failure))
				.setNegative(R.string.cancel)
				.show();

	}

	private void requestCurrentBattle() {
		Client.getCurrentBattle().setTag(TAG)
				.setCallback(new Callback<Resp<Battle>>() {
					@Override
					protected void onRespSuccess(Resp<Battle> resp) {
						if (resp.getData() != null) {
							Launcher.with(getActivity(), BattleActivity.class)
									.putExtra(Launcher.EX_PAYLOAD, resp.getData())
									.putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
									.execute();
						}
					}
				}).fire();
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
							if (mOnNoReadNewsListener != null) {
								mOnNoReadNewsListener.onNoReadNewsNumber(1, 0);
							}

						}
					});

					mListView.addHeaderView(mNewMessageHeaderView);
					TextView textView = (TextView) mNewMessageHeaderView.findViewById(R.id.newMessageCount);
					textView.setText(getString(R.string.new_message_count, count));
				}

				if (mNewMessageHeaderView != null && count > 0) {
					TextView textView = (TextView) mNewMessageHeaderView.findViewById(R.id.newMessageCount);
					textView.setText(getString(R.string.new_message_count, count));
				}

				//多端问题
				if (count == 0) {
					mListView.removeHeaderView(mNewMessageHeaderView);
					mNewMessageHeaderView = null;
					if (mOnNoReadNewsListener != null) {
						mOnNoReadNewsListener.onNoReadNewsNumber(1, 0);
					}
				}

				if (count > 0) {
					if (mOnNoReadNewsListener != null) {
						mOnNoReadNewsListener.onNoReadNewsNumber(1, count);
					}
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

		if (requestCode == CANCEL_BATTLE && resultCode == RESULT_OK) {
			if (data != null) {
				int gameStatus = data.getIntExtra(Launcher.EX_PAYLOAD, -1);
				int id = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
				if (id != -1 && gameStatus == Battle.GAME_STATUS_CANCELED) {
					if (mEconomicCircleAdapter != null) {
						for (int i = 0; i < mEconomicCircleAdapter.getCount(); i++) {
							EconomicCircle item = (EconomicCircle) mEconomicCircleAdapter.getItem(i);
							if (item != null && item.getDataId() == id) {
								item.setGameStatus(EconomicCircle.GAME_STATUS_CANCELED);
								mEconomicCircleAdapter.notifyDataSetChanged();
								break;
							}
						}
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

			void HotAreaClick(EconomicCircle economicCircle);
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
			return 3;
		}

		@Override
		public int getItemViewType(int position) {
			if (mEconomicCircleList.get(position).getType() == 2) {
				return TYPE_OPINION;
			} else if (mEconomicCircleList.get(position).getType() == 1) {
				return TYPE_BORROW_MONEY;
			} else {
				return TYPE_FUTURES_BATTLE;
			}
		}

		@NonNull
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			OpinionViewHolder opinionViewHolder = null;
			BorrowMoneyViewHolder borrowMoneyViewHolder = null;
			FuturesBattleViewHolder futuresBattleViewHolder = null;
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

					case TYPE_FUTURES_BATTLE:
						convertView = LayoutInflater.from(mContext).inflate(R.layout.row_futures_battle, null);
						futuresBattleViewHolder = new FuturesBattleViewHolder(convertView);
						convertView.setTag(R.id.tag_futures_battle, futuresBattleViewHolder);
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
					case TYPE_FUTURES_BATTLE:
						futuresBattleViewHolder = (FuturesBattleViewHolder) convertView.getTag(R.id.tag_futures_battle);
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
				case TYPE_FUTURES_BATTLE:
					futuresBattleViewHolder.bindingData(mContext, (EconomicCircle) getItem(position), mCallback, position);
					break;
			}

			return convertView;
		}

		static class OpinionViewHolder {

			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.isAttention)
			TextView mIsAttention;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.opinionContent)
			CollapsedTextLayout mOpinionContent;
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

				mHotArea.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.HotAreaClick(item);
						}
					}
				});

				mUserName.setText(item.getUserName());

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}


				mOpinionContent.setContentText(item.getContent());

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
			}
		}

		class BorrowMoneyViewHolder {

			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.location)
			TextView mLocation;
			@BindView(R.id.borrowMoneyContent)
			CollapsedTextLayout mBorrowMoneyContent;
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

				mHotArea.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.HotAreaClick(item);
						}
					}
				});

				mUserName.setText(item.getUserName());

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}

				if (TextUtils.isEmpty(item.getContent())) {
					mBorrowMoneyContent.setVisibility(View.GONE);
				} else {
					mBorrowMoneyContent.setVisibility(View.VISIBLE);
					mBorrowMoneyContent.setContentText(item.getContent().trim());
				}

				mNeedAmount.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getMoney())));
				mBorrowDeadline.setText(context.getString(R.string.day, FinanceUtil.formatWithScaleNoZero(item.getDays())));
				mBorrowInterest.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(item.getInterest())));

				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
				if (TextUtils.isEmpty(item.getLocation())) {
					mLocation.setText(R.string.no_location_information);
				} else {
					mLocation.setText(item.getLocation());
				}

				if (!TextUtils.isEmpty(item.getContentImg())) {
					String[] images = item.getContentImg().split(",");
					if (images.length >= 2) {
						mBorrowingImg.setVisibility(View.VISIBLE);
						mCircleMoreIcon.setVisibility(View.VISIBLE);
					} else {
						mBorrowingImg.setVisibility(View.VISIBLE);
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

		static class FuturesBattleViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.isAttention)
			TextView mIsAttention;
			@BindView(R.id.hotArea)
			RelativeLayout mHotArea;
			@BindView(R.id.content)
			CollapsedTextView mContent;
			@BindView(R.id.varietyName)
			TextView mVarietyName;
			@BindView(R.id.progressBar)
			BattleProgress mProgressBar;
			@BindView(R.id.versus)
			ImageView mVersus;
			@BindView(R.id.bounty)
			TextView mBounty;
			@BindView(R.id.status)
			TextView mStatus;
			@BindView(R.id.againstUserAvatar)
			ImageView mAgainstUserAvatar;
			@BindView(R.id.againstUserName)
			TextView mAgainstUserName;
			@BindView(R.id.publishTime)
			TextView mPublishTime;
			@BindView(R.id.location)
			TextView mLocation;

			FuturesBattleViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(Context context, final EconomicCircle item, final Callback callback, int position) {
				if (item == null) return;

				Glide.with(context).load(item.getUserPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mHotArea.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (callback != null) {
							callback.HotAreaClick(item);
						}
					}
				});

				mUserName.setText(item.getUserName());

				if (item.getIsAttention() == 2) {
					mIsAttention.setText(R.string.is_attention);
				} else {
					mIsAttention.setText("");
				}

				mVarietyName.setText(item.getVarietyName());
				mContent.setShowText(item.getContent().trim());
				mProgressBar.setLeftText(String.valueOf(item.getLaunchScore()));
				mProgressBar.setRightText(String.valueOf(item.getAgainstScore()));

				String reward = "";
				switch (item.getCoinType()) {
					case EconomicCircle.COIN_TYPE_CASH:
						reward = item.getReward() + context.getString(R.string.cash);
						break;
					case EconomicCircle.COIN_TYPE_INGOT:
						reward = item.getReward() + context.getString(R.string.ingot);
						break;
					case EconomicCircle.COIN_TYPE_INTEGRAL:
						reward = item.getReward() + context.getString(R.string.integral);
						break;
				}

				mBounty.setText(reward);
				switch (item.getGameStatus()) {
					//取消
					case EconomicCircle.GAME_STATUS_CANCELED:
						mStatus.setText(context.getString(R.string.versus_cancel));
						mAgainstUserAvatar.setImageResource(R.drawable.btn_join_versus);
						mAgainstUserName.setText(context.getString(R.string.join_versus));
						mVersus.setVisibility(View.VISIBLE);
						mProgressBar.showScoreProgress(0, 0, true);
						break;

					//发起对战
					case EconomicCircle.GAME_STATUS_CREATED:
						mStatus.setText(DateUtil.getMinutes(item.getEndline()));
						mAgainstUserAvatar.setImageResource(R.drawable.btn_join_versus);
						mAgainstUserName.setText(context.getString(R.string.join_versus));
						mVersus.setVisibility(View.VISIBLE);
						mProgressBar.showScoreProgress(0, 0, true);
						break;

					//对战中
					case EconomicCircle.GAME_STATUS_STARTED:
						mStatus.setText(context.getString(R.string.versusing));
						Glide.with(context)
								.load(item.getAgainstUserPortrait())
								.placeholder(R.drawable.ic_default_avatar_big)
								.transform(new GlideCircleTransform(context))
								.into(mAgainstUserAvatar);
						mAgainstUserName.setText(item.getAgainstUserName());
						mVersus.setVisibility(View.GONE);
						mProgressBar.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
						break;

					//已结束
					case EconomicCircle.GAME_STATUS_END:
						mStatus.setText(context.getString(R.string.versus_end));
						Glide.with(context)
								.load(item.getAgainstUserPortrait())
								.placeholder(R.drawable.ic_default_avatar)
								.transform(new GlideCircleTransform(context))
								.into(mAgainstUserAvatar);
						mAgainstUserName.setText(item.getAgainstUserName());
						mVersus.setVisibility(View.GONE);
						mProgressBar.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
						break;
				}

				mPublishTime.setText(DateUtil.getFormatTime(item.getCreateTime()));
			}
		}
	}
}
