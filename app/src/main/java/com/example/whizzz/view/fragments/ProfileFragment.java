package com.example.whizzz.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.google.firebase.database.DataSnapshot;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;


public class ProfileFragment extends Fragment {
    Context context;

    DatabaseViewModel databaseViewModel;
    TextView tv_currentUserName_profile_fragment;
    CircleImageView iv_profileImage_profile_fragment;
    String username;
    String imageUrl;
    public ProfileFragment(Context context) {

        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        init(view);
        fetchCurrentUserdata();
        return view;
    }

    private void fetchCurrentUserdata() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                Users user = dataSnapshot.getValue(Users.class);
                if (user != null) {
                    username = user.getUsername();
                    imageUrl = user.getImageUrl();

                    tv_currentUserName_profile_fragment.setText(username);
                    if (imageUrl.equals("default")) {
                        iv_profileImage_profile_fragment.setImageResource(R.drawable.sample_img);
                    } else {
                        Glide.with(context).load(imageUrl).into(iv_profileImage_profile_fragment);
                    }
                } else {
                    Toast.makeText(context, "User not found..", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication()))
                .get(DatabaseViewModel.class);

        tv_currentUserName_profile_fragment = view.findViewById(R.id.tv_profile_fragment_username);
        iv_profileImage_profile_fragment = view.findViewById(R.id.iv_profile_fragment);

    }
}
