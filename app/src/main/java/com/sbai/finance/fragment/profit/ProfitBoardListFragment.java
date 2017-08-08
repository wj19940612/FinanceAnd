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
    public static final int TYPE_TODY = 0;
    public static final int TYPE_WEEK = 1;

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
    private int mType;

    public static ProfitBoardListFragment newInstance(int type) {
        ProfitBoardListFragment profitBoardListFragment = new ProfitBoardListFragment();
        Bundle bundle = new Bundle();
        bundle.putInt("type", type);
        profitBoardListFragment.setArguments(bundle);
        return profitBoardListFragment;
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
    }

    private void initView() {
        mSet = new HashSet<>();
        mLeaderBoardAdapter = new IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter(getActivity());
        mListView.setAdapter(mLeaderBoardAdapter);
        mListView.setEmptyView(mEmpty);
    }

    private void requestProfitBoardData() {
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
