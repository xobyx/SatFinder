package com.xobyx.satfinder;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static android.hardware.Sensor.TYPE_ORIENTATION;

public class CompassFragment extends Fragment {
    private final String name;
    private SensorManager sensorMan;
    private final SensorEventListener sensorLis;
    public CompassView view_compass;
    private TextView tx_compass_degree;
    private TextView tx_dvb_singal_stren;
    private TextView tx_signal_quality;
    public TextView tx_location_text;
    public ImageView img_location_icon;
    private ProgressBar prog_signal_strength;
    private ProgressBar prog_signal_quality;
    private String strAngle;
    private int mxSignal_quality;
    public TextView tx_tv_power;
    public TextView tx_tv_cn;
    public TextView tx_tv_ber;
    private DecimalFormat mDecFormat;

    public CompassFragment() {
        this.name = "compass";
        this.sensorLis = new SensorEventListener() {
            @Override
            public void onSensorChanged(SensorEvent event) {
                int sensorType = event.sensor.getType();
                /*if(sensorType == Sensor.TYPE_ACCELEROMETER) {
                   /// Log.d("compass", "Sensor.TYPE_ACCELEROMETER");
                }*/
              /*  else {
                    if(sensorType == Sensor.TYPE_MAGNETIC_FIELD) {
                      //  Log.d("compass", "Sensor.TYPE_MAGNETIC_FIELD");
                        return;
                    }*/

                    if(sensorType == TYPE_ORIENTATION) {
                        float RotX = event.values[0];
                        float RotY = event.values[1];
                        view_compass.setNavPos(RotX,RotY);

                        tx_compass_degree.setText(String.format(Locale.getDefault(),"%d%s", (int) RotX, strAngle));

                       // Log.d("compass", "Sensor.TYPE_ORIENTATION"+ " value: "+RotX );
                    }
               // }
            }

            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }
        };
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        this.UnRegisterSensorListener();
    }


    @Override  // android.support.v4.app.i
    public View onCreateView(LayoutInflater layoutInflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("compass", "compassFragment onCreateView");
        View view = layoutInflater.inflate(R.layout.compass_layout, container, true);  // layout:compass_layout
        this.view_compass = view.findViewById(R.id.compass);  // id:compass
        this.tx_compass_degree = view.findViewById(R.id.compass_degree); // id:compass_degree
        this.tx_dvb_singal_stren = view.findViewById(R.id.dvb_signal_strength_value); // id:dvb_signal_strength_value
        this.tx_signal_quality = view.findViewById(R.id.dvb_signal_quality_value); // id:dvb_signal_quality_value
        this.strAngle = this.getContext().getResources().getString(R.string.strAngle);  // string:strAngle "°"
        this.tx_location_text = view.findViewById(R.id.location_text); // id:location_text
        this.img_location_icon = view.findViewById(R.id.location_icon); // id:location_icon
        this.img_location_icon.setOnClickListener((MainActivity)this.getActivity());
        this.tx_location_text.setOnClickListener(((MainActivity)this.getActivity()));
        this.prog_signal_strength = view.findViewById(R.id.dvb_signal_strength_progress); // id:dvb_signal_strength_progress
        this.prog_signal_quality = view.findViewById(R.id.dvb_signal_quality_progress); // id:dvb_signal_quality_progress
        this.tx_tv_power = view.findViewById(R.id.tv_power); // id:tv_power
        this.tx_tv_cn = view.findViewById(R.id.tv_cn); // id:tv_cn
        this.tx_tv_ber = view.findViewById(R.id.tv_ber); // id:tv_ber
        this.RegisterSensorListener();
        Locale locale = Locale.getDefault();
        this.mDecFormat = new DecimalFormat();
        this.mDecFormat.applyPattern("0.00");
        DecimalFormatSymbols symbols = new DecimalFormatSymbols();
        symbols.setDecimalSeparator('.');
        this.mDecFormat.setDecimalFormatSymbols(symbols);
        locale.equals(new Locale("ar"));
        return view;
    }

    @Override  // android.support.v4.app.i
    public void onAttachFragment(@NonNull Fragment childFragment) {
        super.onAttachFragment(childFragment);
    }

    public void setLocation_txt(String arg2) {
        if(this.tx_location_text != null) {
            this.tx_location_text.setText(arg2);
        }
    }

    private void UnRegisterSensorListener() {
        this.sensorMan.unregisterListener(this.sensorLis);
    }

    private void RegisterSensorListener() {
        this.sensorMan = (SensorManager)this.getContext().getSystemService(Context.SENSOR_SERVICE);

        Sensor sensor = this.sensorMan.getDefaultSensor(TYPE_ORIENTATION);
        this.sensorMan.registerListener(this.sensorLis, sensor, 0);
    }


    public void set_tv_cn_val(int val) {
        String format = this.mDecFormat.format(((float)val) / 100.0f);
        String format1 = String.format(Locale.getDefault(), this.getString(R.string.cn), format);  // string:cn "CN：%s dB"
        SpannableString spannableString = new SpannableString(format1);
        spannableString.setSpan(new ForegroundColorSpan(0xFF00FF00), format1.indexOf("：") + 1, format1.indexOf(format) + format.length(), 17);
        this.tx_tv_cn.setText(spannableString);
    }

    public void set_tv_power_val(int val) {
        String strTv = this.mDecFormat.format(((float)val) / 100.0f);
        String fstrTv = String.format(Locale.getDefault(), this.getString(R.string.pwr), strTv);  // string:pwr "PWR：%s dBm"
        SpannableString spannableString = new SpannableString(fstrTv);
        spannableString.setSpan(new ForegroundColorSpan(0xFF00FF00), fstrTv.indexOf("：") + 1, fstrTv.indexOf(strTv) + strTv.length(), 17);
        
        this.tx_tv_power.setText(spannableString);
    }

    public  void WriteDebugInfo(HashMap<String, Integer> c)
    {


        SpannableStringBuilder builder = new SpannableStringBuilder();

        for (Map.Entry<String, Integer> entry : c.entrySet()) {
            String key = entry.getKey();
            String value = this.mDecFormat.format(entry.getValue());
            String format= String.format("\u24e2 %s : %s\n",key,value);

            SpannableString span=new SpannableString(format);
            span.setSpan(new ForegroundColorSpan(0xFF00FF00),format.indexOf("：") + 1,
                    format.indexOf(value) + value.length(),17);
            builder.append(span,new ForegroundColorSpan(0xFF00FF00),17);


        }



        ((TextView) this.getView().findViewById(R.id.debugt)).setText(builder);
    }
    public void set_signal_quality(int val) {
        view_compass.setQuality(val);
        this.tx_signal_quality.setText(val + "%");
       /*
        this.prog_signal_quality.setProgress(val);
        this.mxSignal_quality = val;
        if(this.mxSignal_quality < 20) {
            this.prog_signal_quality.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_darkred));  // drawable:progress_darkred
            return;
        }

        this.prog_signal_quality.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_yellow));  // drawable:progress_yellow
        this.prog_signal_strength.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_green));  // drawable:progress_green*/
    }

    public void set_signal_strength(int val) {

        this.view_compass.setStrength(val);

       this.tx_dvb_singal_stren.setText(val + "%");
        /*
        this.prog_signal_strength.setProgress(val);
        if(val < 20) {
            this.prog_signal_strength.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_darkred));  // drawable:progress_darkred
            return;
        }

        if(this.mxSignal_quality > 0) {
            this.prog_signal_strength.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_green));  // drawable:progress_green
            return;
        }

        this.prog_signal_strength.setProgressDrawable(this.getResources().getDrawable(R.drawable.progress_lightred));  */// drawable:progress_lightred
    }
}

