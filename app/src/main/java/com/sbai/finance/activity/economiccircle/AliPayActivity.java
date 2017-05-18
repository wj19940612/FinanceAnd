package com.sbai.finance.activity.economiccircle;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mutual.BorrowInHisActivity;
import com.sbai.finance.model.payment.PaymentPath;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AliPayActivity extends BaseActivity {

	@BindView(R.id.imageView)
	ImageView mImageView;
	@BindView(R.id.ContinueToPay)
	TextView mContinueToPay;
	@BindView(R.id.completePayment)
	TextView mCompletePayment;

	private String mPlatform;
	private int mDataId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_ali_pay);
		ButterKnife.bind(this);

		initData(getIntent());
	}

	private void initData(Intent intent) {
		mPlatform = intent.getStringExtra(Launcher.EX_PAYLOAD);
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
	}

	@OnClick({R.id.ContinueToPay, R.id.completePayment})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.ContinueToPay:
				Client.getPaymentPath(mDataId, mPlatform).setTag(TAG).setIndeterminate(this)
						.setCallback(new Callback2D<Resp<PaymentPath>, PaymentPath>() {
							@Override
							protected void onRespSuccessData(PaymentPath paymentPath) {
									Intent intent = new Intent();
									intent.setAction("android.intent.action.VIEW");
									Uri content_url = Uri.parse(paymentPath.getCodeUrl());
									intent.setData(content_url);
									intent.setClassName("com.android.browser","com.android.browser.BrowserActivity");
									startActivity(intent);
							}
						}).fire();
				break;
			case R.id.completePayment:
				Client.paymentQuery(mDataId).setTag(TAG).setIndeterminate(this)
						.setCallback(new Callback<Resp<JsonPrimitive>>() {
							@Override
							protected void onRespSuccess(Resp<JsonPrimitive> resp) {
								Launcher.with(AliPayActivity.this, BorrowInHisActivity.class).execute();
								finish();
							}
						}).fire();
				break;
		}
	}
}
