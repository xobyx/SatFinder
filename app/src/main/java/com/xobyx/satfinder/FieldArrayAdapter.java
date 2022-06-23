package com.xobyx.satfinder;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.List;

class FieldArrayAdapter extends ArrayAdapter {
    final FieldStrengthFragment fieldStrengthFragment;
    private final int id;

    FieldArrayAdapter(FieldStrengthFragment fieldStrengthFragment, Context context, int i, List list,int id) {
        super(context, i, list);
        this.fieldStrengthFragment = fieldStrengthFragment;

        this.id = id;
    }
    @NonNull
    @Override  // android.widget.ArrayAdapter
    public View getView(int position, View convertView,@NonNull ViewGroup parent) {
        TextView view = (TextView)super.getView(position, convertView, parent);
        if(this.fieldStrengthFragment.mActive_Frequ == id) {
            view.setTextSize(16.0f);
            view.setTextColor(this.fieldStrengthFragment.getActivity().getColor(R.color.white));  // color:white
        }



        return view;
    }
}

