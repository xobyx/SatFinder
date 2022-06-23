package com.xobyx.satfinder;

import androidx.annotation.NonNull;
import com.xobyx.satfinder.base.ChannelBase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Transponder implements Serializable {
    public int satellite_id;
    public int tpId;
    public List<ChannelBase> Channels;
    public TranspondersExpandableList mChannelListAdpt;
    public int mFrequency;
    public int mPolization;

    public int mSymbolRate;
    public int fav;

    public Transponder() {

    }

    public Transponder(int sat_id, int tpId, int freq, int symbol_rate, int polar,int ifav) {
        this.satellite_id = sat_id;
        this.tpId = tpId;
        this.mFrequency = freq;
        this.mSymbolRate = symbol_rate;
        this.mPolization = polar;
        this.fav=ifav;
        this.Channels = null;
    }

    public Transponder(Transponder transponder) {
        this.satellite_id = -1;
        this.tpId = -1;
        this.mFrequency = transponder.mFrequency;
        this.mSymbolRate = transponder.mSymbolRate;
        this.mPolization = transponder.mPolization;
        this.fav = 0;
        this.mChannelListAdpt = null;
        if (transponder.Channels != null && transponder.Channels.size() > 0) {
            this.Channels = new ArrayList<>();
            this.Channels.addAll(transponder.Channels);
        }
    }

    @NonNull
    @Override
    public String toString() {
        String freq = Integer.toString(this.mFrequency / 1000);
        return this.mPolization <= 0 ? freq + "/H/" + Integer.toString(this.mSymbolRate / 1000) : freq + "/V/" + Integer.toString(this.mSymbolRate / 1000);
    }
}

