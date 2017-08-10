package com.sbai.finance.activity.evaluation;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.RewardGetActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leveltest.ExamQuestionsModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.TitleBar;

import java.lang.reflect.Type;
import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class EvaluationStartActivity extends BaseActivity {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.joinPeopleNumber)
    TextView mJoinPeopleNumber;
    @BindView(R.id.startTest)
    TextView mStartTest;
    @BindView(R.id.historyResult)
    TextView mHistoryResult;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_evaluation_start);
        ButterKnife.bind(this);
        translucentStatusBar();

        requestJoinTestedNumber();
        updateCompleteTestNumber(0L);

        boolean isFirstTest = getIntent().getBooleanExtra(ExtraKeys.FIRST_TEST, false);
        if (isFirstTest) {
            mTitleBar.setLeftText(R.string.pass);
            mTitleBar.setLeftTextColor(Color.WHITE);
            mHistoryResult.setVisibility(View.GONE);
        }

    }

    @Override
    public void onBackPressed() {
        if (LocalUser.getUser().getUserInfo().isNewUser()) {
            int reward = LocalUser.getUser().getUserInfo().getRegisterRewardIngot();
            RewardGetActivity.show(getActivity(), reward);
            LocalUser.getUser().setNewUser(false);
        }
        super.onBackPressed();
    }

    private void updateCompleteTestNumber(Long data) {
        mJoinPeopleNumber.setText(getString(R.string.number_people_join, data));
    }

    private void requestJoinTestedNumber() {
        Client.requestJoinTestedNumber()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Long>, Long>() {
                    @Override
                    protected void onRespSuccessData(Long data) {
                        updateCompleteTestNumber(data);
                    }
                })
                .fireFree();
    }

    @OnClick({R.id.startTest, R.id.historyResult})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.startTest:
                if (LocalUser.getUser().isLogin()) {
                    openLevelExamQuestionsPage();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.historyResult:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), HistoryTestResultActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void openLevelExamQuestionsPage() {
        Client.requestExamQuestions()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<String>, String>() {
                    @Override
                    protected void onRespSuccessData(String data) {
                        ArrayList<ExamQuestionsModel> examQuestionsList = getExamQuestionsList(data);
                        if (examQuestionsList != null && !examQuestionsList.isEmpty()) {
                            Launcher.with(getActivity(), EvaluationQuestionsActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, examQuestionsList)
                                    .execute();
                        } else {
                            ToastUtil.show(R.string.get_exam_questions_fail);
                        }
                    }
                })
                .fire();

    }

    private ArrayList<ExamQuestionsModel> getExamQuestionsList(String data) {
        //需要对其进行AES解密
        try {
            String s = SecurityUtil.AESDecrypt(data);
            Log.d(TAG, "getExamQuestionsList: " + s);
            Type type = new TypeToken<ArrayList<ExamQuestionsModel>>() {
            }.getType();
            return new Gson().fromJson(s, type);
        } catch (JsonSyntaxException e) {
            Log.d(TAG, "getExamQuestionsList: " + e);
        }
        return null;

    }
}
