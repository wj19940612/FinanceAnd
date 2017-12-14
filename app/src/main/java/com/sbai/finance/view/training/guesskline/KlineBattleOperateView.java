package com.sbai.finance.view.training.guesskline;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.FinanceUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * k线训练操作
 */

public class KlineBattleOperateView extends LinearLayout {
    @BindView(R.id.imgRank)
    ImageView mImgRank;
    @BindView(R.id.imgAvatar)
    ImageView mImgAvatar;
    @BindView(R.id.totalProfit)
    TextView mTotalProfit;
    @BindView(R.id.positionProfit)
    TextView mPositionProfit;
    @BindView(R.id.buy)
    ImageView mBuy;
    @BindView(R.id.clear)
    ImageView mClear;
    @BindView(R.id.remainKAmount)
    TextView mRemainKAmount;
    @BindView(R.id.pass)
    ImageView mPass;
    @BindView(R.id.endPk)
    TextView mEndPk;
    @BindView(R.id.operate)
    RelativeLayout mOperate;
    private Context mContext;
    private OperateListener mOperateListener;

    public interface OperateListener {
        void buy();

        void clear();

        void pass();
    }

    public KlineBattleOperateView(Context context) {
        this(context, null);
    }

    public KlineBattleOperateView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KlineBattleOperateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        LayoutInflater.from(context).inflate(R.layout.view_kline_battle_operate, this, true);
        ButterKnife.bind(this);
        mContext = context;
    }

    public void setOperateListener(OperateListener operateListener) {
        mOperateListener = operateListener;
    }

    public KlineBattleOperateView complete() {
        mEndPk.setVisibility(VISIBLE);
        mOperate.setVisibility(GONE);
        return this;
    }

    public KlineBattleOperateView battling() {
        mEndPk.setVisibility(GONE);
        mOperate.setVisibility(VISIBLE);
        return this;
    }


    public KlineBattleOperateView buySuccess() {
        mBuy.setVisibility(GONE);
        mClear.setVisibility(VISIBLE);
        return this;
    }

    public KlineBattleOperateView clearSuccess() {
        mBuy.setVisibility(VISIBLE);
        mClear.setVisibility(GONE);
        return this;
    }

    public KlineBattleOperateView remainKline(int amount) {
        mRemainKAmount.setText(String.valueOf(amount));
        return this;
    }

    public KlineBattleOperateView totalProfit(double totalProfit) {
        totalProfit = Double.valueOf(FinanceUtil.formatWithScale(totalProfit));
        if (totalProfit == 0) {
            mTotalProfit.setText("0.00");
            mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        } else if (totalProfit > 0) {
            mTotalProfit.setText("+" + FinanceUtil.formatWithScale(totalProfit));
            mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            mTotalProfit.setText(FinanceUtil.formatWithScale(totalProfit));
            mTotalProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
        return this;
    }

    public KlineBattleOperateView positionProfit(double positionProfit) {
        positionProfit = Double.valueOf(FinanceUtil.formatWithScale(positionProfit));
        if (positionProfit == 0) {
            mPositionProfit.setText("0.00");
            mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        } else if (positionProfit > 0) {
            mPositionProfit.setText("+" + FinanceUtil.formatWithScale(positionProfit));
            mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.redPrimary));
        } else {
            mPositionProfit.setText(FinanceUtil.formatWithScale(positionProfit));
            mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.greenAssist));
        }
        return this;
    }

    public KlineBattleOperateView clearPositionProfit() {
        mPositionProfit.setText(R.string.no_this_data);
        mPositionProfit.setTextColor(ContextCompat.getColor(getContext(), R.color.eighty_white));
        return this;
    }

    @OnClick({R.id.buy, R.id.clear, R.id.pass})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.buy:
                if (mOperateListener != null) {
                    mOperateListener.buy();
                }
                break;
            case R.id.clear:
                if (mOperateListener != null) {
                    mOperateListener.clear();
                }
                break;
            case R.id.pass:
                if (mOperateListener != null) {
                    mOperateListener.pass();
                }
                break;
        }
    }
}
