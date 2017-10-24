package com.example.john.mobicare_uganda.dbsyncing;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.firebase_collections.Login;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.Hashtable;
import java.util.Map;

import connectivity.Constants;
import connectivity.DBHelper;

import static connectivity.Constants.config.EMAIL_;
import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.PASSWORD_;
import static connectivity.Constants.config.PHONE;
import static connectivity.Constants.config.UPDATE_PASSWORD_URL;
import static connectivity.Constants.config.VERIFY;

/**
 * Created by john on 10/22/17.
 */

public class Password {
    Context context;
    private static final String TAG = "Password";
    public Password(Context context){
        this.context = context;
    }

    public String save(String phone,String email, String password, int status) {
        SQLiteDatabase database = new DBHelper(context).getWritableDatabase();
        String message = null;

        try{
            //database.beginTransaction();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Constants.config.PHONE,phone);
            contentValues.put(Constants.config.EMAIL_,email);
            contentValues.put(Constants.config.PASSWORD_,password);
            contentValues.put(Constants.config.VERIFY,status);
            database.insert(Constants.config.TABLE_PHONE_EMAIL, null, contentValues);
            //database.setTransactionSuccessful();
            message = "password cases saved!";

        }catch (Exception e){
            e.printStackTrace();
            message = "Sorry, error: "+e;
        }finally {
            //database.close();
            // database.endTransaction();
        }
        return message;
    }

    public String verify(String email, int status){
        SQLiteDatabase database = new DBHelper(context).getWritableDatabase();
        String message = null;

        try{
            //database.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.config.EMAIL_,email);
            contentValues.put(Constants.config.VERIFY,status);
            database.update(Constants.config.TABLE_PHONE_EMAIL,contentValues,EMAIL_+" = "+email,null);
            //database.setTransactionSuccessful();
            message = "password cases updated!";

        }catch (Exception e){
            e.printStackTrace();
            message = "Sorry, error: "+e;
        }finally {
            //database.close();
            // database.endTransaction();
        }
        return message;
    }

    public String update(String phone, String email, String password,int verified){
        SQLiteDatabase database = new DBHelper(context).getWritableDatabase();
        String message = null;

        try{
            int status = 0;
            //database.beginTransaction();
            ContentValues contentValues = new ContentValues();

            contentValues.put(Constants.config.PHONE,phone);
            contentValues.put(Constants.config.EMAIL_,email);
            contentValues.put(Constants.config.PASSWORD_,password);
            contentValues.put(Constants.config.VERIFY,verified);
            Log.e(TAG,phone);
            database.update(Constants.config.TABLE_PHONE_EMAIL,contentValues,PHONE+"="+phone,null);
            //database.setTransactionSuccessful();
            message = "password cases updated!";

        }catch (Exception e){
            e.printStackTrace();
            message = "Sorry, error: "+e;
        }finally {
            //database.close();
            // database.endTransaction();
        }
        return message;
    }

    public String selectPhone(String phone){
        String email = "";
        SQLiteDatabase db = new DBHelper(context).getReadableDB();
        Cursor cursor = null;
        try{
            db.beginTransaction();
            String query = "SELECT "+Constants.config.EMAIL_+" FROM" +
                    " "+ Constants.config.TABLE_PHONE_EMAIL+" WHERE "+Constants.config.PHONE+" = '"+phone+"'  ORDER BY "+Constants.config.EMAIL_+" DESC LIMIT 1 ";
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()){
                do {
                    email = cursor.getString(cursor.getColumnIndex(Constants.config.EMAIL_));
                }while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return email;
    }

    public String login(String email, String password){
        int verified = 0;
        String message = "";
        SQLiteDatabase db = new DBHelper(context).getReadableDB();
        Cursor cursor = null;
        try{
            db.beginTransaction();
            String query = "SELECT "+Constants.config.VERIFY+" FROM" +
                    " "+ Constants.config.TABLE_PHONE_EMAIL+" WHERE "+Constants.config.EMAIL_+" = '"+email+"' " +
                    " AND "+Constants.config.PASSWORD_+" = '"+password+"' ORDER BY "+Constants.config.EMAIL_+" DESC LIMIT 1 ";
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()){

                do {
                    verified = cursor.getInt(cursor.getColumnIndex(Constants.config.VERIFY));
                }while (cursor.moveToNext());

                if (verified == 0){
                    message = "Email  Not verified";
                }else{
                    message = "Success";
                }
            }else {
                message = "Login Failed ! ";
            }

            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
            message = "Login Failed !";
        }finally {
            db.endTransaction();
        }
        return message;
    }


    ///// TODO: 10/13/17  select here!
    public Cursor selectAll(){
        SQLiteDatabase db = new DBHelper(context).getReadableDB();
        Cursor cursor = null;
        try{
            db.beginTransaction();
            String query = "SELECT *  FROM" +
                    " "+ Constants.config.TABLE_PHONE_EMAIL+"  ORDER BY "+Constants.config.EMAIL_+" ASC ";
            cursor = db.rawQuery(query,null);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return  cursor;
    }
    public  int select(String name){
        int last_id = 0;
        SQLiteDatabase db = new DBHelper(context).getReadableDB();
        Cursor cursor = null;
        try{
            db.beginTransaction();
            String query = "SELECT "+Constants.config.DISTRICT_ID+" FROM" +
                    " "+ Constants.config.TABLE_DISTRICT+" WHERE "+Constants.config.DISTRICT_NAME+" = '"+name+"'  ORDER BY "+Constants.config.DISTRICT_ID+" DESC LIMIT 1 ";
            cursor = db.rawQuery(query,null);
            if (cursor.moveToFirst()){
                do {
                    last_id = cursor.getInt(cursor.getColumnIndex(Constants.config.DISTRICT_ID));
                }while (cursor.moveToNext());
            }
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
        return  last_id;
    }


    public void send(final String phone, final String email, final String password, final int verified, final String type){

        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+UPDATE_PASSWORD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try{
                            int status = 0;
                            if (response.equals("Success")){
                                status = 1;
                            }
                            String message = "";
                            if (type.equals("update")){
                                message = update(phone,email,password,verified);
                            }else {
                               message  = save(phone,email,password,verified);
                            }
                            Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundResource(R.drawable.rounded_blank);
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                        /*Here you can do anything with above textview like text.setTextColor(Color.parseColor("#000000"));*/
                            toast.show();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Log.e(TAG,response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.e(TAG, volleyError.getMessage());
                        try {
                            int status = 0;
                            //String message = saves(hospital_name,s_name,s_number,email,imei,country,other,city,status,date);

                            Toast toast = Toast.makeText(context, "Check our internet Connections", Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundResource(R.drawable.rounded_blank);
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                            toast.show();
                            //Log.e(TAG, volleyError.getMessage());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(PHONE, phone);
                params.put(EMAIL_, email);
                params.put(PASSWORD_, password);
                params.put(VERIFY, String.valueOf(verified));
                params.put("type", type);


                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }



    public void insert_new(JSONArray response){
        long startTime = 0;
        new InsertBackground(context,startTime).execute(response);
    }
    public class InsertBackground extends AsyncTask<JSONArray,Void,String> {

        Context context;
        long startTime;
        long endTime = System.nanoTime();
        long duration = (endTime - startTime)/1000000000;

        InsertBackground(Context context, long start_time){
            this.context = context;
            this.startTime = start_time;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // startTime = System.nanoTime();
            // progressDialog.setTitle("Now Saving ! ...");
        }

        @Override
        protected String doInBackground(JSONArray... jsonArrays) {
            String message = null;
            int status = 1;
            SQLiteDatabase db = new DBHelper(context).getWritableDB();
            try{

                db.beginTransaction();
                //String get_json = get
                //JSONArray jsonArray = new JSONArray(results);
                JSONArray jsonArray = jsonArrays[0];
                db.execSQL("DELETE FROM " + Constants.config.TABLE_PHONE_EMAIL+"  ");
                int total = 0;
                for (int i = 0; i < jsonArray.length(); i++) {
                    ContentValues contentValues = new ContentValues();
                    JSONObject jsonObject = jsonArray.getJSONObject(i);

                    contentValues.put(PHONE,jsonObject.getString(Constants.config.PHONE));
                    contentValues.put(EMAIL_,jsonObject.getString(Constants.config.EMAIL_));
                    contentValues.put(VERIFY,jsonObject.getString(Constants.config.VERIFY));
                    contentValues.put(PASSWORD_,jsonObject.getString(Constants.config.PASSWORD_));

                    db.insert(Constants.config.TABLE_PHONE_EMAIL, null, contentValues);
                    total ++;
                }
                db.setTransactionSuccessful();
                message = total+" records , Password Table Updated successfully!";

            }catch (Exception e){
                e.printStackTrace();
                message = "Error: "+e;
                Log.e("Error: ",e.toString());
            }finally {
                db.endTransaction();
            }
            return  message;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            endTime = System.nanoTime();
            long duration = (endTime - startTime)/1000000000;

            Log.e("Fetch results",s);

        }
    }

}
