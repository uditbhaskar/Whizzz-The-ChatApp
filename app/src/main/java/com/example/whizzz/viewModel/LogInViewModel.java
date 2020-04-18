package com.example.whizzz.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.whizzz.services.repository.FirebaseLoginInstance;
import com.google.android.gms.tasks.Task;

public class LogInViewModel extends ViewModel {
    private FirebaseLoginInstance loginInstance;
    public LiveData<Task> logInUser;

    public LogInViewModel() {
        loginInstance = new FirebaseLoginInstance();
    }

    public void userLogIn(String emailLogIn,String pwdLogIn){
        logInUser=loginInstance.loginUser(emailLogIn, pwdLogIn);
    }

}
