package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 音频播放管理类
 */

public class MediaPlayerManager {

	public static final int STATUS_STOP = 0;
	public static final int STATUS_PLAYING = 1;
	public static final int STATUS_PAUSE = 2;


	public static int playingId;//播放id
	public static String url;//音频
	public static String portrait;//小姐姐头像

	public static int STATUS;
	private static MediaPlayer mMediaPlayer;

	public static void setPlayingId(int playId) {
		MediaPlayerManager.playingId = playId;
	}

	public static void setUrl(String url) {
		MediaPlayerManager.url = url;
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
			mMediaPlayer.setOnCompletionListener(onCompletionListener);
			mMediaPlayer.setDataSource(url);
			mMediaPlayer.prepareAsync();
			mMediaPlayer.setOnPreparedListener(onPreparedListener);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 开始播放
	 */
	public static void start() {
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
			STATUS = STATUS_PLAYING;
		}
	}

	/**
	 * 暂停播放
	 */
	public static void pause() {
		if (mMediaPlayer != null && STATUS == STATUS_PLAYING) {
			mMediaPlayer.pause();
			STATUS = STATUS_PAUSE;
		}
	}

	/**
	 * 继续播放
	 */
	public static void resume() {
		if (mMediaPlayer != null && STATUS == STATUS_PAUSE) {
			mMediaPlayer.start();
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
			STATUS = STATUS_STOP;
		}
	}

	/**
	 * 获取音频的时长
	 *
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
	 *
	 * @return
	 */
	public static int getCurrentPosition() {
		if (mMediaPlayer != null) {
			return mMediaPlayer.getCurrentPosition();
		}
		return 0;
	}
}
