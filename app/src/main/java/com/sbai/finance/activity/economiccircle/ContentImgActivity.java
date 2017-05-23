package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.sbai.finance.R.id.viewPager;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_1;


public class ContentImgActivity extends BaseActivity {

	@BindView(viewPager)
	HackyViewPager mViewPager;

	private ContentImgAdapter mContentImgAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_img);
		ButterKnife.bind(this);


		String[] contentImgArray = getIntent().getStringArrayExtra(EX_PAYLOAD);
		int currentItem = getIntent().getIntExtra(EX_PAYLOAD_1, 0);

		mContentImgAdapter = new ContentImgAdapter(this, contentImgArray);
		mViewPager.setAdapter(mContentImgAdapter);

		mViewPager.setCurrentItem(currentItem);
	}

	private class ContentImgAdapter extends PagerAdapter {

		private Context mContext;
		private String[] mContentImgArray;


		private ContentImgAdapter(Context context, String[] contentImgArray) {
			this.mContentImgArray = contentImgArray;
			this.mContext = context;
		}


		@Override
		public int getCount() {
			return mContentImgArray.length;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView imageView = new PhotoView(mContext);
			Glide.with(mContext).load(mContentImgArray[position])
					.thumbnail(0.1f)
					.dontAnimate()
					.into(imageView);
			container.addView(imageView);

			imageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
				@Override
				public void onPhotoTap(View view, float x, float y) {
					finish();
				}

				@Override
				public void onOutsidePhotoTap() {

				}
			});
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
