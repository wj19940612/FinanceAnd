package com.sbai.finance.fragment.battle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.battle.Battle;
import com.sbai.finance.model.battle.FutureVersus;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.OnItemClickListener;
import com.sbai.finance.view.BattleProgress;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BattleListFragment extends BaseFragment {
    private static final String BATTLE_TYPE = "column-count";

    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.customSwipeRefreshLayout)
    LinearLayout mCustomSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;


    private Set<Integer> mSet;

    private Unbinder mBind;

    private Long mLocationTime;

    private boolean mHasMoreData;
    private ArenaBattleListAdapter mArenaBattleListAdapter;
    private ArrayList<Battle> mBattleList;

    public BattleListFragment() {
    }

    @SuppressWarnings("unused")
    public static BattleListFragment newInstance(int columnCount) {
        BattleListFragment fragment = new BattleListFragment();
        Bundle args = new Bundle();
        args.putInt(BATTLE_TYPE, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_battle_list, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mBattleList = new ArrayList<>();
        initView();
    }

    private void initView() {
        mArenaBattleListAdapter = new ArenaBattleListAdapter(mBattleList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mArenaBattleListAdapter);
        mArenaBattleListAdapter.setOnItemClickListener(new OnItemClickListener<Battle>() {
            @Override
            public void onItemClick(Battle battle, int position) {
                // TODO: 2017/10/31 点击事件
            }
        });
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView) && mHasMoreData) {
                    requestArenaBattleList();
                }
            }
        });
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        return recyclerView != null &&
                recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    @Override
    public void onResume() {
        super.onResume();
        mSet.clear();
        requestArenaBattleList();
    }

    private void requestArenaBattleList() {
        Client.requestArenaBattleListData(mLocationTime)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<FutureVersus>, FutureVersus>() {
                    @Override
                    protected void onRespSuccessData(FutureVersus data) {
                        updateVersusData(data);
                    }
                })
                .fireFree();

    }

    private void updateVersusData(FutureVersus futureVersus) {
        if (futureVersus == null || futureVersus.getList().isEmpty() && mBattleList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
        }

        if (mSet.isEmpty()) {
            mArenaBattleListAdapter.clear();
        }
        if (futureVersus == null) return;

        for (Battle battle : futureVersus.getList()) {
            if (mSet.add(battle.getId())) {
                mArenaBattleListAdapter.add(battle);
            }
        }
        if (!futureVersus.hasMore()) {
            mHasMoreData = false;
        } else if (!futureVersus.getList().isEmpty()) {
            mHasMoreData = true;
            mLocationTime = futureVersus.getList().get(futureVersus.getList().size() - 1).getCreateTime();
        }
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    static class ArenaBattleListAdapter extends RecyclerView.Adapter<ArenaBattleListAdapter.ViewHolder> {

        private List<Battle> mBattleList;
        private Context mContext;
        private OnItemClickListener<Battle> mBattleOnItemClickListener;

        public ArenaBattleListAdapter(List<Battle> battleList, Context context) {
            mBattleList = battleList;
            mContext = context;
        }

        public void clear() {
            mBattleList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Battle> battleList) {
            mBattleList.addAll(battleList);
            notifyDataSetChanged();
        }

        public void add(Battle battle) {
            mBattleList.add(battle);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_future_versus_proceed, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mBattleList.get(position), position, mContext, mBattleOnItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mBattleList != null ? mBattleList.size() : 0;
        }

        public void setOnItemClickListener(OnItemClickListener<Battle> onItemClickListener) {
            mBattleOnItemClickListener = onItemClickListener;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.createAvatar)
            ImageView mCreateAvatar;
            @BindView(R.id.createKo)
            ImageView mCreateKo;
            @BindView(R.id.createName)
            TextView mCreateName;
            @BindView(R.id.varietyName)
            TextView mVarietyName;
            @BindView(R.id.progress)
            BattleProgress mProgress;
            @BindView(R.id.depositAndTime)
            TextView mDepositAndTime;
            @BindView(R.id.againstAvatar)
            ImageView mAgainstAvatar;
            @BindView(R.id.againstKo)
            ImageView mAgainstKo;
            @BindView(R.id.againstName)
            TextView mAgainstName;
            @BindView(R.id.rootLL)
            LinearLayout mRootLL;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final Battle item, final int position, Context context, final OnItemClickListener<Battle> battleOnItemClickListener) {
                mRootLL.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (battleOnItemClickListener != null) {
                            battleOnItemClickListener.onItemClick(item, position);
                        }
                    }
                });
                mVarietyName.setText(item.getVarietyName());
                GlideApp.with(context).load(item.getLaunchUserPortrait())
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

                switch (item.getGameStatus()) {
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
                        mProgress.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
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
                        } else if (item.getWinResult() == Battle.WIN_RESULT_CREATOR_WIN) {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.VISIBLE);
                        } else {
                            mCreateKo.setVisibility(View.GONE);
                            mAgainstKo.setVisibility(View.GONE);
                        }
                        mProgress.showScoreProgress(item.getLaunchScore(), item.getAgainstScore(), false);
                        break;
                }
            }
        }
    }
}
