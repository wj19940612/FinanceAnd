package com.sbai.finance.utils;

import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.IOException;

/**
 * 音频播放管理类
 */

public class MediaPlayerManager {
	private static MediaPlayer mMediaPlayer;
	private static boolean mIsPause;

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
	 * 是否正在播放
	 *
	 * @return
	 */
	public static boolean isPlaying() {
		return mMediaPlayer != null && mMediaPlayer.isPlaying();
	}

	/**
	 * 开始播放
	 */
	public static void start() {
		if (mMediaPlayer != null) {
			mMediaPlayer.start();
		}
	}

	/**
	 * 暂停播放
	 */
	public static void pause() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.pause();
			mIsPause = true;
		}
	}

	/**
	 * 继续播放
	 */
	public static void resume() {
		if (mMediaPlayer != null && mIsPause) {
			mMediaPlayer.start();
			mIsPause = false;
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

	public static void seek(int msec) {
		if (mMediaPlayer != null) {
			mMediaPlayer.seekTo(msec);
		}
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
