package com.example.plutontestmvvm.authantication.signup;

import static com.example.plutontestmvvm.utilites.Constanst.IS_USER_LOGIN;
import static com.example.plutontestmvvm.utilites.Constanst.PROFILE_IMG;
import static com.example.plutontestmvvm.utilites.Constanst.USER_DATA;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
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
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;

import com.example.plutontestmvvm.R;
import com.example.plutontestmvvm.authantication.login.LoginViewModel;
import com.example.plutontestmvvm.databinding.ActivitySignUpBinding;
import com.example.plutontestmvvm.utilites.Utilites;
import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import pub.devrel.easypermissions.AppSettingsDialog;
import pub.devrel.easypermissions.EasyPermissions;

public class SignUpActivity extends AppCompatActivity implements EasyPermissions.PermissionCallbacks {

    private ActivitySignUpBinding mBinding;
    private SignUpViewModel mViewModel;
    private String[] permissions = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA};
    private final static int PERMISSION_REQUEST_CODE = 100;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(mBinding.getRoot());
        FirebaseApp.initializeApp(SignUpActivity.this);
        initViewModel();
        subScribeUiActions();
        getMaxIdFromDataBase();
    }

    private void getMaxIdFromDataBase() {
        Utilites.getFirebaseDatabaseReference().addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    mViewModel.maxId = snapshot.getChildrenCount();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void subScribeUiActions() {
        mViewModel.getUiActionLiveData().observe(this, new Observer<SignUpUiAction>() {
            @Override
            public void onChanged(SignUpUiAction signUpUiAction) {
                switch (signUpUiAction) {
                    case PROFILE_IMAGE:
                        if (hasPermission()) {
                            StartCameraGallery();
                        } else {
                            requestPermission();
                        }
                        break;

                    case SAVE_SUCCESS:
                        finish();
                        break;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.image_menu, menu);
        return true;
    }

    private void StartCameraGallery() {
        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(SignUpActivity.this);
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
        return EasyPermissions.hasPermissions(SignUpActivity.this, permissions);
    }

    private void requestPermission() {
        EasyPermissions.requestPermissions(SignUpActivity.this,
                "this application can not work without this permission.",
                PERMISSION_REQUEST_CODE,
                permissions);
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(SignUpViewModel.class);
        mBinding.setViewModel(mViewModel);
        preferences = getSharedPreferences("login", Context.MODE_PRIVATE);
        editor = preferences.edit();
    }

    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        if (EasyPermissions.somePermissionPermanentlyDenied(SignUpActivity.this,
                perms)) {
            new AppSettingsDialog.Builder(SignUpActivity.this).build().show();
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