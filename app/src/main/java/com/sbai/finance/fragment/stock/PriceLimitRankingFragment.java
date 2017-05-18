package com.sbai.finance.fragment.stock;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.stock.StockCallback;
import com.sbai.finance.net.stock.StockResp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.R.attr.padding;


public class PriceLimitRankingFragment extends BaseFragment {

    private static final String KEY_SORT_TYPE = "sort_type";
    private static final String KEY_STOCK_TYPE = "stock_type";


    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;

    private Unbinder mBind;

    private int mSortType;
    private int mStockType;
    private StockSortAdapter mStockSortAdapter;
    private ArrayList<StockData> mStockDataArrayList;

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
            Log.d(TAG, "onCreate: " + mSortType + " s " + mStockType);
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
        mStockDataArrayList = new ArrayList<>();
//        initEmptyView();
        mStockSortAdapter = new StockSortAdapter(R.layout.row_variey, mStockDataArrayList);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mStockSortAdapter);
        requestStockSortList();
    }

    public void requestStockSortList() {
        mStockDataArrayList.clear();
        Client.getStockSort(mSortType, mStockType)
                .setTag(TAG)
                .setCallback(new StockCallback<StockResp, ArrayList<StockData>>() {
                    @Override
                    public void onDataMsg(ArrayList<StockData> result, StockResp.Msg msg) {
                        updateStockSort(result);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        updateStockSort(null);
                    }
                })
                .fireSync();
    }

    private void updateStockSort(List<StockData> data) {
        if (data == null || data.isEmpty() && mStockDataArrayList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            mStockDataArrayList.addAll(data);
            mStockSortAdapter.notifyDataSetChanged();
        }
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

    private void initEmptyView() {
        mEmpty = new AppCompatTextView(getActivity());
        mEmpty.setPadding(0, 10 * padding, 0, 0);
        mEmpty.setGravity(Gravity.CENTER_HORIZONTAL);
        mEmpty.setTextColor(ContextCompat.getColor(getActivity(), R.color.assistText));
        mEmpty.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
        mEmpty.setCompoundDrawablePadding(padding);
        mEmpty.setCompoundDrawablesWithIntrinsicBounds(0, R.drawable.img_no_message, 0, 0);
    }

    static class StockSortAdapter extends BaseQuickAdapter<StockData, BaseViewHolder> {

        private List<StockData> mStockDataList;

        public StockSortAdapter(int layoutResId, List<StockData> data) {
            super(layoutResId, data);
            this.mStockDataList = data;
        }

        private void clear() {
            mStockDataList.clear();
            notifyDataSetChanged();
        }

        @Override
        protected void convert(BaseViewHolder helper, StockData item) {
            TextView mFutureName = helper.getView(R.id.futureName);
            TextView mFutureCode = helper.getView(R.id.futureCode);
            TextView mLastPrice = helper.getView(R.id.lastPrice);
            TextView mRate = helper.getView(R.id.rate);

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
