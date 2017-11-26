package server_connections;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
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
import com.example.john.mobicare_uganda.firebase_collections.util.SendNotification;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.Get_CurrentDateTime;
import users.CurrentDoctor;
import users.CurrentUser;
import users.Doctor_Details;
import users.User_Details;

import static connectivity.Constants.config.CATEGORY_ID;
import static connectivity.Constants.config.FETCH_STATUS;
import static connectivity.Constants.config.FILE_PATH;
import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.PHONE;
import static connectivity.Constants.config.SAVE_SMS_URL;
import static connectivity.Constants.config.SMS_BODY;
import static connectivity.Constants.config.SMS_DATE;
import static connectivity.Constants.config.SMS_ID;
import static connectivity.Constants.config.SMS_PATH;
import static connectivity.Constants.config.SMS_STATUS;
import static connectivity.Constants.config.SMS_TIME;
import static connectivity.Constants.config.SMS_TYPE;
import static connectivity.Constants.config.WORKER_ID;

/**
 * Created by john on 10/24/17.
 */

public class Messages_Syncing  {
    private Context context;
    private static final String undefined = "undefined";
    private static final String TAG = "Messages_Syncing";
    public Messages_Syncing(Context context){
        this.context = context;
    }
    public void save(String date, String time, String phone, String body, String path, int doctor_id, int category_id,int type, int status){
        SQLiteDatabase db = new DBHelper(context).getWritableDB();
        try{
            db.beginTransaction();
            ContentValues contentValues = new ContentValues();
            contentValues.put(Constants.config.SMS_DATE, date);
            contentValues.put(Constants.config.SMS_TIME, time);
            contentValues.put(Constants.config.PHONE, phone);
            contentValues.put(Constants.config.SMS_BODY, body);
            contentValues.put(Constants.config.SMS_PATH, path);
            contentValues.put(Constants.config.WORKER_ID, doctor_id);
            contentValues.put(Constants.config.CATEGORY_ID, category_id);
            contentValues.put(Constants.config.SMS_TYPE,type);
            contentValues.put(Constants.config.FETCH_STATUS, status);
            db.insert(Constants.config.TABLE_SMS, null, contentValues);
            db.setTransactionSuccessful();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            db.endTransaction();
        }
    }
    //// TODO: 10/15/17  Syncing
    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_SMS ;

        int status = 1;
        SQLiteDatabase database = new DBHelper(context).getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_ID))));
                params.put(SMS_DATE, cursor.getString(cursor.getColumnIndex(SMS_DATE)));
                params.put(SMS_TIME, cursor.getString(cursor.getColumnIndex(SMS_TIME)));
                params.put(PHONE, cursor.getString(cursor.getColumnIndex(PHONE)));
                params.put(SMS_BODY, cursor.getString(cursor.getColumnIndex(SMS_BODY)));
                params.put(SMS_PATH, cursor.getString(cursor.getColumnIndex(SMS_PATH)));
                params.put(WORKER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(WORKER_ID))));
                params.put(CATEGORY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))));
                params.put(SMS_TYPE, String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_TYPE))));
                params.put(FETCH_STATUS, String.valueOf(status));


                wordList.add(params);
            } while (cursor.moveToNext());
        }
        //database.close();
        return wordList;
    }
    public String composeJSONfromSQLite(){
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        int status = 0;
        String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_SMS + " WHERE " + FETCH_STATUS + " = '" + status + "' " +
                " AND "+SMS_PATH+" = '"+undefined+"' ";
        SQLiteDatabase database = new DBHelper(context).getReadableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> params = new HashMap<String, String>();
                params.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_ID))));
                params.put(SMS_DATE, cursor.getString(cursor.getColumnIndex(SMS_DATE)));
                params.put(SMS_TIME, cursor.getString(cursor.getColumnIndex(SMS_TIME)));
                params.put(PHONE, cursor.getString(cursor.getColumnIndex(PHONE)));
                params.put(SMS_BODY, cursor.getString(cursor.getColumnIndex(SMS_BODY)));
                params.put(SMS_PATH, cursor.getString(cursor.getColumnIndex(SMS_PATH)));
                params.put(WORKER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(WORKER_ID))));
                params.put(CATEGORY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))));
                params.put(SMS_TYPE, String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_TYPE))));
                params.put(FETCH_STATUS, String.valueOf(status));
                wordList.add(params);
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
            String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_SMS+ " WHERE " + FETCH_STATUS + " = '" + status + "'" +
                    " AND "+SMS_PATH+" = '"+undefined+"' ";
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
     * @param type
     */
    public void updateSyncStatus(int id, int status, int type, String phone, String message, String doctor_id ){
        SQLiteDatabase database = null;
        try {
            database = new DBHelper(context).getWritableDatabase();
            String updateQuery = "UPDATE " + Constants.config.TABLE_SMS + " SET " + FETCH_STATUS + " = '" + status + "' where " + SMS_ID + "='" + id + "'  ";
            Log.d("query", updateQuery);
            database.execSQL(updateQuery);

            ///// TODO: 10/24/17  send notifications here...!!!.
            if (type == 1){
                String names = new User_Details(context).getName();
                String img_url = new Image_Operations(context).image( new CurrentUser(context).current());
                new SendNotification(context).sendSinglePush_Doctor(names+":users:"+phone,message,img_url, String.valueOf(doctor_id));

            }else {
                String names = new Doctor_Details(context).getTitle()+" "+new Doctor_Details(context).getFName()+" "
                        +new Doctor_Details(context).getLName();
                String img_url = new Image_Operations(context).image( new CurrentDoctor(context).current());
                new SendNotification(context).sendSinglePush_Patient(names+":doctors:"+doctor_id,message,img_url,phone);
            }
            //// TODO: 10/24/17   Echo the respose here...!!!
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
    public void send(final String date, final String time, final String phone, final String body, final String path, final int doctor_id, final int category_id, final int type){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+SAVE_SMS_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        int status = 0;
                        if (response.equals("Success")){
                            status = 1;

                            if (type == 1){
                                String names = new User_Details(context).getName();
                                String img_url = new Image_Operations(context).image( new CurrentUser(context).current());
                                new SendNotification(context).sendSinglePush_Doctor(names+":users:"+phone,body,img_url, String.valueOf(doctor_id));

                            }else {
                                String names = new Doctor_Details(context).getTitle()+" "+new Doctor_Details(context).getFName()+" "
                                        +new Doctor_Details(context).getLName();
                                String img_url = new Image_Operations(context).image( new CurrentDoctor(context).current());
                                new SendNotification(context).sendSinglePush_Patient(names+":doctors:"+doctor_id,body,img_url,phone);
                            }
                        }
                        save(date,time,phone,body,path,doctor_id,category_id,type,status);
                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        try {
                            Log.e(TAG, volleyError.getMessage());
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                int status = 1;
                Map<String, String> params = new Hashtable<String, String>();
                //Adding parameters
                params.put(WORKER_ID, String.valueOf(doctor_id));
                params.put(CATEGORY_ID, String.valueOf(category_id));
                params.put(PHONE, phone);
                params.put(SMS_BODY, body);
                params.put(SMS_DATE, date);
                params.put(SMS_TIME, time);
                params.put(SMS_TYPE, String.valueOf(type));
                params.put(SMS_STATUS, String.valueOf(status));
                //returning parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

}
