package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.example.john.mobicare_uganda.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by john on 10/6/17.
 */

public class schules_s extends BaseAdapter {
    Context context;
    String[] date, time;
    LayoutInflater inflter;
    private Button pick_time,save_appointment;
    List<String> list = new ArrayList<>();
    String[] name;


    public schules_s(Context applicationContext, String[] date, String[] time, String name[]) {
        this.context = applicationContext;
        this.date = date;
        this.time = time;
        list.add("Date");
        this.name = name;

        inflter = (LayoutInflater.from(applicationContext));
    }

    @Override
    public int getCount() {
        return date.length;
    }

    @Override
    public Object getItem(int i) {
        return null;
    }

    @Override
    public long getItemId(int i) {
        return 0;
    }

    @Override
    public View getView(final int i, View view, ViewGroup viewGroup) {
        view = inflter.inflate(R.layout.list_s2, null);
        try {
            TextView textView1 = (TextView) view.findViewById(R.id.text_date);
            TextView textView2 = (TextView) view.findViewById(R.id.text_time);
            TextView textView3 = (TextView) view.findViewById(R.id.name_txt);

            textView1.setText(date[i]);
            textView2.setText(time[i]);
            textView3.setText(name[i]);

        }catch (Exception e){
            e.printStackTrace();
        }


        return view;
    }
}
