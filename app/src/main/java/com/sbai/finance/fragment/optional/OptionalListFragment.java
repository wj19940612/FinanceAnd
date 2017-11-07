package com.sbai.finance.fragment.optional;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.fragment.dialog.AddOptionalDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
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
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * 自选列表页面
 */

public class OptionalListFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {

    public static final int OPTIONAL_CHANGE = 222;
    public static final String OPTIONAL_CHANGE_ACTION = "222";
    @BindView(R.id.rate)
    TextView mRate;
    @BindView(R.id.listView)
    SlideListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.addOptional)
    LinearLayout mAddOptional;
    Unbinder unbinder;
    private SlideListAdapter mSlideListAdapter;
    private int mPage = 0;
    private int mPageSize = 200;
    private HashSet<String> mSet;

    private BroadcastReceiver mOptionalChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() == OPTIONAL_CHANGE_ACTION) {
                Variety variety = intent.getExtras().getParcelable(Launcher.EX_PAYLOAD);
                boolean isAddOptional = intent.getExtras().getBoolean(Launcher.EX_PAYLOAD_1, false);
                if (variety != null) {
                    for (int i = 0; i < mSlideListAdapter.getCount(); i++) {
                        if (variety.getVarietyId() == mSlideListAdapter.getItem(i).getVarietyId()) {
                            variety = mSlideListAdapter.getItem(i);
                            requestDelOptionalData(variety);
                            break;
                        }
                    }
                }
                if (isAddOptional) {
                    reset();
                    requestOptionalData();
                }
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_optional, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        initBroadcastReceiver();
        requestOptionalData();
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(OPTIONAL_CHANGE_ACTION);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOptionalChangeReceiver, intentFilter);
    }

    private void initView() {
        mSet = new HashSet<>();
        mSlideListAdapter = new SlideListAdapter(getContext());
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mSlideListAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mSlideListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variety variety = (Variety) parent.getItemAtPosition(position);
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).executeForResult(OPTIONAL_CHANGE);
                }
                if (variety != null && variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    if (variety.getSmallVarietyTypeCode().equalsIgnoreCase(Variety.STOCK_EXPONENT)) {
                        Launcher.with(getActivity(), StockIndexActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).executeForResult(OPTIONAL_CHANGE);
                    } else {
                        Launcher.with(getActivity(), StockDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, variety).executeForResult(OPTIONAL_CHANGE);
                    }
                }
            }
        });
    }

    private void requestOptionalData() {
        Client.getOptional(mPage).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateOptionInfo((ArrayList<Variety>) data);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        stopRefreshAnimation();
                    }
                }).fireFree();
    }

    private void requestDelOptionalData(final Variety variety) {
        Client.delOptional(variety.getVarietyId()).setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            mSlideListAdapter.remove(variety);
                            mSlideListAdapter.notifyDataSetChanged();
                        } else {
                            ToastUtil.show(resp.getMsg());
                            stopRefreshAnimation();
                        }
                    }
                }).fire();
    }

    /**
     * 新批量请求股票行情接口
     *
     * @param data
     */
    private void requestStockMarketData(List<Variety> data) {
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
                        mSlideListAdapter.addStockData(result);
                    }
                }).fireFree();
    }


    private void requestFutureMarketData(List<Variety> data) {
        if (data == null || data.isEmpty()) return;
        StringBuilder stringBuilder = new StringBuilder();
        for (Variety variety : data) {
            stringBuilder.append(variety.getContractsCode()).append(",");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        Client.getFutureMarketData(stringBuilder.toString()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<FutureData>>, List<FutureData>>() {
                    @Override
                    protected void onRespSuccessData(List<FutureData> data) {
                        mSlideListAdapter.addFutureData(data);
                    }
                })
                .fireFree();
    }

    private void updateOptionInfo(ArrayList<Variety> data) {
        stopRefreshAnimation();
        mSlideListAdapter.clear();
        for (Variety variety : data) {
            if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                if (mSet.add(variety.getVarietyType())) {
                    mSlideListAdapter.add(variety);
                }
            } else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                if (mSet.add(variety.getContractsCode())) {
                    mSlideListAdapter.add(variety);
                }
            }
        }
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
        for (Variety variety : data) {
            if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                stocks.add(variety);
            } else if (variety.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
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
        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    @OnClick(R.id.addOptional)
    public void onClick(View view) {
        if (LocalUser.getUser().isLogin()) {
            AddOptionalDialogFragment.newInstance().show(getFragmentManager());
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mOptionalChangeReceiver);
    }

    private void reset() {
        mPage = 0;
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPTIONAL_CHANGE && resultCode == RESULT_OK) {
            Variety variety = data.getParcelableExtra(Launcher.EX_PAYLOAD);
            boolean isOptionalChanged = data.getBooleanExtra(Launcher.EX_PAYLOAD_1, false);
            if (variety != null && isOptionalChanged) {
                for (int i = 0; i < mSlideListAdapter.getCount(); i++) {
                    if (variety.getVarietyId() == mSlideListAdapter.getItem(i).getVarietyId()) {
                        variety = mSlideListAdapter.getItem(i);
                        requestDelOptionalData(variety);
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onRefresh() {
        reset();
        requestOptionalData();
    }

    @Override
    public void onLoadMore() {
        requestOptionalData();
    }

    public static class SlideListAdapter extends ArrayAdapter<Variety> {
        Context mContext;
        private HashMap<String, FutureData> mFutureDataList;
        private HashMap<String, StockData> mStockDataList;

        public void addFutureData(List<FutureData> futureDataList) {
            for (FutureData futureData : futureDataList) {
                mFutureDataList.put(futureData.getInstrumentId(), futureData);
            }
            notifyDataSetChanged();
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData stockData : stockDataList) {
                mStockDataList.put(stockData.getInstrumentId(), stockData);
            }
            notifyDataSetChanged();
        }

        public SlideListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
            mFutureDataList = new HashMap<>();
            mStockDataList = new HashMap<>();
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mFutureDataList, mStockDataList, mContext);
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

            ViewHolder(View content) {
                ButterKnife.bind(this, content);
            }

            private void bindDataWithView(Variety item, HashMap<String, FutureData> futureMap, HashMap<String, StockData> stockMap, Context context) {
                if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    mFutureName.setText(item.getVarietyName());
                    mFutureCode.setText(context.getString(R.string.stock) + " " + item.getVarietyType());
                    //      mFutureCode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.fanli_content_icon_shares),null,null,null);
                    StockData stockData = stockMap.get(item.getVarietyType());
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
                        mLastPrice.setText("--");
                        mRate.setSelected(true);
                        mRate.setText("--");
                    }
                } else if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    mFutureName.setText(item.getVarietyName());
                    mFutureCode.setText(context.getString(R.string.futures) + " " + item.getContractsCode());
                    //              mFutureCode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.fanli_content_icon_futures),null,null,null);
                    FutureData futureData = futureMap.get(item.getContractsCode());
                    if (futureData != null) {
                        double priceChange = FinanceUtil.subtraction(futureData.getLastPrice(), futureData.getPreSetPrice())
                                .divide(new BigDecimal(futureData.getPreSetPrice()), 4, RoundingMode.HALF_EVEN)
                                .multiply(new BigDecimal(100)).doubleValue();
                        mLastPrice.setText(FinanceUtil.formatWithScale(futureData.getLastPrice(), item.getPriceScale()));
                        if (priceChange >= 0) {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.redPrimary));
                            mRate.setSelected(true);
                            mRate.setText("+" + FinanceUtil.formatWithScale(priceChange) + "%");
                        } else {
                            mLastPrice.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                            mRate.setSelected(false);
                            mRate.setText(FinanceUtil.formatWithScale(priceChange) + "%");
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
}
