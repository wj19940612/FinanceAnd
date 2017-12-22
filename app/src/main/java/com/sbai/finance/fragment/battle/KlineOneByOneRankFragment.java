package com.sbai.finance.fragment.battle;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.arena.KlineRankListActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.battle.KlineRank;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * Created by Administrator on 2017\12\12 0012.
 */

public class KlineOneByOneRankFragment extends BaseFragment {
    Unbinder unbinder;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;

    private OneByOneAdapter mOneByOneAdapter;
    private List<KlineRank.Rank1V1> mDataList;
    private boolean mHasEnter;

    public interface OnFragmentRecycleViewScrollListener {

        void onSwipRefreshEnable(boolean enabled, int fragmentPosition);

        void onFinish();
    }

    OnFragmentRecycleViewScrollListener mOnFragmentRecycleViewScrollListener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof KlineRankListActivity) {
            mOnFragmentRecycleViewScrollListener = (OnFragmentRecycleViewScrollListener) context;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_kline_one_by_one_rank, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mDataList = new ArrayList<>();
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHasEnter = false;
        unbinder.unbind();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mHasEnter) {
            mHasEnter = true;
            refresh();
        }
    }

    public void initScrollState() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() <= 0;
        if (mOnFragmentRecycleViewScrollListener != null) {
            mOnFragmentRecycleViewScrollListener.onSwipRefreshEnable(isTop, 0);
        }
    }

    private void initView() {
        mOneByOneAdapter = new OneByOneAdapter(getActivity(), mDataList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mOneByOneAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                if (mOnFragmentRecycleViewScrollListener != null) {
                    mOnFragmentRecycleViewScrollListener.onSwipRefreshEnable(isTop, 0);
                }
            }
        });
    }

    public void refresh() {
        Client.requestKlineRankData().setTag(TAG).setCallback(new Callback2D<Resp<KlineRank>, KlineRank>() {
            @Override
            protected void onRespSuccessData(KlineRank data) {
                updateKlineRank(data);
            }

            @Override
            public void onFinish() {
                super.onFinish();
                if (mOnFragmentRecycleViewScrollListener != null) {
                    mOnFragmentRecycleViewScrollListener.onFinish();
                }
            }
        }).fireFree();
    }

    private void updateKlineRank(KlineRank data) {
        ((KlineRankListActivity) getActivity()).setKlineRank(data);
        if (data.getRank1v1() == null || data.getRank1v1().size() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.GONE);
        }
        mOneByOneAdapter.clear();
        mOneByOneAdapter.addAll(data.getRank1v1());
    }

    static class OneByOneAdapter extends RecyclerView.Adapter<OneByOneAdapter.ViewHolder> {

        private Context mContext;
        private List<KlineRank.Rank1V1> mDataList;

        public OneByOneAdapter(Context context, List<KlineRank.Rank1V1> dataList) {
            mContext = context;
            mDataList = dataList;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kline_one_rank, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mDataList.get(position), position, getItemCount(), mContext);
        }

        public void clear() {
            mDataList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<KlineRank.Rank1V1> dataList) {
            mDataList.addAll(dataList);
            notifyDataSetChanged();
        }

        @Override
        public int getItemCount() {
            return mDataList == null ? 0 : mDataList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.headerLl)
            LinearLayout mHeaderLl;
            @BindView(R.id.rank)
            TextView mRank;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.winRate)
            TextView mWinRate;
            @BindView(R.id.rankingLL)
            RelativeLayout mRankingLL;
            @BindView(R.id.arenaAwardLL)
            LinearLayout mArenaAwardLL;

            public ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(KlineRank.Rank1V1 data, int position, int itemCount, Context context) {
                if (position == 0) {
                    mHeaderLl.setVisibility(View.VISIBLE);
                } else {
                    mHeaderLl.setVisibility(View.GONE);
                }

                if (position == itemCount - 1 && position == 0) {
                    mRankingLL.setBackgroundResource(R.drawable.bg_row_first_bottom);
                } else if (position == itemCount - 1) {
                    mRankingLL.setBackgroundResource(R.drawable.bg_row_rank_bottom);
                } else if (position == 0) {
                    mRankingLL.setBackgroundResource(R.drawable.bg_arena_award_ranking_first);
                } else {
                    if (position % 2 == 0) {
                        mRankingLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRanking));
                    } else {
                        mRankingLL.setBackgroundColor(ContextCompat.getColor(context, R.color.bgArenaRankingSecondColor));
                    }
                }

                mRank.setText(String.valueOf(position + 1));
                GlideApp.with(context).load(data.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop().into(mAvatar);

                mName.setText(data.getUserName());
                mWinRate.setText(String.format("%.2f", data.getRankingRate() * 100)+"%");

            }
        }
    }

}
