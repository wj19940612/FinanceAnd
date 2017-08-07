package com.sbai.finance.activity.studyroot;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.MyListView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 自习室
 */

public class StudyRoomActivity extends BaseActivity {
    @BindView(R.id.timeImg)
    ImageView mTimeImg;
    @BindView(R.id.time)
    TextView mTime;
    @BindView(R.id.continuousDay)
    TextView mContinuousDay;
    @BindView(R.id.longestContinuousDay)
    TextView mLongestContinuousDay;
    @BindView(R.id.totalScholarship)
    TextView mTotalScholarship;
    @BindView(R.id.studyInfo)
    RelativeLayout mStudyInfo;
    @BindView(R.id.testTitle)
    TextView mTestTitle;
    @BindView(R.id.listView)
    MyListView mListView;
    @BindView(R.id.commit)
    TextView mCommit;
    @BindView(R.id.testResult)
    TextView mTestResult;
    @BindView(R.id.rightAnswer)
    TextView mRightAnswer;
    @BindView(R.id.answerDetail)
    TextView mAnswerDetail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.commit)
    public void onViewClicked() {
    }
}
