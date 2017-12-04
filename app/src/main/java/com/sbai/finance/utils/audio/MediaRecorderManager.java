package com.sbai.finance.utils.audio;

import android.content.Context;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.util.Log;

import com.sbai.finance.App;

import java.io.File;

import static android.media.MediaRecorder.MEDIA_ERROR_SERVER_DIED;
import static android.media.MediaRecorder.MEDIA_RECORDER_ERROR_UNKNOWN;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_DURATION_REACHED;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED;
import static android.media.MediaRecorder.MEDIA_RECORDER_INFO_UNKNOWN;

/**
 * Created by ${wangJie} on 2017/11/30.
 * 录制音频
 */

public class MediaRecorderManager implements MediaRecorder.OnErrorListener, MediaRecorder.OnInfoListener {
    private static final String TAG = "MediaRecorderManager";

    private static final int DEFAULT_MAX_AUDIO_LENGTH = 10 * 60 * 1000;  //最大时间长度

    private MediaRecorder mMediaRecorder;
    private volatile boolean mPreparing;
    private String mRecordAudioPath;
    private MediaMediaRecorderPrepareListener mMediaMediaRecorderPrepareListener;

    public interface MediaMediaRecorderPrepareListener {
        void onMediaMediaRecorderPrepared();
    }

    public void setMediaMediaRecorderPrepareListener(MediaMediaRecorderPrepareListener mediaMediaRecorderPrepareListener) {
        mMediaMediaRecorderPrepareListener = mediaMediaRecorderPrepareListener;
    }

    public MediaRecorderManager() {
    }

    private void onPrepare() {
        mMediaRecorder = new MediaRecorder();
        mMediaRecorder.setOnErrorListener(this);
        mMediaRecorder.setOnInfoListener(this);
        //配置 MediaRecorder
        //从麦克风采集
        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        //输出格式为 aac 格式
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        }
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
        //所有安卓系统都支持采样频率
        mMediaRecorder.setAudioSamplingRate(44100);
        //通用的 AAC 编码格式
        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
        //音质比较好的频率
        mMediaRecorder.setAudioEncodingBitRate(96000);
        //设置最大语音时间长度
        mMediaRecorder.setMaxFileSize(DEFAULT_MAX_AUDIO_LENGTH);

//        mMediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
////        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//        mMediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
//        mMediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        //设置录音文件配置
        mMediaRecorder.setOutputFile(getOutputFilePath());

    }

    private String getOutputFilePath() {
        String absolutePath;
        Context appContext = App.getAppContext();
        if (appContext.getExternalCacheDir() != null) {
            absolutePath = appContext.getExternalCacheDir().getAbsolutePath();
        } else {
            absolutePath = Environment.getExternalStorageDirectory() + "/miss_record";
        }
        File dir = new File(absolutePath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

//        File file = new File(dir, System.currentTimeMillis() + ".3gp");
        File file = new File(dir, System.currentTimeMillis() + ".aac");
//        File file = new File(dir, System.currentTimeMillis() + ".mp3");
        mRecordAudioPath = file.getAbsolutePath();
        return file.getAbsolutePath();
    }

    public String getRecordAudioPath() {
        return mRecordAudioPath;
    }

    public void onRecordStart() {
        //配置 MediaRecorder
        mPreparing = false;
            try {
                onPrepare();
                mMediaRecorder.prepare();
                mMediaRecorder.start();
                mPreparing = true;
                if (mMediaMediaRecorderPrepareListener != null) {
                    mMediaMediaRecorderPrepareListener.onMediaMediaRecorderPrepared();
                }
            } catch (Exception e) {
                mPreparing = false;
                Log.d(TAG, "onPrepare: " + e.toString());
                e.printStackTrace();
            }
    }


    public void onRecordStop() {
        if(mMediaRecorder!=null){
            try {
                mMediaRecorder.setOnErrorListener(null);
                mMediaRecorder.setOnInfoListener(null);
                mMediaRecorder.setPreviewDisplay(null);
                mMediaRecorder.stop();
            } catch (Exception e) {
                Log.d(TAG, "onRecordRelease: " + e.toString());
                e.printStackTrace();
            }
            mMediaRecorder.release();
            mMediaRecorder = null;
        }
    }

    public void onDestroy() {
        deleteRadioFile();
        if (mMediaRecorder != null) {
            onRecordStop();
            mMediaRecorder = null;
        }
    }

    public void deleteRadioFile() {
        if (mRecordAudioPath != null) {
            File file = new File(mRecordAudioPath);
            if (file != null && file.exists()) {
                file.delete();
                mRecordAudioPath = null;
            }
        }
    }

    @Override
    public void onError(MediaRecorder mediaRecorder, int what, int extra) {
        switch (what) {
            case MEDIA_RECORDER_ERROR_UNKNOWN:
                Log.d(TAG, "onError: " + MEDIA_RECORDER_ERROR_UNKNOWN);
                break;
            case MEDIA_ERROR_SERVER_DIED:   //  Media server died. In this case, the application must release the  * MediaRecorder object and instantiate a new one.
                Log.d(TAG, "onError: " + MEDIA_ERROR_SERVER_DIED);
                break;
        }
    }

    @Override
    public void onInfo(MediaRecorder mediaRecorder, int what, int extra) {
        switch (what) {
            case MEDIA_RECORDER_INFO_UNKNOWN:
                Log.d(TAG, "onInfo: " + MEDIA_RECORDER_INFO_UNKNOWN);
                break;
            case MEDIA_RECORDER_INFO_MAX_DURATION_REACHED:   //到达最大时间长度
                Log.d(TAG, "onInfo: " + MEDIA_RECORDER_INFO_MAX_DURATION_REACHED);
                break;
            case MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED:  //到答案最大文件大小
                Log.d(TAG, "onInfo: " + MEDIA_RECORDER_INFO_MAX_FILESIZE_REACHED);
                break;
        }
    }
}
