package com.sbai.finance.activity.leveltest;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.leveltest.ExamQuestionsModel;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.itemAnimator.BaseItemAnimator;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class LevelExamQuestionsActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.exam)
    TextView mExam;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ArrayList<ExamQuestionsModel> mExamQuestionsModelList;
    private int mExamPosition = 0;
    private ExamQuestionsAdapter mExamQuestionsAdapter;
    private ExamQuestionsModel.DataBean mSelectResult;
    private int mSelectPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level_exam_questions);
        ButterKnife.bind(this);
        mTitleBar.setTitleSize(17);
        mExamQuestionsModelList = getIntent().getParcelableArrayListExtra(Launcher.EX_PAYLOAD);

        mExamQuestionsAdapter = new ExamQuestionsAdapter(new ArrayList<ExamQuestionsModel.DataBean>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mExamQuestionsAdapter);
//        DISAPPEARING
        mRecyclerView.setItemAnimator(new BaseItemAnimator());
//        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mExamQuestionsAdapter.setOnExamResultSelectListener(new ExamQuestionsAdapter.OnExamResultSelectListener() {
            @Override
            public void onExamResultSelect(ExamQuestionsModel.DataBean examQuestionsModel, int position) {
                if (mSelectPosition != position && mSelectPosition != -1) {
                    if (mSelectResult != null) {
                        mSelectResult.setSelect(false);
                        mExamQuestionsAdapter.notifyItemChanged(mSelectPosition, mSelectResult);
                    }
                }
                mSelectResult = examQuestionsModel;
                mSelectPosition = position;
                confirmResult(0);
            }

        });
        changeExamProgress();
        updateExam();
    }

    private boolean hasExamQuestions() {
        return mExamQuestionsModelList != null &&
                !mExamQuestionsModelList.isEmpty();
    }

    private void updateExam() {
        if (hasExamQuestions() && mExamPosition <= mExamQuestionsModelList.size()) {
            ExamQuestionsModel examQuestionsModel = mExamQuestionsModelList.get(mExamPosition);
            if (examQuestionsModel != null) {
                mExam.setText(examQuestionsModel.getTopic());
                List<ExamQuestionsModel.DataBean> dataList = examQuestionsModel.getDataList();
                if (dataList != null && !dataList.isEmpty()) {
                    mExamQuestionsAdapter.updateData(dataList);
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (hasExamQuestions() &&
                mExamPosition < (mExamQuestionsModelList.size() + 1)) {
            SmartDialog.with(getActivity(), R.string.is_exit_test)
                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            LevelExamQuestionsActivity.this.finish();
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }

    private void changeExamProgress() {
        if (!hasExamQuestions()) return;
        if (mExamPosition > mExamQuestionsModelList.size()) return;
        String examProgress = (mExamPosition + 1) + "/" + mExamQuestionsModelList.size();
        mTitleBar.setTitle(examProgress);
    }


    //提交答案
    private void confirmResult(int result) {
        if (!hasExamQuestions()) return;

        // TODO: 2017/8/1 调用提交答案接口
//        Client.confirmLevelTestResult()
//                .setTag(TAG)
//                .setIndeterminate(this)
//                .setCallback(new Callback<Resp<Object>>() {
//                    @Override
//                    protected void onRespSuccess(Resp<Object> resp) {
        mExamPosition++;
        if (mExamPosition == mExamQuestionsModelList.size()) {
            // TODO: 2017/8/1 打开结果页面
            Launcher.with(getActivity(), ExamResultActivity.class).execute();
            finish();
        } else {
            updateExam();
            changeExamProgress();
        }
//                    }
//                })
//                .fireFree();

    }


    static class ExamQuestionsAdapter extends RecyclerView.Adapter<ExamQuestionsAdapter.ExamQuestionsViewHolder> {

        static interface OnExamResultSelectListener {
            void onExamResultSelect(ExamQuestionsModel.DataBean examQuestionsModel, int position);
        }

        public OnExamResultSelectListener mOnExamResultSelectListener;

        private ArrayList<ExamQuestionsModel.DataBean> mExamQuestionsModelList;

        public ExamQuestionsAdapter(ArrayList<ExamQuestionsModel.DataBean> examQuestionsModelArrayList) {
            mExamQuestionsModelList = examQuestionsModelArrayList;
        }

        public void updateData(List<ExamQuestionsModel.DataBean> examQuestionsModels) {
            notifyItemMoved(0, mExamQuestionsModelList.size());
            mExamQuestionsModelList.clear();
            mExamQuestionsModelList.addAll(examQuestionsModels);
            notifyItemRangeChanged(0, mExamQuestionsModelList.size());
        }

        public void setOnExamResultSelectListener(OnExamResultSelectListener onExamResultSelectListener) {
            this.mOnExamResultSelectListener = onExamResultSelectListener;
        }

        @Override
        public ExamQuestionsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_exam_questions, parent, false);
            return new ExamQuestionsViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ExamQuestionsViewHolder holder, int position) {
            if (!mExamQuestionsModelList.isEmpty()) {
                holder.bindDataWithView(mExamQuestionsModelList.get(position), mOnExamResultSelectListener, position);
            }
        }

        @Override
        public int getItemCount() {
            return mExamQuestionsModelList != null ? mExamQuestionsModelList.size() : 0;
        }

        static class ExamQuestionsViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.result)
            TextView mResult;
            @BindView(R.id.card)
            CardView mCard;

            ExamQuestionsViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final ExamQuestionsModel.DataBean examQuestionsModel, final OnExamResultSelectListener onExamResultSelectListener, final int position) {
                if (examQuestionsModel == null) return;
                mResult.setText(examQuestionsModel.getResult());
                mResult.setSelected(examQuestionsModel.isSelect());
                mCard.setBackgroundResource(R.drawable.bg_white_rounded_eight_radius);
//                mCard.setBackgroundResource(R.drawable.bg_white_rounded_shade);
                mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCard.setBackgroundResource(R.drawable.bg_color_primary_rounded);
                        mResult.setSelected(true);
                        examQuestionsModel.setSelect(true);
                        if (onExamResultSelectListener != null) {
                            onExamResultSelectListener.onExamResultSelect(examQuestionsModel, position);
                        }
                    }
                });
            }
        }
    }

}
