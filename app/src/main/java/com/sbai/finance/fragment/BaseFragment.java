package com.sbai.finance.fragment;

import android.content.Context;
import android.support.v4.app.Fragment;

import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.net.APIBase;
import com.sbai.finance.utils.TimerHandler;
import com.sbai.httplib.ApiIndeterminate;

public class BaseFragment extends Fragment implements
        ApiIndeterminate, TimerHandler.TimerCallback {

    private TimerHandler mTimerHandler;
    protected String TAG;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        TAG = this.getClass().getSimpleName();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopScheduleJob();
        APIBase.cancel(TAG);
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
        }
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
}
