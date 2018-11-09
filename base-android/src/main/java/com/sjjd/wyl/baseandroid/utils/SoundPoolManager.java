package com.sjjd.wyl.baseandroid.utils;

/**
 * Created by wyl on 2018/10/13.
 */

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Build;

import static android.content.Context.AUDIO_SERVICE;
import static android.support.constraint.Constraints.TAG;

public class SoundPoolManager {

    private boolean playing = false;
    private boolean loaded = false;
    private float actualVolume;
    private float maxVolume;
    private float volume;
    private AudioManager audioManager;
    private SoundPool soundPool;
    private int rightSoundId;
    private int resId;
    private int ringingStreamId;
    private static SoundPoolManager instance;

    private SoundPoolManager(Context context, int id) {
        // AudioManager audio settings for adjusting the volume
        audioManager = (AudioManager) context.getSystemService(AUDIO_SERVICE);
        actualVolume = (float) audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        maxVolume = (float) audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        volume = actualVolume / maxVolume;
        resId = id;
        // Load the sounds
        //因为在5.0上new SoundPool();被弃用 5.0上利用Builder
        //创建SoundPool
        int maxStreams = 1;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            soundPool = new SoundPool.Builder()
                    .setMaxStreams(maxStreams)
                    .build();
        } else {
            soundPool = new SoundPool(maxStreams, AudioManager.STREAM_MUSIC, 0);
        }

        soundPool.setOnLoadCompleteListener(new SoundPool.OnLoadCompleteListener() {
            @Override
            public void onLoadComplete(SoundPool soundPool, int i, int i1) {
                loaded = true;
                if (waitPlaying) playRinging();
            }
        });
        //加载资源ID
        rightSoundId = soundPool.load(context, resId, 1);
    }

    public static SoundPoolManager getInstance(Context context, int id) {
        if (instance == null) {
            instance = new SoundPoolManager(context, id);
        }
        return instance;
    }

    boolean waitPlaying = false;

    //播放
    public void playRinging() {
        if (!loaded) {
            waitPlaying = true;
        }
        LogUtils.e(TAG, "playRinging: " + loaded + " " + playing);
        if (loaded && !playing) {
            ringingStreamId = soundPool.play(rightSoundId, volume, volume, 1, -1, 1f);
            playing = true;
        }
    }

    //Stop播放
    public void stopRinging() {
        if (playing) {
            soundPool.stop(ringingStreamId);
            playing = false;
        }
    }

    public void release() {
        if (soundPool != null) {
            soundPool.unload(rightSoundId);
            soundPool.release();
            soundPool = null;
        }
        instance = null;
    }
}
