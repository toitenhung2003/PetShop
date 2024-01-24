package com.example.finalproject.Utils;

import android.text.TextUtils;
import android.util.Patterns;

public class ValidateUtils {
    public static boolean isValidEmail(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static boolean isMinimumPasswords(CharSequence passwords, int minimumLength) {
        return passwords.length() < minimumLength;
    }

    public static boolean validatePasswords(String newPass, String rePass) {
        return rePass.equals(newPass);
    }

    public static boolean validatePhoneNumber(String phoneNumber) {
        if (!phoneNumber.trim().startsWith("0") || phoneNumber.length() != 10) {
            return false;
        }
        return true;
    }
}
