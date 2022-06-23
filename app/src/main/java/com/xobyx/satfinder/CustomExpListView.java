package com.xobyx.satfinder;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ExpandableListView;

public class CustomExpListView extends ExpandableListView {
    public CustomExpListView(Context arg1) {
        super(arg1);
    }

    public CustomExpListView(Context arg1, AttributeSet arg2) {
        super(arg1, arg2);
    }

    @Override  // android.widget.ListView
    protected void onMeasure(int arg2, int arg3) {
        super.onMeasure(View.MeasureSpec.makeMeasureSpec(960, MeasureSpec.AT_MOST), View.MeasureSpec.makeMeasureSpec(0x270F, MeasureSpec.AT_MOST));
    }
}

