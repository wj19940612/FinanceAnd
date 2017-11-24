package com.sbai.finance.activity.miss;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;

import com.sbai.finance.R;
import com.sbai.finance.activity.BaseActivity;
import com.sbai.finance.service.MediaPlayService;
import com.sbai.finance.utils.ToastUtil;

/**
 * Created by ${wangJie} on 2017/11/23.
 * 包含音频播放的
 */

public abstract class MediaPlayActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBroadcast();
    }

    private void registerBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_STOP);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_ERROR);
        filter.addAction(MediaPlayService.BROADCAST_ACTION_MEDIA_PROGRESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(mMediaPlayBroadcastReceiver, filter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMediaPlayBroadcastReceiver);
    }

    public abstract void onMediaPlayStart(int IAudioId, int source);

    public abstract void onMediaPlay(int IAudioId, int source);

    public abstract void onMediaPlayResume(int IAudioId, int source);

    public abstract void onMediaPlayPause(int IAudioId, int source);

    protected abstract void onMediaPlayStop(int IAudioId, int source);

    protected  void onMediaPlayError(int IAudioId, int source){
        ToastUtil.show(R.string.play_failure);
    }

    public  void onOtherReceive(Context context, Intent intent){

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
