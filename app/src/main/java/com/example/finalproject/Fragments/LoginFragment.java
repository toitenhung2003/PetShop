package com.example.finalproject.Fragments;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalproject.Activities.ManageProductsActivity;
import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Firebase.OnSignInListener;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.DialogUtils;
import com.example.finalproject.Utils.ValidateUtils;
import com.google.firebase.auth.FirebaseUser;

public class LoginFragment extends Fragment implements OnSignInListener {
    private Context mContext;
    private EditText edtEmail;
    private EditText edtPassword;
    private Button btnLogin;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_login, container, false);
        initData(root);
        return root;
    }

    private void initData(View root) {
        FirebaseManager.init();

        mContext = getContext();

        edtEmail = (EditText) root.findViewById(R.id.edt_email);
        edtPassword = (EditText) root.findViewById(R.id.edt_password);
        btnLogin = (Button) root.findViewById(R.id.btn_login);

        btnLogin.setOnClickListener(v -> login());
    }

    private void login() {
        String email = edtEmail.getText().toString();
        String passwords = edtPassword.getText().toString();

        if (CommonActivity.isNullOrEmpty(email)) {
            DialogUtils.showDialog(mContext, "Please enter your email");
            return;
        }
        if (!ValidateUtils.isValidEmail(email)) {
            DialogUtils.showDialog(mContext,
                    "Please enter a valid email format.");
            return;
        }
        if (CommonActivity.isNullOrEmpty(passwords)) {
            DialogUtils.showDialog(mContext, "Please enter your passwords");
            return;
        }
        if (ValidateUtils.isMinimumPasswords(passwords, 6)) {
            DialogUtils.showDialog(mContext,
                    "Password too short, please enter minimum 6 characters!");
            return;
        }

        if (email.equals("admin@gmail.com") && passwords.equals("123456")) {
            Intent intent = new Intent(mContext, ManageProductsActivity.class);
            intent.putExtra("ADMIN", true);
            startActivity(intent);
            reloadView();
        } else {
            FirebaseManager.signInUser(mContext, email, passwords, this);
        }
    }

    private void reloadView() {
        edtEmail.setText("");
        edtPassword.setText("");
    }

    @Override
    public void onSignInSuccess(FirebaseUser user) {
        Intent intent = new Intent(mContext, ManageProductsActivity.class);
        intent.putExtra("ID_USER", user.getUid());
        Log.d("zzzz", "onSignInSuccess: " + user.getUid());
        startActivity(intent);
        reloadView();
    }

    @Override
    public void onSignInFailure(String errorMessage) {
        DialogUtils.showDialog(mContext, "Login fail. " + errorMessage);
    }
}