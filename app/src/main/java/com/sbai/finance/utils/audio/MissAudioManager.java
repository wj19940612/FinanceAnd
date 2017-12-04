package com.sbai.finance.utils.audio;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.sbai.finance.App;
import com.sbai.finance.Preference;

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
    private volatile int mSource; //播放的来源
    private boolean mPaused;
    private String mUuid;
    private IAudio mAudio;
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

    public IAudio getAudio() {
        return mAudio;
    }

    private final String uuid(IAudio audio) {
        return audio.getAudioId() + "@" + audio.getAudioUrl();
    }

    public void start(IAudio audio, int source) {
        mSource = source;
        start(audio);
    }

    public int getSource() {
        return mSource;
    }

    public void start(IAudio audio) {
        mUuid = uuid(audio);
        mAudio = audio;
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
                    onErrorOccur();
                    mp.reset();
                    if (mPreparing) {
                        mPreparing = false;
                    }
                    stop();
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

                    if (!mAudio.getAudioUrl().equals(mMediaPlayer.dataSourcePath)) {
                        mMediaPlayer.reset();
                        initializeAndPrepare(mAudio.getAudioUrl());
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
                        mOnCompletedListener.onCompleted(mAudio.getAudioUrl());
                    }
                    stop();
                }
            });
        }

        mMediaPlayer.reset();
        initializeAndPrepare(mAudio.getAudioUrl());
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
        Log.d("MediaPlayer", "播放地址: " + audioUrl);
        try {
            mMediaPlayer.setDataSource(audioUrl);
            mMediaPlayer.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("MediaPlayer", "initializeAndPrepare: " + e.toString());
        }
        mPreparing = true;
    }

    public void stop() {
        Log.d("MediaPlayer", "stop: ");
        if (mMediaPlayer != null) {
            if (!mPreparing) {
                Preference.get().setAudioPlayPause(false);
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
            Preference.get().setAudioPlayPause(false);
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

    public boolean isPaused(IAudio audio) {
        if (mMediaPlayer != null) {
            return uuid(audio).equals(mUuid) && mPaused && !mStopPostPrepared;
        }
        return false;
    }

    public boolean isStarted(IAudio audio) {
        if (audio == null) return false;
        if (mMediaPlayer != null) {
            return uuid(audio).equals(mUuid) && !mPaused && !mStopPostPrepared;
        }
        return false;
    }

    public int getCurrentPosition() {
        if (mMediaPlayer != null) {
            return mMediaPlayer.getCurrentPosition();
        }
        return 0;
    }

    public int getDuration() {
        if (mMediaPlayer != null && !mPreparing) {
            return mMediaPlayer.getDuration();
        }
        return 0;
    }

    public void setSeekProgress(int progress) {
        if (mMediaPlayer != null) {
            mMediaPlayer.seekTo(progress);
        }
    }

    public boolean isPlaying() {
        return mMediaPlayer != null && mMediaPlayer.isPlaying();
    }

    private static class MyMediaPlayer extends MediaPlayer {

        private String dataSourcePath;

        @Override
        public void setDataSource(String path)
                throws IOException, IllegalArgumentException, SecurityException, IllegalStateException {
            super.setDataSource(path);
            Log.d("MediaPlayer", "setDataSource: " + path);
            dataSourcePath = path;
        }
    }

    public interface IAudio {

        int getAudioId();

        String getAudioUrl();

    }

    public interface OnAudioListener {

        void onAudioStart();

        void onAudioPlay();

        void onAudioPause();

        void onAudioResume();

        void onAudioStop();

        void onAudioError();
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
        Preference.get().setAudioPlayPause(false);
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

    private void onErrorOccur() {
        for (WeakReference<OnAudioListener> reference : mAudioViewList) {
            if (reference.get() != null) {
                reference.get().onAudioError();
            }
        }
    }
}
