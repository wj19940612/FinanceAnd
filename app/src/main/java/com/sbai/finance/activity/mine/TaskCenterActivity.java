package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.mine.Task;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.view.NoScrollListView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ReceiveIntegrationDialog;
import com.sbai.finance.view.mine.IntegrationTaskView;
import com.sbai.finance.view.mine.TaskProgressView;

import java.util.ArrayList;
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
    @BindView(R.id.newManList)
    NoScrollListView mNewManListView;
    @BindView(R.id.normalList)
    NoScrollListView mNormalListView;

    private TaskAdapter mNewManAdapter;
    private TaskAdapter mNormalAdapter;


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

        mNewManAdapter = new TaskAdapter(this);
        mNormalAdapter = new TaskAdapter(this);
        mNewManAdapter.setOnBtnClickListener(new TaskAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(Task task) {
                btnClick(task);
            }
        });
        mNormalAdapter.setOnBtnClickListener(new TaskAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(Task task) {
                btnClick(task);
            }
        });
        mNewManListView.setAdapter(mNewManAdapter);
        mNormalListView.setAdapter(mNormalAdapter);
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
        List<Task> newManList = new ArrayList<>();
        List<Task> normalList = new ArrayList<>();
        for (Task task : data) {
            if (task.getTaskType() == 0) {
                newManList.add(task);
            } else if (task.getTaskType() == 1) {
                normalList.add(task);
            }

            if (newManList.size() == 0) {
                mNewTaskLayout.setVisibility(View.GONE);
            } else {
                mNewTaskLayout.setVisibility(View.VISIBLE);
                mNewManAdapter.addAll(newManList);
            }
            if (normalList.size() == 0) {
                mNormalTaskLayout.setVisibility(View.GONE);
            } else {
                mNormalTaskLayout.setVisibility(View.VISIBLE);
                mNormalAdapter.addAll(newManList);
            }
        }

    }

    private void btnClick(Task task) {
        if (task.getGain() == 1) {
            //领取过了
        } else if (task.getTaskCount() == task.getCompleteCount()) {
            //todo 领取奖励
        } else {
            if (task.getJumpContent().equals(Task.RULE_FUTURE_BATTLE)) {
                //期货对战跳转普通场
            }
            if (task.getJumpContent().equals(Task.RULE_KLINE_BATTLE)) {
                //K线对战
            }
        }

    }

    private void startDialog(int integration) {
        ReceiveIntegrationDialog.get(this, new ReceiveIntegrationDialog.OnCallback() {
            @Override
            public void onDialogClick() {

            }
        }, integration);
        startScheduleJob(2000);
    }

    static class TaskAdapter extends ArrayAdapter<Task> {

        interface OnBtnClickListener {
            public void onBtnClick(Task task);
        }

        private Context mContext;
        private OnBtnClickListener mOnBtnClickListener;

        public TaskAdapter(@NonNull Context context) {
            super(context, 0);
            mContext = context;
        }

        public void setOnBtnClickListener(OnBtnClickListener onBtnClickListener) {
            mOnBtnClickListener = onBtnClickListener;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder = null;
            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_kline_result_rank, parent, false);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), mContext, position, getCount(), mOnBtnClickListener);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.icon)
            ImageView mIcon;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.integration)
            TextView mIntegration;
            @BindView(R.id.tip)
            TextView mTip;
            @BindView(R.id.progress)
            TextView mProgress;
            @BindView(R.id.goBtn)
            TextView mGoBtn;
            @BindView(R.id.bottomLine)
            View mBottomLine;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final Task item, Context mContext, int position, int count, final OnBtnClickListener mOnBtnClickListener) {
                if (position == count - 1) {
                    mBottomLine.setVisibility(View.GONE);
                } else {
                    mBottomLine.setVisibility(View.VISIBLE);
                }
                selectIconAndTip(item, mContext);
                mTip.setText(item.getRuleName());
                mProgress.setText(item.getTaskCount() + "/" + item.getCompleteCount());
                mIntegration.setText(String.format(mContext.getString(R.string.get_integration), item.getIntegral()));
                setGoBtnBgAndListener(item.getGain(), item.getTaskCount(), item.getCompleteCount(), mContext);
                mGoBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (mOnBtnClickListener != null) {
                            mOnBtnClickListener.onBtnClick(item);
                        }
                    }
                });
            }

            private void setGoBtnBgAndListener(final int gain, final int taskCount, final int completeCount, Context mContext) {
                if (gain == 0) {
                    if (taskCount == completeCount) {
                        mGoBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_quire_integration));
                        mGoBtn.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                        mGoBtn.setText(R.string.receive_integration);
                    } else {
                        mGoBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_integration_to_finish));
                        mGoBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                        mGoBtn.setText(R.string.goto_finish);
                    }
                } else {
                    mGoBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_required_integration));
                    mGoBtn.setTextColor(ContextCompat.getColor(mContext, R.color.unluckyText));
                    mGoBtn.setText(R.string.received_integration);
                }
            }

            private void selectIconAndTip(Task task, Context mContext) {
                if (task.getJumpContent().equals(Task.RULE_FUTURE_BATTLE)) {
                    //期货对战
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_stock));
                    mName.setText(R.string.future_battle);
                }
            }
        }
    }
}
