package com.sbai.finance.activity.economiccircle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.ContentImgActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoneyDetails;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BorrowMoneyDetailsActivity extends BaseActivity {

	@BindView(R.id.avatar)
	ImageView mAvatar;
	@BindView(R.id.userName)
	TextView mUserName;
	@BindView(R.id.publishTime)
	TextView mPublishTime;
	@BindView(R.id.location)
	TextView mLocation;
	@BindView(R.id.needAmount)
	TextView mNeedAmount;
	@BindView(R.id.borrowTime)
	TextView mBorrowTime;
	@BindView(R.id.borrowInterest)
	TextView mBorrowInterest;
	@BindView(R.id.borrowMoneyContent)
	TextView mBorrowMoneyContent;
	@BindView(R.id.peopleNum)
	TextView mPeopleNum;
	@BindView(R.id.wantHelpHimArea)
	LinearLayout mWantHelpHimArea;
	@BindView(R.id.giveHelp)
	TextView mGiveHelp;
	@BindView(R.id.isAttention)
	TextView mIsAttention;
	@BindView(R.id.image1)
	ImageView mImage1;
	@BindView(R.id.image2)
	ImageView mImage2;
	@BindView(R.id.image3)
	ImageView mImage3;
	@BindView(R.id.image4)
	ImageView mImage4;

	private int mDataId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_money_details);
		ButterKnife.bind(this);

		initData(getIntent());

		requestBorrowMoneyDetails();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void requestBorrowMoneyDetails() {
		Client.getBorrowMoneyDetail(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<BorrowMoneyDetails>, BorrowMoneyDetails>() {
					@Override
					protected void onRespSuccessData(BorrowMoneyDetails borrowMoneyDetails) {
						updateBorrowDetails(BorrowMoneyDetailsActivity.this, borrowMoneyDetails);
					}
				}).fire();
	}

	private void updateBorrowDetails(final Context context, final BorrowMoneyDetails borrowMoneyDetails) {
		if (borrowMoneyDetails == null) return;

		Glide.with(context).load(borrowMoneyDetails.getPortrait())
				.placeholder(R.drawable.ic_default_avatar)
				.transform(new GlideCircleTransform(context))
				.into(mAvatar);

		mUserName.setText(borrowMoneyDetails.getUserName());
		mPublishTime.setText(DateUtil.getFormatTime(borrowMoneyDetails.getCreateDate()));

		if (TextUtils.isEmpty(borrowMoneyDetails.getLocation())) {
			mLocation.setText(R.string.no_location_information);
		} else {
			mLocation.setText(borrowMoneyDetails.getLocation());
		}

		if (borrowMoneyDetails.getIsAttention() == 2) {
			mIsAttention.setText(R.string.is_attention);
		} else {
			mIsAttention.setText("");
		}

		mBorrowMoneyContent.setText(borrowMoneyDetails.getContent());
		mNeedAmount.setText(this.getString(R.string.RMB, String.valueOf(borrowMoneyDetails.getMoney())));
		mBorrowTime.setText(this.getString(R.string.day, String.valueOf(borrowMoneyDetails.getDays())));
		mBorrowInterest.setText(this.getString(R.string.RMB, String.valueOf(borrowMoneyDetails.getInterest())));

		mAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(context, UserDataActivity.class)
							.putExtra(Launcher.USER_ID, borrowMoneyDetails.getUserId())
							.execute();
				} else {
					Launcher.with(context, LoginActivity.class).execute();
				}
			}
		});

		if (borrowMoneyDetails.getIsIntention() == 1) {
			mGiveHelp.setBackgroundResource(R.drawable.bg_to_confirm);
			mGiveHelp.setText(R.string.wait_to_confirm);
			mGiveHelp.setEnabled(false);
		} else {
			mGiveHelp.setBackgroundResource(R.drawable.bg_give_help);
			mGiveHelp.setText(R.string.give_help);
		}

		mWantHelpHimArea.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Launcher.with(BorrowMoneyDetailsActivity.this, WantHelpHimActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mDataId)
						.execute();
			}
		});

		mGiveHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!LocalUser.getUser().isLogin()) {
					Launcher.with(BorrowMoneyDetailsActivity.this, LoginActivity.class).execute();
				} else {
					SmartDialog.with(getActivity(),
							getString(R.string.give_help_dialog_content, borrowMoneyDetails.getUserName())
							, getString(R.string.give_help_dialog_title, borrowMoneyDetails.getUserName()))
							.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
								@Override
								public void onClick(Dialog dialog) {
									Client.giveHelp(mDataId).setTag(TAG).
											setIndeterminate(BorrowMoneyDetailsActivity.this)
											.setCallback(new Callback<Resp<JsonPrimitive>>() {
												@Override
												protected void onRespSuccess(Resp<JsonPrimitive> resp) {
													if (resp.isSuccess()) {
														borrowMoneyDetails.setIsIntention(1);
														mGiveHelp.setBackgroundResource(R.drawable.bg_to_confirm);
														mGiveHelp.setText(R.string.wait_to_confirm);
														mGiveHelp.setEnabled(false);
													}
												}
											}).fire();
									dialog.dismiss();
								}
							})
							.setTitleMaxLines(2)
							.setTitleTextColor(ContextCompat.getColor(BorrowMoneyDetailsActivity.this, R.color.blackAssist))
							.setMessageTextColor(ContextCompat.getColor(BorrowMoneyDetailsActivity.this, R.color.opinionText))
							.setNegative(R.string.cancel)
							.show();
				}
			}
		});

		String[] images = borrowMoneyDetails.getContentImg().split(",");
		switch (images.length) {
			case 1:
				if (TextUtils.isEmpty(images[0])) {
					mImage1.setVisibility(View.GONE);
					mImage2.setVisibility(View.GONE);
					mImage3.setVisibility(View.GONE);
					mImage4.setVisibility(View.GONE);
				} else {
					mImage1.setVisibility(View.VISIBLE);
					loadImage(context, images[0], mImage1);
					mImage2.setVisibility(View.INVISIBLE);
					mImage3.setVisibility(View.INVISIBLE);
					mImage4.setVisibility(View.INVISIBLE);
				}
				break;
			case 2:
				mImage1.setVisibility(View.VISIBLE);
				loadImage(context, images[0], mImage1);
				mImage2.setVisibility(View.VISIBLE);
				loadImage(context, images[1], mImage2);
				mImage3.setVisibility(View.GONE);
				mImage4.setVisibility(View.GONE);
				break;
			case 3:
				mImage1.setVisibility(View.VISIBLE);
				loadImage(context, images[0], mImage1);
				mImage2.setVisibility(View.VISIBLE);
				loadImage(context, images[1], mImage2);
				mImage3.setVisibility(View.VISIBLE);
				loadImage(context, images[2], mImage3);
				mImage4.setVisibility(View.GONE);
				break;
			case 4:
				mImage1.setVisibility(View.VISIBLE);
				loadImage(context, images[0], mImage1);
				mImage2.setVisibility(View.VISIBLE);
				loadImage(context, images[1], mImage2);
				mImage3.setVisibility(View.VISIBLE);
				loadImage(context, images[2], mImage3);
				mImage4.setVisibility(View.VISIBLE);
				loadImage(context, images[3], mImage4);
				break;
			default:
				break;

		}
	}

	private void loadImage(Context context, String src, ImageView image) {
		Glide.with(context).load(src).placeholder(R.drawable.help).into(image);
	}

	private void imageClick(final Context context, final String[] images,
	                        ImageView imageView, final int i) {
		imageView.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(context, ContentImgActivity.class);
				intent.putExtra(Launcher.EX_PAYLOAD, images);
				intent.putExtra(Launcher.EX_PAYLOAD_1, i);
				context.startActivity(intent);
			}
		});
	}
}
