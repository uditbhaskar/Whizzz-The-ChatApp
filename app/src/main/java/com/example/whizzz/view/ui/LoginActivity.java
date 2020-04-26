package com.example.whizzz.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.whizzz.R;
import com.example.whizzz.viewModel.LogInViewModel;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {

    EditText et_emailIdLogIn;
    EditText et_pwdLogIn;
    Button btn_logIn;
    LogInViewModel logInViewModel;
    String emailLog;
    String pwdLog;
    TextView textToSignUp;
    FirebaseUser currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        init();
        getUserSession();


        listener();
    }

    private void getUserSession() {
        logInViewModel.getFirebaseUserLogInStatus();
        logInViewModel.firebaseUserLoginStatus.observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                currentUser= firebaseUser;
                if(currentUser!=null){

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            }
        });


    }

    private void listener() {
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        btn_logIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);

                emailLog = et_emailIdLogIn.getText().toString();
                pwdLog = et_pwdLogIn.getText().toString();

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
                    logInUser();
                }

            }
        });

        textToSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), SignupActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                finish();
            }
        });


    }

    public void logInUser() {
        logInViewModel.userLogIn(emailLog, pwdLog);
        logInViewModel.logInUser.observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
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
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }


    private void init() {
        et_emailIdLogIn = findViewById(R.id.et_login_email);
        et_pwdLogIn = findViewById(R.id.et_login_password);
        textToSignUp = findViewById(R.id.text_to_signUp);
        btn_logIn = findViewById(R.id.btn_login);
        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(LogInViewModel.class);

    }
}
