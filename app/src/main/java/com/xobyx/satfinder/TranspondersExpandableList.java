package com.xobyx.satfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.xobyx.satfinder.base.ChannelBase;

import java.util.List;

public class TranspondersExpandableList extends BaseExpandableListAdapter {
    private final Context mcontext;
    private final Transponder mtransponder;
    private final List<ChannelBase> ChannelsList;
    private final int mTransPos;
    private final int mSatPos;
//satellite.mTransponders, groupPosition, childPosition
    public TranspondersExpandableList(Context context, List<Transponder> TranspondersList, int SatPos, int TransPos) {
        this.mcontext = context;
        this.mTransPos = TransPos;
        this.mSatPos = SatPos;
        this.mtransponder = TranspondersList.get(this.mTransPos);
        int m_tp_id = mtransponder.tpId;
        this.ChannelsList = DBManager.getInctance(this.mcontext).getTransponderChannels(m_tp_id);
    }

    @Override  // android.widget.ExpandableListAdapter
    public ChannelBase getChild(int groupPosition, int childPosition) {
        return this.ChannelsList == null ? null : this.ChannelsList.get(groupPosition);
    }
    static class Holder1
    {
        TextView txt_channel_name;
        ImageView img_iv_channel_tv;
        ImageView img_channel_type ;
    }
    @Override  // android.widget.ExpandableListAdapter
    public long getChildId(int groupPosition, int childPosition) {
        return 0L;
    }

    @Override  // channels
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if(this.ChannelsList == null) {
            return null;
        }
        final Holder1 mHolder;
        if(convertView == null) {
            convertView = LayoutInflater.from(this.mcontext).inflate(R.layout.transponder_channel, parent, false);  // layout:transponder_channel
            mHolder= new Holder1();
            mHolder. txt_channel_name = convertView.findViewById(R.id.transponder_list_channel);// id:transponder_list_channel
            mHolder. img_iv_channel_tv = convertView.findViewById(R.id.iv_channel_tv1);   // id:iv_channel_tv
            mHolder. img_channel_type = convertView.findViewById(R.id.iv_channel_type1);   // id:iv_channel_type
            convertView.setTag(R.id.aligned,mHolder);
        }
        else
        {
            mHolder= (Holder1) convertView.getTag(R.id.aligned);
        }
        ChannelBase obj = this.ChannelsList.get(childPosition);


        mHolder.txt_channel_name.setText(" " + (childPosition + 1) + ".) " + obj.getChannelName());
        mHolder.img_channel_type.setVisibility(0);
        if((obj.getChannelType() & 16) == 16) {
            mHolder.img_channel_type.setVisibility(0);
            mHolder.img_channel_type.setImageResource(R.drawable.ic_channel_dollers);   // drawable:ic_channel_dollers
        }
        else {
            mHolder.img_channel_type.setVisibility(View.INVISIBLE);
        }

        if((obj.getChannelType() & 0x20) == 0x20) {
            mHolder.img_iv_channel_tv.setImageResource(R.drawable.ic_channel_tv);   // drawable:ic_channel_tv
            mHolder.img_iv_channel_tv.setVisibility(0);
            return convertView;
        }

        mHolder.img_iv_channel_tv.setImageResource(R.drawable.ic_channel_radio);   // drawable:ic_channel_radio
        convertView.setTag(R.id.sat_group, this.mSatPos);  // id:sat_group
        convertView.setTag(R.id.sat_child, this.mTransPos);  // id:sat_child
        return convertView;
    }

    @Override  // android.widget.ExpandableListAdapter
    public int getChildrenCount(int groupPosition) {
        return this.ChannelsList == null ? 0 : this.ChannelsList.size();
    }

    @Override  // android.widget.ExpandableListAdapter
    public Object getGroup(int groupPosition) {
        return this.mtransponder;
    }

    @Override  // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return 1;
    }

    @Override  // android.widget.ExpandableListAdapter
    public long getGroupId(int groupPosition) {
        return 0L;
    }
static class Holder
{
    TextView freqText ;
    TextView symbolText;
    TextView polioText;
    ImageView indicator;
    CheckBox fav;
}
    @Override  // android.widget.ExpandableListAdapter
    public View getGroupView(int groupPosition, boolean isExpanded, View view, ViewGroup parent) {
        final Holder mHolder;
        int i = 0;
        if(view == null) {
            view = LayoutInflater.from(this.mcontext).inflate(R.layout.transponder_item, parent, false);
            mHolder= new Holder();
            mHolder. freqText = view.findViewById(R.id.transponder_list_frequency);
            mHolder. symbolText = view.findViewById(R.id.transponder_list_symbol_rate);
            mHolder.polioText = view.findViewById(R.id.transponder_list_polization);
            mHolder.indicator = view.findViewById(R.id.tp_group_indicator);
            mHolder. fav = view.findViewById(R.id.edit_tp_fav);

            view.setTag(R.id.accelerate,mHolder);

        }
        else
        {
            mHolder = (Holder) view.getTag(R.id.accelerate);
        }


        if(this.mtransponder.Channels == null || this.mtransponder.Channels.size() <= 0) {
            i = View.INVISIBLE;
        }

        mHolder.indicator.setVisibility(i);
        mHolder.fav.setChecked(mtransponder.fav==1);
        mHolder.fav.setOnCheckedChangeListener(new v(this.mtransponder));
        mHolder.freqText.setText(String.valueOf(this.mtransponder.mFrequency / 1000));
        mHolder.polioText.setText(this.mtransponder.mPolization <= 0 ? R.string.strH : R.string.strV);  // string:strH "H"
        mHolder.symbolText.setText(String.valueOf(this.mtransponder.mSymbolRate / 1000));
        view.setTag(R.id.sat_group, this.mSatPos);  // id:sat_group
        view.setTag(R.id.sat_child, this.mTransPos);  // id:sat_child
        return view;
    }

    @Override  // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return false;
    }

    @Override  // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

    private class v implements CompoundButton.OnCheckedChangeListener {
        final private Transponder trans;

        public v(Transponder mtransponder) {

            trans = mtransponder;
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            trans.fav=buttonView.isChecked()?1:0;
            DBManager.getInctance(mcontext).UpdateTransponder_fav(trans);


        }
    }
}

