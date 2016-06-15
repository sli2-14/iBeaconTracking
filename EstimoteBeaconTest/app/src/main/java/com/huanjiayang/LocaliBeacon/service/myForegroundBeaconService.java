package com.huanjiayang.LocaliBeacon.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.app.Notification;
import android.os.RemoteException;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.huanjiayang.LocaliBeacon.R;
import com.huanjiayang.LocaliBeacon.data.BeaconDataModel;

import java.util.Calendar;
import java.util.List;

public class myForegroundBeaconService extends Service {
    private static int FOREGROUND_ID= 51399;
    public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
    public static final String EXTRAS_BEACON = "extrasBeacon";

    private static final int REQUEST_ENABLE_BT = 1234;
    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    private BeaconManager beaconManager;

    private ContentValues values = new ContentValues();

    private int count = 0;

    public myForegroundBeaconService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Configure BeaconManager.
        Log.i("LocaliBeacon_foreground_service", "start running");
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that beacons reported here are already sorted by estimated
                // distance between device and beacon.
                SharedPreferences ibeacon_interval_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                String ibeacon_interval = ibeacon_interval_pref.getString(getString(R.string.iBeacon_recording_interval),getString(R.string.iBeacon_recording_interval_default));
                int ibi = Integer.valueOf(ibeacon_interval);

                count++;
                if(count == ibi) {
                    count = 0;
                    SharedPreferences ibeacon_number_pref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
                    String ibeacon_number = ibeacon_number_pref.getString(getString(R.string.iBeacon_recording_number), getString(R.string.iBeacon_recording_number_default));
                    int ibn = Integer.valueOf(ibeacon_number);

/*
                    Log.i("LocaliBeacon_foreground_service", "Found beacons: " + beacons.size());
                    String locText = "The nearest " + ibeacon_number + " beacons: \n";
                    for (int i = 0; i < beacons.size() && i < ibn; i++)
                        locText = locText + "MAC: " + beacons.get(i).getMacAddress() + " RSSI: " + beacons.get(i).getRssi() + "\n";
                    Log.i("LocaliBeacon_foreground_service", locText);
*/
                    values.put(BeaconDataModel.beaconEntry.COLUMN_DATETIME, Calendar.getInstance().getTime().toString());
/*
                for(int i = 0; i<beacons.size() && i<ibn; i++){
                    if(beacons.size()>i){
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID, beacons.get(0).getMacAddress());
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI01, beacons.get(0).getRssi());
                    }
                    else{
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID, "NA");
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI01, "NA");
                    }

                }
*/

                    if (beacons.size() > 0) {
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID, beacons.get(0).getMacAddress());
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI01, beacons.get(0).getRssi());
                    } else {
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID, "NA");
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI01, "NA");
                    }

                    if (beacons.size() > 1) {
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID, beacons.get(1).getMacAddress());
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI02, beacons.get(1).getRssi());
                    } else {
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID, "NA");
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI02, "NA");
                    }

                    if (beacons.size() > 2) {
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID, beacons.get(2).getMacAddress());
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI03, beacons.get(2).getRssi());
                    } else {
                        values.put(BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID, "NA");
                        values.put(BeaconDataModel.beaconEntry.COLUMN_RSSI03, "NA");
                    }

                    getApplicationContext().getContentResolver().insert(BeaconDataModel.beaconEntry.CONTENT_URI, values);
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        beaconManager.disconnect();
        super.onDestroy();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals("start_beacon_foreground_service")) {
            Log.i("estimode_Loc_foreground_service", "Received start Foreground Intent");
            // Check if device supports Bluetooth Low Energy.
            if (!beaconManager.hasBluetooth()) {
                Log.i("estimode_Loc_foreground_service", "Device does not have Bluetooth Low Energy");

            } else {
                connectToService();
                startForeground(FOREGROUND_ID,
                        buildForegroundNotification());
            }
        }
        else if (intent.getAction().equals("stop_beacon_foreground_service")){
            Log.i("estimode_Loc_foreground_service", "Received Stop Foreground Intent");
            stopForeground(true);
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void connectToService() {
        Log.i("estimode_Loc_foreground_service", "Scanning");
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                try {
                    beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
                } catch (RemoteException e) {
                    Log.i("estimode_Loc_foreground_service", "Cannot start ranging, something terrible happened");
                }
            }
        });
    }


    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true);

        b.setContentTitle("Estimote Beacon Service")
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Running");

        return(b.build());
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
        //throw new UnsupportedOperationException("Not yet implemented");
    }
}
