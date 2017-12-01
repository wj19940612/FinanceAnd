package com.sbai.finance.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;

import com.sbai.finance.ExtraKeys;
import com.sbai.finance.utils.audio.MissAudioManager;
import com.sbai.finance.utils.TimerHandler;

/**
 * Created by ${wangJie} on 2017/11/23.
 * 音频后台播放
 */

public class MediaPlayService extends Service implements TimerHandler.TimerCallback, MissAudioManager.OnAudioListener {

    private static final String TAG = "MediaPlayService";

    private static final int DEFAULT_UPDATE_MEDIA_PROGRESS = 200;

    public static final String BROADCAST_ACTION_MEDIA_START = "broadcast_action_media_start";
    public static final String BROADCAST_ACTION_MEDIA_PLAY = "broadcast_action_media_play";
    public static final String BROADCAST_ACTION_MEDIA_PAUSE = "broadcast_action_media_pause";
    public static final String BROADCAST_ACTION_MEDIA_RESUME = "broadcast_action_media_resume";
    public static final String BROADCAST_ACTION_MEDIA_STOP = "broadcast_action_media_stop";
    public static final String BROADCAST_ACTION_MEDIA_ERROR = "broadcast_action_media_error";

    public static final String BROADCAST_ACTION_MEDIA_PROGRESS = "broadcast_action_media_progress";

    public static final int MEDIA_SOURCE_RECOMMEND_RADIO = 1;//音频的来源  推荐电台 或者电台详情
    public static final int MEDIA_SOURCE_HOT_QUESTION = 2;//音频的来源  最热提问
    public static final int MEDIA_SOURCE_LATEST_QUESTION = 3;//音频的来源  最新提问
    public static final int MEDIA_SOURCE_MISS_PROFILE = 4;//音频的来源  小姐姐个人主页


    private TimerHandler mTimerHandler;
    private Intent mMediaPlayIntent;
    private Intent mMediaStartIntent;
    private MissAudioManager mMissAudioManager;
    private MissAudioManager.IAudio mIAudio;
    private int mSource;

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
        mMediaPlayIntent = new Intent();
        mMediaStartIntent = new Intent();
        mMissAudioManager = MissAudioManager.get();
        mMissAudioManager.addAudioListener(this);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mMissAudioManager == null) {
            mMissAudioManager = MissAudioManager.get();
            mMissAudioManager.addAudioListener(this);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    public void startPlay(MissAudioManager.IAudio iAudio, int source) {
        mSource = source;
        mIAudio = iAudio;
        if (mMissAudioManager != null) {
            mMissAudioManager.start(iAudio, source);
        }
    }

    public void onPausePlay(MissAudioManager.IAudio iAudio) {
        if (mMissAudioManager != null) {
            mMissAudioManager.pause();
        }
    }

    public void seekTo(int progress) {
        if (mMissAudioManager != null) {
            mMissAudioManager.setSeekProgress(progress);
        }
    }

    public void onResume() {
        if (mMissAudioManager != null) {
            mMissAudioManager.resume();
        }
    }

    @Override
    public void onTimeUp(int count) {
        mMediaPlayIntent.setAction(BROADCAST_ACTION_MEDIA_PROGRESS);
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_PROGRESS, mMissAudioManager.getCurrentPosition());
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_TOTAL_PROGRESS, mMissAudioManager.getDuration());
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaPlayIntent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mTimerHandler.removeCallbacksAndMessages(null);
        MissAudioManager.get().removeAudioListener(this);
        mMissAudioManager = null;
        mMediaPlayIntent = null;
    }

    @Override
    public void onAudioStart() {
        mMediaStartIntent.setAction(BROADCAST_ACTION_MEDIA_START);
        if (mIAudio != null) {
            mMediaStartIntent.putExtra(ExtraKeys.IAudio, mIAudio.getAudioId());
        }
        mMediaStartIntent.putExtra(ExtraKeys.MEDIA_PLAY_SOURCE, mSource);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaStartIntent);
        mTimerHandler.sendEmptyMessageDelayed(DEFAULT_UPDATE_MEDIA_PROGRESS, 0);
    }

    @Override
    public void onAudioPlay() {
        mMediaPlayIntent.setAction(BROADCAST_ACTION_MEDIA_PLAY);
        if (mIAudio != null) {
            mMediaPlayIntent.putExtra(ExtraKeys.IAudio, mIAudio.getAudioId());
        }
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_PLAY_SOURCE, mSource);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaPlayIntent);
        mTimerHandler.sendEmptyMessageDelayed(DEFAULT_UPDATE_MEDIA_PROGRESS, 0);
    }

    @Override
    public void onAudioPause() {
        mMediaPlayIntent.setAction(BROADCAST_ACTION_MEDIA_PAUSE);
        if (mIAudio != null) {
            mMediaPlayIntent.putExtra(ExtraKeys.IAudio, mIAudio.getAudioId());
        }
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_PLAY_SOURCE, mSource);
        mTimerHandler.removeCallbacksAndMessages(null);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaPlayIntent);
    }

    @Override
    public void onAudioResume() {
        mMediaPlayIntent.setAction(BROADCAST_ACTION_MEDIA_RESUME);
        if (mIAudio != null) {
            mMediaPlayIntent.putExtra(ExtraKeys.IAudio, mIAudio.getAudioId());
        }
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_PLAY_SOURCE, mSource);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaPlayIntent);
        mTimerHandler.sendEmptyMessageDelayed(DEFAULT_UPDATE_MEDIA_PROGRESS, 0);
    }

    @Override
    public void onAudioStop() {
        mMediaPlayIntent.setAction(BROADCAST_ACTION_MEDIA_STOP);
        if (mIAudio != null) {
            mMediaPlayIntent.putExtra(ExtraKeys.IAudio, mIAudio.getAudioId());
        }
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_PLAY_SOURCE, mSource);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaPlayIntent);
        mTimerHandler.removeCallbacksAndMessages(null);
    }

    @Override
    public void onAudioError() {
        mMediaPlayIntent.setAction(BROADCAST_ACTION_MEDIA_ERROR);
        if (mIAudio != null) {
            mMediaPlayIntent.putExtra(ExtraKeys.IAudio, mIAudio.getAudioId());
        }
        mMediaPlayIntent.putExtra(ExtraKeys.MEDIA_PLAY_SOURCE, mSource);
        LocalBroadcastManager.getInstance(this).sendBroadcast(mMediaPlayIntent);
        mTimerHandler.removeCallbacksAndMessages(null);
    }


    public static abstract class MediaPlayBroadcastReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && !TextUtils.isEmpty(intent.getAction())) {
                int IAudioId = intent.getIntExtra(ExtraKeys.IAudio, -1);
                int source = intent.getIntExtra(ExtraKeys.MEDIA_PLAY_SOURCE, -1);
                switch (intent.getAction()) {
                    case BROADCAST_ACTION_MEDIA_START:
                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_START ");
                        onMediaPlayStart(IAudioId, source);
                        break;
                    case BROADCAST_ACTION_MEDIA_PLAY:
                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_PLAY ");
                        onMediaPlay(IAudioId, source);
                        break;
                    case BROADCAST_ACTION_MEDIA_RESUME:
                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_RESUME ");
                        onMediaPlayResume(IAudioId, source);
                        break;
                    case BROADCAST_ACTION_MEDIA_PAUSE:
                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_PAUSE ");
                        onMediaPlayPause(IAudioId, source);
                        break;
                    case BROADCAST_ACTION_MEDIA_STOP:
                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_STOP ");
                        onMediaPlayStop(IAudioId, source);
                        break;
                    case BROADCAST_ACTION_MEDIA_ERROR:
                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_ERROR ");
                        onMediaPlayError(IAudioId, source);
                        break;
                    case BROADCAST_ACTION_MEDIA_PROGRESS:
                        int mediaPlayCurrentPosition = intent.getIntExtra(ExtraKeys.MEDIA_PROGRESS, -1);
                        int totalDuration = intent.getIntExtra(ExtraKeys.MEDIA_TOTAL_PROGRESS, 0);
//                        Log.d(TAG, "onReceive: BROADCAST_ACTION_MEDIA_PROGRESS "+ mediaPlayCurrentPosition+"  "+totalDuration);
                        onMediaPlayCurrentPosition(IAudioId, source, mediaPlayCurrentPosition, totalDuration);
                        break;
                    default:
                        onOtherReceive(context, intent);
                        break;
                }
            }
        }

        public abstract void onOtherReceive(Context context, Intent intent);

        public abstract void onMediaPlayStart(int IAudioId, int source);

        public abstract void onMediaPlayResume(int IAudioId, int source);

        public abstract void onMediaPlayPause(int IAudioId, int source);

        public abstract void onMediaPlayStop(int IAudioId, int source);

        public abstract void onMediaPlayError(int IAudioId, int source);

        public abstract void onMediaPlay(int IAudioId, int source);


        /**
         * @param IAudioId
         * @param source
         * @param mediaPlayCurrentPosition
         * @param totalDuration            总时长
         */
        public abstract void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration);
    }
}
