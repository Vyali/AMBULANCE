package com.example.vyali.ambulance;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.Context;
import android.content.IntentFilter;
import android.location.Location;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */




public class UpdatePositionService extends IntentService {

    //List<MainActivity.MyList> mdataset;
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    public static final String ACTION_FOO = "com.example.vyali.ambulance.action.FOO";
    public static final String ACTION_BAZ = "com.example.vyali.ambulance.action.BAZ";

    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "com.example.vyali.ambulance.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "com.example.vyali.ambulance.extra.PARAM2";
    private String HALT = null;
    public UpdatePositionService() {
        super("UpdatePositionService");
    }


    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UpdatePositionService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, UpdatePositionService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        final  int waitinterval = 20;
        if (intent != null) {


            //final MainActivity ma =new MainActivity();
//            ma.refreshLocation();

            /*Runnable r = new Runnable() {
                @Override
                public void run() {
                    try{  MainActivity ma =new MainActivity();
                        ma.collapse();
                    }catch (Exception e){

                    }

                }
            };*/
            final long period = 20000;
            Timer timer = new Timer();

            if(HALT==null) {

                TimerTask timerTask = new TimerTask() {
                    //Location dlocation;
                    //String et;
                    int i = 0;

                    @Override
                    public void run() {

                        System.out.println("re44444" + (i++));


                        Intent lclIntent = new Intent(ACTION_BAZ);
                        lclIntent.putExtra("refresh", "1");
                        lclIntent.putExtra("check", String.valueOf(i));
                        //localIntent.putExtra("t","23");
                        LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(lclIntent);

                    }
                };
                timer.schedule(timerTask, 2, period);

            }else{
                timer.cancel();
                timer.purge();
                
            }
          // final String action = intent.getAction();
            String lat = intent.getStringExtra("vehicle_lat");
            String lang = intent.getStringExtra("vehicle_lang");
            String time = intent.getStringExtra("time");
            System.out.println("time is####### "+   time);
            String distance = intent.getStringExtra("distance");
            final String index = intent.getStringExtra("index");

            //sending result back to the activity main
            Intent localIntent = new Intent(ACTION_FOO);
            localIntent.putExtra("d",distance);
            localIntent.putExtra("t",time);
            localIntent.putExtra("ind",index);
            localIntent.putExtra("getcurrent","1");
            //localIntent.putExtra("refresh",0);
            LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);


            LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    Log.d("TAG","in service reciever");
                        HALT = intent.getStringExtra("HALT");

                }
            },new IntentFilter(MainActivity.ACTION_UPDATE));



//            ma.eta.setText(time);
  //          ma.distance.setText(distance);


        }
    }


    /*@Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {

        final MainActivity ma =new MainActivity();
        ma.dummy();

        final long period = 10000;
        new Timer().schedule(new TimerTask() {
            Location dlocation;
            String et;
            @Override
            public void run() {
                ma.dummy();


                Intent localIntent = new Intent(ACTION_FOO);
                localIntent.putExtra("d","23");
                localIntent.putExtra("t","23");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(localIntent);

            }
        }, 2, period);


        return super.onStartCommand(intent, flags, startId);
    }

*/
    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
