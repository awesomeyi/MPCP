package com.yizzle.mpcp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.yizzle.mpcp.WebAPI.SessionAPI;

import org.json.JSONObject;

public class BankActivity extends AppCompatActivity {

    private String username;
    private Fragment viewAccount = ViewAccount.newInstance();
    private Fragment viewTransfer = ViewTransfer.newInstance();

    private void reload() {
        Log.d("SESSION", "reload");
        new FetchData().execute();
        ((ViewAccount) viewAccount).reload();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bank);

        //Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        setTitle("Welcome,");

        //ViewPager
        ViewPager viewPager = (ViewPager) findViewById(R.id.view_pager);
        MyPagerAdapter pagerAdapter = new MyPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);

        //Tabs
        TabLayout tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        tabLayout.setTabsFromPagerAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        final Intent it = new Intent(this, CreateTransfer.class);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(it, 1);
                BankActivity.this.reload();
            }
        });

        new FetchData().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == 1 && resultCode == Activity.RESULT_OK) {
            Log.d("SESSION", "RESULT");
            this.reload();
        }
    }

    private class MyPagerAdapter extends FragmentStatePagerAdapter {

        private String[] titles = {"My account", "View transfers"};

        public MyPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int pos) {
            switch(pos) {
                case 0: return viewAccount;
                case 1: return viewTransfer;
                default: return viewAccount;
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return titles[position];
        }
    }

    private class FetchData extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try {
                SessionAPI session = new SessionAPI(AppData.authcode, AppData.sessionid, AppData.symkey);

                JSONObject jret = session.execute("username");
                username = jret.getString("message");
                username = Character.toUpperCase(username.charAt(0)) + username.substring(1);

            } catch(Exception e) {
                String err = e.getMessage();
                Log.d("SESSION", "error: " + err);
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void v) {
            setTitle("Welcome, " + username + "!");
        }
    }

}
