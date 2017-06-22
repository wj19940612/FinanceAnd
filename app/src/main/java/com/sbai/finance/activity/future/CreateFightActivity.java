package com.sbai.finance.activity.future;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.future.FutureBattleConfig;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.bountyChoice1;
import static com.umeng.analytics.pro.x.l;


public class CreateFightActivity extends BaseActivity {

	public static final int REQ_CODE_CHOOSE_FUTURES = 1001;
	@BindView(R.id.chooseFutures)
	TextView mChooseFutures;
	@BindView(R.id.ingotWar)
	TextView mIngotWar;
	@BindView(R.id.integralWar)
	TextView mIntegralWar;
	@BindView(bountyChoice1)
	TextView mBountyChoice1;
	@BindView(R.id.bountyChoice2)
	TextView mBountyChoice2;
	@BindView(R.id.bountyChoice3)
	TextView mBountyChoice3;
	@BindView(R.id.durationChoice1)
	TextView mDurationChoice1;
	@BindView(R.id.durationChoice2)
	TextView mDurationChoice2;
	@BindView(R.id.durationChoice3)
	TextView mDurationChoice3;
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

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_create_fight);
		ButterKnife.bind(this);
		requestFutureBattleConfig();
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

		mBountyChoice1.setText(mIngotArray[0]);
		mBountyChoice2.setText(mIngotArray[1]);
		mBountyChoice3.setVisibility(View.INVISIBLE);

		mDurationChoice1.setText(getString(R.string.duration_choice1, mDurationArray[0]));
		mDurationChoice2.setText(getString(R.string.duration_choice1, mDurationArray[1]));
		mDurationChoice3.setText(getString(R.string.duration_choice1, mDurationArray[2]));

		mLaunchFight.setEnabled(false);
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

	@OnClick({R.id.chooseFutures, R.id.ingotWar, R.id.integralWar
			, bountyChoice1, R.id.bountyChoice2, R.id.bountyChoice3
			, R.id.durationChoice1, R.id.durationChoice2, R.id.durationChoice3
			, R.id.launch_fight})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.chooseFutures:
				Launcher.with(getActivity(), ChooseFuturesActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mContractsCode)
						.executeForResult(REQ_CODE_CHOOSE_FUTURES);
				break;
			case R.id.ingotWar:
				mIngotWar.setSelected(true);
				mIntegralWar.setSelected(false);
				mBountyChoice1.setSelected(false);
				mBountyChoice2.setSelected(false);
				mBountyChoice3.setSelected(false);
				mBountyChoice3.setVisibility(View.INVISIBLE);
				mBountyChoice1.setText(mIngotArray[0]);
				mBountyChoice2.setText(mIngotArray[1]);
				whetherLaunchFight();
				mCoinType = 2;
				break;
			case R.id.integralWar:
				mIngotWar.setSelected(false);
				mIntegralWar.setSelected(true);
				mBountyChoice1.setSelected(false);
				mBountyChoice2.setSelected(false);
				mBountyChoice3.setSelected(false);
				mBountyChoice3.setVisibility(View.VISIBLE);
				mBountyChoice1.setText(mIntegralArray[0]);
				mBountyChoice2.setText(mIntegralArray[1]);
				mBountyChoice3.setText(mIntegralArray[2]);
				whetherLaunchFight();
				mCoinType = 3;
				break;
			case bountyChoice1:
				mBountyChoice1.setSelected(true);
				mBountyChoice2.setSelected(false);
				mBountyChoice3.setSelected(false);
				whetherLaunchFight();
				mReward = Double.parseDouble(mBountyChoice1.getText().toString().trim());
				break;
			case R.id.bountyChoice2:
				mBountyChoice1.setSelected(false);
				mBountyChoice2.setSelected(true);
				mBountyChoice3.setSelected(false);
				whetherLaunchFight();
				mReward = Double.parseDouble(mBountyChoice2.getText().toString().trim());
				break;
			case R.id.bountyChoice3:
				mBountyChoice1.setSelected(false);
				mBountyChoice2.setSelected(false);
				mBountyChoice3.setSelected(true);
				whetherLaunchFight();
				mReward = Double.parseDouble(mBountyChoice3.getText().toString().trim());
				break;
			case R.id.durationChoice1:
				mDurationChoice1.setSelected(true);
				mDurationChoice2.setSelected(false);
				mDurationChoice3.setSelected(false);
				whetherLaunchFight();
				mEndTime = Integer.parseInt(mDurationChoice1.getText().toString().trim());
				break;
			case R.id.durationChoice2:
				mDurationChoice1.setSelected(false);
				mDurationChoice2.setSelected(true);
				mDurationChoice3.setSelected(false);
				whetherLaunchFight();
				mEndTime = Integer.parseInt(mDurationChoice2.getText().toString().trim());
				break;
			case R.id.durationChoice3:
				mDurationChoice1.setSelected(false);
				mDurationChoice2.setSelected(false);
				mDurationChoice3.setSelected(true);
				whetherLaunchFight();
				mEndTime = Integer.parseInt(mDurationChoice3.getText().toString().trim());
				break;
			case R.id.launch_fight:

				break;
		}
	}

	private void whetherLaunchFight() {
		if (mChooseFutures.isSelected()
				&& (mIngotWar.isSelected() || mIntegralWar.isSelected())
				&& (mBountyChoice1.isSelected() || mBountyChoice2.isSelected() || mBountyChoice3.isSelected())
				&& (mDurationChoice1.isSelected() || mDurationChoice2.isSelected() || mDurationChoice3.isSelected())) {
			mLaunchFight.setEnabled(true);
		} else {
			mLaunchFight.setEnabled(false);
		}
	}
}
