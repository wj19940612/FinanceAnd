package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.mine.TaskProgressView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by Administrator on 2018\1\2 0002.
 */

public class TaskCenterActivity extends BaseActivity {

    @BindView(R.id.backImg)
    ImageView mBackImg;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.getIntegrationTip)
    TextView mGetIntegrationTip;
    @BindView(R.id.getIntegration)
    TextView mGetIntegration;
    @BindView(R.id.taskProgress)
    TaskProgressView mTaskProgress;
    @BindView(R.id.newTaskLayout)
    LinearLayout mNewTaskLayout;
    @BindView(R.id.normalTaskLayout)
    LinearLayout mNormalTaskLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_center);
        ButterKnife.bind(this);
        initView();
    }

    private void initView(){
        mTaskProgress.setProgress(3);
    }
}
