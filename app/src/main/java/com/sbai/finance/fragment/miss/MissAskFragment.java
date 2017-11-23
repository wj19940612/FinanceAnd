package com.sbai.finance.fragment.miss;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.RewardMissActivity;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.HasLabelImageLayout;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGOUT_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;


public class MissAskFragment extends BaseFragment {

    private static final String MISS_ASK_TYPE = "miss_ask_type";
    private static final int REQ_CODE_COMMENT = 1001;

    public static final int MISS_ASK_TYPE_HOT = 0; //最热提问
    public static final int MISS_ASK_TYPE_LATEST = 1; //最热提问

    @BindView(R.id.emptyRecyclerView)
    EmptyRecyclerView mEmptyRecyclerView;
    Unbinder unbinder;
    @BindView(R.id.empty)
    NestedScrollView mEmpty;


    private int mMissAskType;
    private ArrayList<Question> mQuestionList;
    private MissAskAdapter mMissAskAdapter;
    private Long mCreateTime;
    private boolean mLoadMore;
    private OnSwipeRefreshEnableListener mOnSwipeRefreshEnableListener;
    private HashSet<Integer> mSet;

    public interface OnSwipeRefreshEnableListener {
        void onSwipeRefreshEnable(boolean swipeFreshEnable);

        void onRadioPlay(boolean radioPlayViewHasHasFocus);
    }

    public void setOnSwipeRefreshEnableListener(OnSwipeRefreshEnableListener onSwipeRefreshEnableListener) {
        mOnSwipeRefreshEnableListener = onSwipeRefreshEnableListener;
    }

    private MediaPlayService.MediaPlayBroadcastReceiver mRefreshBroadcastReceiver = new MediaPlayService.MediaPlayBroadcastReceiver() {

        @Override
        public void onOtherReceive(Context context, Intent intent) {
            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                int rewardId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
                if (rewardId == RewardInfo.TYPE_QUESTION) {
                    for (int i = 0; i < mMissAskAdapter.getMissAskList().size(); i++) {
                        for (Question result : mMissAskAdapter.getMissAskList()) {
                            if (result.getId() == rewardId) {
                                int questionRewardCount = result.getAwardCount() + 1;
                                result.setAwardCount(questionRewardCount);
                                mMissAskAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }

                if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())
                        || ACTION_LOGOUT_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                    refreshData();
                }
            }
        }

        @Override
        public void onMediaPlayStop(int IAudioId, int source) {

        }

        @Override
        public void onMediaPlayError(int IAudioId, int source) {

        }

        @Override
        public void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {
            if (source == MediaPlayService.MEDIA_SOURCE_RECOMMEND_QUESTION) {
                if (IAudioId == -1) return;
                LinearLayoutManager layoutManager = (LinearLayoutManager) mEmptyRecyclerView.getLayoutManager();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();
                int lastVisibleItemPosition = layoutManager.findLastVisibleItemPosition();
                boolean radioPlayHasFocus = false;
                for (int i = firstVisibleItemPosition; i < lastVisibleItemPosition; i++) {
                    View view = layoutManager.findViewByPosition(i - firstVisibleItemPosition);
                    ImageView playImage = (ImageView) view.findViewById(R.id.playImage);
                    TextView soundTime = (TextView) view.findViewById(R.id.soundTime);
                    ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
                    playImage.setImageResource(R.drawable.ic_pause);
                    progressBar.setMax(totalDuration);
                    int pastTime = MissAudioManager.get().getCurrentPosition();
                    soundTime.setText(getString(R.string._seconds, (totalDuration - pastTime) / 1000));
                    progressBar.setProgress(pastTime);
                    radioPlayHasFocus = true;
                }
                if (mOnSwipeRefreshEnableListener != null) {
                    mOnSwipeRefreshEnableListener.onRadioPlay(radioPlayHasFocus);
                }
            }
        }
    };


    public static MissAskFragment newInstance(int type) {
        MissAskFragment fragment = new MissAskFragment();
        Bundle args = new Bundle();
        args.putInt(MISS_ASK_TYPE, type);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mMissAskType = getArguments().getInt(MISS_ASK_TYPE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miss_ask, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        registerReceiver();
        initView();
        requestAskList(true);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRefreshBroadcastReceiver);
        mEmptyRecyclerView.clearOnChildAttachStateChangeListeners();
        unbinder.unbind();
    }

    public void refreshData() {
        mCreateTime = null;
//        mSet = new HashSet<>();
        requestAskList(true);
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REWARD_SUCCESS);
        filter.addAction(ACTION_LOGIN_SUCCESS);
        filter.addAction(ACTION_LOGOUT_SUCCESS);

        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_STOP);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_ERROR);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PROGRESS);

        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRefreshBroadcastReceiver, filter);
    }

    private void requestAskList(boolean isRefresh) {
        if (mMissAskType == MISS_ASK_TYPE_HOT) {
            requestHotQuestionList(isRefresh);
        } else {
            requestLatestQuestionList(isRefresh);
        }
    }

    private void initView() {
        mEmptyRecyclerView.setEmptyView(mEmpty);
        mQuestionList = new ArrayList<>();
        mMissAskAdapter = new MissAskAdapter(mQuestionList, getActivity());
        mEmptyRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mEmptyRecyclerView.setAdapter(mMissAskAdapter);
        mEmptyRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (isSlideToBottom(recyclerView) && mLoadMore) {
                    requestAskList(false);
                }
                LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                boolean isTop = layoutManager.findFirstCompletelyVisibleItemPosition() == 0;
                if (mOnSwipeRefreshEnableListener != null) {
                    mOnSwipeRefreshEnableListener.onSwipeRefreshEnable(isTop);
                }
            }

            private boolean isSlideToBottom(RecyclerView recyclerView) {
                if (recyclerView == null) return false;
                return recyclerView.computeVerticalScrollExtent() + recyclerView.computeVerticalScrollOffset() >= recyclerView.computeVerticalScrollRange();
            }
        });
        initListener();
    }


    private void initListener() {
        mMissAskAdapter.setCallback(new MissAskAdapter.Callback() {
            @Override
            public void onPraiseClick(final Question item) {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                    Client.praise(item.getId()).setCallback(new Callback2D<Resp<Praise>, Praise>() {
                        @Override
                        protected void onRespSuccessData(Praise praise) {
                            item.setIsPrise(praise.getIsPrise());
                            item.setPriseCount(praise.getPriseCount());
                            mMissAskAdapter.notifyDataSetChanged();
                        }
                    }).fire();
                } else {
                    MissAudioManager.get().stop();
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onCommentClick(Question item) {
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

            @Override
            public void onRewardClick(Question item) {
                if (LocalUser.getUser().isLogin()) {
                    RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
                } else {
                    MissAudioManager.get().stop();
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void onPlayClick(final Question item, int position) {
                umengEventCount(UmengCountEventId.MISS_TALK_VOICE);

                if (MissAudioManager.get().isStarted(item)) {
                    MissAudioManager.get().pause();
                } else if (MissAudioManager.get().isPaused(item)) {
                    MissAudioManager.get().resume();
                } else {
                    updateQuestionListenCount(item);
                    MissAudioManager.get().start(item);
                }
            }

            @Override
            public void onItemClick(Question question) {
                if (question != null) {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, question.getId());
                    intent.putExtra(ExtraKeys.IS_FROM_MISS_TALK, true);
                    startActivityForResult(intent, REQ_QUESTION_DETAIL);

                    umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
                }
            }
        });
    }

    private void updateQuestionListenCount(final Question item) {
        if (!MissVoiceRecorder.isHeard(item.getId())) {
            Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                @Override
                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                    if (resp.isSuccess()) {
                        MissVoiceRecorder.markHeard(item.getId());
                        item.setListenCount(item.getListenCount() + 1);
                        mMissAskAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                protected void onRespFailure(Resp failedResp) {
                    if (failedResp.getCode() == Resp.CODE_LISTENED) {
                        MissVoiceRecorder.markHeard(item.getId());
                        mMissAskAdapter.notifyDataSetChanged();
                    }
                }
            }).fire();
        }
    }

    private void requestHotQuestionList(final boolean isRefresh) {
        Client.getHotQuestionList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        updateLatestQuestionList(questionList, isRefresh);
                    }

                }).fireFree();
    }

    private void updateLatestQuestionList(List<Question> questionList, boolean isRefresh) {
        if (questionList.size() < Client.DEFAULT_PAGE_SIZE) { // load completed
            mLoadMore = false;
        } else {
            if (mMissAskType == MISS_ASK_TYPE_LATEST) {
                mLoadMore = true;
            }
        }
        if (!questionList.isEmpty()) {
            mCreateTime = questionList.get(questionList.size() - 1).getCreateTime();
        }
        if (isRefresh) {
            mMissAskAdapter.clear();
        }
        mMissAskAdapter.addAll(questionList);

//        for (Question result : questionList) {
//            if (mSet.add(result.getId())) {
//                mMissAskAdapter.add(result);
//            }
//        }
    }

    private void requestLatestQuestionList(final boolean isRefresh) {
        Client.getLatestQuestionList(mCreateTime, Client.DEFAULT_PAGE_SIZE).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        updateLatestQuestionList(questionList, isRefresh);
                    }

                }).fire();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == BaseActivity.RESULT_OK) {
            switch (requestCode) {
                case REQ_CODE_COMMENT:
                    if (data != null) {
                        int id = data.getIntExtra(ExtraKeys.QUESTION_ID, -1);
                        List<Question> missAskList = mMissAskAdapter.getMissAskList();
                        if (missAskList != null && !missAskList.isEmpty()) {
                            for (Question result : missAskList) {
                                if (result.getId() == id) {
                                    result.setReplyCount(1);
                                    mMissAskAdapter.notifyDataSetChanged();
                                    break;
                                }
                            }
                        }
                    }
            }
        }
    }

    public static class MissAskAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private List<Question> mMissAskList;
        private Context mContext;
        private Callback mCallback;

        public interface Callback {
            void onPraiseClick(Question item);

            void onCommentClick(Question item);

            void onRewardClick(Question item);

            void onPlayClick(Question item, int position);

            void onItemClick(Question question);
        }

        public void setCallback(Callback callback) {
            mCallback = callback;
        }

        public MissAskAdapter(List<Question> questionList, Context context) {
            mMissAskList = questionList;
            mContext = context;
        }

        public List<Question> getMissAskList() {
            return mMissAskList;
        }

        public void clear() {
            mMissAskList.clear();
            this.notifyDataSetChanged();
        }

        public void addAll(List<Question> questionList) {
            mMissAskList.addAll(questionList);
            notifyDataSetChanged();
        }

        public void add(Question result) {
            mMissAskList.add(result);
            notifyDataSetChanged();
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_misstalk_answer, parent, false);
            return new ViewHolder(view);
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            ((ViewHolder) holder).bindingData(mContext, mMissAskList.get(position), mCallback, position);
        }

        @Override
        public int getItemCount() {
            return mMissAskList.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.askTime)
            TextView mAskTime;
            @BindView(R.id.question)
            TextView mQuestion;
            @BindView(R.id.playImage)
            ImageView mPlayImage;
            @BindView(R.id.soundTime)
            TextView mSoundTime;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.missName)
            TextView mMissName;
            @BindView(R.id.missAvatar)
            HasLabelImageLayout mMissAvatar;
            @BindView(R.id.progressBar)
            ProgressBar mProgressBar;
            @BindView(R.id.praiseNumber)
            TextView mPraiseNumber;
            @BindView(R.id.commentNumber)
            TextView mCommentNumber;
            @BindView(R.id.ingotNumber)
            TextView mIngotNumber;
            @BindView(R.id.rootView)
            LinearLayout mRootView;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final Question item, final Callback callback, final int position) {
                GlideApp.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mAvatar);
                mMissName.setText(item.getCustomName());
                mMissAvatar.setCircleUrl(item.getCustomPortrait());

                mName.setText(item.getUserName());
                mAskTime.setText(DateUtil.formatDefaultStyleTime(item.getCreateTime()));
                mQuestion.setText(item.getQuestionContext());
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));

                if (MissVoiceRecorder.isHeard(item.getId())) {
                    mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.unluckyText));
                } else {
                    mListenerNumber.setTextColor(ContextCompat.getColor(context, R.color.colorPrimary));
                }

                if (item.getIsPrise() == 0) {
                    mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_unpraise, 0, 0, 0);
                } else {
                    mPraiseNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_praise, 0, 0, 0);
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

                mPraiseNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onPraiseClick(item);
                        }
                    }
                });

                mCommentNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onCommentClick(item);
                        }
                    }
                });

                mIngotNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onRewardClick(item);
                        }
                    }
                });

                mPlayImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.onPlayClick(item, position);
                        }
                    }
                });

                mRootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (callback != null) {
                            callback.onCommentClick(item);
                        }
                    }
                });
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
            }
        }

    }

}
