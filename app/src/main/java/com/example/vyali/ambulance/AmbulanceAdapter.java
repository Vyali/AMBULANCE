package com.example.vyali.ambulance;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.icu.lang.UProperty;
import android.nfc.cardemulation.HostNfcFService;
import android.os.Build;
import android.provider.ContactsContract;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.List;
import java.util.logging.Handler;

/**
 * Created by vyali on 10/3/18.
 */

public class AmbulanceAdapter extends RecyclerView.Adapter<AmbulanceAdapter.ViewHolder> {

    //private String mDataset[];
    List<MainActivity.MyList> mDataset;
    Context ctx;
    DatabaseReference dbReference;

    public AmbulanceAdapter(List<MainActivity.MyList> myDataset,Context c){
        mDataset=myDataset;
        ctx= c;

    }




    public class ViewHolder extends RecyclerView.ViewHolder /*implements View.OnClickListener*/{
        public TextView ambName,eta,distance;
        SlidingUpPanelLayout sl;
        List<MainActivity.MyList> mdataset;
        Context c ;

        public ViewHolder(View itemView, final Context c, final List<MainActivity.MyList> mdataset) {
            super(itemView);
            //itemView.setOnClickListener(this);

           // sl=(SlidingUpPanelLayout)  itemView.findViewById(R.id.slide_panel);
//            System.out.println("%%%%%%%panel state "+sl.getPanelState().toString());

            ambName = itemView.findViewById(R.id.amb_name);
            eta = itemView.findViewById(R.id.time);
            distance=itemView.findViewById(R.id.distance);
            this.mdataset = mdataset;
            this.c =c ;
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    MainActivity.MyList list = mdataset.get(position);
                    setVehicleBookingDetail(list.getVehicleDetail().get(position).getVehicleEmail(),list.getVehicleDetail().get(position).getVehicleLat(),list.getVehicleDetail().get(position).getVehicleLang());

                    Intent i = new Intent(c,UpdatePositionService.class);


                    i.putExtra("distance",list.getDistance());
                    i.putExtra("time",list.geteTime());
                    i.putExtra("vehicle_lang",list.getVehicleDetail().get(position).getVehicleLang());
                    i.putExtra("vehicle_lat",list.getVehicleDetail().get(position).getVehicleLat());
                    i.putExtra("vehicle_name",list.getVehicleDetail().get(position).getVehicleName());
                    i.putExtra("index",String.valueOf(position));

                    /*PendingIntent pendingIntent = PendingIntent.getActivity(c,0,i,0);
                    Notification notification;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                         notification = new Notification.Builder(c,"channel1")
                                .setContentTitle("Ambulance Location")
                                .setContentText("Currenlty it is ")
                                .setSmallIcon(R.drawable.ambulance)
                                .setContentIntent(pendingIntent)
                                .build();
                         c.startForegroundService(i);
                    }*/

//            if(sl.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
//                sl.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            }
                    //System.out.println("%^^^^^^%panel state "+sl.getPanelState().toString());

                    c.startService(i);

                }
            });


        }

/*        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            MainActivity.MyList list= this.mdataset.get(position);
            //MainActivity ma = new MainActivity();
           // ma.collapse();



            Intent i = new Intent(this.c,UpdatePositionService.class);
            i.putExtra("distance",list.getDistance());
            i.putExtra("time",list.geteTime());
            i.putExtra("vehicle_lang",list.getVehicleDetail().get(position).getLang());
            i.putExtra("vehicle_lat",list.getVehicleDetail().get(position).getLat());

//            if(sl.getPanelState() == SlidingUpPanelLayout.PanelState.EXPANDED){
//                sl.setPanelState(SlidingUpPanelLayout.PanelState.COLLAPSED);
//            }
            //System.out.println("%^^^^^^%panel state "+sl.getPanelState().toString());
            this.c.startService(i);


        }*/
    }


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.ambulance_name_view,parent,false);

        ViewHolder viewHolder = new ViewHolder(view,ctx,mDataset);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

            String name = mDataset.get(position).getVehicleDetail().get(position).getVehicleName();
            name =name.substring(0,name.indexOf(" "));
            holder.distance.setText(mDataset.get(position).getDistance());
            holder.ambName.setText(name);
//            holder.eta.setText(mDataset.get(position).geteTime());
           holder.eta.setText(mDataset.get(position).geteTime());






    }



    @Override
    public int getItemCount() {
        return mDataset.size();
    }


    void setVehicleBookingDetail(String email,String lat,String lang){

        System.out.println("in vehicle booking detail#%#%#%($*&549875_(_");
      // LatLng latLng = new LatLng(Double.parseDouble(lat),Double.parseDouble(lang));
        int index = email.indexOf("@");
        String email2 = email.substring(0,index);

        MainActivity.AmbUserBookingDetail ambUserBookingDetail = new MainActivity.AmbUserBookingDetail(true,email2,Double.parseDouble(lat),Double.parseDouble(lang));
        dbReference = FirebaseDatabase.getInstance().getReference().child("users");

        try{
            dbReference.child("vehiclebooking").child(email).setValue(ambUserBookingDetail);

        }catch (Exception e){
            e.printStackTrace();
        }





    }
}
