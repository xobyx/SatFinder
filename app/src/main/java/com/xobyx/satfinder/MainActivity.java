package com.xobyx.satfinder;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.drawable.AnimationDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.PreferenceManager;
import androidx.viewpager.widget.ViewPager;
import com.xobyx.satfinder.adapters.ChannelAdapter;
import com.xobyx.satfinder.base.ChannelBase;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import static android.os.PowerManager.ON_AFTER_RELEASE;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;
import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;


public class MainActivity extends FragmentActivity implements DialogInterface.OnDismissListener, DialogInterface.OnShowListener, LocationListener, View.OnClickListener, AdapterView.OnItemSelectedListener, DVBFinder.iDVBFinder, DBManager.onDBChange {

    public static Context rContext;

    static {
        System.loadLibrary("xobyx-jni");
    }

    public final ReadNewChars readNewChars;
    private final boolean isWriteLog;
    public float mxAzimuth;
    public float mxElevation;
    public AlertDialog GPSReqDialog;
    public float mxSkew;
    public AlertDialog about_update_dialog;
    public AlertDialog SWarning_Dialog;
    public int lstrength;
    public int lquality;
    public String SERILNUMBER;
    public String Ha;
    public String BT_ID;
    public String SOFTWARE_ID;
    public DBManager simpleDb;
    public SharedPreferences mpref;
    public AlertDialog mChnDialog;
    public DVBFinder mDvbFinder;
    public Handler mHandler;
    public int mLNB_22k;
    public int mLNB_Disq;
    public int mLNB_Freq;
    public int mSatPos;
    public List<Satellite> mSatellites;
    public int mSigQuality;
    public int mSigStrength;
    public int mTpPos;
    public int lang_Select;
    public int beep_Select;
    public int st_upper_select;
    public int st_down_Select;
    public int qt_upper_select;
    public int qt_down_Select;
    SatPagerAdapter SatPager;
    Location LastLocation;
    private FieldStrengthFragment fieldStrengthFragment;
    private ScanFragment scanFragment;
    private DVBBeeper mBeeper;
    private Spinner spin_lnb_config_freq;
    private Spinner spin_lnb_conf_22k;
    private Spinner main_lnb_diseqc_spinner;
    private ArrayAdapter<String> lnbFreqAdapter;
    private ArrayAdapter<String> lnb22KswitchAdapter;
    private ArrayAdapter<String> lnbDiseqcAdapter;
    private long LastClickMillis;
    private SearchableSpinner satellite_spinner;
    private PowerManager.WakeLock wakeLock;
    private ImageView img_lnb_config;
    private SatSpinnerAdapter sat_spinner_adp;
    private SearchableSpinner tp_spinner;
    private TPAdapter transponderAdapt;
    private TextView txt_antenna_value;
    private View view_transponder_select_list;
    private PrintStream OutputWrite;
    private boolean isArabic;
    private LocationManager locationManager;
    private Location location;
    private Spinner set_lang_spn;
    private Spinner set_beep_spn;
    private Spinner set_st_upper_spn;
    private Spinner set_st_down_spn;
    private DrawerLayout DrawerLayout;
    private Spinner set_qt_upper_spn;
    private LinearLayout drawerView;
    private Spinner set_qt_down_spn;
    private ChannelAdapter channelAdapter;
    private ViewPager mViewPager;
    private ProgressBar progs_channel_progress;
    private ArrayList<Fragment> mFragmentList;
    private TextView txt_channel_total;
    private int CurrentFragment;
    private TextView img_toobar_setting;
    private Locale DefLocale;
    private Button img_toolbar_channel;
    private Button img_toolbar_satellite;
    private MoveImage moveImage_toolbar_bluetooth;

    private CompassFragment compassFragment;
    private SharedPreferences mpref2;

    public MainActivity() {
        this.mFragmentList = new ArrayList<>();

        this.mxAzimuth = -1.0f;
        this.mxElevation = -1.0f;
        this.mxSkew = -1.0f;
        this.mSigStrength = 0;
        this.mSigQuality = 0;
        this.lstrength = -1;
        this.lquality = -1;
        this.wakeLock = null;
        this.isWriteLog = true;
        this.readNewChars = new ReadNewChars(this);
        //this.mViewPagerAdpter = new SatPagerAdapter(getSupportFragmentManager(),mViewPager,mFragmentList);
        this.mHandler = new Handler(new MainActivityHandler(this));

    }

    public static byte[] Integer_to_BytesArray(int i) {
        return new byte[]{((byte) (i & 0xFF)), ((byte) (i >>> 8 & 0xFF)), ((byte) (i >>> 16 & 0xFF)), ((byte) (i >>> 24 & 0xFF))};
    }

    public static String Btohex(byte[] bytes) {
        StringBuilder builder1 = new StringBuilder();
        if (bytes != null && bytes.length != 0) {

            for (byte aByte : bytes) {
                StringBuilder builder = new StringBuilder(Integer.toHexString(aByte & 0xFF));
                while (builder.length() < 2) {
                    builder.insert(0, "0");
                }

                builder1.append(builder);
            }

            return builder1.toString();
        }

        return "";
    }

    public static Context getContext() {
        return MainActivity.rContext;
    }

    public Button Get_img_toolbar_satellite() {
        return img_toolbar_satellite;
    }

    CompassFragment getCompass_Frag() {
        return compassFragment;
    }

    public ViewPager GetViewPager() {
        return mViewPager;
    }

    public boolean IsWritingLogs() {
        return isWriteLog;
    }

    public ChannelAdapter st_getChannelAdapter() {
        return channelAdapter;
    }

    public TextView get_txt_channel_total() {
        return txt_channel_total;
    }

    public ProgressBar get_progs_channel_progress() {
        return progs_channel_progress;
    }

    public ScanFragment getScanFragment() {
        return scanFragment;
    }

    public Spinner getTP_spinner() {

        return tp_spinner;
    }

    public Spinner getSatellite_spinner() {
        return satellite_spinner;
    }

    public FieldStrengthFragment getFieldStrengFrag() {
        return fieldStrengthFragment;
    }

    public View transponder_select() {
        return view_transponder_select_list;
    }

    public MoveImage get_blue_img() {
        return moveImage_toolbar_bluetooth;
    }

    public int getCurrentFragment() {
        return CurrentFragment;
    }

    public void setCurrentFragment(int i) {
        CurrentFragment = i;
    }

    public Button Get_img_toolbar_channel() {
        return img_toolbar_channel;
    }

    private void Start_VBeeping() {
        if (this.mBeeper != null) {
            return;
        }

        this.mBeeper = new DVBBeeper(this);
        if (this.beep_Select != 0) {
            this.mBeeper.start_sound(0, 2500);
        }
    }

    private void Setup_Sat_TP_spinner() {
        this.satellite_spinner = this.findViewById(R.id.satellite_spinner);   // id:satellite_spinner
        satellite_spinner.setTitle(getString(R.string.strSelectsat));

        this.sat_spinner_adp = new SatSpinnerAdapter(this, R.layout.sat_spinner_item, R.id.sat_spinner_name_text, this.mSatellites);  // layout:sat_spinner_item

        this.satellite_spinner.setAdapter(this.sat_spinner_adp);
        this.satellite_spinner.setOnItemSelectedListener(this);
        this.satellite_spinner.setSelection(this.mSatPos);
        this.tp_spinner = this.findViewById(R.id.tp_spinner);   // id:tp_spinner
        this.tp_spinner.setTitle(getString(R.string.strSelect_tp));
        this.tp_spinner.setBasicDialogAdapter(true);
        List<Transponder> mTranzpoder = this.mSatellites.get(this.mSatPos).mTransponders;
        Log.e("MainActivity", "sateName:" + this.mSatellites.get(this.mSatPos).name);
        if (mTranzpoder == null) {
            mTranzpoder = new ArrayList<>();
        }

        this.tp_spinner.setEnabled(mTranzpoder.size() != 0);
        this.transponderAdapt = new TPAdapter(this, R.layout.simple_tp, mTranzpoder);  // layout:simple_tp
        //this.transponderAdapt.setDropDownViewResource(R.layout.simple_center);   // layout:simple_center
        this.tp_spinner.setAdapter(this.transponderAdapt);
        this.tp_spinner.setOnItemSelectedListener(this);
        this.tp_spinner.setSelection(this.mTpPos);
        this.view_transponder_select_list = this.findViewById(R.id.transponder_select_list);   // id:transponder_select_list
    }

    private void Setup_Fragments() {
        if (this.mFragmentList.size() != 0) {
            this.mFragmentList.clear();
        }

        this.mFragmentList.add(new CompassFragment());
        this.mFragmentList.add(new FieldStrengthFragment());
        this.mFragmentList.add(new ScanFragment());
        this.mViewPager = this.findViewById(R.id.main_page_viewer);   // id:main_page_viewer

        this.mViewPager.setCurrentItem(0);
        this.CurrentFragment = 0;
        this.img_toobar_setting = this.findViewById(R.id.toolbar_setting);   // id:toolbar_setting
        this.img_toolbar_channel = this.findViewById(R.id.toolbar_channel);   // id:toolbar_channel
        this.img_toolbar_satellite = this.findViewById(R.id.toolbar_satellite);   // id:toolbar_satellite
        this.moveImage_toolbar_bluetooth = this.findViewById(R.id.toolbar_bluetooth);   // id:toolbar_bluetooth
        // this.img_toolbar_logo = this.findViewById(R.id.toolbar_logo);   // id:toolbar_logo
        this.img_toobar_setting.setOnClickListener(this);
        this.img_toolbar_channel.setOnClickListener(this);
        this.img_toolbar_satellite.setOnClickListener(this);
        this.moveImage_toolbar_bluetooth.setOnClickListener(this);
        this.moveImage_toolbar_bluetooth.setSharedPreference(this.mpref);
        this.moveImage_toolbar_bluetooth.setRtl(this.isArabic);
        this.moveImage_toolbar_bluetooth.reDraw();
        // this.img_toolbar_logo.setOnClickListener(this);
        String[] mArrayLnbFreq = this.getResources().getStringArray(R.array.arrayLnbFreq);   // array:arrayLnbFreq
        this.txt_antenna_value = this.findViewById(R.id.antenna_value);   // id:antenna_value
        this.txt_antenna_value.setText(mArrayLnbFreq[this.mLNB_Freq]);
        this.txt_antenna_value.setOnClickListener(this);

        SatPager = new SatPagerAdapter(this, getSupportFragmentManager(), mViewPager, mFragmentList);
        this.Setup_Sat_TP_spinner();
    }

    public void SendGetDeviceInfo() {
        byte[] bytes = new byte[5];
        bytes[0] = -28;
        bytes[1] = 7;
        bytes[2] = -1;
        bytes[3] = 1;
        bytes[4] = CheckSum(bytes, 4);
        Message message = this.mHandler.obtainMessage(MSG.SEND_TO_DEVICE_15, bytes);
        this.mHandler.sendMessage(message);
    }

    private void ResetAllValues() {
        this.mSigQuality = 0;
        this.mSigStrength = 0;
        this.compassFragment.set_signal_quality(this.mSigQuality);
        this.compassFragment.set_signal_strength(this.mSigStrength);

        this.compassFragment.set_tv_power_val(0);
        this.compassFragment.set_tv_cn_val(0);
        this.Update_Beep();
    }

    private void Show_LNB_ConfigDialog() {
        this.GPSReqDialog = new AlertDialog.Builder(this).create();
        View lnb_config_header_layout = LayoutInflater.from(this).inflate(R.layout.lnb_config_header, null);  // layout:lnb_config_header_layout
        TextView lnb_config_header_text = lnb_config_header_layout.findViewById(R.id.lnb_config_title);   // id:lnb_config_title
        ImageView imageView = lnb_config_header_layout.findViewById(R.id.lnb_config_icon);   // id:lnb_config_icon
        FrameLayout.LayoutParams header_textLayoutParams = (FrameLayout.LayoutParams) lnb_config_header_text.getLayoutParams();
        FrameLayout.LayoutParams imageViewLayoutParams = (FrameLayout.LayoutParams) imageView.getLayoutParams();
        if (this.isArabic) {
            header_textLayoutParams.gravity = 21;
            imageViewLayoutParams.gravity = 19;
        } else {
            header_textLayoutParams.gravity = 19;
            imageViewLayoutParams.gravity = 21;
        }

        lnb_config_header_text.setLayoutParams(header_textLayoutParams);
        imageView.setLayoutParams(imageViewLayoutParams);
        this.GPSReqDialog.setCustomTitle(lnb_config_header_layout);
        this.img_lnb_config = lnb_config_header_layout.findViewById(R.id.lnb_config_icon);   // id:lnb_config_icon
        this.img_lnb_config.setOnClickListener(this);
        View lnb_config_layout = LayoutInflater.from(this).inflate(R.layout.lnb_config, null);  // layout:lnb_config
        this.GPSReqDialog.setView(lnb_config_layout);
        ArrayList<String> LnbFreqs = new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.arrayLnbFreq)));

        this.lnbFreqAdapter = new ArrayAdapter<>(this, R.layout.simple_center, LnbFreqs);  // layout:simple_center
        this.spin_lnb_config_freq = lnb_config_layout.findViewById(R.id.lnb_freq_spinner);   // id:lnb_freq_spinner
        this.spin_lnb_config_freq.setAdapter(this.lnbFreqAdapter);
        this.spin_lnb_config_freq.setSelection(this.mLNB_Freq);
        this.spin_lnb_config_freq.setOnItemSelectedListener(this);

        this.GPSReqDialog.show();
        this.GPSReqDialog.setOnDismissListener(this);


        ArrayList<String> list = new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.arraySwitch)));
        this.lnb22KswitchAdapter = new ArrayAdapter<>(this, R.layout.simple_center, list);  // layout:simple_center
        this.spin_lnb_conf_22k = lnb_config_layout.findViewById(R.id.lnb_22k_spinner);   // id:lnb_22k_spinner
        if (this.mLNB_Freq < 3) {
            this.mLNB_22k = 2;
            this.spin_lnb_conf_22k.setEnabled(false);
            if (this.lnb22KswitchAdapter.getCount() < 3) {
                this.lnb22KswitchAdapter.add("Auto");
            }
        } else {
            if (this.mLNB_22k > 1) {
                this.mLNB_22k = 0;
            }

            this.spin_lnb_conf_22k.setEnabled(true);
            if (this.lnb22KswitchAdapter.getCount() > 2) {
                this.lnb22KswitchAdapter.remove("Auto");
            }
        }

        this.spin_lnb_conf_22k.setAdapter(this.lnb22KswitchAdapter);
        this.spin_lnb_conf_22k.setSelection(this.mLNB_22k);
        this.spin_lnb_conf_22k.setOnItemSelectedListener(this);
        ArrayList<String> stringArray = new ArrayList<>(Arrays.asList(this.getResources().getStringArray(R.array.arrayDiseqc)));
        this.lnbDiseqcAdapter = new ArrayAdapter<>(this, R.layout.simple_center, stringArray);  // layout:simple_center
        this.main_lnb_diseqc_spinner = lnb_config_layout.findViewById(R.id.lnb_diseqc_spinner);   // id:lnb_diseqc_spinner
        this.main_lnb_diseqc_spinner.setAdapter(this.lnbDiseqcAdapter);
        this.main_lnb_diseqc_spinner.setSelection(this.mLNB_Disq);
        this.main_lnb_diseqc_spinner.setOnItemSelectedListener(this);
        this.GPSReqDialog.show();
        this.GPSReqDialog.setOnDismissListener(this);


    }

    public void UpdateAboutDialog() {
        if (this.about_update_dialog == null) {
            return;
        }

        TextView textView = this.about_update_dialog.findViewById(R.id.about_sn);   // id:about_sn
        TextView textView1 = this.about_update_dialog.findViewById(R.id.about_chipid);   // id:about_chipid
        TextView textView2 = this.about_update_dialog.findViewById(R.id.about_hwversion);   // id:about_hwversion
        TextView textView3 = this.about_update_dialog.findViewById(R.id.about_swversion);   // id:about_swversion
        if (this.SERILNUMBER == null) {
            textView.setText("SN: XXXXXXXXXXXX");
        } else {
            textView.setText("SN: " + this.SERILNUMBER);
        }

        if (this.BT_ID == null) {
            textView2.setText("BT：XXXXXXXX");
        } else {
            textView2.setText(this.BT_ID);
        }

        if (this.SOFTWARE_ID == null) {
            textView3.setText("SW：XXXXXXXX");
            return;
        }

        textView3.setText(this.SOFTWARE_ID);
    }

    public Location getSafeLocation()
    {

        return this.location != null? this.location:(LastLocation != null)?LastLocation:new Location("s");

    }

    private void calcSatReceiveArms() {
        Location location = getSafeLocation();
        double longitude =location.getLongitude();
        double latitude =location.getLatitude();
        if (longitude==0||latitude==0) {
           return;

        }

        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);

        Satellite sat = this.mSatellites.get(this.mSatPos);
        double dir = ((double) (sat.position / 10.0f)) - longitude;
        this.mxAzimuth = Float.parseFloat(new DecimalFormat("#.00")
                .format(latitude <= 0.0
                        ? 0.0
                        : 180.0 - Math.toDegrees(
                                Math.atan(Math.tan(Math.toRadians(dir)) / Math.sin(Math.toRadians(latitude))))));
        double v = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(latitude));
        this.mxElevation = Float.parseFloat(new DecimalFormat("#.00").format(Math.toDegrees(Math.atan((v - 0.15) / Math.sqrt(1.0 - Math.pow(v, 2.0))))));
        this.mxSkew = Float.parseFloat(new DecimalFormat("#.00").format(Math.toDegrees(Math.atan(Math.sin(Math.toRadians(dir)) / Math.tan(Math.toRadians(latitude))))));
        Locale.setDefault(locale);
        // this.mCompassFragment.view_compass.setAzimuth(this.mxAzimuth);
        //this.mCompassFragment.view_compass.setElevation(this.mxElevation);
        // this.mCompassFragment.view_compass.setSkew(this.mxSkew);
        // this.mCompassFragment.view_compass.setLocation(longitude,latitude);
        // this.mCompassFragment.view_compass.setSatPosition(sat.position);
        compassFragment.view_compass.setSatAngle(this.mxAzimuth);
        compassFragment.view_compass.setSkew(this.mxSkew);
        compassFragment.view_compass.setmElevation(this.mxElevation);
    }

    public void Update_Beep() {
        if (this.mBeeper == null) {
            return;
        }

        if (this.beep_Select == 0) {
            this.mBeeper.stop_sound();
            return;
        }

        if (this.mSigQuality > 0) {
            if (Math.abs(this.lquality - this.mSigQuality) < 2) {
                this.lquality = this.mSigQuality;
                this.lstrength = 150;
                return;
            }
        } else if (Math.abs(this.lstrength - this.mSigStrength) < 2) {
            this.lquality = 150;
            this.lstrength = this.mSigStrength;
            return;
        }

        this.lstrength = this.mSigStrength;
        this.lquality = this.mSigQuality;
        int i = 0;
        int delay = 3000;
        if (this.mSigQuality > this.qt_down_Select * 5) {
            delay = 600 - this.mSigQuality <= this.qt_upper_select * 5 ? this.mSigQuality - this.qt_down_Select * 5 : (this.qt_upper_select - this.qt_down_Select) * 5 * 450 / ((this.qt_upper_select - this.qt_down_Select) * 5);
            i = 1;
        } else {
            if (this.mSigStrength > this.st_down_Select * 5) {
                delay = 3000 - this.mSigStrength <= this.st_upper_select * 5 ? this.mSigStrength - this.st_down_Select * 5 : (this.st_upper_select - this.st_down_Select) * 5 * 2500 / ((this.st_upper_select - this.st_down_Select) * 5);
            } else if (this.mBeeper.getSleepTime() == 3000) {
                return;
            }
        }

        Log.i("MainActivity", "delay " + delay);
        this.mBeeper.stop_sound();
        this.mBeeper.start_sound(i, delay);
    }

    private void SavePreferences() {

        SharedPreferences.Editor editor = this.mpref.edit();
        if (this.location != null) {
            editor.putString("lat", String.valueOf(location.getLatitude()));
            editor.putString("lon", String.valueOf(location.getLongitude()));
            editor.apply();
        }

        if (this.mSatPos < 0 || this.mSatPos >= this.mSatellites.size()) {
            this.mSatPos = 0;
            editor.putInt("sat_pos", this.mSatPos);
            editor.apply();
        }

        Satellite satellite = this.mSatellites.get(this.mSatPos);
        satellite.mTransponders = this.simpleDb.GetTransponderList(satellite.satelite_id);
        List<Transponder> transponders = this.mSatellites.get(this.mSatPos).mTransponders;
        if (transponders != null && this.mTpPos >= transponders.size()) {
            this.mTpPos = 0;
            editor.putInt("tp_pos", this.mTpPos);
            editor.commit();
        }

    }

    public void Show_Warning_dialog() {
        if (this.SWarning_Dialog != null) {
            return;
        }

        this.SWarning_Dialog = new AlertDialog.Builder(this).setTitle(R.string.strWarn).setMessage(R.string.strAntennaShort).setNegativeButton(R.string.strClose, (dialog, which) -> dialog.dismiss()).create();  // string:strClose "Close"
        this.SWarning_Dialog.setOnDismissListener(dialog -> SWarning_Dialog = null);
        this.SWarning_Dialog.show();
    }

    private void UpdateLoc(Location location) {
        Locale locale = Locale.getDefault();
        Locale.setDefault(Locale.US);
        DecimalFormat format = new DecimalFormat("#.000");
        String Longitude = location.getLongitude() < 0.0 ? format.format(location.getLongitude()) + " " + this.getString(R.string.strW) : format.format(location.getLongitude()) + " " + this.getString(R.string.strE);  // string:strW "W"
        String Latitude = location.getLatitude() < 0.0 ? format.format(location.getLatitude()) + " " + this.getString(R.string.strS) : format.format(location.getLatitude()) + " " + this.getString(R.string.strN);  // string:strS "S"
        this.compassFragment.setLocation_txt(Latitude + " " + Longitude);
        Locale.setDefault(locale);
        this.calcSatReceiveArms();
    }

    private void UpdateLocOffline() {

        String lat = this.mpref.getString("lat", "");
        String lon = this.mpref.getString("lon", "");
        if (lon != null && lat != null && !lat.isEmpty() && !lon.isEmpty()) {
            double longitude_d = Double.parseDouble(lon);
            double latitude_d = Double.parseDouble(lat);
            LastLocation = new Location("am");
            LastLocation.setLatitude(longitude_d);
            LastLocation.setLongitude(longitude_d);

            DecimalFormat format = new DecimalFormat("#.000");
            String Longitude = longitude_d < 0.0 ? format.format(longitude_d) + " " + this.getString(R.string.strW) : format.format(longitude_d) + " " + this.getString(R.string.strE);  // string:strW "W"
            String Latitude = latitude_d < 0.0 ? format.format(latitude_d) + " " + this.getString(R.string.strS) : format.format(latitude_d) + " " + this.getString(R.string.strN);  // string:strS "S"
            this.compassFragment.setLocation_txt(Latitude + " " + Longitude + " Last Location");

            this.calcSatReceiveArms();
        }
    }

    public int ByteArray_to_Short(byte[] bytes, int index) {
        return bytes[index + 1] & 0xFF | (bytes[index] & 0xFF) << 8;
    }

    // zip File check


    @Override
    public void setBluetoothGattCharacteristic(byte[] bytes) {
        if (bytes.length < 2) {
            return;
        }


        int i = bytes.length - 1;
        if (this.CheckSum(bytes, i) != bytes[i]) {
            return;
        }

        if (this.isWriteLog) {
            System.out.print("setBluetoothGattCharacteristic command valid [" + bytes[0] + "] " + this.BytesArrayToHexStr(bytes) + "\n");
            //System.out.print("command valid\n");
            Log.v("Receive From BLe", String.format("command : [%s] from Device msg : %s", bytes[0], BytesArrayToHexStr(bytes)));
        }

        this.readNewChars.SyncZeroCnt();
        switch (bytes[0]) {
            case -19: {
                if (this.scanFragment.isScanRunning()) {
                    Message message = this.mHandler.obtainMessage(MSG.POST_SCAN_PROGRESS_20, bytes);
                    this.mHandler.sendMessage(message);
                }

                return;
            }
            case -18: {
                if (this.scanFragment.isScanRunning()) {
                    Message message = this.mHandler.obtainMessage(MSG.POST_SCAN_TP_21, bytes);
                    this.mHandler.sendMessage(message);
                    return;
                }

                return;
            }
            case -17: {
                if (this.scanFragment.isScanRunning()) {
                    Message message = this.mHandler.obtainMessage(MSG.POST_SCAN_STOP_22, bytes);
                    this.mHandler.sendMessage(message);
                    return;
                }

                return;
            }
            default: {
                this.ProgressRecivedMessage(bytes);
            }
        }
    }

    public int ByteArray_to_Integer(byte[] bytes, int i) {
        return bytes[i + 3] & 0xFF | ((bytes[i] & 0xFF) << 24 | (bytes[i + 1] & 0xFF) << 16 | (bytes[i + 2] & 0xFF) << 8);
    }

    public String Decode_ByteArray(byte[] bytes) {
        String[] encodes = new String[21];
        int i1 = 0;
        encodes[0] = null;
        encodes[1] = "ISO-8859-5";
        encodes[2] = "ISO-8859-6";
        encodes[3] = "ISO-8859-7";
        encodes[4] = "ISO-8859-8";
        encodes[5] = "ISO-8859-9";
        encodes[6] = "ISO-8859-10";
        encodes[7] = "ISO-8859-11";
        encodes[8] = null;
        encodes[9] = "ISO-8859-13";
        encodes[10] = "ISO-8859-14";
        encodes[11] = "ISO-8859-15";
        encodes[12] = null;
        encodes[13] = null;
        encodes[14] = null;
        encodes[15] = null;
        encodes[16] = null;
        encodes[17] = null;
        encodes[18] = "EUC-KR";
        encodes[19] = "GB2312";
        encodes[20] = "Big5";
        String[] Iso = {null, "ISO-8859-1", "ISO-8859-2", "ISO-8859-3", "ISO-8859-4", "ISO-8859-5", "ISO-8859-6", "ISO-8859-7", "ISO-8859-8", "ISO-8859-9", "ISO-8859-10", "ISO-8859-11", null, "ISO-8859-13", "ISO-8859-14", "ISO-8859-15"};
        int i = bytes[0] & 0xFF;
        try {
            if (i <= 0x20) {
                if (i == 16 && bytes.length >= 3) {
                    byte[] array_cpy = Arrays.copyOfRange(bytes, 3, bytes.length);
                    int i2 = bytes[2] & 0xFF;
                    if (i2 > 15) {
                        i2 = 0;
                    }

                    return new String(array_cpy, Iso[i2]);
                }

                byte[] bytes1 = Arrays.copyOfRange(bytes, 1, bytes.length);
                if (i <= 20) {
                    i1 = i;
                }

                return new String(bytes1, encodes[i1]);
            }


            if ((this.mSatellites.get(this.mSatPos).name.equals("ChinaSat 6B")) && bytes.length >= 2) {
                int b = (bytes[0] & 0xFF) << 8 | bytes[1] & 0xFF;
                return b <= 0xB0A1 || b >= 0xF7FE ? new String(bytes, StandardCharsets.UTF_8) : new String(bytes, "GBK");
            }

            return new String(bytes, StandardCharsets.UTF_8);


        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        //
        return null;
    }

    /**
     *  send <p>
     *  [<i>0</i>] : L740<p>
     *  [<i>1</i>] : H740<p>
     *  [<i>2</i>] : Lo[tr.mFrequency/1000]  {@link Transponder#mFrequency}/1000 <p>
     *  [<i>3</i>] : Hi[tr.mFrequency/1000]  {@link Transponder#mFrequency}/1000 <p>
     *  [<i>4</i>] : Lo[tr.mSymbolRate/1000] {@link Transponder#mSymbolRate}/1000 <p>
     *  [<i>5</i>] : Hi[tr.mSymbolRate/1000] {@link Transponder#mSymbolRate}/1000<p>
     *  [<i>6</i>] : tr.mPolization {@link Transponder#mPolization} <p>
     *  [<i>7</i>] : checksum <p>
     */

    public native void SendSelectedSatTp();

    public String ByteArray_to_hexString(byte[] bytes) {
        StringBuilder stringBuilder = new StringBuilder();
        if (bytes != null && bytes.length != 0) {

            for (byte aByte : bytes) {
                StringBuilder builder = new StringBuilder(Integer.toHexString(aByte & 0xFF));
                while (builder.length() < 2) {
                    builder.insert(0, "0");
                }

                stringBuilder.append(builder);
            }

            return stringBuilder.toString();
        }

        return "";
    }

    private void Beep_change(int index) {
        if (index == this.beep_Select) {
            return;
        }

        this.beep_Select = index;
        SharedPreferences.Editor editor = this.mpref.edit();
        editor.putInt("beep", this.beep_Select);
        editor.apply();
        this.lquality = -1;
        this.Update_Beep();
    }

    byte CheckSum(byte[] bytes, int count) {
        int i = 0;
        byte b = 0;
        while (i < count) {
            b = (byte) (b ^ bytes[i]);
            ++i;
        }

        return b;
    }

    /**
     *  Send message <i>15</i> Lnb Config array[9]<p>
     *     <i>484</i> <p>
     *     Lo[mLNB_Freq1] {@link #mLNB_Freq}1 Low<p>
     *     Hi[mLNB_Freq1] {@link #mLNB_Freq}1 High<p>
     *     Lo[mLNB_Freq2] {@link #mLNB_Freq}2 Low<p>
     *     Hi[mLNB_Freq2] {@link #mLNB_Freq}2 High<p>
     *     mLNB_22k {@link #mLNB_22k}<p>
     *     checksum {@link #CheckSum(byte[], int)}<p>
     */

    public native void SendLNBConfig();

    private String BytesArrayToHexStr(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        if (bytes != null && bytes.length != 0) {

            for (byte aByte : bytes) {
                StringBuilder builder1 = new StringBuilder(Integer.toHexString(aByte & 0xFF));
                while (builder1.length() < 2) {
                    builder1.insert(0, "0");
                }

                builder.append(builder1);
                builder.append(" ");
            }

            return builder.toString();
        }

        return "";
    }

    private void Lnb22KChange(int i) {
        if (i == this.mLNB_22k) {
            return;
        }

        this.mLNB_22k = i;
        Satellite satellite = this.mSatellites.get(this.mSatPos);
        satellite.sLNB_22k = this.mLNB_22k;

        satellite.sLNB_Freq = this.mLNB_Freq;

        satellite.sLNB_Disq = this.mLNB_Disq;
        this.simpleDb.update_satellite(satellite);
        if (!this.scanFragment.isScanRunning()) {
            this.SendLNBConfig();
        }

        this.ResetAllValues();
    }

    // send array[4]= 1252 , 0 , checksum
    public native void SendPluse();

    public String ByteArray_to_String(byte[] bytes) {
        StringBuilder builder = new StringBuilder();
        char[] chars = new char[bytes.length];

        for (int i = 0; i < bytes.length; ++i) {
            chars[i] = (char) bytes[i];
        }

        builder.append(chars);
        return builder.toString();
    }

    private void LNBDisqChange(int Disq) {
        if (Disq == this.mLNB_Disq) {
            return;
        }

        this.mLNB_Disq = Disq;
        Satellite satellite = this.mSatellites.get(this.mSatPos);
        satellite.sLNB_22k = this.mLNB_22k;

        satellite.sLNB_Freq = this.mLNB_Freq;

        satellite.sLNB_Disq = this.mLNB_Disq;
        this.simpleDb.update_satellite(satellite);
        if (!this.scanFragment.isScanRunning()) {
            this.SendLNBConfig();
        }

        this.ResetAllValues();
    }

    /**
     * <h>send all parameters message <b>15</b> to handler </h>
     * <p> 1 : array[14]= 1764
     * <p> 2 : Lo[mLNB_Freq1] {@link #mLNB_Freq}
     * <p> 3 : Hi[mLNB_Freq1] {@link #mLNB_Freq}
     * <p>4 : Lo[mLNB_Freq2] {@link #mLNB_Freq}
     * <p> 5 : Hi[mLNB_Freq2] {@link #mLNB_Freq}
     * <p>6 : mLNB_22k {@link #mLNB_22k}
     * <p>7 : mLNB_Disq {@link #mLNB_Disq}
     * <p>8 : Lo[mFrequency / 1000] {@link Transponder#mFrequency} Low
     * <p>9 : Hi[mFrequency / 1000] {@link Transponder#mFrequency} High
     * <p>10 : Lo[mSymbolRate / 1000] {@link Transponder#mSymbolRate} Low
     * <p>11 : Hi[mSymbolRate / 1000] {@link Transponder#mSymbolRate} High
     * <p>12 : mSatPos {@link #mSatPos}
     * <p>13 : checksum {@link MainActivity#CheckSum(byte[], int)}
     */

    public native void SendSelectedSatTpLNBConfig();

    private void LnbFreqChange(int i) {
        if (i == this.mLNB_Freq) {
            return;
        }

        this.mLNB_Freq = i;
        ArrayAdapter<String> adapter = (ArrayAdapter<String>) this.spin_lnb_conf_22k.getAdapter();
        if (i < 3) {
            this.mLNB_22k = 2;
            this.spin_lnb_conf_22k.setEnabled(false);
            if (adapter.getCount() < 3) {
                adapter.add("Auto");
            }
        } else {
            this.mLNB_22k = 0;
            this.spin_lnb_conf_22k.setEnabled(true);
            if (adapter.getCount() > 2) {
                adapter.remove("Auto");
            }
        }

        adapter.setNotifyOnChange(true);
        this.spin_lnb_conf_22k.setSelection(this.mLNB_22k);
        Satellite satellite = this.mSatellites.get(this.mSatPos);
        satellite.sLNB_22k = this.mLNB_22k;

        satellite.sLNB_Freq = this.mLNB_Freq;

        satellite.sLNB_Disq = this.mLNB_Disq;
        this.simpleDb.update_satellite(this.mSatellites.get(this.mSatPos));
        String[] array = this.getResources().getStringArray(R.array.arrayLnbFreq);   // array:arrayLnbFreq
        this.txt_antenna_value.setText(array[this.mLNB_Freq]);
        if (!this.scanFragment.isScanRunning()) {
            this.SendLNBConfig();
        }

        this.ResetAllValues();
    }

    /**
    *
     * a[0] = from -29 to <p>
     * case a[0]= -29 if  a[1],[2] =1 ,send 7 to handler<p>
     * case a[0]= -27 and array_len >= 4 ,mSigStrength=a[1],mSigQuality=a[2],send 3 to handler<p>
    *
    *

    **/
    public native void ProgressRecivedMessage(byte[] arg1);
/////on change////
    private void LanguageChange(int i) {
        if (i == this.lang_Select) {
            return;
        }

        this.ChangeLocale(i);
        this.lang_Select = i;
        SharedPreferences.Editor editor = this.mpref.edit();
        editor.putInt("lang", this.lang_Select);
        editor.apply();
        this.moveImage_toolbar_bluetooth.setDefaultPosition(this.isArabic);
        this.setContentView(R.layout.activity_main);  // layout:activity_main
        this.SettingsPrep();
        this.Setup_Fragments();
        if (this.DrawerLayout.isDrawerOpen(this.drawerView)) {
            this.DrawerLayout.closeDrawer(this.drawerView);
        } else {
            this.DrawerLayout.openDrawer(this.drawerView);
        }

        this.compassFragment = (CompassFragment) this.getSupportFragmentManager().findFragmentByTag(CompassFragment.class.getSimpleName());
        this.fieldStrengthFragment = (FieldStrengthFragment) this.getSupportFragmentManager().findFragmentByTag(FieldStrengthFragment.class.getSimpleName());
        this.scanFragment = (ScanFragment) this.getSupportFragmentManager().findFragmentByTag(ScanFragment.class.getSimpleName());
        if (this.BT_ID != null && !this.BT_ID.contains("1.1")) {
            if (this.scanFragment != null && this.scanFragment.getView() != null) {
                this.scanFragment.getView().setVisibility(VISIBLE);
            }
        } else {
            if (this.scanFragment != null && this.scanFragment.getView() != null) {
                this.scanFragment.getView().setVisibility(GONE);
            }
        }

        this.fieldStrengthFragment.Update_transponder_list(this.mSatellites.get(this.mSatPos).mTransponders, this.mTpPos);
        if (this.location != null) {
            this.UpdateLoc(this.location);
        }

        if (mDvbFinder.DVBFinder_Status == DVB.CONNECTED_AND_CHARS_SET) {
            this.moveImage_toolbar_bluetooth.setImageResource(R.drawable.blue_connected);   // drawable:ic_bluetooth_connected
            this.SendSelectedSatTpLNBConfig();
            return;
        }

        if (mDvbFinder.DVBFinder_Status == DVB.SCAN_START_SUCCESSFULLY) {
            this.moveImage_toolbar_bluetooth.setImageResource(R.drawable.bt_anima);   // drawable:bt_anima
            return;
        }

        this.moveImage_toolbar_bluetooth.setImageResource(R.drawable.blue_disconnected);   // drawable:ic_bluetooth_unconnected
    }

    private void Qt_Down_Change(int i) {
        if (i == this.qt_down_Select) {
            return;
        }

        if (i > this.qt_upper_select) {
            Toast.makeText(this, R.string.strTipsLowerLeUpper, 0).show();  // string:strTipsLowerLeUpper "The lower limit needs to be less than the upper limit"
            this.set_qt_down_spn.setSelection(this.qt_down_Select);
            return;
        }

        this.qt_down_Select = i;
        SharedPreferences.Editor editor = this.mpref.edit();
        editor.putInt("qt_down", this.qt_down_Select);
        editor.apply();
        this.lquality = -1;
        this.Update_Beep();
    }

    private void Qt_UpperChange(int i) {
        if (i == this.qt_upper_select) {
            return;
        }

        if (i < this.qt_down_Select) {
            Toast.makeText(this, R.string.strTipsUpperGeLower, Toast.LENGTH_SHORT).show();  // string:strTipsUpperGeLower "The upper limit needs to be greater than the lower limit"
            this.set_qt_upper_spn.setSelection(this.qt_upper_select);
            return;
        }

        this.qt_upper_select = i;
        SharedPreferences.Editor edit = this.mpref.edit();
        edit.putInt("qt_up", this.qt_upper_select);
        edit.apply();
        this.lquality = -1;
        this.Update_Beep();
    }

    private void SatelliteChange(int i) {
        if (i == this.mSatPos) {
            return;
        }

        this.transponderAdapt.clear();
        SharedPreferences.Editor editor = this.mpref.edit();
        this.mSatPos = i;
        Satellite sat = this.mSatellites.get(this.mSatPos);
        this.mLNB_22k = sat.sLNB_22k;
        this.mLNB_Freq = sat.sLNB_Freq;
        this.mLNB_Disq = sat.sLNB_Disq;
        String[] array = this.getResources().getStringArray(R.array.arrayLnbFreq);   // array:arrayLnbFreq
        this.txt_antenna_value.setText(array[this.mLNB_Freq]);
        List<Transponder> transponders = sat.mTransponders;
        if (transponders == null || (transponders.isEmpty())) {
            transponders = this.simpleDb.GetTransponderList(sat.satelite_id);
            sat.mTransponders = transponders;
        }

        if (transponders == null) {
            return;
        }

        this.mTpPos = 0;
        editor.putInt("sat_pos", i);
        editor.putInt("tp_pos", this.mTpPos);
        editor.apply();
        Log.i("MainActivity", "Satellite " + this.mSatellites.get(this.mSatPos).name + " Tp size " + transponders.size());
        this.transponderAdapt.addAll(transponders);
        this.transponderAdapt.notifyDataSetChanged();
        this.tp_spinner.setEnabled(transponders.size() != 0);
        this.tp_spinner.setSelection(this.mTpPos);
        this.SendSelectedSatTpLNBConfig();
        this.calcSatReceiveArms();
        this.ResetAllValues();
        this.fieldStrengthFragment = (FieldStrengthFragment) this.getSupportFragmentManager().findFragmentByTag(FieldStrengthFragment.class.getSimpleName());
        if (this.fieldStrengthFragment != null) {
            this.fieldStrengthFragment.Update_transponder_list(this.mSatellites.get(this.mSatPos).mTransponders, this.mTpPos);
        }

        for (int i1 = 0; i1 < 4; ++i1) {
            if (this.fieldStrengthFragment != null) {
                this.fieldStrengthFragment.update_vertical_progress(i1, 0);
            }
        }
    }

    private void St_DownChange(int i) {
        if (i == this.st_down_Select) {
            return;
        }

        if (i > this.st_upper_select) {
            Toast.makeText(this, R.string.strTipsLowerLeUpper, 0).show();  // string:strTipsLowerLeUpper "The lower limit needs to be less than the upper limit"
            this.set_st_down_spn.setSelection(this.st_down_Select);
            return;
        }

        this.st_down_Select = i;
        SharedPreferences.Editor editor = this.mpref.edit();
        editor.putInt("st_down", this.st_down_Select);
        editor.apply();
        this.lstrength = -1;
        this.Update_Beep();
    }

    private void St_UpperChange(int i) {
        if (i == this.st_upper_select) {
            return;
        }

        if (i < this.st_down_Select) {
            Toast.makeText(this, R.string.strTipsUpperGeLower, 0).show();  // string:strTipsUpperGeLower "The upper limit needs to be greater than the lower limit"
            this.set_st_upper_spn.setSelection(this.st_upper_select);
            return;
        }

        this.st_upper_select = i;
        SharedPreferences.Editor editor = this.mpref.edit();
        editor.putInt("st_up", this.st_upper_select);
        editor.apply();
        this.lstrength = -1;
        this.Update_Beep();
    }

    private void TpChange(int i) {
        if (i == this.mTpPos) {
            return;
        }

        SharedPreferences.Editor editor = this.mpref.edit();
        this.mTpPos = i;
        editor.putInt("tp_pos", this.mTpPos);
        editor.apply();
        if (!this.scanFragment.isScanRunning()) {
            this.SendSelectedSatTp();
        }

        this.ResetAllValues();
    }
/////on change////
    @Override
    public void On_Bluetooth_connect_fail() {
        Message message = Message.obtain();
        message.what = MSG.END_SESSION_14;
        this.mHandler.sendMessage(message);
    }



    @Override
    public void On_V8_Finder_Disconnect() {
        Message message = Message.obtain();
        message.what = MSG.DEVICE_DISCONNECT_2;
        this.mHandler.sendMessage(message);
    }



    @Override
    public void On_V8_Finder_Connected() {
        Message message = Message.obtain();
        message.what = MSG.CONNECTED_1;
        this.mHandler.sendMessage(message);
    }



    public void insert_channels_with_in_tp_id(int id) {
        ArrayList<ChannelBase> arrayList = new ArrayList<>();

        for (int i = 0; i < this.channelAdapter.getCount(); ++i) {
            if (this.channelAdapter.getItem(i) != null) {
                arrayList.add(this.channelAdapter.getItem(i));
            }
        }

        if (arrayList.size() > 0) {
            DBManager.getInctance(this).InsertTransponderChannels(id, arrayList);
        }
    }

    private void ChangeLocale(int i) {
        Locale locale;
        String[] lang = {"en", "zh", "ar", "es", "pt", "fr", "in", "ru", "b+sr+Latn", "de"};
        Configuration configuration = this.getResources().getConfiguration();
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        if (i <= 0) {
            locale = this.DefLocale;
        } else if (i != 9) {
            locale = new Locale(lang[i - 1]);
        } else if (Build.VERSION.SDK_INT >= 21) {
            locale = new Locale.Builder().setLanguage("sr").setRegion("RS").setScript("Latn").build();
        } else {
            locale = new Locale("sr", "RS", "Latn");
        }

        configuration.setLocale(locale);
        Locale.setDefault(locale);
        //this.createConfigurationContext(configuration);
        this.getResources().updateConfiguration(configuration, metrics);
        this.isArabic = locale.equals(new Locale("ar"));
    }

    @Override  // android.support.v4.app.k
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 3) {
            if (data == null) {
                return;
            }

            SharedPreferences.Editor editor = this.mpref.edit();
            int sat_pos = data.getIntExtra("sat_pos", 0);
            int tp_pos = data.getIntExtra("tp_pos", 0);
            if (sat_pos != -1 && tp_pos != -1) {
                this.mSatPos = sat_pos;
                this.mTpPos = tp_pos;
                editor.putInt("sat_pos", this.mSatPos);
                editor.putInt("tp_pos", this.mTpPos);
                editor.commit();
                this.sat_spinner_adp.clear();
                this.transponderAdapt.clear();
                this.mSatellites.clear();
                this.mSatellites = this.simpleDb.GetAllSatellites();
                Satellite satellite = this.mSatellites.get(this.mSatPos);
                satellite.mTransponders = this.simpleDb.GetTransponderList(satellite.satelite_id);
                this.sat_spinner_adp.addAll(this.mSatellites);
                this.transponderAdapt.addAll(satellite.mTransponders);
                this.satellite_spinner.setSelection(this.mSatPos);
                this.tp_spinner.setSelection(this.mTpPos);
                this.mLNB_22k = this.mSatellites.get(this.mSatPos).sLNB_22k;
                this.mLNB_Freq = this.mSatellites.get(this.mSatPos).sLNB_Freq;
                this.mLNB_Disq = this.mSatellites.get(this.mSatPos).sLNB_Disq;
                String[] array = this.getResources().getStringArray(R.array.arrayLnbFreq);   // array:arrayLnbFreq
                this.txt_antenna_value.setText(array[this.mLNB_Freq]);
                this.SendSelectedSatTpLNBConfig();
                this.calcSatReceiveArms();
            } else {
                Satellite satellite = this.mSatellites.get(this.mSatPos);
                this.sat_spinner_adp.clear();
                this.transponderAdapt.clear();
                this.mSatellites = this.simpleDb.GetAllSatellites();
                this.mSatPos = this.simpleDb.d(satellite);
                this.SavePreferences();
                this.sat_spinner_adp.addAll(this.mSatellites);
                this.transponderAdapt.addAll(this.mSatellites.get(this.mSatPos).mTransponders);
                this.satellite_spinner.setSelection(this.mSatPos);
                this.tp_spinner.setSelection(this.mTpPos);
                editor.putInt("sat_pos", this.mSatPos);
                editor.putInt("tp_pos", this.mTpPos);
                editor.apply();
            }

            this.fieldStrengthFragment = (FieldStrengthFragment) this.getSupportFragmentManager().findFragmentByTag(FieldStrengthFragment.class.getSimpleName());
            if (this.fieldStrengthFragment != null) {
                this.fieldStrengthFragment.Update_transponder_list(this.mSatellites.get(this.mSatPos).mTransponders, this.mTpPos);
            }
            this.SendSelectedSatTpLNBConfig();
        }
    }

    @Override  // android.app.Activity
    public void onAttachFragment(@NonNull Fragment fragment) {
        super.onAttachFragment(fragment);
        Log.i("MainActivity", "onAttachFragment " + fragment.getClass().getSimpleName());
    }

    @Override  // android.support.v4.app.k
    public void onBackPressed() {
        if (this.DrawerLayout.isDrawerOpen(this.drawerView)) {
            this.DrawerLayout.closeDrawer(this.drawerView);
            return;
        }

        super.onBackPressed();
    }

    @Override  // android.view.View$OnClickListener
    public void onClick(View view) {
        if (view == this.img_toobar_setting) {
            if (this.DrawerLayout.isDrawerOpen(this.drawerView)) {
                this.DrawerLayout.closeDrawer(this.drawerView);
                return;
            }

            this.DrawerLayout.openDrawer(this.drawerView);
            return;
        }

        if (view != this.compassFragment.img_location_icon && view != this.compassFragment.tx_location_text) {
            if (view == this.img_toolbar_satellite && this.CurrentFragment == 0) {
                Intent intent = new Intent(this, SatelliteListActivity2.class);
                Location safeLocation = getSafeLocation();
                intent.putExtra("lat",safeLocation.getLatitude());
                intent.putExtra("lng",safeLocation.getLongitude());
                this.startActivityForResult(intent, 3);
                return;
            }

            if (view == this.img_toolbar_channel && this.CurrentFragment == 0) {
                this.ScanTransponderChannel();
                return;
            }

            if (view == this.txt_antenna_value) {
                this.Show_LNB_ConfigDialog();
                return;
            }

            if (view == this.img_lnb_config) {
                this.GPSReqDialog.dismiss();
                return;
            }

            if (view == this.moveImage_toolbar_bluetooth) {
                if (Math.abs(this.LastClickMillis - System.currentTimeMillis()) < 1000L) {
                    Log.i("MainActivity", "click too fast");
                    return;
                }

                this.LastClickMillis = System.currentTimeMillis();
                this.mDvbFinder.getClass();
                if (mDvbFinder.DVBFinder_Status != DVB.RELEASE) {

                    if (this.mDvbFinder.DVBFinder_Status == DVB.REACHED_THE_LIMIT) {
                        this.mDvbFinder.StartConnecting();
                        this.moveImage_toolbar_bluetooth.setImageResource(R.drawable.bt_anima);   // drawable:bt_anima
                        ((AnimationDrawable) this.moveImage_toolbar_bluetooth.getDrawable()).start();
                        return;
                    }

                    this.readNewChars.InterruptThread();
                    this.mDvbFinder.EndDvbFinder();
                    this.moveImage_toolbar_bluetooth.setImageResource(R.drawable.blue_disconnected);   // drawable:ic_bluetooth_unconnected
                    this.mSigQuality = 0;
                    this.mSigStrength = 0;
                    this.Update_Beep();
                    this.readNewChars.InterruptThread();
                    return;
                }

                this.mDvbFinder.StartConnecting();
                this.moveImage_toolbar_bluetooth.setImageResource(R.drawable.bt_anima);   // drawable:bt_anima
                ((AnimationDrawable) this.moveImage_toolbar_bluetooth.getDrawable()).start();
            }

         /*   if (view == this.img_toolbar_logo) {
                View inflate = LayoutInflater.from(this).inflate(R.layout.about, null);  // layout:about
                this.about_update_dialog = new AlertDialog.Builder(this).setTitle(R.string.strAbout).setNegativeButton(R.string.strClose, null).setPositiveButton(R.string.strUpdate, null).setView(inflate).create();  // string:strAbout "About V8 Finder"
                this.about_update_dialog.setCancelable(false);
                this.about_update_dialog.setOnDismissListener(dialog -> about_update_dialog = null);
                this.about_update_dialog.setOnShowListener(this);
                this.about_update_dialog.show();
                this.about_update_dialog.getButton(-1).setOnClickListener(MainActivity.this);
                this.SendGetDeviceInfo();

            }*/
        } else {
            this.requestFineLocation();
            this.compassFragment.tx_location_text.setText(R.string.strGettingLocation);   // string:strGettingLocation "Getting location …"
        }
    }

    @Override  // android.support.v4.app.k
    protected void onCreate(Bundle savedInstanceState) {
        PrintStream printStream;
        super.onCreate(savedInstanceState);
        Intent intent = this.getIntent();
        if (intent == null || !intent.getStringExtra("from").equals("welcome")) {
            this.finish();
        }


        this.simpleDb = DBManager.getInctance(this);
        simpleDb.setOnChangeListener(this);
       /* try {
            this.mZipfile = new ZipFile(packageCodePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
*/
        this.Load_Preferences();

        this.setContentView(R.layout.activity_main);   // layout:activity_main

        this.checkPermissions();
        this.mDvbFinder = DVBFinder.newInstance(this, this);
        MainActivity.rContext = this;
        this.SettingsPrep();
        this.Setup_Fragments();

        boolean mkdir = true;
        if (this.isWriteLog) {
            File directory = new File(Environment.getExternalStorageDirectory() + "/SatFinder2021");
            if (!directory.exists()) {
                mkdir = directory.mkdir();
            }
            if (mkdir) {
                Date date = new Date(System.currentTimeMillis());
                File file = new File(directory, new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss").format(date) + ".txt");

                try {
                    FileOutputStream fileOutputStream = new FileOutputStream(file);
                    printStream = new PrintStream(fileOutputStream);

                    this.OutputWrite = System.out;
                    System.setOut(printStream);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                }
            }

        }

    }

    @Override  // android.support.v4.app.k
    protected void onDestroy() {
        super.onDestroy();
        SavePreferences();
        this.simpleDb.Close();
        this.mFragmentList = null;
        this.readNewChars.InterruptThread();
        this.mDvbFinder.EndDvbFinder();
        this.mDvbFinder.unregisterBlueConnectChangeReceiver();
        this.Destroy_Beeper();
        if (this.isWriteLog) {
            if (this.OutputWrite != null) {
                System.setOut(this.OutputWrite);
            }
        }

        System.exit(0);
    }

    @Override  // android.content.DialogInterface$OnDismissListener
    public void onDismiss(DialogInterface dialogInterface) {
        if (dialogInterface == this.mChnDialog) {
            this.mChnDialog = null;
            this.txt_channel_total = null;
            this.progs_channel_progress = null;
            this.channelAdapter.ClearList();
            this.channelAdapter = null;
            return;
        }

        if (dialogInterface == this.GPSReqDialog) {
            this.GPSReqDialog = null;
            this.lnb22KswitchAdapter.clear();
            this.lnbDiseqcAdapter = null;
            this.lnbFreqAdapter = null;
            this.lnb22KswitchAdapter = null;
        }
    }

    @Override  // android.widget.AdapterView$OnItemSelectedListener
    public void onItemSelected(AdapterView adapterView, View view, int position, long id) {
        if (adapterView == this.satellite_spinner) {
            this.SatelliteChange(position);
            return;
        }

        if (adapterView == this.tp_spinner) {
            this.TpChange(position);
            return;
        }

        if (adapterView == this.spin_lnb_config_freq) {
            this.LnbFreqChange(position);
            return;
        }

        if (adapterView == this.spin_lnb_conf_22k) {
            this.Lnb22KChange(position);
            return;
        }

        if (adapterView == this.main_lnb_diseqc_spinner) {
            this.LNBDisqChange(position);
            return;
        }

        if (adapterView == this.set_lang_spn) {
            this.LanguageChange(position);
            return;
        }

        if (adapterView == this.set_beep_spn) {
            this.Beep_change(position);
            return;
        }

        if (adapterView == this.set_st_upper_spn) {
            this.St_UpperChange(position);
            return;
        }

        if (adapterView == this.set_st_down_spn) {
            this.St_DownChange(position);
            return;
        }

        if (adapterView == this.set_qt_upper_spn) {
            this.Qt_UpperChange(position);
            return;
        }

        if (adapterView == this.set_qt_down_spn) {
            this.Qt_Down_Change(position);
        }
    }

    @Override  // android.location.LocationListener
    public void onLocationChanged(Location location) {
        this.location = location;
        this.locationManager.removeUpdates(this);
        this.UpdateLoc(location);
    }

    @Override  // android.widget.AdapterView$OnItemSelectedListener
    public void onNothingSelected(AdapterView parent) {
    }

    @Override  // android.support.v4.app.k
    protected void onPause() {
        super.onPause();
    }

    @Override  // android.location.LocationListener
    public void onProviderDisabled(String provider) {
    }

    @Override  // android.location.LocationListener
    public void onProviderEnabled(String provider) {
    }

    @Override  // android.support.v4.app.k
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1 && Build.VERSION.SDK_INT >= 23) {
            if (this.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0 && this.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                Toast.makeText(this, "Apk needs all location permission to work normally.", 1).show();
                return;
            }

            Message message = this.mHandler.obtainMessage(MSG.START_SEARCH_DEVICE_10);
            this.mHandler.sendMessageDelayed(message, 800L);
            if (this.locationManager.isProviderEnabled("network")) {
                this.requestSimpleLocation();
                return;
            }

            this.requestFineLocation();
        }
    }

    @Override  // android.support.v4.app.k
    protected void onResume() {
        super.onResume();
        this.Start_VBeeping();
        this.mSigQuality = 0;
        this.LastClickMillis = System.currentTimeMillis();
    }

    @Override  // android.content.DialogInterface$OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        if (dialogInterface == this.about_update_dialog) {
            this.UpdateAboutDialog();
        }
    }

    @Override  // android.support.v4.app.k
    protected void onStart() {
        super.onStart();
        this.compassFragment = (CompassFragment) this.getSupportFragmentManager().findFragmentByTag(CompassFragment.class.getSimpleName());
        this.fieldStrengthFragment = (FieldStrengthFragment) this.getSupportFragmentManager().findFragmentByTag(FieldStrengthFragment.class.getSimpleName());
        this.scanFragment = (ScanFragment) this.getSupportFragmentManager().findFragmentByTag(ScanFragment.class.getSimpleName());
        if (this.scanFragment != null && this.scanFragment.getView() != null) {
            if (this.BT_ID != null && !this.BT_ID.contains("1.1")) {
                this.scanFragment.getView().setVisibility(VISIBLE);
            } else {
                this.scanFragment.getView().setVisibility(GONE);
            }
        }

        this.fieldStrengthFragment.Update_transponder_list(this.mSatellites.get(this.mSatPos).mTransponders, this.mTpPos);
        if (Build.VERSION.SDK_INT < 23) {
            Message message = this.mHandler.obtainMessage(MSG.START_SEARCH_DEVICE_10);
            this.mHandler.sendMessageDelayed(message, 200L);
            if (this.locationManager.isProviderEnabled("network")) {
                this.requestSimpleLocation();
            } else {
                this.requestFineLocation();
            }
        } else if (this.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") == 0 || this.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") == 0) {
            Message message = this.mHandler.obtainMessage(MSG.START_SEARCH_DEVICE_10);
            this.mHandler.sendMessageDelayed(message, 200L);
            if (this.locationManager.isProviderEnabled("network")) {
                this.requestSimpleLocation();
            } else {
                this.requestFineLocation();
            }
        }

        UpdateLocOffline();
        this.wakeLock = ((PowerManager) this.getSystemService(POWER_SERVICE))
                .newWakeLock(FLAG_KEEP_SCREEN_ON | ON_AFTER_RELEASE, "MainActivity");
        this.wakeLock.acquire();
    }

    @Override  // android.location.LocationListener
    public void onStatusChanged(String provider, int status, Bundle extras) {
    }

    @Override  // android.support.v4.app.k
    protected void onStop() {
        super.onStop();
        this.locationManager.removeUpdates(this);
        if (this.wakeLock != null && (this.wakeLock.isHeld())) {
            this.wakeLock.release();
            this.wakeLock = null;
        }

        if (this.scanFragment.isScanRunning()) {
            this.scanFragment.stopScan();
        }
    }

    public void requestFineLocation() {

        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            return;
        }

        this.location = null;
        if (this.locationManager.getProvider("gps") != null) {
            this.locationManager.requestLocationUpdates("gps", 0L, 0.0f, this);
        }
    }

    public void requestSimpleLocation() {
        if (ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_FINE_LOCATION") != 0 && ContextCompat.checkSelfPermission(this, "android.permission.ACCESS_COARSE_LOCATION") != 0) {
            return;
        }

        if (this.locationManager.getProvider("network") != null) {
            this.locationManager.requestLocationUpdates("network", 0L, 0.0f, this);
        }
    }

    void ScanTransponderChannel() {

        if (mDvbFinder.DVBFinder_Status != DVB.CONNECTED_AND_CHARS_SET) {
            Toast toast = Toast.makeText(this, R.string.strGTFinderUnConnect, 0);  // string:strGTFinderUnConnect "V8 Finder is not connected"
            toast.setGravity(17, 0, 0);
            toast.show();
            return;
        }

        if (this.mSigQuality <= 0) {
            Toast toast = Toast.makeText(this, R.string.strChannelUnlock, 0);  // string:strChannelUnlock "Channel is not locked"
            toast.setGravity(17, 0, 0);
            toast.show();
            return;
        }

        this.SendPluse();
        this.mChnDialog = new AlertDialog.Builder(this).create();
        View view = LayoutInflater.from(this).inflate(R.layout.channel_list, null);  // layout:channel_list
        ListView listView = view.findViewById(R.id.channel_list);   // id:channel_list
        this.mChnDialog.setView(view);
        View inflate = LayoutInflater.from(this).inflate(R.layout.channel_title, null);  // layout:channel_title
        this.mChnDialog.setCustomTitle(inflate);
        this.progs_channel_progress = inflate.findViewById(R.id.channel_progress);   // id:channel_progress
        this.txt_channel_total = inflate.findViewById(R.id.channel_total);   // id:channel_total
        this.txt_channel_total.setVisibility(GONE);

        this.channelAdapter = new ChannelAdapter(this, new ArrayList<>());
        listView.setAdapter(this.channelAdapter);
        this.mChnDialog.setOnDismissListener(this);
        this.mChnDialog.show();
        FrameLayout.LayoutParams channel_progressLayoutParams = (FrameLayout.LayoutParams) this.progs_channel_progress.getLayoutParams();
        FrameLayout.LayoutParams txt_channel_totalLayoutParams = (FrameLayout.LayoutParams) this.txt_channel_total.getLayoutParams();
        if (this.isArabic) {
            channel_progressLayoutParams.gravity = 0x800013;
            txt_channel_totalLayoutParams.gravity = 0x800013;
        } else {
            channel_progressLayoutParams.gravity = 0x800015;
            txt_channel_totalLayoutParams.gravity = 0x800015;
        }

        this.progs_channel_progress.setLayoutParams(channel_progressLayoutParams);
        this.txt_channel_total.setLayoutParams(txt_channel_totalLayoutParams);
    }

    public void Send_LNB_Settings() {
        int[][] LnbFrqArray = {new int[]{9750, 10600}, new int[]{9750, 10700}, new int[]{9750, 10750}, new int[]{5150, 5750}, new int[]{5750, 5150}, new int[]{5150, 5150}, new int[]{5750, 5750}, new int[]{5950, 5950}, new int[]{9750, 9750}, new int[]{10000, 10000}, new int[]{10600, 10600}, new int[]{10700, 10700}, new int[]{10750, 10750}, new int[]{0x2BF2, 0x2BF2}, new int[]{11300, 11300}};
        byte[] send = new byte[9];
        send[0] = -28;
        send[1] = 11;
        byte[] LnbFrq1 = MainActivity.Integer_to_BytesArray(LnbFrqArray[this.mLNB_Freq][0]);
        send[2] = LnbFrq1[1];
        send[3] = LnbFrq1[0];
        byte[] LnbFrq2 = MainActivity.Integer_to_BytesArray(LnbFrqArray[this.mLNB_Freq][1]);
        send[4] = LnbFrq2[1];
        send[5] = LnbFrq2[0];
        send[6] = (byte) this.mLNB_22k;
        send[7] = (byte) this.mLNB_Disq;
        send[8] = this.CheckSum(send, 8);
        Message message = this.mHandler.obtainMessage(MSG.SEND_TO_DEVICE_15, send);
        this.mHandler.sendMessage(message);
        this.mSigStrength = 0;
        this.mSigQuality = 0;
        this.Update_Beep();
    }

    public void SendStartBlindScan() {
        byte[] bytes = new byte[3];
        bytes[0] = -28;
        bytes[1] = 12;
        bytes[2] = this.CheckSum(bytes, 2);
        Message message = mHandler.obtainMessage(MSG.SEND_TO_DEVICE_15, bytes);
        mHandler.sendMessage(message);
    }

    public void UpdateTranspondersList() {
        this.transponderAdapt.clear();
        Satellite satellite = this.mSatellites.get(this.mSatPos);
        satellite.mTransponders = DBManager.getInctance(this).GetTransponderList(this.mSatellites.get(this.mSatPos).satelite_id);
        this.transponderAdapt.addAll(this.mSatellites.get(this.mSatPos).mTransponders);
        this.transponderAdapt.notifyDataSetChanged();
        this.fieldStrengthFragment.Update_transponder_list(this.mSatellites.get(this.mSatPos).mTransponders, this.mTpPos);
    }

    private void checkPermissions() {
        int i = 1;
        if (Build.VERSION.SDK_INT >= 23) {
            ArrayList<String> arrayList = new ArrayList<>();
            if (this.checkSelfPermission("android.permission.ACCESS_FINE_LOCATION") != 0) {
                arrayList.add("android.permission.ACCESS_FINE_LOCATION");
            }

            if (this.checkSelfPermission("android.permission.ACCESS_COARSE_LOCATION") != 0) {
                arrayList.add("android.permission.ACCESS_COARSE_LOCATION");
            }

            if (this.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") != 0) {
                arrayList.add("android.permission.WRITE_EXTERNAL_STORAGE");
            }

            if (arrayList.size() >= 1) {
                this.requestPermissions(arrayList.toArray(new String[0]), 1);
            }
        } else {
            Message message = this.mHandler.obtainMessage(MSG.START_SEARCH_DEVICE_10);
            this.mHandler.sendMessageDelayed(message, 1000L);
        }

        this.locationManager = (LocationManager) this.getSystemService("location");
        if (!this.locationManager.isProviderEnabled("gps") && !this.locationManager.isProviderEnabled("network")) {
            i = 0;
        }

        if (i == 0) {
            DialogInterface.OnClickListener ClickListener = (dialogInterface, which) -> {
                if (which == -2) {
                    if (dialogInterface == GPSReqDialog) {
                        GPSReqDialog.dismiss();
                        GPSReqDialog = null;
                    }
                } else {
                    if (dialogInterface == GPSReqDialog) {
                        GPSReqDialog.dismiss();
                        GPSReqDialog = null;
                        Intent intent = new Intent("android.settings.LOCATION_SOURCE_SETTINGS");
                        startActivityForResult(intent, 2);
                    }
                }
            };
            this.GPSReqDialog = new AlertDialog.Builder(this)
                    .setTitle(R.string.strOpenGPS)
                    .setMessage(R.string.strGPSTips)
                    .setPositiveButton(R.string.strSetting, ClickListener)
                    .setNegativeButton(R.string.strNo, ClickListener)
                    .create();  // string:strOpenGPS "GPS Setting"
            this.GPSReqDialog.show();
        }
    }


    private void Destroy_Beeper() {
        if (this.mBeeper != null) {
            this.mBeeper.stop_sound();
            this.mBeeper.Release();
            this.mBeeper = null;
        }
    }

    private void Load_Preferences() {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        this.mpref = this.getSharedPreferences("save_data", 0);
        this.mSatPos = this.mpref.getInt("sat_pos", 0);
        this.mTpPos = this.mpref.getInt("tp_pos", 0);
        this.lang_Select = this.mpref.getInt("lang", 0);
        this.beep_Select = this.mpref.getInt("beep", 1);
        this.st_upper_select = this.mpref.getInt("st_up", 16);
        this.st_down_Select = this.mpref.getInt("st_down", 4);
        this.qt_upper_select = this.mpref.getInt("qt_up", 14);
        this.qt_down_Select = this.mpref.getInt("qt_down", 2);
        this.mSatellites = this.simpleDb.GetAllSatellites();
        this.DefLocale = this.getResources().getConfiguration().locale;
        this.SavePreferences();
        this.mLNB_Freq = this.mSatellites.get(this.mSatPos).sLNB_Freq;
        this.mLNB_22k = this.mSatellites.get(this.mSatPos).sLNB_22k;
        this.mLNB_Disq = this.mSatellites.get(this.mSatPos).sLNB_Disq;
        if (this.mLNB_Freq < 3 && this.mLNB_22k != 2) {
            this.mLNB_22k = 2;
            Satellite satellite = this.mSatellites.get(this.mSatPos);
            satellite.sLNB_22k = this.mLNB_22k;
            this.simpleDb.update_satellite(this.mSatellites.get(this.mSatPos));
        }

        this.ChangeLocale(this.lang_Select);
    }
    private void newLoad_Preferences() {

        this.mpref2= PreferenceManager.getDefaultSharedPreferences(this);
        //this.mpref = this.getSharedPreferences("save_data", 0);
        this.mSatPos = this.mpref2.getInt("sat_pos", 0);
        this.mTpPos = this.mpref2.getInt("tp_pos", 0);
        this.lang_Select = this.mpref2.getInt("lang", 0);
        this.beep_Select = this.mpref2.getInt("beep", 1);
        this.st_upper_select = this.mpref2.getInt("st_up", 16);
        this.st_down_Select = this.mpref2.getInt("st_down", 4);
        this.qt_upper_select = this.mpref2.getInt("qt_up", 14);
        this.qt_down_Select = this.mpref2.getInt("qt_down", 2);
        this.mSatellites = this.simpleDb.GetAllSatellites();
        this.DefLocale = this.getResources().getConfiguration().locale;
        this.SavePreferences();
        this.mLNB_Freq = this.mSatellites.get(this.mSatPos).sLNB_Freq;
        this.mLNB_22k = this.mSatellites.get(this.mSatPos).sLNB_22k;
        this.mLNB_Disq = this.mSatellites.get(this.mSatPos).sLNB_Disq;
        if (this.mLNB_Freq < 3 && this.mLNB_22k != 2) {
            this.mLNB_22k = 2;
            Satellite satellite = this.mSatellites.get(this.mSatPos);
            satellite.sLNB_22k = this.mLNB_22k;
            this.simpleDb.update_satellite(this.mSatellites.get(this.mSatPos));
        }

        this.ChangeLocale(this.lang_Select);
    }
    private void SettingsPrep() {
        this.DrawerLayout = this.findViewById(R.id.main_drawer_layout);   // id:main_drawer_layout
        this.drawerView = this.findViewById(R.id.drawer_list_view);   // id:drawer_list_view
        ViewGroup.LayoutParams params = this.drawerView.getLayoutParams();
        // params.gravity = this.isArabic ? 0x800005 : 0x800003;
        this.drawerView.setLayoutParams(params);
        this.set_lang_spn = this.findViewById(R.id.setting_lang_spn);   // id:setting_lang_spn
        this.set_beep_spn = this.findViewById(R.id.setting_beep_spn);   // id:setting_beep_spn
        this.set_st_upper_spn = this.findViewById(R.id.setting_st_upper_spn);   // id:setting_st_upper_spn
        this.set_st_down_spn = this.findViewById(R.id.setting_st_down_spn);   // id:setting_st_down_spn
        this.set_qt_upper_spn = this.findViewById(R.id.setting_qt_upper_spn);   // id:setting_qt_upper_spn
        this.set_qt_down_spn = this.findViewById(R.id.setting_qt_down_spn);   // id:setting_qt_down_spn
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.simple_center, this.getResources().getStringArray(R.array.arrayLanguage));  // layout:simple_center
        ArrayAdapter<String> adapter1 = new ArrayAdapter<>(this, R.layout.simple_center, this.getResources().getStringArray(R.array.arrayOnoff));  // layout:simple_center
        ArrayList<Integer> arrayList = new ArrayList<>();

        for (int i = 0; i < 20; ++i) {
            arrayList.add(i * 5);
        }

        ArrayAdapter<Integer> adapter2 = new ArrayAdapter<>(this, R.layout.simple_center, arrayList);  // layout:simple_center
        ArrayAdapter<Integer> adapter3 = new ArrayAdapter<>(this, R.layout.simple_center, arrayList);  // layout:simple_center
        ArrayAdapter<Integer> adapter4 = new ArrayAdapter<>(this, R.layout.simple_center, arrayList);  // layout:simple_center
        ArrayAdapter<Integer> adapter5 = new ArrayAdapter<>(this, R.layout.simple_center, arrayList);  // layout:simple_center
        this.set_lang_spn.setOnItemSelectedListener(this);
        this.set_beep_spn.setOnItemSelectedListener(this);
        this.set_lang_spn.setAdapter(adapter);
        this.set_beep_spn.setAdapter(adapter1);
        this.set_beep_spn.setSelection(this.beep_Select);
        this.set_lang_spn.setSelection(this.lang_Select);
        this.set_st_upper_spn.setOnItemSelectedListener(this);
        this.set_st_upper_spn.setAdapter(adapter2);
        this.set_st_upper_spn.setSelection(this.st_upper_select);
        this.set_st_down_spn.setOnItemSelectedListener(this);
        this.set_st_down_spn.setAdapter(adapter3);
        this.set_st_down_spn.setSelection(this.st_down_Select);
        this.set_qt_upper_spn.setOnItemSelectedListener(this);
        this.set_qt_upper_spn.setAdapter(adapter4);
        this.set_qt_upper_spn.setSelection(this.qt_upper_select);
        this.set_qt_down_spn.setOnItemSelectedListener(this);
        this.set_qt_down_spn.setAdapter(adapter5);
        this.set_qt_down_spn.setSelection(this.qt_down_Select);
    }


    @Override
    public void DBchange() {
       /* this.sat_spinner_adp.clear();
        this.transponderAdapt.clear();
        this.mSatellites.clear();
        this.mSatellites = this.simpleDb.GetAllSatellites();
        Satellite satellite = (Satellite) this.mSatellites.get(this.mSatPos);
        satellite.mTransponders = this.simpleDb.GetTransponderList(satellite.satelite_id);
        this.sat_spinner_adp.addAll(this.mSatellites);
        this.transponderAdapt.addAll(satellite.mTransponders);
        this.satellite_spinner.setSelection(this.mSatPos);
        this.tp_spinner.setSelection(this.mTpPos);*/
    }
}

