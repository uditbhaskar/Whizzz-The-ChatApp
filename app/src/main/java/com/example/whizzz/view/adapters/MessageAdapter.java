package com.example.whizzz.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.whizzz.R;
import com.example.whizzz.services.model.Chats;

import java.util.ArrayList;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageHolder> {

    private static final int MSG_TYPE_LEFT_RECEIVED = 0;
    private static final int MSG_TYPE_RIGHT_RECEIVED = 1;
    private ArrayList<Chats> chatArrayList;
    private Context context;
    private String currentUser_received;

    public MessageAdapter(ArrayList<Chats> chatArrayList, Context context, String currentUser_received) {
        this.chatArrayList = chatArrayList;
        this.context = context;
        this.currentUser_received = currentUser_received;
    }


    @NonNull
    @Override
    public MessageHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_TYPE_RIGHT_RECEIVED) {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_right_sent, parent, false);
            return new MessageHolder(view);
        } else {
            View view = LayoutInflater.from(context).inflate(R.layout.chat_item_left_received, parent, false);
            return new MessageHolder(view);
        }

    }

    @Override
    public void onBindViewHolder(@NonNull MessageHolder holder, int position) {
        Chats chats = chatArrayList.get(position);
        String message= chats.getMessage().toString();
        holder.tv_msg.setText(message);
    }

    @Override
    public int getItemCount() {
        return chatArrayList.size();
    }

    public class MessageHolder extends RecyclerView.ViewHolder {
        TextView tv_msg;
        public MessageHolder(@NonNull View itemView) {
            super(itemView);
            tv_msg= itemView.findViewById(R.id.tv_chat_received);

        }
    }

    @Override
    public int getItemViewType(int position) {
        if (chatArrayList.get(position).getReceiverId().equals(currentUser_received)) {
            return MSG_TYPE_RIGHT_RECEIVED;
        } else return MSG_TYPE_LEFT_RECEIVED;
    }
}
