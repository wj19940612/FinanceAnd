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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureBattleConfig;
import com.sbai.finance.model.fund.UserFundInfo;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.BgShadowTextView;
import com.sbai.finance.view.MyGridView;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.ingotWar;

public class CreateBattleActivity extends BaseActivity {

    public static final int REQ_CODE_CHOOSE_FUTURES = 1001;
    public static final String CREATE_SUCCESS_ACTION = "CREATE_SUCCESS_ACTION";
    @BindView(R.id.chooseFutures)
    BgShadowTextView mChooseFutures;
    @BindView(R.id.ingotWar)
    TextView mIngotWar;
    @BindView(R.id.bountyGridView)
    MyGridView mBountyGridView;
    @BindView(R.id.bountyArea)
    LinearLayout mBountyArea;
    @BindView(R.id.durationGridView)
    MyGridView mDurationGridView;
    @BindView(R.id.launch_battle)
    ImageView mLaunchBattle;
    @BindView(R.id.hint)
    TextView mHint;


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
    private UserFundInfo mUserFundInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_fight);
        ButterKnife.bind(this);
        initData(getIntent());
        mLaunchBattle.setEnabled(false);
        mBountyAdapter = new BountyAdapter(this);
        mDurationAdapter = new DurationAdapter(this);
        mBountyGridView.setAdapter(mBountyAdapter);
        mDurationGridView.setAdapter(mDurationAdapter);
        mBountyGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String item = (String) parent.getItemAtPosition(position);
                updateIngotConfig();
                mBountyAdapter.setChecked(position);
                mBountyAdapter.notifyDataSetChanged();
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


        //临时修改，后期会再次加入积分
        mBountyArea.setVisibility(View.VISIBLE);
        if (!mIngotWar.isSelected()) {
            mBountyAdapter.setChecked(-1);
            mBountySelected = false;
        }
        mIngotWar.setSelected(true);
        updateIngotConfig();
        whetherLaunchBattle();
        mCoinType = 2;


        requestFutureBattleConfig();
    }

    private void initData(Intent intent) {
        mUserFundInfo = intent.getParcelableExtra(ExtraKeys.USER_FUND);
        updateVariety(intent);
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

        sortList(mIngotList);
        sortList(mIntegralList);
        sortList(mDurationList);

        updateIngotConfig();
        updateDurationConfig();
    }

    private void sortList(List<String> list) {
        Collections.sort(list, new Comparator<String>() {
            @Override
            public int compare(String o1, String o2) {
                return Integer.parseInt(o1) - Integer.parseInt(o2);
            }
        });
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

    @OnClick({R.id.chooseFutures, R.id.ingotWar, R.id.launch_battle})
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
                updateIngotConfig();
                whetherLaunchBattle();
                mCoinType = 2;
                break;
            case R.id.launch_battle:
                umengEventCount(UmengCountEventId.FUTURE_PK_LAUNCHER);
                launchBattle();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_CHOOSE_FUTURES && resultCode == RESULT_OK) {
            if (data != null) {
                updateVariety(data);
            }
        }
    }

    private void updateVariety(Intent data) {
        Variety variety = data.getParcelableExtra(ExtraKeys.VARIETY);
        mUserFundInfo = data.getParcelableExtra(ExtraKeys.USER_FUND);
        mFutureName = variety.getVarietyName();
        mContractsCode = variety.getContractsCode();
        mVarietyId = variety.getVarietyId();
        mChooseFutures.setText(mFutureName);
        mChooseFutures.setSelected(true);
        whetherLaunchBattle();
    }

    static class BountyAdapter extends ArrayAdapter<String> {
        private Context mContext;
        private int mChecked = -1;

        private BountyAdapter(@NonNull Context context) {
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
            BgShadowTextView mChoice;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(String bounty, int position, int checked) {
                mChoice.setText(bounty);
                if (checked == position) {
                    mChoice.setSelected(true);
                } else {
                    mChoice.setSelected(false);
                }
            }
        }
    }

    static class DurationAdapter extends ArrayAdapter<String> {
        private Context mContext;
        private int mChecked = -1;

        private DurationAdapter(@NonNull Context context) {
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
            BgShadowTextView mChoice;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Context context, String duration, int position, int checked) {
                mChoice.setText(context.getString(R.string.minute, duration));
//                mChoice.setSelected(false);
                if (checked == position) {
                    mChoice.setSelected(true);
                } else {
                    mChoice.setSelected(false);
                }
            }
        }
    }

    private void whetherLaunchBattle() {
        if (mChooseFutures.isSelected()
                && mIngotWar.isSelected()
                && (mBountySelected)
                && (mDurationSelected)) {
            mLaunchBattle.setEnabled(true);
        } else {
            mLaunchBattle.setEnabled(false);
        }
    }


    private void launchBattle() {
        Client.launchBattle(mVarietyId, mCoinType, mReward, mEndTime).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Battle>, Battle>() {
                    @Override
                    protected void onRespSuccessData(Battle battle) {
                        Launcher.with(getActivity(), BattleActivity.class)
                                .putExtra(ExtraKeys.BATTLE, battle)
                                .putExtra(ExtraKeys.USER_FUND, mUserFundInfo)
                                .execute();
                        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(CREATE_SUCCESS_ACTION));
                        finish();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        if (failedResp.isInsufficientFund()) {
                            SmartDialog.with(getActivity(), getString(R.string.balance_is_not_enough))
                                    .setPositive(R.string.go_recharge, new SmartDialog.OnClickListener() {
                                        @Override
                                        public void onClick(Dialog dialog) {
                                            openRechargePage(mCoinType);
                                            dialog.dismiss();
                                        }
                                    })
                                    .setTitle(R.string.hint)
                                    .setNegative(R.string.cancel)
                                    .show();
                        } else {
                            ToastUtil.show(failedResp.getMsg());
                        }
                    }
                }).fire();
    }

    private void openRechargePage(int coinType) {
        switch (coinType) {
            case Battle.COIN_TYPE_INGOT:
                Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_INGOT)
                        .execute();
                break;
            case Battle.COIN_TYPE_SCORE:
                Launcher.with(getActivity(), VirtualProductExchangeActivity.class)
                        .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_SCORE)
                        .execute();
                break;
        }
    }
}
