package com.example.plutontestmvvm.splash;

import static com.example.plutontestmvvm.utilites.Constanst.IS_USER_LOGIN;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.plutontestmvvm.authantication.login.LoginActivity;
import com.example.plutontestmvvm.R;
import com.example.plutontestmvvm.home.HomeActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                intent = new Intent(MainActivity.this, preferences.contains(IS_USER_LOGIN) ? HomeActivity.class : LoginActivity.class);
                startActivity(intent);
                finish();
            }
        },3000);
    }
}