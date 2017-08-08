package com.sbai.finance.fragment.profit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.leaderboard.IngotOrSavantLeaderBoardActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 盈利榜
 */

public class ProfitBoardListFragment extends BaseFragment implements
        CustomSwipeRefreshLayout.OnLoadMoreListener, SwipeRefreshLayout.OnRefreshListener {

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.avatar)
    ImageView mAvatar;
    @BindView(R.id.userName)
    TextView mUserName;
    @BindView(R.id.ingot)
    TextView mIngot;
    @BindView(R.id.rank)
    TextView mRank;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;
    private IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter mLeaderBoardAdapter;
    private Set<Integer> mSet;
    private String mType;

    public static ProfitBoardListFragment newInstance(String type) {
        ProfitBoardListFragment profitBoardListFragment = new ProfitBoardListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        profitBoardListFragment.setArguments(bundle);
        return profitBoardListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mType = getArguments().getString("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profit_board, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        requestProfitBoardData();
    }

    private void initView() {
        mSet = new HashSet<>();
        mLeaderBoardAdapter = new IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter(getActivity(), LeaderBoardRank.PROFIT);
        mListView.setAdapter(mLeaderBoardAdapter);
        mListView.setEmptyView(mEmpty);
    }

    private void requestProfitBoardData() {
        Client.getleaderBoardList(LeaderBoardRank.PROFIT, mType).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<LeaderBoardRank>, LeaderBoardRank>() {
                    @Override
                    protected void onRespSuccessData(LeaderBoardRank data) {
                        updateProfitBoardData(data);
                    }
                }).fireFree();
    }

    private void updateProfitBoardData(LeaderBoardRank data) {
        stopRefreshAnimation();
        mLeaderBoardAdapter.clear();
        for (LeaderBoardRank.DataBean dataBean : data.getData()) {
            if (mSet.add(dataBean.getUser().getId())) {
                mLeaderBoardAdapter.add(dataBean);
            }
        }
        mLeaderBoardAdapter.notifyDataSetChanged();
    }

    private void requestMyProfitBoardData() {

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onLoadMore() {
        requestProfitBoardData();
    }


    @Override
    public void onRefresh() {
        reset();
        requestProfitBoardData();
        requestMyProfitBoardData();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    private void reset() {
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }
}
