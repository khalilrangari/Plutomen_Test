package com.example.plutontestmvvm.home;

import static com.example.plutontestmvvm.home.HomeUiAction.CANCEL;
import static com.example.plutontestmvvm.home.HomeUiAction.DELETE_CLICK;
import static com.example.plutontestmvvm.home.HomeUiAction.EDIT_CLICK;
import static com.example.plutontestmvvm.home.HomeUiAction.ERROR;
import static com.example.plutontestmvvm.home.HomeUiAction.LOGOUT_CLICK;
import static com.example.plutontestmvvm.home.HomeUiAction.PROFILE_IMAGE;
import static com.example.plutontestmvvm.home.HomeUiAction.SAVE;
import static com.example.plutontestmvvm.home.HomeUiAction.VIEW_ALL_CLICK;
import static java.lang.Boolean.FALSE;

import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.databinding.ObservableField;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.plutontestmvvm.models.UserHelperClass;
import com.example.plutontestmvvm.utilites.Utilites;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class HomeViewModel extends ViewModel {

    public boolean isEditEnable = false;
    private MutableLiveData<HomeUiAction> homeUiActionMediatorLiveData = new MutableLiveData<>();
    public ObservableField<String> mFirstName = new ObservableField<>("");
    public ObservableField<String> mLastName = new ObservableField<>("");
    public ObservableField<String> mEmail = new ObservableField<>("");
    public ObservableField<String> mPassword = new ObservableField<>("");
    private ObservableField<Boolean> isFirstNameValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isLastNameValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isEmailValid = new ObservableField<>(FALSE);
    private ObservableField<Boolean> isPasswordValid = new ObservableField<>(FALSE);
    public ObservableField<Boolean> canActivateBtn = new ObservableField<>(FALSE);
    public ObservableField<Boolean> isProgressBarVisible = new ObservableField<>(FALSE);
    public MutableLiveData<UserHelperClass> updatedUserData = new MutableLiveData<>();
    public MutableLiveData<String> profilePic = new MutableLiveData<>();

    public void onTextChangeFirstName(CharSequence s, int start, int before, int count) {
        if (isEditEnable) {
            mFirstName.set(s.toString().trim());
            isFirstNameValid.set(!TextUtils.isEmpty(mFirstName.get()));
            validateRegisterPage();
        }
    }

    public void onTextChangeLastName(CharSequence s, int start, int before, int count) {
        if (isEditEnable) {
            mLastName.set(s.toString().trim());
            isLastNameValid.set(!TextUtils.isEmpty(mLastName.get()));
            validateRegisterPage();
        }
    }

    public void onTextChangeEmail(CharSequence s, int start, int before, int count) {
        if (isEditEnable) {
            mEmail.set(s.toString().trim());
            isEmailValid.set(Utilites.validateEmail(mEmail.get()));
            validateRegisterPage();
        }
    }

    public void onTextChangePassword(CharSequence s, int start, int before, int count) {
        if (isEditEnable) {
            mPassword.set(s.toString().trim());
            isPasswordValid.set(Utilites.validatePassword(mPassword.get()));
            validateRegisterPage();
        }
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

    public void onEditClick() {
        homeUiActionMediatorLiveData.postValue(EDIT_CLICK);
    }

    public void onDeleteClick() {
        homeUiActionMediatorLiveData.postValue(DELETE_CLICK);

    }

    public void onViewAllClick() {
        homeUiActionMediatorLiveData.postValue(VIEW_ALL_CLICK);
    }

    public void onLogoutClick() {
        homeUiActionMediatorLiveData.postValue(LOGOUT_CLICK);
    }

    public MutableLiveData<HomeUiAction> getHomeUiActionMediatorLiveData() {
        return homeUiActionMediatorLiveData;
    }

    public void deleteUser() {
        isProgressBarVisible.set(true);
        Query applesQuery = Utilites.getFirebaseDatabaseReference().orderByChild("email").equalTo(mEmail.get());
        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        appleSnapshot.getRef().removeValue();
                        homeUiActionMediatorLiveData.postValue(LOGOUT_CLICK);
                    }
                } else {
                    homeUiActionMediatorLiveData.postValue(ERROR);
                }
                isProgressBarVisible.set(false);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                homeUiActionMediatorLiveData.postValue(ERROR);
                isProgressBarVisible.set(false);
            }
        });
    }

    public void onSaveClick() {
        homeUiActionMediatorLiveData.postValue(SAVE);
    }

    public void onCancelClick() {
        homeUiActionMediatorLiveData.postValue(CANCEL);
    }

    public void editUserDetails() {
        isProgressBarVisible.set(true);
        Query query = Utilites.getFirebaseDatabaseReference().orderByChild("email").equalTo(mEmail.get());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot appleSnapshot : dataSnapshot.getChildren()) {
                        updatedUserData.postValue(new UserHelperClass(mFirstName.get(),mLastName.get(),mEmail.get(),mPassword.get()));
                        Utilites.getFirebaseDatabaseReference().child(appleSnapshot.getKey())
                                .getRef().setValue(updatedUserData.getValue());
                    }
                } else {
                    homeUiActionMediatorLiveData.postValue(ERROR);
                }
                isProgressBarVisible.set(false);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    public MutableLiveData<UserHelperClass> getUpdatedUserData() {
        return updatedUserData;
    }

    public void onProfileImgClick() {
        homeUiActionMediatorLiveData.postValue(PROFILE_IMAGE);
    }
}
