package com.sbai.finance.activity.economiccircle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
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
	@BindView(R.id.gridView)
	GridView mGridView;
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
	@BindView(R.id.more)
	ImageView mMore;

	private int mMax;
	private int mDataId;
	private List<WantHelpHimOrYou> mWantHelpHimOrYouList;
	private WantHelpHimOrYouAdapter mWantHelpHimOrYouAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_money_details);
		ButterKnife.bind(this);
		calculateAvatarNum(this);

		initData(getIntent());
		mWantHelpHimOrYouList = new ArrayList<>();
		mWantHelpHimOrYouAdapter = new WantHelpHimOrYouAdapter(this, mWantHelpHimOrYouList, mMax);
		mGridView.setAdapter(mWantHelpHimOrYouAdapter);

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
						updateBorrowDetails(BorrowMoneyDetailsActivity.this, borrowMoneyDetails);
					}
				}).fire();
	}

	private void requestWantHelpHimList() {
		Client.getWantHelpHimOrYouList(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<WantHelpHimOrYou>>, List<WantHelpHimOrYou>>() {
					@Override
					protected void onRespSuccessData(List<WantHelpHimOrYou> wantHelpHimOrYouList) {
						mWantHelpHimOrYouList = wantHelpHimOrYouList;
						updateWantHelpHimList();
					}
				}).fire();
	}

	private void updateWantHelpHimList() {
		mWantHelpHimOrYouAdapter.clear();
		mWantHelpHimOrYouAdapter.addAll(mWantHelpHimOrYouList);
		mWantHelpHimOrYouAdapter.notifyDataSetChanged();
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
		mBorrowInterest.setText(context.getString(R.string.RMB, String.valueOf(borrowMoneyDetails.getInterest())));
		mPeopleNum.setText(context.getString(R.string.people_want_help_him_number, String.valueOf(borrowMoneyDetails.getIntentionCount())));

		if (borrowMoneyDetails.getSex() == 2) {
			mPeopleWantHelpHimOrHer.setText(R.string.people_want_help_him);
		} else {
			mPeopleWantHelpHimOrHer.setText(R.string.people_want_help_her);
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
			mGiveHelp.setBackgroundResource(R.drawable.bg_give_help);
			mGiveHelp.setText(R.string.give_help);
		}

		mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Launcher.with(BorrowMoneyDetailsActivity.this, WantHelpHimOrYouActivity.class)
						.putExtra(Launcher.EX_PAYLOAD, mDataId)
						.putExtra(Launcher.USER_ID, borrowMoneyDetails.getUserId())
						.executeForResult(REQ_WANT_HELP_HIM_OR_YOU);
			}
		});

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
					imageClick(context, images, mImage1, 0);
				}
				break;
			case 2:
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

	private void calculateAvatarNum(Context context) {
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		int margin = dp2Px(26);
		int horizontalSpacing = dp2Px(5);
		int avatarWidth = dp2Px(32);
		mMax = (screenWidth - margin + horizontalSpacing) / (horizontalSpacing + avatarWidth);
	}

	public int dp2Px(int dp) {
		float density = this.getResources().getDisplayMetrics().density;
		return (int) (dp * density + .5f);
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

	static class WantHelpHimOrYouAdapter extends ArrayAdapter<WantHelpHimOrYou> {

		private Context mContext;
		private List<WantHelpHimOrYou> mWantHelpHimOrYouList;
		private int mMax;

		private WantHelpHimOrYouAdapter(Context context, List<WantHelpHimOrYou> wantHelpHimOrYouList, int max) {
			super(context, 0);
			this.mContext = context;
			this.mWantHelpHimOrYouList  = wantHelpHimOrYouList;
			this.mMax = max;
		}

		/*@Override
		public int getCount() {
			if (mWantHelpHimOrYouList.size() >= mMax) {
				return mMax;
			}
			return mWantHelpHimOrYouList.size();
		}*/

		@NonNull
		@Override
		public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_helper_image, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}

			viewHolder.bindData(getItem(position), mContext);

			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.userImg)
			ImageView mUserImg;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindData(WantHelpHimOrYou item, Context context) {
				Glide.with(context).load(item.getPortrait())
						.bitmapTransform(new GlideCircleTransform(context))
						.placeholder(R.drawable.ic_default_avatar)
						.into(mUserImg);
			}
		}
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
