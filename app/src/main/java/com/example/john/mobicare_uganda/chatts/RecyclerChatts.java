package com.example.john.mobicare_uganda.chatts;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.john.mobicare_uganda.PatientChatts;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.media.Directory;
import com.example.john.mobicare_uganda.messageme.models.Message;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import adapters.Chatt_List2;
import adapters.MyListAdapter;
import connectivity.Constants;
import connectivity.DBHelper;
import server_connections.Doctor_Operations;
import server_connections.Image_Operations;
import server_connections.SMS_Operations;
import server_connections.Updates;
import users.User_Details;

import static com.example.john.mobicare_uganda.firebase_collections.Config.APP_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.AUDIO_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.IMAGE_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.VIDEO_SUB_FOLDER;
import static connectivity.Constants.config.CHATT_UPDATEALL1_URL;
import static connectivity.Constants.config.HOST_URL;
import static connectivity.Constants.config.SMS_PATIENT_DOCTOR_URL;

/**
 * Created by john on 10/17/17.
 */

public class RecyclerChatts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private ListView listView;
    String phone_number;
    public Context context = this;
    List<String> list;
    ///String[] m_list = null;
    private static final String TAG = "Chatts";
    private LinearLayout progressing_view;
    private ImageButton imageButton_send;
    private EditText editText_message;
    private ProgressBar proogress_;
    private TextView progresstext;
    Bitmap bmp;
    byte[] b;
    String doctor_id;
    String category;
    private ImageView record_button;


    private MediaRecorder mediaRecorder;
    String voiceStoragePath;
    int count  = 0;
    static final String AB = "abcdefghijklmnopqrstuvwxyz";
    static Random rnd = new Random();

    MediaPlayer mediaPlayer;
    DataOutputStream output; // output stream to target file
    boolean isRecording; // indicates if sound is currently being captured

    public int counter;
    int MAX_DURATION=60000;
    private Timer timer = new Timer();
    private ImageView record_camera;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    private Uri fileUri; // file url to store image/video
    private MessagesAdapter mAdapter;
    private List<Messages> messagesList = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chatts_new);

        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        progressing_view = (LinearLayout) findViewById(R.id.progressbar_view);
        imageButton_send = (ImageButton) findViewById(R.id.imageButton_send);
        editText_message = (EditText) findViewById(R.id.editText_message);
        record_button = (ImageView) findViewById(R.id.recording_button);
        proogress_ = (ProgressBar) findViewById(R.id.progress);
        record_camera = (ImageView) findViewById(R.id.record_camera);

        phone_number = new User_Details(context).getContact();
        Bundle extras = getIntent().getExtras();
        b = extras.getByteArray("image");

        doctor_id = getIntent().getStringExtra("id");
        category = getIntent().getStringExtra("category");
        bmp = BitmapFactory.decodeByteArray(b, 0, b.length);

        Log.e(TAG,"DOCTOR: "+doctor_id+" CATEGORY: "+category+" PHONE: "+phone_number);
        imageButton_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessages();
            }
        });

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolBar);

        TextView textView = (TextView) findViewById(R.id.toolbar_title);
        textView.setText(getResources().getString(R.string.app_name));
        TextView textView2 = (TextView) findViewById(R.id.toolbar_subtitle);
        ImageView imageView = (ImageView) findViewById(R.id.toolbar_image);
        textView2.setVisibility(View.GONE);

        mAdapter = new MessagesAdapter(RecyclerChatts.this,messagesList);

        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);


        try {
            setSupportActionBar(toolbar);
            Cursor cursor = new Doctor_Operations(context).selected(doctor_id);
            if (cursor.moveToFirst()){
                do {
                    textView.setText(cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_FNAME))+" "
                            +cursor.getString(cursor.getColumnIndex(Constants.config.WORKER_LNAME)));

                    //textView2.setText(phone_number);

                }while (cursor.moveToNext());
            }
            //imageView.setImageBitmap(Convert.getclip(CompressBitmap.compress(bmp)));

            if (getSupportActionBar() != null){
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);

            }
        }catch (Exception e){
            e.printStackTrace();
        }



        record_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, AudioRecordingActivity.class);
                //startActivity(intent);
                showDialog();
            }
        });

        record_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Intent intent = new Intent(context, Camera_Main.class);
                //startActivity(intent);

                // capture picture
                captureImage();
            }
        });

        record_camera.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                recordVideo();
                return false;
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            //finish();
        }

        Directory.create();

        try {
            serverView();
        }catch (Exception e){
            e.printStackTrace();
        }

    }
    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Launching camera app to record video
     */
    private void recordVideo() {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }



    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        int type = 1;

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String message = "Image Capture From "+new User_Details(context).getName()+" , "+new User_Details(context).getContact();
                new Image_Operations(context).upload(fileUri.getPath(),doctor_id,phone_number,message,type);
            } else if (resultCode == RESULT_CANCELED) {
                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                String message = "Video Capture from "+new User_Details(context).getName()+" , "+new User_Details(context).getContact();
                new Image_Operations(context).upload(fileUri.getPath(),doctor_id,phone_number,message,type);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Toast.makeText(getApplicationContext(),
                        "User cancelled video recording", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to record video
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT)
                        .show();
            }
        }

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // new CallingNumber(c).clearCalls();

                progressing_view.setVisibility(View.GONE);
                Intent intent = new Intent(context, PatientChatts.class);
                intent.putExtra("image",b);
                intent.putExtra("id",doctor_id);
                intent.putExtra("category",category);
                startActivity(intent);
                finish();
            }},300);
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }
    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        String PATH = Environment.getExternalStorageDirectory()+ "/"+APP_FOLDER+"/";
        File folder = new File(PATH);
        if(!folder.exists()){
            folder.mkdir();//If there is no folder it will be created.
        }
        File file_video = new File(PATH + File.separator + VIDEO_SUB_FOLDER);
        File file_image = new File(PATH + File.separator + IMAGE_SUB_FOLDER);
        if(!file_video.exists()){
            file_video.mkdir();
        }
        if(!file_image.exists()){
            file_image.mkdir();
        }
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                IMAGE_SUB_FOLDER);
        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + IMAGE_SUB_FOLDER + " directory");
                return null;
            }
        }
        //String n = new Get_CurrentDateTime().getCurrentDate()+new Get_CurrentDateTime().getCurrentTime();
        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(file_image.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(file_video.getPath() + File.separator
                    + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }
        return mediaFile;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.message_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            //Intent intent = new Intent(context, Welcome_Activity.class);
            //startActivity(intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }

        if (item.getItemId() == R.id.action_switch){
            String message = "Hello am sending message from mobicare app! did u recieve it?";
            try {
                SmsManager sms = SmsManager.getDefault();
                sms.sendTextMessage("+256753955636", null,message , null, null);
                Log.e(TAG, "SMS SENT SUCCESS!");
            }catch (Exception e){
                e.printStackTrace();
                Log.e(TAG, "SMS NOT SENT!");
            }


            //new MyContentObserver();
        }
        if (item.getItemId() == R.id.action_refresh){
            Intent intent = new Intent(context, PatientChatts.class);
            intent.putExtra("image",b);
            intent.putExtra("id",doctor_id);
            intent.putExtra("category",category);
            startActivity(intent);
            finish();
        }
        if (item.getItemId() == R.id.action_clear){
            int status = 0;
            new Updates(context).clearChatts(CHATT_UPDATEALL1_URL,phone_number,doctor_id,status);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // new CallingNumber(c).clearCalls();
                    progressing_view.setVisibility(View.GONE);
                    Intent intent = new Intent(context, PatientChatts.class);
                    intent.putExtra("image",b);
                    intent.putExtra("id",doctor_id);
                    intent.putExtra("category",category);
                    startActivity(intent);
                    finish();
                }},300);
        }
        if (item.getItemId() == R.id.action_recover){
            int status = 1;
            new Updates(context).clearChatts(CHATT_UPDATEALL1_URL,phone_number,doctor_id,status);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // new CallingNumber(c).clearCalls();
                    progressing_view.setVisibility(View.GONE);
                    Intent intent = new Intent(context, PatientChatts.class);
                    intent.putExtra("image",b);
                    intent.putExtra("id",doctor_id);
                    intent.putExtra("category",category);
                    startActivity(intent);
                    finish();
                }},300);
        }
        return super.onOptionsItemSelected(item);
    }



    private void sendMessages() {
        //progressing_view.setVisibility(View.VISIBLE);
        String message = editText_message.getText().toString().trim();
        //final String doctor_id = new Doctor_Details(context).getDoctor_id();
        SQLiteDatabase db  = new DBHelper(context).getWritableDatabase();
        List<String> list = new ArrayList<>();
        if (!message.equals("")){
            int type = 1;
            progressing_view.setVisibility(View.VISIBLE);
            new SMS_Operations(context).updateMessages(doctor_id,category,message,phone_number,type);
            editText_message.setText("");
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    // new CallingNumber(c).clearCalls();
                    progressing_view.setVisibility(View.GONE);
                    Intent intent = new Intent(context, PatientChatts.class);
                    intent.putExtra("image",b);
                    intent.putExtra("id",doctor_id);
                    intent.putExtra("category",category);
                    startActivity(intent);
                    finish();
                }},300);
        }
    }
    public void sms(){
        ProgressDialog dialog = new ProgressDialog(context);
        dialog.setMessage("please wait...");
        new Background(context,listView).execute();
    }
    class Background extends AsyncTask<String,Void,String> {
        //ProgressDialog dialog;
        Context context;
        ListView listView;

        String[] finalBody;
        Long[] finalLg ;
        String[] finalUrl ;
        String[] finalDates ;
        Background(Context context,ListView listView){
            this.context = context;
            this.listView = listView;
            //this.dialog = dialog;
        }
        @Override
        protected void onPreExecute() {
            messagesList.clear();
            super.onPreExecute();
            progressing_view.setVisibility(View.VISIBLE);
            //dialog.show();
        }
        @Override
        protected String doInBackground(String... strings) {
            Chatt_List2 chatts_list = null;
            list = new ArrayList<>();
            Log.e(TAG,"Phone: "+phone_number);
            Bundle extras = getIntent().getExtras();
            byte[] b = extras.getByteArray("image");
            final List<String> date_l = new ArrayList<>();
            // List<String> b_l = new ArrayList<>();
            final List<String> body_l = new ArrayList<>();
            List<Long> type = new ArrayList<>();
            //List<Long> t = new ArrayList<>();
            final List<String> url_l = new ArrayList<>();
            //List<String> url_l2 = new ArrayList<>();
            long st1 = 1;
            long st2 = 0;

            //SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd:HH:mm:ss");
            //HashMap<String, Date> unsorted = new HashMap<String, Date>();

            String mode = "users";
            Cursor cursor = new SMS_Operations(context).selectSMS1(phone_number,doctor_id);
            if (cursor.moveToFirst()){
                Date datess = null;
                do {
                    // list.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_BODY)));
                    String bd = cursor.getString(cursor.getColumnIndex(Constants.config.SMS_BODY));
                    body_l.add(bd);
                    // b_l.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_BODY)));
                    date_l.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_DATE))+":"+
                            cursor.getString(cursor.getColumnIndex(Constants.config.SMS_TIME)));
                    type.add((long) cursor.getInt(cursor.getColumnIndex(Constants.config.SMS_TYPE)));
                    // t.add((long) cursor.getInt(cursor.getColumnIndex(Constants.config.SMS_TYPE)));
                    String url = cursor.getString(cursor.getColumnIndex(Constants.config.SMS_PATH));
                    url_l.add(url);
                    Log.e(TAG,"*********************************************");
                    Log.e(TAG,url);

                    prepareMovieData(bd,cursor.getString(cursor.getColumnIndex(Constants.config.SMS_DATE))+":"+
                            cursor.getString(cursor.getColumnIndex(Constants.config.SMS_TIME)),
                            (long) cursor.getInt(cursor.getColumnIndex(Constants.config.SMS_TYPE)),bmp,url,mode);

                    //url_l2.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_PATH)));

                }while (cursor.moveToNext());
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            try {
                progressing_view.setVisibility(View.GONE);
                //Util.setListViewHeightBasedOnChildren(listView);
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    class DownloadFile extends AsyncTask<String,Integer,Long> {
        ProgressDialog mProgressDialog = new ProgressDialog(context);// Change Mainactivity.this with your activity name.
        String strFolderName;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            mProgressDialog.setMessage("Downloading");
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setCancelable(true);
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            mProgressDialog.show();
        }
        @Override
        protected Long doInBackground(String... aurl) {
            int count;
            try {
                URL url = new URL((String) aurl[0]);
                URLConnection conexion = url.openConnection();
                conexion.connect();
                String sub = aurl[0].substring(aurl[0].lastIndexOf("/"),aurl[0].lastIndexOf("."));
                String targetFileName = sub+""+".jpg";//Change name and subname
                int lenghtOfFile = conexion.getContentLength();
                String PATH = Environment.getExternalStorageDirectory()+ "/MOBICARE/";
                File folder = new File(PATH);
                if(!folder.exists()){
                    folder.mkdir();//If there is no folder it will be created.
                }
                File subfolder = new File(Environment.getExternalStorageDirectory()+ "/MOBICARE/images/");
                if (!subfolder.exists()){
                    subfolder.mkdir();
                }
                InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = new FileOutputStream(subfolder+"/"+targetFileName);
                //OutputStream outputStream = new FileOutputStream()
                byte data[] = new byte[1024];
                long total = 0;
                while ((count = input.read(data)) != -1) {
                    total += count;
                    publishProgress ((int)(total*100/lenghtOfFile));
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            } catch (Exception e) {}
            return null;
        }
        protected void onProgressUpdate(Integer... progress) {
            mProgressDialog.setProgress(progress[0]);
            if(mProgressDialog.getProgress()==mProgressDialog.getMax()){
                mProgressDialog.dismiss();
                Toast.makeText(context, "File Downloaded", Toast.LENGTH_SHORT).show();
            }
        }
        protected void onPostExecute(String result) {
        }
    }
    public void showDialog(){
        hasSDCard();
        String PATH = Environment.getExternalStorageDirectory()+ "/"+APP_FOLDER+"/";
        File folder = new File(PATH);
        if(!folder.exists()){
            folder.mkdir();//If there is no folder it will be created.
        }

        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = getLayoutInflater();
        View view = inflater.inflate(R.layout.activity_audio_recording, null);
        dialog.setView(view);
        dialog.setCancelable(false);

        final TextView textView = (TextView) view.findViewById(R.id.textView);
        final ImageView imageView = (ImageView) view.findViewById(R.id.recording_image);
        ImageButton ok_btn = (ImageButton) view.findViewById(R.id.send_button);
        ImageButton cancel = (ImageButton) view.findViewById(R.id.cancel_button);
        final Button recording_button = (Button) view.findViewById(R.id.recording_button);
        final Button stop_button = (Button) view.findViewById(R.id.stop_button);
        final Button play_button = (Button) view.findViewById(R.id.play_button);
        if (!isRecording){
            imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
        }
        voiceStoragePath = Environment.getExternalStorageDirectory()+"/"+APP_FOLDER;
        File audioVoice = new File(voiceStoragePath + File.separator + AUDIO_SUB_FOLDER);
        if(!audioVoice.exists()){
            audioVoice.mkdir();
        }
        voiceStoragePath = voiceStoragePath + File.separator + AUDIO_SUB_FOLDER+"/" + generateVoiceFilename(6) + ".3gpp";
        System.out.println("Audio path : " + voiceStoragePath);

        stop_button.setEnabled(false);
        play_button.setEnabled(false);
        initializeMediaRecord();



        recording_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaRecorder == null){
                    initializeMediaRecord();
                }
                isRecording = true;
                startAudioRecording(stop_button,recording_button);
                startTimer(imageView,textView);
            }
        });

        stop_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isRecording = false;
                stopAudioRecording(stop_button,play_button);
                imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
            }
        });

        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                playLastStoredAudioMusic(recording_button,play_button);
                mediaPlayerPlaying();
                isRecording = false;
                imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
            }
        });
        final AlertDialog alert = dialog.create();
        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    int type = 1;
                    String messages = "Voice message by " + new User_Details(context).getName() + " , " + new User_Details(context).getContact();
                    if (!voiceStoragePath.equals("")) {
                        new Image_Operations(context).upload(voiceStoragePath,doctor_id,phone_number,messages,type);
                        alert.dismiss();
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // new CallingNumber(c).clearCalls();
                                progressing_view.setVisibility(View.GONE);
                                Intent intent = new Intent(context, PatientChatts.class);
                                intent.putExtra("image",b);
                                intent.putExtra("id",doctor_id);
                                intent.putExtra("category",category);
                                startActivity(intent);
                                finish();
                            }},300);
                    }else {
                        Toast toast = Toast.makeText(context, "Audio file not found! ", Toast.LENGTH_LONG);
                        View views = toast.getView();
                        views.setBackgroundResource(R.drawable.round);
                        TextView text = (TextView) views.findViewById(android.R.id.message);
                        toast.show();
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    Toast toast = Toast.makeText(context, "Error Occured while sending the audio! ", Toast.LENGTH_LONG);
                    View views = toast.getView();
                    views.setBackgroundResource(R.drawable.round);
                    TextView text = (TextView) views.findViewById(android.R.id.message);
                    toast.show();
                }
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }
    private String generateVoiceFilename( int len ){
        StringBuilder sb = new StringBuilder( len );
        for( int i = 0; i < len; i++ )
            sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
        return sb.toString();
    }
    public void startTimer(final ImageView imageView, final TextView textView){
        count = 0;
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (isRecording){
                            count += 1;
                            if (count%2 == 0){
                                imageView.setImageDrawable(getResources().getDrawable(android.R.drawable.ic_btn_speak_now));
                            }else {
                                imageView.setImageDrawable(null);
                            }
                        }
                        textView.setText(count+" s");
                    }
                });

            }
        }, 0, 1000);
    }
    private void startAudioRecording(Button stopButton,Button recordingButton){
        try {
            mediaRecorder.prepare();
            isRecording = true;
            mediaRecorder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
        recordingButton.setEnabled(false);
        stopButton.setEnabled(true);
    }
    private void stopAudioRecording(final Button stopButton, final Button playButton){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaRecorder != null){
                    mediaRecorder.stop();
                    mediaRecorder.release();
                    mediaRecorder = null;
                }
                stopButton.setEnabled(false);
                playButton.setEnabled(true);
            }
        });
    }
    private void playLastStoredAudioMusic(final Button recordingButton, final Button playButton){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mediaPlayer = new MediaPlayer();
                try {
                    mediaPlayer.setDataSource(voiceStoragePath);

                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    mediaPlayer.prepare();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                mediaPlayer.start();
                recordingButton.setEnabled(true);
                playButton.setEnabled(false);
            }
        });
    }
    private void stopAudioPlay(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(mediaPlayer != null){
                    mediaPlayer.stop();
                    mediaPlayer.release();
                    mediaPlayer = null;
                }
            }
        });
    }
    private void hasSDCard(){
        Boolean isSDPresent = android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);
        if(isSDPresent)        {
            System.out.println("There is SDCard");
        }
        else{
            System.out.println("There is no SDCard");
        }
    }
    private void mediaPlayerPlaying(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if(!mediaPlayer.isPlaying()){
                    stopAudioPlay();
                }
            }
        });
    }
    private void initializeMediaRecord(){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mediaRecorder = new MediaRecorder();
                mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
                mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
                mediaRecorder.setOutputFile(voiceStoragePath);
            }
        });
    }

    public void serverView(){
        messagesList.clear();
        progressing_view.setVisibility(View.VISIBLE);
        final long timeBeforeUpload = System.currentTimeMillis();
        StringRequest stringRequest = new StringRequest(Request.Method.POST, HOST_URL+SMS_PATIENT_DOCTOR_URL,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        progressing_view.setVisibility(View.VISIBLE);
                        list = new ArrayList<>();
                        Log.e(TAG,"Phone: "+phone_number);
                        Bundle extras = getIntent().getExtras();
                        byte[] b = extras.getByteArray("image");
                        final List<String> date_l = new ArrayList<>();
                        // List<String> b_l = new ArrayList<>();
                        final List<String> body_l = new ArrayList<>();
                        List<Long> type = new ArrayList<>();
                        //List<Long> t = new ArrayList<>();
                        final List<String> url_l = new ArrayList<>();
                        //List<String> url_l2 = new ArrayList<>();
                        long st1 = 1;
                        long st2 = 0;
                        String mode = "users";
                        try {
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i=0; i<jsonArray.length(); i++){
                                JSONObject jsonObject = jsonArray.getJSONObject(i);
                                // list.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_BODY)));
                                String bd = jsonObject.getString(Constants.config.SMS_BODY);
                                body_l.add(bd);
                                // b_l.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_BODY)));
                                date_l.add(jsonObject.getString(Constants.config.SMS_DATE)+":"+
                                        jsonObject.getString(Constants.config.SMS_TIME));
                                type.add(jsonObject.getLong(Constants.config.SMS_TYPE));
                                // t.add((long) cursor.getInt(cursor.getColumnIndex(Constants.config.SMS_TYPE)));
                                String url = jsonObject.getString(Constants.config.SMS_PATH);
                                url_l.add(url);
                                prepareMovieData(bd,jsonObject.getString(Constants.config.SMS_DATE)+":"+
                                        jsonObject.getString(Constants.config.SMS_TIME),jsonObject.getLong(Constants.config.SMS_TYPE),bmp,url,mode);
                                Log.e(TAG,"*********************************************");
                                Log.e(TAG,url);
                                //url_l2.add(cursor.getString(cursor.getColumnIndex(Constants.config.SMS_PATH)));
                            }
                            progressing_view.setVisibility(View.GONE);
                        }catch (Exception e){
                            e.printStackTrace();
                            sms();
                        }
                        Log.e(TAG,response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Log.e(TAG, volleyError.getMessage());
                        sms();
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                String id = null;
                Map<String, String> params = new Hashtable<String, String>();
                params.put("phone",phone_number);
                params.put("doctor_id",doctor_id);
                //Adding parameters
                return params;
            }
        };
        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    private void prepareMovieData(String bd, String date, long lg, Bitmap bmp, String url, String mode) {
        Messages messages = new Messages(bd, date,lg, bmp,url,mode);
        messagesList.add(messages);
        mAdapter.notifyDataSetChanged();
    }


}