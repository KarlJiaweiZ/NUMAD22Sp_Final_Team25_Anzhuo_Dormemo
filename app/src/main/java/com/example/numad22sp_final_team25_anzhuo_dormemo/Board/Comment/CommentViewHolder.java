package com.example.numad22sp_final_team25_anzhuo_dormemo.Board.Comment;

import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.numad22sp_final_team25_anzhuo_dormemo.R;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class CommentViewHolder extends RecyclerView.ViewHolder {
    public View mView;

    public CommentViewHolder(@NonNull View itemView) {
        super(itemView);
        mView = itemView;
    }

    public void setUsername(String username) {
        TextView usernameTV = mView.findViewById(R.id.comment_username);
        usernameTV.setText(username);
    }

    public void setComment(String comment) {
        TextView answerTV = mView.findViewById(R.id.comment_text);
        answerTV.setText(comment);
    }

    public void setDate(String date) {
        TextView dateTV = mView.findViewById(R.id.comment_date);
        dateTV.setText(date);
    }

    public void setTime(String time) {
        TextView timeTV = mView.findViewById(R.id.comment_time);
        timeTV.setText(time);
    }

    public void setProfileImage(Context context, String profileImage) {
        CircleImageView imageView = mView.findViewById(R.id.post_comment_image);
        Picasso.with(context).load(profileImage).into(imageView);
    }
}
