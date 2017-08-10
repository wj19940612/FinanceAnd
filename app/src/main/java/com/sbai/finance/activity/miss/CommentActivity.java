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
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
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
    private int mInvitationUserId;
    private int mDataId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        mInvitationUserId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        mDataId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
    }


    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mQuestionComment.getText())) {
            super.onBackPressed();
        }
        SmartDialog.single(getActivity(), getString(R.string.give_up_no_restore_comment))
                .setTitle(getString(R.string.hint))
                .setNegative(R.string.give_up, new SmartDialog.OnClickListener() {
                    @Override
                    public void onClick(Dialog dialog) {
                        dialog.dismiss();
                        finish();
                    }
                })
                .setPositive(R.string.continue_comment, new SmartDialog.OnClickListener() {
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

    @Override
    protected void onPostResume() {
        super.onPostResume();
    }

    private void initView() {
        mQuestionComment.addTextChangedListener(mValidationWatcher);
    }


    private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
        @Override
        public void afterTextChanged(Editable s) {
            if (TextUtils.isEmpty(mQuestionComment.getText().toString().trim())) {
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
        requestPublishComment();

    }

    private void requestPublishComment() {
        mPublish.setEnabled(false);
        Client.addComment(mInvitationUserId, null, mQuestionComment.getText().toString().trim(), mDataId)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(R.string.publish_success);
                            setResult(RESULT_OK);
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                            mPublish.setEnabled(true);
                        }
                    }
                }).fireFree();
    }
}
