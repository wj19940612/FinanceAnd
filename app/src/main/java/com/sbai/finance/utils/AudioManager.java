package com.sbai.finance.utils;

public class AudioManager {

    private static AudioManager sAudioManager;

    public AudioManager get() {
        if (sAudioManager == null) {
            sAudioManager = new AudioManager();
        }
        return sAudioManager;
    }


}
