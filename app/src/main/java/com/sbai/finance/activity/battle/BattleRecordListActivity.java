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

import butterknife.BindView;
import butterknife.ButterKnife;

public class BattleRecordListActivity extends BaseActivity implements CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

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
        setContentView(R.layout.activity_future_versus_record);
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
                    Launcher.with(getActivity(), FutureBattleActivity.class)
                            .putExtra(ExtraKeys.BATTLE, item)
                            .execute();
                }
            }
        });
        scrollToTop(mTitleBar, mListView);
    }

    private void requestVersusData() {
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_future_versus_record, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.versusResultImg)
            ImageView mVersusResultImg;
            @BindView(R.id.versusResult)
            TextView mVersusResult;
            @BindView(R.id.versusVarietyAndProfit)
            TextView mVersusVarietyAndProfit;
            @BindView(R.id.myAvatar)
            ImageView mMyAvatar;
            @BindView(R.id.myName)
            TextView mMyName;
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstName)
            TextView mAgainstName;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(final Battle item, Context context) {
                GlideApp.with(context)
                        .load(item.getLaunchUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mMyAvatar);
                GlideApp.with(context)
                        .load(item.getAgainstUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mAgainstAvatar);
                mMyName.setText(item.getLaunchUserName());
                mAgainstName.setText(item.getAgainstUserName());
                String reward = "";
                if (item.getWinResult() == Battle.WIN_RESULT_TIE) {
                    switch (item.getCoinType()) {
                        case Battle.COIN_TYPE_INGOT:
                            reward = 0 + context.getString(R.string.ingot);
                            break;
                        case Battle.COIN_TYPE_CASH:
                            reward = 0 + context.getString(R.string.cash);
                            break;
                        case Battle.COIN_TYPE_SCORE:
                            reward = 0 + context.getString(R.string.integral);
                            break;
                    }

                } else if ((LocalUser.getUser().getUserInfo().getId() == item.getLaunchUser()
                        && item.getWinResult() == Battle.WIN_RESULT_CREATOR_WIN)
                        || (LocalUser.getUser().getUserInfo().getId() == item.getAgainstUser()
                        && item.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN)) {
                    switch (item.getCoinType()) {
                        case Battle.COIN_TYPE_INGOT:
                            reward = Math.round(item.getReward() - item.getCommission()) + context.getString(R.string.ingot);
                            break;
                        case Battle.COIN_TYPE_CASH:
                            reward = item.getReward() + context.getString(R.string.cash);
                            break;
                        case Battle.COIN_TYPE_SCORE:
                            reward = StrFormatter.getFormIntegrate(item.getReward() - item.getCommission()) + context.getString(R.string.integral);
                            break;
                    }
                } else {
                    switch (item.getCoinType()) {
                        case Battle.COIN_TYPE_INGOT:
                            reward = item.getReward() + context.getString(R.string.ingot);
                            break;
                        case Battle.COIN_TYPE_CASH:
                            reward = item.getReward() + context.getString(R.string.cash);
                            break;
                        case Battle.COIN_TYPE_SCORE:
                            reward = item.getReward() + context.getString(R.string.integral);
                            break;
                    }
                }
                if (LocalUser.getUser().getUserInfo().getId() == item.getLaunchUser()) {
                    if (item.getWinResult() == Battle.WIN_RESULT_CREATOR_WIN) {
                        mVersusResultImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_versus_victory));
                        mVersusResult.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                        mVersusResult.setText(context.getString(R.string.wing));
                        mVersusVarietyAndProfit.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                        mVersusVarietyAndProfit.setText(item.getVarietyName() + "  +" + reward);

                    } else if (item.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN) {
                        mVersusResultImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_versus_failure));
                        mVersusResult.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusResult.setText(context.getString(R.string.failure));
                        mVersusVarietyAndProfit.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusVarietyAndProfit.setText(item.getVarietyName() + "  -" + reward);
                    } else if (item.getWinResult() == Battle.WIN_RESULT_TIE) {
                        mVersusResultImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_versus_failure));
                        mVersusResult.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusResult.setText(context.getString(R.string.tie));
                        mVersusVarietyAndProfit.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusVarietyAndProfit.setText(item.getVarietyName() + "  " + reward);
                    }
                } else {
                    if (item.getWinResult() == Battle.WIN_RESULT_CHALLENGER_WIN) {
                        mVersusResultImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_versus_victory));
                        mVersusResult.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                        mVersusResult.setText(context.getString(R.string.wing));
                        mVersusVarietyAndProfit.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                        mVersusVarietyAndProfit.setText(item.getVarietyName() + "  +" + reward);

                    } else if (item.getWinResult() == Battle.WIN_RESULT_CREATOR_WIN) {
                        mVersusResultImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_versus_failure));
                        mVersusResult.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusResult.setText(context.getString(R.string.failure));
                        mVersusVarietyAndProfit.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusVarietyAndProfit.setText(item.getVarietyName() + "  -" + reward);
                    } else if (item.getWinResult() == Battle.WIN_RESULT_TIE) {
                        mVersusResultImg.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_versus_failure));
                        mVersusResult.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusResult.setText(context.getString(R.string.tie));
                        mVersusVarietyAndProfit.setTextColor(ContextCompat.getColor(context, R.color.white));
                        mVersusVarietyAndProfit.setText(item.getVarietyName() + "  " + reward);
                    }
                }
            }
        }
    }
}
