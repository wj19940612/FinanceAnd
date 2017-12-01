package com.sbai.finance.fragment.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.stock.StockDetailActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.Stock;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.httplib.ApiError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 涨跌幅榜页面
 */
public class PriceLimitRankingFragment extends BaseFragment {

    private static final String KEY_DIRECTION = "direction";
    private static final String KEY_EXCHANGE_ID = "exchangeId";

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    TextView mEmpty;

    private Unbinder mBind;

    private int mDirection;
    private int mExchangeID;
    private StockSortAdapter mStockSortAdapter;
    private ArrayList<StockData> mStockDataArrayList;

    public PriceLimitRankingFragment() {
    }

    public static PriceLimitRankingFragment newInstance(int sortType, int stockType) {
        PriceLimitRankingFragment fragment = new PriceLimitRankingFragment();
        Bundle args = new Bundle();
        args.putInt(KEY_DIRECTION, sortType);
        args.putInt(KEY_EXCHANGE_ID, stockType);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mDirection = getArguments().getInt(KEY_DIRECTION);
            mExchangeID = getArguments().getInt(KEY_EXCHANGE_ID);
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
        mStockSortAdapter = new StockSortAdapter(getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mStockSortAdapter);
        requestStockSortList();
    }

    public void requestStockSortList() {
        Client.getStockSort(mDirection, mExchangeID).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                    @Override
                    protected void onRespSuccessData(List<StockData> result) {
                        updateStockSort(result);
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        updateStockSort(null);
                    }
                }).fireFree();
    }

    private void updateStockSort(List<StockData> data) {
        if (data == null || data.isEmpty() && mStockDataArrayList.isEmpty()) {
            mRecyclerView.setVisibility(View.GONE);
            mEmpty.setVisibility(View.VISIBLE);
        } else {
            mRecyclerView.setVisibility(View.VISIBLE);
            mEmpty.setVisibility(View.GONE);
            mStockSortAdapter.addAll(data);
        }
    }


    public void scrollToTop() {
        mRecyclerView.smoothScrollToPosition(0);
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

    class StockSortAdapter extends RecyclerView.Adapter<StockSortAdapter.ViewHolder> {

        Context mContext;
        ArrayList<StockData> mStockDataArrayList;

        public StockSortAdapter(Context context) {
            this.mContext = context;
            mStockDataArrayList = new ArrayList<>();
        }

        public void addAll(List<StockData> datas) {
            mStockDataArrayList.clear();
            mStockDataArrayList.addAll(datas);
            //先显示10个
            notifyItemRangeChanged(0, datas.size());
        }

//        public void clear() {
//            mStockDataArrayList.clear();
////            notifyItemRangeRemoved(0, mStockDataArrayList.size());
//            if(mStockDataArrayList.isEmpty()){
//
//                notifyItemRangeRemoved(0, 10);
//            }
//        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_variey, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (mStockDataArrayList.isEmpty()) return;
            holder.bindDataWithView(mStockDataArrayList.get(position), position, mContext);
        }

        @Override
        public int getItemCount() {
            return mStockDataArrayList != null ? mStockDataArrayList.size() : 0;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.futureName)
            TextView mFutureName;
            @BindView(R.id.futureCode)
            TextView mFutureCode;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.rate)
            TextView mRate;
            @BindView(R.id.rootView)
            LinearLayout mRootView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final StockData item, int position, final Context context) {
                if (item == null) return;
                mFutureName.setText(item.getName());
                mFutureCode.setText(item.getInstrumentId());
                String priceLimit = FinanceUtil.formatToPercentage(item.getUpDropSpeed());
                if (!TextUtils.isEmpty(priceLimit)) {
                    if (priceLimit.startsWith("-")) {
                        mLastPrice.setSelected(false);
                        mRate.setSelected(false);
                        mRate.setText(priceLimit);
                    } else {
                        mLastPrice.setSelected(true);
                        mRate.setSelected(true);
                        mRate.setText("+" + priceLimit);
                    }
                }
                mLastPrice.setText(item.getFormattedLastPrice());
                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Client.getStockInfo(item.getInstrumentId())
                                .setCallback(new Callback2D<Resp<Stock>, Stock>() {
                                    @Override
                                    protected void onRespSuccessData(Stock data) {
                                        Launcher.with(context, StockDetailActivity.class)
                                                .putExtra(Launcher.EX_PAYLOAD, data).execute();
                                    }
                                })
                                .fire();

                    }
                });
            }
        }
    }
}
