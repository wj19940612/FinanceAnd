package com.sbai.finance.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.fund.VirtualProductExchangeActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.cornucopia.AccountFundDetail;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;

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
    @BindView(R.id.fetchFundDescribe)
    TextView mFetchFundDescribe;
    @BindView(R.id.rechargeScore)
    TextView mRechargeScore;

    private OnOrderClickListener mOnOrderClickListener;
    private Context mContext;

    @OnClick(R.id.rechargeScore)
    public void onViewClicked() {
    }

    public interface OnOrderClickListener {

        void buy();

        void sell();

        void fetchFund();
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
        mContext = context;
    }

    public void setOnOrderClickListener(OnOrderClickListener onOrderClickListener) {
        mOnOrderClickListener = onOrderClickListener;
    }

    public void setTotalFund(double totalFund) {
        mTotalFund.setText(FinanceUtil.formatWithScale(totalFund));
    }

    public void setTotalMarket(double totalMarket) {
        mTotalMarket.setText(FinanceUtil.formatWithScale(totalMarket));
    }

    public void setEnableFund(double enableFund) {
        mEnableFund.setText(FinanceUtil.formatWithScale(enableFund));
    }

    public void setFetchFund(double fetchFund) {
        mFetchFund.setText(FinanceUtil.formatWithScale(fetchFund));
    }

    public void setTodayProfit(double todayProfit) {
        todayProfit = Double.valueOf(FinanceUtil.formatWithScale(todayProfit));
        if (todayProfit == 0) {
            mTodayProfit.setText("0.00");
            mTodayProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        } else if (todayProfit > 0) {
            mTodayProfit.setText("+" + FinanceUtil.formatWithScale(todayProfit));
            mTodayProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            mTodayProfit.setText(FinanceUtil.formatWithScale(todayProfit));
            mTodayProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
    }

    public void setHoldingFloat(double floatProfit) {
        floatProfit = Double.valueOf(FinanceUtil.formatWithScale(floatProfit));
        if (floatProfit == 0) {
            mHoldingFloat.setText("0.00");
            mHoldingFloat.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        } else if (floatProfit > 0) {
            mHoldingFloat.setText("+" + FinanceUtil.formatWithScale(floatProfit));
            mHoldingFloat.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            mHoldingFloat.setText(FinanceUtil.formatWithScale(floatProfit));
            mHoldingFloat.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
    }

    public void resetView() {
        String str = mContext.getString(R.string.no_this_data);
        int whiteColor = ContextCompat.getColor(mContext, android.R.color.white);
        int eightyWhite = ContextCompat.getColor(mContext, R.color.eighty_white);
        mTodayProfit.setText(str);
        mTodayProfit.setTextColor(whiteColor);
        mTotalFund.setText(str);
        mTotalFund.setTextColor(eightyWhite);
        mTotalMarket.setText(str);
        mTotalMarket.setTextColor(eightyWhite);
        mHoldingFloat.setText(str);
        mHoldingFloat.setTextColor(whiteColor);
        mEnableFund.setText(str);
        mEnableFund.setTextColor(eightyWhite);
        mFetchFund.setText(str);
        mFetchFund.setTextColor(eightyWhite);
    }

    @OnClick({R.id.buy, R.id.sell, R.id.fetchFundDescribe, R.id.rechargeScore})
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
            case R.id.fetchFundDescribe:
                if (mOnOrderClickListener != null) {
                    mOnOrderClickListener.fetchFund();
                }
                break;
            case R.id.rechargeScore:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getContext(), VirtualProductExchangeActivity.class)
                            .putExtra(ExtraKeys.RECHARGE_TYPE, AccountFundDetail.TYPE_SCORE)
                            .putExtra(ExtraKeys.USER_FUND, 0)
                            .execute();
                } else {
                    Launcher.with(getContext(), LoginActivity.class).execute();
                }
                break;
        }
    }
}
