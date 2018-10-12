package com.example.vyali.ambulance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


//import com.ahmadrosid.lib.drawroutemap.DrawRouteMaps;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import android.content.pm.PackageManager;
import android.location.Location;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity implements OnMapReadyCallback {

    int SOURCE_PICKER_REQUESET =1;
    int DESTINATION_PICKER_REQUEST=2;
    int MY_SOCKET_TIMEOUT_MS = 1500;


    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private FusedLocationProviderClient mFusedLocationProviderClient;
    private GoogleMap mMap;
    FirebaseDatabase database;
    DatabaseReference databaseReference;
    Marker sMarker,dMarker;

    private static final String TAG = MainActivity.class.getSimpleName();
    int ind=0;
    int distance_value =20000;

    private final LatLng mDefaultLocation = new LatLng(-33.8523341, 151.2106085);
    private static final int DEFAULT_ZOOM = 15;
    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    public static final String ACTION_UPDATE = "com.example.vyali.ambulance.action.UPDATE";
    public static final int ACTION_SET_TEXT=1;
    public static final int ACTION_NOT_SET_TEXT=0;


    private int SOURCE_FLAG = 1;
    private int DESTINATION_FLAG = 2;
    private boolean mLocationPermissionGranted;
    protected Location mLastKnownLocation;

    EditText source,destination;
    String source_name,destination_name;
    LatLng sourceLatLang, destinationLatLang,snewlatlang;
    Button call,refresh_location;
    TextView distance,eta,upTimeValue,upTimeMsg;
    ProgressBar progressBar;
    RecyclerView mRecyclerView;
    RecyclerView.Adapter ambulanceAdapter;
    RecyclerView.LayoutManager ambLayoutManager;
    SlidingUpPanelLayout slidingUpPanelLayout;


    String mDataset[];
    double pointx[]={26.8476,28.7041,25.3176,25.4358,26.4270};
    double pointy[]={80.9462,77.105,82.9739,81.8463,83.4039};
    List<LatLng> points;
    List<MyList> ambulanceList;
    //List<VehicleDetail> vehicleDetailList;
    List<VehicleDetail2> vehicleDetail2List;
    double vehicleLat,vehicleLang;

  int ref;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        loadPreferences();

        //initializing database variables
        database= FirebaseDatabase.getInstance();
        //databaseReference = database.getReference();


        mDataset = new String[]{"name1", "name2","name3","name4"};
        source = (EditText) findViewById(R.id.source_name);
        destination = findViewById(R.id.destination_name);
        call= findViewById(R.id.call);
        refresh_location = findViewById(R.id.refresh_location);
        distance=findViewById(R.id.distance);
        eta = findViewById(R.id.eta);
        progressBar = findViewById(R.id.progress_bar);
        progressBar.setVisibility(View.INVISIBLE);
        mRecyclerView = findViewById(R.id.nambulance_list);
        mRecyclerView.setHasFixedSize(true);
        slidingUpPanelLayout = findViewById(R.id.slide_panel);
        ambulanceList = new ArrayList<MyList>();
       // vehicleDetailList = new ArrayList<VehicleDetail>();
        vehicleDetail2List = new ArrayList<VehicleDetail2>();
        points = new ArrayList<LatLng>();
        upTimeValue = findViewById(R.id.upTimeValue);
        upTimeMsg = findViewById(R.id.uptimemsg);

        //creating the latlang list

        for(int i = 0; i<pointx.length-1;i++){

            points.add(i,new LatLng(pointx[i],pointy[i]));
        }

        //StatusReceiver statusReceiver = new StatusReceiver();
        //LocalBroadcastManager.getInstance(this).registerReceiver();
        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {
                        distance.setText(intent.getStringExtra("d"));
                        eta.setText(intent.getStringExtra("t"));
                        ind = Integer.parseInt(intent.getStringExtra("ind"));
                        if(intent.getStringExtra("getcurrent")!=null){
                            getSelectedVehicle();
                        }


                    }
                },new IntentFilter(UpdatePositionService.ACTION_FOO)
        );


        LocalBroadcastManager.getInstance(getApplicationContext()).registerReceiver(
                new BroadcastReceiver() {
                    @Override
                    public void onReceive(Context context, Intent intent) {





                        if(intent.getStringExtra("refresh")!=null){
                            refreshLocation();
                        }

                    }
                },new IntentFilter(UpdatePositionService.ACTION_BAZ)
        );





        //layout manager

        ambLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(ambLayoutManager);

       /* //Adapter

        ambulanceAdapter = new AmbulanceAdapter(ambulanceList);
        mRecyclerView.setAdapter(ambulanceAdapter);*/


        // progressBar.setVisibility(View.INVISIBLE);
        // Construct a GeoDataClient.
        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        // Construct a FusedLocationProviderClient.
        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.mapp);
        mapFragment.getMapAsync(this);






       getLocationPermission();
       mLastKnownLocation =  getDeviceLocation();


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //Do something after 100ms
                if(mLastKnownLocation!=null) {
                    System.out.println("last locas!!!!!!!!!!!!!"+mLastKnownLocation.getLongitude());
                    addMarker(new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude()),
                            "Current Location",SOURCE_FLAG);
                }else{System.out.println("@@@@@@@@@empty location value");}
            }

        }, 2000);




       source.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               selectSource();
           }
       });

        destination.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectDestination();
            }
        });





       call.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               System.out.println("calling the ambulance");
           for(VehicleDetail2 vd2 : vehicleDetail2List){
               System.out.println("*(**&*(vd2"+vd2.getVehicleEmail());
           }
             getOrderedDistanceAndTime(snewlatlang,points,vehicleDetail2List);
            // getOrderedDistanceAndTime(snewlatlang,points,vehicleDetail2List);
             System.out.println("after getorderdistanceandtime");


              //getDistance(sourceLatLang,destinationLatLang);

               //new GetDistanceAndTime().execute();
               refresh_location.setVisibility(View.VISIBLE);
               refresh_location.setEnabled(true);
               call.setVisibility(View.INVISIBLE);
               call.setEnabled(false);
           }
       });


       refresh_location.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               refreshLocation();
           }
       });



    //getVehiclesLocation();
       getVehiclesLocation2();




        /*if(savedInstanceState != null){
            ind = savedInstanceState.getInt("index");
            ambulanceList = (List<MyList>) savedInstanceState.getSerializable("ambulaceList");
            Log.d("RESTORED","oncreate wala"+ambulanceList);

        }*/



    }

    protected void savePreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt("index",ind);
        //Set<MyList> set = new HashSet<>();
        //set.addAll(ambulanceList);
        //editor.putStringSet("ambulanceList");
        System.out.println("IN SAVE PREF"+ambulanceList.toString());
      /*  try {
            editor.putString("ambulanceList", ObjectSerializer.serialize((Serializable) ambulanceList));
        } catch (IOException e) {
            e.printStackTrace();
        }*/
        editor.putFloat("vehicleLat", (float) vehicleLat);
        editor.putFloat("vehicleLang", (float) vehicleLang);
        editor.commit();


    }

    protected void loadPreferences(){
        SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
        ind = sharedPreferences.getInt("index",0);
        //Set set = new HashSet<String>();
        //set = sharedPreferences.getStringSet("ambulanceList",null);
        //ambulanceList = (List<MyList>) set;
       /* try {
            ambulanceList = (ArrayList<MyList>) ObjectSerializer.deserialize(sharedPreferences.getString("ambulanceList", ObjectSerializer.serialize(new ArrayList<MyList>())));
            System.out.println("loaded PREF"+ambulanceList);
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       vehicleLang = (double)sharedPreferences.getFloat("vehicleLang",  83);
       vehicleLat = (double) sharedPreferences.getFloat("vehicleLat",74);
       Log.d("vehicleDouble",""+vehicleLang+vehicleLat);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        savePreferences();
    }
    /*@Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {

        outState.putInt("index",ind);
        outState.putSerializable("ambulanceList", (Serializable) ambulanceList);

        super.onSaveInstanceState(outState, outPersistentState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        ind  = savedInstanceState.getInt("index");
        ambulanceList = (List<MyList>) savedInstanceState.getSerializable("ambulaceList");
        Log.d("RESTORED",""+ambulanceList);
        super.onRestoreInstanceState(savedInstanceState);
    }
*/

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == SOURCE_PICKER_REQUESET){
            if(resultCode == RESULT_OK){
                Place place = PlacePicker.getPlace(this,data);
                System.out.println(place.getName());
                source_name = (String) place.getName();
                source.setText(source_name);
                sourceLatLang = place.getLatLng();
                /*String toastMsg = String.format("place: %s", sourceLatLang);
                String lat = String.valueOf(sourceLatLang.latitude);
                String lang = String.valueOf(sourceLatLang.longitude);
                String ltln = lat+","+lang;
                System.out.println("ltln"+ltln);
                System.out.println("source lat lang..."+sourceLatLang)*/;


                //Toast.makeText(this,toastMsg,Toast.LENGTH_LONG);
                addMarker(sourceLatLang,source_name,SOURCE_FLAG);

            }
        }
        if(requestCode == DESTINATION_PICKER_REQUEST){
            if(resultCode == RESULT_OK){
                Place place =  PlacePicker.getPlace(this,data);
                destination_name = (String) place.getName();
                destination.setText(destination_name);
                destinationLatLang = place.getLatLng();
                addMarker(destinationLatLang,destination_name,DESTINATION_FLAG);
//                drawPath();
                System.out.println("destination lat lang..."+destinationLatLang);


            }
        }

    }



/*
     @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.current_place_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.current_location) {
            updateLocationUI();
            Location deviceLocation  = getDeviceLocation();

            if(deviceLocation!=null){
                System.out.println("not null location value");
                System.out.println(deviceLocation.getLongitude());
            }else {
                System.out.println(" null mlocation value");
                //System.out.println(deviceLocation.getLongitude());


            }
            Toast.makeText(this, "current location", Toast.LENGTH_SHORT);
        }
        if(item.getItemId()==R.id.picker){
            PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
            try {
                startActivityForResult(builder.build(this),SOURCE_PICKER_REQUESET);
            } catch (GooglePlayServicesRepairableException e) {
                e.printStackTrace();
            } catch (GooglePlayServicesNotAvailableException e) {
                e.printStackTrace();
            }

        }






        return true;
    }
    */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
         //LatLng pos = new LatLng(-34, 151);
        //  mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
/*
        LatLng pos = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());
        mMap.addMarker(new MarkerOptions().position(pos).title("current position"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(pos));
*/

      /*  mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            // Return null here, so that getInfoContents() is called next.
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                // Inflate the layouts for the info window, title and snippet.
                View infoWindow = getLayoutInflater().inflate(R.layout.custom_info_contents,
                        (FrameLayout) findViewById(R.id.map), false);

                TextView title = ((TextView) infoWindow.findViewById(R.id.title));
                title.setText(marker.getTitle());

                TextView snippet = ((TextView) infoWindow.findViewById(R.id.snippet));
                snippet.setText(marker.getSnippet());

                return infoWindow;
            }

        });
*/
        getLocationPermission();
        updateLocationUI();

    }




    private void getLocationPermission() {
    /*
     * Request location permission, so that we can get the location of the
     * device. The result of the permission request is handled by a callback,
     * onRequestPermissionsResult.
     */
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[],
                                           @NonNull int[] grantResults) {
        mLocationPermissionGranted = false;
        switch (requestCode) {
            case PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    mLocationPermissionGranted = true;
                }
            }
        }
        updateLocationUI();
    }


    private void updateLocationUI() {
        if (mMap == null) {
            return;
        }
        try {
            if (mLocationPermissionGranted) {
                mMap.setMyLocationEnabled(true);
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
            } else {
                mMap.setMyLocationEnabled(false);
                mMap.getUiSettings().setMyLocationButtonEnabled(false);
               // mLastKnownLocation = null;
                getLocationPermission();
            }
        } catch (SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
    }


    protected Location getDeviceLocation() {
        System.out.println(" getting device location ");


    /*
     * Get the best and most recent location of the device, which may be null in rare
     * cases when a location is not available.
     */
        try {
            if (mLocationPermissionGranted) {
                Task locationResult = mFusedLocationProviderClient.getLastLocation();
                locationResult.addOnCompleteListener(this, new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            // Set the map's camera position to the current location of the device.
                            mLastKnownLocation = (Location) task.getResult();
                            if(mLastKnownLocation!=null){
                                System.out.println("1111not null location value");
                                System.out.println(mLastKnownLocation.getLongitude());

                                snewlatlang = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation.getLongitude());

                            }else {
                                System.out.println(" 1111null mlocation value");
                            }
                            LatLng p = new LatLng(mLastKnownLocation.getLatitude(),mLastKnownLocation
                                    .getLongitude());
                          // addMarker(p);


                        } else {
                            System.out.println("current locationis nulll");
                            Log.d(TAG, "Current location is null. Using defaults.");
                            Log.e(TAG, "Exception: %s", task.getException());
                            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mDefaultLocation, DEFAULT_ZOOM));
                            mMap.getUiSettings().setMyLocationButtonEnabled(false);
                        }
                    }
                });
            }
            else{
                getLocationPermission();
            }
        } catch(SecurityException e)  {
            Log.e("Exception: %s", e.getMessage());
        }
        return mLastKnownLocation;
    }
 protected void selectSource(){
     PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
     try {
         startActivityForResult(builder.build(this),SOURCE_PICKER_REQUESET);
     } catch (GooglePlayServicesRepairableException e) {
         e.printStackTrace();
     } catch (GooglePlayServicesNotAvailableException e) {
         e.printStackTrace();
     }
 }
protected void selectDestination(){
    PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
    try {
        startActivityForResult(builder.build(this),DESTINATION_PICKER_REQUEST);
    } catch (GooglePlayServicesRepairableException e) {
        e.printStackTrace();
    } catch (GooglePlayServicesNotAvailableException e) {
        e.printStackTrace();
    }

    /*final Handler handler = new Handler();
    handler.postDelayed(new Runnable() {
        @Override
        public void run() {
            //Do something after
            DrawRouteMaps.getInstance(getApplicationContext())
                    .draw(snewlatlang, destinationLatLang, mMap);
        }
    }, 1000);*/
}



protected void getDistance(LatLng source, LatLng destination, final int flag){
    progressBar.setVisibility(View.VISIBLE);
    if((source!=null) && (destination!=null) && (source.toString().equals(destination.toString()))){
        System.out.println("select different destination");
    }
    else if(source == null && (destination!=null)){
        System.out.println("source is null");
    }
    else if ((source!=null)&& (destination==null)){
        System.out.println("destination is null");
    }
    else if((source==null) && (destination == null)){
        System.out.println("source and destination are null");
    }

    else if((source!=null) && (destination!=null)) {
        String dltlng = destination.latitude+"%2C"+destination.longitude;
        String sltlng = source.latitude+","+source.longitude;
        String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+sltlng+"&destinations="+dltlng+"&key=AIzaSyAtCsHgfQpJ8kxho7oWtr9SeRN9kwdVKQE ";

        // String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=AIzaSyAtCsHgfQpJ8kxho7oWtr9SeRN9kwdVKQE ";
        //JSONObject jsonObject = new JSONObject();
        final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                System.out.println("response is**#$%^ee^r** ");
                System.out.println(response.toString());
                progressBar.setVisibility(View.INVISIBLE);
                try {
                    JSONObject elements = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);

                    String distance_text = elements.getJSONObject("distance").getString("text");
                    String time_text = elements.getJSONObject("duration").getString("text");
                    distance_value = elements.getJSONObject("distance").getInt("value");

                    if(flag == ACTION_SET_TEXT){
                    distance.setText(distance_text);
                    eta.setText(time_text);
                    }

                    System.out.println("distance is #$#$#$"+distance);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.INVISIBLE);
                System.out.println("error****");
                System.out.println(error);

                // As of f605da3 the following should work
                NetworkResponse response = error.networkResponse;
                if (error instanceof ServerError && response != null) {
                    try {
                        String res = new String(response.data,
                                HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                        // Now you can use any deserializer to make sense of data
                        JSONObject obj = new JSONObject(res);
                        System.out.println("json obj on er@@ror" + obj.toString());
                    } catch (UnsupportedEncodingException e1) {
                        // Couldn't properly decode data to string
                        e1.printStackTrace();
                    } catch (JSONException e2) {
                        // returned data is not JSONObject?
                        e2.printStackTrace();
                    }
                }


            }
        });

        jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


        MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}


    protected void getOrderedDistanceAndTime(LatLng source,List<LatLng> point,List<VehicleDetail2> vl){
        progressBar.setVisibility(View.VISIBLE);
        ambulanceList.clear();

        if(source==null){
            Toast.makeText(this,"Unable to get location choose Source ",Toast.LENGTH_LONG).show();
            return;
        }


         if((source!=null) && (vl!=null)) {


             // System.out.println("##########vl" + vl.toString());
             //String dltlng = destination.latitude+"%2C"+destination.longitude;
             String sltlng = source.latitude + "," + source.longitude;
             //  String dltlng = point.get(0).longitude+"%2C"+point.get(0).latitude;

             String dltlng = vehicleDetail2List.get(0).getVehicleLat() + "%2C" + vehicleDetail2List.get(0).getVehicleLang();
             // String dltlng = vl.get(0).getVehicleLatLang().latitude + "%2c" + vl.get(0).getVehicleLatLang().longitude;
             for (int i = 1; i < vehicleDetail2List.size(); i++) {
                 dltlng = dltlng + "%7C" + vehicleDetail2List.get(i).getVehicleLat() + "%2C" + vehicleDetail2List.get(i).getVehicleLang();
                 //dltlng = dltlng + "%7C" + vl.get(i).getVehicleLatLang().latitude + "%2C" + vl.get(i).getVehicleLatLang().longitude;
             }
             System.out.println("sadfasdfasd#######" + dltlng);
             // String url="https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=YOUR_API_KEY";
             String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=" + sltlng + "&destinations=" + dltlng + "&key=AIzaSyAtCsHgfQpJ8kxho7oWtr9SeRN9kwdVKQE ";

             //String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=AIzaSyAtCsHgfQpJ8kxho7oWtr9SeRN9kwdVKQE ";
             //JSONObject jsonObject = new JSONObject();
           try{
             final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject response) {
                     System.out.println("response is**** ");
                     System.out.println(response.toString());
                     progressBar.setVisibility(View.INVISIBLE);
                     try {
                         JSONArray elements = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements");
                         //String distance_text = elements.getJSONObject("distance").getString("text");
                         //System.out.println("ds^^^^"+distance_text);
                         //JSONObject el = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(1);
                         String distanceText, etaText;
                         int dvalue, tvalue;
                         for (int i = 0; i < elements.length(); i++) {
                             JSONObject jsonObject = elements.getJSONObject(i);
                             distanceText = jsonObject.getJSONObject("distance").getString("text");
                             dvalue = Integer.parseInt(jsonObject.getJSONObject("distance").getString("value"));

                             //System.out.println("**(((())"+distanceText);
                             etaText = jsonObject.getJSONObject("duration").getString("text");
                             tvalue = Integer.parseInt(jsonObject.getJSONObject("duration").getString("value"));

                             // System.out.println("eta %%***))()()("+etaText);
                             // ambulanceList.add(new MyList("name",distanceText,etaText,dvalue,tvalue,vehicleDetailList));
                             ambulanceList.add(new MyList("name", distanceText, etaText, dvalue, tvalue, vehicleDetail2List));

                         }

                         //Adapter

                         //sorting list

                         Collections.sort(ambulanceList, new Comparator<MyList>() {
                             @Override
                             public int compare(MyList o1, MyList o2) {
                                 return Integer.valueOf(o1.getTval()).compareTo(o2.getTval());
                             }
                         });


                         ambulanceAdapter = new AmbulanceAdapter(ambulanceList, getApplicationContext());
                         System.out.println("$%$%$zlist" + ambulanceList.toString());
                         mRecyclerView.setAdapter(ambulanceAdapter);

                         // distance.setText(distance_text);
                         //eta.setText(time_text);
                         // System.out.println("distance is #$#$#$"+distance);
                     } catch (JSONException e) {
                         e.printStackTrace();
                     }

                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                     progressBar.setVisibility(View.INVISIBLE);
                     System.out.println("error****");
                     System.out.println(error);
                     Toast.makeText(MainActivity.this, error.toString(), Toast.LENGTH_SHORT).show();


                     // As of f605da3 the following should work
                     NetworkResponse response = error.networkResponse;
                     if (error instanceof ServerError && response != null) {
                         try {
                             String res = new String(response.data,
                                     HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                             // Now you can use any deserializer to make sense of data
                             JSONObject obj = new JSONObject(res);
                             System.out.println("json obj on er@@ror" + obj.toString());
                         } catch (UnsupportedEncodingException e1) {
                             // Couldn't properly decode data to string
                             e1.printStackTrace();
                         } catch (JSONException e2) {
                             // returned data is not JSONObject?
                             e2.printStackTrace();
                         }
                     }


                 }
             });

               jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


               MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
         }catch (Exception e){

               e.printStackTrace();


           }


             }


    }

void refreshLocation(){
    Date d = new Date();
    CharSequence s  = DateFormat.format("HH:mm:ss", d.getTime());
    upTimeValue.setText(s.toString());
    System.out.println("#$%^&*()(*&^%$%^&*()(*&^%"+s);
        System.out.println("DUMMMY21342341341234123");
    Location dlocation =getDeviceLocation();
    LatLng dlatlang;
    getVehiclesLocation2();
    if(dlocation!=null){
        snewlatlang = new LatLng(dlocation.getLatitude(),dlocation.getLongitude());

       /* if(ambulanceList!=null){
            //Log.d("vehicle"," "+ambulanceList.size());
            if(!ambulanceList.isEmpty()) {
                vehicleLat = Double.parseDouble(ambulanceList.get(ind).getVehicleDetail().get(ind).getVehicleLat());
                vehicleLang = Double.parseDouble(ambulanceList.get(ind).getVehicleDetail().get(ind).getVehicleLang());
            }
        }*/

        dlatlang = new LatLng(vehicleLat,vehicleLang);
        getDistance(snewlatlang,dlatlang,ACTION_SET_TEXT);
        if(vehicleDetail2List!=null){
            //getOrderedDistanceAndTime(snewlatlang,points,vehicleDetail2List);
            //getDistance(snewlatlang,dlatlang,ACTION_SET_TEXT);
            Log.d("DISTANCEVALUE","distance1000s jyada"+String.valueOf(distance_value));
            if(distance_value < 1000){
                upTimeMsg.setText("It is here");
                Log.d("DISTANCEVALUE","distance_value"+String.valueOf(distance_value));
                Intent lclIntent = new Intent(ACTION_UPDATE);
                lclIntent.putExtra("halt","1");

                //localIntent.putExtra("t","23");
                LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(lclIntent);
            }
           // distance.setText(ambulanceList.get(ind).getDistance().toString());
            //eta.setText(ambulanceList.get(ind).geteTime().toString());
        }
    }



}

    void getSelectedVehicle(){

        vehicleLat = Double.parseDouble(ambulanceList.get(ind).getVehicleDetail().get(ind).getVehicleLat());
        vehicleLang = Double.parseDouble(ambulanceList.get(ind).getVehicleDetail().get(ind).getVehicleLang());


    }
/*

   void getVehiclesLocation(){
        // Read from the database
        //databaseReference.child("vehicle");
       databaseReference = database.getReference().child("vehicle");

        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.
                    String id;
                    double lat,lang;
                    //creating hashmap
                   HashMap<String,Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for(Map.Entry<String,Object> key : dataMap.entrySet()){
                        //String d = key.toString();
                        id = key.getKey();
                        Map d = (Map) key.getValue();
                        //System.out.println(d);
                        lat = (double) d.get("lat");
                        lang= (double) d.get("lang");
                        vehicleDetailList.add(new VehicleDetail(id,lat,lang));





                    }



//                    String value = dataSnapshot.getValue(String.class);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }catch (Exception e){
            System.out.println("exception occurs"+e);

        }
    }
*/


    void getVehiclesLocation2(){
        // Read from the database
        //databaseReference.child("vehicle");
        databaseReference = database.getReference().child("vehicles");

        try {
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    // This method is called once with the initial value and again
                    // whenever data at this location is updated.

                   try{
                    String name,email;
                    double lat,lang;
                    System.out.println("#^%&*(*&^%&^%&^%&^%^%80789986987"+dataSnapshot);
                    //creating hashmap
                    HashMap<String,Object> dataMap = (HashMap<String, Object>) dataSnapshot.getValue();
                    for(Map.Entry<String,Object> key : dataMap.entrySet()) {
                        //String d = key.toString();
                      //  id = key.getKey();

                        Map d = (Map) key.getValue();
                        System.out.println(d);
                        name = d.get("name").toString();
                        email = d.get("email").toString();
                        lat = (double) d.get("lat");
                        lang = (double) d.get("lang");


                        vehicleDetail2List.add(new VehicleDetail2(name, email,lat,lang));
                        //System.out.println("@#$%^&*(*&^%$#"+vehicleDetail2List.get(1).getVehicleEmail());

                    }

                    Log.d("MYTAG",vehicleDetail2List.size()+"");

                    }catch (Exception e){
                       e.printStackTrace();
                   }



//                    String value = dataSnapshot.getValue(String.class);

                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Failed to read value
                    Log.w(TAG, "Failed to read value.", error.toException());
                }
            });
        }catch (Exception e){
            System.out.println("exception occurs"+e);

        }
    }


/*private class GetDistanceAndTime extends AsyncTask<URL, Integer, Boolean> {

    Handler mhandler;
    //progressBar.setVisibility(View.INVISIBLE);
    @Override
    protected Boolean doInBackground(URL... urls) {
        mhandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                progressBar = findViewById(R.id.progress_bar);
                progressBar.setVisibility(View.VISIBLE);
            }
        };



        getDistance(sourceLatLang,destinationLatLang);

        return true;
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        progressBar.setVisibility(View.INVISIBLE);
    }



    protected void getDistance(LatLng source,LatLng destination){

        if((source!=null) && (destination!=null) && (source.toString().equals(destination.toString()))){
            System.out.println("select different destination");
        }
        else if(source == null && (destination!=null)){
            System.out.println("source is null");
        }
        else if ((source!=null)&& (destination==null)){
            System.out.println("destination is null");
        }
        else if((source==null) && (destination == null)){
            System.out.println("source and destination are null");
        }

        else if((source!=null) && (destination!=null)) {
            String dltlng = destination.latitude+"%2C"+destination.longitude;
            String sltlng = source.latitude+","+source.longitude;
            String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins="+sltlng+"&destinations="+dltlng+"&key=AIzaSyAtCsHgfQpJ8kxho7oWtr9SeRN9kwdVKQE ";

            // String url = "https://maps.googleapis.com/maps/api/distancematrix/json?units=imperial&origins=40.6655101,-73.89188969999998&destinations=40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.6905615%2C-73.9976592%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626%7C40.659569%2C-73.933783%7C40.729029%2C-73.851524%7C40.6860072%2C-73.6334271%7C40.598566%2C-73.7527626&key=AIzaSyAtCsHgfQpJ8kxho7oWtr9SeRN9kwdVKQE ";
            //JSONObject jsonObject = new JSONObject();
            final JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    System.out.println("response is**** ");
                    System.out.println(response.toString());
                    try {
                        JSONObject elements = response.getJSONArray("rows").getJSONObject(0).getJSONArray("elements").getJSONObject(0);

                        String distance_text = elements.getJSONObject("distance").getString("text");
                        String time_text = elements.getJSONObject("duration").getString("text");
                        distance.setText(distance_text);
                        eta.setText(time_text);
                        System.out.println("distance is #$#$#$"+distance);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    System.out.println("error****");
                    System.out.println(error);

                    // As of f605da3 the following should work
                    NetworkResponse response = error.networkResponse;
                    if (error instanceof ServerError && response != null) {
                        try {
                            String res = new String(response.data,
                                    HttpHeaderParser.parseCharset(response.headers, "utf-8"));
                            // Now you can use any deserializer to make sense of data
                            JSONObject obj = new JSONObject(res);
                            System.out.println("json obj on er@@ror" + obj.toString());
                        } catch (UnsupportedEncodingException e1) {
                            // Couldn't properly decode data to string
                            e1.printStackTrace();
                        } catch (JSONException e2) {
                            // returned data is not JSONObject?
                            e2.printStackTrace();
                        }
                    }


                }
            });

            jsonObjectRequest.setRetryPolicy(new DefaultRetryPolicy(MY_SOCKET_TIMEOUT_MS, DefaultRetryPolicy.DEFAULT_MAX_RETRIES, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));


            MySingleton.getInstance(MainActivity.this).addToRequestQueue(jsonObjectRequest);
        }
    }





}*/


public void addMarker(LatLng p,String t,int f){
   // LatLng p = new LatLng(postion.getLatitude(),postion.getLongitude());
   if(f == SOURCE_FLAG){
       if(sMarker != null) {
           sMarker.remove();
           sMarker = mMap.addMarker(new MarkerOptions().position(p).title(t));
           sMarker.showInfoWindow();
           //mMap.addMarker(new MarkerOptions().position(p).title(t)).showInfoWindow();
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, DEFAULT_ZOOM));
       }
       else{
           sMarker = mMap.addMarker(new MarkerOptions().position(p).title(t));
           sMarker.showInfoWindow();
           //mMap.addMarker(new MarkerOptions().position(p).title(t)).showInfoWindow();
           mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, DEFAULT_ZOOM));
       }
   }
   else if(f == DESTINATION_FLAG){
       dMarker = mMap.addMarker(new MarkerOptions().position(p).title(t));
       dMarker.showInfoWindow();
       //mMap.addMarker(new MarkerOptions().position(p).title(t)).showInfoWindow();

       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(p, DEFAULT_ZOOM));
   }

}

public void drawPath(){
    Polyline polyline = mMap.addPolyline(new PolylineOptions().clickable(true)
            .add(snewlatlang,destinationLatLang));
}

public void collapse(){

    if (slidingUpPanelLayout.getPanelState() != SlidingUpPanelLayout.PanelState.HIDDEN) {
        slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.HIDDEN);
       // item.setTitle(R.string.action_show);
    }
    //slidingUpPanelLayout.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
}

public class MyList {
    String pname,etime,distance;
    int dval,tval;
   // List<VehicleDetail> vDetail;
    List<VehicleDetail2> vDetail2;

    public MyList(String n, String d, String t, int dval,int tval/*List<VehicleDetail> vd*/,List<VehicleDetail2> vd) {
        pname= n;
        etime= t;
        distance= d;
        this.dval = dval;
        this.tval = tval;
        //vDetail = vd;
        vDetail2 = vd;

    }

    public String getPname(){
        return pname;
            }

     public String geteTime(){return etime;}
     public String getDistance(){return distance;}
     public int getDval(){return dval;}
     public int getTval(){return tval;}
     public List<VehicleDetail2> getVehicleDetail(){return vDetail2;}
}


/*
public class VehicleDetail{
    String id,lat,lang;
    public VehicleDetail(String id,double lat,double lang){
        this.id=id;
        this.lang= String.valueOf(lang);
        this.lat= String.valueOf(lat);

    }

    public String getVehicleId(){return id;}
    public String getLat(){return lat;}
    public String getLang(){return lang;}
}*/
public class VehicleDetail2 implements Serializable{
    String email,name,lat,lang;
   // LatLng latLng;


    VehicleDetail2(String name ,String email,double lat,double lang){
        this.name= name;
        this.email= email;
       // this.latLng= latLng;
        this.lat= String.valueOf(lat);
        this.lang = String.valueOf(lang);

    }
    // TODO : add getter methods and do this for new vehicle location database 0
    public String getVehicleName(){return name;}
    public String getVehicleEmail(){return email;}
    public String getVehicleLat(){return lat;}
    public String getVehicleLang(){return lang;}
    //public LatLng getVehicleLatLang(){return latLng;}
}

static class AmbUserBookingDetail{
    boolean f;
    //LatLng userlatlng;
    String email;
    double lat,lang;
    AmbUserBookingDetail(boolean f,String email,double lat, double lang){
       this.f= f;
       //this.userlatlng = userlatLng;
        this.lat = lat;
        this.lang =lang;
        this.email = email;

    }
}

}

