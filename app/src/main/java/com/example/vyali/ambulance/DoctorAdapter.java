package com.example.vyali.ambulance;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vyali on 12/4/18.
 */

public class DoctorAdapter extends RecyclerView.Adapter<DoctorAdapter.DoctorsViewHolder> {
    List<DoctorActivity.DoctorsDetails> mydocList;
    List<DoctorActivity.DoctorsDetails> mydocorgList;
    Context mycontext;

    DoctorAdapter(Context mycontext, List<DoctorActivity.DoctorsDetails> mydocList) {
        this.mycontext = mycontext;
        this.mydocorgList = mydocList;
        this.mydocList = mydocList;

    }


    @Override
    public DoctorsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
           View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctors_card,parent,false);
            return new DoctorsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(DoctorsViewHolder holder, int position) {
        final DoctorActivity.DoctorsDetails doctorsDetails = mydocList.get(position);
        holder.dname.setText(doctorsDetails.getDocName());
        holder.hospitalAdress.setText(doctorsDetails.getDochaddress());
        holder.degree.setText(doctorsDetails.getDocdegree() + "/" + doctorsDetails.getDocSepcial());
        holder.hospitalName.setText(doctorsDetails.getDochname());
        //holder.rating.setText(doctorsDetails.getDocrating());
        holder.yearOfexp.setText(doctorsDetails.getDocyoexp());

        holder.makeAppointment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mycontext, "Your Appointment request for " + doctorsDetails.getDocName() + " has been made", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public Filter getFilter() {
        Filter filter = new Filter() {

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                mydocList = (List<DoctorActivity.DoctorsDetails>) results.values;
                notifyDataSetChanged();

            }

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                List<DoctorActivity.DoctorsDetails> FilterdList = new ArrayList<DoctorActivity.DoctorsDetails>();
                if (mydocorgList == null) {
                    mydocorgList = new ArrayList<DoctorActivity.DoctorsDetails>(mydocList);

                }
                if (constraint == null || constraint.length() == 0) {

                    // set the Original result to return
                    filterResults.count = mydocorgList.size();
                    filterResults.values = mydocorgList;
                }else{
                    constraint = constraint.toString().toLowerCase();
                    for (int i = 0; i < mydocorgList.size(); i++) {
                        String data = mydocorgList.get(i).dname;
                        if (data.toLowerCase().startsWith(constraint.toString())) {
                            FilterdList.add(new DoctorActivity.DoctorsDetails(mydocorgList.get(i).dname,mydocorgList.get(i).special,mydocorgList.get(i).yoexp,
                                    mydocorgList.get(i).degree,mydocorgList.get(i).haddress,mydocorgList.get(i).hname));

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
        return mydocList.size();
    }

    public class DoctorsViewHolder extends RecyclerView.ViewHolder {
        TextView dname, degree, hospitalName, hospitalAdress, rating, yearOfexp;
        Button makeAppointment;

        public DoctorsViewHolder(View itemView) {
            super(itemView);
            dname = (TextView) itemView.findViewById(R.id.dname);
            degree = (TextView) itemView.findViewById(R.id.degree);
            hospitalName = (TextView) itemView.findViewById(R.id.hospital_name);
            hospitalAdress = (TextView) itemView.findViewById(R.id.hos_adderss);
            rating = (TextView) itemView.findViewById(R.id.rating);
            yearOfexp = (TextView) itemView.findViewById(R.id.yoexp);
            makeAppointment = (Button) itemView.findViewById(R.id.make_appointment);
        }

    }


}
