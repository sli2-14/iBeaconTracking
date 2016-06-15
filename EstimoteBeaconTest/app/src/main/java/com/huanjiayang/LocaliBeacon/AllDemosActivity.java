package com.huanjiayang.LocaliBeacon;

import android.app.ActivityManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.huanjiayang.LocaliBeacon.data.BeaconDBHelper;
import com.huanjiayang.LocaliBeacon.data.BeaconDataModel;
import com.huanjiayang.LocaliBeacon.service.MyBeaconService;
import com.huanjiayang.LocaliBeacon.service.myForegroundBeaconService;
import com.opencsv.CSVWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class AllDemosActivity extends ActionBarActivity {

    private int myIntent_ID = 51398;
    private String myForegroundServiceName = "com.huanjiayang.LocaliBeacon.service.myForegroundBeaconService";
    private int sdk = android.os.Build.VERSION.SDK_INT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.all_demos);
        //setTitle("Welcome to LocaliBeacon");
        //Intent serviceIntent = new Intent(AllDemosActivity.this, MyBeaconService.class);
        //startService(serviceIntent);

        if(isServiceRunning(myForegroundServiceName)) {
            //makeToast("Background Service is running");
            findViewById(R.id.start_service_button).setEnabled(false);
            setButtonBackground(R.id.start_service_button,R.drawable.grey_button);
            findViewById(R.id.stop_service_button).setEnabled(true);
            setButtonBackground(R.id.stop_service_button,R.drawable.orange_button);
        }
        else {
            //makeToast("Background Service is not running");
            findViewById(R.id.stop_service_button).setEnabled(false);
            setButtonBackground(R.id.stop_service_button,R.drawable.grey_button);
            findViewById(R.id.start_service_button).setEnabled(true);
            setButtonBackground(R.id.start_service_button,R.drawable.green_button);
        }


        findViewById(R.id.distance_demo_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AllDemosActivity.this, ListBeaconsActivity.class);
                intent.putExtra(ListBeaconsActivity.EXTRAS_TARGET_ACTIVITY, DistanceBeaconActivity.class.getName());
                startActivity(intent);
            }
        });
        findViewById(R.id.start_service_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent startIntent = new Intent(AllDemosActivity.this, myForegroundBeaconService.class);
                startIntent.setAction("start_beacon_foreground_service");
                startService(startIntent);

                if(isServiceRunning(myForegroundServiceName)) {
                    //makeToast("is running");
                    makeToast("Background service started");
                    findViewById(R.id.start_service_button).setEnabled(false);
                    setButtonBackground(R.id.start_service_button,R.drawable.grey_button);
                    findViewById(R.id.stop_service_button).setEnabled(true);
                    setButtonBackground(R.id.stop_service_button,R.drawable.orange_button);
                }
                else {
                    makeToast("not running");
                }
/*
                AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent alarmIntent = new Intent(AllDemosActivity.this, MyBeaconService.class);
                PendingIntent pending = PendingIntent.getService(AllDemosActivity.this,myIntent_ID,alarmIntent,0);

                alarmManager.setRepeating(0,System.currentTimeMillis(),10*1000,pending);
                //set(AlarmManager.RTC_WAKEUP,
                 //       System.currentTimeMillis() + 5 * 1000, pending);

                ((TextView)findViewById(R.id.service_status_text)).setText("Beacon ranging service started");
                Log.i("estimode_Loc_service", "I'm started!");
                */
            }
        });
        findViewById(R.id.stop_service_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent stopIntent = new Intent(AllDemosActivity.this, myForegroundBeaconService.class);
                stopIntent.setAction("stop_beacon_foreground_service");
                startService(stopIntent);

                //if(isServiceRunning(myForegroundServiceName)) {
                    //makeToast("is running");
                //    makeToast("Failed to stop foreground service");
                //}
                //else {
                    makeToast("Background Service Stopped");
                    findViewById(R.id.stop_service_button).setEnabled(false);
                    setButtonBackground(R.id.stop_service_button,R.drawable.grey_button);
                    findViewById(R.id.start_service_button).setEnabled(true);
                    setButtonBackground(R.id.start_service_button,R.drawable.green_button);
                //}
                /*
                Intent myAlarmIntent = new Intent(AllDemosActivity.this, MyBeaconService.class);
                PendingIntent pendingIntent = PendingIntent.getService(AllDemosActivity.this, myIntent_ID, myAlarmIntent, 0);
                AlarmManager myAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                myAlarmManager.cancel(pendingIntent);
                ((TextView)findViewById(R.id.service_status_text)).setText("Beacon ranging service stopped");
                Log.i("estimode_Loc_service", "I'm stopped!");

                */
            }
        });
        findViewById(R.id.clear_db_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getApplicationContext().getContentResolver().delete(BeaconDataModel.beaconEntry.CONTENT_URI, null, null);
                makeToast("iBeacon Localization record cleared");
            }
        });
        findViewById(R.id.debug_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
/*                String[] columns = {
                        BeaconDataModel.beaconEntry._ID,
                        BeaconDataModel.beaconEntry.COLUMN_DATETIME,
                        BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID,
                        BeaconDataModel.beaconEntry.COLUMN_RSSI01,
                        BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID,
                        BeaconDataModel.beaconEntry.COLUMN_RSSI02,
                        BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID,
                        BeaconDataModel.beaconEntry.COLUMN_RSSI03
                };
// A cursor is your primary interface to the query results.
                Cursor cursor = getApplicationContext().getContentResolver().query(
                        BeaconDataModel.beaconEntry.CONTENT_URI, // Uri to Query
                        columns,
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null// sort Arg
                );
// If possible, move to the first row of the query results.
                if (cursor.moveToFirst()) {
// Get the value in each column by finding the appropriate column index.
                    do {
                        int datetimeIndex = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_DATETIME);
                        int id_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry._ID);
                        int Mac01_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID);
                        int rssi01_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_RSSI01);
                        int Mac02_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID);
                        int rssi02_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_RSSI02);
                        int Mac03_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID);
                        int rssi03_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_RSSI03);
                        Log.i("Lboro_estimode_Loc_foreground_service","From database: date_time "+
                                cursor.getString(datetimeIndex)+"; pri_key "+
                                cursor.getInt(id_index)+" MAC01 " +cursor.getString(Mac01_index)+" RSSI01 "+cursor.getInt(rssi01_index)+
                                cursor.getInt(id_index)+" MAC02 " +cursor.getString(Mac02_index)+" RSSI02 "+cursor.getInt(rssi02_index)+
                                cursor.getInt(id_index)+" MAC03 " +cursor.getString(Mac03_index)+" RSSI03 "+cursor.getInt(rssi03_index)
                        );

                    }while(cursor.moveToNext());
                } else {
// That's weird, it works on MY machine...
                    Log.i("Lboro_estimode_Loc_app","No values returned :(");
                }*/

                ExportDatabaseCSVTask myGWT = new ExportDatabaseCSVTask();
                myGWT.execute();

            }
        });
    }


    private void makeToast(String text){
        Toast myToast = Toast.makeText(getApplicationContext(),text,Toast.LENGTH_SHORT);
        myToast.show();
    }


    private boolean isServiceRunning(String serviceName){
        boolean serviceRunning = false;
        ActivityManager am = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningServiceInfo> l = am.getRunningServices(50);
        Iterator<ActivityManager.RunningServiceInfo> i = l.iterator();
        while (i.hasNext()) {
            ActivityManager.RunningServiceInfo runningServiceInfo = (ActivityManager.RunningServiceInfo) i
                    .next();
            //Log.i("LocaliBeacon_debug",runningServiceInfo.service.getClassName());
            if(runningServiceInfo.service.getClassName().equals(serviceName)){
                serviceRunning = true;
            }
        }
        return serviceRunning;
    }


    private void setButtonBackground(int button, int drawable){
        if(sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
            findViewById(button).setBackgroundDrawable(getResources().getDrawable(drawable));
        } else {
            findViewById(button).setBackground(getResources().getDrawable(drawable));
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_all_demos, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent settingIntent = new Intent(getApplicationContext(),SettingsActivity.class);
            startActivity(settingIntent);
            return true;
        }else if (id == R.id.action_help){
            Intent helpIntent = new Intent(getApplicationContext(),HelpActivity.class);
            startActivity(helpIntent);
            return true;
        }else if (id == R.id.action_about){
            Intent aboutIntent = new Intent(getApplicationContext(),AboutActivity.class);
            startActivity(aboutIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    //new async task for file export to csv
    private class ExportDatabaseCSVTask extends AsyncTask<String, String, Boolean> {
        private final ProgressDialog dialog = new ProgressDialog(AllDemosActivity.this);
        private final String TAG = "LocaliBeacon_app";
        private final String FILE_DIR_NM = "LocaliBeacon";
        private final String FILE_AFS = "LocaliBeacon_Export_";
        boolean memoryErr = false;

        // to show Loading dialog box
        @Override
        protected void onPreExecute() {
            this.dialog.setMessage("Exporting database to csv...");
            this.dialog.show();
        }

        // to write process
        protected Boolean doInBackground(final String... args) {

            boolean success = false;

            String currentDateString =  Calendar.getInstance().getTime().toString();

            File dbFile = getDatabasePath(BeaconDBHelper.DB_NAME);
           // Log.i("Lboro_estimode_Loc_foreground_service", "Db path is: " + dbFile); // get the path of db
            //File exportDir = new File(Environment.getExternalStorageDirectory() + File.separator + FILE_DIR_NM, "");
            File exportDir = new File("/sdcard/LocaliBeacon/", "");

            long freeBytesInternal = new File(getApplicationContext().getFilesDir().getAbsoluteFile().toString()).getFreeSpace();
            long megAvailable = freeBytesInternal / 1048576;

            if (megAvailable < 0.1) {
                Log.i("LocaliBeacon_export_service", "Please check" + megAvailable);
                memoryErr = true;
            }else {
                String exportDirStr = exportDir.toString();// to show in dialogbox
                Log.i("LocaliBeacon_export_service", "exportDir path::" + exportDir);
                if (!exportDir.exists()) {
                    exportDir.mkdirs();
                }

                String[] columns = {
                        BeaconDataModel.beaconEntry._ID,
                        BeaconDataModel.beaconEntry.COLUMN_DATETIME,
                        BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID,
                        BeaconDataModel.beaconEntry.COLUMN_RSSI01,
                        BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID,
                        BeaconDataModel.beaconEntry.COLUMN_RSSI02,
                        BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID,
                        BeaconDataModel.beaconEntry.COLUMN_RSSI03
                };
// A cursor is your primary interface to the query results.
                Cursor cursor = getApplicationContext().getContentResolver().query(
                        BeaconDataModel.beaconEntry.CONTENT_URI, // Uri to Query
                        columns,
                        null, // Columns for the "where" clause
                        null, // Values for the "where" clause
                        null// sort Arg
                );

                try {
                    CSVWriter writer = null;
                    String currentDateTime = Calendar.getInstance().getTime().toString();
                    File outFile = new File(exportDir, "LocaliBeacon_Export "+currentDateTime+".csv");
                    outFile.createNewFile();
                    //outFile.createNewFile();
                    writer = new CSVWriter(new FileWriter(outFile), ',');

// If possible, move to the first row of the query results.
                    if (cursor.moveToFirst()) {
// Get the value in each column by finding the appropriate column index.
                        do {
                            int datetimeIndex = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_DATETIME);
                            int id_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry._ID);
                            int Mac01_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_BEACON01_ID);
                            int rssi01_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_RSSI01);
                            int Mac02_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_BEACON02_ID);
                            int rssi02_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_RSSI02);
                            int Mac03_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_BEACON03_ID);
                            int rssi03_index = cursor.getColumnIndex(BeaconDataModel.beaconEntry.COLUMN_RSSI03);
                      /*
                            Log.i("Lboro_estimode_Loc_foreground_service", "From database: date_time " +
                                            cursor.getString(datetimeIndex) + "; pri_key " +
                                            cursor.getInt(id_index) + " MAC01 " + cursor.getString(Mac01_index) + " RSSI01 " + cursor.getInt(rssi01_index) +
                                            " MAC02 " + cursor.getString(Mac02_index) + " RSSI02 " + cursor.getInt(rssi02_index) +
                                            " MAC03 " + cursor.getString(Mac03_index) + " RSSI03 " + cursor.getInt(rssi03_index)
                            );
*/
                            String[] entries = {
                                    cursor.getString(datetimeIndex), Integer.toString(cursor.getInt(id_index)),
                                    cursor.getString(Mac01_index), Integer.toString(cursor.getInt(rssi01_index)),
                                    cursor.getString(Mac02_index), Integer.toString(cursor.getInt(rssi02_index)),
                                    cursor.getString(Mac03_index), Integer.toString(cursor.getInt(rssi03_index))}; // array of your values
                            writer.writeNext(entries);

                        } while (cursor.moveToNext());


                    } else {
// That's weird, it works on MY machine...
                        Log.i("LocaliBeacon_export_service", "No values returned :(");
                    }

                    writer.close();
                }
                catch(IOException e){
                    Log.i("LocaliBeacon_export_service", "IOException :(");
                }
                finally {

                }



            }
            return success;
        }

        // close dialog and give msg
        protected void onPostExecute(Boolean success) {
            makeToast("CSV File exported to /sdcard/LocaliBeacon/");
            if (this.dialog.isShowing()) {
                this.dialog.dismiss();
            }

        }
    }

}


