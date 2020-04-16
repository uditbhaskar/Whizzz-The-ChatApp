package com.example.whizzz.services.repository;


import androidx.lifecycle.MutableLiveData;



import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;


public class FirebaseSignUpInstance {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public MutableLiveData<Task> signInUser(String userNameSignIn, String emailSignIn, String passwordSignIn){
        final MutableLiveData<Task> taskSignIn = new MutableLiveData<>();

        mAuth.createUserWithEmailAndPassword(emailSignIn, passwordSignIn).addOnCompleteListener(taskSignIn::setValue);

        return taskSignIn;
    }
}
