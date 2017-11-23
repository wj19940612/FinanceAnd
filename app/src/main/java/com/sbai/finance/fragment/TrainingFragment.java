package com.sbai.finance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.evaluation.EvaluationStartActivity;
import com.sbai.finance.activity.home.AllTrainingListActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.studyroom.StudyRoomActivity;
import com.sbai.finance.activity.training.CreditIntroduceActivity;
import com.sbai.finance.activity.training.TrainingDetailActivity;
import com.sbai.finance.model.Banner;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.MyTrainingRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingCount;
import com.sbai.finance.model.training.TrainingRecord;
import com.sbai.finance.model.training.UserEachTrainingScoreModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TrainingFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.gift)
    ImageView mGift;
    @BindView(R.id.titleTrainingCircleMiddle)
    ImageView mTitleTrainingCircleMiddle;
    @BindView(R.id.titleTrainingCircleOutside)
    ImageView mTitleTrainingCircleOutside;
    @BindView(R.id.lookTrainDetail)
    FrameLayout mLookTrainDetail;
    @BindView(R.id.scoreTitle)
    TextView mScoreTitle;
    @BindView(R.id.score)
    TextView mScore;
    @BindView(R.id.scoreProgress)
    TextView mScoreProgress;
    @BindView(R.id.lookDetailOrLogin)
    TextView mLookDetailOrLogin;
    @BindView(R.id.title)
    RelativeLayout mTitle;
    @BindView(R.id.rankingList)
    LinearLayout mRankingList;
    @BindView(R.id.reviewLessonRoom)
    LinearLayout mReviewLessonRoom;
    @BindView(R.id.recommendTrainTitle)
    TextView mRecommendTrainTitle;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.testYourLevel)
    TextView mTestYourLevel;
    @BindView(R.id.closeHint)
    ImageView mCloseHint;
    @BindView(R.id.testHint)
    LinearLayout mTestHint;
    @BindView(R.id.card)
    LinearLayout mCard;


    private TrainAdapter mTrainAdapter;
    private UserEachTrainingScoreModel mUserEachTrainingScoreModel;

    private double mOldScore;
    private double mNewScore;
    private double mScoreOffset;
    //如果点击了删除按钮，则退出再次进来在出现
    private boolean showJoinTestHint;
    private Banner mBanner;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_training, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        showJoinTestHint = true;
        mTrainAdapter = new TrainAdapter(getActivity(), new ArrayList<MyTrainingRecord>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mTrainAdapter);
        mTrainAdapter.setOnTrainClickListener(new TrainAdapter.OnTrainClickListener() {
            @Override
            public void onTrainClick(MyTrainingRecord myTrainingRecord, int position) {
                if (myTrainingRecord.getType() == MyTrainingRecord.TYPE_HAS_MORE_TRAINING) {
                    Launcher.with(getActivity(), AllTrainingListActivity.class).execute();
                } else {
                    if (myTrainingRecord.getTrain() != null) {
                        Launcher.with(getActivity(), TrainingDetailActivity.class)
                                .putExtra(ExtraKeys.TRAINING, myTrainingRecord.getTrain())
                                .execute();
                    }
                }
            }
        });

        requestGiftActivity();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            requestUserScore();
            requestMyTrainingList();
            requestGiftActivity();
        }
    }

    private void requestGiftActivity() {
        Client.requestGiftActivity()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback<Resp<Banner>>() {
                    @Override
                    protected void onRespSuccess(Resp<Banner> resp) {
                        if (resp.isSuccess()) {
                            mBanner = resp.getData();
                            updateGiftActivity();
                        }
                    }
                })
                .fire();
    }

    private void updateGiftActivity() {
        if (mBanner == null) {
            mGift.setVisibility(View.GONE);
        } else {
            mGift.setVisibility(View.VISIBLE);
            GlideApp.with(TrainingFragment.this)
                    .load(mBanner.getSmallPic())
                    .placeholder(R.drawable.ic_home_gift)
                    .into(mGift);
        }
    }

    private void requestMyTrainingList() {
        Client.requestMineTrainProjectList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<MyTrainingRecord>>, List<MyTrainingRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<MyTrainingRecord> data) {
                        if (!data.isEmpty()) {
                            mEmpty.setVisibility(View.GONE);
                            mRecommendTrainTitle.setVisibility(View.VISIBLE);
                            mRecyclerView.setVisibility(View.VISIBLE);

                            showMyTrainingList(data);
                        } else {
                            mEmpty.setVisibility(View.VISIBLE);
                            mRecommendTrainTitle.setVisibility(View.GONE);
                            mRecyclerView.setVisibility(View.GONE);
                        }
                    }
                }).fire();
    }

    private void showMyTrainingList(List<MyTrainingRecord> data) {
        // 我的训练记录里面如果有记录，那就是我的训练数据；不然为推荐训练数据（记录对象都空）
        boolean hasTrainingRecord = data.get(0).getRecord() != null;
        if (mUserEachTrainingScoreModel == null) {
            updateCreditMessage(hasTrainingRecord);
        }
        if (hasTrainingRecord) {
            mTrainAdapter.setIsMineTrained(true);
            mRecommendTrainTitle.setText(R.string.mine_train);
            requestTrainCount(data);
        } else {
            mTrainAdapter.setIsMineTrained(false);
            mRecommendTrainTitle.setText(R.string.recommend_train);
        }
        mTrainAdapter.clear();
        mTrainAdapter.addAll(data);
    }

    private void updateCreditMessage(boolean hasTrainingRecord) {
        if (LocalUser.getUser().isLogin()) {
            if (!hasTrainingRecord) {
                mScoreProgress.setText(R.string.you_are_not_complete_train);
            }
        } else {
            mScoreProgress.setText(R.string.login_look_detail);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestUserScore();
        requestMyTrainingList();
    }

    private void requestTrainCount(final List<MyTrainingRecord> data) {
        Client.requestTrainCount()
                .setCallback(new Callback2D<Resp<TrainingCount>, TrainingCount>() {
                    @Override
                    protected void onRespSuccessData(TrainingCount trainingCount) {
                        if (trainingCount != null && trainingCount.getAll() > data.size()) {
                            MyTrainingRecord myTrainingRecord = new MyTrainingRecord();
                            myTrainingRecord.setType(MyTrainingRecord.TYPE_HAS_MORE_TRAINING);
                            data.add(data.size(), myTrainingRecord);
                            mTrainAdapter.clear();
                            mTrainAdapter.addAll(data);
                        }
                    }
                })
                .fire();
    }

    private void updateUserScore(UserEachTrainingScoreModel data) {
        if (LocalUser.getUser().isLogin()) {
            mScoreTitle.setVisibility(View.VISIBLE);
            mScore.setVisibility(View.VISIBLE);

            boolean userLookTrainDetail = Preference.get().isUserLookTrainDetail(LocalUser.getUser().getPhone());
            if (userLookTrainDetail) {
                mLookDetailOrLogin.setVisibility(View.GONE);
            } else {
                mLookDetailOrLogin.setVisibility(View.VISIBLE);
            }
            mLookDetailOrLogin.setText(R.string.look_detail);
            mNewScore = data != null ? (int) data.getUserTotalScore() : 0;

            if (showJoinTestHint) {
                if (data != null && data.isEvaluate()) {
                    mTestHint.setVisibility(View.GONE);
                } else {
                    mTestHint.setVisibility(View.VISIBLE);
                }
            }

            startScoreAnimation(mNewScore);
            double rank = data != null ? data.getRank() : 0;
            if (data != null && (data.isTrain() || data.isEvaluate())) {
                mScoreProgress.setText(getString(R.string.more_than_number, FinanceUtil.formatFloorPercent(rank)));
            } else {
                mScoreProgress.setText(R.string.you_are_not_complete_train);
            }
        } else {
            mTestHint.setVisibility(View.VISIBLE);
            mScore.setVisibility(View.GONE);
            mScoreTitle.setVisibility(View.GONE);
            mScoreProgress.setText(R.string.login_look_detail);
            mLookDetailOrLogin.setVisibility(View.VISIBLE);
            mLookDetailOrLogin.setText(R.string.to_login);

            if (showJoinTestHint) {
                mTestHint.setVisibility(View.VISIBLE);
            } else {
                mTestHint.setVisibility(View.GONE);
            }
        }
    }

    private void startScoreAnimation(double newScore) {
        if (newScore == mOldScore) {
            mScore.setText(String.valueOf((int) newScore));
        } else {
            mOldScore = 0;
            mScoreOffset = FinanceUtil.multiply(newScore, 0.05).doubleValue();
            startScheduleJob(20);
            startAnimation();
        }
    }

    @Override
    public void onTimeUp(int count) {
        super.onTimeUp(count);
        mOldScore += mScoreOffset;
        if (mOldScore >= mNewScore) {
            stopScheduleJob();
            mOldScore = mNewScore;
        }
        mScore.setText(String.valueOf((int) mOldScore));
    }

    private void startAnimation() {
        int animationTime = 1000;
        final RotateAnimation clockwiseAnimation = new RotateAnimation(0f, 180, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        clockwiseAnimation.setDuration(animationTime);
        clockwiseAnimation.setFillAfter(true);
        clockwiseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mTitleTrainingCircleMiddle.startAnimation(clockwiseAnimation);

        final RotateAnimation anticlockwiseAnimation = new RotateAnimation(180f, 0f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        anticlockwiseAnimation.setDuration(animationTime);
        anticlockwiseAnimation.setFillAfter(true);
        anticlockwiseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mTitleTrainingCircleOutside.startAnimation(anticlockwiseAnimation);
    }

    private void requestUserScore() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestUserScore()
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<UserEachTrainingScoreModel>, UserEachTrainingScoreModel>() {
                        @Override
                        protected void onRespSuccessData(UserEachTrainingScoreModel data) {
                            mUserEachTrainingScoreModel = data;
                            updateUserScore(data);
                        }
                    })
                    .fire();
        } else {
            updateUserScore(null);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.gift, R.id.lookTrainDetail, R.id.rankingList,
            R.id.reviewLessonRoom, R.id.closeHint, R.id.testHint, android.R.id.empty})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gift:
                umengEventCount(UmengCountEventId.TRAINING_ACTIVITY);
                openGiftPage();
                break;
            case R.id.lookTrainDetail:
                if (LocalUser.getUser().isLogin()) {
                    if (mUserEachTrainingScoreModel != null) {
                        umengEventCount(UmengCountEventId.TRAINING_KNOW_CREDITS);
                        Launcher.with(getActivity(), CreditIntroduceActivity.class)
                                .putExtra(Launcher.EX_PAYLOAD, mUserEachTrainingScoreModel)
                                .execute();
                    }
                    if (mLookDetailOrLogin.isShown()) {
                        Preference.get().setUserHasLookTrainDetail(LocalUser.getUser().getPhone(), true);
                        mLookDetailOrLogin.setVisibility(View.GONE);
                    }
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.rankingList:
//                umengEventCount(UmengCountEventId.TRAINING_LEADER_BOARD);
//                Launcher.with(getActivity(), LeaderBoardsActivity.class).execute();
//                Launcher.with(getActivity(), InformationAndFocusNewsActivity.class).execute();
                break;
            case R.id.reviewLessonRoom:
                umengEventCount(UmengCountEventId.TRAINING_STUDY_ROOM);
                Launcher.with(getActivity(), StudyRoomActivity.class).execute();
                break;
            case R.id.closeHint:
                showJoinTestHint = false;
                mTestHint.setVisibility(View.GONE);
                break;
            case R.id.testHint:
                if (LocalUser.getUser().isLogin()) {
                    umengEventCount(UmengCountEventId.TRAINING_TEST);
                    Launcher.with(getActivity(), EvaluationStartActivity.class).execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case android.R.id.empty:
                requestMyTrainingList();
                break;
        }
    }

    private void openGiftPage() {
        if (mBanner == null) return;
        if (mBanner.isH5Style()) {
            Launcher.with(getActivity(), WebActivity.class)
                    .putExtra(WebActivity.EX_URL, mBanner.getContent())
                    .execute();
        } else {
            Launcher.with(getActivity(), WebActivity.class)
                    .putExtra(WebActivity.EX_HTML, mBanner.getContent())
                    .putExtra(WebActivity.EX_TITLE, mBanner.getTitle())
                    .execute();
        }
    }

    static class TrainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

        private OnTrainClickListener mOnTrainClickListener;

        interface OnTrainClickListener {
            void onTrainClick(MyTrainingRecord myTrainingRecord, int position);
        }

        public void setOnTrainClickListener(OnTrainClickListener onTrainClickListener) {
            this.mOnTrainClickListener = onTrainClickListener;
        }

        private ArrayList<MyTrainingRecord> mMyTrainingRecords;
        private Context mContext;
        //用来区分自己的训练还是推荐训练
        private boolean mIsMineTrained;

        public TrainAdapter(Context context, ArrayList<MyTrainingRecord> myTrainingRecords) {
            this.mMyTrainingRecords = myTrainingRecords;
            mContext = context;
        }

        public void addAll(List<MyTrainingRecord> myTrainingRecords) {
            mMyTrainingRecords.addAll(myTrainingRecords);
            notifyItemRangeChanged(0, mMyTrainingRecords.size());
        }

        public void clear() {
            mMyTrainingRecords.clear();
            notifyItemRangeRemoved(0, mMyTrainingRecords.size());
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == MyTrainingRecord.TYPE_HAS_MORE_TRAINING) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_training_has_more, parent, false);
                return new MoreTrainingViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_train, parent, false);
                return new DefaultViewHolder(view);
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (holder instanceof DefaultViewHolder) {
                ((DefaultViewHolder) holder).bindDataWithView(mMyTrainingRecords.get(position),
                        mContext, mIsMineTrained, mOnTrainClickListener, position);
            } else if (holder instanceof MoreTrainingViewHolder) {
                ((MoreTrainingViewHolder) holder).bindDataWithView(mMyTrainingRecords.get(position), position, mOnTrainClickListener);
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (mMyTrainingRecords.get(position).getType() == MyTrainingRecord.TYPE_HAS_MORE_TRAINING) {
                return MyTrainingRecord.TYPE_HAS_MORE_TRAINING;
            }
            return 0;
        }

        @Override
        public int getItemCount() {
            return mMyTrainingRecords != null ? mMyTrainingRecords.size() : 0;
        }

        public void setIsMineTrained(boolean isMineTrained) {
            this.mIsMineTrained = isMineTrained;
        }

        static class DefaultViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.trainIcon)
            ImageView mTrainIcon;
            @BindView(R.id.trainTitle)
            TextView mTrainTitle;
            @BindView(R.id.trainStatus)
            TextView mTrainStatus;
            @BindView(R.id.trainTime)
            TextView mTrainTime;
            @BindView(R.id.trainGrade)
            TextView mTrainGrade;
            @BindView(R.id.cardLayout)
            RelativeLayout mCardLayout;
            @BindView(R.id.trainCompleteCount)
            TextView mTrainCompleteCount;

            DefaultViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final MyTrainingRecord myTrainingRecord, Context context,
                                         boolean isMineTrained, final OnTrainClickListener onTrainClickListener,
                                         final int position) {
                if (myTrainingRecord == null) return;

                TrainingRecord record = myTrainingRecord.getRecord();
                Training train = myTrainingRecord.getTrain();

                mCardLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onTrainClickListener != null) {
                            onTrainClickListener.onTrainClick(myTrainingRecord, position);
                        }
                    }
                });

                int finishCount = 0;
                long needTime = 0;
                if (record != null) {
                    finishCount = record.getFinish();
                }

                if (train != null) {
                    GlideApp.with(context)
                            .load(train.getImageUrl())
                            .placeholder(R.drawable.bg_common_replace_image)
                            .into(mTrainIcon);
                    mTrainTitle.setText(train.getTitle());
                    if (isMineTrained) {
                        mTrainCompleteCount.setVisibility(View.VISIBLE);
                        if (finishCount > 0) {
                            mTrainCompleteCount.setText(context.getString(R.string.train_count, finishCount));
                        } else {
                            mTrainCompleteCount.setText(R.string.not_join_train);
                        }
                    }
                    mTrainGrade.setText(context.getString(R.string.train_grade, train.getLevel()));
                    needTime = train.getTime();
                } else {
                    mTrainIcon.setBackgroundResource(R.drawable.bg_common_replace_image);
                }

                if (isMineTrained) {
                    mTrainStatus.setVisibility(View.GONE);
                    mTrainTime.setVisibility(View.VISIBLE);
                    mTrainTime.setText(DateUtil.formatTime(needTime));
                } else {
                    mTrainCompleteCount.setVisibility(View.GONE);
                    mTrainStatus.setVisibility(View.VISIBLE);
                    mTrainTime.setVisibility(View.GONE);
                    mTrainStatus.setText(context.getString(R.string.not_join_train));
                }
            }
        }

        static class MoreTrainingViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.relativeLayout)
            RelativeLayout mRelativeLayout;

            MoreTrainingViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final MyTrainingRecord myTrainingRecord, final int position, final OnTrainClickListener onTrainClickListener) {
                mRelativeLayout.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (onTrainClickListener != null) {
                            onTrainClickListener.onTrainClick(myTrainingRecord, position);
                        }
                    }
                });
            }
        }
    }
}
