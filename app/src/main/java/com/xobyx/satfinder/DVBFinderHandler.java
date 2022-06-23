package com.xobyx.satfinder;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.le.BluetoothLeScanner;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

class DVBFinderHandler implements Handler.Callback {
    private static final int SCAN_START_SUCCESSFULLY_100 = 100;
    private static final int DISCONNECTING_101 = 101;
    private static final int CONNECTED_102 = 102;
    private static final int CONNECTED_AND_CHARS_SET_103 = 103;
    private static final int RELEASE_104 = 104;
    private static final int START_105 = 105;
    private static final int m = 106;
    private static final int UNSUCCESSFUL_107 = 107;
    private static final int START_SCAN_FAILED_108 = 108;
    private static final int REACHED_THE_LIMIT_109 = 109;
    final DVBFinder mDVBFinder;

    DVBFinderHandler(DVBFinder dvbFinder) {
        super();
        mDVBFinder = dvbFinder;

    }

    @Override
    public boolean handleMessage(Message msg) {
        Handler finderHandler = mDVBFinder.getHandler();
        switch (msg.what) {
            case DISCONNECTING_101: {
                if (mDVBFinder.DVBFinder_Status != DISCONNECTING_101) {
                    return false;
                }

                BluetoothDevice device = (BluetoothDevice) msg.obj;
                BluetoothLeScanner bluetoothLeScanner = mDVBFinder.bluetoothLeScanner;

                if (bluetoothLeScanner != null) {
                    bluetoothLeScanner.stopScan(mDVBFinder.mScanCallbak);

                }

                mDVBFinder.sleep(500);
                mDVBFinder.mDVbObj.StartThread();
                mDVBFinder.Connect_Device(device);
                mDVBFinder.mDVbObj.SyncRestartWatchDog();
                return false;
            }
            case CONNECTED_102: {
                mDVBFinder.mDVbObj.SyncRestartWatchDog();
                mDVBFinder.sleep(200);
                boolean discover_start = mDVBFinder.bluetoothGatt != null && mDVBFinder.bluetoothGatt.discoverServices();
                if (!discover_start) {
                    Message message = finderHandler.obtainMessage(UNSUCCESSFUL_107);
                    finderHandler.sendMessage(message);
                }

                Log.i("DVBFinder", mDVBFinder.bluetoothGatt + " discoverService " + discover_start);
                return false;
            }
            case RELEASE_104: {
                if (mDVBFinder.DVBFinder_Status != CONNECTED_AND_CHARS_SET_103
                        && mDVBFinder.DVBFinder_Status != RELEASE_104
                        && mDVBFinder.DVBFinder_Status != UNSUCCESSFUL_107
                        && mDVBFinder.DVBFinder_Status != REACHED_THE_LIMIT_109) {
                    return false;
                }

                if (mDVBFinder.bluetoothGatt != null) {
                    mDVBFinder.bluetoothGatt.close();
                }

                mDVBFinder.bluetoothAdapter = null;
                mDVBFinder.bluetoothLeScanner=null;
                mDVBFinder.bluetoothGatt = null;
                mDVBFinder.blueGattService = null;
                mDVBFinder.gattCharacteristic = null;
                mDVBFinder.blueDevice2 = null;
                mDVBFinder.bluetoothManager = null;
                mDVBFinder.mDVbObj.StopThread();
                Log.i("DVBFinder", "mState is " + mDVBFinder.DVBFinder_Status);
                if (mDVBFinder.DVBFinder_Status == UNSUCCESSFUL_107) {
                    mDVBFinder.sleep(500);
                    mDVBFinder.StartConnecting();
                    return false;
                }

                if (mDVBFinder.DVBFinder_Status == REACHED_THE_LIMIT_109) {
                    mDVBFinder.DVBFinder_Status = RELEASE_104;
                    return false;
                }

                mDVBFinder.DVBFinder_Status = RELEASE_104;
                if (DVBFinder.iDVBFinder == null) {
                    return false;
                }

                DVBFinder.iDVBFinder.On_V8_Finder_Disconnect();
                return false;
            }
            case START_105: {
                if (mDVBFinder.bluetoothAdapter == null || mDVBFinder.bluetoothLeScanner==null) {
                    return false;
                }
                mDVBFinder.bluetoothLeScanner.startScan(mDVBFinder.mScanCallbak);
                if (mDVBFinder.DVBFinder_Status != START_SCAN_FAILED_108) {
                    mDVBFinder.DVBFinder_Status = SCAN_START_SUCCESSFULLY_100;
                    mDVBFinder.mDVbObj.StartThread();
                }
              /*  else {
                    mDVBFinder.DVBFinder_Status = START_SCAN_FAILED_108;
                    Message message = finderHandler.obtainMessage(START_SCAN_FAILED_108);
                    finderHandler.sendMessageDelayed(message, 500L);
                }*/

                mDVBFinder.z = 0;
                return false;
            }
            case 106: {
                if (mDVBFinder.bluetoothGatt == null) {
                    return false;
                }

                mDVBFinder.DVBFinder_Status = DISCONNECTING_101;
                return false;
            }
            case UNSUCCESSFUL_107: {
                if (mDVBFinder.DVBFinder_Status == RELEASE_104) {
                    return false;
                }

                mDVBFinder.mDVbObj.StopThread();
                mDVBFinder.sleep(500);
                Log.i("DVBFinder", "device rescan in " + mDVBFinder.DVBFinder_Status);
                mDVBFinder.EndDvbFinder();
                mDVBFinder.mDVbObj.StartThread();
                if (mDVBFinder.DVBFinder_Status <= DISCONNECTING_101) {
                    if (mDVBFinder.bluetoothGatt != null) {
                        mDVBFinder.bluetoothGatt.close();
                    }

                    mDVBFinder.sleep(500);
                    mDVBFinder.StartConnecting();
                    return false;
                }

                if (mDVBFinder.DVBFinder_Status == UNSUCCESSFUL_107) {
                    if (mDVBFinder.bluetoothGatt != null) {
                        mDVBFinder.bluetoothGatt.disconnect();
                    }

                    mDVBFinder.sleep(200);
                    if (mDVBFinder.bluetoothGatt != null) {
                        mDVBFinder.bluetoothGatt.close();
                    }

                    mDVBFinder.sleep(200);
                    mDVBFinder.StartConnecting();
                    return false;
                }

                mDVBFinder.DVBFinder_Status = UNSUCCESSFUL_107;
                return false;
            }
            case START_SCAN_FAILED_108: {
                if (mDVBFinder.DVBFinder_Status == START_SCAN_FAILED_108) {
                    mDVBFinder.mDVbObj.StopThread();
                    mDVBFinder.EndDvbFinder();
                    mDVBFinder.sleep(500);
                    mDVBFinder.StartConnecting();
                    return false;
                }

                return false;
            }
            case REACHED_THE_LIMIT_109: {
                mDVBFinder.EndDvbFinder();
                mDVBFinder.DVBFinder_Status = REACHED_THE_LIMIT_109;
                return false;
            }
            default: {
                return false;
            }
        }
    }
}

