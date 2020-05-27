package com.example.whizzz.view.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.example.whizzz.services.model.Chats;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.view.adapters.MessageAdapter;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.example.whizzz.viewModel.LogInViewModel;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageActivity extends AppCompatActivity {
    LogInViewModel logInViewModel;
    DatabaseViewModel databaseViewModel;

    CircleImageView iv_profile_image;
    TextView tv_profile_user_name;
    ImageView iv_back_button;

    String profileUserNAme;
    String profileImageURL;
    FirebaseUser currentFirebaseUser;

    EditText et_chat;
    ImageView btn_sendIv;

    String chat;
    String timeStamp;
    String userId_sender; // id of user to whom message is sent or by whom message will be received
    String userId_receiver; //myID

    MessageAdapter messageAdapter;
    ArrayList<Chats> chatsArrayList;
    RecyclerView recyclerView;
    Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);
        init();
        getUserIdOfCurrentProfile();
        getCurrentFirebaseUser();
        fetchAndSaveCurrentProfileTextAndData();

        btn_sendIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chat = et_chat.getText().toString();
                if (!chat.equals("")) {
                    addChatInDataBase();
                } else {
                    Toast.makeText(MessageActivity.this, "Message can't be empty.", Toast.LENGTH_SHORT).show();
                }
                et_chat.setText("");
            }
        });

    }

    private void getCurrentFirebaseUser() {
        logInViewModel.getFirebaseUserLogInStatus();
        logInViewModel.firebaseUserLoginStatus.observe(this, new Observer<FirebaseUser>() {
            @Override
            public void onChanged(FirebaseUser firebaseUser) {
                currentFirebaseUser = firebaseUser;
                userId_receiver = currentFirebaseUser.getUid();
            }
        });
    }

    private void getUserIdOfCurrentProfile() {
        //from user fragment adapter itemView
        //user id of sender
        userId_sender = getIntent().getStringExtra("userid");
    }

    private void fetchAndSaveCurrentProfileTextAndData() {

        databaseViewModel.fetchSelectedUserProfileData(userId_sender);
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
                fetchChatFromDatabase(userId_sender, userId_receiver);
            }
        });

    }

    private void fetchChatFromDatabase(String myId, String senderId) {
        databaseViewModel.fetchChatUser();
        databaseViewModel.fetchedChat.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                chatsArrayList.clear();

                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);
                    assert chats != null;
                    if (chats.getReceiverId().equals(senderId) && chats.getSenderId().equals(myId) || chats.getReceiverId().equals(myId) && chats.getSenderId().equals(senderId)) {
                        chatsArrayList.add(chats);
                    }

                    messageAdapter = new MessageAdapter(chatsArrayList, context, userId_receiver);
                    recyclerView.setAdapter(messageAdapter);
                }
            }
        });
    }

    private void addChatInDataBase() {

        long tsLong = System.currentTimeMillis();
        timeStamp = Long.toString(tsLong);
        databaseViewModel.addChatDb(userId_sender, userId_receiver, chat, timeStamp);
        databaseViewModel.successAddChatDb.observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean) {
                    Toast.makeText(MessageActivity.this, "Sent.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MessageActivity.this, "Message can't be sent.", Toast.LENGTH_SHORT).show();
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
        context = MessageActivity.this;


        iv_profile_image = findViewById(R.id.iv_user_image);
        tv_profile_user_name = findViewById(R.id.tv_profile_user_name);
        iv_back_button = findViewById(R.id.iv_back_button);

        iv_back_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        et_chat = findViewById(R.id.et_chat);
        btn_sendIv = findViewById(R.id.iv_send_button);

        recyclerView = findViewById(R.id.recycler_view_messages_record);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(context);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        chatsArrayList = new ArrayList<>();
    }


}
