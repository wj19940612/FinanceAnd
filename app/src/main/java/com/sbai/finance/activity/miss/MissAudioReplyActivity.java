package com.sbai.finance.activity.miss;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.mine.UserInfo;
import com.sbai.finance.model.miss.MissReplyAnswer;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.radio.MissRecordedAudioLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MissAudioReplyActivity extends BaseActivity implements MissRecordedAudioLayout.OnRecordAudioListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_audio_reply);
        ButterKnife.bind(this);

        initView();

        int questionId = getIntent().getIntExtra(ExtraKeys.QUESTION_ID, -1);
        requestQuestionDetail(questionId);
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
        // TODO: 2017/12/1 缺少用户类型
        mAvatar.setAvatar(data.getUserPortrait(), 0);
        mName.setText(data.getUserName());
        mAskTime.setText(DateUtil.formatDefaultStyleTime(data.getCreateTime()));
        mQuestionContent.setText(data.getQuestionContext());
        if (data.getSolve() == MissReplyAnswer.QUESTION_SOLVE_STATUS_ALREADY_SOLVE) {
            mAudioRecord.setEnabled(false);
            List<MissReplyAnswer.ReplyVOBean> replyVOBeans = data.getReplyVO();
            if (replyVOBeans != null && !replyVOBeans.isEmpty()) {
                MissReplyAnswer.ReplyVOBean replyVOBean = replyVOBeans.get(0);

                mMissAvatar.setAvatar(replyVOBean.getCustomPortrait(), Question.USER_IDENTITY_HOST);
                mMissName.setText(replyVOBean.getCustomName());
                if (!TextUtils.isEmpty(replyVOBean.getCustomContext())) {
                    mMissReplyAnswer.setAudioPath(replyVOBean.getCustomContext());
                }
            }
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @OnClick({R.id.avatar, R.id.questionRl, R.id.missAvatar, R.id.missName, R.id.play, R.id.clickPlay, R.id.playSubmitRl})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.avatar:
                break;
            case R.id.questionRl:
                break;
            case R.id.missAvatar:
                break;
            case R.id.missName:

                break;
            case R.id.play:
            case R.id.clickPlay:
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
                break;
            case R.id.playSubmitRl:

                break;
        }
    }

    @Override
    public void onRecordAudioFinish(String audioPath, int audioLength) {
        Log.d(TAG, "onRecordAudioFinish: " + audioPath);
        if (!TextUtils.isEmpty(audioPath) && mPlayRl.getVisibility() == View.GONE) {
            mPlayRl.setVisibility(View.VISIBLE);
        }
        if (mMissReplyAnswer != null) {
            mMissReplyAnswer.setAudioPath(audioPath);
        }

    }
}
