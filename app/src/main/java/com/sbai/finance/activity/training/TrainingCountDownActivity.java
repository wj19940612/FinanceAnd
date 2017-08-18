package com.sbai.finance.activity.training;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.gifdecoder.GifDecoder;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingQuestion;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 训练倒计时页面
 */
public class TrainingCountDownActivity extends BaseActivity {

    private static final int REQ_CODE_SHOW_RULE = 434;

    @BindView(R.id.gif)
    ImageView mGif;
    @BindView(R.id.background)
    RelativeLayout mBackground;

    private Training mTraining;
    private int mGifRes;
    private int mBackgroundRes;

    private TrainingQuestion mTrainingQuestion;


    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                Log.d(TAG, "handleMessage: " + mTraining);
                switch (mTraining.getPlayType()) {
                    case Training.PLAY_TYPE_REMOVE:
                        if (mTrainingQuestion != null && mTraining != null) {
                            Launcher.with(getActivity(), KlineTrainActivity.class)
                                    .putExtra(ExtraKeys.TRAIN_QUESTIONS, mTrainingQuestion)
                                    .putExtra(ExtraKeys.TRAINING, mTraining)
                                    .execute();
                        }
                        break;
                    case Training.PLAY_TYPE_MATCH_STAR:
                        break;
                    case Training.PLAY_TYPE_SORT:
                        if (mTrainingQuestion != null && mTraining != null) {
                            Launcher.with(getActivity(), SortQuestionActivity.class)
                                    .putExtra(ExtraKeys.TRAIN_QUESTIONS, mTrainingQuestion)
                                    .putExtra(ExtraKeys.TRAIN_TARGET_TIME, mTraining)
                                    .execute();
                        }
                        break;
                    case Training.PLAY_TYPE_JUDGEMENT:
                        Launcher.with(getActivity(), JudgeTrainingActivity.class).execute();
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
        updateScreenOrientation();

        setContentView(R.layout.activity_training_count_down);
        ButterKnife.bind(this);
        requestTrainingContent();

        if (mBackgroundRes != 0) {
            mBackground.setBackgroundResource(mBackgroundRes);
        }

        if (Preference.get().isFirstTrain(mTraining.getId())) {
            Launcher.with(getActivity(), HowPlayActivity.class)
                    .putExtra(ExtraKeys.TRAINING, mTraining)
                    .executeForResult(REQ_CODE_SHOW_RULE);
            Preference.get().setIsFirstTrainFalse(mTraining.getId(), false);
        } else {
            startGifAnimation();
        }
    }

    private void requestTrainingContent() {
        Client.getTrainingContent(mTraining.getId())
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<String>, List<TrainingQuestion>>() {
                    @Override
                    protected void onRespSuccessData(List<TrainingQuestion> data) {
                        if (data != null && !data.isEmpty()) {
                            mTrainingQuestion = data.get(0);
                        }
                    }

                    @Override
                    protected String onInterceptData(String data) {
                        return SecurityUtil.AESDecrypt(data);
                    }
                })

                .fireFree();
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_SHOW_RULE) {
            startGifAnimation();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    private void updateScreenOrientation() {
        if (mTraining.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
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
        }
    }

    private void startGifAnimation() {
        if (mGifRes != 0) {
            Glide.with(getActivity())
                    .load(mGifRes)
                    .listener(new RequestListener<Integer, GlideDrawable>() {
                        @Override
                        public boolean onException(Exception e, Integer model, Target<GlideDrawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(GlideDrawable resource, Integer model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                            // 计算动画时长
                            GifDrawable drawable = (GifDrawable) resource;
                            GifDecoder decoder = drawable.getDecoder();
                            int duration = 0;
                            for (int i = 0; i < drawable.getFrameCount(); i++) {
                                duration += decoder.getDelay(i);
                            }
                            //发送延时消息，通知动画结束
                            mHandler.sendEmptyMessageDelayed(0, duration);
                            return false;
                        }
                    })
                    .diskCacheStrategy(DiskCacheStrategy.SOURCE)//添加缓存
                    .into(new GlideDrawableImageViewTarget(mGif, 1));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mHandler != null) {
            mHandler.removeCallbacksAndMessages(null);
        }
    }
}
