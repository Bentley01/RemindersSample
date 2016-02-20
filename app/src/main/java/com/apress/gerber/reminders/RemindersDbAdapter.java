package com.apress.gerber.reminders;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * Created by Paul on 20-Feb-16.
 */
public class RemindersDbAdapter {

    // set constants for the column names in the database
    public static final String COL_ID = "_id";
    public static final String COL_CONTENT = "content";
    public static final String COL_IMPORTANT = "important";

    // set the indices for each column in the database
    public static final int INDEX_ID = 0;
    public static final int INDEX_CONTENT = INDEX_ID + 1;
    public static final int INDEX_IMPORTANT = INDEX_ID + 2;

    // set up constant for logging
    private static final String TAG = "RemindersDbAdapter";

    private DatabaseHelper mDbHelper;
    private SQLiteDatabase mDb;

    // set up constants for the Db name, table name and version
    private static final String DATABASE_NAME = "dba_remdrs";
    private static final String TABLE_NAME = "tbl_remdrs";
    private static final int DATABASE_VERSION = 1;

    // set up a reference to the context
    private final Context mCtx;

    // SQL statement to create the database
    private static final String DATABASE_CREATE =
            "CREATE TABLE if not exists " + TABLE_NAME + " ( " +
                    COL_ID + " INTEGER PRIMARY KEY autoincrement, " +
                    COL_CONTENT + " TEXT, " +
                    COL_IMPORTANT + " INTEGER );";


    public RemindersDbAdapter(Context ctx) {
        mCtx = ctx;
    }

    public void open() throws SQLException {
        // create a new DatabaseHelper with the current context
        mDbHelper = new DatabaseHelper(mCtx);
        // get a reference to a writable database
        mDb = mDbHelper.getWritableDatabase();
    }

    public void close() {
        // if mDbHelper exists, close it
        if (mDbHelper != null) { mDbHelper.close(); }
    }

    // CREATE
    // id will be created automatically
    public void createReminder(String name, boolean important) {
        // create a ContentValues reference to hold map of values to insert into the database
        ContentValues values = new ContentValues();
        // add map of COLUMN -> value to the content values reference
        values.put(COL_CONTENT, name);
        values.put(COL_IMPORTANT, important ? 1 : 0);
        // insert the new content values into the TABLE_NAME table of the database
        mDb.insert(TABLE_NAME, null, values);
    }

    // READ
    public Reminder fetchReminderById(int id) {

        // create a new cursor to receive the row of a query executed by the database helper
        Cursor cursor = mDb.query(TABLE_NAME, new String[] { COL_ID, COL_CONTENT, COL_IMPORTANT },
                COL_ID + "=?", new String[] { String.valueOf(id)}, null, null, null, null);
        // if the cursor is not null (i.e. something returned by query)
        if (cursor != null) { cursor.moveToFirst(); }

        return new Reminder(cursor.getInt(INDEX_ID), cursor.getString(INDEX_CONTENT),
                cursor.getInt(INDEX_IMPORTANT));
    }

    public Cursor fetchAllReminders() {

        Cursor mCursor = mDb.query(TABLE_NAME, new String[] { COL_ID, COL_CONTENT, COL_IMPORTANT },
                null, null, null, null, null);
        if (mCursor != null) { mCursor.moveToFirst(); }
        return mCursor;
    }

    // UPDATE
    public void updateReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());
        // update database where col id = reminder col id
        mDb.update(TABLE_NAME, values, COL_ID + "=?", new String[] {String.valueOf(reminder.getId())});

    }

    // DELETE

    public void deleteReminderById(int nID) {
        // note we have to convert the int id to a string and put it in an array
        mDb.delete(TABLE_NAME, COL_ID + "=?", new String[] { String.valueOf(nID) });
    }

    public void deleteAllReminders() {
        mDb.delete(TABLE_NAME, null, null);
    }

    // overloaded method using a reminder as the parameter
    public long createReminder(Reminder reminder) {
        ContentValues values = new ContentValues();
        // get values from the parameter reminder
        values.put(COL_CONTENT, reminder.getContent());
        values.put(COL_IMPORTANT, reminder.getImportant());
        // insert into the database and return row number (id)
        return mDb.insert(TABLE_NAME, null, values);
    }

    // inner class Database helper class

    private static class DatabaseHelper extends SQLiteOpenHelper {

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            Log.w(TAG, DATABASE_CREATE);
            // Execute the SQL statement to create a new database
            db.execSQL(DATABASE_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrading database from version " + oldVersion + " to " + newVersion
                + ", which will destroy all old data.");
            // delete old table, if it exists
            db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
            // create new database by calling onCreate(db)
            onCreate(db);

        }
    }

}
