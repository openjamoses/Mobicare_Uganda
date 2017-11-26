package adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.firebase_collections.util.Convert;

import java.util.List;

import connectivity.DBHelper;
import server_connections.Fetch;
import server_connections.Updates;

import static connectivity.Constants.config.APPOINTMENT_DOCTOR_URL;
import static connectivity.Constants.config.OPERATION_APPOINTMENT;
import static connectivity.Constants.config.OPERATION_DOCTORS;
import static connectivity.Constants.config.UPDATE_APPOINTMENTS_DOCTOR_URL;

/**
 * Created by john on 10/6/17.
 */

public class Appointments_pat_list extends BaseAdapter {
    Context context;
    List<String> date, time,status,body,d,t;
    LayoutInflater inflter;
    List<Integer>  ids;
    List<String> names;

    private static final String TAG = "Appointment_Adapter";

    public Appointments_pat_list(Context applicationContext, List<String> names, List<String> date, List<String> time, List<String> status, List<String> body, List<String> d, List<String> t, List<Integer> ids) {
        this.context = applicationContext;
        this.date = date;
        this.time = time;
        this.status = status;
        this.body = body;
        this.d = d;
        this.t = t;
        this.ids = ids;
        this.names = names;
        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return date.size();
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.app_list2, null);

        TextView date_textView = (TextView) view.findViewById(R.id.date_text);
        TextView time_textView = (TextView) view.findViewById(R.id.time_text);
        TextView name_textView = (TextView) view.findViewById(R.id.name_text);

        LinearLayout layout = (LinearLayout) view.findViewById(R.id.message_layout);


        try {

            date_textView.setText(date.get(i));
            time_textView.setText(time.get(i));
            name_textView.setText(names.get(i));

            //status_textView.setText(status[i]);

            setMessages(body.get(i), status.get(i), d.get(i), t.get(i),layout);
        }catch (Exception e){
            e.printStackTrace();
        }
        return view;
    }
    /**
     *
     * @param body
     * @param layout
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void setMessages(String body, String status, String date, String time, LinearLayout layout){
        LinearLayout[] parent = null;
        ImageView[] img_arrow = null;
        ImageView[] img_pics = null;
        TextView[] textview = null;

        LinearLayout.LayoutParams l_param = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params_txt = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        LinearLayout.LayoutParams params_arrow = new LinearLayout.LayoutParams(50, 50);
        LinearLayout.LayoutParams params_img = new LinearLayout.LayoutParams(80, 80);


        params_txt.setMargins(0,10,0,0);
        params_txt.weight = 6.0f;
        params_arrow.setMargins(0,6,0-15,0);
        params_arrow.weight = 1.0f;
        params_img.setMargins(0,0,0,0);
        params_img.weight = 3.0f;
        l_param.setMargins(0,10,0,0);

        int total = 1;
        int count = 0;
        //if(cursor.moveToFirst()){

        parent = new LinearLayout[total];
        img_arrow = new ImageView[total];
        img_pics = new ImageView[total];
        textview = new TextView[total];

        parent[count] = new LinearLayout(context);

        parent[count].setLayoutParams(l_param);
        parent[count].setOrientation(LinearLayout.HORIZONTAL);
        parent[count].setPadding(0,20,0,0);

        img_arrow[count] = new ImageView(context);
        img_pics[count] = new ImageView(context);
        textview[count] = new TextView(context);

        textview[count].setLayoutParams(params_txt);
        textview[count].setBackgroundDrawable(context.getDrawable(R.drawable.rounded_corner));
        textview[count].setTextColor(Color.parseColor("#000000"));

        Spannable word = new SpannableString(body);

        word.setSpan(new ForegroundColorSpan(Color.BLACK), 0, word.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        textview[count].setText(word);
        Spannable wordTwo = new SpannableString("  ("+date+", "+time+" )");

        wordTwo.setSpan(new ForegroundColorSpan(context.getResources().getColor(R.color.colorAccent)), 0, wordTwo.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        textview[count].append(wordTwo);

        if(status.equals("pending")){
            textview[count].setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.presence_away, 0);
        }else if (status.equals("cancel")){
            textview[count].setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.ic_delete, 0);
        }else {
            textview[count].setCompoundDrawablesWithIntrinsicBounds(0,0,android.R.drawable.checkbox_on_background, 0);
        }
        textview[count].setClickable(true);
        textview[count].setPadding(20,20,20,20);

        img_arrow[count].setLayoutParams(params_arrow);
        img_arrow[count].setImageResource(R.drawable.arrow_bg1);
        img_pics[count].setLayoutParams(params_img);

        img_pics[count].setImageResource(R.drawable.default_profile);


        parent[count].addView(img_arrow[count]);
        img_arrow[count].setVisibility(View.INVISIBLE);
        parent[count].addView(textview[count]);
        layout.addView(parent[count]);

    }
}