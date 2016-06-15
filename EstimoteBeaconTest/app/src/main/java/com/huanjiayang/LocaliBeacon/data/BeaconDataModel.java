package com.huanjiayang.LocaliBeacon.data;

import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Huanjia on 11/05/2015.
 */
public class BeaconDataModel {

    public static final String CONTENT_AUTHORITY = "com.huanjiayang.LocaliBeacon";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_BEACON = "beacon";

    public static final class beaconEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_BEACON).build();

        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/"+CONTENT_AUTHORITY+"/"+PATH_BEACON;
        public static final String CONTENT_TYPE_ITEM = "vnd.android.cursor.item/"+CONTENT_AUTHORITY+"/"+PATH_BEACON;

        public static final String TABLE_NAME = "BEACON_TABLE";

        public static final String COLUMN_DATETIME = "DATE_TIME";
        public static final String COLUMN_BEACON01_ID = "BEACON01_ID";
        public static final String COLUMN_RSSI01 = "RSSI01";
        public static final String COLUMN_BEACON02_ID = "BEACON02_ID";
        public static final String COLUMN_RSSI02 = "RSSI02";
        public static final String COLUMN_BEACON03_ID = "BEACON03_ID";
        public static final String COLUMN_RSSI03 = "RSSI03";


        public static Uri buildBeaconUriWithId(long id){
            return ContentUris.withAppendedId(CONTENT_URI, id);
        }

    }

}
