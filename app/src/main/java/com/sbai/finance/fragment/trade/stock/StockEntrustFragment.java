package com.sbai.finance.fragment.trade.stock;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.stock.StockUser;
import com.sbai.finance.model.stocktrade.Entrust;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AnimUtils;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.activity.trade.trade.StockOrderActivity.ACTION_REFRESH_AUTO;
import static com.sbai.finance.activity.trade.trade.StockOrderActivity.ACTION_REFRESH_MANUAL;

/**
 * 股票委托记录页
 */

public class StockEntrustFragment extends BaseFragment {
    @BindView(R.id.lastAndCostPrice)
    TextView mLastAndCostPrice;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.empty)
    NestedScrollView mEmpty;
    Unbinder unbinder;
    private EntrustAdapter mEntrustAdapter;

    private StockUser mStockUser;
    private BroadcastReceiver mReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH_MANUAL)) {
                mStockUser = LocalUser.getUser().getStockUser();
                requestEntrust(true);
            }
            if (intent.getAction().equalsIgnoreCase(ACTION_REFRESH_AUTO)) {
                requestEntrust(false);
            }
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_stock_entrust, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initRecyclerView();
        initBroadcastReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestEntrust(true);
        startScheduleJob(5 * 1000);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            startScheduleJob(5 * 1000);
        } else {
            stopScheduleJob();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        requestEntrust(false);
    }

    private void initBroadcastReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(ACTION_REFRESH_MANUAL);
        intentFilter.addAction(ACTION_REFRESH_AUTO);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mReceiver, intentFilter);
    }

    private void initRecyclerView() {
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEntrustAdapter = new EntrustAdapter(getActivity(), new EntrustAdapter.ItemClickListener() {
            @Override
            public void hideOperateView(int index) {
                hidePriOperateView(index);
            }

            @Override
            public void withdraw(int id, String varietyCode) {
                showWithdrawDialog(id, varietyCode);
            }
        });
        mRecyclerView.setAdapter(mEntrustAdapter);
    }

    private void requestEntrust(final boolean manualRefresh) {
        if (mStockUser == null) return;
        Client.requestEntrust(mStockUser.getType(), mStockUser.getAccount(), mStockUser.getActivityCode(), 0)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Entrust>>, List<Entrust>>() {
                    @Override
                    protected void onRespSuccessData(List<Entrust> data) {
                        updateEntrust(data, manualRefresh);
                    }
                }).fireFree();
    }

    private void requestWithdraw(int id, String varietyCode) {
        Client.requestWithdraw(id, mStockUser.getAccount(), varietyCode)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        notifyRefreshData();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        ToastUtil.show(failedResp.getMsg());
                        notifyRefreshData();
                    }
                }).fireFree();
    }

    private void notifyRefreshData() {
        LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(new Intent(ACTION_REFRESH_AUTO));
    }

    private void updateEntrust(List<Entrust> data, boolean manualRefresh) {
        mEntrustAdapter.setManualRefresh(manualRefresh);
        mEntrustAdapter.clear();
        mEntrustAdapter.addAll(data);
        if (data == null || data.isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.VISIBLE);
        }
    }

    private void showWithdrawDialog(final int id, final String varietyCode) {
        SmartDialog.single(getActivity())
                .setTitle(R.string.tips)
                .setMessage(getString(R.string.is_confirm_withdraw))
                .setNegative(R.string.cancel)
                .setPositive(R.string.confirm_withdraw, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestWithdraw(id, varietyCode);

                    }
                }).show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mReceiver);
    }

    private void hidePriOperateView(int index) {
        if (index < 0 && mRecyclerView.getChildCount() - 1 < index) return;
        View viewGroup = mRecyclerView.getLayoutManager().findViewByPosition(index);
        if (viewGroup != null) {
            View view = viewGroup.findViewById(R.id.operateArea);
            if (view != null) {
                view.setVisibility(View.GONE);
            }
        }
    }

    public static class EntrustAdapter extends RecyclerView.Adapter<EntrustAdapter.ViewHolder> {
        private List<Entrust> mEntrustList;
        private Context mContext;
        private int index = -1;
        private ItemClickListener mItemClickListener;
        private boolean mShowOperateView = true;
        private boolean mManualRefresh;

        interface ItemClickListener {

            void hideOperateView(int index);

            void withdraw(int id, String varietyCode);

        }

        public EntrustAdapter(Context context, boolean showOperateView) {
            this(context, null);
            mShowOperateView = showOperateView;
        }

        EntrustAdapter(Context context, ItemClickListener itemClickListener) {
            mContext = context;
            mEntrustList = new ArrayList<>();
            mItemClickListener = itemClickListener;
        }

        public void addAll(List<Entrust> entrusts) {
            mEntrustList.addAll(entrusts);
            notifyDataSetChanged();
        }

        public void clear() {
            mEntrustList.clear();
            notifyDataSetChanged();
        }

        public boolean isEmpty() {
            return mEntrustList.isEmpty();
        }

        public void setManualRefresh(boolean manualRefresh) {
            this.mManualRefresh = manualRefresh;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_entrust, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            if (position < mEntrustList.size()) {
                holder.bindDataWithView(mEntrustList.get(position), mItemClickListener, mContext, position);
            } else {
                holder.bindDataWithView(null, mItemClickListener, mContext, position);
            }


        }

        @Override
        public int getItemCount() {
            return mEntrustList.size() + 1;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.positionArea)
            LinearLayout mPositionArea;
            @BindView(R.id.operateArea)
            LinearLayout mOperateArea;
            @BindView(R.id.entrustBs)
            TextView mEntrustBs;
            @BindView(R.id.entrustStatus)
            TextView mEntrustStatus;
            @BindView(R.id.stockName)
            TextView mStockName;
            @BindView(R.id.stockCode)
            TextView mStockCode;
            @BindView(R.id.entrustAmount)
            TextView mEntrustAmount;
            @BindView(R.id.entrustPrice)
            TextView mEntrustPrice;
            @BindView(R.id.businessFund)
            TextView mBusinessFund;
            @BindView(R.id.businessDate)
            TextView mBusinessDate;
            @BindView(R.id.businessTime)
            TextView mBusinessTime;
            @BindView(R.id.withdraw)
            TextView mWithdraw;
            @BindView(R.id.split)
            View mSplit;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(final Entrust entrust, final ItemClickListener itemClickListener,
                                         final Context context, final int position) {
                if (position == mEntrustList.size()) {
                    resetView();
                    return;
                }
                mSplit.setVisibility(View.VISIBLE);
                if (entrust.getDirection() == Entrust.ENTRUST_BS_BUY) {
                    mEntrustBs.setText(context.getString(R.string.buy));
                    mEntrustBs.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                } else {
                    mEntrustBs.setText(context.getString(R.string.sell));
                    mEntrustBs.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                }
                switch (entrust.getMoiety()) {
                    case Entrust.ENTRUST_STATUS_WAIT_WITHDRAW:
                        mEntrustStatus.setVisibility(View.VISIBLE);
                        mEntrustStatus.setText(context.getString(R.string.wait_withdraw));
                        break;
                    case Entrust.ENTRUST_STATUS_NO_BUSINESS:
                        mEntrustStatus.setVisibility(View.VISIBLE);
                        mEntrustStatus.setText(context.getString(R.string.wait_business));
                        break;
                    case Entrust.ENTRUST_STATUS_PART_BUSINESS:
                        if (mShowOperateView) {
                            mEntrustStatus.setVisibility(View.VISIBLE);
                            mEntrustStatus.setText(context.getString(R.string.part_business));
                        } else {
                            mEntrustStatus.setVisibility(View.GONE);
                        }
                        break;
                    case Entrust.ENTRUST_STATUS_ALL_BUSINESS:
                        mEntrustStatus.setVisibility(View.GONE);
                        break;
                }
                mStockName.setText(entrust.getVarietyName());
                mStockCode.setText(entrust.getVarietyCode());
                if (mShowOperateView) {
                    mEntrustAmount.setText(FinanceUtil.formatWithThousandsSeparator(entrust.getQuantity(), 0));
                    mEntrustPrice.setText(FinanceUtil.formatWithScale(entrust.getPrice(), 3));
                } else {
                    mEntrustAmount.setText(FinanceUtil.formatWithThousandsSeparator(entrust.getSuccQuantity(), 0));
                    mEntrustPrice.setText(FinanceUtil.formatWithScale(entrust.getBargainPrice(), 3));
                }
                mBusinessFund.setText(FinanceUtil.formatWithThousandsSeparator(entrust.getTotalBargain(), 2));
                if (mShowOperateView) {
                    mBusinessDate.setText(DateUtil.format(entrust.getOrderTime(), "MM/dd"));
                    mBusinessTime.setText(DateUtil.format(entrust.getOrderTime(), "HH:mm"));
                } else {
                    mBusinessDate.setText(DateUtil.format(entrust.getBargainTime(), "MM/dd"));
                    mBusinessTime.setText(DateUtil.format(entrust.getBargainTime(), "HH:mm"));
                }
                if (mManualRefresh) {
                    index = -1;
                }
                if (index > -1 && mEntrustList.size() > index && mEntrustList.get(index) != null) {
                    if (!mManualRefresh && position == index && mEntrustList.get(index).getId() == entrust.getId()) {
                        mOperateArea.setVisibility(View.VISIBLE);
                    } else {
                        mOperateArea.setVisibility(View.GONE);
                    }
                } else {
                    mOperateArea.setVisibility(View.GONE);
                }
                mPositionArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mShowOperateView) return;
                        if (mShowOperateView && (entrust.getMoiety() == Entrust.ENTRUST_STATUS_ALL_BUSINESS || entrust.getMoiety() == Entrust.ENTRUST_STATUS_WAIT_WITHDRAW))
                            return;
                        if (mOperateArea.getVisibility() == View.VISIBLE) {
                            mOperateArea.startAnimation(AnimUtils.createCollapseY(mOperateArea, 100));
                            index = -1;
                        } else {
                            if (itemClickListener != null && index > -1) {
                                itemClickListener.hideOperateView(index);
                            }
                            index = position;
                            mOperateArea.startAnimation(AnimUtils.createExpendY(mOperateArea, 100));
                        }
                    }
                });
                mWithdraw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemClickListener != null) {
                            itemClickListener.withdraw(entrust.getId(), entrust.getVarietyCode());
                        }
                    }
                });
            }

            private void resetView() {
                mSplit.setVisibility(View.GONE);
                mPositionArea.setOnClickListener(null);
                mOperateArea.setVisibility(View.GONE);
                mEntrustBs.setText(null);
                mEntrustStatus.setText(null);
                mStockName.setText(null);
                mStockCode.setText(null);
                mEntrustAmount.setText(null);
                mEntrustPrice.setText(null);
                mBusinessFund.setText(null);
                mBusinessDate.setText(null);
                mBusinessTime.setText(null);
            }
        }

    }
}
