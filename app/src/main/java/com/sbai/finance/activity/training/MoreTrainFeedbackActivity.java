package com.sbai.finance.activity.training;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.model.training.TrainFeedback;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.EditWithWordLimitView;
import com.sbai.finance.view.MyListView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 更多训练反馈页
 */

public class MoreTrainFeedbackActivity extends BaseActivity implements View.OnTouchListener {
    @BindView(R.id.listView)
    MyListView mListView;
    @BindView(R.id.comment)
    EditWithWordLimitView mComment;
    @BindView(R.id.commit)
    TextView mCommit;
    @BindView(R.id.changeTrain)
    TextView mChangeTrain;
    private TrainAdapter mTrainAdapter;
    private List<TrainFeedback> mIds;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_train_feedback);
        ButterKnife.bind(this);
        initView();
        requestTrainData();
    }

    private void initView() {
        mIds = new ArrayList<>();
        mTrainAdapter = new TrainAdapter(getActivity());
        mTrainAdapter.setOnItemClickCallback(new TrainAdapter.OnItemClickCallback() {
            @Override
            public void onSelect(TrainFeedback trainFeedback) {
                mIds.add(trainFeedback);
                setCommitEnable(mComment.getInputComment());
            }

            @Override
            public void onCancel(TrainFeedback trainFeedback) {
                mIds.remove(trainFeedback);
                setCommitEnable(mComment.getInputComment());
            }
        });
        mListView.setAdapter(mTrainAdapter);
        mComment.clearFocus();
        mComment.setOnTextChangeCallback(new EditWithWordLimitView.OnTextChangeCallback() {
            @Override
            public void afterText(String text) {
                setCommitEnable(text);
            }
        });
    }

    private void requestTrainData() {
        Client.getFeedBackTrainList(5).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<TrainFeedback>>, List<TrainFeedback>>() {
                    @Override
                    protected void onRespSuccessData(List<TrainFeedback> data) {
                        updateTrainData(data);
                    }
                }).fireFree();
    }

    private void requestCommitFeedback() {
        StringBuilder sb = new StringBuilder();
        for (TrainFeedback feedback : mIds) {
            sb.append(feedback.getId()).append(",");
        }
        sb.deleteCharAt(sb.length() - 1);
        Client.commitFeedBackTrain(sb.toString(), mComment.getInputComment().trim()).setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {

                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(getString(R.string.thank_you_feedback));
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }

    private void updateTrainData(List<TrainFeedback> data) {
        mIds.clear();
        mCommit.setEnabled(false);
        mTrainAdapter.clear();
        mTrainAdapter.addAll(data);
        mTrainAdapter.notifyDataSetChanged();
    }

    private void setCommitEnable(String comment) {
        if ((mIds.isEmpty() && comment.trim().isEmpty()) || comment.length() > mComment.getWordLimitCount()) {
            mCommit.setEnabled(false);
        } else {
            mCommit.setEnabled(true);
        }
    }

    @OnClick({R.id.changeTrain, R.id.commit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.changeTrain:
                requestTrainData();
                break;
            case R.id.commit:
                requestCommitFeedback();
                break;
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if ((v.getId() == R.id.content && canVerticalScroll(mComment.getEditText()))) {
            v.getParent().requestDisallowInterceptTouchEvent(true);
            if (event.getAction() == MotionEvent.ACTION_UP) {
                v.getParent().requestDisallowInterceptTouchEvent(false);
            }
        }
        return false;
    }

    /**
     * EditText竖直方向是否可以滚动
     *
     * @param editText 需要判断的EditText
     * @return true：可以滚动   false：不可以滚动
     */
    private boolean canVerticalScroll(EditText editText) {
        //滚动的距离
        int scrollY = editText.getScrollY();
        //控件内容的总高度
        int scrollRange = editText.getLayout().getHeight();
        //控件实际显示的高度
        int scrollExtent = editText.getHeight() - editText.getCompoundPaddingTop() - editText.getCompoundPaddingBottom();
        //控件内容总高度与实际显示高度的差值
        int scrollDifference = scrollRange - scrollExtent;

        if (scrollDifference == 0) {
            return false;
        }

        return (scrollY > 0) || (scrollY < scrollDifference - 1);
    }

    static class TrainAdapter extends ArrayAdapter<TrainFeedback> {

        public interface OnItemClickCallback {
            void onSelect(TrainFeedback trainFeedback);

            void onCancel(TrainFeedback trainFeedback);
        }

        private OnItemClickCallback mOnItemClickCallback;

        public void setOnItemClickCallback(OnItemClickCallback onItemClickCallback) {
            mOnItemClickCallback = onItemClickCallback;
        }

        public TrainAdapter(@NonNull Context context) {
            super(context, 0);
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

            ViewHolder viewHolder;
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_train_feedback, null, true);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder) convertView.getTag();
            }
            viewHolder.bindDataWithView(getItem(position), getContext(), mOnItemClickCallback);
            return convertView;
        }

        static class ViewHolder {
            @BindView(R.id.option)
            TextView mOption;
            @BindView(R.id.checkbox)
            CheckBox mCheckbox;
            @BindView(R.id.optionArea)
            LinearLayout mOptionArea;

            ViewHolder(View view) {
                ButterKnife.bind(this, view);
            }

            public void bindDataWithView(final TrainFeedback item, Context context, final OnItemClickCallback onItemClickCallback) {
                if (item.isChecked()) {
                    mCheckbox.setChecked(true);
                } else {
                    mCheckbox.setChecked(false);
                }
                mOption.setText(item.getQuestion());
                mCheckbox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCheckbox.isChecked()) {
                            onItemClickCallback.onSelect(item);
                        } else {
                            onItemClickCallback.onCancel(item);
                        }
                    }
                });
                mOptionArea.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (mCheckbox.isChecked()) {
                            onItemClickCallback.onCancel(item);
                            mCheckbox.setChecked(false);
                        } else {
                            onItemClickCallback.onSelect(item);
                            mCheckbox.setChecked(true);
                        }
                    }
                });
            }
        }
    }
}
