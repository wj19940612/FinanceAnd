package com.sbai.finance.activity.mine;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.Task;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ReceiveIntegrationDialog;
import com.sbai.finance.view.mine.IntegrationTaskView;
import com.sbai.finance.view.mine.TaskProgressView;

import java.util.List;

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
    @BindView(R.id.evaluationTask)
    IntegrationTaskView mEvaluationTask;
    @BindView(R.id.certificationTask)
    IntegrationTaskView mCertificationTask;
    @BindView(R.id.futureTask)
    IntegrationTaskView mFutureTask;
    @BindView(R.id.klineTask)
    IntegrationTaskView mKlineTask;
    @BindView(R.id.radioTask)
    IntegrationTaskView mRadioTask;
    @BindView(R.id.guessTask)
    IntegrationTaskView mGuessTask;
    @BindView(R.id.questionTask)
    IntegrationTaskView mQuestionTask;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_center);
        ButterKnife.bind(this);
        initView();
        requestListData();
    }

    @Override
    public void onTimeUp(int count) {
        SmartDialog.dismiss(this);
        stopScheduleJob();
    }

    private void initView() {
        mTaskProgress.setProgress(3);
        mTaskProgress.flashFirstIcon();
        mTaskProgress.setOnOpenAwardListener(new TaskProgressView.OnOpenAwardListener() {
            @Override
            public void onOpenAward() {
                mTaskProgress.openFirstIcon();
            }
        });
    }

    private void requestListData() {
        Client.requestTaskData().setTag(TAG).setCallback(new Callback2D<Resp<List<Task>>, List<Task>>() {
            @Override
            protected void onRespSuccessData(List<Task> data) {
                updateData(data);
            }
        }).fireFree();
    }

    private void updateData(List<Task> data) {
        for (Task task : data) {
            if (task.getTaskType() == 0) {
                mNewTaskLayout.setVisibility(View.VISIBLE);
            } else if (task.getTaskType() == 1) {
                mNormalTaskLayout.setVisibility(View.VISIBLE);
            }

            if(task.getJumpContent().equals(Task.RULE_FUTURE_BATTLE)){
                //期货对战
                mFutureTask.setVisibility(View.VISIBLE);
            }
        }

    }

    private void startDialog(int integration){
        ReceiveIntegrationDialog.get(this, new ReceiveIntegrationDialog.OnCallback() {
            @Override
            public void onDialogClick() {

            }
        },integration);
        startScheduleJob(2000);
    }
}
