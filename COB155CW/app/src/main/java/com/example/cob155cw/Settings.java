package com.example.cob155cw;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings.System;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

public class Settings extends AppCompatActivity {
    ImageButton returnButton;
    SeekBar seekBar;
    Window window;
    CheckBox SummaryBox;
    DBHelper myDB;
    CheckBox TimeBox;
    CheckBox deleteBox;
    CheckBox UniqueBox;
    boolean[] features;
    int brightness;
    ContentResolver cResolver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Bundle extras = getIntent().getExtras();
        myDB = new DBHelper(this);
        features = extras.getBooleanArray("features");

        returnButton = findViewById(R.id.BackButton);
        returnButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putBooleanArray("features",features);
                    dataBundle.putString("date",extras.getString("date"));
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
        );

        window = getWindow();
        seekBar = findViewById(R.id.BrightnessSeekBar);
        cResolver =  getContentResolver();
        seekBar.setMax(255);                    // max Value of brightness = 255
        seekBar.setKeyProgressIncrement(1);

        try {
            brightness = System.getInt(cResolver, System.SCREEN_BRIGHTNESS);
        } catch (android.provider.Settings.SettingNotFoundException e) {
            throw new RuntimeException(e);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!android.provider.Settings.System.canWrite(this)) {
                Intent intent = new Intent(android.provider.Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            }
        }
        //Set the progress of the seek bar based on the system's brightness
        seekBar.setProgress(brightness);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()            // override seekBar adapter
        {
            public void onStopTrackingTouch(SeekBar seekBar) {

                java.lang.System.out.println(brightness);
                System.putInt(cResolver, System.SCREEN_BRIGHTNESS, brightness);
                WindowManager.LayoutParams layout = window.getAttributes();
                layout.screenBrightness = brightness / (float)255;
                window.setAttributes(layout);
            }


            public void onStartTrackingTouch(SeekBar seekBar)
            {
            }

            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                brightness = Math.max(progress, 20);
            }
        });

        Button LightMode = findViewById(R.id.LightModeButton);
        LightMode.setOnClickListener(
                v -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)     // Click on lightMode and turn off dark mode
        );

        deleteBox = findViewById(R.id.checkBox);
        ImageButton SaveButton = findViewById(R.id.SaveButton);

        View fullscreen = findViewById(android.R.id.content);
        fullscreen.setOnTouchListener(new OnSwipeTouchListener(Settings.this) {
                public void onSwipeRight() {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putBooleanArray("features",features);
                    dataBundle.putString("date",extras.getString("date"));
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
            });

        SaveButton.setOnClickListener(
                v -> {
                    boolean[] features = new boolean[]{false,false,false};
                    features[0] = SummaryBox.isChecked();
                    features[1] = TimeBox.isChecked();
                    features[2] = UniqueBox.isChecked();                // Check which features have been checked
                    if (deleteBox.isChecked()) {
                        Toast.makeText(getApplicationContext(), myDB.deleteAllSessions() + " Sessions removed", Toast.LENGTH_SHORT).show();
                    }       // delete all if deleteBox has been checked
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("date",extras.getString("date"));
                    dataBundle.putBooleanArray("features",features);
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
        );


        SummaryBox = findViewById(R.id.checkBox2);
        SummaryBox.setChecked(features[0]);

        TimeBox = findViewById(R.id.checkBox3);
        TimeBox.setChecked(features[1]);                // Check box if it has already been checked

        UniqueBox = findViewById(R.id.checkBox4);
        UniqueBox.setChecked(features[2]);


        Button DarkMode = findViewById(R.id.DarkModeButton);

        DarkMode.setOnClickListener(
                v -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        );
    }
}