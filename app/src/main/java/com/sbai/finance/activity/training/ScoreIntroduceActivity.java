package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.levelevaluation.TestResultModel;
import com.sbai.finance.model.training.TrainAppraiseAndRemark;
import com.sbai.finance.model.training.UserEachTrainingScoreModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.leveltest.ScoreView;
import com.sbai.finance.view.train.ScoreProgressView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class ScoreIntroduceActivity extends BaseActivity {


    @BindView(R.id.scoreProgress)
    ScoreProgressView mScoreProgress;
    @BindView(R.id.score)
    ScoreView mScore;
    @BindView(R.id.bg_score_explain)
    TextView mBgScoreExplain;

    private UserEachTrainingScoreModel mUserEachTrainingScoreModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_introduce);
        ButterKnife.bind(this);


        mUserEachTrainingScoreModel = getIntent().getParcelableExtra(Launcher.EX_PAYLOAD);
        //测评结果页数据
        TestResultModel historyTestResultModel = getIntent().getParcelableExtra(ExtraKeys.HISTORY_TEST_RESULT);
        if (historyTestResultModel != null) {
            updateScoreViewData(historyTestResultModel);
        }

        requestUserScore();

        //训练首页数据
        if (mUserEachTrainingScoreModel != null) {
            Log.d(TAG, "onCreate: " + mUserEachTrainingScoreModel.toString());
            TestResultModel testResultModel = mUserEachTrainingScoreModel.getTestResultModel();
            Log.d(TAG, "onCreate   testResultModel: " + testResultModel.toString());
            updateScoreViewData(testResultModel);
        }

        requestScoreStageAndRemark();
    }


    private void requestUserScore() {
        Client.requestUserScore()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<UserEachTrainingScoreModel>, UserEachTrainingScoreModel>() {
                    @Override
                    protected void onRespSuccessData(UserEachTrainingScoreModel data) {
                        mUserEachTrainingScoreModel = data;
                        requestScoreStageAndRemark();
                    }
                }).fire();
    }

    private void updateScoreViewData(TestResultModel testResultModel) {
        if (testResultModel != null) {
            mScore.setUserTrainScoreData(testResultModel);
        }
    }

    private void requestScoreStageAndRemark() {
        if (mUserEachTrainingScoreModel != null) {
            Client.requestScoreStageAndRemark()
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<ArrayList<TrainAppraiseAndRemark>>, ArrayList<TrainAppraiseAndRemark>>() {
                        @Override
                        protected void onRespSuccessData(ArrayList<TrainAppraiseAndRemark> data) {
                            if (data != null && !data.isEmpty()) {
                                mScoreProgress.setUserScoreGradeData(data, mUserEachTrainingScoreModel);
                            }
                        }
                    })
                    .fireFree();
        }
    }

}