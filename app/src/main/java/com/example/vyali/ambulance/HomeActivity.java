package com.example.vyali.ambulance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.synnapps.carouselview.CarouselView;
import com.synnapps.carouselview.ImageListener;

public class HomeActivity extends AppCompatActivity {

    CardView ambCard,docCard,bedCard;
    CarouselView carouseView;
    private long backPressedTime = 0;

    int sampleImage[] = {R.drawable.health, R.drawable.health2};

    //protected FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        //mAuth = FirebaseAuth.getInstance();

        ambCard = findViewById(R.id.amb_card);
        docCard = findViewById(R.id.doc_card);
        bedCard =findViewById(R.id.bed_card);


        carouseView = findViewById(R.id.carouselView);
        carouseView.setPageCount(sampleImage.length);
        carouseView.setImageListener(imageListener);




        ambCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetworkAvailable()){
                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                startActivity(intent);
            }
            else{
                    Toast.makeText(HomeActivity.this,"To access Ambulance Service turn your internet connection on",Toast.LENGTH_LONG).show();

                }
            }

        });


        docCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()){
                Intent intent = new Intent(getApplicationContext(),DoctorActivity.class);
                startActivity(intent);
                }
                else{
                    Toast.makeText(HomeActivity.this,"For booking Doctors appointment turn your internet connection on",Toast.LENGTH_LONG).show();
                }
            }
        });

        bedCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isNetworkAvailable()) {
                    Intent intent = new Intent(getApplicationContext(), BedActivity.class);
                    startActivity(intent);
                }else
                {
                    Toast.makeText(HomeActivity.this,"To access Bed Booking Service turn your internet connection on",Toast.LENGTH_LONG).show();
                }

            }
        });
    }

    /*@Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        //updateUI(currentUser);

    }*/

    ImageListener imageListener = new ImageListener() {
        @Override
        public void setImageForPosition(int position, ImageView imageView) {
            imageView.setImageResource(sampleImage[position]);

        }
    };


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_button,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.profile:
                Intent intent = new Intent(this, UserProfileActivity.class);
                startActivity(intent);
                return true;


        }

        return super.onOptionsItemSelected(item);
    }





    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public void onBackPressed() {

       /* new AlertDialog.Builder(this)
                .setTitle("Really Exit?")
                .setMessage("Are you sure you want to exit?")
                .setNegativeButton(android.R.string.no, null)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface arg0, int arg1) {

                        HomeActivity.super.onBackPressed();
                    }
                }).create().show();*/

        long t = System.currentTimeMillis();
        if (t - backPressedTime > 2000) {    // 2 secs
            backPressedTime = t;
            Toast.makeText(this, "Press back again to logout",
                    Toast.LENGTH_SHORT).show();
        } else {    // this guy is serious
            // clean up
            this.finish();
            //super.onBackPressed();       // bye
        }
    }


}
