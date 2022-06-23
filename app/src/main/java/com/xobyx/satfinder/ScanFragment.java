package com.xobyx.satfinder;

import android.os.Bundle;
import android.os.Handler;

import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.xobyx.satfinder.base.ChannelBase;

import java.util.*;

public class ScanFragment extends Fragment implements View.OnClickListener {
    private Button scan_button;
    public TextView scan_cur_tp;
    private ProgressBar scan_progress;
    private TextView scan_progress_text;
    private ArrayAdapter<Transponder> mTranspondersAdp;
    private List<Transponder> mSTransponderList;
    private Transponder mTransponder;
    private ArrayAdapter<ChannelBase> mChannelListAdp;
    private List<ChannelBase> mChannelList;
    private ProgressBar scan_strength_progress;
    private ProgressBar scan_quality_progress;
    private TextView scan_Strength_value;
    private TextView scan_quality_value;
    private TextView scan_tp_total;
    private TextView scan_channel_total;
    private boolean isScanRun;
    public int mState;
    public Timer mTimer;
    TimerTask mTimerTask;
    public Handler mScanHandler;

    public ScanFragment() {

        this.mState = 0;
        this.mScanHandler = new Handler(new ScanFragmentHandler(this));
    }


    @Override  // android.support.v4.app.i
    public void onDestroy() {
        super.onDestroy();
        this.stopScan();
    }

    @Override  // android.support.v4.app.i
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.i("SCAN", "FieldStrengthFragment onCreateView");
        View view = inflater.inflate(R.layout.blind_scan, container, true);  // layout:blind_scan
        this.scan_button = view.findViewById(R.id.scan_button);   // id:scan_button
        Button scan_save_button = view.findViewById(R.id.scan_save_button);   // id:scan_save_button
        this.scan_cur_tp = view.findViewById(R.id.scan_cur_tp);   // id:scan_cur_tp
        this.scan_progress = view.findViewById(R.id.scan_progress);   // id:scan_progress
        this.scan_progress_text = view.findViewById(R.id.scan_progress_text);   // id:scan_progress_text
        ListView scan_tp_list = view.findViewById(R.id.scan_tp_list);   // id:scan_tp_list
        ListView scan_channel_list = view.findViewById(R.id.scan_channel_list);   // id:scan_channel_list
        this.scan_strength_progress = view.findViewById(R.id.scan_strength_progress);   // id:scan_strength_progress
        this.scan_Strength_value = view.findViewById(R.id.scan_Strength_value);   // id:scan_Strength_value
        this.scan_quality_progress = view.findViewById(R.id.scan_quality_progress);   // id:scan_quality_progress
        this.scan_quality_value = view.findViewById(R.id.scan_quality_value);   // id:scan_quality_value
        this.scan_channel_total = view.findViewById(R.id.scan_channel_total);   // id:scan_channel_total
        this.scan_tp_total = view.findViewById(R.id.scan_tp_total);   // id:scan_tp_total
        this.scan_button.setTag("SCAN_BUTTON");
        scan_save_button.setTag("SAVE");
        this.scan_button.setOnClickListener(this);
        scan_save_button.setOnClickListener(this);
        this.isScanRun = false;
        this.mSTransponderList = new ArrayList<>();
        this.mChannelList = new ArrayList<>();
        this.mTranspondersAdp = new ArrayAdapter<>(Objects.requireNonNull(this.getContext()), R.layout.simple, this.mSTransponderList);  // layout:simple
        this.mChannelListAdp = new ArrayAdapter<>(this.getContext(), R.layout.simple, this.mChannelList);  // layout:simple
        scan_tp_list.setAdapter(this.mTranspondersAdp);
        scan_channel_list.setAdapter(this.mChannelListAdp);
        this.scan_strength_progress.setProgress(0);
        this.scan_Strength_value.setText("--");
        this.scan_quality_progress.setProgress(0);
        this.scan_quality_value.setText("--");
        this.scan_tp_total.setText("");
        this.scan_channel_total.setText("");
        return view;
    }

    public void SetScanTransponder(Transponder transponder) {
        this.mTransponder = new Transponder(transponder);
    }

    public void ensureAnimationInfo() {
        if(this.mTimerTask != null && this.mTimer != null) {
            this.mTimerTask.cancel();
            this.mTimer.cancel();
            this.mTimer.purge();
            this.mTimerTask = null;
            this.mTimer = null;
        }
    }

    public void UpdateUi(int p_tp_strength, int p_quality_value) {
        this.scan_Strength_value.setText(p_tp_strength + "%");
        this.scan_strength_progress.setProgress(p_tp_strength);
        if(this.getActivity()!=null) {
            if (p_tp_strength < 20) {
                this.scan_strength_progress.setProgressDrawable(this.getActivity().getDrawable(R.drawable.progress_darkred));   // drawable:progress_darkred
            } else if (p_quality_value > 0) {
                this.scan_strength_progress.setProgressDrawable(this.getActivity().getDrawable(R.drawable.progress_green));   // drawable:progress_green
            } else {
                this.scan_strength_progress.setProgressDrawable(this.getActivity().getDrawable(R.drawable.progress_lightred));   // drawable:progress_lightred
            }

            this.scan_quality_value.setText(p_quality_value + "%");
            this.scan_quality_progress.setProgress(p_quality_value);
            if (p_quality_value < 20) {
                this.scan_quality_progress.setProgressDrawable(this.getActivity().getDrawable(R.drawable.progress_darkred));   // drawable:progress_darkred
                return;
            }

            this.scan_quality_progress.setProgressDrawable(this.getActivity().getDrawable(R.drawable.progress_yellow));   // drawable:progress_yellow
            this.scan_strength_progress.setProgressDrawable(this.getActivity().getDrawable(R.drawable.progress_green));   // drawable:progress_green
        }
    }

    public void AddChannel(ChannelBase channel) {
        Transponder trans = this.mTransponder;
        if(trans.Channels == null) {
            trans.Channels = new ArrayList<>();
        }

        if(!this.mSTransponderList.contains(this.mTransponder)) {
            this.mSTransponderList.add(this.mTransponder);
            this.mTranspondersAdp.notifyDataSetChanged();
        }

        this.mTransponder.Channels.add(channel);
        this.mChannelList.add(channel);
        this.mChannelListAdp.notifyDataSetChanged();
        String strTP = this.getResources().getString(R.string.strTP );   // string:strTP "TP"
        this.scan_tp_total.setText(strTP + " " + this.mSTransponderList.size());
        String strChannels = this.getResources().getString(R.string.strChannel);   // string:strChannel "Channels"
        this.scan_channel_total.setText(strChannels + " " + this.mChannelList.size());
    }

    public boolean isScanRunning() {
        return this.isScanRun;
    }

    public void StopScan() {
        this.scan_button.setText(this.getResources().getString(R.string.strStart));   // string:strStart "Start"
        this.scan_progress.setProgress(0);
        this.scan_progress_text.setText("--");
        this.mTranspondersAdp.clear();
        this.mChannelListAdp.clear();
        this.mTranspondersAdp.notifyDataSetChanged();
        this.mChannelListAdp.notifyDataSetChanged();
        this.mSTransponderList.clear();
        this.mChannelList.clear();
        this.updateScanProgress(0);
        this.scan_strength_progress.setProgress(0);
        this.scan_Strength_value.setText("--");
        this.scan_quality_progress.setProgress(0);
        this.scan_quality_value.setText("--");
        this.scan_tp_total.setText("");
        this.scan_channel_total.setText("");
        this.ensureAnimationInfo();
        this.scan_cur_tp.setText(R.string.strProgress );   // string:strProgress "Scanning"
    }

    public void UpdateScanButtonStop() {
        this.scan_button.setText(R.string.strStart );   // string:strStart "Start"
        this.isScanRun = false;
    }

    public void updateScanProgress(int arg2) {
        this.scan_progress.setProgress(arg2);
        this.scan_progress_text.setText(arg2 + "%");
    }

    public void SetupTimer() {
        if(this.mTimer == null && this.mTimerTask == null) {
            this.mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    if(!isScanRun) {
                        return;
                    }

                    Message message = new Message();
                    message.what = 10;
                    ScanFragment.this.mScanHandler.sendMessage(message);
                }
            };
            this.mTimer = new Timer();
            this.mTimer.schedule(this.mTimerTask, 500L, 500L);
        }
    }

    public void stopScan() {
        this.StopScan();
        if(!this.isScanRun) {
            return;
        }

        MainActivity activity = (MainActivity) this.getActivity();
        if(activity!=null) activity.SendStartBlindScan();
        this.isScanRun = false;
    }

    @Override  // android.view.View$OnClickListener
    public void onClick(View v) {
        int i;
        String tag = (String)v.getTag();
        int hashCode = tag.hashCode();
        int v3 = 0;
        if(hashCode == -1740668108) {
            i = tag.equals("SCAN_BUTTON") ? 0 : -1;
        }
        else if(hashCode == 0x26B97D && (tag.equals("SAVE"))) {
            i = 1;
        }
        else {
            i = -1;
        }

        MainActivity activity = (MainActivity) this.getActivity();
        if(activity==null)return;
        if(i == 0) {
            if(this.isScanRun) {
                activity.SendStartBlindScan();
                this.scan_button.setText(this.getResources().getString(R.string.strStart));   // string:strStart "Start"
                this.scan_cur_tp.setText(R.string.strProgress );   // string:strProgress "Scanning"
                this.isScanRun = false;
                this.ensureAnimationInfo();
            }
            else {
                DVBFinder mDvbFinder = activity.mDvbFinder;
                int dvbFinder_status = mDvbFinder.DVBFinder_Status;

                if(dvbFinder_status == DVB.CONNECTED_AND_CHARS_SET) {
                    activity.Send_LNB_Settings();
                    this.isScanRun = true;
                    this.scan_button.setText(this.getResources().getString(R.string.strStop ));   // string:strStop "Stop"
                    this.updateScanProgress(0);
                    this.mTranspondersAdp.clear();
                    this.mChannelListAdp.clear();
                    this.mTranspondersAdp.notifyDataSetChanged();
                    this.mChannelListAdp.notifyDataSetChanged();
                    this.mSTransponderList.clear();
                    this.mChannelList.clear();
                    this.scan_strength_progress.setProgress(0);
                    this.scan_Strength_value.setText("--");
                    this.scan_quality_progress.setProgress(0);
                    this.scan_quality_value.setText("--");
                    this.scan_tp_total.setText("");
                    this.scan_channel_total.setText("");
                    this.SetupTimer();
                }
            }
        }
        else if(i == 1) {
            if(this.mSTransponderList.size() == 0) {
                Toast.makeText(this.getContext(), R.string.strNoChannels, 0).show();  // string:strNoChannels "No channels to save"
                return;
            }

            Toast.makeText(this.getContext(), R.string.strChannelsSaved, 0).show();  // string:strChannelsSaved "Channel Saved"
            while(v3 < this.mSTransponderList.size()) {
                Transponder transponder = this.mSTransponderList.get(v3);
                Satellite sat = activity.mSatellites.get(activity.mSatPos);
                transponder.satellite_id = sat.satelite_id;
                DBManager.getInctance(this.getContext()).Check_UpdateTransponder(transponder);
                if(transponder.Channels != null && transponder.Channels.size() > 0) {
                    DBManager.getInctance(this.getActivity()).InsertTransponderChannels(transponder.tpId, transponder.Channels);
                }

                ++v3;
            }

            activity.UpdateTranspondersList();
        }
    }
}

