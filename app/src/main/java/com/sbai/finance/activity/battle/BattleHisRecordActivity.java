package com.sbai.finance.activity.battle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.BattleProgress;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BattleHisRecordActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {


    public static final int BATTLE_HISTORY_RECORD_TYPE_GENERAL = 0;
    public static final int BATTLE_HISTORY_RECORD_TYPE_ARENA = 1;

    @BindView(R.id.title)
    TitleBar mTitleBar;
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
        setContentView(R.layout.activity_battle_record_his);
        ButterKnife.bind(this);
        mBattleType = getIntent().getIntExtra(ExtraKeys.BATTLE_HISTORY, BATTLE_HISTORY_RECORD_TYPE_GENERAL);
        initView();
        requestVersusData();
    }


    private void initView() {
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

    }

    private void requestVersusData() {
        if (mBattleType == BATTLE_HISTORY_RECORD_TYPE_GENERAL) {
            Client.getBattleHisRecord(mLocation).setTag(TAG)
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
            Client.requestArenaAllBattleHistory(mLocation).setTag(TAG)
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_battle_his, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.createAvatar)
            ImageView mCreateAvatar;
            @BindView(R.id.createKo)
            ImageView mCreateKo;
            @BindView(R.id.createAvatarRL)
            FrameLayout mCreateAvatarRL;
            @BindView(R.id.createName)
            TextView mCreateName;
            @BindView(R.id.varietyName)
            TextView mVarietyName;
            @BindView(R.id.progress)
            BattleProgress mProgress;
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstKo)
            ImageView mAgainstKo;
            @BindView(R.id.againstAvatarFL)
            FrameLayout mAgainstAvatarFL;
            @BindView(R.id.againstName)
            TextView mAgainstName;
            @BindView(R.id.rootLL)
            LinearLayout mRootLL;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final Battle item, Context context) {
                mVarietyName.setText(item.getVarietyName());

                GlideApp.with(context)
                        .load(item.getLaunchUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mCreateAvatar);
                mCreateName.setText(item.getLaunchUserName());
                mAgainstName.setText(item.getAgainstUserName());
                String reward = "";
                switch (item.getCoinType()) {
                    case Battle.COIN_TYPE_INGOT:
                        reward = context.getString(R.string.battle_reward_, item.getReward(), context.getString(R.string.ingot));
                        break;
                    case Battle.COIN_TYPE_CASH:
                        reward = context.getString(R.string.battle_reward_, item.getReward(), context.getString(R.string.cash));
                        break;
                    case Battle.COIN_TYPE_SCORE:
                        reward = context.getString(R.string.battle_reward_, item.getReward(), context.getString(R.string.integral));
                        break;
                }
                String varietyReward = context.getString(R.string.future_type_reward, item.getVarietyName(), reward);
                mVarietyName.setText(varietyReward);
                switch (item.getGameStatus()) {
                    case Battle.GAME_STATUS_CREATED:
                        mRootLL.setSelected(true);
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        mAgainstAvatar.setImageDrawable(null);
                        mAgainstAvatar.setImageResource(R.drawable.btn_join_battle);
                        mAgainstAvatar.setClickable(false);
                        mAgainstName.setText(context.getString(R.string.join_versus));
                        mProgress.setBattleProfit(0, 0);
                        break;
                    case Battle.GAME_STATUS_STARTED:
                        mRootLL.setSelected(true);
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        GlideApp.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .circleCrop()
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
                        mProgress.setBattleProfit(item.getLaunchScore(), item.getAgainstScore());
                        break;
                    case Battle.GAME_STATUS_END:
                        mRootLL.setSelected(false);
                        GlideApp.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .circleCrop()
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
                        if (item.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN) {
                            mCreateKo.setVisibility(View.VISIBLE);
                            mAgainstKo.setVisibility(View.GONE);
                        } else if (item.getWinResult() == Battle.WIN_RESULT_OWNER_WIN) {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.VISIBLE);
                        } else {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.GONE);
                        }
                        mProgress.setBattleProfit(item.getLaunchScore(), item.getAgainstScore());
                        break;

                }
            }
        }
    }
}
