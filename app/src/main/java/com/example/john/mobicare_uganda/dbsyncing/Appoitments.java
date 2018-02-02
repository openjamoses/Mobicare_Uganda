package com.example.john.mobicare_uganda.dbsyncing;

import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.mobicare_uganda.firebase_collections.util.SendNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.Get_CurrentDateTime;
import server_connections.Doctor_Operations;
import server_connections.Image_Operations;
import users.CurrentDoctor;
import users.CurrentUser;
import users.Dialog_Message;
import users.Doctor_Details;
import users.Set_Alarm;
import users.User_Details;

import static connectivity.Constants.config.APPOINTMENT_BODY;
import static connectivity.Constants.config.APPOINTMENT_DATE;
import static connectivity.Constants.config.APPOINTMENT_STATUS;
import static connectivity.Constants.config.APPOINTMENT_TIME;
import static connectivity.Constants.config.APPOINT_ID;
import static connectivity.Constants.config.CALL_DATE;
import static connectivity.Constants.config.CALL_DURATION;
import static connectivity.Constants.config.CALL_ID;
import static connectivity.Constants.config.CALL_TIME;
import static connectivity.Constants.config.CALL_TYPE;
import static connectivity.Constants.config.CATEGORY_ID;
import static connectivity.Constants.config.DATE;
import static connectivity.Constants.config.DAY;
import static connectivity.Constants.config.FETCH_STATUS;
import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.PHONE;
import static connectivity.Constants.config.SAVE_APPOINTMENT_URL;
import static connectivity.Constants.config.SAVE_SMS_URL;
import static connectivity.Constants.config.SMS_BODY;
import static connectivity.Constants.config.SMS_DATE;
import static connectivity.Constants.config.SMS_STATUS;
import static connectivity.Constants.config.SMS_TIME;
import static connectivity.Constants.config.SMS_TYPE;
import static connectivity.Constants.config.TIME;
import static connectivity.Constants.config.WORKER_ID;
/**
 * Created by john on 11/2/17.
 */

public class Appoitments {
    private Context context;
    private static final String TAG = "Calls_sync";
    public Appoitments(Context context){
        this.context = context;
    }

    public String save_appointments(String date, String time, String day, String doctor_id, String category, String phone, String body, int status) {
        SQLiteDatabase db = new DBHelper(context).getWritableDB();
        String cur_date = new Get_CurrentDateTime().getCurrentDate();
        String cur_time = new Get_CurrentDateTime().getCurrentTime();
        String message = "";
        try{
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.config.APPOINTMENT_DATE, date);
            contentValues.put(Constants.config.APPOINTMENT_TIME, time);
            contentValues.put(DATE,cur_date);
            contentValues.put(TIME,cur_time);
            contentValues.put(DAY,day);
            contentValues.put(Constants.config.WORKER_ID, doctor_id);
            contentValues.put(Constants.config.CATEGORY_ID, category);
            contentValues.put(Constants.config.PHONE, phone);
            contentValues.put(Constants.config.APPOINTMENT_BODY, body);
            contentValues.put(Constants.config.APPOINTMENT_STATUS, status);
            contentValues.put(Constants.config.FETCH_STATUS, 0);
            db.insert(Constants.config.TABLE_APPOINTMENT, null, contentValues);
            db.setTransactionSuccessful();
            message = "Appointment Scheduled!";

        }catch (Exception e){
            Log.e(TAG,"Error: "+e);
        }finally {
            db.endTransaction();
        }
        return message;
    }

    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_APPOINTMENT ;

        SQLiteDatabase database = new DBHelper(context).getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {

                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id",  String.valueOf(cursor.getInt(cursor.getColumnIndex(APPOINT_ID))));
                map.put(APPOINTMENT_DATE, cursor.getString(cursor.getColumnIndex(APPOINTMENT_DATE)));
                map.put(APPOINTMENT_TIME, cursor.getString(cursor.getColumnIndex(APPOINTMENT_TIME)));
                map.put(Constants.config.APPOINTMENT_BODY, cursor.getString(cursor.getColumnIndex(APPOINTMENT_BODY)));
                map.put(DATE,cursor.getString(cursor.getColumnIndex(DATE)));
                map.put(TIME,cursor.getString(cursor.getColumnIndex(TIME)));
                map.put(DAY,cursor.getString(cursor.getColumnIndex(DAY)));
                map.put(Constants.config.WORKER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(WORKER_ID))));
                map.put(Constants.config.CATEGORY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))));
                map.put(Constants.config.PHONE, cursor.getString(cursor.getColumnIndex(PHONE)));
                map.put(APPOINTMENT_STATUS, cursor.getString(cursor.getColumnIndex(APPOINTMENT_STATUS)));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        //database.close();
        return wordList;
    }
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        int status = 0;
        String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_APPOINTMENT + " WHERE " + FETCH_STATUS + " = '" + status + "' ";
        SQLiteDatabase database = new DBHelper(context).getReadableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id",  String.valueOf(cursor.getInt(cursor.getColumnIndex(APPOINT_ID))));
                map.put(APPOINTMENT_DATE, cursor.getString(cursor.getColumnIndex(APPOINTMENT_DATE)));
                map.put(APPOINTMENT_TIME, cursor.getString(cursor.getColumnIndex(APPOINTMENT_TIME)));
                map.put(Constants.config.APPOINTMENT_BODY, cursor.getString(cursor.getColumnIndex(APPOINTMENT_BODY)));
                map.put(DATE,cursor.getString(cursor.getColumnIndex(DATE)));
                map.put(TIME,cursor.getString(cursor.getColumnIndex(TIME)));
                map.put(DAY,cursor.getString(cursor.getColumnIndex(DAY)));
                map.put(Constants.config.WORKER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(WORKER_ID))));
                map.put(Constants.config.CATEGORY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))));
                map.put(Constants.config.PHONE, cursor.getString(cursor.getColumnIndex(PHONE)));
                map.put(APPOINTMENT_STATUS, cursor.getString(cursor.getColumnIndex(APPOINTMENT_STATUS)));

                wordList.add(map);
            } while (cursor.moveToNext());
        }
        Gson gson = new GsonBuilder().create();
        //Use GSON to serialize Array List to JSON
        return gson.toJson(wordList);
    }
    /**
     * Get Sync status of SQLite
     * @return
     */
    public String getSyncStatus(){
        String msg = null;
        if(this.dbSyncCount() == 0){
            msg = "SQLite and Remote MySQL DBs are in Sync!";
        }else{
            msg = "DB Sync neededn";
        }
        return msg;
    }
    /**
     * Get SQLite records that are yet to be Synced
     * @return
     */
    public int dbSyncCount(){
        int count = 0;
        SQLiteDatabase database = null;
        try {
            int status = 0;
            String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_APPOINTMENT + " WHERE " + FETCH_STATUS + " = '" + status + "' ";
            database = new DBHelper(context).getReadableDatabase();
            Cursor cursor = database.rawQuery(selectQuery, null);
            count = cursor.getCount();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                database.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return count;
    }
    /***
     *  @param id
     * @param status
     * @param phone
     * @param message
     * @param doctor_id
     */
    public void updateSyncStatus(int id, int status, String phone, String message, String doctor_id){
        SQLiteDatabase database = null;
        try {
            database = new DBHelper(context).getWritableDatabase();
            String updateQuery = "UPDATE " + Constants.config.TABLE_APPOINTMENT + " SET " + FETCH_STATUS + " = '" + status + "' where " + APPOINT_ID + "='" + id + "'  ";
            Log.d("query", updateQuery);
            database.execSQL(updateQuery);

            if (status == 1){
                String names = new User_Details(context).getName();
                String img_url = new Image_Operations(context).image( new CurrentUser(context).current());
                new SendNotification(context).sendSinglePush_Doctor(names+":users:"+phone,message,img_url, doctor_id);
            }
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try{
                database.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

    public Cursor selected(String date){
        Cursor cursor = null;
        try {
            String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_APPOINTMENT + " WHERE " + APPOINTMENT_DATE + " = '" + date + "'" +
                    " ORDER BY "+APPOINTMENT_DATE+" ASC ";
            SQLiteDatabase database = new DBHelper(context).getReadableDatabase();
            cursor = database.rawQuery(selectQuery, null);

        }catch (Exception e){
            e.printStackTrace();
        }
        return cursor;
    }
    public void send(final String date, final String time, final String day, final String doctor_id, final String category, final String phone, final String body, int status, final ProgressDialog progressDialog){
        try {
            progressDialog.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+SAVE_APPOINTMENT_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        int status = 0;
                        if (response.equals("Success")){
                            status = 1;
                            String names = new User_Details(context).getName();
                            String img_url = new Image_Operations(context).image( new CurrentUser(context).current());
                            new SendNotification(context).sendSinglePush_Doctor(names+":users:"+phone,body,img_url, String.valueOf(doctor_id));
                        }
                        String message = save_appointments(date,time,day,doctor_id,category,phone,body,status);
                        if (message.equals("Appointment Scheduled!")){
                            new Dialog_Message(context).showDialog("Appointment",
                                    "Your Appointment has been Scheduled Successfully, "+new Doctor_Operations(context).getNames(Integer.parseInt(doctor_id)) +" Will now Check your appointment and get back to you Shortly..!!");

                            try{
                                String[] splits = date.split("-");
                                String[] splits2 = time.split(":");
                                new Set_Alarm(context).setAlarm( Integer.parseInt(splits[0]),Integer.parseInt(splits[1]),Integer.parseInt(splits[2]),Integer.parseInt(splits2[0]), Integer.parseInt(splits2[1]));

                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }


                        try {
                            progressDialog.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            Log.e(TAG, volleyError.getMessage());
                            try {
                                progressDialog.dismiss();
                            }catch (Exception e){
                                e.printStackTrace();
                            }
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int status = 1;
                Map<String, String> map = new Hashtable<String, String>();

                map.put(APPOINTMENT_DATE,date);
                map.put(APPOINTMENT_TIME, time);
                map.put(Constants.config.APPOINTMENT_BODY, body);
                map.put(DATE,new Get_CurrentDateTime().getCurrentDate());
                map.put(TIME,new Get_CurrentDateTime().getCurrentDate());
                map.put(DAY,day);
                map.put(Constants.config.WORKER_ID, doctor_id);
                map.put(Constants.config.CATEGORY_ID, category);
                map.put(Constants.config.PHONE,phone);
                map.put(APPOINTMENT_STATUS, String.valueOf(status));

                //returning parameters
                return map;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }
}
