package com.example.finalproject.Firebase;

import com.google.firebase.auth.FirebaseUser;

public interface OnSignInListener {
    void onSignInSuccess(FirebaseUser user);

    void onSignInFailure(String errorMessage);
}
