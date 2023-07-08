package com.example.cob155cw;

import android.annotation.SuppressLint;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.os.IBinder;
import android.text.Editable;
import android.text.TextWatcher;


import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.ListView;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Locale;


public class MainActivity extends AppCompatActivity {
    DBHelper myDB;
    LocalDate monday;
    String formattedDate;

    boolean[] features = new boolean[]{false, false, false, false};
    ImageButton addSessionButton;
    ImageButton userGuidButton;
    ImageButton settingsButton;
    Button weekSelectButton;
    SessionListAdapter itemAdapter;
    ArrayList<SessionData> array_list = new ArrayList<>();
    Bundle extras;

    private LocationTrackingService locationService;
    private boolean isBound = false;

    private final ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder binder) {
            LocationTrackingService.LocalBinder localBinder = (LocationTrackingService.LocalBinder) binder;
            locationService = localBinder.getService();
            isBound = true;
            if (extras == null) {
                retrieveLocalDateTime();
            }
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            locationService = null;
            isBound = false;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent serviceIntent = new Intent(this, LocationTrackingService.class);
        bindService(serviceIntent, serviceConnection, Context.BIND_AUTO_CREATE);
        setContentView(R.layout.activity_main);
        extras = getIntent().getExtras();
        myDB = new DBHelper(this);

        if (extras == null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                getCurrentMonday(LocalDate.now());
            }
        }
        else {

            if (extras.containsKey("date")) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    monday = LocalDate.parse(extras.getString("date"), formatter);              // Get date manually set by user
                }
            }

            if (extras.containsKey("features")) {
                features = extras.getBooleanArray("features");              // If we are not using default settings
            }
        }

        TextView textView = findViewById(R.id.textView);
        Resources resources = getResources();
        textView.setText(String.format(resources.getString(R.string.MainScreenTitle),monday));      // Title

        addSessionButton = findViewById(R.id.imageButton2);
        addSessionButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    Intent i = new Intent(getApplicationContext(),EditSession.class);           // Add session
                    dataBundle.putString("date", String.valueOf(monday));
                    dataBundle.putBooleanArray("features",features);
                    i.putExtras(dataBundle);
                    startActivity(i);

                }
        );

        settingsButton = findViewById(R.id.imageButton7);
        settingsButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putBooleanArray("features",features);                            // To settings page
                    dataBundle.putString("date", String.valueOf(monday));
                    Intent i = new Intent(getApplicationContext(),Settings.class);
                    i.putExtras(dataBundle);
                    startActivity(i);
                }
        );



        weekSelectButton = (Button) textView;
        weekSelectButton.setOnClickListener(
                v -> {
                    Bundle dataBundle = new Bundle();
                    dataBundle.putString("date", String.valueOf(monday));
                    dataBundle.putBooleanArray("features",features);                            // To week select page
                    Intent i = new Intent(getApplicationContext(),WeekSelect.class);
                    i.putExtras(dataBundle);
                    startActivity(i);


                }
        );

        userGuidButton = findViewById(R.id.imageButton9);
        userGuidButton.setOnClickListener(
                v -> {
                    Intent i = new Intent(getApplicationContext(),user_guid.class);         // User guid
                    startActivity(i);
                }
        );


        getSessions();                                                      // Get Sessions from DB

        ListView listView = this.findViewById(R.id.postListView);
        itemAdapter = new SessionListAdapter(this,
                R.layout.item, array_list);                         // Display sessions
        listView.setAdapter(itemAdapter);

        EditText inputSearch = this.findViewById(R.id.searchView2);             // Custom adapter for editText
        inputSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                String text = inputSearch.getText().toString().toLowerCase(Locale.getDefault());
                itemAdapter.filter(text);                                                          // Filter all that do not match the input --> see adapter class for how this is done
            }
            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                          int arg3) {
            }
            @Override
            public void afterTextChanged(Editable arg0) {
            }
        });
        inputSearch.setFocusableInTouchMode(true);


        listView.setOnItemClickListener((arg0, arg1, arg2, arg3) -> {
            int id_To_Search = Integer.parseInt(array_list.get(arg2).id);           // Get the id associated with the item clicked

            Bundle dataBundle = new Bundle();                                   // View Session
            dataBundle.putInt("id", id_To_Search);
            dataBundle.putString("date", String.valueOf(monday));
            dataBundle.putBooleanArray("features",features);
            Intent intent = new Intent(getApplicationContext(),ViewSession.class);

            intent.putExtras(dataBundle);
            startActivity(intent);
        });



}


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Unbind from the LocationTrackingService
        if (isBound) {
            unbindService(serviceConnection);
            isBound = false;
        }
    }

    private void retrieveLocalDateTime() {
        if (isBound && locationService != null) {
            LocalDate localDateTime = locationService.getLocalDateTime();
            getCurrentMonday(localDateTime);
            TextView textView = findViewById(R.id.textView);
            Resources resources = getResources();
            textView.setText(String.format(resources.getString(R.string.MainScreenTitle),monday));      // Title
            getSessions();
        }
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private void createSessionObj(String date, String post,String type,String id) {
        SessionData data;
        data = new SessionData();
        data.sessionDate = date;
        data.sessionTitle = post;                      // Create an object for the session
        data.id = id;
        data.typeImage = type;
        array_list.add(data);

    }

    private void getCurrentMonday(LocalDate today) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            monday = today;
            while (monday.getDayOfWeek() != DayOfWeek.MONDAY) {         // minus the days until we get to monday
                monday = monday.minusDays(1);
            }
        }
    }

    private void getSessions() {
        Cursor res = myDB.getAllSessions();
        res.moveToFirst();

        while(!res.isAfterLast()){
            @SuppressLint("Range") String nam = res.getString(res.getColumnIndex(DBHelper.SESSIONS_COLUMN_NAME));
            @SuppressLint("Range") String dat = res.getString(res.getColumnIndex(DBHelper.SESSIONS_COLUMN_DATE));
            @SuppressLint("Range") String typ = res.getString(res.getColumnIndex(DBHelper.SESSIONS_COLUMN_TYPE));
            @SuppressLint("Range") String id = res.getString(res.getColumnIndex(DBHelper.SESSIONS_COLUMN_ID));
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDate sessionDate = LocalDate.parse(dat);
                if ((sessionDate.isAfter( monday) || sessionDate.isEqual( monday)) && sessionDate.isBefore( monday.plusWeeks(1))) {
                    LocalDate realDate;
                    realDate = LocalDate.parse(dat);
                    formattedDate = realDate.format(DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL));
                    createSessionObj(formattedDate,nam,typ,id);
                }
            }
            res.moveToNext();
        }

        if (features[2]) {
            ArrayList<String> uniques = new ArrayList<>();
            for (SessionData item : array_list) {
                if (!uniques.contains(item.sessionTitle)) {
                    uniques.add(item.sessionTitle);
                }
                else {
                    array_list.remove(item);
                }
            }
        }
    }



}