package com.sbai.finance.activity.miss;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.HorizontalGridView;
import com.sbai.finance.view.MissInfoView;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 我的提问页面
 */
public class MyQuestionsActivity extends BaseActivity {

    @BindView(R.id.questionComment)
    EditText mQuestionComment;
    @BindView(R.id.wordsNumber)
    TextView mWordsNumber;
    @BindView(R.id.missInfoGv)
    HorizontalGridView mMissInfoGv;
    @BindView(R.id.commit)
    TextView mCommit;

    private GirdViewAdapter mGirdViewAdapter;
    private int mSelectedIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_questions);
        ButterKnife.bind(this);
        initView();
        requestMissData();
    }

    private void requestMissData() {
        mGirdViewAdapter.clear();
        for (int i = 0; i < 10; i++) {
            mGirdViewAdapter.add(R.drawable.ic_default_avatar);
        }
        mGirdViewAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQuestionComment.removeTextChangedListener(mValidationWatcher);
    }

    private void initView() {
        mQuestionComment.addTextChangedListener(mValidationWatcher);
        mGirdViewAdapter = new GirdViewAdapter(getActivity());
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
            if (TextUtils.isEmpty(mQuestionComment.getText())) {
                mCommit.setEnabled(false);
            } else {
                mCommit.setEnabled(true);
            }
            mWordsNumber.setText(getString(R.string.words_number, mQuestionComment.getText().length()));
        }
    };

    static class GirdViewAdapter extends ArrayAdapter<Integer> {
        interface OnSelectedCallback {
            void onClick(int oldIndex, int index);
        }

        public GirdViewAdapter(@NonNull Context context) {
            super(context, 0);
        }

        private OnSelectedCallback mOnSelectedCallback;
        private int mSelectedIndex;

        public void setOnSelectedCallback(OnSelectedCallback onSelectedListener) {
            mOnSelectedCallback = onSelectedListener;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_my_questions, null, true);
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

            private void bindDataWithView(Integer item, final int position, final OnSelectedCallback callBack) {
                mMissInfo.setImgRes(item).setUserName("小米");
                mMissInfo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mMissInfo.isSelected()) {
                            mMissInfo.setSelected(false);
                            callBack.onClick(-1, -1);
                            mSelectedIndex = -1;
                        } else {
                            mMissInfo.setSelected(true);
                            callBack.onClick(mSelectedIndex, position);
                            mSelectedIndex = position;
                        }
                    }
                });
            }
        }
    }
}
