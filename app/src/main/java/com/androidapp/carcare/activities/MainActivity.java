package com.androidapp.carcare.activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.androidapp.carcare.R;

import static com.androidapp.carcare.utils.Constants.IsRegisteredUser;
import static com.androidapp.carcare.utils.Constants.PREFS_NAME;
import static com.androidapp.carcare.utils.Constants.isNetworkConnected;

public class MainActivity extends AppCompatActivity {

    Animation animShake;

    RelativeLayout connectionLayout;
    Button retry_butn;
    TextView no_net_text;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        animShake = AnimationUtils.loadAnimation(this, R.anim.shake);


        connectionLayout = findViewById(R.id.connection_layout);
        retry_butn = findViewById(R.id.retry_butn);
        no_net_text = findViewById(R.id.nonet_text);

        if(isNetworkConnected(MainActivity.this)){
            connectionLayout.setVisibility(View.GONE);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if(!autoLogin()){
                        Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }

                }
            },1000);

        }else {
            connectionLayout.setVisibility(View.VISIBLE);
        }

        retry_butn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isNetworkConnected(MainActivity.this)){
                    Intent intent = new Intent(MainActivity.this,LoginActivity.class);
                    startActivity(intent);
                    finish();
                }else no_net_text.startAnimation(animShake);
            }
        });


    }


    SharedPreferences preferences;

    private boolean autoLogin(){
        preferences = getSharedPreferences(PREFS_NAME,MODE_PRIVATE);
        boolean isRegistered = preferences.getBoolean(IsRegisteredUser,false);


        if(isRegistered){

            Intent intent = new Intent(MainActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();

            return  true;
        }


        return false;
    }
}
