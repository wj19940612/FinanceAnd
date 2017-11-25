package com.sbai.finance.activity.trade.trade;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.trade.stock.StockEntrustFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.model.stocktrade.Entrust;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.EmptyRecyclerView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 当日成交
 */

public class TodayBusinessActivity extends BaseActivity {

    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPage = 0;
    private boolean mLoadMore = true;
    private StockEntrustFragment.EntrustAdapter mBusinessAdapter;
    private StockUser mStockUser;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_business);
        ButterKnife.bind(this);
        initSwipeRefreshView();
        initRecyclerView();
        mStockUser = LocalUser.getUser().getStockUser();
        requestBusiness(true);
    }

    private void initSwipeRefreshView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshData();
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setEmptyView(mEmpty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mBusinessAdapter = new StockEntrustFragment.EntrustAdapter(getActivity(), false);
        mRecyclerView.setAdapter(mBusinessAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                handleRecycleScroll();
            }
        });
    }

    private void requestBusiness(final boolean isRefresh) {
        if (mStockUser == null) return;
        Client.requestTodayBusiness(mStockUser.getType(), mStockUser.getAccount(), mStockUser.getActivityCode(), mPage).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Entrust>>, List<Entrust>>() {
                    @Override
                    protected void onRespSuccessData(List<Entrust> data) {
                        updateBusiness(data, isRefresh);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateBusiness(List<Entrust> data, boolean isRefresh) {
        if (data == null || data.isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mEmpty.setVisibility(View.GONE);
            if (isRefresh) {
                mBusinessAdapter.clear();
            }
            if (data.size() < Client.DEFAULT_PAGE_SIZE) {
                mLoadMore = false;
            } else {
                mPage++;
                mLoadMore = true;
            }
            mBusinessAdapter.addAll(data);
        }
    }

    public void refreshData() {
        mPage = 0;
        mLoadMore = true;
        requestBusiness(true);
    }

    private void handleRecycleScroll() {
        if (isSlideToBottom(mRecyclerView) && mLoadMore) {
            requestBusiness(false);
        }
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
