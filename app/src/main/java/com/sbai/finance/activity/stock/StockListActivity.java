package com.sbai.finance.activity.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.market.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Modified by John on 2017-05-9.
 */
public class StockListActivity extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {

    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.marketArea)
    LinearLayout mMarketArea;
    @BindView(R.id.shangHai)
    TextView mShangHai;
    @BindView(R.id.shenZhen)
    TextView mShenZhen;
    @BindView(R.id.board)
    TextView mBoard;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.stock)
    EditText mStock;
    @BindView(R.id.search)
    ImageView mSearch;

    private int mPage = 0;
    private int mPageSize = 15;

    private StockListAdapter mStockListAdapter;
    private List<Variety> mStockIndexData;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stock_list);
        ButterKnife.bind(this);
        initView();

        requestStockData();
        requestStockIndexData();
    }

    private void initView() {
        mStock.setFocusable(false);
        mStockListAdapter = new StockListAdapter(this);
        mListView.setAdapter(mStockListAdapter);
        mListView.setEmptyView(mEmpty);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mStockListAdapter);

        mShangHai.setText(initStockIndex(getString(R.string.ShangHaiStockExchange)));
        mShenZhen.setText(initStockIndex(getString(R.string.ShenzhenStockExchange)));
        mBoard.setText(initStockIndex(getString(R.string.GrowthEnterpriseMarket)));
    }
    private SpannableString  initStockIndex(String market){
        SpannableString  attentionSpannableString = StrUtil.mergeTextWithRatioColor(market,
                "\n" + "--", "\n-- --", 1.133f, 0.667f,
                ContextCompat.getColor(this, R.color.redPrimary),
                ContextCompat.getColor(this, R.color.redPrimary));
        return attentionSpannableString;
    }
    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestVisibleStockMarket();
    }

    private void requestVisibleStockMarket() {
        if (mListView!=null&&mStockListAdapter!=null){
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            List<Variety>  varietyList = new ArrayList<>();
            for (int i = first; i <= last; i++) {
                Variety  variety = mStockListAdapter.getItem(i);
                if (variety.getExchangeStatus() == Variety.EXCHANGE_STATUS_OPEN){
                    varietyList.add(variety);
                }
            }
            for (Variety variety:mStockIndexData){
                if (variety.getExchangeStatus() == Variety.EXCHANGE_STATUS_OPEN){
                    varietyList.add(variety);
                }
            }
            if (varietyList.size()>0){
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
                        startScheduleJob(1000);
                    }
                }).fireSync();
    }

    private void requestStockMarketData(List<Variety> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new StockCallback<StockResp, List<StockData>>() {
                    @Override
                    public void onDataMsg(List<StockData> result, StockResp.Msg msg) {
                        mStockListAdapter.addStockData(result);
                    }
                }).fireSync();
    }
    private void requestStockIndexMarketData( List<Variety> data) {
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new StockCallback<StockResp, List<StockData>>() {
                    @Override
                    public void onDataMsg(List<StockData> result, StockResp.Msg msg) {
                        updateStockIndexMarketData(result);
                    }
                }).fireSync();
    }
    private void requestStockIndexData() {
        Client.getStockIndexVariety().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        mStockIndexData = data;
                        requestStockIndexMarketData(data);
                    }
                }).fire();
    }

    private void updateStockIndexMarketData(List<StockData> data) {
        // 2.判断涨跌
        int s2Color = ContextCompat.getColor(this,R.color.redPrimary);
        int s3Color = ContextCompat.getColor(this,R.color.greenPrimary);
        int color;
        TextView textView=null;
        String market="";
        for (StockData stockData:data){
            String rateChange = stockData.getRise_pre();
            if (rateChange.startsWith("-")) {
                color = s3Color;
                rateChange =rateChange+"%";
            }else{
                color = s2Color;
                rateChange = "+"+rateChange+"%";
            }
            switch (stockData.getStock_code()){
                case Variety.STOCK_EXPONENT_SH:
                    textView = mShangHai;
                    market = getString(R.string.ShangHaiStockExchange);
                    break;
                case Variety.STOCK_EXPONENT_SZ:
                    textView =mShenZhen;
                    market = getString(R.string.ShenzhenStockExchange);
                    break;
                case Variety.STOCK_EXPONENT_GE:
                    textView = mBoard;
                    market = getString(R.string.GrowthEnterpriseMarket);
                    break;
                default:
                    break;
            }
            if (textView!=null){
                SpannableString  spannableString = StrUtil.mergeTextWithRatioColor(market,
                        "\n" +stockData.getLast_price() , "\n"+stockData.getRise_price()+" "+rateChange, 1.133f, 0.667f, color,color);
                textView.setText(spannableString);
            }
        }
    }

    private void updateStockData(List<Variety> data) {
        stopRefreshAnimation();
        mStockListAdapter.addAll(data);
        if (data.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }
        mStockListAdapter.notifyDataSetChanged();
    }

    @OnClick({R.id.stock, R.id.search, R.id.marketArea})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.stock:
            case R.id.search:
                Launcher.with(getActivity(), SearchStockActivity.class).execute();
                break;
            case R.id.marketArea:
                // TODO: 2017/5/3 跳转指数详情
                break;
        }
    }

    @Override
    public void onRefresh() {
        reset();
        requestStockData();
    }

    private void reset() {
        mPage = 0;
        mStockListAdapter.clear();
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

    public static class StockListAdapter extends ArrayAdapter<Variety> {

        private HashMap<String, StockData> mStockDataList;

        public StockListAdapter(@NonNull Context context) {
            super(context, 0);
            mStockDataList = new HashMap<>();
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData data : stockDataList) {
                mStockDataList.put(data.getStock_code(), data);
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
            @BindView(R.id.stopTrade)
            TextView mStopTrade;
            @BindView(R.id.trade)
            LinearLayout mTrade;

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
                    mLastPrice.setText(stockData.getLast_price());
                    String priceChange = stockData.getRise_pre();
                    if (priceChange.startsWith("-")) {
                        mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                        mRate.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                        mRate.setText(priceChange + "%");
                    } else {

                        mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mRate.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                        mRate.setText("+" + priceChange + "%");
                    }
                } else {
                    mLastPrice.setText("--");
                    mRate.setText("--");
                }

                if (item.getExchangeStatus() == Variety.EXCHANGE_STATUS_CLOSE) {
                    mTrade.setVisibility(View.GONE);
                    mStopTrade.setVisibility(View.VISIBLE);
                } else {
                    mTrade.setVisibility(View.VISIBLE);
                    mStopTrade.setVisibility(View.GONE);
                }
            }
        }
    }

}
