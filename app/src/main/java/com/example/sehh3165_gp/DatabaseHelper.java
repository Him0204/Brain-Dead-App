package com.example.sehh3165_gp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

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
    private static final String COLUMN2_TIME = "time";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.beginTransaction();
        try {
            String createLoginTable = "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_EMAIL + " TEXT PRIMARY KEY, " +
                    COLUMN_PASSWD + " TEXT)";
            db.execSQL(createLoginTable);
            String createInfoTable = "CREATE TABLE " + TABLE2_NAME + " (" +
                    COLUMN2_EMAIL + " TEXT PRIMARY KEY, " +
                    COLUMN2_USERNAME + " TEXT, " +
                    COLUMN2_STAGE + " INTEGER, " +
                    COLUMN2_TIME + " INTEGER)";
            db.execSQL(createInfoTable);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error creating tables", e);
        } finally {
            db.endTransaction();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE2_NAME);
        onCreate(db);
    }

    //Create new record in database when signup
    public boolean inputData(String email, String passwd, String username) {
        SQLiteDatabase DB = this.getWritableDatabase();
        DB.beginTransaction();
        long result1 = -1, result2 = -1;
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(COLUMN_EMAIL, email);
            contentValues.put(COLUMN_PASSWD, passwd);
            result1 = DB.insert(TABLE_NAME, null, contentValues);

            ContentValues contentValues2 = new ContentValues();
            contentValues2.put(COLUMN2_EMAIL, email);
            contentValues2.put(COLUMN2_USERNAME, username);
            contentValues2.put(COLUMN2_STAGE, 0);
            contentValues2.put(COLUMN2_TIME, 0);
            result2 = DB.insert(TABLE2_NAME, null, contentValues2);

            if (result1 != -1 && result2 != -1) {
                DB.setTransactionSuccessful();
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error inserting data", e);
        } finally {
            DB.endTransaction();
        }
        return result1 != -1 && result2 != -1;
    }

    //Update password when user forget
    public boolean updatePw(String email, String passwd) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN_PASSWD, passwd);
        int result = DB.update(TABLE_NAME, contentValues, COLUMN_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

    //Check if email exist: T -> exist; F -> not exist
    public boolean checkRecord(String email) {
        try (SQLiteDatabase DB = this.getReadableDatabase(); Cursor cursor = DB.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_EMAIL + " = ?", new String[]{email})) {
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error checking record", e);
            return false;
        }
    }

    //Check if the password is correct, call checkRecord() first before this
    public boolean validate(String email, String passwd) {
        try (SQLiteDatabase DB = this.getReadableDatabase(); Cursor cursor = DB.rawQuery(
                "SELECT * FROM " + TABLE_NAME + " WHERE " +
                        COLUMN_EMAIL + " = ? AND " + COLUMN_PASSWD + " = ?", new String[]{email, passwd})) {
            return cursor != null && cursor.moveToFirst();
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error validating user", e);
            return false;
        }
    }

    //Get user data for lobby, [0]Username [1]Stage [2]Time used
    public String getInfo(String email, int i) {
        String[] getBack = {"Unknown", "Unknown", "Unknown"};
        try (SQLiteDatabase DB = this.getReadableDatabase();
             Cursor cursor = DB.rawQuery("SELECT " + COLUMN2_USERNAME + ", " + COLUMN2_STAGE + ", " + COLUMN2_TIME +
                     " FROM " + TABLE2_NAME + " WHERE " + COLUMN2_EMAIL + " = ?", new String[]{email})) {
            if (cursor != null && cursor.moveToFirst()) {
                getBack[0] = cursor.getString(0);
                getBack[1] = cursor.getString(1);
                getBack[2] = cursor.getString(2);
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error retrieving user info", e);
            return "Error retrieving data";
        }
        return getBack[i];
    }

    //Get all users' data for scoreboard, [1]Username [2]Stage [3]Time used
    public String[][] getAllInfo() {
        SQLiteDatabase DB = this.getReadableDatabase();
        List<String[]> resultList = new ArrayList<>();
        try (Cursor cursor = DB.rawQuery(
                "SELECT "+COLUMN2_USERNAME+", "+COLUMN2_STAGE+", "+COLUMN2_TIME+" FROM "+TABLE2_NAME+
                        " ORDER BY "+COLUMN2_STAGE+" DESC, "+COLUMN2_TIME+" ASC", null)) {

            if (cursor != null) {
                while (cursor.moveToNext()) {
                    String[] row = new String[3];
                    row[0] = cursor.getString(0); // Username
                    row[1] = cursor.getString(1); // Stage
                    row[2] = cursor.getString(2); // Time
                    resultList.add(row);
                }
            }
        } catch (Exception e) {
            Log.e("DatabaseHelper", "Error fetching all user info", e);
        } finally {
            DB.close();
        }
        String[][] getBack = new String[resultList.size()][];
        return resultList.toArray(getBack);
    }

    //Update Stage and Point after completing each level
    public boolean updateStatus(String email, String stage, String time) {
        SQLiteDatabase DB = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COLUMN2_STAGE, stage);
        contentValues.put(COLUMN2_TIME, time);
        int result = DB.update(TABLE2_NAME, contentValues, COLUMN2_EMAIL + "=?", new String[]{email});
        return result > 0;
    }

}
