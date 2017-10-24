package server_connections;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.example.john.mobicare_uganda.dbsyncing.Calls_sync;
import com.example.john.mobicare_uganda.dbsyncing.Messages_sync;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

import cz.msebera.android.httpclient.Header;

import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.OPERATION_CALL;
import static connectivity.Constants.config.OPERATION_SMS;

/**
 * Created by john on 10/14/17.
 */

public class DBController {
    private static final String TAG = "DBController";

    public static void syncCalls(final String url, final String operations, String show, final Context context){
        Log.e(TAG,"******************************** "+url);
        Log.e(TAG,"Syncing started for: "+operations);
        final ProgressDialog prgDialog = new ProgressDialog(context);
        prgDialog.setMessage("Synching SQLite Data with Remote Server. \n Please wait...");
        prgDialog.setCancelable(false);
        //Create AsycHttpClient object
        AsyncHttpClient client = new AsyncHttpClient();
        RequestParams params = new RequestParams();
        int db_count = 0;
        ArrayList<HashMap<String, String>> userList = new ArrayList<HashMap<String, String>>();
        String json_data = "";
        if (operations.equals(OPERATION_CALL)){
            userList = new Calls_sync(context). getAllUsers();
            db_count = new Calls_sync(context).dbSyncCount();
            json_data = new Calls_sync(context). composeJSONfromSQLite();
        }else if (operations.equals(OPERATION_SMS)){
            userList = new Messages_sync(context). getAllUsers();
            db_count = new Messages_sync(context).dbSyncCount();
            json_data = new Messages_sync(context). composeJSONfromSQLite();
        }
        if(userList.size()!=0){
            if(db_count != 0){
                if (!show.equals("")){
                    prgDialog.show();
                }
                params.put("dataJSON",json_data);
                client.post(HOST_URL+url,params ,new AsyncHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {

                        try {
                            String response = new String(responseBody, "UTF-8");
                            prgDialog.hide();
                            try {
                                Log.e(TAG,response);
                                JSONArray arr = new JSONArray(response);
                                System.out.println(arr.length());
                                for(int i=0; i<arr.length();i++){
                                    JSONObject obj = (JSONObject)arr.get(i);
                                    System.out.println(obj.get("id"));

                                    if (operations.equals(OPERATION_CALL)){
                                        new Calls_sync(context).updateSyncStatus(Integer.parseInt(obj.get("id").toString()),Integer.parseInt(obj.get("status").toString()));
                                    }else if (operations.equals(OPERATION_SMS)){
                                        new Messages_sync(context).updateSyncStatus(Integer.parseInt(obj.get("id").toString()),Integer.parseInt(obj.get("status").toString()));
                                    }
                                }
                                //Toast.makeText(getApplicationContext(), "DB Sync completed!", Toast.LENGTH_LONG).show();
                            } catch (JSONException e) {
                                // TODO Auto-generated catch block
                                //Toast.makeText(getApplicationContext(), "Error Occured [Server's JSON response might be invalid]!", Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        if(statusCode == 404){
                            Log.e("Error ", "Error code "+statusCode+" \t "+url);
                            //Toast.makeText(getApplicationContext(), "Requested resource not found", Toast.LENGTH_LONG).show();
                        }else if(statusCode == 500){
                            Log.e("Error ", "Error code "+statusCode+" \t"+url);
                            //Toast.makeText(getApplicationContext(), "Something went wrong at server end", Toast.LENGTH_LONG).show();
                        }else{
                            Log.e("Error ", "Error code "+statusCode+" \t "+url);
                        }
                    }
                });
            }else{
                Log.e("No Sync data", "Empty data to be sync "+url);
            }
        }else{
            Log.e("Empty", "Empty data to be sync "+url);
        }
    }

}
