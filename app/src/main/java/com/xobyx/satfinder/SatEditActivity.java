package com.xobyx.satfinder;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioButton;
import androidx.fragment.app.FragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class SatEditActivity extends FragmentActivity implements DialogInterface.OnShowListener, View.OnClickListener, AdapterView.OnItemClickListener {
    private final String a;
    public List<Transponder> mTrasponderList;
    private SatEditArrayAdapter mSatEditAdap;
    private EditText sat_name_input;
    private EditText sat_position_input;
    private RadioButton sat_pos_west_radio;
    private ImageView sat_edit_tp_add;
    private Button sat_edit_commit;
    private Button sat_edit_cancel;
    private AlertDialog tp_editor_dialog;
    private EditText sat_tp_freq_input;
    private EditText sat_tp_symbolrate_input;
    private RadioButton sat_tp_radio_v;
    private Button tp_editor_dialog_ok_b;
    private Satellite mSatellite;

    public SatEditActivity() {
        this.a = SatEditActivity.class.getSimpleName();
    }

    private boolean SaveEdit() {
        String sat_name = this.sat_name_input.getText().toString();
        String sat_pos = this.sat_position_input.getText().toString();
        float sat_pos_f = sat_pos.length() <= 0 ? 0.0f : Float.parseFloat(sat_pos);
        if(sat_pos_f < 0.0f) {
            return false;
        }

        if(this.mTrasponderList.size() == 0) {
            return false;
        }

        float v = this.sat_pos_west_radio.isChecked() ? -10.0f : 10.0f;
        this.mSatellite.preset = 0;
        this.mSatellite.name = sat_name;
        this.mSatellite.position = sat_pos_f * v;
        if(this.mSatellite.satelite_id > 0) {
            DBManager.getInctance(this).deleteAllTransponderChannelsForSatId(this.mSatellite.satelite_id);
        }

        mSatellite.satelite_id = DBManager.getInctance(this).update_check_satellite(this.mSatellite);
        Log.d(this.a, "add Satellite and SatelliteId is:" + this.mSatellite.satelite_id);
        for(Transponder transponder: this.mTrasponderList) {
            transponder.satellite_id = this.mSatellite.satelite_id;
            transponder.tpId = DBManager.getInctance(this).Check_UpdateTransponder(transponder);
            Log.d(this.a, "add tp and mTipId is:" + transponder.tpId);
        }

        return true;
    }

    private boolean AddNewTp() {
        if(this.sat_tp_freq_input.getText().length()> 0 && this.sat_tp_symbolrate_input.getText().length() > 0) {
            int tp_freq = Integer.parseInt(this.sat_tp_freq_input.getText().toString());
            int symbol_rate = Integer.parseInt(this.sat_tp_symbolrate_input.getText().toString());
            boolean isVertical = this.sat_tp_radio_v.isChecked();
            if(tp_freq != 0 && symbol_rate != 0) {
                Transponder transponder = new Transponder(0, 0, tp_freq * 1000, symbol_rate * 1000, isVertical?1:0,0);
                this.mTrasponderList.add(transponder);
                this.mSatEditAdap.notifyDataSetChanged();
                return true;
            }
        }

        return false;
    }

    @Override  // android.view.View$OnClickListener
    public void onClick(View view) {
        if(view.getTag() != null) {
            int i = (int) view.getTag();
            this.mTrasponderList.remove(i);
            this.mSatEditAdap.notifyDataSetChanged();
            Log.d(this.a, "onClick " + i);
            return;
        }

        if(view == this.sat_edit_tp_add) {
            this.tp_editor_dialog = new AlertDialog.Builder(this).setTitle(R.string.strTpEditor).setPositiveButton(R.string.strTpEditor, null).setNegativeButton(R.string.strTpEditor, null).create();  // string:strTpEditor "TP Editor"
            this.tp_editor_dialog.setTitle(R.string.strTpEditor);  // string:strTpEditor "TP Editor"
            View inflate = LayoutInflater.from(this).inflate(R.layout.tp_input, null);  // layout:tp_input
            this.tp_editor_dialog.setView(inflate);
            this.tp_editor_dialog.setOnShowListener(this);
            this.tp_editor_dialog.show();
            return;
        }

        if(view == this.tp_editor_dialog_ok_b) {
            Log.i(this.a, "on dialog sumbmit click");
            if(this.AddNewTp()) {
                this.tp_editor_dialog.dismiss();
                this.tp_editor_dialog = null;
            }
        }
        else if(view == this.sat_edit_commit) {
            if(this.SaveEdit()) {
                this.setResult(16, null);
                this.finish();
            }
        }
        else if(view == this.sat_edit_cancel) {
            this.finish();
        }
    }

    @Override  // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.sat_edit);   // layout:sat_edit
        Intent intent = this.getIntent();
        this.sat_name_input = this.findViewById(R.id.sat_name_input);   // id:sat_name_input
        this.sat_position_input = this.findViewById(R.id.sat_position_input);   // id:sat_position_input
        RadioButton sat_pos_east_radio = this.findViewById(R.id.sat_pos_east_radio);   // id:sat_pos_east_radio
        this.sat_pos_west_radio = this.findViewById(R.id.sat_pos_west_radio);   // id:sat_pos_west_radio
        this.sat_edit_tp_add = this.findViewById(R.id.sat_edit_tp_add);   // id:sat_edit_tp_add
        ListView sat_edit_tp_list = this.findViewById(R.id.sat_edit_tp_list);   // id:sat_edit_tp_list
        this.sat_edit_commit = this.findViewById(R.id.sat_edit_commit);   // id:sat_edit_commit
        this.sat_edit_cancel = this.findViewById(R.id.sat_edit_cancel);   // id:sat_edit_cancel
        if(intent.getExtras() == null) {
            this.mSatellite = new Satellite();
            Satellite satellite = this.mSatellite;
            satellite.satelite_id = DBManager.getInctance(this).GetFirstSatellite_id() + 1;
            this.mTrasponderList = new ArrayList<>();
        }
        else {
            int sat_id = intent.getIntExtra("Satellite", 0);
            Satellite db = DBManager.getInctance(this).getSatellite_db(sat_id);
            if(db.preset == 1) {
                this.mSatellite = new Satellite(db);
                this.mSatellite.name = this.mSatellite.name + "-NEW";
            }
            else {
                this.mSatellite = db;
            }

            this.sat_name_input.setText(this.mSatellite.name);
            float sat_pos_d10 = this.mSatellite.position / 10.0f;
            this.sat_position_input.setText(Float.toString(sat_pos_d10 >= 0.0f ? this.mSatellite.position / 10.0f : -sat_pos_d10));

            this.mTrasponderList = mSatellite.mTransponders == null ? new ArrayList<>() : mSatellite.mTransponders;
            if(this.mSatellite.position > 0.0f) {
                sat_pos_east_radio.setChecked(true);
            }
            else {
                this.sat_pos_west_radio.setChecked(true);
            }
        }

        this.sat_edit_tp_add.setOnClickListener(this);
        this.sat_edit_commit.setOnClickListener(this);
        this.sat_edit_cancel.setOnClickListener(this);
        this.mSatEditAdap = new SatEditArrayAdapter(this, this, R.layout.tp_edit, this.mTrasponderList);  // layout:tp_edit
        sat_edit_tp_list.setAdapter(this.mSatEditAdap);
        sat_edit_tp_list.setOnItemClickListener(this);
    }

    @Override  // android.widget.AdapterView$OnItemClickListener
    public void onItemClick(AdapterView parent, View view, int position, long id) {
        Log.i(this.a, "onItemClick " + view);
    }

    @Override  // android.content.DialogInterface$OnShowListener
    public void onShow(DialogInterface dialogInterface) {
        this.tp_editor_dialog_ok_b = this.tp_editor_dialog.getButton(-1);
        this.tp_editor_dialog_ok_b.setOnClickListener(this);
        this.sat_tp_freq_input = this.tp_editor_dialog.findViewById(R.id.sat_tp_freq_input);   // id:sat_tp_freq_input
        this.sat_tp_symbolrate_input = this.tp_editor_dialog.findViewById(R.id.sat_tp_symbolrate_input);   // id:sat_tp_symbolrate_input
        RadioButton sat_tp_radio_h = this.tp_editor_dialog.findViewById(R.id.sat_tp_radio_h);   // id:sat_tp_radio_h
        this.sat_tp_radio_v = this.tp_editor_dialog.findViewById(R.id.sat_tp_radio_v);   // id:sat_tp_radio_v
    }
}

