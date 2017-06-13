package com.sbai.finance.activity.economiccircle;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.BorrowMoneyDetails;
import com.sbai.finance.model.economiccircle.GoodHeartPeople;
import com.sbai.finance.model.economiccircle.WhetherAttentionShieldOrNot;
import com.sbai.finance.model.mine.AttentionAndFansNumberModel;
import com.sbai.finance.model.mutual.BorrowMessage;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.DateUtil;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.FinanceUtil;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.KeyBoardHelper;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.StrUtil;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.view.MyListView;
import com.sbai.finance.view.SmartDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
	@BindView(R.id.borrowDeadline)
	TextView mBorrowDeadline;
	@BindView(R.id.borrowInterest)
	TextView mBorrowInterest;
	@BindView(R.id.borrowMoneyContent)
	TextView mBorrowMoneyContent;
	@BindView(R.id.leaveMessageNum)
	TextView mLeaveMessageNum;
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
	@BindView(R.id.goodHeartPeopleArea)
	RelativeLayout mGoodHeartPeopleArea;
	@BindView(R.id.avatarList)
	LinearLayout mAvatarList;
	@BindView(R.id.more)
	ImageView mMore;
	@BindView(R.id.scrollView)
	ScrollView mScrollView;
	@BindView(android.R.id.list)
	MyListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.writeMessage)
	TextView mWriteMessage;
	@BindView(R.id.leaveMessage)
	EditText mLeaveMessage;
	@BindView(R.id.send)
	TextView mSend;
	@BindView(R.id.leaveMessageArea)
	LinearLayout mLeaveMessageArea;

	private int mMax;
	private int mDataId;
	private BorrowMoneyDetails mBorrowMoneyDetails;
	private List<GoodHeartPeople> mGoodHeartPeopleList;
	private List<BorrowMessage> mBorrowMessageList;
	private MessageAdapter mMessageAdapter;
	private KeyBoardHelper mKeyBoardHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_borrow_money_details);
		ButterKnife.bind(this);
		mGoodHeartPeopleList = new ArrayList<>();
		mBorrowMessageList = new ArrayList<>();

		initData(getIntent());
		initView();
		calculateAvatarNum(this);
		requestBorrowMoneyDetails();
		requestGoodHeartPeopleList();
		requestMessageList();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private void initView() {
		setKeyboardHelper();
		mMessageAdapter = new MessageAdapter(getActivity());
		mMessageAdapter.setCallback(new MessageAdapter.Callback() {
			@Override
			public void onUserClick(int userId) {
				Launcher.with(getActivity(), UserDataActivity.class)
						.putExtra(Launcher.USER_ID, userId)
						.executeForResult(REQ_CODE_USERDATA);
			}
		});
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mMessageAdapter);
	}

	private void setKeyboardHelper() {
		mKeyBoardHelper = new KeyBoardHelper(this);
		mKeyBoardHelper.onCreate();
		mKeyBoardHelper.setOnKeyBoardStatusChangeListener(onKeyBoardStatusChangeListener);
	}

	private KeyBoardHelper.OnKeyBoardStatusChangeListener onKeyBoardStatusChangeListener = new KeyBoardHelper.OnKeyBoardStatusChangeListener() {

		@Override
		public void OnKeyBoardPop(int keyboardHeight) {

		}

		@Override
		public void OnKeyBoardClose(int oldKeyboardHeight) {
			mLeaveMessageArea.setVisibility(View.GONE);
		}
	};

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

	private void requestGoodHeartPeopleList() {
		Client.getGoodHeartPeopleList(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<GoodHeartPeople>>, List<GoodHeartPeople>>() {
					@Override
					protected void onRespSuccessData(List<GoodHeartPeople> goodHeartPeopleList) {
						mGoodHeartPeopleList = goodHeartPeopleList;
						updateGoodHeartPeopleList(mGoodHeartPeopleList);
					}
				}).fire();
	}

	private void requestMessageList() {
		Client.getBorrowMessage(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<BorrowMessage>>, List<BorrowMessage>>() {
					@Override
					protected void onRespSuccessData(List<BorrowMessage> borrowMessageList) {
						mBorrowMessageList = borrowMessageList;
						updateMessageList(mBorrowMessageList);
					}
				}).fire();
	}

	private void requestSendMessage() {
		Client.sendBorrowMessage(mDataId, mLeaveMessage.getText().toString().trim()).setTag(TAG)
				.setCallback(new Callback<Resp<Object>>() {
					@Override
					protected void onRespSuccess(Resp<Object> resp) {
						if (resp.isSuccess()) {
							requestMessageList();
						} else {
							ToastUtil.show(resp.getMsg());
						}
					}
				}).fireSync();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mKeyBoardHelper.onDestroy();
	}

	private void updateMessageList(List<BorrowMessage> data) {
		mLeaveMessageNum.setText(getString(R.string.leave_message_number, data.size()));
		mMessageAdapter.clear();
		mMessageAdapter.addAll(data);
		mMessageAdapter.notifyDataSetChanged();
	}

	private void updateGoodHeartPeopleList(final List<GoodHeartPeople> goodHeartPeopleList) {
		int width = (int) Display.dp2Px(32, getResources());
		int height = (int) Display.dp2Px(32, getResources());
		int margin = (int) Display.dp2Px(10, getResources());

		mAvatarList.removeAllViews();

		int size = mGoodHeartPeopleList.size();
		if (size > 0) {
			mGoodHeartPeopleArea.setVisibility(View.VISIBLE);
		}
		if (size >= mMax) {
			size = mMax;
			for (int i = 0; i < size - 1; i++) {
				ImageView imageView = new ImageView(this);
				LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, height);
				params.leftMargin = (i == 0 ? 0 : margin);
				imageView.setLayoutParams(params);
				Glide.with(this).load(goodHeartPeopleList.get(i).getPortrait())
						.bitmapTransform(new GlideCircleTransform(this))
						.placeholder(R.drawable.ic_default_avatar)
						.into(imageView);
				mAvatarList.addView(imageView);

				mAvatarList.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(BorrowMoneyDetailsActivity.this, GoodHeartPeopleActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
								.putExtra(Launcher.USER_ID, mBorrowMoneyDetails.getUserId())
								.executeForResult(REQ_WANT_HELP_HIM_OR_YOU);
					}
				});

				mMore.setVisibility(View.VISIBLE);
				mMore.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(BorrowMoneyDetailsActivity.this, GoodHeartPeopleActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
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
				Glide.with(this).load(goodHeartPeopleList.get(i).getPortrait())
						.bitmapTransform(new GlideCircleTransform(this))
						.placeholder(R.drawable.ic_default_avatar)
						.into(imageView);
				mAvatarList.addView(imageView);

				mMore.setVisibility(View.GONE);
				mAvatarList.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Launcher.with(BorrowMoneyDetailsActivity.this, GoodHeartPeopleActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
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
		mNeedAmount.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(borrowMoneyDetails.getMoney())));
		mBorrowDeadline.setText(context.getString(R.string.day, FinanceUtil.formatWithScaleNoZero(borrowMoneyDetails.getDays())));
		mBorrowInterest.setText(context.getString(R.string.RMB, FinanceUtil.formatWithScaleNoZero(borrowMoneyDetails.getInterest())));

		mAvatar.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (LocalUser.getUser().isLogin()) {
					Launcher.with(context, UserDataActivity.class)
							.putExtra(Launcher.USER_ID, borrowMoneyDetails.getUserId())
							.executeForResult(REQ_CODE_USERDATA);
				} else {
					Launcher.with(context, LoginActivity.class).execute();
				}
			}
		});



		if (LocalUser.getUser().isLogin()) {
			if (borrowMoneyDetails.getUserId() == LocalUser.getUser().getUserInfo().getId()) {
				mGiveHelp.setVisibility(View.GONE);
			}
		}
		if (borrowMoneyDetails.getIsIntention() == 1) {
			mGiveHelp.setText(R.string.submitted);
			mGiveHelp.setEnabled(false);
		} else {
			mGiveHelp.setText(R.string.give_help);
			mGiveHelp.setEnabled(true);
		}

		mGiveHelp.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!LocalUser.getUser().isLogin()) {
					Launcher.with(BorrowMoneyDetailsActivity.this, LoginActivity.class).executeForResult(REQ_BORROW_MONEY_DETAILS);
				} else {
					SmartDialog.with(getActivity(),
							getString(R.string.give_help_dialog_content)
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
														requestBorrowMoneyDetails();
														requestGoodHeartPeopleList();
													}
												}

												@Override
												public void onFailure(VolleyError volleyError) {
													super.onFailure(volleyError);
													//处理多端问题
													requestBorrowMoneyDetails();
													requestGoodHeartPeopleList();
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

		if (!TextUtils.isEmpty(borrowMoneyDetails.getContentImg())) {
			String[] images = borrowMoneyDetails.getContentImg().split(",");
			switch (images.length) {
				case 1:
					mContentImg.setVisibility(View.VISIBLE);
					mImage1.setVisibility(View.VISIBLE);
					loadImage(context, images[0], mImage1);
					mImage2.setVisibility(View.INVISIBLE);
					mImage3.setVisibility(View.INVISIBLE);
					mImage4.setVisibility(View.INVISIBLE);
					imageClick(context, images, mImage1, 0);
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
		} else {
			mContentImg.setVisibility(View.GONE);
		}
	}

	private void calculateAvatarNum(Context context) {
		int screenWidth = context.getResources().getDisplayMetrics().widthPixels;
		int margin = (int) Display.dp2Px(68, getResources());
		int horizontalSpacing = (int) Display.dp2Px(10, getResources());
		int avatarWidth = (int) Display.dp2Px(32, getResources());
		mMax = (screenWidth - margin + horizontalSpacing) / (horizontalSpacing + avatarWidth);
	}

	private void loadImage(Context context, String src, ImageView image) {
		Glide.with(context)
				.load(src)
				.placeholder(R.drawable.img_loading)
				.error(R.drawable.logo_login)
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

	@OnClick({R.id.writeMessage, R.id.giveHelp, R.id.send})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.writeMessage:
				mLeaveMessageArea.setVisibility(View.VISIBLE);
				mLeaveMessage.setText("");
				mLeaveMessage.requestFocus();
				mLeaveMessage.setFocusable(true);
				InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
				inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
				break;
			case R.id.giveHelp:
				break;
			case R.id.send:
				if (!TextUtils.isEmpty(mLeaveMessage.getText())) {
					if (LocalUser.getUser().isLogin()) {
						requestSendMessage();
					} else {
						Launcher.with(getActivity(), LoginActivity.class).execute();
					}
				}
				break;
		}
	}

	static class MessageAdapter extends ArrayAdapter<BorrowMessage> {
		interface Callback {
			void onUserClick(int userId);
		}

		private Callback mCallback;

		public void setCallback(Callback callback) {
			mCallback = callback;
		}

		public MessageAdapter(@NonNull Context context) {
			super(context, 0);
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_message, parent, false);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindDataWithView(getItem(position), getContext(), mCallback);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.message)
			TextView mMessage;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindDataWithView(final BorrowMessage item, Context context, final Callback callback) {
				SpannableString attentionSpannableString = StrUtil.mergeTextWithRatioColor(item.getUserName(),
						": " + item.getContent(), 1.0f, ContextCompat.getColor(context, R.color.blackAssist));
				attentionSpannableString.setSpan(new ClickableSpan() {
					@Override
					public void onClick(View widget) {
						callback.onUserClick(item.getUserId());
					}

					@Override
					public void updateDrawState(TextPaint ds) {
						ds.setUnderlineText(false);
					}
				}, 0, item.getUserName().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				mMessage.setText(attentionSpannableString);
				mMessage.setMovementMethod(LinkMovementMethod.getInstance());
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

		if (requestCode == REQ_CODE_USERDATA && resultCode == RESULT_OK) {
			if (data != null) {
				WhetherAttentionShieldOrNot whetherAttentionShieldOrNot =
						(WhetherAttentionShieldOrNot) data.getSerializableExtra(Launcher.EX_PAYLOAD_1);

				AttentionAndFansNumberModel attentionAndFansNumberModel =
						(AttentionAndFansNumberModel) data.getSerializableExtra(Launcher.EX_PAYLOAD_2);

				if (whetherAttentionShieldOrNot != null) {
					if (whetherAttentionShieldOrNot.isFollow()) {
						mIsAttention.setText(R.string.is_attention);
					} else {
						mIsAttention.setText("");
					}
				}

				Intent intent = new Intent();
				intent.putExtra(Launcher.EX_PAYLOAD_1, whetherAttentionShieldOrNot);
				intent.putExtra(Launcher.EX_PAYLOAD_2, attentionAndFansNumberModel);
				setResult(RESULT_OK, intent);
			}
		}
	}
}
