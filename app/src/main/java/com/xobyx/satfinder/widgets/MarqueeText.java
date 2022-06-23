package com.xobyx.satfinder.widgets;

import android.content.Context;
import android.util.AttributeSet;
import androidx.appcompat.widget.AppCompatTextView;

public class MarqueeText extends AppCompatTextView {
    public MarqueeText(Context arg1) {
        super(arg1);
    }

    public MarqueeText(Context arg1, AttributeSet arg2) {
        super(arg1, arg2);
    }

    public MarqueeText(Context arg1, AttributeSet arg2, int arg3) {
        super(arg1, arg2, arg3);
    }

    @Override  // android.view.View
    public boolean isFocused() {
        return true;
    }
}

