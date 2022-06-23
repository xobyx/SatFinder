package com.xobyx.satfinder.toolnet;

import java.io.File;
import java.io.IOException;

public class MNUtils {
    public static void mfile777(File arg3) {
        try {
            Runtime.getRuntime().exec("chmod 777 " + arg3.getParent());
            Runtime.getRuntime().exec("chmod 777 " + arg3.getAbsolutePath());
        }
        catch(IOException v3) {
            v3.printStackTrace();
        }
    }
}

