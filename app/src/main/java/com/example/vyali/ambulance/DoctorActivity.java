package com.example.vyali.ambulance;

import android.os.AsyncTask;
import android.support.v4.view.MenuItemCompat;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

import static android.widget.Toast.*;

public class DoctorActivity extends AppCompatActivity {
    //FirebaseUser mUser;
    //FirebaseUser mUser;
   // FirebaseAuth mAuth;

    List<DoctorsDetails> mDoctorList;
    RecyclerView docRecyclerView;
    List<DoctorsDetails> mydocList;
    DoctorAdapter mydocAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
   // private DatabaseReference mRefrence;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference mRefrence;
    TextView msg;
    protected ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor);

         msg = findViewById(R.id.er_message);
         progressBar = findViewById(R.id.p_bar);
        progressBar.setVisibility(View.VISIBLE);
        //mAuth = FirebaseAuth.getInstance();
        //mUser = mAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mRefrence = firebaseDatabase.getReference("doctors");
        mDoctorList = new ArrayList<>();

        docRecyclerView = (RecyclerView) findViewById(R.id.doc_recycler);
        mydocList = new ArrayList<>();



        getDataSnapshot();
/*
        MyTask myTask = new MyTask();
        myTask.execute();*/
        /*mydocAdapter = new DoctorAdapter(DoctorActivity.this,mydocList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DoctorActivity.this);
        docRecyclerView.setLayoutManager(layoutManager);
        docRecyclerView.setAdapter(mydocAdapter);
*/


    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataSnapshot();
        mydocAdapter = new DoctorAdapter(DoctorActivity.this,mydocList);
        docRecyclerView.setAdapter(mydocAdapter);
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
                Toast.makeText(DoctorActivity.this,"seasdfaasd"+query, LENGTH_LONG).show();
                System.out.println("34234@$#$#"+query);
                //mydocAdapter = new DoctorAdapter(DoctorActivity.this,mydocList);
                //RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DoctorActivity.this);
                //docRecyclerView.setLayoutManager(layoutManager);
                //docRecyclerView.setAdapter(mydocAdapter);
                mydocAdapter.getFilter().filter(query.toString());
                return true;
            }


            @Override
            public boolean onQueryTextChange(String newText) {
                System.out.println("34234@$#$#"+newText);
                mydocAdapter.getFilter().filter(newText.toString()
                );

                return true;
            }
        });
        return super.onCreateOptionsMenu(menu);
    }

    void prepareDocList(String name, String special , String yoexp, String degree, String hadd, String hname) {
        DoctorsDetails doctorsDetails = new DoctorsDetails(name, special,yoexp,degree,hadd,hname);
        mydocList.add(doctorsDetails);

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
                for (Map.Entry<String, Object> key : keylist.entrySet()) {
                    System.out.println("entry"+key.toString());

                    Map d  = (Map) key.getValue();
                    //String type = (String) d.get("type");

                        String name = (String) d.get("username");
                        System.out.println("name" + name);
                        String special = (String) d.get("specilization");
                        //String yoexp = (String) d.get("Experience");
                        String degree = (String) d.get("degree");
                        String hadd = (String) d.get("hospitaaddress");
                        String hname = (String) d.get("hospital");
                        prepareDocList(name, special,"5",degree,hadd,hname);

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
                mydocAdapter = new DoctorAdapter(DoctorActivity.this,mydocList);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(DoctorActivity.this);
                docRecyclerView.setLayoutManager(layoutManager);
                docRecyclerView.setAdapter(mydocAdapter);

            }else{
                Log.d("fasfafasdfas","dastasandfasdfasdf");

                msg.setText("Unable to access database");
            }

        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            //Toast.makeText(getApplicationContext(), "unable to access database", Toast.LENGTH_SHORT).show();
            System.out.println("@@@@@cancelled" + databaseError);
            Log.d("cancel", databaseError.toString());
            msg.setVisibility(View.VISIBLE);
            msg.setText("Unable to access database");

        }
    });
}catch (Exception e ){
    e.printStackTrace();
}


}


    public static class DoctorsDetails {
        String dname, degree, haddress, hname, rating, yoexp, special;
        String hrating;

        public DoctorsDetails(String name, String special,String yoexp,String degree,String haddress,String hname) {
            dname = name;
            this.degree = degree;
            this.hname = hname;
            this.haddress = haddress;
            //this.rating = rating;
            this.yoexp = yoexp;
            this.special = special;
        }
       /* public DoctorsDetails(String hname, String haddress)
        {
            this.dname=hname;
            this.degree = haddress;
        }*/


        String getDocName() {
            return dname;
        }

        String getDocdegree() {
            return degree;
        }

        String getDochaddress() {
            return haddress;
        }

        String getDochname() {
            return hname;
        }

        String getDocrating() {
            return rating;
        }

        String getDocyoexp() {
            return yoexp;
        }

        String getDocSepcial() {
            return special;
        }


    }


    /*private class MyTask extends AsyncTask<Void , Void, Void>{
        @Override
        protected void onPreExecute() {
            progressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            getDataSnapshot();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            progressBar.setVisibility(View.GONE);
        }
    }*/
}
