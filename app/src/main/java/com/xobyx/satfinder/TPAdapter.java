package com.xobyx.satfinder;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.List;

public class TPAdapter extends ArrayAdapter<Transponder> {
    public TPAdapter(MainActivity mainActivity, int id, List<Transponder> mTranzpoder) {
        super(mainActivity,id,mTranzpoder);
    }

    @NonNull
    @Override
    public Filter getFilter() {
        return super.getFilter();
    }

    static class Holder
    {
        TextView name;
        CheckBox fav;
    }
    class cx implements CompoundButton.OnCheckedChangeListener
    {

        private final Transponder transponder;

        public cx(Transponder s)
        {

            transponder = s;
        }
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {



            transponder.fav=buttonView.isChecked()?1:0;

            ((MainActivity) TPAdapter.this.getContext()).simpleDb.UpdateTransponder_fav(transponder);

        }
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Transponder trans = getItem(position);
        final Holder mHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(this.getContext()).inflate(R.layout.simple_tp, null);
            mHolder =new Holder();
            mHolder.name = convertView.findViewById(R.id.tp_text);   // id:sat_spinner_name_text
            mHolder.fav = convertView.findViewById(R.id.fav2);
            mHolder.fav.setTag(position);



            convertView.setTag(mHolder);
        }
        else
        {
            mHolder = (Holder)convertView.getTag();
        }
        mHolder.fav.setOnCheckedChangeListener(new cx(trans));
        if (trans != null) {
            mHolder.fav.setChecked(trans.fav==1);
            mHolder.name.setText(trans.toString());
        }

        return convertView;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }



    @Override
    public long getItemId(int position) {
        return 0;
    }
}
