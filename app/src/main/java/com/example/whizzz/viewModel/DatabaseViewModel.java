package com.example.whizzz.viewModel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.whizzz.services.repository.FirebaseInstanceDatabase;
import com.google.firebase.database.DataSnapshot;

public class DatabaseViewModel extends ViewModel {
    private FirebaseInstanceDatabase instance;
    public LiveData<Boolean> successAddUserDb;
    public  LiveData<DataSnapshot> fetchUserCurrentData;

    public DatabaseViewModel() {
        instance = new FirebaseInstanceDatabase();
    }

    public void addUserDatabase(String userId, String userName, String emailId, String timestamp, String imageUrl) {
        successAddUserDb = instance.addUserInDatabase(userId, userName, emailId, timestamp, imageUrl);
    }

    public void fetchingUserData(){
        fetchUserCurrentData= instance.fetchUserData();
    }

}
