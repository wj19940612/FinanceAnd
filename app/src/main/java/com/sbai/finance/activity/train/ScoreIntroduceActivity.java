package com.sbai.finance.activity.train;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.TrainAppraiseAndRemark;
import com.sbai.finance.model.training.UserEachTrainingScoreModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.train.ScoreProgressView;
import com.sbai.finance.view.leveltest.ScoreView;

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
        Log.d(TAG, "onCreate: " + mUserEachTrainingScoreModel.toString());
        if (mUserEachTrainingScoreModel != null) {
            mScore.setUserTrainScoreData(mUserEachTrainingScoreModel);
        }

        requestScoreStageAndRemark();
    }

    private void requestScoreStageAndRemark() {
        Client.requestScoreStageAndRemark()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<ArrayList<TrainAppraiseAndRemark>>, ArrayList<TrainAppraiseAndRemark>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<TrainAppraiseAndRemark> data) {
                        if (mUserEachTrainingScoreModel == null) return;
                        mScoreProgress.setUserScoreGradeData(data, mUserEachTrainingScoreModel);
                    }
                })
                .fireFree();
    }

}