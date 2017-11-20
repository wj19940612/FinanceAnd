package com.sbai.finance.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.fragment.miss.MissAskFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.MissVoiceRecorder;
import com.sbai.finance.utils.OnItemClickListener;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGOUT_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_CODE_LOGIN;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

public class MissTalkFragment extends BaseFragment implements MissAudioManager.OnAudioListener {


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.viewSwitcher)
    ViewSwitcher mViewSwitcher;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.missFloatWindow)
    MissFloatWindow mMissFloatWindow;
    private RefreshReceiver mRefreshReceiver;

    Unbinder unbinder;
    private MissListAdapter mMissListAdapter;
    private MissAskFragmentAdapter mMissAskFragmentAdapter;

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
        initView();
        initFloatView();

        requestMissList();

        initSwipeRefreshLayout();
        registerRefreshReceiver();

        MissAudioManager.get().addAudioListener(this);
        mSwipeRefreshLayout.setEnabled(false);
    }

    private void initView() {
        initTitleBar();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
//        recyclerView.setEmptyView(emptyView);
        mMissListAdapter = new MissListAdapter(getActivity(), new ArrayList<Miss>());
        mRecyclerView.setAdapter(mMissListAdapter);
        mMissListAdapter.setOnItemClickListener(new OnItemClickListener<Miss>() {
            @Override
            public void onItemClick(Miss miss, int position) {
                if (miss != null) {
                    MissAudioManager.get().stop();
                    Launcher.with(getActivity(), MissProfileActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, miss.getId()).execute();
                }
            }
        });

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setPadding(Display.dp2Px(15, getResources()));
        mSlidingTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(35, getResources()));
        mSlidingTabLayout.setSelectedIndicatorHeight(3);
        mSlidingTabLayout.setTabViewTextSize(16);
        mSlidingTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
        mMissAskFragmentAdapter = new MissAskFragmentAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mMissAskFragmentAdapter);
        mSlidingTabLayout.setViewPager(mViewPager);
    }

    private void initFloatView() {
        mMissFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
                if (audio instanceof Question && MissAudioManager.get().isStarted(audio)) {
                    Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                    intent.putExtra(ExtraKeys.IS_FROM_MISS_TALK, true);
                    intent.putExtra(Launcher.EX_PAYLOAD, ((Question) audio).getId());
                    startActivityForResult(intent, REQ_QUESTION_DETAIL);

                    umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
                }
            }
        });
    }

    private void initTitleBar() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
                } else {
                    MissAudioManager.get().stop();
                    Intent intent = new Intent(getActivity(), LoginActivity.class);
                    startActivityForResult(intent, REQ_CODE_LOGIN);
                }
            }
        });
    }

    private View createMissHeaderView() {
        LinearLayout header = (LinearLayout) LayoutInflater.from(getActivity()).inflate(R.layout.view_header_miss_talk_1, null);
        EmptyRecyclerView recyclerView = (EmptyRecyclerView) header.findViewById(R.id.recyclerView);
        TextView emptyView = (TextView) header.findViewById(R.id.missEmpty);

        return header;
    }


    @Override
    public void onTimeUp(int count) {
//        int firstVisiblePosition = mListView.getFirstVisiblePosition();
//        int lastVisiblePosition = mListView.getLastVisiblePosition();
//        boolean visibleItemsStarted = false;
//
//        for (int i = firstVisiblePosition; i <= lastVisiblePosition; i++) {
//            // Skip header && footer
//            if (i == 0 || i - 1 >= mQuestionListAdapter.getCount()) continue;
//
//            Question question = mQuestionListAdapter.getItem(i - 1);
//            if (question == null) return;
//
//            if (MissAudioManager.get().isStarted(question)) {
//                View view = mListView.getChildAt(i - firstVisiblePosition);
//                ImageView playImage = (ImageView) view.findViewById(R.id.playImage);
//                TextView soundTime = (TextView) view.findViewById(R.id.soundTime);
//                ProgressBar progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
//                playImage.setImageResource(R.drawable.ic_pause);
//                progressBar.setMax(question.getSoundTime() * 1000);
//                int pastTime = MissAudioManager.get().getCurrentPosition();
//                soundTime.setText(getString(R.string._seconds, (question.getSoundTime() * 1000 - pastTime) / 1000));
//                progressBar.setProgress(pastTime);
//                visibleItemsStarted = true;
//            }
//        }
//
//        if (visibleItemsStarted && mMissFloatWindow.getVisibility() == View.VISIBLE) {
//            mMissFloatWindow.setVisibility(View.GONE);
//        }
//
//        if (!visibleItemsStarted && mMissFloatWindow.getVisibility() == View.GONE) {
//            mMissFloatWindow.setVisibility(View.VISIBLE);
//        }
    }

    private void updateQuestionListenCount(final Question item) {
        if (!MissVoiceRecorder.isHeard(item.getId())) {
            Client.listen(item.getId()).setTag(TAG).setCallback(new Callback<Resp<JsonPrimitive>>() {
                @Override
                protected void onRespSuccess(Resp<JsonPrimitive> resp) {
                    if (resp.isSuccess()) {
                        MissVoiceRecorder.markHeard(item.getId());
                        item.setListenCount(item.getListenCount() + 1);
//                        mQuestionListAdapter.notifyDataSetChanged();
                    }
                }

                @Override
                protected void onRespFailure(Resp failedResp) {
                    if (failedResp.getCode() == Resp.CODE_LISTENED) {
                        MissVoiceRecorder.markHeard(item.getId());
//                        mQuestionListAdapter.notifyDataSetChanged();
                    }
                }
            }).fire();
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                mCreateTime = null;
//                requestMissList();
            }
        });

    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        if (!isVisibleToUser) {
            MissAudioManager.get().stop();
        }
    }

    @Override
    public void onAudioStart() {
//        mQuestionListAdapter.notifyDataSetChanged();
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (audio instanceof Question) {
            mMissFloatWindow.setMissAvatar(((Question) audio).getCustomPortrait());
        }
    }

    @Override
    public void onAudioPlay() {
        startScheduleJob(100);
        mMissFloatWindow.startAnim();
    }

    @Override
    public void onAudioPause() {
        stopScheduleJob();

        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
//        mMissAskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAudioResume() {
        startScheduleJob(100);

        mMissFloatWindow.startAnim();
//        mMissAskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAudioStop() {
        stopScheduleJob();

        mMissFloatWindow.stopAnim();
        mMissFloatWindow.setVisibility(View.GONE);
//        mMissAskAdapter.notifyDataSetChanged();
    }

    @Override
    public void onAudioError() {
        ToastUtil.show(R.string.play_failure);
    }

    private void requestMissList() {
        Client.getMissList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
                    @Override
                    protected void onRespSuccessData(List<Miss> missList) {
                        mMissListAdapter.clear();
                        mMissListAdapter.addAll(missList);
                    }
                }).fireFree();
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
    public void onStop() {
        super.onStop();
        if (!Preference.get().isForeground()) {
            MissAudioManager.get().stop();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
        MissAudioManager.get().removeAudioListener(this);
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mRefreshReceiver);
    }


    public static class MissListAdapter extends RecyclerView.Adapter<MissListAdapter.ViewHolder> {

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
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
            holder.bindDataWithView(mContext, mMissList.get(position), mOnItemClickListener, position);
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

            public void bindDataWithView(Context context, final Miss item, final OnItemClickListener onItemClickListener, final int position) {
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
                            onItemClickListener.onItemClick(item, position);
                        }
                    }
                });
            }
        }
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_CODE_LOGIN && resultCode == RESULT_OK) {
            Launcher.with(getActivity(), SubmitQuestionActivity.class).execute();
        }


        if (requestCode == REQ_QUESTION_DETAIL && resultCode == RESULT_OK) {
//            if (data != null) {
//                Question question = data.getParcelableExtra(ExtraKeys.QUESTION);
//                if (question != null) {
//                    for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
//                        Question item = mQuestionListAdapter.getItem(i);
//                        if (item != null && question.getId() == item.getId()) {
//                            item.setIsPrise(question.getIsPrise());
//                            item.setPriseCount(question.getPriseCount());
//                            item.setReplyCount(question.getReplyCount());
//                            item.setAwardCount(question.getAwardCount());
//                            item.setListenCount(question.getListenCount());
//                        }
//
//                        if (item != null) {
//                            if (MissAudioManager.get().isStarted(item)) {
//                                startScheduleJob(100);
//                                MissAudioManager.get().setOnCompletedListener(new MissAudioManager.OnCompletedListener() {
//                                    @Override
//                                    public void onCompleted(String url) {
//                                        mMissFloatWindow.setVisibility(View.GONE);
//                                        mMissFloatWindow.stopAnim();
//                                        mQuestionListAdapter.notifyDataSetChanged();
//                                        stopScheduleJob();
//                                    }
//                                });
//                            }
//                        }
//                    }
//                    mQuestionListAdapter.notifyDataSetChanged();
//                }
//            }
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
//            if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
//                if (intent.getIntExtra(Launcher.EX_PAYLOAD, -1) == RewardInfo.TYPE_QUESTION) {
//
//                    for (int i = 0; i < mQuestionListAdapter.getCount(); i++) {
//                        Question question = mQuestionListAdapter.getItem(i);
//                        if (question != null) {
//                            if (question.getId() == intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1)) {
//                                int questionRewardCount = question.getAwardCount() + 1;
//                                question.setAwardCount(questionRewardCount);
//                                mQuestionListAdapter.notifyDataSetChanged();
//                            }
//                        }
//                    }
//                }
//            }
//
//            if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())
//                    || ACTION_LOGOUT_SUCCESS.equalsIgnoreCase(intent.getAction())) {
//                mCreateTime = null;
//                requestMissList();
//                requestHotQuestionList();
//            }
        }
    }

    private static class MissAskFragmentAdapter extends FragmentPagerAdapter {
        private Context mContext;

        public MissAskFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MissAskFragment.newInstance(MissAskFragment.MISS_ASK_TYPE_HOT);
                case 1:
                    return MissAskFragment.newInstance(MissAskFragment.MISS_ASK_TYPE_LATEST);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.hot_question);
                case 1:
                    return mContext.getString(R.string.latest_question);
            }
            return super.getPageTitle(position);
        }
    }

}
