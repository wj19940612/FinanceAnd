package com.sbai.finance.activity.leveltest;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.leveltest.ExamQuestionsModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class LevelTestStartActivity extends BaseActivity {

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
        setContentView(R.layout.activity_level_test_start);
        ButterKnife.bind(this);
        translucentStatusBar();

        requestJoinTestedNumber();
        updateCompleteTestNumber(0l);

        // TODO: 2017/8/2 从登陆注册页面进来
        mTitleBar.setLeftText(R.string.pass);
        mTitleBar.setLeftTextColor(Color.WHITE);

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

    // TODO: 2017/7/31 请求题库接口
    private void openLevelExamQuestionsPage() {
        Client.requestExamQuestions()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<ArrayList<ExamQuestionsModel>>, ArrayList<ExamQuestionsModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<ExamQuestionsModel> data) {
                        Launcher.with(getActivity(), LevelExamQuestionsActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, data)
                                .execute();

                    }
                })
                .fire();

    }
}
