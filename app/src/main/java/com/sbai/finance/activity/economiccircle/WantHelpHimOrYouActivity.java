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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.WantHelpHimOrYou;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WantHelpHimOrYouActivity extends BaseActivity {

	private static final int REQ_WANT_HELP_HIM_OR_YOU = 1001;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	@BindView(R.id.payIntention)
	TextView mPayIntention;

	private int mDataId;
	private int mUserId;
	private int mSex;
	private List<WantHelpHimOrYou> mWantHelpHimOrYouList;
	private WantHelpHimOrYouAdapter mWantHelpHimOrYouAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_want_help_him_or_you);
		ButterKnife.bind(this);

		initData(getIntent());

		if (LocalUser.getUser().isLogin()) {
			if (mUserId == LocalUser.getUser().getUserInfo().getId()) {
				mTitleBar.setTitle(R.string.people_want_help_you);
				mPayIntention.setVisibility(View.VISIBLE);
				mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
						final WantHelpHimOrYou wantHelpHimOrYou = (WantHelpHimOrYou) parent.getItemAtPosition(position);
						mPayIntention.setEnabled(true);
						mWantHelpHimOrYouAdapter.setChecked(position);
						mWantHelpHimOrYouAdapter.notifyDataSetInvalidated();

						mPayIntention.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								SmartDialog.with(getActivity(),
										getString(R.string.select_help, wantHelpHimOrYou.getUserName()))
										.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
											@Override
											public void onClick(final Dialog dialog) {
												Client.chooseGoodPeople(mDataId,  mWantHelpHimOrYouList.get(position).getUserId())
														.setTag(TAG)
														.setIndeterminate(WantHelpHimOrYouActivity.this)
														.setCallback(new Callback<Resp<JsonPrimitive>>() {
															@Override
															protected void onRespSuccess(Resp<JsonPrimitive> resp) {
																Launcher.with(getActivity(),PayIntentionActivity.class)
																		.putExtra(Launcher.EX_PAYLOAD, mDataId)
																		.execute();
																dialog.dismiss();
															}
														}).fire();
											}
										})
										.setMessageTextSize(16)
										.setMessageTextColor(ContextCompat.getColor(WantHelpHimOrYouActivity.this, R.color.blackAssist))
										.setNegative(R.string.cancel)
										.show();

							}
						});
					}
				});
			} else {
				if (mSex == 1) {
					mTitleBar.setTitle(R.string.people_want_help_her);
				} else {
					mTitleBar.setTitle(R.string.people_want_help_him);
				}

				mPayIntention.setVisibility(View.GONE);
			}
		}

		mWantHelpHimOrYouAdapter = new WantHelpHimOrYouAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mWantHelpHimOrYouAdapter);

		requestWantHelpHimList();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
		mUserId = intent.getIntExtra(Launcher.USER_ID, -1);
		mSex = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
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

	static class WantHelpHimOrYouAdapter extends ArrayAdapter<WantHelpHimOrYou> {

		private Context mContext;
		private int mChecked = -1;

		private WantHelpHimOrYouAdapter(Context context) {
			super(context, 0);
			mContext = context;
		}

		private void setChecked(int checked) {
			this.mChecked = checked;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_want_help_him_you, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, getItem(position), mChecked, position);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.avatar)
			ImageView mAvatar;
			@BindView(R.id.userName)
			TextView mUserName;
			@BindView(R.id.location)
			TextView mLocation;
			@BindView(R.id.checkboxClick)
			ImageView mCheckboxClick;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(final Context context, final WantHelpHimOrYou item, int checked, int position) {
				if (item == null) return;
				Glide.with(context).load(item.getPortrait())
						.placeholder(R.drawable.ic_default_avatar)
						.transform(new GlideCircleTransform(context))
						.into(mAvatar);

				mUserName.setText(item.getUserName());

				if (TextUtils.isEmpty(item.getLocation())) {
					mLocation.setText(R.string.no_location_information);
				} else {
					mLocation.setText(item.getLocation());
				}

				mAvatar.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (LocalUser.getUser().isLogin()) {
							Launcher.with(context, UserDataActivity.class)
									.putExtra(Launcher.USER_ID, item.getUserId())
									.execute();
						} else {
							Launcher.with(context, LoginActivity.class).executeForResult(REQ_WANT_HELP_HIM_OR_YOU);
						}
					}
				});

				if (checked == position) {
					mCheckboxClick.setVisibility(View.VISIBLE);
				} else {
					mCheckboxClick.setVisibility(View.GONE);
				}

			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_WANT_HELP_HIM_OR_YOU && resultCode == RESULT_OK) {
			setResult(RESULT_OK);
			if (mUserId == LocalUser.getUser().getUserInfo().getId()) {
				mTitleBar.setTitle(R.string.people_want_help_you);
				mPayIntention.setVisibility(View.VISIBLE);
				mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
						final WantHelpHimOrYou wantHelpHimOrYou = (WantHelpHimOrYou) parent.getItemAtPosition(position);
						mPayIntention.setEnabled(true);
						mWantHelpHimOrYouAdapter.setChecked(position);
						mWantHelpHimOrYouAdapter.notifyDataSetInvalidated();

						mPayIntention.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								SmartDialog.with(getActivity(),
										getString(R.string.select_help, wantHelpHimOrYou.getUserName()))
										.setPositive(R.string.ok, new SmartDialog.OnClickListener() {
											@Override
											public void onClick(final Dialog dialog) {
												Client.chooseGoodPeople(mDataId,  mWantHelpHimOrYouList.get(position).getUserId())
														.setTag(TAG)
														.setIndeterminate(WantHelpHimOrYouActivity.this)
														.setCallback(new Callback<Resp<JsonPrimitive>>() {
															@Override
															protected void onRespSuccess(Resp<JsonPrimitive> resp) {
																Launcher.with(getActivity(),PayIntentionActivity.class)
																		.putExtra(Launcher.EX_PAYLOAD, mDataId)
																		.execute();
																dialog.dismiss();
															}
														}).fire();
											}
										})
										.setMessageTextSize(16)
										.setMessageTextColor(ContextCompat.getColor(WantHelpHimOrYouActivity.this, R.color.blackAssist))
										.setNegative(R.string.cancel)
										.show();

							}
						});
					}
				});
			} else {
				if (mSex == 1) {
					mTitleBar.setTitle(R.string.people_want_help_her);
				} else {
					mTitleBar.setTitle(R.string.people_want_help_him);
				}
				mPayIntention.setVisibility(View.GONE);
			}
		}
	}
}
