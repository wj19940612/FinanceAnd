package com.sbai.finance.activity.miss;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MissAudioReplyActivity extends BaseActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_audio_reply);
        ButterKnife.bind(this);
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
                break;
            case R.id.clickPlay:
                break;
            case R.id.playSubmitRl:
                break;
        }
    }
}
