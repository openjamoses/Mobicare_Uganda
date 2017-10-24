package com.example.john.mobicare_uganda;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import adapters.Appoint_Lists;
import adapters.Appointments_pat_list;
import connectivity.Constants;
import server_connections.Appointment_Operations;

/**
 * Created by john on 10/6/17.
 */

public class Appointment_main extends AppCompatActivity {
    private ListView listView;
    private Context context = this;
    List<String> date_list,d_l,t_l;
    List<String> time_list;
    List<String> status_list;
    List<String> body_list,names_list;

    String[] date = null,body = null,d,names;
    String[] time = null,t;
    String[] statuss = null;
    private TextView textView,msg_text;
    private ImageView imageView;
    private final static String TAG = "Appointments";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.appoint_doctor);
        listView = (ListView) findViewById(R.id.listView);
        msg_text = (TextView) findViewById(R.id.msg_text);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        setData();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            //Intent intent = new Intent(context, Welcome_Activity.class);
            //startActivity(intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        return super.onOptionsItemSelected(item);
    }

    public void setData(){
        Cursor cursor = null;
        date_list = new ArrayList<>();
        time_list = new ArrayList<>();
        status_list = new ArrayList<>();
        body_list = new ArrayList<>();
        names_list = new ArrayList<>();

        d_l = new ArrayList<>();
        t_l = new ArrayList<>();

        List<Integer> id_list = new ArrayList<>();
        String status_ = null;
        try {
            cursor = new Appointment_Operations(context).allAppointments2();
            if(cursor.moveToFirst()){
                do {
                    date_list.add(cursor.getString(cursor.getColumnIndex(Constants.config.APPOINTMENT_DATE)));
                    time_list.add(cursor.getString(cursor.getColumnIndex(Constants.config.APPOINTMENT_TIME)));
                    body_list.add(cursor.getString(cursor.getColumnIndex(Constants.config.APPOINTMENT_BODY)));
                    id_list .add(cursor.getInt(cursor.getColumnIndex(Constants.config.APPOINT_ID)));
                    names_list.add(cursor.getString(cursor.getColumnIndex(Constants.config.FIRST_NAME)) +" "
                            +cursor.getString(cursor.getColumnIndex(Constants.config.LAST_NAME)));

                    d_l.add(cursor.getString(cursor.getColumnIndex(Constants.config.DATE)));
                    t_l.add(cursor.getString(cursor.getColumnIndex(Constants.config.TIME)));
                    int status = cursor.getInt(cursor.getColumnIndex(Constants.config.APPOINTMENT_STATUS));

                    if(status == 1){
                        status_ = "pending";
                    }else if(status == 2) {
                        status_ = "approved";
                    }else if (status == 0){
                        status_  = "cancel";
                    }else if (status == 3){
                        status_ = "Disapproved";
                    }else {
                        status_ = "Confirmed";
                    }
                    status_list.add(status_);

                }while (cursor.moveToNext());
            }else {
                Log.e(TAG," No data found to be displayed!");
            }

            date = new String[date_list.size()];
            time = new String[time_list.size()];
            statuss = new String[status_list.size()];
            body = new String[body_list.size()];
            names = new String[names_list.size()];
            names = names_list.toArray(names);
            if (date_list.size()>0){
                msg_text.setText(date_list.size()+" appointments made");
            }else {
                msg_text.setText("no appointments made");
            }

            Integer[] ids = new Integer[id_list.size()];
            ids = id_list.toArray(ids);

            t = new String[t_l.size()];
            d = new String[d_l.size()];

            date = date_list.toArray(date);
            time = time_list.toArray(time);
            statuss = status_list.toArray(statuss);
            body = body_list.toArray(body);

            t = t_l.toArray(t);
            d = d_l.toArray(d);

            for (int i=0; i<date.length; i++){
                Log.e(TAG,date[i]+" , "+time[i]+" , "+statuss[i]+" , "+id_list.get(i));
            }
            Appointments_pat_list adapter = new Appointments_pat_list(context,names,date,time,statuss,body, d,t, ids);
            listView.setAdapter(adapter);

        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
