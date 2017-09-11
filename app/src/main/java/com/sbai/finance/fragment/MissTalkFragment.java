package com.sbai.finance.fragment;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.BitmapDrawable;
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
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.MessagesActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.MyQuestionsActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.RewardMissActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.NewMessage;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Prise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MediaPlayerManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.R.id.missAvatar;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

public class MissTalkFragment extends BaseFragment implements View.OnClickListener {

    private static final int SUBMIT_QUESTION = 1001;
    private static final int MY_QUESTION = 1002;
    private static final int MESSAGE = 1003;

    @BindView(R.id.more)
    ImageView mMore;
    @BindView(R.id.message)
    ImageView mMessage;
    @BindView(R.id.redPoint)
    ImageView mRedPoint;
    Unbinder unbinder;
    @BindView(R.id.LatestListView)
    ListView mLatestListView;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.titleBar)
    RelativeLayout mTitleBar;

    private List<Miss> mMissList;
    private List<Question> mHotQuestionList;
    private List<Question> mLatestQuestionList;
    private MissListAdapter mMissListAdapter;
    private QuestionListAdapter mHotQuestionListAdapter;
    private QuestionListAdapter mLatestQuestionListAdapter;
    private Long mCreateTime;
    private int mPageSize = 20;
    private HashSet<Integer> mSet;
    private PopupWindow mPopupWindow;
    private RefreshReceiver mRefreshReceiver;
    private MediaPlayerManager mMediaPlayerManager;
    private int mPlayingID = -1;
    private TextView mHotQuestion;
    private MyListView mHotListView;
    private LinearLayout mEmpty;
    private TextView mMissEmpty;

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
        mMediaPlayerManager = new MediaPlayerManager(getActivity());

        initPopupWindow();
        initHeaderView1();
        initHeaderView2();
        initLatestQuestionList();

        requestMissList();
        requestHotQuestionList();
        requestLatestQuestionList(true);

        initSwipeRefreshLayout();
        registerRefreshReceiver();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!LocalUser.getUser().isLogin()) {
            mRedPoint.setVisibility(View.GONE);
        }
    }

    private void initPopupWindow() {
        View contentView = LayoutInflater.from(getActivity()).inflate(R.layout.view_popup_window, null);
        mPopupWindow = new PopupWindow(contentView);
        mPopupWindow.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        mPopupWindow.setFocusable(true);
        mPopupWindow.setOutsideTouchable(true);
        mPopupWindow.setBackgroundDrawable(new BitmapDrawable(getResources(), ""));
        mPopupWindow.setClippingEnabled(true);
        TextView tv1 = (TextView) contentView.findViewById(R.id.askHerQuestion);
        TextView tv2 = (TextView) contentView.findViewById(R.id.myQuestion);

        tv1.setOnClickListener(this);
        tv2.setOnClickListener(this);
    }

    private void initHeaderView1() {
        LinearLayout header = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_header_miss_talk_1, null);
        EmptyRecyclerView recyclerView = (EmptyRecyclerView) header.findViewById(R.id.recyclerView);
        mMissEmpty = (TextView) header.findViewById(R.id.missEmpty);
        mMissList = new ArrayList<>();
        mMissListAdapter = new MissListAdapter(getActivity(), mMissList);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setEmptyView(mMissEmpty);
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

    private void initHeaderView2() {
        LinearLayout header = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_header_miss_talk_2, null);
        mHotQuestion = (TextView) header.findViewById(R.id.hotQuestion);
        mHotListView = (MyListView) header.findViewById(R.id.hotListView);
        mEmpty = (LinearLayout) header.findViewById(R.id.empty);

        mHotQuestionListAdapter = new QuestionListAdapter(getActivity());
        mHotListView.setAdapter(mHotQuestionListAdapter);
        mHotListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Question item = (Question) parent.getItemAtPosition(position);
                if (item != null) {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, item.getId());
                    startActivityForResult(intent, REQ_QUESTION_DETAIL);
                }
            }
        });

        mHotQuestionListAdapter.setItemCallback(new QuestionListAdapter.Callback() {
            @Override
            public void loveOnClick(final Question item) {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                    Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

                        @Override
                        protected void onRespSuccessData(Prise prise) {
                            item.setIsPrise(prise.getIsPrise());
                            item.setPriseCount(prise.getPriseCount());
                            mHotQuestionListAdapter.notifyDataSetChanged();
                        }
                    }).fire();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
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
            public void voiceOnClick(final Question item) {
                //播放下一个之前把上一个播放位置的动画停了
                stopPreviousAnimation();

                if (!MissVoiceRecorder.isHeard(item.getId())) {
                    //没听过的
                    Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                            if (resp.isSuccess()) {
                                mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        item.setPlaying(false);
                                        mHotQuestionListAdapter.notifyDataSetChanged();
                                    }
                                });

                                MissVoiceRecorder.markHeard(item.getId());
                                item.setPlaying(true);
                                item.setListenCount(item.getListenCount() + 1);
                                mHotQuestionListAdapter.notifyDataSetChanged();
                                mPlayingID = item.getId();
                            }
                        }
                    }).fire();
                } else {
                    //听过的
                    if (mPlayingID == item.getId()) {
                        mMediaPlayerManager.release();
                        item.setPlaying(false);
                        mHotQuestionListAdapter.notifyDataSetChanged();
                        mPlayingID = -1;
                    } else {
                        mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                item.setPlaying(false);
                                mHotQuestionListAdapter.notifyDataSetChanged();
                            }
                        });
                        item.setPlaying(true);
                        mHotQuestionListAdapter.notifyDataSetChanged();
                        mPlayingID = item.getId();
                    }
                }
            }
        });

        mLatestListView.addHeaderView(header);
    }

    private void initLatestQuestionList() {
        mLatestQuestionListAdapter = new QuestionListAdapter(getActivity());
        mLatestListView.setAdapter(mLatestQuestionListAdapter);
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

        mLatestQuestionListAdapter.setItemCallback(new QuestionListAdapter.Callback() {
            @Override
            public void loveOnClick(final Question item) {
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_PRAISE);
                    Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

                        @Override
                        protected void onRespSuccessData(Prise prise) {
                            item.setIsPrise(prise.getIsPrise());
                            item.setPriseCount(prise.getPriseCount());
                            mLatestQuestionListAdapter.notifyDataSetChanged();
                        }
                    }).fire();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
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
            public void voiceOnClick(final Question item) {
                umengEventCount(UmengCountEventId.MISS_TALK_VOICE);
                //播放下一个之前把上一个播放位置的动画停了
                stopPreviousAnimation();

                if (!MissVoiceRecorder.isHeard(item.getId())) {
                    //没听过的
                    Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                        @Override
                        protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                            if (resp.isSuccess()) {
                                mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
                                    @Override
                                    public void onCompletion(MediaPlayer mp) {
                                        item.setPlaying(false);
                                        mLatestQuestionListAdapter.notifyDataSetChanged();
                                    }
                                });

                                MissVoiceRecorder.markHeard(item.getId());
                                item.setPlaying(true);
                                item.setListenCount(item.getListenCount() + 1);
                                mLatestQuestionListAdapter.notifyDataSetChanged();
                                mPlayingID = item.getId();
                            }
                        }
                    }).fire();
                } else {
                    //听过的
                    if (mPlayingID == item.getId()) {
                        mMediaPlayerManager.release();
                        item.setPlaying(false);
                        mLatestQuestionListAdapter.notifyDataSetChanged();
                        mPlayingID = -1;
                    } else {
                        mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                item.setPlaying(false);
                                mLatestQuestionListAdapter.notifyDataSetChanged();
                            }
                        });
                        item.setPlaying(true);
                        mLatestQuestionListAdapter.notifyDataSetChanged();
                        mPlayingID = item.getId();
                    }
                }
            }
        });
    }

    public void stopPreviousAnimation() {
        if (mPlayingID != -1) {
            for (int i = 0; i < mHotQuestionListAdapter.getCount(); i++) {
                Question question = mHotQuestionListAdapter.getItem(i);
                if (question != null) {
                    if (question.getId() == mPlayingID) {
                        question.setPlaying(false);
                        mHotQuestionListAdapter.notifyDataSetChanged();
                    }
                }
            }
        }

        if (mPlayingID != -1) {
            for (int i = 0; i < mLatestQuestionListAdapter.getCount(); i++) {
                Question question = mLatestQuestionListAdapter.getItem(i);
                if (question != null) {
                    if (question.getId() == mPlayingID) {
                        question.setPlaying(false);
                        mLatestQuestionListAdapter.notifyDataSetChanged();
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
                requestMissList();
                requestHotQuestionList();
                requestLatestQuestionList(true);

                //下拉刷新时关闭语音播放
                mMediaPlayerManager.release();
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
        mMediaPlayerManager.release();
        stopPreviousAnimation();

        mPlayingID = -1;
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        if (LocalUser.getUser().isLogin()) {
            requestNewMessageCount();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            stopScheduleJob();
            //不可见时停止播放和动画
            if (mMediaPlayerManager != null) {
                mMediaPlayerManager.release();
            }

            stopPreviousAnimation();
            mPlayingID = -1;
        } else {
            if (LocalUser.getUser().isLogin()) {
                startScheduleJob(10 * 1000);
            }
        }
    }

    private void requestNewMessageCount() {
        Client.getNewMessageCount().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<NewMessage>>, List<NewMessage>>() {

                    @Override
                    protected void onRespSuccessData(List<NewMessage> newMessagesList) {
                        int count = 0;
                        for (NewMessage newMessage : newMessagesList) {
                            if (newMessage.getClassify() == 4) {
                                count += newMessage.getCount();
                            }
                        }

                        if (count > 0) {
                            mRedPoint.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    protected boolean onErrorToast() {
                        return false;
                    }
                }).fire();

    }

    private void requestMissList() {
        Client.getMissList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
                    @Override
                    protected void onRespSuccessData(List<Miss> missList) {
                        updateMissList(missList);
                    }
                }).fire();
    }


    private void requestHotQuestionList() {
        Client.getHotQuestionList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        if (questionList.size() == 0) {
                            mHotQuestion.setVisibility(View.GONE);
                        } else {
                            mHotQuestion.setVisibility(View.VISIBLE);
                        }

                        mHotQuestionList = questionList;
                        updateHotQuestionList(questionList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        mHotQuestion.setVisibility(View.GONE);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void requestLatestQuestionList(final boolean isRefresh) {
        Client.getLatestQuestionList(mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        if (questionList.size() == 0) {
                            mEmpty.setVisibility(View.VISIBLE);
                            stopRefreshAnimation();
                        } else {
                            mEmpty.setVisibility(View.GONE);
                            mLatestQuestionList = questionList;
                            updateLatestQuestionList(questionList, isRefresh);
                        }
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                        mEmpty.setVisibility(View.VISIBLE);
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
        mHotQuestionListAdapter.clear();
        mHotQuestionListAdapter.addAll(questionList);
    }

    private void updateLatestQuestionList(List<Question> questionList, boolean isRefresh) {
        if (questionList == null) {
            stopRefreshAnimation();
            return;
        }

        if (questionList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mCreateTime = mLatestQuestionList.get(mLatestQuestionList.size() - 1).getCreateTime();
        }

        if (isRefresh) {
            if (mLatestQuestionListAdapter != null) {
                mLatestQuestionListAdapter.clear();
            }
        }
        stopRefreshAnimation();

        for (Question question : questionList) {
            if (mSet.add(question.getId())) {
                mLatestQuestionListAdapter.add(question);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRefreshReceiver);
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

                Glide.with(context).load(item.getPortrait())
                        .placeholder(R.drawable.ic_default_avatar_big)
                        .transform(new GlideCircleTransform(context))
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
            void loveOnClick(Question item);

            void rewardOnClick(Question item);

            void voiceOnClick(Question item);

        }

        private void setItemCallback(Callback callback) {
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
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.name)
            TextView mName;
            @BindView(R.id.askTime)
            TextView mAskTime;
            @BindView(R.id.question)
            TextView mQuestion;
            @BindView(missAvatar)
            ImageView mMissAvatar;
            @BindView(R.id.voice)
            TextView mVoice;
            @BindView(R.id.listenerNumber)
            TextView mListenerNumber;
            @BindView(R.id.loveNumber)
            TextView mLoveNumber;
            @BindView(R.id.commentNumber)
            TextView mCommentNumber;
            @BindView(R.id.ingotNumber)
            TextView mIngotNumber;
            @BindView(R.id.voiceLevel)
            View mVoiceLevel;
            @BindView(R.id.voiceArea)
            LinearLayout mVoiceArea;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final Question item, final Callback callback) {
                if (item == null) return;

                Glide.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                Glide.with(context).load(item.getCustomPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mMissAvatar);

                mName.setText(item.getUserName());
                mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
                mQuestion.setText(item.getQuestionContext());
                mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
                mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
                mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
                mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));
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
                    mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love, 0, 0, 0);
                } else {
                    mLoveNumber.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_miss_love_yellow, 0, 0, 0);
                }

                mMissAvatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Launcher.with(context, MissProfileActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, item.getAnswerCustomId())
                                .execute();
                    }
                });

                mLoveNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.loveOnClick(item);
                        }
                    }
                });

                if (!item.isPlaying()) {
                    mVoiceLevel.clearAnimation();
                    mVoiceLevel.setBackgroundResource(R.drawable.ic_voice_4);
                } else {
                    mVoiceLevel.setBackgroundResource(R.drawable.bg_play_voice);
                    AnimationDrawable animation = (AnimationDrawable) mVoiceLevel.getBackground();
                    if (animation != null) {
                        animation.start();
                    }
                }

                mVoiceArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (callback != null) {
                            callback.voiceOnClick(item);
                        }
                    }
                });
            }
        }
    }

    @OnClick({R.id.more, R.id.message})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.more:
                showPopupWindow();
                break;
            case R.id.message:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_MINE_MESSAGE);
                    Launcher.with(getActivity(), MessagesActivity.class).execute();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, MESSAGE);
                }
                mRedPoint.setVisibility(View.INVISIBLE);
                break;
        }
    }

    private void showPopupWindow() {
        if (mPopupWindow != null) {
            if (!mPopupWindow.isShowing()) {
                mPopupWindow.showAsDropDown(mMore, -(int) Display.dp2Px(90, getResources()), (int) Display.dp2Px(17, getResources()));
            } else {
                mPopupWindow.dismiss();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.askHerQuestion: {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }

                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, SUBMIT_QUESTION);
                }
            }
            break;
            case R.id.myQuestion: {
                if (mPopupWindow.isShowing()) {
                    mPopupWindow.dismiss();
                }

                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.MISS_TALK_MINE_QUESTION);
                    Launcher.with(getActivity(), MyQuestionsActivity.class).execute();
                } else {
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, MY_QUESTION);
                }
            }
            break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SUBMIT_QUESTION && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
        }

        if (requestCode == MY_QUESTION && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), MyQuestionsActivity.class).execute();
        }

        if (requestCode == MESSAGE && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), MessagesActivity.class).execute();
        }

        if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
            if (data != null) {
                Prise prise = data.getParcelableExtra(Launcher.EX_PAYLOAD);
                int replyCount = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
                int rewardCount = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
                int listenCount = data.getIntExtra(Launcher.EX_PAYLOAD_3, -1);
                if (prise != null) {
                    for (int i = 0; i < mHotQuestionListAdapter.getCount(); i++) {
                        Question question = mHotQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setIsPrise(prise.getIsPrise());
                                question.setPriseCount(prise.getPriseCount());
                                mHotQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    for (int i = 0; i < mLatestQuestionListAdapter.getCount(); i++) {
                        Question question = mLatestQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setIsPrise(prise.getIsPrise());
                                question.setPriseCount(prise.getPriseCount());
                                mLatestQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                if (replyCount != -1) {
                    for (int i = 0; i < mHotQuestionListAdapter.getCount(); i++) {
                        Question question = mHotQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setReplyCount(replyCount);
                                mHotQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    for (int i = 0; i < mLatestQuestionListAdapter.getCount(); i++) {
                        Question question = mLatestQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setReplyCount(replyCount);
                                mLatestQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                if (rewardCount != -1) {
                    for (int i = 0; i < mHotQuestionListAdapter.getCount(); i++) {
                        Question question = mHotQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setAwardCount(rewardCount);
                                mHotQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    for (int i = 0; i < mLatestQuestionListAdapter.getCount(); i++) {
                        Question question = mLatestQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setAwardCount(rewardCount);
                                mLatestQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                if (listenCount != -1) {
                    for (int i = 0; i < mHotQuestionListAdapter.getCount(); i++) {
                        Question question = mHotQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setListenCount(listenCount);
                                mHotQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    for (int i = 0; i < mLatestQuestionListAdapter.getCount(); i++) {
                        Question question = mLatestQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setListenCount(listenCount);
                                mLatestQuestionListAdapter.notifyDataSetChanged();
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRefreshReceiver, filter);
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {

                    for (int i = 0; i < mHotQuestionListAdapter.getCount(); i++) {
                        Question question = mHotQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
                                int questionRewardCount = question.getAwardCount() + 1;
                                question.setAwardCount(questionRewardCount);
                                mHotQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }

                    for (int i = 0; i < mLatestQuestionListAdapter.getCount(); i++) {
                        Question question = mLatestQuestionListAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
                                int questionRewardCount = question.getAwardCount() + 1;
                                question.setAwardCount(questionRewardCount);
                                mLatestQuestionListAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
            }

            if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                mSet.clear();
                mCreateTime = null;
                requestMissList();
                requestHotQuestionList();
                requestLatestQuestionList(true);
            }
        }
    }
}
