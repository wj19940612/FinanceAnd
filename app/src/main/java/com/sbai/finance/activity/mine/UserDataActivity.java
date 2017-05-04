package com.sbai.finance.activity.mine;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.AvatarFragment;
import com.sbai.finance.model.economiccircle.UserData;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
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
	@BindView(R.id.diagonal)
	TextView mdiagonal;
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
		mUserId = intent.getIntExtra("userId", 0);
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
			Glide.with(getActivity()).load(mUserData.getUserPortrait())
					.placeholder(R.drawable.ic_default_avatar_big)
					.transform(new GlideCircleTransform(this))
					.into(mAvatar);

			mUserName.setText(mUserData.getUserName());

			if (mUserData.getUserSex() == 1) {
				mUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_female, 0, 0, 0);
			} else {
				mUserName.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_male, 0, 0, 0);
			}

			mLocation.setText(mUserData.getLand());

			if (mUserData.getCertificationStatus() == 0) {
				mAuthentication.setText(R.string.unauthorized);
				mAuthentication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_failed, 0, 0, 0);
			} else {
				mAuthentication.setText(R.string.authenticated);
				mAuthentication.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_news_succeed, 0, 0, 0);
			}
		}

		if (mAttentionAndFansNum != null) {
			mdiagonal.setText(" / ");
			mAttentionNum.setText(getString(R.string.attention_number, String.valueOf(mAttentionAndFansNum.getAttention())));
			mFansNum.setText(getString(R.string.fans_number, String.valueOf(mAttentionAndFansNum.getFollower())));
		}

		if (mWhetherAttentionShieldOrNot != null) {
			if (mWhetherAttentionShieldOrNot.isFollow()) {
				mAttention.setText(R.string.is_attention);
				mAttention.setTextColor(ContextCompat.getColor(this, R.color.greenAssist));
			} else {
				mAttention.setText(R.string.attention);
				mAttention.setTextColor(ContextCompat.getColor(this, R.color.redPrimary));
			}

			if (mWhetherAttentionShieldOrNot.isShield()) {
				mShield.setText(R.string.is_shield);
			} else {
				mShield.setText(R.string.shield_him);
			}
		}
	}

	@OnClick({R.id.attention, R.id.shield, R.id.avatar})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.attention:
				if (mWhetherAttentionShieldOrNot != null) {
					if (mWhetherAttentionShieldOrNot.isFollow()) {
						//取消关注
						cancelAttention();
					} else {
						//进行关注
						doAttention();
					}
				}

				break;
			case R.id.shield:
				if (mWhetherAttentionShieldOrNot != null) {
					if (mWhetherAttentionShieldOrNot.isShield()) {
						//解除屏蔽
						relieveShield();
					} else {
						//进行屏蔽
						doShield();
					}
				}
				break;

			case R.id.avatar:
				if (mUserData != null) {
					AvatarFragment.newInstance(mUserData.getUserPortrait())
							.show(getSupportFragmentManager());
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
											mShield.setText(R.string.is_shield);
											ToastUtil.curt(R.string.shield + mUserData.getUserName());
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
											mShield.setText(R.string.shield_him);
											ToastUtil.curt(R.string.relieve_shield + mUserData.getUserName());
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
							mAttention.setText(R.string.is_attention);
							mAttention.setTextColor(ContextCompat.getColor(UserDataActivity.this, R.color.greenAssist));
							mWhetherAttentionShieldOrNot.setFollow(true);
							ToastUtil.curt("关注" + mUserData.getUserName());
						}
					}
				}).fire();
	}

	private void cancelAttention() {
		SmartDialog.with(getActivity(), "", getString(R.string.cancel_attention_dialog_title, mUserData.getUserName()))
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
											mAttention.setText(R.string.attention);
											mAttention.setTextColor(ContextCompat.getColor(UserDataActivity.this, R.color.redPrimary));
											mWhetherAttentionShieldOrNot.setFollow(false);
											ToastUtil.curt("取消关注" + mUserData.getUserName());
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
}
