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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;

import java.util.HashSet;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BattleHisRecordActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_battle_record_his);
        ButterKnife.bind(this);
        initView();
        requestVersusData();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            .putExtra(BattleActivity.PAGE_TYPE, BattleActivity.PAGE_TYPE_RECORD)
                            .putExtra(Launcher.EX_PAYLOAD, item).execute();
                }
            }
        });
        scrollToTop(mTitleBar, mListView);
    }

    private void requestVersusData() {
        Client.getBattleHisRecord(mLocation).setTag(TAG)
                .setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
                    @Override
                    protected void onRespSuccessData(FutureVersus data) {
                        updateVersusData(data);
                    }
                }).fireFree();
    }

    private void updateVersusData(FutureVersus futureVersus) {
        stopRefreshAnimation();
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
        mVersusRecordListAdapter.addAll(futureVersus.getList());
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
            private void bindDataWithView(final Battle item, Context context) {
                mVarietyName.setText(item.getVarietyName());
                Glide.with(context).load(item.getLaunchUserPortrait())
                        .load(item.getLaunchUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
                        .into(mCreateAvatar);
                mCreateName.setText(item.getLaunchUserName());
                mCreateProfit.setText(String.valueOf(item.getLaunchScore()));
                mAgainstName.setText(item.getAgainstUserName());
                mAgainstProfit.setText(String.valueOf(item.getAgainstScore()));
                String reward = "";
                switch (item.getCoinType()) {
                    case Battle.COIN_TYPE_BAO:
                        reward = item.getReward() + context.getString(R.string.ingot);
                        break;
                    case Battle.COIN_TYPE_CASH:
                        reward = item.getReward() + context.getString(R.string.cash);
                        break;
                    case Battle.COIN_TYPE_INTEGRAL:
                        reward = item.getReward() + context.getString(R.string.integral);
                        break;
                }
                switch (item.getGameStatus()) {
                    case Battle.GAME_STATUS_CREATED:
                        mDepositAndTime.setText(reward + " " + DateUtil.getMinutes(item.getEndline()));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        mAgainstAvatar.setImageDrawable(null);
                        mAgainstAvatar.setImageResource(R.drawable.btn_join_versus);
                        mAgainstAvatar.setClickable(false);
                        mAgainstName.setText(context.getString(R.string.join_versus));
                        showScoreProgress(0, 0, true);
                        break;
                    case Battle.GAME_STATUS_STARTED:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versusing));
                        mCreateKo.setVisibility(View.GONE);
                        mAgainstKo.setVisibility(View.GONE);
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
                        showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;
                    case Battle.GAME_STATUS_END:
                        mDepositAndTime.setText(reward + " " + context.getString(R.string.versus_end));
                        Glide.with(context).load(item.getLaunchUserPortrait())
                                .load(item.getAgainstUserPortrait())
                                .placeholder(R.drawable.ic_default_avatar_big)
                                .transform(new GlideCircleTransform(context))
                                .into(mAgainstAvatar);
                        mAgainstAvatar.setClickable(false);
                        if (item.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN) {
                            mCreateKo.setVisibility(View.VISIBLE);
                            mAgainstKo.setVisibility(View.GONE);
                        } else if (item.getWinResult() == Battle.WIN_RESULT_CREATOR_WIN) {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.VISIBLE);
                        } else {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.GONE);
                        }
                        showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;

                }
            }
            private void showScoreProgress(double createProfit, double fighterProfit, boolean isInviting) {
                String myFlag = "";
                String fighterFlag = "";
                if (isInviting) {
                    mProgressBar.setProgress(0);
                    mProgressBar.setSecondaryProgress(0);
                    mCreateProfit.setText(null);
                    mAgainstProfit.setText(null);
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
                    mProgressBar.setSecondaryProgress(100);
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
