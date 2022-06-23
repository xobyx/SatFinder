package com.xobyx.satfinder;

import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.fragment.app.Fragment;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class FieldStrengthFragment extends Fragment implements AdapterView.OnItemSelectedListener {
    public final String name;
    public Spinner spn1;
    public Spinner spn2;
    public Spinner spn3;
    public Spinner spn4;
    public FieldArrayAdapter arrayAdapter;
    public FieldArrayAdapter arrayAdapter1;
    public FieldArrayAdapter arrayAdapter2;
    public FieldArrayAdapter arrayAdapter3;
    public TextView mText1;
    public TextView mText2;
    public TextView mText3;
    public TextView mText4;
    public VerticalProgress mprog1;
    public DVBFinder mDvbFinder;
    public Handler mHandler;
    public int mPos1;
    public int mPos2;
    public int mPos3;
    public int mPos4;
    public List<Transponder> mTps;
    public VerticalProgress mprog2;
    public VerticalProgress mprog3;
    public VerticalProgress mprog4;
    public int mActive_Frequ;
    public int mVal1;
    public int mVal2;
    public int mVal3;
    public int mVal4;
    public Timer timer;
    TimerTask timerTask;

    static {
        System.loadLibrary("xobyx-jni");
    }

    public FieldStrengthFragment() {
        this.name = "field";
        this.mActive_Frequ = 0;
        this.mHandler = new Handler(new FieldStrengthHandler(this));
    }

    @Override  // android.support.v4.app.i
    public void onDestroy() {
        super.onDestroy();
        if(this.timer != null) {
            this.timer.cancel();
            this.timer.purge();
        }
    }


    @Override  // android.support.v4.app.i
    public View onCreateView(LayoutInflater arg2, ViewGroup arg3, Bundle arg4) {
        Log.i("field", "FieldStrengthFragment onCreateView");
        View view = arg2.inflate(R.layout.field_strength_layout, arg3, true);  // layout:field_strength_layout
        this.spn1 = view.findViewById(R.id.field_tp_spn1);   // id:field_tp_spn1
        this.spn2 = view.findViewById(R.id.field_tp_spn2);   // id:field_tp_spn2
        this.spn3 = view.findViewById(R.id.field_tp_spn3);   // id:field_tp_spn3
        this.spn4 = view.findViewById(R.id.field_tp_spn4);   // id:field_tp_spn4
        this.mText1 = view.findViewById(R.id.field_tp_value1);   // id:field_tp_value1
        this.mText2 = view.findViewById(R.id.field_tp_value2);   // id:field_tp_value2
        this.mText3 = view.findViewById(R.id.field_tp_value3);   // id:field_tp_value3
        this.mText4 = view.findViewById(R.id.field_tp_value4);   // id:field_tp_value4
        this.mprog1 = view.findViewById(R.id.field_tp_progress1);   // id:field_tp_progress1
        this.mprog2 = view.findViewById(R.id.field_tp_progress2);   // id:field_tp_progress2
        this.mprog3 = view.findViewById(R.id.field_tp_progress3);   // id:field_tp_progress3
        this.mprog4 = view.findViewById(R.id.field_tp_progress4);   // id:field_tp_progress4
        this.update_vertical_progress(0, 0);
        this.update_vertical_progress(1, 0);
        this.update_vertical_progress(2, 0);
        this.update_vertical_progress(3, 0);
        this.mDvbFinder = ((MainActivity) Objects.requireNonNull(this.getActivity())).mDvbFinder;
        return view;
    }

    public void Update_transponder_list(List<Transponder> pTransponders, int pTpPos) {
        this.mTps = pTransponders;
        this.mPos4 = pTpPos;
        this.mPos3 = pTpPos;
        this.mPos2 = pTpPos;
        this.mPos1 = pTpPos;
        this.arrayAdapter = new FieldArrayAdapter(this, this.getContext(), R.layout.simple_center, pTransponders,0);  // layout:simple_center
        this.arrayAdapter.setDropDownViewResource(R.layout.simple_center);   // layout:simple_center
        this.spn1.setAdapter(this.arrayAdapter);
        this.spn1.setSelection(this.mPos1);
        this.spn1.setOnItemSelectedListener(this);
        this.arrayAdapter1 = new FieldArrayAdapter(this, this.getContext(), R.layout.simple_center, pTransponders,1);  // layout:simple_center
        this.arrayAdapter1.setDropDownViewResource(R.layout.simple_center);   // layout:simple_center
        this.spn2.setAdapter(this.arrayAdapter1);
        this.spn2.setSelection(this.mPos2);
        this.spn2.setOnItemSelectedListener(this);
        this.arrayAdapter2 = new FieldArrayAdapter(this, this.getContext(), R.layout.simple_center, pTransponders,2);  // layout:simple_center
        this.arrayAdapter2.setDropDownViewResource(R.layout.simple_center);   // layout:simple_center
        this.spn3.setAdapter(this.arrayAdapter2);
        this.spn3.setSelection(this.mPos3);
        this.spn3.setOnItemSelectedListener(this);
        this.arrayAdapter3 = new FieldArrayAdapter(this, this.getContext(), R.layout.simple_center, pTransponders,3);  // layout:simple_center
        this.arrayAdapter3.setDropDownViewResource(R.layout.simple_center);   // layout:simple_center
        this.spn4.setAdapter(this.arrayAdapter3);
        this.spn4.setSelection(this.mPos4);
        this.spn4.setOnItemSelectedListener(this);
    }

    public void ensureAnimationInfo() {
        if(this.timerTask != null && this.timer != null) {
            this.timerTask.cancel();
            this.timer.cancel();
            this.timer.purge();
            this.timerTask = null;
            this.timer = null;
        }
    }

    public void update_vertical_progress(int pindex, int val) {
        if(pindex == 0) {
            this.mVal1 = val;
            this.mText1.setText(this.mVal1 + "%");
            this.mprog1.setVal(val);
        }
        else {
            if(pindex == 1) {
                this.mVal2 = val;
                this.mText2.setText(this.mVal2 + "%");
                this.mprog2.setVal(val);
                return;
            }

            if(pindex == 2) {
                this.mVal3 = val;
                this.mText3.setText(this.mVal3 + "%");
                this.mprog3.setVal(val);
                return;
            }

            if(pindex == 3) {
                this.mVal4 = val;
                this.mText4.setText(this.mVal4 + "%");
                this.mprog4.setVal(val);

            }
        }
    }

    public void StartTimer() {
        if(this.timer == null && this.timerTask == null) {
            this.timerTask = new TimerTask(){

                @Override
                public void run() {
                    int dvbFinder_status = mDvbFinder.DVBFinder_Status;

                    if(dvbFinder_status != DVB.CONNECTED_AND_CHARS_SET) {
                        return;
                    }

                    Message message = new Message();
                    message.what = 16;
                    mHandler.sendMessage(message);
                }
            };
            this.timer = new Timer();
            this.timer.schedule(this.timerTask, 3000L, 3000L);
        }
    }

    /**
     * native method send progress bar index
     *
     * @param index progress bar index
     *
     */
    public native void SendCurrentTp(int index);

    public void setActiveFreq_color(int pindex) {
        Spinner[] spinners = new Spinner[4];
        int i = 0;
        spinners[0] = this.spn1;
        spinners[1] = this.spn2;
        spinners[2] = this.spn3;
        spinners[3] = this.spn4;
        TextView[] textViews = {this.mText1, this.mText2, this.mText3, this.mText4};
        this.mActive_Frequ = pindex;
        while(i < 4) {
            if(i == pindex) {
                TextView v4 = (TextView)spinners[i].getSelectedView();
                if(v4 != null) {
                    v4.setTextColor(this.getActivity().getColor(R.color.red));   // color:red
                    textViews[i].setTextColor(this.getActivity().getColor(R.color.red));   // color:red
                }
            }
            else {
                TextView selectedView = (TextView)spinners[i].getSelectedView();
                if(selectedView != null) {
                    selectedView.setTextColor(this.getActivity().getColor(R.color.white));   // color:white
                    textViews[i].setTextColor(this.getActivity().getColor(R.color.white));   // color:white
                }
            }

            ++i;
        }
    }

    @Override  // android.widget.AdapterView$OnItemSelectedListener
    public void onItemSelected(AdapterView parent, View view, int position, long id) {
        if(parent == this.spn1) {
            if(position == this.mPos1) {
                return;
            }

            this.mPos1 = position;
            return;
        }

        if(parent == this.spn2) {
            if(position == this.mPos2) {
                return;
            }

            this.mPos2 = position;
            return;
        }

        if(parent == this.spn3) {
            if(position == this.mPos3) {
                return;
            }

            this.mPos3 = position;
            return;
        }

        if(parent == this.spn4) {
            if(position == this.mPos4) {
                return;
            }

            this.mPos4 = position;
        }
    }

    @Override  // android.widget.AdapterView$OnItemSelectedListener
    public void onNothingSelected(AdapterView parent) {
    }
}

