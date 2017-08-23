package com.sbai.finance.view.training;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.sbai.finance.R;
import com.sbai.finance.model.training.RemoveTraining;
import com.sbai.finance.utils.Display;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * K线训练页面菱形组合布局
 */

public class KlineTrainView extends RelativeLayout {

    private DiamondGroupView[] views;
    private int mPriSelectedIndex;
    private OnEndCallback mOnEndCallback;
    private Map<Integer, DiamondGroupView> mMatchMap;

    public interface OnEndCallback {
        void onAllEnd();

        void onMatchEnd();
    }

    public void setOnEndCallback(OnEndCallback onEndCallback) {
        mOnEndCallback = onEndCallback;
    }

    public void setTrainData(List<RemoveTraining> data) {
        if (data == null) return;
        Collections.shuffle(data);
        refresh(data);
    }

    private void refresh(List<RemoveTraining> trainData) {
        mMatchMap.clear();
        mPriSelectedIndex = -1;
        for (int i = 0; i < trainData.size(); i++) {
            RemoveTraining training = trainData.get(i);
            if (training == null) return;
            views[i].setTag(training);
            if (views[i].getVisibility() == INVISIBLE) {
                views[i].resetView();
                views[i].setVisibility(VISIBLE);
            }
            views[i].setSelected(false);
            if (training.isImage()) {
                views[i].setBackgroundType(DiamondView.TYPE_DARK)
                        .setImageVisible(true)
                        .setImageUrl(training.getImageUrl())
                        .setDescribeVisible(false);
            } else {
                views[i].setBackgroundType(DiamondView.TYPE_WHITE)
                        .setImageVisible(false)
                        .setDescribe(String.valueOf(training.getContent()))
                        .setDescribeVisible(true);
            }
        }
        for (int i = trainData.size(); i < views.length; i++) {
            views[i].setVisibility(INVISIBLE);
        }
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
        mMatchMap = new HashMap<>();
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

        for (int i = 0; i < views.length; i++) {
            setOnClickListener(views[i], i);
            setOnClickListener(views[i].getDescribe(), i);
            setOnClickListener(views[i].getKlineImg(), i);
        }

        for (int i = 0; i < views.length; i++) {
            setOnDisappearListener(views[i]);
        }
        for (int i = 0; i < views.length; i++) {
            setOnSelectedDrawFinishListener(views[i], i);
        }
    }

    private void setOnSelectedDrawFinishListener(final DiamondGroupView view, final int index) {
        view.setOnSelectDrawFinishCallback(new DiamondGroupView.OnSelectDrawFinishCallback() {
            @Override
            public void onFinishDraw() {
                if (mMatchMap.get(index) != null) {
                    view.startDisappearAnim();
                    mMatchMap.get(index).startDisappearAnim();
                }
            }
        });
    }

    private void setOnDisappearListener(DiamondGroupView view) {
        view.setOnClearCallback(new DiamondGroupView.OnClearCallback() {
            @Override
            public void onClear() {
                updateIsEnd();
            }
        });
    }

    private void setOnClickListener(View view, final int index) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!views[index].getSelected()) {
                    views[index].setSelected(true);
                    if (mPriSelectedIndex == -1) {
                        mPriSelectedIndex = index;
                    } else {
                        RemoveTraining training1 = (RemoveTraining) views[mPriSelectedIndex].getTag();
                        RemoveTraining training2 = (RemoveTraining) views[index].getTag();
                        if (training1 != null && training2 != null) {
                            if (training1.getSeq() == training2.getSeq()) {
                                mMatchMap.put(index, views[mPriSelectedIndex]);
                                if (mOnEndCallback != null) {
                                    mOnEndCallback.onMatchEnd();
                                }
                            } else {
                                views[mPriSelectedIndex].startErrorAnim();
                                views[mPriSelectedIndex].setSelected(false);
                                views[index].startErrorAnim();
                                views[index].setSelected(false);
                            }
                            mPriSelectedIndex = -1;
                        }
                    }
                }
            }
        });
    }

    private void updateIsEnd() {
        boolean end = true;
        for (int i = 0; i < views.length; i++) {
            if (views[i].getVisibility() == VISIBLE) {
                end = false;
                break;
            }
        }
        if (end && mOnEndCallback != null) {
            mOnEndCallback.onAllEnd();
        }
    }

    public void startAppearAnim() {
        for (int i = 0; i < views.length; i++) {
            views[i].startAppearAnim();
        }
    }
}
