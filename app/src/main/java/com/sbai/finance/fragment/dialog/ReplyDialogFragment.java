package com.sbai.finance.fragment.dialog;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.miss.ReplyActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.miss.QuestionReply;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;
import static com.sbai.finance.activity.BaseActivity.REQ_LOGIN;

/**
 * Created by lixiaokuan0819 on 2017/8/3.
 */

public class ReplyDialogFragment extends BottomDialogFragment {

	public static final int REQ_REPLY = 1001;

	private Callback mCallback;

	public void setCallback(Callback callback) {
		mCallback = callback;
	}

	public interface Callback {
		void onLoginSuccess();
		void onReplySuccess();
	}

	@BindView(R.id.replyContent)
	TextView mReplyContent;
	@BindView(R.id.reply)
	TextView mReply;
	@BindView(R.id.cancel)
	TextView mCancel;

	Unbinder unbinder;

	private QuestionReply.DataBean mQuestionReply;

	public static ReplyDialogFragment newInstance() {
		return new ReplyDialogFragment();
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
			mReplyContent.setText(mQuestionReply.getUserModel().getUserName() + ":" + mQuestionReply.getContent());
		}
	}

	@OnClick({R.id.reply, R.id.cancel})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.reply:
				if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
					if (LocalUser.getUser().isLogin()) {
						Intent intent = new Intent(getActivity(), ReplyActivity.class);
						intent.putExtra(Launcher.EX_PAYLOAD, mQuestionReply.getUserModel().getId());
						intent.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId());
						intent.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getId());
						intent.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName());
						startActivityForResult(intent, REQ_REPLY);
					} else {
						if (mCallback != null) {
							mCallback.onLoginSuccess();
						}
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivityForResult(intent,REQ_LOGIN);
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

		if (requestCode == REQ_LOGIN && resultCode == RESULT_OK) {
			if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
				Intent intent = new Intent(getActivity(), ReplyActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, mQuestionReply.getUserModel().getId());
				intent.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId());
				intent.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getId());
				intent.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName());
				startActivityForResult(intent, REQ_REPLY);
			}
		}


		if (requestCode == REQ_REPLY && resultCode == RESULT_OK) {
			mCallback.onReplySuccess();
			dismissAllowingStateLoss();
		}
	}

	public void setItemData(QuestionReply.DataBean questionReply) {
		this.mQuestionReply = questionReply;
	}
}
