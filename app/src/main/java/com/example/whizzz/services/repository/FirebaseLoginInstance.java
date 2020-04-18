package com.example.whizzz.services.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class FirebaseLoginInstance {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public MutableLiveData<Task> loginUser(String emailLogin,String pwdLogin){
        final MutableLiveData<Task> taskLogin = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(emailLogin, pwdLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                taskLogin.setValue(task);
            }
        });

        return taskLogin;
    }
}
