package com.sbai.finance.view.training.Kline;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.sbai.finance.R;
import com.sbai.finance.model.training.question.KData;

import java.util.List;

public class MvKlineView extends RelativeLayout {

    private Kline mKline;
    private OverLayer mOverLayer;
    private LinearLayout mJudgeButtons;

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

        params = new LayoutParams(LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        mOverLayer = new OverLayer(getContext());
        addView(mOverLayer, params);

        params = new LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        mJudgeButtons = (LinearLayout) LayoutInflater.from(getContext()).inflate(R.layout.view_judge_buttons, null);
        params.addRule(ALIGN_PARENT_RIGHT);
        params.addRule(CENTER_VERTICAL);
        addView(mJudgeButtons, params);
    }

    public void setDataList(List<KData> dataList) {
        mKline.setDataList(dataList);
    }
}
