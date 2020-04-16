package com.example.whizzz.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whizzz.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText et_emailIdLogIn;
    EditText et_pwdLogIn;
    Button btn_logIn;
    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();

        listener();
    }

    private void listener() {
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);

                String emailLog = et_emailIdLogIn.getText().toString();
                String pwdLog = et_pwdLogIn.getText().toString();

                if ((pwdLog.isEmpty() && emailLog.isEmpty())) {
                    Toast.makeText(LoginActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                    et_emailIdLogIn.requestFocus();
                } else if (emailLog.isEmpty()) {
                    et_emailIdLogIn.setError("Please enter your Email Id.");
                    et_emailIdLogIn.requestFocus();
                } else if (pwdLog.isEmpty()) {
                    et_pwdLogIn.setError("Please enter your password.");
                    et_pwdLogIn.requestFocus();
                } else {
                    mAuth.signInWithEmailAndPassword(emailLog, pwdLog).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {

                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthInvalidUserException invalidEmail) {
                                    Toast.makeText(LoginActivity.this, "Invalid credentials, please try again.", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException wrongPassword) {
                                    Toast.makeText(LoginActivity.this, "Wrong password or username , please try again.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(LoginActivity.this, "Error occurred!", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Welcome back!", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                                finish();
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                            }
                        }
                    });
                }

            }
        });


    }


    private void init() {
        et_emailIdLogIn = findViewById(R.id.et_login_email);
        et_pwdLogIn = findViewById(R.id.et_login_password);
        btn_logIn = findViewById(R.id.btn_login);
        mAuth = FirebaseAuth.getInstance();
    }
}
