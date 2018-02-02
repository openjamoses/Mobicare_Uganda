package com.example.john.mobicare_uganda.recycler;

/**
 * Created by john on 12/3/17.
 */

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.chatts.Chatts_Tests;
import com.example.john.mobicare_uganda.doctors.Profile_PatientView;
import com.example.john.mobicare_uganda.firebase_collections.util.LoginStatus;
import com.example.john.mobicare_uganda.fragments.Doctor_Fragment;
import com.example.john.mobicare_uganda.views.Appointment_Scheduler;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import connectivity.Constants;
import connectivity.SessionManager;
import server_connections.Doctor_Cantacts;
import server_connections.Doctor_Category;
import server_connections.Doctor_Facility;
import server_connections.Doctor_Operations;
import services.CallDetectionService;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private List<MyList> list;
    private Context mCtx;

    public CustomAdapter(List<MyList> list, Context mCtx) {
        this.list = list;
        this.mCtx = mCtx;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recycler_list, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(final CustomAdapter.ViewHolder holder, int position) {
        final MyList myList = list.get(position);
        holder.textViewHead.setText(myList.getHead());
        holder.textViewDesc.setText(myList.getDesc());
        holder.textstatus.setText(myList.getStatus());

        category(myList.getID(), holder.textViewDesc);
        updateStatus(myList.getID(), holder.textstatus);

        holder.buttonViewOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                final String doctor = myList.getHead();
                final int cat_id = 1;
                //will show popup menu here
                //creating a popup menu
                final PopupMenu popup = new PopupMenu(mCtx, holder.buttonViewOption);
                //inflating menu from xml resource
                popup.inflate(R.menu.options_menu);
                //adding click listener
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.action_call:
                                //handle menu1 click
                                try {
                                    try {
                                        String contact = "";
                                        Cursor cursor = new Doctor_Cantacts(mCtx).doctorContact(myList.getID());
                                        if (cursor != null){
                                            if (cursor.moveToFirst()){
                                                do {
                                                    contact = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_CONTACT));
                                                }while (cursor.moveToNext());

                                               /** SessionManager sessionManager = new SessionManager(mCtx);
                                                sessionManager.dialNumber(contact,"patient");
                                                processStartService(); **/

                                                Intent callIntent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + contact));
                                                callIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                mCtx.startActivity(callIntent);
                                            }else {
                                                Toast.makeText(mCtx, "W're sory, Contact Details Not found..!",Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    }catch (Exception e){
                                        e.printStackTrace();
                                    }
                                }catch (Exception e){
                                    e.printStackTrace();
                                }
                                break;
                            case R.id.action_sms:

                                    Intent intent = new Intent(mCtx,Chatts_Tests.class);
                                    intent.putExtra("name",doctor);
                                    //intent.putExtra("image",b);
                                    intent.putExtra("id",String.valueOf(myList.getID()));
                                    intent.putExtra("category",String.valueOf(cat_id));
                                    intent.putExtra("message","");
                                    mCtx.startActivity(intent);

                                break;
                            case R.id.action_appointment:
                                //handle menu3 click
                                Intent intent2 = new Intent(mCtx,Appointment_Scheduler.class);
                                intent2.putExtra("name",doctor);
                                intent2.putExtra("id",String.valueOf(myList.getID()));
                                intent2.putExtra("category",String.valueOf(cat_id));
                                mCtx.startActivity(intent2);

                                break;
                            case R.id.action_profile:
                                //handle menu3 click
                                Intent intent3 = new Intent(mCtx,Profile_PatientView.class);
                                Log.e("CustomAdapter", String.valueOf(myList.getID()));
                                intent3.putExtra("doctor_id",String.valueOf(myList.getID()));
                                mCtx.startActivity(intent3);
                                break;
                        }
                        return false;
                    }
                });
                //displaying the popup
                popup.show();

            }
        });
    }


    @Override
    public int getItemCount() {
        return list.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {

        public TextView textViewHead;
        public TextView textViewDesc;
        public TextView textstatus;
        public TextView buttonViewOption;

        public ViewHolder(View itemView) {
            super(itemView);

            textViewHead = (TextView) itemView.findViewById(R.id.textViewHead);
            textViewDesc = (TextView) itemView.findViewById(R.id.textViewDesc);
            textstatus = (TextView) itemView.findViewById(R.id.textstatus);
            buttonViewOption = (TextView) itemView.findViewById(R.id.textViewOptions);
        }
    }

    // Filter Class
    public void filter(String charText, String search_type, TextView textView) {
        //charText = charText.toLowerCase(Locale.getDefault());
        Doctor_Fragment.list.clear();
        if (charText.length() == 0) {
            Doctor_Fragment.list.addAll(list);
        } else {
            List<String> name_list = new ArrayList<>();
            List<Integer> id_list = new ArrayList<>();
            try {
                Cursor cursor = null;
                if (search_type.equals("Category")){
                    cursor = new Doctor_Operations(mCtx).getCategory(charText);
                }else if (search_type.equals("Facility")){
                    cursor = new Doctor_Operations(mCtx).setFacilty(charText);
                }else if (search_type.equals("District")){
                    cursor = new Doctor_Operations(mCtx).setDistrict(charText);
                }else {
                    cursor = new Doctor_Operations(mCtx).getSearch(charText);
                }
                if (cursor.moveToFirst()){
                    int count = 0;
                    do {
                        MyList myList = new MyList(cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_FNAME))+" "+
                                cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_FNAME)),
                                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi molestie nisi dui.",
                                "offline",
                                cursor.getInt(cursor.getColumnIndex(Constants.config.WORKER_ID)));
                        Doctor_Fragment.list.add(myList);
                        count ++;
                    }while (cursor.moveToNext());

                    textView.setText(count+" - records found...!");
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        notifyDataSetChanged();
    }
    public void category(Integer id, TextView textView){
        List<String> list1 = new ArrayList<>();
        List<String> list2 = new ArrayList<>();
        final List<String> list3 = new ArrayList<>();

        list1 = new Doctor_Facility(mCtx).getMajor(id);
        list2 = new Doctor_Category(mCtx).getMajor(id);

        textView.setText("("+list1.size()+") Health Facilities: ");
        for (int i=0; i<list1.size(); i++){
            if (i<list1.size()){
                textView.append(list1.get(i)+", ");
            }else  if (i==list1.size()-1){
                textView.append(list1.get(i)+".");
            }
        }
        textView.append("("+list2.size()+") Category: ");
        for (int i=0; i<list2.size(); i++){
            if (i<list2.size()){
                textView.append(list2.get(i)+", ");
            }else if (i==list2.size()-1){
                textView.append(list2.get(i)+".");
            }

        }
    }

    private void updateStatus(final Integer doctor_id, final TextView textView){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void run() {
                statusSet(new LoginStatus(mCtx).selectDoctor(String.valueOf(doctor_id)),textView);
            }
        }, 0, 10000);//1 minutes
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void statusSet(final String s, final TextView textView) {
        ((Activity) mCtx).runOnUiThread(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                try {
                    textView.setText(s+"");
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

    private void processStartService() {
        Intent intent = new Intent(mCtx, CallDetectionService.class);
        mCtx.startService(intent);
    }

}