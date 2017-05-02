package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.userName;

public class UserDataActivity extends BaseActivity {

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(userName)
	TextView mUserName;
	@BindView(R.id.location)
	TextView mLocation;
	@BindView(R.id.attentionNum)
	TextView mAttentionNum;
	@BindView(R.id.fansNum)
	TextView mFansNum;
	@BindView(R.id.hisPublish)
	RelativeLayout mHisPublish;
	@BindView(R.id.authentication)
	TextView mAuthentication;
	@BindView(R.id.realNameAuthentication)
	RelativeLayout mRealNameAuthentication;
	@BindView(R.id.attention)
	TextView mAttention;
	@BindView(R.id.shield)
	TextView mShield;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_data);
		ButterKnife.bind(this);

		Glide.with(getActivity()).load("")
				.placeholder(R.drawable.ic_default_avatar_big)
				.bitmapTransform(new GlideCircleTransform(getActivity()))
				.into(mAvatar);
		mUserName.setText("古天乐");
		mUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
		mLocation.setText(" 浙江  温州 ");
		mAttentionNum.setText(getString(R.string.attention_number, 100));
		mFansNum.setText(getString(R.string.fans_number, 100));

	}

	@OnClick({R.id.attention, R.id.shield})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.attention:

				break;
			case R.id.shield:

				SmartDialog.with(getActivity(),
						getString(R.string.shield_dialog_content, "古天乐")
						, getString(R.string.shield_dialog_title, "古天乐"))
						.setPositive(android.R.string.ok, new SmartDialog.OnClickListener() {
							@Override
							public void onClick(Dialog dialog) {
								dialog.dismiss();
								ToastUtil.curt("移除 " + "古天乐");

							}
						})
						.setTitleMaxLines(2)
						.setNegative(android.R.string.cancel)
						.show();

				break;
		}
	}
}
