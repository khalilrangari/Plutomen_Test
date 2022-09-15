package com.example.plutontestmvvm.home;

import static com.example.plutontestmvvm.utilites.Constanst.IS_USER_LOGIN;
import static com.example.plutontestmvvm.utilites.Constanst.PROFILE_IMG;
import static com.example.plutontestmvvm.utilites.Constanst.USER_DATA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.example.plutontestmvvm.R;
import com.example.plutontestmvvm.authantication.login.LoginActivity;
import com.example.plutontestmvvm.authantication.login.LoginViewModel;
import com.example.plutontestmvvm.authantication.signup.SignUpActivity;
import com.example.plutontestmvvm.databinding.ActivityHomeBinding;
import com.example.plutontestmvvm.models.UserHelperClass;
import com.example.plutontestmvvm.splash.MainActivity;
import com.example.plutontestmvvm.userdetails.UserDetailsActivity;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.zip.Inflater;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class HomeActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ActivityHomeBinding mBinding;
    private HomeViewModel mHomeViewModel;
    private UserHelperClass mUserHelperClass;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final static int PERMISSION_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();
        setUpViewModel();
        getIntentData(preferences.getString(USER_DATA, ""));
        subscribeUiAction();
        subscribeUpdatedUserData();
        subscribeProfileImage();
    }

    private void subscribeProfileImage() {
        mHomeViewModel.profilePic.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String previouslyEncodedImage) {
                if( !previouslyEncodedImage.equalsIgnoreCase("") ){
                    byte[] b = Base64.decode(previouslyEncodedImage, Base64.DEFAULT);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(b, 0, b.length);
                    mBinding.ivProfile.setImageBitmap(bitmap);
                }
            }
        });
    }

    private void subscribeUpdatedUserData() {
        mHomeViewModel.getUpdatedUserData().observe(this, new Observer<UserHelperClass>() {
            @Override
            public void onChanged(UserHelperClass userHelperClass) {
                SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = preferences.edit();
                editor.putBoolean(IS_USER_LOGIN, true);
                editor.putString(USER_DATA,new Gson().toJson(userHelperClass));
                editor.apply();
                enableDisableView(false);
            }
        });
    }

    private void subscribeUiAction() {
        mHomeViewModel.getHomeUiActionMediatorLiveData().observe(this, new Observer<HomeUiAction>() {
            @Override
            public void onChanged(HomeUiAction homeUiAction) {
                switch (homeUiAction) {
                    case EDIT_CLICK:
                        enableDisableView(true);
                        break;
                    case DELETE_CLICK:
                        showLogoutAlertDialog();
                        break;
                    case VIEW_ALL_CLICK:
                        startActivity(new Intent(HomeActivity.this, UserDetailsActivity.class));
                        break;
                    case LOGOUT_CLICK:
                        SharedPreferences preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = preferences.edit();
                        editor.remove(IS_USER_LOGIN);
                        editor.remove(USER_DATA);
                        editor.remove(PROFILE_IMG);
                        editor.apply();
                        finish();
                        Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                        startActivity(intent);
                        break;
                    case CANCEL:
                        enableDisableView(false);
                        break;
                    case ERROR:
                        Toast.makeText(HomeActivity.this, "some thing went wrong", Toast.LENGTH_SHORT).show();
                        break;
                    case SAVE:
                        mHomeViewModel.editUserDetails();
                        break;
                    case PROFILE_IMAGE:
                        if (hasPermission()) {
                            StartCameraGallery();
                        } else {
                            requestPermission();
                        }
                        break;
                }
            }
        });
    }

    private void requestPermission() {
        EasyPermissions.requestPermissions(HomeActivity.this,
                "this application can not work without this permission.",
                PERMISSION_REQUEST_CODE,
                permissions);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    private void StartCameraGallery() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(HomeActivity.this);
        builder.setTitle("Add Photo!");
        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (options[item].equals("Take Photo")) {
                    Intent camera_intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(camera_intent, 1);
                } else if (options[item].equals("Choose from Gallery")) {
                    Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 2);
                } else if (options[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private boolean hasPermission() {
        return EasyPermissions.hasPermissions(HomeActivity.this, permissions);
    }

    private void enableDisableView(boolean isEnable) {
        mBinding.tilFirstName.setEnabled(isEnable);
        mBinding.tilLastName.setEnabled(isEnable);
        mBinding.tilEmail.setEnabled(isEnable);
        mBinding.tilPassword.setEnabled(isEnable);
        mBinding.ivProfile.setEnabled(isEnable);
        mBinding.ivEdit.setEnabled(isEnable);

        mBinding.gpAllBtn.setVisibility(isEnable ? View.GONE : View.VISIBLE);
        mBinding.gpUpdateBtn.setVisibility(isEnable ? View.VISIBLE : View.GONE);
        mHomeViewModel.isEditEnable = isEnable;
        if (!isEnable){
            getIntentData(preferences.getString(USER_DATA, ""));
        }
    }

    private void showLogoutAlertDialog() {
        // setup the alert builder
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete Your Account");
        builder.setMessage("Are you sure you want to delete your account");

        // add a button
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                mHomeViewModel.deleteUser();
            }
        });

        builder.setNegativeButton(getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        // create and show the alert dialog
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void setUpViewModel() {
        mHomeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        mBinding.setViewModel(mHomeViewModel);
        enableDisableView(false);
        mHomeViewModel.profilePic.postValue(preferences.getString(PROFILE_IMG,""));
    }

    private void getIntentData(String userData) {
        if (userData != null && !userData.isEmpty()) {
            mUserHelperClass = new Gson().fromJson(userData, UserHelperClass.class);
            mHomeViewModel.mFirstName.set(mUserHelperClass.getFirstName());
            mHomeViewModel.mLastName.set(mUserHelperClass.getLastName());
            mHomeViewModel.mEmail.set(mUserHelperClass.getEmail());
            mHomeViewModel.mPassword.set(mUserHelperClass.getPassword());
        }
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(HomeActivity.this,
                perms)) {
            new AppSettingsDialog.Builder(HomeActivity.this).build().show();
        } else {
            requestPermission();
        }
    }

    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 1) {
                Bitmap photo = (Bitmap) data.getExtras().get("data");
                mBinding.ivProfile.setImageBitmap(photo);
                storeImageInSharedPrefrence(photo);
            } else if (requestCode == 2) {
                Uri selectedImage = data.getData();
                String[] filePath = {MediaStore.Images.Media.DATA};
                Cursor c = getContentResolver().query(selectedImage, filePath, null, null, null);
                c.moveToFirst();
                int columnIndex = c.getColumnIndex(filePath[0]);
                String picturePath = c.getString(columnIndex);
                c.close();
                Bitmap thumbnail = (BitmapFactory.decodeFile(picturePath));
                Log.w("path of image from gallery......******************.........", picturePath + "");
                mBinding.ivProfile.setImageBitmap(thumbnail);
                storeImageInSharedPrefrence(thumbnail);
            }
        }
    }

    private void storeImageInSharedPrefrence(Bitmap photo) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String encodedImage = Base64.encodeToString(b, Base64.DEFAULT);
        editor.putString(PROFILE_IMG,encodedImage);
        editor.apply();
    }
}