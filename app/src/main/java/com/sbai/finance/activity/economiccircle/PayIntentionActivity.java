package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.web.PaymentActivity;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.model.payment.UsablePlatform;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class PayIntentionActivity extends BaseActivity {

	@BindView(R.id.intentionAmount)
	TextView mIntentionAmount;
	@BindView(android.R.id.list)
	ListView mListView;
	@BindView(R.id.payIntention)
	TextView mPayIntention;

	private double mAmount;
	private int mDataId;
	private int mUserId;
	private static String mPlatform;
	private String mPaymentPath;
	private List<UsablePlatform> mUsablePlatformList;
	private PayIntentionAdapter mPayIntentionAdapter;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pay_intention);
		ButterKnife.bind(this);

		initData(getIntent());

		mPayIntentionAdapter = new PayIntentionAdapter(this);
		mUsablePlatformList = new ArrayList<>();
		mListView.setAdapter(mPayIntentionAdapter);
		mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				mPayIntentionAdapter.setChecked(position);
				mPayIntentionAdapter.notifyDataSetChanged();
				Log.i(TAG, "onItemClick: " + position);
			}
		});

		requestIntentionAmount();
		requestUsablePlatformList();
	}

	private void initData(Intent intent) {
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
		mUserId = intent.getIntExtra(Launcher.USER_ID, -1);
	}

	private void requestIntentionAmount() {
		Client.getIntentionAmount().setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<Double>, Double>() {
					@Override
					protected void onRespSuccessData(Double amount) {
						mAmount = amount;
						mIntentionAmount.setText(getString(R.string.intention_amount, String.valueOf(mAmount)));
					}
				}).fire();
	}

	private void requestUsablePlatformList() {
		Client.getUsablePlatform().setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<List<UsablePlatform>>, List<UsablePlatform>>() {
					@Override
					protected void onRespSuccessData(List<UsablePlatform> usablePlatformList) {
						for (UsablePlatform usablePlatform : usablePlatformList) {
							if (usablePlatform.getStatus() == 1) {
								mUsablePlatformList.add(usablePlatform);
							}
						}
						updateUsablePlatformList();
					}
				}).fire();
	}

	private void updateUsablePlatformList() {
		mPayIntentionAdapter.clear();
		mPayIntentionAdapter.addAll(mUsablePlatformList);
		mPayIntentionAdapter.notifyDataSetChanged();
	}

	static class PayIntentionAdapter extends ArrayAdapter<UsablePlatform> {

		private Context mContext;
		private int mChecked = -1;

		private PayIntentionAdapter(Context context) {
			super(context, 0);
			this.mContext = context;
		}

		private void setChecked(int checked) {
			this.mChecked = checked;
		}

		@NonNull
		@Override
		public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
			ViewHolder viewHolder;
			if (convertView == null) {
				convertView = LayoutInflater.from(getContext()).inflate(R.layout.row_pay_intention, null);
				viewHolder = new ViewHolder(convertView);
				convertView.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) convertView.getTag();
			}
			viewHolder.bindingData(mContext, getItem(position), mChecked, position);
			return convertView;
		}

		static class ViewHolder {
			@BindView(R.id.checkbox)
			ImageView mCheckbox;
			@BindView(R.id.payName)
			TextView mPayName;

			ViewHolder(View view) {
				ButterKnife.bind(this, view);
			}

			private void bindingData(Context context, UsablePlatform item, int checked, int position) {
				if (item == null) return;
				mPayName.setText(item.getName());

				if (checked == position) {
					mCheckbox.setBackgroundResource(R.drawable.ic_checkbox_checked);
					mPlatform = item.getPlatform();
				} else {
					mCheckbox.setBackgroundResource(R.drawable.ic_checkbox);
				}
			}
		}
	}

	@OnClick(R.id.payIntention)
	public void onViewClicked() {
		Client.getPaymentPath(mDataId, mPlatform).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
					@Override
					protected void onRespSuccessData(PaymentPath paymentPath) {
						mPaymentPath = paymentPath.getCodeUrl();
						Launcher.with(getActivity(), PaymentActivity.class)
								.putExtra(PaymentActivity.EX_URL, paymentPath.getCodeUrl())
								.execute();
					}
				}).fire();
	}
}
