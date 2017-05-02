package com.sbai.finance.fragment.future;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.future.FutureTradeActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.FutureData;
import com.sbai.finance.model.Variety;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.netty.Netty;
import com.sbai.finance.netty.NettyHandler;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;


public class FutureFragment extends BaseFragment {

    @BindView(R.id.rate)
    TextView mRate;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    TextView mEmpty;

    private Unbinder unbinder;

    private FutureListAdapter mFutureListAdapter;

    private String mFutureType;
    private Integer mPage = 0;
    private Integer mPageSize = 15;

    public static FutureFragment newInstance(String type) {
        FutureFragment futureFragment = new FutureFragment();
        Bundle bundle = new Bundle();
        bundle.putString("type", type);
        futureFragment.setArguments(bundle);
        return futureFragment;
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
        View view = inflater.inflate(R.layout.fragment_future, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPage = 0;
        mPageSize = 10;

        initView();
    }

    private void initView() {
        mFutureListAdapter = new FutureListAdapter(getActivity());
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
        requestVarietyList();
        Netty.get().addHandler(mNettyHandler);
    }

    @Override
    public void onPause() {
        super.onPause();
        Netty.get().removeHandler(mNettyHandler);
    }

    @OnClick(R.id.rate)
    public void onClick(View view) {

    }

    private NettyHandler mNettyHandler = new NettyHandler<Resp<FutureData>>() {
        @Override
        public void onReceiveData(Resp<FutureData> data) {
            if (data.getCode() == Netty.REQ_QUOTA) {
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

    private void updateFutureData(List<Variety> varietyList) {
        if (varietyList == null) {
            return;
        }
        mFutureListAdapter.clear();
        mFutureListAdapter.addAll(varietyList);
        mFutureListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    public void requestVarietyList() {
        Client.getVarietyList(Variety.VAR_FUTURE, mPage, mPageSize, mFutureType)
                .setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<Variety>>, List<Variety>>() {
                    @Override
                    protected void onRespSuccessData(List<Variety> data) {
                        updateFutureData(data);
                    }
                }).fireSync();
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
            @BindView(R.id.stopTrade)
            TextView mStopTrade;
            @BindView(R.id.trade)
            LinearLayout mTrade;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindingData(Variety item, HashMap<String, FutureData> map, Context context) {
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
