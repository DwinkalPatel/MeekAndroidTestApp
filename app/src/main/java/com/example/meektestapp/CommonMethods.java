package com.example.meektestapp;

import android.text.TextUtils;
import android.util.Patterns;

public class CommonMethods {

    public static boolean  isValidString(String string) {
        return string != null && !string.isEmpty() && !string.equals("null") && !string.equals("--");
    }

    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }


}
