package com.example.finalproject.Firebase;

import com.google.firebase.auth.FirebaseUser;

public interface OnVerifyEmailListener {
    void onVerifySuccess(FirebaseUser user);

    void onVerifyFailure(String errorMessage);
}
