package com.example.whizzz.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class BottomSheetProfileDetailUser extends BottomSheetDialogFragment {
    String username;
    String imageURL;
    String bio;
    Context context;

    CircleImageView iv_profile_bottom_sheet_profile_image;
    TextView tv_profile__bottom_sheet_fragment_username;
    TextView tv_profile_bottom_sheet_fragment_bio;

    public BottomSheetProfileDetailUser(String username, String imageURL, String bio, Context context) {
        this.username = username;
        this.imageURL = imageURL;
        this.bio = bio;
        this.context = context;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(BottomSheetDialogFragment.STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_bottom_sheet_show_profile, container, false);
        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        init(view);
        setDetails(username, imageURL, bio);
        return view;
    }

    private void setDetails(String username, String imageURL, String bio) {
        tv_profile__bottom_sheet_fragment_username.setText(username);
        tv_profile_bottom_sheet_fragment_bio.setText(bio);

        if (imageURL.equals("default")) {
            iv_profile_bottom_sheet_profile_image.setImageResource(R.drawable.sample_img);
        } else {
            Glide.with(context).load(imageURL).into(iv_profile_bottom_sheet_profile_image);
        }

    }

    private void init(View view) {
        iv_profile_bottom_sheet_profile_image = view.findViewById(R.id.iv_profile_bottom_sheet);
        tv_profile__bottom_sheet_fragment_username = view.findViewById(R.id.tv_profile__bottom_sheet_fragment_username);
        tv_profile_bottom_sheet_fragment_bio = view.findViewById(R.id.tv_profile_bottom_sheet_fragment_bio);
    }

}
