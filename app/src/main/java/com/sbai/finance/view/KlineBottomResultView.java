package com.sbai.finance.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;

import butterknife.BindView;

/**
 * Created by Administrator on 2017\12\11 0011.
 */

public class KlineBottomResultView extends RelativeLayout {
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
    }
}
