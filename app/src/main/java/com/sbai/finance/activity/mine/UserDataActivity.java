package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.AvatarDialogFragment;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.UserData;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class UserDataActivity extends BaseActivity {

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.userName)
	TextView mUserName;
	@BindView(R.id.location)
	TextView mLocation;
	@BindView(R.id.attentionNum)
	TextView mAttentionNum;
	@BindView(R.id.fansNum)
	TextView mFansNum;
	@BindView(R.id.hisPublishArea)
	RelativeLayout mHisPublishArea;
	@BindView(R.id.attention)
	TextView mAttention;
	@BindView(R.id.shield)
	TextView mShield;
	@BindView(R.id.hisPublish)
	TextView mHisPublish;
	@BindView(R.id.attentionShieldArea)
	LinearLayout mAttentionShieldArea;
	@BindView(R.id.authenticationIcon)
	ImageView mAuthenticationIcon;

	private int mUserId;
	private UserData mUserData;
	private AttentionAndFansNumberModel mAttentionAndFansNum;
	private WhetherAttentionShieldOrNot mWhetherAttentionShieldOrNot;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_data);
		ButterKnife.bind(this);
		mUserData = new UserData();
		mAttentionAndFansNum = new AttentionAndFansNumberModel();
		mWhetherAttentionShieldOrNot = new WhetherAttentionShieldOrNot();

		initData(getIntent());
		requestUserData();
	}

	private void initData(Intent intent) {
		mUserId = intent.getIntExtra(Launcher.USER_ID, -1);
	}

	private void requestUserData() {
		Client.getUserData(mUserId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<UserData>, UserData>() {
					@Override
					protected void onRespSuccessData(UserData userData) {
						mUserData = userData;
						initView();
					}
				}).fire();

		Client.getAttentionFollowUserNumber(mUserId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<AttentionAndFansNumberModel>, AttentionAndFansNumberModel>() {
					@Override
					protected void onRespSuccessData(AttentionAndFansNumberModel AttentionAndFansNumberModel) {
						mAttentionAndFansNum = AttentionAndFansNumberModel;
						initView();
					}
				}).fire();

		Client.whetherAttentionShieldOrNot(mUserId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<WhetherAttentionShieldOrNot>, WhetherAttentionShieldOrNot>() {
					@Override
					protected void onRespSuccessData(WhetherAttentionShieldOrNot whetherAttentionShieldOrNot) {
						mWhetherAttentionShieldOrNot = whetherAttentionShieldOrNot;
						initView();
					}
				}).fire();

	}

	private void initView() {
		if (mUserData != null) {
			Glide.with(this).load(mUserData.getUserPortrait())
					.placeholder(R.drawable.ic_default_avatar_big)
					.transform(new GlideCircleTransform(this))
					.dontAnimate()
					.into(mAvatar);

			mUserName.setText(mUserData.getUserName());

			if (mUserData.getUserSex() == 1) {
				mUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_female, 0);
			} else {
				mUserName.setCompoundDrawablesWithIntrinsicBounds(0, 0, R.drawable.ic_male, 0);
			}

			if (TextUtils.isEmpty(mUserData.getLand())) {
				mLocation.setText(R.string.no_location_information);
			} else {
				mLocation.setText(mUserData.getLand());
			}

			if (mUserData.getCertificationStatus() == 1) {
				mAuthenticationIcon.setImageResource(R.drawable.ic_authenticated);
			} else {
				mAuthenticationIcon.setImageResource(R.drawable.ic_unauthorized);
			}
		}

		if (mAttentionAndFansNum != null) {
			if (mAttentionAndFansNum.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
				mAttentionShieldArea.setVisibility(View.GONE);
				mHisPublish.setText(R.string.my_publish);
			}
			mAttentionNum.setText(getString(R.string.attention_number, mAttentionAndFansNum.getAttention()));
			mFansNum.setText(getString(R.string.fans_number, mAttentionAndFansNum.getFollower()));
		}

		if (mWhetherAttentionShieldOrNot != null) {
			if (mWhetherAttentionShieldOrNot.isFollow()) {
				mAttention.setText(R.string.is_attention);
			} else {
				mAttention.setText(R.string.plus_attention);
			}

			if (mWhetherAttentionShieldOrNot.isShield()) {
				mShield.setText(R.string.is_shield);
			} else {
				mShield.setText(R.string.shield_him);
			}
		}
	}

	@OnClick({R.id.attention, R.id.shield, R.id.avatar, R.id.hisPublishArea})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.attention:
				if (mUserData != null && mWhetherAttentionShieldOrNot != null) {
					if (mWhetherAttentionShieldOrNot.isFollow()) {
						cancelAttention();
					} else {
						doAttention();
					}
				}

				break;
			case R.id.shield:
				if (mUserData != null && mWhetherAttentionShieldOrNot != null) {
					if (mWhetherAttentionShieldOrNot.isShield()) {
						relieveShield();
					} else {
						doShield();
					}
				}
				break;

			case R.id.avatar:
				if (mUserData != null) {
					AvatarDialogFragment.newInstance(mUserData.getUserPortrait())
							.show(getSupportFragmentManager());
				}
				break;

			case R.id.hisPublishArea:
				if (mAttentionAndFansNum != null) {
					Launcher.with(this, PublishActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mAttentionAndFansNum.getUserId())
							.execute();
				}
				break;
		}
	}

	private void doShield() {
		SmartDialog.with(getActivity(),
				getString(R.string.shield_dialog_content, mUserData.getUserName())
				, getString(R.string.shield_dialog_title, mUserData.getUserName()))
				.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						Client.shieldOrRelieveShieldUser(mUserId, 0).setTag(TAG).setIndeterminate(UserDataActivity.this)
								.setCallback(new Callback<Resp<JsonPrimitive>>() {
									@Override
									protected void onRespSuccess(Resp<JsonPrimitive> resp) {
										if (resp.isSuccess()) {
											requestUserData();
											ToastUtil.curt("已屏蔽" + mUserData.getUserName());
										}
									}
								}).fire();
						dialog.dismiss();
					}
				})
				.setTitleMaxLines(2)
				.setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
				.setNegative(R.string.cancel)
				.show();
	}

	private void relieveShield() {
		SmartDialog.with(getActivity(),
				getString(R.string.relieve_shield_dialog_content, mUserData.getUserName())
				, getString(R.string.relieve_shield_dialog_title, mUserData.getUserName()))
				.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						Client.shieldOrRelieveShieldUser(mUserId, 1).setTag(TAG).setIndeterminate(UserDataActivity.this)
								.setCallback(new Callback<Resp<JsonPrimitive>>() {
									@Override
									protected void onRespSuccess(Resp<JsonPrimitive> resp) {
										if (resp.isSuccess()) {
											requestUserData();
											ToastUtil.curt("解除屏蔽" + mUserData.getUserName());
										}
									}
								}).fire();
						dialog.dismiss();
					}
				})
				.setTitleMaxLines(2)
				.setTitleTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setMessageTextColor(ContextCompat.getColor(this, R.color.opinionText))
				.setNegative(R.string.cancel)
				.show();
	}

	private void doAttention() {
		Client.attentionOrRelieveAttentionUser(mUserId, 0)
				.setTag(TAG)
				.setIndeterminate(this)
				.setCallback(new Callback<Resp<JsonPrimitive>>() {
					@Override
					protected void onRespSuccess(Resp<JsonPrimitive> resp) {
						if (resp.isSuccess()) {
							requestUserData();
							ToastUtil.curt("已关注" + mUserData.getUserName());
						}
					}
				}).fire();
	}

	private void cancelAttention() {
		SmartDialog.with(getActivity(), getString(R.string.cancel_attention_dialog_title, mUserData.getUserName()))
				.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						Client.attentionOrRelieveAttentionUser(mUserId, 1)
								.setTag(TAG)
								.setIndeterminate(UserDataActivity.this)
								.setCallback(new Callback<Resp<JsonPrimitive>>() {
									@Override
									protected void onRespSuccess(Resp<JsonPrimitive> resp) {
										if (resp.isSuccess()) {
											requestUserData();
											ToastUtil.curt("取消关注" + mUserData.getUserName());
										}
									}
								}).fire();
						dialog.dismiss();
					}
				})
				.setMessageTextSize(17)
				.setTitleMaxLines(2)
				.setMessageTextColor(ContextCompat.getColor(this, R.color.blackAssist))
				.setNegative(R.string.cancel)
				.show();
	}

	@Override
	public void onBackPressed() {
		Intent intent = new Intent();
		intent.putExtra(Launcher.EX_PAYLOAD_1, mWhetherAttentionShieldOrNot);
		intent.putExtra(Launcher.EX_PAYLOAD_2, mAttentionAndFansNum);
		setResult(RESULT_OK, intent);
		super.onBackPressed();
	}
}
