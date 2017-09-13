package com.sbai.finance.activity.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.WebActivity;
import com.sbai.finance.activity.mine.FeedbackActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.training.Experience;
import com.sbai.finance.model.training.Question;
import com.sbai.finance.model.training.TrainedUserRecord;
import com.sbai.finance.model.training.Training;
import com.sbai.finance.model.training.TrainingDetail;
import com.sbai.finance.model.training.TrainingRecord;
import com.sbai.finance.model.training.TrainingTarget;
import com.sbai.finance.model.training.question.KData;
import com.sbai.finance.model.training.question.RemoveData;
import com.sbai.finance.model.training.question.SortData;
import com.sbai.finance.net.API;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.view.IconTextRow;
import com.sbai.finance.view.ImageListView;
import com.sbai.finance.view.ObservableScrollView;
import com.sbai.finance.view.TitleBar;
import com.sbai.finance.view.dialog.ShareDialog;
import com.sbai.finance.view.training.ExperienceView;
import com.sbai.finance.view.training.TrainingAchievementView2;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class TrainingDetailActivity extends BaseActivity {

    private static final int REQ_TRAIN_EXPERIENCE = 1001;

    @BindView(R.id.titleBar)
    TitleBar mTitleBar;
    @BindView(R.id.extraBackground)
    LinearLayout mExtraBackground;
    @BindView(R.id.title)
    TextView mTitle;
    @BindView(R.id.introduce)
    TextView mIntroduce;
    @BindView(R.id.scrollView)
    ObservableScrollView mObservableScrollView;
    @BindView(R.id.duration)
    TextView mDuration;
    @BindView(R.id.difficulty)
    TextView mDifficulty;
    @BindView(R.id.imageListView)
    ImageListView mImageListView;
    @BindView(R.id.completeNumber)
    TextView mCompleteNumber;
    @BindView(R.id.relatedKnowledge)
    TextView mRelatedKnowledge;
    @BindView(R.id.writeExperience)
    TextView mWriteExperience;
    @BindView(R.id.empty)
    LinearLayout mEmpty;
    @BindView(R.id.startTraining)
    Button mStartTraining;
    @BindView(R.id.experience1)
    ExperienceView mExperience1;
    @BindView(R.id.experience2)
    ExperienceView mExperience2;
    @BindView(R.id.trainingExperience)
    IconTextRow mTrainingExperience;
    @BindView(R.id.background)
    LinearLayout mBackground;

    private TrainingDetail mTrainingDetail;
    private Training mTraining;
    private TrainingRecord mMyTrainingRecord;
    private TrainingAchievementView2[] mAchievementView2s;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_training_detail);
        ButterKnife.bind(this);
        translucentStatusBar();

        initData(getIntent());

        initBackground();
        initTitleBar();

        mObservableScrollView.setScrollChangedListener(new ObservableScrollView.OnScrollChangedListener() {
            @Override
            public void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldX, int oldY) {
                float alpha = 0;
                if (y < 0) {
                    alpha = 0;
                } else if (y > 300) {
                    alpha = 1;
                } else {
                    alpha = y / 300.0f;
                }
                mTitleBar.setTitleAlpha(alpha);
            }
        });

        initAchievementViews();
        requestTrainDetail();
        requestFinishPeopleList();
        requestHotExperienceList();
    }

    private void initTitleBar() {
        mTitleBar.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                share();
            }
        });
    }

    private void initAchievementViews() {
        mAchievementView2s = new TrainingAchievementView2[3];
        mAchievementView2s[0] = (TrainingAchievementView2) findViewById(R.id.achievement0);
        mAchievementView2s[1] = (TrainingAchievementView2) findViewById(R.id.achievement1);
        mAchievementView2s[2] = (TrainingAchievementView2) findViewById(R.id.achievement2);
    }

    private void updateAchievementViews() {
        if (mTrainingDetail != null) {
            List<TrainingTarget> targets = mTrainingDetail.getTargets();
            if (targets != null && !targets.isEmpty()) {
                for (int i = 0; i < targets.size() && i < mAchievementView2s.length; i++) {
                    TrainingTarget target = targets.get(i);
                    mAchievementView2s[i].setVisibility(View.VISIBLE);

                    updateAchievementView(mAchievementView2s[i], target);

                    if (mMyTrainingRecord != null && mMyTrainingRecord.getMaxLevel() >= target.getLevel()) {
                        mAchievementView2s[i].setAchieved(true);
                    }
                }
            }
        }
    }

    private void updateAchievementView(TrainingAchievementView2 achievementView, TrainingTarget target) {
        switch (target.getType()) {
            case TrainingTarget.TYPE_FINISH:
                achievementView.setContent(R.string.mission_complete);
                break;
            case TrainingTarget.TYPE_RATE:
                achievementView.setContent(getString(R.string.accuracy_to_,
                        FinanceUtil.formatToPercentage(target.getRate(), 0)));
                break;
            case TrainingTarget.TYPE_TIME:
                achievementView.setContent(formatTime(target.getTime(),
                        R.string._seconds_complete,
                        R.string._minutes_complete,
                        R.string._minutes_x_seconds_complete));
                break;
        }
    }

    private String formatTime(int seconds, int secondRes, int minRes, int minSecondRes) {
        if (seconds / 60 == 0) {
            return getString(secondRes, seconds);
        } else if (seconds % 60 == 0) {
            return getString(minRes, seconds / 60);
        } else {
            return getString(minSecondRes, seconds / 60, seconds % 60);
        }
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        requestMyTrainingRecord();
    }

    private void requestMyTrainingRecord() {
        if (LocalUser.getUser().isLogin()) {
            Client.getMyTrainingRecord(mTraining.getId()).setTag(TAG)
                    .setCallback(new Callback2D<Resp<TrainingRecord>, TrainingRecord>() {
                        @Override
                        protected void onRespSuccessData(TrainingRecord data) {
                            mMyTrainingRecord = data;
                            updateAchievementViews();

                            if (mMyTrainingRecord.getFinish() > 0) {
                                mWriteExperience.setVisibility(View.VISIBLE);
                            }
                        }
                    }).fireFree();
        }
    }

    private void initData(Intent intent) {
        mTraining = intent.getParcelableExtra(ExtraKeys.TRAINING);
    }

    private void initBackground() {
        switch (mTraining.getType()) {
            case Training.TYPE_THEORY:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
                mBackground.setBackgroundResource(R.drawable.bg_training_detail_theory);
                mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.redTheoryTraining));
                mStartTraining.setBackgroundResource(R.drawable.bg_train_theory);
                break;
            case Training.TYPE_TECHNOLOGY:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
                mBackground.setBackgroundResource(R.drawable.bg_training_detail_technology);
                mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.violetTechnologyTraining));
                mStartTraining.setBackgroundResource(R.drawable.bg_train_technology);
                break;
            case Training.TYPE_FUNDAMENTAL:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
                mBackground.setBackgroundResource(R.drawable.bg_training_detail_fundamental);
                mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowFundamentalTraining));
                mStartTraining.setBackgroundResource(R.drawable.bg_train_fundamentals);
                break;
            case Training.TYPE_COMPREHENSIVE:
                mTitleBar.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
                mBackground.setBackgroundResource(R.drawable.bg_training_detail_comprehensive);
                mExtraBackground.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.blueComprehensiveTraining));
                mStartTraining.setBackgroundResource(R.drawable.bg_train_comprehensive);
                break;
        }
    }

    private void requestTrainDetail() {
        Client.getTrainingDetail(mTraining.getId()).setTag(TAG).setIndeterminate(this)
                .setCallback(new Callback2D<Resp<TrainingDetail>, TrainingDetail>() {
                    @Override
                    protected void onRespSuccessData(TrainingDetail data) {
                        mTrainingDetail = data;
                        updateTrainDetail(data);
                        updateAchievementViews();
                    }
                }).fire();
    }


    private void requestFinishPeopleList() {
        Client.getTrainedUserRecords(0, 4, mTraining.getId()).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<List<TrainedUserRecord>>>() {
                    @Override
                    protected void onRespSuccess(Resp<List<TrainedUserRecord>> resp) {
                        if (resp.getData() != null) {
                            List<TrainedUserRecord> data = resp.getData();
                            List<String> userPortraitList = new ArrayList<String>();
                            for (TrainedUserRecord userRecord : data) {
                                userPortraitList.add(userRecord.getUser().getUserPortrait());
                            }
                            mImageListView.setImages(userPortraitList, R.drawable.ic_board_head_more_grey);
                            mCompleteNumber.setText(getString(R.string.complete_number, resp.getResultCount()));
                        }
                    }
                }).fire();
    }

    private void requestHotExperienceList() {
        Client.getHotExperienceList(mTraining.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
                    @Override
                    protected void onRespSuccessData(List<Experience> experienceList) {
                        updateHotExperienceList(experienceList);
                    }

                    @Override
                    public void onFailure(VolleyError volleyError) {
                        super.onFailure(volleyError);
                        mTrainingExperience.setSubTextVisible(View.INVISIBLE);
                        mTrainingExperience.setClickable(false);
                        mEmpty.setVisibility(View.VISIBLE);
                        mExperience1.setVisibility(View.GONE);
                        mExperience2.setVisibility(View.GONE);
                    }
                }).fire();
    }

    private void updateHotExperienceList(List<Experience> experienceList) {
        if (experienceList.size() == 0) {
            requestLatestExperienceList();
        } else if (experienceList.size() == 1) {
            mEmpty.setVisibility(View.GONE);
            mExperience1.setVisibility(View.VISIBLE);
            mExperience2.setVisibility(View.GONE);
            mExperience1.setData(experienceList.get(0));
        } else {
            mEmpty.setVisibility(View.GONE);
            mExperience1.setVisibility(View.VISIBLE);
            mExperience2.setVisibility(View.VISIBLE);
            mExperience1.setData(experienceList.get(0));
            mExperience2.setData(experienceList.get(1));
        }
    }

    private void requestLatestExperienceList() {
        if (mTraining != null) {
            Client.getLatestExperienceList(0, 2, mTraining.getId()).setTag(TAG)
                    .setCallback(new Callback2D<Resp<List<Experience>>, List<Experience>>() {
                        @Override
                        protected void onRespSuccessData(List<Experience> experienceList) {
                            updateLatestExperienceList(experienceList);
                        }

                        @Override
                        public void onFailure(VolleyError volleyError) {
                            super.onFailure(volleyError);
                            mTrainingExperience.setSubTextVisible(View.INVISIBLE);
                            mTrainingExperience.setClickable(false);
                            mEmpty.setVisibility(View.VISIBLE);
                            mExperience1.setVisibility(View.GONE);
                            mExperience2.setVisibility(View.GONE);
                        }
                    }).fire();
        }
    }

    private void updateLatestExperienceList(List<Experience> experienceList) {
        if (experienceList.size() == 0) {
            mEmpty.setVisibility(View.VISIBLE);
            mExperience1.setVisibility(View.GONE);
            mExperience2.setVisibility(View.GONE);
            mTrainingExperience.setSubTextVisible(View.INVISIBLE);
            mTrainingExperience.setClickable(false);
        } else if (experienceList.size() == 1) {
            mEmpty.setVisibility(View.GONE);
            mExperience1.setVisibility(View.VISIBLE);
            mExperience2.setVisibility(View.GONE);
            mExperience1.setData(experienceList.get(0));
        } else {
            mEmpty.setVisibility(View.GONE);
            mExperience1.setVisibility(View.VISIBLE);
            mExperience2.setVisibility(View.VISIBLE);
            mExperience1.setData(experienceList.get(0));
            mExperience2.setData(experienceList.get(1));
        }
    }

    private void updateTrainDetail(TrainingDetail trainingDetail) {
        Training training = trainingDetail.getTrain();
        if (training != null) {
            mTitleBar.setTitle(training.getTitle());
            mTitle.setText(training.getTitle());
            mIntroduce.setText(training.getRemark());
            mDifficulty.setText(getString(R.string.train_level, training.getLevel()));
            mDuration.setText(formatTime(training.getTime(),
                    R.string._seconds,
                    R.string._minutes,
                    R.string._minutes_x_seconds));
        }
    }

    @OnClick({R.id.relatedKnowledge,
            R.id.trainingExperience, R.id.writeExperience,
            R.id.startTraining})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.relatedKnowledge:
                if (mTrainingDetail != null && mTrainingDetail.getTrain() != null) {
                    Launcher.with(getActivity(), WebActivity.class)
                            .putExtra(WebActivity.EX_URL, API.getHost() + mTrainingDetail.getTrain().getKnowledgeUrl())
                            .execute();
                }
                break;
            case R.id.trainingExperience:
                Launcher.with(getActivity(), TrainingExperienceActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTraining)
                        .executeForResult(REQ_TRAIN_EXPERIENCE);
                break;
            case R.id.writeExperience:
                Launcher.with(getActivity(), WriteExperienceActivity.class)
                        .putExtra(ExtraKeys.TRAINING, mTraining)
                        .putExtra(ExtraKeys.TRAIN_RESULT, 0)
                        .execute();
                break;
            case R.id.startTraining:
                if (LocalUser.getUser().isLogin()) {
                    requestTrainingContent();
                } else {
                    Launcher.with(getActivity(), LoginActivity.class).execute();
                }
                break;
        }
    }

    private void requestTrainingContent() {
        if (mTraining.getPlayType() == Training.PLAY_TYPE_REMOVE
                || mTraining.getPlayType() == Training.PLAY_TYPE_MATCH_STAR) {
            Client.getTrainingContent(mTraining.getId()).setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<String>, List<Question<RemoveData>>>() {
                        @Override
                        protected String onInterceptData(String data) {
                            return SecurityUtil.AESDecrypt(data);
                        }

                        @Override
                        protected void onRespSuccessData(List<Question<RemoveData>> data) {
                            if (!data.isEmpty()) {
                                Question question = data.get(0);
                                if (question.getType() == Question.TYPE_MATCH) {
                                    startTraining(question);
                                }
                            }
                        }
                    }).fire();

        } else if (mTraining.getPlayType() == Training.PLAY_TYPE_SORT) {
            Client.getTrainingContent(mTraining.getId()).setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<String>, List<Question<SortData>>>() {
                        @Override
                        protected String onInterceptData(String data) {
                            return SecurityUtil.AESDecrypt(data);
                        }

                        @Override
                        protected void onRespSuccessData(List<Question<SortData>> data) {
                            if (!data.isEmpty()) {
                                Question question = data.get(0);
                                if (question.getType() == Question.TYPE_SORT) {
                                    startTraining(question);
                                }
                            }
                        }
                    }).fire();

        } else if (mTraining.getPlayType() == Training.PLAY_TYPE_JUDGEMENT) {
            Client.getTrainingContent(mTraining.getId()).setTag(TAG).setIndeterminate(this)
                    .setCallback(new Callback2D<Resp<String>, List<Question<KData>>>() {
                        @Override
                        protected String onInterceptData(String data) {
                            return SecurityUtil.AESDecrypt(data);
                        }

                        @Override
                        protected void onRespSuccessData(List<Question<KData>> data) {
                            if (!data.isEmpty()) {
                                Question question = data.get(0);
                                if (question.getType() == Question.TYPE_TRUE_OR_FALSE) {
                                    startTraining(question);
                                }
                            }
                        }
                    }).fire();
        }
    }

    private void startTraining(Question question) {
        if (mTrainingDetail != null) {
            Launcher.with(getActivity(), TrainingCountDownActivity.class)
                    .putExtra(ExtraKeys.TRAINING_DETAIL, mTrainingDetail)
                    .putExtra(ExtraKeys.QUESTION, question)
                    .execute();
        } else {
            requestTrainDetailAndStartTraining(question);
        }
    }

    private void requestTrainDetailAndStartTraining(final Question question) {
        Client.getTrainingDetail(mTraining.getId()).setTag(TAG)
                .setCallback(new Callback2D<Resp<TrainingDetail>, TrainingDetail>() {
                    @Override
                    protected void onRespSuccessData(TrainingDetail data) {
                        mTrainingDetail = data;
                        updateTrainDetail(data);
                        updateAchievementViews();
                        startTraining(question);
                    }
                }).fire();
    }

    private void share() {
        if (mTrainingDetail != null) {
            ShareDialog.with(getActivity())
                    .setTitle(getString(R.string.share_title))
                    .setShareTitle(getString(R.string.train_share_share_title, mTrainingDetail.getTrain().getTitle()))
                    .setShareDescription(getString(R.string.train_share_description))
                    .setShareUrl(String.format(Client.SHARE_URL_TRAIN_EXPERIENCE, mTraining.getId()))
                    .hasFeedback(true)
                    .setListener(new ShareDialog.OnShareDialogCallback() {
                        @Override
                        public void onSharePlatformClick(ShareDialog.SHARE_PLATFORM platform) {
                            Client.share().setTag(TAG).fire();
                        }

                        @Override
                        public void onFeedbackClick(View view) {
                            if (LocalUser.getUser().isLogin()) {
                                umengEventCount(UmengCountEventId.ME_FEEDBACK);
                                Launcher.with(getActivity(), FeedbackActivity.class)
                                        .putExtra(ExtraKeys.TRAINING, mTraining.getId())
                                        .execute();
                            } else {
                                Launcher.with(getActivity(), LoginActivity.class).execute();
                            }
                        }
                    }).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQ_TRAIN_EXPERIENCE && resultCode == RESULT_OK) {
            requestHotExperienceList();
        }
    }
}
