package com.example.john.mobicare_uganda.chatts;

import android.app.Activity;
import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;

import static com.example.john.mobicare_uganda.firebase_collections.Config.APP_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.AUDIO_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.IMAGE_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.VIDEO_SUB_FOLDER;

/**
 * Created by john on 10/23/17.
 */

public class Chatt_Downloads {
    Handler mainHandler = new Handler(Looper.getMainLooper());

    int downloadedSize = 0;
    int totalSize = 0;

    public void download(Activity context, TextView textView, ProgressBar progressBar, String url, ImageButton download_video, VideoView videioView){
        new DownloadFile(textView,progressBar,context,download_video,videioView).execute(url);
    }
    class DownloadFile extends AsyncTask<String,Integer,Long> {
        String strFolderName;
        ProgressBar progressBar;
        TextView textView;
        ImageButton download_video;
        VideoView videioView;
        //ProgressDialog dialog;
        Activity context;
        long downloadedLength;
        DownloadFile(TextView textView, ProgressBar progressBar, Activity context, ImageButton download_video, VideoView videioView){
            this.progressBar = progressBar;
            this.textView = textView;
            this.context = context;
            this.download_video = download_video;
            this.videioView = videioView;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                    //dialog.show();
                }
            };
            mainHandler.post(myRunnable);
        }

        @Override
        protected Long doInBackground(String... aurl) {
            int count;
            try {
                if (aurl[0] != null && aurl[0].length() > 4 && Patterns.WEB_URL.matcher(aurl[0]).matches()) {
                    URL url = new URL((String) aurl[0]);
                    URLConnection conexion = url.openConnection();


                    conexion.connect();
                    //this is the total size of the file which we are downloading
                    totalSize = conexion.getContentLength();
                    Runnable myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setMax(totalSize);
                        }
                    };
                    mainHandler.post(myRunnable);

                    String sub = aurl[0].substring(aurl[0].lastIndexOf("/"), aurl[0].length());
                    String targetFileName = sub;//Change name and subname
                    final int lenghtOfFile = conexion.getContentLength();
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

                    InputStream input = new BufferedInputStream(url.openStream());
                    OutputStream output = null;
                    File file1 = null;
                    String string_path = "";

                    if (aurl[0] != null && aurl[0].length() > 4 && Patterns.WEB_URL.matcher(aurl[0]).matches()) {
                        String[] splits = aurl[0].split("/");
                        if (splits.length >= 10) {
                            String[] tags = splits[9].split("_");
                            String tag = tags[0];

                            if (tag.equals("IMG")) {
                                output = new FileOutputStream(subfolder + "/" + targetFileName);
                                file1 = new File(subfolder + "/" + targetFileName);
                                string_path = subfolder + "/" + targetFileName;
                            } else if (tag.equals("VID")){
                                output = new FileOutputStream(subfolder2 + "/" + targetFileName);
                                file1 = new File(subfolder2 + "/" + targetFileName);
                                string_path = subfolder2 + "/" + targetFileName;
                            }else {
                                output = new FileOutputStream(subfolder3 + "/" + targetFileName);
                                file1 = new File(subfolder3 + "/" + targetFileName);
                                string_path = subfolder3 + "/" + targetFileName;
                            }
                            Log.e("TYPE: ", " TAG: " + tag);
                        }
                    }




                    //OutputStream outputStream = new FileOutputStream()
                    byte data[] = new byte[1024];
                    long total = 0;
                   // final StringBuilder stringBuilder = new StringBuilder();
                    while ((count = input.read(data)) != -1) {
                        total += count;
                        downloadedSize += count;
                        publishProgress((int) (total * 100 / lenghtOfFile));

                        //stringBuilder.append(input.read(data));
                        final long finalTotal = total;
                         myRunnable = new Runnable() {
                            @Override
                            public void run() {
                                textView.setText(FileSize.size((int) (finalTotal * 100 / lenghtOfFile) ));
                                progressBar.setProgress(downloadedSize);
                                float per = ((float)downloadedSize/totalSize) * 100;
                                textView.setText("" + (int)per + "%" );
                            }
                        };
                        mainHandler.post(myRunnable);
                        output.write(data, 0, count);
                    }
                    output.flush();
                    output.close();
                    input.close();

                    final File finalFile = file1;
                    myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            if (downloadedSize != totalSize){
                                download_video.setVisibility(View.VISIBLE);
                            }else {
                                download_video.setVisibility(View.GONE);

                                Bitmap thumb = ThumbnailUtils.createVideoThumbnail(finalFile.getAbsolutePath(),
                                        MediaStore.Images.Thumbnails.MINI_KIND);
                                BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                                videioView.setBackgroundDrawable(bitmapDrawable);
                            }
                        }
                    };
                    mainHandler.post(myRunnable);




                    myRunnable = new Runnable() {
                        @Override
                        public void run() {
                            progressBar.setVisibility(View.INVISIBLE);
                        }
                    };
                    mainHandler.post(myRunnable);
                }
            } catch (Exception e) {}
            return null;
        }
        protected void onPostExecute(String result) {
            progressBar.setVisibility(View.INVISIBLE);
            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.INVISIBLE);
                    //dialog.dismiss();
                }
            };
            mainHandler.post(myRunnable);
        }
    }


    //// TODO: 10/24/17  .....!!!!
    public void download2(final TextView textView, final ProgressBar progressBar, final String url_, final ImageButton download_video, final VideoView videioView) {
        new B_Task(textView,progressBar,url_,download_video,videioView).execute();

    }
    private class B_Task extends AsyncTask<String,Void,String>{
        TextView textView;
        ProgressBar progressBar;
        String url_;
        ImageButton download_video;
        VideoView videioView;

        public B_Task(TextView textView,ProgressBar progressBar,String url_,ImageButton download_video,VideoView videioView){
            this.textView = textView;
            this.progressBar = progressBar;
            this.url_ = url_;
            this.download_video = download_video;
            this.videioView = videioView;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            Runnable myRunnable = new Runnable() {
                @Override
                public void run() {
                    progressBar.setVisibility(View.VISIBLE);
                }
            };
            mainHandler.post(myRunnable);
        }

        @Override
        protected String doInBackground(String... strings) {
            String downloadUrl = url_;
            String downloadPath = "";
            long downloadedLength = 0;
            long totalSize = 0;
            try {

                Runnable myRunnable = new Runnable() {
                    @Override
                    public void run() {

                    }
                };
                mainHandler.post(myRunnable);


                String sub = url_.substring(url_.lastIndexOf("/"), url_.length());
                String targetFileName = sub;//Change name and subname

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

                //InputStream input = new BufferedInputStream(url.openStream());
                OutputStream output = null;
                File file1 = null;
                String string_path = "";

                if (url_ != null && url_.length() > 4 && Patterns.WEB_URL.matcher(url_).matches()) {
                    String[] splits = url_.split("/");
                    if (splits.length >= 10) {
                        String[] tags = splits[9].split("_");
                        String tag = tags[0];

                        if (tag.equals("IMG")) {
                            file1 = new File(subfolder + "/" + targetFileName);
                            string_path = subfolder + "/" + targetFileName;
                        } else if (tag.equals("VID")){
                            file1 = new File(subfolder2 + "/" + targetFileName);
                            string_path = subfolder2 + "/" + targetFileName;
                        }else {
                            file1 = new File(subfolder3 + "/" + targetFileName);
                            string_path = subfolder3 + "/" + targetFileName;
                        }
                        Log.e("TYPE: ", " TAG: " + tag);
                    }
                }




                File file = new File(downloadPath);
                URL url = new URL(downloadUrl);

                BufferedInputStream inputStream = null;
                BufferedOutputStream outputStream = null;

                URLConnection connection = url.openConnection();
                totalSize = connection.getContentLength();

                if (file1.exists()) {
                    downloadedLength = file.length();
                    connection.setRequestProperty("Range", "bytes=" + downloadedLength + "-");
                    outputStream = new BufferedOutputStream(new FileOutputStream(file1, true));

                } else {
                    outputStream = new BufferedOutputStream(new FileOutputStream(file1));
                }


                connection.connect();

                inputStream = new BufferedInputStream(connection.getInputStream());


                byte[] buffer = new byte[1024 * 8];
                int byteCount;

                while ((byteCount = inputStream.read(buffer)) != -1) {
                    downloadedLength += byteCount;
                    float per = ((float)downloadedLength/totalSize) * 100;

                    final long finalTotalSize = totalSize;
                    myRunnable = new Runnable() {
                        @Override
                        public void run() {

                            progressBar.setProgress(downloadedSize);
                            float per = ((float)downloadedSize/ finalTotalSize) * 100;
                            textView.setText("" + (int)per + "%" );
                        }
                    };
                    mainHandler.post(myRunnable);

                    outputStream.write(buffer, 0, byteCount);
                    break;

                }

                inputStream.close();
                outputStream.flush();
                outputStream.close();

                final long finalTotalSize1 = totalSize;
                final File finalFile = file1;
                myRunnable = new Runnable() {
                    @Override
                    public void run() {
                        if (downloadedSize != finalTotalSize1){
                            download_video.setVisibility(View.VISIBLE);
                        }else {
                            download_video.setVisibility(View.GONE);

                            Bitmap thumb = ThumbnailUtils.createVideoThumbnail(finalFile.getAbsolutePath(),
                                    MediaStore.Images.Thumbnails.MINI_KIND);
                            BitmapDrawable bitmapDrawable = new BitmapDrawable(thumb);
                            videioView.setBackgroundDrawable(bitmapDrawable);
                        }
                    }
                };
                mainHandler.post(myRunnable);



            }catch (Exception e){
                e.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
        }
    }

}
