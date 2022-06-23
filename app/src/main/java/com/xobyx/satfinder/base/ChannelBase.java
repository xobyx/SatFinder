package com.xobyx.satfinder.base;

import android.widget.CheckBox;

public class ChannelBase {
    private static final byte RADIO = 64;
    private static final byte TV = 32;
    private static final byte PAID = 16;
    private String ChannelName;
    private Byte ChannelType;

    public ChannelBase(String name, byte type) {
        this.ChannelName=name;
        this.ChannelType=type;
    }

    public ChannelBase() {

    }

    public String getChannelName() {
        return this.ChannelName;
    }

    public void setChannelType(Byte type) {
        this.ChannelType = type;
    }

    public void setChannelName(String name) {
        this.ChannelName = name;
    }

    public Byte getChannelType() {
        return this.ChannelType;
    }

    public boolean isRadioChannel(){
        return hasFlag(ChannelType,RADIO);
    }

    private boolean hasFlag(byte b  ,byte flag) {
        return (b & flag) == flag;
    }

    public boolean isTvChannel() {
        return hasFlag(ChannelType,TV);
    }
    public boolean isPaidChannel() {
        return hasFlag(ChannelType,PAID);
    }
}

