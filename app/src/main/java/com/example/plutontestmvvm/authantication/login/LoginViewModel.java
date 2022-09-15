package com.example.plutontestmvvm.authantication.login;

import static com.example.plutontestmvvm.authantication.login.LoginUiAction.BTN_CREATE_ACCOUNT;
import static com.example.plutontestmvvm.authantication.login.LoginUiAction.BTN_LOGIN;
import static com.example.plutontestmvvm.authantication.login.LoginUiAction.ERROR;

import static java.lang.Boolean.FALSE;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.example.plutontestmvvm.models.UserHelperClass;
import com.example.plutontestmvvm.utilites.Utilites;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class LoginViewModel extends ViewModel {

    private MediatorLiveData<LoginUiAction> uiActionLiveData = new MediatorLiveData<>();
    private String mEmail, mPassword;
    private ObservableField<Boolean> isEmailValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isPasswordValid = new ObservableField<>(FALSE);
    public ObservableField<Boolean> canActivateBtn = new ObservableField<>(FALSE);
    private MediatorLiveData<UserHelperClass> userData = new MediatorLiveData<>();

    public void onLoginClick(){
        Query checkUser = Utilites.getFirebaseDatabaseReference().orderByChild("email").equalTo(mEmail);
        checkUser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    boolean isDataFound = false;
                    for (DataSnapshot snapshot1: snapshot.getChildren()) {
                        if (snapshot1.child("password").getValue().equals(mPassword)){
                            isDataFound = true;
                            userData.postValue(new UserHelperClass(
                                    snapshot1.child("firstName").getValue().toString(),
                                    snapshot1.child("lastName").getValue().toString(),
                                    snapshot1.child("email").getValue().toString(),
                                    snapshot1.child("password").getValue().toString()
                            ));
                            break;
                        }else {
                            isDataFound = false;
                        }
                    }
                    if (!isDataFound){
                        uiActionLiveData.postValue(ERROR);
                    }
                } else {
                    uiActionLiveData.postValue(ERROR);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public void onSignUpClick(){
        uiActionLiveData.postValue(BTN_CREATE_ACCOUNT);
    }

    public MediatorLiveData<LoginUiAction> getUiActionLiveData() {
        return uiActionLiveData;
    }

    public void onTextChangeEmail(CharSequence s, int start, int before, int count) {
        mEmail = s.toString().trim();
        isEmailValid.set(Utilites.validateEmail(mEmail));
        validateRegisterPage();
    }

    public void onTextChangePassword(CharSequence s, int start, int before, int count) {
        mPassword = s.toString().trim();
        isPasswordValid.set(Utilites.validatePassword(mPassword));
        validateRegisterPage();
    }

    private void validateRegisterPage() {
        if (Boolean.TRUE.equals(isEmailValid.get()) &&
                Boolean.TRUE.equals(isPasswordValid.get())) {
            canActivateBtn.set(true);
            return;
        }
        canActivateBtn.set(false);
    }

    public MediatorLiveData<UserHelperClass> getUserData() {
        return userData;
    }
}
