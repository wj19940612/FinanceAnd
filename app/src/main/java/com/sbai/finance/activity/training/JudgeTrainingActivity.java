package com.sbai.finance.activity.training;

import android.content.Intent;
import android.os.Bundle;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.question.KData;
import com.sbai.finance.view.training.Kline.MvKlineView;

import butterknife.BindView;
import butterknife.ButterKnife;

public class JudgeTrainingActivity extends BaseActivity {

    @BindView(R.id.klineView)
    MvKlineView mKlineView;

    private Training mTraining;
    private Question<KData> mQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_judge_training);
        ButterKnife.bind(this);

        initData(getIntent());

        mKlineView.setDataList(mQuestion.getContent());
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
        mQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
    }
}
