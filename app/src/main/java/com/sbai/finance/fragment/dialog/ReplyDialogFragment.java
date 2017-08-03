package com.sbai.finance.fragment.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.miss.ReplyActivity;
import com.sbai.finance.model.missTalk.QuestionReply;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * Created by lixiaokuan0819 on 2017/8/3.
 */

public class ReplyDialogFragment extends BaseDialogFragment{

	@BindView(R.id.replyContent)
	TextView mReplyContent;
	@BindView(R.id.reply)
	TextView mReply;
	@BindView(R.id.cancel)
	TextView mCancel;

	Unbinder unbinder;

	private QuestionReply.DataBean mQuestionReply;
	private int mInvitationUserId;

	public static ReplyDialogFragment newInstance(QuestionReply.DataBean questionReply, int invitationUserId) {
		ReplyDialogFragment fragment = new ReplyDialogFragment();
		Bundle bundle = new Bundle();
		bundle.putSerializable("questionReply", questionReply);
		bundle.putInt("invitationUserId", invitationUserId);
		fragment.setArguments(bundle);
		return fragment;
	}

	@Override
	public void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mQuestionReply = (QuestionReply.DataBean) getArguments().get("questionReply");
			mInvitationUserId = (int) getArguments().get("invitationUserId");
		}
	}

	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.dialog_fragment_reply, container, false);
		unbinder = ButterKnife.bind(this, view);
		mReplyContent.setText(mQuestionReply.getUserModel().getUserName() + ":" + mQuestionReply.getContent());
		return view;
	}


	@OnClick({R.id.replyContent, R.id.reply, R.id.cancel})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.replyContent:
				break;
			case R.id.reply:
				Launcher.with(getActivity(), ReplyActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mInvitationUserId)
						.putExtra(Launcher.EX_PAYLOAD_1, mQuestionReply.getDataId())
						.putExtra(Launcher.EX_PAYLOAD_2, mQuestionReply.getReplyParentId())
						.putExtra(Launcher.EX_PAYLOAD_3, mQuestionReply.getUserModel().getUserName())
						.execute();
				dismissAllowingStateLoss();
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
}
