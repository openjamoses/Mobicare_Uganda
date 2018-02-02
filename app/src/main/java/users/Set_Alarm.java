package users;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.john.mobicare_uganda.dbsyncing.Appointment_Reciever;

import java.util.Calendar;

import static android.content.Context.ALARM_SERVICE;

/**
 * Created by john on 1/22/18.
 */

public class Set_Alarm {
    private Context context;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    public Set_Alarm(Context context){
        this.context = context;
        alarmManager = (AlarmManager)context. getSystemService(ALARM_SERVICE);
    }
    public void setAlarm(int year, int month, int day, int hour, int minute){
        try {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);

            Intent myIntent = new Intent(context, Appointment_Reciever.class);
            pendingIntent = PendingIntent.getBroadcast(context, 192837, myIntent, 0);
            alarmManager.set(AlarmManager.RTC_WAKEUP, c.getTimeInMillis(), pendingIntent);

        }catch (Exception e){
            Log.e("Alarm Error: ","Error occured: "+e);
        }
    }
    private void cancelAlarm() {
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
        }
    }
}
