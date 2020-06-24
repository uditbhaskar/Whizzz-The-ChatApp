package com.example.whizzz.view.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.Intent;
import android.net.sip.SipSession;
import android.os.Bundle;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.whizzz.R;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.example.whizzz.viewModel.LogInViewModel;
import com.google.android.gms.tasks.Task;

import java.util.Objects;

public class ForgetPasswordActivity extends AppCompatActivity {

    ImageView iv_back_button;
    EditText et_email_to_reset;
    Button btn_reset;
    LogInViewModel logInViewModel;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        init();
        listeners();

    }

    private void listeners() {
        final AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.8F);

        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = et_email_to_reset.getText().toString().trim();
                et_email_to_reset.clearFocus();
                v.startAnimation(buttonClick);
                dismissKeyboard();

                if(email.isEmpty()){
                    et_email_to_reset.setError("Please enter your authorised Email Id.");
                    Toast.makeText(ForgetPasswordActivity.this, "Field is empty", Toast.LENGTH_SHORT).show();
                    et_email_to_reset.requestFocus();
                }else{
                    et_email_to_reset.setClickable(false);
                    resetPassword(email);
                }

            }
        });
    }

    private void resetPassword(String email) {
        logInViewModel.addPasswordResetEmail(email);
        logInViewModel.successPasswordReset.observe(this, new Observer<Task>() {
            @Override
            public void onChanged(Task task) {
                if(!task.isSuccessful()){
                    et_email_to_reset.setClickable(true);
                    et_email_to_reset.setText("");
                    String error= Objects.requireNonNull(task.getException()).getMessage();
                    et_email_to_reset.requestFocus();
                    Toast.makeText(ForgetPasswordActivity.this, error, Toast.LENGTH_SHORT).show();

                }else{
                    Toast.makeText(ForgetPasswordActivity.this, "Please check your Email.", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(ForgetPasswordActivity.this, LoginActivity.class);
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
        et_email_to_reset = findViewById(R.id.et_email_to_reset);
        iv_back_button = findViewById(R.id.iv_back_button_forget_pwd_layout);
        btn_reset = findViewById(R.id.btn_reset);

        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(LogInViewModel.class);

    }


}