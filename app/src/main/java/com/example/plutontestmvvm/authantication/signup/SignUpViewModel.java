package com.example.plutontestmvvm.authantication.signup;

import static com.example.plutontestmvvm.authantication.signup.SignUpUiAction.PROFILE_IMAGE;
import static com.example.plutontestmvvm.authantication.signup.SignUpUiAction.SAVE_SUCCESS;
import static com.example.plutontestmvvm.utilites.Constanst.DATA_BASE_NAME;

import static java.lang.Boolean.FALSE;

import android.text.TextUtils;

import androidx.databinding.ObservableField;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plutontestmvvm.models.UserHelperClass;
import com.example.plutontestmvvm.utilites.Utilites;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class SignUpViewModel extends ViewModel {

    private MediatorLiveData<SignUpUiAction> uiActionLiveData = new MediatorLiveData<>();
    private String mFirstName, mLastName, mEmail, mPassword;
    private ObservableField<Boolean> isFirstNameValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isLastNameValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isEmailValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isPasswordValid = new ObservableField<>(FALSE);
    public ObservableField<Boolean> canActivateBtn = new ObservableField<>(FALSE);
    public long maxId = 0;

    public void onProfileImgClick() {
        uiActionLiveData.postValue(PROFILE_IMAGE);
    }

    public MediatorLiveData<SignUpUiAction> getUiActionLiveData() {
        return uiActionLiveData;
    }

    public void onTextChangeFirstName(CharSequence s, int start, int before, int count) {
        mFirstName = s.toString().trim();
        isFirstNameValid.set(!TextUtils.isEmpty(mFirstName));
        validateRegisterPage();
    }

    public void onTextChangeLastName(CharSequence s, int start, int before, int count) {
        mLastName = s.toString().trim();
        isLastNameValid.set(!TextUtils.isEmpty(mLastName));
        validateRegisterPage();
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
        if (Boolean.TRUE.equals(isFirstNameValid.get()) &&
                Boolean.TRUE.equals(isLastNameValid.get()) &&
                Boolean.TRUE.equals(isEmailValid.get()) &&
                Boolean.TRUE.equals(isPasswordValid.get())) {
            canActivateBtn.set(true);
            return;
        }
        canActivateBtn.set(false);
    }

    public void onCreateAccountClick(){
        Utilites.getFirebaseDatabaseReference().child(String.valueOf(maxId+1)).setValue(new UserHelperClass(mFirstName,mLastName,mEmail,mPassword));
        uiActionLiveData.postValue(SAVE_SUCCESS);
    }
}
