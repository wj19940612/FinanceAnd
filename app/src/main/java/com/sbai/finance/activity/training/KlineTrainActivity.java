package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.RemoveTraining;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.training.KlineTrainView;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * K线概念训练页面
 */

public class KlineTrainActivity extends BaseActivity {


    @BindView(R.id.indexView)
    TextView mIndexView;
    @BindView(R.id.trainView)
    KlineTrainView mTrainView;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.progressBar)
    TrainProgressBar mProgressBar;
    private TrainingQuestion mTrainingQuestion;
    private List<TrainingQuestion.ContentBean> mTrainings;
    private Training mTraining;

    /*用来标记第几组题*/
    private int mIndex;
    private int mSize;
    private int mPage;
    private boolean mIsSuccess;

    private CountDownTimer mCountDownTimer;
    //游戏进行的时间
    private long mTrainingCountTime;

    private int mTrainTargetTime;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_train);
        ButterKnife.bind(this);
        initData(getIntent());
        initView();
        initTrainView();
    }

    private void initData(Intent intent) {
        mTrainingQuestion = intent.getParcelableExtra(ExtraKeys.TRAIN_QUESTIONS);
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
    }

    private void initView() {
        if (mTraining == null) return;
        mProgressBar.setTotalSecondTime(mTraining.getTime());
        mTrainTargetTime = mTraining.getTime() * 1000;
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), HowPlayActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTraining)
                        .execute();
            }
        });


            mCountDownTimer = new CountDownTimer(mTrainTargetTime, 1) {
                @Override
                public void onTick(long millisUntilFinished) {
                    mTrainingCountTime = mTrainTargetTime - millisUntilFinished;
                    mTitleBar.setTitle(DateUtil.format(mTrainingCountTime, "mm: ss. SS"));
                    mProgressBar.setTrainChangeTime(mTrainingCountTime);
                }

                @Override
                public void onFinish() {
                    mCountDownTimer.cancel();
                    mTitleBar.setTitle(DateUtil.format(mTrainTargetTime, "mm: ss. SS"));
                    mIsSuccess = false;
                    requestEndTrain();
                }
            }.start();

        mTrainView.setOnEndCallback(new KlineTrainView.OnEndCallback() {
            @Override
            public void onEnd() {
                updateTrainData();
            }
        });
        }


    private void initTrainView() {
        if (mTrainingQuestion != null && mTrainingQuestion.getContent() != null
                && !mTrainingQuestion.getContent().isEmpty()) {
            mTrainings = mTrainingQuestion.getContent();
            mSize = mTrainingQuestion.getContent().size();
            mPage = mSize / 3;
            if (mSize % 3 > 0) {
                mPage++;
            }
            updateTrainData();
        } else {
            mTrainView.setVisibility(View.GONE);
            mIndexView.setVisibility(View.GONE);
        }
    }

    private void updateTrainData() {
        if (mTrainings == null || mTrainings.isEmpty()) {
            mIsSuccess = true;
            requestEndTrain();
        } else {
            List<RemoveTraining> trainList = new ArrayList<>();
            for (int i = 0; i < 3; i++) {
                if (i < mTrainings.size()) {
                    trainList.add(mTrainings.get(i).getKey());
                    trainList.add(mTrainings.get(i).getValue());
                }
            }
            for (int i = 0; i < trainList.size() / 2; i++) {
                mTrainings.remove(0);
            }
            mTrainView.setTrainData(trainList);
            mTrainView.startAppearAnim();
            mIndex++;
            mIndexView.setText(mIndex + "/" + mPage);
        }
    }

    private void requestEndTrain() {
        TrainingResultActivity.show(getActivity(), mTraining, (int) mTrainingCountTime, mIsSuccess);
        finish();
    }

    @Override
    public void onBackPressed() {
        SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
                .setTitle(getString(R.string.is_sure_exit_train))
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositive(R.string.continue_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @OnClick(R.id.trainView)
    public void onViewClicked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCountDownTimer.cancel();
    }
}
