package com.example.whizzz.view.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.example.whizzz.viewModel.LogInViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    LogInViewModel logInViewModel;
    DatabaseViewModel databaseViewModel;
    CircleImageView iv_profile_image;
    TextView tv_profile_user_name;
    ImageView iv_back_button;
    String userId_currentProfile;
    String profileUserNAme;
    String profileImageURL;
    FirebaseUser currentFirebaseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        getUserIdOfCurrentProfile();
        getCurrentFirebaseUser();
        fetchAndSaveCurrentProfileTextAndData();
    }

    private void getCurrentFirebaseUser() {
        logInViewModel.getFirebaseUserLogInStatus();
        logInViewModel.firebaseUserLoginStatus.observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                currentFirebaseUser = firebaseUser;
            }
        });
    }

    private void getUserIdOfCurrentProfile() {
        //from user fragment adapter itemView
        userId_currentProfile = getIntent().getStringExtra("userid");
    }

    private void fetchAndSaveCurrentProfileTextAndData() {

        databaseViewModel.fetchSelectedUserProfileData(userId_currentProfile);
        databaseViewModel.fetchSelectedProfileUserData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);

                assert user != null;
                profileUserNAme = user.getUsername();
                profileImageURL = user.getImageUrl();

                tv_profile_user_name.setText(profileUserNAme);
                if (profileImageURL.equals("default")) {
                    iv_profile_image.setImageResource(R.drawable.sample_img);
                } else {
                    Glide.with(MessageActivity.this).load(profileImageURL).into(iv_profile_image);
                }

            }
        });

    }


    private void init() {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(DatabaseViewModel.class);
        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(LogInViewModel.class);

        iv_profile_image = findViewById(R.id.iv_user_image);
        tv_profile_user_name = findViewById(R.id.tv_profile_user_name);
        iv_back_button = findViewById(R.id.iv_back_button);


        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


    }
}
