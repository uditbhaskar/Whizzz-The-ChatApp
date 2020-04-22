package com.example.whizzz.services.repository;


import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;


public class FirebaseSignUpInstance {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    public MutableLiveData<String> currentUserId;


    public MutableLiveData<Task> signInUser(String userNameSignIn, String emailSignIn, String passwordSignIn) {
        final MutableLiveData<Task> taskSignIn = new MutableLiveData<>();
        currentUserId = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(emailSignIn, passwordSignIn).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> value) {
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String currentUser = Objects.requireNonNull(firebaseUser).getUid();
                currentUserId.setValue(currentUser);
                taskSignIn.setValue(value);

            }
        });

        return taskSignIn;
    }

}
