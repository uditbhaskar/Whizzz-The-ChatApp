package com.example.whizzz.view.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.view.adapters.ViewPagerAdapter;
import com.example.whizzz.view.fragments.ChatFragment;
import com.example.whizzz.view.fragments.ProfileFragment;
import com.example.whizzz.view.fragments.UserFragment;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.example.whizzz.viewModel.LogInViewModel;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {
    LogInViewModel logInViewModel;
    Toolbar toolbar;
    DatabaseViewModel databaseViewModel;

    LinearLayout linearLayout;
    ProgressBar progressBar;
    TextView currentUserName;
    CircleImageView profileImage;
    String username;
    String imageUrl;

    TabLayout tabLayout;
    ViewPager viewPager;
    ViewPagerAdapter viewPagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        init();
        fetchCurrentUserdata();
        setupPagerFragment();
        onOptionMenuClicked();

    }

    private void setupPagerFragment() {
        viewPagerAdapter = new ViewPagerAdapter(getSupportFragmentManager(), ViewPagerAdapter.POSITION_UNCHANGED);

        viewPagerAdapter.addFragment(new ChatFragment(this), "Chats");
        viewPagerAdapter.addFragment(new UserFragment(this), "Users");
        viewPagerAdapter.addFragment(new ProfileFragment(this), "Profile");

        viewPager.setAdapter(viewPagerAdapter);

        tabLayout.setupWithViewPager(viewPager);

    }

    private void fetchCurrentUserdata() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null) {
                    progressBar.setVisibility(View.GONE);
                    linearLayout.setVisibility(View.VISIBLE);
                    username = user.getUsername();
                    imageUrl = user.getImageUrl();
                    //  Toast.makeText(HomeActivity.this, "Welcome back " + username + ".", Toast.LENGTH_SHORT).show();
                    currentUserName.setText(username);
                    if (imageUrl.equals("default")) {
                        profileImage.setImageResource(R.drawable.sample_img);
                    } else {
                        Glide.with(getApplicationContext()).load(imageUrl).into(profileImage);
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "User not found..", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void getUserAuthToSignOut() {
        logInViewModel.getFirebaseAuth();
        logInViewModel.firebaseAuthLiveData.observe(this, new Observer<FirebaseAuth>() {
            @Override
            public void onChanged(FirebaseAuth firebaseAuth) {
                firebaseAuth.signOut();
                Intent intent = new Intent(HomeActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    public void onOptionMenuClicked() {
        toolbar.inflateMenu(R.menu.logout);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.log_out_home) {
                    getUserAuthToSignOut();
                    Toast.makeText(HomeActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                    return true;
                } else {
                    return false;
                }
            }

        });
    }





    private void init() {

        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(LogInViewModel.class);

        toolbar = findViewById(R.id.toolbar);

        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(getApplication()))
                .get(DatabaseViewModel.class);

        currentUserName = findViewById(R.id.tv_username);
        profileImage = findViewById(R.id.iv_profile_image);
        linearLayout = findViewById(R.id.linearLayout);
        progressBar = findViewById(R.id.progress_bar);
        tabLayout = findViewById(R.id.tab_layout);
        viewPager = findViewById(R.id.view_pager);

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewPager.setCurrentItem(2); // to go to profile fragment
            }
        });

    }

    private void status(String status){
        databaseViewModel.addStatusInDatabase("status", status);
    }

    @Override
    protected void onResume() {
        super.onResume();
        status("online");
    }

    @Override
    protected void onPause() {
        super.onPause();
        status("offline");
    }
}
