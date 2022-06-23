package com.xobyx.satfinder;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

class ReadNewChars {
    final MainActivity mMainActivity;
    private final Runnable mRunnable;
    private Thread mThread;
    private int mCnt;

    public ReadNewChars(MainActivity activity) {
        this.mThread = null;
        this.mMainActivity = activity;
        this.mRunnable = () -> {
            mCnt = 0;
            while (true) {

                DVBFinder finder = mMainActivity.mDvbFinder;
                if (finder.DVBFinder_Status != DVB.CONNECTED_AND_CHARS_SET) {
                    return;
                }

                try {
                    Thread.sleep(500L);
                } catch (InterruptedException interruptedException) {
                    interruptedException.printStackTrace();
                }


                synchronized (ReadNewChars.this) {
                    mCnt++;
                }

                Log.i("DVBFinder", "mCnt " + mCnt);
                Handler mHandler = mMainActivity.mHandler;
                if (mCnt > 8) {
                    Message message = mHandler.obtainMessage(MSG.END_SESSION_14);
                    mHandler.sendMessage(message);
                    mCnt = 0;
                    InterruptThread();
                }


                if (mMainActivity.BT_ID == null) {
                    Message message = mHandler.obtainMessage(MSG.REQUEST_BT_ID_23);
                    mHandler.sendMessage(message);
                }


            }


        };
    }

    public void SyncZeroCnt() {
        synchronized (this) {
            this.mCnt = 0;
        }
    }

    public void Start() {
        this.mThread = new Thread(this.mRunnable);
        this.mCnt = 0;
        this.mThread.start();
    }

    public void InterruptThread() {
        if (this.mThread != null) {
            this.mThread.interrupt();
        }
    }
}
