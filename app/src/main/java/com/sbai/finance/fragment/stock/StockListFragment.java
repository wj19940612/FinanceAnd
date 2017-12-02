package com.sbai.finance.fragment.stock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.home.SearchOptionalActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static com.sbai.finance.utils.Network.registerNetworkChangeReceiver;
import static com.sbai.finance.utils.Network.unregisterNetworkChangeReceiver;

/**
 * 股票列表
 */

public class StockListFragment extends BaseFragment {
    @BindView(R.id.search)
    EditText mSearch;
    @BindView(R.id.shangHai)
    TextView mShangHai;
    @BindView(R.id.shenZhen)
    TextView mShenZhen;
    @BindView(R.id.board)
    TextView mBoard;
    @BindView(R.id.rate)
    TextView mRate;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private int mPage = 0;
    private boolean mLoadMore = true;

    private StockListAdapter mStockListAdapter;
    private List<Stock> mStockIndexData;
    private HashSet<String> mSet;
    private BroadcastReceiver mBroadcastReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                requestStockIndexData();
                refreshData();
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initSwipeRefreshView();
        initRecyclerView();
        refreshData();
        requestStockIndexData();
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
        mStockListAdapter = new StockListAdapter(getContext());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mStockListAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(mRecyclerView) && mLoadMore) {
                    requestStockData(false);
                }
            }
        });
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        if (recyclerView == null) return false;
        return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    private void initView() {
        mSet = new HashSet<>();
        mSearch.setFocusable(false);
        mStockListAdapter = new StockListAdapter(getContext());

        mShangHai.setText(initStockIndex(getString(R.string.ShangHaiStockExchange)));
        mShenZhen.setText(initStockIndex(getString(R.string.ShenzhenStockExchange)));
        mBoard.setText(initStockIndex(getString(R.string.GrowthEnterpriseMarket)));

        registerNetworkChangeReceiver(getActivity(), mBroadcastReceiver);
    }

    private SpannableString initStockIndex(String market) {
        SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(market,
                "\n" + "--", "\n-- --", 1.133f, 0.667f,
                ContextCompat.getColor(getContext(), R.color.redPrimary),
                ContextCompat.getColor(getContext(), R.color.redPrimary));
        return attentionSpannableString;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        unregisterNetworkChangeReceiver(getActivity(), mBroadcastReceiver);
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestVisibleStockMarket();
    }

    public void refreshData() {
        mPage = 0;
        mSet.clear();
        mLoadMore = true;
        requestStockData(true);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onResume() {
        super.onResume();
        startScheduleJob(5 * 1000);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            startScheduleJob(5 * 1000);
        } else {
            stopScheduleJob();
        }
    }

    private void requestVisibleStockMarket() {
        if (mRecyclerView != null && mStockListAdapter != null) {
            RecyclerView.LayoutManager layoutManager = mRecyclerView.getLayoutManager();
            if (layoutManager instanceof LinearLayoutManager) {
                LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
                int lastItemPosition = linearManager.findLastVisibleItemPosition();
                int firstItemPosition = linearManager.findFirstVisibleItemPosition();
                List<Stock> stockList = new ArrayList<>();
                for (int i = firstItemPosition; i <= lastItemPosition; i++) {
                    Stock stock = mStockListAdapter.getStock(i);
                    if (stock != null) {
                        if (stock.getExchangeOpened() == Stock.EXCHANGE_STATUS_OPEN) {
                            stockList.add(stock);
                        }
                    }
                }
                if (stockList.size() > 0) {
                    requestStockMarketData(stockList);
                    requestStockIndexMarketData(mStockIndexData);
                }
            }
        }

    }

    private void requestStockData(final boolean isRefresh) {
        stopScheduleJob();
        Client.getStockVariety(mPage).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Stock>>, List<Stock>>() {
                    @Override
                    protected void onRespSuccessData(List<Stock> data) {
                        updateStockData(data, isRefresh);
                        requestStockMarketData(data);
                        startScheduleJob(5 * 1000);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fireFree();
    }

    private void requestStockMarketData(List<Stock> data) {
        if (data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Stock stock : data) {
            stringBuilder.append(stock.getVarietyCode()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                    @Override
                    protected void onRespSuccessData(List<StockData> result) {
                        mStockListAdapter.addStockData(result);
                    }
                }).fireFree();
    }

    private void requestStockIndexMarketData(List<Stock> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Stock stock : data) {
            stringBuilder.append(stock.getVarietyCode()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                    @Override
                    protected void onRespSuccessData(List<StockData> result) {
                        updateStockIndexMarketData(result);
                    }
                }).fireFree();
    }

    private void requestStockIndexData() {
        Client.getStockIndex().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Stock>>, List<Stock>>() {
                    @Override
                    protected void onRespSuccessData(List<Stock> data) {
                        updateStockIndexData(data);
                        requestStockIndexMarketData(data);
                    }
                }).fire();
    }

    private void updateStockIndexData(List<Stock> data) {
        switch (data.size()) {
            case 0:
                return;
            case 1:
                mShangHai.setTag(data.get(0));
                mShangHai.setText(initStockIndex(data.get(0).getVarietyName()));
                break;
            case 2:
                mShangHai.setTag(data.get(0));
                mShangHai.setText(initStockIndex(data.get(0).getVarietyName()));
                mShenZhen.setTag(data.get(1));
                mShenZhen.setText(initStockIndex(data.get(1).getVarietyName()));
                break;
            default:
                mShangHai.setTag(data.get(0));
                mShangHai.setText(initStockIndex(data.get(0).getVarietyName()));
                mShenZhen.setTag(data.get(1));
                mShenZhen.setText(initStockIndex(data.get(1).getVarietyName()));
                mBoard.setTag(data.get(2));
                mBoard.setText(initStockIndex(data.get(2).getVarietyName()));
                break;
        }
        mStockIndexData = data;

    }

    private void updateStockIndexMarketData(List<StockData> data) {
        if (getContext() == null) return;
        // 2.判断涨跌
        int redColor = ContextCompat.getColor(getContext(), R.color.redPrimary);
        int greenColor = ContextCompat.getColor(getContext(), R.color.greenAssist);
        int color;
        Stock stock;
        SpannableString spannableString;
        for (StockData stockData : data) {
            String rateChange = FinanceUtil.formatToPercentage(stockData.getUpDropSpeed());
            String ratePrice = stockData.getFormattedUpDropPrice();
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            stock = (Stock) mShangHai.getTag();
            if (stock != null && stock.getVarietyCode().equalsIgnoreCase(stockData.getInstrumentId())) {
                spannableString = StrUtil.mergeTextWithRatioColor(stock.getVarietyName(),
                        "\n" + stockData.getFormattedLastPrice(), "\n" + ratePrice + "   " + rateChange, 1.133f, 0.667f, color, color);
                mShangHai.setText(spannableString);
            }
            stock = (Stock) mShenZhen.getTag();
            if (stock != null && stock.getVarietyCode().equalsIgnoreCase(stockData.getInstrumentId())) {
                spannableString = StrUtil.mergeTextWithRatioColor(stock.getVarietyName(),
                        "\n" + stockData.getFormattedLastPrice(), "\n" + ratePrice + "   " + rateChange, 1.133f, 0.667f, color, color);
                mShenZhen.setText(spannableString);
            }
            stock = (Stock) mBoard.getTag();
            if (stock != null && stock.getVarietyCode().equalsIgnoreCase(stockData.getInstrumentId())) {
                spannableString = StrUtil.mergeTextWithRatioColor(stock.getVarietyName(),
                        "\n" + stockData.getFormattedLastPrice(), "\n" + ratePrice + "   " + rateChange, 1.133f, 0.667f, color, color);
                mBoard.setText(spannableString);
            }
        }
    }

    private void updateStockData(List<Stock> data, boolean isRefresh) {
        if (isRefresh) {
            mStockListAdapter.clear();
        }
        if (data.size() < Client.DEFAULT_PAGE_SIZE) {
            mLoadMore = false;
        } else {
            mPage++;
            mLoadMore = true;
        }
        mStockListAdapter.addAll(data);
        if (mStockListAdapter.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
        }
    }

    @OnClick({R.id.search, R.id.shangHai, R.id.shenZhen, R.id.board})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                Launcher.with(getActivity(), SearchOptionalActivity.class).putExtra("type", SearchOptionalActivity.TYPE_STOCK_ONLY).execute();
                break;
            case R.id.shangHai:
                launcherIndexActivity((Stock) mShangHai.getTag());
                break;
            case R.id.shenZhen:
                launcherIndexActivity((Stock) mShenZhen.getTag());
                break;
            case R.id.board:
                launcherIndexActivity((Stock) mBoard.getTag());
                break;
        }
    }

    private void launcherIndexActivity(Stock stock) {
        if (stock != null && stock.getVarietyType().equalsIgnoreCase(Stock.EXPEND)) {
            Launcher.with(getActivity(), StockIndexActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, stock).execute();
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    public static class StockListAdapter extends RecyclerView.Adapter<StockListAdapter.ViewHolder> {

        private HashMap<String, StockData> mStockDataList;
        private List<Stock> mStockList;
        private Context mContext;

        StockListAdapter(@NonNull Context context) {
            mStockList = new ArrayList<>();
            mContext = context;
            mStockDataList = new HashMap<>();
        }

        public void addAll(List<Stock> stocks) {
            mStockList.addAll(stocks);
            notifyDataSetChanged();
        }

        public void clear() {
            mStockList.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return mStockList.isEmpty();
        }

        public Stock getStock(int index) {
            if (mStockList.size() > index && index > -1) {
                return mStockList.get(index);
            }
            return null;
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData data : stockDataList) {
                mStockDataList.put(data.getInstrumentId(), data);
            }
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindingData(mStockList.get(position), mStockDataList, mContext);
        }

        @Override
        public int getItemCount() {
            return mStockList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.futureName)
            TextView mFutureName;
            @BindView(R.id.futureCode)
            TextView mFutureCode;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.rate)
            TextView mRate;
            @BindView(R.id.rootView)
            LinearLayout mRootView;


            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            private void bindingData(final Stock item, HashMap<String, StockData> map, final Context context) {
                mFutureName.setText(item.getVarietyName());
                mFutureCode.setText(item.getVarietyCode());
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Launcher.with(context, StockDetailActivity.class).
                                putExtra(Launcher.EX_PAYLOAD, item).execute();
                    }
                });

                StockData stockData = map.get(item.getVarietyCode());
                if (stockData != null) {
                    mLastPrice.setText(stockData.getFormattedLastPrice());
                    if (stockData.isDelist()) {
                        mRate.setEnabled(false);
                        mRate.setText(R.string.delist);
                        mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
                    } else {
                        mRate.setEnabled(true);
                        String priceChange = FinanceUtil.formatToPercentage(stockData.getUpDropSpeed());
                        if (priceChange.startsWith("-")) {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                            mRate.setSelected(false);
                            mRate.setText(priceChange);
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mRate.setSelected(true);
                            mRate.setText("+" + priceChange);
                        }
                    }
                } else {
                    mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                    mLastPrice.setText("--");
                    mRate.setSelected(true);
                    mRate.setText("--");
                }
            }
        }
    }
}
