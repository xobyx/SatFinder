package com.xobyx.satfinder;

import android.content.Context;
import android.media.AudioAttributes;
import android.media.SoundPool;

import java.util.HashMap;

public class DVBBeeper {
    private final HashMap<String, Integer> hashMap;
    private final Runnable mRunnable;
    private final SoundPool mSoundPool;
    public boolean mSound_on;
    private Thread mBeep_thread;
    private int mSleep_time;
    private int mSound_file_res;

    public DVBBeeper(Context context) {
        this.hashMap = new HashMap<>();

        this.mSoundPool = new SoundPool.Builder()
                .setAudioAttributes(new AudioAttributes.Builder()
                        .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC)
                        .setUsage(AudioAttributes.USAGE_MEDIA).build())
                .setMaxStreams(3)
                .build();
        // this.mSoundPool = new SoundPool(3, AudioManager.STREAM_MUSIC, 100);
        this.hashMap.put("beep", this.mSoundPool.load(context, R.raw.beep, 1));  // raw:beep
        this.hashMap.put("quality", this.mSoundPool.load(context, R.raw.quality, 1));  // raw:quality
        this.hashMap.put("strength", this.mSoundPool.load(context, R.raw.strength, 1));  // raw:strength
        this.mRunnable = () -> {
            while (true) {

                if (!mSound_on) {
                    return;
                }

                mSoundPool.play(mSound_file_res, 1.0f, 1.0f, 1, 0, 1.0f);
                try {
                    Thread.sleep(mSleep_time);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
    }


    public int getSleepTime() {
        return this.mSleep_time;
    }

    public void start_sound(int file, int sleep_t) {
        this.mSound_on = true;
        this.mSleep_time = sleep_t;
        if (file == 0) this.mSound_file_res = this.hashMap.get("beep");
        else this.mSound_file_res = this.hashMap.get("quality");
        this.mBeep_thread = new Thread(this.mRunnable);
        this.mBeep_thread.start();
    }

    public void Release() {
        this.stop_sound();
        this.mSoundPool.release();
    }

    public void stop_sound() {
        this.mSound_on = false;
        if (this.mBeep_thread == null) {
            return;
        }

        this.mBeep_thread.interrupt();
        try {
            this.mBeep_thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

