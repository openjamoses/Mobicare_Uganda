package com.example.john.mobicare_uganda.dbsyncing;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.ArrayList;
import java.util.HashMap;

import connectivity.Constants;
import connectivity.DBHelper;

import static connectivity.Constants.config.CALL_DATE;
import static connectivity.Constants.config.CALL_DURATION;
import static connectivity.Constants.config.CALL_TIME;
import static connectivity.Constants.config.CALL_TYPE;
import static connectivity.Constants.config.CATEGORY_ID;
import static connectivity.Constants.config.FETCH_STATUS;
import static connectivity.Constants.config.PHONE;
import static connectivity.Constants.config.SMS_BODY;
import static connectivity.Constants.config.SMS_DATE;
import static connectivity.Constants.config.SMS_ID;
import static connectivity.Constants.config.SMS_PATH;
import static connectivity.Constants.config.SMS_TIME;
import static connectivity.Constants.config.SMS_TYPE;
import static connectivity.Constants.config.WORKER_ID;

/**
 * Created by john on 10/15/17.
 */

public class Messages_sync {
    private Context context;
    private static final String TAG = "Calls_sync";
    public Messages_sync(Context context){
        this.context = context;
    }

    public ArrayList<HashMap<String, String>> getAllUsers() {
        ArrayList<HashMap<String, String>> wordList;
        wordList = new ArrayList<HashMap<String, String>>();
        String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_SMS ;

        SQLiteDatabase database = new DBHelper(context).getReadableDatabase();
        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_ID))));
                map.put(PHONE, cursor.getString(cursor.getColumnIndex(PHONE)));
                map.put(WORKER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(WORKER_ID))));
                map.put(SMS_DATE, cursor.getString(cursor.getColumnIndex(SMS_DATE)));
                map.put(SMS_TIME, cursor.getString(cursor.getColumnIndex(SMS_TIME)));
                map.put(SMS_BODY, cursor.getString(cursor.getColumnIndex(SMS_BODY)));
                map.put(CATEGORY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))));
                map.put(SMS_TYPE, String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_TYPE))));
                map.put(SMS_PATH, cursor.getString(cursor.getColumnIndex(SMS_PATH)));
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
        String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_SMS + " WHERE " + FETCH_STATUS + " = '" + status + "' ";
        SQLiteDatabase database = new DBHelper(context).getReadableDatabase();

        Cursor cursor = database.rawQuery(selectQuery, null);
        if (cursor.moveToFirst()) {
            do {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put("id", String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_ID))));
                map.put(PHONE, cursor.getString(cursor.getColumnIndex(PHONE)));
                map.put(WORKER_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(WORKER_ID))));
                map.put(SMS_DATE, cursor.getString(cursor.getColumnIndex(SMS_DATE)));
                map.put(SMS_TIME, cursor.getString(cursor.getColumnIndex(SMS_TIME)));
                map.put(SMS_BODY, cursor.getString(cursor.getColumnIndex(SMS_BODY)));
                map.put(CATEGORY_ID, String.valueOf(cursor.getInt(cursor.getColumnIndex(CATEGORY_ID))));
                map.put(SMS_TYPE, String.valueOf(cursor.getInt(cursor.getColumnIndex(SMS_TYPE))));
                map.put(SMS_PATH, cursor.getString(cursor.getColumnIndex(SMS_PATH)));
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
            String selectQuery = "SELECT  * FROM " + Constants.config.TABLE_SMS + " WHERE " + FETCH_STATUS + " = '" + status + "' ";
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
     *
     * @param id
     * @param status
     */
    public void updateSyncStatus(int id, int status){
        SQLiteDatabase database = null;
        try {
            database = new DBHelper(context).getWritableDatabase();
            String updateQuery = "UPDATE " + Constants.config.TABLE_SMS + " SET " + FETCH_STATUS + " = '" + status + "' where " + SMS_ID + "='" + id + "'  ";
            Log.d("query", updateQuery);
            database.execSQL(updateQuery);
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

}