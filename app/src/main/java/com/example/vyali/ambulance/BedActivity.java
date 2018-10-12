package com.example.vyali.ambulance;

import android.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.spec.ECField;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BedActivity extends AppCompatActivity {
    RecyclerView bedRecyclerView;
    List<HospitalDetails> myhosList;
    BedAdapter myBedAdapters;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRefrence;
    //TextView msg;
    protected ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bed);
        //msg = findViewById(R.id.erbed_message);
        progressBar = findViewById(R.id.pbed_bar);

        bedRecyclerView = findViewById(R.id.bed_recycler);
        myhosList = new ArrayList<>();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRefrence = firebaseDatabase.getReference("hospitals");


        getDataSnapshot();
        //preparehosList("abcd","asdf","fasd","fasd","fas","fsdf");
        /*myBedAdapters = new BedAdapter(BedActivity.this,myhosList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BedActivity.this);
        bedRecyclerView.setLayoutManager(layoutManager);
        bedRecyclerView.setAdapter(myBedAdapters);*/


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.search_menu,menu);
        MenuItem menuItem = menu.findItem(R.id.search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                myBedAdapters.getFilter().filter(query.toString());
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                myBedAdapters.getFilter().filter(newText.toString());
                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);

    }

    void preparehosList(String name, String address , String avlbed, String ttlbed, String yoexp, String rating) {
        BedActivity.HospitalDetails doctorsDetails = new HospitalDetails(name, address,avlbed,ttlbed,yoexp,rating);
        myhosList.add(doctorsDetails);

    }



    void getDataSnapshot(){
        progressBar.setVisibility(View.VISIBLE);
        try {
            mRefrence.addListenerForSingleValueEvent(new ValueEventListener() {

                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot != null){

                        System.out.println("in datasna[show ))(((((()()(((" + dataSnapshot.toString());


                        Log.d("value", "sucess5555");
                        HashMap<String, Object> keylist = (HashMap<String, Object>) dataSnapshot.getValue();
                        System.out.println("hasmap"+ keylist.toString());


                        Log.d("SNAPSHOT", "value is " + keylist);
                        //System.out.println("msg#@#@#@#"+keylist);
                        String hname,haddress,ttlbed,avlbed,rating;

                        for (Map.Entry<String, Object> key : keylist.entrySet()) {
                            System.out.println("entry"+key.toString());

                                Map d  = (Map) key.getValue();
                                hname = (String) d.get("hname");
                                System.out.println("name" + hname);
                                haddress = (String) d.get("haddress");
                                //yoexp = (String) d.get("Experience");
                                 avlbed = d.get("avlbed").toString();
                                 ttlbed =  d.get("ttlbed").toString();
                            //Log.d("LOGTTLBED",  d.get("ttlbed").toString());

                                 rating = d.get("rating").toString();
                                preparehosList(hname, haddress,avlbed,ttlbed,"5",rating);

                   /* else if(type.equals("h")){
                        String name = (String) d.get("username");
                        String address = (String) d.get("hospitaaddress");
                        prepareHosList(name,address);
                    }*/
                    /*Map d = (Map) key.getValue();

                    String name = (String) d.get("username");
                    String special = (String) d.get("specilization");
                    String yoexp = (String) d.get("Experience");
                    String rating = (String) d.get("rating");
                    String degree = (String) d.get("degree");
                    String hadd = (String) d.get("hospitaaddress");
                    String hname = (String) d.get("hospital");*/

                        }
                        progressBar.setVisibility(View.GONE);

                        //mydocAdapter.notifyDataSetChanged();
                       myBedAdapters = new BedAdapter(BedActivity.this,myhosList);
                        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(BedActivity.this);
                        bedRecyclerView.setLayoutManager(layoutManager);
                        bedRecyclerView.setAdapter(myBedAdapters);

                    }else{
                        Log.d("fasfafasdfas","dastasandfasdfasdf");

                        //msg.setText("Unable to access database");
                    }

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    //Toast.makeText(getApplicationContext(), "unable to access database", Toast.LENGTH_SHORT).show();
                    System.out.println("@@@@@cancelled" + databaseError);
                    Log.d("cancel", databaseError.toString());
                    //msg.setVisibility(View.VISIBLE);
                    //msg.setText("Unable to access database");

                }
            });
        }catch (Exception e ){
            e.printStackTrace();
        }


    }


   /* void bookthebed(String date,String where){
        UserBookingDetail userBookingDetail =new UserBookingDetail(date,where);
        Toast.makeText(this,"date"+date,Toast.LENGTH_SHORT).show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mrefrence = FirebaseDatabase.getInstance().getReference();
        if(user!= null){
            try {
                mrefrence.child("users").child(user.getUid()).setValue(userBookingDetail);
            }catch (Exception e){
                Log.d("EXCEp",""+e);
                Toast.makeText(this,"unable to write "+e,Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this,"Log in first",Toast.LENGTH_SHORT).show();
        }

    }
*/
    public static class HospitalDetails {
        String hname,haddress ,avlbed,ttlbed, yoexp,rating;

        public HospitalDetails(String name, String haddress, String avlbed,String ttlbed,String yoexp,String rating) {
            hname = name;
            this.haddress = haddress;
            this.avlbed = avlbed;
            this.ttlbed = ttlbed;
            this.rating = rating;
            this.yoexp = yoexp;

        }

        String getHName() {
            return hname;
        }

        String gethaddress() {
            return haddress;
        }

        String getAvlbed() {
            return avlbed;
        }
        String getTtlbed(){     return ttlbed; }

        String gethrating() {
            return rating;
        }

        String gethexp() {
            return yoexp;
        }




    }
    static class UserBookingDetail{
        String date,where;
        public UserBookingDetail(String date,String where){
            this.date =date;
            this.where =where;
        }

    }
}
