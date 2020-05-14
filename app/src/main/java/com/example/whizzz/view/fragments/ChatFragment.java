package com.example.whizzz.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.whizzz.R;

public class ChatFragment extends Fragment {
    private Context context;
    public ChatFragment(Context context) {
        this.context= context;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View view =inflater.inflate(R.layout.fragment_chats, container, false);
       return view;
    }
}
