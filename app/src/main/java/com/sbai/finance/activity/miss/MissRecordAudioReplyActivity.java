package com.sbai.finance.activity.miss;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.miss.MissReplyAnswer;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FileUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.radio.MissRecordedAudioLayout;
import com.sbai.httplib.ApiError;

import java.io.File;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MissRecordAudioReplyActivity extends MediaPlayActivity implements MissRecordedAudioLayout.OnRecordAudioListener {

    public static final int QUESTION_TYPE_IS_NOT_SPECIFIED_MISS = 1;

    private static final int SUBMIT_INIT = 0;
    private static final int SUBMIT_PROCEED = 1;
    private static final int SUBMIT_ERROR = 2;
    private static final int SUBMIT_SUCCESS = 3;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.avatar)
    HasLabelImageLayout mAvatar;
    @BindView(R.id.name)
    TextView mName;
    @BindView(R.id.askTime)
    TextView mAskTime;
    @BindView(R.id.questionContent)
    TextView mQuestionContent;
    @BindView(R.id.questionRl)
    RelativeLayout mQuestionRl;
    @BindView(R.id.missAvatar)
    HasLabelImageLayout mMissAvatar;
    @BindView(R.id.missName)
    TextView mMissName;
    @BindView(R.id.play)
    ImageView mPlay;
    @BindView(R.id.clickPlay)
    TextView mClickPlay;
    @BindView(R.id.audioLength)
    TextView mAudioLength;
    @BindView(R.id.loading)
    ImageView mLoading;
    @BindView(R.id.submitStatus)
    TextView mSubmitStatus;
    @BindView(R.id.submitStatusLL)
    LinearLayout mSubmitStatusLL;
    @BindView(R.id.playSubmitRl)
    RelativeLayout mPlaySubmitRl;
    @BindView(R.id.submitResultHint)
    TextView mSubmitResultHint;
    @BindView(R.id.playRl)
    RelativeLayout mPlayRl;
    @BindView(R.id.audioRecord)
    MissRecordedAudioLayout mAudioRecord;

    MissReplyAnswer mMissReplyAnswer;
    private boolean mSubmitSuccess = true;


    private boolean mSubmitting;

    private int mRecordAudioLength;
    private int mTotalTime;
    private String mRecordAudioPath;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_record_audio_reply);
        ButterKnife.bind(this);

        mAudioRecord.setOnRecordAudioListener(this);

        int questionId = getIntent().getIntExtra(ExtraKeys.QUESTION_ID, -1);
        int questionType = getIntent().getIntExtra(ExtraKeys.QUESTION_TYPE, 0);
        if (questionType == QUESTION_TYPE_IS_NOT_SPECIFIED_MISS) {
            mTitleBar.setTitle(R.string.question_answer_detail);
        } else {
            mTitleBar.setTitle(R.string.wait_me_answer);
        }

        Client.updateAnswerReadStatus(questionId).setTag(TAG).fireFree();
        requestQuestionDetail(questionId);
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        mPlay.setSelected(true);
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        mPlay.setSelected(true);
        startScheduleJob(1000, 0);
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        mPlay.setSelected(true);
        startScheduleJob(1000, 0);
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        mPlay.setSelected(false);
        stopScheduleJob();
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        mPlay.setSelected(false);
        stopScheduleJob();
        mAudioLength.setText(getString(R.string.voice_time, mRecordAudioLength));
        mTotalTime = mRecordAudioLength;
    }

    @Override
    protected void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {

    }


    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        int time = --mTotalTime;
        if (time < 0) return;
        mAudioLength.setText(getString(R.string.voice_time, time));
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopPlay();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopPlay();
    }

    private void stopPlay() {
        MissAudioManager.get().stop();
        mPlay.setSelected(false);
        stopScheduleJob();
        mAudioLength.setText(getString(R.string.voice_time, mRecordAudioLength));
        mTotalTime = mRecordAudioLength;
    }

    private void requestQuestionDetail(int questionId) {
        Client.requestMissAnswerQuestionInfo(questionId)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<MissReplyAnswer>, MissReplyAnswer>() {
                    @Override
                    protected void onRespSuccessData(MissReplyAnswer data) {
                        mMissReplyAnswer = data;
                        updateReplyAnswerInfo(data);
                    }
                })
                .fireFree();
    }

    private void updateReplyAnswerInfo(MissReplyAnswer data) {
        mAvatar.setAvatar(data.getUserPortrait(), data.getUserType());
        mName.setText(data.getUserName());
        mAskTime.setText(DateUtil.formatDefaultStyleTime(data.getCreateTime()));
        mQuestionContent.setText(data.getQuestionContext());
        if (data.getSolve() == MissReplyAnswer.QUESTION_SOLVE_STATUS_ALREADY_SOLVE) {
            mAudioRecord.setEnabled(false);
            mPlayRl.setVisibility(View.VISIBLE);
            List<MissReplyAnswer.ReplyVOBean> replyVOBeans = data.getReplyVO();
            if (replyVOBeans != null && !replyVOBeans.isEmpty()) {
                MissReplyAnswer.ReplyVOBean replyVOBean = replyVOBeans.get(0);

                mMissAvatar.setAvatar(replyVOBean.getCustomPortrait(), Question.USER_IDENTITY_MISS);
                mMissName.setText(replyVOBean.getCustomName());
                if (!TextUtils.isEmpty(replyVOBean.getCustomContext())) {
                    mMissReplyAnswer.setAudioPath(replyVOBean.getCustomContext());
                }
                mTotalTime = replyVOBean.getSoundTime();
                mRecordAudioLength = replyVOBean.getSoundTime();
                mAudioLength.setText(getString(R.string.voice_time, replyVOBean.getSoundTime()));
            }
            changeSubmitStatus(SUBMIT_SUCCESS);
        } else {
            if (LocalUser.getUser().isLogin()) {
                UserInfo userInfo = LocalUser.getUser().getUserInfo();
                mMissAvatar.setAvatar(userInfo.getUserPortrait(), Question.USER_IDENTITY_MISS);
                mMissName.setText(userInfo.getUserName());
            }
        }
    }


    @Override
    public void onBackPressed() {
        if (mSubmitting) {
            showSubmittingHintDialog();
        } else if (!mSubmitSuccess) {
            SmartDialog.single(getActivity(), getString(R.string.you_still_have_the_recording_not_sent_can_you_really_give_up))
                    .setNegative(R.string.cancel)
                    .setPositive(R.string.give_up_sent, new SmartDialog.OnClickListener() {
                        @Override
                        public void onClick(Dialog dialog) {
                            dialog.dismiss();
                            finish();
                        }
                    })
                    .show();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void finish() {
        super.finish();
        FileUtils.deleteFile();
    }

    private void showSubmittingHintDialog() {
        SmartDialog.single(getActivity(), getString(R.string.stop_submit_hint))
                .setTitle(R.string.stop_submit)
                .setPositive(R.string.again)
                .setNegative(R.string.exit, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .show();
    }

    @OnClick({R.id.avatar, R.id.questionRl, R.id.missAvatar, R.id.missName, R.id.play, R.id.clickPlay, R.id.submitStatusLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                if (mMissReplyAnswer != null) {
                    Launcher.with(getActivity(), LookBigPictureActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mMissReplyAnswer.getUserPortrait())
                            .execute();
                }
                break;
            case R.id.questionRl:
                break;
            case R.id.missAvatar:
                if (mMissReplyAnswer != null) {
                    if (mMissReplyAnswer.getReplyVO() != null && !mMissReplyAnswer.getReplyVO().isEmpty()) {
                        Launcher.with(getActivity(), MissProfileDetailActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mMissReplyAnswer.getReplyVO().get(0).getCustomId())
                                .execute();
                    } else {
                        if (LocalUser.getUser().isLogin() && LocalUser.getUser().isMiss()) {
                            Launcher.with(getActivity(), MissProfileDetailActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, LocalUser.getUser().getUserInfo().getCustomId())
                                    .execute();
                        }
                    }
                }
                break;
            case R.id.play:
            case R.id.clickPlay:
                playAudio();
                break;
            case R.id.submitStatusLL:
                showSubmitAudioVerifyDialog();
                break;
        }
    }

    private void playAudio() {
        if (mMissReplyAnswer != null) {
            if (!TextUtils.isEmpty(mMissReplyAnswer.getAudioUrl())) {
                if (MissAudioManager.get().isStarted(mMissReplyAnswer)) {
                    MissAudioManager.get().pause();
                } else if (MissAudioManager.get().isPaused(mMissReplyAnswer)) {
                    MissAudioManager.get().resume();
                } else {
                    MissAudioManager.get().start(mMissReplyAnswer);
                }
            }
        }
    }

    private void showSubmitAudioVerifyDialog() {
        SmartDialog.single(getActivity(), getString(R.string.submit_before))
                .setTitle(R.string.verify_submit)
                .setNegative(R.string.cancel)
                .setPositive(R.string.send, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        submitRecordAudio();
                    }
                }).show();
    }


    public void convertAudio(String path) {
        final File file = new File(path);
        Log.d(TAG, "convertAudio: " + file.getAbsolutePath());
        mSubmitting = true;
        submitMp3File(file);
    }

    private void submitMp3File(final File convertedFile) {
        Client.submitFile("file", convertedFile)
                .setTag(TAG)
                .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (mMissReplyAnswer != null) {
                            mMissReplyAnswer.setAudioPath((String) resp.getData());
                        }

                        submitMissAnswer((String) resp.getData(), convertedFile);
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        changeSubmitStatus(SUBMIT_ERROR);
                    }
                })
                .fireFree();
    }

    private void submitRecordAudio() {
        if (mMissReplyAnswer != null
                && !TextUtils.isEmpty(mMissReplyAnswer.getAudioUrl())
                && LocalUser.getUser().isLogin()) {
            changeSubmitStatus(SUBMIT_PROCEED);
            convertAudio(mMissReplyAnswer.getAudioUrl());
        }
    }

    private void submitMissAnswer(final String data, final File convertedFile) {
        Client.submitMissAnswer(mMissReplyAnswer.getId(),
                data, mRecordAudioLength,
                LocalUser.getUser().getUserInfo().getCustomId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        changeSubmitStatus(SUBMIT_SUCCESS);

                        mMissReplyAnswer.setAudioPath(data);
                        if (convertedFile != null) {
                            boolean delete = convertedFile.delete();
                            mRecordAudioPath = null;
                        }
                        SmartDialog.dismiss(getActivity());
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        changeSubmitStatus(SUBMIT_ERROR);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        mSubmitting = false;
                    }
                })
                .fireFree();

    }

    private void changeSubmitStatus(int submitStatus) {
        switch (submitStatus) {
            case SUBMIT_INIT:

                break;
            case SUBMIT_PROCEED:
                mLoading.startAnimation(AnimationUtils.loadAnimation(this, R.anim.loading));
                mLoading.setVisibility(View.VISIBLE);
                mSubmitStatus.setText(R.string.submitting);
                mAudioRecord.setEnabled(false);
                break;
            case SUBMIT_ERROR:
                changeSubmitStatusText(false);

                mLoading.clearAnimation();
                mLoading.setVisibility(View.GONE);

                mAudioRecord.setEnabled(true);
                mSubmitStatus.setText(R.string.restart_submit);
                mSubmitting = false;
                break;
            case SUBMIT_SUCCESS:
                mSubmitSuccess = true;
                mSubmitting = false;

                mLoading.clearAnimation();
                mLoading.setVisibility(View.VISIBLE);
                mLoading.setImageResource(R.drawable.ic_training_result_tick);

                mSubmitStatusLL.setBackgroundColor(Color.TRANSPARENT);
                mSubmitStatusLL.setEnabled(false);
                mSubmitStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                mSubmitStatus.setText(R.string.send_success);
                mAudioRecord.setEnabled(false);
                changeSubmitStatusText(true);
                break;
        }
    }

    private void changeSubmitStatusText(boolean isSuccess) {
        mSubmitResultHint.setVisibility(View.VISIBLE);
        if (isSuccess) {
            mSubmitResultHint.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            mSubmitResultHint.setText(R.string.audio_submit_success_hint);
        } else {
            mSubmitResultHint.setTextColor(Color.parseColor("#F55A53"));
            mSubmitResultHint.setText(R.string.submit_error);
        }
    }

    @Override
    public void onRecordAudioFinish(String audioPath, int audioLength) {
        Log.d(TAG, "onRecordAudioFinish: " + audioPath);
        if (!TextUtils.isEmpty(audioPath)) {
            mRecordAudioPath = audioPath;
            mSubmitSuccess = false;
            if (mPlayRl.getVisibility() == View.GONE) {
                mPlayRl.setVisibility(View.VISIBLE);
            }
            mAudioLength.setText(getString(R.string.voice_time, audioLength));
            mRecordAudioLength = audioLength;
            mTotalTime = audioLength;
            if (mMissReplyAnswer != null) {
                mMissReplyAnswer.setAudioPath(audioPath);
            }
        }
    }
}
