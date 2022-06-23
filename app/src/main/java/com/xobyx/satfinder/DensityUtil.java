package com.xobyx.satfinder;

import android.content.Context;

public class DensityUtil {
    public static int getScreenDensity(Context context, float v) {
        return (int)(v * context.getResources().getDisplayMetrics().density + 0.5f);
    }
}

