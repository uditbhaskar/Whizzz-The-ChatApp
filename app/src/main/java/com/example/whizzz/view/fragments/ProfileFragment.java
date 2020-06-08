package com.example.whizzz.view.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class ProfileFragment extends Fragment {
    Context context;

    DatabaseViewModel databaseViewModel;
    TextView tv_currentUserName_profile_fragment;
    CircleImageView iv_profileImage_profile_fragment;
    ImageView btn_profile_image_change;
    String username;
    String imageUrl;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    String timeStamp;
    private StorageTask uploadImageTask;
    private StorageReference fileReference;

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

    private void openImage() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, IMAGE_REQUEST);
    }


    private void uploadImage() {
        final ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage("Uploading Image.");
        progressDialog.show();


        if (imageUri != null) {
            long tsLong = System.currentTimeMillis();
            timeStamp = Long.toString(tsLong);
            fetchingImageFileReference();
            databaseViewModel.fetchImageFileReference(timeStamp, imageUri, context);
            databaseViewModel.imageFileReference.observe(this, new Observer<StorageReference>() {
                @Override
                public void onChanged(StorageReference storageReference) {
                    fileReference = storageReference;
                    uploadImageTask = fileReference.putFile(imageUri);
                    uploadImageTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                        @Override
                        public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                            if (!task.isSuccessful()) {
                                throw Objects.requireNonNull(task.getException());
                            }
                            return fileReference.getDownloadUrl();
                        }
                    }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Uri downLoadUri = task.getResult();
                                assert downLoadUri != null;
                                String mUri = downLoadUri.toString();
                                databaseViewModel.addImageUrlInDatabase("imageUrl", mUri);
                            } else {
                                Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show();
                            }
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            });

        } else {
            Toast.makeText(context, "No image selected.", Toast.LENGTH_SHORT).show();
        }
    }

    private void fetchingImageFileReference() {
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();
            if (uploadImageTask != null && uploadImageTask.isInProgress()) {
                Toast.makeText(context, "Upload in progress.", Toast.LENGTH_SHORT).show();
            } else {

                uploadImage();
            }
        }
    }

    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication()))
                .get(DatabaseViewModel.class);

        tv_currentUserName_profile_fragment = view.findViewById(R.id.tv_profile_fragment_username);
        iv_profileImage_profile_fragment = view.findViewById(R.id.iv_profile_fragment);
        btn_profile_image_change = view.findViewById(R.id.btn_profile_image_change);

        btn_profile_image_change.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openImage();
            }
        });

    }


}
