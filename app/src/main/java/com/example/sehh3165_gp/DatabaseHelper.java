package com.example.sehh3165_gp;

import android.content.Context;
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

}
