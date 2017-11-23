package com.sbai.finance.fragment;


import android.content.Context;
import android.content.Intent;
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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.MissProfileActivity;
import com.sbai.finance.activity.miss.QuestionDetailActivity;
import com.sbai.finance.activity.miss.SubmitQuestionActivity;
import com.sbai.finance.activity.miss.radio.RadioStationPlayActivityActivity;
import com.sbai.finance.fragment.miss.MissAskFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.model.miss.MissSwitcherModel;
import com.sbai.finance.model.miss.Question;
import com.sbai.finance.model.radio.Radio;
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
import com.sbai.finance.view.radio.MissRadioLayout;
import com.sbai.finance.view.MissRadioViewSwitcher;
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
import static com.sbai.finance.activity.BaseActivity.REQ_CODE_LOGIN;
import static com.sbai.finance.activity.BaseActivity.REQ_QUESTION_DETAIL;

public class MissTalkFragment extends BaseFragment implements MissAudioManager.OnAudioListener, MissAskFragment.OnSwipeRefreshEnableListener {


    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    EmptyRecyclerView mRecyclerView;
    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.swipeRefreshLayout)
    VerticalSwipeRefreshLayout mSwipeRefreshLayout;
    @BindView(R.id.missFloatWindow)
    MissFloatWindow mMissFloatWindow;
    @BindView(R.id.missRadioViewSwitcher)
    MissRadioViewSwitcher mMissRadioViewSwitcher;
    @BindView(R.id.missRadioLayout)
    MissRadioLayout mMissRadioLayout;
    @BindView(R.id.appBarLayout)
    AppBarLayout mAppBarLayout;

    Unbinder unbinder;
    private MissListAdapter mMissListAdapter;
    private MissAskFragmentAdapter mMissAskFragmentAdapter;
    private boolean mSwipeRefreshEnable = true;
    private int mVerticalOffset;

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
        MissAudioManager.get().addAudioListener(this);
        requestRadioList();
        requestMissSwitcherList();
    }

    private void requestMissSwitcherList() {
        Client.requestMissSwitcherList()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<MissSwitcherModel>>, List<MissSwitcherModel>>() {
                    @Override
                    protected void onRespSuccessData(List<MissSwitcherModel> data) {

                    }

                    @Override
                    public void onFinish() {
                        super.onFinish();
                        // TODO: 2017/11/21 测试数据
                        ArrayList<MissSwitcherModel> missSwitcherModels = new ArrayList<>();
                        for (int i = 0; i < 10; i++) {
                            MissSwitcherModel missSwitcherModel = new MissSwitcherModel();
                            missSwitcherModel.setData(i + "啦啦啦范德萨范德萨发范德萨范德萨发的都是发的发的说法佛挡杀佛" + i);
                            missSwitcherModels.add(missSwitcherModel);
                        }
                        mMissRadioViewSwitcher.setSwitcherData(missSwitcherModels);
                    }
                })
                .fireFree();
    }

    private void requestRadioList() {
        Client.requestRadioList()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Radio>>, List<Radio>>() {
                    @Override
                    protected void onRespSuccessData(List<Radio> data) {
                        mMissRadioLayout.setMissRadioList(data);
                    }
                })
                .fireFree();
    }

    @Override
    public void onResume() {
        super.onResume();
        requestMissList();
    }

    private void initView() {
        initTitleBar();
        initSwipeRefreshLayout();

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

        mMissAskFragmentAdapter = new MissAskFragmentAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mViewPager.setAdapter(mMissAskFragmentAdapter);


        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setPadding(Display.dp2Px(15, getResources()));
        mSlidingTabLayout.setSelectedIndicatorPadding((int) Display.dp2Px(60, getResources()));
        mSlidingTabLayout.setSelectedIndicatorHeight(2);
        mSlidingTabLayout.setTabViewTextSize(16);
        mSlidingTabLayout.setTabViewTextColor(ContextCompat.getColorStateList(getActivity(), R.color.sliding_tab_text));
        mSlidingTabLayout.setViewPager(mViewPager);


        mAppBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                mVerticalOffset = verticalOffset;
                boolean b = mVerticalOffset >= 0 && mSwipeRefreshEnable;
                if (mSwipeRefreshLayout.isEnabled() != b) {
                    mSwipeRefreshLayout.setEnabled(b);
                }
            }
        });

        mMissRadioLayout.setOnMissRadioPlayListener(new MissRadioLayout.OnMissRadioPlayListener() {
            @Override
            public void onMissRadioPlay(Radio radio, boolean isPlaying) {
                ToastUtil.show(radio.getAudioName());
            }

            @Override
            public void onMissRadioClick(Radio radio) {
                Launcher.with(getActivity(), RadioStationPlayActivityActivity.class)
                        .putExtra(ExtraKeys.RADIO, radio)
                        .execute();
            }
        });

        MissAskFragment missHotQuestionFragment = getMissHotQuestionFragment();
        if (missHotQuestionFragment != null) {
            missHotQuestionFragment.setOnSwipeRefreshEnableListener(this);
        }

        MissAskFragment missLatestQuestionFragment = getMissLatestQuestionFragment();
        if (missLatestQuestionFragment != null) {
            missLatestQuestionFragment.setOnSwipeRefreshEnableListener(this);
        }
    }

    private void initSwipeRefreshLayout() {
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                requestMissList();
                requestRadioList();
                requestMissSwitcherList();
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


    private MissAskFragment getMissHotQuestionFragment() {
        return (MissAskFragment) mMissAskFragmentAdapter.getFragment(0);
    }

    private MissAskFragment getMissLatestQuestionFragment() {
        return (MissAskFragment) mMissAskFragmentAdapter.getFragment(1);
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

    private static class MissAskFragmentAdapter extends FragmentPagerAdapter {
        private Context mContext;
        private FragmentManager mFragmentManager;

        public MissAskFragmentAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
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

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }

}
