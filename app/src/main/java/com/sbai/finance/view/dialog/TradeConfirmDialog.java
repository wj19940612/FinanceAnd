package com.sbai.finance.view.dialog;

import android.app.Activity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.model.local.StockOrder;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.view.SmartDialog;

/**
 * Modified by john on 27/11/2017
 * <p>
 * Description: 股票交易（买入，卖出）确认弹框
 * <p>
 * APIs:
 */
public class TradeConfirmDialog {

    private Activity mActivity;
    private SmartDialog mSmartDialog;
    private View mView;

    private StockOrder mStockOrder;

    private TextView mTitle;
    private TextView mDirection;
    private TextView mStock;
    private TextView mPrice;
    private TextView mVolume;
    private TextView mFee;
    private TextView mTotalValue;
    private TextView mCancel;
    private TextView mConfirm;

    private OnConfirmClickListener mOnConfirmClickListener;

    public interface OnConfirmClickListener {
        void onConfirmClick(SmartDialog dialog);
    }

    public static TradeConfirmDialog with(Activity activity, StockOrder stockOrder) {
        TradeConfirmDialog confirmDialog = new TradeConfirmDialog();
        confirmDialog.mActivity = activity;
        confirmDialog.mSmartDialog = SmartDialog.single(activity);
        confirmDialog.mView = LayoutInflater.from(activity).inflate(R.layout.dialog_trade_confirm, null);
        confirmDialog.mSmartDialog.setCustomView(confirmDialog.mView);
        confirmDialog.mStockOrder = stockOrder;
        confirmDialog.init();
        return confirmDialog;
    }

    public TradeConfirmDialog setOnConfirmClickListener(OnConfirmClickListener onConfirmClickListener) {
        mOnConfirmClickListener = onConfirmClickListener;
        return this;
    }

    private void init() {
        mTitle = (TextView) mView.findViewById(R.id.title);
        mDirection = (TextView) mView.findViewById(R.id.direction);
        mStock = (TextView) mView.findViewById(R.id.stock);
        mPrice = (TextView) mView.findViewById(R.id.price);
        mVolume = (TextView) mView.findViewById(R.id.volume);
        mFee = (TextView) mView.findViewById(R.id.fee);
        mTotalValue = (TextView) mView.findViewById(R.id.totalValue);
        mCancel = (TextView) mView.findViewById(R.id.cancel);
        mConfirm = (TextView) mView.findViewById(R.id.confirm);

        int color = ContextCompat.getColor(mActivity, R.color.redPrimary);
        String yuan = mActivity.getString(R.string.yuan);
        if (mStockOrder.getDeputeType() == StockOrder.DEPUTE_TYPE_ENTRUST_BUY) {
            mTitle.setText(R.string.entrust_buy_confirm);
            mDirection.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.direction_x),
                    mActivity.getString(R.string.buy_in), color));
            mStock.setText(mActivity.getString(R.string.stock_x)
                    + mStockOrder.getVarietyName() + "(" + mStockOrder.getVarietyCode() + ")");
            mPrice.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.price_x),
                    String.valueOf(mStockOrder.getPrice()), color));
            mVolume.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.volume_x),
                    FinanceUtil.formatWithScale(mStockOrder.getQuantity(), 0), color));
            mFee.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.fee_x),
                    FinanceUtil.formatWithScale(mStockOrder.getFee()), color));
            mTotalValue.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.total_value_x),
                    FinanceUtil.formatWithThousandsSeparator(mStockOrder.getValue()) + yuan, color));
            mConfirm.setTextColor(color);
        } else {
            color = ContextCompat.getColor(mActivity, R.color.greenAssist);
            mTitle.setText(R.string.entrust_sell_confirm);
            mDirection.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.direction_x),
                    mActivity.getString(R.string.sell_out), color));
            mStock.setText(mActivity.getString(R.string.stock_x)
                    + mStockOrder.getVarietyName() + "(" + mStockOrder.getVarietyCode() + ")");
            mPrice.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.price_x),
                    String.valueOf(mStockOrder.getPrice()), color));
            mVolume.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.volume_x),
                    FinanceUtil.formatWithScale(mStockOrder.getQuantity(), 0), color));
            mFee.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.fee_x),
                    FinanceUtil.formatWithScale(mStockOrder.getFee()), color));
            mTotalValue.setText(StrUtil.mergeTextWithColor(mActivity.getString(R.string.total_value_x),
                    FinanceUtil.formatWithThousandsSeparator(mStockOrder.getValue()) + yuan, color));
            mConfirm.setTextColor(color);
        }
        mCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mSmartDialog.dismiss();
            }
        });
        mConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onConfirmClick(mSmartDialog);
                }
            }
        });
    }


    public void show() {
        mSmartDialog.setWidthScale(0.74f)
                .show();
    }
}
