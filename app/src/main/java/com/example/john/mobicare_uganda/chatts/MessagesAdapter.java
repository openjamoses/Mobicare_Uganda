package com.example.john.mobicare_uganda.chatts;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.RecyclerView;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
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
import com.example.john.mobicare_uganda.firebase_collections.util.Downloads;

import java.io.File;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import server_connections.Image_Operations;
import users.CurrentUser;

import static com.example.john.mobicare_uganda.firebase_collections.Config.APP_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.AUDIO_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.IMAGE_SUB_FOLDER;
import static com.example.john.mobicare_uganda.firebase_collections.Config.VIDEO_SUB_FOLDER;

/**
 * Created by john on 10/17/17.
 */

public class MessagesAdapter extends RecyclerView.Adapter<MessagesAdapter.MyViewHolder> {

    private List<Messages> moviesList;
    Context context;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title, year, genre;
        public TextView textView,textView2,image_text1,image_text2,video_text1,video_text2,audio_text1,audio_text2;
        public ImageView imageView,imageView_r,
                imageView_file,imageView_file2,imageView2,
                reciever_img2,reciever_img3,
                audio_img1,audio_img2,video_imageView1,video_imageView2,sender_img2,sender_img3;
        public ImageButton imageButton1,imageButton2,imageButton_pause1,imageButton_pause2;

        public LinearLayout sender_layout,reciever_layout,hide_video,hide_video2,image_layout1,image_layout2,
                audio_layout1,audio_layout2,video_layout1,video_layout2,layout_media1,layout_media2;
        public SeekBar seekBar1,seekBar2;
        public MyViewHolder(View view) {
            super(view);

            sender_layout = (LinearLayout) view.findViewById(R.id.sender_layout);
            reciever_layout = (LinearLayout) view.findViewById(R.id.reciever_layout);

            textView = (TextView) view.findViewById(R.id.recieve_text1);
            textView2 = (TextView) view.findViewById(R.id.recieve_text2);
            imageView = (ImageView) view.findViewById(R.id.reciever_img);
            imageView_r = (ImageView) view.findViewById(R.id.reciever_img);
            imageView_file = (ImageView) view.findViewById(R.id.imageView_file);
            imageView_file2 = (ImageView) view.findViewById(R.id.imageView_file2);
            video_imageView1 = (ImageView) view.findViewById(R.id.video_imageView1);
            video_imageView2 = (ImageView) view.findViewById(R.id.video_imageView2);
            hide_video = (LinearLayout) view.findViewById(R.id.hide_video);
            hide_video2 = (LinearLayout) view.findViewById(R.id.hide_video2);

            image_layout1 = (LinearLayout) view.findViewById(R.id.image_layout1);
            image_layout2 = (LinearLayout) view.findViewById(R.id.imag_layout2);

            audio_layout1 = (LinearLayout) view.findViewById(R.id.audion_layout1);
            audio_layout2 = (LinearLayout) view.findViewById(R.id.audion_layout2);

            video_layout1 = (LinearLayout) view.findViewById(R.id.video_layout1);
            video_layout2 = (LinearLayout) view.findViewById(R.id.video_layout2);

            reciever_img2 = (ImageView) view.findViewById(R.id.reciever_img2);
            reciever_img3 = (ImageView) view.findViewById(R.id.reciever_img3);

            audio_img1 = (ImageView) view.findViewById(R.id.audio_img1);
            audio_img2 = (ImageView) view.findViewById(R.id.audio_img2);

            sender_img2 = (ImageView) view.findViewById(R.id.sender_img2);
            sender_img3 = (ImageView) view.findViewById(R.id.sender_img3);

            image_text1 = (TextView) view.findViewById(R.id.image_text1);
            image_text2 = (TextView) view.findViewById(R.id.image_text2);
            video_text1 = (TextView) view.findViewById(R.id.video_text1);
            video_text2 = (TextView) view.findViewById(R.id.video_text2);

            audio_text1 = (TextView) view.findViewById(R.id.audio_text1);
            audio_text2 = (TextView) view.findViewById(R.id.audio_text2);

            imageButton1 = (ImageButton) view.findViewById(R.id.imageButton1);
            imageButton2 = (ImageButton) view.findViewById(R.id.imageButton2);

            imageButton_pause1 = (ImageButton) view.findViewById(R.id.imageButton_pause1);
            imageButton_pause2 = (ImageButton) view.findViewById(R.id.imageButton_pause2);

            seekBar1 = (SeekBar) view.findViewById(R.id.seekBar1);
            seekBar2 = (SeekBar) view.findViewById(R.id.seekBar2);

            layout_media1 = (LinearLayout) view.findViewById(R.id.layout_media1);
            layout_media2 = (LinearLayout) view.findViewById(R.id.layout_media2);
            imageView2 = (ImageView) view.findViewById(R.id.sender_img);

        }
    }


    public MessagesAdapter(Context context, List<Messages> moviesList) {
        this.moviesList = moviesList;
        this.context = context;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.chatt_lists, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Messages messages = moviesList.get(position);

        Drawable vectorDrawable = ResourcesCompat.getDrawable(context.getResources(), R.drawable.profile2, null);
        Bitmap bitmap3 = ((BitmapDrawable) vectorDrawable).getBitmap();

        Spannable word = new SpannableString(messages.getMessage());
        Spannable wordTwo = new SpannableString("  ("+messages.dates+" )");
        wordTwo.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String f = "";
        if (!messages.getUrl().equals("undefined") ) {
            String[] splits = messages.getUrl().split("/");

            if (splits.length == 10) {
                f = splits[9];
                Log.e("EXTENSION:", "Extension = " + splits[9].substring(splits[9].length() - 3, splits[9].length()));
            }
            Log.e("Chatt_List2", messages.getMessage() + " \t " + messages.getLg() + " \t " +messages.getUrl());
        }
        String f_n = "";
        if (messages.getUrl() != null && messages.getUrl().length() > 4 && Patterns.WEB_URL.matcher(messages.getUrl()).matches() ) {
            f_n = messages.getUrl().substring(messages.getUrl().lastIndexOf("/"+1,messages.getUrl().length()));
        }
        // External sdcard location
        String PATH = Environment.getExternalStorageDirectory()+ "/"+APP_FOLDER+"/";
        File folder = new File(PATH);
        if(!folder.exists()){
            folder.mkdir();//If there is no folder it will be created.
        }
        File file_video = new File(PATH + File.separator + VIDEO_SUB_FOLDER);
        final File file_image = new File(PATH + File.separator + IMAGE_SUB_FOLDER);
        final File file_audio = new File(PATH + File.separator + AUDIO_SUB_FOLDER);
        if(!file_video.exists()){
            file_video.mkdir();
        }
        if(!file_image.exists()){
            file_image.mkdir();
        }
        if(!file_audio.exists()){
            file_audio.mkdir();
        }
        if (!f.equals("") ) {
            String tag = f.split("_")[0];

            File file1 = new File(file_image + "/" + f);
            if (tag.equals("IMG")) {
                if (messages.getMode().equals("doctor")){

                    if (messages.getLg() == 1){
                        setImage(holder.imageView_file,f,file_image,messages.getUrl());
                        holder.reciever_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));

                    }else {
                        setImage(holder.imageView_file2,f,file_image,messages.getUrl());
                        holder.sender_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }
                    holder.hide_video.setVisibility(View.GONE);
                    holder.hide_video2.setVisibility(View.GONE);
                    holder.audio_layout1.setVisibility(View.GONE);
                    holder.audio_layout2.setVisibility(View.GONE);

                }else {
                    if (messages.getLg() == 0){
                        setImage(holder.imageView_file,f,file_image,messages.getUrl());
                        holder.reciever_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));

                    }else {
                        setImage(holder.imageView_file2,f,file_image,messages.getUrl());
                        holder.sender_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }
                    holder.hide_video.setVisibility(View.GONE);
                    holder.hide_video2.setVisibility(View.GONE);
                    holder.audio_layout1.setVisibility(View.GONE);
                    holder.audio_layout2.setVisibility(View.GONE);
                }

                holder.hide_video.setVisibility(View.GONE);
                holder.hide_video2.setVisibility(View.GONE);
                holder.audio_layout1.setVisibility(View.GONE);
                holder.audio_layout2.setVisibility(View.GONE);
            } else if (tag.equals("VID")){
                if (messages.getMode().equals("doctor")){

                    if (messages.getLg() == 1){
                        setVideo(holder.video_layout1,holder.video_imageView1,f,file_video,messages.getUrl());
                        holder.reciever_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        //image_layout2.setVisibility(View.GONE);
                    }else {
                        setVideo(holder.video_layout2,holder.video_imageView2,f,file_video,messages.getUrl());
                        holder.sender_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }
                    holder.image_layout2.setVisibility(View.GONE);
                    holder.image_layout1.setVisibility(View.GONE);
                    holder.imageView_file.setVisibility(View.GONE);
                    holder.imageView_file2.setVisibility(View.GONE);

                    holder.audio_layout1.setVisibility(View.GONE);
                    holder.audio_layout2.setVisibility(View.GONE);

                }else {
                    if (messages.getMode().equals("doctor")){
                        if (messages.getLg() == 1){
                            setVideo(holder.video_layout1,holder.video_imageView1,f,file_video,messages.getUrl());
                            holder.reciever_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                            //image_layout2.setVisibility(View.GONE);
                        }else {
                            setVideo(holder.video_layout2,holder.video_imageView2,f,file_video,messages.getUrl());
                            holder.sender_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        holder.image_layout2.setVisibility(View.GONE);
                        holder.image_layout1.setVisibility(View.GONE);
                        holder.imageView_file.setVisibility(View.GONE);
                        holder.imageView_file2.setVisibility(View.GONE);

                        holder.audio_layout1.setVisibility(View.GONE);
                        holder.audio_layout2.setVisibility(View.GONE);

                    }else {
                        if (messages.getLg() == 0){
                            setVideo(holder.video_layout1,holder.video_imageView1,f,file_video,messages.getUrl());
                            holder.reciever_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                            //image_layout2.setVisibility(View.GONE);
                        }else {
                            setVideo(holder.video_layout2,holder.video_imageView2,f,file_video,messages.getUrl());
                            holder.sender_img3.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                        }
                        holder.image_layout2.setVisibility(View.GONE);
                        holder.image_layout1.setVisibility(View.GONE);
                        holder.imageView_file.setVisibility(View.GONE);
                        holder.imageView_file2.setVisibility(View.GONE);

                        holder.audio_layout1.setVisibility(View.GONE);
                        holder.audio_layout2.setVisibility(View.GONE);
                    }

                }

            }else {
                if (messages.getMode().equals("doctor")){
                    if (messages.getLg() == 1){
                        setAudio(holder.imageButton1,holder.imageButton_pause1,holder.seekBar1,f,file_audio,messages.getUrl());
                        holder.audio_img1.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }else {
                        setAudio(holder.imageButton2,holder.imageButton_pause2,holder.seekBar2,f,file_audio,messages.getUrl());
                        holder.audio_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }
                    holder.hide_video.setVisibility(View.GONE);
                    holder.hide_video2.setVisibility(View.GONE);
                    holder.image_layout2.setVisibility(View.GONE);
                    holder.image_layout1.setVisibility(View.GONE);


                    Log.e("Chatts Lists2","AUDI TAG was found: "+tag);
                }else {
                    if (messages.getLg() == 0){
                        setAudio(holder.imageButton1,holder.imageButton_pause1,holder.seekBar1,f,file_audio,messages.getUrl());
                        holder.audio_img1.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }else {
                        setAudio(holder.imageButton2,holder.imageButton_pause2,holder.seekBar2,f,file_audio,messages.getUrl());
                        holder.audio_img2.setImageBitmap(Convert.getclip(CompressBitmap.compress(bitmap3)));
                    }
                    holder.hide_video.setVisibility(View.GONE);
                    holder.hide_video2.setVisibility(View.GONE);
                    holder.image_layout2.setVisibility(View.GONE);
                    holder.image_layout1.setVisibility(View.GONE);


                    Log.e("Chatts Lists2","AUDI TAG was found: "+tag);
                }

            }
            holder.sender_layout.setVisibility(View.GONE);
            holder.reciever_layout.setVisibility(View.GONE);

            holder.image_text1.setText(word);
            holder.image_text2.setText(word);
            holder.image_text1.append(wordTwo);
            holder.image_text2.append(wordTwo);


            holder.audio_text1.setText(word);
            holder.audio_text2.setText(word);
            holder.audio_text1.append(wordTwo);
            holder.audio_text2.append(wordTwo);

            holder.video_text1.setText(word);
            holder.video_text2.setText(word);
            holder.video_text1.append(wordTwo);
            holder.video_text2.append(wordTwo);

        }else {
            holder.layout_media1.setVisibility(View.GONE);
            holder.layout_media2.setVisibility(View.GONE);
            holder.audio_layout1.setVisibility(View.GONE);
            holder.audio_layout2.setVisibility(View.GONE);
        }

        Log.e("Chatt@2", "URL: "+file_image+"/"+f_n);
        try{
            final String finalF_n = f_n;

            // videoView.setVideoPath(file_image.getPath()+"/"+f_n);
            if (messages.getMessage().equals("")){
                holder.textView.setVisibility(View.GONE);
                holder.textView2.setVisibility(View.GONE);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        if (messages.getMode().equals("doctor")){
            if (messages.getLg() == 1){
                holder.textView.setText(word);
                ///

                holder.textView.append(wordTwo);
                holder.sender_layout.setVisibility(View.GONE);
                holder.layout_media2.setVisibility(View.GONE);
            }else {
                holder.textView2.setText(word);
                holder.textView2.append(wordTwo);
                holder.reciever_layout.setVisibility(View.GONE);
                holder.layout_media1.setVisibility(View.GONE);
            }
        }else {
            if (messages.getLg() == 0){
                holder.textView.setText(word);
                ///

                holder.textView.append(wordTwo);
                holder.sender_layout.setVisibility(View.GONE);
                holder.layout_media2.setVisibility(View.GONE);
            }else {
                holder.textView2.setText(word);
                holder.textView2.append(wordTwo);
                holder.reciever_layout.setVisibility(View.GONE);
                holder.layout_media1.setVisibility(View.GONE);
            }
        }

        Bitmap bitmap = null,bitmap1 = null;
        final String img_url = new Image_Operations(context).image( new CurrentUser(context).current());
        if (img_url != null){
            //Convert.getclip(Downloads.getBitmap(img_url));
            Drawable d = holder.imageView2.getDrawable();
            if (d != null) {
               // Bitmap bitmaps = ((BitmapDrawable)d).getBitmap();
                //holder.imageView2.setImageBitmap(Convert.getclip(bitmaps));
               // Bitmap b = new Downloads(context).getBitmap(img_url,holder.imageView2);
            }

            Log.e("Chats ....",img_url);

        }
        try {
            Bitmap bitmap_1 = Convert.getclip(messages.getBmp());
            Bitmap bitmap_2 = Convert.getclip(bitmap3);
            holder.imageView.setImageBitmap(bitmap_2);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return moviesList.size();
    }
    private void setAudio(final ImageButton imageButton,final ImageButton imageButton2, final SeekBar seek_bar, String f, File file, String url) {
        final Timer timer = new Timer();
        imageButton.setEnabled(false);
        imageButton2.setVisibility(View.GONE);
        final boolean[] play = {false};
        Log.e("Audio file: ", "********************************" + file + "/" + f);
        File file1 = new File(file + "/" + f);
        final MediaPlayer mp = new MediaPlayer();
        final Handler seekHandler = new Handler();

        String audio_path = "";
        if (file1.exists()) {
            audio_path = file.getAbsolutePath() + "/" + f;
        } else {
            audio_path = url;
        }
        Log.e("AUDIO PATH", audio_path);
        try {
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

        } catch (Exception e) {
            e.printStackTrace();
            Log.e("Audio erro: ", "********************************");
        }
    }

    public void setImage(final ImageView imageView, final String f, final File file, final String url){
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



        }else {
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
    public void setVideo(final LinearLayout layout, final ImageView imageView, String f, File file, String url){
        MediaPlayer mediaPlayer;
        MediaController mediaControls = null;
        final int position = 0;
        if (mediaControls == null) {
            // create an object of media controller class
            mediaControls = new MediaController(context);
            // mediaControls.setAnchorView(videoView);
        }
        String paths = "";
        if (file.exists()){
            String[] tags = f.split("_");
            // new VideoPlayer(context,videoView,file+"/"+f);
            paths = file+"/"+f;
        }else {
            paths = url;
            //videoView.setVideoPath(url);
        }

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
