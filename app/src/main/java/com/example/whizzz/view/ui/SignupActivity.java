package com.example.whizzz.view.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

import java.util.Objects;

public class SignupActivity extends AppCompatActivity {

    EditText et_usernameSignIn;
    EditText et_emailIdSignIn;
    EditText et_pwdSignIn;
    Button btn_signIn;
    TextView textToLogin;
    FirebaseAuth mAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        init();
        listeners();
    }

    private void listeners() {

        btn_signIn.setOnClickListener(new View.OnClickListener() {
            final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

            @Override
            public void onClick(View v) {
                v.startAnimation(buttonClick);

                String emailId = et_emailIdSignIn.getText().toString();
                String pwd = et_pwdSignIn.getText().toString();
                String userName = et_usernameSignIn.getText().toString();
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
                    mAuth.createUserWithEmailAndPassword(emailId, pwd).addOnCompleteListener(SignupActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                try {
                                    throw Objects.requireNonNull(task.getException());
                                } catch (FirebaseAuthUserCollisionException existEmail) {
                                    Toast.makeText(SignupActivity.this, "Email Id already exists.", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthWeakPasswordException weakPassword) {
                                    Toast.makeText(SignupActivity.this, "Password length should be more then six characters.", Toast.LENGTH_SHORT).show();
                                } catch (FirebaseAuthInvalidCredentialsException malformedEmail) {
                                    Toast.makeText(SignupActivity.this, "Invalid credentials, please try again.", Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    Toast.makeText(SignupActivity.this, "SignUp unsuccessful. Try again.", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(SignupActivity.this, "SignUp successful.", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(SignupActivity.this, HomeActivity.class));
                                overridePendingTransition(R.anim.slide_in, R.anim.slide_out);
                                finish();
                            }
                        }
                    });
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

    private void init() {
        et_usernameSignIn = findViewById(R.id.et_signin_username);
        et_emailIdSignIn = findViewById(R.id.et_signin_email);
        et_pwdSignIn = findViewById(R.id.et_signin_password);
        btn_signIn = findViewById(R.id.btn_signin);
        textToLogin = findViewById(R.id.text_to_login);
        mAuth = FirebaseAuth.getInstance();
    }
}
