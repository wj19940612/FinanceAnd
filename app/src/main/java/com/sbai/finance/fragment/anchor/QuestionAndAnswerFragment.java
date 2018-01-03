package com.sbai.finance.fragment.anchor;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.anchor.CommentActivity;
import com.sbai.finance.activity.anchor.MissProfileDetailActivity;
import com.sbai.finance.activity.anchor.QuestionDetailActivity;
import com.sbai.finance.activity.anchor.radio.RadioStationPlayActivity;
import com.sbai.finance.fragment.MediaPlayFragment;
import com.sbai.finance.model.anchor.Anchor;
import com.sbai.finance.model.anchor.Praise;
import com.sbai.finance.model.anchor.Question;
import com.sbai.finance.model.anchor.RewardInfo;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.OnItemClickListener;
import com.sbai.finance.utils.OnPageSelectedListener;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.view.EmptyRecyclerView;
import com.sbai.finance.view.MissFloatWindow;
import com.sbai.finance.view.VerticalSwipeRefreshLayout;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

import static com.sbai.finance.activity.BaseActivity.ACTION_LOGIN_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_LOGOUT_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.ACTION_REWARD_SUCCESS;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

/**
 * 米圈
 */
public class QuestionAndAnswerFragment extends MediaPlayFragment implements MissAskFragment.OnMissAskPageListener {


    Unbinder unbinder;

    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.missFloatWindow)
    MissFloatWindow mMissFloatWindow;
    private MissListAdapter mMissListAdapter;
    private MissAskFragmentAdapter mMissAskFragmentAdapter;
    private boolean mSwipeRefreshEnable = true;
    private int mVerticalOffset;

    private Radio mRadio;
    private MediaPlayService mMediaPlayService;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_question_and_answer, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mRootMissFloatWindow = mMissFloatWindow;
        initView();
        initFloatView();
    }


    private void changeFloatWindowView() {
        MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
        if (mMissFloatWindow != null) {
            if (audio instanceof Question) {
                Question playQuestion = (Question) audio;
                mMissFloatWindow.setMissAvatar(playQuestion.getCustomPortrait());
            } else if (audio instanceof Radio) {
                mMissFloatWindow.setMissAvatar(((Radio) audio).getUserPortrait());
            }
            if (!MissAudioManager.get().isPlaying()) {
                mMissFloatWindow.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestMissList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            requestMissList();
        }
    }

    @Override
    public IntentFilter getIntentFilter() {
        IntentFilter intentFilter = super.getIntentFilter();
        intentFilter.addAction(ACTION_LOGIN_SUCCESS);
        intentFilter.addAction(ACTION_LOGOUT_SUCCESS);
        intentFilter.addAction(ACTION_REWARD_SUCCESS);
        intentFilter.addAction(CommentActivity.BROADCAST_ACTION_REPLY_SUCCESS);
        return intentFilter;
    }

    @Override
    public void onMediaPlay(int IAudioId, int source) {
        changeFloatWindowView();
        notifyFragmentDataSetChange(source);
    }

    @Override
    public void onMediaPlayStart(int IAudioId, int source) {
        notifyFragmentDataSetChange(source);
    }

    @Override
    public void onMediaPlayResume(int IAudioId, int source) {
        notifyFragmentDataSetChange(source);
    }

    @Override
    public void onMediaPlayPause(int IAudioId, int source) {
        mMissFloatWindow.setVisibility(View.GONE);
        notifyFragmentDataSetChange(source);
    }

    @Override
    protected void onMediaPlayStop(int IAudioId, int source) {
        mMissFloatWindow.setVisibility(View.GONE);
        notifyFragmentDataSetChange(source);
    }

    @Override
    public void onOtherReceive(Context context, Intent intent) {
        super.onOtherReceive(context, intent);
        if (ACTION_LOGIN_SUCCESS.equalsIgnoreCase(intent.getAction())
                || ACTION_LOGOUT_SUCCESS.equalsIgnoreCase(intent.getAction())) {
            MissAskFragment missHotQuestionFragment = getMissHotQuestionFragment();
            if (missHotQuestionFragment != null) {
                missHotQuestionFragment.refreshData();
            }

            MissAskFragment missLatestQuestionFragment = getMissLatestQuestionFragment();
            if (missLatestQuestionFragment != null) {
                missLatestQuestionFragment.refreshData();
            }
        }

        if (ACTION_REWARD_SUCCESS.equalsIgnoreCase(intent.getAction())) {
            int rewardType = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
            int rewardId = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);

            if (rewardType == RewardInfo.TYPE_QUESTION && rewardId != -1) {
                MissAskFragment missHotQuestionFragment = getMissHotQuestionFragment();
                if (missHotQuestionFragment != null) {
                    missHotQuestionFragment.updateRewardInfo(rewardId);
                }

                MissAskFragment missLatestQuestionFragment = getMissLatestQuestionFragment();
                if (missLatestQuestionFragment != null) {
                    missLatestQuestionFragment.updateRewardInfo(rewardId);
                }
            }
        }
    }


    public void setService(MediaPlayService mediaPlayService) {
        mMediaPlayService = mediaPlayService;
        MissAskFragment missLatestQuestionFragment = getMissLatestQuestionFragment();
        if (missLatestQuestionFragment != null) {
            missLatestQuestionFragment.setService(mediaPlayService);
        }
        MissAskFragment missHotQuestionFragment = getMissHotQuestionFragment();
        if (missHotQuestionFragment != null) {
            missHotQuestionFragment.setService(mediaPlayService);
        }
    }

    private void notifyFragmentDataSetChange(int source) {
        if (source != MediaPlayService.MEDIA_SOURCE_HOT_QUESTION) {
            MissAskFragment missHotAskFragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(0);
            if (missHotAskFragment != null) {
                missHotAskFragment.notifyFragmentDataSetChange();
            }
            MissAskFragment missLatestAskFragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(1);
            if (missLatestAskFragment != null) {
                missLatestAskFragment.notifyFragmentDataSetChange();
            }
        }
    }


    private void initView() {
        initSwipeRefreshLayout();

        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 1);
        gridLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mMissListAdapter = new MissListAdapter(getActivity(), new ArrayList<Anchor>());
        mRecyclerView.setAdapter(mMissListAdapter);
        mMissListAdapter.setOnItemClickListener(new OnItemClickListener<Anchor>() {
            @Override
            public void onItemClick(Anchor anchor, int position) {
                if (anchor != null) {
                    Launcher.with(getActivity(), MissProfileDetailActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, anchor.getId()).execute();
                }
            }
        });

        mMissAskFragmentAdapter = new MissAskFragmentAdapter(getChildFragmentManager(), getActivity(), this);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(mMissAskFragmentAdapter);
        mViewPager.post(new Runnable() {
            @Override
            public void run() {
                setVisibleFragmentLabel(0);
            }
        });
        mViewPager.addOnPageChangeListener(mOnPageSelectedListener);

        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mSlidingTabLayout.setSelectedIndicatorHeight(2);
        mSlidingTabLayout.setCustomTabView(R.layout.view_question_and_answer_title, R.id.questionAndAnswerTitle);
        mSlidingTabLayout.setViewPager(mViewPager);

        mAppBarLayout.addOnOffsetChangedListener(mOnOffsetChangedListener);
        mSlidingTabLayout.highlightItem(0);
    }

    private OnPageSelectedListener mOnPageSelectedListener = new OnPageSelectedListener() {
        @Override
        public void onPageSelected(int position) {
            setVisibleFragmentLabel(position);
            MissAskFragment fragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(position);
            if (fragment != null) {
                fragment.refreshData();
            }
        }
    };

    private void setVisibleFragmentLabel(int position) {
        MissAskFragment missAskFragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(position);
        if (missAskFragment != null) {
            missAskFragment.setSelectFragment(position);
        }

        int other = position == 0 ? 1 : 0;
        MissAskFragment otherMissAskFragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(other);
        if (otherMissAskFragment != null) {
            otherMissAskFragment.setSelectFragment(-1);
        }
    }

    private AppBarLayout.OnOffsetChangedListener mOnOffsetChangedListener = new AppBarLayout.OnOffsetChangedListener() {

        @Override
        public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
            mVerticalOffset = verticalOffset;
            boolean b = mVerticalOffset >= 0 && mSwipeRefreshEnable;
            if (mSwipeRefreshLayout.isEnabled() != b) {
                mSwipeRefreshLayout.setEnabled(b);
            }
        }
    };

    @Override
    public void onStart() {
        super.onStart();
        MissAskFragment missHotQuestionFragment = getMissHotQuestionFragment();
        if (missHotQuestionFragment != null) {
            missHotQuestionFragment.setOnMissAskPageListener(this);
        }

        MissAskFragment missLatestQuestionFragment = getMissLatestQuestionFragment();
        if (missLatestQuestionFragment != null) {
            missLatestQuestionFragment.setOnMissAskPageListener(this);
        }
    }

    private void initFloatView() {
        mMissFloatWindow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
                if (audio != null && MissAudioManager.get().isStarted(audio)) {
                    if (audio instanceof Question) {
                        Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
                        intent.putExtra(ExtraKeys.IS_FROM_MISS_TALK, true);
                        intent.putExtra(Launcher.EX_PAYLOAD, ((Question) audio).getId());
                        startActivityForResult(intent, REQ_QUESTION_DETAIL);

                        umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
                    } else {
                        Launcher.with(getContext(), RadioStationPlayActivity.class)
                                .putExtra(ExtraKeys.RADIO, (Radio) audio)
                                .execute();
                    }
                }

            }
        });
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMissList();
                MissAskFragment missHotQuestionFragment = getMissHotQuestionFragment();
                if (missHotQuestionFragment != null) {
                    missHotQuestionFragment.refreshData();
                }

                MissAskFragment missLatestQuestionFragment = getMissLatestQuestionFragment();
                if (missLatestQuestionFragment != null) {
                    missLatestQuestionFragment.refreshData();
                }
            }
        });
    }

    @Override
    public void onSwipeRefreshEnable(boolean swipeFreshEnable) {
        mSwipeRefreshEnable = swipeFreshEnable;
        boolean b = mVerticalOffset >= 0 && mSwipeRefreshEnable;
        if (mSwipeRefreshLayout.isEnabled() != b) {
            mSwipeRefreshLayout.setEnabled(b);
        }
    }

    @Override
    public void onRadioPlay(Question question, boolean radioPlayViewHasHasFocus, int source) {
//        mPlayPage = (source == MediaPlayService.MEDIA_SOURCE_HOT_QUESTION && mPosition == 0)
//                || (source == MediaPlayService.MEDIA_SOURCE_LATEST_QUESTION && mPosition == 1);
        if (radioPlayViewHasHasFocus
                && mMissFloatWindow.getVisibility() == View.VISIBLE) {
            mMissFloatWindow.setVisibility(View.GONE);
        }

        if (!radioPlayViewHasHasFocus
                && mMissFloatWindow.getVisibility() == View.GONE) {
            mMissFloatWindow.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onChangeMissFloatWindow(boolean hideFloatWindow) {
        if (hideFloatWindow && mMissFloatWindow != null) {
            mMissFloatWindow.setVisibility(View.GONE);
        }
        changeFloatWindowView();
    }

    @Override
    public void onOpenQuestionPage(Question question) {
        if (question != null) {
            Intent intent = new Intent(getActivity(), QuestionDetailActivity.class);
            intent.putExtra(Launcher.EX_PAYLOAD, question.getId());
            intent.putExtra(ExtraKeys.IS_FROM_MISS_TALK, true);
            startActivityForResult(intent, REQ_QUESTION_DETAIL);
            umengEventCount(UmengCountEventId.MISS_TALK_QUESTION_DETAIL);
        }
    }


    private MissAskFragment getMissHotQuestionFragment() {
        return (MissAskFragment) mMissAskFragmentAdapter.getFragment(0);
    }

    private MissAskFragment getMissLatestQuestionFragment() {
        return (MissAskFragment) mMissAskFragmentAdapter.getFragment(1);
    }


    private void requestMissList() {
        Client.getMissList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Anchor>>, List<Anchor>>() {
                    @Override
                    protected void onRespSuccessData(List<Anchor> anchorList) {
                        mMissListAdapter.clear();
                        mMissListAdapter.addAll(anchorList);
                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        if (mSwipeRefreshLayout.isRefreshing()) {
                            mSwipeRefreshLayout.setRefreshing(false);
                        }
                    }
                }).fireFree();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mViewPager.removeOnPageChangeListener(mOnPageSelectedListener);
        mOnOffsetChangedListener = null;
        unbinder.unbind();
        stopScheduleJob();
    }

    public static class MissListAdapter extends RecyclerView.Adapter<MissListAdapter.ViewHolder> {

        private OnItemClickListener mOnItemClickListener;

        public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
            this.mOnItemClickListener = onItemClickListener;
        }

        private List<Anchor> mAnchorList;
        private Context mContext;
        private LayoutInflater mLayoutInflater;

        public MissListAdapter(Context context, List<Anchor> anchorList) {
            this.mAnchorList = anchorList;
            this.mContext = context;
            mLayoutInflater = LayoutInflater.from(context);
        }

        public void clear() {
            mAnchorList.clear();
            notifyItemRangeRemoved(0, mAnchorList.size());
        }

        public void addAll(List<Anchor> anchorList) {
            mAnchorList.addAll(anchorList);
            notifyDataSetChanged();
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mLayoutInflater.inflate(R.layout.row_misstalk_list, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mContext, mAnchorList.get(position), mOnItemClickListener, position);
        }

        @Override
        public int getItemCount() {
            return mAnchorList != null ? mAnchorList.size() : 0;
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

            public void bindDataWithView(Context context, final Anchor item, final OnItemClickListener onItemClickListener, final int position) {
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

        if (requestCode == REQ_QUESTION_DETAIL && resultCode == BaseActivity.RESULT_OK) {
            if (data != null) {
                Question question = data.getParcelableExtra(ExtraKeys.QUESTION);
                Praise praise = data.getParcelableExtra(ExtraKeys.PRAISE);
                if (question != null && praise != null) {
                    question.setIsPrise(praise.getIsPrise());
                    question.setPriseCount(praise.getPriseCount());
                }
                updateFragmentPageQuestion(question);
            }
        }
    }

    private void updateFragmentPageQuestion(Question question) {
        if (question != null) {
            MissAskFragment missHotAskFragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(0);
            missHotAskFragment.updateQuestion(question);
            MissAskFragment missLatestAskFragment = (MissAskFragment) mMissAskFragmentAdapter.getFragment(1);
            missLatestAskFragment.updateQuestion(question);
        }
    }

    private static class MissAskFragmentAdapter extends FragmentPagerAdapter {
        private Context mContext;
        private FragmentManager mFragmentManager;
        private MissAskFragment.OnMissAskPageListener mOnMissAskPageListener;

        public MissAskFragmentAdapter(FragmentManager fm, Context context, MissAskFragment.OnMissAskPageListener onMissAskPageListener) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
            this.mOnMissAskPageListener = onMissAskPageListener;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return MissAskFragment.newInstance(MissAskFragment.MISS_ASK_TYPE_HOT, mOnMissAskPageListener);
                case 1:
                    return MissAskFragment.newInstance(MissAskFragment.MISS_ASK_TYPE_LATEST, mOnMissAskPageListener);
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

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

}
