package com.example.finalproject.Firebase;

import com.google.firebase.auth.FirebaseUser;

public interface OnSignUpListener {
    void onSignUpSuccess(FirebaseUser user);

    void onSignUpFailure(String errorMessage);
}
