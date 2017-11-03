package com.sbai.finance.fragment.battle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.BuildConfig;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.arena.ArenaActivityAndUserStatus;
import com.sbai.finance.model.arena.ArenaAwardExchangeRule;
import com.sbai.finance.model.arena.ArenaAwardRanking;
import com.sbai.finance.model.arena.ArenaInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class BattleRankingFragment extends BaseFragment {


    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private Unbinder mBind;
    private ArenaAwardRankingAdapter mArenaAwardRankingAdapter;
    private ArrayList<ArenaAwardRanking> mArenaAwardRankingArrayList;


    public BattleRankingFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_arena_battle_ranking, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mArenaAwardRankingArrayList = new ArrayList<>();
        initView();
    }

    private void initView() {
        mArenaAwardRankingAdapter = new ArenaAwardRankingAdapter(mArenaAwardRankingArrayList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mArenaAwardRankingAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestArenaAwardRankingData();
        // TODO: 2017/11/3 模拟数据
//        ArrayList<ArenaAwardRanking> arenaAwardRankings = new ArrayList<>();
//        for (int i = 1; i < 31; i++) {
//            ArenaAwardRanking arenaAwardRanking = new ArenaAwardRanking();
//            arenaAwardRanking.setRank(i);
//            arenaAwardRanking.setUserName("溺水的鱼 " + i);
//            arenaAwardRanking.setUserPortrait("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3352433622,2935084950&fm=173&s=D00065BC440277ED42B0195E0300E0B2&w=400&h=387&img.JPEG");
//            arenaAwardRankings.add(arenaAwardRanking);
//        }
//
//        ArrayList<ArenaAwardExchangeRule> arenaAwardExchangeRules = new ArrayList<>();
//        for (int i = 0; i < 10; i++) {
//            ArenaAwardExchangeRule awardExchangeRule = new ArenaAwardExchangeRule();
//            if (i == 0) {
//                awardExchangeRule.setType(ArenaAwardExchangeRule.AWARD_EXCHANGE_RULE_TTPE_RANK);
//                awardExchangeRule.setPrizeName("鱼");
//                awardExchangeRule.setScore("1-10");
//            }
//            if (i == 3) {
//                awardExchangeRule.setType(ArenaAwardExchangeRule.AWARD_EXCHANGE_RULE_TTPE_RANK);
//                awardExchangeRule.setPrizeName("就卡萨会尽快恢复到");
//                awardExchangeRule.setScore("11-20");
//            }
//
//            if (i == 7) {
//                awardExchangeRule.setType(ArenaAwardExchangeRule.AWARD_EXCHANGE_RULE_TTPE_RANK);
//                awardExchangeRule.setPrizeName("小黄段子");
//                awardExchangeRule.setScore("21-30");
//            }
//            arenaAwardExchangeRules.add(awardExchangeRule);
//        }
//
//        processAward(arenaAwardRankings, arenaAwardExchangeRules);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }

    private void requestArenaAwardRankingData() {
        Client.requestArenaAwardRankingData(ArenaInfo.DEFAULT_ACTIVITY_CODE)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<ArenaAwardRanking>>, List<ArenaAwardRanking>>() {
                    @Override
                    protected void onRespSuccessData(List<ArenaAwardRanking> data) {
                        if (data != null) {
                            requestAwardExchangeRule(data);
                        }
//                        // TODO: 2017/11/1 模拟数据
//                        List<ArenaAwardRanking> arenaAwardRankings = new ArrayList<ArenaAwardRanking>();
//                        for (int i = 0; i < 30; i++) {
//                            ArenaAwardRanking arenaAwardRanking = new ArenaAwardRanking();
//                            arenaAwardRanking.setRank(i + 1);
//                            arenaAwardRanking.setUserPortrait("https://ss0.baidu.com/6ONWsjip0QIZ8tyhnq/it/u=3352433622,2935084950&fm=173&s=D00065BC440277ED42B0195E0300E0B2&w=400&h=387&img.JPEG");
//                            arenaAwardRanking.setPrizeName(" 小米\n max5 ");
//                            arenaAwardRanking.setTotalCount(i * i);
//                            arenaAwardRanking.setUserName(" 溺水的鱼 " + i + " " + i + "" + i + " 号");
//                            arenaAwardRanking.setScore(i * i * i * i * 10);
//                            arenaAwardRankings.add(arenaAwardRanking);
//                        }
//                        updateAwardRankingList(arenaAwardRankings);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);

                    }
                })
                .fireFree();
    }


    private void requestAwardExchangeRule(final List<ArenaAwardRanking> arenaAwardRankingList) {
        Client.requesrAwardExchangeRule(ArenaActivityAndUserStatus.DEFAULT_ACTIVITY_CODE)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<ArenaAwardExchangeRule>>, List<ArenaAwardExchangeRule>>() {
                    @Override
                    protected void onRespSuccessData(List<ArenaAwardExchangeRule> data) {
                        if (!data.isEmpty()) {
                            processAward(arenaAwardRankingList, data);
                        }
                    }
                })
                .fireFree();
    }

    private void processAward(List<ArenaAwardRanking> arenaAwardRankingList, List<ArenaAwardExchangeRule> data) {
        int endPosition = -1;
        int firstPosition = -1;
        for (ArenaAwardExchangeRule awardExchangeRule : data) {
            if (awardExchangeRule.isRanking()) {
                String score = awardExchangeRule.getScore();
                if (!TextUtils.isEmpty(score)) {
                    String[] split = score.split("-");
                    try {
                        if (split.length == 2) {
                            firstPosition = Integer.valueOf(split[0]);
                            endPosition = Integer.valueOf(split[1]);
                        }

                        if (endPosition != -1 && firstPosition != -1) {
                            for (int i = firstPosition-1; i < endPosition; i++) {
                                ArenaAwardRanking arenaAwardRanking = arenaAwardRankingList.get(i);
                                arenaAwardRanking.setPrizeName(awardExchangeRule.getPrizeName());
                            }
                        }
                    } catch (NumberFormatException e) {
                        if (BuildConfig.DEBUG) {
                            ToastUtil.show("服务端排行榜数据有问题");
                        }
                    }
                }
            }
        }

        mArenaAwardRankingAdapter.clear();
        mArenaAwardRankingAdapter.addAll(arenaAwardRankingList);
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
            @BindView(R.id.headerLl)
            LinearLayout mHeaderLl;
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
            @BindView(R.id.rankingLL)
            LinearLayout mRankingLL;
            @BindView(R.id.arenaAwardLL)
            LinearLayout mArenaAwardLL;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(ArenaAwardRanking item, int position, Context context) {
                if (position == 0) {
                    mHeaderLl.setVisibility(View.VISIBLE);
                } else {
                    mHeaderLl.setVisibility(View.GONE);
                }
                if (item == null) return;
                if (item.getRank() < 4) {
                    switch (item.getRank()) {
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
                    mRanking.setText(String.valueOf(item.getRank()));
                }


                if (position == 0) {
                    mRankingLL.setBackgroundResource(R.drawable.bg_arena_award_ranking_first);
                } else {
                    if (position % 2 == 0) {
                        mRankingLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRanking));
                    } else {
                        mRankingLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRankingSecondColor));
                    }
                }

                GlideApp.with(context)
                        .load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mAvatar);
                SpannableString spannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(),
                        "\n+" + String.valueOf(item.getScore()), 1.4f,
                        ContextCompat.getColor(context, R.color.yellowAssist));
                mNameAndProfit.setText(spannableString);
                mBattleCount.setText(String.valueOf(item.getTotalCount()));
                mAward.setText(item.getPrizeName());
            }
        }
    }
}
