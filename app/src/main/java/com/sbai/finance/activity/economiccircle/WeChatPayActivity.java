package com.sbai.finance.activity.economiccircle;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.JsonPrimitive;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.activity.mutual.BorrowDetailsActivity;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.Display;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.QRCodeUtil;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.sbai.finance.R.id.completePayment;


public class WeChatPayActivity extends BaseActivity {

	@BindView(R.id.imageView)
	ImageView mImageView;
	@BindView(completePayment)
	TextView mCompletePayment;

	private String mPaymentPath;
	private int mDataId;
	private Bitmap mBitmap;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_we_chat_pay);
		ButterKnife.bind(this);

		initData(getIntent());

		new Thread(new Runnable() {
			@Override
			public void run() {
				mBitmap = QRCodeUtil.createQRCode(mPaymentPath, (int) Display.dp2Px(200, getResources()));

				runOnUiThread(new Runnable() {
					@Override
					public void run() {
						mImageView.setImageBitmap(mBitmap);
					}
				});
			}
		}).start();

		mImageView.setOnLongClickListener(new View.OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {

				final AlertDialog.Builder builder = new AlertDialog.Builder(WeChatPayActivity.this);
				View view = View.inflate(WeChatPayActivity.this, R.layout.dialog_save_picture, null);
				builder.setView(view);
				final AlertDialog dialog = builder.create();
				dialog.show();

				TextView savePicture = (TextView) view.findViewById(R.id.save_picture);
				savePicture.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mImageView.setDrawingCacheEnabled(true);
						Bitmap imageBitmap = mImageView.getDrawingCache();
						if (imageBitmap != null) {
							new SaveImageTask().execute(imageBitmap);
						}
						dialog.dismiss();
					}
				});


				return true;
			}
		});
	}

	private void initData(Intent intent) {
		mPaymentPath = intent.getStringExtra(Launcher.EX_PAYLOAD);
		mDataId = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
	}

	private class SaveImageTask extends AsyncTask<Bitmap, Void, String> {
		@Override
		protected String doInBackground(Bitmap... params) {
			String result = getResources().getString(R.string.save_picture_failed);

				String sdcard = Environment.getExternalStorageDirectory().toString();

				File file = new File(sdcard + "/Download");
				if (!file.exists()) {
					file.mkdirs();
				}
			File imageFile = new File(file.getAbsolutePath(),new Date().getTime()+".jpg");

			try {
				FileOutputStream outStream = new FileOutputStream(imageFile);
				Bitmap image = params[0];
				image.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
				outStream.flush();
				outStream.close();
				result = getResources().getString(R.string.save_picture_success,  file.getAbsolutePath());
			} catch (Exception e) {
				e.printStackTrace();
			}

			// 其次把文件插入到系统图库
			try {
				MediaStore.Images.Media.insertImage(getContentResolver(), imageFile.getAbsolutePath(), new Date().getTime()+".jpg", null);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}

			// 最后通知图库更新
			sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://" +  Environment.getExternalStorageDirectory())));

			return result;
		}

		@Override
		protected void onPostExecute(String result) {
			Toast.makeText(getApplicationContext(),result,Toast.LENGTH_SHORT).show();

			mImageView.setDrawingCacheEnabled(false);
		}
	}

	@OnClick(completePayment)
	public void onViewClicked() {
		Client.paymentQuery(mDataId).setTag(TAG).setIndeterminate(this)
				.setCallback(new Callback<Resp<JsonPrimitive>>() {
					@Override
					protected void onRespSuccess(Resp<JsonPrimitive> resp) {
						Launcher.with(WeChatPayActivity.this, BorrowDetailsActivity.class)
								.putExtra(Launcher.EX_PAYLOAD, mDataId)
								.putExtra(Launcher.EX_PAYLOAD_1,mDataId)
								.execute();
						finish();
					}
				}).fire();
	}
}
