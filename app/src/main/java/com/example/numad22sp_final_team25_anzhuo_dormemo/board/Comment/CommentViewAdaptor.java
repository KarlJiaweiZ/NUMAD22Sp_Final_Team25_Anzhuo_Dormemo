package com.example.numad22sp_final_team25_anzhuo_dormemo.board.Comment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
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
        List<String> postIDs = new ArrayList<>(commentsHashMap.keySet());
        String cid = postIDs.get(position);
        Comments currentItem = commentsHashMap.get(cid);
        holder.setComment(currentItem.comment);
        holder.setTime(currentItem.time);
        holder.setDate(currentItem.date);
        holder.setUsername(currentItem.username);

        final String[] profilePictureMessageOwner = new String[1];

        // update users profilePicture
        FirebaseDatabase.getInstance().getReference().child("Users").child(currentItem.uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    profilePictureMessageOwner[0] = Objects.requireNonNull(snapshot.child("UserPic").getValue()).toString();
                }
                holder.setProfileImage(context, profilePictureMessageOwner[0]);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return commentsHashMap.size();
    }
}
