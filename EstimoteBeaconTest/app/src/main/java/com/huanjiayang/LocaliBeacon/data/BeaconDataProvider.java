package com.huanjiayang.LocaliBeacon.data;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class BeaconDataProvider extends ContentProvider {

    public static final int ALL_BEACON = 102;
    public static final int BEACON = 100;
    public static final int BEACON_WITH_ID = 101;
    private static final UriMatcher beaconUriMatcher = buildUriMatcher();
    public static BeaconDBHelper myDBHelper;

    private static UriMatcher buildUriMatcher(){
        final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);

        matcher.addURI(BeaconDataModel.CONTENT_AUTHORITY,"/",ALL_BEACON);

        matcher.addURI(BeaconDataModel.CONTENT_AUTHORITY,BeaconDataModel.PATH_BEACON,BEACON);

        matcher.addURI(BeaconDataModel.CONTENT_AUTHORITY,BeaconDataModel.PATH_BEACON+"/#",BEACON_WITH_ID);

        return matcher;
    }

    public BeaconDataProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // TODO: Implement this to handle requests to update one or more rows.
        int match_code = beaconUriMatcher.match(uri);

        switch(match_code){
            case BEACON:{
                myDBHelper.clearTable(BeaconDataModel.beaconEntry.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.
        int match_code = beaconUriMatcher.match(uri);

        switch(match_code){
            case ALL_BEACON:
                return BeaconDataModel.beaconEntry.CONTENT_TYPE_DIR;
            case BEACON:
                return BeaconDataModel.beaconEntry.CONTENT_TYPE_DIR;
            case BEACON_WITH_ID:
                return BeaconDataModel.beaconEntry.CONTENT_TYPE_ITEM;
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        // TODO: Implement this to handle requests to insert a new row.
        int match_code = beaconUriMatcher.match(uri);
        Uri retUri = null;

        switch(match_code){
            case BEACON:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                long _id = db.insert(BeaconDataModel.beaconEntry.TABLE_NAME,null,values);
                if (_id > 0)
                    retUri = BeaconDataModel.beaconEntry.buildBeaconUriWithId(_id);
                else
                    throw new SQLException("failed to insert");
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return retUri;
    }

    @Override
    public boolean onCreate() {
        // TODO: Implement this to initialize your content provider on startup.
        myDBHelper = new BeaconDBHelper(getContext(),BeaconDBHelper.DB_NAME,null,BeaconDBHelper.DB_VERSION);
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        int match_code = beaconUriMatcher.match(uri);
        Cursor myCursor;

        switch(match_code){
            case BEACON:{
                SQLiteDatabase db = myDBHelper.getWritableDatabase();
                myCursor = db.query(
                        BeaconDataModel.beaconEntry.TABLE_NAME, // Table to Query
                        projection,
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null, // columns to group by
                        null, // columns to filter by row groups
                        null // sort order
                );
               // Log.i("LocaliBeacon", "querying for BEACON");
               // Log.i("LocaliBeacon", myCursor.getCount()+"");
                break;
            }
            /*
            case WEATHER_WITH_ID:{
                myCursor = myDBHelper.getReadableDatabase().query(
                        WeatherDataModel.weatherEntry.TABLE_NAME,
                        projection,
                        WeatherDataModel.weatherEntry.COLUMN_DAY_NO + " = '" + ContentUris.parseId(uri) + "'",
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            }*/
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        myCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return myCursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int match_code = beaconUriMatcher.match(uri);

        switch(match_code){
            case BEACON:{
                myDBHelper.clearTable(BeaconDataModel.beaconEntry.TABLE_NAME);
                break;
            }
            default:
                throw new UnsupportedOperationException("Not yet implemented");
        }

        return 0;
    }
}
