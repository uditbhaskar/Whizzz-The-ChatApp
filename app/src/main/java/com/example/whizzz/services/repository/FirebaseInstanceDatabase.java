package com.example.whizzz.services.repository;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.webkit.MimeTypeMap;

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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;
import java.util.Objects;

public class FirebaseInstanceDatabase {
    private FirebaseDatabase instance = FirebaseDatabase.getInstance();
    private FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
    private StorageReference storageReference = FirebaseStorage.getInstance().getReference("uploads");


    public MutableLiveData<DataSnapshot> fetchAllUserNames() {
        final MutableLiveData<DataSnapshot> fetchAllUSerName = new MutableLiveData<>();

        instance.getReference("Users").orderByChild("username").addValueEventListener(new ValueEventListener() {
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


    private String getFileExtension(Uri uri, Context context) {
        ContentResolver contentResolver = Objects.requireNonNull(context).getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getMimeTypeFromExtension(contentResolver.getType(uri));
    }


    public MutableLiveData<StorageReference> fetchFileReference(String timeStamp, Uri imageUri, Context context) {
        final MutableLiveData<StorageReference> fetchFileReferenceImage = new MutableLiveData<>();
        final StorageReference fileReference = storageReference.child(timeStamp + "." + getFileExtension(imageUri, context));
        fetchFileReferenceImage.setValue(fileReference);
        return fetchFileReferenceImage;
    }

    public MutableLiveData<DataSnapshot> fetchSelectedUserIdData(String userId) {
        final MutableLiveData<DataSnapshot> fetchSelectedUserIDData = new MediatorLiveData<>();

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


    public MutableLiveData<DataSnapshot> fetchUserDataCurrent() {
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

    public MutableLiveData<DataSnapshot> fetchChatUser() {
        final MutableLiveData<DataSnapshot> fetchUserChat = new MutableLiveData<>();
        instance.getReference("Chats").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fetchUserChat.setValue(dataSnapshot);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return fetchUserChat;
    }

    public MutableLiveData<Boolean> addChatsInDatabase(String senderId, String receiverId, String message, String timestamp) {
        final MutableLiveData<Boolean> successAddChatsDb = new MutableLiveData<>();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("senderId", senderId);
        hashMap.put("receiverId", receiverId);
        hashMap.put("message", message);
        hashMap.put("timestamp", timestamp);

        instance.getReference("Chats").push().setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddChatsDb.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddChatsDb.setValue(false);
            }
        });

        return successAddChatsDb;
    }


    public MutableLiveData<Boolean> addImageUrlInDatabase(String imageUrl, Object mUri) {
        final MutableLiveData<Boolean> successAddUriImage = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(imageUrl, mUri);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddUriImage.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddUriImage.setValue(false);
            }
        });


        return successAddUriImage;
    }

    public MutableLiveData<Boolean> addUsernameInDatabase(String usernameUpdated, Object username) {
        final MutableLiveData<Boolean> successAddUserName = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(usernameUpdated, username);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddUserName.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddUserName.setValue(false);
            }
        });

        return successAddUserName;
    }

    public MutableLiveData<Boolean> addBioInDatabase(String bioUpdated, Object bio) {
        final MutableLiveData<Boolean> successAddBio = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(bioUpdated, bio);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddBio.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddBio.setValue(false);
            }
        });

        return successAddBio;
    }

    public MutableLiveData<Boolean> addStatusInDatabase(String statusUpdated, Object status){
        final MutableLiveData<Boolean> successAddStatus = new MutableLiveData<>();
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        HashMap<String, Object> map = new HashMap<>();
        map.put(statusUpdated, status);
        reference.updateChildren(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successAddStatus.setValue(true);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                successAddStatus.setValue(false);
            }
        });
        return successAddStatus;
    }





    public MutableLiveData<Boolean> addUserInDatabase(String userId, String userName, String emailId, String timestamp, String imageUrl) {
        final MutableLiveData<Boolean> successAddUserDb = new MutableLiveData<>();

        HashMap<String, String> hashMap = new HashMap<>();
        hashMap.put("id", userId);
        hashMap.put("username", userName);
        hashMap.put("emailId", emailId);
        hashMap.put("timestamp", timestamp);
        hashMap.put("imageUrl", imageUrl);
        hashMap.put("bio", "Hey there!");
        hashMap.put("status", "offline");

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
