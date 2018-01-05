package com.sbai.finance.activity.mine;

import android.content.Context;
import android.content.Intent;
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

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.MainActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.anchor.SubmitQuestionActivity;
import com.sbai.finance.activity.anchor.radio.AllRadioListActivity;
import com.sbai.finance.activity.arena.klinebattle.BattleKlineActivity;
import com.sbai.finance.activity.battle.BattleListActivity;
import com.sbai.finance.activity.evaluation.EvaluationStartActivity;
import com.sbai.finance.activity.mine.userinfo.CreditApproveActivity;
import com.sbai.finance.activity.mine.userinfo.ModifyUserInfoActivity;
import com.sbai.finance.activity.trade.trade.StockOrderActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.BoxProgress;
import com.sbai.finance.model.mine.Task;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
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

    public static final int TASK_NEW_MAN = 0;
    public static final int TASK_NORMAL = 1;
    public static final int TASK_BOX = 2;

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
    }

    @Override
    public void onTimeUp(int count) {
        SmartDialog.dismiss(this);
        stopScheduleJob();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestListData();
        requestProgressData();
    }

    private void initView() {
        mTaskProgress.setOnOpenAwardListener(new TaskProgressView.OnOpenAwardListener() {
            @Override
            public void onOpenAward(boolean first) {
                requestReceiveIntegration(TASK_BOX, first ? Task.RULE_BOX01 : Task.RULE_BOX02);
            }
        });

        mNewManAdapter = new TaskAdapter(this);
        mNormalAdapter = new TaskAdapter(this);
        mNewManAdapter.setOnBtnClickListener(new TaskAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(Task task) {
                clickReceive(task);
            }
        });
        mNormalAdapter.setOnBtnClickListener(new TaskAdapter.OnBtnClickListener() {
            @Override
            public void onBtnClick(Task task) {
                clickReceive(task);
            }
        });
        mNewManListView.setAdapter(mNewManAdapter);
        mNormalListView.setAdapter(mNormalAdapter);
    }

    private void requestListData() {
        Client.requestTaskData().setTag(TAG).setCallback(new Callback2D<Resp<List<Task>>, List<Task>>() {
            @Override
            protected void onRespSuccessData(List<Task> data) {
                updateListData(data);
            }
        }).fireFree();
    }

    private void requestProgressData() {
        Client.requestBoxProgress().setTag(TAG).setCallback(new Callback2D<Resp<BoxProgress>, BoxProgress>() {
            @Override
            protected void onRespSuccessData(BoxProgress data) {
                updateBoxProgress(data);
            }
        }).fireFree();
    }

    private void requestReceiveIntegration(int taskType, String ruleCode) {
        Client.requestReceiveIntegration(taskType, ruleCode).setTag(TAG).setCallback(new Callback2D<Resp<Object>, Object>() {
            @Override
            protected void onRespSuccessData(Object data) {
                updateReceiveResult((int) (data));
            }
        }).fireFree();
    }

    private void updateListData(List<Task> data) {
        List<Task> newManList = new ArrayList<>();
        List<Task> normalList = new ArrayList<>();
        for (Task task : data) {
            if (task.getTaskType() == 0) {
                newManList.add(task);
            } else if (task.getTaskType() == 1) {
                normalList.add(task);
            }
        }

        if (newManList.size() == 0) {
            mNewTaskLayout.setVisibility(View.GONE);
        } else {
            mNewTaskLayout.setVisibility(View.VISIBLE);
            mNewManAdapter.clear();
            mNewManAdapter.addAll(newManList);
        }
        if (normalList.size() == 0) {
            mNormalTaskLayout.setVisibility(View.GONE);
        } else {
            mNormalTaskLayout.setVisibility(View.VISIBLE);
            mNormalAdapter.clear();
            mNormalAdapter.addAll(normalList);
        }

    }

    private void updateBoxProgress(BoxProgress boxProgress) {
        mGetIntegration.setText(String.valueOf(boxProgress.getTodayIntegral()));
        mTaskProgress.setProgress(boxProgress.getCompleteCount());
        if (boxProgress.getCompleteCount() >= 3 && boxProgress.getGainThree() == 0) {
            //达到要求未领奖励
            mTaskProgress.flashIcon(true);
        } else if (boxProgress.getCompleteCount() < 3) {
            //没达到要求
            mTaskProgress.setNotArriveAwardIcon(true);
        } else if (boxProgress.getGainThree() == 1) {
            mTaskProgress.openIcon(true);
        }
        if (boxProgress.getCompleteCount() == 5 && boxProgress.getGainLast() == 0) {
            //达到要求未领奖励
            mTaskProgress.flashIcon(false);
        } else if (boxProgress.getCompleteCount() < 5) {
            //没达到要求
            mTaskProgress.setNotArriveAwardIcon(false);
        } else if (boxProgress.getGainLast() == 1) {
            mTaskProgress.openIcon(false);
        }
    }

    private void updateReceiveResult(int integration) {
        startDialog(integration);
    }

    private void clickReceive(Task task) {
        if (task.getGain() == 1) {
            //领取过了
        } else if (task.getGain() == 2) {
            //未领取
            requestReceiveIntegration(task.getTaskType(), task.getRuleCode());
        } else {
            //去完成
            if (task.getRuleCode().equals(Task.RULE_FUTURE_BATTLE)) {
                //期货对战跳转普通场
                umengEventCount(UmengCountEventId.ARENA_FUTURE_PK);
                Launcher.with(getActivity(), BattleListActivity.class).execute();
            }
            if (task.getRuleCode().equals(Task.RULE_KLINE_BATTLE)) {
                //K线对战
                Launcher.with(getActivity(), BattleKlineActivity.class)
                        .execute();
            }
            if (task.getRuleCode().equals(Task.RULE_RADIO)) {
                //收听电台
                Launcher.with(getActivity(), AllRadioListActivity.class).execute();
            }
            if (task.getRuleCode().equals(Task.RULE_GUESS)) {
                //参与猜大盘
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), StockOrderActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
            if (task.getRuleCode().equals(Task.RULE_ASK)) {
                //姐说提问
                Launcher.with(getActivity(), SubmitQuestionActivity.class)
                        .execute();
            }
            if (task.getRuleCode().equals(Task.RULE_DISCUSS)) {
                //姐说音频、文章、话题、问题评论
                Launcher.with(getActivity(), MainActivity.class)
                        .putExtra(ExtraKeys.MAIN_PAGE_CURRENT_ITEM, MainActivity.PAGE_POSITION_ANCHOR)
                        .execute();
            }
            if (task.getRuleCode().equals(Task.RULE_INVITE)) {
                //邀请新用户注册成功
                Launcher.with(getActivity(), WebActivity.class)
                        .putExtra(WebActivity.EX_URL, task.getJumpContent())
                        .execute();
            }
            if (task.getRuleCode().equals(Task.RULE_CERTIFICATION)) {
                //实名认证通过审核
                umengEventCount(UmengCountEventId.ME_CERTIFICATION);
                Launcher.with(getActivity(), CreditApproveActivity.class).execute();
            }
            if (task.getRuleCode().equals(Task.RULE_EVALUATION)) {
                //金融测评完成
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.ME_FINANCE_TEST);
                    Launcher.with(getActivity(), EvaluationStartActivity.class).execute();
                    Preference.get().setIsFirstOpenWalletPage(LocalUser.getUser().getPhone());
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }
            if (task.getRuleCode().equals(Task.RULE_HEAD)) {
                //修改昵称头像
                umengEventCount(UmengCountEventId.ME_MOD_USER_INFO);
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), ModifyUserInfoActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
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
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_task, parent, false);
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
                mName.setText(item.getTaskName());
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
                if (gain == 2) {
                    mGoBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_quire_integration));
                    mGoBtn.setTextColor(ContextCompat.getColor(mContext, R.color.white));
                    mGoBtn.setText(R.string.receive_integration);
                } else if(gain == 0){
                    mGoBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_integration_to_finish));
                    mGoBtn.setTextColor(ContextCompat.getColor(mContext, R.color.colorPrimary));
                    mGoBtn.setText(R.string.goto_finish);
                } else{
                    mGoBtn.setBackground(ContextCompat.getDrawable(mContext, R.drawable.btn_required_integration));
                    mGoBtn.setTextColor(ContextCompat.getColor(mContext, R.color.unluckyText));
                    mGoBtn.setText(R.string.received_integration);
                }
            }

            private void selectIconAndTip(Task task, Context mContext) {
                if (task.getRuleCode().equals(Task.RULE_FUTURE_BATTLE)) {
                    //期货对战
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_stock));
                }
                if (task.getRuleCode().equals(Task.RULE_KLINE_BATTLE)) {
                    //K线对战
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_kline));
                }
                if (task.getRuleCode().equals(Task.RULE_RADIO)) {
                    //收听电台
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_buy_radio));
                }
                if (task.getRuleCode().equals(Task.RULE_GUESS)) {
                    //参与猜大盘
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_guess));
                }
                if (task.getRuleCode().equals(Task.RULE_ASK)) {
                    //姐说提问
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_question));
                }
                if (task.getRuleCode().equals(Task.RULE_DISCUSS)) {
                    //姐说音频、文章、话题、问题评论
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_comment));
                }
                if (task.getRuleCode().equals(Task.RULE_INVITE)) {
                    //邀请新用户注册成功
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_invite));
                }
                if (task.getRuleCode().equals(Task.RULE_CERTIFICATION)) {
                    //实名认证通过审核
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_certification));
                }
                if (task.getRuleCode().equals(Task.RULE_EVALUATION)) {
                    //金融测评完成
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_financial_evaluation));
                }
                if (task.getRuleCode().equals(Task.RULE_HEAD)) {
                    //修改一次头像和昵称
                    mIcon.setImageDrawable(ContextCompat.getDrawable(mContext, R.drawable.ic_task_head));
                }
            }
        }
    }
}
