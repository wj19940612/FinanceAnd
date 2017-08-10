package com.sbai.finance.activity.studyroom;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.studyroom.MyStudyInfo;
import com.sbai.finance.model.studyroom.StudyOption;
import com.sbai.finance.model.studyroom.StudyResult;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.AppInfo;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.SecurityUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.TitleBar;

import java.util.ArrayList;
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
    @BindView(R.id.title)
    TitleBar mTitle;
    @BindView(R.id.scrollView)
    ScrollView mScrollView;
    private OptionAdapter mOptionAdapter;
    private boolean mIsLearned;
    private int mSelectedIndex = -1;
    private String mTrainId;
    private BroadcastReceiver mLoginReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            requestMyStudyData();
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_study_room);
        ButterKnife.bind(this);
        initStudyView();
        initListView();
        initLoginReceiver();
        requestMyStudyData();
        requestTrainData();
    }

    private void initLoginReceiver() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(LoginActivity.ACTION_LOGIN_SUCCESS);
        LocalBroadcastManager.getInstance(getActivity())
                .registerReceiver(mLoginReceiver, intentFilter);
    }

    private void initStudyView() {
        mTime.setText(DateUtil.getStudyFormatTime(System.currentTimeMillis())
                + " " + getString(R.string.week)
                + DateUtil.getDayOfWeek(System.currentTimeMillis()));
        if (DateUtil.isDayTime()) {
            mTimeImg.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_study_room_day));
        } else {
            mTimeImg.setImageDrawable(ContextCompat.getDrawable(getActivity(), R.drawable.ic_study_room_night));
        }
    }

    private void initListView() {
        scrollToTop(mTitle, mScrollView);
        mOptionAdapter = new OptionAdapter(getActivity());
        mOptionAdapter.setOnClickCallback(new OptionAdapter.OnClickCallback() {
            @Override
            public void onSelect(int oldIndex, int index) {
                mSelectedIndex = index;
                if (oldIndex == -1 && oldIndex == index) return;
                clearFocus(oldIndex);
                if (!mIsLearned) {
                    mCommit.setEnabled(true);
                }
            }

            @Override
            public void onUnSelected() {
                mSelectedIndex = -1;
                mCommit.setEnabled(false);
            }
        });
        mListView.setAdapter(mOptionAdapter);
    }

    private void clearFocus(int index) {
        if (mListView != null && mListView.getCount() > index) {
            View view = mListView.getChildAt(index);
            if (view != null) {
                CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                if (cb != null && cb.isChecked()) {
                    cb.setChecked(false);
                }
            }
        }
    }

    private void updateResultView() {
        mCommit.setVisibility(View.GONE);
        mTestResult.setVisibility(View.VISIBLE);
        mExplainArea.setVisibility(View.VISIBLE);
        StudyOption.ContentBean item = mOptionAdapter.getItem(mSelectedIndex);
        if (item == null) return;
        if (item.isRight()) {
            mTestResult.setText(getString(R.string.answer_right_know_more));
            updateResultListView(true);
        } else {
            mTestResult.setText(getString(R.string.answer_wrong_study_together));
            updateResultListView(false);
        }
    }

    private void updateResultListView(boolean right) {
        if (mListView != null) {
            mListView.setEnabled(false);
            for (int i = 0; i < mListView.getCount(); i++) {
                View view = mListView.getChildAt(i);
                if (view != null) {
                    CheckBox cb = (CheckBox) view.findViewById(R.id.checkbox);
                    TextView answer = (TextView) view.findViewById(R.id.answer);
                    LinearLayout optionArea = (LinearLayout) view.findViewById(R.id.optionArea);
                    if (cb != null) {
                        cb.setVisibility(View.GONE);
                    }
                    if (i == mSelectedIndex) {
                        if (right) {
                            if (answer != null) {
                                answer.setVisibility(View.VISIBLE);
                                answer.setText(getString(R.string.answer_right));
                            }
                            if (optionArea != null) {
                                optionArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.colorPrimary));
                            }
                        } else {
                            if (answer != null) {
                                answer.setVisibility(View.VISIBLE);
                                answer.setText(getString(R.string.answer_wrong));
                            }
                            if (optionArea != null) {
                                optionArea.setBackgroundColor(ContextCompat.getColor(getActivity(), R.color.yellowAssist));
                            }
                        }
                    }
                }
            }
        }
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
        if (data == null) return;
        mOptionAdapter.clear();
        mOptionAdapter.addAll(data.getContent());
        mOptionAdapter.notifyDataSetChanged();

        mTrainId = data.getId();
        //update explain info
        mTestTitle.setText(data.getTitle());
        mAnswerDetail.setText(data.getAnalysis());
        for (StudyOption.ContentBean contentBean : data.getContent()) {
            if (contentBean.isRight()) {
                mRightAnswer.setText(getString(R.string.right_answer, data.getTitle() + ":" + contentBean.getContent()));
                break;
            }
        }
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
        mContinuousDay.setText(getString(R.string.day, data.getHoldStudy()));
        mLongestContinuousDay.setText(getString(R.string.day, data.getHoldStudyMax()));
        mTotalScholarship.setText(getString(R.string.ingot_number_no_blank, data.getTotalReward()));
        if (LocalUser.getUser().isLogin()) {
            if (data.isLearned()) {
                mCommit.setText(getString(R.string.today_already_study));
                mCommit.setEnabled(false);
                mIsLearned = true;
            } else {
                mCommit.setText(getString(R.string.hand_in_paper));
                mIsLearned = false;
            }
        }
    }

    private void requestHandInPaper() {
        StudyResult studyResult = new StudyResult();
        studyResult.setDataId(AppInfo.getDeviceHardwareId(getActivity()));
        List<StudyResult.AnswersBean> answerList = new ArrayList<>();
        StudyResult.AnswersBean answersBean = new StudyResult.AnswersBean();
        answersBean.setTopicId(mTrainId);
        answerList.add(answersBean);
        studyResult.setAnswers(answerList);

        List<StudyResult.AnswersBean.AnswerIdsBean> answerIdList = new ArrayList<>();
        StudyResult.AnswersBean.AnswerIdsBean answersId = new StudyResult.AnswersBean.AnswerIdsBean();
        answersId.setOptionId(mOptionAdapter.getItem(mSelectedIndex).getId());
        answerIdList.add(answersId);
        answersBean.setAnswerIds(answerIdList);

        Client.handInPaper(new Gson().toJson(studyResult)).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            updateResultView();
                            requestMyStudyData();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    @OnClick(R.id.commit)
    public void onViewClicked() {
        if (LocalUser.getUser().isLogin()) {
            requestHandInPaper();
        } else {
            Launcher.with(getActivity(), LoginActivity.class).execute();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mLoginReceiver);
    }

    static class OptionAdapter extends ArrayAdapter<StudyOption.ContentBean> {
        public interface OnClickCallback {
            void onSelect(int oldIndex, int index);

            void onUnSelected();
        }

        private OnClickCallback mOnClickCallback;
        private int mSelectedIndex = -1;

        public void setOnClickCallback(OnClickCallback onClickCallback) {
            mOnClickCallback = onClickCallback;
        }

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
            viewHolder.bindDataWithView(getItem(position), position, getContext(), mOnClickCallback);
            return convertView;
        }

        class ViewHolder {
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

            public void bindDataWithView(final StudyOption.ContentBean item, final int position, Context context, final OnClickCallback onClickCallback) {
                mOption.setText(item.getContent());
                mOptionArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCheckbox.isChecked()) {
                            setUnSelected(onClickCallback);
                        } else {
                            setSelected(position, onClickCallback);
                        }
                    }
                });
            }

            private void setUnSelected(OnClickCallback onClickCallback) {
                mCheckbox.setChecked(false);
                if (onClickCallback != null) {
                    onClickCallback.onUnSelected();
                }
                mSelectedIndex = -1;
            }

            private void setSelected(int position, OnClickCallback onClickCallback) {
                mCheckbox.setChecked(true);
                if (onClickCallback != null) {
                    onClickCallback.onSelect(mSelectedIndex, position);
                }
                mSelectedIndex = position;
            }
        }
    }
}