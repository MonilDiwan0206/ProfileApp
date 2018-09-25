package com.example.monil0206.profileapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ViewPager mainPager;
    private TextView profile;
    private TextView users;
    private TextView notif;
    private PagerViewAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if(!isConnected(MainActivity.this)){
            buildDialog(MainActivity.this).show();
            //finish();
        }
        mAuth = FirebaseAuth.getInstance();
        mainPager = (ViewPager) findViewById(R.id.mainPager);
        profile = (TextView) findViewById(R.id.profile);
        users = (TextView) findViewById(R.id.users);
        notif = (TextView) findViewById(R.id.notif);

        pagerAdapter = new PagerViewAdapter(getSupportFragmentManager());
        mainPager.setAdapter(pagerAdapter);
        mainPager.setOffscreenPageLimit(2);

        mainPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onPageSelected(int i) {
                changeTabs(i);

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });



    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void changeTabs(int i) {

        if(i == 0){
            profile.setTextColor(getColor(R.color.colorwhite));
            profile.setTextSize(22);

            users.setTextColor(getColor(R.color.textTabLight));
            users.setTextSize(16);

            notif.setTextColor(getColor(R.color.textTabLight));
            notif.setTextSize(16);

        } else if(i==1){
            profile.setTextColor(getColor(R.color.textTabLight));
            profile.setTextSize(16);

            users.setTextColor(getColor(R.color.colorwhite));
            users.setTextSize(22);

            notif.setTextColor(getColor(R.color.textTabLight));
            notif.setTextSize(16);

        } else if(i==2) {
            profile.setTextColor(getColor(R.color.textTabLight));
            profile.setTextSize(16);

            users.setTextColor(getColor(R.color.textTabLight));
            users.setTextSize(16);

            notif.setTextColor(getColor(R.color.colorwhite));
            notif.setTextSize(22);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null){
            Intent loginIntent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginIntent);
            finish();
        }
    }
    public boolean isConnected(Context context){
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netinfo = cm.getActiveNetworkInfo();

        if(netinfo != null && netinfo.isConnectedOrConnecting()) {
            android.net.NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            android.net.NetworkInfo mobile = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if ((mobile != null && mobile.isConnectedOrConnecting()) || (wifi != null && wifi.isConnectedOrConnecting()))
                return true;
            else return false;
        }
        return false;
    }
    public AlertDialog.Builder buildDialog(Context c){
        AlertDialog.Builder builder = new AlertDialog.Builder(c);
        builder.setTitle("No Internet Connection");
        builder.setMessage("Please make sure your mobile data or wifi is on to proceed....");
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Intent mainIntent = new Intent(MainActivity.this,MainActivity.class);
                startActivity(mainIntent);
                finish();
            }
        });
        return builder;
    }
}
