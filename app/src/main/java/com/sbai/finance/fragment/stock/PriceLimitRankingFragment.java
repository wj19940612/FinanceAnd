package com.sbai.finance.fragment.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;


public class PriceLimitRankingFragment extends BaseFragment {

    private static final String KEY_SORT_TYPE = "sort_type";
    private static final String KEY_STOCK_TYPE = "sort_type";

    @BindView(android.R.id.list)
    ListView mListView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;

    private Unbinder mBind;
    private int mSortType;
    private int mStockType;
    private StockSortAdapter mStockSortAdapter;

    public PriceLimitRankingFragment() {
    }

    public static PriceLimitRankingFragment newInstance(int sort_type, int stock_type) {
        PriceLimitRankingFragment fragment = new PriceLimitRankingFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_SORT_TYPE, sort_type);
        args.putInt(KEY_STOCK_TYPE, stock_type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mSortType = getArguments().getInt(KEY_SORT_TYPE);
            mStockType = getArguments().getInt(KEY_STOCK_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_price_limit_ranking, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mListView.setEmptyView(mEmpty);
        mStockSortAdapter = new StockSortAdapter(getActivity());
        mListView.setAdapter(mStockSortAdapter);
        requestStockSortList();
    }

    private void requestStockSortList() {
        Client.getSstockSort(mSortType, mStockType)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                    @Override
                    protected void onRespSuccessData(List<StockData> data) {
                        updateStockSort(data);
                    }
                })
                .fireSync();
    }

    private void updateStockSort(List<StockData> data) {
        if (data == null) return;
        mStockSortAdapter.clear();
        mStockSortAdapter.addAll(data);
    }


    @Override
    public void onResume() {
        super.onResume();
        startScheduleJob(6000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mBind.unbind();
    }


    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestStockSortList();
    }

    static class StockSortAdapter extends ArrayAdapter<StockData> {

        private Context mContext;

        public StockSortAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
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
            viewHolder.bindDataWithView(getItem(position));
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.futureName)
            TextView mFutureName;
            @BindView(R.id.futureCode)
            TextView mFutureCode;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.rate)
            TextView mRate;
            @BindView(R.id.trade)
            LinearLayout mTrade;
            @BindView(R.id.stopTrade)
            TextView mStopTrade;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(StockData item) {
                if (item == null) return;
                mFutureName.setText(item.getCode_name());
                mFutureCode.setText(item.getStock_code());
                String priceLimit = item.getValue1();
                if (!TextUtils.isEmpty(priceLimit)) {
                    if (priceLimit.startsWith("-")) {
                        mLastPrice.setSelected(true);
                        mRate.setSelected(true);
                        mRate.setText(priceLimit + "%");
                    } else {
                        mLastPrice.setSelected(false);
                        mRate.setSelected(false);
                        mRate.setText("+" + priceLimit + "%");
                    }
                }
                mLastPrice.setText(item.getLast_price());
            }
        }
    }
}
