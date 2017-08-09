package com.sbai.finance.fragment.profit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.leaderboard.IngotOrSavantLeaderBoardActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
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
    @BindView(R.id.myBoardInfo)
    LinearLayout mMyBoardInfo;
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
        initMyBoardView();
        initView();
        requestProfitBoardData();
    }

    private void initView() {
        mSet = new HashSet<>();
        mLeaderBoardAdapter = new IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter(getActivity(), LeaderBoardRank.PROFIT);
        mLeaderBoardAdapter.setCallback(new IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter.Callback() {
            @Override
            public void onWarshipClick(LeaderBoardRank.DataBean item) {
                if (item.getUser() != null) {
                    requestWorship(item.getUser().getId());
                }
            }
        });
        mListView.setAdapter(mLeaderBoardAdapter);
        mListView.setEmptyView(mEmpty);
    }

    private void initMyBoardView() {
        if (LocalUser.getUser().isLogin()) {
            mMyBoardInfo.setVisibility(View.VISIBLE);
        } else {
            mMyBoardInfo.setVisibility(View.GONE);
        }
    }

    private void requestProfitBoardData() {
        Client.getleaderBoardList(LeaderBoardRank.PROFIT, mType).setTag(TAG)
                .setCallback(new Callback2D<Resp<LeaderBoardRank>, LeaderBoardRank>() {
                    @Override
                    protected void onRespSuccessData(LeaderBoardRank data) {
                        updateProfitBoardData(data);
                    }
                }).fireFree();
    }

    private void requestWorship(int id) {
        Client.worship(id).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            requestProfitBoardData();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void updateProfitBoardData(LeaderBoardRank data) {
        stopRefreshAnimation();
        mLeaderBoardAdapter.clear();
        for (LeaderBoardRank.DataBean dataBean : data.getData()) {
            if (dataBean.getUser() != null && mSet.add(dataBean.getUser().getId())) {
                mLeaderBoardAdapter.add(dataBean);
            }
        }
        mLeaderBoardAdapter.notifyDataSetChanged();
        updateMyLeaderData(data);
    }

    private void updateMyLeaderData(LeaderBoardRank data) {
        if (data.getCurr() == null) return;
        if (LocalUser.getUser().isLogin()) {
            mMyBoardInfo.setVisibility(View.VISIBLE);
            Glide.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mAvatar);
            mUserName.setText(LocalUser.getUser().getUserInfo().getUserName());
            if (mType.equalsIgnoreCase(LeaderBoardRank.INGOT)
                    || mType.equalsIgnoreCase(LeaderBoardRank.PROFIT)) {
                mIngot.setText(getString(R.string.ingot_number_no_blank, data.getCurr().getScore()));
            } else if (mType.equalsIgnoreCase(LeaderBoardRank.SAVANT)) {
                mIngot.setText(getString(R.string.integrate_number_no_blank, String.valueOf(data.getCurr().getScore())));
            }
            if (data.getCurr().getNo() > 3) {
                mRank.setText(getString(R.string.rank, data.getCurr().getNo()));
            } else {
                LeaderBoardRank.DataBean dataBean = null;
                int rank = 0;
                for (int i = 0; i < data.getData().size(); i++) {
                    if (i > 2) {
                        break;
                    }
                    if (data.getData().get(i).getUser() != null) {
                        if (data.getData().get(i).getUser().getId() == LocalUser.getUser().getUserInfo().getId()) {
                            dataBean = data.getData().get(i);
                            rank = i + 1;
                            break;
                        }
                    }
                }
                if (dataBean != null) {
                    if (mType.equalsIgnoreCase(LeaderBoardRank.INGOT)
                            || mType.equalsIgnoreCase(LeaderBoardRank.PROFIT)) {
                        if (dataBean.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(getString(R.string.ingot_number_no_blank, Math.round(data.getCurr().getScore())),
                                    " +" + getString(R.string.ingot_number_no_blank, dataBean.getWorshipCount())
                                    , ContextCompat.getColor(getActivity(), R.color.unluckyText)));
                        } else {
                            mIngot.setText(getString(R.string.ingot_number_no_blank, Math.round(data.getCurr().getScore())));
                        }
                    } else if (mType.equalsIgnoreCase(LeaderBoardRank.SAVANT)) {
                        if (dataBean.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(getString(R.string.integrate_number_no_blank, String.valueOf(data.getCurr().getScore())),
                                    " +" + getString(R.string.integrate_number_no_blank, String.valueOf(dataBean.getWorshipCount()))
                                    , ContextCompat.getColor(getActivity(), R.color.unluckyText)));
                        } else {
                            mIngot.setText(getString(R.string.integrate_number_no_blank, String.valueOf(data.getCurr().getScore())));
                        }
                    }

                }
                mRank.setText("");
                switch (rank) {
                    case 1:
                        mRank.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_rank_top_1), null);
                        break;
                    case 2:
                        mRank.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_rank_top_2), null);
                        break;
                    case 3:
                        mRank.setCompoundDrawablesRelativeWithIntrinsicBounds(null, null, ContextCompat.getDrawable(getActivity(), R.drawable.ic_rank_top_3), null);
                        break;
                }
            }
        }
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
