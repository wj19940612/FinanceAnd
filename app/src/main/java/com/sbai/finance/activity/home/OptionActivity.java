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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.activity.stock.StockTradeActivity;
import com.sbai.finance.model.FutureData;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
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

public class OptionActivity extends BaseActivity implements AbsListView.OnScrollListener {

    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.listView)
    SlideListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private SlideListAdapter mSlideListAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestOptionalData();
            }
        });
        mSlideListAdapter = new SlideListAdapter(this);
        mSlideListAdapter.setOnDelClickListener(new SlideListAdapter.OnDelClickListener() {
            @Override
            public void onClick(final int position) {
                requestDelOptionalData(mSlideListAdapter.getItem(position).getVarietyId());
            }
        });
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
                    Launcher.with(getActivity(), StockTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                }
            }
        });
        mListView.setOnScrollListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestOptionalData();
        Netty.get().subscribe(Netty.REQ_SUB_ALL);
        Netty.get().addHandler(mNettyHandler);
    }

    @Override
    protected void onPause() {
        super.onPause();
        Netty.get().subscribe(Netty.REQ_UNSUB_ALL);
        Netty.get().removeHandler(mNettyHandler);
    }

    private void requestOptionalData() {
        Client.getOptional(Variety.VAR_FUTURE).setTag(TAG)
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

    private void updateOptionInfo(ArrayList<Variety> data) {
        stopRefreshAnimation();
        mSlideListAdapter.clear();
        mSlideListAdapter.addAll(data);
        mSlideListAdapter.notifyDataSetChanged();

    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }

    private NettyHandler mNettyHandler = new NettyHandler<Resp<FutureData>>() {
        @Override
        public void onReceiveData(Resp<FutureData> data) {
            if (data.getCode() == Netty.REQ_QUOTA && data.hasData()) {
                updateListViewVisibleItem(data.getData());
                mSlideListAdapter.addFutureData(data.getData());
            }
        }
    };

    private void updateListViewVisibleItem(FutureData data) {
        if (mListView != null && mSlideListAdapter != null) {
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i <= last; i++) {
                Variety variety = mSlideListAdapter.getItem(i);
                if (variety != null
                        && data.getInstrumentId().equalsIgnoreCase(variety.getContractsCode())) {
                    View childView = mListView.getChildAt(i - mListView.getFirstVisiblePosition());
                    if (childView != null) {
                        TextView lastPrice = ButterKnife.findById(childView, R.id.lastPrice);
                        TextView rate = ButterKnife.findById(childView, R.id.rate);
                        double priceChange = FinanceUtil.subtraction(data.getLastPrice(), data.getPreSetPrice())
                                .divide(new BigDecimal(data.getPreSetPrice()), 4, RoundingMode.HALF_EVEN)
                                .multiply(new BigDecimal(100)).doubleValue();
                        lastPrice.setText(FinanceUtil.formatWithScale(data.getLastPrice(), variety.getPriceScale()));
                        if (priceChange >= 0) {
                            lastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                            rate.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                            rate.setText("+" + FinanceUtil.formatWithScale(priceChange) + "%");
                        } else {
                            lastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
                            rate.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
                            rate.setText("-" + FinanceUtil.formatWithScale(priceChange) + "%");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    public static class SlideListAdapter extends ArrayAdapter<Variety> {
        Context mContext;
        private OnDelClickListener mOnDelClickListener;
        private HashMap<String, FutureData> mFutureDataList;

        interface OnDelClickListener {
            void onClick(int position);
        }

        public void addFutureData(FutureData futureData) {
            mFutureDataList.put(futureData.getInstrumentId(), futureData);
        }

        public SlideListAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
            mFutureDataList = new HashMap<>();
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
            viewHolder.bindDataWithView(getItem(position), mFutureDataList, mContext);
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

            private void bindDataWithView(Variety item, HashMap<String, FutureData> map, Context context) {
                mFutureName.setText(item.getVarietyName());
                mFutureCode.setText(item.getContractsCode());

                FutureData futureData = map.get(item.getContractsCode());
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
