package com.sbai.finance.activity.miss;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MissAvatarActivity extends AppCompatActivity {

	@BindView(R.id.missAvatar)
	PhotoView mMissAvatar;

	private String mPortrait;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_miss_avatar);
		ButterKnife.bind(this);
		initData(getIntent());

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
	}
}
