package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.sbai.finance.R.id.del;
import static com.sbai.finance.R.id.viewPager;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_1;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_2;


public class ContentImgActivity extends BaseActivity {
    public static final String DEL_IMAGE="del";
	@BindView(viewPager)
	HackyViewPager mViewPager;
    @BindView(del)
	ImageView mDel;
	private ContentImgAdapter mContentImgAdapter;
	private LocalBroadcastManager mLocalBroadcastManager;
	private List<String> mPhotoList;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_content_img);
		ButterKnife.bind(this);
		mLocalBroadcastManager=LocalBroadcastManager.getInstance(this);
		mPhotoList = Arrays.asList(getIntent().getStringArrayExtra(EX_PAYLOAD));
		mPhotoList = new ArrayList<>(mPhotoList);
		int currentItem = getIntent().getIntExtra(EX_PAYLOAD_1, 0);
		int del = getIntent().getIntExtra(EX_PAYLOAD_2,-1);
		if (del!=-1){
			mDel.setVisibility(View.VISIBLE);
		}
		mContentImgAdapter = new ContentImgAdapter(this,mPhotoList);
		mViewPager.setAdapter(mContentImgAdapter);
		mViewPager.setCurrentItem(currentItem);

	}
	@OnClick(R.id.del)
	public void onClick(View view){
		int currentIndex = mViewPager.getCurrentItem();
		Intent intent = new Intent();
		intent.setAction(DEL_IMAGE);
		Bundle bundle = new Bundle();
		bundle.putInt(DEL_IMAGE,currentIndex);
		intent.putExtras(bundle);
		mLocalBroadcastManager.sendBroadcastSync(intent);
		if (mPhotoList.size()<=1){
			finish();
		}else{
			mPhotoList.remove(currentIndex);
			mContentImgAdapter.notifyDataSetChanged();
			mViewPager.setCurrentItem(0);
		}
	}

	private class ContentImgAdapter extends PagerAdapter {

		private Context mContext;
		private List<String> mList;

		private ContentImgAdapter(Context context,List<String> list) {
			this.mContext = context;
			mList = list;
		}
       public void setList(List<String> stringList){
		   mList =stringList;
		   notifyDataSetChanged();
	   }

		@Override
		public int getCount() {
			return mList.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return POSITION_NONE;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			PhotoView imageView = new PhotoView(mContext);
			Glide.with(mContext).load(mList.get(position))
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
