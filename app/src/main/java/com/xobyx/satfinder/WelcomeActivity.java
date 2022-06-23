package com.xobyx.satfinder;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

public class WelcomeActivity extends Activity {



    @Override  // android.app.Activity
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.setContentView(R.layout.welcome);  // layout:welcome




        ((TextView)this.findViewById(R.id.version_text)).setText("V 8.4");  // id:version_text
        new Handler().postDelayed(() -> {
            WelcomeActivity.this.finish();
            Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
            intent.putExtra("from", "welcome");
            WelcomeActivity.this.startActivity(intent);
        }, 1000L);
    }

    @Override  // android.app.Activity
    protected void onDestroy() {
        super.onDestroy();
    }
}

