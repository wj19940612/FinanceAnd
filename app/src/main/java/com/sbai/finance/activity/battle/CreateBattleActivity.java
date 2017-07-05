package com.sbai.finance.activity.battle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureBattleConfig;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.ingotWar;

public class CreateBattleActivity extends BaseActivity {

    public static final int REQ_CODE_CHOOSE_FUTURES = 1001;
    public static final String CREATE_SUCCESS_ACTION = "CREATE_SUCCESS_ACTION";
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
    @BindView(R.id.bountyArea)
    LinearLayout mBountyArea;

    private String mContractsCode = null;
    private int mVarietyId;
    private int mCoinType;
    private double mReward;
    private int mEndTime;
    private String mFutureName;
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
        mLaunchFight.setEnabled(false);

        mBountyAdapter = new BountyAdapter(this);
        mDurationAdapter = new DurationAdapter(this);
        mBountyGridView.setAdapter(mBountyAdapter);
        mDurationGridView.setAdapter(mDurationAdapter);
        requestFutureBattleConfig();

        mBountyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                if (mIntegralWar.isSelected()) {
                    updateIntegralConfig();
                    mBountyAdapter.setChecked(position);
                    mBountyAdapter.notifyDataSetChanged();
                } else {
                    updateIngotConfig();
                    mBountyAdapter.setChecked(position);
                    mBountyAdapter.notifyDataSetChanged();
                }
                mBountySelected = true;
                whetherLaunchBattle();
                mReward = Double.parseDouble(item);
            }
        });
        mDurationGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                updateDurationConfig();
                mDurationAdapter.setChecked(position);
                mDurationAdapter.notifyDataSetChanged();
                mDurationSelected = true;
                whetherLaunchBattle();
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
        mIngotList = Arrays.asList(futureBattleConfig.getGold().split(","));
        mIntegralList = Arrays.asList(futureBattleConfig.getIntegral().split(","));
        mDurationList = Arrays.asList(futureBattleConfig.getTime().split(","));
        mIngotList = new ArrayList<>(mIngotList);
        mIntegralList = new ArrayList<>(mIntegralList);
        mDurationList = new ArrayList<>(mDurationList);

        if (mIngotList != null && !mIngotList.isEmpty()) {
            mBountyArea.setVisibility(View.VISIBLE);
        }

        updateIngotConfig();
        updateDurationConfig();
    }

    private void updateIngotConfig() {
        if (mIngotList == null || mIngotList.isEmpty()) return;
        mBountyAdapter.clear();
        mBountyAdapter.addAll(mIngotList);
    }

    private void updateIntegralConfig() {
        if (mIntegralList == null || mIntegralList.isEmpty()) return;
        mBountyAdapter.clear();
        mBountyAdapter.addAll(mIntegralList);
    }


    private void updateDurationConfig() {
        if (mDurationList == null || mDurationList.isEmpty()) return;
        mDurationAdapter.clear();
        mDurationAdapter.addAll(mDurationList);
    }

    @OnClick({R.id.chooseFutures, ingotWar, R.id.integralWar, R.id.launch_fight})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.chooseFutures:
                Launcher.with(getActivity(), ChooseFuturesActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mContractsCode)
                        .executeForResult(REQ_CODE_CHOOSE_FUTURES);
                whetherLaunchBattle();
                break;
            case ingotWar:
                mBountyArea.setVisibility(View.VISIBLE);
                if (!mIngotWar.isSelected()) {
                    mBountyAdapter.setChecked(-1);
                    mBountySelected = false;
                }
                mIngotWar.setSelected(true);
                mIntegralWar.setSelected(false);
                updateIngotConfig();
                whetherLaunchBattle();
                mCoinType = 2;
                break;
            case R.id.integralWar:
                mBountyArea.setVisibility(View.VISIBLE);
                if (!mIntegralWar.isSelected()) {
                    mBountyAdapter.setChecked(-1);
                    mBountySelected = false;
                }
                mIntegralWar.setSelected(true);
                mIngotWar.setSelected(false);
                updateIntegralConfig();
                whetherLaunchBattle();
                mCoinType = 3;
                break;
            case R.id.launch_fight:
                umengEventCount(UmengCountEventIdUtils.BATTLE_HALL_LAUNCH_BATTLE);
                createBattleRoom();
                break;
        }
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
                whetherLaunchBattle();
            }
        }
    }

    static class BountyAdapter extends ArrayAdapter<String> {
        private Context mContext;
        private int mChecked = -1;

        public BountyAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        public void setChecked(int checked) {
            this.mChecked = checked;
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
            viewHolder.bindingData(getItem(position), position, mChecked);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.choice)
            TextView mChoice;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(String bounty, int position, int checked) {
                mChoice.setText(bounty);
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
        private int mChecked = -1;

        public DurationAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
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
            viewHolder.bindingData(mContext, getItem(position), position, mChecked);
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

            public void bindingData(Context context, String duration, int position, int checked) {
                mChoice.setText(context.getString(R.string.minute, duration));
                mChoice.setSelected(false);
                if (checked == position) {
                    mChoice.setSelected(true);
                } else {
                    mChoice.setEnabled(false);
                }
            }
        }
    }

    private void whetherLaunchBattle() {
        if (mChooseFutures.isSelected()
                && (mIngotWar.isSelected() || mIntegralWar.isSelected())
                && (mBountySelected)
                && (mDurationSelected)) {
            mLaunchFight.setEnabled(true);
        } else {
            mLaunchFight.setEnabled(false);
        }
    }


    private void createBattleRoom() {
        Client.launchFight(mVarietyId, mCoinType, mReward, mEndTime).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Battle>, Battle>(false) {
                    @Override
                    protected void onRespSuccessData(Battle battle) {
                        Launcher.with(getActivity(), BattleActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, battle)
                                .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_VERSUS)
                                .execute();
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(CREATE_SUCCESS_ACTION));
                        finish();
                    }

                    @Override
                    protected void onReceive(Resp<Battle> battleResp) {
                        super.onReceive(battleResp);
                        showCreateBattleMoneyIsNotEnoughDialog(battleResp);

                    }
                }).fire();
    }

    private void showCreateBattleMoneyIsNotEnoughDialog(Resp<Battle> battleResp) {
        if (battleResp.getCode() == 2201) {
            SmartDialog.with(getActivity()).setMessage("余额不足,创建失败")
                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setTitle("提示")
                    .setNegativeVisible(View.GONE)
                    .show();
        } else {
            ToastUtil.show(battleResp.getMsg());
        }
    }
}
