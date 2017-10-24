package com.example.john.mobicare_uganda.chatts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.media.ThumbnailUtils;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.firebase_collections.util.CompressBitmap;
import com.example.john.mobicare_uganda.firebase_collections.util.Convert;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import server_connections.Image_Operations;
import users.CurrentUser;
import users.Image;

import static com.example.john.mobicare_uganda.firebase_collections.Config.APP_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.AUDIO_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.IMAGE_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.VIDEO_SUB_FOLDER;

/**
 * Created by john on 10/22/17.
 */

public class C_ListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    List<String> message, dates,url;
    LayoutInflater inflter;
    List<Long> fetch;
    List<Long> lg;
    String mode;
    private int screen_w = 0, screen_h = 0;

    public C_ListAdapter(Activity context, List<String> message, List<String> dates, List<Long> lg, List<Long> fetch, List<String> url, String mode) {
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
        View view=inflater.inflate(R.layout.chatt_lists, null,true);

        try {
            LinearLayout sender_layout = (LinearLayout) view.findViewById(R.id.sender_layout);
            LinearLayout reciever_layout = (LinearLayout) view.findViewById(R.id.reciever_layout);

            TextView textView = (TextView) view.findViewById(R.id.recieve_text1);
            TextView textView2 = (TextView) view.findViewById(R.id.recieve_text2);
            ImageView imageView = (ImageView) view.findViewById(R.id.reciever_img);
            ImageView imageView_r = (ImageView) view.findViewById(R.id.reciever_img);
            final ImageView imageView_file = (ImageView) view.findViewById(R.id.imageView_file);
            final ImageView imageView_file2 = (ImageView) view.findViewById(R.id.imageView_file2);
            ImageView video_imageView1 = (ImageView) view.findViewById(R.id.video_imageView1);
            ImageView video_imageView2 = (ImageView) view.findViewById(R.id.video_imageView2);
            LinearLayout hide_video = (LinearLayout) view.findViewById(R.id.hide_video);
            LinearLayout hide_video2 = (LinearLayout) view.findViewById(R.id.hide_video2);

            LinearLayout image_layout1 = (LinearLayout) view.findViewById(R.id.image_layout1);
            LinearLayout image_layout2 = (LinearLayout) view.findViewById(R.id.imag_layout2);

            LinearLayout audio_layout1 = (LinearLayout) view.findViewById(R.id.audion_layout1);
            LinearLayout audio_layout2 = (LinearLayout) view.findViewById(R.id.audion_layout2);

            LinearLayout video_layout1 = (LinearLayout) view.findViewById(R.id.video_layout1);
            LinearLayout video_layout2 = (LinearLayout) view.findViewById(R.id.video_layout2);

            ImageView reciever_img2 = (ImageView) view.findViewById(R.id.reciever_img2);
            ImageView reciever_img3 = (ImageView) view.findViewById(R.id.reciever_img3);

            ImageView audio_img1 = (ImageView) view.findViewById(R.id.audio_img1);
            ImageView audio_img2 = (ImageView) view.findViewById(R.id.audio_img2);

            ImageView sender_img2 = (ImageView) view.findViewById(R.id.sender_img2);
            ImageView sender_img3 = (ImageView) view.findViewById(R.id.sender_img3);

            TextView image_text1 = (TextView) view.findViewById(R.id.image_text1);
            TextView image_text2 = (TextView) view.findViewById(R.id.image_text2);
            TextView video_text1 = (TextView) view.findViewById(R.id.video_text1);
            TextView video_text2 = (TextView) view.findViewById(R.id.video_text2);

            TextView audio_text1 = (TextView) view.findViewById(R.id.audio_text1);
            TextView audio_text2 = (TextView) view.findViewById(R.id.audio_text2);

            ImageButton imageButton1 = (ImageButton) view.findViewById(R.id.imageButton1);
            ImageButton imageButton2 = (ImageButton) view.findViewById(R.id.imageButton2);

            ImageButton imageButton_pause1 = (ImageButton) view.findViewById(R.id.imageButton_pause1);
            ImageButton imageButton_pause2 = (ImageButton) view.findViewById(R.id.imageButton_pause2);

            ImageButton download_img = (ImageButton) view.findViewById(R.id.download_img);
            ImageButton download_video = (ImageButton) view.findViewById(R.id.download_video);
            ImageButton download_video2 = (ImageButton) view.findViewById(R.id.download_video2);

            SeekBar seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
            SeekBar seekBar2 = (SeekBar) view.findViewById(R.id.seekBar2);

            //hide_video.setVisibility(View.GONE);
            //screen_w = 310;
            //screen_h = 300;
            //videoView.getLayoutParams().height = screen_w;
            //videoView.getLayoutParams().width = screen_w;

            //videoView2.getLayoutParams().height = screen_w;
            //videoView2.getLayoutParams().width = screen_w;
            LinearLayout layout_media1 = (LinearLayout) view.findViewById(R.id.layout_media1);
            LinearLayout layout_media2 = (LinearLayout) view.findViewById(R.id.layout_media2);

            Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.profile2, null);
            Bitmap bitmap3 = ((BitmapDrawable) vectorDrawable).getBitmap();

            Spannable word = new SpannableString(message.get(i));
            Spannable wordTwo = new SpannableString("  (" + dates.get(i) + " )");
            wordTwo.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

            String f = "";
            if (!url.get(i).equals("undefined")) {
                String[] splits = url.get(i).split("/");

                if (splits.length == 10) {
                    f = splits[9];
                    Log.e("EXTENSION:", "Extension = " + splits[9].substring(splits[9].length() - 3, splits[9].length()));
                }
                Log.e("Chatt_List2", message.get(i) + " \t " + lg.get(i) + " \t " + url.get(i));
            }
            String f_n = "";
            if (url.get(i) != null && url.get(i).length() > 4 && Patterns.WEB_URL.matcher(url.get(i)).matches()) {
                f_n = url.get(i).substring(url.get(i).lastIndexOf("/" + 1, url.get(i).length()));
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
                if (tag.equals("IMG")) {
                    if (mode.equals("doctor")) {

                        if (lg.get(i) == 1) {
                            setImage(imageView_file, f, file_image, url.get(i),download_img);
                            reciever_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));

                        } else {
                            setImage(imageView_file2, f, file_image, url.get(i),download_img);
                            sender_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        hide_video.setVisibility(View.GONE);
                        hide_video2.setVisibility(View.GONE);
                        audio_layout1.setVisibility(View.GONE);
                        audio_layout2.setVisibility(View.GONE);

                    } else {
                        if (lg.get(i) == 0) {
                            setImage(imageView_file, f, file_image, url.get(i),download_img);
                            reciever_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));

                        } else {
                            setImage(imageView_file2, f, file_image, url.get(i),download_img);
                            sender_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        hide_video.setVisibility(View.GONE);
                        hide_video2.setVisibility(View.GONE);
                        audio_layout1.setVisibility(View.GONE);
                        audio_layout2.setVisibility(View.GONE);
                    }

                    hide_video.setVisibility(View.GONE);
                    hide_video2.setVisibility(View.GONE);
                    audio_layout1.setVisibility(View.GONE);
                    audio_layout2.setVisibility(View.GONE);
                } else if (tag.equals("VID")) {
                    if (mode.equals("doctor")) {

                        if (lg.get(i) == 1) {
                            setVideo(video_layout1, video_imageView1, f, file_video, url.get(i),download_video);
                            reciever_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                            //image_layout2.setVisibility(View.GONE);
                        } else {
                            setVideo(video_layout2, video_imageView2, f, file_video, url.get(i),download_video);
                            sender_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        image_layout2.setVisibility(View.GONE);
                        image_layout1.setVisibility(View.GONE);
                        imageView_file.setVisibility(View.GONE);
                        imageView_file2.setVisibility(View.GONE);

                        audio_layout1.setVisibility(View.GONE);
                        audio_layout2.setVisibility(View.GONE);

                    } else {
                        if (mode.equals("doctor")) {
                            if (lg.get(i) == 1) {
                                setVideo(video_layout1, video_imageView1, f, file_video, url.get(i),download_video);
                                reciever_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                                //image_layout2.setVisibility(View.GONE);
                            } else {
                                setVideo(video_layout2, video_imageView2, f, file_video, url.get(i),download_video);
                                sender_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                            }
                            image_layout2.setVisibility(View.GONE);
                            image_layout1.setVisibility(View.GONE);
                            imageView_file.setVisibility(View.GONE);
                            imageView_file2.setVisibility(View.GONE);

                            audio_layout1.setVisibility(View.GONE);
                            audio_layout2.setVisibility(View.GONE);

                        } else {
                            if (lg.get(i) == 0) {
                                setVideo(video_layout1, video_imageView1, f, file_video, url.get(i),download_video);
                                reciever_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                                //image_layout2.setVisibility(View.GONE);
                            } else {
                                setVideo(video_layout2, video_imageView2, f, file_video, url.get(i),download_video);
                                sender_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                            }
                            image_layout2.setVisibility(View.GONE);
                            image_layout1.setVisibility(View.GONE);
                            imageView_file.setVisibility(View.GONE);
                            imageView_file2.setVisibility(View.GONE);

                            audio_layout1.setVisibility(View.GONE);
                            audio_layout2.setVisibility(View.GONE);
                        }

                    }

                } else {
                    if (mode.equals("doctor")) {
                        if (lg.get(i) == 1) {
                            setAudio(imageButton1, imageButton_pause1, seekBar1, f, file_audio, url.get(i));
                            audio_img1.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        } else {
                            setAudio(imageButton2, imageButton_pause2, seekBar2, f, file_audio, url.get(i));
                            audio_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        hide_video.setVisibility(View.GONE);
                        hide_video2.setVisibility(View.GONE);
                        image_layout2.setVisibility(View.GONE);
                        image_layout1.setVisibility(View.GONE);


                        Log.e("Chatts Lists2", "AUDI TAG was found: " + tag);
                    } else {
                        if (lg.get(i) == 0) {
                            setAudio(imageButton1, imageButton_pause1, seekBar1, f, file_audio, url.get(i));
                            audio_img1.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        } else {
                            setAudio(imageButton2, imageButton_pause2, seekBar2, f, file_audio, url.get(i));
                            audio_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        hide_video.setVisibility(View.GONE);
                        hide_video2.setVisibility(View.GONE);
                        image_layout2.setVisibility(View.GONE);
                        image_layout1.setVisibility(View.GONE);


                        Log.e("Chatts Lists2", "AUDI TAG was found: " + tag);
                    }

                }
                sender_layout.setVisibility(View.GONE);
                reciever_layout.setVisibility(View.GONE);

                image_text1.setText(word);
                image_text2.setText(word);
                image_text1.append(wordTwo);
                image_text2.append(wordTwo);


                audio_text1.setText(word);
                audio_text2.setText(word);
                audio_text1.append(wordTwo);
                audio_text2.append(wordTwo);

                video_text1.setText(word);
                video_text2.setText(word);
                video_text1.append(wordTwo);
                video_text2.append(wordTwo);

            } else {
                layout_media1.setVisibility(View.GONE);
                layout_media2.setVisibility(View.GONE);
                audio_layout1.setVisibility(View.GONE);
                audio_layout2.setVisibility(View.GONE);
            }
            Log.e("Chatt@2", "URL: " + file_image + "/" + f_n);
            try {
                final String finalF_n = f_n;

                // videoView.setVideoPath(file_image.getPath()+"/"+f_n);
                if (message.get(i).equals("")) {
                    textView.setVisibility(View.GONE);
                    textView2.setVisibility(View.GONE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            final ImageView imageView2 = (ImageView) view.findViewById(R.id.sender_img);


            if (mode.equals("doctor")) {
                try {
                    if (lg.get(i) == 1) {
                        textView.setText(word);
                        ///

                        textView.append(wordTwo);
                        sender_layout.setVisibility(View.GONE);
                        layout_media2.setVisibility(View.GONE);
                    } else {
                        textView2.setText(word);
                        textView2.append(wordTwo);
                        reciever_layout.setVisibility(View.GONE);
                        layout_media1.setVisibility(View.GONE);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                if (lg.get(i) == 0) {
                    textView.setText(word);
                    ///

                    textView.append(wordTwo);
                    sender_layout.setVisibility(View.GONE);
                    layout_media2.setVisibility(View.GONE);
                } else {
                    textView2.setText(word);
                    textView2.append(wordTwo);
                    reciever_layout.setVisibility(View.GONE);
                    layout_media1.setVisibility(View.GONE);
                }
            }

            try {
                Bitmap bitmap = null, bitmap1 = null;
                final String img_url = new Image_Operations(context).image(new CurrentUser(context).current());

                Bitmap bitmap_2 = Convert.getclip(bitmap3);
                imageView.setImageBitmap(bitmap_2);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }

    private void setAudio(final ImageButton imageButton,final ImageButton imageButton2, final SeekBar seek_bar, String f, File file, String url) {
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
        }else {
            audio_path = url;
        }
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

        }catch (Exception e){
            e.printStackTrace();
            Log.e("Audio erro: ","********************************");
        }
    }



    public void setImage(final ImageView imageView, final String f, final File file, final String url,final ImageButton download_img){
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
        }
    }

    public void setVideo(final LinearLayout layout, final ImageView imageView, String f, File file, String url,ImageButton download_video){

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
            if (file.exists()) {
                String[] tags = f.split("_");
                // new VideoPlayer(context,videoView,file+"/"+f);
                paths = file + "/" + f;
                download_video.setVisibility(View.GONE);
            } else {
                paths = url;
                download_video.setVisibility(View.VISIBLE);
                //videoView.setVideoPath(url);
            }
            Bitmap thumbnail = ThumbnailUtils.createVideoThumbnail(paths,
                    MediaStore.Images.Thumbnails.MINI_KIND);
            imageView.setImageBitmap(thumbnail);

            final String finalPaths = paths;
            layout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, com.example.john.mobicare_uganda.firebase_collections.util.VideoPlayer.class);
                    intent.putExtra("paths", finalPaths);
                    context.startActivity(intent);
                }
            });
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, com.example.john.mobicare_uganda.firebase_collections.util.VideoPlayer.class);
                    intent.putExtra("paths", finalPaths);
                    context.startActivity(intent);
                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
        /**
         videoView.setMediaController(mediaControls);
         videoView.requestFocus();
         videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
         public MediaPlayer mediaPlayer;
         @Override
         public void onPrepared(MediaPlayer mediaPlayer) {
         // if we have a position on savedInstanceState, the video
         // playback should start from here
         videoView.seekTo(position);

         System.out.println("vidio is ready for playing");

         videoView.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View view) {
         if (position == 0)
         {
         videoView.start();
         } else
         {
         // if we come from a resumed activity, video playback will
         // be paused
         videoView.pause();
         }
         }
         });


         }
         });
         // implement on completion listener on video view
         videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
         @Override
         public void onCompletion(MediaPlayer mp) {
         //Toast.makeText(getApplicationContext(), "Thank You...!!!", Toast.LENGTH_LONG).show(); // display a toast when an video is completed
         }
         });
         videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
         @Override
         public boolean onError(MediaPlayer mp, int what, int extra) {
         //Toast.makeText(getApplicationContext(), "Oops An Error Occur While Playing Video...!!!", Toast.LENGTH_LONG).show(); // display a toast when an error is occured while playing an video
         return false;
         }
         });

         ****/
    }
}