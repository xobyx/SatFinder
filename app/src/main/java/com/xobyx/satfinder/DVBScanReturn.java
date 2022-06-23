package com.xobyx.satfinder;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

class DVBScanReturn {
    private Thread thread;
    private final Runnable mRunnable;
    private int Watchdog;
    private boolean isStart;


    public DVBScanReturn(final DVBFinder dvbFinder) {
        this.thread = null;
        this.isStart = false;
        this.mRunnable = () -> {
            Watchdog=0;
            while (isStart) {
                Log.i("DVBFinder", "DVBFinder Watchdog " + Watchdog);
                Watchdog++;
                dvbFinder.sleep(500);
                if (Watchdog < 40) {
                    continue;
                }

                Handler finderHandler = dvbFinder.getHandler();
                Message message = finderHandler.obtainMessage(DVB.REACHED_THE_LIMIT);
                finderHandler.sendMessage(message);
                Watchdog=0;
                if (DVBFinder.iDVBFinder != null) {
                    DVBFinder.iDVBFinder.On_Bluetooth_connect_fail();
                }

                StopThread();
            }
        };
    }




    public void SyncRestartWatchDog() {
        synchronized (this) {
            this.Watchdog = 0;
        }
    }

    public void StartThread() {
        this.thread = new Thread(this.mRunnable);
        this.Watchdog = 0;
        this.isStart = true;
        this.thread.start();
    }

    public void StopThread() {
        Thread v0 = this.thread;
        if (v0 != null) {
            v0.interrupt();
        }

        this.isStart = false;
    }
}
