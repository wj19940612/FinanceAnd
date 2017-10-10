package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MissAudioManager {

    public interface OnCompletedListener {
        void onCompleted(String url);
    }

    private static MissAudioManager sMissAudioManager;

    public static MissAudioManager get() {
        if (sMissAudioManager == null) {
            sMissAudioManager = new MissAudioManager();
        }
        return sMissAudioManager;
    }

    private MyMediaPlayer mMediaPlayer;
    private boolean mPreparing;
    private boolean mPaused;
    private String mAudioUrl;
    private OnCompletedListener mOnCompletedListener;

    public void setOnCompletedListener(OnCompletedListener onCompletedListener) {
        mOnCompletedListener = onCompletedListener;
    }

    public String getAudioUrl() {
        return mAudioUrl;
    }

    public void play(String audioUrl) {
        mAudioUrl = audioUrl;
        mPaused = false;

        if (mPreparing) return;

        stop();

        if (mMediaPlayer == null) {
            mMediaPlayer = new MyMediaPlayer();
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    Log.d("MediaPlayer", "onError: " + what + ", extra: " + extra);
                    mp.reset();
                    return false;
                }
            });
            mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    mPreparing = false;
                    if (!mAudioUrl.equals(mMediaPlayer.dataSourcePath)) {
                        mMediaPlayer.reset();
                        initializeAndPrepare(mAudioUrl);
                    } else { // prepared, start
                        mMediaPlayer.start();
                    }
                }
            });
            mMediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    if (mOnCompletedListener != null) {
                        mOnCompletedListener.onCompleted(mAudioUrl);
                    }
                    stop();
                }
            });
        }

        mMediaPlayer.reset();
        initializeAndPrepare(audioUrl);
    }

    private void initializeAndPrepare(String audioUrl) {
        mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mMediaPlayer.setDataSource(audioUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        mMediaPlayer.prepareAsync();
        mPreparing = true;
    }

    public void stop() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.release();
            mMediaPlayer = null;
            mAudioUrl = null;
        }
    }

    public void pause() { // pause is async
        if (mMediaPlayer != null && !mPaused) {
            mMediaPlayer.pause();
            mPaused = true;
        }
    }

    public void resume() { // resume is also async
        if (mMediaPlayer != null && mPaused) {
            mMediaPlayer.start();
            mPaused = false;
        }
    }

    public boolean isPaused(String audioUrl) {
        if (mMediaPlayer != null) {
            return audioUrl != null && audioUrl.equals(mAudioUrl) && mPaused;
        }
        return false;
    }

    public boolean isPlaying(String audioUrl) {
        return audioUrl != null && audioUrl.equals(mAudioUrl) && !mPaused;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    private static class MyMediaPlayer extends MediaPlayer {

        private String dataSourcePath;

        @Override
        public void setDataSource(String path)
                throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
            super.setDataSource(path);
            dataSourcePath = path;
        }
    }
}
