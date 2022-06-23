package com.xobyx.satfinder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.ScanCallback;
import android.bluetooth.le.ScanResult;
import android.bluetooth.le.ScanSettings;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class DVBLeScanCallback extends ScanCallback {
    final DVBFinder mDVBF;

    DVBLeScanCallback(DVBFinder arg1) {
        super();
        this.mDVBF = arg1;

    }

    @Override
    public void onScanFailed(int errorCode) {

        if(errorCode==SCAN_FAILED_INTERNAL_ERROR){}
            else if (errorCode==SCAN_FAILED_ALREADY_STARTED){}
                 else if (errorCode==SCAN_FAILED_APPLICATION_REGISTRATION_FAILED){}
                     else if (errorCode==SCAN_FAILED_FEATURE_UNSUPPORTED){}
        Handler handler = mDVBF.getHandler();
        mDVBF.DVBFinder_Status = DVB.START_SCAN_FAILED;
        Message message = handler.obtainMessage(DVB.START_SCAN_FAILED);
        handler.sendMessage(message);
    }

    @Override
    public void onScanResult(int callbackType, ScanResult result) {

        if(callbackType== ScanSettings.CALLBACK_TYPE_FIRST_MATCH && result.getDevice()!=null)
        {
            Handler handler = mDVBF.getHandler();
            mDVBF.mDVbObj.SyncRestartWatchDog();


            mDVBF.TxPower =result.getTxPower();
            synchronized(this) {
                if(this.mDVBF.DVBFinder_Status != DVB.SCAN_START_SUCCESSFULLY) {
                    return;
                }

                mDVBF.DVBFinder_Status = DVB.FOUND_DEVICE;
                Message message = handler.obtainMessage(DVB.FOUND_DEVICE, result.getDevice());
                handler.sendMessage(message);
            }

            this.mDVBF.mDevice_rssi = result.getRssi();
        }
    }

     // android.bluetooth.BluetoothAdapter$LeScanCallback
    public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
        if(device != null && device.getName() != null) {
            Log.i("DVBFinder", "device " + device.getName() + " rssi " + rssi + " address " + device.getAddress());
            if(rssi > -95 && ("V8 Finder-BT03".equals(device.getName()))) {
                this.mDVBF.mDVbObj.SyncRestartWatchDog();
                synchronized(this) {
                    if(mDVBF.DVBFinder_Status != DVB.SCAN_START_SUCCESSFULLY) {
                        return;
                    }

                    mDVBF.DVBFinder_Status = DVB.FOUND_DEVICE;
                    Handler handler = mDVBF.getHandler();
                    Message message = handler.obtainMessage(DVB.FOUND_DEVICE, device);
                    handler.sendMessage(message);
                }

                this.mDVBF.mDevice_rssi = rssi;

            }
        }
    }
}

