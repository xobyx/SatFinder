package com.xobyx.satfinder;

import android.os.Handler;
import android.os.Message;

class ScanFragmentHandler implements Handler.Callback {
    final ScanFragment mFrag;

    ScanFragmentHandler(ScanFragment arg1) {
        super();
        this.mFrag = arg1;

    }

    @Override  // android.os.Handler$Callback
    public boolean handleMessage(Message msg) {
        int[] ids = {R.string.strProgress, R.string.strProgress1, R.string.strProgress2};  // string:strProgress "Scanning"
       // String[] v1 = {"0x7F0B0038", "0x7F0B0039", "0x7F0B003A"};  // string:strProgress "Scanning"
        if(msg.what == 10) {
            if(this.mFrag.mState < 2) {
                int mState = this.mFrag.mState;
                this.mFrag.mState = mState + 1;
            }
            else {
                this.mFrag.mState = 0;
            }

            this.mFrag.scan_cur_tp.setText(ids[this.mFrag.mState]);
        }

        return false;
    }
}

