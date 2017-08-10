package com.sbai.finance.activity.trainexperience;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.utils.Launcher;
import com.sbai.finance.view.TitleBar;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;


public class LookBigPictureActivity extends BaseActivity implements View.OnClickListener {

	@BindView(R.id.missAvatar)
	PhotoView mMissAvatar;
	@BindView(R.id.titleBar)
	TitleBar mTitleBar;

	private String mPortrait;
	private int mDelete;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_look_big_picture);
		ButterKnife.bind(this);
		initData(getIntent());
		initTitleBar();

		Glide.with(this).load(mPortrait)
				.thumbnail(0.1f)
				.error(R.drawable.ic_default_avatar_big)
				.dontAnimate()
				.into(mMissAvatar);

		mMissAvatar.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
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
	}

	private void initTitleBar() {
		if (mDelete != -1) {
			mTitleBar.setRightVisible(true);
			mTitleBar.setRightImage(ContextCompat.getDrawable(getActivity(), R.drawable.ic_delete));
			mTitleBar.setOnRightViewClickListener(this);
		} else {
			mTitleBar.setRightVisible(false);
		}
	}

	@Override
	public void onClick(View v) {
		setResult(RESULT_OK);
		finish();
	}
}
