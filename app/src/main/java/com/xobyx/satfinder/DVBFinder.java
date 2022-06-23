package com.xobyx.satfinder;

import android.bluetooth.*;
import android.bluetooth.le.*;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static android.bluetooth.BluetoothGatt.GATT_SUCCESS;
import static android.bluetooth.BluetoothGattCharacteristic.WRITE_TYPE_NO_RESPONSE;
import static android.bluetooth.BluetoothProfile.STATE_CONNECTED;
import static android.bluetooth.BluetoothProfile.STATE_DISCONNECTED;

public class DVBFinder {
    static iDVBFinder iDVBFinder;
    private static DVBFinder mInctance = null;
    private static boolean mReadye_to_write = false;
    public BluetoothManager bluetoothManager;
    public BluetoothAdapter bluetoothAdapter;
    public BluetoothDevice blueDevice1;
    public BluetoothDevice blueDevice2;
    public BluetoothGatt bluetoothGatt;
    public int DVBFinder_Status;

    public int TxPower;

    public ScanCallback mScanCallbak;
    private BlueConnectChangeReceiver mBlueConnectChangeReceiver;
    public int mDevice_rssi;
    public DVBScanReturn mDVbObj;
    private final Handler handler;
    private DVBBluetoothGattCallback dvbBluetoothGattCallback;
    private final Context mContext;
    public BluetoothGattService blueGattService;
    public BluetoothGattCharacteristic gattCharacteristic;
    public int z;
    public BluetoothLeScanner bluetoothLeScanner;

    private DVBFinder(Context context, iDVBFinder idvbfinder) {

        this.bluetoothManager = null;
        this.bluetoothAdapter = null;
        this.blueDevice1 = null;
        this.blueDevice2 = null;
        this.bluetoothGatt = null;
        this.blueGattService = null;
        this.gattCharacteristic = null;
        this.mBlueConnectChangeReceiver = null;
        this.dvbBluetoothGattCallback = null;
        this.mContext = context;
        DVBFinder.iDVBFinder = idvbfinder;
        this.DVBFinder_Status = DVB.RELEASE;
        this.mDVbObj = new DVBScanReturn(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED");
        if (this.mBlueConnectChangeReceiver == null) {
            this.mBlueConnectChangeReceiver = new BlueConnectChangeReceiver();
            this.mContext.registerReceiver(this.mBlueConnectChangeReceiver, intentFilter);
        }

        this.handler = new Handler(this.mContext.getMainLooper(), new DVBFinderHandler(this));
        this.mScanCallbak = new DVBLeScanCallback(this);
    }

    public Handler getHandler() {
        return handler;
    }

    public static DVBFinder newInstance(Context context, iDVBFinder iDVBFinder) {
        if (DVBFinder.mInctance == null) {
            DVBFinder.mInctance = new DVBFinder(context, iDVBFinder);
            return DVBFinder.mInctance;
        }

        DVBFinder.iDVBFinder = iDVBFinder;
        return DVBFinder.mInctance;
    }


    public void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException v3) {
            v3.printStackTrace();
        }
    }

    public void Connect_Device(BluetoothDevice device) {
        if (this.bluetoothAdapter != null && device != null) {
            Log.i("DVBFinder", "connectDvbFinder now");
            this.blueDevice2 = device;
            this.blueDevice1 = this.bluetoothAdapter.getRemoteDevice(device.getAddress());
            if (this.blueDevice1 == null) {
                return;
            }

            this.dvbBluetoothGattCallback = new DVBBluetoothGattCallback();
            this.bluetoothGatt = this.blueDevice1.connectGatt(this.mContext, false, this.dvbBluetoothGattCallback);
            Log.i("DVBFinder", "start connect Gatt " + this.bluetoothGatt);
        }
    }

    public void unregisterBlueConnectChangeReceiver() {
        if (mBlueConnectChangeReceiver != null) {
            mContext.unregisterReceiver(mBlueConnectChangeReceiver);
            mBlueConnectChangeReceiver = null;
        }
    }

    public void setNewBluetoothChars(byte[] mBluetoothGattCharacteristic) {
        Log.i("DVBFinder", "read " + this.bytesArrayToString(mBluetoothGattCharacteristic));
        if (iDVBFinder != null) {
            iDVBFinder.setBluetoothGattCharacteristic(mBluetoothGattCharacteristic);
        }
    }

    private String bytesArrayToString(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        if (bytes != null && bytes.length != 0) {

            for (byte aByte : bytes) {

                StringBuilder s = new StringBuilder(Integer.toHexString(aByte & 0xFF));
                while (s.length() < 2) {
                    s.insert(0, "0");
                }

                builder.append(s);
                builder.append(" ");
            }

            return builder.toString();
        }

        return "";
    }

    public void EndDvbFinder() {
        synchronized (this) {
            Log.i("DVBFinder", "release");
            if (this.bluetoothLeScanner != null && this.DVBFinder_Status == DVB.SCAN_START_SUCCESSFULLY) {
                this.bluetoothLeScanner.stopScan(this.mScanCallbak);
            }

            if (this.bluetoothGatt != null) {
                Log.i("DVBFinder", "disconnect " + this.bluetoothGatt);
                this.bluetoothGatt.disconnect();
            }

            this.mDVbObj.StopThread();
            if (this.DVBFinder_Status == DVB.FOUND_DEVICE) {
                Message message = this.handler.obtainMessage(DVB.RELEASE);
                this.handler.sendMessage(message);
            }

            this.DVBFinder_Status = DVB.RELEASE;
        }
    }

    public void StartConnecting() {
        synchronized (this) {
            if (bluetoothManager == null) {
                bluetoothManager = (BluetoothManager) this.mContext.getSystemService(Context.BLUETOOTH_SERVICE);
                if (this.bluetoothManager == null) {
                    Toast toast = Toast.makeText(this.mContext, R.string.strBTConnectFail, Toast.LENGTH_LONG);  // string:strBTConnectFail "Bluetooth connect fail"
                    toast.setGravity(17, 0, 0);
                    toast.show();
                }
            }

            if (this.bluetoothAdapter == null ||this.bluetoothLeScanner==null) {
                this.bluetoothAdapter = this.bluetoothManager.getAdapter();
                this.bluetoothLeScanner =this.bluetoothAdapter.getBluetoothLeScanner();

            }

            if (this.bluetoothAdapter == null ||this.bluetoothLeScanner==null) {
                Toast toast = Toast.makeText(this.mContext, R.string.strBTConnectFail, 0);  // string:strBTConnectFail "Bluetooth connect fail"
                toast.setGravity(17, 0, 0);
                toast.show();
                return;
            }

            if (this.bluetoothAdapter.isEnabled()) {
                ScanFilter m = new ScanFilter.Builder().setDeviceName("V8 Finder-BT03").build();
                ScanSettings build = new ScanSettings.Builder().setNumOfMatches(1).setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY).setCallbackType(ScanSettings.CALLBACK_TYPE_FIRST_MATCH).build();
                ArrayList<ScanFilter> filters = new ArrayList<>();
                filters.add(m);
                this.bluetoothLeScanner.startScan(filters,build, this.mScanCallbak);
                boolean startLeScan = this.DVBFinder_Status!=DVB.START_SCAN_FAILED;

                this.z = 0;
                if (startLeScan) {
                    Log.i("DVBFinder", "startLeScan");
                    this.DVBFinder_Status = DVB.SCAN_START_SUCCESSFULLY;
                    this.mDVbObj.StartThread();
                } //else {
                   // this.DVBFinder_Status = DVB.START_SCAN_FAILED_108;
                  //  Message message = this.handler.obtainMessage(DVB.START_SCAN_FAILED_108);
                  //  this.handler.sendMessageDelayed(message, 500L);
              //  }
            } else {
                this.bluetoothAdapter.enable();
                this.DVBFinder_Status = DVB.RELEASE;
            }

        }
    }
    public int getDeviceSignle()
    {
        bluetoothGatt.readRemoteRssi();
        return mDevice_rssi<=-100?0 :(mDevice_rssi>=-50)?100:2*(mDevice_rssi+100);

    }
    public void write(byte[] bytes) {
        if (this.gattCharacteristic != null && this.bluetoothGatt != null) {

            int i = 0;
            while (DVBFinder.mReadye_to_write) {
                this.sleep(100);
                ++i;
                if (i > 10) {
                    break;
                }
            }

            if (i > 10) {
                Log.i("DVBFinder", "write data timeout");
                DVBFinder.mReadye_to_write = false;
            } else {
                DVBFinder.mReadye_to_write = true;
            }

            this.gattCharacteristic.setWriteType(WRITE_TYPE_NO_RESPONSE);
            this.gattCharacteristic.setValue(bytes);
            this.bluetoothGatt.writeCharacteristic(this.gattCharacteristic);


        }
    }

    public interface iDVBFinder {
        void setBluetoothGattCharacteristic(byte[] arg1);

        void On_Bluetooth_connect_fail();

        void On_V8_Finder_Disconnect();

        void On_V8_Finder_Connected();
    }


    class DVBBluetoothGattCallback extends BluetoothGattCallback {


        @Override  // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicChanged(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic) {
            if (bluetoothAdapter != null && bluetoothGatt != null && bluetoothGattCharacteristic != null) {
                byte[] value = bluetoothGattCharacteristic.getValue();
                if ("0000ffe1-0000-1000-8000-00805f9b34fb".equalsIgnoreCase(bluetoothGattCharacteristic.getUuid().toString())) {
                    setNewBluetoothChars(value);
                }
            }
        }

        @Override  // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicRead(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic bluetoothGattCharacteristic, int status) {
            if (status == 0 && (bluetoothAdapter != null && bluetoothGatt != null && bluetoothGattCharacteristic != null)) {
                bluetoothGattCharacteristic.getValue();
                bluetoothGattCharacteristic.getUuid();
            }
        }

        @Override  // android.bluetooth.BluetoothGattCallback
        public void onCharacteristicWrite(BluetoothGatt bluetoothGatt, BluetoothGattCharacteristic gattCharacteristic, int status) {
            DVBFinder.mReadye_to_write = false;
            if (status == GATT_SUCCESS) {
                Log.i("DVBFinder", "write success");
                return;
            }

            Log.i("DVBFinder", "write fail " + status);
        }

        @Override  // android.bluetooth.BluetoothGattCallback
        public void onConnectionStateChange(BluetoothGatt bluetoothGatt, int status, int newState) {
            Log.i("DVBFinder", "gatt " + bluetoothGatt + " status " + status + " newState " + newState);
            if (DVBFinder.this.bluetoothGatt != bluetoothGatt) {
                return;
            }

            if (newState == STATE_CONNECTED && status == GATT_SUCCESS) {
                Log.i("DVBFinder", bluetoothGatt.getDevice().getName() + " connected status " + status + " DVBFinder Status " + DVBFinder_Status);
                if (DVBFinder_Status != DVB.FOUND_DEVICE) {
                    bluetoothGatt.close();
                    return;
                }

                DVBFinder_Status = DVB.CONNECTED;
                Message msg = handler.obtainMessage(DVB.CONNECTED);
                handler.sendMessage(msg);
                return;
            }

            if (newState == STATE_DISCONNECTED) {
                Log.i("DVBFinder", bluetoothGatt.getDevice().getName() + " disconnect state " + DVBFinder_Status);
                DVBFinder.mReadye_to_write = false;
                if (DVBFinder_Status == DVB.SCAN_START_SUCCESSFULLY) {
                    return;
                }

                if (DVBFinder_Status == DVB.FOUND_DEVICE || DVBFinder_Status == DVB.CONNECTED) {
                    DVBFinder_Status = DVB.UNSUCCESSFUL;
                }

                Message message = handler.obtainMessage(DVB.RELEASE);
                handler.sendMessage(message);
            }
        }

        @Override  // android.bluetooth.BluetoothGattCallback
        public void onReadRemoteRssi(BluetoothGatt bluetoothGatt, int rssi, int status) {
            if (status ==GATT_SUCCESS) {
                mDevice_rssi= rssi;
                return;
            }

            mDevice_rssi =200;
        }

        @Override  // android.bluetooth.BluetoothGattCallback
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            Log.i("DVBFinder", "onServicesDiscovered " + status + " mState " + DVBFinder_Status);
            if (gatt != bluetoothGatt) {
                return;
            }

            if (DVBFinder_Status != DVB.CONNECTED) {
                return;
            }

            if (status != GATT_SUCCESS) {
                Message message = handler.obtainMessage(DVB.UNSUCCESSFUL);
                handler.sendMessage(message);
                DVBFinder_Status = DVB.UNSUCCESSFUL;
                return;


            }
            List<BluetoothGattService> gattServiceList = gatt.getServices();
            Log.i("DVBFinder", "services size " + gattServiceList.size());
            if (gattServiceList.size() == 0) {
                Message message = handler.obtainMessage(DVB.UNSUCCESSFUL);
                handler.sendMessage(message);
                DVBFinder_Status = DVB.UNSUCCESSFUL;
                return;
            }


            for (BluetoothGattService gattService : gattServiceList) {
                Log.i("DVBFinder", "SERVICE UUID: " + gattService.getUuid().toString());
                if (!"0000ffe0-0000-1000-8000-00805f9b34fb".equalsIgnoreCase(gattService.getUuid().toString()))
                    continue;


                blueGattService = gattService;


                for (BluetoothGattCharacteristic characteristic : gattService.getCharacteristics()) {


                    Log.i("DVBFinder", "characteristic UUID: " + characteristic.getUuid().toString());
                    if (("0000ffe1-0000-1000-8000-00805f9b34fb".equalsIgnoreCase(characteristic.getUuid().toString())) && (characteristic.getProperties() & 16) != 0) {
                        Log.i("DVBFinder", "setNotificationForCharacteristic");
                        if (!gatt.setCharacteristicNotification(characteristic, true)) {
                            Log.i("DVBFinder", "Seting proper notification status for characteristic failed!");
                            Message message = handler.obtainMessage(DVB.UNSUCCESSFUL);
                            handler.sendMessage(message);
                            DVBFinder_Status = DVB.UNSUCCESSFUL;
                            return;
                        }

                        BluetoothGattDescriptor descriptor = characteristic.getDescriptor(UUID.fromString("00002902-0000-1000-8000-00805f9b34fb"));
                        if (descriptor != null) {
                            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
                            gatt.writeDescriptor(descriptor);
                        }
                    }

                    if (!"0000ffe1-0000-1000-8000-00805f9b34fb".equalsIgnoreCase(characteristic.getUuid().toString()))
                        continue;


                    gattCharacteristic= characteristic;
                    DVBFinder_Status = DVB.CONNECTED_AND_CHARS_SET;
                    mDVbObj.StopThread();

                    if (iDVBFinder == null) continue;


                    iDVBFinder.On_V8_Finder_Connected();

                }
                break;

            }
            if (DVBFinder_Status == DVB.CONNECTED_AND_CHARS_SET) return;

            Message message = handler.obtainMessage(DVB.UNSUCCESSFUL);
            handler.sendMessage(message);
            DVBFinder_Status = DVB.UNSUCCESSFUL;


        }
    }

    class BlueConnectChangeReceiver extends BroadcastReceiver {


        @Override  // android.content.BroadcastReceiver
        public void onReceive(Context context, Intent intent) {
            if ("android.bluetooth.adapter.action.CONNECTION_STATE_CHANGED".equals(intent.getAction())) {
                Log.i("DVBFinder", "bluetooth connect changed");
                if (bluetoothAdapter != null && (bluetoothAdapter.isEnabled())&&bluetoothLeScanner!=null) {

                    if (DVBFinder_Status == DVB.RELEASE) {
                        Message message = handler.obtainMessage(DVB.START);
                        handler.sendMessageDelayed(message, 200L);
                    }
                }
            }
        }
    }
}

