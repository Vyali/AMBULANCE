package com.example.vyali.ambulance;

import android.app.DialogFragment;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyali on 14/4/18.
 */

public class BedAdapter extends RecyclerView.Adapter<BedAdapter.HospitalViewHolder> {
    Context context;
    List<BedActivity.HospitalDetails> horglist;
    List<BedActivity.HospitalDetails> hospitalDetailsList;

    public BedAdapter(Context context, List<BedActivity.HospitalDetails> hlist){
        this.context = context;
        horglist = hlist;
        hospitalDetailsList = hlist;


    }
    @Override
    public HospitalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hospital_card,parent,false);
        return new HospitalViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final HospitalViewHolder holder, int position) {
        /*ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(context,
                R.array.bed_type, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        holder.spinner.setAdapter(adapter);
*/
        final BedActivity.HospitalDetails hospitalDetails = hospitalDetailsList.get(position);
        holder.hname.setText(hospitalDetails.getHName());
        holder.haddress.setText(hospitalDetails.gethaddress());

        holder.ttlbed.setText(hospitalDetails.getTtlbed());
        //holder.rating.setText(doctorsDetails.getDocrating());
        holder.avlbed.setText(hospitalDetails.getAvlbed());
        holder.bookbed.setEnabled(false);
        holder.bookbed.setBackgroundColor(context.getResources().getColor(R.color.disable));
        holder.bookbed.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(context, "Your Appointment request for " + hospitalDetails.getHName() + " has been made", Toast.LENGTH_SHORT).show();
                //((BedActivity)context).bookthebed(holder.bookingDate.getText().toString(),hospitalDetails.getHName().toString());
                bookthebed(holder.bookingDate.getText().toString(),hospitalDetails.getHName().toString());
            }
        });
/*        holder.spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    holder.bedcost.setVisibility(View.VISIBLE);
                    if(position==1){
                        holder.bedcost.setText("400 Rs");
                    }
                    if(position==2){
                        holder.bedcost.setText("500 Rs");
                    }
                    if(position==3){holder.bedcost.setText("600 Rs");}

                    Toast.makeText(context, "itemselse" + position, Toast.LENGTH_SHORT).show();

                }

            }


            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                Toast.makeText(context,"select something",Toast.LENGTH_SHORT).show();
            }
        });*/

        holder.bookingDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //
                // Toast.makeText(context,"date picker",Toast.LENGTH_SHORT).show();
                //BedActivity bedActivity = new BedActivity();
                //bedActivity.showDatePicker();
                android.support.v4.app.DialogFragment dialogFragment = new DatePickerFrament();
                Bundle bundle = new Bundle();
                bundle.putSerializable("holder", holder);
                dialogFragment.setArguments(bundle);
                dialogFragment.show(((BedActivity)context).getSupportFragmentManager(),"date");
                //holder.bookingDate.setText();



            }
        });


    }



    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                hospitalDetailsList = (List<BedActivity.HospitalDetails>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<BedActivity.HospitalDetails> FilterdList = new ArrayList<BedActivity.HospitalDetails>();
                if (horglist == null) {
                    horglist = new ArrayList<BedActivity.HospitalDetails>(hospitalDetailsList);

                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    filterResults.count = horglist.size();
                    filterResults.values = horglist;
                }else{
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < horglist.size(); i++) {
                        String data = horglist.get(i).hname;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilterdList.add(new BedActivity.HospitalDetails(horglist.get(i).hname,
                                    horglist.get(i).haddress,horglist.get(i).avlbed,horglist.get(i).ttlbed
                            , horglist.get(i).yoexp,horglist.get(i).rating));


                        }
                    }
                    // set the Filtered result to return
                    filterResults.count =FilterdList.size();
                    filterResults.values =FilterdList;
                }


                return filterResults;
            }



        };
        return filter;
    }







    @Override
    public int getItemCount() {
        return hospitalDetailsList.size();
    }

    public class HospitalViewHolder extends RecyclerView.ViewHolder implements Serializable {
        TextView hname, haddress,avlbed,ttlbed ,yearOfexp,rating,bedcost,bookingDate;
        Button bookbed;
        //Spinner spinner;

        public HospitalViewHolder(View itemView) {
            super(itemView);


            hname = (TextView) itemView.findViewById(R.id.hname);
            haddress = (TextView) itemView.findViewById(R.id.address);
            avlbed = (TextView) itemView.findViewById(R.id.available_bed);
            ttlbed = (TextView) itemView.findViewById(R.id.total_bed);
            rating = (TextView) itemView.findViewById(R.id.hrating);
            yearOfexp = (TextView) itemView.findViewById(R.id.since);
            bookbed = (Button) itemView.findViewById(R.id.book_bed);
           // spinner =itemView.findViewById(R.id.bedtype);
            bedcost =itemView.findViewById(R.id.bedcost);
            bookingDate = itemView.findViewById(R.id.booking_date);

        }

    }

    void bookthebed(String date,String where){
        BedActivity.UserBookingDetail userBookingDetail =new BedActivity.UserBookingDetail(date,where);
        Toast.makeText(context,"date"+date,Toast.LENGTH_SHORT).show();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mrefrence = FirebaseDatabase.getInstance().getReference();
        if(user!= null){
            try {
                mrefrence.child("users").child(user.getUid()).setValue(userBookingDetail);
            }catch (Exception e){
                Log.d("EXCEp",""+e);
                Toast.makeText(context,"unable to write "+e,Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(context,"Log in first",Toast.LENGTH_SHORT).show();
        }

    }

}
