package com.sbai.finance.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.utils.MissAudioManager;
import com.sbai.finance.utils.TimerHandler;

/**
 * Created by ${wangJie} on 2017/11/23.
 * 音频后台播放
 */

public class MediaPlayService extends Service implements TimerHandler.TimerCallback, MissAudioManager.OnAudioListener {

    private static final int DEFAULT_UPDATE_MEDIA_PROGRESS = 100;

    public static final String BROADCAST_ACTION_MEDIA_START = "broadcast_action_media_start";
    public static final String BROADCAST_ACTION_MEDIA_PLAY = "broadcast_action_media_play";
    public static final String BROADCAST_ACTION_MEDIA_PAUSE = "BROADCAST_ACTION_MEDIA_PAUSE";
    public static final String BROADCAST_ACTION_MEDIA_RESUME = "broadcast_action_media_resume";
    public static final String BROADCAST_ACTION_MEDIA_STOP = "broadcast_action_media_stop";
    public static final String BROADCAST_ACTION_MEDIA_ERROR = "broadcast_action_media_error";
    public static final String BROADCAST_ACTION_MEDIA_PROGRESS = "broadcast_action_media_progress";

    private TimerHandler mTimerHandler;
    private Intent mProgressIntent;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return new MediaBinder();
    }

    public class MediaBinder extends Binder {
        public MediaPlayService getMediaPlayService() {
            return MediaPlayService.this;
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mTimerHandler = new TimerHandler(this);
        MissAudioManager.get().addAudioListener(this);
        mProgressIntent = new Intent();
    }

    public void srartPlay(MissAudioManager.IAudio iAudio) {
        MissAudioManager.get().start(iAudio);
    }

    @Override
    public void onTimeUp(int count) {
        mProgressIntent.putExtra(ExtraKeys.MEDIA_PROGRESS, MissAudioManager.get().getCurrentPosition());
        LocalBroadcastManager.getInstance(this).sendBroadcast(mProgressIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onAudioStart() {
        mTimerHandler.sendEmptyMessageDelayed(DEFAULT_UPDATE_MEDIA_PROGRESS, 0);
    }

    @Override
    public void onAudioPlay() {

    }

    @Override
    public void onAudioPause() {

    }

    @Override
    public void onAudioResume() {

    }

    @Override
    public void onAudioStop() {
        mTimerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onAudioError() {

    }
}
