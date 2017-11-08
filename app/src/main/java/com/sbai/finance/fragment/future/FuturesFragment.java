package com.sbai.finance.fragment.future;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.Variety;
import com.sbai.finance.view.ScrollableViewPager;
import com.sbai.finance.view.training.NoScrollViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 国内 国外期货
 */

public class FuturesFragment extends BaseFragment {
    @BindView(R.id.futureForeign)
    TextView mFutureForeign;
    @BindView(R.id.futureChina)
    TextView mFutureChina;
    @BindView(R.id.viewPager)
    ScrollableViewPager mViewPager;
    Unbinder unbinder;

    private FuturePagesAdapter mFuturePagesAdapter;
    private int mPagePosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_futures_list, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFutureForeign.setSelected(true);
        initViewPager();
    }

    private void initViewPager() {
        mViewPager.setScrollable(false);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mFuturePagesAdapter = new FuturePagesAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mFuturePagesAdapter);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick({R.id.futureForeign, R.id.futureChina})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.futureForeign:
                if (mPagePosition != 0) {
                    mPagePosition = 0;
                    mFutureForeign.setSelected(true);
                    mFutureChina.setSelected(false);
                    mViewPager.setCurrentItem(0);
                }
                break;
            case R.id.futureChina:
                if (mPagePosition != 1) {
                    mPagePosition = 1;
                    mFutureForeign.setSelected(false);
                    mFutureChina.setSelected(true);
                    mViewPager.setCurrentItem(1);
                }
                break;
        }
    }

    static class FuturePagesAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public FuturePagesAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.future_foreign);
                case 1:
                    return mContext.getString(R.string.future_china);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return FutureListFragment.newInstance(Variety.FUTURE_FOREIGN);
                case 1:
                    return FutureListFragment.newInstance(Variety.FUTURE_CHINA);
            }
            return null;
        }

        @Override
        public int getCount() {
            return 2;
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
