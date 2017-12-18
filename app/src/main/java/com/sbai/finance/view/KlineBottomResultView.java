package com.sbai.finance.view;

import android.content.Context;
import android.os.Binder;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.utils.DateUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2017\12\11 0011.
 */

public class KlineBottomResultView extends LinearLayout {
    @BindView(R.id.stockName)
    TextView mStockName;
    @BindView(R.id.stockId)
    TextView mStockId;
    @BindView(R.id.stockUpDown)
    TextView mStockUpDown;
    @BindView(R.id.stockTimeLine)
    TextView mStockTimeLine;

    private Context mContext;

    public KlineBottomResultView(Context context) {
        this(context, null);
    }

    public KlineBottomResultView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public KlineBottomResultView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        LayoutInflater.from(mContext).inflate(R.layout.view_kline_bottom_result, this, true);
        ButterKnife.bind(this);
    }

    public void updateStockName(String stockName) {
        mStockName.setText(stockName);
    }

    public void updateStockId(String stockId) {
        mStockId.setText(stockId);
    }

    public void updateStockUpDown(double upDown) {
        if (upDown >= 0) {
            mStockUpDown.setTextColor(ContextCompat.getColor(mContext, R.color.redPrimary));
            mStockUpDown.setText("+" + String.format("%.2f", upDown) + "%");
        } else {
            mStockUpDown.setTextColor(ContextCompat.getColor(mContext, R.color.greenAssist));
            mStockUpDown.setText(String.format("%.2f", upDown) + "%");
        }
    }

    public void updateStockLine(long date1, long date2) {
        String time1 = DateUtil.format(date1, DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR);
        String time2 = DateUtil.format(date2, DateUtil.FORMAT_SPECIAL_SLASH_NO_HOUR);
        mStockTimeLine.setText(time1 + "-" + time2);
    }

    public void updateStockLine(String date1, String date2) {
        mStockTimeLine.setText(date1 + "-" + date2);
    }

    public void updateStock(String battleStockName, String battleStockCode, String battleStockStartTime, String battleStockEndTime, double rise) {
        updateStockName(battleStockName);
        updateStockId(battleStockCode);
        updateStockUpDown(rise);
        updateStockLine(battleStockStartTime, battleStockEndTime);
    }
}
