package com.sbai.finance.activity.miss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.fragment.dialog.ReplyDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.QuestionCollect;
import com.sbai.finance.model.miss.QuestionReply;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MediaPlayerManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.glide.GlideApp;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.listenerNumber;
import static com.sbai.finance.R.id.playImage;
import static com.sbai.finance.R.id.progressBar;
import static com.sbai.finance.activity.miss.ReplyActivity.ACTION_REPLY_SUCCESS;
import static com.sbai.finance.net.Client.SHARE_URL_QUESTION;


public class QuestionDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    private static final int REQ_COMMENT = 1001;
    private static final int REQ_COMMENT_LOGIN = 1002;
    private static final int REQ_REWARD_LOGIN = 1003;

    public static final int REQ_CODE_QUESTION_DETAIL = 5555;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.commentArea)
    RelativeLayout mCommentArea;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.praise)
    LinearLayout mPraiseArea;
    @BindView(R.id.praiseImage)
    ImageView mPraiseImage;
    @BindView(R.id.collect)
    LinearLayout mCollect;
    @BindView(R.id.collectImage)
    ImageView mCollectImage;
    @BindView(R.id.comment)
    LinearLayout mComment;
    @BindView(R.id.reward)
    LinearLayout mReward;
    @BindView(R.id.missAvatar)
    ImageView mMissAvatarPlaying;
    @BindView(R.id.VoiceAnimator)
    ImageView mVoiceAnimator;
    @BindView(R.id.floatWindow)
    LinearLayout mFloatWindow;

    private int mQuestionId;
    private int mType = 1;
    private int mPageSize = 20;
    private int mPage = 0;
    private HashSet<String> mSet;
    private View mFootView;
    private QuestionReplyListAdapter mQuestionReplyListAdapter;
    private Question mQuestionDetail;
    private Question mPlayingItem;
    private RefreshReceiver mRefreshReceiver;
    private int mPlayingID;
    private Praise mPraise;
    private String mMongoId;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mAskTime;
    private TextView mQuestion;
    private ImageView mMissAvatar;
    private ImageView mPlayImage;
    private ProgressBar mProgressBar;
    private TextView mVoiceTime;
    private TextView mListenerNumber;
    private TextView mPraiseNumber;
    private TextView mRewardNumber;
    private TextView mCommentNumber;
    private TextView mNoComment;
    private ReplyDialogFragment mReplyDialogFragment;
    private CountDownTimer mCountDownTimer;
    private static AudioManager mAudioManager;
    private boolean mIsPause;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        ButterKnife.bind(this);

        initData(getIntent());
        mSet = new HashSet<>();
        mAudioManager = (AudioManager) getActivity().getSystemService(AUDIO_SERVICE);
        initHeaderView();
        mQuestionReplyListAdapter = new QuestionReplyListAdapter(this);
        mListView.setAdapter(mQuestionReplyListAdapter);
        mListView.setOnItemClickListener(this);
        mListView.setFocusable(false);

        if (mQuestionDetail != null) {
            updateQuestionDetail(mQuestionDetail);
        } else {
            requestQuestionDetail();
        }
        requestQuestionReplyList(true);
        initSwipeRefreshLayout();
        registerRefreshReceiver();

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void share() {
        umengEventCount(UmengCountEventId.MISS_TALK_SHARE);
        ShareDialog.with(getActivity())
                .setTitle(getString(R.string.share_title))
                .setShareTitle(getString(R.string.question_share_share_title))
                .setShareDescription(getString(R.string.question_share_description))
                .setShareUrl(String.format(SHARE_URL_QUESTION, mQuestionId))
                .hasFeedback(false)
                .setListener(new ShareDialog.OnShareDialogCallback() {
                    @Override
                    public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                        Client.share().setTag(TAG).fire();
                        switch (platform) {
                            case SINA_WEIBO:
                                umengEventCount(UmengCountEventId.MISS_TALK_SHARE_WEIBO);
                                break;
                            case WECHAT_FRIEND:
                                umengEventCount(UmengCountEventId.MISS_TALK_SHARE_FRIEND);
                                break;
                            case WECHAT_CIRCLE:
                                umengEventCount(UmengCountEventId.MISS_TALK_SHARE_CIRCLE);
                                break;
                        }
                    }

                    @Override
                    public void onFeedbackClick(View view) {
                    }
                }).show();
    }

    private void requestQuestionDetail() {
        Client.getQuestionDetails(mQuestionId).setTag(TAG)
                .setCallback(new Callback2D<Resp<Question>, Question>() {
                    @Override
                    protected void onRespSuccessData(Question question) {
                        updateQuestionDetail(question);
                        mQuestionDetail = question;
                    }
                }).fire();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                if (mMongoId != null) {
                    mMongoId = null;
                }
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                mListView.removeFooterView(mFootView);
                mFootView = null;
                requestQuestionDetail();
                requestQuestionReplyList(true);

                //关掉语音和语音动画
                MediaPlayerManager.release();
                mPlayingID = -1;
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestQuestionReplyList(false);
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        //mMediaPlayerManager.release();
        mPlayingID = -1;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
    }

    private void initData(Intent intent) {
        mQuestionDetail = (Question) intent.getSerializableExtra(ExtraKeys.QUESTION);
        mPlayingItem = (Question) intent.getSerializableExtra(ExtraKeys.PLAYING_ITEM);
        mQuestionId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        mMongoId = intent.getStringExtra(Launcher.EX_PAYLOAD_1);
    }

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_question_detail, null);
        mAvatar = (ImageView) header.findViewById(R.id.avatar);
        mName = (TextView) header.findViewById(R.id.name);
        mAskTime = (TextView) header.findViewById(R.id.askTime);
        mQuestion = (TextView) header.findViewById(R.id.question);
        mMissAvatar = (ImageView) header.findViewById(R.id.missAvatar);
        mPlayImage = (ImageView) header.findViewById(playImage);
        mProgressBar = (ProgressBar) header.findViewById(progressBar);
        mVoiceTime = (TextView) header.findViewById(R.id.voiceTime);
        mListenerNumber = (TextView) header.findViewById(listenerNumber);
        mPraiseNumber = (TextView) header.findViewById(R.id.praiseNumber);
        mRewardNumber = (TextView) header.findViewById(R.id.rewardNumber);
        mCommentNumber = (TextView) header.findViewById(R.id.commentNumber);
        mNoComment = (TextView) header.findViewById(R.id.noComment);
        mListView.addHeaderView(header);
    }

    private void updateQuestionDetail(final Question question) {
        GlideApp.with(this).load(question.getUserPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mAvatar);

        GlideApp.with(this).load(question.getCustomPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mMissAvatar);

        mName.setText(question.getUserName());
        mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(question.getCreateTime()));
        mQuestion.setText(question.getQuestionContext());
        mListenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(question.getListenCount())));
        mPraiseNumber.setText(getString(R.string.praise_miss, StrFormatter.getFormatCount(question.getPriseCount())));
        mRewardNumber.setText(getString(R.string.reward_miss, StrFormatter.getFormatCount(question.getAwardCount())));

        if (question.getIsPrise() == 0) {
            mPraiseImage.setImageResource(R.drawable.ic_miss_unpraise);
        } else {
            mPraiseImage.setImageResource(R.drawable.ic_miss_praise);
        }

        if (question.getCollect() == 0) {
            mCollectImage.setImageResource(R.drawable.ic_miss_uncollect);
        } else {
            mCollectImage.setImageResource(R.drawable.ic_miss_collect);
        }

        if (MissVoiceRecorder.isHeard(question.getId())) {
            mListenerNumber.setTextColor(ContextCompat.getColor(this, R.color.unluckyText));
        } else {
            mListenerNumber.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        if (question.isPlaying()) {
            mPlayImage.setImageResource(R.drawable.ic_pause);
        } else {
            mPlayImage.setImageResource(R.drawable.ic_play);
        }

        if (question.isProgressIsZero()) {
            mProgressBar.setMax(0);
            mProgressBar.setProgress(0);
        } else {
            mProgressBar.setMax(MediaPlayerManager.getDuration());
            mProgressBar.setProgress(MediaPlayerManager.getCurrentPosition());
            setProgressBar(mProgressBar);
        }

        if (question.isPause()) {
            mVoiceTime.setText(getString(R.string.voice_time, (MediaPlayerManager.getDuration() - MediaPlayerManager.getCurrentPosition()) / 1000));
        } else {
            mVoiceTime.setText(getString(R.string.voice_time, question.getSoundTime()));
        }

        if (question.isCountDown()) {
            setCountDownTime(mVoiceTime, MediaPlayerManager.getDuration() - MediaPlayerManager.getCurrentPosition());
        }

        if (mPlayingItem != null && mQuestionDetail.getId() != mPlayingItem.getId()) {
            if (MediaPlayerManager.isPlaying()) {
                mFloatWindow.setVisibility(View.VISIBLE);

                GlideApp.with(getActivity()).load(mPlayingItem.getCustomPortrait())//设置悬浮窗小姐姐头像
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mMissAvatarPlaying);

                mVoiceAnimator.setBackgroundResource(R.drawable.bg_miss_voice_float);
                AnimationDrawable animation = (AnimationDrawable) mVoiceAnimator.getBackground();
                animation.start();

                mFloatWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                        intent.putExtra(ExtraKeys.QUESTION, mPlayingItem);
                        startActivityForResult(intent, REQ_QUESTION_DETAIL);
                        umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
                    }
                });
            }
        }

        mMissAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(QuestionDetailActivity.this, MissProfileActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, question.getAnswerCustomId())
                        .execute();
            }
        });

        mPlayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mQuestionDetail.isPause()) {
                    MediaPlayerManager.resume();
                    setCountDownTime(mVoiceTime, MediaPlayerManager.getDuration() - MediaPlayerManager.getCurrentPosition());
                    mQuestionDetail.setPlaying(true);
                    mQuestionDetail.setPause(false);
                    mPlayImage.setImageResource(R.drawable.ic_pause);
                    setProgressBar(mProgressBar);
                }

                if (!MissVoiceRecorder.isHeard(mQuestionDetail.getId())) {
                    //没听过的
                    Client.listen(mQuestionDetail.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                            if (resp.isSuccess()) {

                                MediaPlayerManager.play(mQuestionDetail.getAnswerContext(), new MediaPlayer.OnPreparedListener() {
                                    @Override
                                    public void onPrepared(MediaPlayer mp) {
                                        //准备好了
                                        int result = mAudioManager.requestAudioFocus(afChangeListener,
                                                AudioManager.STREAM_MUSIC,
                                                AudioManager.AUDIOFOCUS_GAIN);
                                        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                                            //获取焦点之后开始播放,避免音轨并发
                                            MediaPlayerManager.start();
                                        }
                                        MediaPlayerManager.start();
                                        MissVoiceRecorder.markHeard(mQuestionDetail.getId());
                                        setProgressBar(mProgressBar);
                                        setCountDownTime(mVoiceTime, MediaPlayerManager.getDuration());
                                        mQuestionDetail.setProgressIsZero(false);

                                    }
                                }, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        //播放结束
                                        //播放结束
                                        mQuestionDetail.setPlaying(false);
                                        mQuestionDetail.setProgressIsZero(true);
                                        mPlayImage.setImageResource(R.drawable.ic_play);
                                        mProgressBar.setProgress(0);
                                        mFloatWindow.setVisibility(View.GONE);
                                        mPlayingID = -1;
                                        mIsPause = false;
                                        stopCountDownTime();
                                        mVoiceTime.setText(getString(R.string._seconds, mQuestionDetail.getSoundTime()));
                                        mAudioManager.abandonAudioFocus(afChangeListener);
                                    }
                                });

                                mQuestionDetail.setPlaying(true);
                                mPlayImage.setImageResource(R.drawable.ic_pause);
                                mQuestionDetail.setListenCount(mQuestionDetail.getListenCount() + 1);
                                mListenerNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                                mListenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(mQuestionDetail.getListenCount())));
                                mPlayingID = mQuestionDetail.getId();
                            }
                        }
                    }).fire();
                } else {
                    //听过的
                    if (mPlayingID == mQuestionDetail.getId()) {
                        if (mQuestionDetail.isPause()) {
                            MediaPlayerManager.resume();
                            setCountDownTime(mVoiceTime, MediaPlayerManager.getDuration() - MediaPlayerManager.getCurrentPosition());
                            mQuestionDetail.setPlaying(true);
                            mQuestionDetail.setPause(false);
                            mPlayImage.setImageResource(R.drawable.ic_pause);
                            setProgressBar(mProgressBar);
                        } else {
                            stopCountDownTime();
                            MediaPlayerManager.pause();
                            mQuestionDetail.setPlaying(false);
                            mQuestionDetail.setProgressIsZero(false);
                            mQuestionDetail.setPause(true);
                            mPlayImage.setImageResource(R.drawable.ic_play);
                        }
                    } else {

                        MediaPlayerManager.play(mQuestionDetail.getAnswerContext(), new MediaPlayer.OnPreparedListener() {
                            @Override
                            public void onPrepared(MediaPlayer mp) {
                                //准备好了
                                int result = mAudioManager.requestAudioFocus(afChangeListener,
                                        AudioManager.STREAM_MUSIC,
                                        AudioManager.AUDIOFOCUS_GAIN);
                                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                                    //获取焦点之后开始播放,避免音轨并发
                                    MediaPlayerManager.start();
                                    setProgressBar(mProgressBar);
                                    setCountDownTime(mVoiceTime, MediaPlayerManager.getDuration());
                                    mQuestionDetail.setProgressIsZero(false);
                                }
                            }
                        }, new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                //播放结束
                                mQuestionDetail.setPlaying(false);
                                mQuestionDetail.setProgressIsZero(true);
                                mQuestionDetail.setPause(false);
                                mPlayImage.setImageResource(R.drawable.ic_play);
                                mProgressBar.setProgress(0);
                                mFloatWindow.setVisibility(View.GONE);
                                mPlayingID = -1;
                                mIsPause = false;
                                stopCountDownTime();
                                mVoiceTime.setText(getString(R.string._seconds, mQuestionDetail.getSoundTime()));
                                mAudioManager.abandonAudioFocus(afChangeListener);
                            }
                        });
                        mQuestionDetail.setPlaying(true);
                        mPlayImage.setImageResource(R.drawable.ic_pause);
                        mPlayingID = mQuestionDetail.getId();
                    }
                }
            }
        });
    }

    private void stopCountDownTime() {
        if (mCountDownTimer != null) {
            mCountDownTimer.cancel();
            mCountDownTimer = null;
        }
    }

    public void setProgressBar(final ProgressBar progressBar) {
        progressBar.setMax(MediaPlayerManager.getDuration());
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    while (MediaPlayerManager.isPlaying()) {
                        progressBar.setProgress(MediaPlayerManager.getCurrentPosition());
                        try {
                            // 每100毫秒更新一次位置
                            Thread.sleep(100);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (IllegalStateException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private void setCountDownTime(final TextView sound, final int soundTime) {
        mCountDownTimer = new CountDownTimer(soundTime + 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sound.setText(getString(R.string._seconds, millisUntilFinished / 1000 - 1));
            }

            @Override
            public void onFinish() {
                mPlayImage.setImageResource(R.drawable.ic_play);
                mProgressBar.setProgress(0);
                mVoiceTime.setText(getString(R.string._seconds, mQuestionDetail.getSoundTime()));
                if (mFloatWindow != null) {
                    mFloatWindow.setVisibility(View.GONE);
                }
            }
        }.start();
    }

    public AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.pause();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (!MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.start();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.release();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.release();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                if (MediaPlayerManager.isPlaying()) {
                    MediaPlayerManager.release();
                }
            }
        }
    };

    private void requestQuestionReplyList(final boolean isRefresh) {
        Client.getQuestionReplyList(mType, mQuestionId, mPage, mPageSize, mMongoId != null ? mMongoId : null)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<QuestionReply>, QuestionReply>() {
                    @Override
                    protected void onRespSuccessData(QuestionReply questionReply) {
                        if (questionReply.getData() != null) {
                            updateQuestionReplyList(questionReply.getData(), questionReply.getResultCount(), isRefresh);
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    private void updateQuestionReplyList(List<QuestionReply.DataBean> questionReplyList, int resultCount, boolean isRefresh) {
        mCommentNumber.setText(getString(R.string.comment_number_string, StrFormatter.getFormatCount(resultCount)));
        if (resultCount > 0) {
            mNoComment.setVisibility(View.GONE);
            mCommentArea.setBackgroundColor(ContextCompat.getColor(this, R.color.background));
        } else {
            mNoComment.setVisibility(View.VISIBLE);
            mCommentArea.setBackgroundColor(Color.WHITE);
        }

        if (questionReplyList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mPage++;
        }

        if (questionReplyList.size() < mPageSize && mPage > 0) {
            if (mFootView == null) {
                mFootView = View.inflate(getActivity(), R.layout.view_footer_load_complete, null);
                mListView.addFooterView(mFootView, null, true);
            }
        }

        if (isRefresh) {
            if (mQuestionReplyListAdapter != null) {
                mQuestionReplyListAdapter.clear();
            }
        }
        stopRefreshAnimation();

        for (QuestionReply.DataBean questionReply : questionReplyList) {
            if (questionReply != null) {
                if (mSet.add(questionReply.getId())) {
                    mQuestionReplyListAdapter.add(questionReply);
                }
            }
        }
    }

    @OnClick({R.id.praise, R.id.collect, R.id.comment, R.id.reward})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.praise:
                praise();
                break;
            case R.id.collect:
                collect();
                break;
            case R.id.comment:
                comment();
                break;
            case R.id.reward:
                reward();
                break;
        }
    }

    private void praise() {
        if (mQuestionDetail != null) {
            if (LocalUser.getUser().isLogin()) {
                umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                Client.praise(mQuestionDetail.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {

                    @Override
                    protected void onRespSuccessData(Praise praise) {
                        mPraise = praise;
                        int praiseCount;
                        if (praise.getIsPrise() == 0) {
                            mPraiseImage.setImageResource(R.drawable.ic_miss_unpraise);
                            praiseCount = mQuestionDetail.getPriseCount() - 1;
                            mQuestionDetail.setPriseCount(praiseCount);
                        } else {
                            mPraiseImage.setImageResource(R.drawable.ic_miss_praise);
                            praiseCount = mQuestionDetail.getPriseCount() + 1;
                            mQuestionDetail.setPriseCount(praiseCount);
                        }
                        mPraiseNumber.setText(getString(R.string.praise_miss, StrFormatter.getFormatCount(praiseCount)));
                    }
                }).fire();
            } else {
                Launcher.with(getActivity(), LoginActivity.class).execute();
            }
        }
    }

    private void collect() {
        if (mQuestionDetail != null) {
            if (LocalUser.getUser().isLogin()) {
                Client.collectQuestion(mQuestionDetail.getId()).setCallback(new Callback2D<Resp<QuestionCollect>, QuestionCollect>() {
                    @Override
                    protected void onRespSuccessData(QuestionCollect questionCollect) {
                        if (questionCollect.getCollect() == 0) {
                            mCollectImage.setImageResource(R.drawable.ic_miss_uncollect);
                        } else {
                            mCollectImage.setImageResource(R.drawable.ic_miss_collect);
                        }
                    }
                }).fire();
            } else {
                Launcher.with(getActivity(), LoginActivity.class).execute();
            }
        }
    }

    private void comment() {
        if (mQuestionDetail != null) {
            if (LocalUser.getUser().isLogin()) {
                Launcher.with(getActivity(), CommentActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mQuestionDetail.getQuestionUserId())
                        .putExtra(Launcher.EX_PAYLOAD_1, mQuestionDetail.getId())
                        .executeForResult(REQ_COMMENT);

            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, REQ_COMMENT_LOGIN);
            }
        }
    }

    private void reward() {
        if (mQuestionDetail != null) {
            if (LocalUser.getUser().isLogin()) {
                RewardMissActivity.show(getActivity(), mQuestionId, RewardInfo.TYPE_QUESTION);
            } else {
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, REQ_REWARD_LOGIN);
            }
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        QuestionReply.DataBean item = (QuestionReply.DataBean) parent.getItemAtPosition(position);
        if (item != null) {
            if (mReplyDialogFragment == null) {
                mReplyDialogFragment = ReplyDialogFragment.newInstance();
            }
            mReplyDialogFragment.setItemData(item);
            if (!mReplyDialogFragment.isAdded()) {
                mReplyDialogFragment.show(getSupportFragmentManager());
            }

        }
    }

    static class QuestionReplyListAdapter extends ArrayAdapter<QuestionReply.DataBean> {
        private Context mContext;

        private QuestionReplyListAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_question_reply, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.bindingData(mContext, getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.opinionContent)
            TextView mOpinionContent;
            @BindView(R.id.replyName)
            TextView mReplyName;
            @BindView(R.id.replyContent)
            TextView mReplyContent;
            @BindView(R.id.replyArea)
            LinearLayout mReplyArea;
            @BindView(R.id.publishTime)
            TextView mPublishTime;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(Context context, QuestionReply.DataBean item) {
                if (item == null) return;

                if (item.getUserModel() != null) {
                    GlideApp.with(context).load(item.getUserModel().getUserPortrait())
                            .placeholder(R.drawable.ic_default_avatar)
                            .circleCrop()
                            .into(mAvatar);
                    mUserName.setText(item.getUserModel().getUserName());
                } else {
                    GlideApp.with(context).load(R.drawable.ic_default_avatar)
                            .circleCrop()
                            .into(mAvatar);
                    mUserName.setText("");
                }

                mPublishTime.setText(DateUtil.getMissFormatTime(item.getCreateDate()));
                mOpinionContent.setText(item.getContent());

                if (item.getReplys() != null) {
                    mReplyArea.setVisibility(View.VISIBLE);
                    if (item.getReplys().size() == 0) {
                        mReplyArea.setVisibility(View.GONE);
                    } else {
                        mReplyArea.setVisibility(View.VISIBLE);
                        if (item.getReplys().get(0) != null) {
                            if (item.getReplys().get(0).getUserModel() != null) {
                                mReplyName.setText(context.getString(R.string.reply_name, item.getReplys().get(0).getUserModel().getUserName()));
                            } else {
                                mReplyName.setText("");
                            }
                            mReplyContent.setText(item.getReplys().get(0).getContent());
                        } else {
                            mReplyName.setText("");
                            mReplyContent.setText("");
                        }
                    }
                } else {
                    mReplyArea.setVisibility(View.GONE);
                }
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_COMMENT && resultCode == RESULT_OK) {
            mSet.clear();
            mPage = 0;
            mMongoId = null;
            mSwipeRefreshLayout.setLoadMoreEnable(true);
            mListView.removeFooterView(mFootView);
            mFootView = null;
            requestQuestionDetail();
            requestQuestionReplyList(true);
            mListView.setSelection(0);
        }

        if (requestCode == REQ_COMMENT_LOGIN && resultCode == RESULT_OK) {
            if (mQuestionDetail != null) {
                Launcher.with(getActivity(), CommentActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mQuestionDetail.getQuestionUserId())
                        .putExtra(Launcher.EX_PAYLOAD_1, mQuestionDetail.getId())
                        .executeForResult(REQ_COMMENT);
            }
        }
    }

    private void registerRefreshReceiver() {
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REPLY_SUCCESS);
        filter.addAction(ACTION_REWARD_SUCCESS);
        filter.addAction(ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_REPLY_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mPage = 0;
                mMongoId = null;
                mListView.removeFooterView(mFootView);
                mFootView = null;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestQuestionDetail();
                requestQuestionReplyList(true);
                mListView.setSelection(0);
            }

            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (mQuestionDetail != null) {
                    int rewardCount = mQuestionDetail.getAwardCount() + 1;
                    mQuestionDetail.setAwardCount(rewardCount);
                    mRewardNumber.setText(getString(R.string.reward_miss, StrFormatter.getFormatCount(rewardCount)));
                }
            }

            if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mPage = 0;
                mMongoId = null;
                mListView.removeFooterView(mFootView);
                mFootView = null;
                requestQuestionDetail();
                requestQuestionReplyList(true);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mQuestionDetail != null) {
            intent.putExtra(ExtraKeys.QUESTION_ID, mQuestionDetail.getId());
            intent.putExtra(Launcher.EX_PAYLOAD, mPraise);
            intent.putExtra(Launcher.EX_PAYLOAD_1, mQuestionDetail.getReplyCount());
            intent.putExtra(Launcher.EX_PAYLOAD_2, mQuestionDetail.getAwardCount());
            intent.putExtra(Launcher.EX_PAYLOAD_3, mQuestionDetail.getListenCount());
            setResult(RESULT_OK, intent);
        }
        super.onBackPressed();
    }
}
