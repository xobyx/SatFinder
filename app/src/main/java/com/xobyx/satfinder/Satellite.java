package com.xobyx.satfinder;

import android.location.Location;
import androidx.annotation.NonNull;

import java.io.Serializable;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class Satellite implements Serializable {
    public int satelite_id;
    public float position;
    public int preset;
    public int sLNB_Freq;
    public int sLNB_22k;
    public int sLNB_Disq;
    public String name;
    public List<Transponder> mTransponders;
    public boolean isFav;
    int Capable = -1;

    public Satellite() {
        this.mTransponders = new ArrayList<>();
        this.satelite_id = -1;
    }

    public Satellite(Satellite satellite) {
        this.satelite_id = -1;
        this.position = satellite.position;
        this.name = satellite.name;
        this.isFav = false;
        this.sLNB_22k = satellite.sLNB_22k;
        this.sLNB_Disq = satellite.sLNB_Disq;
        this.sLNB_Freq = satellite.sLNB_Freq;
        this.Capable = satellite.Capable;

        this.mTransponders = new ArrayList<>();
        if (satellite.mTransponders != null && satellite.mTransponders.size() > 0) {
            for (Transponder transponder : satellite.mTransponders) {
                this.mTransponders.add(new Transponder(transponder));
            }
        }

        this.preset = 0;

    }

    public boolean isCapable(double lat, double lng) {
        if (Capable == -1) {
            double dir = ((double) (position / 10.0f)) - lng;
            double v = Math.cos(Math.toRadians(dir)) * Math.cos(Math.toRadians(lat));
            float mxElevation = Float.parseFloat(new DecimalFormat("#.00").format(Math.toDegrees(Math.atan((v - 0.15) / Math.sqrt(1.0 - Math.pow(v, 2.0))))));
            Capable = mxElevation > 9 ? 1 : 0;
            return mxElevation > 9;
        }
        return Capable == 1;

    }

    public boolean isCapable(Location loc) {
        double lat = loc.getLatitude();
        double lng = loc.getLongitude();
        return isCapable(lat, lng);

    }

    @NonNull
    @Override
    public String toString() {
        float v0 = this.position / 10.0f;
        return v0 <= 0.0f ? this.name + " " + -v0 + " " + MainActivity.getContext().getResources().getString(R.string.strE)
                : this.name + " " + v0 + " " + MainActivity.getContext().getResources().getString(R.string.strW);  // string:strW "W"
    }
}

