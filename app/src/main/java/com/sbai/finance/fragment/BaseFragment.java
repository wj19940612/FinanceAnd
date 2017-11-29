package com.sbai.finance.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ScrollView;

import com.sbai.finance.Preference;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.API;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.httplib.ApiIndeterminate;
import com.umeng.analytics.MobclickAgent;

public class BaseFragment extends Fragment implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    private TimerHandler mTimerHandler;

    protected String TAG;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MobclickAgent.openActivityDurationTrack(false);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onStop() {
        super.onStop();
        boolean isMediaPlay = this instanceof MediaPlayFragment;
        if (!isMediaPlay) {
            if (!Preference.get().isForeground()) {
                MissAudioManager.get().stop();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScheduleJob();
        API.cancel(TAG);
    }

    private void scrollToTop(View view) {
        if (view instanceof AbsListView) {
            ((AbsListView) view).smoothScrollToPositionFromTop(0, 0);
        } else if (view instanceof RecyclerView) {
            ((RecyclerView) view).smoothScrollToPosition(0);
        } else if (view instanceof ScrollView) {
            ((ScrollView) view).smoothScrollTo(0, 0);
        }
    }

    protected void scrollToTop(View anchor, final View view) {
        anchor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scrollToTop(view);
            }
        });
    }

    protected void startScheduleJob(int millisecond) {
        stopScheduleJob();

        if (mTimerHandler == null) {
            mTimerHandler = new TimerHandler(this);
        }

        mTimerHandler.sendEmptyMessageDelayed(millisecond, 0);
    }

    protected void stopScheduleJob() {
        if (mTimerHandler != null) {
            mTimerHandler.removeCallbacksAndMessages(null);
            mTimerHandler.resetCount();
        }
    }

    /**
     * 友盟统计埋点
     *
     * @param eventKey
     */
    protected void umengEventCount(String eventKey) {
        MobclickAgent.onEvent(getActivity(), eventKey);
    }

    @Override
    public void onTimeUp(int count) {

    }

    @Override
    public void onHttpUiShow(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onHttpUiShow(tag);
        }
    }

    @Override
    public void onHttpUiDismiss(String tag) {
        if (getActivity() != null && getActivity() instanceof BaseActivity) {
            ((BaseActivity) getActivity()).onHttpUiDismiss(tag);
        }
    }

    protected void addTopPaddingWithStatusBar(View view) {
        int paddingTop = getStatusBarHeight();
        view.setPadding(0, paddingTop, 0, 0);
    }

    private int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }
}
