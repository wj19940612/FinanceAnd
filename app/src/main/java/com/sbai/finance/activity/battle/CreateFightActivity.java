package com.sbai.finance.activity.battle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureBattleConfig;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventIdUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.ingotWar;
import static com.sbai.finance.model.battle.Battle.PAGE_VERSUS;

public class CreateFightActivity extends BaseActivity {

	public static final int REQ_CODE_CHOOSE_FUTURES = 1001;
	@BindView(R.id.chooseFutures)
	TextView mChooseFutures;
	@BindView(ingotWar)
	TextView mIngotWar;
	@BindView(R.id.integralWar)
	TextView mIntegralWar;
	@BindView(R.id.bountyGridView)
	GridView mBountyGridView;
	@BindView(R.id.durationGridView)
	GridView mDurationGridView;
	@BindView(R.id.launch_fight)
	ImageView mLaunchFight;

	private String mContractsCode = null;
	private int mVarietyId;
	private int mCoinType;
	private double mReward;
	private int mEndTime;
	private String mFutureName;
	private String[] mIngotArray;
	private String[] mIntegralArray;
	private String[] mDurationArray;
	private List<String> mIngotList;//元宝赏金
	private List<String> mIntegralList;//积分赏金
	private List<String> mDurationList;//时长
	private BountyAdapter mBountyAdapter;
	private DurationAdapter mDurationAdapter;
	private boolean mBountySelected = false;
	private boolean mDurationSelected = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_fight);
		ButterKnife.bind(this);

		requestFutureBattleConfig();

		mBountyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = (String) parent.getItemAtPosition(position);
				if (mIntegralWar.isSelected()) {
					mBountyAdapter.clear();
					mBountyAdapter.addAll(mIntegralList);
					mBountyAdapter.setChecked(position);
					mBountyAdapter.notifyDataSetChanged();
				} else {
					mBountyAdapter.clear();
					mBountyAdapter.addAll(mIngotList);
					mBountyAdapter.setChecked(position);
					mBountyAdapter.notifyDataSetChanged();
				}
				mBountySelected = true;
				whetherLaunchFight();
				mReward = Double.parseDouble(item);
			}
		});
		mDurationGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = (String) parent.getItemAtPosition(position);
				mDurationAdapter.clear();
				mDurationAdapter.addAll(mDurationList);
				mDurationAdapter.setChecked(position);
				mDurationAdapter.notifyDataSetChanged();
				mDurationSelected = true;
				whetherLaunchFight();
				mEndTime = Integer.parseInt(item);
			}
		});
	}

	private void requestFutureBattleConfig() {
		Client.getFutureBattleConfig().setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<FutureBattleConfig>, FutureBattleConfig>() {
					@Override
					protected void onRespSuccessData(FutureBattleConfig futureBattleConfig) {
						updateFutureBattleConfig(futureBattleConfig);
					}
				}).fire();
	}

	private void updateFutureBattleConfig(FutureBattleConfig futureBattleConfig) {

		mIngotArray = futureBattleConfig.getGold().split(",");
		mIntegralArray = futureBattleConfig.getIntegral().split(",");
		mDurationArray = futureBattleConfig.getTime().split(",");

		mIngotList = Arrays.asList(mIngotArray);
		mIntegralList = Arrays.asList(mIntegralArray);
		mDurationList = Arrays.asList(mDurationArray);

		mIngotList = new ArrayList<>(mIngotList);
		mIntegralList = new ArrayList<>(mIntegralList);
		mDurationList = new ArrayList<>(mDurationList);

		mBountyAdapter = new BountyAdapter(this, mIngotList);
		mDurationAdapter = new DurationAdapter(this, mDurationList);
		mBountyGridView.setAdapter(mBountyAdapter);
		mDurationGridView.setAdapter(mDurationAdapter);

		updateIngotConfig();
		updateDurationConfig();
		mLaunchFight.setEnabled(false);
	}

	private void updateIngotConfig() {
		mBountyAdapter.clear();
		mBountyAdapter.addAll(mIngotList);
	}

	private void updateIntegralConfig() {
		mBountyAdapter.clear();
		mBountyAdapter.addAll(mIntegralList);
	}


	private void updateDurationConfig() {
		mDurationAdapter.clear();
		mDurationAdapter.addAll(mDurationList);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_CODE_CHOOSE_FUTURES && resultCode == RESULT_OK) {
			if (data != null) {
				mFutureName = data.getStringExtra(Launcher.EX_PAYLOAD);
				mContractsCode = data.getStringExtra(Launcher.EX_PAYLOAD_1);
				mVarietyId = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
				mChooseFutures.setText(mFutureName);
				mChooseFutures.setSelected(true);
				whetherLaunchFight();
			}
		}
	}

	static class BountyAdapter extends ArrayAdapter<String> {
		private Context mContext;
		private List<String> mBountyAList;
		private int mChecked = -1;

		private BountyAdapter(Context context, List<String> ingotList) {
			super(context, 0);
			this.mContext = context;
			this.mBountyAList = ingotList;
		}

		public void setChecked(int checked) {
			this.mChecked = checked;
		}

		private void setIntegralList(List<String> integralList) {
			this.mBountyAList = integralList;
		}

		private void setIngotList(List<String> ingotList) {
			this.mBountyAList = ingotList;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.row_create_fight, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mBountyAList, position, mChecked);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.choice)
			TextView mChoice;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(List<String> bountyAList, int position, int checked) {
				mChoice.setText(bountyAList.get(position));
				mChoice.setSelected(false);
				if (checked == position) {
					mChoice.setSelected(true);
				} else {
					mChoice.setEnabled(false);
				}
			}
		}
	}

	static class DurationAdapter extends ArrayAdapter<String> {
		private Context mContext;
		private List<String> mDurationList;
		private int mChecked = -1;

		private DurationAdapter(Context context, List<String> durationList) {
			super(context, 0);
			this.mContext = context;
			this.mDurationList = durationList;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(R.layout.row_create_fight, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, mDurationList, position, mChecked);
			return convertView;
		}

		public void setChecked(int checked) {
			this.mChecked = checked;
		}

		static class ViewHolder {
			@BindView(R.id.choice)
			TextView mChoice;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			public void bindingData(Context context, List<String> durationList, int position, int checked) {
				mChoice.setText(context.getString(R.string.minute, durationList.get(position)));
				mChoice.setSelected(false);
				if (checked == position) {
					mChoice.setSelected(true);
				} else {
					mChoice.setEnabled(false);
				}
			}
		}
	}

	private void whetherLaunchFight() {
		if (mChooseFutures.isSelected()
				&& (mIngotWar.isSelected() || mIntegralWar.isSelected())
				&& (mBountySelected)
				&& (mDurationSelected)) {
			mLaunchFight.setEnabled(true);
		} else {
			mLaunchFight.setEnabled(false);
		}
	}

	@OnClick({R.id.chooseFutures, ingotWar, R.id.integralWar, R.id.launch_fight})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.chooseFutures:
				Launcher.with(getActivity(), ChooseFuturesActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mContractsCode)
						.executeForResult(REQ_CODE_CHOOSE_FUTURES);
				whetherLaunchFight();
				break;
			case ingotWar:
				if (!mIngotWar.isSelected()) {
					mBountyAdapter.setChecked(-1);
					mBountySelected = false;
				}
				mIngotWar.setSelected(true);
				mIntegralWar.setSelected(false);
				mBountyAdapter.setIngotList(mIngotList);
				updateIngotConfig();
				whetherLaunchFight();
				mCoinType = 2;
				break;
			case R.id.integralWar:
				if (!mIntegralWar.isSelected()) {
					mBountyAdapter.setChecked(-1);
					mBountySelected = false;
				}
				mIntegralWar.setSelected(true);
				mIngotWar.setSelected(false);
				mBountyAdapter.setIntegralList(mIntegralList);
				updateIntegralConfig();
				whetherLaunchFight();
				mCoinType = 3;
				break;
			case R.id.launch_fight:
				umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_LAUNCH_BATTLE);
				Client.launchFight(mVarietyId, mCoinType, mReward, mEndTime).setTag(TAG).setIndeterminate(this)
						.setCallback(new Callback2D<Resp<Battle>, Battle>() {
							@Override
							protected void onRespSuccessData(Battle battle) {
								battle.setPageType(PAGE_VERSUS);
								Launcher.with(getActivity(), BattleActivity.class)
										.putExtra(Launcher.EX_PAYLOAD, battle)
										.execute();
								finish();
							}
						}).fire();

				break;
		}
	}
}
