package com.example.john.mobicare_uganda.views;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.dbsyncing.Appointment_Reciever;
import com.example.john.mobicare_uganda.dbsyncing.Appoitments;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import connectivity.Get_CurrentDateTime;
import server_connections.Doctor_Operations;
import users.Dialog_Message;
import users.User_Details;

/**
 * Created by john on 11/2/17.
 */
public class Scheduler_1 extends AppCompatActivity {
    String date_,time1,time2;
    private TextView selected_date,selected_time,time_from,time_to,date_text,selected_day;
    private DatePicker datePicker;
    private TimePicker timePicker;
    private ImageView imageView;

    int doctor_id,category_id;
    AlarmManager alarmManager;
    private PendingIntent pendingIntent;
    private Context context = this;
    private Button submit_btn;
    int flag1 = 1;
    int flag2 = 1;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.scheduler_1);

        selected_date = (TextView) findViewById(R.id.select_date);
        selected_day = (TextView) findViewById(R.id.select_day);

        selected_time = (TextView) findViewById(R.id.select_t);
        date_text = (TextView) findViewById(R.id.date_text);
        time_from = (TextView) findViewById(R.id.text_time_from);
        time_to = (TextView) findViewById(R.id.text_time_to);

        imageView = (ImageView) findViewById(R.id.imageView);

        timePicker = (TimePicker) findViewById(R.id.tp_timepicker) ;
        datePicker = (DatePicker) findViewById(R.id.simpleDatePicker);
        submit_btn = (Button) findViewById(R.id.submit_btn);

        date_ = getIntent().getStringExtra("date");
        time1 = getIntent().getStringExtra("time1");
        time2 = getIntent().getStringExtra("time2");

        doctor_id = Integer.parseInt(getIntent().getStringExtra("doctor_id"));
        category_id = Integer.parseInt(getIntent().getStringExtra("category_id"));

        date_text.setText(date_);
        time_from.setText(time1);
        time_to.setText(time2);

        selected_time.setText(time1);
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("hh:mm");
            Date date = null;
            date = sdf.parse(time1);

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            timePicker.setCurrentHour(c.get(Calendar.HOUR_OF_DAY));
            timePicker.setCurrentMinute(c.get(Calendar.MINUTE));
            timePicker.setIs24HourView(false);



            int today = Get_CurrentDateTime.getCurrentDay();
            int day_s = Get_CurrentDateTime.getDate_By(date_);
            selected_day.setText(date_);

            if (today == day_s){
                selected_date.setText(new Get_CurrentDateTime().getCurrentDate());

            }else if (today < day_s || today > day_s ){
                int diff = day_s - today;
                String dt = new Get_CurrentDateTime().getCurrentDate();
                SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd");
                Calendar cl = Calendar.getInstance();
                cl.setTime(sf.parse(dt));
                cl.add(Calendar.DATE, diff+7);  // number of days to add
                dt = sf.format(cl.getTime());  // dt is now the new date
                selected_date.setText(dt);
            }


            final String [] splits1 = time1.split(":");
            final String [] splits2 = time2.split(":");
            timePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker timePicker, int hrs, int min) {
                    selected_time.setText(hrs+":"+min);
                    String sel_time = hrs+":"+min;

                    if (hrs >= Integer.parseInt(splits1[0]) && hrs <= Integer.parseInt(splits2[0]) ){
                        sel_time = hrs+":"+min;
                        if (flag2 == 1){
                            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.single_tick));
                            submit_btn.setEnabled(true);
                        }
                        flag1 = 1;
                    }else {
                        sel_time ="("+sel_time+ ")\nOut of Range!";
                        imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                        submit_btn.setEnabled(false);
                        flag1 = 1;
                    }
                    selected_time.setText(sel_time);
                }
            });

            submit_btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    String time_selected = selected_time.getText().toString().trim();
                    String date_selected = selected_date.getText().toString().trim();
                    showDialog(date_selected, time_selected);
                }
            });
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            setMin();
        }catch (Exception e){
            e.printStackTrace();
        }

        setDate();
        try{

        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void setDate(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            date = sdf.parse(new Get_CurrentDateTime().getCurrentDate());

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            datePicker.setMinDate(c.getTimeInMillis());

            datePicker.init(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH), new DatePicker.OnDateChangedListener() {

                @Override
                public void onDateChanged(DatePicker datePicker, int year, int month, int dayOfMonth) {
                    // Log.d("Date", "Year=" + year + " Month=" + (month + 1) + " day=" + dayOfMonth);

                    SimpleDateFormat simpledateformat = new SimpleDateFormat("EEEE");
                    Date date = new Date(year, month, dayOfMonth-1);
                    String dayOfWeek = simpledateformat.format(date);
                    selected_day.setText(dayOfWeek);
                    String select = year+"-"+month+"-"+dayOfMonth;
                    if (dayOfWeek.equals(date_)){
                        selected_date.setText(select);
                        if (flag1 == 1){
                            imageView.setImageDrawable(context.getResources().getDrawable(R.drawable.single_tick));
                            submit_btn.setEnabled(true);
                        }
                        flag2 = 1;
                    }else {
                        selected_date.setText("("+select+")\nOut of Range!");
                        imageView.setImageDrawable(context.getResources().getDrawable(android.R.drawable.ic_menu_close_clear_cancel));
                        submit_btn.setEnabled(false);
                        flag2 = 0;
                    }


                }
            });
            c.add(Calendar.DATE,30);
            datePicker.setMaxDate(c.getTimeInMillis());


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public static String getCurrentDay(){
        String daysArray[] = {"Sunday","Monday","Tuesday", "Wednesday","Thursday","Friday", "Saturday"};
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        return daysArray[day];

    }
    public void setMin(){
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            Date date = null;
            date = sdf.parse(new Get_CurrentDateTime().getCurrentDate());

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            //datePicker.setMinDate(c.getTimeInMillis());
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public void showDialog(final String date, final String ss){
        // final int block_id = new Block_Operations(context).seletedID(number);
        final AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );

        //LayoutInflater inflater = context.getLayoutInflater();
        View view = inflater.inflate(R.layout.appoint_dialog, null);
        dialog.setView(view);

        Button ok_btn = (Button) view.findViewById(R.id.ok_btn);
        Button cancel = (Button) view.findViewById(R.id.cancel_btn);

        final EditText editText = (EditText) view.findViewById(R.id.block_text);
        TextView textView = (TextView) view.findViewById(R.id.msg_txt);
        TextView selected_day = (TextView) view.findViewById(R.id.day_selected);
        TextView selected_date = (TextView) view.findViewById(R.id.date_selected);
        TextView selected_time = (TextView) view.findViewById(R.id.time_selected);

        selected_day.setText(date_);
        selected_date.setText(date);
        selected_time.setText(ss);

        textView.setText("Are you sure you want to schedule appointments to "+new Doctor_Operations(context).getNames(doctor_id)+"?");

        final AlertDialog alert = dialog.create();
        try {
            alert.show();
        }catch (Exception e){
            e.printStackTrace();
        }
        ok_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String body = editText.getText().toString().trim();
                if (!body.equals("")){
                    String phone = new User_Details(context).getContact();
                    ProgressDialog progressDialog = new ProgressDialog(context);
                    progressDialog.setMessage("please wait..");
                    new Appoitments(context).send(date,ss,date_,String.valueOf(doctor_id),String.valueOf(category_id),phone,body,1,progressDialog);
                    //Toast.makeText(context,message,Toast.LENGTH_SHORT).show();
                    try{
                        String[] splits = date.split("-");
                        String[] splits2 = ss.split(":");
                        alert.dismiss();

                          //finish();
                    }catch (Exception e){
                        e.printStackTrace();
                    }
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

    private void setAlarm(int year, int month, int day, int hour, int minute){
        try {
            Calendar c = Calendar.getInstance();
            c.set(year, month, day);
            c.set(Calendar.HOUR_OF_DAY, hour);
            c.set(Calendar.MINUTE, minute);
            alarmManager = (AlarmManager)context. getSystemService(ALARM_SERVICE);
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
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // handle arrow click here
        if (item.getItemId() == android.R.id.home) {
            //Intent intent = new Intent(context, Welcome_Activity.class);
            //startActivity(intent);
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

}
