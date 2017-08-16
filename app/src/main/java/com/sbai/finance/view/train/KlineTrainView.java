package com.sbai.finance.view.train;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sbai.finance.model.training.Train;
import com.sbai.finance.utils.Display;

import java.util.List;

/**
 * K线训练页面菱形组合布局
 */

public class KlineTrainView extends RelativeLayout {
    private List<Train> mTrainData;
    private DiamondGroupView[] views;

    public void setTrainData(List<Train> data) {
        mTrainData = data;
        refresh();
    }

    private void refresh() {

    }

    public KlineTrainView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView();
    }

    public KlineTrainView(Context context) {
        super(context);
        initView();
    }

    private void initView() {
        views = new DiamondGroupView[6];
        views[0] = new DiamondGroupView(getContext());
        views[0].setId(View.generateViewId());
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_TOP);
        views[0].setLayoutParams(params);
        addView(views[0], params);

        views[1] = new DiamondGroupView(getContext());
        views[1].setId(View.generateViewId());
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_TOP);
        params.addRule(RIGHT_OF, views[0].getId());
        params.setMargins((int) Display.dp2Px(12, getResources()), 0, 0, 0);
        views[1].setLayoutParams(params);
        addView(views[1], params);

        views[2] = new DiamondGroupView(getContext());
        views[2].setId(View.generateViewId());
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL);
        params.setMargins((int) Display.dp2Px(57.5f, getResources()), 0, 0, 0);
        views[2].setLayoutParams(params);
        addView(views[2], params);

        views[3] = new DiamondGroupView(getContext());
        views[3].setId(View.generateViewId());
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(CENTER_VERTICAL);
        params.addRule(RIGHT_OF, views[2].getId());
        params.setMargins((int) Display.dp2Px(12, getResources()), 0, 0, 0);
        views[3].setLayoutParams(params);
        addView(views[3], params);

        views[4] = new DiamondGroupView(getContext());
        views[4].setId(View.generateViewId());
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        views[4].setLayoutParams(params);
        addView(views[4], params);

        views[5] = new DiamondGroupView(getContext());
        views[5].setId(View.generateViewId());
        params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.addRule(ALIGN_PARENT_BOTTOM);
        params.addRule(RIGHT_OF, views[4].getId());
        params.setMargins((int) Display.dp2Px(12, getResources()), 0, 0, 0);
        views[5].setLayoutParams(params);
        addView(views[5], params);
        views[5].setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                views[1].startAnim();
                views[0].startAnim();
            }
        });

    }
}
