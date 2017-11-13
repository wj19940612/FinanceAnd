package com.sbai.finance.fragment.stock;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.home.SearchOptionalActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.Network;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

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

public class StockListFragment extends BaseFragment
        implements SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener, AdapterView.OnItemClickListener {
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
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    Unbinder unbinder;

    private int mPage = 0;
    private int mPageSize = 15;

    private StockListAdapter mStockListAdapter;
    private List<Variety> mStockIndexData;
    private HashSet<String> mSet;
    private BroadcastReceiver mBroadcastReceiver = new Network.NetworkChangeReceiver() {
        @Override
        protected void onNetworkChanged(int availableNetworkType) {
            if (availableNetworkType > Network.NET_NONE) {
                requestStockIndexData();
                reset();
                requestStockData();
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
        requestStockData();
        requestStockIndexData();
    }

    private void initView() {
        mSet = new HashSet<>();
        mSearch.setFocusable(false);
        mStockListAdapter = new StockListAdapter(getContext());
        mListView.setAdapter(mStockListAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setOnItemClickListener(this);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mStockListAdapter);

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
        if (mListView != null && mStockListAdapter != null) {
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            List<Variety> varietyList = new ArrayList<>();
            for (int i = first; i <= last; i++) {
                Variety variety = mStockListAdapter.getItem(i);
                if (variety != null) {
                    if (variety.getExchangeStatus() == Variety.EXCHANGE_STATUS_OPEN) {
                        varietyList.add(variety);
                    }
                }
            }
            if (varietyList.size() > 0) {
                requestStockMarketData(varietyList);
                requestStockIndexMarketData(mStockIndexData);
            }
        }
    }

    private void requestStockData() {
        stopScheduleJob();
        Client.getStockVariety(mPage, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateStockData(data);
                        requestStockMarketData(data);
                        startScheduleJob(5 * 1000);
                    }
                }).fireFree();
    }

    private void requestStockMarketData(List<Variety> data) {
        if (data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
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

    private void requestStockIndexMarketData(List<Variety> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
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
        Client.getStockIndexVariety().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateStockIndexData(data);
                        requestStockIndexMarketData(data);
                    }
                }).fire();
    }

    private void updateStockIndexData(List<Variety> data) {
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
            case 3:
                mShangHai.setTag(data.get(0));
                mShangHai.setText(initStockIndex(data.get(0).getVarietyName()));
                mShenZhen.setTag(data.get(1));
                mShenZhen.setText(initStockIndex(data.get(1).getVarietyName()));
                mBoard.setTag(data.get(2));
                mBoard.setText(initStockIndex(data.get(2).getVarietyName()));
                break;
            default:
                break;
        }
        mStockIndexData = data;

    }

    private void updateStockIndexMarketData(List<StockData> data) {
        // 2.判断涨跌
        int redColor = ContextCompat.getColor(getContext(), R.color.redPrimary);
        int greenColor = ContextCompat.getColor(getContext(), R.color.greenAssist);
        int color;
        Variety variety;
        SpannableString spannableString;
        for (StockData stockData : data) {
            String rateChange = FinanceUtil.formatToPercentage(stockData.getUpDropSpeed());
            String ratePrice = stockData.getUpDropPrice();
            if (rateChange.startsWith("-")) {
                color = greenColor;
            } else {
                color = redColor;
                rateChange = "+" + rateChange;
                ratePrice = "+" + ratePrice;
            }
            variety = (Variety) mShangHai.getTag();
            if (variety != null && variety.getVarietyType().equalsIgnoreCase(stockData.getInstrumentId())) {
                spannableString = StrUtil.mergeTextWithRatioColor(variety.getVarietyName(),
                        "\n" + stockData.getLastPrice(), "\n" + ratePrice + "   " + rateChange, 1.133f, 0.667f, color, color);
                mShangHai.setText(spannableString);
            }
            variety = (Variety) mShenZhen.getTag();
            if (variety != null && variety.getVarietyType().equalsIgnoreCase(stockData.getInstrumentId())) {
                spannableString = StrUtil.mergeTextWithRatioColor(variety.getVarietyName(),
                        "\n" + stockData.getLastPrice(), "\n" + ratePrice + "   " + rateChange, 1.133f, 0.667f, color, color);
                mShenZhen.setText(spannableString);
            }
            variety = (Variety) mBoard.getTag();
            if (variety != null && variety.getVarietyType().equalsIgnoreCase(stockData.getInstrumentId())) {
                spannableString = StrUtil.mergeTextWithRatioColor(variety.getVarietyName(),
                        "\n" + stockData.getLastPrice(), "\n" + ratePrice + "   " + rateChange, 1.133f, 0.667f, color, color);
                mBoard.setText(spannableString);
            }
        }
    }

    private void updateStockData(List<Variety> data) {
        stopRefreshAnimation();
        if (mSet.isEmpty()) {
            mStockListAdapter.clear();
        }
        for (Variety variety : data) {
            if (mSet.add(variety.getVarietyType())) {
                mStockListAdapter.add(variety);
            }
        }
        if (data.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }
        mStockListAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.search, R.id.shangHai, R.id.shenZhen, R.id.board})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.search:
                Launcher.with(getActivity(), SearchOptionalActivity.class).putExtra("type", SearchOptionalActivity.TYPE_STOCK_ONLY).execute();
                break;
            case R.id.shangHai:
                launcherIndexActivity((Variety) mShangHai.getTag());
                break;
            case R.id.shenZhen:
                launcherIndexActivity((Variety) mShenZhen.getTag());
                break;
            case R.id.board:
                launcherIndexActivity((Variety) mBoard.getTag());
                break;
        }
    }

    private void launcherIndexActivity(Variety variety) {
        if (variety != null && variety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.STOCK_EXPONENT)) {
            Launcher.with(getActivity(), StockIndexActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, variety).execute();
        }
    }

    @Override
    public void onRefresh() {
        reset();
        requestStockData();
    }

    private void reset() {
        mPage = 0;
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    @Override
    public void onLoadMore() {
        requestStockData();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Variety variety = (Variety) parent.getAdapter().getItem(position);
        if (variety != null) {
            Launcher.with(getActivity(), StockDetailActivity.class).
                    putExtra(Launcher.EX_PAYLOAD, variety).execute();
        }
    }

    public static class StockListAdapter extends ArrayAdapter<Variety> {

        private HashMap<String, StockData> mStockDataList;

        public StockListAdapter(@NonNull Context context) {
            super(context, 0);
            mStockDataList = new HashMap<>();
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData data : stockDataList) {
                mStockDataList.put(data.getInstrumentId(), data);
            }
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(getItem(position), mStockDataList, getContext());
            return convertView;
        }

        static class ViewHolder {

            @BindView(R.id.futureName)
            TextView mFutureName;
            @BindView(R.id.futureCode)
            TextView mFutureCode;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.rate)
            TextView mRate;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Variety item, HashMap<String, StockData> map, Context context) {
                mFutureName.setText(item.getVarietyName());
                if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    mFutureCode.setText(item.getContractsCode());
                } else if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    mFutureCode.setText(item.getVarietyType());
                }

                StockData stockData = map.get(item.getVarietyType());
                if (stockData != null) {
                    mLastPrice.setText(stockData.getLastPrice());
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
