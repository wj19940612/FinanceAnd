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

public class MissAudioReplyActivity extends MediaPlayActivity implements MissRecordedAudioLayout.OnRecordAudioListener {

    public static final int QUESTION_TYPE_IS_NOT_SPECIFIED_MISS = 1;

    private static final int SUBMIT_INIT = 0;
    private static final int SUBMIT_PROCEED = 1;
    private static final int SUBMIT_ERROR = 2;
    private static final int SUBMIT_SUCCESS = 3;

    String TAG = "MediaRecorderManager";

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
    private int mRecordAudioLength;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_audio_reply);
        ButterKnife.bind(this);

        initView();

        int questionId = getIntent().getIntExtra(ExtraKeys.QUESTION_ID, -1);
        int questionType = getIntent().getIntExtra(ExtraKeys.QUESTION_TYPE, 0);
        if (questionType == QUESTION_TYPE_IS_NOT_SPECIFIED_MISS) {
            mTitleBar.setTitle(R.string.question_answer_detail);
        } else {
            mTitleBar.setTitle(R.string.wait_me_answer);
        }
        requestQuestionDetail(questionId);
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        mPlay.setSelected(true);
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        mPlay.setSelected(true);
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        mPlay.setSelected(true);
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        mPlay.setSelected(false);
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        mPlay.setSelected(false);
    }

    @Override
    protected void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {

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

                mMissAvatar.setAvatar(replyVOBean.getCustomPortrait(), Question.USER_IDENTITY_HOST);
                mMissName.setText(replyVOBean.getCustomName());
                if (!TextUtils.isEmpty(replyVOBean.getCustomContext())) {
                    mMissReplyAnswer.setAudioPath(replyVOBean.getCustomContext());
                }
                mAudioLength.setText(getString(R.string.voice_time, replyVOBean.getSoundTime()));
            }
            changeSubmitStatus(SUBMIT_SUCCESS);
        } else {
            if (LocalUser.getUser().isLogin()) {
                UserInfo userInfo = LocalUser.getUser().getUserInfo();
                mMissAvatar.setAvatar(userInfo.getUserPortrait(), Question.USER_IDENTITY_HOST);
                mMissName.setText(userInfo.getUserName());
            }
        }
    }

    private void initView() {
        mAudioRecord.setOnRecordAudioListener(this);
    }


    @Override
    public void onBackPressed() {
        if (!mSubmitSuccess) {
            SmartDialog.single(getActivity(), getString(R.string.stop_submit_hint))
                    .setNegative(R.string.again)
                    .setPositive(R.string.stop, new SmartDialog.OnClickListener() {
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

    @OnClick({R.id.avatar, R.id.questionRl, R.id.missAvatar, R.id.missName, R.id.play, R.id.clickPlay, R.id.submitStatusLL})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                if (mMissReplyAnswer != null) {
                    Launcher.with(getActivity(), LookBigPictureActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mMissReplyAnswer.getUserPortrait())
                            .putExtra(Launcher.EX_PAYLOAD_2, 0)
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
                        if (LocalUser.getUser().isLogin()) {
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

    private void submitRecordAudio() {
        if (mMissReplyAnswer != null
                && !TextUtils.isEmpty(mMissReplyAnswer.getAudioUrl())
                && LocalUser.getUser().isLogin()) {
            mSubmitSuccess = false;
            changeSubmitStatus(SUBMIT_PROCEED);
            Client.submitFile("file", new File(mMissReplyAnswer.getAudioUrl()))
                    .setTag(TAG)
                    .setRetryPolicy(new DefaultRetryPolicy(100000, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT))
                    .setCallback(new Callback<Resp<Object>>() {

                        @Override
                        protected void onRespSuccess(Resp<Object> resp) {
                            submitMissAnswer(resp.getData());
                        }

                        @Override
                        public void onFailure(ApiError apiError) {
                            super.onFailure(apiError);
                            changeSubmitStatus(SUBMIT_ERROR);
                        }
                    })
                    .fireFree();
        }
    }

    private void submitMissAnswer(Object data) {
        Client.submitMissAnswer(mMissReplyAnswer.getId(),
                (String) data, mRecordAudioLength,
                LocalUser.getUser().getUserInfo().getCustomId())
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        changeSubmitStatus(SUBMIT_SUCCESS);
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
                mSubmitSuccess = true;
                break;
            case SUBMIT_SUCCESS:
                mSubmitSuccess = true;
                mLoading.clearAnimation();
                mLoading.setVisibility(View.VISIBLE);
                mSubmitStatusLL.setBackgroundColor(Color.TRANSPARENT);
                mSubmitStatusLL.setEnabled(false);
                mLoading.setImageResource(R.drawable.ic_training_result_tick);
                mSubmitStatus.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                mSubmitStatus.setText(R.string.submit_success);
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
            if (mPlayRl.getVisibility() == View.GONE) {
                mPlayRl.setVisibility(View.VISIBLE);
            }
            mAudioLength.setText(getString(R.string.voice_time, audioLength));
            mRecordAudioLength = audioLength;
            if (mMissReplyAnswer != null) {
                mMissReplyAnswer.setAudioPath(audioPath);
            }
        }
    }
}
