package com.example.sehh3165_gp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "_DB";

    protected static final String TABLE_NAME = "login";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWD = "password";

    private static final String TABLE2_NAME = "info";
    private static final String COLUMN2_EMAIL = "email";
    private static final String COLUMN2_USERNAME = "username";
    private static final String COLUMN2_STAGE = "stage";
    private static final String COLUMN2_POINT = "point";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " (" +
                COLUMN_EMAIL + " PRIMARY KEY, " +
                COLUMN_PASSWD + " TEXT)";
        db.execSQL(query);
        query = "CREATE TABLE " + TABLE2_NAME + " (" +
                COLUMN2_EMAIL + " PRIMARY KEY, " +
                COLUMN2_USERNAME + " TEXT, " +
                COLUMN2_STAGE + " TEXT, " +
                COLUMN2_POINT + " TEXT)";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    public Boolean inputData(String email, String passwd, String username) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWD, passwd);
        long result1 = DB.insert(TABLE_NAME, null, contentValues);
        ContentValues contentValues2 = new ContentValues();
        contentValues2.put(COLUMN2_EMAIL, email);
        contentValues2.put(COLUMN2_USERNAME, username);
        contentValues2.put(COLUMN2_STAGE, 1);
        contentValues2.put(COLUMN2_POINT, 0);
        long result2 = DB.insert(TABLE2_NAME, null, contentValues2);

        return result1 != -1 && result2 != -1;
    }

    public Boolean updatePw(String email, String passwd) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWD, passwd);
        long result = DB.update(TABLE_NAME, contentValues, COLUMN_EMAIL+"=?", new String[]{email});

        return result != -1;
    }

    public Boolean checkRecord(String email) {
        SQLiteDatabase DB = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_EMAIL+" = ?", new String[]{email});
        return cursor.getCount() > 0;
    }

    public Boolean validate(String email, String passwd) {
        SQLiteDatabase DB = this.getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = DB.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_EMAIL+" = ? and "+COLUMN_PASSWD+" = ?", new String[]{email, passwd});
        return cursor.getCount() > 0;
    }

}
