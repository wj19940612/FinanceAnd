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
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoneyDetails;
import com.sbai.finance.model.economiccircle.WantHelpHimOrYou;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BorrowMoneyDetailsActivity extends BaseActivity {

	private static final int REQ_BORROW_MONEY_DETAILS = 1001;
	private static final int REQ_WANT_HELP_HIM_OR_YOU = 1002;

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
	@BindView(R.id.peopleWantHelpHimOrHer)
	TextView mPeopleWantHelpHimOrHer;
	@BindView(R.id.peopleNum)
	TextView mPeopleNum;
	@BindView(R.id.giveHelp)
	TextView mGiveHelp;
	@BindView(R.id.isAttention)
	TextView mIsAttention;
	@BindView(R.id.contentImg)
	LinearLayout mContentImg;
	@BindView(R.id.image1)
	ImageView mImage1;
	@BindView(R.id.image2)
	ImageView mImage2;
	@BindView(R.id.image3)
	ImageView mImage3;
	@BindView(R.id.image4)
	ImageView mImage4;
	@BindView(R.id.avatarList)
	LinearLayout mAvatarList;
	@BindView(R.id.more)
	ImageView mMore;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;

	private int mMax;
	private int mDataId;
	private BorrowMoneyDetails mBorrowMoneyDetails;
	private List<WantHelpHimOrYou> mWantHelpHimOrYouList;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_money_details);
		ButterKnife.bind(this);
		calculateAvatarNum(this);

		initData(getIntent());
		mWantHelpHimOrYouList = new ArrayList<>();

		requestBorrowMoneyDetails();
		requestWantHelpHimList();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void requestBorrowMoneyDetails() {
		Client.getBorrowMoneyDetail(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<BorrowMoneyDetails>, BorrowMoneyDetails>() {
					@Override
					protected void onRespSuccessData(BorrowMoneyDetails borrowMoneyDetails) {
						mBorrowMoneyDetails = borrowMoneyDetails;
						updateBorrowDetails(BorrowMoneyDetailsActivity.this, mBorrowMoneyDetails);
					}
				}).fire();
	}

	private void requestWantHelpHimList() {
		Client.getWantHelpHimOrYouList(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<WantHelpHimOrYou>>, List<WantHelpHimOrYou>>() {
					@Override
					protected void onRespSuccessData(List<WantHelpHimOrYou> wantHelpHimOrYouList) {
						mWantHelpHimOrYouList = wantHelpHimOrYouList;
						updateWantHelpHimList(mWantHelpHimOrYouList);
					}
				}).fire();
	}

	private void updateWantHelpHimList(final List<WantHelpHimOrYou> wantHelpHimOrYouList) {
		int width = (int) Display.dp2Px(32, getResources());
		int height = (int) Display.dp2Px(32, getResources());
		int margin = (int) Display.dp2Px(5, getResources());

		mAvatarList.removeAllViews();

		int size = mWantHelpHimOrYouList.size();
		if (size >= mMax) {
			size = mMax;
			for (int i = 0; i < size - 1; i++) {
				ImageView imageView = new ImageView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
				params.leftMargin = (i == 0 ? 0 : margin);
				imageView.setLayoutParams(params);
				Glide.with(this).load(wantHelpHimOrYouList.get(i).getPortrait())
						.bitmapTransform(new GlideCircleTransform(this))
						.placeholder(R.drawable.ic_default_avatar)
						.into(imageView);
				mAvatarList.addView(imageView);

				mAvatarList.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(BorrowMoneyDetailsActivity.this, WantHelpHimOrYouActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
								.putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
								.putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
								.executeForResult(REQ_WANT_HELP_HIM_OR_YOU);
					}
				});

				mMore.setVisibility(View.VISIBLE);
				mMore.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(BorrowMoneyDetailsActivity.this, WantHelpHimOrYouActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
								.putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
								.putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
								.executeForResult(REQ_WANT_HELP_HIM_OR_YOU);
					}
				});
			}
		} else {

			for (int i = 0; i < size; i++) {
				ImageView imageView = new ImageView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
				params.leftMargin = (i == 0 ? 0 : margin);
				imageView.setLayoutParams(params);
				Glide.with(this).load(wantHelpHimOrYouList.get(i).getPortrait())
						.bitmapTransform(new GlideCircleTransform(this))
						.placeholder(R.drawable.ic_default_avatar)
						.into(imageView);
				mAvatarList.addView(imageView);

				mMore.setVisibility(View.GONE);
				mAvatarList.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(BorrowMoneyDetailsActivity.this, WantHelpHimOrYouActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
								.putExtra(Launcher.EX_PAYLOAD_1, mBorrowMoneyDetails.getSex())
								.putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
								.executeForResult(REQ_WANT_HELP_HIM_OR_YOU);
					}
				});
			}
		}
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
		mNeedAmount.setText(context.getString(R.string.RMB, String.valueOf(FinanceUtil.formatWithScaleNoZero(borrowMoneyDetails.getMoney()))));
		mBorrowTime.setText(context.getString(R.string.day, String.valueOf(FinanceUtil.formatWithScaleNoZero(borrowMoneyDetails.getDays()))));
		mBorrowInterest.setText(context.getString(R.string.RMB, String.valueOf(FinanceUtil.formatWithScaleNoZero(borrowMoneyDetails.getInterest()))));
		mPeopleNum.setText(context.getString(R.string.people_want_help_him_number, String.valueOf(borrowMoneyDetails.getIntentionCount())));

		if (borrowMoneyDetails.getSex() == 1) {
			mPeopleWantHelpHimOrHer.setText(R.string.people_want_help_her);
		} else {
			mPeopleWantHelpHimOrHer.setText(R.string.people_want_help_him);
		}

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
			mGiveHelp.setBackgroundResource(R.drawable.btn_give_help);
			mGiveHelp.setText(R.string.give_help);
		}


		if (LocalUser.getUser().isLogin()) {
			if (borrowMoneyDetails.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
				mGiveHelp.setVisibility(View.GONE);
				mPeopleWantHelpHimOrHer.setText(R.string.people_want_help_you);
			}
		}

		mGiveHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!LocalUser.getUser().isLogin()) {
					Launcher.with(BorrowMoneyDetailsActivity.this, LoginActivity.class).executeForResult(REQ_BORROW_MONEY_DETAILS);
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
														String peopleNum = mPeopleNum.getText().toString().trim();
														mBorrowMoneyDetails.setIntentionCount(Integer.parseInt(peopleNum.substring(1, peopleNum.length() - 1)) + 1);
														mPeopleNum.setText(context.getString(R.string.people_want_help_him_number, String.valueOf(borrowMoneyDetails.getIntentionCount())));
														requestWantHelpHimList();
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

		if (borrowMoneyDetails.getContentImg() != null) {
			String[] images = borrowMoneyDetails.getContentImg().split(",");
			switch (images.length) {
				case 1:
					if (TextUtils.isEmpty(images[0])) {
						mImage1.setVisibility(View.GONE);
						mImage2.setVisibility(View.GONE);
						mImage3.setVisibility(View.GONE);
						mImage4.setVisibility(View.GONE);
					} else {
						mContentImg.setVisibility(View.VISIBLE);
						mImage1.setVisibility(View.VISIBLE);
						loadImage(context, images[0], mImage1);
						mImage2.setVisibility(View.INVISIBLE);
						mImage3.setVisibility(View.INVISIBLE);
						mImage4.setVisibility(View.INVISIBLE);
						imageClick(context, images, mImage1, 0);
					}
					break;
				case 2:
					mContentImg.setVisibility(View.VISIBLE);
					mImage1.setVisibility(View.VISIBLE);
					loadImage(context, images[0], mImage1);
					mImage2.setVisibility(View.VISIBLE);
					loadImage(context, images[1], mImage2);
					mImage3.setVisibility(View.INVISIBLE);
					mImage4.setVisibility(View.INVISIBLE);
					imageClick(context, images, mImage1, 0);
					imageClick(context, images, mImage2, 1);
					break;
				case 3:
					mContentImg.setVisibility(View.VISIBLE);
					mImage1.setVisibility(View.VISIBLE);
					loadImage(context, images[0], mImage1);
					mImage2.setVisibility(View.VISIBLE);
					loadImage(context, images[1], mImage2);
					mImage3.setVisibility(View.VISIBLE);
					loadImage(context, images[2], mImage3);
					mImage4.setVisibility(View.INVISIBLE);
					imageClick(context, images, mImage1, 0);
					imageClick(context, images, mImage2, 1);
					imageClick(context, images, mImage3, 2);
					break;
				case 4:
					mContentImg.setVisibility(View.VISIBLE);
					mImage1.setVisibility(View.VISIBLE);
					loadImage(context, images[0], mImage1);
					mImage2.setVisibility(View.VISIBLE);
					loadImage(context, images[1], mImage2);
					mImage3.setVisibility(View.VISIBLE);
					loadImage(context, images[2], mImage3);
					mImage4.setVisibility(View.VISIBLE);
					loadImage(context, images[3], mImage4);
					imageClick(context, images, mImage1, 0);
					imageClick(context, images, mImage2, 1);
					imageClick(context, images, mImage3, 2);
					imageClick(context, images, mImage4, 3);
					break;
				default:
					break;
			}
		}
	}

	private void calculateAvatarNum(Context context) {
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		int margin = (int) Display.dp2Px(26, getResources());
		int horizontalSpacing = (int) Display.dp2Px(5, getResources());
		int avatarWidth = (int) Display.dp2Px(32, getResources());
		mMax = (screenWidth - margin + horizontalSpacing) / (horizontalSpacing + avatarWidth);
	}

	private void loadImage(Context context, String src, ImageView image) {
		Glide.with(context).load(src)
				.thumbnail(0.1f)
				.into(image);
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_BORROW_MONEY_DETAILS && resultCode == RESULT_OK) {
			requestBorrowMoneyDetails();
		}

		if (requestCode == REQ_WANT_HELP_HIM_OR_YOU && resultCode == RESULT_OK) {
			requestBorrowMoneyDetails();
		}
	}
}
