package com.sbai.finance.activity.miss;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
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
import com.sbai.finance.model.system.Share;
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
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    private RefreshReceiver mRefreshReceiver;
    private Praise mPraise;
    private String mMongoId;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mAskTime;
    private TextView mQuestion;
    private ImageView mMissAvatar;
    private ImageView mPlayImage;
    private ProgressBar mProgressBar;
    private TextView mSoundTime;
    private TextView mListenerNumber;
    private TextView mPraiseNumber;
    private TextView mRewardNumber;
    private TextView mCommentNumber;
    private TextView mNoComment;
    private ReplyDialogFragment mReplyDialogFragment;
    private static AudioManager mAudioManager;
    private boolean mCancelCollect = false;
    private Timer mTimer = new Timer();
    private TimerTask mTimerTask;

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
                requestShareData();
            }
        });

    }

    private void share(String shareTitle, String shareDescription, String shareUrl, String shareThumbUrl) {
        umengEventCount(UmengCountEventId.MISS_TALK_SHARE);
        ShareDialog.with(getActivity())
                .setTitle(getString(R.string.share_title))
                .setShareTitle(shareTitle)
                .setShareDescription(shareDescription)
                .setShareUrl(shareUrl)
                .setShareThumbUrl(shareThumbUrl)
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

    private void requestShareData() {
        final String shareTitle = getString(R.string.question_share_share_title);
        final String shareDescription = getString(R.string.question_share_description);
        final String shareUrl = String.format(SHARE_URL_QUESTION, mQuestionId);
        Client.requestShareData(Share.SHARE_CODE_QUESTION_ANSWER)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Share>, Share>() {
                    @Override
                    protected void onRespSuccessData(Share data) {
                        share(data.getTitle(), data.getContent(), data.getShareLink(), data.getShareLeUrl());
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        share(shareTitle, shareDescription, shareUrl, null);
                    }
                })
                .fireFree();

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
    public void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
    }

    private void initData(Intent intent) {
        mQuestionDetail = (Question) intent.getSerializableExtra(ExtraKeys.QUESTION);
        mQuestionId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        if (mQuestionDetail != null) {
            mQuestionId = mQuestionDetail.getId();
        }
        mMongoId = intent.getStringExtra(Launcher.EX_PAYLOAD_1);
    }

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_question_detail, null);
        mAvatar = (ImageView) header.findViewById(R.id.avatar);
        mName = (TextView) header.findViewById(R.id.name);
        mAskTime = (TextView) header.findViewById(R.id.askTime);
        mQuestion = (TextView) header.findViewById(R.id.question);
        mMissAvatar = (ImageView) header.findViewById(R.id.missAvatar);
        mPlayImage = (ImageView) header.findViewById(R.id.playImage);
        mProgressBar = (ProgressBar) header.findViewById(R.id.progressBar);
        mSoundTime = (TextView) header.findViewById(R.id.soundTime);
        mListenerNumber = (TextView) header.findViewById(R.id.listenerNumber);
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

        if (MediaPlayerManager.playingId != question.getId() && MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
            mFloatWindow.setVisibility(View.VISIBLE);
            initFloatWindow();
        }

        //item点进来的三种状态的显示  1 播放中 2 暂停 3 停止或者不是播放中的id
        if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING
                && MediaPlayerManager.playingId == question.getId()) {
            mPlayImage.setImageResource(R.drawable.ic_pause);
            startCountDownTime(mSoundTime, question.getSoundTime(), mProgressBar);
        } else if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PAUSE
                && MediaPlayerManager.playingId == question.getId()) {
            mPlayImage.setImageResource(R.drawable.ic_play);
            mProgressBar.setMax(question.getSoundTime() * 1000);
            mProgressBar.setProgress(MediaPlayerManager.getCurrentPosition());
            mSoundTime.setText(getString(R.string._seconds, (question.getSoundTime() * 1000 - MediaPlayerManager.getCurrentPosition()) / 1000));
        } else {
            mPlayImage.setImageResource(R.drawable.ic_play);
            mProgressBar.setMax(0);
            mProgressBar.setProgress(0);
            mSoundTime.setText(getString(R.string._seconds, question.getSoundTime()));
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
                if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_STOP) {
                    playVoice(question, mPlayImage, mProgressBar, mSoundTime, mListenerNumber);
                } else {
                    if (MediaPlayerManager.playingId == question.getId()) {
                        if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PAUSE) {
                            MediaPlayerManager.resume();
                            mPlayImage.setImageResource(R.drawable.ic_pause);
                            startCountDownTime(mSoundTime, question.getSoundTime(), mProgressBar);
                        } else {
                            MediaPlayerManager.pause();
                            mPlayImage.setImageResource(R.drawable.ic_play);
                            stopCountDownTime();
                        }
                    } else {
                        stopPreviousVoice();
                        //关闭上一个语音,开始这个
                        playVoice(question, mPlayImage, mProgressBar, mSoundTime, mListenerNumber);
                    }
                }
            }
        });
    }

    private void initFloatWindow() {
        GlideApp.with(getActivity()).load(MediaPlayerManager.portrait)
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
                intent.putExtra(Launcher.EX_PAYLOAD, MediaPlayerManager.playingId);
                startActivityForResult(intent, REQ_QUESTION_DETAIL);
                umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
            }
        });
    }

    public void stopPreviousVoice() {
        MediaPlayerManager.release();
        if (mFloatWindow != null) {
            mFloatWindow.setVisibility(View.GONE);
        }
    }

    private void playVoice(final Question item, final ImageView playImage,
                           final ProgressBar progressBar, final TextView soundTime, final TextView listenerNumber) {

        if (!MissVoiceRecorder.isHeard(item.getId())) {
            //没听过的
            Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                @Override
                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                    if (resp.isSuccess()) {
                        MissVoiceRecorder.markHeard(item.getId());
                        item.setListenCount(item.getListenCount() + 1);
                        listenerNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                        listenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
                    }
                }
            }).fire();
        }

        final ProgressDialog progressDialog = new ProgressDialog(getActivity());
        progressDialog.setMessage("正在缓冲...");
        progressDialog.show();

        MediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                int result = mAudioManager.requestAudioFocus(afChangeListener,
                        AudioManager.STREAM_MUSIC,
                        AudioManager.AUDIOFOCUS_GAIN);
                if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                    //获取焦点之后开始播放,避免音轨并发
                    MediaPlayerManager.start();
                    playImage.setImageResource(R.drawable.ic_pause);
                    progressDialog.dismiss();
                    startCountDownTime(soundTime, item.getSoundTime(), progressBar);
                    MediaPlayerManager.setPlayingId(item.getId());
                    MediaPlayerManager.setPortrait(item.getCustomPortrait());
                }
            }
        }, new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                playImage.setImageResource(R.drawable.ic_play);
                mFloatWindow.setVisibility(View.GONE);
                MediaPlayerManager.release();
                stopCountDownTime();
                progressBar.setProgress(0);
                soundTime.setText(getString(R.string._seconds, item.getSoundTime()));
                mAudioManager.abandonAudioFocus(afChangeListener);
            }
        });
    }

    private void stopCountDownTime() {
        if (mTimerTask != null) {
            mTimerTask.cancel();
            mTimerTask = null;
        }
    }

    public AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
        public void onAudioFocusChange(int focusChange) {
            if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
                    MediaPlayerManager.pause();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                if (MediaPlayerManager.STATUS != MediaPlayerManager.STATUS_PLAYING) {
                    MediaPlayerManager.start();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
                    MediaPlayerManager.release();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
                if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
                    MediaPlayerManager.release();
                }

            } else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
                if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
                    MediaPlayerManager.release();
                }
            }
        }
    };

    private void startCountDownTime(final TextView sound, final int soundTime, final ProgressBar progressBar) {
        progressBar.setMax(soundTime * 1000);
        mTimerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int position = MediaPlayerManager.getCurrentPosition();
                        sound.setText(getString(R.string._seconds, (soundTime * 1000 - position) / 1000));
                        progressBar.setProgress(position);
                    }
                });

            }
        };
        mTimer.schedule(mTimerTask, 0, 100);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //返回上一页是关闭倒计时任务
        stopCountDownTime();
    }

    private void requestQuestionReplyList(final boolean isRefresh) {
        Client.getQuestionReplyList(mType, mQuestionId, mPage, mPageSize, mMongoId)
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
                            mCancelCollect = true;
                            mCollectImage.setImageResource(R.drawable.ic_miss_uncollect);
                        } else {
                            mCancelCollect = false;
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
        filter.addAction(ACTION_PLAY_FINISH);
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

            if (ACTION_PLAY_FINISH.equalsIgnoreCase(intent.getAction())) {
                requestQuestionDetail();
                mFloatWindow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mQuestionDetail != null) {
            intent.putExtra(ExtraKeys.CANCEL_COLLECT, mCancelCollect);
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
