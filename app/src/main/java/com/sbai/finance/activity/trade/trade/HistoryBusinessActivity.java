package com.sbai.finance.activity.trade.trade;

import android.content.BroadcastReceiver;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.trade.stock.StockEntrustFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.model.stocktrade.Entrust;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.picker.DatePickerPopWin;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * 历史成交
 */

public class HistoryBusinessActivity extends BaseActivity {
    @BindView(R.id.startTime)
    TextView mStartTime;
    @BindView(R.id.startTimeArea)
    LinearLayout mStartTimeArea;
    @BindView(R.id.endTime)
    TextView mEndTime;
    @BindView(R.id.endTimeArea)
    LinearLayout mEndTimeArea;
    @BindView(R.id.lastAndCostPrice)
    TextView mLastAndCostPrice;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;

    private int mPage = 0;
    private boolean mLoadMore = true;
    private StockEntrustFragment.EntrustAdapter mBusinessAdapter;
    private StockUser mStockUser;
    private BroadcastReceiver mNetworkChangeReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                if (mStockUser == null) {
                    requestStockAccount();
                } else {
                    refreshData();
                }
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_business);
        ButterKnife.bind(this);
        initView();
        initSwipeRefreshView();
        initRecyclerView();
        mStockUser = LocalUser.getUser().getStockUser();
        requestBusiness(true);
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        registerNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterNetworkChangeReceiver(this, mNetworkChangeReceiver);
    }

    private void initView() {
        mStartTime.setText(DateUtil.format(System.currentTimeMillis() - 6 * 24 * 60 * 60 * 1000, "yyyy-MM-dd"));
        mEndTime.setText(DateUtil.format(System.currentTimeMillis(), "yyyy-MM-dd"));
    }

    @OnClick({R.id.startTimeArea, R.id.endTimeArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startTimeArea:
                showDatePickerDialog(mStartTime.getText().toString(), mStartTime);
                break;
            case R.id.endTimeArea:
                showDatePickerDialog(mEndTime.getText().toString(), mEndTime);
                break;
        }
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

    private void requestSwitchAccount(final StockUser stockUser) {
        Client.requestSwitchAccount(stockUser.getId(), stockUser.getAccount())
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        setCurrentStockUser(stockUser);
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        super.onRespFailure(failedResp);
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void requestStockAccount() {
        Client.getStockAccount().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockUser>>, List<StockUser>>() {
                    @Override
                    protected void onRespSuccessData(List<StockUser> data) {
                        if (!data.isEmpty()) {
                            updateStockAccount(data);
                        }
                    }
                }).fireFree();
    }

    private void updateStockAccount(List<StockUser> data) {
        for (StockUser stockUser : data) {
            if (stockUser.getActive() == StockUser.ACCOUNT_ACTIVE) {
                mStockUser = stockUser;
                break;
            }
        }
        if (mStockUser == null) {
            requestSwitchAccount(data.get(0));
        } else {
            setCurrentStockUser(mStockUser);
        }

    }

    private void setCurrentStockUser(StockUser stockUser) {
        mStockUser = stockUser;
        LocalUser.getUser().setStockUser(stockUser);
        refreshData();
    }

    private void requestBusiness(final boolean isRefresh) {
        if (mStockUser == null) return;
        Client.requestHistoryBusiness(mStockUser.getType(), mStockUser.getAccount(), mStockUser.getActivityCode(),
                mStartTime.getText().toString(), mEndTime.getText().toString(), mPage).setTag(TAG)
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
        if ((data == null || data.isEmpty()) && isRefresh) {
            mEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
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

    private void showDatePickerDialog(String date, final TextView time) {

        new DatePickerPopWin.Builder(getActivity(),
                new DatePickerPopWin.OnDatePickedListener() {
                    @Override
                    public void onDatePickCompleted(int year, int month, int day) {
                        time.setText(new String(year + "-" + month + "-" + day));
                        refreshData();
                    }
                })
                .textConfirm(getString(R.string.ok)) //text of confirm button
                .textCancel(getString(R.string.cancel)) //text of cancel button
                .colorCancel(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .colorConfirm(ContextCompat.getColor(getActivity(), R.color.colorPrimary))
                .btnTextSize(16) // button text size
                .viewTextSize(9) // pick view text size
                .minYear(1970) //min year in loop
                .maxYear(2550) // max year in loop
                .dateChose(date) // date chose when init popwindow
                .build()
                .showPopWin(getActivity());


    }


}
