package com.sbai.finance.activity;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.utils.Launcher;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 引导页
 */

public class GuideActivity extends BaseActivity {

    @BindView(R.id.viewPager)
    ViewPager mViewPager;

    private ViewPageAdapter mViewPageAdapter;

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
    }

    @Override
    public void onBackPressed() {

    }

    private static class ViewPageAdapter extends PagerAdapter {
        private List<Integer> mRes;
        private Activity mActivity;

        public ViewPageAdapter(List<Integer> res, Activity activity) {
            mRes = res;
            mActivity = activity;
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
            ImageView imageView = new ImageView(mActivity);
            imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);

            container.addView(imageView);
            Glide.with(mActivity).load(mRes.get(position)).into(imageView);

            if (position == getCount() - 1) {
                imageView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Preference.get().setNoShowGuide();
                        Launcher.with(mActivity, MainActivity.class).execute();
                        mActivity.finish();
                    }
                });
            }

            return imageView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }
    }
}
