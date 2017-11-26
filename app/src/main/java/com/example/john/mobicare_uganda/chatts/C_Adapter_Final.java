package com.example.john.mobicare_uganda.chatts;

import android.app.Activity;
import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelFileDescriptor;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.res.ResourcesCompat;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.firebase_collections.util.CompressBitmap;
import com.example.john.mobicare_uganda.firebase_collections.util.Convert;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import connectivity.Constants;
import server_connections.Image_Operations;
import users.CurrentUser;
import users.Image;

import static android.content.Context.DOWNLOAD_SERVICE;
import static android.os.Environment.DIRECTORY_DOWNLOADS;
import static com.example.john.mobicare_uganda.firebase_collections.Config.APP_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.AUDIO_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.IMAGE_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.VIDEO_SUB_FOLDER;
import static connectivity.Constants.config.OPERATION_AUDIO;
import static connectivity.Constants.config.OPERATION_IMAGE;
import static connectivity.Constants.config.OPERATION_VIDEO;

/**
 * Created by john on 10/23/17.
 */

public class C_Adapter_Final extends ArrayAdapter<String> {
    private final Activity context;
    List<String> message, dates,url;
    LayoutInflater inflter;
    List<Long> fetch;
    List<Long> lg;
    String mode;
    private int screen_w = 0, screen_h = 0;
    final String strPref_Download_ID = "PREF_DOWNLOAD_ID";
    Handler mainHandler = new Handler(Looper.getMainLooper());

    SharedPreferences preferenceManager;
    DownloadManager downloadManager;
    private BroadcastReceiver downloadReceiver,downloadReceiver2,downloadReceiver3;
    boolean downloading = false;

    public C_Adapter_Final(Activity context, List<String> message, List<String> dates, List<Long> lg, List<Long> fetch, List<String> url, String mode) {
        super(context, R.layout.chatt_lists, message);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.message = message;
        this.fetch = fetch;
        this.dates = dates;
        this.lg = lg;
        this.url = url;
        this.mode = mode;
    }

    public View getView(int i, View views, ViewGroup parent) {
        LayoutInflater inflater=context.getLayoutInflater();

        View view = null;
        if (mode.equals("doctor")) {
            if (lg.get(i) == 1){
                view=inflater.inflate(R.layout.list_right_chatt, null,true);
            }else {
                view=inflater.inflate(R.layout.list_left_chatt, null,true);
            }
        }else {
            if (lg.get(i) == 1){
                view=inflater.inflate(R.layout.list_left_chatt, null,true);
            }else {
                view=inflater.inflate(R.layout.list_right_chatt, null,true);
            }
        }
        if (view != null) {
            view.clearFocus();
        }
        try {
            preferenceManager
                    = PreferenceManager.getDefaultSharedPreferences(context);
            downloadManager
                    = (DownloadManager)context.getSystemService(DOWNLOAD_SERVICE);

            //// TODO: 10/23/17  body
            LinearLayout body_layout = (LinearLayout) view.findViewById(R.id.body_layout);
            TextView body_text = (TextView) view.findViewById(R.id.body_text);
            ImageView body_sync = (ImageView) view.findViewById(R.id.body_sync);
            ImageView body_single = (ImageView) view.findViewById(R.id.body_single);
            ImageView body_double = (ImageView) view.findViewById(R.id.body_double);



            //// TODO: 10/23/17  images
            LinearLayout layout_media = (LinearLayout) view.findViewById(R.id.layout_media);
            LinearLayout image_layout = (LinearLayout) view.findViewById(R.id.image_layout);
            ProgressBar progress_img = (ProgressBar) view.findViewById(R.id.progress_image);
            ImageView imageView_src = (ImageView) view.findViewById(R.id.imageView_src);
            Button download_img = (Button) view.findViewById(R.id.download_img);
            TextView image_text = (TextView) view.findViewById(R.id.image_text);
            TextView image_size = (TextView) view.findViewById(R.id.image_size);

            ImageView image_sync = (ImageView) view.findViewById(R.id.image_sync);
            ImageView image_single = (ImageView) view.findViewById(R.id.image_single);
            ImageView image_double = (ImageView) view.findViewById(R.id.image_double);


            //// TODO: 10/23/17  Videos...!!!!
            LinearLayout layout_video = (LinearLayout) view.findViewById(R.id.layout_video);
            ProgressBar progress_video = (ProgressBar) view.findViewById(R.id.progress_video);
            VideoView videoView = (VideoView) view.findViewById(R.id.videoView);
            Button download_video = (Button) view.findViewById(R.id.download_video);
            Button play_video = (Button) view.findViewById(R.id.play_video);

            TextView video_text = (TextView) view.findViewById(R.id.video_text);
            TextView video_size = (TextView) view.findViewById(R.id.video_size);

            ImageView video_sync = (ImageView) view.findViewById(R.id.video_sync);
            ImageView video_single = (ImageView) view.findViewById(R.id.video_single);
            ImageView video_double = (ImageView) view.findViewById(R.id.video_double);

            //// TODO: 10/23/17  Aduo...!!!
            LinearLayout audio_layout = (LinearLayout) view.findViewById(R.id.audio_layout);
            ProgressBar progress_audio = (ProgressBar) view.findViewById(R.id.progress_audio);
            SeekBar seekBar = (SeekBar) view.findViewById(R.id.seekBar);
            ImageButton audio_play = (ImageButton) view.findViewById(R.id.audio_play);
            ImageButton audio_pause_download = (ImageButton) view.findViewById(R.id.audio_pause_download);
            ImageButton download_audio = (ImageButton) view.findViewById(R.id.download_audio);
            TextView audio_text = (TextView) view.findViewById(R.id.audio_text);
            TextView audio_size = (TextView) view.findViewById(R.id.audio_size);

            ImageView audio_sync = (ImageView) view.findViewById(R.id.audio_sync);
            ImageView audio_single = (ImageView) view.findViewById(R.id.audio_single);
            ImageView audio_double = (ImageView) view.findViewById(R.id.audio_double);

            Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.profile2, null);
            Bitmap bitmap3 = ((BitmapDrawable) vectorDrawable).getBitmap();

            Spannable word = new SpannableString(message.get(i));
            Spannable wordTwo = new SpannableString("  (" + dates.get(i) + " )");
            wordTwo.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String f = "";
            int flag = 0;
            if (!url.get(i).equals("undefined")) {
                String[] splits = url.get(i).split("/");

                if (splits.length == 10) {
                    f = splits[9];
                    Log.e("EXTENSION:", "Extension = " + splits[9].substring(splits[9].length() - 3, splits[9].length()));

                }else if (splits.length == 6){
                    f = splits[5];
                    flag = 1;
                }
                Log.e("Chatt_List2", message.get(i) + " \t " + lg.get(i) + " \t " + url.get(i));
            }
           // String f_n = "";
            if (url.get(i) != null && url.get(i).length() > 4 && Patterns.WEB_URL.matcher(url.get(i)).matches()) {
              //  f_n = url.get(i).substring(url.get(i).lastIndexOf("/" + 1, url.get(i).length()));
            }
            // External sdcard location
            String PATH = Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/";
            File folder = new File(PATH);
            if (!folder.exists()) {
                folder.mkdir();//If there is no folder it will be created.
            }
            File file_video = new File(PATH + File.separator + VIDEO_SUB_FOLDER);
            final File file_image = new File(PATH + File.separator + IMAGE_SUB_FOLDER);
            final File file_audio = new File(PATH + File.separator + AUDIO_SUB_FOLDER);
            if (!file_video.exists()) {
                file_video.mkdir();
            }
            if (!file_image.exists()) {
                file_image.mkdir();
            }
            if (!file_audio.exists()) {
                file_audio.mkdir();
            }
            if (!f.equals("")) {
                String tag = f.split("_")[0];
                File file1 = new File(file_image + "/" + f);
                if (flag == 0) {
                    if (tag.equals("IMG")) {
                        setImage(imageView_src, f, file_image, url.get(i), download_img, image_size, progress_img);
                        layout_video.setVisibility(View.GONE);
                        audio_layout.setVisibility(View.GONE);
                    } else if (tag.equals("VID")) {
                        setVideo(layout_video, videoView, f, file_video, url.get(i), download_video, video_size, progress_video,play_video);
                        image_layout.setVisibility(View.GONE);
                        audio_layout.setVisibility(View.GONE);
                    } else {
                        setAudio(audio_play, audio_pause_download, seekBar, f, file_audio, url.get(i), audio_size, progress_audio, download_audio);
                        layout_video.setVisibility(View.GONE);
                        image_layout.setVisibility(View.GONE);
                    }
                }else{
                    File file = new File(url.get(i).substring(0,url.get(i).lastIndexOf("/")+1));
                    if (tag.equals("IMG")) {
                        setImage(imageView_src, f, file, url.get(i), download_img, image_size, progress_img);
                        layout_video.setVisibility(View.GONE);
                        audio_layout.setVisibility(View.GONE);
                    } else if (tag.equals("VID")) {
                        setVideo(layout_video, videoView, f, file, url.get(i), download_video, video_size, progress_video,play_video);
                        image_layout.setVisibility(View.GONE);
                        audio_layout.setVisibility(View.GONE);
                    } else {
                        setAudio(audio_play, audio_pause_download, seekBar, f, file, url.get(i), audio_size, progress_audio, download_audio);
                        layout_video.setVisibility(View.GONE);
                        image_layout.setVisibility(View.GONE);
                    }
                }
                Log.e("Chatts_with flag","Flag = "+flag+" , file: "+f+" , "+url.get(i).substring(0,url.get(i).lastIndexOf("/")+1));
                body_layout.setVisibility(View.GONE);
                image_text.setText(word);
                image_text.append(wordTwo);


                audio_text.setText(word);
                audio_text.append(wordTwo);


                video_text.setText(word);
                video_text.append(wordTwo);

            } else {
                layout_media.setVisibility(View.GONE);
                audio_layout.setVisibility(View.GONE);
                body_text.setText(word);
                ///
                body_text.append(wordTwo);
            }
            //Log.e("Chatt@2", "URL: " + file_image + "/" + f);
            try {
                //final String finalF_n = f_n;

                // videoView.setVideoPath(file_image.getPath()+"/"+f_n);
                if (message.get(i).equals("")) {
                    body_layout.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
    private void setAudio(final ImageButton imageButton, final ImageButton imageButton2, final SeekBar seek_bar, String f, File file, final String url, final TextView textView, final ProgressBar progressBar, final ImageButton download_img) {
        final Timer timer = new Timer();
        imageButton.setEnabled(false);
        imageButton2.setVisibility(View.GONE);
        final boolean[] play = {false};
        Log.e("Audio file: ","********************************"+file+"/"+f);
        File file1 = new File(file+"/"+f);
        final MediaPlayer mp=new MediaPlayer();
        final Handler seekHandler = new Handler();

        String audio_path = "";
        if (file1.exists()){
            audio_path = file.getAbsolutePath() + "/" + f;
            download_img.setVisibility(View.GONE);
        }else {
            audio_path = url;
            download_img.setVisibility(View.VISIBLE);
        }
        download_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DownloadImageAudio(url,textView,download_img,progressBar);
            }
        });
        Log.e("AUDIO PATH", audio_path);
        try{
            mp.setDataSource(audio_path);
            mp.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mediaPlayer) {
                    imageButton.setEnabled(true);
                    imageButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
                    imageButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            mp.start();
                            imageButton.setVisibility(View.GONE);
                            imageButton2.setVisibility(View.VISIBLE);
                            //imageButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
                        }
                    });
                    seek_bar.setMax(mp.getDuration());
                    timer.scheduleAtFixedRate(new TimerTask() {
                        @Override
                        public void run() {
                            seek_bar.setProgress(mp.getCurrentPosition());
                        }
                    }, 0, 100);//1 minutes
                }
            });
            imageButton2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mp.pause();
                    imageButton.setVisibility(View.VISIBLE);
                    imageButton2.setVisibility(View.GONE);

                }
            });
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mediaPlayer) {
                    //mp.release();
                    imageButton.setVisibility(View.VISIBLE);
                    imageButton2.setVisibility(View.GONE);
                    //imageButton.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_media_play));
                }
            });
            mp.prepareAsync();

            reg3(progressBar,textView,download_img);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("Audio erro: ","********************************");
        }
    }

    private void DownloadImageAudio(String url_path, TextView textView, ImageButton download_img, ProgressBar progressBar) {
        String PATH = Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/";
        File folder = new File(PATH);

        if (!folder.exists()) {
            folder.mkdir();//If there is no folder it will be created.
        }
        File subfolder = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + IMAGE_SUB_FOLDER + "/");
        if (!subfolder.exists()) {
            subfolder.mkdir();
        }
        File subfolder2 = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + VIDEO_SUB_FOLDER + "/");
        if (!subfolder.exists()) {
            subfolder2.mkdir();
        }

        File subfolder3 = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + AUDIO_SUB_FOLDER + "/");
        if (!subfolder3.exists()) {
            subfolder3.mkdir();
        }
        File file1 = null;
        String targetFileName = "";
        String path = DIRECTORY_DOWNLOADS;
        if (url_path != null && url_path.length() > 4 && Patterns.WEB_URL.matcher(url_path).matches()) {
            String sub = url_path.substring(url_path.lastIndexOf("/"), url_path.length());
            targetFileName = sub;//Change name and subname
            String[] splits = url_path.split("/");
            if (splits.length >= 10) {
                String[] tags = splits[9].split("_");
                String tag = tags[0];
                if (tag.equals("IMG")) {

                    path = subfolder.getAbsolutePath();
                    file1 = new File(subfolder + "/" + targetFileName);
                } else if (tag.equals("VID")){
                    path = subfolder2.getAbsolutePath();
                    file1 = new File(subfolder2 + "/" + targetFileName);
                }else {
                    path = subfolder3.getAbsolutePath();
                    file1 = new File(subfolder3 + "/" + targetFileName);

                }
                Log.e("TYPE: ", " TAG: " + tag);
            }
        }

        Uri downloadUri = Uri.parse(url_path);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setDescription(targetFileName);
        request.setDestinationUri(Uri.fromFile(file1));

        long id = downloadManager.enqueue(request);

        //Save the request id
        SharedPreferences.Editor PrefEdit = preferenceManager.edit();
        PrefEdit.putLong(strPref_Download_ID, id);
        PrefEdit.putString("path",file1.getAbsolutePath());
        PrefEdit.putString("type",OPERATION_AUDIO);
        PrefEdit.putBoolean("isdownloading",true);
        PrefEdit.commit();
        downloading = true;
        ///// TODO: 10/24/17  show progress...!!!!
        showProgresss(textView,progressBar);
        download_img.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);

    }


    public void setImage(final ImageView imageView, final String f, final File file, final String url, final Button download_img, final TextView textView, final ProgressBar progressBar){
        File file1 = new File(file+"/"+f);
        if (file1.exists()){
            Log.e("CHATTSSS: ", "IMAGE PATH: " + file.getAbsolutePath() + "/" + f);
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context)
                            .load(file.getAbsolutePath() + "/" + f)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                }
            });
            Log.e("IMAGE: ", file + "/" + f);

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                    if ((context.getResources().getConfiguration().screenLayout &
                            Configuration.SCREENLAYOUT_SIZE_MASK) ==
                            Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                        // on a large screen device ...
                        dialog.setContentView(R.layout.dialog);}

                    else dialog.setContentView(R.layout.dialog);

                    final ImageView im = (ImageView) dialog.findViewById(R.id.imageView);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(context)
                                    .load(file.getAbsolutePath() + "/" + f)
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(im);
                        }
                    });
                    im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            });

            long length = file.length();
            download_img.setVisibility(View.GONE);
        }else {
            download_img.setVisibility(View.VISIBLE);
            ((Activity) context).runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Glide.with(context)
                            .load(url)
                            .thumbnail(0.5f)
                            .crossFade()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imageView);
                }
            });

            download_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DownloadImage(url,imageView,textView,download_img,progressBar);
                }
            });

            imageView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View view) {

                    final Dialog dialog = new Dialog(context, R.style.CustomDialog);
                    if ((context.getResources().getConfiguration().screenLayout &
                            Configuration.SCREENLAYOUT_SIZE_MASK) ==
                            Configuration.SCREENLAYOUT_SIZE_XLARGE) {
                        // on a large screen device ...
                        dialog.setContentView(R.layout.dialog);}

                    else dialog.setContentView(R.layout.dialog);

                    final ImageView im = (ImageView) dialog.findViewById(R.id.imageView);

                    ((Activity) context).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Glide.with(context)
                                    .load(url)
                                    .thumbnail(0.5f)
                                    .crossFade()
                                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                                    .into(im);
                        }
                    });
                    im.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    Window window = dialog.getWindow();
                    window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                }
            });



            //// TODO: 10/24/17
            boolean isdownloading = preferenceManager.getBoolean("isdownloading",false);
            if (isdownloading){
                downloading = true;
                showProgresss(textView,progressBar);
            }else {
                downloading = false;
            }
            //// TODO: 10/24/17  register reciever...!
            reg2(progressBar,textView,imageView,download_img);
        }
    }

    public void setVideo(final LinearLayout layout, final VideoView videioView, String f, File file, final String url, final Button download_video, final TextView video_size, final ProgressBar progress_video,Button play_video){

        try {
            MediaPlayer mediaPlayer;
            MediaController mediaControls = null;
            final int position = 0;
            if (mediaControls == null) {
                // create an object of media controller class
                mediaControls = new MediaController(context);
                // mediaControls.setAnchorView(videoView);
            }
            String paths = "";
            long length = 0;
            File file1 = new File(file+"/"+f);


            long ll = 0;
            try {

                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {
                        try  {
                            //Your code goes here
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });


                URL pathss_url = new URL(url);
                URLConnection conexion = pathss_url.openConnection();
                conexion.connect();
                ll = conexion.getContentLength();
            }catch (Exception e){
                e.printStackTrace();
            }
            if (file1.exists()) {
                String[] tags = f.split("_");
                // new VideoPlayer(context,videoView,file+"/"+f);
                paths = file + "/" + f;
                length = file1.length();
                download_video.setVisibility(View.GONE);
                play_video.setVisibility(View.VISIBLE);
            } else {
                paths = url;
                //URL url_ = new URL(url);
                //URLConnection urlConnection = url_.openConnection();
                //urlConnection.connect();
                ///ength = urlConnection.getContentLength();
                download_video.setVisibility(View.VISIBLE);
                play_video.setVisibility(View.GONE);
                try {
                    URL pathss_url = new URL(url);
                    URLConnection conexion = pathss_url.openConnection();
                    conexion.connect();
                    length = conexion.getContentLength();
                }catch (Exception e){
                    e.printStackTrace();
                }
            }

            if (ll != length){
               // download_video.setVisibility(View.VISIBLE);
            }

            download_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //new Chatt_Downloads().download(context,video_size,progress_video,url,download_video,videioView);
                    //new Chatt_Downloads().download2(video_size,progress_video,url,download_video,videioView);
                    DownloadData(url,videioView,video_size,download_video,progress_video);
                }
            });



            video_size.setText(size(length));
            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(paths,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
            videioView.setBackgroundDrawable(bitmapDrawable);
            final String finalPaths = paths;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, com.example.john.mobicare_uganda.firebase_collections.util.VideoPlayer.class);
                    intent.putExtra("paths", finalPaths);
                    context.startActivity(intent);
                }
            });
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, com.example.john.mobicare_uganda.firebase_collections.util.VideoPlayer.class);
                    intent.putExtra("paths", finalPaths);
                    context.startActivity(intent);
                }
            });

            play_video.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, com.example.john.mobicare_uganda.firebase_collections.util.VideoPlayer.class);
                    intent.putExtra("paths", finalPaths);
                    context.startActivity(intent);
                }
            });

            boolean isdownloading = preferenceManager.getBoolean("isdownloading",false);
            if (isdownloading){
                downloading = true;
                showProgresss(video_size,progress_video);
            }else {
                downloading = false;
            }
            //// TODO: 10/24/17  register reciever...!
            reg(progress_video,video_size,videioView,download_video,play_video);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void reg(final ProgressBar progress_video, final TextView video_size, final VideoView videioView, final Button download_video, final Button play_video){
        IntentFilter intentFilter
                = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub

                downloading = false;
                progress_video.setVisibility(View.GONE);

                SharedPreferences.Editor PrefEdit = preferenceManager.edit();
                PrefEdit.putBoolean("isdownloading",false);

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(preferenceManager.getLong(strPref_Download_ID, 0));
                Cursor cursor = downloadManager.query(query);
                if(cursor.moveToFirst()){
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    long downloadID = preferenceManager.getLong(strPref_Download_ID, 0);
                    String path = preferenceManager.getString("path",null);
                    String type = preferenceManager.getString("type",null);
                    if (type.equals(OPERATION_VIDEO)) {
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            //Retrieve the saved request id

                            ParcelFileDescriptor file;
                            try {
                                //if (type.equals(OPERATION_VIDEO)) {
                                    Bitmap thumb = ThumbnailUtils.createVideoThumbnail(path,
                                            MediaStore.Images.Thumbnails.MINI_KIND);
                                    BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                                    videioView.setBackgroundDrawable(bitmapDrawable);

                                    long leng = new File(path).length();
                                    video_size.setText(size(leng));
                                    download_video.setVisibility(View.GONE);
                                    play_video.setVisibility(View.VISIBLE);
                              //  }

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } else {
                            download_video.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        context.registerReceiver(downloadReceiver, intentFilter);
    }

    private void reg2(final ProgressBar progress_image, final TextView image_size, final ImageView imageView, final Button download_image ){

        try{
            context.unregisterReceiver(downloadReceiver);
        }catch (Exception e){
            e.printStackTrace();
        }

        IntentFilter intentFilter
                = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver2 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub

                downloading = false;
                progress_image.setVisibility(View.GONE);

                SharedPreferences.Editor PrefEdit = preferenceManager.edit();
                PrefEdit.putBoolean("isdownloading",false);

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(preferenceManager.getLong(strPref_Download_ID, 0));
                Cursor cursor = downloadManager.query(query);
                if(cursor.moveToFirst()){
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    long downloadID = preferenceManager.getLong(strPref_Download_ID, 0);
                    final String path = preferenceManager.getString("path",null);
                    String type = preferenceManager.getString("type",null);
                    if (type.equals(OPERATION_IMAGE)) {
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            //Retrieve the saved request id


                            ParcelFileDescriptor file;
                            try {
                                ((Activity) context).runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        Glide.with(context)
                                                .load(path)
                                                .thumbnail(0.5f)
                                                .crossFade()
                                                .diskCacheStrategy(DiskCacheStrategy.ALL)
                                                .into(imageView);
                                    }
                                });

                                long leng = new File(path).length();
                                image_size.setText(size(leng));
                                download_image.setVisibility(View.GONE);

                            } catch (Exception e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }

                        } else {
                            download_image.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        context.registerReceiver(downloadReceiver2, intentFilter);

    }



    private void reg3(final ProgressBar progress_image, final TextView image_size, final ImageButton download_image ){

        IntentFilter intentFilter
                = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        downloadReceiver3 = new BroadcastReceiver() {
            @Override
            public void onReceive(Context arg0, Intent arg1) {
                // TODO Auto-generated method stub

                downloading = false;
                progress_image.setVisibility(View.GONE);

                SharedPreferences.Editor PrefEdit = preferenceManager.edit();
                PrefEdit.putBoolean("isdownloading",false);

                DownloadManager.Query query = new DownloadManager.Query();
                query.setFilterById(preferenceManager.getLong(strPref_Download_ID, 0));
                Cursor cursor = downloadManager.query(query);
                if(cursor.moveToFirst()){
                    int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
                    int status = cursor.getInt(columnIndex);
                    long downloadID = preferenceManager.getLong(strPref_Download_ID, 0);
                    final String path = preferenceManager.getString("path",null);
                    String type = preferenceManager.getString("type","");
                    if (type.equals(OPERATION_AUDIO)) {
                        if (status == DownloadManager.STATUS_SUCCESSFUL) {
                            //Retrieve the saved request id

                        } else {
                            download_image.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };
        context.registerReceiver(downloadReceiver3, intentFilter);
    }
    public String size(long size){
        String hrSize = "";
        try {
            double m = size / (1024.0 *1024.0);
            DecimalFormat dec = new DecimalFormat("0.00");

            if (m > 1) {
                hrSize = dec.format(m).concat(" MB");
            } else {
                hrSize = dec.format(size).concat(" KB");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return hrSize;
    }
    private void DownloadData(String url_path, final VideoView videoView, final TextView textView, final Button download_video, final ProgressBar progress_video) {

        String PATH = Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/";
        File folder = new File(PATH);

        if (!folder.exists()) {
            folder.mkdir();//If there is no folder it will be created.
        }
        File subfolder = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + IMAGE_SUB_FOLDER + "/");
        if (!subfolder.exists()) {
            subfolder.mkdir();
        }
        File subfolder2 = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + VIDEO_SUB_FOLDER + "/");
        if (!subfolder.exists()) {
            subfolder2.mkdir();
        }

        File subfolder3 = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + AUDIO_SUB_FOLDER + "/");
        if (!subfolder3.exists()) {
            subfolder3.mkdir();
        }

        File file1 = null;
        String targetFileName = "";
        String path = DIRECTORY_DOWNLOADS;
        if (url_path != null && url_path.length() > 4 && Patterns.WEB_URL.matcher(url_path).matches()) {
            String sub = url_path.substring(url_path.lastIndexOf("/"), url_path.length());
            targetFileName = sub;//Change name and subname
            String[] splits = url_path.split("/");
            if (splits.length >= 10) {
                String[] tags = splits[9].split("_");
                String tag = tags[0];
                if (tag.equals("IMG")) {

                    path = subfolder.getAbsolutePath();
                    file1 = new File(subfolder + "/" + targetFileName);
                } else if (tag.equals("VID")){
                    path = subfolder2.getAbsolutePath();
                    file1 = new File(subfolder2 + "/" + targetFileName);
                }else {
                    path = subfolder3.getAbsolutePath();
                    file1 = new File(subfolder3 + "/" + targetFileName);

                }
                Log.e("TYPE: ", " TAG: " + tag);
            }
        }

        Uri downloadUri = Uri.parse(url_path);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setDescription(targetFileName);
        request.setDestinationUri(Uri.fromFile(file1));

        long id = downloadManager.enqueue(request);

        //Save the request id
        SharedPreferences.Editor PrefEdit = preferenceManager.edit();
        PrefEdit.putLong(strPref_Download_ID, id);
        PrefEdit.putString("path",file1.getAbsolutePath());
        PrefEdit.putString("type", Constants.config.OPERATION_VIDEO);
        PrefEdit.putBoolean("isdownloading",true);
        PrefEdit.commit();
        downloading = true;
        ///// TODO: 10/24/17  show progress...!!!!
        showProgresss(textView,progress_video);
        download_video.setVisibility(View.GONE);
        progress_video.setVisibility(View.VISIBLE);
    }


    public void showProgresss(final TextView textView, final ProgressBar progressBar){
        try{
            final long DownloadManagerId = preferenceManager.getLong(strPref_Download_ID, 0);
            new Thread(new Runnable() {

                @Override
                public void run() {
                    DownloadManager manager = (DownloadManager)context. getSystemService(Context.DOWNLOAD_SERVICE);
                    while (downloading) {

                        DownloadManager.Query q = new DownloadManager.Query();
                        q.setFilterById(DownloadManagerId); //filter by id which you have receieved when reqesting download from download manager
                        Cursor cursor = manager.query(q);
                        cursor.moveToFirst();
                        final int bytes_downloaded = cursor.getInt(cursor
                                .getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                        final int bytes_total = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                        if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                            downloading = false;
                        }
                        final int dl_progress = (int) ((bytes_downloaded * 100l) / bytes_total);
                        Runnable myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                if (dl_progress <= 99){
                                    String path = preferenceManager.getString("path",null);
                                    long leng = new File(path).length();
                                    textView.setText("("+size(bytes_downloaded)+"/"+size(leng)+") - "+dl_progress+"%");
                                    progressBar.setVisibility(View.VISIBLE);
                                }
                            }
                        };
                        mainHandler.post(myRunnable);

                        // Log.d(Constants.MAIN_VIEW_ACTIVITY, statusMessage(cursor));
                        cursor.close();
                    }

                }
            }).start();

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    //// TODO: 10/23/17

    private void DownloadImage(String url_path, final ImageView imageView, final TextView textView, final Button download_image, final ProgressBar progress_image) {

        String PATH = Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/";
        File folder = new File(PATH);

        if (!folder.exists()) {
            folder.mkdir();//If there is no folder it will be created.
        }
        File subfolder = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + IMAGE_SUB_FOLDER + "/");
        if (!subfolder.exists()) {
            subfolder.mkdir();
        }
        File subfolder2 = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + VIDEO_SUB_FOLDER + "/");
        if (!subfolder.exists()) {
            subfolder2.mkdir();
        }

        File subfolder3 = new File(Environment.getExternalStorageDirectory() + "/" + APP_FOLDER + "/" + AUDIO_SUB_FOLDER + "/");
        if (!subfolder3.exists()) {
            subfolder3.mkdir();
        }

        File file1 = null;
        String targetFileName = "";
        String path = DIRECTORY_DOWNLOADS;
        if (url_path != null && url_path.length() > 4 && Patterns.WEB_URL.matcher(url_path).matches()) {
            String sub = url_path.substring(url_path.lastIndexOf("/"), url_path.length());
            targetFileName = sub;//Change name and subname
            String[] splits = url_path.split("/");
            if (splits.length >= 10) {
                String[] tags = splits[9].split("_");
                String tag = tags[0];
                if (tag.equals("IMG")) {

                    path = subfolder.getAbsolutePath();
                    file1 = new File(subfolder + "/" + targetFileName);
                } else if (tag.equals("VID")){
                    path = subfolder2.getAbsolutePath();
                    file1 = new File(subfolder2 + "/" + targetFileName);
                }else {
                    path = subfolder3.getAbsolutePath();
                    file1 = new File(subfolder3 + "/" + targetFileName);

                }
                Log.e("TYPE: ", " TAG: " + tag);
            }
        }

        Uri downloadUri = Uri.parse(url_path);
        DownloadManager.Request request = new DownloadManager.Request(downloadUri);
        request.setDescription(targetFileName);
        request.setDestinationUri(Uri.fromFile(file1));

        long id = downloadManager.enqueue(request);

        //Save the request id
        SharedPreferences.Editor PrefEdit = preferenceManager.edit();
        PrefEdit.putLong(strPref_Download_ID, id);
        PrefEdit.putString("path",file1.getAbsolutePath());
        PrefEdit.putString("type", Constants.config.OPERATION_IMAGE);
        PrefEdit.putBoolean("isdownloading",true);
        PrefEdit.commit();
        downloading = true;
        ///// TODO: 10/24/17  show progress...!!!!
        showProgresss(textView,progress_image);
        download_image.setVisibility(View.GONE);
        progress_image.setVisibility(View.VISIBLE);
    }

}