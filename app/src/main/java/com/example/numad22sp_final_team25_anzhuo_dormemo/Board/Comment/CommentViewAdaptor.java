package com.example.numad22sp_final_team25_anzhuo_dormemo.Board.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.R;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

public class CommentViewAdaptor extends RecyclerView.Adapter<CommentViewHolder> {
    public HashMap<String, Comments> commentsHashMap;
    public Context context;

    public CommentViewAdaptor(HashMap<String, Comments> commentsHashMap, Context context) {
        this.commentsHashMap = commentsHashMap;
        this.context = context;
    }

    @NonNull
    @Override
    public CommentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.single_comment, parent, false);
        return new CommentViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CommentViewHolder holder, int position) {
        List<String> postIDs = commentsHashMap.keySet().stream().sorted().collect(Collectors.toList());
        String cid = postIDs.get(position);
        Comments currentItem = commentsHashMap.get(cid);
        holder.setComment(currentItem.comment);
        holder.setTime(currentItem.time);
        holder.setDate(currentItem.date);
        holder.setUsername(currentItem.username);
        holder.setProfileImage(this.context, currentItem.profileImage);
    }

    @Override
    public int getItemCount() {
        return commentsHashMap.size();
    }
}
