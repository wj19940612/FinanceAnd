package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.sbai.finance.R.id.viewPager;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_1;


public class ContentImgActivity extends BaseActivity {

	@BindView(viewPager)
	ViewPager mViewPager;
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

	public class ContentImgAdapter extends PagerAdapter {

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
			ImageView imageView = new ImageView(mContext);
			Glide.with(mContext).load(mContentImgArray[position])
					.placeholder(R.drawable.help)
					.into(imageView);
			container.addView(imageView);
			return imageView;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}
}
