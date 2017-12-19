package com.sbai.finance.fragment.miss;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.CommentActivity;
import com.sbai.finance.activity.miss.MissProfileDetailActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.RewardMissActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.fragment.MediaPlayFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.utils.audio.MissVoiceRecorder;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.httplib.ApiError;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_CODE_COMMENT;
import static com.sbai.finance.activity.BaseActivity.REQ_CODE_LOGIN;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;
import static com.sbai.finance.activity.BaseActivity.REQ_SUBMIT_QUESTION_LOGIN;
import static com.sbai.finance.activity.miss.MissProfileDetailActivity.CUSTOM_ID;

/**
 * Created by Administrator on 2017\11\22 0022.
 */

public class MissProfileQuestionFragment extends MediaPlayFragment {
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    Unbinder mBind;

    Button mAsk;
    MissFloatWindow mMissFloatWindow;

    private QuestionListAdapter mQuestionListAdapter;
    private List<Question> mQuestionList;
    private boolean mHasMoreData;

    private int mCustomId;
    private Long mCreateTime;
    private int mPageSize = 20;

    private Miss mMiss;
    private Question mPlayIngItem;

    OnFragmentRecycleViewScrollListener mOnFragmentRecycleViewScrollListener;
    private boolean mHasEnter;

    private MediaPlayService mMediaPlayService;
    private Rect mRect;

    public interface OnFragmentRecycleViewScrollListener {

        void onSwipRefreshEnable(boolean enabled, int fragmentPosition);
    }

    public static MissProfileQuestionFragment newInstance(int customId) {
        MissProfileQuestionFragment missProfileQuestionFragment = new MissProfileQuestionFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(CUSTOM_ID, customId);
        missProfileQuestionFragment.setArguments(bundle);
        return missProfileQuestionFragment;
    }

    public void setService(MediaPlayService mediaPlayService) {
        mMediaPlayService = mediaPlayService;
    }

    public void initScrollState() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
        boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
        if (mOnFragmentRecycleViewScrollListener != null) {
            mOnFragmentRecycleViewScrollListener.onSwipRefreshEnable(isTop, 0);
        }
    }

    public void setMiss(Miss miss) {
        mMiss = miss;
    }

    public void refresh() {
//        mSet.clear();
        mCreateTime = null;
        mHasMoreData = true;
        mQuestionList.clear();
        requestHerAnswerList(true);
    }

    private void updateFloatStatus() {
        if (MissAudioManager.get().isPlaying()) {
            MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
            if (audio != null) {
                if (audio instanceof Radio) {
                    mMissFloatWindow.startAnim();
                    mMissFloatWindow.setVisibility(View.VISIBLE);
                    mMissFloatWindow.setMissAvatar(((Radio) audio).getUserPortrait());
                } else if (audio instanceof Question) {
                    mMissFloatWindow.startAnim();
                    mMissFloatWindow.setVisibility(View.VISIBLE);
                    mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
                }
            }

        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MissProfileDetailActivity) {
            mOnFragmentRecycleViewScrollListener = (OnFragmentRecycleViewScrollListener) context;
            mMissFloatWindow = ((MissProfileDetailActivity) context).getFloatWindow();
            mAsk = ((MissProfileDetailActivity) context).getAskBtn();
            if (mAsk != null) {
                mAsk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (LocalUser.getUser().isLogin()) {
                            Launcher.with(getActivity(), SubmitQuestionActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, mCustomId)
                                    .execute();
                        } else {
                            stopVoice();
                            Launcher.with(MissProfileQuestionFragment.this,LoginActivity.class).executeForResult(REQ_SUBMIT_QUESTION_LOGIN);
                        }
                    }
                });
            }
        }
        if (mMediaPlayService == null && context instanceof MissProfileDetailActivity) {
            mMediaPlayService = ((MissProfileDetailActivity) context).getMediaPlayService();
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCustomId = getArguments().getInt(CUSTOM_ID);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miss_profile_question, container, false);
        mBind = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
        mRootMissFloatWindow = mMissFloatWindow;
        mRect = new Rect();
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_REWARD_SUCCESS);
        intentFilter.addAction(ACTION_LOGIN_SUCCESS);
        return intentFilter;
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        mQuestionListAdapter.notifyDataSetChanged();
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Question) {
            mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
        }
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        startScheduleJob(100);
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        startScheduleJob(100);

        mMissFloatWindow.startAnim();
        mQuestionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        stopScheduleJob();

        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
        mQuestionListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        stopScheduleJob();
        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
        mQuestionListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {
        super.onMediaPlayCurrentPosition(IAudioId, source, mediaPlayCurrentPosition, totalDuration);
        int firstVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        int lastVisiblePosition = ((LinearLayoutManager) mRecyclerView.getLayoutManager()).findLastVisibleItemPosition();
        boolean visibleItemsStarted = false;
        if (firstVisiblePosition >= 0 && lastVisiblePosition >= 0 && mQuestionList.size() > 0) {
            if (mQuestionList != null && mQuestionList.size() > 0) {
                for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
                    if (i >= mQuestionListAdapter.getCount()) continue; // Skip header
                    Question question = mQuestionList.get(i);
                    if (question != null && MissAudioManager.get().isStarted(question)) {
                        View view = mRecyclerView.getChildAt(i - firstVisiblePosition);
                        visibleItemsStarted = view.getGlobalVisibleRect(mRect);
                        TextView soundTime = view.findViewById(R.id.soundTime);
                        ProgressBar progressBar = view.findViewById(R.id.progressBar);
                        progressBar.setMax(question.getSoundTime() * 1000);
                        int pastTime = MissAudioManager.get().getCurrentPosition();
                        soundTime.setText(getString(R.string._seconds, (question.getSoundTime() * 1000 - pastTime) / 1000));
                        progressBar.setProgress(pastTime);
                    }
                }
            }
        }

        if (visibleItemsStarted && mMissFloatWindow.getVisibility() == View.VISIBLE) {
            mMissFloatWindow.setVisibility(View.GONE);
        }

        if (!visibleItemsStarted && mMissFloatWindow.getVisibility() == View.GONE) {
            mMissFloatWindow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onOtherReceive(Context context, Intent intent) {
        if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
            if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_MISS) {
                if (mMiss != null) {
                    int rewardCount = mMiss.getTotalAward() + 1;
                    mMiss.setTotalAward(rewardCount);
                }
            }
        }

        if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
            if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {
                if (mMiss != null) {
                    int rewardCount = mMiss.getTotalAward() + 1;
                    mMiss.setTotalAward(rewardCount);
                }

                for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
                    Question question = mQuestionList.get(i);
                    if (question != null) {
                        if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
                            int questionRewardCount = question.getAwardCount() + 1;
                            question.setAwardCount(questionRewardCount);
                            mQuestionListAdapter.notifyDataSetChanged();
                        }
                    }
                }
            }
        }

        if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
            mCreateTime = null;
            ((MissProfileDetailActivity) getActivity()).refreshData();
            requestHerAnswerList(true);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!mHasEnter) {
            mHasEnter = true;
            refresh();
        }
        updateFloatStatus();
        mQuestionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    private void initView() {
        mQuestionList = new ArrayList<Question>();
        mQuestionListAdapter = new QuestionListAdapter(mQuestionList, getActivity());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setAdapter(mQuestionListAdapter);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView) && mHasMoreData) {
                    requestHerAnswerList(false);
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                if (mOnFragmentRecycleViewScrollListener != null) {
                    mOnFragmentRecycleViewScrollListener.onSwipRefreshEnable(isTop, 0);
                }
            }
        });

        mQuestionListAdapter.setCallback(new QuestionListAdapter.Callback() {
            @Override
            public void itemClick(Question item) {
                gotoQuestionDetail(item, mPlayIngItem);

                if (mMiss != null && MissAudioManager.get().isStarted(mMiss)) {
                    MissAudioManager.get().stop();
                }
            }

            @Override
            public void praiseOnClick(final Question item) {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                    Client.praise(item.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {

                        @Override
                        protected void onRespSuccessData(Praise praise) {
                            item.setIsPrise(praise.getIsPrise());
                            item.setPriseCount(praise.getPriseCount());
                            mQuestionListAdapter.notifyDataSetChanged();
                            int praiseCount;
                            if (mMiss != null) {
                                if (praise.getIsPrise() == 0) {
                                    praiseCount = mMiss.getTotalPrise() - 1;
                                    mMiss.setTotalPrise(praiseCount);
                                } else {
                                    praiseCount = mMiss.getTotalPrise() + 1;
                                    mMiss.setTotalPrise(praiseCount);
                                }
                                ((MissProfileDetailActivity) getActivity()).praiseAdd(praiseCount);
//                                mPraiseNumber.setText(getString(R.string.praise_number, StrFormatter.getFormatCount(praiseCount)));
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

            @Override
            public void onComment(Question item) {
                if (item.getReplyCount() == 0) {
                    if (LocalUser.getUser().isLogin()) {
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra(Launcher.EX_PAYLOAD, item.getQuestionUserId());
                        intent.putExtra(Launcher.EX_PAYLOAD_1, item.getId());
                        startActivityForResult(intent, REQ_CODE_COMMENT);
                    } else {
                        MissAudioManager.get().stop();
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, item.getId());
                    startActivityForResult(intent, REQ_QUESTION_DETAIL);
                }
            }
        });
    }

    public void gotoQuestionDetail(Question item, Question playingItem) {
        if (item != null) {
            if (playingItem != null) {
                Launcher.with(this, QuestionDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, item.getId())
                        .putExtra(ExtraKeys.PLAYING_ID, playingItem.getId())
                        .putExtra(ExtraKeys.PLAYING_URL, playingItem.getAnswerContext())
                        .putExtra(ExtraKeys.PLAYING_AVATAR, playingItem.getCustomPortrait())
                        .excuteForResultFragment(REQ_QUESTION_DETAIL);
            } else {
                Launcher.with(this, QuestionDetailActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, item.getId())
                        .excuteForResultFragment(REQ_QUESTION_DETAIL);
            }
        }
    }

    private void toggleQuestionVoice(Question item) {
        if (MissAudioManager.get().isStarted(item)) {
            if (mMediaPlayService != null) {
                mMediaPlayService.onPausePlay(item);
            }
        } else if (MissAudioManager.get().isPaused(item)) {
            if (mMediaPlayService != null) {
                mMediaPlayService.onResume();
            }
        } else {
            updateQuestionListenCount(item);
            if (mMediaPlayService != null) {
                mMediaPlayService.startPlay(item, MediaPlayService.MEDIA_SOURCE_MISS_PROFILE);
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
                        mQuestionListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                protected void onRespFailure(Resp failedResp) {
                    if (failedResp.getCode() == Resp.CODE_LISTENED) {
                        MissVoiceRecorder.markHeard(item.getId());
                        mQuestionListAdapter.notifyDataSetChanged();
                    }
                }
            }).fire();
        }
    }

    private void requestHerAnswerList(final boolean isRefresh) {
        Client.getHerAnswerList(mCustomId, mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        if (questionList.size() == 0 && mCreateTime == null) {
                            mEmpty.setVisibility(View.VISIBLE);
                        } else {
                            mEmpty.setVisibility(View.GONE);
                            updateHerAnswerList(questionList, isRefresh);
                        }
                    }

                    @Override
                    public void onFailure(ApiError apiError) {
                        super.onFailure(apiError);
                        if (mCreateTime == null) {
                            mEmpty.setVisibility(View.VISIBLE);
                            mQuestionListAdapter.clear();
                            mQuestionListAdapter.notifyDataSetChanged();
                        }
                    }
                }).fire();
    }

    private void updateHerAnswerList(final List<Question> questionList, boolean isRefresh) {
        if (questionList == null) {
            return;
        }

        if (questionList.size() < mPageSize) {
            mHasMoreData = false;
        } else {
            mHasMoreData = true;
            mCreateTime = questionList.get(questionList.size() - 1).getCreateTime();
        }

        if (isRefresh) {
            if (mQuestionListAdapter != null) {
                mQuestionListAdapter.clear();
            }
        }

        resumeLastPlayUI(questionList);

        mQuestionListAdapter.addAll(questionList);
    }

    private void resumeLastPlayUI(List<Question> questionList) {
        for (Question question : questionList) {
            if (MissAudioManager.get().isStarted(question)) {
                mMissFloatWindow.setMissAvatar(question.getCustomPortrait());
                startScheduleJob(100);
                break;
            }
            if (MissAudioManager.get().isPaused(question)) {
                mMissFloatWindow.setMissAvatar(question.getCustomPortrait());
                break;
            }
        }
    }

    protected boolean isSlideToBottom(RecyclerView recyclerView) {
        return recyclerView != null &&
                recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
    }

    public void stopVoice() {
        stopScheduleJob();
        MissAudioManager.get().stop();
        mQuestionListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHasEnter = false;
        mBind.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQ_SUBMIT_QUESTION_LOGIN && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, mCustomId)
                    .execute();
        }
        if (requestCode == REQ_QUESTION_DETAIL && data != null) {
            Question question = data.getParcelableExtra(ExtraKeys.QUESTION);
            Praise praise = data.getParcelableExtra(ExtraKeys.PRAISE);
            if (question != null) {
                for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
                    Question item = mQuestionList.get(i);
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
                                ((MissProfileDetailActivity) getActivity()).praiseAdd(mMiss.getTotalPrise());
                            }
                        }

                        if (item != null) {
                            if (MissAudioManager.get().isStarted(item)) {
                                startScheduleJob(100);
                                MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
                                    @Override
                                    public void onCompleted(String url) {
                                        mMissFloatWindow.setVisibility(View.GONE);
                                        mMissFloatWindow.stopAnim();
                                        mQuestionListAdapter.notifyDataSetChanged();
                                        stopScheduleJob();
                                    }
                                });
                            }
                        }
                    }
                }

                mQuestionListAdapter.notifyDataSetChanged();
            }
        } else if (requestCode == REQ_CODE_COMMENT) {
            if (data != null) {
                int id = data.getIntExtra(ExtraKeys.QUESTION_ID, -1);
                List<Question> missAskList = mQuestionList;
                if (missAskList != null && !missAskList.isEmpty()) {
                    for (Question result : missAskList) {
                        if (result.getId() == id) {
                            result.setReplyCount(1);
                            mQuestionListAdapter.notifyDataSetChanged();
                            break;
                        }
                    }
                }
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    static class QuestionListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Question> mQuestionList;
        private Context mContext;

        public interface Callback {

            void itemClick(Question item);

            void praiseOnClick(Question item);

            void rewardOnClick(Question item);

            void playOnClick(Question item);

            void onComment(Question item);
        }

        private Callback mCallback;

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        private boolean isTheDifferentMonth(int position) {
            if (position == 0) {
                return true;
            }
            Question pre = mQuestionList.get(position - 1);
            Question next = mQuestionList.get(position);
            //判断两个时间在不在一个月内  不是就要显示标题
            if (pre == null || next == null) return true;
            long preTime = pre.getCreateTime();
            long nextTime = next.getCreateTime();
            return !DateUtil.isInThisMonth(nextTime, preTime);
        }

        public QuestionListAdapter(List<Question> questionList, Context context) {
            mQuestionList = questionList;
            mContext = context;
        }

        public void clear() {
            mQuestionList.clear();
            notifyDataSetChanged();
        }

        public void addAll(List<Question> questionList) {
            mQuestionList.addAll(questionList);
            notifyDataSetChanged();
        }

        public void add(Question question) {
            mQuestionList.add(question);
            notifyDataSetChanged();
        }

        public int getCount() {
            return mQuestionList == null ? 0 : mQuestionList.size();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.row_miss_profile_question, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).bindingData(mContext, mQuestionList.get(position), mCallback, isTheDifferentMonth(position), position, getCount());
        }

        @Override
        public int getItemCount() {
            return mQuestionList != null ? mQuestionList.size() : 0;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.item)
            LinearLayout mItemLayout;
            @BindView(R.id.month)
            TextView mMonth;
            @BindView(R.id.day)
            TextView mDay;
            @BindView(R.id.wordDay)
            TextView mWordDay;
            @BindView(R.id.title)
            TextView mTitle;
            @BindView(R.id.playImage)
            ImageView mPlayImage;
            @BindView(R.id.soundTime)
            TextView mSoundTime;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.progressBar)
            ProgressBar mProgressBar;
            @BindView(R.id.praiseNumber)
            TextView mPraiseNumber;
            @BindView(R.id.commentNumber)
            TextView mCommentNumber;
            @BindView(R.id.ingotNumber)
            TextView mIngotNumber;
            @BindView(R.id.label)
            LinearLayout mLabel;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            private void bindingData(Context context, final Question item, final Callback callback, boolean theDifferentMonth, int position, int count) {
                if (item == null) return;

                if (position == count - 1) {
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mItemLayout.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, (int) Display.dp2Px(64, context.getResources()));
                } else {
                    RecyclerView.LayoutParams layoutParams = (RecyclerView.LayoutParams) mItemLayout.getLayoutParams();
                    layoutParams.setMargins(0, 0, 0, 0);
                }
                if (theDifferentMonth) {
                    mMonth.setVisibility(View.VISIBLE);
                    mMonth.setText(DateUtil.getFormatYearMonth(item.getCreateTime()));
                } else {
                    mMonth.setVisibility(View.GONE);
                }

                mDay.setText(DateUtil.getFormatDay(item.getCreateTime()).substring(0, 2));
                mTitle.setText(item.getQuestionContext());
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

                mItemLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            callback.itemClick(item);
                        }
                    }
                });

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

                mCommentNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            callback.onComment(item);
                        }
                    }
                });
            }
        }
    }
}
