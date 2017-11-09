package com.sbai.finance.activity.battle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BattleRecordResultListActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public static final int BATTLE_HISTORY_RECORD_TYPE_GENERAL = 0;
    public static final int BATTLE_HISTORY_RECORD_TYPE_ARENA = 1;
    @BindView(R.id.title)
    TitleBar mTitleBar;
    @BindView(R.id.successRate)
    TextView mSuccessRate;
    @BindView(R.id.battleCountAndIngot)
    TextView mBattleCountAndIngot;
    @BindView(R.id.headerLl)
    LinearLayout mHeaderLl;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.customSwipeRefreshLayout)
    CustomSwipeRefreshLayout mCustomSwipeRefreshLayout;

    private VersusRecordListAdapter mVersusRecordListAdapter;
    private Long mLocation;
    private HashSet<Integer> mSet;
    private int mBattleType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_future_versus_record);
        ButterKnife.bind(this);
        mBattleType = getIntent().getIntExtra(ExtraKeys.BATTLE_HISTORY, BATTLE_HISTORY_RECORD_TYPE_GENERAL);
        initView();
        requestVersusData();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initView() {
        String title = getString(R.string.versus_record,
                mBattleType == BATTLE_HISTORY_RECORD_TYPE_ARENA ?
                        getString(R.string.money_reward_game) : getString(R.string.ordinary_battle));
        mTitleBar.setTitle(title);

        mListView.setEmptyView(mEmpty);
        mSet = new HashSet<>();
        mVersusRecordListAdapter = new VersusRecordListAdapter(getActivity());
        mCustomSwipeRefreshLayout.setOnRefreshListener(this);
        mCustomSwipeRefreshLayout.setOnLoadMoreListener(this);
        mCustomSwipeRefreshLayout.setAdapter(mListView, mVersusRecordListAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mVersusRecordListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Battle item = (Battle) parent.getItemAtPosition(position);
                if (item != null) {
                    Launcher.with(getActivity(), BattleActivity.class)
                            .putExtra(ExtraKeys.BATTLE, item)
                            .execute();
                }
            }
        });
        scrollToTop(mTitleBar, mListView);
    }

    private void requestVersusData() {
        if (mBattleType == BATTLE_HISTORY_RECORD_TYPE_GENERAL) {
            Client.getMyVersusRecord(mLocation).setTag(TAG)
                    .setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
                        @Override
                        protected void onRespSuccessData(FutureVersus data) {
                            updateVersusData(data);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            stopRefreshAnimation();
                        }
                    }).fireFree();
        } else {
            Client.requestUserArenaBattleResult(mLocation).setTag(TAG)
                    .setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
                        @Override
                        protected void onRespSuccessData(FutureVersus data) {
                            updateVersusData(data);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            stopRefreshAnimation();
                        }
                    }).fireFree();
        }

    }

    private void updateVersusData(FutureVersus futureVersus) {
        if (mSet.isEmpty()) {
            mVersusRecordListAdapter.clear();
            updateUserBattleResult(futureVersus);
            List<Battle> list = futureVersus.getList();
            if (list != null && !list.isEmpty()) {
                mHeaderLl.setVisibility(View.VISIBLE);
            } else {
                mHeaderLl.setVisibility(View.GONE);
            }
        }
        for (Battle battle : futureVersus.getList()) {
            if (mSet.add(battle.getId())) {
                mVersusRecordListAdapter.add(battle);
            }
        }
        if (!futureVersus.hasMore()) {
            mCustomSwipeRefreshLayout.setLoadMoreEnable(false);
        } else if (futureVersus.getList().size() > 0) {
            mLocation = futureVersus.getList().get(futureVersus.getList().size() - 1).getCreateTime();
        }
        mVersusRecordListAdapter.notifyDataSetChanged();
    }

    private void updateUserBattleResult(FutureVersus futureVersus) {
        if (futureVersus != null) {
            mSuccessRate.setText(getString(R.string.win_rate, futureVersus.getBattleWinRate()));
            String ingot = futureVersus.getGold() > 0 ? "+" + futureVersus.getGold() : String.valueOf(futureVersus.getGold());
            mBattleCountAndIngot.setText(
                    getString(R.string.battle_count_profit,
                            StrFormatter.formIngotNumber(futureVersus.getTotalCount()),
                            ingot));
        }
    }

    @Override
    public void onLoadMore() {
        requestVersusData();
    }

    @Override
    public void onRefresh() {
        reset();
        requestVersusData();
    }

    private void reset() {
        mSet.clear();
        mLocation = null;
        mCustomSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    private void stopRefreshAnimation() {
        if (mCustomSwipeRefreshLayout.isRefreshing()) {
            mCustomSwipeRefreshLayout.setRefreshing(false);
        }
        if (mCustomSwipeRefreshLayout.isLoading()) {
            mCustomSwipeRefreshLayout.setLoading(false);
        }
    }

    static class VersusRecordListAdapter extends ArrayAdapter<Battle> {

        public VersusRecordListAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_future_versus_result_record, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext(), position);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.versusResult)
            TextView mVersusResult;
            @BindView(R.id.versusVariety)
            TextView mVersusVariety;
            @BindView(R.id.profit)
            TextView mProfit;
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstName)
            TextView mAgainstName;
            @BindView(R.id.rootLL)
            LinearLayout mRootLL;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final Battle item, Context context, int position) {

                boolean isHomeOwner = LocalUser.getUser().getUserInfo().getId() == item.getLaunchUser();

                String againstUserPortrait = isHomeOwner ? item.getAgainstUserPortrait() : item.getLaunchUserPortrait();
                String againstUserName = isHomeOwner ? item.getAgainstUserName() : item.getLaunchUserName();
                GlideApp.with(context)
                        .load(againstUserPortrait)
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mAgainstAvatar);
                mAgainstName.setText(againstUserName);
                mVersusVariety.setText(item.getVarietyName());
                String result;
                String profit = "";

                if (position % 2 == 0) {
                    mRootLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRankingSecondColor));
                } else {
                    mRootLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRanking));
                }
                if (item.getWinResult() == Battle.WIN_RESULT_TIE) {
                    result = context.getString(R.string.tie);
                    profit = context.getString(R.string.plus_int, 0);
                    mVersusResult.setSelected(false);
                    mProfit.setSelected(false);
                } else {
                    boolean mineIsWinBattle = mineIsWinBattle(item);
                    mVersusResult.setSelected(mineIsWinBattle);
                    mProfit.setSelected(mineIsWinBattle);
                    if (mineIsWinBattle) {
                        result = context.getString(R.string.wing);
                        profit = context.getString(R.string.plus_int, Math.round(item.getReward() - item.getCommission()));
                    } else {
                        result = context.getString(R.string.failure);
                        profit = context.getString(R.string.minus_int, item.getReward());
                    }
                    if (item.getCoinType() == Battle.COIN_TYPE_SCORE) {
                        profit = context.getString(R.string.arena_score, profit);
                    }
                }

                mProfit.setText(profit);
                mVersusResult.setText(result);
            }

            public boolean mineIsWinBattle(Battle battle) {
                boolean result = false;
                if (battle.getLaunchUser() == LocalUser.getUser().getUserInfo().getId()) {
                    result = battle.getWinResult() == 1;
                } else {
                    result = battle.getWinResult() == 2;
                }
                return result;
            }
        }

    }
}
