package com.pinkstation4vr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    private static final int MIC_PERMISSION_REQUEST = 410;
    private LinearLayout libraryList;
    private TextView micStatus;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        getWindow().getDecorView().setSystemUiVisibility(
            View.SYSTEM_UI_FLAG_FULLSCREEN
            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        );
        buildInterface();
        loadQuestGames();
        updateMicStatus();
    }

    private int dp(float value) {
        return (int) (value * getResources().getDisplayMetrics().density + 0.5f);
    }

    private TextView label(String value, int size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private Button button(String value) {
        Button b = new Button(this);
        b.setText(value);
        b.setTextColor(Color.WHITE);
        b.setAllCaps(false);
        b.setBackgroundTintList(android.content.res.ColorStateList.valueOf(Color.rgb(190, 35, 130)));
        return b;
    }

    private void buildInterface() {
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(dp(28), dp(20), dp(28), dp(16));
        root.setBackgroundColor(Color.rgb(12, 5, 24));

        TextView title = label("PINKSTATION 4", 30, Color.rgb(239, 130, 255));
        title.setTypeface(Typeface.DEFAULT_BOLD);
        root.addView(title);

        TextView heading = label("GAME LIBRARY", 20, Color.WHITE);
        heading.setPadding(0, dp(8), 0, dp(2));
        root.addView(heading);

        TextView description = label(
            "Quest games installed on this headset appear here. Select a game to launch it.",
            13, Color.LTGRAY
        );
        root.addView(description);

        LinearLayout controls = new LinearLayout(this);
        controls.setOrientation(LinearLayout.HORIZONTAL);
        controls.setGravity(Gravity.CENTER_VERTICAL);
        controls.setPadding(0, dp(10), 0, dp(10));

        Button refresh = button("Refresh games");
        refresh.setOnClickListener(v -> loadQuestGames());
        controls.addView(refresh);

        Button microphone = button("Enable microphone");
        microphone.setOnClickListener(v -> requestMicrophone());
        LinearLayout.LayoutParams micParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        micParams.leftMargin = dp(10);
        controls.addView(microphone, micParams);
        root.addView(controls);

        micStatus = label("", 13, Color.rgb(205, 170, 220));
        micStatus.setPadding(0, 0, 0, dp(8));
        root.addView(micStatus);

        ScrollView scroll = new ScrollView(this);
        libraryList = new LinearLayout(this);
        libraryList.setOrientation(LinearLayout.VERTICAL);
        scroll.addView(libraryList);
        root.addView(scroll, new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, 0, 1f
        ));

        TextView footer = label(
            "PinkStation is a game launcher, not a PS4 emulator. Only compatible installed Quest apps can launch.",
            11, Color.GRAY
        );
        footer.setPadding(0, dp(8), 0, 0);
        root.addView(footer);

        setContentView(root);
    }

    private void loadQuestGames() {
        if (libraryList == null) return;
        libraryList.removeAllViews();

        Intent query = new Intent(Intent.ACTION_MAIN);
        query.addCategory(Intent.CATEGORY_LAUNCHER);
        List<ResolveInfo> results = getPackageManager().queryIntentActivities(query, 0);
        Map<String, ResolveInfo> unique = new LinkedHashMap<>();

        for (ResolveInfo info : results) {
            String packageName = info.activityInfo.packageName;
            if (!packageName.equals(getPackageName())) {
                unique.put(packageName, info);
            }
        }

        List<ResolveInfo> games = new ArrayList<>(unique.values());
        Collections.sort(games, new Comparator<ResolveInfo>() {
            @Override public int compare(ResolveInfo a, ResolveInfo b) {
                return String.valueOf(a.loadLabel(getPackageManager())).compareToIgnoreCase(
                    String.valueOf(b.loadLabel(getPackageManager()))
                );
            }
        });

        if (games.isEmpty()) {
            TextView empty = label(
                "No launchable games were found. Install a Quest game, then tap Refresh games.",
                16, Color.WHITE
            );
            empty.setPadding(dp(12), dp(22), dp(12), dp(22));
            libraryList.addView(empty);
            return;
        }

        TextView count = label(games.size() + " installed launchable apps", 13, Color.LTGRAY);
        count.setPadding(dp(4), dp(6), 0, dp(8));
        libraryList.addView(count);

        for (ResolveInfo info : games) {
            addGameCard(info);
        }
    }

    private void addGameCard(ResolveInfo info) {
        String packageName = info.activityInfo.packageName;
        String gameName = String.valueOf(info.loadLabel(getPackageManager()));
        Drawable icon = info.loadIcon(getPackageManager());

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setPadding(dp(12), dp(8), dp(12), dp(8));
        card.setBackgroundTintList(null);
        card.setBackgroundColor(Color.rgb(34, 20, 48));

        ImageView image = new ImageView(this);
        image.setImageDrawable(icon);
        LinearLayout.LayoutParams imageParams = new LinearLayout.LayoutParams(dp(54), dp(54));
        imageParams.rightMargin = dp(14);
        card.addView(image, imageParams);

        LinearLayout textColumn = new LinearLayout(this);
        textColumn.setOrientation(LinearLayout.VERTICAL);
        TextView name = label(gameName, 17, Color.WHITE);
        name.setTypeface(Typeface.DEFAULT_BOLD);
        TextView packageLabel = label(packageName, 10, Color.LTGRAY);
        textColumn.addView(name);
        textColumn.addView(packageLabel);
        card.addView(textColumn, new LinearLayout.LayoutParams(0, -2, 1f));

        Button launch = button("Play");
        launch.setOnClickListener(v -> launchGame(packageName));
        card.addView(launch);

        LinearLayout.LayoutParams cardParams = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );
        cardParams.bottomMargin = dp(8);
        libraryList.addView(card, cardParams);
    }

    private void launchGame(String packageName) {
        Intent launch = getPackageManager().getLaunchIntentForPackage(packageName);
        if (launch == null) {
            Toast.makeText(this, "This app does not expose a launch button.", Toast.LENGTH_LONG).show();
            return;
        }
        try {
            startActivity(launch);
        } catch (Exception e) {
            Toast.makeText(this, "Could not launch this app: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void requestMicrophone() {
        if (checkSelfPermission(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            updateMicStatus();
            Toast.makeText(this, "Microphone permission is already enabled.", Toast.LENGTH_SHORT).show();
            return;
        }
        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, MIC_PERMISSION_REQUEST);
    }

    private void updateMicStatus() {
        if (micStatus == null) return;
        boolean allowed = checkSelfPermission(Manifest.permission.RECORD_AUDIO)
            == PackageManager.PERMISSION_GRANTED;
        micStatus.setText(allowed
            ? "Microphone permission: allowed (no audio is being recorded)."
            : "Microphone permission: not granted.");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MIC_PERMISSION_REQUEST) {
            updateMicStatus();
            boolean granted = grantResults.length > 0
                && grantResults[0] == PackageManager.PERMISSION_GRANTED;
            Toast.makeText(this,
                granted ? "Microphone permission granted." : "Microphone permission not granted.",
                Toast.LENGTH_LONG).show();
        }
    }
}
