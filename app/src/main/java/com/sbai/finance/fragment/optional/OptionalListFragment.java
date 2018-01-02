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
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.home.SearchOptionalActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.activity.stock.StockIndexActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.slidingListView.SlideListView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;

/**
 * 自选列表页面
 */

public class OptionalListFragment extends BaseFragment implements
        SwipeRefreshLayout.OnRefreshListener {

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
    private HashSet<String> mSet;

    private BroadcastReceiver mOptionalChangeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(OPTIONAL_CHANGE_ACTION)
                    || intent.getAction().equalsIgnoreCase(ACTION_LOGIN_SUCCESS)) {
                requestOptionalData();
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
        intentFilter.addAction(ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mOptionalChangeReceiver, intentFilter);
    }

    private void initView() {
        mSet = new HashSet<>();
        mSlideListAdapter = new SlideListAdapter(getContext());
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mSlideListAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mSlideListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Stock stock = (Stock) parent.getItemAtPosition(position);
                if (stock != null) {
                    if (stock.getType().equalsIgnoreCase(Stock.OPTIONAL_TYPE_INDEX)) {
                        Launcher.with(getActivity(), StockIndexActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, stock).executeForResult(OPTIONAL_CHANGE);
                    } else {
                        Launcher.with(getActivity(), StockDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, stock).executeForResult(OPTIONAL_CHANGE);
                    }
                }
            }
        });
    }

    private void requestOptionalData() {
        if (LocalUser.getUser().isLogin()) {
            Client.getOptional().setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<Stock>>, List<Stock>>() {
                        @Override
                        protected void onRespSuccessData(List<Stock> data) {
                            updateOptionInfo((ArrayList<Stock>) data);
                        }

                        @Override
                        public void onFinish() {
                            super.onFinish();
                            stopRefreshAnimation();
                        }
                    }).fireFree();
        }
    }

    /**
     * 新批量请求股票行情接口
     *
     * @param data
     */
    private void requestStockMarketData(List<Stock> data) {
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
                        mSlideListAdapter.addStockData(result);
                    }
                }).fireFree();
    }

    private void updateOptionInfo(ArrayList<Stock> data) {
        stopRefreshAnimation();
        mSlideListAdapter.clear();
        mSet.clear();
        for (Stock stock : data) {
            if (!TextUtils.isEmpty(stock.getVarietyCode()) && mSet.add(stock.getVarietyCode())) {
                mSlideListAdapter.add(stock);
            }
        }
        mSlideListAdapter.notifyDataSetChanged();
        requestStockMarketData(data);
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    @OnClick(R.id.addOptional)
    public void onClick(View view) {
        if (LocalUser.getUser().isLogin()) {
            umengEventCount(UmengCountEventId.FIND_OPTIONAL_ADD);
            Launcher.with(getContext(), SearchOptionalActivity.class).execute();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == OPTIONAL_CHANGE && resultCode == RESULT_OK) {
            requestOptionalData();
        }
    }

    @Override
    public void onRefresh() {
        requestOptionalData();
    }

    public static class SlideListAdapter extends ArrayAdapter<Stock> {
        Context mContext;
        private HashMap<String, StockData> mStockDataList;

        public void addStockData(List<StockData> stockDataList) {
            for (StockData stockData : stockDataList) {
                mStockDataList.put(stockData.getInstrumentId(), stockData);
            }
            notifyDataSetChanged();
        }

        public SlideListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
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
            viewHolder.bindDataWithView(getItem(position), mStockDataList, mContext);
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

            private void bindDataWithView(Stock item, HashMap<String, StockData> stockMap, Context context) {
                mFutureName.setText(item.getVarietyName());
                mFutureCode.setText(context.getString(R.string.stock) + " " + item.getVarietyCode());
                //      mFutureCode.setCompoundDrawablesWithIntrinsicBounds(ContextCompat.getDrawable(context,R.drawable.fanli_content_icon_shares),null,null,null);
                StockData stockData = stockMap.get(item.getVarietyCode());
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
                    mLastPrice.setText("--");
                    mRate.setSelected(true);
                    mRate.setText("--");
                }
            }
        }
    }
}
