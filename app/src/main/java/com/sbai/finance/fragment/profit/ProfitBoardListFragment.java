package com.sbai.finance.fragment.profit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.leaderboard.IngotOrSavantLeaderBoardActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.transform.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.Set;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
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
    @BindView(R.id.tipInfo)
    TextView mTipInfo;
    @BindView(R.id.info)
    TextView mInfo;
    private IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter mLeaderBoardAdapter;
    private Set<Integer> mSet;
    private String mType;

    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestProfitBoardData();
        }
    };

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
        initInfoView();
        initView();
        initLoginReceiver();
        requestProfitBoardData();
    }

    private void initInfoView() {
        if (mType.equalsIgnoreCase(LeaderBoardRank.TODAY)) {
            mInfo.setText(getString(R.string.every_day_re_calculate));
        } else {
            mInfo.setText(getString(R.string.every_week_re_calculate));
        }
    }

    private void initLoginReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginReceiver, intentFilter);
    }

    private void initView() {
        mSet = new HashSet<>();
        initFooterView();
        mLeaderBoardAdapter = new IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter(getActivity(), LeaderBoardRank.PROFIT);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setLoadMoreEnable(false);
        mSwipeRefreshLayout.setAdapter(mListView, mLeaderBoardAdapter);
        mLeaderBoardAdapter.setCallback(new IngotOrSavantLeaderBoardActivity.LeaderBoardAdapter.Callback() {
            @Override
            public void onWarshipClick(LeaderBoardRank.DataBean item) {
                if (item.getUser() != null) {
                    if (LocalUser.getUser().isLogin()) {
                        requestWorship(item.getUser().getId());
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                }
            }
        });
        mListView.setAdapter(mLeaderBoardAdapter);
        mListView.setEmptyView(mEmpty);
    }

    private void initFooterView() {
        View view = new View(getActivity());
        AbsListView.LayoutParams params = new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (int) Display.dp2Px(60, getResources()));
        view.setLayoutParams(params);
        mListView.addFooterView(view);
    }

    public void scrollToTop() {
        mListView.smoothScrollToPosition(0);
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
        Client.worship(id, LeaderBoardRank.PROFIT, mType).setTag(TAG)
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
        mSet.clear();
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
        if (LocalUser.getUser().isLogin()) {
            mMyBoardInfo.setVisibility(View.VISIBLE);
            mTipInfo.setVisibility(View.GONE);
            GlideApp.with(getActivity())
                    .load(LocalUser.getUser().getUserInfo().getUserPortrait())
                    .placeholder(R.drawable.ic_default_avatar)
                    .transform(new GlideCircleTransform(getActivity()))
                    .into(mAvatar);
            mUserName.setText(getString(R.string.me));
            if (data.getType().equalsIgnoreCase(LeaderBoardRank.INGOT)
                    || data.getType().equalsIgnoreCase(LeaderBoardRank.PROFIT)) {
                if (data.getCurr() == null) {
                    mIngot.setText(getString(R.string.ingot_number_no_blank, 0));
                    mRank.setText(getString(R.string.you_no_enter_leader_board));
                } else {
                    if (data.getCurr().getNo() < 0) {
                        mRank.setText(getString(R.string.you_no_enter_leader_board));
                    } else {
                        if (data.getCurr().getNo() > 3) {
                            mRank.setText(getString(R.string.rank, data.getCurr().getNo()));
                        }
                    }
                    mIngot.setText(getString(R.string.ingot_number_no_blank, (int) data.getCurr().getScore()));
                }
            } else if (data.getType().equalsIgnoreCase(LeaderBoardRank.SAVANT)) {
                if (data.getCurr() == null) {
                    mIngot.setText(getString(R.string.integrate_number_no_blank, "0"));
                    mRank.setText(getString(R.string.you_no_enter_leader_board));
                } else {
                    if (data.getCurr().getNo() < 0) {
                        mRank.setText(getString(R.string.you_no_enter_leader_board));
                    } else {
                        if (data.getCurr().getNo() > 3) {
                            mRank.setText(getString(R.string.rank, data.getCurr().getNo()));
                        }
                    }
                    mIngot.setText(getString(R.string.integrate_number_no_blank, String.valueOf((int) data.getCurr().getScore())));
                }
            }
            if (data.getCurr() != null && (data.getCurr().getNo() > 0 && data.getCurr().getNo() < 4)) {
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
                    if (data.getType().equalsIgnoreCase(LeaderBoardRank.INGOT)
                            || data.getType().equalsIgnoreCase(LeaderBoardRank.PROFIT)) {
                        if (dataBean.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(getString(R.string.ingot_number_no_blank, (int) (data.getCurr().getScore())),
                                    " +" + getString(R.string.ingot_number_no_blank, dataBean.getWorshipCount())
                                    , ContextCompat.getColor(getActivity(), R.color.unluckyText)));
                        } else {
                            mIngot.setText(getString(R.string.ingot_number_no_blank, (int) (data.getCurr().getScore())));
                        }
                    } else if (data.getType().equalsIgnoreCase(LeaderBoardRank.SAVANT)) {
                        if (dataBean.getWorshipCount() > 0) {
                            mIngot.setText(StrUtil.mergeTextWithColor(getString(R.string.integrate_number_no_blank, String.valueOf(data.getCurr().getScore())),
                                    " +" + getString(R.string.ingot_number_no_blank, dataBean.getWorshipCount())
                                    , ContextCompat.getColor(getActivity(), R.color.unluckyText)));
                        } else {
                            mIngot.setText(getString(R.string.integrate_number_no_blank, String.valueOf((int) (data.getCurr().getScore()))));
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
        } else {
            mMyBoardInfo.setVisibility(View.GONE);
            mTipInfo.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(getActivity())
                .unregisterReceiver(mLoginReceiver);
    }

    @Override
    public void onLoadMore() {
    }


    @Override
    public void onRefresh() {
        reset();
        requestProfitBoardData();
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
    }

    @OnClick(R.id.tipInfo)
    public void onViewClicked() {
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }
}
