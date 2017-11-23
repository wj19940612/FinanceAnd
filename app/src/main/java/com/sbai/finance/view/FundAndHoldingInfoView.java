package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.stocktrade.PositionRecords;
import com.sbai.finance.utils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 订单页 资金和收益信息
 */

public class FundAndHoldingInfoView extends LinearLayout {
    @BindView(R.id.todayProfit)
    TextView mTodayProfit;
    @BindView(R.id.totalFund)
    TextView mTotalFund;
    @BindView(R.id.totalMarket)
    TextView mTotalMarket;
    @BindView(R.id.holdingFloat)
    TextView mHoldingFloat;
    @BindView(R.id.enableFund)
    TextView mEnableFund;
    @BindView(R.id.fetchFund)
    TextView mFetchFund;
    @BindView(R.id.buy)
    TextView mBuy;
    @BindView(R.id.sell)
    TextView mSell;

    private OnOrderClickListener mOnOrderClickListener;

    public interface OnOrderClickListener {

        void buy();

        void sell();
    }

    public FundAndHoldingInfoView(Context context) {
        this(context, null);
    }

    public FundAndHoldingInfoView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FundAndHoldingInfoView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.layout_fund_holding_info, this, true);
        ButterKnife.bind(this);
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        mOnOrderClickListener = onOrderClickListener;
    }

    public void setAssetData(PositionRecords data) {
        mEnableFund.setText(FinanceUtil.formatWithScale(data.getUsableMoney()));
        mFetchFund.setText(FinanceUtil.formatWithScale(data.getUsableDraw()));
    }

    @OnClick({R.id.buy, R.id.sell})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buy:
                if (mOnOrderClickListener != null) {
                    mOnOrderClickListener.buy();
                }
                break;
            case R.id.sell:
                if (mOnOrderClickListener != null) {
                    mOnOrderClickListener.sell();
                }
                break;
        }
    }
}
