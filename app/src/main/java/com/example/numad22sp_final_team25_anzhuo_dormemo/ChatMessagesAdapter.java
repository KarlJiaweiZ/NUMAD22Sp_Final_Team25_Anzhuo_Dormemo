package com.example.numad22sp_final_team25_anzhuo_dormemo;


import android.graphics.Color;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatMessagesAdapter extends RecyclerView.Adapter<ChatMessagesAdapter.ChatMessageViewHolder> {

    private List<ChatMessages> userMessagesList;
    private FirebaseAuth firebaseAuth;
    private DatabaseReference databaseReference;

    public ChatMessagesAdapter (List<ChatMessages> userMessagesList) {
        this.userMessagesList = userMessagesList;
    }

    public class ChatMessageViewHolder extends RecyclerView.ViewHolder {

        public TextView senderMessageText;
        public TextView senderMsgTime;
        public TextView receiverID;
        public TextView receiverMessageText;
        public TextView receiverStatus;
        public TextView receiverMsgTime;
        public CircleImageView receiverProfileImage;

        public ChatMessageViewHolder(View itemView) {
            super(itemView);
            senderMessageText = (TextView) itemView.findViewById(R.id.sender_message_text);
            senderMsgTime = (TextView) itemView.findViewById(R.id.sender_datetime);
            receiverID = (TextView) itemView.findViewById(R.id.receiver_id_info);
            receiverMessageText = (TextView) itemView.findViewById(R.id.receiver_message_text);
            receiverStatus = (TextView) itemView.findViewById(R.id.receiver_status_info);
            receiverMsgTime = (TextView) itemView.findViewById(R.id.receiver_datetime);
            receiverProfileImage = (CircleImageView) itemView.findViewById(R.id.chat_message_profile_image);
        }
    }

    @NonNull
    @Override
    public ChatMessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chat_message_holder_layout, parent, false);
        firebaseAuth = FirebaseAuth.getInstance();
        return new ChatMessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChatMessageViewHolder holder, int position) {
        String messageSenderId = firebaseAuth.getCurrentUser().getUid();
        ChatMessages messages = userMessagesList.get(position);
        String fromUserId = messages.getUserID();

        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fromUserId);
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("UserPic")) {
                    String receiverImage = snapshot.child("UserPic").getValue().toString();
                    Picasso.get().load(receiverImage).into(holder.receiverProfileImage);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.hasChild("Status")) {
                    String senderStatus = snapshot.child("Status").getValue().toString();
                    holder.receiverStatus.setText("- " + senderStatus);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.senderMessageText.setVisibility(View.GONE);
        holder.senderMsgTime.setVisibility(View.GONE);
        holder.receiverID.setVisibility(View.GONE);
        holder.receiverMessageText.setVisibility(View.GONE);
        holder.receiverStatus.setVisibility(View.GONE);
        holder.receiverMsgTime.setVisibility(View.GONE);
        holder.receiverProfileImage.setVisibility(View.GONE);

        if(fromUserId.equals(messageSenderId)) {
            holder.senderMessageText.setVisibility(View.VISIBLE);
            holder.senderMessageText.setBackgroundResource(R.drawable.chat_send_messages_layout);
            holder.senderMessageText.setTextColor(Color.WHITE);
            holder.senderMessageText.setText(messages.getMessage());
            holder.senderMsgTime.setVisibility(View.VISIBLE);
            holder.senderMsgTime.setText(messages.getDate() + " " + messages.getTime());
        }
        else{
            holder.receiverID.setVisibility(View.VISIBLE);
            holder.receiverMessageText.setVisibility(View.VISIBLE);
            holder.receiverStatus.setVisibility(View.VISIBLE);
            holder.receiverMsgTime.setVisibility(View.VISIBLE);
            holder.receiverProfileImage.setVisibility(View.VISIBLE);

            holder.receiverID.setText(messages.getUserName());
            holder.receiverMessageText.setBackgroundResource(R.drawable.chat_messages_layout);
            holder.receiverMessageText.setTextColor(Color.WHITE);
            holder.receiverMessageText.setText(messages.getMessage());
            holder.receiverMsgTime.setText(messages.getDate() + " " + messages.getTime());
        }

    }

    @Override
    public int getItemCount() {
        return userMessagesList.size();
    }


}
