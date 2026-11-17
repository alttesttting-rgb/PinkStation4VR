package com.pinkstation4vr;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
    static { System.loadLibrary("pinkstation"); }
    private native String nativeStatus();

    @Override public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(5894 | 1024 | 512);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.CENTER);
        root.setPadding(32,32,32,32);
        root.setBackgroundColor(Color.rgb(15, 8, 28));
        TextView title = new TextView(this);
        title.setText("PINKSTATION 4 VR");
        title.setTextColor(Color.rgb(235, 130, 255));
        title.setTextSize(30);
        title.setGravity(Gravity.CENTER);
        TextView status = new TextView(this);
        status.setText("Android starter build\\n\\n" + nativeStatus() + "\\n\\nPS4 emulation is not implemented yet.");
        status.setTextColor(Color.WHITE);
        status.setTextSize(16);
        status.setGravity(Gravity.CENTER);
        status.setPadding(0,28,0,0);
        root.addView(title);
        root.addView(status);
        setContentView(root);
    }
}
