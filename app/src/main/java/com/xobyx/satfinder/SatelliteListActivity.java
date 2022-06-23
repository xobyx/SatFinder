package com.xobyx.satfinder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.PopupMenu;
import androidx.fragment.app.FragmentActivity;

public class SatelliteListActivity extends FragmentActivity implements View.OnClickListener, AdapterView.OnItemLongClickListener, ExpandableListView.OnChildClickListener, ExpandableListView.OnGroupClickListener, PopupMenu.OnMenuItemClickListener {
    private SatelliteListAdapter satelliteListAdapter;
    private ExpandableListView expandableListView;
    private ImageView imageView;
    private int mGroup;
    private int mChild;
    private final String name;

    public SatelliteListActivity() {
        this.name = "Satellite";
    }

    @Override  // android.app.Activity
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 16) {
            this.satelliteListAdapter.SetSatelliteListFromDB();
            Intent v1 = new Intent();
            v1.putExtra("sat_pos", -1);
            v1.putExtra("tp_pos", -1);
            this.setResult(3, v1);
        }
    }

    @Override  // android.widget.ExpandableListView$OnChildClickListener
    public boolean onChildClick(ExpandableListView arg1, View arg2, int arg3, int arg4, long arg5) {
        int v1 = (int) arg2.getTag(R.id.sat_group);   // id:sat_group
        int v2 = (int) arg2.getTag(R.id.sat_child);   // id:sat_child
        Intent v3 = new Intent();
        v3.putExtra("sat_pos", v1);
        v3.putExtra("tp_pos", v2);
        this.setResult(3, v3);
        this.finish();
        return false;
    }

    @Override  // android.view.View$OnClickListener
    public void onClick(View arg2) {
        if(arg2 == this.imageView) {
            this.startActivityForResult(new Intent(this, SatEditActivity.class), 16);
        }
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle arg2) {
        super.onCreate(arg2);
        this.setContentView(R.layout.satellite_main_list);   // layout:satellite_main_list
        this.expandableListView = this.findViewById(R.id.satellite_main_list);   // id:satellite_main_list
        this.imageView = this.findViewById(R.id.sat_add_button);   // id:sat_add_button
        this.satelliteListAdapter = new SatelliteListAdapter(this);
        this.expandableListView.setAdapter(this.satelliteListAdapter);
        this.expandableListView.setOnGroupClickListener(this);
        this.expandableListView.setOnChildClickListener(this);
        this.expandableListView.setOnItemLongClickListener(this);
        this.imageView.setOnClickListener(this);
    }

    @Override  // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
        this.satelliteListAdapter = null;
    }

    @Override  // android.widget.ExpandableListView$OnGroupClickListener
    public boolean onGroupClick(ExpandableListView parent, View v, int groupPosition, long id) {
        int vTag = (int)v.getTag(R.id.sat_group);   // id:sat_group
        int vTag1 = (int)v.getTag(R.id.sat_child);   // id:sat_child
        if(parent != this.expandableListView) {
            Transponder child = this.satelliteListAdapter.getChild(vTag, vTag1);
            if(child.Channels == null) {
                child.Channels = DBManager.getInctance(this).getTransponderChannels(child.tpId);
            }

            if(child.Channels != null && child.Channels.size() > 0) {
                return false;
            }

            Intent intent = new Intent();
            intent.putExtra("sat_pos", vTag);
            intent.putExtra("tp_pos", vTag1);
            this.setResult(3, intent);
            this.finish();
        }

        return false;
    }

    @Override
    public boolean onItemLongClick(AdapterView parent, View view, int position, long id) {
        Log.i(this.name, "onItemLongClick " + position);
        int tag = (int) view.getTag(R.id.sat_group);   // id:sat_group
        int tag1 = (int) view.getTag(R.id.sat_child);   // id:sat_child
        this.satelliteListAdapter.getGroup(tag);
        this.mGroup = tag;
        this.mChild = tag1;
        PopupMenu popupMenu = new PopupMenu(this, view);
        popupMenu.getMenuInflater().inflate(R.menu.menu, popupMenu.getMenu());  // menu:menu
        popupMenu.setOnMenuItemClickListener(this);
        popupMenu.show();
        return true;
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch(menuItem.getItemId()) {
            case R.id.menu_delete: {  // id:menu_delete
                break;
            }
            case R.id.menu_edit: {  // id:menu_edit
                Satellite satellite = this.satelliteListAdapter.SatelliteList.get(this.mGroup);
                Intent intent = new Intent(this, SatEditActivity.class);
                intent.putExtra("Satellite", satellite.satelite_id);
                this.startActivityForResult(intent, 16);
                return false;
            }
            default: {
                return false;
            }
        }

        Satellite satellite = this.satelliteListAdapter.SatelliteList.get(this.mGroup);
        if(this.mChild == -1) {
            this.satelliteListAdapter.SatelliteList.remove(this.mGroup);
            DBManager.getInctance(this).DeleteSatellite_db(satellite);
            this.satelliteListAdapter.notifyDataSetChanged();

            Intent intent = new Intent();
            intent.putExtra("sat_pos", -1);
            intent.putExtra("tp_pos", -1);
            this.setResult(3, intent);

        }
        else {
            if(satellite.mTransponders.size() <= 1) {
                return false;
            }

            Transponder transponder = satellite.mTransponders.get(this.mChild);
            satellite.mTransponders.remove(this.mChild);
            DBManager.getInctance(this).DeleteTransponder_Channels(transponder);
            this.satelliteListAdapter.notifyDataSetChanged();

            Intent intent = new Intent();
            intent.putExtra("sat_pos", -1);
            intent.putExtra("tp_pos", -1);
            this.setResult(3, intent);
        }

        return false;
    }
}

