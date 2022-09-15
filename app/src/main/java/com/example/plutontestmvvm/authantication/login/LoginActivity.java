package com.example.plutontestmvvm.authantication.login;

import static com.example.plutontestmvvm.utilites.Constanst.IS_USER_LOGIN;
import static com.example.plutontestmvvm.utilites.Constanst.USER_DATA;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.plutontestmvvm.R;
import com.example.plutontestmvvm.authantication.signup.SignUpActivity;
import com.example.plutontestmvvm.databinding.ActivityLoginBinding;
import com.example.plutontestmvvm.home.HomeActivity;
import com.example.plutontestmvvm.models.UserHelperClass;
import com.google.firebase.FirebaseApp;
import com.google.gson.Gson;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding mBinding;
    private LoginViewModel mViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        FirebaseApp.initializeApp(LoginActivity.this);
        initViewModel();
        subscribeUiAction();
        subscribeUserData();
    }

    private void subscribeUserData() {
        mViewModel.getUserData().observe(this, new Observer<UserHelperClass>() {
            @Override
            public void onChanged(UserHelperClass userHelperClass) {
                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(IS_USER_LOGIN, true);
                editor.putString(USER_DATA,new Gson().toJson(userHelperClass));
                editor.apply();
                Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(LoginViewModel.class);
        mBinding.setViewModel(mViewModel);
    }

    private void subscribeUiAction() {
        mViewModel.getUiActionLiveData().observe(this, new Observer<LoginUiAction>() {
            @Override
            public void onChanged(LoginUiAction loginUiAction) {
                switch (loginUiAction){
                    case BTN_LOGIN:
                        break;
                    case BTN_CREATE_ACCOUNT:
                        startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
                        break;
                    case ERROR:
                        Toast.makeText(LoginActivity.this, "Email and Password incorrect please try again", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }
}