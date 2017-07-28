package com.sbai.finance.activity.miss;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 对小姐姐的提问的评论页面
 */
public class CommentActivity extends BaseActivity {

    @BindView(R.id.questionComment)
    EditText mQuestionComment;
    @BindView(R.id.wordsNumber)
    TextView mWordsNumber;
    @BindView(R.id.publish)
    TextView mPublish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initView();
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

    private void initView() {
        mQuestionComment.addTextChangedListener(mValidationWatcher);
    }

    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mQuestionComment.getText())) {
                mPublish.setEnabled(false);
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            } else if (mQuestionComment.getText().length() > 140) {
                mPublish.setEnabled(false);
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.redAssist));
            } else {
                mPublish.setEnabled(true);
                mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
            }
            mWordsNumber.setText(getString(R.string.words_number, mQuestionComment.getText().length()));
        }
    };

    @OnClick(R.id.publish)
    public void onViewClicked() {
        // TODO: 28/07/2017 发送发布
        /*发布：
1.点击发布传图片慢的时候可视情况加loading效果
2.服务端也校验，然后经过敏感词处理，没问题的直接可在前台展示
3.成功以后出现toast（发布成功），然后返回上级页面
4.校验失败给出toast（输入内容有误），然后停留在此页面
        * */

    }
}
