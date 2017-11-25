package com.sbai.finance.activity.miss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
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

import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
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
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.httplib.ApiError;

import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.net.Client.SHARE_URL_QUESTION;


public class QuestionDetailActivity extends BaseActivity implements AdapterView.OnItemClickListener, MissAudioManager.OnAudioListener {

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
    @BindView(R.id.missFloatWindow)
    MissFloatWindow mMissFloatWindow;

    private int mQuestionId;
    private boolean mIsFromMissTalk;

    private int mType = 1;
    private int mPageSize = 20;
    private int mPage = 0;

    private HashSet<String> mSet;
    private View mFootView;
    private QuestionReplyListAdapter mQuestionReplyListAdapter;
    private Question mQuestion;
    private RefreshReceiver mRefreshReceiver;
    private Praise mPraise;
    private String mReplayMsgId;
    private HasLabelImageLayout mAvatar;
    private TextView mName;
    private TextView mAskTime;
    private TextView mQuestionContent;
    private HasLabelImageLayout mMissAvatar;
    private ImageView mPlayImage;
    private ProgressBar mProgressBar;
    private TextView mSoundTime;
    private TextView mListenerNumber;
    private TextView mPraiseNumber;
    private TextView mRewardNumber;
    private TextView mCommentNumber;
    private TextView mNoComment;
    private ReplyDialogFragment mReplyDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_question_detail);
        ButterKnife.bind(this);

        initData(getIntent());
        mSet = new HashSet<>();

        initHeaderView();

        mQuestionReplyListAdapter = new QuestionReplyListAdapter(this);
        mListView.setAdapter(mQuestionReplyListAdapter);
        mListView.setOnItemClickListener(this);

        requestQuestionDetail();
        requestQuestionReplyList(true);

        initSwipeRefreshLayout();

        registerRefreshReceiver();

        MissAudioManager.get().addAudioListener(this);

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestShareData();
            }
        });
    }

    private void requestShareData() {
        final String shareTitle = getString(R.string.share_title);
        final String shareDescription = getString(R.string.question_share_description);
        final String shareUrl = String.format(SHARE_URL_QUESTION, mQuestionId);
        Client.requestShareData(Share.SHARE_CODE_QUESTION_ANSWER)
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<Share>, Share>() {
                    @Override
                    protected void onRespSuccessData(Share data) {
                        data.setShareLink(data.getShareLink().concat("?questionId=" + mQuestionId));
                        share(data.getTitle(), data.getContent(), data.getShareLink(), data.getShareLeUrl());
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        share(shareTitle, shareDescription, shareUrl, null);
                    }
                })
                .fireFree();
    }

    private void share(String shareTitle, String shareDescription, String shareUrl, String shareThumbUrl) {
        umengEventCount(UmengCountEventId.MISS_TALK_SHARE);

        ShareDialog.with(getActivity())
                .setTitle(getString(R.string.share_title))
                .setShareTitle(getString(R.string.question_share_share_title))
                .setShareDescription(getString(R.string.question_share_description))
                .setShareUrl(String.format(SHARE_URL_QUESTION, mQuestionId))
                .setShareTitle(shareTitle)
                .setShareDescription(shareDescription)
                .setShareUrl(shareUrl)
                .setShareThumbUrl(shareThumbUrl)
                .hasFeedback(false)
                .setListener(new ShareDialog.OnShareDialogCallback() {
                    @Override
                    public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                        Client.share().setTag(TAG).fire();
                        stopQuestionVoice();
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
                        mQuestion = question;
                        updateQuestionDetail();
                    }
                }).fire();
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mPage = 0;
                if (mReplayMsgId != null) {
                    mReplayMsgId = null;
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
        getActivity().unregisterReceiver(mRefreshReceiver);
        MissAudioManager.get().removeAudioListener(this);
    }

    private void initData(Intent intent) {
        mQuestionId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        mReplayMsgId = intent.getStringExtra(Launcher.EX_PAYLOAD_1); // 来消息，把他人的回复置顶
        mIsFromMissTalk = intent.getBooleanExtra(ExtraKeys.IS_FROM_MISS_TALK, false);
    }

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_question_detail, null);
        mAvatar = header.findViewById(R.id.avatar);
        mName = (TextView) header.findViewById(R.id.name);
        mAskTime = (TextView) header.findViewById(R.id.askTime);
        mQuestionContent = (TextView) header.findViewById(R.id.questionContent);
        mMissAvatar = header.findViewById(R.id.missAvatar);
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

    private void updateQuestionDetail() {
        mAvatar.setAvatar(mQuestion.getUserPortrait(), mQuestion.getUserType());
        mMissAvatar.setAvatar(mQuestion.getCustomPortrait(), Question.USER_IDENTITY_HOST);

        mName.setText(mQuestion.getUserName());
        mAskTime.setText(DateUtil.formatDefaultStyleTime(mQuestion.getCreateTime()));
        mQuestionContent.setText(mQuestion.getQuestionContext());
        mListenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(mQuestion.getListenCount())));
        mPraiseNumber.setText(getString(R.string.praise_miss, StrFormatter.getFormatCount(mQuestion.getPriseCount())));
        mRewardNumber.setText(getString(R.string.reward_miss, StrFormatter.getFormatCount(mQuestion.getAwardCount())));

        if (mQuestion.getIsPrise() == 0) {
            mPraiseImage.setImageResource(R.drawable.ic_miss_unpraise);
        } else {
            mPraiseImage.setImageResource(R.drawable.ic_miss_praise);
        }

        if (mQuestion.getCollect() == 0) {
            mCollectImage.setImageResource(R.drawable.ic_miss_uncollect);
        } else {
            mCollectImage.setImageResource(R.drawable.ic_miss_collect);
        }

        if (MissVoiceRecorder.isHeard(mQuestion.getId())) {
            mListenerNumber.setTextColor(ContextCompat.getColor(this, R.color.unluckyText));
        } else {
            mListenerNumber.setTextColor(ContextCompat.getColor(this, R.color.colorPrimary));
        }

        mAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Launcher.with(getActivity(), LookBigPictureActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mQuestion.getUserPortrait())
                        .putExtra(Launcher.EX_PAYLOAD_2, 0)
                        .execute();
            }
        });

        mMissAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopQuestionVoice();
                Launcher.with(QuestionDetailActivity.this, MissProfileDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mQuestion.getAnswerCustomId())
                        .execute();
            }
        });

        mPlayImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleQuestionVoice(mQuestion);
            }
        });

        if (MissAudioManager.get().getAudio() instanceof Question) {
            final Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (MissAudioManager.get().isStarted(playingQuestion)
                    && mQuestion.getId() != playingQuestion.getId()) {
                mMissFloatWindow.setVisibility(View.VISIBLE);
                mMissFloatWindow.setMissAvatar(playingQuestion.getCustomPortrait(),playingQuestion.getUserType());
                mMissFloatWindow.startAnim();
                mMissFloatWindow.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);

                        Launcher.with(getActivity(), QuestionDetailActivity.class)
                                .putExtra(ExtraKeys.IS_FROM_MISS_TALK, true)
                                .putExtra(Launcher.EX_PAYLOAD, playingQuestion.getId())
                                .execute();
                    }
                });
            }
        }

        mProgressBar.setMax(mQuestion.getSoundTime() * 1000);
        if (MissAudioManager.get().isStarted(mQuestion)) {
            setPlayingState();

            startScheduleJob(100);
        } else if (MissAudioManager.get().isPaused(mQuestion)) {
            setPauseState();
        } else {
            setStopState();
        }
    }

    private void setStopState() {
        mPlayImage.setImageResource(R.drawable.ic_play);
        mProgressBar.setProgress(0);
        mSoundTime.setText(getString(R.string._seconds, mQuestion != null ? mQuestion.getSoundTime() : 0));
    }

    private void setPauseState() {
        mPlayImage.setImageResource(R.drawable.ic_play);
        int pastTime = MissAudioManager.get().getCurrentPosition();
        mSoundTime.setText(getString(R.string._seconds, (mQuestion.getSoundTime() * 1000 - pastTime) / 1000));
        mProgressBar.setProgress(pastTime);
    }

    private void setPlayingState() {
        mPlayImage.setImageResource(R.drawable.ic_pause);
        int pastTime = MissAudioManager.get().getCurrentPosition();
        mSoundTime.setText(getString(R.string._seconds, (mQuestion.getSoundTime() * 1000 - pastTime) / 1000));
        mProgressBar.setProgress(pastTime);
    }

    private void toggleQuestionVoice(final Question question) {
        if (MissAudioManager.get().isStarted(question)) {
            MissAudioManager.get().pause();
        } else if (MissAudioManager.get().isPaused(question)) {
            MissAudioManager.get().resume();
            startScheduleJob(100);
        } else {
            updateQuestionListenCount(question);
            MissAudioManager.get().start(question);
        }
    }

    private void updateQuestionListenCount(final Question question) {
        if (!MissVoiceRecorder.isHeard(question.getId())) {
            Client.listen(question.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                @Override
                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                    if (resp.isSuccess()) {
                        MissVoiceRecorder.markHeard(question.getId());
                        question.setListenCount(question.getListenCount() + 1);
                        mListenerNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                        mListenerNumber.setText(getString(R.string.listener_number, StrFormatter.getFormatCount(question.getListenCount())));
                    }

                }

                @Override
                protected void onRespFailure(Resp failedResp) {
                    if (failedResp.getCode() == Resp.CODE_LISTENED) {
                        MissVoiceRecorder.markHeard(question.getId());
                        mListenerNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
                    }
                }
            }).fire();
        }
    }

    @Override
    public void onTimeUp(int count) {
        setPlayingState();
    }

    public void stopQuestionVoice() {
        MissAudioManager.get().stop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Preference.get().isForeground()) {
            stopQuestionVoice();
        }
    }

    private void requestQuestionReplyList(final boolean isRefresh) {
        Client.getQuestionReplyList(mType, mQuestionId, mPage, mPageSize, mReplayMsgId)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<QuestionReply>, QuestionReply>() {
                    @Override
                    protected void onRespSuccessData(QuestionReply questionReply) {
                        if (questionReply.getData() != null) {
                            updateQuestionReplyList(questionReply.getData(), questionReply.getResultCount(), isRefresh);
                        }
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
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
        if (mQuestion != null) {
            if (LocalUser.getUser().isLogin()) {
                umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                Client.praise(mQuestion.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {

                    @Override
                    protected void onRespSuccessData(Praise praise) {
                        mPraise = praise;
                        if (praise.getIsPrise() == 0) {
                            mPraiseImage.setImageResource(R.drawable.ic_miss_unpraise);
                        } else {
                            mPraiseImage.setImageResource(R.drawable.ic_miss_praise);
                        }

                        mQuestion.setIsPrise(praise.getIsPrise());
                        mQuestion.setPriseCount(praise.getPriseCount());
                        mPraiseNumber.setText(getString(R.string.praise_miss,
                                StrFormatter.getFormatCount(mQuestion.getPriseCount())));
                    }
                }).fire();
            } else {
                stopQuestionVoice();
                Launcher.with(getActivity(), LoginActivity.class).execute();
            }
        }
    }

    private void collect() {
        if (mQuestion != null) {
            if (LocalUser.getUser().isLogin()) {
                Client.collectQuestion(mQuestion.getId()).setCallback(new Callback2D<Resp<QuestionCollect>, QuestionCollect>() {
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
                stopQuestionVoice();
                Launcher.with(getActivity(), LoginActivity.class).execute();
            }
        }
    }

    private void comment() {
        if (mQuestion != null) {
            if (LocalUser.getUser().isLogin()) {
                Launcher.with(getActivity(), CommentActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mQuestion.getQuestionUserId())
                        .putExtra(Launcher.EX_PAYLOAD_1, mQuestion.getId())
                        .executeForResult(CommentActivity.REQ_CODE_COMMENT);

            } else {
                stopQuestionVoice();
                Intent intent = new Intent(getActivity(), LoginActivity.class);
                startActivityForResult(intent, CommentActivity.REQ_CODE_COMMENT_LOGIN);
            }
        }
    }

    private void reward() {
        if (mQuestion != null) {
            if (LocalUser.getUser().isLogin()) {
                RewardMissActivity.show(getActivity(), mQuestionId, RewardInfo.TYPE_QUESTION);
            } else {
                stopQuestionVoice();
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
                mReplyDialogFragment = ReplyDialogFragment.newInstance(item);
            }
            if (!mReplyDialogFragment.isAdded()) {
                mReplyDialogFragment.show(getSupportFragmentManager());
            }

            mReplyDialogFragment.setCallback(new ReplyDialogFragment.Callback() {
                                                 @Override
                                                 public void onLoginSuccess() {
                                                     stopQuestionVoice();
                                                 }
                                             }
            );
        }
    }

    @Override
    public void onAudioStart() {
        if (MissAudioManager.get().getAudio() instanceof Question) {
            Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (playingQuestion.getId() == mQuestionId) {
                setPlayingState();
                mMissFloatWindow.setVisibility(View.GONE);
            } else {
                mMissFloatWindow.setMissAvatar(playingQuestion.getCustomPortrait(), playingQuestion.getUserType());
                mMissFloatWindow.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onAudioPlay() {
        if (MissAudioManager.get().getAudio() instanceof Question) {
            Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (playingQuestion.getId() == mQuestionId) {
                startScheduleJob(100);
            } else {
                mMissFloatWindow.startAnim();
            }
        }
    }

    @Override
    public void onAudioPause() {
        if (MissAudioManager.get().getAudio() instanceof Question) {
            Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (playingQuestion.getId() == mQuestionId) {
                setPauseState();
                stopScheduleJob();
            } else {
                mMissFloatWindow.stopAnim();
                mMissFloatWindow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAudioResume() {
        if (MissAudioManager.get().getAudio() instanceof Question) {
            Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (playingQuestion.getId() == mQuestionId) {
                setPlayingState();
                startScheduleJob(100);
            } else {
                mMissFloatWindow.setVisibility(View.VISIBLE);
                mMissFloatWindow.startAnim();
            }
        }
    }

    @Override
    public void onAudioStop() {
        if (MissAudioManager.get().getAudio() instanceof Question) {
            Question playingQuestion = (Question) MissAudioManager.get().getAudio();
            if (playingQuestion.getId() == mQuestionId) {
                setStopState();
                stopScheduleJob();
            } else {
                mMissFloatWindow.stopAnim();
                mMissFloatWindow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onAudioError() {
        ToastUtil.show(R.string.play_failure);
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
            HasLabelImageLayout mAvatar;
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
            @BindView(R.id.reviewPriceCount)
            TextView mReviewPriceCount;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final QuestionReply.DataBean item) {
                if (item == null) return;

                QuestionReply.DataBean.UserModelBean userModelBean = item.getUserModel();
                if (userModelBean != null) {

                    int userIdentity = userModelBean.getCustomId() != 0 ? Question.USER_IDENTITY_HOST : Question.USER_IDENTITY_ORDINARY;

                    mAvatar.setAvatar(userModelBean.getUserPortrait(), userIdentity);

                    mUserName.setText(item.getUserModel().getUserName());

                    mAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, item.getUserModel().getUserPortrait())
                                    .putExtra(Launcher.EX_PAYLOAD_2, 0)
                                    .execute();
                        }
                    });
                } else {
                    mAvatar.setAvatar("", 0);
                    mUserName.setText("");

                    mAvatar.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, "")
                                    .putExtra(Launcher.EX_PAYLOAD_2, 0)
                                    .execute();
                        }
                    });
                }

                mReviewPriceCount.setText(String.valueOf(item.getReviewPriseCount()));
                if (item.getReviewPriseStatus() == QuestionReply.DataBean.QUESTION_REVIEW_PRISE) {
                    mReviewPriceCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
                } else {
                    mReviewPriceCount.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
                }

                mPublishTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateDate()));
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
        if (requestCode == CommentActivity.REQ_CODE_COMMENT && resultCode == RESULT_OK) {
            mSet.clear();
            mPage = 0;
            mReplayMsgId = null;
            mSwipeRefreshLayout.setLoadMoreEnable(true);
            mListView.removeFooterView(mFootView);
            mFootView = null;
            requestQuestionDetail();
            requestQuestionReplyList(true);
            mListView.setSelection(0);
        }

        if (requestCode == CommentActivity.REQ_CODE_COMMENT_LOGIN && resultCode == RESULT_OK) {
            if (mQuestion != null) {
                Launcher.with(getActivity(), CommentActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mQuestion.getQuestionUserId())
                        .putExtra(Launcher.EX_PAYLOAD_1, mQuestion.getId())
                        .executeForResult(CommentActivity.REQ_CODE_COMMENT);
            }
        }
    }

    private void registerRefreshReceiver() {
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REWARD_SUCCESS);
        filter.addAction(ACTION_LOGIN_SUCCESS);
        filter.addAction(CommentActivity.BROADCAST_ACTION_REPLY_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
        registerReceiver(mRefreshReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {

            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (mQuestion != null) {
                    int rewardCount = mQuestion.getAwardCount() + 1;
                    mQuestion.setAwardCount(rewardCount);
                    mRewardNumber.setText(getString(R.string.reward_miss, StrFormatter.getFormatCount(rewardCount)));
                }
            }

            if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mPage = 0;
                mReplayMsgId = null;
                mListView.removeFooterView(mFootView);
                mFootView = null;
                requestQuestionReplyList(true);
            }

            if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(intent.getAction())) {
                stopQuestionVoice();
            }

            if (CommentActivity.BROADCAST_ACTION_REPLY_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mPage = 0;
                mReplayMsgId = null;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                mListView.removeFooterView(mFootView);
                mFootView = null;
                requestQuestionDetail();
                requestQuestionReplyList(true);
                mListView.setSelection(0);
            }
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        if (mQuestion != null) {
            intent.putExtra(ExtraKeys.QUESTION, mQuestion);
            intent.putExtra(ExtraKeys.PRAISE, mPraise);
            setResult(RESULT_OK, intent);
        }

        if (mIsFromMissTalk) {
            stopScheduleJob();
        } else {
            stopQuestionVoice();
        }

        super.onBackPressed();
    }
}
