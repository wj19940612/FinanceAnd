package com.sbai.finance.utils;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.widget.ProgressBar;

import java.io.IOException;

/**
 * Created by lixiaokuan0819 on 2017/8/2.
 */

public class MediaPlayerManager {

	private MediaPlayer mMediaPlayer;
	private AudioManager mAudioManager;

	public MediaPlayerManager(Context context) {
		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
	}


	public void play(String url, MediaPlayer.OnCompletionListener onCompletionListener) {

		try {
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						mMediaPlayer.reset();
						return false;
					}
				});
			} else {
				mMediaPlayer.reset();
			}

			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					int result = mAudioManager.requestAudioFocus(afChangeListener,
							AudioManager.STREAM_MUSIC,
							AudioManager.AUDIOFOCUS_GAIN);
					if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						mMediaPlayer.start();
					}
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playUrl(String url, final ProgressBar progressBar, MediaPlayer.OnCompletionListener onCompletionListener) {

		try {
			if (mMediaPlayer == null) {
				mMediaPlayer = new MediaPlayer();
				mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
					@Override
					public boolean onError(MediaPlayer mp, int what, int extra) {
						mMediaPlayer.reset();
						return false;
					}
				});
			} else {
				mMediaPlayer.reset();
			}

			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
				@Override
				public void onPrepared(MediaPlayer mp) {
					int result = mAudioManager.requestAudioFocus(afChangeListener,
							AudioManager.STREAM_MUSIC,
							AudioManager.AUDIOFOCUS_GAIN);
					if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
						mMediaPlayer.start();
						progressBar.setMax(mMediaPlayer.getDuration());
						new Thread(new Runnable() {
							@Override
							public void run() {
								while (true) {
									// 将SeekBar位置设置到当前播放位置
									progressBar.setProgress(mMediaPlayer.getCurrentPosition());
									try {
										// 每100毫秒更新一次位置
										Thread.sleep(100);
									} catch (InterruptedException e) {
										e.printStackTrace();
									}
								}
							}
						}).start();
					}
				}
			});

		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	public void release() {
		if (mMediaPlayer != null) {
			mAudioManager.abandonAudioFocus(afChangeListener);
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
		}
	}

	AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.pause();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (!mMediaPlayer.isPlaying()) {
					mMediaPlayer.start();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
				mAudioManager.abandonAudioFocus(afChangeListener);

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
				if (mMediaPlayer.isPlaying()) {
					mMediaPlayer.stop();
				}
			}
		}
	};
}
