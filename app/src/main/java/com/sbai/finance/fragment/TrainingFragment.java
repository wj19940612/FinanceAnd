package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.util.Log;
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

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.leveltest.LevelTestStartActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.train.ScoreIntroduceActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.TrainProjectModel;
import com.sbai.finance.model.training.UserEachTrainingScoreModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.NumberFormatUtils;
import com.sbai.finance.utils.StrUtil;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class TrainingFragment extends BaseFragment {


    Unbinder unbinder;
    @BindView(R.id.gift)
    ImageView mGift;
    @BindView(R.id.rankingList)
    TextView mRankingList;
    @BindView(R.id.reviewLessonRoom)
    TextView mReviewLessonRoom;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.score)
    TextView mScore;
    @BindView(R.id.scoreHint)
    TextView mScoreHint;
    @BindView(R.id.lookTrainDetail)
    FrameLayout mLookTrainDetail;
    @BindView(R.id.title)
    RelativeLayout mTitle;
    @BindView(R.id.card)
    CardView mCard;
    @BindView(R.id.recommendTrainTitle)
    TextView mRecommendTrainTitle;
    @BindView(R.id.titleTrainingCircleMiddle)
    ImageView mTitleTrainingCircleMiddle;
    @BindView(R.id.titleTrainingCircleOutside)
    ImageView mTitleTrainingCircleOutside;
    @BindView(android.R.id.empty)
    AppCompatTextView mEmpty;
    @BindView(R.id.testYourLevel)
    TextView mTestYourLevel;
    @BindView(R.id.closeHint)
    TextView mCloseHint;
    @BindView(R.id.testHint)
    LinearLayout mTestHint;


    private TrainAdapter mTrainAdapter;
    private UserEachTrainingScoreModel mUserEachTrainingScoreModel;


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
        mTrainAdapter = new TrainAdapter(getActivity(), new ArrayList<TrainProjectModel>());
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        mRecyclerView.setLayoutManager(linearLayoutManager);
        mRecyclerView.setItemAnimator(null);
        mRecyclerView.setAdapter(mTrainAdapter);
        startAnimation();
        requestUserScore();
        updateUserScore(null);
        requestMineTrainingProjectList();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            requestUserScore();
            requestMineTrainingProjectList();
        }
    }

    private void requestMineTrainingProjectList() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestMineTrainProjectList()
                    .setTag(TAG)
                    .setCallback(new Callback2D<Resp<ArrayList<TrainProjectModel>>, ArrayList<TrainProjectModel>>() {
                        @Override
                        protected void onRespSuccessData(ArrayList<TrainProjectModel> data) {
                            if (data != null && !data.isEmpty()) {
                                mTrainAdapter.setIsMineTrained(true);
                                mRecommendTrainTitle.setText(R.string.mine_train);
                                updateTrainProjectList(data);
                            } else {
                                requestRecommendTrainProjectList();
                            }
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                            requestRecommendTrainProjectList();
                        }
                    })
                    .fire();
        } else {
            requestRecommendTrainProjectList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestUserScore();
        requestMineTrainingProjectList();
    }

    private void requestRecommendTrainProjectList() {
        Client.requestTrainProjectList()
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<ArrayList<TrainProjectModel>>, ArrayList<TrainProjectModel>>() {
                    @Override
                    protected void onRespSuccessData(ArrayList<TrainProjectModel> data) {
                        mTrainAdapter.setIsMineTrained(false);
                        updateTrainProjectList(data);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        updateTrainProjectList(null);
                    }
                }).fire();
    }

    private void updateTrainProjectList(ArrayList<TrainProjectModel> trainProjectModels) {
        if (trainProjectModels == null || trainProjectModels.isEmpty()) {
            mEmpty.setVisibility(View.VISIBLE);
            mRecommendTrainTitle.setVisibility(View.GONE);
            mRecyclerView.setVisibility(View.GONE);
        } else {
            mEmpty.setVisibility(View.GONE);
            mRecommendTrainTitle.setVisibility(View.VISIBLE);
            mRecyclerView.setVisibility(View.VISIBLE);
            Log.d(TAG, "updateTrainProjectList: " + trainProjectModels);
            mTrainAdapter.clear();
            mTrainAdapter.addAll(trainProjectModels);
            showJoinTestLayout(trainProjectModels);
        }
    }

    private void showJoinTestLayout(ArrayList<TrainProjectModel> trainProjectModels) {
        int joinTestCount = 0;
        if (trainProjectModels == null) return;
        for (TrainProjectModel data : trainProjectModels) {
            TrainProjectModel.RecordBean record = data.getRecord();
            if (record != null) {
                joinTestCount += record.getFinish();
            }
        }
        if (joinTestCount > 0) {
            mTestHint.setVisibility(View.GONE);
        } else {
            mTestHint.setVisibility(View.VISIBLE);
        }
    }

    private void updateUserScore(UserEachTrainingScoreModel data) {
        if (LocalUser.getUser().isLogin()) {
            boolean userLookTrainDetail = Preference.get().isUserLookTrainDetail(LocalUser.getUser().getPhone());
            if (userLookTrainDetail) {
                mScoreHint.setVisibility(View.GONE);
            } else {
                mScoreHint.setVisibility(View.VISIBLE);
            }
            mScoreHint.setText(R.string.look_detail);

            int score = data != null ? (int) data.getUserTotalScore() : 0;
            double rank = data != null ? data.getRank() : 0;
            SpannableString spannableString;
            if (score == 0) {
                spannableString = StrUtil.mergeTextWithRatioColor(
                        getString(R.string.lemi_score), "\n" + score, getString(R.string.you_are_not_trained),
                        2.5f, 0.95f, Color.WHITE, Color.WHITE);
            } else {
                spannableString = StrUtil.mergeTextWithRatioColor(
                        getString(R.string.lemi_score), "\n" + score, "\n超过" + NumberFormatUtils.formatPercentString(rank),
                        2.5f, 0.95f, Color.WHITE, Color.WHITE);
            }
            mScore.setText(spannableString);

        } else {
            mScore.setText(R.string.login_look_detail);
            mScoreHint.setText(R.string.to_login);
        }
    }

    private void startAnimation() {
        int animationTime = 3000;
        final RotateAnimation clockwiseAnimation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF,
                0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        clockwiseAnimation.setDuration(animationTime);
        clockwiseAnimation.setFillAfter(true);
        clockwiseAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        mTitleTrainingCircleMiddle.startAnimation(clockwiseAnimation);

        final RotateAnimation anticlockwiseAnimation = new RotateAnimation(360f, 0f, Animation.RELATIVE_TO_SELF,
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
                break;
            case R.id.lookTrainDetail:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), ScoreIntroduceActivity.class)
                            .putExtra(Launcher.EX_PAYLOAD, mUserEachTrainingScoreModel)
                            .execute();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case R.id.rankingList:
                // TODO: 2017/8/4 排行榜
                break;
            case R.id.reviewLessonRoom:
                // TODO: 2017/8/4 自习室
                break;
            case R.id.closeHint:
                mTestHint.setVisibility(View.GONE);
                break;
            case R.id.testHint:
                if (LocalUser.getUser().isLogin()) {
                    Launcher.with(getActivity(), LevelTestStartActivity.class).execute();
                    if (mScoreHint.isShown()) {
                        Preference.get().setUserHasLookTrainDetail(LocalUser.getUser().getPhone(), true);
                        mScoreHint.setVisibility(View.GONE);
                    }
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
            case android.R.id.empty:
                requestMineTrainingProjectList();
                break;
        }
    }

    static class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {

        private ArrayList<TrainProjectModel> mTrainProjectModels;
        private Context mContext;
        //用来区分自己的训练还是推荐训练
        private boolean mIsMineTrained;

        public TrainAdapter(Context context, ArrayList<TrainProjectModel> trainProjectModels) {
            this.mTrainProjectModels = trainProjectModels;
            mContext = context;
        }

        public void addAll(ArrayList<TrainProjectModel> trainProjectModels) {
            mTrainProjectModels.addAll(trainProjectModels);
            notifyItemRangeChanged(0, mTrainProjectModels.size());
        }

        public void clear() {
            mTrainProjectModels.clear();
            notifyItemRangeRemoved(0, mTrainProjectModels.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_train, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mTrainProjectModels.get(position), mContext, mIsMineTrained);
        }

        @Override
        public int getItemCount() {
            return mTrainProjectModels != null ? mTrainProjectModels.size() : 0;
        }

        public void setIsMineTrained(boolean isMineTrained) {
            this.mIsMineTrained = isMineTrained;
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            @BindView(R.id.trainIcon)
            ImageView mTrainIcon;
            @BindView(R.id.card)
            CardView mCard;
            @BindView(R.id.trainTitle)
            TextView mTrainTitle;
            @BindView(R.id.trainStatus)
            TextView mTrainStatus;
            @BindView(R.id.trainTime)
            TextView mTrainTime;
            @BindView(R.id.trainGrade)
            TextView mTrainGrade;

            ViewHolder(View view) {
                super(view);
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(TrainProjectModel trainProjectModel, Context context, boolean isMineTrained) {
                if (trainProjectModel == null) return;

                TrainProjectModel.RecordBean record = trainProjectModel.getRecord();
                TrainProjectModel.TrainBean train = trainProjectModel.getTrain();

                int finishCount = 0;
                long needTime = 0;
                if (record != null) {
                    finishCount = record.getFinish();
                }
                SpannableString spannableString = null;

                if (train != null) {
                    Glide.with(context)
                            .load(train.getImageUrl())
                            .placeholder(R.drawable.bg_common_replace_image)
                            .into(mTrainIcon);
                    if (isMineTrained) {
                        spannableString = StrUtil.mergeTextWithRatio(train.getTitle()
                                , "\n" + context.getString(R.string.train_count, finishCount),
                                0.7f);
                        mTrainTitle.setText(spannableString);
                    } else {
                        mTrainTitle.setText(train.getTitle());
                    }
                    mTrainGrade.setText(context.getString(R.string.train_grade, train.getLevel()));
                    needTime = train.getTime();
                } else {
                    mTrainIcon.setBackgroundResource(R.drawable.bg_common_replace_image);
                }


                if (isMineTrained) {
                    mTrainStatus.setVisibility(View.GONE);
                    mTrainTime.setVisibility(View.VISIBLE);
                    mTrainTime.setText(DateUtil.getMinutes(needTime));
                } else {
                    mTrainStatus.setVisibility(View.VISIBLE);
                    mTrainTime.setVisibility(View.GONE);
                    mTrainStatus.setText(context.getString(R.string.not_join_train));
                }
            }
        }
    }
}
