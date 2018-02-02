package com.example.john.mobicare_uganda;

/**
 * Created by john on 11/29/17.
 */

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.mobicare_uganda.authentications.PhoneVerification;
import com.example.john.mobicare_uganda.firebase_collections.Config;
import com.example.john.mobicare_uganda.firebase_collections.DeviceToken;
import com.example.john.mobicare_uganda.firebase_collections.NotificationUtils;
import com.example.john.mobicare_uganda.firebase_collections.util.ProcessingService;
import com.example.john.mobicare_uganda.firebase_collections.util.UpdateStatus;
import com.example.john.mobicare_uganda.fragments.Dashboard_Fragment;
import com.example.john.mobicare_uganda.fragments.Doctor_Fragment;
import com.example.john.mobicare_uganda.fragments.Menu_Fragment;
import com.example.john.mobicare_uganda.views.MemoryUsage;
import com.example.john.mobicare_uganda.views.Profile_P;
import com.example.john.mobicare_uganda.welcome.SolidBackgroundExampleActivity;
import com.example.john.mobicare_uganda.welcome.Welcome_Activity;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.SessionManager;
import server_connections.Appointment_Operations;
import server_connections.Block_Operations;
import server_connections.Fetch;
import server_connections.Image_Operations;
import server_connections.Patients_operations;
import server_connections.Relation_Operations;
import server_connections.Reply_Operations;
import server_connections.SMS_Operations;
import server_connections.Schedule_Operations;
import services.Check_Connections;
import services.Server_Service;
import users.CurrentUser;
import users.Doctor_Details;
import users.User_Details;
import users.User_Type;

import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.OPERATION_APPOINTMENT;
import static connectivity.Constants.config.OPERATION_BLOCK;
import static connectivity.Constants.config.OPERATION_FILE;
import static connectivity.Constants.config.OPERATION_PATIENTS;
import static connectivity.Constants.config.OPERATION_RELATION;
import static connectivity.Constants.config.OPERATION_REPLY;
import static connectivity.Constants.config.OPERATION_SCHEDULE;
import static connectivity.Constants.config.OPERATION_SMS;
import static connectivity.Constants.config.SLECTED_PATIENT_URL;
import static connectivity.Constants.config.SMS_PATIENT_URL;
import static connectivity.SessionManager.PREF_NAME;
import static connectivity.SessionManager.PRIVATE_MODE;

public class MainActivity extends AppCompatActivity {
    /** Called when the activity is first created. */

    private Context context = this;
    private TextView mTextMessage;
    private final static String TAG = "MainActivity_Doctor";
    private ProgressDialog progressDialog;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private Toolbar toolbar;
    String session_name ;
    SharedPreferences pref;
    // Editor for Shared preferences
    SharedPreferences.Editor editor;
    SessionManager session;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);
        Intent intentss = new Intent(context, ProcessingService.class);
        startService(intentss);

        String welcome = getIntent().getStringExtra("welcome");
        creatUserSession();
        //sendToken();

        String phone = getIntent().getStringExtra("phone");

        ImageView toolbar_image = (ImageView) findViewById(R.id.toolbar_image);
        new UpdateStatus(context).user_status_Image(phone, toolbar_image);

        mTextMessage = (TextView) findViewById(R.id.toolbar_subtitle);
        //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");
                    //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    //txtMessage.setText(message);
                }
            }
        };




        /**

        MemoryUsage.memory(context);

        try {



            Log.e(TAG,"Patient_Page!");




            if (welcome == null) {
                finish();
            }



            if (!session_name.equals("user")) {
                creatUserSession();
            }

            if (session_name.equals("user")) {
                sendToken();


                SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
                new Fetch(context).selected(SMS_PATIENT_URL, OPERATION_SMS, db, OPERATION_PATIENTS);
                //new Fetch(context).selected(PATIENT_REPLY_URL, OPERATION_REPLY, db, OPERATION_PATIENTS);
                //new Fetch(context).selected(FILE_PATIENT_URL, OPERATION_FILE, db, OPERATION_PATIENTS);
                String phone = new User_Details(context).getContact();


                Log.e(TAG, "*****************************");
                Log.e(TAG, phone);
                ImageView toolbar_image = (ImageView) findViewById(R.id.toolbar_image);
                new UpdateStatus(context).user_status_Image(phone, toolbar_image);
            }
            fetch_user();

            mTextMessage = (TextView) findViewById(R.id.toolbar_subtitle);
            //BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
            //navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            mRegistrationBroadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {
                    // checking for type intent filter
                    if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                        // gcm successfully registered
                        // now subscribe to `global` topic to receive app wide notifications
                        FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                        displayFirebaseRegId();
                    } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                        // new push notification is received
                        String message = intent.getStringExtra("message");
                        //Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                        //txtMessage.setText(message);
                    }
                }
            };


            displayFirebaseRegId();

        }catch (Exception e){
            e.printStackTrace();
        }



        int[] icons = {R.drawable.ic_dashboard_black_24dp,
                R.drawable.search_normal,
                R.drawable.home_normal
        };
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_tab_content);


        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        tabLayout.getTabAt(0).select();
         ***/
    }


    private void creatUserSession(){
        try {
            Cursor cursor = null;
            try {
                String phone = new CurrentUser(context).current();
                cursor = new Patients_operations(context).selectPatient(phone);
                Intent intentss = new Intent(context, ProcessingService.class);
                startService(intentss);
                SessionManager session = new SessionManager(getApplicationContext());
                if (cursor.moveToFirst()) {
                    if (stopService(new Intent(context, Server_Service.class)) == false) {
                        Log.e(TAG, "Service has just been started!");
                        startService(new Intent(context, Server_Service.class));
                    } else {
                        Log.e(TAG, "Service is already running!");
                    }
                    do {

                        int useID = cursor.getInt(cursor.getColumnIndex(Constants.config.U_ID));
                        String fname = cursor.getString(cursor.getColumnIndex(Constants.config.FIRST_NAME));
                        String lname = cursor.getString(cursor.getColumnIndex(Constants.config.LAST_NAME));
                        String contact = cursor.getString(cursor.getColumnIndex(Constants.config.CONTACT));
                        int district = cursor.getInt(cursor.getColumnIndex(Constants.config.DISTRICT_ID));

                        String gender = cursor.getString(cursor.getColumnIndex(Constants.config.GENDER));
                        String dob = cursor.getString(cursor.getColumnIndex(Constants.config.DOB));
                        String phone_id = cursor.getString(cursor.getColumnIndex(Constants.config.PHONE_SERIAL));
                        String reg_date = cursor.getString(cursor.getColumnIndex(Constants.config.REG_DATE));
                        int status = cursor.getInt(cursor.getColumnIndex(Constants.config.USER_STATUS));
                        ///Creating Session..
                        session.createLoginSession(useID, fname, lname, contact, String.valueOf(district), gender, dob, phone_id, reg_date, String.valueOf(status));
                    } while (cursor.moveToNext());
                    sendToken();
                    //todo other things follows from here...!!!!!

                    SQLiteDatabase db = DBHelper.getHelper(context).getWritableDatabase();
                    new Fetch(context).selected(SMS_PATIENT_URL, OPERATION_SMS, db, OPERATION_PATIENTS);

                    displayFirebaseRegId();

                    int[] icons = {R.drawable.ic_dashboard_black_24dp,
                            R.drawable.search_normal,
                            R.drawable.home_normal
                    };
                    TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                    ViewPager viewPager = (ViewPager) findViewById(R.id.main_tab_content);


                    setupViewPager(viewPager);


                    tabLayout.setupWithViewPager(viewPager);

                    for (int i = 0; i < icons.length-1; i++) {
                        tabLayout.getTabAt(i).setIcon(icons[i]);
                    }
                    tabLayout.getTabAt(0).select();



                }else{
                    createUserDirect(phone,session);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                //cursor.close();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void createUserDirect(String phone,SessionManager session) {
        try{
            ProgressDialog progressDialog = new ProgressDialog(context);
            progressDialog.setMessage("Creating Session...");
            progressDialog.show();
            selected(phone,session,progressDialog);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void selected(final String phone,final SessionManager session, final ProgressDialog progressDialog){
        final SQLiteDatabase db = DBHelper.getHelper(context).getWritableDatabase();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+SLECTED_PATIENT_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("Result",response);
                        try{
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                int useID = (int) jsonObject.getLong(Constants.config.USER_ID);
                                String fname = jsonObject.getString(Constants.config.FIRST_NAME);
                                String lname = jsonObject.getString(Constants.config.LAST_NAME);
                                String contact = jsonObject.getString(Constants.config.CONTACT);
                                int district = (int) jsonObject.getLong(Constants.config.DISTRICT_ID);

                                String gender = jsonObject.getString(Constants.config.GENDER);
                                String dob = jsonObject.getString(Constants.config.DOB);
                                String phone_id = jsonObject.getString(Constants.config.PHONE_SERIAL);
                                String reg_date = jsonObject.getString(Constants.config.REG_DATE);
                                String password = jsonObject.getString(Constants.config.USER_PASSWORD);
                                int status = (int) jsonObject.getLong(Constants.config.USER_STATUS);

                                //todo: Creating sessions here...!!!
                                session.createLoginSession(useID, fname, lname, contact, String.valueOf(district), gender, dob, phone_id, reg_date, String.valueOf(status));

                                //db.beginTransactionNonExclusive();
                                ContentValues contentValues = new ContentValues();
                                contentValues.put(Constants.config.USER_ID, jsonObject.getLong(Constants.config.USER_ID));
                                contentValues.put(Constants.config.FIRST_NAME, jsonObject.getString(Constants.config.FIRST_NAME));
                                contentValues.put(Constants.config.LAST_NAME, jsonObject.getString(Constants.config.LAST_NAME));
                                contentValues.put(Constants.config.GENDER, jsonObject.getString(Constants.config.GENDER));
                                contentValues.put(Constants.config.USER_STATUS, jsonObject.getLong(Constants.config.USER_STATUS));

                                contentValues.put(Constants.config.DISTRICT_ID, jsonObject.getLong(Constants.config.DISTRICT_ID));
                                contentValues.put(Constants.config.REG_DATE, jsonObject.getString(Constants.config.REG_DATE));
                                contentValues.put(Constants.config.CONTACT, jsonObject.getString(Constants.config.CONTACT));

                                contentValues.put(Constants.config.DOB, jsonObject.getString(Constants.config.DOB));
                                contentValues.put(Constants.config.PHONE_SERIAL, jsonObject.getString(Constants.config.PHONE_SERIAL));
                                contentValues.put(Constants.config.USER_PASSWORD, jsonObject.getString(Constants.config.USER_PASSWORD));

                                Log.e(TAG, jsonObject.getString(Constants.config.FIRST_NAME));

                                db.insert(Constants.config.TABLE_PATIENTS, null, contentValues);
                                //db.setTransactionSuccessful();
                            }
                            if (progressDialog != null){
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }


                            SQLiteDatabase db = DBHelper.getHelper(context).getWritableDatabase();
                            new Fetch(context).selected(SMS_PATIENT_URL, OPERATION_SMS, db, OPERATION_PATIENTS);

                            displayFirebaseRegId();

                            int[] icons = {R.drawable.ic_dashboard_black_24dp,
                                    R.drawable.search_normal,
                                    R.drawable.home_normal
                            };
                            TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
                            ViewPager viewPager = (ViewPager) findViewById(R.id.main_tab_content);


                            setupViewPager(viewPager);


                            tabLayout.setupWithViewPager(viewPager);

                            for (int i = 0; i < icons.length-1; i++) {
                                tabLayout.getTabAt(i).setIcon(icons[i]);
                            }
                            tabLayout.getTabAt(0).select();


                        }catch (Exception e){
                            e.printStackTrace();
                        }finally {
                            try{
                                //db.endTransaction();
                            }catch (Exception e){
                                e.printStackTrace();
                            }

                        }
                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.e(TAG, volleyError.getMessage());
                        try {
                            Toast toast = Toast.makeText(context, "Check our internet Connections", Toast.LENGTH_LONG);
                            View view = toast.getView();
                            view.setBackgroundResource(R.drawable.round);
                            TextView text = (TextView) view.findViewById(android.R.id.message);
                            //toast.show();
                            //Log.e(TAG, volleyError.getMessage());
                            if (progressDialog != null){
                                if (progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                            }

                        }catch (Exception e){
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new Hashtable<String, String>();
                params.put("phone", phone);
                //Adding parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }



    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(getApplicationContext(), Welcome_Activity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        //startActivity(intent);
        finish();
    }

    private void displayFirebaseRegId() {
        //SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        String regId = new DeviceToken(context).token();
        if(regId == null) {
            regId = FirebaseInstanceId.getInstance().getToken();
        }
        Log.e(TAG, "Firebase reg id: " + regId);

        if (!TextUtils.isEmpty(regId))
            //txtRegId.setText("Firebase Reg Id: " + regId);
            Log.e(TAG,"Firebase Reg Id: " + regId);
        else
            //txtRegId.setText("Firebase Reg Id is not received yet!");
            Log.e(TAG,"Firebase Reg Id is not received yet!");
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            // register GCM registration complete receiver
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.REGISTRATION_COMPLETE));
            // register new push message receiver
            // by doing this, the activity will be notified each time a new message arrives
            LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                    new IntentFilter(Config.PUSH_NOTIFICATION));

            // clear the notification area when the app is opened
            NotificationUtils.clearNotifications(getApplicationContext());
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        try {
            if (!new Check_Connections(context).checkInternetConenction()) {
                LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
            } else {
                // register GCM registration complete receiver
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(Config.REGISTRATION_COMPLETE));

                // register new push message receiver
                // by doing this, the activity will be notified each time a new message arrives
                LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                        new IntentFilter(Config.PUSH_NOTIFICATION));

                // clear the notification area when the app is opened
                NotificationUtils.clearNotifications(getApplicationContext());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        super.onPause();
    }

    public void sendToken(){
        try {
            String token = new DeviceToken(context).token();
            String phone = new User_Details(context).getContact();
            if (token != null) {
                new SendTokens(context).sendTokenToServer_Patient(token, phone);
            }

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.insertNewFragment(new Dashboard_Fragment());
        adapter.insertNewFragment(new Doctor_Fragment());
        //adapter.insertNewFragment(new Menu_Fragment());
       // adapter.insertNewFragment(new SearchFragment());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void insertNewFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        try {
            switch (id) {
                case android.R.id.home:
                    finish();
                    return true;
                case R.id.action_share:
                    shareApp(context);
                    return true;
                case R.id.action_logout:
                    // todo: goto back activity from here
                    SessionManager session = new SessionManager(getApplicationContext());
                    session.logoutUser();
                    Intent intents = new Intent(getApplicationContext(), SolidBackgroundExampleActivity.class);
                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intents);
                    finish();
                    //Intent intentss = new Intent(context, ProcessingService.class);
                    //stopService(intentss);
                    return true;
                case R.id.action_profile:
                    Intent intent = new Intent(context, Profile_P.class);
                    startActivity(intent);
                case R.id.action_switch:
                    Intent intentsx = new Intent(context, PhoneVerification.class);
                    startActivity(intentsx);
                    //finish();
                case R.id.action_exit:
                    finish();
                default:
                    return super.onOptionsItemSelected(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);

    }
    private  void shareApp(Context context) {

        String names = new User_Details(context).getName();
        int applicationNameId = context.getApplicationInfo().labelRes;
        final String appPackageName = context.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(applicationNameId));
        String text = names+" invites you to install Mobicare application for Medical Consultations: ";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        startActivity(Intent.createChooser(i, "Share link:"));
    }

}