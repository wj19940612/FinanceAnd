package com.sbai.finance.activity.miss;

import android.animation.ArgbEvaluator;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.training.LookBigPictureActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Attention;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;
import com.sbai.httplib.ApiError;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.R.id.playImage;

/**
 * 小姐姐详细资料页面
 */
public class MissProfileActivity extends BaseActivity implements
        AdapterView.OnItemClickListener, View.OnClickListener, MissAudioManager.OnAudioListener {

    private static final int REQ_SUBMIT_QUESTION_LOGIN = 1001;
    private static final int REQ_MISS_REWARD_LOGIN = 1002;

    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.titleBar)
    TitleBar mTitleBar;

    private HerAnswerAdapter mHerAnswerAdapter;
    private Long mCreateTime;
    private int mPageSize = 20;
    private HashSet<Integer> mSet;
    private int mCustomId;
    private List<Question> mHerAnswerList;
    private Miss mMiss;
    private RefreshReceiver mRefreshReceiver;
    private ImageView mAvatar;
    private TextView mName;
    private TextView mVoiceTime;
    private TextView mPraiseNumber;
    private TextView mIntroduce;
    private LinearLayout mAttention;
    private ImageView mAttentionImage;
    private TextView mAttentionNumber;
    private TextView mRewardNumber;
    private LinearLayout mVoiceIntroduce;
    private View mVoiceLevel;
    private TextView mAttentionText;
    private LinearLayout mReward;
    private LinearLayout mEmpty;
    private ArgbEvaluator mEvaluator;
    private Question mPlayIngItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_miss_profile);
        ButterKnife.bind(this);

        translucentStatusBar();
        initData(getIntent());
        initHeaderView();

        mSet = new HashSet<>();
        mEvaluator = new ArgbEvaluator();
        mHerAnswerList = new ArrayList<>();
        mHerAnswerAdapter = new HerAnswerAdapter(this);
        mListView.setAdapter(mHerAnswerAdapter);
        mListView.setOnItemClickListener(this);
        mSwipeRefreshLayout.setProgressViewEndTarget(false, (int) Display.dp2Px(100, getResources()));

        requestMissDetail();
        requestHerAnswerList(true);
        initSwipeRefreshLayout();
        registerRefreshReceiver();
        MissAudioManager.get().stop();
        MissAudioManager.get().addAudioListener(this);

        mSwipeRefreshLayout.setOnScrollListener(new CustomSwipeRefreshLayout.OnScrollListener() {
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (mTitleBar != null) {
                    int bgColor = 0X0affffff;
                    float alpha = 0;
                    if (getListScrollY() < 0) {
                        bgColor = 0X0aFFFFFF;
                        alpha = 0;
                    } else if (getListScrollY() > 300) {
                        bgColor = 0XFF55ADFF;
                        alpha = 1;
                    } else {
                        bgColor = (int) mEvaluator.evaluate(getListScrollY() / 300.f, 0X03aFFFFF, 0XFF55ADFF);
                        alpha = getListScrollY() / 300.f;
                    }
                    mTitleBar.setBackgroundColor(bgColor);
                    mTitleBar.setTitleAlpha(alpha);
                }
            }

            @Override
            public int scrollStateChange(int scrollState) {
                return 0;
            }
        });

        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), SubmitQuestionActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mCustomId)
                            .execute();
                } else {
                    stopVoice();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQ_SUBMIT_QUESTION_LOGIN);
                }
            }
        });


        mHerAnswerAdapter.setCallback(new HerAnswerAdapter.Callback() {
            @Override
            public void praiseOnClick(final Question item) {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                    Client.praise(item.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {

                        @Override
                        protected void onRespSuccessData(Praise praise) {
                            item.setIsPrise(praise.getIsPrise());
                            item.setPriseCount(praise.getPriseCount());
                            mHerAnswerAdapter.notifyDataSetChanged();
                            int praiseCount;
                            if (mMiss != null) {
                                if (praise.getIsPrise() == 0) {
                                    praiseCount = mMiss.getTotalPrise() - 1;
                                    mMiss.setTotalPrise(praiseCount);
                                } else {
                                    praiseCount = mMiss.getTotalPrise() + 1;
                                    mMiss.setTotalPrise(praiseCount);
                                }
                                mPraiseNumber.setText(getString(R.string.praise_number, StrFormatter.getFormatCount(praiseCount)));
                            }
                        }
                    }).fire();
                } else {
                    stopVoice();
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void rewardOnClick(Question item) {
                if (LocalUser.getUser().isLogin()) {
                    RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
                } else {
                    stopVoice();
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void playOnClick(final Question item) {
                umengEventCount(UmengCountEventId.MISS_TALK_VOICE);
                mPlayIngItem = item;
                toggleQuestionVoice(item);
            }
        });
    }

    private void toggleQuestionVoice(Question item) {
        if (MissAudioManager.get().isStarted(item)) {
            MissAudioManager.get().pause();
            mHerAnswerAdapter.notifyDataSetChanged();
            stopScheduleJob();
        } else if (MissAudioManager.get().isPaused(item)) {
            MissAudioManager.get().resume();
            mHerAnswerAdapter.notifyDataSetChanged();
            startScheduleJob(100);
        } else {
            stopAnim();
            updateQuestionListenCount(item);
            MissAudioManager.get().start(item);
            mHerAnswerAdapter.notifyDataSetChanged();
            MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
                @Override
                public void onCompleted(String url) {
                    mHerAnswerAdapter.notifyDataSetChanged();
                    stopScheduleJob();
                }
            });
            startScheduleJob(100);
        }
    }


    @Override
    public void onTimeUp(int count) {
        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int lastVisiblePosition = mListView.getLastVisiblePosition();
        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
            if (i == 0 || i - 1 >= mHerAnswerAdapter.getCount()) continue; // Skip header
            Question question = mHerAnswerAdapter.getItem(i - 1);
            if (question != null && MissAudioManager.get().isStarted(question)) {
                View view = mListView.getChildAt(i - firstVisiblePosition);
                TextView soundTime = (TextView) view.findViewById(R.id.soundTime);
                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                progressBar.setMax(question.getSoundTime() * 1000);
                int pastTime = MissAudioManager.get().getCurrentPosition();
                soundTime.setText(getString(R.string._seconds, (question.getSoundTime() * 1000 - pastTime) / 1000));
                progressBar.setProgress(pastTime);
            }
        }
    }

    private void updateQuestionListenCount(final Question item) {
        if (!MissVoiceRecorder.isHeard(item.getId())) {
            Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                @Override
                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                    if (resp.isSuccess()) {
                        MissVoiceRecorder.markHeard(item.getId());
                        item.setListenCount(item.getListenCount() + 1);
                        mHerAnswerAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                protected void onRespFailure(Resp failedResp) {
                    if (failedResp.getCode() == Resp.CODE_LISTENED) {
                        MissVoiceRecorder.markHeard(item.getId());
                        mHerAnswerAdapter.notifyDataSetChanged();
                    }
                }
            }).fire();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!Preference.get().isForeground()) {
            stopVoice();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        stopVoice();
    }

    public void stopVoice() {
        stopAnim();
        stopScheduleJob();
        MissAudioManager.get().stop();
        mHerAnswerAdapter.notifyDataSetChanged();
    }

    private void initData(Intent intent) {
        mCustomId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
    }

    private void initHeaderView() {
        LinearLayout header = (LinearLayout) getLayoutInflater().inflate(R.layout.view_header_miss_profile, null);
        mAvatar = (ImageView) header.findViewById(R.id.avatar);
        mName = (TextView) header.findViewById(R.id.name);
        mVoiceIntroduce = (LinearLayout) header.findViewById(R.id.voiceIntroduce);
        mVoiceTime = (TextView) header.findViewById(R.id.voiceTime);
        mVoiceLevel = header.findViewById(R.id.voiceLevel);
        mPraiseNumber = (TextView) header.findViewById(R.id.praiseNumber);
        mIntroduce = (TextView) header.findViewById(R.id.introduce);
        mAttention = (LinearLayout) header.findViewById(R.id.attention);
        mAttentionImage = (ImageView) header.findViewById(R.id.attentionImage);
        mAttentionText = (TextView) header.findViewById(R.id.attentionText);
        mAttentionNumber = (TextView) header.findViewById(R.id.attentionNumber);
        mReward = (LinearLayout) header.findViewById(R.id.reward);
        mRewardNumber = (TextView) header.findViewById(R.id.rewardNumber);
        mEmpty = (LinearLayout) header.findViewById(R.id.empty);

        mAvatar.setOnClickListener(this);
        mAttention.setOnClickListener(this);
        mReward.setOnClickListener(this);
        mListView.addHeaderView(header);
    }

    public int getListScrollY() {
        View view = mListView.getChildAt(0);

        if (view == null) {
            return 0;
        }

        int firstVisiblePosition = mListView.getFirstVisiblePosition();
        int top = view.getTop();
        return -top + firstVisiblePosition * view.getHeight();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.avatar:
                if (mMiss != null) {
                    umengEventCount(UmengCountEventId.MISS_TALK_AVATAR);
                    Launcher.with(this, LookBigPictureActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mMiss.getPortrait())
                            .putExtra(Launcher.EX_PAYLOAD_2, 0)
                            .execute();
                } else {
                    Launcher.with(this, LookBigPictureActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, "")
                            .putExtra(Launcher.EX_PAYLOAD_2, 0)
                            .execute();
                }
                break;
            case R.id.attention:
                if (mMiss != null) {
                    if (LocalUser.getUser().isLogin()) {
                        umengEventCount(UmengCountEventId.MISS_TALK_ATTENTION);

                        Client.attention(mMiss.getId()).setCallback(new Callback2D<Resp<Attention>, Attention>() {

                            @Override
                            protected void onRespSuccessData(Attention attention) {
                                if (attention.getIsAttention() == 0) {
                                    mAttentionImage.setImageResource(R.drawable.ic_not_attention);
                                    mAttentionText.setText(R.string.attention);
                                } else {
                                    mAttentionImage.setImageResource(R.drawable.ic_attention);
                                    mAttentionText.setText(R.string.is_attention);
                                }
                                mAttentionNumber.setText(getString(R.string.count,
                                        StrFormatter.getFormatCount(attention.getAttentionCount())));
                            }
                        }).fire();
                    } else {
                        stopVoice();
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                } else {
                    ToastUtil.show(getString(R.string.no_miss));
                }
                break;
            case R.id.reward:
                if (mMiss != null) {
                    if (LocalUser.getUser().isLogin()) {
                        RewardMissActivity.show(getActivity(), mCustomId, RewardInfo.TYPE_MISS);
                    } else {
                        stopVoice();
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, REQ_MISS_REWARD_LOGIN);
                    }
                } else {
                    ToastUtil.show(getString(R.string.no_miss));
                }
                break;
        }
    }

    private void requestMissDetail() {
        Client.getMissDetail(mCustomId).setTag(TAG)
                .setCallback(new Callback2D<Resp<Miss>, Miss>() {
                    @Override
                    protected void onRespSuccessData(Miss miss) {
                        updateMissDetail(miss);
                        mMiss = miss;
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        mPraiseNumber.setVisibility(View.GONE);
                    }
                }).fire();
    }

    private void updateMissDetail(final Miss miss) {
        GlideApp.with(this).load(miss.getPortrait())
                .placeholder(R.drawable.ic_default_avatar)
                .circleCrop()
                .into(mAvatar);

        mName.setText(miss.getName());
        mPraiseNumber.setText(getString(R.string.praise_number, StrFormatter.getFormatCount(miss.getTotalPrise())));
        mTitleBar.setTitle(miss.getName());

        if (miss.getSoundTime() == 0 || TextUtils.isEmpty(miss.getBriefingSound())) {
            mVoiceIntroduce.setVisibility(View.GONE);
        } else {
            mVoiceIntroduce.setVisibility(View.VISIBLE);
            mVoiceTime.setText(getString(R.string.voice_time, miss.getSoundTime()));
        }

        if (!TextUtils.isEmpty(miss.getBriefingText())) {
            mIntroduce.setText(miss.getBriefingText());
        } else {
            mIntroduce.setText(R.string.no_miss_introduce);
        }

        if (miss.isAttention() == 0) {
            mAttentionImage.setImageResource(R.drawable.ic_not_attention);
            mAttentionText.setText(R.string.attention);
        } else {
            mAttentionImage.setImageResource(R.drawable.ic_attention);
            mAttentionText.setText(R.string.is_attention);
        }
        mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(miss.getTotalAttention())));
        mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(miss.getTotalAward())));

        mVoiceIntroduce.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                toggleMissVoiceIntroduce(miss);
            }
        });
    }

    private void toggleMissVoiceIntroduce(Miss miss) {
        if (MissAudioManager.get().isStarted(miss)) {
            MissAudioManager.get().stop();
            stopAnim();
        } else {
            MissAudioManager.get().start(miss);
            mHerAnswerAdapter.notifyDataSetChanged();
            startAnim();
        }
    }

    private void startAnim() {
        mVoiceLevel.setBackgroundResource(R.drawable.bg_miss_introduce_voice);
        AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
        animation.start();
    }

    private void stopAnim() {
        mVoiceLevel.clearAnimation();
        mVoiceLevel.setBackgroundResource(R.drawable.ic_miss_voice_4);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Question item = (Question) parent.getItemAtPosition(position);
        if (item != null) {
            if (mPlayIngItem != null) {
                Launcher.with(this, QuestionDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, item.getId())
                        .putExtra(ExtraKeys.PLAYING_ID, mPlayIngItem.getId())
                        .putExtra(ExtraKeys.PLAYING_URL, mPlayIngItem.getAnswerContext())
                        .putExtra(ExtraKeys.PLAYING_AVATAR, mPlayIngItem.getCustomPortrait())
                        .executeForResult(REQ_QUESTION_DETAIL);
            } else {
                Launcher.with(this, QuestionDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, item.getId())
                        .executeForResult(REQ_QUESTION_DETAIL);
            }
        }

        if (mMiss != null && MissAudioManager.get().isStarted(mMiss)) {
            MissAudioManager.get().stop();
            stopAnim();
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mCreateTime = null;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                requestMissDetail();
                requestHerAnswerList(true);
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestHerAnswerList(false);
                    }
                }, 1000);
            }
        });
    }

    private void requestHerAnswerList(final boolean isRefresh) {
        Client.getHerAnswerList(mCustomId, mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        if (questionList.size() == 0 && mCreateTime == null) {
                            mEmpty.setVisibility(View.VISIBLE);
                            stopRefreshAnimation();
                        } else {
                            mEmpty.setVisibility(View.GONE);
                            mHerAnswerList = questionList;
                            updateHerAnswerList(questionList, isRefresh);
                        }
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        stopRefreshAnimation();
                        if (mCreateTime == null) {
                            mEmpty.setVisibility(View.VISIBLE);
                            mVoiceIntroduce.setVisibility(View.GONE);
                            mAttentionNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(0)));
                            mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(0)));
                            mIntroduce.setText(R.string.no_miss_introduce);
                            mHerAnswerAdapter.clear();
                            mHerAnswerAdapter.notifyDataSetChanged();
                        }
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

    private void updateHerAnswerList(final List<Question> questionList, boolean isRefresh) {
        if (questionList == null) {
            stopRefreshAnimation();
            return;
        }

        if (questionList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mCreateTime = mHerAnswerList.get(mHerAnswerList.size() - 1).getCreateTime();
        }

        if (isRefresh) {
            if (mHerAnswerAdapter != null) {
                mHerAnswerAdapter.clear();
            }
        }
        stopRefreshAnimation();

        for (Question question : questionList) {
            if (mSet.add(question.getId())) {
                mHerAnswerAdapter.add(question);
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
        unregisterReceiver(mRefreshReceiver);
    }

    @Override
    public void onAudioStart() {

    }

    @Override
    public void onAudioPlay() {

    }

    @Override
    public void onAudioPause() {

    }

    @Override
    public void onAudioResume() {

    }

    @Override
    public void onAudioStop() {
        stopVoice();
    }

    @Override
    public void onAudioError() {
        ToastUtil.show(R.string.play_failure);
    }

    static class HerAnswerAdapter extends ArrayAdapter<Question> {

        public interface Callback {
            void praiseOnClick(Question item);

            void rewardOnClick(Question item);

            void playOnClick(Question item);
        }

        private Context mContext;
        private Callback mCallback;

        private HerAnswerAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            Question pre = getItem(position - 1);
            Question next = getItem(position);
            //判断两个时间在不在一个月内  不是就要显示标题
            if (pre == null || next == null) return true;
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            return !DateUtil.isInThisMonth(nextTime, preTime);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_miss_answer, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }

            viewHolder.bindingData(mContext, getItem(position), mCallback, isTheDifferentMonth(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.date)
            TextView mDate;
            @BindView(R.id.day)
            TextView mDay;
            @BindView(R.id.question)
            TextView mQuestion;
            @BindView(R.id.soundTime)
            TextView mSoundTime;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.praiseNumber)
            TextView mPraiseNumber;
            @BindView(R.id.commentNumber)
            TextView mCommentNumber;
            @BindView(R.id.ingotNumber)
            TextView mIngotNumber;
            @BindView(playImage)
            ImageView mPlayImage;
            @BindView(R.id.progressBar)
            ProgressBar mProgressBar;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            static class HeaderViewHolder {
                @BindView(R.id.adsorb_text)
                TextView mAdsorbText;

                HeaderViewHolder(View view) {
                    ButterKnife.bind(this, view);
                }
            }

            public void bindingData(final Context context, final Question item,
                                    final Callback callback, boolean theDifferentMonth) {
                if (item == null) return;

                if (theDifferentMonth) {
                    mDate.setVisibility(View.VISIBLE);
                    mDate.setText(DateUtil.getFormatYearMonth(item.getCreateTime()));
                } else {
                    mDate.setVisibility(View.GONE);
                }

                mDay.setText(DateUtil.getFormatDay(item.getCreateTime()).substring(0, 2));
                mQuestion.setText(item.getQuestionContext());
                mSoundTime.setText(context.getString(R.string.voice_time, item.getSoundTime()));
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));

                if (MissVoiceRecorder.isHeard(item.getId())) {
                    mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
                } else {
                    mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }

                if (item.getPriseCount() == 0) {
                    mPraiseNumber.setText(context.getString(R.string.praise));
                } else {
                    mPraiseNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
                }

                if (item.getReplyCount() == 0) {
                    mCommentNumber.setText(context.getString(R.string.comment));
                } else {
                    mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
                }

                if (item.getAwardCount() == 0) {
                    mIngotNumber.setText(context.getString(R.string.reward));
                } else {
                    mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));
                }

                if (item.getIsPrise() == 0) {
                    mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
                } else {
                    mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
                }

                mProgressBar.setMax(item.getSoundTime() * 1000);
                if (MissAudioManager.get().isStarted(item)) {
                    mPlayImage.setImageResource(R.drawable.ic_pause);
                    int pastTime = MissAudioManager.get().getCurrentPosition();
                    mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - pastTime) / 1000));
                    mProgressBar.setProgress(pastTime);
                } else if (MissAudioManager.get().isPaused(item)) {
                    mPlayImage.setImageResource(R.drawable.ic_play);
                    int pastTime = MissAudioManager.get().getCurrentPosition();
                    mSoundTime.setText(context.getString(R.string._seconds, (item.getSoundTime() * 1000 - pastTime) / 1000));
                    mProgressBar.setProgress(pastTime);
                } else {
                    mPlayImage.setImageResource(R.drawable.ic_play);
                    mProgressBar.setProgress(0);
                    mSoundTime.setText(context.getString(R.string._seconds, item.getSoundTime()));
                }

                mPraiseNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.praiseOnClick(item);
                        }
                    }
                });

                mIngotNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.rewardOnClick(item);
                        }
                    }
                });

                mPlayImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.playOnClick(item);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_MISS_REWARD_LOGIN && resultCode == RESULT_OK) {
            RewardMissActivity.show(getActivity(), mCustomId, RewardInfo.TYPE_MISS);
        }

        if (requestCode == REQ_SUBMIT_QUESTION_LOGIN && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, mCustomId)
                    .execute();
        }

        if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
            if (data != null) {
                Question question = data.getParcelableExtra(ExtraKeys.QUESTION);
                Praise praise = data.getParcelableExtra(ExtraKeys.PRAISE);
                if (question != null) {
                    for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
                        Question item = mHerAnswerAdapter.getItem(i);
                        if (item != null && question.getId() == item.getId()) {
                            item.setIsPrise(question.getIsPrise());
                            item.setPriseCount(question.getPriseCount());
                            item.setReplyCount(question.getReplyCount());
                            item.setAwardCount(question.getAwardCount());
                            item.setListenCount(question.getListenCount());

                            if (praise != null) {
                                if (mMiss != null) {
                                    if (question.getIsPrise() == 0) {
                                        mMiss.setTotalPrise(mMiss.getTotalPrise() - 1);
                                    } else {
                                        mMiss.setTotalPrise(mMiss.getTotalPrise() + 1);
                                    }
                                    mPraiseNumber.setText(getString(R.string.praise_number, StrFormatter.getFormatCount(mMiss.getTotalPrise())));
                                }
                            }
                        }
                    }

                    mHerAnswerAdapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void registerRefreshReceiver() {
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REWARD_SUCCESS);
        filter.addAction(ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mRefreshReceiver, filter);
        registerReceiver(mRefreshReceiver, new IntentFilter(Intent.ACTION_SCREEN_OFF));
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_MISS) {
                    if (mMiss != null) {
                        int rewardCount = mMiss.getTotalAward() + 1;
                        mMiss.setTotalAward(rewardCount);
                        mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(rewardCount)));
                    }
                }
            }

            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {
                    if (mMiss != null) {
                        int rewardCount = mMiss.getTotalAward() + 1;
                        mMiss.setTotalAward(rewardCount);
                        mRewardNumber.setText(getString(R.string.count, StrFormatter.getFormatCount(rewardCount)));
                    }

                    for (int i = 0; i < mHerAnswerAdapter.getCount(); i++) {
                        Question question = mHerAnswerAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
                                int questionRewardCount = question.getAwardCount() + 1;
                                question.setAwardCount(questionRewardCount);
                                mHerAnswerAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mCreateTime = null;
                requestMissDetail();
                requestHerAnswerList(true);
            }

            if (Intent.ACTION_SCREEN_OFF.equalsIgnoreCase(intent.getAction())) {
                stopVoice();
            }
        }
    }
}
