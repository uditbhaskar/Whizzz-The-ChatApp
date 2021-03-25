package com.example.whizzz.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
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
    private DatabaseViewModel databaseViewModel;
    private ArrayList<Users> mUSer;
    private String currentUserId;
    private RecyclerView recyclerView;
    private UserFragmentAdapter userFragmentAdapter;
    EditText et_search;

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

        databaseViewModel.fetchUserByNameAll();
        databaseViewModel.fetchUserNames.observe(this, new Observer<DataSnapshot>() {
            @Override
            public void onChanged(DataSnapshot dataSnapshot) {
                if (et_search.getText().toString().equals("")) {
                    mUSer.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Users user = snapshot.getValue(Users.class);

                        assert user != null;
                        if (!(user.getEmailId() == null)
                        ) {
                            if (!currentUserId.equals(user.getId())) {
                                mUSer.add(user);

                            }
                        }
                        userFragmentAdapter = new UserFragmentAdapter(mUSer, context, false);
                        recyclerView.setAdapter(userFragmentAdapter);

                    }

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
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(), DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        mUSer = new ArrayList<>();
        et_search = view.findViewById(R.id.et_search);
        et_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    searchUsers(s.toString().toLowerCase());
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (et_search.getText().toString().startsWith(" "))
                    et_search.setText("");
            }
        });

    }

    private void searchUsers(String searchText) {

        if (!(searchText.isEmpty() && searchText.equals(""))) {
            databaseViewModel.fetchSearchedUser(searchText);
            databaseViewModel.fetchSearchUser.observe(this, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(DataSnapshot dataSnapshot) {
                    mUSer.clear();
                    for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                        Users users = snapshot.getValue(Users.class);
                        assert users != null;
                        if (!users.getId().equals(currentUserId)) {
                            mUSer.add(users);
                        }

                    }
                    userFragmentAdapter = new UserFragmentAdapter(mUSer, context, false);
                    recyclerView.setAdapter(userFragmentAdapter);

                }
            });
        }else {
            fetchingAllUserNAme();
        }
    }
}
