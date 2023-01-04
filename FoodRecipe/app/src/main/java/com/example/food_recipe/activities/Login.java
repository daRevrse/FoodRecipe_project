package com.example.pj_off.activities;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.example.pj_off.R;
import com.example.pj_off.apCompact.MyApp;
import com.example.pj_off.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;


public class Login extends MyApp {
    FirebaseAuth fAuth;
    ActivityLoginBinding loginBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login);

        fAuth = FirebaseAuth.getInstance();


        loginBinding.loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = loginBinding.email.getText().toString().trim();
                String password = loginBinding.password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    loginBinding.email.setError(getResources().getString(R.string.emial_required));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    loginBinding.password.setError(getResources().getString(R.string.password_required));
                    return;
                }

                if (password.length() < 6) {
                    loginBinding.password.setError(getResources().getString(R.string.password_length));
                    return;
                }

                loginBinding.idSpinKit.setVisibility(View.VISIBLE);

                // Authentification de l'ussr

                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(Login.this, "Logged in SuccessFully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ShowResultActivity.class));
                        } else {
                            Toast.makeText(Login.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            loginBinding.idSpinKit.setVisibility(View.GONE);

                        }


                    }
                });

            }
        });

        loginBinding.createAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Login.this, Register.class));
               // Animatoo.INSTANCE.animateSlideUp(Login.this);
            }
        });


    }
}