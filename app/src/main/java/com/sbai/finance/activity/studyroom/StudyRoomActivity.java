package com.sbai.finance.activity.studyroom;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.finance.App;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.studyroom.MyStudyInfo;
import com.sbai.finance.model.studyroom.StudyOption;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.view.MyListView;

import java.util.List;

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
    @BindView(R.id.explainArea)
    RelativeLayout mExplainArea;
    @BindView(R.id.studyDays)
    TextView mStudyDays;
    private OptionAdapter mOptionAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        ButterKnife.bind(this);
        initStudyView();
        initListView();
        requestMyStudyData();
        requestTrainData();
    }

    private void initStudyView() {
        mTime.setText(DateUtil.getStudyFormatTime(System.currentTimeMillis())
                + " " + getString(R.string.week)
                + DateUtil.getDayOfWeek(System.currentTimeMillis()));
    }

    private void initListView() {
        mOptionAdapter = new OptionAdapter(getActivity());
        mListView.setAdapter(mOptionAdapter);
    }

    private void requestTrainData() {
        Client.getTrainCourse(AppInfo.getDeviceHardwareId(getActivity())).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<Object>, Object>() {
                    @Override
                    protected void onRespSuccessData(Object data) {
                        StudyOption studyOption = new Gson().fromJson(SecurityUtil.AESDecrypt((String) data), StudyOption.class);
                        updateTrainData(studyOption);
                    }
                }).fireFree();
    }

    private void updateTrainData(StudyOption data) {
        mOptionAdapter.clear();
        mOptionAdapter.addAll(data.getContent());
        mOptionAdapter.notifyDataSetChanged();
    }

    private void requestMyStudyData() {
        Client.getMyStudyInfo().setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<MyStudyInfo>, MyStudyInfo>() {
                    @Override
                    protected void onRespSuccessData(MyStudyInfo data) {
                        updateMyStudyData(data);
                    }
                }).fireFree();
    }

    private void updateMyStudyData(MyStudyInfo data) {
        mStudyDays.setText(String.valueOf(data.getTotalStudy()));
        mContinuousDay.setText(String.valueOf(data.getHoldStudy()));
        mLongestContinuousDay.setText(String.valueOf(data.getHoldStudyMax()));
        mTotalScholarship.setText(getString(R.string.ingot_number, String.valueOf(data.getTotalReward())));

    }

    @OnClick(R.id.commit)
    public void onViewClicked() {
    }

    static class OptionAdapter extends ArrayAdapter<StudyOption.ContentBean> {

        public OptionAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_study_room, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext());
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.option)
            TextView mOption;
            @BindView(R.id.checkbox)
            CheckBox mCheckbox;
            @BindView(R.id.answer)
            TextView mAnswer;
            @BindView(R.id.optionArea)
            LinearLayout mOptionArea;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(StudyOption.ContentBean item, Context context) {
                mOption.setText(item.getContent());
            }

            @OnClick(R.id.optionArea)
            public void onClick(View view) {

            }
        }
    }
}
