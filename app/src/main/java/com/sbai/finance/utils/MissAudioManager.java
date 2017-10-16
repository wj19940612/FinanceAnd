package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.sbai.finance.App;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static android.content.Context.AUDIO_SERVICE;

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
    private AudioManager mAudioManager;
    private AudioManager.OnAudioFocusChangeListener mOnAudioFocusChangeListener;
    private volatile boolean mPreparing;
    private volatile boolean mStopPostPrepared;
    private boolean mPaused;
    private String mAudioUrl;
    private String mUuid;
    private OnCompletedListener mOnCompletedListener;
    private List<WeakReference<OnAudioListener>> mAudioViewList;

    public MissAudioManager() {
        mAudioManager = (AudioManager) App.getAppContext().getSystemService(AUDIO_SERVICE);
        mOnAudioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            @Override
            public void onAudioFocusChange(int focusChange) {
                switch (focusChange) {
                    case AudioManager.AUDIOFOCUS_GAIN: // Granted audio focus again
                        resume();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS: // Permanent loss of audio focus
                        stop();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT: // Short time
                        pauseTransiently();
                        break;
                    case AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK: // Short time, allow mix, just low volume
                        break;
                }
            }
        };
        mAudioViewList = new ArrayList<>();
    }

    public void setOnCompletedListener(OnCompletedListener onCompletedListener) {
        mOnCompletedListener = onCompletedListener;
    }

    public void addAudioListener(OnAudioListener listener) {
        mAudioViewList.add(new WeakReference<>(listener));
    }

    public void removeAudioListener(OnAudioListener listener) {
        Iterator iterator = mAudioViewList.iterator();
        while (iterator.hasNext()) {
            WeakReference reference = (WeakReference) iterator.next();
            if (reference.get() == listener) {
                iterator.remove();
            }
        }
    }

    public String getAudioUrl() {
        return mAudioUrl;
    }

    private final String uuid(String audioUrl, int id) {
        return id + "@" + audioUrl;
    }

    public void play(String audioUrl, int id) {
        mUuid = uuid(audioUrl, id);
        mAudioUrl = audioUrl;
        mPaused = false;
        mStopPostPrepared = false;
        onStart();

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
                    Log.d("MediaPlayer", "onPrepared: ");
                    mPreparing = false;

                    if (mStopPostPrepared || mPaused) {
                        stop();
                        return;
                    }

                    if (!mAudioUrl.equals(mMediaPlayer.dataSourcePath)) {
                        mMediaPlayer.reset();
                        initializeAndPrepare(mAudioUrl);
                    } else { // prepared, start
                        mMediaPlayer.start();
                        requestAudioFocus();
                        onPlay();
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

    private void requestAudioFocus() {
        int requestAudioFocus = mAudioManager.requestAudioFocus(mOnAudioFocusChangeListener,
                AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN_TRANSIENT);
        if (requestAudioFocus == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            Log.d("MediaPlayer", "requestAudioFocus: success");
        } else {
            Log.d("MediaPlayer", "requestAudioFocus: " + requestAudioFocus);
        }
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
        Log.d("MediaPlayer", "stop: ");
        if (mMediaPlayer != null) {
            if (!mPreparing) {
                mMediaPlayer.stop();
                mMediaPlayer.release();
                mMediaPlayer = null;
                mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
                onStop();
            } else {
                mStopPostPrepared = true;
            }
        }
    }

    public void pause() { // onPause is async
        if (mMediaPlayer != null && !mPaused) {
            if (!mPreparing) {
                mMediaPlayer.pause();
            }
            mAudioManager.abandonAudioFocus(mOnAudioFocusChangeListener);
            mPaused = true;
            onPause();
        }
    }

    private void pauseTransiently() {
        if (mMediaPlayer != null && !mPaused) {
            if (!mPreparing) {
                mMediaPlayer.pause();
            }
            mPaused = true;
            onPause();
        }
    }

    public void resume() { // onResume is also async
        if (mMediaPlayer != null && mPaused) {
            if (!mPreparing) {
                mMediaPlayer.start();
            }
            requestAudioFocus();
            mPaused = false;
            onResume();
        }
    }

    public boolean isPaused(String audioUrl, int id) {
        if (mMediaPlayer != null) {
            return uuid(audioUrl, id).equals(mUuid) && mPaused && !mStopPostPrepared;
        }
        return false;
    }

    public boolean isPlaying(String audioUrl, int id) {
        if (mMediaPlayer != null) {
            return uuid(audioUrl, id).equals(mUuid) && !mPaused && !mStopPostPrepared;
        }
        return false;
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

    public interface OnAudioListener {

        void onAudioStart();

        void onAudioPlay();

        void onAudioPause();

        void onAudioResume();

        void onAudioStop();
    }

    private void onStart() {
        for (WeakReference<OnAudioListener> reference : mAudioViewList) {
            if (reference.get() != null) {
                reference.get().onAudioStart();
            }
        }
    }

    private void onPlay() {
        for (WeakReference<OnAudioListener> reference : mAudioViewList) {
            if (reference.get() != null) {
                reference.get().onAudioPlay();
            }
        }
    }

    private void onPause() {
        for (WeakReference<OnAudioListener> reference : mAudioViewList) {
            if (reference.get() != null) {
                reference.get().onAudioPause();
            }
        }
    }

    private void onResume() {
        for (WeakReference<OnAudioListener> reference : mAudioViewList) {
            if (reference.get() != null) {
                reference.get().onAudioResume();
            }
        }
    }

    private void onStop() {
        for (WeakReference<OnAudioListener> reference : mAudioViewList) {
            if (reference.get() != null) {
                reference.get().onAudioStop();
            }
        }
    }
}
