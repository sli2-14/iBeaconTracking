package com.huanjiayang.LocaliBeacon.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Huanjia on 11/05/2015.
 */
public class BeaconDBHelper extends SQLiteOpenHelper {
    public static  final int DB_VERSION = 2;

    public static final String DB_NAME = "beacon.db";

    public SQLiteDatabase beaconDB;

    public BeaconDBHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DB_NAME, null, DB_VERSION);
        beaconDB = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        beaconDB = db;

        String query = "CREATE TABLE " +
                BeaconDataModel.beaconEntry.TABLE_NAME +
                " ( " + BeaconDataModel.beaconEntry._ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                BeaconDataModel.beaconEntry.COLUMN_DATETIME +
                " TEXT NOT NULL, " +
                BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID +
                " TEXT NOT NULL, " +
                BeaconDataModel.beaconEntry.COLUMN_RSSI01 +
                " INTEGER NOT NULL, " +
                BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID +
                " TEXT NOT NULL, " +
                BeaconDataModel.beaconEntry.COLUMN_RSSI02 +
                " INTEGER NOT NULL, " +
                BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID +
                " TEXT NOT NULL, " +
                BeaconDataModel.beaconEntry.COLUMN_RSSI03 +
                " INTEGER NOT NULL );";

        db.execSQL(query);
       // Log.i("LocaliBeacon", "BeaconDBHelper onCreate()");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + BeaconDataModel.beaconEntry.TABLE_NAME);

        onCreate(db);
    }


    public void clearTable(String table_name){
        beaconDB.execSQL("DELETE FROM "+ table_name);
    }
}
