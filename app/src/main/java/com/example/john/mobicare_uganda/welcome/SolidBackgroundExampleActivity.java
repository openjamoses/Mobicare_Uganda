package com.example.john.mobicare_uganda.welcome;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import com.codemybrainsout.onboarder.AhoyOnboarderActivity;
import com.codemybrainsout.onboarder.AhoyOnboarderCard;
import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.authentications.Login_Activity;
import com.example.john.mobicare_uganda.authentications.Password_Verifications;
import com.example.john.mobicare_uganda.firebase_collections.Config;
import com.example.john.mobicare_uganda.firebase_collections.DeviceToken;
import com.example.john.mobicare_uganda.firebase_collections.NotificationUtils;
import com.example.john.mobicare_uganda.firebase_collections.util.ProcessingService;
import com.example.john.mobicare_uganda.views.MemoryUsage;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import connectivity.Constants;
import connectivity.DBHelper;
import connectivity.SessionManager;
import server_connections.Doctor_Operations;
import server_connections.Fetch;
import server_connections.Patients_operations;
import services.Check_Connections;
import services.Server_Login;
import services.Server_Service;
import simcard_details.Phone_Serial;
import users.Doctor_Details;
import users.Signup_Users;
import users.User_Type;

import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.CALL_PHONE;
import static android.Manifest.permission.CAMERA;
import static android.Manifest.permission.READ_CONTACTS;
import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.READ_PHONE_STATE;
import static android.Manifest.permission.RECEIVE_SMS;
import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_CALL_LOG;
import static android.Manifest.permission.WRITE_CONTACTS;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static connectivity.Constants.config.DISTRICT_URL;
import static connectivity.Constants.config.OPERATION_DISTRICT;


public class SolidBackgroundExampleActivity extends AhoyOnboarderActivity {

    private Context context = this;
    private final static String TAG = "SolidBackground";
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    ////private TextView txtRegId, txtMessage;
    private EditText editTextEmail,firstText,lastText,phoneText;
    private ProgressDialog progressDialog;
    public static final int REQUEST_ID_MULTIPLE_PERMISSIONS = 1;
    private static int SPLASH_TIME_OUT = 2000;
    public static final int RequestPermissionCode = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /**
        List<Integer> colorList = new ArrayList<>();
        colorList.add(R.color.colorPrimary);
        colorList.add(R.color.solid_one);
        colorList.add(R.color.solid_two);
        colorList.add(R.color.colorAccent);

        setColorBackground(colorList);
         **/
//      Typeface face = Typeface.createFromAsset(getAssets(), "fonts/Roboto-Light.ttf");
//      setFont(face);

        MemoryUsage.memory(context);
        new Server_Login(context).startServer();
        startService();
        if(stopService(new Intent(context, Server_Service.class)) == false){
            Log.e(TAG,"Service has just been started!");
            startService(new Intent(context, Server_Service.class));
        }else {
            Log.e(TAG,"Service is already running!");
        }

        if(checkAndRequestPermissions()) {
            Log.e(TAG, "All permissions has been granted!");
        }else {
            //requestPermission();
            Log.e(TAG, "Some permissions has not been granted!");
        }

        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                // startService(new Intent(getApplicationContext(), MyFirebaseInstanceIDService.class));
                //startService(new Intent(getApplicationContext(), MyFirebaseMessagingService.class));

                // checking for type intent filter
                if (intent.getAction().equals(Config.REGISTRATION_COMPLETE)) {
                    // gcm successfully registered
                    // now subscribe to `global` topic to receive app wide notifications
                    FirebaseMessaging.getInstance().subscribeToTopic(Config.TOPIC_GLOBAL);
                    displayFirebaseRegId();
                } else if (intent.getAction().equals(Config.PUSH_NOTIFICATION)) {
                    // new push notification is received
                    String message = intent.getStringExtra("message");

                    Toast.makeText(getApplicationContext(), "Push notification: " + message, Toast.LENGTH_LONG).show();
                    //txtMessage.setText(message);
                }
            }
        };
        displayFirebaseRegId();
        checkSession();
    }

    private void checkSession(){
        AhoyOnboarderCard ahoyOnboarderCard0 = new AhoyOnboarderCard("MobiCare Uganda", " \"Medical Consultations Made Easy!. \" ",R.drawable.icon);
        AhoyOnboarderCard ahoyOnboarderCard1 = new AhoyOnboarderCard("Our Services", "Phone calls, SMS and Realtime Chatts, and Appointment Schedulling to the Health worker of Your choice...", android.R.drawable.ic_menu_my_calendar);
        //AhoyOnboarderCard ahoyOnboarderCard2 = new AhoyOnboarderCard("Messages", "With Mobicare application you can send sms, realtime messages including audio or video recording as well as Camera caption", android.R.drawable.sym_action_chat);
        // AhoyOnboarderCard ahoyOnboarderCard3 = new AhoyOnboarderCard("Appointments", "You have a choice to make an appointment to the health worker of your choice.", android.R.drawable.ic_lock_idle_alarm);

        ahoyOnboarderCard0.setBackgroundColor(R.color.black_transparent);
        ahoyOnboarderCard1.setBackgroundColor(R.color.black_transparent);
        //ahoyOnboarderCard2.setBackgroundColor(R.color.black_transparent);
        //ahoyOnboarderCard3.setBackgroundColor(R.color.black_transparent);

        List<AhoyOnboarderCard> pages = new ArrayList<>();

        pages.add(ahoyOnboarderCard0);


        SessionManager session = new SessionManager(getApplicationContext());
        if(session.isLoggedIn()){

            //pages.add(ahoyOnboarderCard1);
            // pages.add(ahoyOnboarderCard2);
            //pages.add(ahoyOnboarderCard3);
            ahoyOnboarderCard0.setBackgroundColor(R.color.grey_200);

            for (AhoyOnboarderCard page : pages) {
                page.setTitleColor(R.color.black);
                page.setDescriptionColor(R.color.grey_600);
            }

            setFinishButtonTitle("Get Started!");
            showNavigationControls(true);
            setGradientBackground();
            setOnboardPages(pages);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    String session_type = new User_Type(context).getSession();
                    if (session_type != null){
                        if(session_type.equals("user")){
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.putExtra("welcome","welcome");
                            startActivity(intent);
                            finish();
                        }else{

                            String phone = new Doctor_Details(context).getContact();
                            new SessionManager(context).loginDoctor(phone);
                            new SessionManager(context).loginUser("");
                            Intent intent = new Intent(context, Login_Activity.class);
                            intent.putExtra("phone",phone);
                            startActivity(intent);
                            Log.e("SolidBackground",phone);
                            finish();
                        }
                    }

                }},1000);

        }else{
            SQLiteDatabase db = new DBHelper(context).getWritableDatabase();
            new Fetch(context).fetchALL(DISTRICT_URL,OPERATION_DISTRICT,db);
            pages.add(ahoyOnboarderCard1);
            // pages.add(ahoyOnboarderCard2);
            //pages.add(ahoyOnboarderCard3);

            for (AhoyOnboarderCard page : pages) {
                page.setTitleColor(R.color.black);
                page.setDescriptionColor(R.color.grey_600);
            }

            String phone_id = new Phone_Serial(context).details();
            Cursor cursor = new Patients_operations(context).getDetails(phone_id);
            String phone_number = "",password = "";
            try {
                if (cursor != null) {

                    if (cursor.moveToFirst()) {
                        do {
                            phone_number = cursor.getString(cursor.getColumnIndex(Constants.config.CONTACT));
                            password = cursor.getString(cursor.getColumnIndex(Constants.config.USER_PASSWORD));
                        } while (cursor.moveToNext());
                        setFinishButtonTitle("Login with: " + phone_number);
                    } else {
                        setFinishButtonTitle("MobiCare Signup");
                    }
                }
            }catch (Exception e){
                e.printStackTrace();
            }finally {
                if (cursor != null)
                    cursor.close();
            }

            showNavigationControls(true);
            setGradientBackground();
            setOnboardPages(pages);
        }
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

    @Override
    protected void onPause() {
        super.onPause();
        if (!new Check_Connections(context).checkInternetConenction()) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        }
    }

    public void startService(){
        Intent intent = new Intent(context, ProcessingService.class);
        startService(intent);
    }

    @Override
    public void onFinishButtonPressed() {
        String phone_id = new Phone_Serial(context).details();
        Cursor cursor = new Patients_operations(context).getDetails(phone_id);
        String phone_number = "",password = "";
        if (cursor != null){
            if (cursor.moveToFirst()){
                do {
                    phone_number = cursor.getString(cursor.getColumnIndex(Constants.config.CONTACT));
                    password = cursor.getString(cursor.getColumnIndex(Constants.config.USER_PASSWORD));
                }while (cursor.moveToNext());


                Intent intent = new Intent(context, Password_Verifications.class);
                intent.putExtra("phone",phone_number);
                intent.putExtra("password",password);
                intent.putExtra("type","user");
                intent.putExtra("registration", "none");
                startActivity(intent);
                //finish();
            }else{
                Intent intent = new Intent(context, Signup_Users.class);
                startActivity(intent);
                //finish();
            }
        }



    }


    private  boolean checkAndRequestPermissions() {
        int camerapermission = ContextCompat.checkSelfPermission(this, CAMERA);
        int writepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int permissionLocation = ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionRecordAudio = ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO);

        int readsmsmission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS);
        int writecallogpermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_CALL_LOG);

        int callphonepermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE);
        int phonestatepermission = ContextCompat.checkSelfPermission(this, READ_PHONE_STATE);

        List<String> listPermissionsNeeded = new ArrayList<>();
        if (camerapermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(CAMERA);
        }
        if (writepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (permissionLocation != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (permissionRecordAudio != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.RECORD_AUDIO);
        }

        if (readsmsmission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.READ_SMS);
        }
        if (writecallogpermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.WRITE_CALL_LOG);
        }
        if (callphonepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.CALL_PHONE);
        }
        if (phonestatepermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(READ_PHONE_STATE);
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toArray(new String[listPermissionsNeeded.size()]), REQUEST_ID_MULTIPLE_PERMISSIONS);
            return false;
        }
        return true;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        Log.d(TAG, "Permission callback called-------");
        switch (requestCode) {
            case REQUEST_ID_MULTIPLE_PERMISSIONS: {

                Map<String, Integer> perms = new HashMap<>();
                // Initialize the map with both permissions
                perms.put(CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.ACCESS_FINE_LOCATION, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.RECORD_AUDIO, PackageManager.PERMISSION_GRANTED);

                perms.put(Manifest.permission.READ_SMS, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_CALL_LOG, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.CALL_PHONE, PackageManager.PERMISSION_GRANTED);
                perms.put(READ_PHONE_STATE, PackageManager.PERMISSION_GRANTED);

                // Fill with actual results from user
                if (grantResults.length > 0) {
                    for (int i = 0; i < permissions.length; i++)
                        perms.put(permissions[i], grantResults[i]);
                    // Check for both permissions
                    if (perms.get(CAMERA) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.READ_SMS) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.WRITE_CALL_LOG) == PackageManager.PERMISSION_GRANTED
                            && perms.get(Manifest.permission.CALL_PHONE) == PackageManager.PERMISSION_GRANTED
                            && perms.get(READ_PHONE_STATE) == PackageManager.PERMISSION_GRANTED

                            && perms.get(Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "All permissions services permission granted");
                        // process the normal flow
                        //Intent i = new Intent(MainActivity_Doctor.this, WelcomeActivity.class);
                        //startActivity(i);
                        //finish();
                        //else any one or both the permissions are not granted
                    } else {
                        Log.d(TAG, "Some permissions are not granted ask again ");
                        //permission is denied (this is the first time, when "never ask again" is not checked) so ask again explaining the usage of permission
//                        // shouldShowRequestPermissionRationale will return true
                        //show the dialog or snackbar saying its necessary and try again otherwise proceed with setup.
                        if (ActivityCompat.shouldShowRequestPermissionRationale(this, CAMERA)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)

                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_CALL_LOG)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)
                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.MODIFY_PHONE_STATE)

                                || ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.RECORD_AUDIO)) {
                            showDialogOK("Service Permissions are required for this app",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    checkAndRequestPermissions();
                                                    break;
                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    // proceed with logic by disabling the related features or quit the app.
                                                    finish();
                                                    break;
                                            }
                                        }
                                    });
                        }
                        //permission is denied (and never ask again is  checked)
                        //shouldShowRequestPermissionRationale will return false
                        else {
                            explain("You need to give some mandatory permissions to continue. Do you want to go to app settings?");
                            //                            //proceed with logic by disabling the related features or quit the app.
                        }
                    }
                }
            }
        }

    }

    private void showDialogOK(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", okListener)
                .create()
                .show();
    }
    private void explain(String msg){
        final android.support.v7.app.AlertDialog.Builder dialog = new android.support.v7.app.AlertDialog.Builder(this);
        dialog.setMessage(msg)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        //  permissionsclass.requestPermission(type,code);
                        startActivity(new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:com.exampledemo.parsaniahardik.marshmallowpermission")));
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                        finish();
                    }
                });
        dialog.show();
    }

    private void requestPermission() {

        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                Uri.parse("package:" + getPackageName()));
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);

        ActivityCompat.requestPermissions(SolidBackgroundExampleActivity.this, new String[]
                {
                        CAMERA,
                        READ_CONTACTS,
                        READ_PHONE_STATE,
                        WRITE_CALL_LOG,
                        ACCESS_FINE_LOCATION,
                        RECORD_AUDIO,
                        RECEIVE_SMS,
                        RECEIVE_SMS,
                        CALL_PHONE,
                        WRITE_EXTERNAL_STORAGE,
                        READ_EXTERNAL_STORAGE,
                        WRITE_CONTACTS

                }, RequestPermissionCode);

    }
}
