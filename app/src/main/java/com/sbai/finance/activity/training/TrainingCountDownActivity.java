package com.sbai.finance.activity.training;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingResult;
import com.sbai.finance.model.training.TrainingSubmit;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.dialog.TrainingRuleDialog;
import com.sbai.httplib.ApiError;
import com.sbai.httplib.BuildConfig;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import pl.droidsonroids.gif.GifDrawable;
import pl.droidsonroids.gif.GifImageView;

/**
 * 训练倒计时页面
 */
public class TrainingCountDownActivity extends BaseActivity {

    @BindView(R.id.gif)
    GifImageView mGif;
    @BindView(R.id.background)
    RelativeLayout mBackground;

    private TrainingDetail mTrainingDetail;
    private Training mTraining;
    private Question mQuestion;

    private int mGifRes;
    private int mBackgroundRes;
    private GifDrawable mGifFromResource;

    private List<TrainingSubmit> mTrainingSubmitList;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                switch (mTraining.getPlayType()) {
                    case Training.PLAY_TYPE_REMOVE:
                        if (mQuestion != null && mTrainingDetail != null) {
                            Launcher.with(getActivity(), KlineTrainActivity.class)
                                    .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                                    .putExtra(ExtraKeys.QUESTION, mQuestion)
                                    .execute();
                        }
                        break;
                    case Training.PLAY_TYPE_MATCH_STAR:
                        if (mQuestion != null && mTrainingDetail != null) {
                            Launcher.with(getActivity(), NounExplanationActivity.class)
                                    .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                                    .putExtra(ExtraKeys.QUESTION, mQuestion)
                                    .execute();
                        }
                        break;
                    case Training.PLAY_TYPE_SORT:
                        if (mQuestion != null && mTrainingDetail != null) {
                            Launcher.with(getActivity(), SortQuestionActivity.class)
                                    .putExtra(ExtraKeys.QUESTION, mQuestion)
                                    .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                                    .execute();
                        }
                        break;
                    case Training.PLAY_TYPE_JUDGEMENT:
                        Launcher.with(getActivity(), JudgeTrainingActivity.class)
                                .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                                .putExtra(ExtraKeys.QUESTION, mQuestion)
                                .execute();
                        break;
                }
                finish();
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initData(getIntent());
        setContentView(R.layout.activity_training_count_down);
        ButterKnife.bind(this);

        translucentStatusBar();

        updateScreenOrientation();

        resubmitTrainingSubmits();

        if (mBackgroundRes != 0) {
            mBackground.setBackgroundResource(mBackgroundRes);
        }

        mBackground.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (Preference.get().isFirstTrain(mTraining.getId())) {
                    TrainingRuleDialog.with(getActivity(), mTraining)
                            .setOnDismissListener(new TrainingRuleDialog.OnDismissListener() {
                                @Override
                                public void onDismiss() {
                                    startGifAnimation();
                                }
                            }).show();
                    Preference.get().setIsFirstTrainFalse(mTraining.getId(), false);
                } else {
                    startGifAnimation();
                }
            }
        }, 120);
    }

    private void resubmitTrainingSubmits() {
        mTrainingSubmitList = new ArrayList<>();
        String phone = LocalUser.getUser().getPhone();
        List<TrainingSubmit> submits = Preference.get().getTrainingSubmits(phone);
        Iterator<TrainingSubmit> iterator = submits.iterator();
        while (iterator.hasNext()) {
            TrainingSubmit submit = iterator.next();
            iterator.remove();
            resubmit(submit);
        }
    }

    private void resubmit(final TrainingSubmit submit) {
        String json = new Gson().toJson(submit);
        Client.submitTrainingResult(SecurityUtil.AESEncrypt(json)).setTag(TAG)
                .setCallback(new Callback<Resp<TrainingResult>>() {
                    @Override
                    protected void onRespSuccess(Resp<TrainingResult> resp) {
                        if (BuildConfig.DEBUG) {
                            ToastUtil.show(resp.getData().toString());
                        }
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        mTrainingSubmitList.add(submit);
                        super.onFailure(apiError);
                    }
                }).fireFree();
        String phone = LocalUser.getUser().getPhone();
        Preference.get().setTrainingSubmits(phone, mTrainingSubmitList);
    }

    private void updateScreenOrientation() {
        mBackground.postDelayed(new Runnable() { // delay action for SAMSUNG phone
            @Override
            public void run() {
                if (mTraining.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                } else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                }
            }
        }, 100);
    }

    private void initData(Intent intent) {
        mTrainingDetail = intent.getParcelableExtra(ExtraKeys.TRAINING_DETAIL);
        if (mTrainingDetail != null) {
            mTraining = mTrainingDetail.getTrain();
        }
        mQuestion = intent.getParcelableExtra(ExtraKeys.QUESTION);

        switch (mTraining.getType()) {
            case Training.TYPE_THEORY:
                mGifRes = R.drawable.ic_count_down_theory;
                mBackgroundRes = R.color.redTheoryCountDown;
                break;
            case Training.TYPE_TECHNOLOGY:
                mGifRes = R.drawable.ic_count_down_technology;
                mBackgroundRes = R.color.violetTechnologyCountDown;
                break;
            case Training.TYPE_FUNDAMENTAL:
                mGifRes = R.drawable.ic_count_down_fundamentals;
                mBackgroundRes = R.color.yellowFundamentalCountDown;
                break;
            case Training.TYPE_COMPREHENSIVE:
                mGifRes = R.drawable.ic_count_down_fundamentals;
                mBackgroundRes = R.color.blueComprehensiveTraining;
                break;
        }
    }

    private void startGifAnimation() {
        try {
            mGifFromResource = new GifDrawable(getResources(), mGifRes);
            mGifFromResource.setLoopCount(1);
            mGif.setImageDrawable(mGifFromResource);
            mHandler.sendEmptyMessageDelayed(0, mGifFromResource.getDuration());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
        if (mGifFromResource != null) {
            mGifFromResource.recycle();
        }
    }
}
