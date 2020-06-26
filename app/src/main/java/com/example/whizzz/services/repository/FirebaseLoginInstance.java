package com.example.whizzz.services.repository;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.example.whizzz.services.notifications.Token;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseLoginInstance {
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser = mAuth.getCurrentUser();


    public MutableLiveData<FirebaseUser> getFirebaseUserLoginStatus() {
        final MutableLiveData<FirebaseUser> firebaseUserLoginStatus = new MutableLiveData<>();
        firebaseUserLoginStatus.setValue(firebaseUser);
        return firebaseUserLoginStatus;

    }

    public MutableLiveData<FirebaseAuth> getFirebaseAuth() {
        final MutableLiveData<FirebaseAuth> firebaseAuth = new MutableLiveData<>();
        firebaseAuth.setValue(mAuth);
        return firebaseAuth;
    }

    public MutableLiveData<Boolean> successUpdateToken(String newToken) {
        final MutableLiveData<Boolean> successTokenUpdate = new MutableLiveData<>();
        FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Tokens");
        Token token = new Token(newToken);
        assert firebaseUser != null;
        reference.child(firebaseUser.getUid()).setValue(token);
        return successTokenUpdate;
    }




    public MutableLiveData<Task> loginUser(String emailLogin, String pwdLogin) {
        final MutableLiveData<Task> taskLogin = new MutableLiveData<>();

        mAuth.signInWithEmailAndPassword(emailLogin, pwdLogin).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                taskLogin.setValue(task);
            }
        });

        return taskLogin;
    }

    public MutableLiveData<Task> resetPassword(String email) {
        final MutableLiveData<Task> successResetPassword = new MutableLiveData<>();

        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                successResetPassword.setValue(task);
            }
        });


        return successResetPassword;
    }
}
