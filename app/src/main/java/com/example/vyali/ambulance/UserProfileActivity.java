package com.example.vyali.ambulance;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {

    //private GoogleApiClient mGoogleApiClient;
    TextView userProfileName;
    TextView name;
    TextView userPhone;
    TextView contactNum;
    TextView nameWhere,numBedBooked;
    TextView userEmail;
    TextView dob, address;
    ImageButton refresh;
    ProgressBar refreshing;
    Uri imgUri;

    ImageButton userProfilePic;
    ImageView logout, edit;
    FirebaseAuth mAuth;
    FirebaseUser currUser;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference reference;
    private GoogleApiClient muserGoogleApiClient;


    @Override
    protected void onStart() {
        super.onStart();

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        currUser = FirebaseAuth.getInstance().getCurrentUser();

        reference = FirebaseDatabase.getInstance().getReference().child("users").child(currUser.getUid());

        userProfileName = (TextView) findViewById(R.id.user_profile_name);
        name = (TextView) findViewById(R.id.username);
        dob = (TextView) findViewById(R.id.dob);
        address = (TextView) findViewById(R.id.addressid);
        nameWhere = findViewById(R.id.nameWhere);
        numBedBooked = findViewById(R.id.numBedBooked);
        refresh = findViewById(R.id.refresh_button);
        refreshing = findViewById(R.id.refreshing_booking);

        refreshing.setVisibility(View.INVISIBLE);



        userPhone = (TextView) findViewById(R.id.user_profile_phone);
        userEmail = (TextView) findViewById(R.id.user_profile_email);
        userProfilePic = (ImageButton) findViewById(R.id.user_profile_photo);
        contactNum = (TextView) findViewById(R.id.contactnum);
        logout = (ImageView) findViewById(R.id.logout);


        try{
            userEmail.setText(currUser.getEmail());
            userProfileName.setText(currUser.getEmail());
            userPhone.setText(currUser.getPhoneNumber());





        }catch (Exception e){
            Toast.makeText(UserProfileActivity.this,"EXCeption"+e,Toast.LENGTH_SHORT).show();
        }


        //InputStream inputStream = null;
        try {
            //imgUri = currUser.getPhotoUrl();

                imgUri = Uri.parse("android.resource://com.example.vyali.ambulance/drawable/fbp");
                System.out.println("#$%^&*()"+imgUri);

            //inputStream = getContentResolver().openInputStream(imgUri);
            //Drawable myDrawable = Drawable.createFromStream(inputStream, imgUri.toString());
           // userProfilePic.setImageURI(imgUri);
            Picasso.with(UserProfileActivity.this).load(imgUri).placeholder(R.drawable.ic_account_circle_black_24dp).transform(new CircleTransform()).into(userProfilePic);
        } catch (Exception e) {
            e.printStackTrace();
        }
        //imgUri = Uri.parse("android.resource://com.example.vyali.ambulance/drawable/fbp.jpg");
        //Picasso.with(UserProfileActivity.this).load(imgUri).placeholder(R.drawable.fbp).transform(new CircleTransform()).into(userProfilePic);



    logout.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
           signout();
        }
    });


    refresh.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            refresh.setVisibility(View.INVISIBLE);
            getBookingDetail();
        }
    });
       // getBookingDetail();

      /*  if(savedInstanceState !=null){
            Log.d("NOTNULL","not null saved");

        }
        else{Log.d("NOTNULL","null state");}

*/

      loadPreferences();

    }


 public void savePreferences(){
        Log.d("NOTNULL","in save prefrences");
     SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
     SharedPreferences.Editor editor = sharedPreferences.edit();
     editor.putString("nameWhere",nameWhere.getText().toString());
     editor.putString("numBedBooked",numBedBooked.getText().toString());
     editor.commit();
 }

 public void loadPreferences(){
     Log.d("NOTNULL","in load prefrences");
     SharedPreferences sharedPreferences = getPreferences(MODE_PRIVATE);
     nameWhere.setText(sharedPreferences.getString("nameWhere","book pls"));
     numBedBooked.setText(sharedPreferences.getString("numBedBooked","1"));
 }

    @Override
    public void onBackPressed() {

        savePreferences();
        super.onBackPressed();
    }

 /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("NOTNULL","state called asdfasdf");
        outState.putString("nameWhere",nameWhere.getText().toString());
        outState.putString("numBedBooked",numBedBooked.getText().toString());

    }*/

    /*@Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.d("NOTNULL","reupstate called asdfasdf");
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("NOTNULL","restate called asdfasdf");
        nameWhere.setText(savedInstanceState.getString("nameWhere"));
        numBedBooked.setText(savedInstanceState.getString("numBedBooked"));


    }*/

    void signout(){
        FirebaseAuth.getInstance().signOut();
        //FirebaseUser cuser = mAuth.getCurrentUser();



            Intent intent = new Intent(UserProfileActivity.this, LoginActivity.class);
            finishAffinity();
            startActivity(intent);

            /*Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("com.package.ACTION_LOGOUT");
            sendBroadcast(broadcastIntent);*/

    }

    public void getBookingDetail(){
        refreshing.setVisibility(View.VISIBLE);
        //reference.child("users").child(currUser.getUid());
        try {
            reference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        Log.d("###",dataSnapshot.getValue().toString());
                        HashMap<String, Object> m = (HashMap<String, Object>) dataSnapshot.getValue();
                        String w = m.get("where").toString();
                        String d = m.get("date").toString();
                        Log.d("$$$",""+w);
                        nameWhere.setText(w);
                        numBedBooked.setText(d);

                        /*for (Map.Entry<String, Object> k : m.entrySet()) {
                            Map d = (Map) k.getValue();
                            //nameWhere.setText(d.get("where").toString());
                            //String w = (String) d.get("where");
                            //Log.d("value3232",w);

                        }*/
                    }

                    refreshing.setVisibility(View.GONE);
                    refresh.setVisibility(View.VISIBLE);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    Toast.makeText(UserProfileActivity.this,""+databaseError,Toast.LENGTH_SHORT).show();
                    Log.d("#####",databaseError.toString());
                }
            });
        }catch (Exception e){
           Log.d("@@@@@@@","333"+e);
        }
    }
}
class CircleTransform implements Transformation {
    @Override
    public Bitmap transform(Bitmap source) {
        int size = Math.min(source.getWidth(), source.getHeight());

        int x = (source.getWidth() - size);
        int y = (source.getHeight() - size);

        Bitmap squaredBitmap = Bitmap.createBitmap(source, x, y, size, size);
        if (squaredBitmap != source) {
            source.recycle();
        }

        Bitmap bitmap = Bitmap.createBitmap(size, size, source.getConfig());

        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        BitmapShader shader = new BitmapShader(squaredBitmap,
                BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        paint.setShader(shader);
        paint.setAntiAlias(true);

        float r = size / 2f;
        canvas.drawCircle(r, r, r, paint);


        squaredBitmap.recycle();
        return bitmap;
    }

    @Override
    public String key() {
        return "circle";
    }
}

