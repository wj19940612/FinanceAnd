package com.sbai.finance.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableString;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.TrainProjectModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;

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
    @BindView(R.id.testHint)
    TextView mTestHint;
    private TrainAdapter mTrainAdapter;


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
        mRecyclerView.setAdapter(mTrainAdapter);

        requestUserScore();
        updateUserScore();
        requestTrainProjectList();

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isAdded()) {
            requestUserScore();
            requestTrainProjectList();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        requestUserScore();
    }

    public void loginSuccess() {
        requestUserScore();

    }

    // TODO: 2017/8/3 请求训练项目
    private void requestTrainProjectList() {
        Client.requestTrainProjectList()
                .setIndeterminate(this)
                .setTag(TAG)
                .setCallback(new Callback2D<Resp<List<TrainProjectModel>>, List<TrainProjectModel>>() {
                    @Override
                    protected void onRespSuccessData(List<TrainProjectModel> data) {
                    }
                }).fire();

        ArrayList<TrainProjectModel> trainProjectModels = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            TrainProjectModel trainProjectModel = new TrainProjectModel();
            trainProjectModel.setGrade("L" + i);
            trainProjectModel.setTrainName("K" + i + i + "训练");
            trainProjectModel.setCompleteNeedTime(i + "分钟");
            trainProjectModel.setCompleteNumber(i);
            if (i % 2 == 0) {
                trainProjectModel.setTrained(true);
            }
            trainProjectModel.setTrainIcon("https://ss1.baidu.com/6ONXsjip0QIZ8tyhnq/it/u=3792466390,2360703660&fm=173&s=FDB01E9D3E9470C6CE3C89600300F033&w=640&h=959&img.JPEG");
            trainProjectModels.add(trainProjectModel);
        }

        updateTrainProjectList(trainProjectModels);
    }

    private void updateTrainProjectList(ArrayList<TrainProjectModel> trainProjectModels) {
        if (trainProjectModels == null) {

        } else {
            mTrainAdapter.addAll(trainProjectModels);
        }
    }

    // TODO: 2017/8/3 更新用户分数
    private void updateUserScore() {
        if (LocalUser.getUser().isLogin()) {
            boolean userLookTrainDetail = Preference.get().isUserLookTrainDetail(LocalUser.getUser().getPhone());
            if (userLookTrainDetail) {
                mScoreHint.setVisibility(View.GONE);
            } else {
                mScoreHint.setVisibility(View.VISIBLE);
            }
            mScoreHint.setText(R.string.look_detail);
            // TODO: 2017/8/3 模拟数据
            int score = 0;
            SpannableString spannableString;
            if (score == 0) {
                spannableString = StrUtil.mergeTextWithRatioColor(
                        getString(R.string.lemi_score), "\n0", "\n你还没有完成训练",
                        2f, 0.95f, Color.WHITE, Color.WHITE);
            } else {
                spannableString = StrUtil.mergeTextWithRatioColor(
                        getString(R.string.lemi_score), "\n860", "\n超过",
                        2f, 0.95f, Color.WHITE, Color.WHITE);
            }
            mScore.setText(spannableString);
        } else {
            mScore.setText(R.string.login_look_detail);
            mScoreHint.setText(R.string.to_login);
        }
    }

    // TODO: 2017/8/3 获取用户分数
    private void requestUserScore() {
        if (LocalUser.getUser().isLogin()) {
            Client.requestUserScore()
                    .setTag(TAG)
                    .setIndeterminate(this)
                    .fire();
            updateUserScore();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.gift, R.id.lookTrainDetail, R.id.rankingList, R.id.reviewLessonRoom, R.id.testHint})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.gift:
                break;
            case R.id.lookTrainDetail:
                if (LocalUser.getUser().isLogin()) {
                    // TODO: 2017/8/3 打开详情页面
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
            case R.id.testHint:
                mTestHint.setVisibility(View.GONE);
                break;
        }
    }

    static class TrainAdapter extends RecyclerView.Adapter<TrainAdapter.ViewHolder> {

        private ArrayList<TrainProjectModel> mTrainProjectModels;
        private Context mContext;

        public TrainAdapter(Context context, ArrayList<TrainProjectModel> trainProjectModels) {
            this.mTrainProjectModels = trainProjectModels;
            mContext = context;
        }

        public void addAll(ArrayList<TrainProjectModel> trainProjectModels) {
            this.notifyItemRangeRemoved(0, mTrainProjectModels.size());
            mTrainProjectModels.clear();
            mTrainProjectModels.addAll(trainProjectModels);
            notifyItemRangeChanged(0, mTrainProjectModels.size());
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_train, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.bindDataWithView(mTrainProjectModels.get(position), mContext);
        }

        @Override
        public int getItemCount() {
            return mTrainProjectModels != null ? mTrainProjectModels.size() : 0;
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

            public void bindDataWithView(TrainProjectModel trainProjectModel, Context context) {
                if (trainProjectModel == null) return;
                Glide.with(context).load(trainProjectModel.getTrainIcon())
                        .into(mTrainIcon);
                SpannableString spannableString = StrUtil.mergeTextWithRatio(trainProjectModel.getTrainName()
                        , "\n" + context.getString(R.string.train_count, trainProjectModel.getCompleteNumber()),
                        0.7f);
                mTrainTitle.setText(spannableString);
                mTrainGrade.setText(trainProjectModel.getGrade());
                if (trainProjectModel.isTrained()) {
                    mTrainStatus.setVisibility(View.GONE);
                    mTrainTime.setVisibility(View.VISIBLE);
                    mTrainTime.setText(trainProjectModel.getCompleteNeedTime());
                } else {
                    mTrainStatus.setVisibility(View.VISIBLE);
                    mTrainTime.setVisibility(View.GONE);
                    mTrainStatus.setText(context.getString(R.string.not_join_train));
                }
            }
        }
    }
}
