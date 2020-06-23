package com.example.whizzz.view.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ScrollView;
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
    FrameLayout frameLayoutLogin;
    ScrollView scrollViewLogin;
    TextView tv_forgetPassword;

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
                currentUser = firebaseUser;
                if (currentUser != null) {
                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
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
                et_emailIdLogIn.clearFocus();
                et_pwdLogIn.clearFocus();
                v.startAnimation(buttonClick);
                dismissKeyboard();
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
                    scrollViewLogin.setClickable(false);
                    et_emailIdLogIn.setClickable(false);
                    et_pwdLogIn.setClickable(false);
                    et_emailIdLogIn.setClickable(false);
                    textToSignUp.setClickable(false);
                    frameLayoutLogin.setVisibility(View.VISIBLE);
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
                    frameLayoutLogin.setVisibility(View.GONE);
                    scrollViewLogin.setClickable(true);
                    et_emailIdLogIn.setClickable(true);
                    et_pwdLogIn.setClickable(true);
                    et_emailIdLogIn.setClickable(true);
                    textToSignUp.setClickable(true);

                    et_emailIdLogIn.setText("");
                    et_pwdLogIn.setText("");
                    et_emailIdLogIn.requestFocus();
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

                    Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }

    public void dismissKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        assert imm != null;
        imm.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }


    private void init() {
        et_emailIdLogIn = findViewById(R.id.et_login_email);
        et_pwdLogIn = findViewById(R.id.et_login_password);
        textToSignUp = findViewById(R.id.text_to_signUp);
        btn_logIn = findViewById(R.id.btn_login);
        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(LogInViewModel.class);
        frameLayoutLogin = findViewById(R.id.frame_layout_login);
        scrollViewLogin  = findViewById(R.id.scrollViewSignIn);
        tv_forgetPassword = findViewById(R.id.tv_forget_password);

        tv_forgetPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, ForgetPasswordActivity.class));
            }
        });

    }
}
