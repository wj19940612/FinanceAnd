package com.sbai.finance.utils.ffmpeg;

/**
 * Created by ${wangJie} on 2017/12/5.
 */

public enum AudioFormat {
    AAC,
    MP3,
    M4A,
    WMA,
    WAV,
    FLAC;

    public String getFormat() {
        return name().toLowerCase();
    }
}
