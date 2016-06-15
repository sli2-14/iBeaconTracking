package com.huanjiayang.LocaliBeacon.service;

import android.app.IntentService;
import android.app.Notification;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.huanjiayang.LocaliBeacon.R;

import java.util.List;

/**
 * Created by Huanjia on 25/02/2015.
 */
public class MyBeaconService extends IntentService{
    private static int FOREGROUND_ID= 51399;
        //private static final String TAG = com.huanjiayang.LocaliBeacon.ListBeaconsActivity.class.getSimpleName;
        public static final String EXTRAS_TARGET_ACTIVITY = "extrasTargetActivity";
        public static final String EXTRAS_BEACON = "extrasBeacon";

        private static final int REQUEST_ENABLE_BT = 1234;
        private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

        private BeaconManager beaconManager;


    public MyBeaconService() {
        super("MyBeaconClass");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Configure verbose debug logging.
        //L.enableDebugLogging(true);


    }

    @Override
    protected void onHandleIntent(Intent intent) {
        startForeground(FOREGROUND_ID,
                buildForegroundNotification());
        Log.i("LocaliBeacon_service", " running!");

        // Configure BeaconManager.
        beaconManager = new BeaconManager(this);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, final List<Beacon> beacons) {
                // Note that beacons reported here are already sorted by estimated
                // distance between device and beacon.
                Log.i("LocaliBeacon_service","Found beacons: " + beacons.size());
                String locText = "The nearest 3 beacons: \n";
                for(int i = 0; i<beacons.size() && i<3; i++)
                    locText = locText + "MAC: " + beacons.get(i).getMacAddress() + " RSSI: " + beacons.get(i).getRssi() + "\n";
                Log.i("estimode_Loc_service", locText);
            }
        });
        stopForeground(true);
    }

    private Notification buildForegroundNotification() {
        NotificationCompat.Builder b=new NotificationCompat.Builder(this);

        b.setOngoing(true);

        b.setContentTitle("Estimote Beacon Service")
                .setSmallIcon(R.drawable.beacon_gray)
                .setContentText("Running");

        return(b.build());
    }
}
