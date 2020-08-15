package com.example.whizzz.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.whizzz.services.repository.FirebaseLoginInstance;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LogInViewModel extends ViewModel {

    private FirebaseLoginInstance loginInstance;
    public LiveData<Task> logInUser;
    public LiveData<FirebaseUser> firebaseUserLoginStatus;
    public LiveData<FirebaseAuth> firebaseAuthLiveData;
    public LiveData<Task> successPasswordReset;
    public LiveData<Boolean> successUpdateToken;


    public LogInViewModel() {
        loginInstance = new FirebaseLoginInstance();
    }

    public void userLogIn(String emailLogIn, String pwdLogIn) {
        logInUser = loginInstance.loginUser(emailLogIn, pwdLogIn);
    }

    public void getFirebaseUserLogInStatus() {
        firebaseUserLoginStatus = loginInstance.getFirebaseUserLoginStatus();
    }

    public void getFirebaseAuth() {
        firebaseAuthLiveData = loginInstance.getFirebaseAuth();
    }

    public void addPasswordResetEmail(String email) {
        successPasswordReset = loginInstance.resetPassword(email);
    }

    public void updateToken(String newToken) {
        successUpdateToken = loginInstance.successUpdateToken(newToken);
    }

}
