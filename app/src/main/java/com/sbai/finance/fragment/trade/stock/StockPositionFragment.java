package com.sbai.finance.fragment.trade.stock;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.trade.trade.StockOrderActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.stock.StockData;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.model.stocktrade.PositionRecords;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 股票持仓
 */

public class StockPositionFragment extends BaseFragment {
    @BindView(R.id.lastAndCostPrice)
    TextView mLastAndCostPrice;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.empty)
    TextView mEmpty;
    Unbinder unbinder;
    @BindView(R.id.stockPrompt)
    ImageView mStockPrompt;
    private PositionAdapter mPositionAdapter;
    private Map<String, Position> mPositionMap;
    private PositionRecords mPositionRecords;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_position, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mPositionMap = new HashMap<>();
        initView();
        initRecyclerView();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestAsset();
        startScheduleJob(5 * 1000);
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestStockMarketData();
    }

    private void requestAsset() {
        Client.requestAsset(Position.TYPE_SIMULATE_ACTIVITY, "MA100213", "test")
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<PositionRecords>, PositionRecords>() {
                    @Override
                    protected void onRespSuccessData(PositionRecords data) {
                        updateAssetAndPosition(data);
                    }
                }).fireFree();
    }

    private void updateAssetAndPosition(PositionRecords data) {
        mPositionMap.clear();
        mPositionAdapter.clear();
        mPositionAdapter.addAll(data.getList());
        for (Position position : data.getList()) {
            mPositionMap.put(position.getVarietyCode(), position);
        }
        if (getActivity() instanceof StockOrderActivity) {
            StockOrderActivity activity = (StockOrderActivity) getActivity();
            activity.updateEnableAndFetchFund(data);
        }
        requestStockMarketData();
    }

    private void requestStockMarketData() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<String, Position> entry : mPositionMap.entrySet()) {
            sb.append(entry.getKey()).append(",");
        }
        if (sb.length() > 0) {
            sb.deleteCharAt(sb.length() - 1);
            Client.getStockMarketData(sb.toString())
                    .setCallback(new Callback2D<Resp<List<StockData>>, List<StockData>>() {
                        @Override
                        protected void onRespSuccessData(List<StockData> result) {
                            mPositionAdapter.addStockData(result);
                            if (getActivity() instanceof StockOrderActivity) {
                                StockOrderActivity activity = (StockOrderActivity) getActivity();
                                activity.updateAssetAndPosition(result, mPositionMap);
                            }
                        }
                    }).fireFree();
        }
    }

    private void initView() {
        mLastAndCostPrice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017-11-21  
            }
        });
    }

    private void initRecyclerView() {
        mRecyclerView.setEmptyView(mEmpty);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mPositionAdapter = new PositionAdapter(getActivity(), new PositionAdapter.ItemClickListener() {
            @Override
            public void hideOperateView(int index) {
                hidePriOperateView(index);
            }

            @Override
            public void buy(Position position) {
                // TODO: 2017-11-21
            }

            @Override
            public void sell(Position position) {
                // TODO: 2017-11-21
            }

            @Override
            public void detail(Position position) {
                // TODO: 2017-11-21
            }
        });
        mRecyclerView.setAdapter(mPositionAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    private void hidePriOperateView(int index) {
        if (index < 0 && mRecyclerView.getChildCount() - 1 < index) return;
        View viewGroup = mRecyclerView.getChildAt(index);
        if (viewGroup != null) {
            View view = viewGroup.findViewById(R.id.operateArea);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    @OnClick(R.id.stockPrompt)
    public void onViewClicked() {
        showTipDialog();
    }

    private void showTipDialog() {
        SmartDialog.single(getActivity())
                .setTitle(R.string.tips)
                .setMessage(getString(R.string.cost_price_describe))
                .setNegative(R.string.know)
                .setPositiveVisable(View.GONE)
                .show();

    }

    static class PositionAdapter extends RecyclerView.Adapter<PositionAdapter.ViewHolder> {
        private List<Position> mPositionList;
        private Map<String, String> mLastPriceMap;
        private Context mContext;
        private int index = -1;
        private ItemClickListener mItemClickListener;

        interface ItemClickListener {

            void hideOperateView(int index);

            void buy(Position position);

            void sell(Position position);

            void detail(Position position);
        }

        PositionAdapter(Context context) {
            this(context, null);
        }

        PositionAdapter(Context context, ItemClickListener itemClickListener) {
            mContext = context;
            mPositionList = new ArrayList<>();
            mLastPriceMap = new HashMap<>();
            mItemClickListener = itemClickListener;
        }

        public void addAll(List<Position> broadcasts) {
            mPositionList.addAll(broadcasts);
            notifyDataSetChanged();
        }

        public void clear() {
            mPositionList.clear();
            notifyDataSetChanged();
        }

        public void addStockData(List<StockData> stockDataList) {
            for (StockData data : stockDataList) {
                mLastPriceMap.put(data.getInstrumentId(), data.getLastPrice());
            }
            notifyDataSetChanged();
        }

        public void addStockData(StockData stockData) {
            mLastPriceMap.put(stockData.getInstrumentId(), stockData.getLastPrice());
            notifyDataSetChanged();
        }


        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_position, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mPositionList.get(position), mLastPriceMap, mItemClickListener, mContext, position);
        }

        @Override
        public int getItemCount() {
            return mPositionList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.positionArea)
            LinearLayout mPositionArea;
            @BindView(R.id.operateArea)
            LinearLayout mOperateArea;
            @BindView(R.id.stockName)
            TextView mStockName;
            @BindView(R.id.positionValue)
            TextView mPositionValue;
            @BindView(R.id.positionAmount)
            TextView mPositionAmount;
            @BindView(R.id.enableAmount)
            TextView mEnableAmount;
            @BindView(R.id.lastPrice)
            TextView mLastPrice;
            @BindView(R.id.costPrice)
            TextView mCostPrice;
            @BindView(R.id.floatValue)
            TextView mFloatValue;
            @BindView(R.id.floatRate)
            TextView mFloatRate;
            @BindView(R.id.buy)
            TextView mBuy;
            @BindView(R.id.sell)
            TextView mSell;
            @BindView(R.id.positionDetail)
            TextView mPositionDetail;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(final Position stockPosition, Map<String, String> lastPriceMap,
                                         final ItemClickListener itemClickListener, final Context context, final int position) {
                int color = ContextCompat.getColor(context, R.color.blackPrimary);
                String lastPriceStr = lastPriceMap.get(stockPosition.getVarietyCode());
                if (!TextUtils.isEmpty(lastPriceStr)) {
                    double costPrice = stockPosition.getAvgBuyPrice();
                    double lastPrice = Double.valueOf(lastPriceStr);
                    if (costPrice > lastPrice) {
                        color = ContextCompat.getColor(context, R.color.greenAssist);
                    } else if (costPrice < lastPrice) {
                        color = ContextCompat.getColor(context, R.color.redAssist);
                    }
                }
                mStockName.setText(stockPosition.getVarietyName());
                mStockName.setTextColor(color);
                mPositionValue.setText(stockPosition.getMarket());
                mPositionValue.setTextColor(color);
                mPositionAmount.setText(String.valueOf(stockPosition.getTotalQty()));
                mPositionAmount.setTextColor(color);
                mEnableAmount.setText(String.valueOf(stockPosition.getUsableQty()));
                mEnableAmount.setTextColor(color);
                mFloatRate.setTextColor(color);
                mFloatValue.setTextColor(color);
                if (TextUtils.isEmpty(lastPriceStr)) {
                    mLastPrice.setText("-.-");
                    mFloatValue.setText("0.00");
                    mFloatRate.setText("0%");
                } else {
                    mLastPrice.setText(lastPriceStr);
                    double difference = Double.valueOf(lastPriceStr) - stockPosition.getAvgBuyPrice();
                    mFloatValue.setText(FinanceUtil.formatWithScale(difference * stockPosition.getTotalQty()));
                    if (difference > 0) {
                        mFloatRate.setText("+" + FinanceUtil.formatToPercentage(difference / stockPosition.getAvgBuyPrice()));
                    } else if (difference < 0) {
                        mFloatRate.setText(FinanceUtil.formatToPercentage(difference / stockPosition.getAvgBuyPrice()));
                    } else {
                        mFloatRate.setText("0%");
                    }
                }
                mLastPrice.setTextColor(color);
                mCostPrice.setText(String.valueOf(stockPosition.getAvgBuyPrice()));
                mCostPrice.setTextColor(color);
                mOperateArea.setVisibility(View.GONE);
                if (position == index) {
                    mOperateArea.setVisibility(View.VISIBLE);
                }
                mPositionArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOperateArea.getVisibility() == View.VISIBLE) {
                            mOperateArea.setVisibility(View.GONE);
                            index = -1;
                        } else {
                            if (itemClickListener != null && index > -1) {
                                itemClickListener.hideOperateView(index);
                            }
                            index = position;
                            mOperateArea.setVisibility(View.VISIBLE);
                        }
                    }
                });
                mBuy.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideOperateView();
                        if (itemClickListener != null) {
                            itemClickListener.buy(stockPosition);
                        }
                    }
                });
                mSell.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideOperateView();
                        if (itemClickListener != null) {
                            itemClickListener.sell(stockPosition);
                        }
                    }
                });
                mPositionDetail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        hideOperateView();
                        if (itemClickListener != null) {
                            itemClickListener.detail(stockPosition);
                        }
                    }
                });
            }

            private void hideOperateView() {
                mOperateArea.setVisibility(View.GONE);
                index = -1;
            }
        }
    }
}
