package com.xobyx.satfinder;

import android.os.Handler;
import android.os.Message;

class FieldStrengthHandler implements Handler.Callback {
    final FieldStrengthFragment fragment;

    FieldStrengthHandler(FieldStrengthFragment arg1) {
        super();
        this.fragment = arg1;

    }

    @Override  // android.os.Handler$Callback
    public boolean handleMessage(Message msg) {
        if(msg.what == 15) {//send to device
            byte[] bytes = (byte[])msg.obj;

            if(fragment.mDvbFinder.DVBFinder_Status == DVB.CONNECTED_AND_CHARS_SET) {
                fragment.mDvbFinder.write(bytes);
            }
        }
        else if(msg.what == 16) {
            this.fragment.mActive_Frequ = this.fragment.mActive_Frequ < 3 ? this.fragment.mActive_Frequ + 1 : 0;
            this.fragment.setActiveFreq_color(this.fragment.mActive_Frequ);
            this.fragment.SendCurrentTp(this.fragment.mActive_Frequ);
            return false;
        }

        return false;
    }
}

