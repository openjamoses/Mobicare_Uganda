package com.example.john.mobicare_uganda.dbsyncing;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.provider.Settings;
import android.util.Log;

import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;

/**
 * Created by john on 11/4/17.
 */

public class Appointment_Reciever extends BroadcastReceiver {
    private static final String TAG = "Appointment_Reciever";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.e(TAG, "Alarm reciever has been called successfully!");
        showNotification(context);
    }

    public void showNotification(Context context){

        try {
            Uri alarmSound = Uri.parse(ContentResolver.SCHEME_ANDROID_RESOURCE
                    + "://" + context. getPackageName() + "/raw/notification");
            android.support.v4.app.NotificationCompat.Builder builder =
                    new android.support.v4.app.NotificationCompat.Builder(context)
                            .setSmallIcon(android.R.drawable.ic_menu_my_calendar)
                            .setAutoCancel(true)
                            .setSound(alarmSound)
                            .setContentTitle(context.getResources().getString(R.string.app_name))
                            .setContentText("You have made appointment for today.");

            Intent notificationIntent = new Intent(context, MainActivity.class);
            //notificationIntent.putExtra("message", message);
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
            builder.setContentIntent(contentIntent);
            builder.setVibrate(new long[] { 1000, 1000});
            builder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);
            // Add as notification
            NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(0, builder.build());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
