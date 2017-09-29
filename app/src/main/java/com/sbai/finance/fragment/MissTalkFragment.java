package com.sbai.finance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
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
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.CommentActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.RewardMissActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Praise;
import com.sbai.finance.model.miss.Question;
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
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGOUT_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

public class MissTalkFragment extends BaseFragment {


    private static final int SUBMIT_QUESTION = 1001;
    @BindView(R.id.askQuestion)
    TextView mAskQuestion;
    @BindView(R.id.titleBar)
    RelativeLayout mTitleBar;
    @BindView(R.id.LatestListView)
    ListView mLatestListView;
    @BindView(R.id.empty)
    TextView mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;


    private List<Question> mLatestQuestionList;
    private MissListAdapter mMissListAdapter;
    private QuestionListAdapter mQuestionListAdapter;
    private Long mCreateTime;
    private int mPageSize = 20;
    private HashSet<Integer> mSet;
    private RefreshReceiver mRefreshReceiver;
    private int mPlayingID = -1;
    private View mFootView;
    private boolean mIsPause = false;
    Unbinder unbinder;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_miss_talk, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mSet = new HashSet<>();
        mLatestListView.setEmptyView(mEmpty);

        initMissLeaderView();
        initLatestQuestionList();

        requestMissList();
        requestHotQuestionList(true);
        initSwipeRefreshLayout();
        registerRefreshReceiver();
    }

    private void initMissLeaderView() {
        LinearLayout header = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_header_miss_talk_1, null);
        EmptyRecyclerView recyclerView = (EmptyRecyclerView) header.findViewById(R.id.recyclerView);
        TextView emptyView = (TextView) header.findViewById(R.id.missEmpty);
        mMissListAdapter = new MissListAdapter(getActivity(), new ArrayList<Miss>());
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setEmptyView(emptyView);
        recyclerView.setAdapter(mMissListAdapter);
        mMissListAdapter.setOnItemClickListener(new MissListAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(Miss item) {
                if (item != null) {
                    Launcher.with(getActivity(), MissProfileActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, item.getId()).execute();
                }
            }
        });

        mLatestListView.addHeaderView(header);

    }

    private void initLatestQuestionList() {
        mQuestionListAdapter = new QuestionListAdapter(getActivity());
        mLatestListView.setAdapter(mQuestionListAdapter);
        mLatestListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question item = (Question) parent.getItemAtPosition(position);
                if (item != null) {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, item.getId());
                    startActivityForResult(intent, REQ_QUESTION_DETAIL);
                    umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
                }
            }
        });

        mQuestionListAdapter.setCallback(new QuestionListAdapter.Callback() {
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
                        }
                    }).fire();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void commentOnClick(Question item) {
                if (item.getReplyCount() == 0) {
                    if (LocalUser.getUser().isLogin()) {
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra(Launcher.EX_PAYLOAD, item.getQuestionUserId());
                        intent.putExtra(Launcher.EX_PAYLOAD_1, item.getId());
                        startActivity(intent);
                    } else {
                        Launcher.with(getActivity(), LoginActivity.class).execute();
                    }
                } else {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, item.getId());
                    startActivityForResult(intent, REQ_QUESTION_DETAIL);
                }
            }

            @Override
            public void rewardOnClick(Question item) {
                if (LocalUser.getUser().isLogin()) {
                    RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
            }

            @Override
            public void voiceOnClick(final Question item, final ImageView playImage,
                                     final ProgressBar progressBar, final TextView voiceTime) {
                umengEventCount(UmengCountEventId.MISS_TALK_VOICE);
                if (!MissVoiceRecorder.isHeard(item.getId())) {
                    //没听过的
                    Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                            if (resp.isSuccess()) {
                                stopPreviousAnimation();
                                MediaPlayerManager.getInstance().play(item.getAnswerContext(), progressBar,
                                        voiceTime, new MediaPlayer.OnCompletionListener() {
                                            @Override
                                            public void onCompletion(MediaPlayer mp) {
                                                item.setPlaying(false);
                                                mQuestionListAdapter.notifyDataSetChanged();
                                                progressBar.setProgress(0);
                                                mPlayingID = -1;
                                                mIsPause = false;
                                            }
                                        });

                                MissVoiceRecorder.markHeard(item.getId());
                                item.setPlaying(true);
                                item.setListenCount(item.getListenCount() + 1);
                                mQuestionListAdapter.notifyDataSetChanged();
                                mPlayingID = item.getId();
                            }
                        }
                    }).fire();
                } else {
                    //听过的
                    if (mPlayingID == item.getId()) {
                        if (mIsPause) {
                            MediaPlayerManager.getInstance().resume();
                            item.setPlaying(true);
                            mQuestionListAdapter.notifyDataSetChanged();
                            MediaPlayerManager.getInstance().setProgressBar(progressBar);
                            mIsPause = false;
                        } else {
                            MediaPlayerManager.getInstance().pause();
                            item.setPlaying(false);
                            item.setProgressIsZero(false);
                            mQuestionListAdapter.notifyDataSetChanged();
                            mIsPause = true;
                        }
                    } else {
                        stopPreviousAnimation();
                        MediaPlayerManager.getInstance().play(item.getAnswerContext(), progressBar,
                                voiceTime, new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        item.setPlaying(false);
                                        mQuestionListAdapter.notifyDataSetChanged();
                                        progressBar.setProgress(0);
                                        mPlayingID = -1;
                                        mIsPause = false;
                                    }
                                });

                        item.setPlaying(true);
                        mQuestionListAdapter.notifyDataSetChanged();
                        mPlayingID = item.getId();
                    }
                }
            }
        });
    }

    public void stopPreviousAnimation() {
        if (mPlayingID != -1) {
            for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
                Question question = mQuestionListAdapter.getItem(i);
                if (question != null) {
                    if (question.getId() == mPlayingID) {
                        question.setPlaying(false);
                        question.setProgressIsZero(true);
                        mQuestionListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mCreateTime = null;
                mSwipeRefreshLayout.setLoadMoreEnable(true);
                mLatestListView.removeFooterView(mFootView);
                mFootView = null;
                requestMissList();
                requestHotQuestionList(true);

                //下拉刷新时关闭语音播放
               MediaPlayerManager.getInstance().release();
                mPlayingID = -1;
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new VerticalSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mLatestListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestLatestQuestionList(false);
                    }
                }, 1000);
            }
        });
    }

    @Override
    public void onPause() {
        super.onPause();
        stopScheduleJob();
        //锁屏或者在后台运行或者跳转页面时停止播放和动画
        MediaPlayerManager.getInstance().release();
        //stopPreviousAnimation(progressBar);

        mPlayingID = -1;
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            //不可见时停止播放和动画
                MediaPlayerManager.getInstance().release();
            //stopPreviousAnimation(progressBar);
            mPlayingID = -1;
        }
    }


    private void requestMissList() {
        Client.getMissList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
                    @Override
                    protected void onRespSuccessData(List<Miss> missList) {
                        updateMissList(missList);
                    }
                }).fireFree();
    }


    private void requestHotQuestionList(final boolean isRefreshing) {
        Client.getHotQuestionList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        if (!questionList.isEmpty()) {
                            Question question = questionList.get(0);
                            question.setType(Question.TYPE_HORT);
                        }
                        updateHotQuestionList(questionList);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        requestLatestQuestionList(isRefreshing);
                    }
                }).fireFree();
    }

    private void requestLatestQuestionList(final boolean isRefreshing) {
        Client.getLatestQuestionList(mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        if (!questionList.isEmpty() && isRefreshing) {
                            Question question = questionList.get(0);
                            question.setType(Question.TYPE_LATEST);
                        }
                        mLatestQuestionList = questionList;
                        updateLatestQuestionList(questionList);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
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

    private void updateMissList(List<Miss> missList) {
        mMissListAdapter.clear();
        mMissListAdapter.addAll(missList);
    }

    private void updateHotQuestionList(List<Question> questionList) {
        mQuestionListAdapter.clear();
        mQuestionListAdapter.addAll(questionList);
    }

    private void updateLatestQuestionList(List<Question> questionList) {

        if (questionList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mCreateTime = mLatestQuestionList.get(mLatestQuestionList.size() - 1).getCreateTime();
        }

        if (questionList.size() < mPageSize && mCreateTime != null) {
            if (mFootView == null) {
                mFootView = View.inflate(getActivity(), R.layout.view_footer_load_complete, null);
                mLatestListView.addFooterView(mFootView, null, true);
            }
        }

        for (Question question : questionList) {
            if (mSet.add(question.getId())) {
                mQuestionListAdapter.add(question);
            }
        }
        mQuestionListAdapter.addAll(questionList);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRefreshReceiver);
    }

    @OnClick(R.id.askQuestion)
    public void onViewClicked() {
        if (LocalUser.getUser().isLogin()) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
        } else {
            Intent intent = new Intent(getActivity(), LoginActivity.class);
            startActivityForResult(intent, SUBMIT_QUESTION);
        }
    }


    public static class MissListAdapter extends RecyclerView.Adapter<MissListAdapter.ViewHolder> {

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        public interface OnItemClickListener {
            void onItemClick(Miss miss);
        }

        private List<Miss> mMissList;
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public MissListAdapter(Context context, List<Miss> missList) {
            this.mMissList = missList;
            this.mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void clear() {
            mMissList.clear();
            notifyItemRangeRemoved(0, mMissList.size());
        }

        public void addAll(List<Miss> missList) {
            mMissList.addAll(missList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.row_misstalk_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mContext, mMissList.get(position), mOnItemClickListener);
        }

        @Override
        public int getItemCount() {
            return mMissList != null ? mMissList.size() : 0;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;

            public ViewHolder(View itemView) {
                super(itemView);
                ButterKnife.bind(this, itemView);
            }

            public void bindDataWithView(Context context, final Miss item, final OnItemClickListener onItemClickListener) {
                if (item == null) return;
                GlideApp.with(context).load(item.getPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .circleCrop()
                        .into(mAvatar);
                mName.setText(item.getName());

                mAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(item);
                        }
                    }
                });
            }
        }
    }

    static class QuestionListAdapter extends ArrayAdapter<Question> {

        private Context mContext;
        private Callback mCallback;

        public interface Callback {
            void praiseOnClick(Question item);

            void commentOnClick(Question item);

            void rewardOnClick(Question item);

            void voiceOnClick(Question item, ImageView playImage, ProgressBar progressBar, TextView voiceTime);

        }

        private void setCallback(Callback callback) {
            mCallback = callback;
        }

        private QuestionListAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_misstalk_answer, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(mContext, getItem(position), mCallback);
            return convertView;
        }


        static class ViewHolder {
            @BindView(R.id.hotQuestion)
            TextView mHotQuestion;
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.askTime)
            TextView mAskTime;
            @BindView(R.id.question)
            TextView mQuestion;
            @BindView(R.id.missAvatar)
            ImageView mMissAvatar;
            @BindView(R.id.voiceTime)
            TextView mVoiceTime;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.praiseNumber)
            TextView mPraiseNumber;
            @BindView(R.id.commentNumber)
            TextView mCommentNumber;
            @BindView(R.id.ingotNumber)
            TextView mIngotNumber;
            @BindView(R.id.playImage)
            ImageView mPlayImage;
            @BindView(R.id.progressBar)
            ProgressBar mProgressBar;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final Question item, final Callback callback) {
                if (item == null) return;

                if (item.getType() == Question.TYPE_HORT) {
                    mHotQuestion.setVisibility(View.VISIBLE);
                    mHotQuestion.setText(R.string.hot_question);
                    mHotQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_hot, 0, 0, 0);
                } else if (item.getType() == Question.TYPE_LATEST) {
                    mHotQuestion.setVisibility(View.VISIBLE);
                    mHotQuestion.setText(R.string.latest_question);
                    mHotQuestion.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_title_latest, 0, 0, 0);
                } else {
                    mHotQuestion.setVisibility(View.GONE);
                }

                GlideApp.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mAvatar);

                GlideApp.with(context).load(item.getCustomPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .circleCrop()
                        .into(mMissAvatar);

                mName.setText(item.getUserName());
                mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
                mQuestion.setText(item.getQuestionContext());
                mVoiceTime.setText(context.getString(R.string.voice_time, item.getSoundTime()));
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
                mPraiseNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
                mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
                mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

                mCommentNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.commentOnClick(item);
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

                mMissAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, MissProfileActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, item.getAnswerCustomId())
                                .execute();
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

                if (!item.isPlaying()) {
                    mPlayImage.setImageResource(R.drawable.ic_play);
                } else {
                    mPlayImage.setImageResource(R.drawable.ic_pause);
                }

                if (item.isProgressIsZero()) {
                    mProgressBar.setProgress(0);
                } else {
                    mProgressBar.setProgress(MediaPlayerManager.getInstance().getCurrentDuration());
                }

                mPlayImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.voiceOnClick(item, mPlayImage, mProgressBar, mVoiceTime);
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SUBMIT_QUESTION && resultCode == BaseActivity. RESULT_OK) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
        }

        if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
            if (data != null) {
                Praise prise = data.getParcelableExtra(Launcher.EX_PAYLOAD);
                int replyCount = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
                int rewardCount = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
                int listenCount = data.getIntExtra(Launcher.EX_PAYLOAD_3, -1);
                for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
                    Question question = mQuestionListAdapter.getItem(i);
                    if (question != null) {
                        if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                            if (prise != null) {
                                question.setIsPrise(prise.getIsPrise());
                                question.setPriseCount(prise.getPriseCount());
                            }
                            if (replyCount != -1) {
                                question.setReplyCount(replyCount);
                            }
                            if (rewardCount != -1) {
                                question.setAwardCount(rewardCount);
                            }
                            if (listenCount != -1) {
                                question.setListenCount(listenCount);
                            }
                            mQuestionListAdapter.notifyDataSetChanged();
                        }
                    }
                }

                if (replyCount != -1) {

                    for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
                        Question question = mQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {

                                mQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }
        }
    }

    private void registerRefreshReceiver() {
        mRefreshReceiver = new RefreshReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_REWARD_SUCCESS);
        filter.addAction(ACTION_LOGIN_SUCCESS);
        filter.addAction(ACTION_LOGOUT_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRefreshReceiver, filter);
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {

                    for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
                        Question question = mQuestionListAdapter.getItem(i);
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

            if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())
                    || ACTION_LOGOUT_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mCreateTime = null;
                mLatestListView.removeFooterView(mFootView);
                mFootView = null;
                requestMissList();
                requestHotQuestionList(true);
            }
        }
    }
}
