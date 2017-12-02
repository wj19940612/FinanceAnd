package com.sbai.finance.activity.miss;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;

import com.sbai.finance.Preference;
import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.ToastUtil;
import com.sbai.finance.utils.audio.MissAudioManager;

/**
 * Created by ${wangJie} on 2017/11/23.
 * 包含音频播放的
 */

public abstract class MediaPlayActivity extends BaseActivity {

    protected MediaPlayService mMediaPlayService;

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            mMediaPlayService = ((MediaPlayService.MediaBinder) iBinder).getMediaPlayService();
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
        }
    };

    @Override
    protected void onStart() {
        super.onStart();
        registerBroadcast();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (Preference.get().isAudioPlayPause()) {
            MissAudioManager.IAudio audio = MissAudioManager.get().getAudio();
            if (audio != null) {
                if (MissAudioManager.get().isPaused(audio)) {
                    MissAudioManager.get().resume();
                }
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMediaPlayBroadcastReceiver);
        if (MissAudioManager.get().isPlaying()) {
            if (!Preference.get().isForeground()) {
                MissAudioManager.get().pause();
                Preference.get().setAudioPlayPause(true);
            }
        }
    }

    private void registerBroadcast() {
        LocalBroadcastManager.getInstance(this).registerReceiver(mMediaPlayBroadcastReceiver, getIntentFilter());
    }

    protected IntentFilter getIntentFilter() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_START);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_RESUME);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PAUSE);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_STOP);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_ERROR);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PROGRESS);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PLAY);
        return filter;
    }


    public abstract void onMediaPlayStart(int IAudioId, int source);

    public abstract void onMediaPlay(int IAudioId, int source);

    public abstract void onMediaPlayResume(int IAudioId, int source);

    public abstract void onMediaPlayPause(int IAudioId, int source);

    protected abstract void onMediaPlayStop(int IAudioId, int source);

    protected void onMediaPlayError(int IAudioId, int source) {
        ToastUtil.show(R.string.play_failure);
    }

    public void onOtherReceive(Context context, Intent intent) {

    }

    protected abstract void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration);


    private MediaPlayService.MediaPlayBroadcastReceiver mMediaPlayBroadcastReceiver = new MediaPlayService.MediaPlayBroadcastReceiver() {
        @Override
        public void onOtherReceive(Context context, Intent intent) {
            MediaPlayActivity.this.onOtherReceive(context, intent);
        }

        @Override
        public void onMediaPlayStart(int IAudioId, int source) {
            MediaPlayActivity.this.onMediaPlayStart(IAudioId, source);
        }

        @Override
        public void onMediaPlayResume(int IAudioId, int source) {
            MediaPlayActivity.this.onMediaPlayResume(IAudioId, source);
        }

        @Override
        public void onMediaPlayPause(int IAudioId, int source) {
            MediaPlayActivity.this.onMediaPlayPause(IAudioId, source);
        }

        @Override
        public void onMediaPlayStop(int IAudioId, int source) {
            MediaPlayActivity.this.onMediaPlayStop(IAudioId, source);
        }

        @Override
        public void onMediaPlayError(int IAudioId, int source) {
            MediaPlayActivity.this.onMediaPlayError(IAudioId, source);
        }

        @Override
        public void onMediaPlay(int IAudioId, int source) {
            MediaPlayActivity.this.onMediaPlay(IAudioId, source);
        }

        @Override
        public void onMediaPlayCurrentPosition(int IAudioId, int source, int mediaPlayCurrentPosition, int totalDuration) {
            MediaPlayActivity.this.onMediaPlayCurrentPosition(IAudioId, source, mediaPlayCurrentPosition, totalDuration);
        }
    };

}
