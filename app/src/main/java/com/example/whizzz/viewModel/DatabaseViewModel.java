package com.example.whizzz.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.whizzz.services.repository.FirebaseInstanceDatabase;

public class DatabaseViewModel extends ViewModel {
    private FirebaseInstanceDatabase instance;
    public LiveData<Boolean> successAddUserDb;

    public DatabaseViewModel() {
        instance = new FirebaseInstanceDatabase();
    }

    public void addUserDatabase(String userId, String userName, String emailId, String timestamp, String imageUrl) {
        successAddUserDb = instance.addUserInDatabase(userId, userName, emailId, timestamp, imageUrl);
    }


}
