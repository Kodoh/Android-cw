package com.example.cob155cw;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public class CalendarContentProvider extends ContentProvider {

    private static final String AUTHORITY = "com.example.provider.calendar";

    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/events");

    private static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/vnd.com.example.provider.calendar";
    private static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/vnd.com.example.provider.calendar";

    private ContentResolver contentResolver;

    @Override
    public boolean onCreate() {
        contentResolver = getContext().getContentResolver();
        return true;
    }


    // to query calendar (idea not implemented)
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor cursor = contentResolver.query(CalendarContract.Events.CONTENT_URI, projection, selection, selectionArgs, sortOrder);
        if (cursor != null) {
            cursor.setNotificationUri(contentResolver, uri);
        }
        return cursor;
    }

    // to insert into calendar
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        Uri insertedUri = contentResolver.insert(CalendarContract.Events.CONTENT_URI, values);
        if (insertedUri != null) {
            contentResolver.notifyChange(uri, null);
        }
        return insertedUri;
    }

    // to update calendar
    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        int rowsUpdated = contentResolver.update(CalendarContract.Events.CONTENT_URI, values, selection, selectionArgs);
        if (rowsUpdated > 0) {
            contentResolver.notifyChange(uri, null);
        }
        return rowsUpdated;
    }

    // to delete calendar item
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted = contentResolver.delete(CalendarContract.Events.CONTENT_URI, selection, selectionArgs);
        if (rowsDeleted > 0) {
            contentResolver.notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    // to get type of calendar item (wanted to do this if I had time)
    @Override
    public String getType(Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case EVENTS:
                return CONTENT_TYPE;
            case EVENT_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI: " + uri);
        }
    }

    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int EVENTS = 1;
    private static final int EVENT_ID = 2;

    static {
        sUriMatcher.addURI(AUTHORITY, "events", EVENTS);
        sUriMatcher.addURI(AUTHORITY, "events/#", EVENT_ID);
    }
}
