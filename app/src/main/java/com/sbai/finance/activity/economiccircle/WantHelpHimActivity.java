package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mine.LoginActivity;
import com.sbai.finance.activity.mine.UserDataActivity;
import com.sbai.finance.model.LocalUser;
import com.sbai.finance.model.economiccircle.WantHelpHimOrYou;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.GlideCircleTransform;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;


public class WantHelpHimActivity extends BaseActivity {

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(android.R.id.empty)
	TextView mEmpty;
	private int mDataId;
	private List<WantHelpHimOrYou> mWantHelpHimOrYouList;
	private WantHelpHimOrYouAdapter mWantHelpHimOrYouAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_want_help_him);
		ButterKnife.bind(this);

		initData(getIntent());

		mWantHelpHimOrYouAdapter = new WantHelpHimOrYouAdapter(this);
		mListView.setEmptyView(mEmpty);
		mListView.setAdapter(mWantHelpHimOrYouAdapter);

		requestWantHelpHimList();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
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

		private WantHelpHimOrYouAdapter(Context context) {
			super(context, 0);
			mContext = context;
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
			viewHolder.bindingData(mContext, getItem(position));
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

			public void bindingData(final Context context, final WantHelpHimOrYou item) {
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
							Launcher.with(context, LoginActivity.class).execute();
						}
					}
				});

			}
		}
	}
}
