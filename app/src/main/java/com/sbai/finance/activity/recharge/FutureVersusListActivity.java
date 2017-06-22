package com.sbai.finance.activity.recharge;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.future.CreateFightActivity;
import com.sbai.finance.activity.future.FutureBattleActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.activity.mine.wallet.RechargeActivity;
import com.sbai.finance.fragment.dialog.BindBankHintDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.versus.FutureVersus;
import com.sbai.finance.model.versus.VersusGaming;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class FutureVersusListActivity extends BaseActivity {
	@BindView(R.id.titleBar)
	TitleBar mTitle;
	@BindView(R.id.listView)
	ListView mListView;
	@BindView(R.id.matchVersus)
	TextView mMatchVersus;
	@BindView(R.id.createVersus)
	TextView mCreateVersus;
	@BindView(R.id.createAndMatchArea)
	LinearLayout mCreateAndMatchArea;
	@BindView(R.id.currentVersus)
	TextView mCurrentVersus;
	private ImageView mAvatar;
	private TextView mIntegral;
	private TextView mWining;
	private TextView mRecharge;
	private VersusListAdapter mVersusListAdapter;
	private long mLocation;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_future_versus_list);
		ButterKnife.bind(this);
		initView();
	}

	private void initView() {
		FrameLayout header = (FrameLayout) getLayoutInflater().inflate(R.layout.layout_future_versus_header, mListView, false);
		ImageView versusBanner = (ImageView) header.findViewById(R.id.versusBanner);
		TextView seeVersusRecord = (TextView) header.findViewById(R.id.seeVersusRecord);
		TextView versusRule = (TextView) header.findViewById(R.id.versusRule);
		Glide.with(getActivity()).load(R.drawable.versus_banner).asGif().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(versusBanner);
		seeVersusRecord.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.with(getActivity(), FutureVersusRecordActivity.class).execute();

			}
		});
		versusRule.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				BindBankHintDialogFragment.newInstance(R.string.versus_rule_title, R.string.versus_rule_tip).show(getSupportFragmentManager());
			}
		});
		View customView = mTitle.getCustomView();
		mAvatar = (ImageView) customView.findViewById(R.id.avatar);
		mIntegral = (TextView) customView.findViewById(R.id.integral);
		mWining = (TextView) customView.findViewById(R.id.wining);
		mRecharge = (TextView) customView.findViewById(R.id.recharge);
		if (LocalUser.getUser().isLogin()) {
			Glide.with(getActivity())
					.load(LocalUser.getUser().getUserInfo().getUserPortrait())
					.placeholder(R.drawable.ic_default_avatar)
					.transform(new GlideCircleTransform(getActivity()))
					.into(mAvatar);
		} else {
			mAvatar.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.ic_default_avatar));
		}
		mAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), UserDataActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, LocalUser.getUser().getUserInfo().getId())
							.execute();
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}
		});
		mRecharge.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(getActivity(), RechargeActivity.class).execute();
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}
		});

		mListView.addHeaderView(header);
		mVersusListAdapter = new VersusListAdapter(getActivity());
		mVersusListAdapter.setCallback(new VersusListAdapter.Callback() {
			@Override
			public void onClick(VersusGaming item, boolean isCreate) {
				if (LocalUser.getUser().isLogin()) {
					if (isCreate) {
						Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.EX_PAYLOAD, item.getLaunchUser()).execute();
					} else {
						if (item.getGameStatus() == VersusGaming.GAME_STATUS_MATCH) {
							showJoinVersusDialog();
						} else {
							Launcher.with(getActivity(), UserDataActivity.class).putExtra(Launcher.EX_PAYLOAD, item.getAgainstUser()).execute();
						}
					}
				} else {
					Launcher.with(getActivity(), LoginActivity.class).execute();
				}
			}
		});
		mListView.setAdapter(mVersusListAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				VersusGaming item = mVersusListAdapter.getItem(position);
				if (item != null) {
					item.setPageType(VersusGaming.PAGE_VERSUS);
					Launcher.with(getActivity(), FutureBattleActivity.class).putExtra(Launcher.EX_PAYLOAD, item).execute();
				}
			}
		});
		mVersusListAdapter.notifyDataSetChanged();
		requestVersusData();

	}

	private void requestVersusData() {
		Client.getVersusGaming(mLocation).setTag(TAG)
				.setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
					@Override
					protected void onRespSuccessData(FutureVersus data) {
						updateVersusData(data);
					}
				}).fireSync();
	}

	private void updateVersusData(FutureVersus futureVersus) {
		mVersusListAdapter.clear();
		mVersusListAdapter.addAll(futureVersus.getList());
		mVersusListAdapter.notifyDataSetChanged();
	}

	@OnClick({R.id.createVersus, R.id.matchVersus, R.id.currentVersus, R.id.titleBar})
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.titleBar:
				mListView.smoothScrollToPositionFromTop(0, 0);
				break;
			case R.id.createVersus:
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(this, CreateFightActivity.class).execute();
				} else {
					Launcher.with(this, LoginActivity.class).execute();
				}
				break;
			case R.id.matchVersus:
				//showAskMatchDialog();
				break;
			case R.id.currentVersus:
				break;
			default:
				break;
		}
	}

	private void showJoinVersusDialog() {
		SmartDialog.with(getActivity(), getString(R.string.join_versus_tip), getString(R.string.join_versus_title))
				.setMessageTextSize(15)
				.setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						// TODO: 2017-06-21  进行余额查询，余额充足进入对战，余额不足弹窗提示充值
						Launcher.with(getActivity(), FutureBattleActivity.class).execute();
						// showJoinVersusFailureDialog();
					}
				})
				.setTitleMaxLines(1)
				.setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
				.setNegative(R.string.cancel)
				.show();

	}

	private void showJoinVersusFailureDialog() {
		SmartDialog.with(getActivity(), getString(R.string.join_versus_failure_tip), getString(R.string.join_versus_failure_title))
				.setMessageTextSize(15)
				.setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						Launcher.with(getActivity(), RechargeActivity.class).execute();
					}
				})
				.setTitleMaxLines(1)
				.setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
				.setNegative(R.string.cancel)
				.show();
	}

	private void showMatchingDialog() {
		SmartDialog.with(getActivity(), getString(R.string.matching_tip), getString(R.string.matching))
				.setMessageTextSize(15)
				.setPositive(R.string.cancel_matching, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						showCancelMatchDialog();
					}
				})
				.setTitleMaxLines(1)
				.setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
				.setCancelableOnTouchOutside(false)
				.setNegativeHide()
				.show();
	}

	private void showCancelMatchDialog() {
		SmartDialog.with(getActivity(), getString(R.string.cancel_tip), getString(R.string.cancel_matching))
				.setMessageTextSize(15)
				.setPositive(R.string.no_waiting, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				})
				.setNegative(R.string.continue_versus, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
						showMatchingDialog();
					}
				})
				.setTitleMaxLines(1)
				.setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
				.setCancelableOnTouchOutside(false)
				.show();

	}

	static class VersusListAdapter extends ArrayAdapter<VersusGaming> {
		interface Callback {
			void onClick(VersusGaming item, boolean isCreate);
		}

		private Callback mCallback;

		public void setCallback(Callback callback) {
			mCallback = callback;
		}

		public VersusListAdapter(@NonNull Context context) {
			super(context, 0);
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_future_versus, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindDataWithView(getItem(position), getContext(), mCallback);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.createAvatar)
			ImageView mCreateAvatar;
			@BindView(R.id.createKo)
			ImageView mCreateKo;
			@BindView(R.id.createName)
			TextView mCreateName;
			@BindView(R.id.varietyName)
			TextView mVarietyName;
			@BindView(R.id.progressBar)
			ProgressBar mProgressBar;
			@BindView(R.id.createProfit)
			TextView mCreateProfit;
			@BindView(R.id.againstProfit)
			TextView mAgainstProfit;
			@BindView(R.id.fighterDataArea)
			RelativeLayout mFighterDataArea;
			@BindView(R.id.depositAndTime)
			TextView mDepositAndTime;
			@BindView(R.id.againstAvatar)
			ImageView mAgainstAvatar;
			@BindView(R.id.againstKo)
			ImageView mAgainstKo;
			@BindView(R.id.againstName)
			TextView mAgainstName;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindDataWithView(final VersusGaming item, Context context, final Callback callback) {
				mVarietyName.setText(item.getVarietyName());
				Glide.with(context).load(item.getLaunchUserPortrait())
						.load(item.getLaunchUserPortrait())
						.placeholder(R.drawable.ic_default_avatar_big)
						.transform(new GlideCircleTransform(context))
						.into(mCreateAvatar);
				mCreateName.setText(item.getLaunchUserName());
				mCreateProfit.setText(String.valueOf(item.getLaunchScore()));
				mCreateAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						callback.onClick(item, true);
					}
				});

				mAgainstName.setText(item.getAgainstUserName());
				mAgainstAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						callback.onClick(item, false);
					}
				});
				mAgainstProfit.setText(String.valueOf(item.getAgainstScore()));
				String reward = "";
				switch (item.getCoinType()) {
					case VersusGaming.COIN_TYPE_BAO:
						reward = item.getReward() + context.getString(R.string.integral);
						break;
					case VersusGaming.COIN_TYPE_CASH:
						reward = item.getReward() + context.getString(R.string.cash);
						break;
					case VersusGaming.COIN_TYPE_INTEGRAL:
						reward = item.getReward() + context.getString(R.string.ingot);
						break;
				}
				switch (item.getGameStatus()) {
					case VersusGaming.GAME_STATUS_MATCH:
						mDepositAndTime.setText(reward + " " + DateUtil.getMinutes(item.getEndline()));
						mCreateKo.setVisibility(View.GONE);
						mAgainstKo.setVisibility(View.GONE);
						mAgainstAvatar.setBackground(ContextCompat.getDrawable(context, R.drawable.btn_join_versus));
						showScoreProgress(0, 0, true);
						break;
					case VersusGaming.GAME_STATUS_START:
						mDepositAndTime.setText(reward + " " + context.getString(R.string.versusing));
						mCreateKo.setVisibility(View.GONE);
						mAgainstKo.setVisibility(View.GONE);
						Glide.with(context).load(item.getLaunchUserPortrait())
								.load(item.getAgainstUserPortrait())
								.placeholder(R.drawable.ic_default_avatar_big)
								.transform(new GlideCircleTransform(context))
								.into(mAgainstAvatar);
						showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
						break;
					case VersusGaming.GAME_STATUS_END:
						mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
						Glide.with(context).load(item.getLaunchUserPortrait())
								.load(item.getAgainstUserPortrait())
								.placeholder(R.drawable.ic_default_avatar_big)
								.transform(new GlideCircleTransform(context))
								.into(mAgainstAvatar);
						if (item.getWinResult() == VersusGaming.RESULT_AGAINST_WIN) {
							mCreateKo.setVisibility(View.VISIBLE);
							mAgainstKo.setVisibility(View.GONE);
							mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
						} else if (item.getWinResult() == VersusGaming.RESULT_CREATE_WIN) {
							mCreateKo.setVisibility(View.GONE);
							mAgainstKo.setVisibility(View.VISIBLE);
							mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
						} else {
							mCreateKo.setVisibility(View.GONE);
							mAgainstKo.setVisibility(View.GONE);
							mDepositAndTime.setText(reward + " " + context.getString(R.string.tie));
						}
						showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
						break;

				}
			}

			/**
			 * 显示对抗状态条
			 *
			 * @param createProfit  我的盈利状况
			 * @param fighterProfit 对抗者盈利状况
			 * @param isInviting    是否正在邀请中
			 * @return
			 */
			private void showScoreProgress(double createProfit, double fighterProfit, boolean isInviting) {
				String myFlag = "";
				String fighterFlag = "";
				if (isInviting) {
					mProgressBar.setProgress(0);
					mProgressBar.setSecondaryProgress(0);
				} else {
					//正正
					if ((createProfit > 0 && fighterProfit >= 0) || (createProfit >= 0 && fighterProfit > 0)) {
						int progress = (int) (createProfit * 100 / (createProfit + fighterProfit));
						mProgressBar.setProgress(progress);
					}
					//正负
					if (createProfit >= 0 && fighterProfit < 0) {
						mProgressBar.setProgress(100);
					}
					//负正
					if (createProfit < 0 && fighterProfit >= 0) {
						mProgressBar.setProgress(0);
					}
					//负负
					if (createProfit < 0 && fighterProfit < 0) {
						int progress = (int) (Math.abs(createProfit) * 100 / (Math.abs(createProfit) + Math.abs(fighterProfit)));
						mProgressBar.setProgress(100 - progress);
					}
					//都为0
					if (createProfit == 0 && fighterProfit == 0) {
						mProgressBar.setProgress(50);
					}

					if (createProfit > 0) {
						myFlag = "+";
					}

					if (fighterProfit > 0) {
						fighterFlag = "+";
					}
					mCreateProfit.setText(myFlag + FinanceUtil.formatWithScale(createProfit));
					mAgainstProfit.setText(fighterFlag + FinanceUtil.formatWithScale(fighterProfit));

				}
			}
		}
	}
}
