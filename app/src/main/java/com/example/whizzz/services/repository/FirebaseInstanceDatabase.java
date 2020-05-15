package com.example.whizzz.services.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import java.util.HashMap;

public class FirebaseInstanceDatabase {
    private FirebaseDatabase instance = FirebaseDatabase.getInstance();
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


    public MutableLiveData<DataSnapshot> fetchAllUserNames(){
        final MutableLiveData<DataSnapshot> fetchAllUSerName = new MutableLiveData<>();

        instance.getReference("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchAllUSerName.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return fetchAllUSerName;
    }

    public MutableLiveData<DataSnapshot> fetchSelectedUserIdData(String userId){
        final MutableLiveData<DataSnapshot> fetchSelectedUserIDData =new MediatorLiveData<>();

        instance.getReference("Users").child(userId).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchSelectedUserIDData.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return fetchSelectedUserIDData;
    }




    public MutableLiveData<DataSnapshot> fetchUserDataCurrent(){
        final MutableLiveData<DataSnapshot> fetchCurrentUserData = new MutableLiveData<>();

        instance.getReference("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchCurrentUserData.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return fetchCurrentUserData;
    }



    public MutableLiveData<Boolean> addUserInDatabase(String userId, String userName, String emailId, String timestamp, String imageUrl) {
        final MutableLiveData<Boolean> successAddUserDb = new MutableLiveData<>();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        hashMap.put("username", userName);
        hashMap.put("emailId", emailId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("imageUrl", imageUrl);

        instance.getReference("Users").child(userId).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddUserDb.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddUserDb.setValue(false);
            }
        });

        return successAddUserDb;
    }
}
