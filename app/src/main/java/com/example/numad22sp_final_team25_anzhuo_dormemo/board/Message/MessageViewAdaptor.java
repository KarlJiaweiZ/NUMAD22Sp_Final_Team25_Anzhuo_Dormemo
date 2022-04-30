package com.example.numad22sp_final_team25_anzhuo_dormemo.board.Message;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.CommentsActivity;
import com.example.numad22sp_final_team25_anzhuo_dormemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class MessageViewAdaptor extends RecyclerView.Adapter<MessageViewHolder> {
    public HashMap<String, Messages> messagesHashmap;
    public Context context;
    public String currentUserID;

    public MessageViewAdaptor(HashMap<String, Messages> messagesHashmap, Context context) {
        this.messagesHashmap = messagesHashmap;
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
        List<String> postIDs = new ArrayList<>(messagesHashmap.keySet());
        String mid = postIDs.get(position);
        Messages currentItem = messagesHashmap.get(mid);
        holder.setMessage(currentItem.message);

        DatabaseReference likeReference = FirebaseDatabase.getInstance().getReference().child("Dorms").child(currentItem.dormName).child("Messages").child(mid).child("likes");
        Context context = this.context;

        final String[] profilePictureMessageOwner = new String[1];

        holder.setPostAuthor(currentItem.username);
        holder.setTime(currentItem.time);
        holder.setDate(currentItem.date);
        holder.setPriorityIcon(context, currentItem.priority);

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

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        likeReference.addValueEventListener(new ValueEventListener() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int numberOfLikes = (int)snapshot.getChildrenCount() - 1;
                if (snapshot.exists() && snapshot.hasChild(currentUserID)) {
                    holder.likeButton.setImageResource(R.drawable.message_like_icon);
                } else {
                    holder.likeButton.setImageResource(R.drawable.message_dislike_icon);
                }
                holder.likeCountText.setText(numberOfLikes + " Likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.likeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentItem.likes.containsKey(currentUserID)) {
                    // remove
                    likeReference.child(currentUserID).removeValue();
                    currentItem.likes.remove(currentUserID);
                } else {
                    // add
                    likeReference.child(currentUserID).setValue(true);
                    currentItem.likes.put(currentUserID, true);
                }
            }
        });

        holder.commentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // start CommentsActivity
                Intent commentsIntent = new Intent(context, CommentsActivity.class);
                commentsIntent.putExtra("postKey", mid);
                commentsIntent.putExtra("dormName", currentItem.dormName);
                context.startActivity(commentsIntent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messagesHashmap.size();
    }
}
