package com.example.sehh3165_gp;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "BrainDead_DB";

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

    //Create new record in database when signup
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

    //Update password when user forget
    public Boolean updatePw(String email, String passwd) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_EMAIL, email);
        contentValues.put(COLUMN_PASSWD, passwd);
        long result = DB.update(TABLE_NAME, contentValues, COLUMN_EMAIL+"=?", new String[]{email});

        return result != -1;
    }

    //Check if email exist: T -> exist; F -> not exist
    public Boolean checkRecord(String email) {
        SQLiteDatabase DB = this.getReadableDatabase();
        try (Cursor cursor = DB.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+COLUMN_EMAIL+" = ?", new String[]{email})){
            if (cursor != null && cursor.moveToFirst())
                return cursor.getCount() > 0;
        }
        return false;
    }

    //Check if the password is correct, call checkRecord() first before this
    public Boolean validate(String email, String passwd) {
        SQLiteDatabase DB = this.getReadableDatabase();
        try (Cursor cursor = DB.rawQuery(
                "SELECT * FROM "+TABLE_NAME+" WHERE "+
                        COLUMN_EMAIL+" = ? and "+COLUMN_PASSWD+" = ?", new String[]{email, passwd})){
            if (cursor != null && cursor.moveToFirst())
                return cursor.getCount() > 0;
        }
        return false;
    }

    //Get user data for lobby, [0]Username [1]Stage [2]Point
    public void getInfo(String email, String[] getBack) {
        SQLiteDatabase DB = this.getReadableDatabase();
        try (Cursor cursor = DB.rawQuery(
                "SELECT "+COLUMN2_USERNAME+", "+COLUMN2_STAGE+", "+COLUMN2_POINT+" FROM " +
                        TABLE2_NAME + " WHERE " + COLUMN2_EMAIL + " = ?", new String[]{email})) {
            if (cursor != null && cursor.moveToFirst()) {
                getBack[0] = cursor.getString(0);
                getBack[1] = cursor.getString(1);
                getBack[2] = cursor.getString(2);
            }
        }
    }

    //Get all users' data for scoreboard, [1]Username [2]Stage [3]Point
    public void getAllInfo(String[][] getBack) {
        SQLiteDatabase DB = this.getReadableDatabase();
        try (Cursor cursor = DB.rawQuery(
                "SELECT "+COLUMN2_USERNAME+", "+COLUMN2_STAGE+", "+COLUMN2_POINT+" FROM "+TABLE2_NAME, null)) {
            int i = 0;
            while (cursor != null && cursor.moveToNext()) {
                if (getBack[i] == null) {
                    getBack[i] = new String[3];
                }
                getBack[i][0] = cursor.getString(0);
                getBack[i][1] = cursor.getString(1);
                getBack[i][2] = cursor.getString(2);

                i++;
            }
        }
    }

    //Update Stage and Point after completing each level
    public Boolean updateStatus(String email, String stage, String point) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN2_STAGE, stage);
        contentValues.put(COLUMN2_POINT, point);
        long result = DB.update(TABLE2_NAME, contentValues, COLUMN2_EMAIL+"=?", new String[]{email});
        return result != -1;
    }
}
