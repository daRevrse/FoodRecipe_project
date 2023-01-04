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
import com.example.pj_off.databinding.ActivityRegisterBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends MyApp {

    FirebaseAuth fAuth;

    ActivityRegisterBinding registerBinding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        registerBinding= DataBindingUtil.setContentView(this,R.layout.activity_register);

        fAuth = FirebaseAuth.getInstance();


        if (fAuth.getCurrentUser() !=null) {
            startActivity(new Intent(getApplicationContext(), ShowResultActivity.class));
            finish();

        }

        registerBinding.registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = registerBinding.email.getText().toString().trim();
                String password = registerBinding.password.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    registerBinding.email.setError(getResources().getString(R.string.emial_required));
                    return;
                }
                if (TextUtils.isEmpty(password)) {
                    registerBinding.password.setError(getResources().getString(R.string.password_required));
                    return;
                }

                if (password.length()<6) {
                    registerBinding.password.setError(getResources().getString(R.string.password_length));
                    return;
                }
                registerBinding.idSpinKit.setVisibility(View.VISIBLE);


                fAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(Register.this, "User Created", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), ShowResultActivity.class));
                        }
                        else {
                            Toast.makeText(Register.this, "Error " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            registerBinding.idSpinKit.setVisibility(View.GONE);

                        }

                    }
                });
            }
        });

        registerBinding.linkRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });

    }
}