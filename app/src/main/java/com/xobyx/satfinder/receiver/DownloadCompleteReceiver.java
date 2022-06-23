package com.xobyx.satfinder.receiver;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Environment;

import com.xobyx.satfinder.toolnet.MNUtils;
import java.io.File;

public class DownloadCompleteReceiver extends BroadcastReceiver {
    @Override  // android.content.BroadcastReceiver
    public void onReceive(Context context, Intent intent) {
        DownloadManager v0 = (DownloadManager)context.getSystemService(Context.DOWNLOAD_SERVICE);
        if(("android.intent.action.DOWNLOAD_COMPLETE".equals(intent.getAction())) && v0 != null) {
            long v1 = intent.getLongExtra("extra_download_id", 0L);
            DownloadManager.Query v7 = new DownloadManager.Query();
            v7.setFilterById(new long[]{v1});
            Cursor v7_1 = v0.query(v7);
            if(v7_1 != null && (v7_1.moveToFirst()) && 8 == v7_1.getInt(v7_1.getColumnIndex("status"))) {
                File v0_1 = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);
                if(v0_1 != null && (v0_1.exists())) {
                    String v1_1 = context.getSharedPreferences("update_version_info", 0).getString("target_apk_name", "v8finder.apk");
                    String v0_2 = v0_1.getPath() + File.separator + v1_1;
                    File v1_2 = new File(v0_2);
                    if(v1_2.exists()) {
                        if(Build.VERSION.SDK_INT < 24) {
                            MNUtils.mfile777(v1_2);
                        }

                       // InstallUtils.a(context, v0_2);
                    }
                }
            }

            if(v7_1 != null && !v7_1.isClosed()) {
                v7_1.close();
            }
        }
    }
}

