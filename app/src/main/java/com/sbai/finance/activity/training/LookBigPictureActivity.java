package com.sbai.finance.activity.training;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.utils.audio.OnPlayRadioManager;
import com.sbai.finance.view.SmartDialog;
import com.sbai.finance.view.TitleBar;
import com.sbai.glide.GlideApp;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class LookBigPictureActivity extends BaseActivity implements View.OnClickListener,OnPlayRadioManager {

	@BindView(R.id.imageView)
	PhotoView mImageView;
	@BindView(R.id.titleBar)
	TitleBar mTitleBar;

	private String mPortrait;
	private int mDelete;
	private int mMissAvatar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_big_picture);
		ButterKnife.bind(this);
		initData(getIntent());
		initTitleBar();
		if (mMissAvatar == 0) {
			GlideApp.with(this).load(mPortrait)
					.thumbnail(0.1f)
					.error(R.drawable.ic_default_avatar_big)
					.dontAnimate()
					.into(mImageView);
		} else {
			GlideApp.with(this).load(mPortrait)
					.thumbnail(0.1f)
					.error(R.drawable.ic_default_image)
					.dontAnimate()
					.into(mImageView);
		}

		mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
			@Override
			public void onPhotoTap(View view, float x, float y) {
				finish();
			}

			@Override
			public void onOutsidePhotoTap() {

			}
		});
	}

	private void initData(Intent intent) {
		mPortrait = intent.getStringExtra(Launcher.EX_PAYLOAD);
		mDelete = intent.getIntExtra(Launcher.EX_PAYLOAD_1, -1);
		mMissAvatar = intent.getIntExtra(Launcher.EX_PAYLOAD_2, 0);
	}

	private void initTitleBar() {
		if (mMissAvatar == 0) {
			mTitleBar.setVisibility(View.GONE);
		} else {
			mTitleBar.setVisibility(View.VISIBLE);
			if (mDelete != -1) {
				mTitleBar.setRightVisible(true);
				mTitleBar.setRightTextLeftImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete_photo));
				mTitleBar.setOnRightViewClickListener(this);
			} else {
				mTitleBar.setRightVisible(false);
			}
		}
	}

	@Override
	public void onClick(View v) {
		showDeleteDialog();
	}

	private void showDeleteDialog() {
		SmartDialog.single(getActivity())
				.setTitle(getString(R.string.is_sure_delete_photo))
				.setNegative(R.string.cancel, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						dialog.dismiss();
					}
				})
				.setPositive(R.string.confirm, new SmartDialog.OnClickListener() {
					@Override
					public void onClick(Dialog dialog) {
						setResult(RESULT_OK);
						dialog.dismiss();
						finish();
					}
				}).show();
	}
}
