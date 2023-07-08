package com.example.cob155cw;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteDatabase;

public class DBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "COB155cw.db";
    public static final String SESSIONS_COLUMN_ID = "id";
    public static final String SESSIONS_COLUMN_NAME = "name";
    public static final String SESSIONS_COLUMN_DATE = "date";
    public static final String SESSIONS_COLUMN_TYPE = "type";
    public static final String SESSIONS_COLUMN_TIME = "time";
    public static final String SESSIONS_COLUMN_NOTES = "notes";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME , null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // TODO Auto-generated method stub
        db.execSQL(
                "create table sessions " +
                        "(id integer primary key, name text,date text,type text, time real,notes text)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO Auto-generated method stub
        db.execSQL("DROP TABLE IF EXISTS sessions");
        onCreate(db);
    }

    public boolean insertSession (String name, String date, String type, String time,String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("date", date);
        contentValues.put("type", type);
        contentValues.put("time", time);
        contentValues.put("notes", notes);
        db.insert("sessions", null, contentValues);
        return true;
    }

    public Cursor getData(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from sessions where id="+id+"", null );
    }


    public boolean updateContact (Integer id, String name, String date, String type, String time,String notes) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("name", name);
        contentValues.put("date", date);
        contentValues.put("type", type);
        contentValues.put("time", time);
        contentValues.put("notes", notes);
        db.update("sessions", contentValues, "id = ? ", new String[] { Integer.toString(id) } );
        return true;
    }

    public void deleteSession (Integer id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("sessions",
                "id = ? ",
                new String[] { Integer.toString(id) });
    }


    public int deleteAllSessions () {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete("sessions",
                "1",
                null);
    }

    @SuppressLint("Range")
    public Cursor getAllSessions() {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery( "select * from sessions", null );
    }

}
