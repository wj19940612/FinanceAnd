package com.sbai.finance.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.sbai.finance.Preference;
import com.sbai.finance.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * 引导页
 */

public class GuideActivity extends BaseActivity {
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    @BindView(R.id.enter)
    TextView mEnter;
    private ViewPageAdapter mViewPageAdapter;
    private int mPage;
    private ViewPager.OnPageChangeListener mPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            mPage = position;
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        translucentStatusBar();
        initViewPager();
    }

    private void initViewPager() {
        List<Integer> resList = new ArrayList<>();
        resList.add(R.drawable.ic_guide_page1);
        resList.add(R.drawable.ic_guide_page2);
        resList.add(R.drawable.ic_guide_page3);
        mViewPageAdapter = new ViewPageAdapter(resList, getActivity());
        mViewPager.setAdapter(mViewPageAdapter);
        mViewPager.setOnPageChangeListener(mPageChangeListener);
    }

    @OnClick(R.id.enter)
    public void onViewClicked() {
        if (mPage == 2) {
            Preference.get().setIsFirstOpenAppFalse();
            startActivity(new Intent(this, MainActivity.class));
            supportFinishAfterTransition();
        }
    }

    @Override
    public void onBackPressed() {

    }

    private static class ViewPageAdapter extends PagerAdapter {
        private List<Integer> mRes;
        private Context mContext;

        public ViewPageAdapter(List<Integer> res, Context context) {
            mRes = res;
            mContext = context;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            final int pos = position;
            ImageView imageView = new ImageView(mContext);
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);

            container.addView(imageView);
            Glide.with(mContext).load(mRes.get(pos)).into(imageView);
            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
