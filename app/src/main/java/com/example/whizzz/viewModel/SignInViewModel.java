package com.example.whizzz.viewModel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.whizzz.services.repository.FirebaseSignUpInstance;
import com.google.android.gms.tasks.Task;

public class SignInViewModel extends ViewModel {

    private FirebaseSignUpInstance signUpInstance;
    public  LiveData<Task> signInUser;

    public SignInViewModel(){
        signUpInstance = new FirebaseSignUpInstance();
    }

    public void userSignIn(String userNameSignIn, String emailSignIn, String passwordSignIn){
        signInUser = signUpInstance.signInUser(userNameSignIn,emailSignIn,passwordSignIn);
    }

}
