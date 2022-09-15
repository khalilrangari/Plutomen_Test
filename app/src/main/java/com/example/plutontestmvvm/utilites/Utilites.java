package com.example.plutontestmvvm.utilites;

import static com.example.plutontestmvvm.utilites.Constanst.DATA_BASE_NAME;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilites {
    public static boolean validateEmail(String email){
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        return email.matches(emailPattern) && email.length() > 0;
    }

    public static boolean validatePassword(String password){
        Pattern pattern;
        Matcher matcher;
        final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[A-Z])(?=.*[@#$%^&+=!])(?=\\S+$).{8,}$";
        pattern = Pattern.compile(PASSWORD_PATTERN);
        matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static DatabaseReference getFirebaseDatabaseReference(){
        return FirebaseDatabase.getInstance().getReference(DATA_BASE_NAME);
    }
}
