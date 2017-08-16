package com.sbai.finance.activity.training;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Experience;
import com.sbai.finance.model.training.TrainPraise;
import com.sbai.finance.model.training.TrainedUserRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrFormatter;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.ImageListView;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.dialog.ShareDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.net.Client.SHARE_URL_TRAIN_EXPERIENCE;


public class TrainDetailActivity extends BaseActivity {

    private static final int TYPE_THEORY = 1;
    private static final int TYPE_TECHNOLOGY = 2;
    private static final int TYPE_FUNDAMENTALS = 3;
    private static final int TYPE_COMPREHENSIVE = 4;

    @BindView(R.id.back)
    ImageView mBack;
    @BindView(R.id.titleName)
    TextView mTitleName;
    @BindView(R.id.share)
    ImageView mShare;
    @BindView(R.id.titleBar)
    RelativeLayout mTitleBar;
    @BindView(R.id.duration)
    TextView mDuration;
    @BindView(R.id.difficulty)
    TextView mDifficulty;
    @BindView(R.id.imageListView)
    ImageListView mImageListView;
    @BindView(R.id.completeNumber)
    TextView mCompleteNumber;
    @BindView(R.id.relevantKnowledge)
    RelativeLayout mRelevantKnowledge;
    @BindView(R.id.hotExperience)
    LinearLayout mHotExperience;
    @BindView(R.id.HotListView)
    MyListView mHotListView;
    @BindView(R.id.writeExperience)
    TextView mWriteExperience;
    @BindView(R.id.empty)
    LinearLayout mEmpty;
    @BindView(R.id.startTrain)
    TextView mStartTrain;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.introduce)
    TextView mIntroduce;
    @BindView(R.id.background)
    RelativeLayout mBackground;

    private int mTrainId;
    private int mType;
    private int mPage = 0;
    private int mPageSize = 3;

    private TrainingDetail mTrainDetail;

    private List<String> mCompletePeopleList;
    private List<Experience> mHotExperienceList;
    private HotExperienceListAdapter mHotExperienceListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_detail);
        ButterKnife.bind(this);

        initData(getIntent());
        initBackGround(mType);

        mCompletePeopleList = new ArrayList<>();
        mHotExperienceList = new ArrayList<>();
        mHotExperienceListAdapter = new HotExperienceListAdapter(this);
        mHotListView.setEmptyView(mEmpty);
        mHotListView.setFocusable(false);
        mHotListView.setAdapter(mHotExperienceListAdapter);

        requestTrainingDetail();
        requestFinishPeopleList();
        requestHotExperienceList();
    }

    private void initData(Intent intent) {
        mType = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
        mTrainId = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        // TODO: 15/08/2017 直接接收一个  training 修改 extraKey
    }

    private void initBackGround(int type) {
        switch (type) {
            case TYPE_THEORY:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
                mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
                mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_theory));
                break;
            case TYPE_TECHNOLOGY:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
                mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
                mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_technology));
                break;
            case TYPE_FUNDAMENTALS:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
                mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
                mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_fundamentals));
                break;
            case TYPE_COMPREHENSIVE:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
                mBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
                mStartTrain.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.bg_train_comprehensive));
                break;
        }
    }

    private void requestTrainingDetail() {
        Client.getTrainingDetail(mTrainId).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<TrainingDetail>, TrainingDetail>() {
                    @Override
                    protected void onRespSuccessData(TrainingDetail data) {
                        mTrainDetail = data;
                        updateTrainDetail();
                    }
                }).fire();
    }

    private void requestFinishPeopleList() {
        Client.getTrainedUserRecords(mPage, mPageSize, mTrainId).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<TrainedUserRecord>>, List<TrainedUserRecord>>() {
                    @Override
                    protected void onRespSuccessData(List<TrainedUserRecord> data) {
                        for (TrainedUserRecord userRecord : data) {
                            mCompletePeopleList.add(userRecord.getUser().getUserPortrait());
                        }
                        mImageListView.setImages(mCompletePeopleList, R.drawable.ic_board_head_more_grey);
                    }
                }).fire();
    }

    private void requestHotExperienceList() {
        Client.getHotExperienceList(mTrainId).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
                    @Override
                    protected void onRespSuccessData(List<Experience> experienceList) {
                        for (int i = 0; i < 2; i++) {
                            mHotExperienceList.add(experienceList.get(i));
                        }
                        updateHotExperienceList(mHotExperienceList);
                    }
                }).fire();
    }

    private void updateHotExperienceList(List<Experience> experienceList) {
        mHotExperienceListAdapter.clear();
        mHotExperienceListAdapter.addAll(experienceList);
    }

    private void updateTrainDetail() {
        if (mTrainDetail.getTrain() != null) {
            Training training = mTrainDetail.getTrain();
            mTitle.setText(training.getTitle());
            mIntroduce.setText(training.getRemark());
            mDuration.setText(getString(R.string.train_duration, training.getTime() / 60));
            mDifficulty.setText(getString(R.string.train_level, training.getLevel()));
            mCompleteNumber.setText(getString(R.string.complete_number, training.getFinishCount()));
        }
    }

    static class HotExperienceListAdapter extends ArrayAdapter<Experience> {

        private Context mContext;

        public HotExperienceListAdapter(@NonNull Context context) {
            super(context, 0);
            this.mContext = context;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_train_experience, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindingData(mContext, getItem(position));
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.avatar)
            ImageView mAvatar;
            @BindView(R.id.userName)
            TextView mUserName;
            @BindView(R.id.hotArea)
            LinearLayout mHotArea;
            @BindView(R.id.experience)
            TextView mExperience;
            @BindView(R.id.publishTime)
            TextView mPublishTime;
            @BindView(R.id.loveNumber)
            TextView mLoveNumber;
            @BindView(R.id.imageView)
            ImageView mImageView;
            @BindView(R.id.star1)
            ImageView mStar1;
            @BindView(R.id.star2)
            ImageView mStar2;
            @BindView(R.id.star3)
            ImageView mStar3;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindingData(final Context context, final Experience item) {
                if (item == null) return;

                if (item.getUserModel() != null) {
                    Glide.with(context).load(item.getUserModel().getUserPortrait())
                            .placeholder(R.drawable.ic_default_avatar)
                            .transform(new GlideCircleTransform(context))
                            .into(mAvatar);

                    mUserName.setText(item.getUserModel().getUserName());
                    mPublishTime.setText(DateUtil.getMissFormatTime(item.getCreateDate()));
                } else {
                    Glide.with(context).load(R.drawable.ic_default_avatar)
                            .transform(new GlideCircleTransform(context))
                            .into(mAvatar);
                    mUserName.setText("");
                    mPublishTime.setText("");
                }

                mExperience.setText(item.getContent());
                mLoveNumber.setText(StrFormatter.getFormatCount(item.getPraise()));

                if (item.getIsPraise() == 1) {
                    mLoveNumber.setSelected(true);
                } else {
                    mLoveNumber.setSelected(false);
                }

                mLoveNumber.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (LocalUser.getUser().isLogin()) {
                            Client.trainExperiencePraise(item.getId(), item.getIsPraise() == 0 ? 1 : 0)
                                    .setCallback(new Callback2D<Resp<TrainPraise>, TrainPraise>() {
                                        @Override
                                        protected void onRespSuccessData(TrainPraise data) {
                                            if (data.getIsPraise() == 1) {
                                                mLoveNumber.setSelected(true);
                                            } else {
                                                mLoveNumber.setSelected(false);
                                            }
                                            item.setIsPraise(data.getIsPraise());
                                            mLoveNumber.setText(StrFormatter.getFormatCount(data.getPraise()));
                                        }
                                    }).fire();

                        } else {
                            Launcher.with(context, LoginActivity.class).execute();
                        }
                    }
                });

                if (item.getPicture() == null || "".equalsIgnoreCase(item.getPicture())) {
                    mImageView.setVisibility(View.GONE);
                } else {
                    mImageView.setVisibility(View.VISIBLE);
                    Glide.with(context).load(item.getPicture())
                            .placeholder(R.drawable.ic_default_image)
                            .into(mImageView);

                    mImageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Launcher.with(context, LookBigPictureActivity.class)
                                    .putExtra(Launcher.EX_PAYLOAD, item.getPicture())
                                    .execute();
                        }
                    });
                }

                switch (item.getStar()) {
                    case 1:
                        mStar1.setVisibility(View.VISIBLE);
                        mStar2.setVisibility(View.GONE);
                        mStar3.setVisibility(View.GONE);
                        break;
                    case 2:
                        mStar1.setVisibility(View.VISIBLE);
                        mStar2.setVisibility(View.VISIBLE);
                        mStar3.setVisibility(View.GONE);
                        break;
                    case 3:
                        mStar1.setVisibility(View.VISIBLE);
                        mStar2.setVisibility(View.VISIBLE);
                        mStar3.setVisibility(View.VISIBLE);
                        break;
                }
            }
        }
    }

    @OnClick({R.id.back, R.id.share, R.id.relevantKnowledge, R.id.hotExperience, R.id.writeExperience, R.id.startTrain})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                finish();
                break;
            case R.id.share:
                share();
                break;
            case R.id.relevantKnowledge:
                if (mTrainDetail != null && mTrainDetail.getTrain() != null) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_TITLE, "相关知识点")
                            .putExtra(WebActivity.EX_URL, mTrainDetail.getTrain().getKnowledgeUrl())
                            .execute();
                }
                break;
            case R.id.hotExperience:
                Launcher.with(getActivity(), TrainExperienceActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD_1, mTrainId)
                        .execute();
                break;
            case R.id.writeExperience:
                Launcher.with(getActivity(), WriteExperienceActivity.class)
                        .putExtra(Launcher.EX_PAYLOAD, mType)
                        .execute();
                break;
            case R.id.startTrain:
                // TODO: 2017/8/10 开始训练
                ToastUtil.show("开始训练");
                break;
        }
    }

    private void share() {
        ShareDialog.with(getActivity())
                .setTitle(getString(R.string.share_title))
                .setShareTitle(getString(R.string.train_share_share_title, mTrainDetail.getTrain().getTitle()))
                .setShareDescription(getString(R.string.train_share_description))
                .setShareUrl(String.format(SHARE_URL_TRAIN_EXPERIENCE, mTrainId))
                .hasFeedback(true)
                .setListener(new ShareDialog.OnShareDialogCallback() {
                    @Override
                    public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                        Client.share().setTag(TAG).fire();
                    }

                    @Override
                    public void onFeedbackClick(View view) {
                        Launcher.with(getActivity(), FeedbackActivity.class).execute();
                    }
                }).show();
    }
}
