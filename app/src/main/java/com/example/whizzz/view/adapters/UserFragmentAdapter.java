package com.example.whizzz.view.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.whizzz.R;
import com.example.whizzz.services.model.Users;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class UserFragmentAdapter extends RecyclerView.Adapter<UserFragmentAdapter.UserFragmentHolder> {

    private ArrayList<Users> usersArrayList;
    private Context context;

    public UserFragmentAdapter(ArrayList<Users> usersArrayList, Context context) {
        this.usersArrayList = usersArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserFragmentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(R.layout.user_list_item_view, parent, false);
        return new UserFragmentHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserFragmentHolder holder, int position) {
        Users users = usersArrayList.get(position);

        String imageUrl = users.getImageUrl();
        String userName = users.getUsername();

        if (imageUrl.equals("default")) {
            holder.iv_profile_image.setImageResource(R.drawable.sample_img);
        } else {
            Glide.with(context).load(imageUrl).into(holder.iv_profile_image);
        }

        holder.tv_name.setText(userName);

    }

    @Override
    public int getItemCount() {
        return usersArrayList.size();
    }

    public class UserFragmentHolder extends RecyclerView.ViewHolder {
        CircleImageView iv_profile_image;
        TextView tv_name;

        UserFragmentHolder(@NonNull View itemView) {
            super(itemView);
            iv_profile_image = itemView.findViewById(R.id.profile_image);
            tv_name = itemView.findViewById(R.id.user_name_list);
        }
    }

}
