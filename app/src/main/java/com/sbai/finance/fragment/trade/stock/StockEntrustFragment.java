package com.sbai.finance.fragment.trade.stock;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.ImageFloder;
import com.sbai.finance.model.stocktrade.Entrust;
import com.sbai.finance.model.stocktrade.Position;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StockCodeUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

/**
 * 股票委托记录页
 */

public class StockEntrustFragment extends BaseFragment {
    @BindView(R.id.lastAndCostPrice)
    TextView mLastAndCostPrice;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.empty)
    TextView mEmpty;
    Unbinder unbinder;
    private EntrustAdapter mEntrustAdapter;
    private int mPage;

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
        initView();
        initRecyclerView();
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
        mEntrustAdapter = new EntrustAdapter(getActivity(), new EntrustAdapter.ItemClickListener() {
            @Override
            public void hideOperateView(int index) {
                hidePriOperateView(index);
            }

            @Override
            public void withdraw(int id) {
                showWithdrawDialog(id);
                // TODO: 2017-11-21
            }
        });
        mRecyclerView.setAdapter(mEntrustAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        requestEntrust();
    }

    private void requestEntrust() {
        Client.requestEntrust(Position.TYPE_SIMULATE, "jf100009", null, mPage)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Entrust>>, List<Entrust>>() {
                    @Override
                    protected void onRespSuccessData(List<Entrust> data) {
                        updateEntrust(data);
                    }
                }).fireFree();
    }

    private void requestWithdraw(int id) {
        Client.requestWithdraw(id)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        requestEntrust();
                    }

                    @Override
                    protected void onRespFailure(Resp failedResp) {
                        ToastUtil.show(failedResp.getMsg());
                    }
                }).fireFree();
    }

    private void updateEntrust(List<Entrust> data) {
        mEntrustAdapter.clear();
        mEntrustAdapter.addAll(data);
    }

    private void showWithdrawDialog(final int id) {
        SmartDialog.single(getActivity())
                .setTitle(R.string.tips)
                .setMessage(getString(R.string.is_confirm_withdraw))
                .setNegative(R.string.cancel)
                .setPositive(R.string.confirm_withdraw, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        requestWithdraw(id);

                    }
                }).show();
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

    public static class EntrustAdapter extends RecyclerView.Adapter<EntrustAdapter.ViewHolder> {
        private List<Entrust> mPositionList;
        private Context mContext;
        private int index = -1;
        private ItemClickListener mItemClickListener;
        private boolean mShowOperateView = true;

        interface ItemClickListener {

            void hideOperateView(int index);

            void withdraw(int id);

        }

        public EntrustAdapter(Context context, boolean showOperateView) {
            this(context, null);
            mShowOperateView = showOperateView;
        }

        EntrustAdapter(Context context, ItemClickListener itemClickListener) {
            mContext = context;
            mPositionList = new ArrayList<>();
            mItemClickListener = itemClickListener;
        }

        public void addAll(List<Entrust> entrusts) {
            mPositionList.addAll(entrusts);
            notifyDataSetChanged();
        }

        public void clear() {
            mPositionList.clear();
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_stock_entrust, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mPositionList.get(position), mItemClickListener, mContext, position);
        }

        @Override
        public int getItemCount() {
            return mPositionList.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.positionArea)
            LinearLayout mPositionArea;
            @BindView(R.id.operateArea)
            RelativeLayout mOperateArea;
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

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(final Entrust entrust, final ItemClickListener itemClickListener,
                                         final Context context, final int position) {
                if (entrust.getDirection() == Entrust.ENTRUST_BS_BUY) {
                    mEntrustBs.setText(context.getString(R.string.buy));
                    mEntrustBs.setTextColor(ContextCompat.getColor(context, R.color.redAssist));
                } else {
                    mEntrustBs.setText(context.getString(R.string.sell));
                    mEntrustBs.setTextColor(ContextCompat.getColor(context, R.color.greenAssist));
                }
                switch (entrust.getMoiety()) {
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
                mStockCode.setText(entrust.getVarietyCode() + "." + StockCodeUtil.getExchangeType(entrust.getVarietyCode()));
                if (mShowOperateView) {
                    mEntrustAmount.setText(String.valueOf(entrust.getQuantity()));
                    mEntrustPrice.setText(FinanceUtil.formatWithScale(entrust.getPrice(), 3));
                } else {
                    mEntrustAmount.setText(String.valueOf(entrust.getSuccQuantity()));
                    mEntrustPrice.setText(FinanceUtil.formatWithScale(entrust.getBargainPrice(), 3));
                }
                mBusinessFund.setText(FinanceUtil.formatWithScale(entrust.getTotalBargain()));
                mBusinessDate.setText(DateUtil.format(entrust.getBargainTime(), "MM/dd"));
                mBusinessTime.setText(DateUtil.format(entrust.getBargainTime(), "HH:mm"));

                mPositionArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!mShowOperateView) return;
                        if (mShowOperateView && entrust.getMoiety() == Entrust.ENTRUST_STATUS_ALL_BUSINESS)
                            return;
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
                mWithdraw.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (itemClickListener != null) {
                            itemClickListener.withdraw(entrust.getId());
                        }
                    }
                });
            }
        }
    }
}
