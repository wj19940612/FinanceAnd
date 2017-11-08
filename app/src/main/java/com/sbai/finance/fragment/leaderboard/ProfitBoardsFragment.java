package com.sbai.finance.fragment.leaderboard;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.sbai.finance.R;
import com.sbai.finance.fragment.BaseFragment;
import com.sbai.finance.model.leaderboard.LeaderBoardRank;
import com.sbai.finance.view.ScrollableViewPager;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * 盈利榜
 */

public class ProfitBoardsFragment extends BaseFragment {
    @BindView(R.id.today)
    TextView mToday;
    @BindView(R.id.thisWeek)
    TextView mThisWeek;
    @BindView(R.id.viewPager)
    ScrollableViewPager mViewPager;
    Unbinder unbinder;
    private ProfitPagesAdapter mFuturePagesAdapter;
    private int mPagePosition;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profit_boards, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mToday.setSelected(true);
        initViewPager();
    }

    private void initViewPager() {
        mViewPager.setScrollable(false);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setCurrentItem(0, false);
        mFuturePagesAdapter = new ProfitPagesAdapter(getChildFragmentManager(), getActivity());
        mViewPager.setAdapter(mFuturePagesAdapter);
    }

    @OnClick({R.id.today, R.id.thisWeek})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.today:
                if (mPagePosition != 0) {
                    mPagePosition = 0;
                    mToday.setSelected(true);
                    mThisWeek.setSelected(false);
                    mViewPager.setCurrentItem(0);
                }
                break;
            case R.id.thisWeek:
                if (mPagePosition != 1) {
                    mPagePosition = 1;
                    mToday.setSelected(false);
                    mThisWeek.setSelected(true);
                    mViewPager.setCurrentItem(1);
                }
                break;
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    static class ProfitPagesAdapter extends FragmentPagerAdapter {

        private Context mContext;
        private FragmentManager mFragmentManager;

        public ProfitPagesAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            mFragmentManager = fm;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.today);
                case 1:
                    return mContext.getString(R.string.this_week);
            }
            return super.getPageTitle(position);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return ProfitBoardListFragment.newInstance(LeaderBoardRank.TODAY);
                case 1:
                    return ProfitBoardListFragment.newInstance(LeaderBoardRank.WEEK);
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
