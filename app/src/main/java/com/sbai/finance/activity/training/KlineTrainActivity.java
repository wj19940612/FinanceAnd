package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.RemoveTraining;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.model.training.question.RemoveData;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.RenderScriptGaussianBlur;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.TrainingRuleDialog;
import com.sbai.finance.view.training.KlineTrainView;
import com.sbai.finance.view.training.TrainProgressBar;

import java.util.ArrayList;
import java.util.Collections;
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
    @BindView(R.id.bgImg)
    ImageView mBgImg;
    @BindView(R.id.content)
    RelativeLayout mContent;
    private List<RemoveData> mTrainings;
    private TrainingDetail mTrainingDetail;
    private Question mQuestion;
    /*用来标记第几组题*/
    private int mIndex;
    private int mSize;
    private boolean mIsSuccess;

    //游戏进行的时间
    private long mTrainingCountTime;

    private int mTrainTargetTime;

    private RenderScriptGaussianBlur mRenderScriptGaussianBlur;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_kline_train);
        ButterKnife.bind(this);
        translucentStatusBar();
        initData(getIntent());
        initView();
        initTrainView();
    }

    private void initData(Intent intent) {
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        mQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);
    }

    private void initView() {
        if (mTrainingDetail == null) return;
        mRenderScriptGaussianBlur = new RenderScriptGaussianBlur(this);
        mProgressBar.setTotalSecondTime(mTrainingDetail.getTrain().getTime());
        mTrainTargetTime = mTrainingDetail.getTrain().getTime() * 1000;
        mProgressBar.setOnTimeUpListener(new TrainProgressBar.OnTimeUpListener() {
            @Override
            public void onTick(long millisUntilUp) {
                mTrainingCountTime = millisUntilUp;
                formatTime(mTrainingCountTime);
            }

            @Override
            public void onFinish() {
                formatTime(mTrainTargetTime);
                if (!mIsSuccess) {
                    requestEndTrain();
                }
            }


            private void formatTime(long time) {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT) {
                    String format = DateUtil.format(time, "mm:ss.SS");
                    mTitleBar.setTitle(format);
                } else {
                    String format = DateUtil.format(time, "mm:ss.SSS");
                    String substring = format.substring(0, format.length() - 1);
                    mTitleBar.setTitle(substring);
                }
            }

        });

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showHowPlayDialog();

            }
        });

        mTrainView.setOnEndCallback(new KlineTrainView.OnEndCallback() {

            @Override
            public void onAllEnd() {
                updateTrainData();
            }

            @Override
            public void onMatchEnd() {
                mIndex++;
                mIndexView.setText(mIndex + "/" + mSize);
            }
        });
    }


    private void initTrainView() {
        if (mQuestion != null && mQuestion.getContent() != null
                && !mQuestion.getContent().isEmpty()) {
            mTrainings = mQuestion.getContent();
            Collections.shuffle(mTrainings);
            mSize = mQuestion.getContent().size();
            mIndexView.setText(mIndex + "/" + mSize);
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
        }
    }

    private void requestEndTrain() {
        TrainingSubmit trainingSubmit = new TrainingSubmit(mTrainingDetail.getTrain().getId());
        trainingSubmit.setTime((int) (mTrainingCountTime / 1000));
        trainingSubmit.setFinish(mIsSuccess);
        trainingSubmit.addQuestionId(mQuestion.getId());
        Launcher.with(getActivity(), TrainingResultActivity.class)
                .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                .putExtra(ExtraKeys.TRAINING_SUBMIT, trainingSubmit)
                .execute();
        finish();
    }

    private void showHowPlayDialog() {
        mBgImg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBgImg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);
        TrainingRuleDialog.with(getActivity(), mTrainingDetail.getTrain())
                .setOnDismissListener(new TrainingRuleDialog.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        mBgImg.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                    }
                })
                .show();
    }

    private void showCloseDialog() {
        mBgImg.setVisibility(View.VISIBLE);
        mContent.setDrawingCacheEnabled(true);
        mContent.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);

        Bitmap bitmap = mContent.getDrawingCache();
        mBgImg.setImageBitmap(mRenderScriptGaussianBlur.gaussianBlur(25, bitmap));
        mContent.setVisibility(View.INVISIBLE);

        SmartDialog.single(getActivity(), getString(R.string.exit_train_will_not_save_train_record))
                .setTitle(getString(R.string.is_sure_exit_train))
                .setNegative(R.string.exit_train, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setOnDismissListener(new SmartDialog.OnDismissListener() {
                    @Override
                    public void onDismiss(Dialog dialog) {
                        dialog.dismiss();
                        mBgImg.setVisibility(View.GONE);
                        mContent.setVisibility(View.VISIBLE);
                    }
                })
                .setPositive(R.string.continue_train)
                .show();
    }

    @Override
    public void onBackPressed() {
        showCloseDialog();
    }

    @OnClick(R.id.trainView)
    public void onViewClicked() {

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mProgressBar.cancelCountDownTimer();
    }
}
