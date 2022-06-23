package com.xobyx.satfinder.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;

import com.xobyx.satfinder.R;
import com.xobyx.satfinder.base.ChannelBase;
import java.util.ArrayList;
import java.util.List;

public class ChannelAdapter extends ArrayAdapter<ChannelBase> {
    static class ChannelViewHolder {
        ImageView img_channel_type;
        TextView txt_channel_name;
        ImageView img_iv_channel_tv;


    }

    private List<ChannelBase> channels;
    private final Context context;

    public ChannelAdapter(Context context, List<ChannelBase> list) {
        super(context,R.layout.adapter_channel_item);
        this.channels = list;
        this.context = context;
    }

    public void ClearList() {

        if(this.channels != null) {
            this.channels.clear();
            this.channels = null;
        }
    }

    public void add(ChannelBase channelBase) {
        if(this.channels == null) {
            this.channels = new ArrayList<>();
        }

        if(channelBase != null) {
            this.channels.add(channelBase);
            this.notifyDataSetChanged();
        }
    }

    @Override  // android.widget.Adapter
    public int getCount() {
        return this.channels == null ? 0 : this.channels.size();
    }

    @Override  // android.widget.Adapter
    public ChannelBase getItem(int position) {
        return this.channels.get(position);
    }

    @Override  // android.widget.Adapter
    public long getItemId(int position) {
        return position;
    }

    @NonNull
    @Override  // android.widget.Adapter
    public View getView(int position, View convertView, @NonNull ViewGroup parent) {
        ChannelViewHolder holder;
        if(convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.adapter_channel_item, parent, false);  // layout:adapter_channel_item
            holder = new ChannelViewHolder();

            holder.txt_channel_name = convertView.findViewById(R.id.tv_channel_name);   // id:tv_channel_name
            holder.img_iv_channel_tv = convertView.findViewById(R.id.iv_channel_tv);   // id:iv_channel_tv
            holder.img_channel_type = convertView.findViewById(R.id.iv_channel_type);   // id:iv_channel_type
            convertView.setTag(holder);
        }
        else {
            holder = (ChannelViewHolder)convertView.getTag();
        }

        ChannelBase channelBase = this.channels.get(position);
        holder.txt_channel_name.setText(" " + (position + 1) + ".) " + channelBase.getChannelName());
        holder.img_channel_type.setVisibility(0);
        if(channelBase.isPaidChannel()) {
            holder.img_channel_type.setVisibility(0);
            holder.img_channel_type.setImageResource(R.drawable.ic_channel_dollers);   // drawable:ic_channel_dollers
        }
        else {
            holder.img_channel_type.setVisibility(View.INVISIBLE);
        }

        if(channelBase.isTvChannel()) {
            holder.img_iv_channel_tv.setImageResource(R.drawable.ic_channel_tv);   // drawable:ic_channel_tv
            holder.img_iv_channel_tv.setVisibility(0);
            return convertView;
        }

        holder.img_iv_channel_tv.setImageResource(R.drawable.ic_channel_radio);   // drawable:ic_channel_radio
        return convertView;
    }
}

