package com.example.plutontestmvvm.userdetails;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Bundle;
import android.widget.Toast;

import com.example.plutontestmvvm.R;
import com.example.plutontestmvvm.databinding.ActivityUserDetailsBinding;
import com.example.plutontestmvvm.home.HomeViewModel;
import com.example.plutontestmvvm.models.UserHelperClass;
import com.example.plutontestmvvm.userdetails.adapter.UserListAdapter;

import java.util.AbstractList;
import java.util.ArrayList;

public class UserDetailsActivity extends AppCompatActivity {

    private ActivityUserDetailsBinding mBinding;
    private UserDetailsViewModel mViewModel;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityUserDetailsBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        setUpViewModel();
        subscribeUserData();
        subscribeUiActions();
    }

    private void subscribeUiActions() {
        mViewModel.getUserDetailsLiveData().observe(this, new Observer<UserDetailsUiActions>() {
            @Override
            public void onChanged(UserDetailsUiActions userDetailsUiActions) {
                switch (userDetailsUiActions) {
                    case BACK_CLICK:
                        onBackPressed();
                        break;
                    case ERROR:
                        Toast.makeText(UserDetailsActivity.this, "Something went wrong", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void subscribeUserData() {
        mViewModel.getUserData().observe(this, new Observer<ArrayList<UserHelperClass>>() {
            @Override
            public void onChanged(ArrayList<UserHelperClass> userHelperClasses) {
                adapter = new UserListAdapter(userHelperClasses);
                mBinding.rvUserList.setAdapter(adapter);
            }
        });
    }

    private void setUpViewModel() {
        mViewModel = new ViewModelProvider(this).get(UserDetailsViewModel.class);
        mBinding.setViewModel(mViewModel);
        mViewModel.getAllUsersApi();
    }
}