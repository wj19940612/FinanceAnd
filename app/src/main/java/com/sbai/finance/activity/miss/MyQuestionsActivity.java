package com.sbai.finance.activity.miss;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.drawable.AnimationDrawable;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.miss.Prise;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.miss.RewardInfo;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MediaPlayerManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.UmengCountEventIdUtils;
import com.sbai.finance.view.CustomSwipeRefreshLayout;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * 我的提问页面
 */
public class MyQuestionsActivity extends BaseActivity implements AdapterView.OnItemClickListener {

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.listView)
    ListView mListView;
    @BindView(R.id.empty)
    RelativeLayout mEmpty;
    @BindView(R.id.swipeRefreshLayout)
    CustomSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.askQuestion)
    TextView mAskQuestion;

    private MyQuestionAdapter mMyQuestionAdapter;
    private Long mCreateTime;
    private int mPageSize = 20;
    private HashSet<Integer> mSet;
    private List<Question> mMyQuestionList;
    private RefreshReceiver mRefreshReceiver;
    private MediaPlayerManager mMediaPlayerManager;
    private int mPlayingID = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);
        ButterKnife.bind(this);
        mSet = new HashSet<>();
        mMediaPlayerManager = new MediaPlayerManager(this);
        mMyQuestionList = new ArrayList<>();
        mMyQuestionAdapter = new MyQuestionAdapter(this);
        mListView.setEmptyView(mEmpty);
        mListView.setAdapter(mMyQuestionAdapter);
        mListView.setOnItemClickListener(this);
        initSwipeRefreshLayout();
        registerRefreshReceiver();

        mMyQuestionAdapter.setOnClickCallback(new MyQuestionAdapter.OnClickCallback() {
            @Override
            public void onLoveClick(final Question item) {
                umengEventCount(UmengCountEventIdUtils.MISS_TALK_PRAISE);
                Client.prise(item.getId()).setCallback(new Callback2D<Resp<Prise>, Prise>() {

                    @Override
                    protected void onRespSuccessData(Prise prise) {
                        item.setIsPrise(prise.getIsPrise());
                        item.setPriseCount(prise.getPriseCount());
                        mMyQuestionAdapter.notifyDataSetChanged();
                    }
                }).fire();
            }

            @Override
            public void onRewardClick(Question item) {
                RewardMissActivity.show(getActivity(), item.getId(), RewardInfo.TYPE_QUESTION);
            }

            @Override
            public void onVoiceClick(final Question item) {
                umengEventCount(UmengCountEventIdUtils.MISS_TALK_VOICE);
                //播放下一个之前把上一个播放位置的动画停了
                if (mPlayingID != -1) {
                    for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                        Question question = mMyQuestionAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == mPlayingID) {
                                question.setPlaying(false);
                                mMyQuestionAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

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
                                        mMyQuestionAdapter.notifyDataSetChanged();
                                    }
                                });

                                MissVoiceRecorder.markHeard(item.getId());
                                item.setPlaying(true);
                                item.setListenCount(item.getListenCount() + 1);
                                mMyQuestionAdapter.notifyDataSetChanged();
                                mPlayingID = item.getId();
                            }
                        }
                    }).fire();
                } else {
                    //听过的
                    if (mPlayingID == item.getId()) {
                        mMediaPlayerManager.release();
                        item.setPlaying(false);
                        mMyQuestionAdapter.notifyDataSetChanged();
                        mPlayingID = -1;
                    } else {
                        mMediaPlayerManager.play(item.getAnswerContext(), new MediaPlayer.OnCompletionListener() {
                            @Override
                            public void onCompletion(MediaPlayer mp) {
                                item.setPlaying(false);
                                mMyQuestionAdapter.notifyDataSetChanged();
                            }
                        });
                        item.setPlaying(true);
                        mMyQuestionAdapter.notifyDataSetChanged();
                        mPlayingID = item.getId();
                    }
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        requestMyQuestionList();
    }

    @Override
    protected void onPause() {
        super.onPause();
        //锁屏或者在后台运行或者跳转页面时停止播放和动画
        mMediaPlayerManager.release();
        if (mPlayingID != -1) {
            for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                Question question = mMyQuestionAdapter.getItem(i);
                if (question != null) {
                    if (question.getId() == mPlayingID) {
                        question.setPlaying(false);
                        mMyQuestionAdapter.notifyDataSetChanged();
                    }
                }
            }
        }
        mPlayingID = -1;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRefreshReceiver);
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mSet.clear();
                mCreateTime = null;
                requestMyQuestionList();
                //下拉刷新时关闭语音播放
                mMediaPlayerManager.release();
                mPlayingID = -1;
            }
        });

        mSwipeRefreshLayout.setOnLoadMoreListener(new CustomSwipeRefreshLayout.OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                mListView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        requestMyQuestionList();
                    }
                }, 1000);
            }
        });
    }

    private void requestMyQuestionList() {
        Client.getMyQuestionList(mCreateTime, mPageSize).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Question>>, List<Question>>() {
                    @Override
                    protected void onRespSuccessData(List<Question> questionList) {
                        mMyQuestionList = questionList;
                        updateMyQuestionList(questionList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        stopRefreshAnimation();
                    }
                }).fire();
    }

    private void updateMyQuestionList(final List<Question> questionList) {
        if (questionList == null) {
            stopRefreshAnimation();
            return;
        }

        if (questionList.size() < mPageSize) {
            mSwipeRefreshLayout.setLoadMoreEnable(false);
        } else {
            mCreateTime = mMyQuestionList.get(mMyQuestionList.size() - 1).getCreateTime();
        }

        if (mSwipeRefreshLayout.isRefreshing()) {
            if (mMyQuestionAdapter != null) {
                mMyQuestionAdapter.clear();
            }
        }
        stopRefreshAnimation();

        for (Question question : questionList) {
            if (mSet.add(question.getId())) {
                mMyQuestionAdapter.add(question);
            }
        }
    }

    private void stopRefreshAnimation() {
        if (mSwipeRefreshLayout.isRefreshing()) {
            mSwipeRefreshLayout.setRefreshing(false);
        }

        if (mSwipeRefreshLayout.isLoading()) {
            mSwipeRefreshLayout.setLoading(false);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Question item = (Question) parent.getItemAtPosition(position);
        if (item != null && item.getSolve() == 1) {
            Launcher.with(getActivity(), QuestionDetailActivity.class)
                    .putExtra(Launcher.EX_PAYLOAD, item.getId()).executeForResult(REQ_QUESTION_DETAIL);
        }
    }

    @OnClick(R.id.askQuestion)
    public void onViewClicked() {
        Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
    }

    static class MyQuestionAdapter extends ArrayAdapter<Question> {

        private Context mContext;
        private OnClickCallback mOnClickCallback;

        public void setOnClickCallback(OnClickCallback onClickCallback) {
            mOnClickCallback = onClickCallback;
        }

        interface OnClickCallback {
            void onLoveClick(Question item);

            void onRewardClick(Question item);

            void onVoiceClick(Question item);
        }

        private MyQuestionAdapter(@NonNull Context context) {
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

            viewHolder.bindingData(mContext, getItem(position), mOnClickCallback, position);
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
            @BindView(R.id.missAvatar)
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
            @BindView(R.id.label)
            LinearLayout mLabel;
            @BindView(R.id.noMissReply)
            TextView mNoMissReply;
            @BindView(R.id.voiceLevel)
            View mVoiceLevel;
            @BindView(R.id.voiceArea)
            LinearLayout mVoiceArea;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final Question item,
                                    final OnClickCallback onClickCallback, final int position) {
                if (item == null) return;

                GlideApp.with(context).load(item.getUserPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mAvatar);

                GlideApp.with(context).load(item.getCustomPortrait())
                        .placeholder(R.drawable.ic_default_avatar)
                        .transform(new GlideCircleTransform(context))
                        .into(mMissAvatar);

                if (item.getSolve() == 0) {
                    mLabel.setVisibility(View.GONE);
                    mVoiceArea.setVisibility(View.GONE);
                    mMissAvatar.setVisibility(View.GONE);
                    mListenerNumber.setVisibility(View.GONE);
                    mNoMissReply.setVisibility(View.VISIBLE);
                } else {
                    mLabel.setVisibility(View.VISIBLE);
                    mLabel.setVisibility(View.VISIBLE);
                    mVoiceArea.setVisibility(View.VISIBLE);
                    mMissAvatar.setVisibility(View.VISIBLE);
                    mListenerNumber.setVisibility(View.VISIBLE);
                    mNoMissReply.setVisibility(View.GONE);
                }

                mName.setText(item.getUserName());
                mAskTime.setText(DateUtil.getFormatSpecialSlashNoHour(item.getCreateTime()));
                mQuestion.setText(item.getQuestionContext());
                mVoice.setText(context.getString(R.string.voice_time, item.getSoundTime()));
                mListenerNumber.setText(context.getString(R.string.listener_number, StrFormatter.getFormatCount(item.getListenCount())));
                mLoveNumber.setText(StrFormatter.getFormatCount(item.getPriseCount()));
                mCommentNumber.setText(StrFormatter.getFormatCount(item.getReplyCount()));
                mIngotNumber.setText(StrFormatter.getFormatCount(item.getAwardCount()));

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

                mIngotNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickCallback != null) {
                            onClickCallback.onRewardClick(item);
                        }
                    }
                });

                mLoveNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onClickCallback != null) {
                            onClickCallback.onLoveClick(item);
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
                        if (onClickCallback != null) {
                            onClickCallback.onVoiceClick(item);
                        }
                    }
                });
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
            if (data != null) {
                Prise prise = data.getParcelableExtra(Launcher.EX_PAYLOAD);
                int replyCount = data.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
                int rewardCount = data.getIntExtra(Launcher.EX_PAYLOAD_2, -1);
                int listenCount = data.getIntExtra(Launcher.EX_PAYLOAD_3, -1);
                if (prise != null) {
                    for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                        Question question = mMyQuestionAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setIsPrise(prise.getIsPrise());
                                question.setPriseCount(prise.getPriseCount());
                                mMyQuestionAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                if (replyCount != -1) {
                    for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                        Question question = mMyQuestionAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setReplyCount(replyCount);
                                mMyQuestionAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                if (rewardCount != -1) {
                    for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                        Question question = mMyQuestionAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setAwardCount(rewardCount);
                                mMyQuestionAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                }

                if (listenCount != -1) {
                    for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                        Question question = mMyQuestionAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == data.getIntExtra(Launcher.QUESTION_ID, -1)) {
                                question.setListenCount(listenCount);
                                mMyQuestionAdapter.notifyDataSetChanged();
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
        LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mRefreshReceiver, filter);
    }

    private class RefreshReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
                if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {
                    for (int i = 0; i < mMyQuestionAdapter.getCount(); i++) {
                        Question question = mMyQuestionAdapter.getItem(i);
                        if (question != null) {
                            if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
                                int questionRewardCount = question.getAwardCount() + 1;
                                question.setAwardCount(questionRewardCount);
                                mMyQuestionAdapter.notifyDataSetChanged();
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
}
