package com.sbai.finance.fragment.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.CommentActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.QuestionReply;
import com.sbai.finance.model.radio.Radio;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by ${wangJie} on 2017/11/23.
 */

public class ReplyDialogFragment extends BottomDialogFragment {
    public static final int REQ_REPLY = 1001;

    private Callback mCallback;

    public void setCallback(Callback callback) {
        mCallback = callback;
    }

    public interface Callback {
        void onLoginSuccess();
    }

    @BindView(R.id.replyContent)
    TextView mReplyContent;
    @BindView(R.id.reply)
    TextView mReply;
    @BindView(R.id.cancel)
    TextView mCancel;

    Unbinder unbinder;

    private QuestionReply.DataBean mQuestionReply;
    private Radio mRadio;

    public static ReplyDialogFragment newInstance(QuestionReply.DataBean questionReply) {
        return newInstance(questionReply, null);
    }

    public static ReplyDialogFragment newInstance(QuestionReply.DataBean questionReply, Radio radio) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(ExtraKeys.QUESTION, questionReply);
        bundle.putParcelable(ExtraKeys.RADIO, radio);
        ReplyDialogFragment replyDialogFragment = new ReplyDialogFragment();
        replyDialogFragment.setArguments(bundle);
        return replyDialogFragment;
    }

    public void setData(QuestionReply.DataBean questionReply, Radio radio) {
        mQuestionReply = questionReply;
        mRadio = radio;
    }

    public void setData(QuestionReply.DataBean questionReply) {
        setData(questionReply, null);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mQuestionReply = getArguments().getParcelable(ExtraKeys.QUESTION);
            mRadio = getArguments().getParcelable(ExtraKeys.RADIO);
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_fragment_reply, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
            String s = mQuestionReply.getUserModel().getUserName() + ":" + mQuestionReply.getContent();
            mReplyContent.setText(s);
        }
    }

    @OnClick({R.id.reply, R.id.cancel})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.reply:
                if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
                    if (LocalUser.getUser().isLogin()) {
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra(Launcher.EX_PAYLOAD, mQuestionReply.getUserModel().getId());
                        intent.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId());
                        intent.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getId());
                        intent.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName());
                        if (mRadio != null) {
                            intent.putExtra(ExtraKeys.RADIO, mRadio.getRadioId());
                            intent.putExtra(ExtraKeys.IAudio, mRadio.getAudioId());
                            intent.putExtra(ExtraKeys.COMMENT_SOURCE, CommentActivity.COMMENT_TYPE_RADIO);
                        }
                        startActivityForResult(intent, REQ_REPLY);
                        dismiss();
                    } else {
                        if (mCallback != null) {
                            mCallback.onLoginSuccess();
                        }
                        Intent intent = new Intent(getActivity(), LoginActivity.class);
                        startActivityForResult(intent, LoginActivity.REQ_CODE_LOGIN);
                    }
                }
                break;
            case R.id.cancel:
                dismiss();
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == BaseActivity.RESULT_OK) {
            if (requestCode == LoginActivity.REQ_CODE_LOGIN) {
                if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
                    Intent intent = new Intent(getActivity(), CommentActivity.class);
                    intent.putExtra(Launcher.EX_PAYLOAD, mQuestionReply.getUserModel().getId());
                    intent.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId());
                    intent.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getId());
                    intent.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName());
                    startActivityForResult(intent, REQ_REPLY);
                }
                dismiss();
            }
        } else {
            dismiss();
        }

    }
}
