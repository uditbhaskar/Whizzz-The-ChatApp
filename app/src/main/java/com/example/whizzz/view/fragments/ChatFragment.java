package com.example.whizzz.view.fragments;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whizzz.R;
import com.example.whizzz.services.model.Chats;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.view.adapters.UserFragmentAdapter;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class ChatFragment extends Fragment {
    private Context context;
    private UserFragmentAdapter userAdapter;
    private ArrayList<Users> mUsers;
    private String currentUserId;
    RelativeLayout relative_layout_chat_fragment;
    private ArrayList<String> userList; //list of all other users with chat record
    private DatabaseViewModel databaseViewModel;
    private RecyclerView recyclerView_chat_fragment;

    public ChatFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        init(view);
        fetchAllChat();
        return view;
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

        databaseViewModel.fetchChatUser();
        databaseViewModel.fetchedChat.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Chats chats = snapshot.getValue(Chats.class);


                    assert chats != null;
                    if (chats.getSenderId().equals(currentUserId)) {
                        userList.add(chats.getReceiverId());
                    }
                    if (chats.getReceiverId().equals(currentUserId)) {
                        userList.add(chats.getSenderId());
                    }
                }
                ChatListOfUsers();
            }


        });


    }


    private void updateToken(String tokens){

    }

    private void ChatListOfUsers() {
        databaseViewModel.fetchUserNameAll();
        databaseViewModel.fetchUserNames.observe(this, new Observer<DataSnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users users = snapshot.getValue(Users.class);

                    for (String id : userList) {
                        assert users != null;
                        if (users.getId().equals(id)) {
                            if(!mUsers.contains(users)) {
                                mUsers.add(users);
                            }
                        }
                    }

                }
                if(mUsers.size()>=1){
                    relative_layout_chat_fragment.setVisibility(View.GONE);
                }
                userAdapter = new UserFragmentAdapter(mUsers, context,true);
                recyclerView_chat_fragment.setAdapter(userAdapter);
            }
        });

    }


    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication()))
                .get(DatabaseViewModel.class);
        relative_layout_chat_fragment = view.findViewById(R.id.relative_layout_chat_fragment);
        recyclerView_chat_fragment = view.findViewById(R.id.recycler_view_chat_fragment);
        recyclerView_chat_fragment.setLayoutManager(new LinearLayoutManager(context));
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView_chat_fragment.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView_chat_fragment.addItemDecoration(dividerItemDecoration);
        mUsers = new ArrayList<>();
        userList = new ArrayList<>();

    }
}
