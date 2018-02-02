package com.example.john.mobicare_uganda.doctors;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.firebase_collections.util.UpdateStatus;

import java.util.ArrayList;
import java.util.List;

import connectivity.Constants;
import server_connections.Doctor_Cantacts;
import server_connections.Doctor_Category;
import server_connections.Doctor_Facility;
import users.Doctor_Details;
import users.Util;

/**
 * Created by john on 1/22/18.
 */

public class Profile_PatientView extends AppCompatActivity {
    private final static String TAG = "Profile";
    private TextView minor_contact,textView_reg,textView_gender;
    private RelativeLayout minor_layout,reg_layout;
    private Context context = this;
    ImageView add_contact,add_minor;
    String doctor_id;
    String fname="",lname="",gender = "",reg_number="";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.doctor_profile);

        ListView listView_cmajor = (ListView) findViewById(R.id.listView_contact_major);
        ListView listView_cminor = (ListView) findViewById(R.id.listView_contact_minor);
        ListView listView_category = (ListView) findViewById(R.id.listView_category);
        ListView listView_facility = (ListView) findViewById(R.id.listView_facility);
        ListView listView_name = (ListView) findViewById(R.id.list_name);
        minor_contact = (TextView) findViewById(R.id.tvNumber2);
        textView_reg = (TextView) findViewById(R.id.textView_reg);
        textView_gender = (TextView) findViewById(R.id.textView_gender);

        add_contact = (ImageView) findViewById(R.id.add_contact);
        minor_layout = (RelativeLayout) findViewById(R.id.minor_layout);
        reg_layout = (RelativeLayout) findViewById(R.id.reg_layout);

        ImageView imageView = (ImageView) findViewById(R.id.status_toggle) ;
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setVisibility(View.GONE);
        //TODO: Adding the correspondings lists items....
        listValuesCMajor(listView_cmajor);
        listValuesCMinor(listView_cminor);
        listCategory(listView_category);
        listFacility(listView_facility);

        listName(listView_name);
        listReg();

        ///TODO: Expands the listview programmably
        Util.setListViewHeightBasedOnChildren(listView_cmajor);
        Util.setListViewHeightBasedOnChildren(listView_cminor);
        Util.setListViewHeightBasedOnChildren(listView_category);
        Util.setListViewHeightBasedOnChildren(listView_facility);
        Util.setListViewHeightBasedOnChildren(listView_name);

        doctor_id = getIntent().getStringExtra("doctor_id");
        setDetails();
        new UpdateStatus(context).doctor_status_Image(doctor_id,imageView);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    private void setDetails(){
        Cursor cursor = null;
        try{
            cursor = new Doctor_Cantacts(context).getDoctorDetails(Integer.parseInt(doctor_id));
            if (cursor.moveToFirst()){
                do {
                    fname = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_FNAME));
                    lname = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_LNAME));
                    gender = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_GENDER));
                    reg_number = cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_REG_NUMBER));
                }while (cursor.moveToNext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    private void listValuesCMajor(ListView listView_cmajor) {
        if (doctor_id == null){
            doctor_id = getIntent().getStringExtra("doctor_id");
        }


        Log.e(TAG, "Doctor_ID: "+doctor_id);
        List<String> list = new ArrayList<>();
        list = new Doctor_Cantacts(context).getMajor(Integer.parseInt(doctor_id));
        for(int i=0; i<list.size(); i++){
            Log.e(TAG,list.get(i));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                R.layout.simple_list_item,
                list );

        listView_cmajor.setAdapter(arrayAdapter);
    }
    private void listValuesCMinor(ListView listView_cmajor) {

        List<String> list = new ArrayList<>();
        list = new Doctor_Cantacts(context).getMinor(Integer.parseInt(doctor_id));
        for(int i=0; i<list.size(); i++){
            Log.e(TAG,list.get(i));
        }
        if(list.size() == 0){
            minor_layout.setVisibility(View.GONE);
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                R.layout.simple_list_item,
                list );

        listView_cmajor.setAdapter(arrayAdapter);
    }

    private void listCategory(ListView listView){

        List<String> list = new ArrayList<>();
        list = new Doctor_Category(context).getMajor(Integer.parseInt(doctor_id));
        for(int i=0; i<list.size(); i++){
            Log.e(TAG,list.get(i));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                R.layout.simple_list_item,
                list );

        listView.setAdapter(arrayAdapter);
    }

    private void listFacility(ListView listView){
        List<String> list = new ArrayList<>();
        list = new Doctor_Facility(context).getMajor(Integer.parseInt(doctor_id));
        for(int i=0; i<list.size(); i++){
            Log.e(TAG,list.get(i));
        }
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                R.layout.simple_list_item,
                list );

        listView.setAdapter(arrayAdapter);
    }

    private void listName(ListView listView){
        textView_gender.setText(gender+"");

        List<String> list = new ArrayList<>();

        list.add(fname);
        list.add(lname);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(
                context,
                R.layout.simple_list_item,
                list );

        listView.setAdapter(arrayAdapter);

    }

    private void listReg(){
        if(reg_number != null){
            textView_reg.setText(reg_number);
        }else {
            reg_layout.setVisibility(View.GONE);
        }
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

}