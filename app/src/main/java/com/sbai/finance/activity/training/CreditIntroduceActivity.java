package com.sbai.finance.activity.training;

import android.os.Bundle;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.levelevaluation.EvaluationResult;
import com.sbai.finance.model.training.TrainAppraiseAndRemark;
import com.sbai.finance.model.training.UserEachTrainingScoreModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.leveltest.ScoreView;
import com.sbai.finance.view.training.ScoreProgressView;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;


public class CreditIntroduceActivity extends BaseActivity {


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


        //训练首页数据
        if (mUserEachTrainingScoreModel != null) {
            EvaluationResult evaluationResult = mUserEachTrainingScoreModel.getTestResultModel();
            updateScoreViewData(evaluationResult);
        }

        requestScoreStageAndRemark();
    }

    private void updateScoreViewData(EvaluationResult evaluationResult) {
        if (evaluationResult != null) {
            mScore.setUserTrainScoreData(evaluationResult);
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