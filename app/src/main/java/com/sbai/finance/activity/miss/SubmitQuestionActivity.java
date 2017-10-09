package com.sbai.finance.activity.miss;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.miss.Miss;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.HorizontalGridView;
import com.sbai.finance.view.MissInfoView;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 提交问题页面
 */
public class SubmitQuestionActivity extends BaseActivity {

    public static final int REQ_CODE_ASK_QUESTION_LIKE_MISS = 6818;

    @BindView(R.id.questionComment)
    EditText mQuestionComment;
    @BindView(R.id.wordsNumber)
    TextView mWordsNumber;
    @BindView(R.id.missInfoGv)
    HorizontalGridView mMissInfoGv;
    @BindView(R.id.title)
    TitleBar mTitle;
    private GirdViewAdapter mGirdViewAdapter;
    private int mSelectedIndex = -1;
    private int mDefaultMissId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_submit_question);
        ButterKnife.bind(this);
        initData(getIntent());
        initView();
        requestMissData();
    }

    private void initData(Intent intent) {
        mDefaultMissId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
    }

    private void initView() {
        mTitle.setRightViewEnable(false);
        mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.unluckyText));
        mTitle.setOnRightViewClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                requestCommitQuestion();
            }
        });
        mQuestionComment.setFocusable(true);
        mQuestionComment.requestFocus();
        mQuestionComment.addTextChangedListener(mValidationWatcher);
        mGirdViewAdapter = new GirdViewAdapter(getActivity(), mDefaultMissId);
        mGirdViewAdapter.setOnSelectedCallback(new GirdViewAdapter.OnSelectedCallback() {
            @Override
            public void onClick(int oldIndex, int index) {
                mSelectedIndex = index;
                if (oldIndex > -1 && oldIndex != index) {
                    clearFocus(oldIndex);
                }
            }
        });
        mMissInfoGv.setAdapter(mGirdViewAdapter);
    }

    private void requestMissData() {
        Client.getMissList().setTag(TAG)
                .setCallback(new Callback2D<Resp<List<Miss>>, List<Miss>>() {
                    @Override
                    protected void onRespSuccessData(List<Miss> data) {
                        updateMissData(data);
                    }
                }).fireFree();
    }

    private void requestCommitQuestion() {
        umengEventCount(UmengCountEventId.MISS_TALK_ASK_QUESTION);
        Integer missId = null;
        if (mSelectedIndex != -1) {
            missId = mGirdViewAdapter.getItem(mSelectedIndex).getId();
        }
        Client.addQuestion(mQuestionComment.getText().toString().trim(), missId).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(R.string.question_commit_and_please_wait_miss_answer);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fire();
    }

    private void updateMissData(List<Miss> data) {
        mGirdViewAdapter.clear();
        mGirdViewAdapter.addAll(data);
        mGirdViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mQuestionComment.getText())) {
            super.onBackPressed();
        }
        SmartDialog.single(getActivity(), getString(R.string.give_up_question_no_restore_comment))
                .setTitle(getString(R.string.hint))
                .setNegative(R.string.give_up_question, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositive(R.string.continue_question, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                    }
                }).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQuestionComment.removeTextChangedListener(mValidationWatcher);
    }


    private void clearFocus(int index) {
        if (mMissInfoGv.getChildCount() > index) {
            View view = mMissInfoGv.getChildAt(index);
            if (view != null) {
                MissInfoView missInfoView = (MissInfoView) view.findViewById(R.id.missInfo);
                if (missInfoView != null) {
                    missInfoView.setSelected(false);
                }
            }

        }
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mQuestionComment.getText().toString().trim())) {
                mTitle.setRightViewEnable(false);
                mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.unluckyText));
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            } else if (mQuestionComment.getText().length() > 140) {
                mTitle.setRightViewEnable(false);
                mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.unluckyText));
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.redAssist));
            } else {
                mTitle.setRightViewEnable(true);
                mTitle.setRightTextColor(ContextCompat.getColorStateList(getActivity(), R.color.colorPrimary));
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            }
            mWordsNumber.setText(getString(R.string.words_number, mQuestionComment.getText().length()));
        }
    };

    static class GirdViewAdapter extends ArrayAdapter<Miss> {
        interface OnSelectedCallback {
            void onClick(int oldIndex, int index);
        }

        public GirdViewAdapter(@NonNull Context context, int defaultMissId) {
            super(context, 0);
            mDefaultMissId = defaultMissId;
        }

        private OnSelectedCallback mOnSelectedCallback;
        private int mSelectedIndex;
        private int mDefaultMissId;

        public void setOnSelectedCallback(OnSelectedCallback onSelectedListener) {
            mOnSelectedCallback = onSelectedListener;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_miss_info, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), position, mOnSelectedCallback);
            return convertView;
        }

        class ViewHolder {
            @BindView(R.id.missInfo)
            MissInfoView mMissInfo;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            private void bindDataWithView(Miss item, final int position, final OnSelectedCallback callBack) {
                mMissInfo.setImgRes(item.getPortrait()).setUserName(item.getName());
                if (mDefaultMissId == item.getId()) {
                    setSelected(position, callBack);
                    mDefaultMissId = -1;
                }
                mMissInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMissInfo.isSelected()) {
                            setUnSelected(callBack);
                        } else {
                            setSelected(position, callBack);
                        }
                    }
                });
            }

            private void setUnSelected(OnSelectedCallback callBack) {
                mMissInfo.setSelected(false);
                callBack.onClick(-1, -1);
                mSelectedIndex = -1;
            }

            private void setSelected(int position, OnSelectedCallback callBack) {
                mMissInfo.setSelected(true);
                callBack.onClick(mSelectedIndex, position);
                mSelectedIndex = position;
            }

        }
    }
}
