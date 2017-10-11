package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import com.sbai.finance.App;

import java.io.IOException;

import static android.content.Context.AUDIO_SERVICE;

/**
 * 音频播放管理类
 */

public class MediaPlayerManager {

	public static final int STATUS_STOP = 0;
	public static final int STATUS_PLAYING = 1;
	public static final int STATUS_PAUSE = 2;

	public static int STATUS;
	public static int playingId;//播放id
	public static String portrait;//小姐姐头像

	private static MediaPlayer mMediaPlayer;
	private static AudioManager mAudioManager;

	public static void setPlayingId(int playId) {
		MediaPlayerManager.playingId = playId;
	}

	public static void setPortrait(String portrait) {
		MediaPlayerManager.portrait = portrait;
	}

	/**
	 * 播放在线音频
	 *
	 * @param url
	 * @param onCompletionListener
	 */
	public static void play(String url, MediaPlayer.OnPreparedListener onPreparedListener,
	                        MediaPlayer.OnCompletionListener onCompletionListener) {
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();
			mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
				@Override
				public boolean onError(MediaPlayer mp, int what, int extra) {
					if (mMediaPlayer != null) {
						mMediaPlayer.reset();
					}
					return false;
				}
			});
		} else {
			mMediaPlayer.reset();
		}

		try {
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(onPreparedListener);
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始播放
	 */
	public static void start() {
		if (mMediaPlayer != null) {
			int result = requestAudioFocus();
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				mMediaPlayer.start();
			}

			STATUS = STATUS_PLAYING;
		}
	}

	/**
	 * 暂停播放
	 */
	public static void pause() {
		if (mMediaPlayer != null && STATUS == STATUS_PLAYING) {
			mMediaPlayer.pause();
			abandonAudioFocus();
			STATUS = STATUS_PAUSE;
		}
	}

	/**
	 * 继续播放
	 */
	public static void resume() {
		if (mMediaPlayer != null && STATUS == STATUS_PAUSE) {
			int result = requestAudioFocus();
			if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				mMediaPlayer.start();
			}

			STATUS = STATUS_PLAYING;
		}
	}

	/**
	 * 停止播放,释放资源
	 */
	public static void release() {
		if (mMediaPlayer != null) {
			mMediaPlayer.stop();
			mMediaPlayer.release();
			mMediaPlayer = null;
			abandonAudioFocus();
			STATUS = STATUS_STOP;
		}
	}

	/**
	 * 获取音频的时长
	 * @return
	 */
	public static int getDuration() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getDuration();
		}
		return 0;
	}

	/**
	 * 获取音频当前播放位置
	 * @return
	 */
	public static int getCurrentPosition() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}

	/**
	 * 获取音频焦点,防止音轨并发
	 * @return
	 */
	private static int requestAudioFocus() {
		if (mAudioManager == null) {
			mAudioManager = (AudioManager) App.getAppContext().getSystemService(AUDIO_SERVICE);
		}

		return mAudioManager.requestAudioFocus(afChangeListener,
				AudioManager.STREAM_MUSIC,
				AudioManager.AUDIOFOCUS_GAIN);
	}

	/**
	 * 释放焦点
	 */
	private static void abandonAudioFocus() {
		if (mAudioManager != null) {
			mAudioManager.abandonAudioFocus(afChangeListener);
		}
	}

	public static AudioManager.OnAudioFocusChangeListener afChangeListener = new AudioManager.OnAudioFocusChangeListener() {
		public void onAudioFocusChange(int focusChange) {
			if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.pause();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
				if (MediaPlayerManager.STATUS != MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.start();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.release();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.release();
				}

			} else if (focusChange == AudioManager.AUDIOFOCUS_REQUEST_FAILED) {
				if (MediaPlayerManager.STATUS == MediaPlayerManager.STATUS_PLAYING) {
					MediaPlayerManager.release();
				}
			}
		}
	};
}
