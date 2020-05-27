package com.example.whizzz.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whizzz.R;
import com.example.whizzz.services.model.Users;
import com.example.whizzz.view.adapters.UserFragmentAdapter;
import com.example.whizzz.viewModel.DatabaseViewModel;
import com.google.firebase.database.DataSnapshot;

import java.util.ArrayList;
import java.util.Objects;

public class UserFragment extends Fragment {
    private Context context;
    DatabaseViewModel databaseViewModel;
    ArrayList<Users> mUSer;
    String currentUserId;
    RecyclerView recyclerView;
    UserFragmentAdapter userFragmentAdapter;

    public UserFragment(Context context) {
        this.context = context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_user, container, false);
        init(view);
        fetchingAllUserNAme();
        return view;
    }



    private void fetchingAllUserNAme() {
        databaseViewModel.fetchingUserDataCurrent();
        databaseViewModel.fetchUserCurrentData.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                Users users = dataSnapshot.getValue(Users.class);
                assert users != null;
                currentUserId = users.getId();
            }
        });

        databaseViewModel.fetchUserNameAll();
        databaseViewModel.fetchUserNames.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {

                mUSer.clear();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Users user = snapshot.getValue(Users.class);

                    assert user != null;
                    if (!currentUserId.equals(user.getId())) {
                        mUSer.add(user);

                    }
                    userFragmentAdapter = new UserFragmentAdapter(mUSer, context);
                    recyclerView.setAdapter(userFragmentAdapter);
                }

            }
        });
    }

    private void init(View view) {
        databaseViewModel = new ViewModelProvider(this, ViewModelProvider.AndroidViewModelFactory
                .getInstance(Objects.requireNonNull(getActivity()).getApplication()))
                .get(DatabaseViewModel.class);

        recyclerView = view.findViewById(R.id.user_list_recycle_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(context));
        mUSer = new ArrayList<>();

    }
}
