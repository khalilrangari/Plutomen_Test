package com.example.plutontestmvvm.userdetails.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.plutontestmvvm.databinding.ItemUserDetailsBinding;
import com.example.plutontestmvvm.models.UserHelperClass;

import java.util.ArrayList;

public class UserListAdapter extends RecyclerView.Adapter<UserListAdapter.MyViewHolder> {

    private ArrayList<UserHelperClass> usersData;

    public UserListAdapter(ArrayList<UserHelperClass> usersData) {
        this.usersData = usersData;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(ItemUserDetailsBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.mBinding.tvName.setText(String.format("%s %s",
                usersData.get(position).getFirstName(),
                usersData.get(position).getLastName()));
        holder.mBinding.tvEmail.setText(usersData.get(position).getEmail());
    }

    @Override
    public int getItemCount() {
        return usersData != null && usersData.size() > 0 ? usersData.size() : 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        ItemUserDetailsBinding mBinding;
        public MyViewHolder(@NonNull ItemUserDetailsBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }
}
