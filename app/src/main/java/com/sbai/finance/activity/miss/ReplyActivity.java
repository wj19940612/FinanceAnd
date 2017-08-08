package com.sbai.finance.activity.miss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 回复页面
 */
public class ReplyActivity extends BaseActivity {

    public static final String REFRESH_REPLY = "refresh_reply";

    @BindView(R.id.questionComment)
    EditText mQuestionComment;
    @BindView(R.id.wordsNumber)
    TextView mWordsNumber;
    @BindView(R.id.publish)
    TextView mPublish;
    private int mInvitationUserId;
    private int mDataId;
    private String mReplyParentId;
    private String mUserName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reply);
        ButterKnife.bind(this);
        initData();
        initView();
        if (!LocalUser.getUser().isLogin()) {
            Launcher.with(this, LoginActivity.class).execute();
        }
    }

    private void initData() {
        mInvitationUserId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        mDataId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mReplyParentId = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);
        mUserName = getIntent().getStringExtra(Launcher.EX_PAYLOAD_3);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mQuestionComment.removeTextChangedListener(mValidationWatcher);
    }

    private void initView() {
        if (mUserName != null) {
            mQuestionComment.setHint(getString(R.string.reply_someBody, mUserName));
        }
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
        requestReplyComment();
    }

    private void requestReplyComment() {
        Client.addComment(mInvitationUserId, mReplyParentId, mQuestionComment.getText().toString().trim(), mDataId)
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            Intent intent = new Intent();
                            intent.setAction(REFRESH_REPLY);
                            LocalBroadcastManager.getInstance(ReplyActivity.this).sendBroadcastSync(intent);
                            ToastUtil.show(R.string.publish_success);
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                        }
                    }
                }).fireFree();
    }
}
