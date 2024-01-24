package com.example.finalproject.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.finalproject.Firebase.FirebaseManager;
import com.example.finalproject.Firebase.OnSignUpListener;
import com.example.finalproject.R;
import com.example.finalproject.Utils.CommonActivity;
import com.example.finalproject.Utils.DialogUtils;
import com.example.finalproject.Utils.ValidateUtils;
import com.google.firebase.auth.FirebaseUser;

public class SignUpFragment extends Fragment implements OnSignUpListener {
    private OnChangeTabListener listener;

    private Context mContext;
    private EditText edtEmail;
    private EditText edtPassword;
    private EditText edtPassword2;
    private Button btnRegister;

    public SignUpFragment(OnChangeTabListener listener) {
        this.listener = listener;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);
        initData(root);
        return root;
    }

    private void initData(View root) {
        FirebaseManager.init();

        mContext = getContext();

        edtEmail = (EditText) root.findViewById(R.id.edt_email);
        edtPassword = (EditText) root.findViewById(R.id.edt_password);
        edtPassword2 = (EditText) root.findViewById(R.id.edt_password2);
        btnRegister = (Button) root.findViewById(R.id.btn_register);

        btnRegister.setOnClickListener(v -> register());
    }

    private void register() {
        String email = edtEmail.getText().toString();
        String passwords = edtPassword.getText().toString();
        String passwords2 = edtPassword2.getText().toString();

        if (CommonActivity.isNullOrEmpty(email)) {
            DialogUtils.showDialog(mContext, "Please enter your email");
            return;
        }
        if (!ValidateUtils.isValidEmail(email)) {
            DialogUtils.showDialog(mContext,
                    "Please enter a valid email format.");
            return;
        }
        if (CommonActivity.isNullOrEmpty(passwords) || CommonActivity.isNullOrEmpty(passwords2)) {
            DialogUtils.showDialog(mContext, "Please enter your passwords");
            return;
        }
        if (ValidateUtils.isMinimumPasswords(passwords, 6)
                || ValidateUtils.isMinimumPasswords(passwords2, 6)) {
            DialogUtils.showDialog(mContext,
                    "Password too short, please enter minimum 6 characters!");
            return;
        }
        if (!ValidateUtils.validatePasswords(passwords, passwords2)) {
            DialogUtils.showDialog(mContext,
                    "Passwords do not match");
            return;
        }

        FirebaseManager.signUpUser(mContext, email, passwords, this);
    }

    private void reloadView() {
        edtEmail.setText("");
        edtPassword.setText("");
        edtPassword2.setText("");
    }

    @Override
    public void onSignUpSuccess(FirebaseUser user) {
        DialogUtils.showDialog(mContext, null, "Register successfully",
                (dialog, which) -> {
                    dialog.dismiss();
                    new Handler().postDelayed(() -> {
                        listener.onChangeTab(0);
                        reloadView();
                    }, 200);
                });
    }

    @Override
    public void onSignUpFailure(String errorMessage) {
        DialogUtils.showDialog(mContext, "Register fail. " + errorMessage);
    }
}