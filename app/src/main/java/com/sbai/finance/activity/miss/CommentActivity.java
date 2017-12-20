package com.sbai.finance.activity.miss;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.KeyBoardUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.UmengCountEventId;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.utils.audio.OnPlayRadioManager;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.android.volley.DefaultRetryPolicy.DEFAULT_BACKOFF_MULT;
import static com.android.volley.DefaultRetryPolicy.DEFAULT_TIMEOUT_MS;

/**
 * 对小姐姐的提问的评论页面
 */
public class CommentActivity extends BaseActivity implements OnPlayRadioManager{

    public static final String BROADCAST_ACTION_REPLY_SUCCESS = "broadcast_action_reply_success";

    public static final int COMMENT_TYPE_RADIO = 3;
    public static final int COMMENT_TYPE_QUESTION = 1;  //评论的类型：4话题,3电台

    public static final int REQ_CODE_COMMENT = 5091;
    public static final int REQ_CODE_COMMENT_LOGIN = 1705;

    @BindView(R.id.questionComment)
    EditText mQuestionComment;
    @BindView(R.id.wordsNumber)
    TextView mWordsNumber;
    @BindView(R.id.publish)
    TextView mPublish;
    @BindView(R.id.cancelArea)
    View mCancelArea;
    private int mInvitationUserId;
    private int mDataId;
    private String mReplyParentId;
    private String mUserName;

    private int mAudioId;
    private int mCommentType;
    private int mRadioId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);
        ButterKnife.bind(this);
        initData();
        initView();
    }

    private void initData() {
        Intent intent = getIntent();
        mInvitationUserId = getIntent().getIntExtra(Launcher.EX_PAYLOAD, -1);
        mDataId = getIntent().getIntExtra(Launcher.EX_PAYLOAD_1, -1);
        mReplyParentId = getIntent().getStringExtra(Launcher.EX_PAYLOAD_2);
        mUserName = getIntent().getStringExtra(Launcher.EX_PAYLOAD_3);

        mCommentType = intent.getIntExtra(ExtraKeys.COMMENT_SOURCE, COMMENT_TYPE_QUESTION);
        mAudioId = intent.getIntExtra(ExtraKeys.IAudio, -1);
        mRadioId = intent.getIntExtra(ExtraKeys.RADIO, 0);
    }


    @Override
    public void onBackPressed() {
        if (TextUtils.isEmpty(mQuestionComment.getText())) {
            super.onBackPressed();
        }
        showCloseDialog();
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
        getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
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

    @OnClick({R.id.publish, R.id.cancelArea})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.publish:
                umengEventCount(UmengCountEventId.MISS_TALK_COMMENT);
                requestPublishComment();
                break;
            case R.id.cancelArea:
                if (TextUtils.isEmpty(mQuestionComment.getText())) {
                    finish();
                }

                showCloseDialog();
                break;
        }
    }

    private void showCloseDialog() {
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
                        mQuestionComment.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mQuestionComment.setFocusable(true);
                                mQuestionComment.requestFocus();
                                KeyBoardUtils.openKeyBoard(mQuestionComment);
                            }
                        }, 50);
                    }
                }).show();
    }

    private void requestPublishComment() {
        mPublish.setEnabled(false);
        String content = mQuestionComment.getText().toString().trim();
        Client.addComment(mInvitationUserId, mReplyParentId, mCommentType, content, mDataId, mAudioId, mRadioId)
                .setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT_MS, 0, DEFAULT_BACKOFF_MULT))
                .setTag(TAG)
                .setCallback(new Callback<Resp<Object>>() {
                    @Override
                    protected void onRespSuccess(Resp<Object> resp) {
                        if (resp.isSuccess()) {
                            ToastUtil.show(R.string.publish_success);
                            Intent intent = new Intent();
                            intent.setAction(BROADCAST_ACTION_REPLY_SUCCESS);
                            intent.putExtra(ExtraKeys.QUESTION_ID, mDataId);
                            setResult(RESULT_OK, intent);
                            LocalBroadcastManager.getInstance(getActivity()).sendBroadcast(intent);
                            Intent lastIntent = getIntent();
                            lastIntent.putExtra(ExtraKeys.QUESTION_ID, mDataId);
                            setResult(RESULT_OK, lastIntent);
                            finish();
                        } else {
                            ToastUtil.show(resp.getMsg());
                            mPublish.setEnabled(true);
                        }
                    }
                }).fireFree();
    }
}
