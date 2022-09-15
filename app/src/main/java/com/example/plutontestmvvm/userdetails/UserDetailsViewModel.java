package com.example.plutontestmvvm.userdetails;

import static com.example.plutontestmvvm.userdetails.UserDetailsUiActions.BACK_CLICK;
import static com.example.plutontestmvvm.userdetails.UserDetailsUiActions.ERROR;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plutontestmvvm.models.UserHelperClass;
import com.example.plutontestmvvm.utilites.Utilites;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.AbstractList;
import java.util.ArrayList;

public class UserDetailsViewModel extends ViewModel {
    private MutableLiveData<UserDetailsUiActions> mUserDetailsLiveData = new MutableLiveData<>();
    private MutableLiveData<ArrayList<UserHelperClass>> userData = new MutableLiveData<>();

    public void onBackClick() {
        mUserDetailsLiveData.postValue(BACK_CLICK);
    }

    public MutableLiveData<UserDetailsUiActions> getUserDetailsLiveData() {
        return mUserDetailsLiveData;
    }

    public void getAllUsersApi() {
        DatabaseReference databaseReference = Utilites.getFirebaseDatabaseReference();
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    if (snapshot.getChildrenCount() > 0) {
                        ArrayList<UserHelperClass> userDataList = new ArrayList<>();
                        for (DataSnapshot snapshot1 : snapshot.getChildren()) {
                            userDataList.add(new UserHelperClass(
                                    snapshot1.child("firstName").getValue().toString(),
                                    snapshot1.child("lastName").getValue().toString(),
                                    snapshot1.child("email").getValue().toString(),
                                    snapshot1.child("password").getValue().toString()
                            ));
                        }
                        userData.postValue(userDataList);
                    } else {
                        mUserDetailsLiveData.postValue(ERROR);
                    }
                } else {
                    mUserDetailsLiveData.postValue(ERROR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<ArrayList<UserHelperClass>> getUserData() {
        return userData;
    }
}
