package com.xobyx.satfinder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;

import java.util.List;

public class SatelliteListAdapter extends BaseExpandableListAdapter {
    private final Context context;
    public List<Satellite> SatelliteList;

    public SatelliteListAdapter(Context context) {
        this.context = context;
        this.SatelliteList = DBManager.getInctance(this.context).GetAllSatellites();
    }

    public void SetSatelliteListFromDB() {
        this.SatelliteList = DBManager.getInctance(this.context).GetAllSatellites();
        this.notifyDataSetChanged();
    }

    @Override  // android.widget.ExpandableListAdapter
    public Transponder getChild(int groupPosition, int childPosition) {
        Satellite sat = this.SatelliteList.get(groupPosition);
        return sat.mTransponders.get(childPosition);
    }

    @Override  // android.widget.ExpandableListAdapter
    public long getChildId(int groupPosition, int childPosition) {
        return 0L;
    }

    @Override  // android.widget.ExpandableListAdapter
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Satellite satellite = this.SatelliteList.get(groupPosition);
        if (satellite.mTransponders == null || satellite.mTransponders.size() == 0) {
            satellite.mTransponders = DBManager.getInctance(this.context).GetTransponderList(satellite.satelite_id);
        }

        Transponder transponder = satellite.mTransponders.get(childPosition);
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.simple_expendable, null);  // layout:simple_expendable
        }

        ExpandableListView expandableListView = convertView.findViewById(R.id.sat_tp_channel_list);  // id:sat_tp_channel_list
        if (transponder.Channels == null) {
            transponder.Channels = DBManager.getInctance(this.context).getTransponderChannels(transponder.tpId);
        }

        expandableListView.setGroupIndicator(null);
        if (transponder.mChannelListAdpt == null) {
            transponder.mChannelListAdpt = new TranspondersExpandableList(this.context, satellite.mTransponders, groupPosition, childPosition);
        }

        expandableListView.setAdapter(transponder.mChannelListAdpt);
        expandableListView.invalidate();
        expandableListView.setOnChildClickListener(((SatelliteListActivity) this.context));
        expandableListView.setOnGroupClickListener(((SatelliteListActivity) this.context));
        convertView.setTag(R.id.sat_group, groupPosition);  // id:sat_group
        convertView.setTag(R.id.sat_child, childPosition);  // id:sat_child
        return convertView;
    }

    @Override  // android.widget.ExpandableListAdapter
    public int getChildrenCount(int groupPosition) {
        Satellite v3 = this.SatelliteList.get(groupPosition);
        if (v3.mTransponders == null || v3.mTransponders.size() == 0) {
            v3.mTransponders = DBManager.getInctance(this.context).GetTransponderList(v3.satelite_id);
        }

        return v3.mTransponders == null ? 0 : v3.mTransponders.size();
    }
    @Override  // android.widget.ExpandableListAdapter
    public Object getGroup(int groupPosition) {
        return this.SatelliteList.get(groupPosition);
    }

    @Override  // android.widget.ExpandableListAdapter
    public int getGroupCount() {
        return this.SatelliteList.size();
    }

    @Override  // android.widget.ExpandableListAdapter
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override  // android.widget.ExpandableListAdapter
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        Holder mHolder;
        if (convertView == null) {
            convertView = LayoutInflater.from(this.context).inflate(R.layout.satellite_item, null);  // layout:satellite_item
            mHolder = new Holder();
            mHolder.nameText = convertView.findViewById(R.id.satellite_list_name);   // id:satellite_list_name
            mHolder.posText = convertView.findViewById(R.id.satellite_list_position);   // id:satellite_list_position
            mHolder.fav = convertView.findViewById(R.id.edit_sat_fav);


            convertView.setTag(R.id.about_chipid, mHolder);
        } else {
            mHolder = (Holder) convertView.getTag(R.id.about_chipid);
        }


        Satellite sat = this.SatelliteList.get(groupPosition);
        mHolder.fav.setOnCheckedChangeListener((v,t)->{
            sat.isFav = t;
            DBManager.getInctance(context).update_satellite_fav(sat);
        });
        mHolder.nameText.setText(sat.name);
        mHolder.posText.setText(sat.position <= 0.0f ? -sat.position / 10.0f + " " + this.context.getResources().getString(R.string.strE) : sat.position / 10.0f + " " + this.context.getResources().getString(R.string.strW));  // string:strW "W"
        mHolder.fav.setChecked(sat.isFav);

        convertView.setTag(R.id.sat_group, groupPosition);  // id:sat_group
        convertView.setTag(R.id.sat_child, -1);  // id:sat_child
        return convertView;
    }

    @Override  // android.widget.ExpandableListAdapter
    public boolean hasStableIds() {
        return true;
    }

    @Override  // android.widget.ExpandableListAdapter
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    static class Holder {

        TextView nameText;
        TextView posText;
        CheckBox fav;
    }

}

