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
import com.sbai.finance.model.missTalk.QuestionReply;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

import static android.app.Activity.RESULT_OK;

/**
 * Created by lixiaokuan0819 on 2017/8/3.
 */

public class ReplyDialogFragment extends BaseDialogFragment {

	private static final int REPLY = 1001;

	@BindView(R.id.replyContent)
	TextView mReplyContent;
	@BindView(R.id.reply)
	TextView mReply;
	@BindView(R.id.cancel)
	TextView mCancel;

	Unbinder unbinder;

	private QuestionReply.DataBean mQuestionReply;

	public static ReplyDialogFragment newInstance(QuestionReply.DataBean questionReply) {
		ReplyDialogFragment fragment = new ReplyDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putParcelable("questionReply", questionReply);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mQuestionReply = getArguments().getParcelable("questionReply");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_reply, container, false);
		unbinder = ButterKnife.bind(this, view);
		if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
			mReplyContent.setText(mQuestionReply.getUserModel().getUserName() + ":" + mQuestionReply.getContent());
		}

		return view;
	}


	@OnClick({R.id.reply, R.id.cancel})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.reply:
				if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
					if (LocalUser.getUser().isLogin()) {
						Launcher.with(getActivity(), ReplyActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mQuestionReply.getUserModel().getId())
								.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId())
								.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getId())
								.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName())
								.execute();
						dismissAllowingStateLoss();
					} else {
						Intent intent = new Intent(getActivity(), LoginActivity.class);
						startActivityForResult(intent, REPLY);
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
		if (requestCode == REPLY && resultCode == RESULT_OK) {
			if (mQuestionReply.getUserModel() != null && mQuestionReply != null) {
				Launcher.with(getActivity(), ReplyActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mQuestionReply.getUserModel().getId())
						.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId())
						.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getId())
						.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName())
						.execute();
				dismissAllowingStateLoss();
			}
		}
	}
}
