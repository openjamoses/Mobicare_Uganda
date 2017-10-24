package com.example.john.mobicare_uganda;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

import adapters.Schedules;
import adapters.schules_s;
import server_connections.Schedule_Operations;
import users.Doctor_Details;

/**
 * Created by john on 10/6/17.
 */

public class Scheduless_main extends AppCompatActivity {

    CollapsingToolbarLayout collapsingToolbar;
    ImageView imageView;
    private Context context = this;
    private static final String TAG = "Schedule_Activity";

    private List<String> list1 = new ArrayList<>();
    private List<String> list2 = new ArrayList<>();
    private List<String> list3 = new ArrayList<>();
    private List< List<String> > allList = new ArrayList<>();

    private ListView listView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.schedule_d);

            // add back arrow to toolbar
            if (getSupportActionBar() != null) {
                getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                getSupportActionBar().setDisplayShowHomeEnabled(true);
            }

            listView = (ListView) findViewById(R.id.listView);
            addValueToList();
            collapsingToolbar =
                    (CollapsingToolbarLayout) findViewById(R.id.toolbar_layout);


            collapsingToolbar.setExpandedTitleTextAppearance(R.style.expandedappbar);
            collapsingToolbar.setCollapsedTitleTextAppearance(R.style.collapsedappbar);
        }catch (Exception e){
            e.printStackTrace();
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
    private void addValueToList() {

        try {
            String doctor_id = new Doctor_Details(context).getDoctor_id();
            allList = new Schedule_Operations(context).getAll();
            list1 = allList.get(1);
            list2 = allList.get(2);
            list3 = allList.get(0);

            String[] name = new String[list3.size()];
            name = list3.toArray(name);

            String[] date = new String[list1.size()];
            date = list1.toArray(date);

            String[] time = new String[list2.size()];
            time = list2.toArray(time);

            schules_s customAdapter = new schules_s(context, date, time, name);
            listView.setAdapter(customAdapter);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
}
