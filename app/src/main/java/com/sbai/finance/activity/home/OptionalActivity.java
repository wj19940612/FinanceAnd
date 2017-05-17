package com.sbai.finance.activity.home;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.activity.stock.StockTradeActivity;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.slidingListView.SlideItem;
import com.sbai.finance.view.slidingListView.SlideListView;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017-04-18.
 */

public class OptionalActivity extends BaseActivity implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {

    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    SlideListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.varietyTitle)
    LinearLayout mVarietyTitle;

    private SlideListAdapter mSlideListAdapter;
    private int mPage;
    private int mPageSize = 15;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_optional);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mVarietyTitle.setVisibility(View.GONE);
        mSlideListAdapter = new SlideListAdapter(this);
        mSlideListAdapter.setOnDelClickListener(new SlideListAdapter.OnDelClickListener() {
            @Override
            public void onClick(final int position) {
                requestDelOptionalData(mSlideListAdapter.getItem(position).getVarietyId());
            }
        });
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mSlideListAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mSlideListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variety variety = (Variety) parent.getItemAtPosition(position);
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                }
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    if (variety.getVarietyType().equalsIgnoreCase(Variety.STOCK_EXPONENT_SH)
                            ||variety.getVarietyType().equalsIgnoreCase(Variety.STOCK_EXPONENT_SZ)
                            ||variety.getVarietyType().equalsIgnoreCase(Variety.STOCK_EXPONENT_GE)){
                        Launcher.with(getActivity(), StockIndexActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                    }else{
                        Launcher.with(getActivity(), StockDetailActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOptionalData();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private void requestOptionalData() {
        Client.getOptional(mPage).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateOptionInfo((ArrayList<Variety>) data);
                    }
                }).fireSync();
    }

    private void requestDelOptionalData(Integer varietyId) {
        Client.delOptional(varietyId).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            requestOptionalData();
                        } else {
                            ToastUtil.curt(resp.getMsg());
                            stopRefreshAnimation();
                        }
                    }
                }).fire();
    }
    private void requestStockMarketData(List<Variety> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getVarietyType()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getStockMarketData(stringBuilder.toString())
                .setCallback(new StockCallback<StockResp, List<StockData>>() {
                    @Override
                    public void onDataMsg(List<StockData> result, StockResp.Msg msg) {
                        if (result!=null){
                          mSlideListAdapter.addStockData(result);
                        }
                    }
                }).fireSync();
    }
    private void requestFutureMarketData(List<Variety> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getContractsCode()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getFutureMarketData(stringBuilder.toString()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<FutureData>>,List<FutureData>>() {
                    @Override
                    protected void onRespSuccessData(List<FutureData> data) {
                        mSlideListAdapter.addFutureData(data);
                    }
                })
              .fireSync();
    }

    private void updateOptionInfo(ArrayList<Variety> data) {
        if (data.isEmpty()){
            mVarietyTitle.setVisibility(View.GONE);
        }else{
            mVarietyTitle.setVisibility(View.VISIBLE);
        }
        stopRefreshAnimation();
        mSlideListAdapter.clear();
        mSlideListAdapter.addAll(data);
        if (data.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }
        mSlideListAdapter.notifyDataSetChanged();
        requestMarketData(data);
    }
    private void requestMarketData(ArrayList<Variety> data) {
        List<Variety> futures = new ArrayList<>();
        List<Variety> stocks = new ArrayList<>();
        for (Variety variety:data){
            if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)){
               stocks.add(variety);
            }else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE) ){
                futures.add(variety);
            }
        }
        requestFutureMarketData(futures);
        requestStockMarketData(stocks);
    }


    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
    @Override
    public void onRefresh() {
        reset();
        requestOptionalData();
    }

    private void reset() {
        mPage = 0;
        mSlideListAdapter.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    @Override
    public void onLoadMore() {
        requestOptionalData();
    }

    public static class SlideListAdapter extends ArrayAdapter<Variety> {
        Context mContext;
        private OnDelClickListener mOnDelClickListener;
        private HashMap<String, FutureData> mFutureDataList;
        private HashMap<String, StockData> mStockDataList;
        interface OnDelClickListener {
            void onClick(int position);
        }

        public void addFutureData(List<FutureData> futureDataList) {
            for (FutureData futureData:futureDataList){
               mFutureDataList.put(futureData.getInstrumentId(), futureData);
            }
            notifyDataSetChanged();
        }
        public void addStockData(List<StockData> stockDataList) {
            for (StockData stockData : stockDataList) {
                mStockDataList.put(stockData.getStock_code(), stockData);
            }
            notifyDataSetChanged();
        }
        public SlideListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
            mFutureDataList = new HashMap<>();
            mStockDataList = new HashMap<>();
        }

        public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
            mOnDelClickListener = onDelClickListener;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                View content = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
                View menu = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_delete_btn, parent, false);
                viewHolder = new ViewHolder(content, menu);
                SlideItem slideItem = new SlideItem(mContext);
                slideItem.setContentView(content, menu);
                convertView = slideItem;
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.mDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mOnDelClickListener.onClick(position);
                }
            });
            viewHolder.bindDataWithView(getItem(position), mFutureDataList,mStockDataList, mContext);
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
            private TextView mDel;

            ViewHolder(View content, View menu) {
                ButterKnife.bind(this, content);
                mDel = (TextView) menu.findViewById(R.id.del);
            }

            private void bindDataWithView(Variety item, HashMap<String, FutureData> futureMap,HashMap<String, StockData> stockMap, Context context) {
                if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)){
                    mFutureName.setText(item.getVarietyName());
                    mFutureCode.setText(item.getVarietyType());
                    StockData stockData = stockMap.get(item.getVarietyType());
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
                }else  {
                    mFutureName.setText(item.getVarietyName());
                    mFutureCode.setText(item.getContractsCode());
                    FutureData futureData = futureMap.get(item.getContractsCode());
                    if (futureData != null) {
                        double priceChange = FinanceUtil.subtraction(futureData.getLastPrice(), futureData.getPreSetPrice())
                                .divide(new BigDecimal(futureData.getPreSetPrice()), 4, RoundingMode.HALF_EVEN)
                                .multiply(new BigDecimal(100)).doubleValue();
                        mLastPrice.setText(FinanceUtil.formatWithScale(futureData.getLastPrice(), item.getPriceScale()));
                        if (priceChange >= 0) {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mRate.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mRate.setText("+" + FinanceUtil.formatWithScale(priceChange) + "%");
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                            mRate.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                            mRate.setText("-" + FinanceUtil.formatWithScale(priceChange) + "%");
                        }
                    } else {
                        mLastPrice.setText("--");
                        mRate.setText("--.--%");
                    }
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
