package com.xobyx.satfinder;


import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


import com.xobyx.satfinder.base.ChannelBase;

import java.util.*;

class MainActivityHandler implements Handler.Callback {

    final MainActivity mainActv;

    MainActivityHandler(MainActivity arg1) {
        super();
        this.mainActv = arg1;

    }


    HashMap<String,Integer> map= new HashMap<>();

    {
        map.put("SigStrength", 0);
        map.put("SigQuality", 0);
        map.put("ShortPower", 0);
        map.put("Power", 0);
        map.put("tv_cn", 0);
        map.put("ber", 0);
        map.put("dvbMode", 0);
        map.put("constellation", 0);
        map.put("codeRate", 0);
        map.put("rollOff", 0);
        map.put("pilot", 0);
        map.put("rssi", 0);
    }
    @Override  // android.os.Handler$Callback
    public boolean handleMessage(Message msg) {
        Log.i("MainActivity", "msg.what " + msg.what);
        byte[] msg_b =  msg.obj!=null ?(byte[]) msg.obj:null;
        switch(msg.what) {
            case MSG.CONNECTED_1: {
                TextView textView = (TextView)LayoutInflater.from(this.mainActv.getApplicationContext()).inflate(R.layout.simple_center, null);  // layout:simple_center
                textView.setTextSize(1, 20.0f);
                textView.setTextColor(this.mainActv.getColor(R.color.black));   // color:black
                textView.setText(R.string.strConnected );   // string:strConnected "V8 Finder Connected"
                textView.setBackgroundResource(R.drawable.round_bg);   // drawable:round_bg
                Toast toast = new Toast(mainActv);
                toast.setDuration(0);
                toast.setView(textView);
                toast.setGravity(17, 0, 0);
                toast.show();
                mainActv.get_blue_img().setImageResource(R.drawable.blue_connected);   // drawable:ic_bluetooth_connected
                mainActv.get_blue_img().connect=true;
                mainActv.get_blue_img().setRssi(mainActv.mDvbFinder.getDeviceSignle());
                this.mainActv.SendSelectedSatTpLNBConfig();

                for(int i = 0; i < 4; ++i) {
                    mainActv.getFieldStrengFrag().update_vertical_progress(i, 0);
                }

                this.mainActv.mHandler.postDelayed(mainActv::SendGetDeviceInfo, 1000L);
                mainActv.readNewChars.Start();
                return false;
            }
            case MSG.UPDATE_STRENGTH_QUALITY_UI_3: {
                if(mainActv.getCurrentFragment() == 0) {
                    mainActv.getCompass_Frag().set_signal_strength(this.mainActv.mSigStrength);
                    mainActv.getCompass_Frag().set_signal_quality(this.mainActv.mSigQuality);
                    mainActv.Update_Beep();
                    return false;
                }

                return false;
            }
            case MSG.POST_NEW_CHANNEL_BLIND_4: {
                if(mainActv.getCurrentFragment() == 0
                        && this.mainActv.mChnDialog != null
                        || mainActv.getCurrentFragment() == 2) {
                    ChannelBase channelBase = new ChannelBase();
                    channelBase.setChannelType(msg_b[msg_b.length - 1]);
                    byte[] bytes1 = Arrays.copyOfRange(msg_b, 0, msg_b.length - 1);
                    String decode_byteArray = this.mainActv.Decode_ByteArray(bytes1);
                    if(decode_byteArray != null) {
                        if(mainActv.getCurrentFragment() == 2) {
                            if(mainActv.IsWritingLogs()) {
                                System.out.print("blind update channels " + decode_byteArray +"\n");
                            }

                            mainActv.getScanFragment().AddChannel(new ChannelBase(decode_byteArray,msg_b[msg_b.length - 1]));
                            return false;
                        }

                        if(this.mainActv.mChnDialog != null) {
                            Log.i("MainActivity", "mChnAdapter count " + mainActv.st_getChannelAdapter().getCount());
                            if(mainActv.IsWritingLogs()) {
                                System.out.print("update channels " + decode_byteArray+"\n");
                            }

                            channelBase.setChannelName(decode_byteArray);
                            mainActv.st_getChannelAdapter().add(channelBase);
                            return false;
                        }
                    }
                }

                return false;
            }
            case MSG.SCAN_CHANNEL_FINISH_5: {
                if(this.mainActv.mChnDialog != null && mainActv.get_txt_channel_total() != null) {
                    mainActv.get_txt_channel_total().setText(Integer.toString(msg.arg1));
                    mainActv.get_progs_channel_progress().setVisibility(View.GONE);
                    mainActv.get_txt_channel_total().setVisibility(View.VISIBLE);
                    int tpId = this.mainActv.mSatellites.get(this.mainActv.mSatPos).mTransponders.get(this.mainActv.mTpPos).tpId;
                    mainActv.insert_channels_with_in_tp_id(tpId);
                    return false;
                }

                return false;
            }
            case MSG.UPDATE_FIELD_ACTIVE_FREQ_6: {

                if(msg.arg1 == mainActv.getFieldStrengFrag().mActive_Frequ) {
                    mainActv.getFieldStrengFrag().update_vertical_progress(msg.arg1, msg.arg2);
                    return false;
                }

                return false;
            }
            case MSG.SEND_ALL_CUR_PARAMETER_7: {
                this.mainActv.SendSelectedSatTpLNBConfig();  // native
                return false;
            }
            case MSG.UPDATE_ABOUT_8: {
                this.mainActv.Ha = mainActv.ByteArray_to_hexString( msg_b);
                mainActv.UpdateAboutDialog();
                return false;
            }
            case MSG.POST_DEVICE_PARAMS_9: {
                int a_sw3 = this.mainActv.ByteArray_to_Integer(msg_b, 0);
                int a_sw = this.mainActv.ByteArray_to_Short(msg_b, 4);
                byte a_sw1 = (msg_b)[6];
                byte a_sw2 = (msg_b)[7];
                byte a_BT = (msg_b)[8];
                byte[] bytes = new byte[4];
                System.arraycopy(msg_b, 9, bytes, 0, bytes.length);
                this.mainActv.BT_ID = "BT0" + ((int)a_BT) + ": V" + ((int) (msg_b)[13]) + "." + ((int) (msg_b)[14]);
                this.mainActv.SOFTWARE_ID = "SW: " + a_sw + ((int)a_sw1) + ((int)a_sw2) + "_" + a_sw3 + "_" + mainActv.ByteArray_to_hexString( bytes);
                mainActv.UpdateAboutDialog();
                if(this.mainActv.BT_ID.contains("1.1")) {
                    mainActv.getScanFragment().getView().setVisibility(View.GONE);
                    return false;
                }

                mainActv.getScanFragment().getView().setVisibility(View.VISIBLE);
                return false;
            }
            case MSG.START_SEARCH_DEVICE_10: {

                if(mainActv.mDvbFinder.DVBFinder_Status == DVB.RELEASE) {
                    this.mainActv.mDvbFinder.StartConnecting();
                    mainActv.get_blue_img().setImageResource(R.drawable.bt_anima);   // drawable:bt_anima
                    ((AnimationDrawable)mainActv.get_blue_img().getDrawable()).start();
                    mainActv.get_blue_img().connect=false;

                    return false;
                }

                return false;
            }
            case MSG.POST_SERIAL_NUMBER_13: {
                if(msg.obj != null) {

                    this.mainActv.SERILNUMBER = mainActv.ByteArray_to_String(msg_b);
                }

                mainActv.UpdateAboutDialog();
                return false;
            }
            case MSG.END_SESSION_14: {
                this.mainActv.mSigStrength = 0;
                this.mainActv.mSigQuality = 0;
                this.mainActv.mDvbFinder.EndDvbFinder();
                return false;
            }
            case MSG.SEND_TO_DEVICE_15: {
                mainActv.readNewChars.SyncZeroCnt();
                if(mainActv.IsWritingLogs()) {
                    System.out.print("Send to Device MainActiviyCallback (15) write " + mainActv.ByteArray_to_hexString( msg_b)+"\n");
                    Log.v("Send","MainActivityHandler 15 "+ mainActv.ByteArray_to_hexString( msg_b));
                }


                if(mainActv.mDvbFinder.DVBFinder_Status == DVB.CONNECTED_AND_CHARS_SET) {
                    this.mainActv.mDvbFinder.write(msg_b);
                    return false;
                }

                return false;
            }
            case MSG.DEVICE_DISCONNECT_2:
            case MSG.CONNECT_FAIL_16: {
                TextView textView = (TextView)LayoutInflater.from(this.mainActv.getApplicationContext()).inflate(R.layout.simple_center, null);  // layout:simple_center
                textView.setTextSize(1, 20.0f);
                textView.setTextColor(this.mainActv.getColor(R.color.black));   // color:black
                if(msg.what == 16) {
                    textView.setText(R.string.strBTConnectFail);   // string:strBTConnectFail "Bluetooth connect fail"
                }
                else {
                    textView.setText(R.string.strDisconnect);   // string:strDisconnect "V8 Finder Disconnect"
                }

                textView.setBackgroundResource(R.drawable.round_bg);   // drawable:round_bg
                Toast toast = new Toast(mainActv);
                toast.setDuration(0);
                toast.setView(textView);
                toast.setGravity(17, 0, 0);
                toast.show();
                mainActv.get_blue_img().setImageResource(R.drawable.blue_disconnected);   // drawable:ic_bluetooth_unconnected
                mainActv.get_blue_img().connect=false;

                mainActv.getCompass_Frag().set_signal_quality(0);
                mainActv.getCompass_Frag().set_signal_strength(0);
                mainActv.getCompass_Frag().set_tv_cn_val(0);
                mainActv.getCompass_Frag().set_tv_power_val(0);
                //mainActv.getCompass_Frag().e(0);
                if(mainActv.getScanFragment().isScanRunning()) {
                    mainActv.getScanFragment().stopScan();
                }

                this.mainActv.mSigQuality = 0;
                this.mainActv.mSigStrength = 0;
                mainActv.Update_Beep();
                mainActv.readNewChars.InterruptThread();
                this.mainActv.SERILNUMBER = null;
                this.mainActv.Ha = null;
                this.mainActv.BT_ID = null;
                this.mainActv.SOFTWARE_ID = null;
                if(mainActv.getCurrentFragment() == 2) {
                    mainActv.GetViewPager().setCurrentItem(0);
                    mainActv.setCurrentFragment( 0);
                }

                mainActv.getScanFragment().StopScan();
                mainActv.getScanFragment().getView().setVisibility(View.GONE);
                return false;
            }
            case MSG.ANTENNA_SHORT_17: {

                if(msg.arg1 == 0) {
                    mainActv.Show_Warning_dialog();  // string:strWarn "Warning"
                    return false;
                }

                if(this.mainActv.SWarning_Dialog != null) {
                    this.mainActv.SWarning_Dialog.dismiss();
                    return false;
                }

                return false;
            }
            case MSG.POST_SCAN_PROGRESS_20: {
                mainActv.getScanFragment().updateScanProgress(msg_b[1]);
                return false;
            }
            case MSG.POST_SCAN_TP_21: {
                int freq = this.mainActv.ByteArray_to_Short(msg_b, 3) * 1000;
                int symbolRate = this.mainActv.ByteArray_to_Short(msg_b, 5) * 1000;
                byte polar = msg_b[7];
                byte tp_strength = msg_b[8];
                byte quality_value = msg_b[9];
                Satellite satellite = this.mainActv.mSatellites.get(this.mainActv.mSatPos);
                Transponder transponder = new Transponder(satellite.satelite_id, -1, freq, symbolRate, polar,0);
                mainActv.getScanFragment().SetScanTransponder(transponder);
                mainActv.getScanFragment().UpdateUi(tp_strength, quality_value);
                return false;
            }
            case MSG.POST_SCAN_STOP_22: {
                mainActv.getScanFragment().UpdateScanButtonStop();
                return false;
            }
            case MSG.REQUEST_BT_ID_23: {
                if(mainActv.BT_ID == null) {
                    mainActv.SendGetDeviceInfo();
                    return false;
                }

                return false;
            }
            case MSG.POST_COMPS_PARAMS_24: { //send from native z  (-11)case 18
                if(mainActv.getCurrentFragment() == 0) {
                    byte SigStrength = msg_b[0];
                    byte SigQuality = msg_b[1];
                    short Power = (short) this.mainActv.ByteArray_to_Short(msg_b, 2);
                    int tv_cn = this.mainActv.ByteArray_to_Short(msg_b, 4);
                    int a_ber = this.mainActv.ByteArray_to_Integer(msg_b, 6);
                    byte a_dvbMode = msg_b[10];
                    byte a_constellation = msg_b[11];
                    byte acodeRate = msg_b[12];
                    byte a_rollOff = msg_b[13];
                    byte a_tpilot = msg_b[14];
                    this.mainActv.mSigStrength = SigStrength;
                    this.mainActv.mSigQuality = SigQuality;
                    mainActv.getCompass_Frag().set_signal_strength(this.mainActv.mSigStrength);
                    mainActv.getCompass_Frag().set_signal_quality(this.mainActv.mSigQuality);
                    //mainActv.getCompass_Frag().e(a_ber);
                    mainActv.getCompass_Frag().set_tv_power_val(Power);
                    mainActv.getCompass_Frag().set_tv_cn_val(tv_cn);
                    mainActv.Update_Beep();
                    mainActv.get_blue_img().connect=true;
                    mainActv.get_blue_img().setRssi(mainActv.mDvbFinder.getDeviceSignle());
                    Log.d("MainActivity", "stn:" + ((int)SigStrength) + "\tqt:" + ((int)SigQuality) + "\t power:" + this.mainActv.ByteArray_to_Short(msg_b, 2) + "\t short power:" + ((int)Power) + "\t cn:" + tv_cn + "\tber:" + a_ber);
                    Log.d("MainActivity", "dvbMode:" + ((int)a_dvbMode) + "\tconstellation:" + ((int)a_constellation) + "\t codeRate:" + ((int)acodeRate) + "\t rollOff:" + ((int)a_rollOff) + "\tpilot:" + ((int)a_tpilot));

                    map.put("SigStrength",((int)SigStrength));
                    map.put("SigQuality",((int)SigQuality));
                    map.put("ShortPower",this.mainActv.ByteArray_to_Short(msg_b,2));
                    map.put( "Power",((int)Power));
                    map.put("tv_cn",tv_cn );
                    map.put("ber",a_ber);
                    map.put("dvbMode",((int)a_dvbMode));
                    map.put("constellation",((int)a_constellation));
                    map.put("codeRate",((int)acodeRate));
                    map.put("rollOff",((int)a_rollOff));
                    map.put("pilot",((int)a_tpilot));
                    map.put("rssi",mainActv.mDvbFinder.getDeviceSignle());

                    mainActv.getCompass_Frag().WriteDebugInfo(map);
                    return false;
                }

                return false;
            }
            default: {
                return false;
            }
        }
    }
}

