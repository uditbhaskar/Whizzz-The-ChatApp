package com.example.whizzz.view.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whizzz.R;
import com.example.whizzz.services.model.ChatList;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.view.adapters.UserFragmentAdapter;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.example.whizzz.viewModel.LogInViewModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.ArrayList;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private Context context;
    private UserFragmentAdapter userAdapter;
    private ArrayList<Users> mUsers;
    private String currentUserId;
    private ArrayList<ChatList> userList;  //list of all other users with chat record
    private DatabaseViewModel databaseViewModel;
    private LogInViewModel logInViewModel;
    private RecyclerView recyclerView_chat_fragment;
    RelativeLayout relative_layout_chat_fragment;

    public ChatFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        init(view);
        fetchAllChat();
        getTokens();


        return view;
    }

   public void getTokens() {
       FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener((Activity) context, new OnSuccessListener<InstanceIdResult>() {
           @Override
           public void onSuccess(InstanceIdResult instanceIdResult) {
               String mToken = instanceIdResult.getToken();
               updateToken(mToken); //updating token in firebase database

           }
       });
   }

    private void fetchAllChat() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;
                currentUserId = users.getId();
            }
        });

        databaseViewModel.getChaListUserDataSnapshot(currentUserId);
        databaseViewModel.getChaListUserDataSnapshot.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    ChatList chatList = dataSnapshot1.getValue(ChatList.class);
                    userList.add(chatList);
                }

                chatLists();
            }
        });


    }

    private void chatLists() {
        databaseViewModel.fetchUserByNameAll();
        databaseViewModel.fetchUserNames.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot dataSnapshot1 : dataSnapshot.getChildren()) {
                    Users users = dataSnapshot1.getValue(Users.class);
                    for (ChatList chatList : userList) {
                        assert users != null;
                        if (users.getId().equals(chatList.getId())) {
                            if(!mUsers.contains(users))
                            mUsers.add(users);
                        }
                    }
                }
                if(mUsers.size()<1){
                    relative_layout_chat_fragment.setVisibility(View.VISIBLE);
                }else {
                    relative_layout_chat_fragment.setVisibility(View.GONE);
                }

                userAdapter = new UserFragmentAdapter(mUsers, context, true);
                recyclerView_chat_fragment.setAdapter(userAdapter);
            }
        });
    }


    private void updateToken(String token) {
        logInViewModel.updateToken(token);
    }


    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication()))
                .get(DatabaseViewModel.class);

        logInViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication()))
                .get(LogInViewModel.class);

        relative_layout_chat_fragment = view.findViewById(R.id.relative_layout_chat_fragment);
        recyclerView_chat_fragment = view.findViewById(R.id.recycler_view_chat_fragment);
        recyclerView_chat_fragment.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_chat_fragment.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView_chat_fragment.addItemDecoration(dividerItemDecoration);
        mUsers = new ArrayList<>();
        userList = new ArrayList<>();

    }
}
