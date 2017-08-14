package com.sbai.finance.activity.training;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.dialog.UploadHelpImageDialogFragment;
import com.sbai.finance.net.Callback;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.ImageUtils;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.ValidationWatcher;
import com.sbai.finance.view.TitleBar;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class WriteExperienceActivity extends BaseActivity {

	private static final int REQ_LOOK_BIG_IMAGE = 1001;

	@BindView(R.id.titleBar)
	TitleBar mTitleBar;
	@BindView(R.id.experience)
	EditText mExperience;
	@BindView(R.id.wordsNumber)
	TextView mWordsNumber;
	@BindView(R.id.publish)
	TextView mPublish;
	@BindView(R.id.addPhoto)
	ImageView mAddPhoto;


	private boolean mIsAddPhoto = false;
	private String mPath;
	private int mType;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_write_experience);
		ButterKnife.bind(this);
		initData(getIntent());
		mExperience.addTextChangedListener(mValidationWatcher);
	}

	private void initData(Intent intent) {
		mType = intent.getIntExtra(Launcher.EX_PAYLOAD, -1);
	}

	private ValidationWatcher mValidationWatcher = new ValidationWatcher() {
		@Override
		public void afterTextChanged(Editable s) {
			if (TextUtils.isEmpty(mExperience.getText().toString().trim())) {
				mPublish.setEnabled(false);
				mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
			} else if (mExperience.getText().length() > 140) {
				mPublish.setEnabled(false);
				mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.redAssist));
			} else {
				mPublish.setEnabled(true);
				mWordsNumber.setTextColor(ContextCompat.getColor(getActivity(), R.color.unluckyText));
			}
			mWordsNumber.setText(getString(R.string.words_number, mExperience.getText().length()));
		}
	};

	@OnClick({R.id.addPhoto, R.id.publish})
	public void onViewClicked(View view) {
		switch (view.getId()) {
			case R.id.addPhoto:
				if (mIsAddPhoto) {
					Launcher.with(getActivity(), LookBigPictureActivity.class)
							.putExtra(Launcher.EX_PAYLOAD, mPath)
							.putExtra(Launcher.EX_PAYLOAD_1, 0)
							.executeForResult(REQ_LOOK_BIG_IMAGE);
				} else {
					addPhoto();
				}

				break;
			case R.id.publish:
				requestWriteExperience();
				break;
		}
	}

	private void addPhoto() {
		UploadHelpImageDialogFragment.newInstance().setOnDismissListener(new UploadHelpImageDialogFragment.OnDismissListener() {
			@Override
			public void onGetImagePath(String path) {
				if (!TextUtils.isEmpty(path)) {
					Glide.with(getActivity()).load(path)
							.placeholder(R.drawable.ic_default_image)
							.into(mAddPhoto);
					mPath = path;
					mIsAddPhoto = true;
				}
			}
		}).show(getSupportFragmentManager());
	}

	private void requestWriteExperience() {
		if (mPath != null) {
			String imageURL = ImageUtils.compressImageToBase64(mPath);
			Client.uploadPicture(imageURL).setTag(TAG).setIndeterminate(this)
					.setCallback(new Callback2D<Resp<List<String>>, List<String>>() {
						@Override
						protected void onRespSuccessData(List<String> data) {
							String picture = data.get(0);
							Client.writeExperience(4, mType, 2, mExperience.getText().toString().trim(), picture)
									.setTag(TAG)
									.setIndeterminate(WriteExperienceActivity.this)
									.setCallback(new Callback<Resp<Object>>() {
										@Override
										protected void onRespSuccess(Resp<Object> resp) {
											if (resp.isSuccess()) {
												setResult(RESULT_OK);
												ToastUtil.show(R.string.publish_success);
												finish();
											} else {
												ToastUtil.show(resp.getMsg());
											}
										}
									}).fireFree();
						}

						@Override
						public void onFailure(VolleyError volleyError) {
							super.onFailure(volleyError);
							mPublish.setEnabled(true);
						}
					}).fire();
		} else {
			Client.writeExperience(4, 2, mType, mExperience.getText().toString().trim(), null)
					.setTag(TAG)
					.setIndeterminate(WriteExperienceActivity.this)
					.setCallback(new Callback<Resp<Object>>() {
						@Override
						protected void onRespSuccess(Resp<Object> resp) {
							if (resp.isSuccess()) {
								setResult(RESULT_OK);
								ToastUtil.show(R.string.publish_success);
								finish();
							} else {
								ToastUtil.show(resp.getMsg());
							}
						}
					}).fire();
		}
	}


	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQ_LOOK_BIG_IMAGE && resultCode == RESULT_OK) {
			Glide.with(getActivity()).load(R.drawable.ic_add_pic)
					.into(mAddPhoto);
			mIsAddPhoto = false;
		}
	}
}
