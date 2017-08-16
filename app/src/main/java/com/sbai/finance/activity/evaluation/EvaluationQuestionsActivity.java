package com.sbai.finance.activity.evaluation;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.levelevaluation.ExamQuestions;
import com.sbai.finance.model.levelevaluation.TestAnswerUtils;
import com.sbai.finance.model.levelevaluation.TestResultModel;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.itemAnimator.BaseItemAnimator;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.autofit.AutofitTextView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class EvaluationQuestionsActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.exam)
    AutofitTextView mExam;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    private ArrayList<ExamQuestions> mExamQuestionsList;
    private int mExamPosition = 0;
    private ExamQuestionsAdapter mExamQuestionsAdapter;
    private ExamQuestions.ContentBean mSelectResult;
    private int mSelectPosition = -1;

    private TestAnswerUtils mTestAnswerUtils;
    private ArrayList<TestAnswerUtils.AnswersBean> mTestAnswerList;
    private ExamQuestions mSelectQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_questions);
        ButterKnife.bind(this);

        mTestAnswerUtils = new TestAnswerUtils();
        mTestAnswerList = new ArrayList<>();

        mTitleBar.setTitleSize(17);
        mExamQuestionsList = getIntent().getParcelableArrayListExtra(Launcher.EX_PAYLOAD);
        mExamQuestionsAdapter = new ExamQuestionsAdapter(new ArrayList<ExamQuestions.ContentBean>());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mExamQuestionsAdapter);
        mRecyclerView.setItemAnimator(new BaseItemAnimator());

        mExamQuestionsAdapter.setOnExamResultSelectListener(new ExamQuestionsAdapter.OnExamResultSelectListener() {
            @Override
            public void onExamResultSelect(ExamQuestions.ContentBean examQuestionsModel, int position) {
                changeExam(examQuestionsModel, position);
            }
        });
        changeExamProgress();
        updateExam();
    }

    private void changeExam(ExamQuestions.ContentBean examQuestionsModel, int position) {
        if (mSelectPosition != position && mSelectPosition != -1) {
            if (mSelectResult != null) {
                mSelectResult.setSelect(false);
                mExamQuestionsAdapter.notifyItemChanged(mSelectPosition, mSelectResult);
            }
        }
        saveSelectedResult(examQuestionsModel);
        mSelectResult = examQuestionsModel;
        mSelectPosition = position;
        selectResult();
    }

    //组装答案
    private void saveSelectedResult(ExamQuestions.ContentBean examQuestionsModel) {

        TestAnswerUtils.AnswersBean.AnswerIdsBean answerIdsBean = new TestAnswerUtils.AnswersBean.AnswerIdsBean();
        answerIdsBean.setOptionId(examQuestionsModel.getId());

        ArrayList<TestAnswerUtils.AnswersBean.AnswerIdsBean> answerIdsBeen = new ArrayList<>();
        answerIdsBeen.add(answerIdsBean);

        TestAnswerUtils.AnswersBean answersBean = new TestAnswerUtils.AnswersBean();
        if (mSelectQuestion != null) {
            answersBean.setTopicId(mSelectQuestion.getId());
        }
        answersBean.setAnswerIds(answerIdsBeen);
        if (mExamPosition == mExamQuestionsList.size()) {
            mTestAnswerList.remove(mExamPosition - 1);
        }
        mTestAnswerList.add(answersBean);
    }

    private boolean hasExamQuestions() {
        return mExamQuestionsList != null &&
                !mExamQuestionsList.isEmpty();
    }

    private void updateExam() {
        if (hasExamQuestions() && mExamPosition <= mExamQuestionsList.size()) {
            mSelectQuestion = mExamQuestionsList.get(mExamPosition);
            if (mSelectQuestion != null) {
                mExam.setText(mSelectQuestion.getDigest());
                ArrayList<ExamQuestions.ContentBean> dataList = mSelectQuestion.getContent();
                if (dataList != null && !dataList.isEmpty()) {
                    mExamQuestionsAdapter.updateData(dataList);
                }
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (hasExamQuestions() &&
                mExamPosition < (mExamQuestionsList.size() + 1)) {
            SmartDialog.with(getActivity(), R.string.is_exit_test)
                    .setPositive(R.string.ok, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            EvaluationQuestionsActivity.this.finish();
                        }
                    }).show();
        } else {
            super.onBackPressed();
        }
    }


    private void changeExamProgress() {
        if (!hasExamQuestions()) return;
        if (mExamPosition > mExamQuestionsList.size()) return;
        String examProgress = (mExamPosition + 1) + "/" + mExamQuestionsList.size();
        mTitleBar.setTitle(examProgress);
    }


    //提交答案
    private void selectResult() {
        if (!hasExamQuestions()) return;
        if (mExamPosition < mExamQuestionsList.size()) {
            mExamPosition++;
        }
        if (mExamPosition == mExamQuestionsList.size()) {
            confirmResult();
        } else {
            updateExam();
            changeExamProgress();
        }
    }

    private void confirmResult() {
        mTestAnswerUtils.setAnswers(mTestAnswerList);
        Client.confirmLevelTestResult(mTestAnswerUtils)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<TestResultModel>, TestResultModel>() {
                    @Override
                    protected void onRespSuccessData(TestResultModel data) {
                        Log.d(TAG, "onRespSuccessData: " + data.toString());

                        UserInfo userInfo = LocalUser.getUser().getUserInfo();
                        userInfo.setEvaluate(1);
                        LocalUser.getUser().setUserInfo(userInfo);
                        Launcher.with(getActivity(), EvaluationResultActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, data)
                                .execute();
                        finish();

                    }
                })
                .fire();
    }


    static class ExamQuestionsAdapter extends RecyclerView.Adapter<ExamQuestionsAdapter.ExamQuestionsViewHolder> {

        interface OnExamResultSelectListener {
            void onExamResultSelect(ExamQuestions.ContentBean examQuestionsModel, int position);
        }

        public OnExamResultSelectListener mOnExamResultSelectListener;

        private ArrayList<ExamQuestions.ContentBean> mExamQuestionsModelList;
        private int mShowCount;

        public ExamQuestionsAdapter(ArrayList<ExamQuestions.ContentBean> examQuestionsModelArrayList) {
            mExamQuestionsModelList = examQuestionsModelArrayList;
        }

        public void updateData(ArrayList<ExamQuestions.ContentBean> examQuestionsModels) {
            if (mShowCount > 0) {
                mExamQuestionsModelList.clear();
                notifyItemRangeRemoved(0, mShowCount);
            }
            mExamQuestionsModelList.addAll(examQuestionsModels);
            notifyItemRangeChanged(0, mExamQuestionsModelList.size());
            mShowCount = mExamQuestionsModelList.size();
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
            @BindView(R.id.resultTitle)
            TextView mResultTitle;

            ExamQuestionsViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final ExamQuestions.ContentBean examQuestionsModel,
                                         final OnExamResultSelectListener onExamResultSelectListener,
                                         final int position) {
                if (examQuestionsModel == null) return;
                mResult.setSelected(examQuestionsModel.isSelect());
                mResult.setText(examQuestionsModel.getContent());
                mResultTitle.setText(getQuestionsNumber(examQuestionsModel.getSeq()));
                mResultTitle.setTextColor(Color.parseColor("#222222"));
                mCard.setBackgroundResource(R.drawable.bg_white_rounded_sixteen_radius);
                mCard.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mCard.setBackgroundResource(R.drawable.bg_color_primary_rounded);
                        mResult.setSelected(true);
                        mResultTitle.setTextColor(Color.WHITE);
                        if (onExamResultSelectListener != null) {
                            onExamResultSelectListener.onExamResultSelect(examQuestionsModel, position);
                        }
                    }
                });
            }

            /**
             * 将1 转为A
             * 2  转为B
             *
             * @param number
             */
            private String getQuestionsNumber(int number) {
                String[] strings = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M", "N", "O", "P"
                        , "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z"};
                if (number < 26) {
                    return strings[number - 1];
                }
                return "";
            }

        }
    }


}
