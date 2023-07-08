package com.example.cob155cw;

import android.annotation.SuppressLint;
import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import android.os.SystemClock;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Chronometer;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.content.Intent;
import android.database.Cursor;
import android.widget.TextView;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class EditSession extends AppCompatActivity {
    private DBHelper myDB ;
    ImageButton returnButton;
    String[] dayArray;
    TextView name ;
    int id_To_Update;
    Spinner date;
    Spinner type;
    Chronometer time;
    TextView notes;
    public static final String NOTIFICATION_CHANNEL_ID = "10001" ;
    private final static String default_notification_channel_id = "default" ;
    final Calendar myCalendar = Calendar. getInstance () ;
    int dayOfWeek;
    boolean[] features;
    private long pauseOffset;
    private boolean running;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_session);
        Bundle extras = getIntent().getExtras();
        myDB = new DBHelper(this);
        features = extras.getBooleanArray("features");
        String monday = extras.getString("date");

        dayArray = new String[] {
                "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday", "Sunday"
        };

        Spinner daysSpinner = findViewById(R.id.spinner1);
        ArrayAdapter<String> dayAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, dayArray);                // Add days of week to dropdown
        dayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daysSpinner.setAdapter(dayAdapter);

        String[] TypeArray = new String[] {
                "Track", "Gym", "Long run", "Easy run"
        };
        Spinner typeSpinner = findViewById(R.id.spinner);
        ArrayAdapter<String> typeAdapter = new ArrayAdapter<>(this,
                android.R.layout.simple_spinner_item, TypeArray);               // Add types of session to dropdown
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        typeSpinner.setAdapter(typeAdapter);

        View fullscreen = findViewById(android.R.id.content);
        fullscreen.setOnTouchListener(new OnSwipeTouchListener(EditSession.this) {
            public void onSwipeRight() {
                Bundle dataBundle = new Bundle();
                dataBundle.putString("date", String.valueOf(monday));
                dataBundle.putBooleanArray("features",features);        // Return to main screen if clicked
                Intent i = new Intent(getApplicationContext(),MainActivity.class);
                i.putExtras(dataBundle);
                startActivity(i);
            }

        });

        returnButton = findViewById(R.id.imageButton);
        returnButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("date", String.valueOf(monday));
                    dataBundle.putBooleanArray("features",features);        // Return to main screen if clicked
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
        );


        name = findViewById(R.id.editTextTextPersonName);
        date = daysSpinner;
        type = typeSpinner;
        time = findViewById(R.id.chronometer);
        notes = findViewById(R.id.editTextTextMultiLine);

        if (extras.containsKey("id")) {         // For updating rather than adding
            int Value = extras.getInt("id");
            Cursor rs = myDB.getData(Value);
            id_To_Update = Value;
            rs.moveToFirst();

            @SuppressLint("Range") String nam = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_NAME));
            @SuppressLint("Range") String dat = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_DATE));
            @SuppressLint("Range") String typ = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_TYPE));
            @SuppressLint("Range") String tim = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_TIME));
            @SuppressLint("Range") String note = rs.getString(rs.getColumnIndex(DBHelper.SESSIONS_COLUMN_NOTES));

            // Get data associated with id and set fields to these values

            if (!rs.isClosed()) {
                rs.close();
            }

            LocalDate realDate = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                realDate = LocalDate.parse(dat);
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                dayOfWeek = realDate.getDayOfWeek().getValue();
            }

            name.setText(nam);

            date.setSelection(dayOfWeek - 1);

            type.setSelection(((ArrayAdapter<String>) type.getAdapter()).getPosition(typ));

            time.setText(tim);

            notes.setText(note);

            }


        time.setFormat("%s");
        time.setBase(SystemClock.elapsedRealtime());

    }

    public void onSave(View view) {         // When we confirm session details
        Bundle extras = getIntent().getExtras();
        String monday = extras.getString("date");
        LocalDate realDate = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            realDate = LocalDate.parse(monday);                     // Convert string --> LocalDate
        }
        int dateIndex = Arrays.asList(dayArray).indexOf(date.getSelectedItem().toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            realDate = realDate.plusDays(dateIndex);                // Get date by adding Monday + day in week index
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            localToCalendar(realDate.getYear(),realDate.getMonthValue(),realDate.getDayOfMonth(),name.getText().toString(),type.getSelectedItem().toString());
        }

        if (!extras.containsKey("id")) {                    // For adding data rather than updating
            assert realDate != null;
            if (myDB.insertSession(name.getText().toString(), realDate.toString(),
                    type.getSelectedItem().toString(), time.getText().toString(),
                    notes.getText().toString())) {
                Toast.makeText(getApplicationContext(), "Session added",
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "Session not added",
                        Toast.LENGTH_SHORT).show();
            }

            Bundle dataBundle = new Bundle();
            dataBundle.putString("date", String.valueOf(monday));
            dataBundle.putBooleanArray("features",features);
            Intent i = new Intent(getApplicationContext(),MainActivity.class);          // Take user back to main screen
            i.putExtras(dataBundle);
            startActivity(i);

        } else {
              // For updating session
                assert realDate != null;
                if (myDB.updateContact(id_To_Update, name.getText().toString(), realDate.toString(),
                        type.getSelectedItem().toString(), time.getText().toString(),
                        notes.getText().toString())) {

                    Toast.makeText(getApplicationContext(), "Session updated", Toast.LENGTH_SHORT).show();

                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("date", String.valueOf(monday));
                    dataBundle.putBooleanArray("features",features);            // Take user back to main screen
                    Intent i = new Intent(getApplicationContext(),MainActivity.class);
                    i.putExtras(dataBundle);
                    startActivity(i);

                } else {
                    Toast.makeText(getApplicationContext(), "not Updated", Toast.LENGTH_SHORT).show();
                }
            }
        }


        //
    public void startTimer(View v) {
        if (!running) {
            time.setBase(SystemClock.elapsedRealtime() - pauseOffset);
            time.start();
            running = true;
        }
    }

    public void pauseTimer(View v) {
        if (running) {
            time.stop();
            pauseOffset = SystemClock.elapsedRealtime() - time.getBase();
            running = false;
        }
    }

    public void resetTimer(View v) {
        time.setBase(SystemClock.elapsedRealtime());
        pauseOffset = 0;
    }

    private void notificationDelay (Notification notification , long delay) {
        Intent notificationIntent = new Intent( this, notificationReceiver. class ) ;
        notificationIntent.putExtra(notificationReceiver.ID , 1 ) ;
        notificationIntent.putExtra(notificationReceiver.NOTIFICATION , notification) ;
        // pending intent created to store the intent until it is needed
        PendingIntent pendingIntent = PendingIntent.getBroadcast (this, 0 , notificationIntent , PendingIntent.FLAG_UPDATE_CURRENT) ;
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE) ;
        assert alarmManager != null;
        // alarm manager used to call the intent after [(8 am of selected date) in millis - current time in millis] is met
        alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP , delay , pendingIntent) ;
    }

    private Notification notificationCreate (String title, String content,String type) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder( this, default_notification_channel_id ) ;
        builder.setContentTitle( title) ;
        builder.setContentText(content) ;
        if (type.equals("Gym")) {
            builder.setSmallIcon(R.drawable.gym);
        }                                                               // Add different images based on type of session
        if (type.equals("Track")) {
            builder.setSmallIcon(R.drawable.track);
        }
        if (type.equals("Long run")) {
            builder.setSmallIcon(R.drawable.running);
        }
        if (type.equals("Easy run")) {
            builder.setSmallIcon(R.drawable.easyrun);
        }
        builder.setAutoCancel( true ) ;
        builder.setChannelId( NOTIFICATION_CHANNEL_ID ) ;
        return builder.build() ;
    }




    private void localToCalendar (int year , int monthOfYear , int dayOfMonth,String name, String type) {
        myCalendar .set(Calendar. YEAR , year) ;
        myCalendar .set(Calendar. MONTH , monthOfYear - 1) ;
        myCalendar .set(Calendar. DAY_OF_MONTH , dayOfMonth) ;
        myCalendar.set(Calendar.HOUR_OF_DAY, 8);
        myCalendar.set(Calendar.MINUTE, 0);

        // so we can get the millis its converted to Calendar

        String dateFormat = "dd/MM/yy" ;
        SimpleDateFormat sdfObj = new SimpleDateFormat(dateFormat ,Locale. getDefault()) ;
        Date date = myCalendar.getTime();       // used to get delay for alarm
        String title = "You have a " + type + " session today - '" + name + "'";
        notificationDelay(notificationCreate(title,sdfObj.format(date),type) , date.getTime()-System.currentTimeMillis()) ;
    }

}