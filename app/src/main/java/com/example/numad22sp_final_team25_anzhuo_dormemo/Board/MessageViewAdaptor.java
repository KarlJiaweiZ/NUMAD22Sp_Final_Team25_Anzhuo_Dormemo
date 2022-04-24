package com.example.numad22sp_final_team25_anzhuo_dormemo.Board;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.R;

import java.util.ArrayList;

public class MessageViewAdaptor extends RecyclerView.Adapter<MessageViewHolder> {
    public ArrayList<Messages> messagesArrayList;
    public Context context;

    public MessageViewAdaptor(ArrayList<Messages> messagesArrayList, Context context) {
        this.messagesArrayList = messagesArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Messages currentItem = messagesArrayList.get(position);
        holder.setMessage(currentItem.message);
        holder.setProfileImage(this.context, currentItem.profilePicture);
        holder.setPostAuthor(currentItem.username);
        holder.setTime(currentItem.time);
        holder.setDate(currentItem.date);
    }

    @Override
    public int getItemCount() {
        return messagesArrayList.size();
    }
}
