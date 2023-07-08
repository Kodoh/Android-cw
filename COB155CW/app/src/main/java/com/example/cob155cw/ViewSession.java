package com.example.cob155cw;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.provider.CalendarContract;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;


public class ViewSession extends AppCompatActivity {

    DBHelper myDB;
    String formattedDate;
    TextView name ;
    String typ;

    String dat;
    String note;
    ImageButton calendarButton;
    Button returnButton;
    Button shareButton;
    Button editButton;
    Button deleteButton;
    TextView date;
    boolean[] features;
    String tim;
    TextView type;
    TextView time;
    String nam;
    TextView notes;
    private static final int CALENDAR_PERMISSION_REQUEST_CODE = 100;
    int id_To_Update = 0;

    @SuppressLint("Range")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_session);
        Bundle extras = getIntent().getExtras();
        features = extras.getBooleanArray("features");
        myDB = new DBHelper(this);

        calendarButton = findViewById(R.id.imageButton11);
        calendarButton.setOnClickListener(
        v -> {
            if (checkCalendarPermissions()) {
                // Permissions granted, proceed with accessing the calendar
                insertCalendarEvent();
            } else {
                // Permissions not granted, request them
                requestCalendarPermissions();
            }
        }
    );

        returnButton = findViewById(R.id.BackButton2);
        returnButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("date", extras.getString("date"));         // When clicked return to main screen
                    dataBundle.putBooleanArray("features",extras.getBooleanArray("features"));
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
        );



        shareButton = findViewById(R.id.imageButton5);
        shareButton.setOnClickListener(
                v -> {                                  // When clicked share session to other apps
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType("text/plain");
                    shareIntent.putExtra(Intent.EXTRA_TEXT, "On " + dat + " there is a " + typ + " session (" + nam + ") see app for further info");

                    if (shareIntent.resolveActivity(getPackageManager()) != null) {
                        startActivity(shareIntent);
                    }
                }
        );

        editButton = findViewById(R.id.imageButton4);
        editButton.setOnClickListener(
                v -> {                                              // When clicked take user to edit session screen
                    int id_To_Search = extras.getInt("id");
                    Bundle dataBundle = new Bundle();
                    dataBundle.putInt("id", id_To_Search);
                    dataBundle.putBooleanArray("features",extras.getBooleanArray("features"));
                    dataBundle.putString("date", extras.getString("date"));
                    Intent intent = new Intent(getApplicationContext(),EditSession.class);

                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
        );

        deleteButton = findViewById(R.id.imageButton6);
        deleteButton.setOnClickListener(
                v -> {                                            // When clicked delete session
                    int id_To_Search = extras.getInt("id");
                    myDB.deleteSession(id_To_Search);
                    Toast.makeText(getApplicationContext(), "Deleted", Toast.LENGTH_SHORT).show();
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("date", extras.getString("date"));
                    dataBundle.putBooleanArray("features",features);
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);     // Then return to main screen
                    intent.putExtras(dataBundle);
                    startActivity(intent);
                }
        );

        View fullscreen = findViewById(android.R.id.content);
        fullscreen.setOnTouchListener(new OnSwipeTouchListener(ViewSession.this) {
            public void onSwipeRight() {
                Bundle dataBundle = new Bundle();
                dataBundle.putString("date", extras.getString("date"));         // When clicked return to main screen
                dataBundle.putBooleanArray("features",extras.getBooleanArray("features"));
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);

                intent.putExtras(dataBundle);
                startActivity(intent);
            }

        });

        // Access views
        name = findViewById(R.id.LightingLabel);
        date = findViewById(R.id.BrightnessLabel);
        type = findViewById(R.id.TimeLabel2);
        time = findViewById(R.id.TimeLabel);
        notes = findViewById(R.id.SummaryLabel);

        int id = extras.getInt("id");
        Cursor rs = myDB.getData(id);
        id_To_Update = id;
        rs.moveToFirst();

        // Get session associated with id_To_Update
        nam = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_NAME));
        dat = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_DATE));
        typ = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_TYPE));
        tim = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_TIME));
        note = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_NOTES));

        if (!rs.isClosed())  {
            rs.close();
        }


        // Set text on views equal to the corresponding value in the database
        name.setText(nam);
        name.setFocusable(false);

        // Format to full date
        LocalDate realDate = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            realDate = LocalDate.parse(dat);
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            formattedDate = realDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG));
        }
        date.setText(formattedDate);
        date.setFocusable(false);

        type.setText(typ);
        type.setFocusable(false);

        if (features[1]) {
            time.setText(tim);          // If time setting set add time to view
            time.setFocusable(false);
        }

        else {      // Else display warning message
            Resources resources = getResources();
            time.setText(resources.getString(R.string.timeDisabled));
            time.setFocusable(false);
        }

        notes.setText(note);
        notes.setFocusable(false);


    }

    private void insertCalendarEvent() {
        LocalDate realDate;
        long millis = 0;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            realDate = LocalDate.parse(dat);
            LocalDateTime localDateTime = realDate.atStartOfDay();
            ZoneId zoneId = ZoneId.systemDefault(); // Use the system default time zone
            ZonedDateTime zonedDateTime = localDateTime.atZone(zoneId);
            millis = zonedDateTime.toInstant().toEpochMilli();
        }


        ContentValues eventValues = new ContentValues();
        eventValues.put(CalendarContract.Events.CALENDAR_ID, 1);
        eventValues.put(CalendarContract.Events.TITLE, nam);
        eventValues.put(CalendarContract.Events.DESCRIPTION, typ + " Session");
        eventValues.put(CalendarContract.Events.DTSTART, millis);
        eventValues.put(CalendarContract.Events.DTEND, System.currentTimeMillis() + 1000 * 60 * 120);
        eventValues.put(CalendarContract.Events.EVENT_TIMEZONE, "UTC");

        // Insert the event using the content provider
        ContentResolver contentResolver = getContentResolver();
        Uri uri = contentResolver.insert(CalendarContentProvider.CONTENT_URI, eventValues);

        if (uri != null) {
            // Event inserted successfully
            Toast.makeText(this, "Event inserted", Toast.LENGTH_SHORT).show();
        } else {
            // Failed to insert event
            Toast.makeText(this, "Failed to insert event", Toast.LENGTH_SHORT).show();
        }
    }

    private boolean checkCalendarPermissions() {
        int readCalendarPermission = ContextCompat.checkSelfPermission(this, "android.permission.READ_CALENDAR");
        int writeCalendarPermission = ContextCompat.checkSelfPermission(this, "android.permission.WRITE_CALENDAR");
        return readCalendarPermission == PackageManager.PERMISSION_GRANTED && writeCalendarPermission == PackageManager.PERMISSION_GRANTED;
    }

    private void requestCalendarPermissions() {
        ActivityCompat.requestPermissions(this, new String[]{"android.permission.READ_CALENDAR", "android.permission.WRITE_CALENDAR"}, CALENDAR_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CALENDAR_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Calendar permissions granted, proceed with accessing the calendar
                insertCalendarEvent();
            } else {
                // Calendar permissions denied, show a message or handle accordingly
                Toast.makeText(this, "Calendar permissions denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
