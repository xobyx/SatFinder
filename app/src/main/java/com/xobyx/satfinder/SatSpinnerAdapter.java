package com.xobyx.satfinder;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;


import java.io.Serializable;
import java.util.List;

@SuppressLint("ParcelCreator")
class SatSpinnerAdapter extends ArrayAdapter<Satellite> implements Serializable, Parcelable {
    final MainActivity ma;

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {

    }

    static class Holder
    {
        TextView dir ;
        TextView name;
        CheckBox fav ;
    }
    SatSpinnerAdapter(Context context, int layout, int resid, List<Satellite> items) {
        super(context, layout, resid, items);
        this.ma = (MainActivity) context;

    }




    @Override  // android.widget.ArrayAdapter
    public View getDropDownView(int position, View convertView, @NonNull ViewGroup parent) {
        Satellite satellite = this.getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(this.ma).inflate(R.layout.sat_spinner_dropview, null);  // layout:sat_spinner_dropview
        }

        TextView name = convertView.findViewById(R.id.sat_drop_angle_text);   // id:sat_drop_angle_text
        TextView dir_txt = convertView.findViewById(R.id.sat_drop_name_text);
        if(satellite!=null) {
            if (satellite.isCapable(ma.getSafeLocation()))
                name.setTextColor(Color.WHITE);
            else
                name.setTextColor(Color.RED);


            float dir_numb;
            dir_numb = satellite.position / 10.0f;

            if (dir_numb >= 0.0f) {
                name.setText(String.format("%s%s ", dir_numb, ma.getResources().getString(R.string.strE)));   // string:strE "E"
            } else {
                name.setText(String.format("%s%s ", -dir_numb, ma.getResources().getString(R.string.strW)));  // string:strW "W"
            }

            dir_txt.setText(satellite.name);
        }
        return convertView;
    }

    @NonNull
    @Override  // android.widget.ArrayAdapter
    public View getView(final int position, View convertView, @NonNull ViewGroup parent) {
        Holder mHolder;
        Satellite sat = getItem(position);
        if(convertView == null) {
            convertView = LayoutInflater.from(this.ma).inflate(R.layout.sat_spinner_item, null);
            mHolder=new Holder();// layout:sat_spinner_item
            mHolder.dir = convertView.findViewById(R.id.sat_spinner_angle_text);   // id:sat_spinner_angle_text
            mHolder.name = convertView.findViewById(R.id.sat_spinner_name_text);   // id:sat_spinner_name_text
            mHolder.fav = convertView.findViewById(R.id.fav);
            mHolder.fav.setTag(sat);

            convertView.setTag(mHolder);
        }
        else {
            mHolder = (Holder)convertView.getTag();
        }



        mHolder.fav.setOnCheckedChangeListener((v,c)->{
            Satellite satellite = (Satellite) v.getTag();
            satellite.isFav=c;
            ma.simpleDb.update_satellite_fav(satellite);
        });
        float f_dir;
        if (sat != null) {
            f_dir = sat.position / 10.0f;

            if (f_dir >= 0.0f) {
                mHolder.dir.setText(String.format("%s%s ", f_dir, ma.getResources().getString(R.string.strE)));   // string:strE "E"
            } else {
                mHolder.dir.setText(String.format("%s%s ", -f_dir, ma.getResources().getString(R.string.strW)));   // string:strW "W"
            }
            mHolder.fav.setChecked(sat.isFav);
            if(sat.isCapable(ma.getSafeLocation())) mHolder.name.setTextColor(Color.WHITE);
            else
                mHolder.name.setTextColor(Color.RED);
            mHolder.name.setText(sat.name);
        }
        return convertView;
    }
}

