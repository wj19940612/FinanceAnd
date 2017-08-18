package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.sbai.finance.model.training.question.KData;

import java.util.List;

public class MvKlineView extends RelativeLayout {

    private Kline mKline;
    private OverLayer mOverLayer;

    public MvKlineView(Context context) {
        super(context);
        init();
    }

    public MvKlineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        RelativeLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);

        mKline = new Kline(getContext());
        addView(mKline, params);
    }

    public void setDataList(List<KData> dataList) {
        mKline.setDataList(dataList);
    }
}
