package com.example.cob155cw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import static java.time.temporal.TemporalAdjusters.firstInMonth;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class WeekSelect extends AppCompatActivity {
    LocalDate firstMonday;
    String formattedDate;
    String month;
    boolean[] features;
    String[] mondays;
    Resources resources;
    String popularType;
    ImageButton nextMonthButton;
    ImageButton lastMonthButton;
    DBHelper myDB;
    LocalDate realDate;
    String newDate;
    int week;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_week_select);
        Bundle extras = getIntent().getExtras();


        realDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            realDate = LocalDate.parse(extras.getString("date"));
        }

        // Get the full date
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedDate = realDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        }
        // From the full date get the month and the year and display
        String[] s = formattedDate.split(" ");
        TextView monthYear = findViewById(R.id.LightingLabel);
        resources = getResources();
        monthYear.setText(String.format(resources.getString((R.string.monthYear)),s[0],s[2]));

        // Get the first monday of the month and then get the following mondays in that month
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            firstMonday = realDate.with(firstInMonth(DayOfWeek.MONDAY));
            mondays = new String[]{firstMonday.format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), firstMonday.plusWeeks(1).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), firstMonday.plusWeeks(2).format(DateTimeFormatter.ofPattern("yyyy-MM-dd")), firstMonday.plusWeeks(3).format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))};
        }

        // Get week views
        Button week1 = findViewById(R.id.textView2);
        Button week2 = findViewById(R.id.textView3);
        Button week3 = findViewById(R.id.textView4);
        Button week4 = findViewById(R.id.textView5);

        // Set the text of each view to the appropriate monday in that month
        week1.setText(String.format(resources.getString(R.string.weekStart), mondays[0]));
        week2.setText(String.format(resources.getString(R.string.weekStart), mondays[1]));
        week3.setText(String.format(resources.getString(R.string.weekStart), mondays[2]));
        week4.setText(String.format(resources.getString(R.string.weekStart), mondays[3]));

        // Click on a week and return to the main screen but with a different week
        week1.setOnClickListener(
                v -> {
                    System.out.println(week);
                    Bundle dataBundle = new Bundle();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    dataBundle.putBooleanArray("features",features);
                    dataBundle.putString("date", mondays[0]);
                    intent.putExtras(dataBundle);
                    startActivity(intent);

                }
        );
        week2.setOnClickListener(
                v -> {
                    System.out.println(week);
                    Bundle dataBundle = new Bundle();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    dataBundle.putString("date", mondays[1]);
                    dataBundle.putBooleanArray("features",features);
                    intent.putExtras(dataBundle);
                    startActivity(intent);

                }
        );
        week3.setOnClickListener(
                v -> {
                    System.out.println(week);
                    Bundle dataBundle = new Bundle();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    dataBundle.putBooleanArray("features",features);
                    dataBundle.putString("date", mondays[2]);
                    intent.putExtras(dataBundle);
                    startActivity(intent);

                }
        );
        week4.setOnClickListener(
                v -> {
                    System.out.println(week);
                    Bundle dataBundle = new Bundle();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    dataBundle.putString("date", mondays[3]);
                    dataBundle.putBooleanArray("features",features);
                    intent.putExtras(dataBundle);
                    startActivity(intent);

                }
        );

        View fullscreen = findViewById(android.R.id.content);
        fullscreen.setOnTouchListener(new OnSwipeTouchListener(WeekSelect.this) {
            public void onSwipeRight() {
                Bundle dataBundle = new Bundle();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    newDate = realDate.minusMonths(1).toString();
                }
                dataBundle.putString("date", String.valueOf(newDate));
                dataBundle.putBooleanArray("features",features);
                Intent i = new Intent(getApplicationContext(),WeekSelect.class);
                i.putExtras(dataBundle);
                startActivity(i);
            }

            public void onSwipeLeft() {
                Bundle dataBundle = new Bundle();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    newDate = realDate.plusMonths(1).toString();
                }
                dataBundle.putString("date", String.valueOf(newDate));
                dataBundle.putBooleanArray("features",features);
                Intent i = new Intent(getApplicationContext(),WeekSelect.class);
                i.putExtras(dataBundle);
                startActivity(i);
            }
        });

        features = extras.getBooleanArray("features");
        if (features[0]) {                              // If the summary feature in settings is checked
            myDB = new DBHelper(this);
            Cursor res = myDB.getAllSessions();
            res.moveToFirst();
            int todo = 0;
            int done = 0;
            SharedPreferences sharedPreferences = getSharedPreferences("my_preferences", MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();

            editor.putInt("Track", 0);
            editor.putInt("Long run", 0);
            editor.putInt("Easy run",0);
            editor.putInt("Easy run", 0);
            int maxNum = 0;
            while (!res.isAfterLast()) {
                @SuppressLint("Range") String dat = res.getString(res.getColumnIndex(DBHelper.SESSIONS_COLUMN_DATE));
                @SuppressLint("Range") String typ = res.getString(res.getColumnIndex(DBHelper.SESSIONS_COLUMN_TYPE));
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    LocalDate sessionDate = LocalDate.parse(dat);
                    if ((sessionDate.isAfter(firstMonday) || sessionDate.isEqual(firstMonday)) && sessionDate.isBefore(firstMonday.plusMonths(1))) {
                        if (sessionDate.isAfter(LocalDate.now())) {
                            done = done + 1;            // if sessions is in month and before the current date
                        } else {
                            todo = todo + 1;        // if in month and after current date
                        }
                        editor.putInt(typ, sharedPreferences.getInt(typ, 0) + 1);
                        editor.apply();
                        if (sharedPreferences.getInt(typ, 0) > maxNum) {
                            popularType = typ;
                        }
                    }
                }
                res.moveToNext();
            }



            // Get views for summary
            TextView summary = findViewById(R.id.BrightnessLabel);
            TextView todoView = findViewById(R.id.SummaryLabel2);
            TextView doneView = findViewById(R.id.SummaryLabel);
            TextView popView = findViewById(R.id.SummaryLabel3);


            // Get the month
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                month = firstMonday.getMonth().toString().substring(0, 1).toUpperCase() + firstMonday.getMonth().toString().substring(1).toLowerCase();
            }
            // Set sub title text for summary
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                summary.setText(String.format(resources.getString(R.string.summary),month));
            }
            // Set text for summary
            todoView.setText(String.format(resources.getString(R.string.toDo),todo));
            doneView.setText(String.format(resources.getString(R.string.done),done));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                if (popularType != null) {
                    popView.setText(String.format(resources.getString(R.string.popSession), popularType, month));
                }
            }

        }

        // Return week select for the next month
        nextMonthButton = findViewById(R.id.imageButton8);
        nextMonthButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        newDate = realDate.plusMonths(1).toString();
                    }
                    dataBundle.putString("date", String.valueOf(newDate));
                    dataBundle.putBooleanArray("features",features);
                    Intent i = new Intent(getApplicationContext(),WeekSelect.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
        );

        // Return week select for the last month
        lastMonthButton = findViewById(R.id.imageButton6);
        lastMonthButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        newDate = realDate.minusMonths(1).toString();
                    }
                    dataBundle.putString("date", String.valueOf(newDate));
                    dataBundle.putBooleanArray("features",features);
                    Intent i = new Intent(getApplicationContext(),WeekSelect.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
        );
    }


}

