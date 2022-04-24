package com.example.numad22sp_final_team25_anzhuo_dormemo.Board;

import android.content.Context;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.R;
import com.google.firebase.auth.FirebaseAuth;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessageViewHolder extends RecyclerView.ViewHolder {
    public View mView;
    public ImageButton likeButton;
    public ImageButton commentButton;
    public TextView likeCountText;
//    public int countLike;
    public String currentUserID;

    public MessageViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;

        likeButton = mView.findViewById(R.id.like_button);
        commentButton = mView.findViewById(R.id.comment_button);
        likeCountText = mView.findViewById(R.id.number_of_likes);

        currentUserID = FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public void setPostAuthor(String postAuthor) {
        TextView tv = mView.findViewById(R.id.post_message_username);
        tv.setText(postAuthor);
    }

    public void setProfileImage(Context context, String profileImage) {
        CircleImageView image = mView.findViewById(R.id.post_message_userimage);
        Picasso.with(context).load(profileImage).into(image);
    }

    public void setMessage(String message) {
        TextView tv = mView.findViewById(R.id.user_message);
        tv.setText(message);
    }

    public void setTime(String time) {
        TextView tv = mView.findViewById(R.id.post_time);
        tv.setText(time);
    }

    public void setDate(String date) {
        TextView tv = mView.findViewById(R.id.post_date);
        tv.setText(date);
    }
}
