package com.sbai.finance.fragment.battle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.arena.ArenaAwardRanking;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.StrUtil;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BattleRankingFragment extends BaseFragment {


    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.customSwipeRefreshLayout)
    LinearLayout mCustomSwipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private ArenaAwardRankingAdapter mArenaAwardRankingAdapter;

    public BattleRankingFragment() {
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
        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.header_battle_ranking_lisr, null);
//        mListView.addHeaderView(headerView);
        mArenaAwardRankingAdapter = new ArenaAwardRankingAdapter(new ArrayList<ArenaAwardRanking>(),getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mArenaAwardRankingAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestArenaAwardRankingData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void requestArenaAwardRankingData() {
        Client.requestArenaAwardRankingData()
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<ArenaAwardRanking>>, List<ArenaAwardRanking>>() {
                    @Override
                    protected void onRespSuccessData(List<ArenaAwardRanking> data) {
                        updateAwardRankingList(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        // TODO: 2017/10/26  测试数据
                        List<ArenaAwardRanking> arenaAwardRankings = new ArrayList<ArenaAwardRanking>();
                        for (int i = 0; i < 30; i++) {
                            ArenaAwardRanking arenaAwardRanking = new ArenaAwardRanking();
                            arenaAwardRanking.setRanking(i + 1);
                            arenaAwardRanking.setAvatar("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3352433622,2935084950&fm=173&s=D00065BC440277ED42B0195E0300E0B2&w=400&h=387&img.JPEG");
                            arenaAwardRanking.setAward(" 小米\n max5 ");
                            arenaAwardRanking.setBattleCount(i * i);
                            arenaAwardRanking.setName(" 溺水的鱼 " + i + " " + i + "" + i + " 号");
                            arenaAwardRanking.setProfit(i * i * i * i * 10);
                            arenaAwardRanking.setBattleCount(i * i * i * i * i * 100);
                            arenaAwardRankings.add(arenaAwardRanking);
                        }
                        updateAwardRankingList(arenaAwardRankings);
                    }
                })
                .fireFree();
    }

    private void updateAwardRankingList(List<ArenaAwardRanking> data) {
        if (data != null) {
            mArenaAwardRankingAdapter.clear();
            mArenaAwardRankingAdapter.addAll(data);
        }
    }

    static class ArenaAwardRankingAdapter extends RecyclerView.Adapter<ArenaAwardRankingAdapter.ViewHolder> {

        private List<ArenaAwardRanking> mArenaAwardRankingList;
        private Context mContext;

        public ArenaAwardRankingAdapter(List<ArenaAwardRanking> arenaAwardRankingListRanking, Context context) {
            mArenaAwardRankingList = arenaAwardRankingListRanking;
            mContext = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_arena_award_ranking, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mArenaAwardRankingList.get(position), position, mContext);
        }

        public void clear() {
            mArenaAwardRankingList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<ArenaAwardRanking> arenaAwardRankingList) {
            mArenaAwardRankingList.addAll(arenaAwardRankingList);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mArenaAwardRankingList != null ? mArenaAwardRankingList.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.ranking)
            TextView mRanking;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.nameAndProfit)
            TextView mNameAndProfit;
            @BindView(R.id.battleCount)
            TextView mBattleCount;
            @BindView(R.id.award)
            TextView mAward;
            @BindView(R.id.arenaAwardLL)
            LinearLayout mArenaAwardLL;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(ArenaAwardRanking item, int position, Context context) {
                if (item == null) return;
                if (item.getRanking() < 4) {
                    switch (item.getRanking()) {
                        case 1:
                            mRanking.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rank_first, 0);
                            break;
                        case 2:
                            mRanking.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rank_second, 0);
                            break;
                        case 3:
                            mRanking.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_rank_third, 0);
                            break;
                    }
                    mRanking.setText("");
                } else {
                    mRanking.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                    mRanking.setText(String.valueOf(item.getRanking()));
                }


                if (position == 0) {
                    mArenaAwardLL.setBackgroundResource(R.drawable.bg_arena_award_ranking_first);
                } else {
                    if (position % 2 == 0) {
                        mArenaAwardLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRanking));
                    } else {
                        mArenaAwardLL.setBackgroundColor(ContextCompat.getColor(context, R.color.creditEndColor));
                    }
                }

                GlideApp.with(context)
                        .load(item.getAvatar())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mAvatar);
                SpannableString spannableString = StrUtil.mergeTextWithColor(item.getName(),
                        "\n+" + StrFormatter.formIngotNumber(item.getProfit()),
                        ContextCompat.getColor(context, R.color.yellowAssist));
                mNameAndProfit.setText(spannableString);
                mBattleCount.setText(String.valueOf(item.getBattleCount()));
                mAward.setText(item.getAward());
            }
        }
    }
}
