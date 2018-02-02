package com.example.john.mobicare_uganda.welcome;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.example.john.mobicare_uganda.MainActivity;
import com.example.john.mobicare_uganda.R;
import com.example.john.mobicare_uganda.authentications.PhoneVerification;
import com.example.john.mobicare_uganda.fragments.Dashboard_Fragment;
import com.example.john.mobicare_uganda.fragments.Doctor_Fragment;
import com.example.john.mobicare_uganda.fragments.Menu_Fragment;
import com.example.john.mobicare_uganda.views.Profile_P;

import java.util.ArrayList;
import java.util.List;

import connectivity.SessionManager;
import users.User_Details;

/**
 * Created by john on 1/22/18.
 */

public class MainActivity2 extends AppCompatActivity {
    private Toolbar toolbar;
    private Context context = this;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar = (Toolbar) findViewById(R.id.toolBar);
        setSupportActionBar(toolbar);

        int[] icons = {R.drawable.ic_dashboard_black_24dp,
                R.drawable.search_normal,
                R.drawable.home_normal
        };
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        ViewPager viewPager = (ViewPager) findViewById(R.id.main_tab_content);

        setupViewPager(viewPager);


        tabLayout.setupWithViewPager(viewPager);

        for (int i = 0; i < icons.length; i++) {
            tabLayout.getTabAt(i).setIcon(icons[i]);
        }
        tabLayout.getTabAt(0).select();
    }

    private void setupViewPager(ViewPager viewPager) {
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.insertNewFragment(new Dashboard_Fragment());
        adapter.insertNewFragment(new Doctor_Fragment());
        adapter.insertNewFragment(new Menu_Fragment());
        // adapter.insertNewFragment(new SearchFragment());
        viewPager.setAdapter(adapter);
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        public ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        public void insertNewFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        try {
            switch (id) {
                case android.R.id.home:
                    finish();
                    return true;
                case R.id.action_share:
                    shareApp(context);
                    return true;
                case R.id.action_logout:
                    // todo: goto back activity from here
                    SessionManager session = new SessionManager(getApplicationContext());
                    session.logoutUser();
                    Intent intents = new Intent(getApplicationContext(), Welcome_Activity.class);
                    intents.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intents);
                    finish();
                    //Intent intentss = new Intent(context, ProcessingService.class);
                    //stopService(intentss);
                    return true;
                case R.id.action_profile:
                    Intent intent = new Intent(context, Profile_P.class);
                    startActivity(intent);
                case R.id.action_switch:
                    Intent intentsx = new Intent(context, PhoneVerification.class);
                    startActivity(intentsx);
                    //finish();
                default:
                    return super.onOptionsItemSelected(item);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return super.onOptionsItemSelected(item);

    }
    private  void shareApp(Context context) {

        String names = new User_Details(context).getName();
        int applicationNameId = context.getApplicationInfo().labelRes;
        final String appPackageName = context.getPackageName();
        Intent i = new Intent(Intent.ACTION_SEND);
        i.setType("text/plain");
        i.putExtra(Intent.EXTRA_SUBJECT, context.getResources().getString(applicationNameId));
        String text = names+" invites you to install Mobicare application for Medical Consultations: ";
        String link = "https://play.google.com/store/apps/details?id=" + appPackageName;
        i.putExtra(Intent.EXTRA_TEXT, text + " " + link);
        startActivity(Intent.createChooser(i, "Share link:"));
    }
}
