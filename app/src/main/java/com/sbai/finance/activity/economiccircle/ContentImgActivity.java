package com.sbai.finance.activity.economiccircle;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.view.HackyViewPager;
import com.sbai.glide.GlideApp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import uk.co.senab.photoview.PhotoView;
import uk.co.senab.photoview.PhotoViewAttacher;

import static com.sbai.finance.utils.Launcher.EX_PAYLOAD;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_1;
import static com.sbai.finance.utils.Launcher.EX_PAYLOAD_2;

public class ContentImgActivity extends BaseActivity implements ViewPager.OnPageChangeListener {
    public static final String DEL_IMAGE = "del";

    @BindView(R.id.viewPager)
    HackyViewPager mViewPager;
    @BindView(R.id.del)
    ImageView mDel;
    @BindView(R.id.pointGroup)
    LinearLayout mPointGroup;

    private ContentImgAdapter mContentImgAdapter;
    private LocalBroadcastManager mLocalBroadcastManager;
    private List<String> mPhotoList;
    private int previousPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_img);
        ButterKnife.bind(this);
        mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
        mPhotoList = Arrays.asList(getIntent().getStringArrayExtra(EX_PAYLOAD));
        mPhotoList = new ArrayList<>(mPhotoList);
        initPointGroup();
        int currentItem = getIntent().getIntExtra(EX_PAYLOAD_1, 0);
        previousPosition = currentItem;
        int del = getIntent().getIntExtra(EX_PAYLOAD_2, -1);
        if (del != -1) {
            mDel.setVisibility(View.VISIBLE);
        }
        mContentImgAdapter = new ContentImgAdapter(this, mPhotoList);
        mViewPager.setAdapter(mContentImgAdapter);
        mViewPager.setCurrentItem(currentItem);
        mViewPager.addOnPageChangeListener(this);
        if (mPointGroup.getChildCount() > 1) {
            mPointGroup.getChildAt(previousPosition).setEnabled(true);
        }
    }

    private void initPointGroup() {
        View view;
        LinearLayout.LayoutParams params;
        if (mPhotoList != null && mPhotoList.size() > 1) {
            for (int i = 0; i < mPhotoList.size(); i++) {

                // 每循环一次需要向LinearLayout中添加一个点的view对象
                view = new View(this);
                view.setBackgroundResource(R.drawable.bg_point);
                params = new LinearLayout.LayoutParams(20, 20);
                if (i != 0) {
                    // 当前不是第一个点, 需要设置左边距
                    params.leftMargin = 20;
                }
                view.setLayoutParams(params);
                view.setEnabled(false);
                mPointGroup.addView(view);
            }
        }
    }

    @OnClick(R.id.del)
    public void onClick(View view) {
        int currentIndex = mViewPager.getCurrentItem();
        Intent intent = new Intent();
        intent.setAction(DEL_IMAGE);
        intent.putExtra(DEL_IMAGE, currentIndex);
        mLocalBroadcastManager.sendBroadcastSync(intent);
        if (mPhotoList.size() <= 1) {
            finish();
        } else {
            mPhotoList.remove(currentIndex);
            mContentImgAdapter.notifyDataSetChanged();
            mViewPager.setCurrentItem(0);
            mPointGroup.removeAllViews();
            initPointGroup();
            if (mPointGroup.getChildCount() > 1) {
                mPointGroup.getChildAt(0).setEnabled(true);
            }
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        int newPosition = position;
        // 把当前选中的点给切换了, 还有描述信息也切换
        mPointGroup.getChildAt(previousPosition).setEnabled(false);
        mPointGroup.getChildAt(newPosition).setEnabled(true);
        // 把当前的索引赋值给前一个索引变量, 方便下一次再切换.
        previousPosition = newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    private class ContentImgAdapter extends PagerAdapter {

        private Context mContext;
        private List<String> mList;

        private ContentImgAdapter(Context context, List<String> list) {
            this.mContext = context;
            mList = list;
        }

        public void setList(List<String> stringList) {
            mList = stringList;
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
            GlideApp.with(mContext).load(mList.get(position))
                    .thumbnail(0.1f)
                    .error(R.drawable.ic_default_avatar_big)
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
