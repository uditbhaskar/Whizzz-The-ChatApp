package com.example.whizzz.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.whizzz.R;
import com.example.whizzz.services.repository.FirebaseSignUpInstance;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.example.whizzz.viewModel.SignInViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText et_usernameSignIn;
    EditText et_emailIdSignIn;
    EditText et_pwdSignIn;
    Button btn_signIn;
    TextView textToLogin;
    SignInViewModel signInViewModel;
    DatabaseViewModel databaseViewModel;
    String emailId;
    String pwd;
    String userName;
    Context context;
    String userId;
    String url;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        Objects.requireNonNull(getSupportActionBar()).hide();
        init();
        listeners();
    }

    private void listeners() {

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);

                emailId = et_emailIdSignIn.getText().toString();
                pwd = et_pwdSignIn.getText().toString();
                userName = et_usernameSignIn.getText().toString();
                if ((pwd.isEmpty() && emailId.isEmpty() && userName.isEmpty())) {
                    Toast.makeText(SignupActivity.this, "Fields are empty!", Toast.LENGTH_SHORT).show();
                    et_usernameSignIn.requestFocus();
                } else if (userName.isEmpty()) {
                    et_usernameSignIn.setError("Please enter a username.");
                    et_usernameSignIn.requestFocus();
                } else if (emailId.isEmpty()) {
                    et_emailIdSignIn.setError("Please enter your Email Id.");
                    et_emailIdSignIn.requestFocus();
                } else if (pwd.isEmpty()) {
                    et_pwdSignIn.setError("Please set your password.");
                    et_pwdSignIn.requestFocus();
                } else {
                    signInUsers();
                }
            }
        });

        textToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), LoginActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);

            }
        });
    }

    public void signInUsers() {
        signInViewModel.userSignIn(userName, emailId, pwd);
        signInViewModel.getCurrentUserId();
        signInViewModel.currentUserId.observe(this, new Observer<String>() {
            @Override
            public void onChanged(String s) {
                userId = s;
            }
        });

        signInViewModel.signInUser.observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if (!task.isSuccessful()) {
                    try {
                        throw Objects.requireNonNull(task.getException());
                    } catch (FirebaseAuthUserCollisionException existEmail) {
                        Toast.makeText(context, "Email Id already exists.", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthWeakPasswordException weakPassword) {
                        Toast.makeText(context, "Password length should be more then six characters.", Toast.LENGTH_SHORT).show();
                    } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                        Toast.makeText(context, "Invalid credentials, please try again.", Toast.LENGTH_SHORT).show();
                    } catch (Exception e) {
                        Toast.makeText(context, "SignUp unsuccessful. Try again.", Toast.LENGTH_SHORT).show();
                    }

                } else {
                    addUserInDatabase(userName,emailId,userId);
                    Toast.makeText(SignupActivity.this, "SignUp successful.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(SignupActivity.this, HomeActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                    finish();
                }
            }

        });
    }

    private void addUserInDatabase(String userName, String email, String userId ){
        long tsLong = System.currentTimeMillis();
        String timeStamp = Long.toString(tsLong);
        url="xyz";
        databaseViewModel.addUserDatabase(userId,userName,email,timeStamp,url);
        databaseViewModel.successAddUserDb.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                Toast.makeText(context, "DATA ADDED IN DATABASE", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        et_usernameSignIn = findViewById(R.id.et_signin_username);
        et_emailIdSignIn = findViewById(R.id.et_signin_email);
        et_pwdSignIn = findViewById(R.id.et_signin_password);
        btn_signIn = findViewById(R.id.btn_signin);
        textToLogin = findViewById(R.id.text_to_login);
        context = SignupActivity.this;
        signInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(SignInViewModel.class);
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(DatabaseViewModel.class);
    }
}
