package com.sbai.finance.fragment.future;

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
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.model.future.FutureData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.CustomSwipeRefreshLayout;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class FutureListFragment extends BaseFragment implements AbsListView.OnScrollListener,
        SwipeRefreshLayout.OnRefreshListener, CustomSwipeRefreshLayout.OnLoadMoreListener {

    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.rate)
    TextView mRate;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private Unbinder unbinder;

    private FutureListAdapter mFutureListAdapter;

    private String mFutureType;
    private int mPage;
    private HashSet<String> mSet;

    public static FutureListFragment newInstance(String type) {
        FutureListFragment futureListFragment = new FutureListFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        futureListFragment.setArguments(bundle);
        return futureListFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mFutureType = getArguments().getString("type");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_future_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPage = 0;
        mSet = new HashSet<>();
        initView();
    }
    public void scrollToTop(){
        mListView.smoothScrollToPosition(0);
    }
    private void initView() {
        mFutureListAdapter = new FutureListAdapter(getActivity());
        mSwipeRefreshLayout.setOnRefreshListener(this);
        mSwipeRefreshLayout.setOnLoadMoreListener(this);
        mSwipeRefreshLayout.setAdapter(mListView, mFutureListAdapter);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mFutureListAdapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Variety variety = (Variety) parent.getItemAtPosition(position);
                if (variety != null) {
                    Launcher.with(getActivity(), FutureTradeActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, variety).execute();
                }
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        Netty.get().addHandler(mNettyHandler);
        //reset();
        requestVarietyList();
    }

    @Override
    public void onPause() {
        super.onPause();
        Netty.get().removeHandler(mNettyHandler);
    }

    private NettyHandler mNettyHandler = new NettyHandler<Resp<FutureData>>() {
        @Override
        public void onReceiveData(Resp<FutureData> data) {
            if (data.getCode() == Netty.REQ_QUOTA && data.hasData()) {
                updateListViewVisibleItem(data.getData());
                mFutureListAdapter.addFutureData(data.getData());
            }
        }
    };

    private void updateListViewVisibleItem(FutureData data) {
        if (mListView != null && mFutureListAdapter != null) {
            int first = mListView.getFirstVisiblePosition();
            int last = mListView.getLastVisiblePosition();
            for (int i = first; i <= last; i++) {
                if (i < mFutureListAdapter.getCount()) {
                    Variety variety = mFutureListAdapter.getItem(i);
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
                              //  rate.setTextColor(ContextCompat.getColor(getActivity(), R.color.redPrimary));
                                rate.setSelected(true);
                                rate.setText("+" + FinanceUtil.formatWithScale(priceChange) + "%");
                            } else {
                                lastPrice.setTextColor(ContextCompat.getColor(getActivity(), R.color.greenAssist));
                                rate.setSelected(false);
                                rate.setText(FinanceUtil.formatWithScale(priceChange) + "%");
                            }
                        }
                    }
                }
            }
        }
    }

    private void updateFutureData(List<Variety> varietyList) {
        stopRefreshAnimation();
        if (mSet.isEmpty()){
           mFutureListAdapter.clear();
        }
        for (Variety variety:varietyList){
            if (mSet.add(variety.getContractsCode())){
                mFutureListAdapter.add(variety);
            }
        }
        if (varietyList.size() < 15) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }
        mFutureListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void requestVarietyList() {
        Client.getVarietyList(Variety.VAR_FUTURE, mPage, mFutureType).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateFutureData(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }

                }).fireSync();
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
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        int topRowVerticalPosition =
                (mListView == null || mListView.getChildCount() == 0) ? 0 : mListView.getChildAt(0).getTop();
        mSwipeRefreshLayout.setEnabled(firstVisibleItem == 0 && topRowVerticalPosition >= 0);
    }

    @Override
    public void onRefresh() {
        reset();
        requestVarietyList();
    }

    private void reset() {
        mPage = 0;
        mSet.clear();
        mSwipeRefreshLayout.setLoadMoreEnable(true);
    }

    @Override
    public void onLoadMore() {
        requestVarietyList();
    }

    public static class FutureListAdapter extends ArrayAdapter<Variety> {

        private HashMap<String, FutureData> mFutureDataList;

        public FutureListAdapter(@NonNull Context context) {
            super(context, 0);
            mFutureDataList = new HashMap<>();
        }

        public void addFutureData(FutureData futureData) {
            mFutureDataList.put(futureData.getInstrumentId(), futureData);
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
            viewHolder.bindingData(getItem(position), mFutureDataList, getContext());
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

            private void bindingData(Variety item, HashMap<String, FutureData> map, Context context) {
                mFutureName.setText(item.getVarietyName());
                if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_FUTURE)) {
                    mFutureCode.setText(item.getContractsCode());
                } else if (item.getBigVarietyTypeCode().equalsIgnoreCase(Variety.VAR_STOCK)) {
                    mFutureCode.setText(item.getVarietyType());
                }

                FutureData futureData = map.get(item.getContractsCode());
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
                    mLastPrice.setText("--");
                    mRate.setText("--.--%");
                    mRate.setSelected(true);
                }
            }
        }
    }
}
