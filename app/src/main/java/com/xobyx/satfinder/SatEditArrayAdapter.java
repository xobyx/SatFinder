package com.xobyx.satfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;

import java.util.List;

class SatEditArrayAdapter extends ArrayAdapter<Transponder> {
    final SatEditActivity satEditActivity;

    SatEditArrayAdapter(SatEditActivity editActivity, Context context, int layout, List<Transponder> list) {
        super(context, layout, list);
        this.satEditActivity = editActivity;

    }

    @NonNull
    @Override  // android.widget.ArrayAdapter
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        Transponder trans = this.satEditActivity.mTrasponderList.get(position);
        LayoutInflater inflater = LayoutInflater.from(this.satEditActivity);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.tp_edit, null);  // layout:tp_edit

        }

        TextView freqText = convertView.findViewById(R.id.sat_edit_tp_freq);   // id:sat_edit_tp_freq
        TextView symbolText = convertView.findViewById(R.id.sat_edit_tp_symbolrate);
        convertView.findViewById(R.id.sat_edit_tp_delete).setOnClickListener((v)->{
            this.satEditActivity.mTrasponderList.remove(position);
            remove(position);
            notifyDataSetChanged();
        });   // id:sat_edit_tp_delete
        TextView polarText = convertView.findViewById(R.id.sat_edit_tp_polar);   // id:sat_edit_tp_polar
        freqText.setText(Integer.toString(trans.mFrequency / 1000));
        symbolText.setText(Integer.toString(trans.mSymbolRate / 1000));
        if (trans.mPolization > 0) {
            polarText.setText(R.string.strV);   // string:strV "V"
            return convertView;
        }

        polarText.setText(R.string.strH);   // string:strH "H"
        return convertView;
    }

    public void remove(int index) {
        remove(getItem(index));
    }
}

