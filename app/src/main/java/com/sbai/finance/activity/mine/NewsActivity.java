package com.sbai.finance.activity.mine;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.fragment.mine.EconomicCircleNewsFragment;
import com.sbai.finance.fragment.mine.MutualHelpFragment;
import com.sbai.finance.fragment.mine.SystemNewsFragment;
import com.sbai.finance.model.mine.HistoryNewsModel;
import com.sbai.finance.model.mine.NotReadMessageNumberModel;
import com.sbai.finance.net.Callback2D;
import com.sbai.finance.net.Client;
import com.sbai.finance.net.Resp;
import com.sbai.finance.utils.OnNoReadNewsListener;
import com.sbai.finance.view.slidingTab.SlidingTabLayout;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class NewsActivity extends BaseActivity implements OnNoReadNewsListener {

    @BindView(R.id.slidingTabLayout)
    SlidingTabLayout mSlidingTabLayout;
    @BindView(R.id.viewPager)
    ViewPager mViewPager;
    private NewsAdapter mNewsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        ButterKnife.bind(this);

        mSlidingTabLayout.setDistributeEvenly(true);
        mSlidingTabLayout.setDividerColors(ContextCompat.getColor(getActivity(), android.R.color.transparent));
        mSlidingTabLayout.setCustomTabView(R.layout.layout_new, R.id.news);
        mSlidingTabLayout.setSelectedIndicatorPadding((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 25, getResources().getDisplayMetrics()));
//        mSlidingTabLayout.setPadding(Display.dp2Px(13, getResources()));
        mSlidingTabLayout.setSelectedIndicatorColors(ContextCompat.getColor(this, R.color.blueAssist));
        mNewsAdapter = new NewsAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mNewsAdapter);
        mViewPager.setOffscreenPageLimit(3);
        mSlidingTabLayout.setViewPager(mViewPager);
        requestNoReadNewsNumber();
    }

    private void requestNoReadNewsNumber() {
        Client.getNoReadMessageNumber()
                .setTag(TAG)
                .setIndeterminate(this)
                .setCallback(new Callback2D<Resp<List<NotReadMessageNumberModel>>, List<NotReadMessageNumberModel>>(false) {
                    @Override
                    protected void onRespSuccessData(List<NotReadMessageNumberModel> data) {
                        handleNotReadData(data);
                    }
                })
                .fireSync();
    }

    private void handleNotReadData(List<NotReadMessageNumberModel> data) {
        for (NotReadMessageNumberModel notReadNews : data) {
            switch (notReadNews.getClassify()) {
                case HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE:
                    EconomicCircleNewsFragment economicCircleNewsFragment = (EconomicCircleNewsFragment) mNewsAdapter.getFragment(0);
                    if (notReadNews.getCount() > 0) {
                        if (economicCircleNewsFragment != null) {
                            economicCircleNewsFragment.setNotReadNewsNumber(notReadNews);
                        }
                        mSlidingTabLayout.setRedPointVisibility(0, View.VISIBLE);
                    }
                    break;
                case HistoryNewsModel.NEW_TYPE_MUTUAL_HELP:
                    MutualHelpFragment mutualHelpFragment = (MutualHelpFragment) mNewsAdapter.getFragment(1);
                    if (notReadNews.getCount() > 0) {
                        if (mutualHelpFragment != null) {
                            mutualHelpFragment.setNotReadNewsNumber(notReadNews);
                        }
                        mSlidingTabLayout.setRedPointVisibility(1, View.VISIBLE);
                    }
                    break;
                case HistoryNewsModel.NEW_TYPE_SYSTEM_NEWS:
                    SystemNewsFragment systemNewsFragment = (SystemNewsFragment) mNewsAdapter.getFragment(2);
                    if (notReadNews.getCount() > 0) {
                        if (systemNewsFragment != null) {
                            systemNewsFragment.setNotReadNewsNumber(notReadNews);
                        }
                        mSlidingTabLayout.setRedPointVisibility(2, View.VISIBLE);
                    }
                    break;
            }
        }
    }

    @Override
    public void onNoReadNewsNumber(int index, int count) {
        Log.d(TAG, "onNoReadNewsNumber: " + index);
        switch (index) {
            case HistoryNewsModel.NEW_TYPE_ECONOMIC_CIRCLE:
                mSlidingTabLayout.setRedPointVisibility(0, View.GONE);
                break;
            case HistoryNewsModel.NEW_TYPE_MUTUAL_HELP:
                mSlidingTabLayout.setRedPointVisibility(1, View.GONE);
                break;
            case HistoryNewsModel.NEW_TYPE_SYSTEM_NEWS:
                mSlidingTabLayout.setRedPointVisibility(2, View.GONE);
                break;
        }
    }

    private static class NewsAdapter extends FragmentPagerAdapter {

        private FragmentManager mFragmentManager;
        private Context mContext;

        public NewsAdapter(FragmentManager fm, Context context) {
            super(fm);
            mFragmentManager = fm;
            mContext = context;
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case 0:
                    return new EconomicCircleNewsFragment();
                case 1:
                    return new MutualHelpFragment();
                case 2:
                    return new SystemNewsFragment();
                default:
                    break;
            }
            return null;
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return mContext.getString(R.string.economic_circle);
                case 1:
                    return mContext.getString(R.string.help);
                case 2:
                    return mContext.getString(R.string.system_news);
            }
            return "";
        }

        public Fragment getFragment(int position) {
            return mFragmentManager.findFragmentByTag("android:switcher:" + R.id.viewPager + ":" + position);
        }
    }
}
